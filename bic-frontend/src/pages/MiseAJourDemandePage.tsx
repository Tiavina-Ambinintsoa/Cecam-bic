import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { clientApi } from "@/api/clientApi";
import { contratApi } from "@/api/contratApi";
import type { ClientResponse } from "@/types/client";
import type { ContratResponse } from "@/types/contrat";

const PHASES = [
  { value: "DEMANDE_EN_COURS", label: "Demande en cours" },
  { value: "ACTIF", label: "Actif" },
  { value: "REFUSE", label: "Refusé" },
  { value: "ABANDONNE", label: "Abandonné / Annulé" },
  { value: "FERME", label: "Fermé" },
];

export function MiseAJourDemandePage() {
  const [numero, setNumero] = useState("");
  const [client, setClient] = useState<ClientResponse | null>(null);
  const [contrats, setContrats] = useState<ContratResponse[]>([]);
  const [nouvellesPhases, setNouvellesPhases] = useState<Record<number, string>>({});
  const [nouvelleAdresse, setNouvelleAdresse] = useState({ typeAdresse: "Individu - Adresse secondaire", adresseComplete: "" });

  async function rechercher() {
    const found = await clientApi.rechercherParCin(numero);
    setClient(found);
    if (found) setContrats(await contratApi.listerParClient(found.id));
  }

  async function appliquerPhase(contratId: number) {
    const phase = nouvellesPhases[contratId];
    if (!phase) return;
    await contratApi.changerPhase(contratId, phase);
    if (client) setContrats(await contratApi.listerParClient(client.id));
    window.alert("Phase mise à jour.");
  }

  async function ajouterAdresse() {
    if (!client || !nouvelleAdresse.adresseComplete) return;
    await clientApi.ajouterAdresse(client.id, nouvelleAdresse);
    setNouvelleAdresse({ typeAdresse: "Individu - Adresse secondaire", adresseComplete: "" });
    window.alert("Adresse ajoutée.");
  }

  return (
    <div className="mx-auto max-w-2xl space-y-6 p-6">
      <Card>
        <CardHeader><CardTitle>Mise à jour demande</CardTitle></CardHeader>
        <CardContent className="flex items-end gap-3">
          <div className="flex-1 space-y-1.5">
            <Label>Numéro CIN du client</Label>
            <Input value={numero} onChange={(e) => setNumero(e.target.value)} placeholder="Ex : 301021985001" />
          </div>
          <Button onClick={rechercher} disabled={!numero}>Rechercher</Button>
        </CardContent>
      </Card>

      {client && (
        <>
          <Card>
            <CardHeader><CardTitle>{client.prenom} {client.nom} — {client.codeClientCb}</CardTitle></CardHeader>
            <CardContent className="space-y-3">
              {contrats.map((c) => (
                <div key={c.id} className="flex items-center gap-3 rounded-md border p-3">
                  <div className="flex-1 text-sm">
                    <p className="font-medium">{c.codeContratCb} — {c.typeContrat}</p>
                    <p className="text-slate-500">Phase actuelle : {c.phaseDemande}</p>
                  </div>
                  <Select onValueChange={(v) => setNouvellesPhases({ ...nouvellesPhases, [c.id]: v as string })}>
                    <SelectTrigger className="w-48"><SelectValue placeholder="Nouvelle phase" /></SelectTrigger>
                    <SelectContent>
                      {PHASES.map((p) => <SelectItem key={p.value} value={p.value}>{p.label}</SelectItem>)}
                    </SelectContent>
                  </Select>
                  <Button size="sm" onClick={() => appliquerPhase(c.id)}>Appliquer</Button>
                </div>
              ))}
            </CardContent>
          </Card>

          <Card>
            <CardHeader><CardTitle>Ajouter une adresse</CardTitle></CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-1.5">
                <Label>Adresse complète</Label>
                <Input value={nouvelleAdresse.adresseComplete} onChange={(e) => setNouvelleAdresse({ ...nouvelleAdresse, adresseComplete: e.target.value })} />
              </div>
              <Button variant="outline" onClick={ajouterAdresse}>+ Ajouter cette adresse</Button>
            </CardContent>
          </Card>
        </>
      )}
    </div>
  );
}