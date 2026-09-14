import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useForm, useWatch } from "react-hook-form";
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

const today = new Date().toISOString().slice(0, 10);

export function ContratForm() {
  const { clientId } = useParams<{ clientId: string }>();
  const navigate = useNavigate();
  const [submitting, setSubmitting] = useState(false);


  const form = useForm<ContratFormInput, unknown, ContratFormValues>({
    resolver: zodResolver(contratSchema),
    defaultValues: {
      modeRattachement: "NOUVELLE_DEMANDE",
      typeContrat: "Prêt personnel",
      dateDemande: today,
      devise: "Ariary malgache",
    },
  });

  const modeRattachement = useWatch({ control: form.control, name: "modeRattachement" });

  async function onSubmit(values: ContratFormValues) {
    if (!clientId) return;
    setSubmitting(true);
    try {
      const contrat = await contratApi.creer({ ...values, clientId: Number(clientId) });
      navigate(`/demande/rapport/${contrat.id}`);
    } finally {
      setSubmitting(false);
    }
  }

  const { setContratId } = useDemandeEnCours();

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
    <form onSubmit={form.handleSubmit(onSubmit)} className="mx-auto max-w-3xl space-y-6 p-6">
      <Card>
        <CardHeader><CardTitle>Données sur la demande</CardTitle></CardHeader>
        <CardContent className="space-y-6">
          <div className="space-y-2">
            <Label>Rattachement</Label>
            <RadioGroup
              defaultValue="NOUVELLE_DEMANDE"
              onValueChange={(v) => form.setValue("modeRattachement", v as ModeRattachement)}
              className="grid gap-2"
            >
              <div className="flex items-center gap-2">
                <RadioGroupItem value="NOUVELLE_DEMANDE" id="mode-nouvelle" />
                <Label htmlFor="mode-nouvelle" className="font-normal">Nouvelle demande</Label>
              </div>
              <div className="flex items-center gap-2">
                <RadioGroupItem value="DEMANDE_EXISTANTE" id="mode-existante" />
                <Label htmlFor="mode-existante" className="font-normal">Rattacher client à une demande existante</Label>
              </div>
              <div className="flex items-center gap-2">
                <RadioGroupItem value="ENTREPRISE" id="mode-entreprise" />
                <Label htmlFor="mode-entreprise" className="font-normal">Rattacher client à une entreprise</Label>
              </div>
            </RadioGroup>
          </div>

          {modeRattachement === "DEMANDE_EXISTANTE" && (
            <div className="space-y-1.5">
              <Label>Code Client CB <span className="text-red-500">*</span></Label>
              <Input {...form.register("codeClientCbCible")} placeholder="Ex : L00190003" />
              {form.formState.errors.codeClientCbCible && <p className="text-sm text-red-500">{form.formState.errors.codeClientCbCible.message}</p>}
            </div>
          )}

          <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
            <div className="space-y-1.5">
              <Label>Type de contrat <span className="text-red-500">*</span></Label>
              <Select defaultValue="Prêt personnel" onValueChange={(v) => form.setValue("typeContrat", v as string)}>
                <SelectTrigger><SelectValue /></SelectTrigger>
                <SelectContent>
                  <SelectItem value="Prêt personnel">Prêt personnel</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-1.5">
              <Label>Type de relation avec l'entreprise</Label>
              <Input className="h-10" {...form.register("typeRelationEntreprise")} />
            </div>

            <div className="space-y-1.5">
              <Label>Rôle Client <span className="text-red-500">*</span></Label>
              <Select onValueChange={(v) => form.setValue("roleClient", v as RoleClient, { shouldValidate: true })}>
                <SelectTrigger><SelectValue placeholder="Sélectionner" /></SelectTrigger>
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
              <Label>Date de la demande <span className="text-red-500">*</span></Label>
              <Input type="date" {...form.register("dateDemande")} />
            </div>

            <div className="space-y-1.5">
              <Label>Montant Financé <span className="text-red-500">*</span></Label>
              <Input type="number" step="0.01" {...form.register("montantFinance")} />
              {form.formState.errors.montantFinance && (
                <p className="text-sm text-red-500">{form.formState.errors.montantFinance.message}</p>
              )}
            </div>

            <div className="space-y-1.5">
              <Label>Nombre total échéances <span className="text-red-500">*</span></Label>
              <Input type="number" {...form.register("nombreTotalEcheances")} />
              {form.formState.errors.nombreTotalEcheances && (
                <p className="text-sm text-red-500">{form.formState.errors.nombreTotalEcheances.message}</p>
              )}
            </div>

            <div className="space-y-1.5">
              <Label>Montant échéance mensuelle</Label>
              <Input type="number" step="0.01" {...form.register("montantEcheanceMensuelle")} />
            </div>

            <div className="space-y-1.5">
              <Label>Devise <span className="text-red-500">*</span></Label>
              <Select defaultValue="Ariary malgache" onValueChange={(v) => form.setValue("devise", v as string)}>
                <SelectTrigger><SelectValue /></SelectTrigger>
                <SelectContent>
                  <SelectItem value="Ariary malgache">Ariary malgache</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-1.5">
              <Label>Périodicité de paiement</Label>
              <Select onValueChange={(v) => form.setValue("periodicitePaiement", v as string)}>
                <SelectTrigger><SelectValue placeholder="Sélectionner" /></SelectTrigger>
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

      <div className="flex justify-end">
        <Button type="submit" disabled={submitting}>
          {submitting ? "Enregistrement..." : "Générer le rapport de solvabilité"}
        </Button>
      </div>
    </form>
  );
}