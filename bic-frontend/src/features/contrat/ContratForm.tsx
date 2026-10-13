import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useForm, useWatch, Controller } from "react-hook-form";
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

const today = new Date().toISOString().slice(0, 10);

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

  const form = useForm<ContratFormInput, unknown, ContratFormValues>({
    resolver: zodResolver(contratSchema),
    defaultValues: {
      modeRattachement: "NOUVELLE_DEMANDE",
      typeContrat: "Prêt personnel",
      dateDemande: today,
      devise: "Ariary malgache",
      periodiciteBudget: "Mensuelle",
    },
  });

  const modeRattachement = useWatch({ control: form.control, name: "modeRattachement" });
  const formValues = useWatch({ control: form.control });
  const recette = Number(formValues.montantRecette) || 0;
  const depense = Number(formValues.montantDepense) || 0;
  const aBudget = Boolean(formValues.montantRecette || formValues.montantDepense);
  const reste = recette - depense;
  const ratio = depense > 0 ? ((reste / depense) * 100) : null;

  const getEvaluationRisque = () => {
    if (!aBudget || ratio === null) return null;
    if (ratio <= 0) return { label: "Risque Très Élevé (Catégorie E, score 300 - 399)", color: "text-red-700 bg-red-50 border-red-300" };
    if (ratio <= 25) return { label: "Risque Élevé (Catégorie D, score 400 - 459)", color: "text-orange-700 bg-orange-50 border-orange-300" };
    if (ratio <= 50) return { label: "Risque Moyen (Catégorie C, score 460 - 499)", color: "text-yellow-800 bg-yellow-50 border-yellow-300" };
    if (ratio < 100) return { label: "Risque Faible (Catégorie B, score 500 - 529)", color: "text-lime-800 bg-lime-50 border-lime-300" };
    return { label: "Risque Très Faible (Catégorie A, score 530 - 850)", color: "text-emerald-800 bg-emerald-50 border-emerald-300" };
  };

  const evaluation = getEvaluationRisque();

  async function onSubmit(values: ContratFormValues) {
    if (!clientId) return;
    setSubmitting(true);
    try {
      const contrat = await contratApi.creer({ ...values, clientId: Number(clientId) });
      setContratId(contrat.id);
      navigate(`/demande/rapport/${contrat.id}`);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form onSubmit={form.handleSubmit(onSubmit)} className="mx-auto max-w-5xl space-y-6 p-6">
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
                className={getFieldBorderClass(!!form.formState.errors.codeClientCbCible, !!formValues.codeClientCbCible)}
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
                <SelectTrigger className={getFieldBorderClass(!!form.formState.errors.typeContrat, !!formValues.typeContrat)}>
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
                <SelectTrigger className={getFieldBorderClass(!!form.formState.errors.roleClient, !!formValues.roleClient)}>
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
                    className={getFieldBorderClass(!!form.formState.errors.dateDemande, !!field.value)}
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
                    className={`h-10 ${getFieldBorderClass(!!form.formState.errors.montantFinance, !!field.value)}`}
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
              <Label className="font-semibold text-slate-800">Nombre total échéances <span className="text-red-500">*</span></Label>
              <Input
                type="number"
                {...form.register("nombreTotalEcheances")}
                className={getFieldBorderClass(!!form.formState.errors.nombreTotalEcheances, !!formValues.nombreTotalEcheances)}
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
                className={getFieldBorderClass(!!form.formState.errors.montantEcheanceMensuelle, !!formValues.montantEcheanceMensuelle)}
              />
            </div>

            <div className="space-y-1.5">
              <Label className="font-semibold text-slate-800">Devise <span className="text-red-500">*</span></Label>
              <Select defaultValue="Ariary malgache" onValueChange={(v) => form.setValue("devise", v as string)}>
                <SelectTrigger className={getFieldBorderClass(!!form.formState.errors.devise, !!formValues.devise)}>
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
                <SelectTrigger className={getFieldBorderClass(false, !!formValues.periodicitePaiement)}>
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

      {/* CARTE 2 : Revenus et Dépenses (Capacité budgétaire) */}
      <Card>
        <CardHeader>
          <CardTitle>Revenus et Dépenses (Capacité budgétaire)</CardTitle>
        </CardHeader>
        <CardContent className="space-y-5">
          <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
            <div className="space-y-1.5">
              <Label className="font-normal text-slate-600">Périodicité</Label>
              <Select
                defaultValue="Mensuelle"
                onValueChange={(v) => form.setValue("periodiciteBudget", v as string)}
              >
                <SelectTrigger className={getFieldBorderClass(false, !!formValues.periodiciteBudget)}>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="Mensuelle">Mensuelle</SelectItem>
                  <SelectItem value="Annuelle">Annuelle</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-1.5">
              <Label className="font-normal text-slate-600">Revenus / Recettes (Ar)</Label>
              <Controller
                control={form.control}
                name="montantRecette"
                render={({ field }) => (
                  <MontantInput
                    className={`h-10 ${getFieldBorderClass(false, !!field.value)}`}
                    value={String(field.value ?? "")}
                    onValueChange={(v) => field.onChange(v ? Number(v) : undefined)}
                    placeholder="Ex: 150 000"
                  />
                )}
              />
            </div>

            <div className="space-y-1.5">
              <Label className="font-normal text-slate-600">Dépenses (Ar)</Label>
              <Controller
                control={form.control}
                name="montantDepense"
                render={({ field }) => (
                  <MontantInput
                    className={`h-10 ${getFieldBorderClass(false, !!field.value)}`}
                    value={String(field.value ?? "")}
                    onValueChange={(v) => field.onChange(v ? Number(v) : undefined)}
                    placeholder="Ex: 120 000"
                  />
                )}
              />
            </div>
          </div>

          {/* Calcul dynamique du reste et du ratio */}
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
                    {ratio !== null ? `${ratio.toFixed(2)} %` : "—"}
                  </span>
                </div>
                <div>
                  <span className="text-slate-500 block">Évaluation de solvabilité</span>
                  {evaluation ? (
                    <span className={`inline-block px-2.5 py-1 text-xs font-semibold rounded-md border ${evaluation.color}`}>
                      {evaluation.label}
                    </span>
                  ) : (
                    <span className="text-slate-400 text-xs">Veuillez renseigner les dépenses</span>
                  )}
                </div>
              </div>

              {ratio !== null && (
                <div className="text-xs text-slate-500 border-t pt-2">
                  <p>
                    <strong>Barème d&apos;évaluation :</strong> Reste ≤ 0% (Risque Très Élevé - E) | Reste ≤ 25% (Risque Élevé - D) | Reste ~ 50% (Risque Moyen - C) | Reste &lt; 100% (Risque Faible - B) | Recette ≥ 2× Dépenses (Risque Très Faible - A).
                  </p>
                </div>
              )}
            </div>
          )}
        </CardContent>
      </Card>

      <div className="flex justify-end">
        <Button type="submit" disabled={submitting}>
          {submitting ? "Enregistrement..." : "Générer le rapport de solvabilité"}
        </Button>
      </div>
    </form>
  );
}
