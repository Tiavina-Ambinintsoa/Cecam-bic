import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useForm, useFieldArray } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger } from "@/components/ui/select";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { clientSchema, type ClientFormValues } from "./clientSchema";
import { clientApi } from "@/api/clientApi";
import { useDemandeEnCours } from "@/context/DemandeEnCoursContext";

const FIELD_H = "min-h-10 h-auto py-2";

export function ClientForm() {
  const navigate = useNavigate();
  const [submitting, setSubmitting] = useState(false);
  const [categorieLabel, setCategorieLabel] = useState("0215 - I M F");
  const [genreLabel, setGenreLabel] = useState<string>();
  const [titreLabel, setTitreLabel] = useState<string>();
  const { setClientId } = useDemandeEnCours();

  const form = useForm<ClientFormValues>({
    resolver: zodResolver(clientSchema),
    defaultValues: {
      nationalite: "Malgache",
      categorieTiersCode: "0215",
      adresses: [{ typeAdresse: "Individu - Adresse principale", adresseComplete: "" }],
      identifiants: [{ typeIdentifiant: "CIN", numero: "" }],
    },
  });

  const adressesArray = useFieldArray({ control: form.control, name: "adresses" });
  const identifiantsArray = useFieldArray({ control: form.control, name: "identifiants" });

  async function onSubmit(values: ClientFormValues) {
  setSubmitting(true);
  try {
    const { client } = await clientApi.creer(values);
    setClientId(client.id);
    navigate(`/demande/nouvelle/contrat/${client.id}`);
  } finally {
    setSubmitting(false);
  }
}

  async function onSubmit(values: ClientFormValues) {
    setSubmitting(true);
    try {
      const { client } = await clientApi.creer(values);
      navigate(`/demande/nouvelle/contrat/${client.id}`);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form onSubmit={form.handleSubmit(onSubmit)} className="mx-auto max-w-4xl space-y-6 p-6">
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

          <div className="space-y-1.5">
            <Label htmlFor="categorieTiersCode">Catégorie Tiers <span className="text-red-500">*</span></Label>
            <Select
              defaultValue="0215"
              onValueChange={(v) => {
                form.setValue("categorieTiersCode", v as string, { shouldValidate: true });
                const map: Record<string, string> = {
                  "0215": "0215 - I M F", "0004": "0004 - Ménages",
                  "0001": "0001 - Administration publique", "0011": "0011 - Administration centrale", "0019": "0019 - NCA",
                  "0002": "0002 - Institution financière", "0211": "0211 - Banque Centrale", "0212": "0212 - Banques",
                  "0213": "0213 - Établissements financiers", "0214": "0214 - Institutions financières spécialisées",
                  "0022": "0022 - Bureau de change", "0231": "0231 - Assurances",
                  "0023": "0023 - Sociétés financières non établissement de crédit", "0232": "0232 - Autres sociétés financières",
                  "0029": "0029 - Autres institutions financières", "0003": "0003 - Sociétés non financières",
                  "0321": "0321 - Sociétés non financières privées franches", "0322": "0322 - Sociétés non financières privées non franches",
                  "3221": "3221 - Grandes entreprises", "3222": "3222 - PME", "3223": "3223 - TPE", "0329": "0329 - NCA",
                };
                setCategorieLabel(map[v as string] ?? (v as string));
              }}
            >
              <SelectTrigger id="categorieTiersCode" className={FIELD_H}><span className="whitespace-normal text-left">{categorieLabel}</span></SelectTrigger>
              <SelectContent>
                <SelectGroup>
                  <SelectLabel>Ménages / IMF</SelectLabel>
                  <SelectItem value="0004">0004 - Ménages</SelectItem>
                  <SelectItem value="0215">0215 - I M F</SelectItem>
                </SelectGroup>
                <SelectGroup>
                  <SelectLabel>Administrations publiques</SelectLabel>
                  <SelectItem value="0001">0001 - Administration publique</SelectItem>
                  <SelectItem value="0011">0011 - Administration centrale</SelectItem>
                  <SelectItem value="0019">0019 - NCA</SelectItem>
                </SelectGroup>
                <SelectGroup>
                  <SelectLabel>Institutions financières</SelectLabel>
                  <SelectItem value="0002">0002 - Institution financière</SelectItem>
                  <SelectItem value="0211">0211 - Banque Centrale</SelectItem>
                  <SelectItem value="0212">0212 - Banques</SelectItem>
                  <SelectItem value="0213">0213 - Établissements financiers</SelectItem>
                  <SelectItem value="0214">0214 - Institutions financières spécialisées</SelectItem>
                  <SelectItem value="0022">0022 - Bureau de change</SelectItem>
                  <SelectItem value="0231">0231 - Assurances</SelectItem>
                  <SelectItem value="0023">0023 - Sociétés financières non établissement de crédit</SelectItem>
                  <SelectItem value="0232">0232 - Autres sociétés financières</SelectItem>
                  <SelectItem value="0029">0029 - Autres institutions financières</SelectItem>
                </SelectGroup>
                <SelectGroup>
                  <SelectLabel>Sociétés non financières</SelectLabel>
                  <SelectItem value="0003">0003 - Sociétés non financières</SelectItem>
                  <SelectItem value="0321">0321 - Sociétés non financières privées franches</SelectItem>
                  <SelectItem value="0322">0322 - Sociétés non financières privées non franches</SelectItem>
                  <SelectItem value="3221">3221 - Grandes entreprises</SelectItem>
                  <SelectItem value="3222">3222 - PME</SelectItem>
                  <SelectItem value="3223">3223 - TPE</SelectItem>
                  <SelectItem value="0329">0329 - NCA</SelectItem>
                </SelectGroup>
              </SelectContent>
            </Select>
            {form.formState.errors.categorieTiersCode && <p className="text-sm text-red-500">{form.formState.errors.categorieTiersCode.message}</p>}
          </div>

          <div className="space-y-1.5"><Label>Prénom <span className="text-red-500">*</span></Label><Input className={FIELD_H} {...form.register("prenom")} /></div>
          <div className="space-y-1.5"><Label>Deuxième Prénom</Label><Input className={FIELD_H} {...form.register("deuxiemePrenom")} /></div>
          <div className="space-y-1.5"><Label>Nom <span className="text-red-500">*</span></Label><Input className={FIELD_H} {...form.register("nom")} /></div>
          <div className="space-y-1.5"><Label>Date de naissance <span className="text-red-500">*</span></Label><Input className={FIELD_H} type="date" {...form.register("dateNaissance")} /></div>
          <div className="space-y-1.5"><Label>Ville de naissance</Label><Input className={FIELD_H} {...form.register("villeNaissance")} /></div>
          <div className="space-y-1.5"><Label>Pays de naissance</Label><Input className={FIELD_H} {...form.register("paysNaissance")} /></div>

          <div className="space-y-1.5">
            <Label htmlFor="genre">Genre <span className="text-red-500">*</span></Label>
            <Select onValueChange={(v) => { form.setValue("genre", v as "FEMME" | "HOMME", { shouldValidate: true }); setGenreLabel(v === "FEMME" ? "Femme" : "Homme"); }}>
              <SelectTrigger id="genre" className={FIELD_H}><span>{genreLabel ?? "Sélectionner"}</span></SelectTrigger>
              <SelectContent>
                <SelectItem value="FEMME">Femme</SelectItem>
                <SelectItem value="HOMME">Homme</SelectItem>
              </SelectContent>
            </Select>
            {form.formState.errors.genre && <p className="text-sm text-red-500">{form.formState.errors.genre.message}</p>}
          </div>

          <div className="space-y-1.5"><Label>Nationalité <span className="text-red-500">*</span></Label><Input className={FIELD_H} {...form.register("nationalite")} /></div>
          <div className="space-y-1.5"><Label>État civil</Label><Input className={FIELD_H} {...form.register("etatCivil")} /></div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader><CardTitle>Adresses</CardTitle></CardHeader>
        <CardContent className="space-y-6">
          {adressesArray.fields.map((field, index) => (
            <div key={field.id} className="space-y-3">
              <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
                <div className="space-y-1.5">
                  <Label>Type d'adresse <span className="text-red-500">*</span></Label>
                  <Select defaultValue={field.typeAdresse} onValueChange={(v) => form.setValue(`adresses.${index}.typeAdresse`, v as string)}>
                    <SelectTrigger className={FIELD_H}><span>{field.typeAdresse}</span></SelectTrigger>
                    <SelectContent>
                      <SelectItem value="Individu - Adresse principale">Individu - Adresse principale</SelectItem>
                      <SelectItem value="Individu - Adresse secondaire">Individu - Adresse secondaire</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
                <div className="space-y-1.5"><Label>Adresse complète <span className="text-red-500">*</span></Label><Input className={FIELD_H} {...form.register(`adresses.${index}.adresseComplete` as const)} /></div>
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
          {/* Bouton "+ Ajouter une adresse" retiré ici — il ira dans l'écran Modification demande */}
        </CardContent>
      </Card>

      <Card>
        <CardHeader><CardTitle>Identifiants</CardTitle></CardHeader>
        <CardContent className="space-y-4">
          {identifiantsArray.fields.map((field, index) => (
            <div key={field.id} className="grid grid-cols-1 gap-4 md:grid-cols-2">
              <div className="space-y-1.5"><Label>Type identifiant <span className="text-red-500">*</span></Label><Input className={FIELD_H} {...form.register(`identifiants.${index}.typeIdentifiant` as const)} /></div>
              <div className="space-y-1.5"><Label>Numéro <span className="text-red-500">*</span></Label><Input className={FIELD_H} {...form.register(`identifiants.${index}.numero` as const)} /></div>
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