import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useForm, useWatch, Controller, useFieldArray } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { contratSchema, type ContratFormValues, type ContratFormInput } from "./contratSchema";
import { contratApi } from "@/api/contratApi";
import type { ModeRattachement, RoleClient } from "@/types/contrat";
import { useDemandeEnCours } from "@/context/DemandeEnCoursContext";
import { ChampsObligatoiresNote } from "@/components/ui/champs-obligatoires-note";
import { MontantInput } from "@/components/ui/montant-input";
import { DateInput } from "@/components/ui/date-input";
import { Plus, Trash2 } from "lucide-react";

const today = new Date().toISOString().slice(0, 10);

const FIELD_H = "min-h-10 h-auto py-2";

const getFieldBorderClass = (hasError?: boolean, hasValue?: boolean) => {
  if (hasError) return "border-red-500 focus-visible:ring-red-500 focus-visible:border-red-500";
  if (hasValue) return "border-blue-500 focus-visible:ring-blue-500 focus-visible:border-blue-500";
  return "";
};

export function ContratForm() {
  const { clientId } = useParams<{ clientId: string }>();
  const navigate = useNavigate();
  const { setContratId } = useDemandeEnCours();
  const [submitting, setSubmitting] = useState(false);
  const [erreurSoumission, setErreurSoumission] = useState<string | null>(null);

  const form = useForm<ContratFormInput, unknown, ContratFormValues>({
    resolver: zodResolver(contratSchema),
    defaultValues: {
      modeRattachement: "NOUVELLE_DEMANDE",
      typeContrat: "Prêt personnel",
      dateDemande: today,
      devise: "Ariary malgache",
      periodiciteBudget: "Mensuelle",
      typeGarantie: "Terrain",
      revenusComplementaires: [],
    },
  });

  const revenusArray = useFieldArray({ control: form.control, name: "revenusComplementaires" });
  const modeRattachement = useWatch({ control: form.control, name: "modeRattachement" });
  const formValues = useWatch({ control: form.control });

  // Calculs budgétaires
  const recette = Number(formValues.montantRecette) || 0;
  const depense = Number(formValues.montantDepense) || 0;
  const aBudget = Boolean(formValues.montantRecette || formValues.montantDepense);
  const reste = recette - depense;
  const ratioBudget = depense > 0 ? ((reste / depense) * 100) : null;

  const getEvaluationBudget = () => {
    if (!aBudget || ratioBudget === null) return null;
    if (ratioBudget <= 0) return { label: "Risque Très Élevé (Catégorie E, score 300 - 399)", color: "text-red-700 bg-red-50 border-red-300" };
    if (ratioBudget <= 25) return { label: "Risque Élevé (Catégorie D, score 400 - 459)", color: "text-orange-700 bg-orange-50 border-orange-300" };
    if (ratioBudget <= 50) return { label: "Risque Moyen (Catégorie C, score 460 - 499)", color: "text-yellow-800 bg-yellow-50 border-yellow-300" };
    if (ratioBudget < 100) return { label: "Risque Faible (Catégorie B, score 500 - 529)", color: "text-lime-800 bg-lime-50 border-lime-300" };
    return { label: "Risque Très Faible (Catégorie A, score 530 - 850)", color: "text-emerald-800 bg-emerald-50 border-emerald-300" };
  };
  const evaluationBudget = getEvaluationBudget();

  // Calculs garantie (+3% du montant demandé par mois)
  const montantFinance = Number(formValues.montantFinance) || 0;
  const dureeMois = Number(formValues.nombreTotalEcheances) || 1;
  const tauxInteretTotal = 0.03 * dureeMois;
  const montantInteret = montantFinance * tauxInteretTotal;
  const totalDemandeInteret = montantFinance + montantInteret;
  const valeurGarantie = Number(formValues.valeurGarantie) || 0;
  const aGarantie = Boolean(formValues.valeurGarantie || formValues.typeGarantie);
  const ratioGarantie = totalDemandeInteret > 0 && valeurGarantie > 0
    ? ((valeurGarantie / totalDemandeInteret) * 100)
    : null;

  const getEvaluationGarantie = () => {
    if (!aGarantie || ratioGarantie === null) return null;
    if (ratioGarantie <= 100) return { label: "Risque Très Élevé (Insuffisant ≤ 100%)", color: "text-red-700 bg-red-50 border-red-300" };
    if (ratioGarantie <= 120) return { label: "Risque Élevé (100% à 120%)", color: "text-orange-700 bg-orange-50 border-orange-300" };
    if (ratioGarantie <= 150) return { label: "Risque Moyen (120% à 150%)", color: "text-yellow-800 bg-yellow-50 border-yellow-300" };
    if (ratioGarantie <= 200) return { label: "Risque Faible (150% à 200%)", color: "text-lime-800 bg-lime-50 border-lime-300" };
    return { label: "Risque Très Faible (Satisfaisant > 200%)", color: "text-emerald-800 bg-emerald-50 border-emerald-300" };
  };
  const evaluationGarantie = getEvaluationGarantie();

  const valeurPatrimoine = Number(formValues.valeurPatrimoine) || 0;
  const aPatrimoine = formValues.valeurPatrimoine !== undefined && formValues.valeurPatrimoine !== null;
  const margePatrimoine = aPatrimoine ? valeurPatrimoine - totalDemandeInteret : null;
  const ratioPatrimoine = totalDemandeInteret > 0 && valeurPatrimoine > 0
    ? valeurPatrimoine / totalDemandeInteret * 100
    : null;
  const getEvaluationPatrimoine = () => {
    if (ratioPatrimoine === null) return null;
    if (ratioPatrimoine <= 100) return { label: "Insuffisant — inférieur au montant dû", color: "text-red-700 bg-red-50 border-red-300" };
    if (ratioPatrimoine <= 120) return { label: "Marge faible (100 % à 120 %)", color: "text-orange-700 bg-orange-50 border-orange-300" };
    if (ratioPatrimoine <= 150) return { label: "Marge moyenne (120 % à 150 %)", color: "text-yellow-800 bg-yellow-50 border-yellow-300" };
    if (ratioPatrimoine <= 200) return { label: "Marge satisfaisante (150 % à 200 %)", color: "text-lime-800 bg-lime-50 border-lime-300" };
    return { label: "Marge très satisfaisante (> 200 %)", color: "text-emerald-800 bg-emerald-50 border-emerald-300" };
  };
  const evaluationPatrimoine = getEvaluationPatrimoine();

  async function onSubmit(values: ContratFormValues) {
    if (!clientId) return;
    setSubmitting(true);
    setErreurSoumission(null);
    try {
      const revenusComplementaires = values.revenusComplementaires?.flatMap((revenu) => {
        const typeRevenu = revenu.typeRevenu?.trim();
        const description = revenu.description?.trim();
        const montantAnnuel = revenu.montantAnnuel;
        if (!typeRevenu || !description || montantAnnuel === undefined || montantAnnuel <= 0) return [];
        return [{
          typeRevenu,
          description,
          montantAnnuel,
          devise: revenu.devise?.trim() || values.devise,
        }];
      });
      const contrat = await contratApi.creer({
        ...values,
        revenusComplementaires,
        clientId: Number(clientId),
      });
      setContratId(contrat.id);
      navigate(`/demande/rapport/${contrat.id}`);
    } catch {
      setErreurSoumission("Erreur lors de la création du contrat. Vérifiez les données et réessayez.");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form onSubmit={form.handleSubmit(onSubmit)} className="mx-auto max-w-5xl space-y-6 p-6">
      {erreurSoumission && <div className="rounded-md border border-red-200 bg-red-50 p-3 text-sm text-red-700">{erreurSoumission}</div>}
      <ChampsObligatoiresNote />

      {/* CARTE 1 : Données sur la demande */}
      <Card>
        <CardHeader><CardTitle>Données sur la demande</CardTitle></CardHeader>
        <CardContent className="space-y-6">
          <div className="space-y-2">
            <Label className="font-normal text-slate-600">Rattachement</Label>
            <RadioGroup
              defaultValue="NOUVELLE_DEMANDE"
              onValueChange={(v) => form.setValue("modeRattachement", v as ModeRattachement)}
              className="grid gap-2"
            >
              <div className="flex items-center gap-2">
                <RadioGroupItem value="NOUVELLE_DEMANDE" id="mode-nouvelle" />
                <Label htmlFor="mode-nouvelle" className="font-normal text-slate-700">Nouvelle demande</Label>
              </div>
              <div className="flex items-center gap-2">
                <RadioGroupItem value="DEMANDE_EXISTANTE" id="mode-existante" />
                <Label htmlFor="mode-existante" className="font-normal text-slate-700">Rattacher client à une demande existante</Label>
              </div>
            </RadioGroup>
          </div>

          {modeRattachement === "DEMANDE_EXISTANTE" && (
            <div className="space-y-1.5">
              <Label className="font-semibold text-slate-800">Code Client CB <span className="text-red-500">*</span></Label>
              <Input
                {...form.register("codeClientCbCible")}
                placeholder="Ex : L00190003"
                className={`${FIELD_H} ${getFieldBorderClass(!!form.formState.errors.codeClientCbCible, !!formValues.codeClientCbCible)}`}
              />
              {form.formState.errors.codeClientCbCible && (
                <p className="text-sm text-red-500">{form.formState.errors.codeClientCbCible.message}</p>
              )}
            </div>
          )}

          <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
            <div className="space-y-1.5">
              <Label className="font-semibold text-slate-800">Type de contrat <span className="text-red-500">*</span></Label>
              <Select defaultValue="Prêt personnel" onValueChange={(v) => form.setValue("typeContrat", v as string)}>
                <SelectTrigger className={`${FIELD_H} ${getFieldBorderClass(!!form.formState.errors.typeContrat, !!formValues.typeContrat)}`}>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="Prêt personnel">Prêt personnel</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-1.5">
              <Label className="font-semibold text-slate-800">Rôle Client <span className="text-red-500">*</span></Label>
              <Select onValueChange={(v) => form.setValue("roleClient", v as RoleClient, { shouldValidate: true })}>
                <SelectTrigger className={`${FIELD_H} ${getFieldBorderClass(!!form.formState.errors.roleClient, !!formValues.roleClient)}`}>
                  <SelectValue placeholder="Sélectionner" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="TITULAIRE">Titulaire</SelectItem>
                  <SelectItem value="CO_TITULAIRE">Co-titulaire</SelectItem>
                  <SelectItem value="GARANT">Garant</SelectItem>
                </SelectContent>
              </Select>
              {form.formState.errors.roleClient && (
                <p className="text-sm text-red-500">{form.formState.errors.roleClient.message}</p>
              )}
            </div>

            <div className="space-y-1.5">
              <Label className="font-semibold text-slate-800">Date de la demande <span className="text-red-500">*</span></Label>
              <Controller
                control={form.control}
                name="dateDemande"
                render={({ field }) => (
                  <DateInput
                    value={field.value}
                    onValueChange={field.onChange}
                    className={`${FIELD_H} ${getFieldBorderClass(!!form.formState.errors.dateDemande, !!field.value)}`}
                  />
                )}
              />
            </div>

            <div className="space-y-1.5">
              <Label className="font-semibold text-slate-800">Montant Financé <span className="text-red-500">*</span></Label>
              <Controller
                control={form.control}
                name="montantFinance"
                render={({ field }) => (
                  <MontantInput
                    className={`${FIELD_H} ${getFieldBorderClass(!!form.formState.errors.montantFinance, !!field.value)}`}
                    value={String(field.value ?? "")}
                    onValueChange={(v) => field.onChange(Number(v))}
                  />
                )}
              />
              {form.formState.errors.montantFinance && (
                <p className="text-sm text-red-500">{form.formState.errors.montantFinance.message}</p>
              )}
            </div>

            <div className="space-y-1.5">
              <Label className="font-semibold text-slate-800">Nombre total échéances (mois) <span className="text-red-500">*</span></Label>
              <Input
                type="number"
                {...form.register("nombreTotalEcheances")}
                className={`${FIELD_H} ${getFieldBorderClass(!!form.formState.errors.nombreTotalEcheances, !!formValues.nombreTotalEcheances)}`}
              />
              {form.formState.errors.nombreTotalEcheances && (
                <p className="text-sm text-red-500">{form.formState.errors.nombreTotalEcheances.message}</p>
              )}
            </div>

            <div className="space-y-1.5">
              <Label className="font-normal text-slate-600">Montant échéance mensuelle</Label>
              <Input
                type="number"
                step="0.01"
                {...form.register("montantEcheanceMensuelle")}
                className={`${FIELD_H} ${getFieldBorderClass(!!form.formState.errors.montantEcheanceMensuelle, !!formValues.montantEcheanceMensuelle)}`}
              />
            </div>

            <div className="space-y-1.5">
              <Label className="font-semibold text-slate-800">Devise <span className="text-red-500">*</span></Label>
              <Select defaultValue="Ariary malgache" onValueChange={(v) => form.setValue("devise", v as string)}>
                <SelectTrigger className={`${FIELD_H} ${getFieldBorderClass(!!form.formState.errors.devise, !!formValues.devise)}`}>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="Ariary malgache">Ariary malgache</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-1.5">
              <Label className="font-normal text-slate-600">Périodicité de paiement</Label>
              <Select onValueChange={(v) => form.setValue("periodicitePaiement", v as string)}>
                <SelectTrigger className={`${FIELD_H} ${getFieldBorderClass(false, !!formValues.periodicitePaiement)}`}>
                  <SelectValue placeholder="Sélectionner" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="Mensuelle">Mensuelle</SelectItem>
                  <SelectItem value="Trimestrielle">Trimestrielle</SelectItem>
                  <SelectItem value="Semestrielle">Semestrielle</SelectItem>
                  <SelectItem value="Annuelle">Annuelle</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* CARTE 2 : Revenus et Dépenses (Capacité budgétaire) - OBLIGATOIRE */}
      <Card>
        <CardHeader>
          <div className="flex items-center justify-between">
            <CardTitle>
              Capacité budgétaire (Revenus et Dépenses) <span className="text-red-500">*</span>
            </CardTitle>
            <span className="text-xs font-semibold text-blue-700 bg-blue-50 border border-blue-200 px-2.5 py-1 rounded-full">
              Obligatoire — conditionne l&apos;évaluation du score
            </span>
          </div>
        </CardHeader>
        <CardContent className="space-y-5">
          <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
            <div className="space-y-1.5">
              <Label className="font-normal text-slate-600">Périodicité</Label>
              <Select
                defaultValue="Mensuelle"
                onValueChange={(v) => form.setValue("periodiciteBudget", v as string)}
              >
                <SelectTrigger className={`${FIELD_H} ${getFieldBorderClass(false, !!formValues.periodiciteBudget)}`}>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="Mensuelle">Mensuelle</SelectItem>
                  <SelectItem value="Annuelle">Annuelle</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-1.5">
              <Label className="font-semibold text-slate-800">
                Revenus / Recettes (Ar) <span className="text-red-500">*</span>
              </Label>
              <Controller
                control={form.control}
                name="montantRecette"
                render={({ field }) => (
                  <MontantInput
                    className={`${FIELD_H} ${getFieldBorderClass(!!form.formState.errors.montantRecette, !!field.value)}`}
                    value={String(field.value ?? "")}
                    onValueChange={(v) => field.onChange(v ? Number(v) : undefined)}
                    placeholder="Ex: 150 000"
                  />
                )}
              />
              {form.formState.errors.montantRecette && (
                <p className="text-sm text-red-500">{form.formState.errors.montantRecette.message}</p>
              )}
            </div>

            <div className="space-y-1.5">
              <Label className="font-semibold text-slate-800">
                Dépenses (Ar) <span className="text-red-500">*</span>
              </Label>
              <Controller
                control={form.control}
                name="montantDepense"
                render={({ field }) => (
                  <MontantInput
                    className={`${FIELD_H} ${getFieldBorderClass(!!form.formState.errors.montantDepense, !!field.value)}`}
                    value={String(field.value ?? "")}
                    onValueChange={(v) => field.onChange(v !== undefined && v !== "" ? Number(v) : undefined)}
                    placeholder="Ex: 120 000"
                  />
                )}
              />
              {form.formState.errors.montantDepense && (
                <p className="text-sm text-red-500">{form.formState.errors.montantDepense.message}</p>
              )}
            </div>
          </div>

          {/* Calcul dynamique du reste et du ratio budgétaire */}
          {aBudget && (
            <div className="rounded-lg border bg-slate-50 p-4 space-y-3">
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 text-sm">
                <div>
                  <span className="text-slate-500 block">Reste à vivre (Recette - Dépense)</span>
                  <span className={`text-base font-semibold ${reste < 0 ? "text-red-600" : "text-slate-900"}`}>
                    {reste.toLocaleString("fr-FR")} Ar
                  </span>
                </div>
                <div>
                  <span className="text-slate-500 block">Ratio Reste / Dépense</span>
                  <span className="text-base font-semibold text-slate-900">
                    {ratioBudget !== null ? `${ratioBudget.toFixed(2)} %` : "—"}
                  </span>
                </div>
                <div>
                  <span className="text-slate-500 block">Évaluation de solvabilité</span>
                  {evaluationBudget ? (
                    <span className={`inline-block px-2.5 py-1 text-xs font-semibold rounded-md border ${evaluationBudget.color}`}>
                      {evaluationBudget.label}
                    </span>
                  ) : (
                    <span className="text-slate-400 text-xs">Veuillez renseigner les dépenses</span>
                  )}
                </div>
              </div>

              {ratioBudget !== null && (
                <div className="text-xs text-slate-500 border-t pt-2">
                  <p>
                    <strong>Barème budgétaire :</strong> Reste ≤ 0% (Risque Très Élevé - E) | Reste ≤ 25% (Risque Élevé - D) | Reste ~ 50% (Risque Moyen - C) | Reste &lt; 100% (Risque Faible - B) | Recette ≥ 2× Dépenses (Risque Très Faible - A).
                  </p>
                </div>
              )}
            </div>
          )}
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <CardTitle>Professions et autres revenus</CardTitle>
              <p className="mt-1 text-sm text-slate-500">
                Facultatif — les revenus annuels renseignés complètent le revenu pris en compte pour le score.
              </p>
            </div>
            <Button
              type="button"
              variant="outline"
              onClick={() => revenusArray.append({ typeRevenu: "PROFESSION", devise: form.getValues("devise") })}
            >
              <Plus className="mr-1.5 h-4 w-4" /> Ajouter un autre moyen de revenus
            </Button>
          </div>
        </CardHeader>
        <CardContent className="space-y-4">
          {revenusArray.fields.map((field, index) => (
            <div key={field.id} className="rounded-lg border border-slate-200 bg-slate-50/50 p-4">
              <div className="mb-3 flex items-center justify-between">
                <span className="text-sm font-semibold text-slate-700">Revenu complémentaire #{index + 1}</span>
                <Button type="button" variant="ghost" size="sm" onClick={() => revenusArray.remove(index)}>
                  <Trash2 className="mr-1 h-4 w-4 text-red-500" /> Supprimer
                </Button>
              </div>
              <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
                <div className="space-y-1.5">
                  <Label>Type de revenu</Label>
                  <Select
                    defaultValue={field.typeRevenu || "PROFESSION"}
                    onValueChange={(value) => {
                      if (value) form.setValue(`revenusComplementaires.${index}.typeRevenu`, value);
                    }}
                  >
                    <SelectTrigger className={FIELD_H}><SelectValue /></SelectTrigger>
                    <SelectContent>
                      <SelectItem value="PROFESSION">Profession / activité</SelectItem>
                      <SelectItem value="AUTRES_REVENUS">Autres revenus</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
                <div className="space-y-1.5">
                  <Label>Description</Label>
                  <Input
                    {...form.register(`revenusComplementaires.${index}.description`)}
                    placeholder="Ex. élevage, plantation, commerce"
                    className={FIELD_H}
                  />
                </div>
                <div className="space-y-1.5">
                  <Label>Revenu annuel (Ar)</Label>
                  <Controller
                    control={form.control}
                    name={`revenusComplementaires.${index}.montantAnnuel`}
                    render={({ field: amountField }) => (
                      <MontantInput
                        className={FIELD_H}
                        value={String(amountField.value ?? "")}
                        onValueChange={(value) => amountField.onChange(value ? Number(value) : undefined)}
                        placeholder="Ex. 600 000"
                      />
                    )}
                  />
                </div>
              </div>
            </div>
          ))}
        </CardContent>
      </Card>

      {/* CARTE 3 : Garantie - Optionnel mais valorisant */}
      <Card>
        <CardHeader>
          <div className="flex items-center justify-between">
            <CardTitle>Garantie de la demande</CardTitle>
            <span className="text-xs font-normal text-slate-500 bg-slate-100 border px-2.5 py-1 rounded-full">
              Optionnel — impacte le score si complétée
            </span>
          </div>
        </CardHeader>
        <CardContent className="space-y-5">
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
            <div className="space-y-1.5">
              <Label className="font-normal text-slate-600">Type de garantie</Label>
              <Select
                defaultValue="Terrain"
                onValueChange={(v) => form.setValue("typeGarantie", v as string)}
              >
                <SelectTrigger className={`${FIELD_H} ${getFieldBorderClass(false, !!formValues.typeGarantie)}`}>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="Terrain">Terrain</SelectItem>
                  <SelectItem value="Maison">Maison</SelectItem>
                  <SelectItem value="Hypothèque immobilière">Hypothèque immobilière</SelectItem>
                  <SelectItem value="Gage de véhicule">Gage de véhicule</SelectItem>
                  <SelectItem value="Nantissement de matériel">Nantissement de matériel</SelectItem>
                  <SelectItem value="Caution solidaire">Caution solidaire</SelectItem>
                  <SelectItem value="Dépôt de garantie / Cash collateral">Dépôt de garantie / Cash collateral</SelectItem>
                  <SelectItem value="Autre garantie réelle">Autre garantie réelle</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-1.5">
              <Label className="font-normal text-slate-600">Valeur de la garantie (Ar)</Label>
              <Controller
                control={form.control}
                name="valeurGarantie"
                render={({ field }) => (
                  <MontantInput
                    className={`${FIELD_H} ${getFieldBorderClass(false, !!field.value)}`}
                    value={String(field.value ?? "")}
                    onValueChange={(v) => field.onChange(v ? Number(v) : undefined)}
                    placeholder="Ex: 2 000 000"
                  />
                )}
              />
            </div>
          </div>

          {/* Calcul dynamique de la couverture de garantie */}
          {aGarantie && (
            <div className="rounded-lg border bg-slate-50 p-4 space-y-3">
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 text-sm">
                <div>
                  <span className="text-slate-500 block">Total Demande + Intérêts (+3%/mois)</span>
                  <span className="text-base font-semibold text-slate-900">
                    {Math.round(totalDemandeInteret).toLocaleString("fr-FR")} Ar
                  </span>
                  <span className="text-xs text-slate-400 block">
                    (Demande {montantFinance.toLocaleString("fr-FR")} Ar + Intérêts {Math.round(montantInteret).toLocaleString("fr-FR")} Ar sur {dureeMois} mois)
                  </span>
                </div>
                <div>
                  <span className="text-slate-500 block">Ratio Couverture Garantie</span>
                  <span className="text-base font-semibold text-slate-900">
                    {ratioGarantie !== null ? `${ratioGarantie.toFixed(2)} %` : "—"}
                  </span>
                  <span className="text-xs text-slate-400 block">
                    (Valeur garantie / Total dû)
                  </span>
                </div>
                <div>
                  <span className="text-slate-500 block">Évaluation de la garantie</span>
                  {evaluationGarantie ? (
                    <span className={`inline-block px-2.5 py-1 text-xs font-semibold rounded-md border ${evaluationGarantie.color}`}>
                      {evaluationGarantie.label}
                    </span>
                  ) : (
                    <span className="text-slate-400 text-xs">Veuillez renseigner la valeur de la garantie</span>
                  )}
                </div>
              </div>

              {ratioGarantie !== null && (
                <div className="text-xs text-slate-500 border-t pt-2">
                  <p>
                    <strong>Barème de garantie :</strong> Couverture ≤ 100% (Risque Très Élevé - Insuffisant) | 100% à 120% (Risque Élevé) | 120% à 150% (Risque Moyen) | 150% à 200% (Risque Faible) | &gt; 200% (Risque Très Faible - Satisfaisant).
                  </p>
                </div>
              )}
            </div>
          )}
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <CardTitle>Patrimoine total du client</CardTitle>
              <p className="mt-1 text-sm text-slate-500">
                Facultatif — comparez le patrimoine à la demande et à ses intérêts, comme pour la garantie.
              </p>
            </div>
          </div>
        </CardHeader>
        <CardContent className="space-y-5">
          <div className="space-y-1.5">
            <Label>Patrimoine total du client (Ar)</Label>
            <Controller
              control={form.control}
              name="valeurPatrimoine"
              render={({ field }) => (
                <MontantInput
                  className={FIELD_H}
                  value={String(field.value ?? "")}
                  onValueChange={(value) => field.onChange(value ? Number(value) : undefined)}
                  placeholder="Ex. 5 000 000"
                />
              )}
            />
          </div>

          {aPatrimoine && (
            <div className="rounded-lg border bg-slate-50 p-4">
              <div className="grid grid-cols-1 gap-4 text-sm sm:grid-cols-3">
                <div>
                  <span className="block text-slate-500">Demande + intérêts</span>
                  <span className="font-semibold text-slate-900">
                    {Math.round(totalDemandeInteret).toLocaleString("fr-FR")} Ar
                  </span>
                </div>
                <div>
                  <span className="block text-slate-500">Marge patrimoine</span>
                  <span className={`font-semibold ${margePatrimoine !== null && margePatrimoine < 0 ? "text-red-600" : "text-slate-900"}`}>
                    {margePatrimoine?.toLocaleString("fr-FR")} Ar
                  </span>
                  <span className="block text-xs text-slate-400">Patrimoine − demande et intérêts</span>
                </div>
                <div>
                  <span className="block text-slate-500">Couverture / évaluation</span>
                  <span className="font-semibold text-slate-900">
                    {ratioPatrimoine !== null ? `${ratioPatrimoine.toFixed(2)} %` : "—"}
                  </span>
                  {evaluationPatrimoine && (
                    <span className={`mt-1 inline-block rounded-md border px-2 py-1 text-xs font-semibold ${evaluationPatrimoine.color}`}>
                      {evaluationPatrimoine.label}
                    </span>
                  )}
                </div>
              </div>
            </div>
          )}
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Dépôts et crédits actifs</CardTitle>
          <p className="text-sm text-slate-500">
            Montants totaux déclarés pour le demandeur, le garant et les autres membres concernés.
          </p>
        </CardHeader>
        <CardContent className="grid grid-cols-1 gap-4 md:grid-cols-2">
          <div className="space-y-1.5">
            <Label>Total des dépôts (Ar)</Label>
            <Controller
              control={form.control}
              name="totalDepot"
              render={({ field }) => (
                <MontantInput
                  className={FIELD_H}
                  value={String(field.value ?? "")}
                  onValueChange={(value) => field.onChange(value ? Number(value) : undefined)}
                  placeholder="Somme des dépôts"
                />
              )}
            />
          </div>
          <div className="space-y-1.5">
            <Label>Total des crédits actifs (Ar)</Label>
            <Controller
              control={form.control}
              name="totalCreditActif"
              render={({ field }) => (
                <MontantInput
                  className={FIELD_H}
                  value={String(field.value ?? "")}
                  onValueChange={(value) => field.onChange(value ? Number(value) : undefined)}
                  placeholder="Somme des crédits actifs"
                />
              )}
            />
            
          </div>
        </CardContent>
      </Card>

      <div className="flex justify-end pt-4">
        <Button
          type="submit"
          disabled={submitting}
          size="lg"
          className="bg-black text-white hover:bg-black/90 font-medium"
        >
          {submitting ? "Enregistrement..." : "Générer le rapport de solvabilité"}
        </Button>
      </div>
    </form>
  );
}
