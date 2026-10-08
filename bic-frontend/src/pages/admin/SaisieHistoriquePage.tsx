import { useState } from "react";
import { useForm, useFieldArray, useWatch, Controller } from "react-hook-form";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger } from "@/components/ui/select";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { DateInput } from "@/components/ui/date-input";
import { MontantInput } from "@/components/ui/montant-input";
import { ChampsObligatoiresNote } from "@/components/ui/champs-obligatoires-note";
import { clientApi } from "@/api/clientApi";
import { axiosClient } from "@/api/axiosClient";
import { useToast } from "@/hooks/use-toast";

const ROLES: Record<string, string> = { TITULAIRE: "Titulaire", CO_TITULAIRE: "Co-titulaire", GARANT: "Garant" };
const ROLES_LIES: Record<string, string> = { CO_TITULAIRE: "Co-titulaire", GARANT: "Garant" };
const PHASES: Record<string, string> = {
  DEMANDE_EN_COURS: "Demande en cours", ACTIF: "Actif", REFUSE: "Refusé", FERME: "Fermé", ABANDONNE: "Abandonné / Annulé",
};
const MOTIFS: Record<string, string> = { "": "Aucun", FIN_A_TERME: "Fin à terme", FIN_ANTICIPEE: "Fin anticipée" };
const PERIODICITES: Record<string, string> = {
  Mensuelle: "Mensuelle", Trimestrielle: "Trimestrielle", Semestrielle: "Semestrielle", Annuelle: "Annuelle",
};
const NATURES: Record<string, string> = { REELLE: "Garantie réelle", PERSONNELLE: "Garantie personnelle (signature)" };

interface EcheanceLigne { numeroEcheance: number; dateEcheance: string; montantDu: string; montantPaye: string; datePaiement: string; }
interface GarantieLigne {
  nature: string; typeGarantie: string; codeEtablissementGarantie: string; nomGarant: string;
  codeClientCbGarant: string; montantCouvert: string; dateDebutValidite: string; dateFinValidite: string;
}
interface LienLigne { codeClientCb: string; nomClient: string; roleClient: string; }

interface FormValues {
  codeContratCb: string; codeContratEtablissement: string; codeEtablissementDeclarant: string;
  typeContrat: string; typeRelationEntreprise: string; roleClient: string; phaseDemande: string; motifCloture: string;
  devise: string; periodicitePaiement: string;
  dateDemande: string; dateDebutContrat: string; datePremiereEcheance: string; dateFinContrat: string;
  montantFinance: string; montantTotalDu: string; montantEcheanceMensuelle: string;
  codeRestructuration: string; dateCodeRestructuration: string; note: string;
  echeances: EcheanceLigne[]; garanties: GarantieLigne[]; liens: LienLigne[];
}

function ChampSelect(props: {
  label: string; valeur: string; options: Record<string, string>; onChange: (v: string) => void; obligatoire?: boolean;
}) {
  return (
    <div className="space-y-1.5">
      <Label className={props.obligatoire ? "font-semibold" : undefined}>
        {props.label}{props.obligatoire && <span className="text-red-500"> *</span>}
      </Label>
      <Select value={props.valeur} onValueChange={(v) => props.onChange(v as string)}>
        <SelectTrigger className="h-10"><span>{props.options[props.valeur] ?? "Sélectionner"}</span></SelectTrigger>
        <SelectContent>
          {Object.entries(props.options).map(([cle, libelle]) => (
            <SelectItem key={cle} value={cle}>{libelle}</SelectItem>
          ))}
        </SelectContent>
      </Select>
    </div>
  );
}

function messageErreur(e: unknown): string {
  const data = (e as { response?: { data?: { message?: string; detail?: string } } })?.response?.data;
  return data?.message ?? data?.detail ?? "vérifiez les champs saisis.";
}

const nombreOuNull = (v: string) => (v ? Number(v) : null);
const texteOuNull = (v: string) => (v && v.trim() ? v.trim() : null);

