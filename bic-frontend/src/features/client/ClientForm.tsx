import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useForm, useFieldArray, Controller } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger } from "@/components/ui/select";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { clientSchema, type ClientFormInput, type ClientFormValues } from "./clientSchema";
import { clientApi } from "@/api/clientApi";
import { useDemandeEnCours } from "@/context/DemandeEnCoursContext";
import { ChampsObligatoiresNote } from "@/components/ui/champs-obligatoires-note";
import { DateInput } from "@/components/ui/date-input";
import { MontantInput } from "@/components/ui/montant-input";
import type { ClientRequest } from "@/types/client";

const FIELD_H = "min-h-10 h-auto py-2";

export function ClientForm() {
  const navigate = useNavigate();
  const { setClientId } = useDemandeEnCours();
  const [submitting, setSubmitting] = useState(false);
  const [genreLabel, setGenreLabel] = useState<string>();
  const [titreLabel, setTitreLabel] = useState<string>();

  const form = useForm<ClientFormInput, unknown, ClientFormValues>({
    resolver: zodResolver(clientSchema),
    defaultValues: {
      titre: "M.",
      nationalite: "Malgache",
      categorieTiersCode: "0004",
      adresses: [{ typeAdresse: "Individu - Adresse principale", adresseComplete: "" }],
      identifiants: [{ typeIdentifiant: "CIN", numero: "" }],
      affaireIndividuelle: { nom: "", adresse: { adresseComplete: "" } },
    },
  });

  const adressesArray = useFieldArray({ control: form.control, name: "adresses" });
  const identifiantsArray = useFieldArray({ control: form.control, name: "identifiants" });

  async function onSubmit(values: ClientFormValues) {
    setSubmitting(true);
    try {
      const nomAffaire = values.affaireIndividuelle?.nom?.trim();
      const adresseAffaire = values.affaireIndividuelle?.adresse;
      const payload: ClientRequest = {
        ...values,
        affaireIndividuelle: nomAffaire ? {
          nom: nomAffaire,
          adresses: adresseAffaire?.adresseComplete?.trim() ? [{
            adresseComplete: adresseAffaire.adresseComplete.trim(),
            numeroRue: adresseAffaire.numeroRue,
            codePostal: adresseAffaire.codePostal,
            ville: adresseAffaire.ville,
            commune: adresseAffaire.commune,
            region: adresseAffaire.region,
            pays: adresseAffaire.pays,
          }] : [],
        } : undefined,
      };
      const { client } = await clientApi.creer(payload);
      setClientId(client.id);
      navigate(`/demande/nouvelle/contrat/${client.id}`);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form onSubmit={form.handleSubmit(onSubmit)} className="mx-auto max-w-4xl space-y-6 p-6">
      <ChampsObligatoiresNote />

      <Card>
        <CardHeader><CardTitle>Détails client</CardTitle></CardHeader>
        <CardContent className="grid grid-cols-1 gap-4 md:grid-cols-2">
          <div className="space-y-1.5">
            <Label>Titre</Label>
            <Select onValueChange={(v) => { form.setValue("titre", v as string); setTitreLabel(v as string); }}>
              <SelectTrigger className={FIELD_H}><span>{titreLabel ?? "Sélectionner"}</span></SelectTrigger>
              <SelectContent>
                <SelectItem value="Mr">Mr</SelectItem>
                <SelectItem value="Mme">Mme</SelectItem>
                <SelectItem value="Mademoiselle">Mademoiselle</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <input type="hidden" {...form.register("categorieTiersCode")} />
          <div className="space-y-1.5 rounded-lg border bg-slate-50 p-3">
            <span className="block text-base font-semibold">Catégorie du dossier</span>
            <p className="text-base text-slate-600">Individu — Ménage (0004), définie automatiquement pour le périmètre CECAM Individuel.</p>
          </div>

          <div className="space-y-1.5"><Label className="font-semibold">Prénom <span className="text-red-500">*</span></Label><Input className={FIELD_H} {...form.register("prenom")} /></div>
          <div className="space-y-1.5"><Label>Deuxième Prénom</Label><Input className={FIELD_H} {...form.register("deuxiemePrenom")} /></div>
          <div className="space-y-1.5"><Label className="font-semibold">Nom <span className="text-red-500">*</span></Label><Input className={FIELD_H} {...form.register("nom")} /></div>

          <div className="space-y-1.5">
            <Label className="font-semibold">Date de naissance <span className="text-red-500">*</span></Label>
            <Controller control={form.control} name="dateNaissance" render={({ field }) => (
              <DateInput className={FIELD_H} value={field.value} onValueChange={field.onChange} />
            )} />
          </div>

          <div className="space-y-1.5"><Label>Ville de naissance</Label><Input className={FIELD_H} {...form.register("villeNaissance")} /></div>
          <div className="space-y-1.5"><Label>Pays de naissance</Label><Input className={FIELD_H} {...form.register("paysNaissance")} /></div>

          <div className="space-y-1.5">
            <Label htmlFor="genre" className="font-semibold">Genre <span className="text-red-500">*</span></Label>
            <Select onValueChange={(v) => { form.setValue("genre", v as "FEMME" | "HOMME", { shouldValidate: true }); setGenreLabel(v === "FEMME" ? "Femme" : "Homme"); }}>
              <SelectTrigger id="genre" className={FIELD_H}><span>{genreLabel ?? "Sélectionner"}</span></SelectTrigger>
              <SelectContent>
                <SelectItem value="FEMME">Femme</SelectItem>
                <SelectItem value="HOMME">Homme</SelectItem>
              </SelectContent>
            </Select>
            {form.formState.errors.genre && <p className="text-sm text-red-500">{form.formState.errors.genre.message}</p>}
          </div>

          <div className="space-y-1.5"><Label className="font-semibold">Nationalité <span className="text-red-500">*</span></Label><Input className={FIELD_H} {...form.register("nationalite")} /></div>
          <div className="space-y-1.5"><Label>État civil</Label><Input className={FIELD_H} {...form.register("etatCivil")} /></div>
          <div className="space-y-1.5"><Label>Code Client CECAM</Label><Input className={FIELD_H} {...form.register("codeClientEtablissement")} /></div>
          {/* Téléphone retiré : c'est une information de la fiche client, pas de la demande */}
        </CardContent>
      </Card>

      <Card>
        <CardHeader><CardTitle>Emploi</CardTitle></CardHeader>
        <CardContent className="grid grid-cols-1 gap-4 md:grid-cols-2">
          <div className="space-y-1.5"><Label>Statut Emploi</Label><Input className={FIELD_H} {...form.register("emploi.statutEmploi")} /></div>
          <div className="space-y-1.5"><Label>Nom de l'Employeur</Label><Input className={FIELD_H} {...form.register("emploi.nomEmployeur")} /></div>
          <div className="space-y-1.5"><Label>Profession</Label><Input className={FIELD_H} {...form.register("emploi.profession")} /></div>
          <div className="space-y-1.5">
            <Label>Date d'embauche</Label>
            <Controller control={form.control} name="emploi.dateEmbauche" render={({ field }) => (
              <DateInput className={FIELD_H} value={field.value} onValueChange={field.onChange} />
            )} />
          </div>
          <div className="space-y-1.5">
            <Label>Total Revenu Annuel</Label>
            <Controller control={form.control} name="emploi.revenuAnnuelTotal" render={({ field }) => (
              <MontantInput className={FIELD_H} value={String(field.value ?? "")} onValueChange={(v) => field.onChange(v ? Number(v) : undefined)} />
            )} />
          </div>
          <div className="space-y-1.5">
            <Label>Devise</Label>
            <Select defaultValue="Ariary malgache" onValueChange={(v) => form.setValue("emploi.devise", v as string)}>
              <SelectTrigger className={FIELD_H}><span>Ariary malgache</span></SelectTrigger>
              <SelectContent><SelectItem value="Ariary malgache">Ariary malgache</SelectItem></SelectContent>
            </Select>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Affaire individuelle <span className="font-normal text-slate-500">(facultatif)</span></CardTitle>
        </CardHeader>
        <CardContent className="grid grid-cols-1 gap-4 md:grid-cols-2">
          <div className="space-y-1.5 md:col-span-2">
            <Label>Nom de l'affaire individuelle</Label>
            <Input className={FIELD_H} {...form.register("affaireIndividuelle.nom")} />
            {form.formState.errors.affaireIndividuelle?.nom && (
              <p className="text-sm text-red-500">{form.formState.errors.affaireIndividuelle.nom.message}</p>
            )}
          </div>
          <div className="space-y-1.5 md:col-span-2">
            <Label>Adresse complète — Entreprise</Label>
            <Input className={FIELD_H} {...form.register("affaireIndividuelle.adresse.adresseComplete")} />
            {form.formState.errors.affaireIndividuelle?.adresse?.adresseComplete && (
              <p className="text-sm text-red-500">{form.formState.errors.affaireIndividuelle.adresse.adresseComplete.message}</p>
            )}
          </div>
          <div className="space-y-1.5"><Label>N° et nom de la rue</Label><Input className={FIELD_H} {...form.register("affaireIndividuelle.adresse.numeroRue")} /></div>
          <div className="space-y-1.5"><Label>Code postal</Label><Input className={FIELD_H} {...form.register("affaireIndividuelle.adresse.codePostal")} /></div>
          <div className="space-y-1.5"><Label>Ville</Label><Input className={FIELD_H} {...form.register("affaireIndividuelle.adresse.ville")} /></div>
          <div className="space-y-1.5"><Label>Commune</Label><Input className={FIELD_H} {...form.register("affaireIndividuelle.adresse.commune")} /></div>
          <div className="space-y-1.5"><Label>Région</Label><Input className={FIELD_H} {...form.register("affaireIndividuelle.adresse.region")} /></div>
          <div className="space-y-1.5"><Label>Pays</Label><Input className={FIELD_H} {...form.register("affaireIndividuelle.adresse.pays")} /></div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader><CardTitle>Adresses</CardTitle></CardHeader>
        <CardContent className="space-y-6">
          {adressesArray.fields.map((field, index) => (
            <div key={field.id} className="space-y-3">
              <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
                <div className="space-y-1.5">
                  <Label className="font-semibold">Type d'adresse <span className="text-red-500">*</span></Label>
                  <Select defaultValue={field.typeAdresse} onValueChange={(v) => form.setValue(`adresses.${index}.typeAdresse`, v as string)}>
                    <SelectTrigger className={FIELD_H}><span>{field.typeAdresse}</span></SelectTrigger>
                    <SelectContent>
                      <SelectItem value="Individu - Adresse principale">Individu - Adresse principale</SelectItem>
                      <SelectItem value="Individu - Adresse secondaire">Individu - Adresse secondaire</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
                <div className="space-y-1.5"><Label className="font-semibold">Adresse complète <span className="text-red-500">*</span></Label><Input className={FIELD_H} {...form.register(`adresses.${index}.adresseComplete` as const)} /></div>
              </div>
              <div className="grid grid-cols-2 gap-4 md:grid-cols-3">
                <div className="space-y-1.5"><Label>N° et nom de la rue</Label><Input className={FIELD_H} {...form.register(`adresses.${index}.numeroRue` as const)} /></div>
                <div className="space-y-1.5"><Label>Code postal</Label><Input className={FIELD_H} {...form.register(`adresses.${index}.codePostal` as const)} /></div>
                <div className="space-y-1.5"><Label>Ville</Label><Input className={FIELD_H} {...form.register(`adresses.${index}.ville` as const)} /></div>
                <div className="space-y-1.5"><Label>Commune</Label><Input className={FIELD_H} {...form.register(`adresses.${index}.commune` as const)} /></div>
                <div className="space-y-1.5"><Label>Région</Label><Input className={FIELD_H} {...form.register(`adresses.${index}.region` as const)} /></div>
                <div className="space-y-1.5"><Label>Pays</Label><Input className={FIELD_H} {...form.register(`adresses.${index}.pays` as const)} /></div>
              </div>
            </div>
          ))}
        </CardContent>
      </Card>

      <Card>
        <CardHeader><CardTitle>Identifiants</CardTitle></CardHeader>
        <CardContent className="space-y-4">
          {identifiantsArray.fields.map((field, index) => (
            <div key={field.id} className="grid grid-cols-1 gap-4 md:grid-cols-2">
              <div className="space-y-1.5"><Label className="font-semibold">Type identifiant <span className="text-red-500">*</span></Label><Input className={FIELD_H} {...form.register(`identifiants.${index}.typeIdentifiant` as const)} /></div>
              <div className="space-y-1.5"><Label className="font-semibold">Numéro <span className="text-red-500">*</span></Label><Input className={FIELD_H} {...form.register(`identifiants.${index}.numero` as const)} /></div>
            </div>
          ))}
        </CardContent>
      </Card>

      <div className="flex justify-end">
        <Button type="submit" disabled={submitting}>{submitting ? "Enregistrement..." : "Continuer vers la demande"}</Button>
      </div>
    </form>
  );
}
