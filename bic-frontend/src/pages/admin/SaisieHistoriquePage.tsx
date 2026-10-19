import { useState, type ReactNode } from "react";
import { Controller, useFieldArray, useForm, useWatch } from "react-hook-form";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { DateInput } from "@/components/ui/date-input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger } from "@/components/ui/select";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { clientApi } from "@/api/clientApi";
import { axiosClient } from "@/api/axiosClient";
import { useToast } from "@/hooks/use-toast";
import { ChampsObligatoiresNote } from "@/components/ui/champs-obligatoires-note";
import { ORIGINE_DONNEE_LABELS, type OrigineDonnee } from "@/types/origineDonnee";

interface EcheanceLigne {
  numeroEcheance: string; dateEcheance: string; montantDu: string;
  montantPaye: string; datePaiement: string; statut: string;
}

interface MoisLigne {
  mois: string; nombreEcheances: string; nombreEcheancesImpayees: string;
  joursDeRetard: string; statut: string; detailsSource: string;
}

interface GarantieLigne {
  nature: string; typeGarantie: string; codeEtablissementGarantie: string;
  nomGarant: string; codeClientCbGarant: string; montantCouvert: string;
  dateDebutValidite: string; dateFinValidite: string;
}

interface ClientLieLigne { codeClientCb: string; nomClient: string; roleClient: string; }

interface FormValues {
  codeContratCb: string; codeContratEtablissement: string; codeEtablissementDeclarant: string;
  codeEtablissementCrypte: string; typeContrat: string; typeRelationEntreprise: string;
  roleClient: string; phaseDemande: string; motifCloture: string;
  dateDemande: string; dateDebutContrat: string; dateFinContrat: string;
  datePremiereEcheance: string; dateDerniereModificationSource: string;
  montantFinance: string; montantTotalDu: string; montantEcheanceMensuelle: string;
  nombreTotalEcheances: string; devise: string; periodicitePaiement: string; modeReglement: string;
  montantProchaineEcheance: string; dateProchaineEcheance: string; dateDernierReglement: string;
  nombreEcheancesRestantes: string; montantRestantDu: string; nombreEcheancesImpayees: string;
  montantImpayes: string; maxMontantImpayes: string; maxNombreEcheancesImpayees: string;
  dateMaxNombreEcheancesImpayees: string; nombreJoursRetard: string; maxNombreJoursRetard: string;
  dateMaxNombreJoursRetard: string; pireStatut: string; datePireStatut: string;
  codeRestructuration: string; dateCodeRestructuration: string; contratOrigine: string;
  nouveauContrat: string; note: string;
  echeances: EcheanceLigne[]; historiquePaiementMensuel: MoisLigne[];
  garanties: GarantieLigne[]; clientsLies: ClientLieLigne[];
  origineDonnee: OrigineDonnee;
}

const defaults: FormValues = {
  codeContratCb: "", codeContratEtablissement: "", codeEtablissementDeclarant: "",
  codeEtablissementCrypte: "", typeContrat: "", typeRelationEntreprise: "", roleClient: "TITULAIRE",
  phaseDemande: "ACTIF", motifCloture: "", dateDemande: "", dateDebutContrat: "", dateFinContrat: "",
  datePremiereEcheance: "", dateDerniereModificationSource: "", montantFinance: "", montantTotalDu: "",
  montantEcheanceMensuelle: "", nombreTotalEcheances: "", devise: "Ariary malgache", periodicitePaiement: "",
  modeReglement: "", montantProchaineEcheance: "", dateProchaineEcheance: "", dateDernierReglement: "",
  nombreEcheancesRestantes: "", montantRestantDu: "", nombreEcheancesImpayees: "", montantImpayes: "",
  maxMontantImpayes: "", maxNombreEcheancesImpayees: "", dateMaxNombreEcheancesImpayees: "",
  nombreJoursRetard: "", maxNombreJoursRetard: "", dateMaxNombreJoursRetard: "", pireStatut: "",
  datePireStatut: "", codeRestructuration: "", dateCodeRestructuration: "", contratOrigine: "",
  nouveauContrat: "", note: "", echeances: [], historiquePaiementMensuel: [], garanties: [], clientsLies: [],
  origineDonnee: "NON_RENSEIGNEE",
};

const optionalNumber = (value: string) => value.trim() === "" ? null : Number(value);
const optionalDate = (value: string) => value || null;

