import { useState } from "react";
import { useForm, useFieldArray, useWatch } from "react-hook-form";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger } from "@/components/ui/select";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { clientApi } from "@/api/clientApi";
import { axiosClient } from "@/api/axiosClient";
import { useToast } from "@/hooks/use-toast";
import { ChampsObligatoiresNote } from "@/components/ui/champs-obligatoires-note";

interface EcheanceLigne {
  numeroEcheance: number; dateEcheance: string; montantDu: string;
  montantPaye: string; datePaiement: string; statut: string;
}

interface FormValues {
  codeContratCb: string; codeContratEtablissement: string; typeContrat: string;
  roleClient: string; phaseDemande: string;
  dateDemande: string; dateDebutContrat: string; dateFinContrat: string;
  montantFinance: string; montantTotalDu: string; montantEcheanceMensuelle: string;
  devise: string; periodicitePaiement: string;
  echeances: EcheanceLigne[];
}

export function SaisieHistoriquePage() {
  const { toast } = useToast();
  const [cinRecherche, setCinRecherche] = useState("");
  const [clientId, setClientId] = useState<number | null>(null);
  const [recherche, setRecherche] = useState(false);

  const { register, control, handleSubmit, setValue } = useForm<FormValues>({
    defaultValues: {
      devise: "Ariary malgache", phaseDemande: "ACTIF", roleClient: "TITULAIRE",
      echeances: [{ numeroEcheance: 1, dateEcheance: "", montantDu: "", montantPaye: "", datePaiement: "", statut: "PAYE_A_TEMPS" }],
    },
  });
  const { fields, append, remove } = useFieldArray({ control, name: "echeances" });

  const roleClient = useWatch({ control, name: "roleClient" });
  const phaseDemande = useWatch({ control, name: "phaseDemande" });
  const echeancesWatch = useWatch({ control, name: "echeances" });

  async function rechercherClient() {
    setRecherche(true);
    try {
      const client = await clientApi.rechercherParCin(cinRecherche);
      if (client) { setClientId(client.id); toast({ title: `Client trouvé : ${client.prenom} ${client.nom}` }); }
      else toast({ title: "Client introuvable pour ce CIN", variant: "destructive" });
    } finally { setRecherche(false); }
  }

  async function onSubmit(values: FormValues) {
    if (!clientId) { toast({ title: "Recherchez d'abord un client par CIN", variant: "destructive" }); return; }
    await axiosClient.post("/contrats/historique", {
      clientId,
      codeContratCb: values.codeContratCb, codeContratEtablissement: values.codeContratEtablissement,
      typeContrat: values.typeContrat, roleClient: values.roleClient, phaseDemande: values.phaseDemande,
      dateDemande: values.dateDemande, dateDebutContrat: values.dateDebutContrat, dateFinContrat: values.dateFinContrat || null,
      montantFinance: Number(values.montantFinance), montantTotalDu: values.montantTotalDu ? Number(values.montantTotalDu) : null,
      montantEcheanceMensuelle: values.montantEcheanceMensuelle ? Number(values.montantEcheanceMensuelle) : null,
      nombreTotalEcheances: values.echeances.length, devise: values.devise, periodicitePaiement: values.periodicitePaiement,
      echeances: values.echeances.map((e) => ({
        numeroEcheance: e.numeroEcheance, dateEcheance: e.dateEcheance, montantDu: Number(e.montantDu),
        montantPaye: e.montantPaye ? Number(e.montantPaye) : null, datePaiement: e.datePaiement || null, statut: e.statut,
      })),
    });
    toast({ title: "Historique enregistré." });
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="mx-auto max-w-4xl space-y-6 p-6">
      <ChampsObligatoiresNote />

      <Card>
        <CardHeader><CardTitle>1. Identifier le client</CardTitle></CardHeader>
        <CardContent className="flex items-end gap-3">
          <div className="flex-1 space-y-1.5">
            <Label className="font-semibold">CIN du client *</Label>
            <Input value={cinRecherche} onChange={(e) => setCinRecherche(e.target.value)} />
          </div>
          <Button type="button" onClick={rechercherClient} disabled={recherche}>Rechercher</Button>
          {clientId && <span className="text-sm text-green-600">Client #{clientId} sélectionné</span>}
        </CardContent>
      </Card>

      <Card>
        <CardHeader><CardTitle>2. Données du contrat</CardTitle></CardHeader>
        <CardContent className="grid grid-cols-1 gap-4 md:grid-cols-3">
          <div className="space-y-1.5"><Label className="font-semibold">Code Contrat CB *</Label><Input className="h-10" {...register("codeContratCb")} /></div>
          <div className="space-y-1.5"><Label>Code Contrat Établissement</Label><Input className="h-10" {...register("codeContratEtablissement")} /></div>
          <div className="space-y-1.5"><Label className="font-semibold">Type de contrat *</Label><Input className="h-10" {...register("typeContrat")} placeholder="Prêt personnel" /></div>

          <div className="space-y-1.5">
            <Label className="font-semibold">Rôle *</Label>
            <Select defaultValue="TITULAIRE" onValueChange={(v) => setValue("roleClient", v as string)}>
              <SelectTrigger className="h-10"><span>{roleClient}</span></SelectTrigger>
              <SelectContent>
                <SelectItem value="TITULAIRE">Titulaire</SelectItem>
                <SelectItem value="CO_TITULAIRE">Co-titulaire</SelectItem>
                <SelectItem value="GARANT">Garant</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div className="space-y-1.5">
            <Label className="font-semibold">Phase *</Label>
            <Select defaultValue="ACTIF" onValueChange={(v) => setValue("phaseDemande", v as string)}>
              <SelectTrigger className="h-10"><span>{phaseDemande}</span></SelectTrigger>
              <SelectContent>
                <SelectItem value="DEMANDE_EN_COURS">Demande en cours</SelectItem>
                <SelectItem value="ACTIF">Actif</SelectItem>
                <SelectItem value="REFUSE">Refusé</SelectItem>
                <SelectItem value="FERME">Fermé</SelectItem>
                <SelectItem value="ABANDONNE">Abandonné / Annulé</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div className="space-y-1.5">
            <Label className="font-semibold">Devise *</Label>
            <Select defaultValue="Ariary malgache" onValueChange={(v) => setValue("devise", v as string)}>
              <SelectTrigger className="h-10"><span>Ariary malgache</span></SelectTrigger>
              <SelectContent><SelectItem value="Ariary malgache">Ariary malgache</SelectItem></SelectContent>
            </Select>
          </div>

          <div className="space-y-1.5"><Label className="font-semibold">Date de la demande *</Label><Input className="h-10" type="date" {...register("dateDemande")} /></div>
          <div className="space-y-1.5"><Label>Date début de contrat</Label><Input className="h-10" type="date" {...register("dateDebutContrat")} /></div>
          <div className="space-y-1.5"><Label>Date fin de contrat</Label><Input className="h-10" type="date" {...register("dateFinContrat")} /></div>

          <div className="space-y-1.5"><Label className="font-semibold">Montant Financé *</Label><Input className="h-10" {...register("montantFinance")} /></div>
          <div className="space-y-1.5"><Label>Montant Total Dû (avec intérêts, si différent)</Label><Input className="h-10" {...register("montantTotalDu")} /></div>
          <div className="space-y-1.5"><Label>Montant Échéance Mensuelle</Label><Input className="h-10" {...register("montantEcheanceMensuelle")} /></div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader><CardTitle>3. Échéances (historique complet)</CardTitle></CardHeader>
        <CardContent className="space-y-4">
          {fields.map((f, i) => (
            <div key={f.id} className="grid grid-cols-2 gap-3 border-b pb-3 md:grid-cols-6">
              <div className="space-y-1"><Label className="text-xs">N°</Label><Input className="h-9" type="number" {...register(`echeances.${i}.numeroEcheance` as const, { valueAsNumber: true })} /></div>
              <div className="space-y-1"><Label className="text-xs">Date échéance</Label><Input className="h-9" type="date" {...register(`echeances.${i}.dateEcheance` as const)} /></div>
              <div className="space-y-1"><Label className="text-xs">Montant dû</Label><Input className="h-9" {...register(`echeances.${i}.montantDu` as const)} /></div>
              <div className="space-y-1"><Label className="text-xs">Montant payé</Label><Input className="h-9" {...register(`echeances.${i}.montantPaye` as const)} /></div>
              <div className="space-y-1"><Label className="text-xs">Date paiement</Label><Input className="h-9" type="date" {...register(`echeances.${i}.datePaiement` as const)} /></div>
              <div className="flex items-end gap-1">
                <Select defaultValue={f.statut} onValueChange={(v) => setValue(`echeances.${i}.statut`, v as string)}>
                  <SelectTrigger className="h-9"><span className="text-xs">{echeancesWatch?.[i]?.statut}</span></SelectTrigger>
                  <SelectContent>
                    <SelectItem value="A_VENIR">À venir</SelectItem>
                    <SelectItem value="PAYE_A_TEMPS">Payé à temps</SelectItem>
                    <SelectItem value="EN_RETARD">En retard</SelectItem>
                    <SelectItem value="IMPAYE">Impayé</SelectItem>
                  </SelectContent>
                </Select>
                <Button type="button" variant="outline" size="sm" onClick={() => remove(i)}>✕</Button>
              </div>
            </div>
          ))}
          <Button type="button" variant="outline"
            onClick={() => append({ numeroEcheance: fields.length + 1, dateEcheance: "", montantDu: "", montantPaye: "", datePaiement: "", statut: "A_VENIR" })}>
            + Ajouter une échéance
          </Button>
        </CardContent>
      </Card>

      <div className="flex justify-end">
        <Button type="submit" size="lg">Enregistrer l'historique</Button>
      </div>
    </form>
  );
}