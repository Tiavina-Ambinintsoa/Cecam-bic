import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { clientApi } from "@/api/clientApi";
import type { ClientRequest } from "@/types/client";
import { useToast } from "@/hooks/use-toast";
import { Download, Upload, CheckCircle2, AlertCircle, FileSpreadsheet } from "lucide-react";

const ENTETES_STANDARDS = [
  "prenom", "nom", "dateNaissance", "genre", "numeroIdentifiant",
  "adresseComplete", "ville", "commune", "region", "telephone",
  "codeClientEtablissement", "nationalite", "pays"
];

const ENTETES_REQUISES = ["prenom", "nom", "dateNaissance", "genre", "numeroIdentifiant", "adresseComplete"];

function normaliserCle(raw: string): string {
  return raw
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .toLowerCase()
    .replace(/[^a-z0-9]/g, "");
}

const ALIASES_COLONNES: Record<string, string> = {
  cin: "numeroIdentifiant",
  numeroidentifiant: "numeroIdentifiant",
  numerocin: "numeroIdentifiant",
  numcin: "numeroIdentifiant",
  identifiant: "numeroIdentifiant",
  prenom: "prenom",
  prenoms: "prenom",
  nom: "nom",
  nomfamille: "nom",
  datenaissance: "dateNaissance",
  datedenaissance: "dateNaissance",
  naissance: "dateNaissance",
  genre: "genre",
  sexe: "genre",
  adressecomplete: "adresseComplete",
  adresse: "adresseComplete",
  ville: "ville",
  commune: "commune",
  region: "region",
  telephone: "telephone",
  tel: "telephone",
  codeclientetablissement: "codeClientEtablissement",
  codeclientcecam: "codeClientEtablissement",
  codeclient: "codeClientEtablissement",
  codeetablissement: "codeClientEtablissement",
  nationalite: "nationalite",
  pays: "pays",
  titre: "titre",
};

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
  if (guillemets || lignes.length < 1) throw new Error("Le fichier CSV est vide ou mal formé.");

  const entetesBrutes = lignes[0].map((h) => h.replace(/^﻿/, "").trim());
  if (!entetesBrutes.length || entetesBrutes.some((h) => !h)) {
    throw new Error("Les en-têtes du fichier CSV sont vides.");
  }

  // Mapper chaque en-tête vers sa clé canonique
  const entetesCanoniques = entetesBrutes.map((h) => {
    const cleNormalisee = normaliserCle(h);
    return ALIASES_COLONNES[cleNormalisee] ?? h;
  });

  const ensembleEntetes = new Set(entetesCanoniques);
  const manquantes = ENTETES_REQUISES.filter((h) => !ensembleEntetes.has(h));
  if (manquantes.length) {
    throw new Error(`Colonnes obligatoires manquantes dans le CSV : ${manquantes.join(", ")}`);
  }

  const ligneTropLarge = lignes.slice(1).findIndex((values) => values.slice(entetesCanoniques.length).some((v) => v !== ""));
  if (ligneTropLarge >= 0) {
    throw new Error(`La ligne ${ligneTropLarge + 2} contient plus de colonnes que l'en-tête.`);
  }

  return lignes.slice(1).map((values) =>
    Object.fromEntries(entetesCanoniques.map((h, i) => [h, values[i] ?? ""]))
  );
}

function dateIso(raw: string): string {
  const value = raw.trim();
  if (!value) throw new Error("dateNaissance obligatoire non renseignée");
  let iso = "";
  if (/^\d{4}-\d{2}-\d{2}$/.test(value)) {
    iso = value;
  } else {
    const m = /^(\d{1,2})[/.-](\d{1,2})[/.-](\d{4})$/.exec(value);
    if (!m) throw new Error(`Date « ${value} » invalide. Utilisez le format jj/mm/aaaa ou aaaa-mm-jj.`);
    iso = `${m[3]}-${m[2].padStart(2, "0")}-${m[1].padStart(2, "0")}`;
  }
  const parsed = new Date(`${iso}T00:00:00Z`);
  if (Number.isNaN(parsed.getTime()) || parsed.toISOString().slice(0, 10) !== iso) {
    throw new Error(`Date calendrier inexistante (« ${value} »).`);
  }
  return iso;
}

function normaliserGenre(raw?: string): "HOMME" | "FEMME" {
  const clean = (raw ?? "").trim().toUpperCase();
  if (clean === "HOMME" || clean === "H" || clean === "M" || clean === "MASCULIN") return "HOMME";
  if (clean === "FEMME" || clean === "F" || clean === "FEMININ") return "FEMME";
  throw new Error(`Genre invalide (« ${raw} »). Précisez HOMME ou FEMME.`);
}

