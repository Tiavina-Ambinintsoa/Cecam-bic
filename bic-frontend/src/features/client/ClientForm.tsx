import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useForm, useFieldArray, Controller, useWatch } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { clientSchema, type ClientFormInput, type ClientFormValues } from "./clientSchema";
import { clientApi } from "@/api/clientApi";
import { useDemandeEnCours } from "@/context/DemandeEnCoursContext";
import { ChampsObligatoiresNote } from "@/components/ui/champs-obligatoires-note";
import { DateInput } from "@/components/ui/date-input";
import { MontantInput } from "@/components/ui/montant-input";
import { Plus, Trash2 } from "lucide-react";

const FIELD_H = "min-h-10 h-auto py-2";

const categoriesGroupes = [
  {
    label: "Ménages / IMF",
    options: [
      { code: "0004", libelle: "0004 - Ménages" },
      { code: "0215", libelle: "0215 - I M F" },
    ],
  },
  {
    label: "Administrations publiques",
    options: [
      { code: "0001", libelle: "0001 - Administration publique" },
      { code: "0011", libelle: "0011 - Administration centrale" },
      { code: "0019", libelle: "0019 - NCA" },
    ],
  },
  {
    label: "Institutions financières",
    options: [
      { code: "0002", libelle: "0002 - Institution financière" },
      { code: "0211", libelle: "0211 - Banque Centrale" },
      { code: "0212", libelle: "0212 - Banques" },
      { code: "0213", libelle: "0213 - Établissements financiers" },
      { code: "0214", libelle: "0214 - Institutions financières spécialisées" },
      { code: "0022", libelle: "0022 - Bureau de change" },
      { code: "0231", libelle: "0231 - Assurances" },
      { code: "0023", libelle: "0023 - Sociétés financières non établissement de crédit" },
      { code: "0232", libelle: "0232 - Autres sociétés financières" },
      { code: "0029", libelle: "0029 - Autres institutions financières" },
    ],
  },
  {
    label: "Sociétés non financières",
    options: [
      { code: "0003", libelle: "0003 - Sociétés non financières" },
      { code: "0321", libelle: "0321 - Sociétés non financières privées franches" },
      { code: "0322", libelle: "0322 - Sociétés non financières privées non franches" },
      { code: "3221", libelle: "3221 - Grandes entreprises" },
      { code: "3222", libelle: "3222 - PME" },
      { code: "3223", libelle: "3223 - TPE" },
      { code: "0329", libelle: "0329 - NCA" },
    ],
  },
];

const categoriesLibelles: Record<string, string> = {
  "0004": "0004 - Ménages",
  "0215": "0215 - I M F",
  "0001": "0001 - Administration publique",
  "0011": "0011 - Administration centrale",
  "0019": "0019 - NCA",
  "0002": "0002 - Institution financière",
  "0211": "0211 - Banque Centrale",
  "0212": "0212 - Banques",
  "0213": "0213 - Établissements financiers",
  "0214": "0214 - Institutions financières spécialisées",
  "0022": "0022 - Bureau de change",
  "0231": "0231 - Assurances",
  "0023": "0023 - Sociétés financières non établissement de crédit",
  "0232": "0232 - Autres sociétés financières",
  "0029": "0029 - Autres institutions financières",
  "0003": "0003 - Sociétés non financières",
  "0321": "0321 - Sociétés non financières privées franches",
  "0322": "0322 - Sociétés non financières privées non franches",
  "3221": "3221 - Grandes entreprises",
  "3222": "3222 - PME",
  "3223": "3223 - TPE",
  "0329": "0329 - NCA",
};

const getFieldBorderClass = (hasError?: boolean, hasValue?: boolean) => {
  if (hasError) return "border-red-500 focus-visible:ring-red-500 focus-visible:border-red-500";
  if (hasValue) return "border-blue-500 focus-visible:ring-blue-500 focus-visible:border-blue-500";
  return "";
};

