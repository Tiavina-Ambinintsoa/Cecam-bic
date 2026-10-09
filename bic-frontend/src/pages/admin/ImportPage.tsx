import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { clientApi } from "@/api/clientApi";
import type { ClientRequest } from "@/types/client";
import { ORIGINE_DONNEE_LABELS, type OrigineDonnee } from "@/types/origineDonnee";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { useToast } from "@/hooks/use-toast";

const ENTETES = ["prenom", "nom", "dateNaissance", "genre", "numeroIdentifiant", "adresseComplete", "ville", "commune", "region", "telephone"];
const ENTETES_REQUISES = ENTETES.filter((entete) => entete !== "telephone");

type CsvRow = Record<string, string>;

function analyserCsv(text: string): CsvRow[] {
  let compteurVirgules = 0, compteurPointsVirgules = 0, guillemetsEntete = false;
  for (let i = 0; i < text.length && text[i] !== "\n" && text[i] !== "\r"; i++) {
    if (text[i] === '"' && text[i + 1] === '"' && guillemetsEntete) i++;
    else if (text[i] === '"') guillemetsEntete = !guillemetsEntete;
    else if (!guillemetsEntete && text[i] === ",") compteurVirgules++;
    else if (!guillemetsEntete && text[i] === ";") compteurPointsVirgules++;
  }
  const separateur = compteurPointsVirgules >= compteurVirgules ? ";" : ",";
  const lignes: string[][] = [];
  let ligne: string[] = [], cellule = "", guillemets = false;
  for (let i = 0; i < text.length; i++) {
    const c = text[i];
    if (c === '"' && guillemets && text[i + 1] === '"') { cellule += '"'; i++; }
    else if (c === '"') guillemets = !guillemets;
    else if (!guillemets && c === separateur) { ligne.push(cellule.trim()); cellule = ""; }
    else if (!guillemets && (c === "\n" || c === "\r")) {
      if (c === "\r" && text[i + 1] === "\n") i++;
      ligne.push(cellule.trim());
      if (ligne.some((v) => v !== "")) lignes.push(ligne);
      ligne = []; cellule = "";
    } else cellule += c;
  }
  ligne.push(cellule.trim());
  if (ligne.some((v) => v !== "")) lignes.push(ligne);
  if (guillemets || lignes.length < 2) throw new Error("Le CSV est vide ou mal formé.");
  const entetes = lignes[0].map((h) => h.replace(/^﻿/, "").trim());
  const manquantes = ENTETES_REQUISES.filter((h) => !entetes.includes(h));
  if (manquantes.length) throw new Error(`Colonnes manquantes : ${manquantes.join(", ")}`);
  return lignes.slice(1).map((values) => Object.fromEntries(entetes.map((h, i) => [h, values[i] ?? ""])));
}

function dateIso(raw: string): string {
  const value = raw.trim();
  const iso = /^\d{4}-\d{2}-\d{2}$/.test(value) ? value : (() => {
    const m = /^(\d{1,2})\/(\d{1,2})\/(\d{4})$/.exec(value);
    if (!m) throw new Error("dateNaissance doit être au format jj/mm/aaaa ou aaaa-mm-jj");
    return `${m[3]}-${m[2].padStart(2, "0")}-${m[1].padStart(2, "0")}`;
  })();
  const parsed = new Date(`${iso}T00:00:00Z`);
  if (Number.isNaN(parsed.getTime()) || parsed.toISOString().slice(0, 10) !== iso) throw new Error("dateNaissance n'est pas une date valide");
  return iso;
}

function clientDepuisCsv(row: CsvRow, origineDonnee: OrigineDonnee): ClientRequest {
  const prenom = row.prenom?.trim(), nom = row.nom?.trim(), cin = row.numeroIdentifiant?.trim();
  const genre = row.genre?.trim().toUpperCase();
  if (!prenom || !nom || !cin || !row.adresseComplete?.trim()) throw new Error("Une ligne ne contient pas le prénom, le nom, le CIN ou l'adresse complète requis.");
  if (genre !== "HOMME" && genre !== "FEMME") throw new Error("genre doit être HOMME ou FEMME");
  return {
    categorieTiersCode: row.categorieTiersCode?.trim() || "0004",
    prenom, nom, dateNaissance: dateIso(row.dateNaissance ?? ""), genre,
    nationalite: row.nationalite?.trim() || "Malgache",
    origineDonnee,
    telephone: row.telephone?.trim() || undefined,
    identifiants: [{ typeIdentifiant: "CIN", numero: cin }],
    adresses: [{ typeAdresse: row.typeAdresse?.trim() || "Individu - Adresse principale",
      adresseComplete: row.adresseComplete.trim(), ville: row.ville?.trim(), commune: row.commune?.trim(),
      region: row.region?.trim(), pays: row.pays?.trim() || "Madagascar" }],
  };
}