export function SaisieHistoriquePage() {
  const { toast } = useToast();
  const [cin, setCin] = useState("");
  const [codeCb, setCodeCb] = useState("");
  const [client, setClient] = useState<{ id: number; libelle: string } | null>(null);
  const [recherche, setRecherche] = useState(false);

  const { register, control, handleSubmit, setValue } = useForm<FormValues>({
    defaultValues: {
      devise: "Ariary malgache", phaseDemande: "FERME", roleClient: "TITULAIRE", motifCloture: "FIN_A_TERME",
      codeEtablissementDeclarant: "001", periodicitePaiement: "Mensuelle", typeContrat: "Prêt Personnel",
      echeances: [{ numeroEcheance: 1, dateEcheance: "", montantDu: "", montantPaye: "", datePaiement: "" }],
      garanties: [], liens: [],
    },
  });
  const echeances = useFieldArray({ control, name: "echeances" });
  const garanties = useFieldArray({ control, name: "garanties" });
  const liens = useFieldArray({ control, name: "liens" });

  const roleClient = useWatch({ control, name: "roleClient" });
  const phaseDemande = useWatch({ control, name: "phaseDemande" });
  const motifCloture = useWatch({ control, name: "motifCloture" });
  const periodicite = useWatch({ control, name: "periodicitePaiement" });
  const garantiesWatch = useWatch({ control, name: "garanties" });
  const liensWatch = useWatch({ control, name: "liens" });

  async function rechercherClient() {
    setRecherche(true);
    try {
      const trouve = codeCb.trim()
        ? await clientApi.rechercherParCodeCb(codeCb.trim())
        : await clientApi.rechercherParCin(cin.trim());
      if (trouve) {
        setClient({ id: trouve.id, libelle: `${trouve.prenom} ${trouve.nom} (${trouve.codeClientCb})` });
        toast({ title: `Client trouvé : ${trouve.prenom} ${trouve.nom}` });
      } else {
        setClient(null);
        toast({ title: "Client introuvable", variant: "destructive" });
      }
    } finally {
      setRecherche(false);
    }
  }

  async function onSubmit(v: FormValues) {
    if (!client) { toast({ title: "Recherchez d'abord un client", variant: "destructive" }); return; }
    try {
      await axiosClient.post("/contrats/historique", {
        clientId: client.id,
        codeContratCb: texteOuNull(v.codeContratCb),
        codeContratEtablissement: texteOuNull(v.codeContratEtablissement),
        codeEtablissementDeclarant: texteOuNull(v.codeEtablissementDeclarant),
        typeContrat: v.typeContrat,
        typeRelationEntreprise: texteOuNull(v.typeRelationEntreprise),
        roleClient: v.roleClient,
        phaseDemande: v.phaseDemande,
        motifCloture: v.motifCloture || null,
        devise: v.devise,
        periodicitePaiement: texteOuNull(v.periodicitePaiement),
        dateDemande: v.dateDemande,
        dateDebutContrat: v.dateDebutContrat || null,
        datePremiereEcheance: v.datePremiereEcheance || null,
        dateFinContrat: v.dateFinContrat || null,
        montantFinance: Number(v.montantFinance),
        montantTotalDu: nombreOuNull(v.montantTotalDu),
        montantEcheanceMensuelle: nombreOuNull(v.montantEcheanceMensuelle),
        nombreTotalEcheances: v.echeances.length,
        codeRestructuration: texteOuNull(v.codeRestructuration),
        dateCodeRestructuration: v.dateCodeRestructuration || null,
        note: texteOuNull(v.note),
        echeances: v.echeances.map((e) => ({
          numeroEcheance: e.numeroEcheance,
          dateEcheance: e.dateEcheance,
          montantDu: Number(e.montantDu),
          montantPaye: nombreOuNull(e.montantPaye),
          datePaiement: e.datePaiement || null,
        })),
        garanties: v.garanties.map((g) => ({
          nature: g.nature, typeGarantie: g.typeGarantie,
          codeEtablissementGarantie: texteOuNull(g.codeEtablissementGarantie),
          nomGarant: texteOuNull(g.nomGarant), codeClientCbGarant: texteOuNull(g.codeClientCbGarant),
          montantCouvert: nombreOuNull(g.montantCouvert),
          dateDebutValidite: g.dateDebutValidite || null, dateFinValidite: g.dateFinValidite || null,
        })),
        liens: v.liens.map((l) => ({
          codeClientCb: l.codeClientCb, nomClient: texteOuNull(l.nomClient), roleClient: l.roleClient,
        })),
      });
      toast({ title: "Historique enregistré." });
    } catch (e) {
      toast({ title: `Enregistrement refusé : ${messageErreur(e)}`, variant: "destructive" });
    }
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="mx-auto max-w-5xl space-y-6 p-6">
      <ChampsObligatoiresNote />

      <Card>
        <CardHeader><CardTitle>1. Identifier le client (titulaire du contrat)</CardTitle></CardHeader>
        <CardContent className="flex flex-wrap items-end gap-3">
          <div className="min-w-56 flex-1 space-y-1.5">
            <Label>CIN</Label>
            <Input value={cin} onChange={(e) => setCin(e.target.value)} />
          </div>
          <div className="min-w-56 flex-1 space-y-1.5">
            <Label>ou Code client CB</Label>
            <Input value={codeCb} onChange={(e) => setCodeCb(e.target.value)} />
          </div>
          <Button type="button" onClick={rechercherClient} disabled={recherche}>Rechercher</Button>
          {client && <span className="text-sm text-green-600">{client.libelle}</span>}
        </CardContent>
      </Card>

      <Card>
        <CardHeader><CardTitle>2. Contrat</CardTitle></CardHeader>
        <CardContent className="grid grid-cols-1 gap-4 md:grid-cols-3">
          <div className="space-y-1.5"><Label>Code Contrat CB (généré si vide)</Label><Input className="h-10" {...register("codeContratCb")} /></div>
          <div className="space-y-1.5"><Label>Code Contrat Établissement</Label><Input className="h-10" {...register("codeContratEtablissement")} /></div>
          <div className="space-y-1.5"><Label>Code Établissement déclarant</Label><Input className="h-10" {...register("codeEtablissementDeclarant")} /></div>

          <div className="space-y-1.5"><Label className="font-semibold">Type de contrat <span className="text-red-500">*</span></Label><Input className="h-10" {...register("typeContrat")} /></div>
          <div className="space-y-1.5"><Label>Type de relation avec l'entreprise</Label><Input className="h-10" {...register("typeRelationEntreprise")} /></div>
          <ChampSelect obligatoire label="Rôle du client" valeur={roleClient} options={ROLES} onChange={(v) => setValue("roleClient", v)} />

          <ChampSelect obligatoire label="Phase" valeur={phaseDemande} options={PHASES} onChange={(v) => setValue("phaseDemande", v)} />
          <ChampSelect label="Motif de clôture / d'annulation" valeur={motifCloture} options={MOTIFS} onChange={(v) => setValue("motifCloture", v)} />
          <ChampSelect label="Périodicité de paiement" valeur={periodicite} options={PERIODICITES} onChange={(v) => setValue("periodicitePaiement", v)} />

          <div className="space-y-1.5">
            <Label className="font-semibold">Date de la demande <span className="text-red-500">*</span></Label>
            <Controller control={control} name="dateDemande" render={({ field }) => (
              <DateInput className="h-10" value={field.value} onValueChange={field.onChange} />)} />
          </div>
          <div className="space-y-1.5">
            <Label>Date début de contrat</Label>
            <Controller control={control} name="dateDebutContrat" render={({ field }) => (
              <DateInput className="h-10" value={field.value} onValueChange={field.onChange} />)} />
          </div>
          <div className="space-y-1.5">
            <Label>Date première échéance</Label>
            <Controller control={control} name="datePremiereEcheance" render={({ field }) => (
              <DateInput className="h-10" value={field.value} onValueChange={field.onChange} />)} />
          </div>
          <div className="space-y-1.5">
            <Label>Date fin de contrat</Label>
            <Controller control={control} name="dateFinContrat" render={({ field }) => (
              <DateInput className="h-10" value={field.value} onValueChange={field.onChange} />)} />
          </div>

          <div className="space-y-1.5">
            <Label className="font-semibold">Montant financé <span className="text-red-500">*</span></Label>
            <Controller control={control} name="montantFinance" render={({ field }) => (
              <MontantInput className="h-10" value={field.value} onValueChange={field.onChange} />)} />
          </div>
          <div className="space-y-1.5">
            <Label>Montant total dû (avec intérêts)</Label>
            <Controller control={control} name="montantTotalDu" render={({ field }) => (
              <MontantInput className="h-10" value={field.value} onValueChange={field.onChange} />)} />
          </div>
          <div className="space-y-1.5">
            <Label>Montant échéance mensuelle</Label>
            <Controller control={control} name="montantEcheanceMensuelle" render={({ field }) => (
              <MontantInput className="h-10" value={field.value} onValueChange={field.onChange} />)} />
          </div>

          <div className="space-y-1.5"><Label>Code restructuration</Label><Input className="h-10" {...register("codeRestructuration")} /></div>
          <div className="space-y-1.5">
            <Label>Date code restructuration</Label>
            <Controller control={control} name="dateCodeRestructuration" render={({ field }) => (
              <DateInput className="h-10" value={field.value} onValueChange={field.onChange} />)} />
          </div>
          <div className="space-y-1.5"><Label>Note</Label><Input className="h-10" {...register("note")} /></div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader><CardTitle>3. Échéances (le statut est calculé automatiquement)</CardTitle></CardHeader>
        <CardContent className="space-y-4">
          {echeances.fields.map((f, i) => (
            <div key={f.id} className="grid grid-cols-2 gap-3 border-b pb-3 md:grid-cols-6">
              <div className="space-y-1"><Label className="text-xs">N°</Label>
                <Input className="h-10" type="number" {...register(`echeances.${i}.numeroEcheance` as const, { valueAsNumber: true })} /></div>
              <div className="space-y-1"><Label className="text-xs">Date échéance</Label>
                <Controller control={control} name={`echeances.${i}.dateEcheance` as const} render={({ field }) => (
                  <DateInput className="h-10" value={field.value} onValueChange={field.onChange} />)} /></div>
              <div className="space-y-1"><Label className="text-xs">Montant dû</Label>
                <Controller control={control} name={`echeances.${i}.montantDu` as const} render={({ field }) => (
                  <MontantInput className="h-10" value={field.value} onValueChange={field.onChange} />)} /></div>
              <div className="space-y-1"><Label className="text-xs">Montant payé</Label>
                <Controller control={control} name={`echeances.${i}.montantPaye` as const} render={({ field }) => (
                  <MontantInput className="h-10" value={field.value} onValueChange={field.onChange} />)} /></div>
              <div className="space-y-1"><Label className="text-xs">Date paiement</Label>
                <Controller control={control} name={`echeances.${i}.datePaiement` as const} render={({ field }) => (
                  <DateInput className="h-10" value={field.value} onValueChange={field.onChange} />)} /></div>
              <div className="flex items-end">
                <Button type="button" variant="outline" onClick={() => echeances.remove(i)}>Supprimer</Button>
              </div>
            </div>
          ))}
          <Button type="button" variant="outline"
            onClick={() => echeances.append({ numeroEcheance: echeances.fields.length + 1, dateEcheance: "", montantDu: "", montantPaye: "", datePaiement: "" })}>
            + Ajouter une échéance
          </Button>
        </CardContent>
      </Card>

      <Card>
        <CardHeader><CardTitle>4. Garanties</CardTitle></CardHeader>
        <CardContent className="space-y-4">
          {garanties.fields.map((f, i) => (
            <div key={f.id} className="grid grid-cols-1 gap-3 border-b pb-3 md:grid-cols-4">
              <ChampSelect label="Nature" valeur={garantiesWatch?.[i]?.nature ?? "REELLE"} options={NATURES}
                onChange={(v) => setValue(`garanties.${i}.nature`, v)} />
              <div className="space-y-1.5"><Label className="font-semibold">Type de garantie <span className="text-red-500">*</span></Label>
                <Input className="h-10" {...register(`garanties.${i}.typeGarantie` as const)} /></div>
              <div className="space-y-1.5"><Label>Code établissement garantie</Label>
                <Input className="h-10" {...register(`garanties.${i}.codeEtablissementGarantie` as const)} /></div>
              <div className="space-y-1.5"><Label>Montant couvert</Label>
                <Controller control={control} name={`garanties.${i}.montantCouvert` as const} render={({ field }) => (
                  <MontantInput className="h-10" value={field.value} onValueChange={field.onChange} />)} /></div>
              <div className="space-y-1.5"><Label>Nom du garant</Label>
                <Input className="h-10" {...register(`garanties.${i}.nomGarant` as const)} /></div>
              <div className="space-y-1.5"><Label>Code client CB du garant</Label>
                <Input className="h-10" {...register(`garanties.${i}.codeClientCbGarant` as const)} /></div>
              <div className="space-y-1.5"><Label>Début de validité</Label>
                <Controller control={control} name={`garanties.${i}.dateDebutValidite` as const} render={({ field }) => (
                  <DateInput className="h-10" value={field.value} onValueChange={field.onChange} />)} /></div>
              <div className="space-y-1.5"><Label>Fin de validité</Label>
                <Controller control={control} name={`garanties.${i}.dateFinValidite` as const} render={({ field }) => (
                  <DateInput className="h-10" value={field.value} onValueChange={field.onChange} />)} /></div>
              <div className="md:col-span-4">
                <Button type="button" variant="outline" onClick={() => garanties.remove(i)}>Supprimer cette garantie</Button>
              </div>
            </div>
          ))}
          <Button type="button" variant="outline"
            onClick={() => garanties.append({ nature: "REELLE", typeGarantie: "", codeEtablissementGarantie: "", nomGarant: "",
              codeClientCbGarant: "", montantCouvert: "", dateDebutValidite: "", dateFinValidite: "" })}>
            + Ajouter une garantie
          </Button>
        </CardContent>
      </Card>

      <Card>
        <CardHeader><CardTitle>5. Clients liés (co-titulaire ou garant avec son propre code CB)</CardTitle></CardHeader>
        <CardContent className="space-y-4">
          <p className="text-sm text-slate-500">
            Le contrat apparaîtra aussi dans le rapport du client lié, avec son rôle. Le nom est complété automatiquement si le code CB existe en base.
          </p>
          {liens.fields.map((f, i) => (
            <div key={f.id} className="grid grid-cols-1 gap-3 border-b pb-3 md:grid-cols-4">
              <div className="space-y-1.5"><Label className="font-semibold">Code client CB <span className="text-red-500">*</span></Label>
                <Input className="h-10" {...register(`liens.${i}.codeClientCb` as const)} /></div>
              <div className="space-y-1.5"><Label>Nom (facultatif)</Label>
                <Input className="h-10" {...register(`liens.${i}.nomClient` as const)} /></div>
              <ChampSelect obligatoire label="Rôle" valeur={liensWatch?.[i]?.roleClient ?? "GARANT"} options={ROLES_LIES}
                onChange={(v) => setValue(`liens.${i}.roleClient`, v)} />
              <div className="flex items-end">
                <Button type="button" variant="outline" onClick={() => liens.remove(i)}>Supprimer</Button>
              </div>
            </div>
          ))}
          <Button type="button" variant="outline"
            onClick={() => liens.append({ codeClientCb: "", nomClient: "", roleClient: "GARANT" })}>
            + Ajouter un client lié
          </Button>
        </CardContent>
      </Card>

      <div className="flex justify-end">
        <Button type="submit" size="lg">Enregistrer l'historique</Button>
      </div>
    </form>
  );
}