import { useState, useEffect, type ReactNode } from "react";
import { useSearchParams, Link, useNavigate } from "react-router-dom";
import { Controller, useFieldArray, useForm, useWatch } from "react-hook-form";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { DateInput } from "@/components/ui/date-input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger } from "@/components/ui/select";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { clientApi } from "@/api/clientApi";
import { contratApi } from "@/api/contratApi";
import { axiosClient } from "@/api/axiosClient";
import { useToast } from "@/hooks/use-toast";
import { ChampsObligatoiresNote } from "@/components/ui/champs-obligatoires-note";
import { ORIGINE_DONNEE_LABELS, type OrigineDonnee } from "@/types/origineDonnee";
import { ArrowLeft, Edit3, PlusCircle } from "lucide-react";

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
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const paramClientId = searchParams.get("clientId");
  const paramContratId = searchParams.get("contratId");

  const [cinRecherche, setCinRecherche] = useState("");
  const [clientId, setClientId] = useState<number | null>(paramClientId ? Number(paramClientId) : null);
  const [clientLibelle, setClientLibelle] = useState("");
  const [contratEnEdition, setContratEnEdition] = useState<number | null>(paramContratId ? Number(paramContratId) : null);
  const [recherche, setRecherche] = useState(false);
  const [chargementInitial, setChargementInitial] = useState(false);
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

  useEffect(() => {
    let actif = true;

    async function initialiser() {
      if (paramContratId) {
        setChargementInitial(true);
        try {
          const detail = await contratApi.obtenirDetailHistorique(Number(paramContratId));
          if (!actif) return;

          setContratEnEdition(detail.id);
          if (detail.clientId) {
            setClientId(detail.clientId);
            setClientLibelle(`${detail.clientPrenom ?? ""} ${detail.clientNom ?? ""} · #${detail.clientId}`);
          }
          if (detail.clientCin) {
            setCinRecherche(detail.clientCin);
          }

          reset({
            codeContratCb: detail.codeContratCb ?? "",
            codeContratEtablissement: detail.codeContratEtablissement ?? "",
            codeEtablissementDeclarant: detail.codeEtablissementDeclarant ?? "",
            codeEtablissementCrypte: detail.codeEtablissementCrypte ?? "",
            typeContrat: detail.typeContrat ?? "",
            typeRelationEntreprise: detail.typeRelationEntreprise ?? "",
            roleClient: detail.roleClient ?? "TITULAIRE",
            phaseDemande: detail.phaseDemande ?? "ACTIF",
            motifCloture: detail.motifCloture ?? "",
            dateDemande: detail.dateDemande ?? "",
            dateDebutContrat: detail.dateDebutContrat ?? "",
            dateFinContrat: detail.dateFinContrat ?? "",
            datePremiereEcheance: detail.datePremiereEcheance ?? "",
            dateDerniereModificationSource: detail.dateDerniereModificationSource ?? "",
            montantFinance: detail.montantFinance != null ? String(detail.montantFinance) : "",
            montantTotalDu: detail.montantTotalDu != null ? String(detail.montantTotalDu) : "",
            montantEcheanceMensuelle: detail.montantEcheanceMensuelle != null ? String(detail.montantEcheanceMensuelle) : "",
            nombreTotalEcheances: detail.nombreTotalEcheances != null ? String(detail.nombreTotalEcheances) : "",
            devise: detail.devise ?? "Ariary malgache",
            periodicitePaiement: detail.periodicitePaiement ?? "",
            modeReglement: detail.modeReglement ?? "",
            montantProchaineEcheance: detail.montantProchaineEcheance != null ? String(detail.montantProchaineEcheance) : "",
            dateProchaineEcheance: detail.dateProchaineEcheance ?? "",
            dateDernierReglement: detail.dateDernierReglement ?? "",
            nombreEcheancesRestantes: detail.nombreEcheancesRestantes != null ? String(detail.nombreEcheancesRestantes) : "",
            montantRestantDu: detail.montantRestantDu != null ? String(detail.montantRestantDu) : "",
            nombreEcheancesImpayees: detail.nombreEcheancesImpayees != null ? String(detail.nombreEcheancesImpayees) : "",
            montantImpayes: detail.montantImpayes != null ? String(detail.montantImpayes) : "",
            maxMontantImpayes: detail.maxMontantImpayes != null ? String(detail.maxMontantImpayes) : "",
            maxNombreEcheancesImpayees: detail.maxNombreEcheancesImpayees != null ? String(detail.maxNombreEcheancesImpayees) : "",
            dateMaxNombreEcheancesImpayees: detail.dateMaxNombreEcheancesImpayees ?? "",
            nombreJoursRetard: detail.nombreJoursRetard != null ? String(detail.nombreJoursRetard) : "",
            maxNombreJoursRetard: detail.maxNombreJoursRetard != null ? String(detail.maxNombreJoursRetard) : "",
            dateMaxNombreJoursRetard: detail.dateMaxNombreJoursRetard ?? "",
            pireStatut: detail.pireStatut ?? "",
            datePireStatut: detail.datePireStatut ?? "",
            codeRestructuration: detail.codeRestructuration ?? "",
            dateCodeRestructuration: detail.dateCodeRestructuration ?? "",
            contratOrigine: detail.contratOrigine ?? "",
            nouveauContrat: detail.nouveauContrat ?? "",
            note: detail.note ?? "",
            origineDonnee: (detail.origineDonnee as OrigineDonnee) ?? "NON_RENSEIGNEE",
            echeances: (detail.echeances ?? []).map((e) => ({
              numeroEcheance: String(e.numeroEcheance),
              dateEcheance: e.dateEcheance ?? "",
              montantDu: String(e.montantDu ?? ""),
              montantPaye: e.montantPaye != null ? String(e.montantPaye) : "",
              datePaiement: e.datePaiement ?? "",
              statut: e.statut ?? "PAYE",
            })),
            historiquePaiementMensuel: (detail.historiquePaiementMensuel ?? []).map((m) => ({
              mois: m.mois ? m.mois.substring(0, 7) : "",
              nombreEcheances: m.nombreEcheances != null ? String(m.nombreEcheances) : "",
              nombreEcheancesImpayees: m.nombreEcheancesImpayees != null ? String(m.nombreEcheancesImpayees) : "",
              joursDeRetard: m.joursDeRetard != null ? String(m.joursDeRetard) : "",
              statut: m.statut ?? "",
              detailsSource: m.detailsSource ?? "",
            })),
            garanties: (detail.garanties ?? []).map((g) => ({
              nature: g.nature ?? "REELLE",
              typeGarantie: g.typeGarantie ?? "",
              codeEtablissementGarantie: g.codeEtablissementGarantie ?? "",
              nomGarant: g.nomGarant ?? "",
              codeClientCbGarant: g.codeClientCbGarant ?? "",
              montantCouvert: g.montantCouvert != null ? String(g.montantCouvert) : "",
              dateDebutValidite: g.dateDebutValidite ?? "",
              dateFinValidite: g.dateFinValidite ?? "",
            })),
            clientsLies: (detail.clientsLies ?? []).map((l) => ({
              codeClientCb: l.codeClientCb ?? "",
              nomClient: l.nomClient ?? "",
              roleClient: l.roleClient ?? "TITULAIRE",
            })),
          });

          toast({ title: "Contrat historique chargé pour modification." });
        } catch (e: unknown) {
          toast({ title: "Impossible de charger le contrat historique", variant: "destructive" });
        } finally {
          if (actif) setChargementInitial(false);
        }
      } else if (paramClientId) {
        setChargementInitial(true);
        try {
          const client = await clientApi.obtenirParId(Number(paramClientId));
          if (!actif) return;
          setClientId(client.id);
          setClientLibelle(`${client.prenom} ${client.nom} · #${client.id}`);
          const cin = client.identifiants?.find((i) => i.typeIdentifiant?.toUpperCase() === "CIN")?.numero;
          if (cin) setCinRecherche(cin);
        } catch {
          // Fallback silencieux si non trouvé
        } finally {
          if (actif) setChargementInitial(false);
        }
      }
    }

    initialiser();
    return () => {
      actif = false;
    };
  }, [paramClientId, paramContratId, reset, toast]);

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
    } catch {
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
      const payload = {
        clientId,
        codeContratCb: values.codeContratCb.trim() || null,
        codeContratEtablissement: values.codeContratEtablissement.trim() || null,
        codeEtablissementDeclarant: values.codeEtablissementDeclarant.trim() || null,
        codeEtablissementCrypte: values.codeEtablissementCrypte.trim() || null,
        typeContrat: values.typeContrat.trim(),
        typeRelationEntreprise: values.typeRelationEntreprise.trim() || null,
        roleClient: values.roleClient,
        phaseDemande: values.phaseDemande,
        origineDonnee: values.origineDonnee,
        motifCloture: values.motifCloture || null,
        dateDemande: optionalDate(values.dateDemande),
        dateDebutContrat: optionalDate(values.dateDebutContrat),
        dateFinContrat: optionalDate(values.dateFinContrat),
        datePremiereEcheance: optionalDate(values.datePremiereEcheance),
        dateDerniereModificationSource: optionalDate(values.dateDerniereModificationSource),
        montantFinance: optionalNumber(values.montantFinance),
        montantTotalDu: optionalNumber(values.montantTotalDu),
        montantEcheanceMensuelle: optionalNumber(values.montantEcheanceMensuelle),
        nombreTotalEcheances: optionalNumber(values.nombreTotalEcheances),
        devise: values.devise,
        periodicitePaiement: values.periodicitePaiement.trim() || null,
        modeReglement: values.modeReglement.trim() || null,
        montantProchaineEcheance: optionalNumber(values.montantProchaineEcheance),
        dateProchaineEcheance: optionalDate(values.dateProchaineEcheance),
        dateDernierReglement: optionalDate(values.dateDernierReglement),
        nombreEcheancesRestantes: optionalNumber(values.nombreEcheancesRestantes),
        montantRestantDu: optionalNumber(values.montantRestantDu),
        nombreEcheancesImpayees: optionalNumber(values.nombreEcheancesImpayees),
        montantImpayes: optionalNumber(values.montantImpayes),
        maxMontantImpayes: optionalNumber(values.maxMontantImpayes),
        maxNombreEcheancesImpayees: optionalNumber(values.maxNombreEcheancesImpayees),
        dateMaxNombreEcheancesImpayees: optionalDate(values.dateMaxNombreEcheancesImpayees),
        nombreJoursRetard: optionalNumber(values.nombreJoursRetard),
        maxNombreJoursRetard: optionalNumber(values.maxNombreJoursRetard),
        dateMaxNombreJoursRetard: optionalDate(values.dateMaxNombreJoursRetard),
        pireStatut: values.pireStatut.trim() || null,
        datePireStatut: optionalDate(values.datePireStatut),
        codeRestructuration: values.codeRestructuration.trim() || null,
        dateCodeRestructuration: optionalDate(values.dateCodeRestructuration),
        contratOrigine: values.contratOrigine.trim() || null,
        nouveauContrat: values.nouveauContrat.trim() || null,
        note: values.note.trim() || null,
        echeances: values.echeances.filter((e) => e.dateEcheance && e.montantDu).map((e) => ({
          numeroEcheance: Number(e.numeroEcheance),
          dateEcheance: e.dateEcheance,
          montantDu: Number(e.montantDu),
          montantPaye: optionalNumber(e.montantPaye),
          datePaiement: optionalDate(e.datePaiement),
          statut: e.statut,
        })),
        historiquePaiementMensuel: values.historiquePaiementMensuel.filter((m) => m.mois && m.statut.trim()).map((m) => ({
          mois: `${m.mois}-01`,
          nombreEcheances: optionalNumber(m.nombreEcheances),
          nombreEcheancesImpayees: optionalNumber(m.nombreEcheancesImpayees),
          joursDeRetard: optionalNumber(m.joursDeRetard),
          statut: m.statut.trim(),
          detailsSource: m.detailsSource.trim() || null,
        })),
        garanties: values.garanties.filter((g) => g.typeGarantie.trim()).map((g) => ({
          nature: g.nature,
          typeGarantie: g.typeGarantie.trim(),
          codeEtablissementGarantie: g.codeEtablissementGarantie.trim() || null,
          nomGarant: g.nomGarant.trim() || null,
          codeClientCbGarant: g.codeClientCbGarant.trim() || null,
          montantCouvert: optionalNumber(g.montantCouvert),
          dateDebutValidite: optionalDate(g.dateDebutValidite),
          dateFinValidite: optionalDate(g.dateFinValidite),
        })),
        clientsLies: values.clientsLies.filter((l) => l.codeClientCb.trim()).map((l) => ({
          codeClientCb: l.codeClientCb.trim(),
          nomClient: l.nomClient.trim() || null,
          roleClient: l.roleClient,
        })),
        bienLeasing: null,
      };

      if (contratEnEdition) {
        await contratApi.modifierHistorique(contratEnEdition, payload);
        toast({ title: `Contrat #${contratEnEdition} mis à jour avec succès.` });
      } else {
        await axiosClient.post("/contrats/historique", payload);
        toast({ title: "Nouveau contrat historique enregistré." });
        reset(defaults);
      }

      if (paramClientId) {
        navigate("/admin/clients");
      }
    } catch {
      toast({ title: "Échec de l'enregistrement", variant: "destructive" });
    } finally {
      setEnregistrement(false);
    }
  }

  if (chargementInitial) {
    return (
      <div className="flex h-64 items-center justify-center text-sm text-muted-foreground">
        Chargement des données du contrat…
      </div>
    );
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="mx-auto max-w-6xl space-y-6 p-6">
      {/* Lien retour & En-tête */}
      <div className="flex items-center justify-between border-b pb-4">
        <div>
          <Link
            to="/admin/clients"
            className="inline-flex items-center gap-1.5 text-xs font-medium text-muted-foreground hover:text-foreground mb-1"
          >
            <ArrowLeft className="size-3.5" />
            Retour à la gestion des sociétaires
          </Link>
          <h1 className="font-heading text-2xl font-bold text-foreground flex items-center gap-2">
            {contratEnEdition ? (
              <>
                <Edit3 className="size-6 text-indigo-600" />
                Modification du contrat historique #{contratEnEdition}
              </>
            ) : (
              <>
                <PlusCircle className="size-6 text-primary" />
                Saisie d'un contrat historique
              </>
            )}
          </h1>
          <p className="text-xs text-muted-foreground mt-0.5">
            {contratEnEdition
              ? "Modifiez l'échéancier, les observations périodiques et les garanties rattachées."
              : "Enregistrement d'antécédents de crédit pour le calcul du score interne CECAM."}
          </p>
        </div>
      </div>

      <ChampsObligatoiresNote />

      <Card>
        <CardHeader><CardTitle>1. Identifier le client</CardTitle></CardHeader>
        <CardContent className="flex flex-wrap items-end gap-3">
          <Champ label="CIN du client *">
            <Input
              value={cinRecherche}
              onChange={(e) => setCinRecherche(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") {
                  e.preventDefault();
                  rechercherClient();
                }
              }}
            />
          </Champ>
          <Button type="button" onClick={rechercherClient} disabled={recherche}>
            {recherche ? "Recherche…" : "Rechercher"}
          </Button>
          {clientId && <span className="pb-2 text-sm font-semibold text-emerald-700">{clientLibelle}</span>}
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
              <SelectContent>
                <SelectItem value="TITULAIRE">Titulaire</SelectItem>
                <SelectItem value="CO_TITULAIRE">Co-titulaire</SelectItem>
                <SelectItem value="GARANT">Garant</SelectItem>
              </SelectContent>
            </Select>
          </Champ>
          <Champ label="Phase *">
            <Select value={phaseDemande} onValueChange={(v) => { if (v) setValue("phaseDemande", v); }}>
              <SelectTrigger><span>{phaseDemande}</span></SelectTrigger>
              <SelectContent>
                <SelectItem value="DEMANDE_EN_COURS">Demande en cours</SelectItem>
                <SelectItem value="ACTIF">Actif</SelectItem>
                <SelectItem value="FERME">Fermé</SelectItem>
                <SelectItem value="REFUSE">Refusé</SelectItem>
                <SelectItem value="ABANDONNE">Abandonné</SelectItem>
              </SelectContent>
            </Select>
          </Champ>
          {phaseDemande === "FERME" && (
            <Champ label="Motif de clôture">
              <Select value={motifCloture} onValueChange={(v) => { if (v) setValue("motifCloture", v); }}>
                <SelectTrigger><span>{motifCloture || "Sélectionner un motif"}</span></SelectTrigger>
                <SelectContent>
                  <SelectItem value="FIN_A_TERME">Fin à terme</SelectItem>
                  <SelectItem value="FIN_ANTICIPEE">Fin anticipée</SelectItem>
                </SelectContent>
              </Select>
            </Champ>
          )}
          <Champ label="Date demande"><Controller control={control} name="dateDemande" render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ>
          <Champ label="Date début contrat"><Controller control={control} name="dateDebutContrat" render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ>
          <Champ label="Date fin contrat"><Controller control={control} name="dateFinContrat" render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ>
          <Champ label="Date 1ère échéance"><Controller control={control} name="datePremiereEcheance" render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ>
          <Champ label="Date dernière modif source"><Controller control={control} name="dateDerniereModificationSource" render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ>
        </CardContent>
      </Card>

      <Card>
        <CardHeader><CardTitle>3. Montants et conditions financières</CardTitle></CardHeader>
        <CardContent className="grid grid-cols-1 gap-4 md:grid-cols-3">
          <Champ label="Montant financé *"><Input type="number" min="0" step="0.01" {...register("montantFinance", { required: true })} /></Champ>
          <Champ label="Montant total dû"><Input type="number" min="0" step="0.01" {...register("montantTotalDu")} /></Champ>
          <Champ label="Échéance mensuelle"><Input type="number" min="0" step="0.01" {...register("montantEcheanceMensuelle")} /></Champ>
          <Champ label="Nombre total d'échéances *"><Input type="number" min="1" {...register("nombreTotalEcheances", { required: true })} /></Champ>
          <Champ label="Devise"><Input {...register("devise")} /></Champ>
          <Champ label="Périodicité paiement"><Input {...register("periodicitePaiement")} placeholder="Mensuelle, Trimestrielle..." /></Champ>
          <Champ label="Mode de règlement"><Input {...register("modeReglement")} /></Champ>
          <Champ label="Montant prochaine échéance"><Input type="number" min="0" step="0.01" {...register("montantProchaineEcheance")} /></Champ>
          <Champ label="Date prochaine échéance"><Controller control={control} name="dateProchaineEcheance" render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ>
          <Champ label="Date dernier règlement"><Controller control={control} name="dateDernierReglement" render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ>
          <Champ label="Échéances restantes"><Input type="number" min="0" {...register("nombreEcheancesRestantes")} /></Champ>
          <Champ label="Montant restant dû"><Input type="number" min="0" step="0.01" {...register("montantRestantDu")} /></Champ>
        </CardContent>
      </Card>

      <Card>
        <CardHeader><CardTitle>4. Impayés, retards et restructuration</CardTitle></CardHeader>
        <CardContent className="grid grid-cols-1 gap-4 md:grid-cols-3">
          <Champ label="Échéances impayées"><Input type="number" min="0" {...register("nombreEcheancesImpayees")} /></Champ>
          <Champ label="Montant impayés"><Input type="number" min="0" step="0.01" {...register("montantImpayes")} /></Champ>
          <Champ label="Max montant impayés"><Input type="number" min="0" step="0.01" {...register("maxMontantImpayes")} /></Champ>
          <Champ label="Max échéances impayées"><Input type="number" min="0" {...register("maxNombreEcheancesImpayees")} /></Champ>
          <Champ label="Date max impayés"><Controller control={control} name="dateMaxNombreEcheancesImpayees" render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ>
          <Champ label="Jours de retard"><Input type="number" min="0" {...register("nombreJoursRetard")} /></Champ>
          <Champ label="Max jours de retard"><Input type="number" min="0" {...register("maxNombreJoursRetard")} /></Champ>
          <Champ label="Date max jours retard"><Controller control={control} name="dateMaxNombreJoursRetard" render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ>
          <Champ label="Pire statut"><Input {...register("pireStatut")} placeholder="0, 1, 2, DEFAUT..." /></Champ>
          <Champ label="Date pire statut"><Controller control={control} name="datePireStatut" render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ>
          <Champ label="Code restructuration"><Input {...register("codeRestructuration")} /></Champ>
          <Champ label="Date restructuration"><Controller control={control} name="dateCodeRestructuration" render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ>
          <Champ label="Contrat d'origine"><Input {...register("contratOrigine")} /></Champ>
          <Champ label="Nouveau contrat"><Input {...register("nouveauContrat")} /></Champ>
          <Champ label="Note"><Input {...register("note")} /></Champ>
        </CardContent>
      </Card>

      <Card>
        <CardHeader><CardTitle>5. Échéancier précis</CardTitle></CardHeader>
        <CardContent className="space-y-3">
          {echeances.fields.map((f, i) => (
            <div key={f.id} className="grid grid-cols-2 gap-3 border-b pb-3 md:grid-cols-7">
              <Champ label="N°"><Input type="number" min="1" {...register(`echeances.${i}.numeroEcheance`)} /></Champ>
              <Champ label="Date échéance"><Controller control={control} name={`echeances.${i}.dateEcheance`} render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ>
              <Champ label="Montant dû"><Input type="number" min="0" step="0.01" {...register(`echeances.${i}.montantDu`)} /></Champ>
              <Champ label="Montant payé"><Input type="number" min="0" step="0.01" {...register(`echeances.${i}.montantPaye`)} /></Champ>
              <Champ label="Date paiement"><Controller control={control} name={`echeances.${i}.datePaiement`} render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ>
              <Champ label="Statut">
                <Select defaultValue={f.statut} onValueChange={(v) => { if (v) setValue(`echeances.${i}.statut`, v); }}>
                  <SelectTrigger><span>{f.statut || "Statut"}</span></SelectTrigger>
                  <SelectContent>
                    <SelectItem value="PAYE">Payé</SelectItem>
                    <SelectItem value="EN_RETARD">En retard</SelectItem>
                    <SelectItem value="IMPAYE">Impayé</SelectItem>
                    <SelectItem value="A_TEMPS">À temps</SelectItem>
                  </SelectContent>
                </Select>
              </Champ>
              <div className="flex items-end"><Button type="button" variant="outline" onClick={() => echeances.remove(i)}>✕</Button></div>
            </div>
          ))}
          <Button type="button" variant="outline" onClick={() => echeances.append({ numeroEcheance: String(echeances.fields.length + 1), dateEcheance: "", montantDu: "", montantPaye: "", datePaiement: "", statut: "PAYE" })}>+ Ajouter une échéance</Button>
        </CardContent>
      </Card>

      <Card>
        <CardHeader><CardTitle>6. Historique mensuel consolidé (si pas d'échéances exactes)</CardTitle></CardHeader>
        <CardContent className="space-y-3">
          {mois.fields.map((f, i) => (
            <div key={f.id} className="grid grid-cols-2 gap-3 border-b pb-3 md:grid-cols-6">
              <Champ label="Mois (AAAA-MM)"><Input type="month" {...register(`historiquePaiementMensuel.${i}.mois`)} /></Champ>
              <Champ label="Échéances"><Input type="number" min="0" {...register(`historiquePaiementMensuel.${i}.nombreEcheances`)} /></Champ>
              <Champ label="Impayées"><Input type="number" min="0" {...register(`historiquePaiementMensuel.${i}.nombreEcheancesImpayees`)} /></Champ>
              <Champ label="Jours retard"><Input type="number" min="0" {...register(`historiquePaiementMensuel.${i}.joursDeRetard`)} /></Champ>
              <Champ label="Statut"><Input {...register(`historiquePaiementMensuel.${i}.statut`)} placeholder="0, 1, 2..." /></Champ>
              <div className="flex items-end gap-2">
                <Champ label="Détail source"><Input {...register(`historiquePaiementMensuel.${i}.detailsSource`)} /></Champ>
                <Button type="button" variant="outline" onClick={() => mois.remove(i)}>✕</Button>
              </div>
            </div>
          ))}
          <Button type="button" variant="outline" onClick={() => mois.append({ mois: "", nombreEcheances: "1", nombreEcheancesImpayees: "0", joursDeRetard: "0", statut: "0", detailsSource: "" })}>+ Ajouter un mois d'observation</Button>
        </CardContent>
      </Card>

      <Card>
        <CardHeader><CardTitle>7. Garanties et liens</CardTitle></CardHeader>
        <CardContent className="space-y-4">
          {garanties.fields.map((f, i) => (
            <div key={f.id} className="grid grid-cols-2 gap-3 border-b pb-3 md:grid-cols-4">
              <Champ label="Nature">
                <Select defaultValue={f.nature} onValueChange={(v) => { if (v) setValue(`garanties.${i}.nature`, v); }}>
                  <SelectTrigger><span>{f.nature || "Nature"}</span></SelectTrigger>
                  <SelectContent>
                    <SelectItem value="REELLE">Réelle</SelectItem>
                    <SelectItem value="PERSONNELLE">Personnelle</SelectItem>
                  </SelectContent>
                </Select>
              </Champ>
              <Champ label="Type de garantie"><Input {...register(`garanties.${i}.typeGarantie`)} /></Champ>
              <Champ label="Code établissement"><Input {...register(`garanties.${i}.codeEtablissementGarantie`)} /></Champ>
              <Champ label="Nom garant"><Input {...register(`garanties.${i}.nomGarant`)} /></Champ>
              <Champ label="Code client CB garant"><Input {...register(`garanties.${i}.codeClientCbGarant`)} /></Champ>
              <Champ label="Montant couvert"><Input type="number" min="0" step="0.01" {...register(`garanties.${i}.montantCouvert`)} /></Champ>
              <Champ label="Début de validité"><Controller control={control} name={`garanties.${i}.dateDebutValidite`} render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ>
              <div className="flex items-end gap-2">
                <Champ label="Fin de validité"><Controller control={control} name={`garanties.${i}.dateFinValidite`} render={({ field }) => <DateInput value={field.value} onValueChange={field.onChange} />} /></Champ>
                <Button type="button" variant="outline" onClick={() => garanties.remove(i)}>✕</Button>
              </div>
            </div>
          ))}
          <Button type="button" variant="outline" onClick={() => garanties.append({ nature: "REELLE", typeGarantie: "", codeEtablissementGarantie: "", nomGarant: "", codeClientCbGarant: "", montantCouvert: "", dateDebutValidite: "", dateFinValidite: "" })}>+ Ajouter une garantie</Button>

          {clientsLies.fields.map((f, i) => (
            <div key={f.id} className="grid grid-cols-2 gap-3 border-b pb-3 md:grid-cols-4">
              <Champ label="Code client CB lié"><Input {...register(`clientsLies.${i}.codeClientCb`)} /></Champ>
              <Champ label="Nom client lié"><Input {...register(`clientsLies.${i}.nomClient`)} /></Champ>
              <Champ label="Rôle lié">
                <Select defaultValue={f.roleClient} onValueChange={(v) => { if (v) setValue(`clientsLies.${i}.roleClient`, v); }}>
                  <SelectTrigger><span>{f.roleClient || "Rôle"}</span></SelectTrigger>
                  <SelectContent>
                    <SelectItem value="TITULAIRE">Titulaire</SelectItem>
                    <SelectItem value="CO_TITULAIRE">Co-titulaire</SelectItem>
                    <SelectItem value="GARANT">Garant</SelectItem>
                  </SelectContent>
                </Select>
              </Champ>
              <div className="flex items-end"><Button type="button" variant="outline" onClick={() => clientsLies.remove(i)}>Supprimer le lien</Button></div>
            </div>
          ))}
          <Button type="button" variant="outline" onClick={() => clientsLies.append({ codeClientCb: "", nomClient: "", roleClient: "TITULAIRE" })}>+ Ajouter un client lié</Button>
        </CardContent>
      </Card>

      <div className="flex items-center justify-between pt-4 border-t">
        <Link
          to="/admin/clients"
          className="text-sm text-muted-foreground hover:text-foreground"
        >
          Annuler et retourner
        </Link>
        <Button type="submit" size="lg" disabled={enregistrement || !clientId} className="px-8">
          {enregistrement
            ? "Enregistrement en cours…"
            : contratEnEdition
            ? "Mettre à jour le contrat historique"
            : "Enregistrer l'historique"}
        </Button>
      </div>
    </form>
  );
}