function clientDepuisCsv(row: CsvRow): ClientRequest {
  const prenom = row.prenom?.trim();
  const nom = row.nom?.trim();
  const cin = row.numeroIdentifiant?.trim();
  const adresseComplete = row.adresseComplete?.trim();

  if (!prenom) throw new Error("Le prénom est obligatoire.");
  if (!nom) throw new Error("Le nom de famille est obligatoire.");
  if (!cin) throw new Error("Le CIN (numéro d'identité) est obligatoire.");
  if (!adresseComplete) throw new Error("L'adresse complète est obligatoire.");

  const genre = normaliserGenre(row.genre);

  return {
    categorieTiersCode: "0004",
    titre: row.titre?.trim() || undefined,
    prenom,
    nom,
    dateNaissance: dateIso(row.dateNaissance ?? ""),
    genre,
    nationalite: row.nationalite?.trim() || "Malgache",
    codeClientEtablissement: row.codeClientEtablissement?.trim() || undefined,
    telephone: row.telephone?.trim() || undefined,
    identifiants: [{ typeIdentifiant: "CIN", numero: cin }],
    adresses: [
      {
        typeAdresse: row.typeAdresse?.trim() || "Individu - Adresse principale",
        adresseComplete,
        ville: row.ville?.trim() || undefined,
        commune: row.commune?.trim() || undefined,
        region: row.region?.trim() || undefined,
        pays: row.pays?.trim() || "Madagascar",
      },
    ],
  };
}

