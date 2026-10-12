import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { modificationContratApi } from "@/api/modificationContratApi";
import type { ModificationContratResponse } from "@/types/modificationContrat";
import { useToast } from "@/hooks/use-toast";

const PHASES: Record<string, string> = {
  DEMANDE_EN_COURS: "Demande en cours", ACTIF: "Actif", REFUSE: "Refusé", FERME: "Fermé", ABANDONNE: "Abandonné / Annulé",
};

export function DemandesModificationPage() {
  const { toast } = useToast();
  const [demandes, setDemandes] = useState<ModificationContratResponse[]>([]);
  const [commentaires, setCommentaires] = useState<Record<number, string>>({});
  const [chargement, setChargement] = useState(true);
  const [traitement, setTraitement] = useState<number | null>(null);

  async function charger() {
    setChargement(true);
    try { setDemandes(await modificationContratApi.listerEnAttente()); }
    catch { toast({ title: "Impossible de charger les demandes en attente.", variant: "destructive" }); }
    finally { setChargement(false); }
  }

  useEffect(() => { void charger(); }, []);

  async function approuver(demande: ModificationContratResponse) {
    setTraitement(demande.id);
    try {
      await modificationContratApi.approuver(demande.id);
      toast({ title: "Mise à jour approuvée et appliquée." });
      await charger();
    } catch { toast({ title: "Impossible d’approuver cette demande.", variant: "destructive" }); }
    finally { setTraitement(null); }
  }

  async function refuser(demande: ModificationContratResponse) {
    setTraitement(demande.id);
    try {
      await modificationContratApi.refuser(demande.id, commentaires[demande.id] ?? "");
      toast({ title: "Demande refusée." });
      await charger();
    } catch { toast({ title: "Impossible de refuser cette demande.", variant: "destructive" }); }
    finally { setTraitement(null); }
  }

  return <div className="mx-auto max-w-5xl space-y-6 p-6">
    <div className="flex flex-wrap items-center justify-between gap-3">
      <div><h1 className="text-2xl font-semibold">Demandes de mise à jour</h1><p className="mt-1 text-base text-muted-foreground">Validation réservée à l’administrateur · {demandes.length} en attente</p></div>
      <Button variant="outline" onClick={() => void charger()} disabled={chargement}>Actualiser</Button>
    </div>
    {chargement ? <p className="text-base text-muted-foreground">Chargement des demandes…</p> : demandes.length === 0 ?
      <Card><CardContent className="py-8 text-center text-base text-muted-foreground">Aucune demande de mise à jour en attente.</CardContent></Card> :
      <div className="space-y-4">{demandes.map((d) => <Card key={d.id}>
        <CardHeader><CardTitle>{d.clientNomComplet} · Contrat {d.codeContratCb}</CardTitle>
          <p className="text-base text-muted-foreground">Soumise par {d.soumisPar} le {new Date(d.dateSoumission).toLocaleString("fr-FR")}</p>
        </CardHeader>
        <CardContent className="space-y-5">
          <dl className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
            <div><dt className="text-sm text-muted-foreground">Phase actuelle → demandée</dt><dd className="text-base font-medium">{PHASES[d.phaseActuelle] ?? d.phaseActuelle} → {PHASES[d.phaseDemande] ?? d.phaseDemande}</dd></div>
            <div><dt className="text-sm text-muted-foreground">Motif de clôture</dt><dd className="text-base">{d.motifCloture === "FIN_A_TERME" ? "Fin à terme" : d.motifCloture === "FIN_ANTICIPEE" ? "Fin anticipée / annulation" : "—"}</dd></div>
            <div><dt className="text-sm text-muted-foreground">Échéance actuelle → demandée</dt><dd className="text-base">{d.montantEcheanceActuel == null ? "—" : d.montantEcheanceActuel.toLocaleString("fr-FR")} → {d.montantEcheanceMensuelle == null ? "—" : d.montantEcheanceMensuelle.toLocaleString("fr-FR")}</dd></div>
            <div><dt className="text-sm text-muted-foreground">Code Contrat CECAM (actuel → demandé)</dt><dd className="text-base">{d.codeContratEtablissementActuel || "—"} → {d.codeContratEtablissement || "—"}</dd></div>
            <div><dt className="text-sm text-muted-foreground">Code Demande CECAM (actuel → demandé)</dt><dd className="text-base">{d.codeDemandeEtablissementActuel || "—"} → {d.codeDemandeEtablissement || "—"}</dd></div>
            <div><dt className="text-sm text-muted-foreground">Devise (actuelle → demandée)</dt><dd className="text-base">{d.deviseActuelle || "—"} → {d.devise}</dd></div>
          </dl>
          <div><p className="text-sm text-muted-foreground">Justification de l’agent</p><p className="mt-1 whitespace-pre-wrap text-base">{d.justification}</p></div>
          <div className="space-y-2"><Label htmlFor={`commentaire-${d.id}`}>Commentaire admin en cas de refus (facultatif)</Label>
            <textarea id={`commentaire-${d.id}`} rows={2} maxLength={500} value={commentaires[d.id] ?? ""} onChange={(e) => setCommentaires((v) => ({ ...v, [d.id]: e.target.value }))} className="w-full rounded-lg border border-input bg-background px-3 py-2 text-base outline-none focus-visible:ring-3 focus-visible:ring-ring/50" />
          </div>
          <div className="flex flex-wrap gap-3">
            <Button onClick={() => void approuver(d)} disabled={traitement !== null}>{traitement === d.id ? "Traitement…" : "Approuver et appliquer"}</Button>
            <Button variant="outline" onClick={() => void refuser(d)} disabled={traitement !== null}>Refuser</Button>
          </div>
        </CardContent>
      </Card>)}</div>}
  </div>;
}