export function ClientForm() {
  const navigate = useNavigate();
  const { setClientId } = useDemandeEnCours();
  const [submitting, setSubmitting] = useState(false);
  const [genreLabel, setGenreLabel] = useState<string>();
  const [titreLabel, setTitreLabel] = useState<string>("Mr");
  const [categorieLabel, setCategorieLabel] = useState<string>("0215 - I M F");
  const [etatCivilLabel, setEtatCivilLabel] = useState<string>();

  const form = useForm<ClientFormInput, unknown, ClientFormValues>({
    resolver: zodResolver(clientSchema),
    defaultValues: {
      titre: "Mr",
      nationalite: "Malgache",
      categorieTiersCode: "0215",
      adresses: [{ typeAdresse: "Individu - Adresse principale", adresseComplete: "" }],
      identifiants: [
        {
          typeIdentifiant: "CIN",
          numero: "",
          dateDelivrance: "",
          dateExpiration: "",
          paysDelivrance: "Madagascar",
        },
      ],
      contacts: [{ typeContact: "Téléphone principal", valeurContact: "" }],
      ajouterAffaireIndividuelle: false,
      affaireIndividuelle: {
        nom: "",
        adresses: [{ typeAdresse: "Entreprise - Adresse Principale", adresseComplete: "" }],
        identifiants: [{ typeIdentifiant: "NIF", numero: "" }],
        contacts: [{ typeContact: "Téléphone professionnel", valeurContact: "" }],
      },
    },
  });

  const adressesArray = useFieldArray({ control: form.control, name: "adresses" });
  const identifiantsArray = useFieldArray({ control: form.control, name: "identifiants" });
  const contactsArray = useFieldArray({ control: form.control, name: "contacts" });

  const affaireAdressesArray = useFieldArray({
    control: form.control,
    name: "affaireIndividuelle.adresses",
  });
  const affaireIdentifiantsArray = useFieldArray({
    control: form.control,
    name: "affaireIndividuelle.identifiants",
  });
  const affaireContactsArray = useFieldArray({
    control: form.control,
    name: "affaireIndividuelle.contacts",
  });

  const ajouterAffaire = useWatch({ control: form.control, name: "ajouterAffaireIndividuelle" });
  const formValues = useWatch({ control: form.control });

  async function onSubmit(values: ClientFormValues) {
    setSubmitting(true);
    try {
      const { ajouterAffaireIndividuelle, contacts, ...restValues } = values;
      const primaryContact = contacts?.find((c) => c.valeurContact?.trim())?.valeurContact;

      const affairePayload =
        ajouterAffaireIndividuelle && values.affaireIndividuelle?.nom?.trim()
          ? {
              nom: values.affaireIndividuelle.nom.trim(),
              adresses: (values.affaireIndividuelle.adresses || [])
                .filter((a) => a.adresseComplete?.trim())
                .map((a) => ({
                  adresseComplete: a.adresseComplete!.trim(),
                })),
            }
          : undefined;

      const clientPayload = {
        ...restValues,
        telephone: primaryContact || restValues.telephone,
        affaireIndividuelle: affairePayload,
        identifiants: values.identifiants.map((i) => ({
          typeIdentifiant: i.typeIdentifiant,
          numero: i.numero,
        })),
        adresses: values.adresses.map((a) => ({
          typeAdresse: a.typeAdresse,
          adresseComplete: a.adresseComplete,
        })),
      };

      const { client } = await clientApi.creer(clientPayload);
      setClientId(client.id);
      navigate(`/demande/nouvelle/contrat/${client.id}`);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form onSubmit={form.handleSubmit(onSubmit)} className="mx-auto max-w-6xl space-y-6 p-6">
      <ChampsObligatoiresNote />

      {/* CARTE 1 : Détails client */}
      <Card>
        <CardHeader><CardTitle>Détails client</CardTitle></CardHeader>
        <CardContent className="space-y-4">
          {/* Ligne 1 : Titre (1 col) et Catégorie Tiers (2 cols) */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="space-y-1.5 md:col-span-1">
              <Label className="font-normal text-slate-600">Titre</Label>
              <Select
                defaultValue="Mr"
                onValueChange={(v) => {
                  form.setValue("titre", v as string);
                  setTitreLabel(v as string);
                }}
              >
                <SelectTrigger className={`${FIELD_H} ${getFieldBorderClass(false, !!formValues.titre)}`}>
                  <span>{titreLabel}</span>
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="Mr">Mr</SelectItem>
                  <SelectItem value="Mme">Mme</SelectItem>
                  <SelectItem value="Mademoiselle">Mademoiselle</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-1.5 md:col-span-2">
              <Label className="font-semibold text-slate-800">
                Catégorie Tiers <span className="text-red-500">*</span>
              </Label>
              <Select
                defaultValue="0215"
                onValueChange={(v) => {
                  form.setValue("categorieTiersCode", v as string, { shouldValidate: true });
                  setCategorieLabel(categoriesLibelles[v as string] ?? (v as string));
                }}
              >
                <SelectTrigger
                  className={`${FIELD_H} ${getFieldBorderClass(
                    !!form.formState.errors.categorieTiersCode,
                    !!formValues.categorieTiersCode
                  )}`}
                >
                  <span>{categorieLabel}</span>
                </SelectTrigger>
                <SelectContent>
                  {categoriesGroupes.map((groupe) => (
                    <SelectGroup key={groupe.label}>
                      <SelectLabel>{groupe.label}</SelectLabel>
                      {groupe.options.map((option) => (
                        <SelectItem key={option.code} value={option.code}>
                          {option.libelle}
                        </SelectItem>
                      ))}
                    </SelectGroup>
                  ))}
                </SelectContent>
              </Select>
              {form.formState.errors.categorieTiersCode && (
                <p className="text-sm text-red-500">{form.formState.errors.categorieTiersCode.message}</p>
              )}
            </div>
          </div>

          {/* Ligne 2 : Prénom *, Deuxième Prénom, Nom * (3 champs) */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="space-y-1.5">
              <Label className="font-semibold text-slate-800">
                Prénom <span className="text-red-500">*</span>
              </Label>
              <Input
                className={`${FIELD_H} ${getFieldBorderClass(
                  !!form.formState.errors.prenom,
                  !!formValues.prenom
                )}`}
                {...form.register("prenom")}
              />
              {form.formState.errors.prenom && (
                <p className="text-sm text-red-500">{form.formState.errors.prenom.message}</p>
              )}
            </div>

            <div className="space-y-1.5">
              <Label className="font-normal text-slate-600">Deuxième Prénom</Label>
              <Input
                className={`${FIELD_H} ${getFieldBorderClass(
                  !!form.formState.errors.deuxiemePrenom,
                  !!formValues.deuxiemePrenom
                )}`}
                {...form.register("deuxiemePrenom")}
              />
            </div>

            <div className="space-y-1.5">
              <Label className="font-semibold text-slate-800">
                Nom <span className="text-red-500">*</span>
              </Label>
              <Input
                className={`${FIELD_H} ${getFieldBorderClass(
                  !!form.formState.errors.nom,
                  !!formValues.nom
                )}`}
                {...form.register("nom")}
              />
              {form.formState.errors.nom && (
                <p className="text-sm text-red-500">{form.formState.errors.nom.message}</p>
              )}
            </div>
          </div>

          {/* Ligne 3 : Date de naissance *, Ville de naissance, Pays de naissance (3 champs) */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="space-y-1.5">
              <Label className="font-semibold text-slate-800">
                Date de naissance <span className="text-red-500">*</span>
              </Label>
              <Controller
                control={form.control}
                name="dateNaissance"
                render={({ field }) => (
                  <DateInput
                    className={`${FIELD_H} ${getFieldBorderClass(
                      !!form.formState.errors.dateNaissance,
                      !!field.value
                    )}`}
                    value={field.value}
                    onValueChange={field.onChange}
                  />
                )}
              />
              {form.formState.errors.dateNaissance && (
                <p className="text-sm text-red-500">{form.formState.errors.dateNaissance.message}</p>
              )}
            </div>

            <div className="space-y-1.5">
              <Label className="font-normal text-slate-600">Ville de naissance</Label>
              <Input
                className={`${FIELD_H} ${getFieldBorderClass(
                  !!form.formState.errors.villeNaissance,
                  !!formValues.villeNaissance
                )}`}
                {...form.register("villeNaissance")}
              />
            </div>

            <div className="space-y-1.5">
              <Label className="font-normal text-slate-600">Pays de naissance</Label>
              <Input
                className={`${FIELD_H} ${getFieldBorderClass(
                  !!form.formState.errors.paysNaissance,
                  !!formValues.paysNaissance
                )}`}
                {...form.register("paysNaissance")}
              />
            </div>
          </div>

          {/* Ligne 4 : Genre *, Nationalité *, État civil (3 champs) */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="space-y-1.5">
              <Label htmlFor="genre" className="font-semibold text-slate-800">
                Genre <span className="text-red-500">*</span>
              </Label>
              <Select
                onValueChange={(v) => {
                  form.setValue("genre", v as "FEMME" | "HOMME", { shouldValidate: true });
                  setGenreLabel(v === "FEMME" ? "Femme" : "Homme");
                }}
              >
                <SelectTrigger
                  id="genre"
                  className={`${FIELD_H} ${getFieldBorderClass(
                    !!form.formState.errors.genre,
                    !!formValues.genre
                  )}`}
                >
                  <span>{genreLabel ?? "Sélectionner"}</span>
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="FEMME">Femme</SelectItem>
                  <SelectItem value="HOMME">Homme</SelectItem>
                </SelectContent>
              </Select>
              {form.formState.errors.genre && (
                <p className="text-sm text-red-500">{form.formState.errors.genre.message}</p>
              )}
            </div>

            <div className="space-y-1.5">
              <Label className="font-semibold text-slate-800">
                Nationalité <span className="text-red-500">*</span>
              </Label>
              <Input
                className={`${FIELD_H} ${getFieldBorderClass(
                  !!form.formState.errors.nationalite,
                  !!formValues.nationalite
                )}`}
                {...form.register("nationalite")}
              />
              {form.formState.errors.nationalite && (
                <p className="text-sm text-red-500">{form.formState.errors.nationalite.message}</p>
              )}
            </div>

            <div className="space-y-1.5">
              <Label className="font-normal text-slate-600">État civil</Label>
              <Select
                onValueChange={(v) => {
                  form.setValue("etatCivil", v as string);
                  setEtatCivilLabel(v as string);
                }}
              >
                <SelectTrigger
                  className={`${FIELD_H} ${getFieldBorderClass(
                    !!form.formState.errors.etatCivil,
                    !!formValues.etatCivil
                  )}`}
                >
                  <span>{etatCivilLabel ?? "Sélectionner"}</span>
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="Célibataire">Célibataire</SelectItem>
                  <SelectItem value="Marié(e)">Marié(e)</SelectItem>
                  <SelectItem value="Veuf/Veuve">Veuf/Veuve</SelectItem>
                  <SelectItem value="Divorcé(e)">Divorcé(e)</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* CARTE 2 : Documents d'identification */}
      <Card>
        <CardHeader className="flex flex-row items-center justify-between pb-2">
          <CardTitle>Documents d&apos;identification</CardTitle>
          <Button
            type="button"
            variant="outline"
            size="sm"
            onClick={() =>
              identifiantsArray.append({
                typeIdentifiant: "Passeport",
                numero: "",
                dateDelivrance: "",
                dateExpiration: "",
                paysDelivrance: "Madagascar",
              })
            }
          >
            <Plus className="mr-1 h-4 w-4" /> Ajouter un document
          </Button>
        </CardHeader>
        <CardContent className="space-y-6">
          {identifiantsArray.fields.map((field, index) => {
            const currentItem = formValues.identifiants?.[index];
            const itemErrors = form.formState.errors.identifiants?.[index];

            return (
              <div key={field.id} className="relative rounded-lg border border-slate-200 bg-slate-50/50 p-4 space-y-4">
                <div className="flex items-center justify-between">
                  <span className="text-xs font-semibold uppercase tracking-wider text-slate-500">
                    Document #{index + 1}
                  </span>
                  {identifiantsArray.fields.length > 1 && (
                    <Button
                      type="button"
                      variant="ghost"
                      size="sm"
                      className="text-red-500 hover:text-red-700 h-8 px-2"
                      onClick={() => identifiantsArray.remove(index)}
                    >
                      <Trash2 className="h-4 w-4 mr-1" /> Supprimer
                    </Button>
                  )}
                </div>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  {/* 1. Type de document */}
                  <div className="space-y-1.5">
                    <Label className="font-semibold text-slate-800">
                      Type de document <span className="text-red-500">*</span>
                    </Label>
                    <Select
                      defaultValue={field.typeIdentifiant || "CIN"}
                      onValueChange={(v) => form.setValue(`identifiants.${index}.typeIdentifiant`, v as string)}
                    >
                      <SelectTrigger className={`${FIELD_H} ${getFieldBorderClass(!!itemErrors?.typeIdentifiant, !!currentItem?.typeIdentifiant)}`}>
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="CIN">CIN</SelectItem>
                        <SelectItem value="Passeport">Passeport</SelectItem>
                        <SelectItem value="Carte de séjour">Carte de séjour</SelectItem>
                        <SelectItem value="Permis de conduire">Permis de conduire</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>

                  {/* 2. Numéro document * */}
                  <div className="space-y-1.5">
                    <Label className="font-semibold text-slate-800">
                      Numéro document <span className="text-red-500">*</span>
                    </Label>
                    <Input
                      className={`${FIELD_H} ${getFieldBorderClass(!!itemErrors?.numero, !!currentItem?.numero)}`}
                      {...form.register(`identifiants.${index}.numero` as const)}
                      placeholder="Ex: 101234567890"
                    />
                    {itemErrors?.numero && (
                      <p className="text-sm text-red-500">{itemErrors.numero.message}</p>
                    )}
                  </div>

                  {/* 3. Pays de délivrance */}
                  <div className="space-y-1.5">
                    <Label className="font-normal text-slate-600">Pays de délivrance</Label>
                    <Input
                      className={`${FIELD_H} ${getFieldBorderClass(false, !!currentItem?.paysDelivrance)}`}
                      {...form.register(`identifiants.${index}.paysDelivrance` as const)}
                      placeholder="Ex: Madagascar"
                    />
                  </div>

                  {/* 4. Date de délivrance */}
                  <div className="space-y-1.5">
                    <Label className="font-normal text-slate-600">Date de délivrance</Label>
                    <Controller
                      control={form.control}
                      name={`identifiants.${index}.dateDelivrance` as const}
                      render={({ field: dateField }) => (
                        <DateInput
                          className={`${FIELD_H} ${getFieldBorderClass(false, !!dateField.value)}`}
                          value={dateField.value}
                          onValueChange={dateField.onChange}
                        />
                      )}
                    />
                  </div>

                  {/* 5. Date d'expiration */}
                  <div className="space-y-1.5">
                    <Label className="font-normal text-slate-600">Date d&apos;expiration</Label>
                    <Controller
                      control={form.control}
                      name={`identifiants.${index}.dateExpiration` as const}
                      render={({ field: dateField }) => (
                        <DateInput
                          className={`${FIELD_H} ${getFieldBorderClass(false, !!dateField.value)}`}
                          value={dateField.value}
                          onValueChange={dateField.onChange}
                        />
                      )}
                    />
                  </div>
                </div>
              </div>
            );
          })}
        </CardContent>
      </Card>

      {/* CARTE 3 : Adresses */}
      <Card>
        <CardHeader className="flex flex-row items-center justify-between pb-2">
          <div>
            <CardTitle>Adresses</CardTitle>
            <p className="text-xs text-slate-500 italic mt-1">
              (Veuillez remplir l’adresse complète en utilisant N° et Nom de la Rue, Code Postal, Ville et Pays)
            </p>
          </div>
          <Button
            type="button"
            variant="outline"
            size="sm"
            onClick={() =>
              adressesArray.append({
                typeAdresse: "Individu - Adresse secondaire",
                adresseComplete: "",
              })
            }
          >
            <Plus className="mr-1 h-4 w-4" /> Ajouter une adresse
          </Button>
        </CardHeader>
        <CardContent className="space-y-4">
          {adressesArray.fields.map((field, index) => {
            const currentItem = formValues.adresses?.[index];
            const itemErrors = form.formState.errors.adresses?.[index];

            return (
              <div key={field.id} className="relative rounded-lg border border-slate-200 bg-slate-50/50 p-4 space-y-3">
                <div className="flex items-center justify-between">
                  <span className="text-xs font-semibold uppercase tracking-wider text-slate-500">
                    Adresse #{index + 1}
                  </span>
                  {adressesArray.fields.length > 1 && (
                    <Button
                      type="button"
                      variant="ghost"
                      size="sm"
                      className="text-red-500 hover:text-red-700 h-8 px-2"
                      onClick={() => adressesArray.remove(index)}
                    >
                      <Trash2 className="h-4 w-4 mr-1" /> Supprimer
                    </Button>
                  )}
                </div>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  {/* Champ 1 : Type d'adresse */}
                  <div className="space-y-1.5 md:col-span-1">
                    <Label className="font-semibold text-slate-800">
                      Type d&apos;adresse <span className="text-red-500">*</span>
                    </Label>
                    <Select
                      defaultValue={field.typeAdresse || "Individu - Adresse principale"}
                      onValueChange={(v) => form.setValue(`adresses.${index}.typeAdresse`, v as string)}
                    >
                      <SelectTrigger className={`${FIELD_H} ${getFieldBorderClass(!!itemErrors?.typeAdresse, !!currentItem?.typeAdresse)}`}>
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="Individu - Adresse principale">Individu - Adresse principale</SelectItem>
                        <SelectItem value="Individu - Adresse secondaire">Individu - Adresse secondaire</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>

                  {/* Champ 2 : Adresse complète */}
                  <div className="space-y-1.5 md:col-span-2">
                    <Label className="font-semibold text-slate-800">
                      Adresse complète <span className="text-red-500">*</span>
                    </Label>
                    <Input
                      className={`${FIELD_H} ${getFieldBorderClass(!!itemErrors?.adresseComplete, !!currentItem?.adresseComplete)}`}
                      {...form.register(`adresses.${index}.adresseComplete` as const)}
                      placeholder="Ex: Lot II M 45 Tsianolondroa, 301 Fianarantsoa, Madagascar"
                    />
                    {itemErrors?.adresseComplete && (
                      <p className="text-sm text-red-500">{itemErrors.adresseComplete.message}</p>
                    )}
                  </div>
                </div>
              </div>
            );
          })}
        </CardContent>
      </Card>

      {/* CARTE 4 : Contacts */}
      <Card>
        <CardHeader className="flex flex-row items-center justify-between pb-2">
          <CardTitle>Contacts</CardTitle>
          <Button
            type="button"
            variant="outline"
            size="sm"
            onClick={() =>
              contactsArray.append({
                typeContact: "Téléphone portable",
                valeurContact: "",
              })
            }
          >
            <Plus className="mr-1 h-4 w-4" /> Ajouter un contact
          </Button>
        </CardHeader>
        <CardContent className="space-y-4">
          {contactsArray.fields.map((field, index) => {
            const currentItem = formValues.contacts?.[index];

            return (
              <div key={field.id} className="relative rounded-lg border border-slate-200 bg-slate-50/50 p-4 space-y-3">
                <div className="flex items-center justify-between">
                  <span className="text-xs font-semibold uppercase tracking-wider text-slate-500">
                    Contact #{index + 1}
                  </span>
                  {contactsArray.fields.length > 1 && (
                    <Button
                      type="button"
                      variant="ghost"
                      size="sm"
                      className="text-red-500 hover:text-red-700 h-8 px-2"
                      onClick={() => contactsArray.remove(index)}
                    >
                      <Trash2 className="h-4 w-4 mr-1" /> Supprimer
                    </Button>
                  )}
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {/* Champ 1 : Type de contact */}
                  <div className="space-y-1.5">
                    <Label className="font-semibold text-slate-800">
                      Type de contact <span className="text-red-500">*</span>
                    </Label>
                    <Select
                      defaultValue={field.typeContact || "Téléphone principal"}
                      onValueChange={(v) => form.setValue(`contacts.${index}.typeContact`, v as string)}
                    >
                      <SelectTrigger className={`${FIELD_H} ${getFieldBorderClass(false, !!currentItem?.typeContact)}`}>
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="Téléphone principal">Téléphone principal</SelectItem>
                        <SelectItem value="Téléphone secondaire">Téléphone secondaire</SelectItem>
                        <SelectItem value="Téléphone portable">Téléphone portable</SelectItem>
                        <SelectItem value="Fax">Fax</SelectItem>
                        <SelectItem value="Email">Email</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>

                  {/* Champ 2 : Valeur contact */}
                  <div className="space-y-1.5">
                    <Label className="font-normal text-slate-600">Valeur contact</Label>
                    <Input
                      className={`${FIELD_H} ${getFieldBorderClass(false, !!currentItem?.valeurContact)}`}
                      {...form.register(`contacts.${index}.valeurContact` as const)}
                      placeholder="Ex: +261 34 12 345 67 ou nom@email.com"
                    />
                  </div>
                </div>
              </div>
            );
          })}
        </CardContent>
      </Card>

      {/* CARTE 5 : Emploi (Placé en dernier) */}
      <Card>
        <CardHeader><CardTitle>Emploi</CardTitle></CardHeader>
        <CardContent className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="space-y-1.5">
              <Label className="font-normal text-slate-600">Statut Emploi</Label>
              <Input
                className={`${FIELD_H} ${getFieldBorderClass(false, !!formValues.emploi?.statutEmploi)}`}
                {...form.register("emploi.statutEmploi")}
                placeholder="Ex: Salarié, Indépendant..."
              />
            </div>
            <div className="space-y-1.5">
              <Label className="font-normal text-slate-600">Nom de l&apos;Employeur</Label>
              <Input
                className={`${FIELD_H} ${getFieldBorderClass(false, !!formValues.emploi?.nomEmployeur)}`}
                {...form.register("emploi.nomEmployeur")}
              />
            </div>
            <div className="space-y-1.5">
              <Label className="font-normal text-slate-600">Profession</Label>
              <Input
                className={`${FIELD_H} ${getFieldBorderClass(false, !!formValues.emploi?.profession)}`}
                {...form.register("emploi.profession")}
              />
            </div>
            <div className="space-y-1.5">
              <Label className="font-normal text-slate-600">Date d&apos;embauche</Label>
              <Controller
                control={form.control}
                name="emploi.dateEmbauche"
                render={({ field }) => (
                  <DateInput
                    className={`${FIELD_H} ${getFieldBorderClass(false, !!field.value)}`}
                    value={field.value}
                    onValueChange={field.onChange}
                  />
                )}
              />
            </div>
            <div className="space-y-1.5">
              <Label className="font-normal text-slate-600">Total Revenu Annuel</Label>
              <Controller
                control={form.control}
                name="emploi.revenuAnnuelTotal"
                render={({ field }) => (
                  <MontantInput
                    className={`${FIELD_H} ${getFieldBorderClass(false, !!field.value)}`}
                    value={String(field.value ?? "")}
                    onValueChange={(v) => field.onChange(v ? Number(v) : undefined)}
                  />
                )}
              />
            </div>
            <div className="space-y-1.5">
              <Label className="font-normal text-slate-600">Devise</Label>
              <Select
                defaultValue="Ariary malgache"
                onValueChange={(v) => form.setValue("emploi.devise", v as string)}
              >
                <SelectTrigger className={`${FIELD_H} ${getFieldBorderClass(false, !!formValues.emploi?.devise)}`}>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="Ariary malgache">Ariary malgache</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>

          {/* Checkbox "Ajouter affaire individuelle" en bas d'Emploi */}
          <div className="border-t pt-4">
            <label className="flex items-center gap-3 cursor-pointer">
              <input
                type="checkbox"
                className="h-4 w-4 rounded border-slate-300 text-blue-600 focus:ring-blue-500"
                {...form.register("ajouterAffaireIndividuelle")}
              />
              <span className="font-medium text-slate-800 text-sm">
                Ajouter une affaire individuelle
              </span>
            </label>
          </div>
        </CardContent>
      </Card>

      {/* CARTE 6 : Affaire individuelle (Si case cochée) */}
      {ajouterAffaire && (
        <Card className="border-blue-200 bg-blue-50/20">
          <CardHeader>
            <CardTitle>Affaire individuelle</CardTitle>
          </CardHeader>
          <CardContent className="space-y-6">
            {/* Nom de l'affaire individuelle * */}
            <div className="space-y-1.5 max-w-xl">
              <Label className="font-semibold text-slate-800">
                Nom de l&apos;affaire individuelle <span className="text-red-500">*</span>
              </Label>
              <Input
                className={`${FIELD_H} ${getFieldBorderClass(
                  !!form.formState.errors.affaireIndividuelle?.nom,
                  !!formValues.affaireIndividuelle?.nom
                )}`}
                {...form.register("affaireIndividuelle.nom")}
                placeholder="Ex: Établissement RAKOTO & Fils"
              />
              {form.formState.errors.affaireIndividuelle?.nom && (
                <p className="text-sm text-red-500">
                  {form.formState.errors.affaireIndividuelle.nom.message}
                </p>
              )}
            </div>

            {/* Sous-section Adresses affaire individuelle */}
            <div className="border-t pt-4 space-y-4">
              <div className="flex items-center justify-between">
                <h4 className="font-semibold text-sm text-slate-700">Adresses de l&apos;affaire individuelle</h4>
                <Button
                  type="button"
                  variant="outline"
                  size="sm"
                  onClick={() =>
                    affaireAdressesArray.append({
                      typeAdresse: "Entreprise - Adresse Principale",
                      adresseComplete: "",
                    })
                  }
                >
                  <Plus className="mr-1 h-4 w-4" /> Ajouter une adresse
                </Button>
              </div>

              {affaireAdressesArray.fields.map((field, idx) => (
                <div key={field.id} className="grid grid-cols-1 md:grid-cols-3 gap-3 items-end p-3 bg-white rounded border">
                  <div className="space-y-1.5">
                    <Label className="font-normal text-xs text-slate-600">Type d&apos;adresse</Label>
                    <Select
                      defaultValue={field.typeAdresse || "Entreprise - Adresse Principale"}
                      onValueChange={(v) => form.setValue(`affaireIndividuelle.adresses.${idx}.typeAdresse`, v as string)}
                    >
                      <SelectTrigger className="h-9">
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="Entreprise - Adresse Principale">Entreprise - Adresse Principale</SelectItem>
                        <SelectItem value="Entreprise - Adresse Secondaire">Entreprise - Adresse Secondaire</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                  <div className="space-y-1.5 md:col-span-2 flex gap-2">
                    <div className="flex-1 space-y-1.5">
                      <Label className="font-normal text-xs text-slate-600">Adresse complète</Label>
                      <Input
                        className="h-9"
                        {...form.register(`affaireIndividuelle.adresses.${idx}.adresseComplete` as const)}
                        placeholder="Ex: Lot IV B 12, Antananarivo"
                      />
                    </div>
                    {affaireAdressesArray.fields.length > 1 && (
                      <Button
                        type="button"
                        variant="ghost"
                        size="sm"
                        className="text-red-500 h-9 px-2 self-end"
                        onClick={() => affaireAdressesArray.remove(idx)}
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    )}
                  </div>
                </div>
              ))}
            </div>

            {/* Sous-section Identifiants affaire individuelle */}
            <div className="border-t pt-4 space-y-4">
              <div className="flex items-center justify-between">
                <h4 className="font-semibold text-sm text-slate-700">Identifiants de l&apos;affaire individuelle</h4>
                <Button
                  type="button"
                  variant="outline"
                  size="sm"
                  onClick={() =>
                    affaireIdentifiantsArray.append({
                      typeIdentifiant: "STAT",
                      numero: "",
                    })
                  }
                >
                  <Plus className="mr-1 h-4 w-4" /> Ajouter un identifiant
                </Button>
              </div>

              {affaireIdentifiantsArray.fields.map((field, idx) => (
                <div key={field.id} className="grid grid-cols-1 md:grid-cols-2 gap-3 items-end p-3 bg-white rounded border">
                  <div className="space-y-1.5">
                    <Label className="font-normal text-xs text-slate-600">Type d&apos;identifiant</Label>
                    <Select
                      defaultValue={field.typeIdentifiant || "NIF"}
                      onValueChange={(v) => form.setValue(`affaireIndividuelle.identifiants.${idx}.typeIdentifiant`, v as string)}
                    >
                      <SelectTrigger className="h-9">
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="NIF">NIF</SelectItem>
                        <SelectItem value="STAT">STAT</SelectItem>
                        <SelectItem value="RCS">RCS</SelectItem>
                        <SelectItem value="Autre">Autre</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                  <div className="space-y-1.5 flex gap-2">
                    <div className="flex-1 space-y-1.5">
                      <Label className="font-normal text-xs text-slate-600">Numéro</Label>
                      <Input
                        className="h-9"
                        {...form.register(`affaireIndividuelle.identifiants.${idx}.numero` as const)}
                        placeholder="Ex: 4001234567"
                      />
                    </div>
                    {affaireIdentifiantsArray.fields.length > 1 && (
                      <Button
                        type="button"
                        variant="ghost"
                        size="sm"
                        className="text-red-500 h-9 px-2 self-end"
                        onClick={() => affaireIdentifiantsArray.remove(idx)}
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    )}
                  </div>
                </div>
              ))}
            </div>

            {/* Sous-section Contacts affaire individuelle */}
            <div className="border-t pt-4 space-y-4">
              <div className="flex items-center justify-between">
                <h4 className="font-semibold text-sm text-slate-700">Contacts de l&apos;affaire individuelle</h4>
                <Button
                  type="button"
                  variant="outline"
                  size="sm"
                  onClick={() =>
                    affaireContactsArray.append({
                      typeContact: "Téléphone professionnel",
                      valeurContact: "",
                    })
                  }
                >
                  <Plus className="mr-1 h-4 w-4" /> Ajouter un contact
                </Button>
              </div>

              {affaireContactsArray.fields.map((field, idx) => (
                <div key={field.id} className="grid grid-cols-1 md:grid-cols-2 gap-3 items-end p-3 bg-white rounded border">
                  <div className="space-y-1.5">
                    <Label className="font-normal text-xs text-slate-600">Type de contact</Label>
                    <Select
                      defaultValue={field.typeContact || "Téléphone professionnel"}
                      onValueChange={(v) => form.setValue(`affaireIndividuelle.contacts.${idx}.typeContact`, v as string)}
                    >
                      <SelectTrigger className="h-9">
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="Téléphone professionnel">Téléphone professionnel</SelectItem>
                        <SelectItem value="Téléphone portable">Téléphone portable</SelectItem>
                        <SelectItem value="Email">Email</SelectItem>
                        <SelectItem value="Fax">Fax</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                  <div className="space-y-1.5 flex gap-2">
                    <div className="flex-1 space-y-1.5">
                      <Label className="font-normal text-xs text-slate-600">Valeur contact</Label>
                      <Input
                        className="h-9"
                        {...form.register(`affaireIndividuelle.contacts.${idx}.valeurContact` as const)}
                        placeholder="Ex: +261 20 22 123 45"
                      />
                    </div>
                    {affaireContactsArray.fields.length > 1 && (
                      <Button
                        type="button"
                        variant="ghost"
                        size="sm"
                        className="text-red-500 h-9 px-2 self-end"
                        onClick={() => affaireContactsArray.remove(idx)}
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      )}

      <div className="flex justify-end pt-4">
        <Button type="submit" disabled={submitting} size="lg">
          {submitting ? "Enregistrement..." : "Continuer vers la demande"}
        </Button>
      </div>
    </form>
  );
}
