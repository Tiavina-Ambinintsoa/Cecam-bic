import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { MontantInput } from "@/components/ui/montant-input";
import { contratApi } from "@/api/contratApi";
import { modificationContratApi } from "@/api/modificationContratApi";
import type { ContratResponse, MotifCloture, PhaseDemande } from "@/types/contrat";
import type { ModificationContratRequest, ModificationContratResponse } from "@/types/modificationContrat";
import { useToast } from "@/hooks/use-toast";

const PHASES: { value: PhaseDemande; label: string }[] = [
  { value: "DEMANDE_EN_COURS", label: "Demande en cours" },
  { value: "ACTIF", label: "Actif" },
  { value: "REFUSE", label: "Refusé" },
  { value: "FERME", label: "Fermé" },
  { value: "ABANDONNE", label: "Abandonné / Annulé" },
];
const DEVISES = ["Ariary malgache", "Euro", "Dollar américain"];
const PHASE_LABELS = Object.fromEntries(PHASES.map(({ value, label }) => [value, label])) as Record<PhaseDemande, string>;

export function MiseAJourDemandePage() {
  const { toast } = useToast();
  const [codeRecherche, setCodeRecherche] = useState("");
  const [contrat, setContrat] = useState<ContratResponse | null>(null);
  const [phase, setPhase] = useState<PhaseDemande | "">("");
  const [motif, setMotif] = useState<MotifCloture | "">("");
  const [codeContratEtablissement, setCodeContratEtablissement] = useState("");
  const [codeDemandeEtablissement, setCodeDemandeEtablissement] = useState("");
  const [montant, setMontant] = useState("");
  const [devise, setDevise] = useState("Ariary malgache");
  const [justification, setJustification] = useState("");
  const [chargement, setChargement] = useState(false);
  const [envoi, setEnvoi] = useState(false);
  const [mesDemandes, setMesDemandes] = useState<ModificationContratResponse[]>([]);

  async function chargerMesDemandes() {
    try { setMesDemandes(await modificationContratApi.listerMesDemandes()); } catch { /* La saisie reste disponible si l'historique ne charge pas. */ }
  }

  useEffect(() => { void chargerMesDemandes(); }, []);

  async function rechercher() {
    setChargement(true); setContrat(null);
    try {
      const trouve = await contratApi.rechercherParIdentifiant(codeRecherche.trim());
      if (!trouve) { toast({ title: "Demande introuvable — saisissez le code Contrat CECAM ou le code Contrat CB.", variant: "destructive" }); return; }
      setContrat(trouve);
      setPhase(trouve.phaseDemande as PhaseDemande);
      setCodeContratEtablissement(trouve.codeContratEtablissement ?? "");
      setCodeDemandeEtablissement(trouve.codeDemandeEtablissement ?? "");
      setMontant(trouve.montantEcheanceMensuelle == null ? "" : String(trouve.montantEcheanceMensuelle));
      setDevise(trouve.devise || "Ariary malgache");
      setMotif(trouve.motifCloture ?? ""); setJustification("");
    } finally { setChargement(false); }
  }

  async function soumettre() {
    if (!contrat || !phase) return;
    if (!justification.trim()) {
      toast({ title: "Justification obligatoire — expliquez la raison de la mise à jour.", variant: "destructive" });
      return;
    }
    if ((phase === "ABANDONNE" || phase === "FERME") && !motif) {
      toast({ title: "Motif obligatoire — sélectionnez le motif de clôture ou d'annulation.", variant: "destructive" });
      return;
    }
    const montantNormalise = montant.trim().replace(/[\s\u00a0]/g, "").replace(",", ".");
    const montantNumerique = montantNormalise ? Number(montantNormalise) : null;
    if (montantNumerique !== null && (!Number.isFinite(montantNumerique) || montantNumerique < 0)) {
      toast({ title: "Montant invalide", variant: "destructive" }); return;
    }

    const payload: ModificationContratRequest = {
      phaseDemande: phase,
      motifCloture: motif || null,
      codeContratEtablissement: codeContratEtablissement.trim() || null,
      codeDemandeEtablissement: codeDemandeEtablissement.trim() || null,
      montantEcheanceMensuelle: montantNumerique,
      devise,
      justification: justification.trim(),
    };
    setEnvoi(true);
    try {
      await modificationContratApi.soumettre(contrat.id, payload);
      toast({ title: "Demande envoyée à l’administrateur — changements appliqués après validation." });
      setJustification("");
      await chargerMesDemandes();
    } catch (error) {
      const status = (error as { response?: { status?: number } }).response?.status;
      toast({ title: status === 409 ? "Une demande est déjà en attente pour ce contrat." : "Envoi impossible", variant: "destructive" });
    } finally { setEnvoi(false); }
  }

  return (
    <div className="mx-auto max-w-5xl space-y-6 p-6">
      <Card>
        <CardHeader><CardTitle>Mise à jour demande — Saisie</CardTitle></CardHeader>
        <CardContent className="flex flex-col items-stretch gap-3 sm:flex-row sm:items-end">
          <div className="flex-1 space-y-1.5"><Label htmlFor="identifiant-contrat">Code Contrat CECAM ou Code Contrat CB</Label>
            <Input id="identifiant-contrat" value={codeRecherche} onChange={(e) => setCodeRecherche(e.target.value)} onKeyDown={(e) => { if (e.key === "Enter") { e.preventDefault(); void rechercher(); } }} />
          </div>
          <Button onClick={() => void rechercher()} disabled={!codeRecherche.trim() || chargement}>{chargement ? "Recherche…" : "Rechercher"}</Button>
        </CardContent>
      </Card>

      {contrat && <>
      <Card>
          <CardHeader><CardTitle>Codes relatifs à la demande</CardTitle><p className="text-base text-slate-600">Client : {[contrat.client?.prenom, contrat.client?.nom].filter(Boolean).join(" ")} · Contrat CB : {contrat.codeContratCb}</p></CardHeader>
          <CardContent className="space-y-4">
            <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
              <div className="space-y-1.5"><Label htmlFor="code-contrat">Code Contrat CECAM</Label><Input id="code-contrat" value={codeContratEtablissement} onChange={(e) => setCodeContratEtablissement(e.target.value)} /></div>
              <div className="space-y-1.5"><Label htmlFor="code-demande">Code Demande CECAM</Label><Input id="code-demande" value={codeDemandeEtablissement} onChange={(e) => setCodeDemandeEtablissement(e.target.value)} /></div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader><CardTitle>Modification soumise à validation</CardTitle><p className="text-base text-slate-600">Aucun changement ne sera appliqué avant l’approbation de l’administrateur.</p></CardHeader>
          <CardContent className="space-y-5">
            <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
              <div className="space-y-1.5"><Label htmlFor="phase">Phase de la demande</Label>
                <Select value={phase} onValueChange={(v) => { if (v) setPhase(v as PhaseDemande); }}>
                  <SelectTrigger id="phase"><SelectValue placeholder="Sélectionner" /></SelectTrigger>
                  <SelectContent>{PHASES.map((p) => <SelectItem key={p.value} value={p.value}>{p.label}</SelectItem>)}</SelectContent>
                </Select>
              </div>
              {(phase === "ABANDONNE" || phase === "FERME") && <div className="space-y-1.5"><Label htmlFor="motif">Motif de clôture / annulation</Label>
                <Select value={motif} onValueChange={(v) => { if (v) setMotif(v as MotifCloture); }}>
                  <SelectTrigger id="motif"><SelectValue placeholder="Sélectionner" /></SelectTrigger>
                  <SelectContent>
                    <SelectItem value="FIN_A_TERME">Fin à terme</SelectItem>
                    <SelectItem value="FIN_ANTICIPEE">Fin anticipée / annulation</SelectItem>
                  </SelectContent>
                </Select>
              </div>}
            </div>
            <div className="grid grid-cols-1 gap-4 border-t pt-4 md:grid-cols-2">
              <div className="space-y-1.5"><Label htmlFor="montant-echeance">Montant échéance mensuelle</Label><MontantInput id="montant-echeance" value={montant} onValueChange={setMontant} /></div>
              <div className="space-y-1.5"><Label>Devise</Label>
                <Select value={devise} onValueChange={(v) => { if (v) setDevise(v); }}>
                  <SelectTrigger><SelectValue /></SelectTrigger><SelectContent>{DEVISES.map((d) => <SelectItem key={d} value={d}>{d}</SelectItem>)}</SelectContent>
                </Select>
              </div>
            </div>
            <div className="space-y-1.5"><Label htmlFor="justification">Justification de la mise à jour <span className="text-red-600">*</span></Label>
              <textarea id="justification" value={justification} onChange={(e) => setJustification(e.target.value)} rows={3} maxLength={500} className="w-full rounded-lg border border-input bg-background px-3 py-2 text-base outline-none focus-visible:ring-3 focus-visible:ring-ring/50" />
            </div>
            <Button onClick={() => void soumettre()} disabled={envoi}>{envoi ? "Envoi…" : "Envoyer la demande à l’administrateur"}</Button>
          </CardContent>
        </Card>
      </>}

      <Card>
        <CardHeader><CardTitle>Mes demandes de modification</CardTitle></CardHeader>
        <CardContent>
          {mesDemandes.length === 0 ? <p className="text-base text-muted-foreground">Aucune demande envoyée.</p> :
            <ul className="divide-y">{mesDemandes.map((d) => <li key={d.id} className="flex flex-wrap items-center justify-between gap-2 py-3 text-base">
              <span>{d.clientNomComplet} · {d.codeContratCb} · {PHASE_LABELS[d.phaseDemande]}</span>
              <span className={d.statut === "EN_ATTENTE" ? "font-semibold text-amber-700" : d.statut === "APPROUVEE" ? "font-semibold text-green-700" : "font-semibold text-red-700"}>
                {d.statut === "EN_ATTENTE" ? "En attente" : d.statut === "APPROUVEE" ? "Approuvée" : "Refusée"}
              </span>
              {d.statut === "REFUSEE" && d.commentaireAdmin && <p className="w-full text-sm text-slate-600">Motif du refus : {d.commentaireAdmin}</p>}
            </li>)}</ul>}
        </CardContent>
      </Card>
    </div>
  );
}