function Champ({ label, children }: { label: string; children: ReactNode }) {
  return <div className="space-y-1.5"><Label>{label}</Label>{children}</div>;
}

export function SaisieHistoriquePage() {
  const { toast } = useToast();
  const [cinRecherche, setCinRecherche] = useState("");
  const [clientId, setClientId] = useState<number | null>(null);
  const [clientLibelle, setClientLibelle] = useState("");
  const [recherche, setRecherche] = useState(false);
  const [enregistrement, setEnregistrement] = useState(false);

  const { register, control, handleSubmit, setValue, reset } = useForm<FormValues>({ defaultValues: defaults });
  const echeances = useFieldArray({ control, name: "echeances" });
  const mois = useFieldArray({ control, name: "historiquePaiementMensuel" });
  const garanties = useFieldArray({ control, name: "garanties" });
  const clientsLies = useFieldArray({ control, name: "clientsLies" });
  const roleClient = useWatch({ control, name: "roleClient" });
  const phaseDemande = useWatch({ control, name: "phaseDemande" });
  const motifCloture = useWatch({ control, name: "motifCloture" });
  const origineDonnee = useWatch({ control, name: "origineDonnee" });

  async function rechercherClient() {
    if (!cinRecherche.trim()) {
      toast({ title: "Saisissez le CIN à rechercher", variant: "destructive" });
      return;
    }
    setRecherche(true);
    try {
      const client = await clientApi.rechercherParCin(cinRecherche.trim());
      if (!client) {
        setClientId(null);
        setClientLibelle("");
        toast({ title: "Client introuvable pour ce CIN", variant: "destructive" });
        return;
      }
      setClientId(client.id);
      setClientLibelle(`${client.prenom} ${client.nom} · #${client.id}`);
      toast({ title: "Client sélectionné" });
    } catch (error) {
      toast({ title: "Recherche impossible", variant: "destructive" });
    } finally {
      setRecherche(false);
    }
  }

  async function onSubmit(values: FormValues) {
    if (!clientId) {
      toast({ title: "Recherchez d'abord un client par CIN", variant: "destructive" });
      return;
    }
    setEnregistrement(true);
    try {
      await axiosClient.post("/contrats/historique", {
        clientId,
        codeContratCb: values.codeContratCb.trim() || null,
        codeContratEtablissement: values.codeContratEtablissement.trim() || null,
        codeEtablissementDeclarant: values.codeEtablissementDeclarant.trim() || null,
        codeEtablissementCrypte: values.codeEtablissementCrypte.trim() || null,
        typeContrat: values.typeContrat.trim(), typeRelationEntreprise: values.typeRelationEntreprise.trim() || null,
        roleClient: values.roleClient, phaseDemande: values.phaseDemande,
        origineDonnee: values.origineDonnee,
        motifCloture: values.motifCloture || null,
        dateDemande: optionalDate(values.dateDemande), dateDebutContrat: optionalDate(values.dateDebutContrat),
        dateFinContrat: optionalDate(values.dateFinContrat), datePremiereEcheance: optionalDate(values.datePremiereEcheance),
        dateDerniereModificationSource: optionalDate(values.dateDerniereModificationSource),
        montantFinance: optionalNumber(values.montantFinance), montantTotalDu: optionalNumber(values.montantTotalDu),
        montantEcheanceMensuelle: optionalNumber(values.montantEcheanceMensuelle),
        nombreTotalEcheances: optionalNumber(values.nombreTotalEcheances), devise: values.devise,
        periodicitePaiement: values.periodicitePaiement.trim() || null,
        modeReglement: values.modeReglement.trim() || null,
        montantProchaineEcheance: optionalNumber(values.montantProchaineEcheance),
        dateProchaineEcheance: optionalDate(values.dateProchaineEcheance), dateDernierReglement: optionalDate(values.dateDernierReglement),
        nombreEcheancesRestantes: optionalNumber(values.nombreEcheancesRestantes),
        montantRestantDu: optionalNumber(values.montantRestantDu),
        nombreEcheancesImpayees: optionalNumber(values.nombreEcheancesImpayees),
        montantImpayes: optionalNumber(values.montantImpayes), maxMontantImpayes: optionalNumber(values.maxMontantImpayes),
        maxNombreEcheancesImpayees: optionalNumber(values.maxNombreEcheancesImpayees),
        dateMaxNombreEcheancesImpayees: optionalDate(values.dateMaxNombreEcheancesImpayees),
        nombreJoursRetard: optionalNumber(values.nombreJoursRetard), maxNombreJoursRetard: optionalNumber(values.maxNombreJoursRetard),
        dateMaxNombreJoursRetard: optionalDate(values.dateMaxNombreJoursRetard), pireStatut: values.pireStatut.trim() || null,
        datePireStatut: optionalDate(values.datePireStatut),
        codeRestructuration: values.codeRestructuration.trim() || null,
        dateCodeRestructuration: optionalDate(values.dateCodeRestructuration),
        contratOrigine: values.contratOrigine.trim() || null, nouveauContrat: values.nouveauContrat.trim() || null,
        note: values.note.trim() || null,
        echeances: values.echeances.filter((e) => e.dateEcheance && e.montantDu).map((e) => ({
          numeroEcheance: Number(e.numeroEcheance), dateEcheance: e.dateEcheance, montantDu: Number(e.montantDu),
          montantPaye: optionalNumber(e.montantPaye), datePaiement: optionalDate(e.datePaiement), statut: e.statut,
        })),
        historiquePaiementMensuel: values.historiquePaiementMensuel.filter((m) => m.mois && m.statut.trim()).map((m) => ({
          mois: `${m.mois}-01`, nombreEcheances: optionalNumber(m.nombreEcheances),
          nombreEcheancesImpayees: optionalNumber(m.nombreEcheancesImpayees),
          joursDeRetard: optionalNumber(m.joursDeRetard), statut: m.statut.trim(), detailsSource: m.detailsSource.trim() || null,
        })),
        garanties: values.garanties.filter((g) => g.typeGarantie.trim()).map((g) => ({
          nature: g.nature, typeGarantie: g.typeGarantie.trim(),
          codeEtablissementGarantie: g.codeEtablissementGarantie.trim() || null,
          nomGarant: g.nomGarant.trim() || null, codeClientCbGarant: g.codeClientCbGarant.trim() || null,
          montantCouvert: optionalNumber(g.montantCouvert), dateDebutValidite: optionalDate(g.dateDebutValidite),
          dateFinValidite: optionalDate(g.dateFinValidite),
        })),
        clientsLies: values.clientsLies.filter((l) => l.codeClientCb.trim()).map((l) => ({
          codeClientCb: l.codeClientCb.trim(), nomClient: l.nomClient.trim() || null, roleClient: l.roleClient,
        })),
        bienLeasing: null,
      });
      toast({ title: "Historique enregistré." });
      reset(defaults);
    } catch (error) {
      toast({ title: "Échec de l'enregistrement", variant: "destructive" });
    } finally {
      setEnregistrement(false);
    }
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="mx-auto max-w-6xl space-y-6 p-6">
      <ChampsObligatoiresNote />

      <Card>
        <CardHeader><CardTitle>1. Identifier le client</CardTitle></CardHeader>
        <CardContent className="flex flex-wrap items-end gap-3">
          <Champ label="CIN du client *"><Input value={cinRecherche} onChange={(e) => setCinRecherche(e.target.value)} /></Champ>
          <Button type="button" onClick={rechercherClient} disabled={recherche}>{recherche ? "Recherche…" : "Rechercher"}</Button>
          {clientId && <span className="pb-2 text-sm text-green-700">{clientLibelle}</span>}
        </CardContent>
      </Card>

      <Card>
        <CardHeader><CardTitle>2. Contrat et déclaration source</CardTitle></CardHeader>
        <CardContent className="grid grid-cols-1 gap-4 md:grid-cols-3">
          <Champ label="Code Contrat CB"><Input {...register("codeContratCb")} /></Champ>
          <Champ label="Code Contrat Établissement"><Input {...register("codeContratEtablissement")} /></Champ>
          <Champ label="Code établissement déclarant"><Input {...register("codeEtablissementDeclarant")} /></Champ>
          <Champ label="Code établissement crypté"><Input {...register("codeEtablissementCrypte")} /></Champ>
          <Champ label="Type de contrat *"><Input {...register("typeContrat", { required: true })} placeholder="Prêt personnel" /></Champ>
          <Champ label="Origine des données contrat">
            <Select value={origineDonnee} onValueChange={(v) => setValue("origineDonnee", v as OrigineDonnee)}>
              <SelectTrigger><span>{ORIGINE_DONNEE_LABELS[origineDonnee]}</span></SelectTrigger>
              <SelectContent>
                {(Object.entries(ORIGINE_DONNEE_LABELS) as [OrigineDonnee, string][]).map(([value, label]) => (
                  <SelectItem key={value} value={value}>{label}</SelectItem>
                ))}
              </SelectContent>
            </Select>
          </Champ>
          <Champ label="Type de relation avec l'entreprise"><Input {...register("typeRelationEntreprise")} /></Champ>
          <Champ label="Rôle *">
            <Select value={roleClient} onValueChange={(v) => { if (v) setValue("roleClient", v); }}>
              <SelectTrigger><span>{roleClient}</span></SelectTrigger>
              <SelectContent><SelectItem value="TITULAIRE">Titulaire</SelectItem><SelectItem value="CO_TITULAIRE">Co-titulaire</SelectItem><SelectItem value="GARANT">Garant</SelectItem></SelectContent>
            </Select>
          </Champ>
          <Champ label="Phase *">
            <Select value={phaseDemande} onValueChange={(v) => { if (v) setValue("phaseDemande", v); }}>
              <SelectTrigger><span>{phaseDemande}</span></SelectTrigger>
              <SelectContent>
                <SelectItem value="DEMANDE_EN_COURS">Demande en cours</SelectItem><SelectItem value="ACTIF">Actif</SelectItem>
                <SelectItem value="REFUSE">Refusé</SelectItem><SelectItem value="FERME">Fermé</SelectItem>
                <SelectItem value="ABANDONNE">Abandonné / annulé</SelectItem>
              </SelectContent>
            </Select>
          </Champ>
          <Champ label="Motif de clôture"><Select value={motifCloture || "aucun"} onValueChange={(v) => { if (v) setValue("motifCloture", v === "aucun" ? "" : v); }}>
            <SelectTrigger><span>{motifCloture || "Non renseigné"}</span></SelectTrigger>
            <SelectContent><SelectItem value="aucun">Non renseigné</SelectItem><SelectItem value="FIN_A_TERME">Fin à terme</SelectItem><SelectItem value="FIN_ANTICIPEE">Fin anticipée</SelectItem></SelectContent>
          </Select></Champ>
          <Champ label="Date de la demande"><Controller control={control} name="dateDemande" render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ>
          <Champ label="Date début de contrat"><Controller control={control} name="dateDebutContrat" render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ>
          <Champ label="Date fin de contrat"><Controller control={control} name="dateFinContrat" render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ>
          <Champ label="Date première échéance"><Controller control={control} name="datePremiereEcheance" render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ>
          <Champ label="Dernière modification dans la source"><Controller control={control} name="dateDerniereModificationSource" render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ>
          <Champ label="Devise *"><Input {...register("devise", { required: true })} /></Champ>
          <Champ label="Périodicité de paiement"><Input {...register("periodicitePaiement")} placeholder="Mensuelle - 1 mois" /></Champ>
          <Champ label="Mode de règlement"><Input {...register("modeReglement")} /></Champ>
        </CardContent>
      </Card>

      <Card>
        <CardHeader><CardTitle>3. Montants et situation des échéances</CardTitle></CardHeader>
        <CardContent className="grid grid-cols-1 gap-4 md:grid-cols-4">
          <Champ label="Montant financé"><Input type="number" min="0.01" step="0.01" {...register("montantFinance", { min: 0.01 })} /></Champ>
          <Champ label="Montant total dû"><Input type="number" min="0" step="0.01" {...register("montantTotalDu")} /></Champ>
          <Champ label="Échéance mensuelle"><Input type="number" min="0" step="0.01" {...register("montantEcheanceMensuelle")} /></Champ>
          <Champ label="Nombre total d'échéances"><Input type="number" min="1" step="1" {...register("nombreTotalEcheances", { min: 1 })} /></Champ>
          <Champ label="Montant prochaine échéance"><Input type="number" min="0" step="0.01" {...register("montantProchaineEcheance")} /></Champ>
          <Champ label="Date prochaine échéance"><Controller control={control} name="dateProchaineEcheance" render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ>
          <Champ label="Date dernier règlement"><Controller control={control} name="dateDernierReglement" render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ>
          <Champ label="Nombre d'échéances restantes"><Input type="number" min="0" step="1" {...register("nombreEcheancesRestantes")} /></Champ>
          <Champ label="Montant restant dû"><Input type="number" min="0" step="0.01" {...register("montantRestantDu")} /></Champ>
          <Champ label="Nombre d'échéances impayées"><Input type="number" min="0" step="1" {...register("nombreEcheancesImpayees")} /></Champ>
          <Champ label="Montant des impayés"><Input type="number" min="0" step="0.01" {...register("montantImpayes")} /></Champ>
          <Champ label="Montant maximal des impayés"><Input type="number" min="0" step="0.01" {...register("maxMontantImpayes")} /></Champ>
          <Champ label="Maximum d'échéances impayées"><Input type="number" min="0" step="1" {...register("maxNombreEcheancesImpayees")} /></Champ>
          <Champ label="Date du maximum d'impayés"><Controller control={control} name="dateMaxNombreEcheancesImpayees" render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ>
          <Champ label="Retard actuel (jours)"><Input type="number" min="0" step="1" {...register("nombreJoursRetard")} /></Champ>
          <Champ label="Retard maximal (jours)"><Input type="number" min="0" step="1" {...register("maxNombreJoursRetard")} /></Champ>
          <Champ label="Date du retard maximal"><Controller control={control} name="dateMaxNombreJoursRetard" render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ>
          <Champ label="Pire statut"><Input {...register("pireStatut")} /></Champ>
          <Champ label="Date du pire statut"><Controller control={control} name="datePireStatut" render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ>
          <Champ label="Code restructuration"><Input {...register("codeRestructuration")} /></Champ>
          <Champ label="Date restructuration"><Controller control={control} name="dateCodeRestructuration" render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ>
          <Champ label="Contrat d'origine"><Input {...register("contratOrigine")} /></Champ>
          <Champ label="Nouveau contrat"><Input {...register("nouveauContrat")} /></Champ>
          <Champ label="Note de déclaration"><Input {...register("note")} /></Champ>
        </CardContent>
      </Card>

      <Card>
        <CardHeader><CardTitle>4. Historique mensuel déclaré</CardTitle></CardHeader>
        <CardContent className="space-y-4">
          <p className="text-sm text-muted-foreground">Saisissez les mois et statuts publiés par la source. Ces lignes ne supposent pas de date de paiement exacte.</p>
          {mois.fields.map((f, i) => <div key={f.id} className="grid grid-cols-2 gap-3 border-b pb-3 md:grid-cols-6">
            <Champ label="Mois"><Input type="month" {...register(`historiquePaiementMensuel.${i}.mois`)} /></Champ>
            <Champ label="Échéances"><Input type="number" min="0" {...register(`historiquePaiementMensuel.${i}.nombreEcheances`)} /></Champ>
            <Champ label="Impayées"><Input type="number" min="0" {...register(`historiquePaiementMensuel.${i}.nombreEcheancesImpayees`)} /></Champ>
            <Champ label="Retard jours"><Input type="number" min="0" {...register(`historiquePaiementMensuel.${i}.joursDeRetard`)} /></Champ>
            <Champ label="Statut source"><Input {...register(`historiquePaiementMensuel.${i}.statut`)} placeholder="À jour / 1-29 / 30-59 / 60-89 jours…" /></Champ>
            <div className="flex items-end gap-2"><Input aria-label="Détails bruts" placeholder="Détails source" {...register(`historiquePaiementMensuel.${i}.detailsSource`)} /><Button type="button" variant="outline" onClick={() => mois.remove(i)}>✕</Button></div>
          </div>)}
          <Button type="button" variant="outline" onClick={() => mois.append({ mois: "", nombreEcheances: "", nombreEcheancesImpayees: "", joursDeRetard: "", statut: "", detailsSource: "" })}>+ Ajouter un mois</Button>
        </CardContent>
      </Card>

      <Card>
        <CardHeader><CardTitle>5. Échéances détaillées (si le calendrier exact est disponible)</CardTitle></CardHeader>
        <CardContent className="space-y-4">
          {echeances.fields.map((f, i) => <div key={f.id} className="grid grid-cols-2 gap-3 border-b pb-3 md:grid-cols-6">
            <Champ label="N°"><Input type="number" min="1" {...register(`echeances.${i}.numeroEcheance`)} /></Champ>
            <Champ label="Date"><Controller control={control} name={`echeances.${i}.dateEcheance`} render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ>
            <Champ label="Montant dû"><Input type="number" min="0" step="0.01" {...register(`echeances.${i}.montantDu`)} /></Champ>
            <Champ label="Montant payé"><Input type="number" min="0" step="0.01" {...register(`echeances.${i}.montantPaye`)} /></Champ>
            <Champ label="Date paiement"><Controller control={control} name={`echeances.${i}.datePaiement`} render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ>
            <div className="flex items-end gap-2"><Select defaultValue={f.statut} onValueChange={(v) => { if (v) setValue(`echeances.${i}.statut`, v); }}>
              <SelectTrigger><span>{f.statut || "Statut"}</span></SelectTrigger><SelectContent>
                <SelectItem value="A_VENIR">À venir</SelectItem><SelectItem value="PAYE_A_TEMPS">Payé à temps</SelectItem>
                <SelectItem value="EN_RETARD">En retard</SelectItem><SelectItem value="IMPAYE">Impayé</SelectItem>
              </SelectContent></Select><Button type="button" variant="outline" onClick={() => echeances.remove(i)}>✕</Button></div>
          </div>)}
          <Button type="button" variant="outline" onClick={() => echeances.append({ numeroEcheance: String(echeances.fields.length + 1), dateEcheance: "", montantDu: "", montantPaye: "", datePaiement: "", statut: "A_VENIR" })}>+ Ajouter une échéance</Button>
        </CardContent>
      </Card>

      <Card>
        <CardHeader><CardTitle>6. Garanties et clients liés</CardTitle></CardHeader>
        <CardContent className="space-y-5">
          {garanties.fields.map((f, i) => <div key={f.id} className="grid grid-cols-2 gap-3 border-b pb-4 md:grid-cols-4">
            <Champ label="Nature"><Select defaultValue={f.nature} onValueChange={(v) => { if (v) setValue(`garanties.${i}.nature`, v); }}><SelectTrigger><span>{f.nature || "Nature"}</span></SelectTrigger><SelectContent><SelectItem value="REELLE">Réelle</SelectItem><SelectItem value="PERSONNELLE">Personnelle</SelectItem></SelectContent></Select></Champ>
            <Champ label="Type de garantie"><Input {...register(`garanties.${i}.typeGarantie`)} /></Champ>
            <Champ label="Code établissement"><Input {...register(`garanties.${i}.codeEtablissementGarantie`)} /></Champ>
            <Champ label="Nom garant"><Input {...register(`garanties.${i}.nomGarant`)} /></Champ>
            <Champ label="Code client CB garant"><Input {...register(`garanties.${i}.codeClientCbGarant`)} /></Champ>
            <Champ label="Montant couvert"><Input type="number" min="0" step="0.01" {...register(`garanties.${i}.montantCouvert`)} /></Champ>
            <Champ label="Début de validité"><Controller control={control} name={`garanties.${i}.dateDebutValidite`} render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ>
            <div className="flex items-end gap-2"><Champ label="Fin de validité"><Controller control={control} name={`garanties.${i}.dateFinValidite`} render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ><Button type="button" variant="outline" onClick={() => garanties.remove(i)}>✕</Button></div>
          </div>)}
          <Button type="button" variant="outline" onClick={() => garanties.append({ nature: "REELLE", typeGarantie: "", codeEtablissementGarantie: "", nomGarant: "", codeClientCbGarant: "", montantCouvert: "", dateDebutValidite: "", dateFinValidite: "" })}>+ Ajouter une garantie</Button>

          {clientsLies.fields.map((f, i) => <div key={f.id} className="grid grid-cols-2 gap-3 border-b pb-3 md:grid-cols-4">
            <Champ label="Code client CB lié"><Input {...register(`clientsLies.${i}.codeClientCb`)} /></Champ><Champ label="Nom client lié"><Input {...register(`clientsLies.${i}.nomClient`)} /></Champ>
            <Champ label="Rôle lié"><Select defaultValue={f.roleClient} onValueChange={(v) => { if (v) setValue(`clientsLies.${i}.roleClient`, v); }}><SelectTrigger><span>{f.roleClient || "Rôle"}</span></SelectTrigger><SelectContent><SelectItem value="TITULAIRE">Titulaire</SelectItem><SelectItem value="CO_TITULAIRE">Co-titulaire</SelectItem><SelectItem value="GARANT">Garant</SelectItem></SelectContent></Select></Champ>
            <div className="flex items-end"><Button type="button" variant="outline" onClick={() => clientsLies.remove(i)}>Supprimer le lien</Button></div>
          </div>)}
          <Button type="button" variant="outline" onClick={() => clientsLies.append({ codeClientCb: "", nomClient: "", roleClient: "TITULAIRE" })}>+ Ajouter un client lié</Button>
        </CardContent>
      </Card>

      <div className="flex justify-end"><Button type="submit" size="lg" disabled={enregistrement || !clientId}>{enregistrement ? "Enregistrement…" : "Enregistrer l'historique"}</Button></div>
    </form>
  );
}