export function ImportPage() {
  const { toast } = useToast();
  const [rows, setRows] = useState<CsvRow[]>([]);
  const [nomFichier, setNomFichier] = useState("");
  const [chargement, setChargement] = useState(false);
  const [resultat, setResultat] = useState<{
    crees: number;
    existants: number;
    erreurs: { ligne: number; message: string }[];
  } | null>(null);

  async function chargerFichier(file?: File) {
    setRows([]);
    setResultat(null);
    setNomFichier(file?.name ?? "");
    if (!file) return;
    try {
      const parsed = analyserCsv(await file.text());
      setRows(parsed);
      toast({
        title: `${parsed.length} sociétaire(s) détecté(s) — Fichier prêt pour l'intégration`,
      });
    } catch (error) {
      setNomFichier("");
      toast({
        title: error instanceof Error ? error.message : "Fichier CSV invalide",
        variant: "destructive",
      });
    }
  }

  async function importer() {
    setChargement(true);
    setResultat(null);
    let crees = 0, existants = 0;
    const erreurs: { ligne: number; message: string }[] = [];

    for (const [index, row] of rows.entries()) {
      try {
        const payload = clientDepuisCsv(row);
        try {
          const response = await clientApi.creer(payload);
          if (response.clientTrouve) existants++;
          else crees++;
        } catch (serverError: unknown) {
          const axiosError = serverError as { response?: { data?: { message?: string; error?: string } }; message?: string };
          const msg =
            axiosError?.response?.data?.message ||
            axiosError?.response?.data?.error ||
            axiosError?.message ||
            "Refusé par le serveur.";
          erreurs.push({ ligne: index + 2, message: msg });
        }
      } catch (error) {
        erreurs.push({
          ligne: index + 2,
          message: error instanceof Error ? error.message : "Données de la ligne invalides.",
        });
      }
    }

    setResultat({ crees, existants, erreurs });
    toast({
      title: erreurs.length
        ? `Import terminé avec ${erreurs.length} anomalie(s)`
        : `Importation réussie (${crees} créés, ${existants} déjà enregistrés)`,
      variant: erreurs.length ? "destructive" : undefined,
    });
    setChargement(false);
  }

  function telechargerModele() {
    const entetes = ENTETES_STANDARDS.join(";");
    const exemple1 = "Jean;Rakoto;1985-04-12;HOMME;101234567890;Lot II M 45 Tsianolondroa;Fianarantsoa;Fianarantsoa;Haute Matsiatra;0341234567;CLI-CECAM-001;Malgache;Madagascar";
    const exemple2 = "Marie;Rasoa;1990-09-20;FEMME;201987654321;Tanambao Rue 12;Fianarantsoa;Fianarantsoa;Haute Matsiatra;0329876543;CLI-CECAM-002;Malgache;Madagascar";
    const contenu = `${entetes}\n${exemple1}\n${exemple2}\n`;

    // \uFEFF garantit la bonne reconnaissance UTF-8 par Microsoft Excel sous Windows
    const blob = new Blob(["\uFEFF" + contenu], { type: "text/csv;charset=utf-8" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "modele-import-clients-cecam.csv";
    a.click();
    window.setTimeout(() => URL.revokeObjectURL(url), 10_000);
  }

  return (
    <div className="mx-auto max-w-5xl space-y-6 p-6">
      <Card className="border-sky-100 shadow-sm">
        <CardHeader className="bg-gradient-to-r from-sky-50/60 to-white pb-4">
          <div className="flex items-center gap-2">
            <FileSpreadsheet className="size-5 text-primary" />
            <CardTitle className="text-xl font-bold text-foreground">Importation de sociétaires — CSV</CardTitle>
          </div>
          <p className="text-xs text-muted-foreground mt-1">
            Intégration en masse des sociétaires dans le système Scoring CECAM. Traitement sécurisé exécuté localement dans votre navigateur.
          </p>
        </CardHeader>

        <CardContent className="space-y-5 pt-4 text-sm text-slate-700">
          <div className="rounded-lg border border-sky-200 bg-sky-50/50 p-4">
            <h4 className="font-semibold text-sky-900 mb-1">Règles d'intégration et détection des doublons :</h4>
            <ul className="list-disc list-inside space-y-1 text-xs text-sky-800">
              <li><strong>Colonnes obligatoires :</strong> {ENTETES_REQUISES.join(", ")} (les en-têtes comme <code>cin</code>, <code>CIN</code>, <code>numeroIdentifiant</code>, <code>adresse</code> ou <code>adresseComplete</code> sont automatiquement reconnus).</li>
              <li><strong>Détection doublon CIN :</strong> si le sociétaire existe déjà dans la base CECAM, sa fiche existante est préservée sans créer de doublon.</li>
              <li><strong>Formats de date acceptés :</strong> <code>aaaa-mm-jj</code> ou <code>jj/mm/aaaa</code> (ex: 12/04/1985).</li>
              <li><strong>Séparateur CSV :</strong> point-virgule (<code>;</code>) ou virgule (<code>,</code>).</li>
            </ul>
          </div>

          <div className="flex flex-wrap items-center gap-3">
            <Button type="button" variant="outline" onClick={telechargerModele} className="gap-2">
              <Download className="size-4" />
              Télécharger le modèle CSV officiel
            </Button>

            <label className="inline-flex items-center gap-2 rounded-lg border border-border bg-white px-3 py-2 text-xs font-medium text-slate-700 hover:bg-slate-50 cursor-pointer transition-colors shadow-xs">
              <Upload className="size-4 text-primary" />
              <span>Choisir un fichier CSV…</span>
              <input
                type="file"
                accept=".csv,text/csv"
                className="hidden"
                onChange={(e) => void chargerFichier(e.target.files?.[0])}
              />
            </label>
          </div>

          {nomFichier && (
            <div className="flex items-center gap-2 rounded-md bg-slate-100 px-3 py-2 text-xs font-medium text-slate-800">
              <FileSpreadsheet className="size-4 text-primary" />
              <span>{nomFichier} — <strong>{rows.length}</strong> ligne(s) valide(s) prête(s) à être importée(s).</span>
            </div>
          )}

          {rows.length > 0 && (
            <Button
              onClick={() => void importer()}
              disabled={chargement}
              className="gap-2"
            >
              <Upload className="size-4" />
              {chargement ? "Importation en cours…" : `Intégrer ${rows.length} sociétaire(s)`}
            </Button>
          )}

          {resultat && (
            <div className="space-y-3 pt-2">
              <div className="flex flex-wrap items-center gap-4 text-xs font-semibold">
                <span className="flex items-center gap-1.5 text-emerald-700">
                  <CheckCircle2 className="size-4" />
                  Nouveaux créés : {resultat.crees}
                </span>
                <span className="flex items-center gap-1.5 text-sky-700">
                  <CheckCircle2 className="size-4" />
                  Déjà enregistrés (préservés) : {resultat.existants}
                </span>
                {resultat.erreurs.length > 0 && (
                  <span className="flex items-center gap-1.5 text-red-700">
                    <AlertCircle className="size-4" />
                    Anomalies : {resultat.erreurs.length}
                  </span>
                )}
              </div>

              {resultat.erreurs.length > 0 && (
                <div role="alert" className="rounded-lg border border-red-200 bg-red-50 p-4 text-xs text-red-900">
                  <p className="font-semibold mb-1">
                    Les lignes suivantes n'ont pas pu être intégrées (les autres lignes valides ont été enregistrées) :
                  </p>
                  <ul className="list-disc list-inside space-y-1">
                    {resultat.erreurs.map((err) => (
                      <li key={err.ligne}>
                        <strong>Ligne {err.ligne} :</strong> {err.message}
                      </li>
                    ))}
                  </ul>
                </div>
              )}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