export function ImportPage() {
  const { toast } = useToast();
  const [rows, setRows] = useState<CsvRow[]>([]);
  const [nomFichier, setNomFichier] = useState("");
  const [chargement, setChargement] = useState(false);
  const [resultat, setResultat] = useState<{ crees: number; existants: number } | null>(null);
  const [origineDonnee, setOrigineDonnee] = useState<OrigineDonnee>("NON_RENSEIGNEE");

  async function chargerFichier(file?: File) {
    setRows([]); setResultat(null); setNomFichier(file?.name ?? "");
    if (!file) return;
    try { setRows(analyserCsv(await file.text())); }
    catch (error) { setNomFichier(""); toast({ title: error instanceof Error ? error.message : "CSV invalide", variant: "destructive" }); }
  }

  async function importer() {
    setChargement(true); setResultat(null);
    let crees = 0, existants = 0;
    try {
      for (const row of rows) {
        const response = await clientApi.creer(clientDepuisCsv(row, origineDonnee));
        if (response.clientTrouve) existants++; else crees++;
      }
      setResultat({ crees, existants });
      toast({ title: `Import terminé — ${crees} créé(s), ${existants} déjà présent(s).` });
    } catch (error) {
      toast({ title: "Import interrompu — vérifiez le CSV et les champs obligatoires.", variant: "destructive" });
    } finally { setChargement(false); }
  }

  function telechargerModele() {
    const contenu = `${ENTETES.join(";")}\nPrénom;Nom;01/01/1990;HOMME;CIN_EXEMPLE;Adresse;Ville;Commune;Région;`;
    const url = URL.createObjectURL(new Blob([contenu], { type: "text/csv;charset=utf-8" }));
    const a = document.createElement("a"); a.href = url; a.download = "modele-import-clients.csv"; a.click();
    window.setTimeout(() => URL.revokeObjectURL(url), 10_000);
  }

  return <div className="mx-auto max-w-3xl space-y-6 p-6">
    <Card><CardHeader><CardTitle>Importation de clients — CSV</CardTitle></CardHeader>
      <CardContent className="space-y-4 text-sm">
        <p>Import réservé à l’administrateur. Le fichier reste dans le navigateur ; les clients existants identifiés par CIN ne sont pas recréés.</p>
        <p>Colonnes obligatoires : {ENTETES_REQUISES.join(" ; ")}. Téléphone facultatif. Séparateur accepté : point-virgule ou virgule. Aucun fichier contenant des mots de passe ou jetons n’est accepté.</p>
        <div className="max-w-md space-y-1.5">
          <Label>Origine des données du fichier</Label>
          <Select value={origineDonnee} onValueChange={(value) => setOrigineDonnee(value as OrigineDonnee)}>
            <SelectTrigger><SelectValue /></SelectTrigger>
            <SelectContent>
              {(Object.entries(ORIGINE_DONNEE_LABELS) as [OrigineDonnee, string][]).map(([value, label]) => (
                <SelectItem key={value} value={value}>{label}</SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
        <div className="flex flex-wrap gap-3"><Button type="button" variant="outline" onClick={telechargerModele}>Télécharger le modèle CSV</Button>
          <input type="file" accept=".csv,text/csv" onChange={(e) => void chargerFichier(e.target.files?.[0])} /></div>
        {nomFichier && <p>{nomFichier} — {rows.length} ligne(s) prête(s) à importer.</p>}
        {rows.length > 0 && <Button onClick={() => void importer()} disabled={chargement}>{chargement ? "Import en cours…" : `Importer ${rows.length} client(s)`}</Button>}
        {resultat && <p className="font-medium">Créés : {resultat.crees} · Déjà présents : {resultat.existants}</p>}
      </CardContent>
    </Card>
  </div>;
}
