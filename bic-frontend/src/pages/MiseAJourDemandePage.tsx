import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { MontantInput } from "@/components/ui/montant-input";
import { contratApi } from "@/api/contratApi";
import type { ContratResponse } from "@/types/contrat";
import { useToast } from "@/hooks/use-toast";

const PHASES = [
  { value: "DEMANDE_EN_COURS", label: "Demande en cours" },
  { value: "ACTIF", label: "Actif" },
  { value: "REFUSE", label: "Refusé" },
  { value: "FERME", label: "Fermé" },
  { value: "ABANDONNE", label: "Abandonné / Annulé" },
];
const DEVISES = ["Ariary malgache", "Euro", "Dollar américain"];

export function MiseAJourDemandePage() {
  const { toast } = useToast();
  const [codeRecherche, setCodeRecherche] = useState("");
  const [contrat, setContrat] = useState<ContratResponse | null>(null);
  const [phase, setPhase] = useState("");
  const [motif, setMotif] = useState("");
  const [codeContratEtablissement, setCodeContratEtablissement] = useState("");
  const [codeDemandeEtablissement, setCodeDemandeEtablissement] = useState("");
  const [montant, setMontant] = useState("");
  const [devise, setDevise] = useState("Ariary malgache");
  const [chargement, setChargement] = useState(false);

  async function rechercher() {
    setChargement(true); setContrat(null);
    try {
      const trouve = await contratApi.rechercherParCodeEtablissement(codeRecherche.trim());
      if (!trouve) { toast({ title: "Demande introuvable", variant: "destructive" }); return; }
      setContrat(trouve); setPhase(trouve.phaseDemande);
      setCodeContratEtablissement(trouve.codeContratEtablissement ?? "");
      setCodeDemandeEtablissement(trouve.codeDemandeEtablissement ?? "");
      setMontant(trouve.montantEcheanceMensuelle == null ? "" : String(trouve.montantEcheanceMensuelle));
      setDevise(trouve.devise || "Ariary malgache");
      setMotif("");
    } finally { setChargement(false); }
  }

  async function appliquerPhase() {
    if (!contrat || !phase) return;
    await contratApi.changerPhase(contrat.id, phase, motif);
    toast({ title: "Phase mise à jour." });
  }

  async function appliquerCodes() {
    if (!contrat) return;
    await contratApi.changerCodesEtablissement(contrat.id, codeContratEtablissement, codeDemandeEtablissement);
    toast({ title: "Codes de demande mis à jour." });
  }

  async function appliquerMontant() {
    if (!contrat) return;
    const valeur = montant.trim() ? Number(montant.replace(/\s/g, "")) : null;
    if (valeur !== null && (!Number.isFinite(valeur) || valeur < 0)) {
      toast({ title: "Montant invalide", variant: "destructive" }); return;
    }
    await contratApi.changerEcheanceMensuelle(contrat.id, valeur, devise);
    toast({ title: "Échéance mensuelle mise à jour." });
  }

  return (
    <div className="mx-auto max-w-3xl space-y-6 p-6">
      <Card>
        <CardHeader><CardTitle>Mise à jour demande — Saisie</CardTitle></CardHeader>
        <CardContent className="flex items-end gap-3">
          <div className="flex-1 space-y-1.5"><Label>Code Contrat établissement</Label>
            <Input value={codeRecherche} onChange={(e) => setCodeRecherche(e.target.value)} onKeyDown={(e) => { if (e.key === "Enter") void rechercher(); }} />
          </div>
          <Button onClick={rechercher} disabled={!codeRecherche.trim() || chargement}>{chargement ? "Recherche…" : "Rechercher"}</Button>
        </CardContent>
      </Card>

      {contrat && <>
        <Card>
          <CardHeader><CardTitle>Codes relatifs à la demande</CardTitle><p className="text-sm text-slate-500">Contrat CB : {contrat.codeContratCb}</p></CardHeader>
          <CardContent className="space-y-4">
            <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
              <div className="space-y-1.5"><Label>Code Contrat établissement</Label><Input value={codeContratEtablissement} onChange={(e) => setCodeContratEtablissement(e.target.value)} /></div>
              <div className="space-y-1.5"><Label>Code Demande établissement</Label><Input value={codeDemandeEtablissement} onChange={(e) => setCodeDemandeEtablissement(e.target.value)} /></div>
            </div>
            <Button variant="outline" onClick={appliquerCodes}>Enregistrer les codes</Button>
          </CardContent>
        </Card>

        <Card>
          <CardHeader><CardTitle>Mise à jour</CardTitle></CardHeader>
          <CardContent className="space-y-5">
            <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
              <div className="space-y-1.5"><Label>Phase de la demande</Label>
                <Select value={phase} onValueChange={(v) => { if (v) setPhase(v); }}>
                  <SelectTrigger><SelectValue placeholder="Sélectionner" /></SelectTrigger>
                  <SelectContent>{PHASES.map((p) => <SelectItem key={p.value} value={p.value}>{p.label}</SelectItem>)}</SelectContent>
                </Select>
              </div>
              {(phase === "ABANDONNE" || phase === "FERME") && <div className="space-y-1.5"><Label>Motif de clôture / annulation</Label>
                <Select value={motif} onValueChange={(v) => { if (v) setMotif(v); }}>
                  <SelectTrigger><SelectValue placeholder="Sélectionner" /></SelectTrigger>
                  <SelectContent>
                    <SelectItem value="FIN_A_TERME">Fin à terme</SelectItem>
                    <SelectItem value="FIN_ANTICIPEE">Fin anticipée / annulation</SelectItem>
                  </SelectContent>
                </Select>
              </div>}
            </div>
            <Button variant="outline" onClick={appliquerPhase}>Enregistrer la phase</Button>
            <div className="grid grid-cols-1 gap-4 border-t pt-4 md:grid-cols-2">
              <div className="space-y-1.5"><Label>Montant échéance mensuelle</Label><MontantInput value={montant} onValueChange={setMontant} /></div>
              <div className="space-y-1.5"><Label>Devise</Label>
                <Select value={devise} onValueChange={(v) => { if (v) setDevise(v); }}>
                  <SelectTrigger><SelectValue /></SelectTrigger><SelectContent>{DEVISES.map((d) => <SelectItem key={d} value={d}>{d}</SelectItem>)}</SelectContent>
                </Select>
              </div>
            </div>
            <Button onClick={appliquerMontant}>Enregistrer l’échéance</Button>
          </CardContent>
        </Card>
      </>}
    </div>
  );
}
