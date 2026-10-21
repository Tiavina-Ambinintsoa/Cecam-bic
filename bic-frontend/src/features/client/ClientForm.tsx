import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useForm, useFieldArray, Controller, useWatch, type FieldErrors } from "react-hook-form";
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

// Fonctions de formatage automatique
function capitalizeWords(str: string): string {
  if (!str) return "";
  return str.replace(/\b\p{L}+/gu, (w) => w.charAt(0).toUpperCase() + w.slice(1).toLowerCase());
}

function formatTelephone(val: string): string {
  const digits = val.replace(/\D/g, "");
  if (digits.length <= 3) return digits;
  if (digits.length <= 5) return `${digits.slice(0, 3)} ${digits.slice(3)}`;
  if (digits.length <= 8) return `${digits.slice(0, 3)} ${digits.slice(3, 5)} ${digits.slice(5)}`;
  return `${digits.slice(0, 3)} ${digits.slice(3, 5)} ${digits.slice(5, 8)} ${digits.slice(8, 10)}`;
}

function formatCIN(val: string): string {
  const digits = val.replace(/\D/g, "");
  const parts: string[] = [];
  for (let i = 0; i < digits.length && i < 12; i += 3) {
    parts.push(digits.slice(i, i + 3));
  }
  return parts.join(" ");
}

// Hauteur stricte et monotone pour un alignement parfait de tous les champs
const FIELD_H = "h-10 py-2";

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
  const [titreLabel, setTitreLabel] = useState<string>("M.");
  const [categorieLabel, setCategorieLabel] = useState<string>("0215 - I M F");
  const [etatCivilLabel, setEtatCivilLabel] = useState<string>();

  // Démarrer tout en haut au chargement de la page
  useEffect(() => {
    window.scrollTo({ top: 0, left: 0, behavior: "instant" });
  }, []);

  const form = useForm<ClientFormInput, unknown, ClientFormValues>({
    resolver: zodResolver(clientSchema),
    defaultValues: {
      titre: "M.",
      nationalite: "Malgache",
      dateAdhesion: "",
      categorieTiersCode: "0215",
      adresses: [{ typeAdresse: "Individu - Adresse principale", adresseComplete: "" }],
      identifiants: [{ typeIdentifiant: "CIN", numero: "" }],
      documentsIdentification: [],
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
  const documentsArray = useFieldArray({ control: form.control, name: "documentsIdentification" });
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

  const [erreurSoumission, setErreurSoumission] = useState<string | null>(null);

  async function onSubmit(values: ClientFormValues) {
    setSubmitting(true);
    setErreurSoumission(null);
    try {
      const { ajouterAffaireIndividuelle, contacts, documentsIdentification, ...restValues } = values;
      const primaryContact = contacts?.find((c) => c.valeurContact?.trim())?.valeurContact;

      // Rassembler identifiants obligatoires (CIN) + documents d'identification non obligatoires si renseignés
      const tousIdentifiants = [
        ...values.identifiants.map((i) => ({
          typeIdentifiant: i.typeIdentifiant,
          numero: i.numero,
        })),
        ...(documentsIdentification || [])
          .filter((doc) => doc.numero?.trim())
          .map((doc) => ({
            typeIdentifiant: doc.typeDocument || "Passeport",
            numero: doc.numero!.trim(),
          })),
      ];

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
        nom: values.nom.trim().toUpperCase(),
        prenom: values.prenom.trim(),
        dateAdhesion: values.dateAdhesion?.trim() || undefined,
        telephone: primaryContact || restValues.telephone,
        affaireIndividuelle: affairePayload,
        identifiants: tousIdentifiants,
        adresses: values.adresses.map((a) => ({
          typeAdresse: a.typeAdresse,
          adresseComplete: a.adresseComplete,
        })),
      };

      const { client } = await clientApi.creer(clientPayload);
      setClientId(client.id);
      navigate(`/demande/nouvelle/contrat/${client.id}`);
    } catch {
      setErreurSoumission("Erreur lors de l'enregistrement du client. Vérifiez les données et réessayez.");
    } finally {
      setSubmitting(false);
    }
  }

  // Scroll automatique vers le premier champ non rempli ou erroné avec mise en avant visuelle
  function onFormError(errors: FieldErrors<ClientFormValues>) {
    const errorKeys = Object.keys(errors);
    if (!errorKeys.length) return;
    const firstKey = errorKeys[0];

    setTimeout(() => {
      const el = document.querySelector(`[name="${firstKey}"]`) ||
                 document.querySelector(`[name^="${firstKey}."]`) ||
                 document.querySelector(`#${firstKey}`) ||
                 document.querySelector(".border-red-500");

      if (el) {
        el.scrollIntoView({ behavior: "smooth", block: "center" });
        (el as HTMLElement).focus?.();
        el.classList.add("ring-4", "ring-red-400", "animate-pulse");
        setTimeout(() => {
          el.classList.remove("ring-4", "ring-red-400", "animate-pulse");
        }, 2200);
      }
    }, 50);
  }

  return (
    <form onSubmit={form.handleSubmit(onSubmit, onFormError)} className="mx-auto max-w-6xl w-full min-w-0 space-y-6 p-4 sm:p-6 overflow-hidden">
      {erreurSoumission && <div className="rounded-md border border-red-200 bg-red-50 p-3 text-sm text-red-700">{erreurSoumission}</div>}
      <ChampsObligatoiresNote />

      {/* 1. DÉTAILS CLIENT */}
      <Card className="w-full min-w-0">
        <CardHeader><CardTitle>Détails client</CardTitle></CardHeader>
        <CardContent className="space-y-4">
          {/* Ligne 1 : Titre (1 col, "M." par défaut) et Catégorie Tiers (2 cols) */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="space-y-1.5 md:col-span-1 min-w-0">
              <Label className="font-normal text-slate-600">Titre</Label>
              <Select
                defaultValue="M."
                onValueChange={(v) => {
                  form.setValue("titre", v as string);
                  setTitreLabel(v as string);
                }}
              >
                <SelectTrigger className={`${FIELD_H} ${getFieldBorderClass(false, !!formValues.titre)}`}>
                  <span>{titreLabel}</span>
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="M.">M.</SelectItem>
                  <SelectItem value="Mme">Mme</SelectItem>
                  <SelectItem value="Mademoiselle">Mademoiselle</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-1.5 md:col-span-2 min-w-0">
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
                  <span className="truncate">{categorieLabel}</span>
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

          {/* Ligne 2 : Nom * (en majuscules), Prénom(s) * */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="space-y-1.5 min-w-0">
              <Label className="font-semibold text-slate-800">
                Nom <span className="text-red-500">*</span>
              </Label>
              <Controller
                control={form.control}
                name="nom"
                render={({ field }) => (
                  <Input
                    {...field}
                    value={field.value || ""}
                    onChange={(e) => field.onChange(e.target.value.toUpperCase())}
                    style={{ textTransform: "uppercase" }}
                    placeholder="NOM DU CLIENT"
                    className={`${FIELD_H} font-medium uppercase ${getFieldBorderClass(
                      !!form.formState.errors.nom,
                      !!field.value
                    )}`}
                  />
                )}
              />
              {form.formState.errors.nom && (
                <p className="text-sm text-red-500">{form.formState.errors.nom.message}</p>
              )}
            </div>

            <div className="space-y-1.5 min-w-0">
              <Label className="font-semibold text-slate-800">
                Prénom(s) <span className="text-red-500">*</span>
              </Label>
              <Controller
                control={form.control}
                name="prenom"
                render={({ field }) => (
                  <Input
                    className={`${FIELD_H} ${getFieldBorderClass(
                      !!form.formState.errors.prenom,
                      !!field.value
                    )}`}
                    placeholder="Prénom(s)"
                    value={field.value || ""}
                    onChange={(e) => field.onChange(capitalizeWords(e.target.value))}
                  />
                )}
              />
              {form.formState.errors.prenom && (
                <p className="text-sm text-red-500">{form.formState.errors.prenom.message}</p>
              )}
            </div>
          </div>

          {/* Ligne 3 : Date de naissance *, Ville de naissance, Pays de naissance (3 champs) */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="space-y-1.5 min-w-0">
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

            <div className="space-y-1.5 min-w-0">
              <Label className="font-normal text-slate-600">Ville de naissance</Label>
              <Input
                className={`${FIELD_H} ${getFieldBorderClass(
                  !!form.formState.errors.villeNaissance,
                  !!formValues.villeNaissance
                )}`}
                {...form.register("villeNaissance")}
              />
            </div>

            <div className="space-y-1.5 min-w-0">
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
            <div className="space-y-1.5 min-w-0">
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

            <div className="space-y-1.5 min-w-0">
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

            <div className="space-y-1.5 min-w-0">
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

          {/* Ligne 5 : Membre depuis le et Numéro membre CECAM */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="space-y-1.5 min-w-0">
              <Label className="font-normal text-slate-700">
                Membre depuis le
              </Label>
              <Controller
                control={form.control}
                name="dateAdhesion"
                render={({ field }) => (
                  <DateInput
                    className={`${FIELD_H} ${getFieldBorderClass(
                      !!form.formState.errors.dateAdhesion,
                      !!field.value
                    )}`}
                    value={field.value}
                    onValueChange={field.onChange}
                  />
                )}
              />
              {form.formState.errors.dateAdhesion && (
                <p className="text-sm text-red-500">{form.formState.errors.dateAdhesion.message}</p>
              )}
              <p className="text-xs text-slate-400">Date d'adhésion du sociétaire à la CECAM.</p>
            </div>

            <div className="space-y-1.5 min-w-0">
              <Label className="font-semibold text-slate-800">
                Numéro membre CECAM
              </Label>
              <Input
                className={`${FIELD_H} ${getFieldBorderClass(
                  !!form.formState.errors.codeClientEtablissement,
                  !!formValues.codeClientEtablissement
                )}`}
                placeholder="Ex. 3752"
                {...form.register("codeClientEtablissement")}
              />
              <p className="text-xs text-slate-400">Identifiant sociétaire / livret CECAM.</p>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* 2. ADRESSES EN 2E */}
      <Card className="w-full min-w-0">
        <CardHeader className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pb-3">
          <div>
            <CardTitle>Adresses</CardTitle>
            <p className="text-xs text-slate-500 italic mt-1">
              (Veuillez remplir l’adresse complète en utilisant N° et Nom de la Rue, Code Postal, Ville et Pays)
            </p>
          </div>
          <Button
            type="button"
            size="sm"
            className="bg-black text-white hover:bg-black/90 font-medium w-full sm:w-auto shrink-0"
            onClick={() =>
              adressesArray.append({
                typeAdresse: "Individu - Adresse secondaire",
                adresseComplete: "",
              })
            }
          >
            <Plus className="mr-1.5 h-4 w-4" /> Ajouter une adresse
          </Button>
        </CardHeader>
        <CardContent className="space-y-4">
          {adressesArray.fields.map((field, index) => {
            const currentItem = formValues.adresses?.[index];
            const itemErrors = form.formState.errors.adresses?.[index];

            return (
              <div key={field.id} className="relative rounded-lg border border-slate-200 bg-slate-50/50 p-4 space-y-3 w-full min-w-0">
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
                  <div className="space-y-1.5 md:col-span-1 min-w-0">
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
                  <div className="space-y-1.5 md:col-span-2 min-w-0">
                    <Label className="font-semibold text-slate-800">
                      Adresse complète <span className="text-red-500">*</span>
                    </Label>
                    <Controller
                      control={form.control}
                      name={`adresses.${index}.adresseComplete`}
                      render={({ field: addrField }) => (
                        <Input
                          className={`${FIELD_H} ${getFieldBorderClass(!!itemErrors?.adresseComplete, !!currentItem?.adresseComplete)}`}
                          value={addrField.value || ""}
                          onChange={(e) => addrField.onChange(capitalizeWords(e.target.value))}
                          placeholder="Ex: Lot II M 45 Tsianolondroa, 301 Fianarantsoa, Madagascar"
                        />
                      )}
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

      {/* 3. IDENTIFIANTS EN 3E (Type identifiant CIN, Numéro) - Bouton Ajouter BIEN VISIBLE */}
      <Card className="w-full min-w-0">
        <CardHeader className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pb-3">
          <div>
            <CardTitle>Identifiants</CardTitle>
            <p className="text-xs text-slate-500 mt-1">Identifiant principal officiel (ex: CIN malgache)</p>
          </div>
          <Button
            type="button"
            size="sm"
            className="bg-black text-white hover:bg-black/90 font-medium w-full sm:w-auto shrink-0 shadow-sm"
            onClick={() =>
              identifiantsArray.append({
                typeIdentifiant: "CIN",
                numero: "",
              })
            }
          >
            <Plus className="mr-1.5 h-4 w-4" /> Ajouter un identifiant
          </Button>
        </CardHeader>
        <CardContent className="space-y-4">
          {identifiantsArray.fields.map((field, index) => {
            const currentItem = formValues.identifiants?.[index];
            const itemErrors = form.formState.errors.identifiants?.[index];

            return (
              <div key={field.id} className="relative rounded-lg border border-slate-200 bg-slate-50/50 p-4 space-y-3 w-full min-w-0">
                <div className="flex items-center justify-between">
                  <span className="text-xs font-semibold uppercase tracking-wider text-slate-500">
                    Identifiant #{index + 1}
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

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="space-y-1.5 min-w-0">
                    <Label className="font-semibold text-slate-800">
                      Type identifiant <span className="text-red-500">*</span>
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
                        <SelectItem value="Autre identifiant">Autre identifiant</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>

                  <div className="space-y-1.5 min-w-0">
                    <Label className="font-semibold text-slate-800">
                      Numéro <span className="text-red-500">*</span>
                    </Label>
                    <Controller
                      control={form.control}
                      name={`identifiants.${index}.numero` as const}
                      render={({ field: numField }) => {
                        const isCIN = (currentItem?.typeIdentifiant || "CIN") === "CIN";
                        return (
                          <Input
                            className={`${FIELD_H} ${getFieldBorderClass(!!itemErrors?.numero, !!numField.value)}`}
                            value={numField.value || ""}
                            onChange={(e) => {
                              const val = isCIN ? formatCIN(e.target.value) : e.target.value;
                              numField.onChange(val);
                            }}
                            placeholder={isCIN ? "Ex: 108 111 089 921" : "Ex: 101234567890"}
                          />
                        );
                      }}
                    />
                    {itemErrors?.numero && (
                      <p className="text-sm text-red-500">{itemErrors.numero.message}</p>
                    )}
                  </div>
                </div>
              </div>
            );
          })}

        </CardContent>
      </Card>

      {/* 4. DOCUMENTS D'IDENTIFICATION EN 4E (Non obligatoire) */}
      <Card className="w-full min-w-0">
        <CardHeader className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pb-3">
          <div>
            <CardTitle>Documents d&apos;identification</CardTitle>
            <span className="text-xs font-normal text-slate-500">Optionnel — Passeport, Carte de séjour, Permis de conduire...</span>
          </div>
          <Button
            type="button"
            size="sm"
            className="bg-black text-white hover:bg-black/90 font-medium w-full sm:w-auto shrink-0"
            onClick={() =>
              documentsArray.append({
                typeDocument: "Passeport",
                numero: "",
                dateDelivrance: "",
                dateExpiration: "",
                paysDelivrance: "Madagascar",
              })
            }
          >
            <Plus className="mr-1.5 h-4 w-4" /> Ajouter un document
          </Button>
        </CardHeader>
        <CardContent className="space-y-4">
          {documentsArray.fields.length === 0 ? (
            <p className="text-sm text-slate-400 italic py-2">
              Aucun document d&apos;identification complémentaire ajouté. Cliquez sur &quot;Ajouter un document&quot; si le client possède un passeport, permis ou carte de séjour.
            </p>
          ) : (
            documentsArray.fields.map((field, index) => {
              const currentItem = formValues.documentsIdentification?.[index];

              return (
                <div key={field.id} className="relative rounded-lg border border-slate-200 bg-slate-50/50 p-4 space-y-4 w-full min-w-0">
                  <div className="flex items-center justify-between">
                    <span className="text-xs font-semibold uppercase tracking-wider text-slate-500">
                      Document complémentaire #{index + 1}
                    </span>
                    <Button
                      type="button"
                      variant="ghost"
                      size="sm"
                      className="text-red-500 hover:text-red-700 h-8 px-2"
                      onClick={() => documentsArray.remove(index)}
                    >
                      <Trash2 className="h-4 w-4 mr-1" /> Supprimer
                    </Button>
                  </div>

                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    {/* 1. Type de document */}
                    <div className="space-y-1.5 min-w-0">
                      <Label className="font-normal text-slate-600">Type de document</Label>
                      <Select
                        defaultValue={field.typeDocument || "Passeport"}
                        onValueChange={(v) => form.setValue(`documentsIdentification.${index}.typeDocument`, v as string)}
                      >
                        <SelectTrigger className={`${FIELD_H} ${getFieldBorderClass(false, !!currentItem?.typeDocument)}`}>
                          <SelectValue />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="Passeport">Passeport</SelectItem>
                          <SelectItem value="Carte de séjour">Carte de séjour</SelectItem>
                          <SelectItem value="Permis de conduire">Permis de conduire</SelectItem>
                        </SelectContent>
                      </Select>
                    </div>

                    {/* 2. Numéro document */}
                    <div className="space-y-1.5 min-w-0">
                      <Label className="font-normal text-slate-600">Numéro document</Label>
                      <Input
                        className={`${FIELD_H} ${getFieldBorderClass(false, !!currentItem?.numero)}`}
                        {...form.register(`documentsIdentification.${index}.numero` as const)}
                        placeholder="Ex: A1234567"
                      />
                    </div>

                    {/* 3. Pays de délivrance */}
                    <div className="space-y-1.5 min-w-0">
                      <Label className="font-normal text-slate-600">Pays de délivrance</Label>
                      <Input
                        className={`${FIELD_H} ${getFieldBorderClass(false, !!currentItem?.paysDelivrance)}`}
                        {...form.register(`documentsIdentification.${index}.paysDelivrance` as const)}
                        placeholder="Ex: Madagascar"
                      />
                    </div>

                    {/* 4. Date de délivrance */}
                    <div className="space-y-1.5 min-w-0">
                      <Label className="font-normal text-slate-600">Date de délivrance</Label>
                      <Controller
                        control={form.control}
                        name={`documentsIdentification.${index}.dateDelivrance` as const}
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
                    <div className="space-y-1.5 min-w-0">
                      <Label className="font-normal text-slate-600">Date d&apos;expiration</Label>
                      <Controller
                        control={form.control}
                        name={`documentsIdentification.${index}.dateExpiration` as const}
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
            })
          )}
        </CardContent>
      </Card>

      {/* 5. CONTACTS EN 5E */}
      <Card className="w-full min-w-0">
        <CardHeader className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pb-3">
          <CardTitle>Contacts</CardTitle>
          <Button
            type="button"
            size="sm"
            className="bg-black text-white hover:bg-black/90 font-medium w-full sm:w-auto shrink-0"
            onClick={() =>
              contactsArray.append({
                typeContact: "Téléphone portable",
                valeurContact: "",
              })
            }
          >
            <Plus className="mr-1.5 h-4 w-4" /> Ajouter un contact
          </Button>
        </CardHeader>
        <CardContent className="space-y-4">
          {contactsArray.fields.map((field, index) => {
            const currentItem = formValues.contacts?.[index];

            return (
              <div key={field.id} className="relative rounded-lg border border-slate-200 bg-slate-50/50 p-4 space-y-3 w-full min-w-0">
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
                  <div className="space-y-1.5 min-w-0">
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
                  <div className="space-y-1.5 min-w-0">
                    <Label className="font-normal text-slate-600">Valeur contact</Label>
                    <Controller
                      control={form.control}
                      name={`contacts.${index}.valeurContact` as const}
                      render={({ field: contactField }) => {
                        const typeContact = currentItem?.typeContact || "Téléphone principal";
                        const isTel = !typeContact || typeContact.toLowerCase().includes("téléphone") || typeContact.toLowerCase().includes("tel");
                        return (
                          <Input
                            className={`${FIELD_H} ${getFieldBorderClass(false, !!contactField.value)}`}
                            value={contactField.value || ""}
                            onChange={(e) => {
                              const val = isTel ? formatTelephone(e.target.value) : e.target.value;
                              contactField.onChange(val);
                            }}
                            placeholder={isTel ? "Ex: 034 14 329 43" : "Ex: client@email.com"}
                          />
                        );
                      }}
                    />
                  </div>
                </div>
              </div>
            );
          })}
        </CardContent>
      </Card>

      {/* 6. EMPLOI EN 6E */}
      <Card className="w-full min-w-0">
        <CardHeader><CardTitle>Emploi</CardTitle></CardHeader>
        <CardContent className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="space-y-1.5 min-w-0">
              <Label className="font-normal text-slate-600">Statut Emploi</Label>
              <Input
                className={`${FIELD_H} ${getFieldBorderClass(false, !!formValues.emploi?.statutEmploi)}`}
                {...form.register("emploi.statutEmploi")}
                placeholder="Ex: Salarié, Indépendant..."
              />
            </div>
            <div className="space-y-1.5 min-w-0">
              <Label className="font-normal text-slate-600">Nom de l&apos;Employeur</Label>
              <Input
                className={`${FIELD_H} ${getFieldBorderClass(false, !!formValues.emploi?.nomEmployeur)}`}
                {...form.register("emploi.nomEmployeur")}
              />
            </div>
            <div className="space-y-1.5 min-w-0">
              <Label className="font-normal text-slate-600">Profession</Label>
              <Input
                className={`${FIELD_H} ${getFieldBorderClass(false, !!formValues.emploi?.profession)}`}
                {...form.register("emploi.profession")}
              />
            </div>
            <div className="space-y-1.5 min-w-0">
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
            <div className="space-y-1.5 min-w-0">
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
            <div className="space-y-1.5 min-w-0">
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
                className="h-4 w-4 rounded border-slate-300 text-black focus:ring-black"
                {...form.register("ajouterAffaireIndividuelle")}
              />
              <span className="font-medium text-slate-800 text-sm">
                Ajouter une affaire individuelle
              </span>
            </label>
          </div>
        </CardContent>
      </Card>

      {/* 7. SECTION AFFAIRE INDIVIDUELLE (100% RESPONSIVE SUR TOUS LES ÉCRANS) */}
      {ajouterAffaire && (
        <Card className="border-slate-300 bg-slate-50/40 w-full min-w-0 overflow-hidden shadow-sm">
          <CardHeader>
            <CardTitle>Affaire individuelle</CardTitle>
            <p className="text-xs text-slate-500">
              Tous les champs ci-dessous sont obligatoires pour l&apos;enregistrement de l&apos;affaire individuelle.
            </p>
          </CardHeader>
          <CardContent className="space-y-6">
            {/* Nom de l'affaire individuelle * */}
            <div className="space-y-1.5 w-full max-w-xl min-w-0">
              <Label className="font-semibold text-slate-800">
                Nom de l&apos;affaire individuelle <span className="text-red-500">*</span>
              </Label>
              <Input
                className={`${FIELD_H} w-full min-w-0 ${getFieldBorderClass(
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

            {/* Sous-section : Affaire individuelle / Adresses */}
            <div className="border-t pt-4 space-y-4 w-full min-w-0">
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
                <div>
                  <h4 className="font-semibold text-sm text-slate-800">
                    Affaire individuelle / Adresses <span className="text-red-500">*</span>
                  </h4>
                  {form.formState.errors.affaireIndividuelle?.adresses?.root && (
                    <p className="text-xs text-red-500 mt-0.5">
                      {form.formState.errors.affaireIndividuelle.adresses.root.message}
                    </p>
                  )}
                </div>
                <Button
                  type="button"
                  size="sm"
                  className="bg-black text-white hover:bg-black/90 font-medium w-full sm:w-auto shrink-0"
                  onClick={() =>
                    affaireAdressesArray.append({
                      typeAdresse: "Entreprise - Adresse Principale",
                      adresseComplete: "",
                    })
                  }
                >
                  <Plus className="mr-1.5 h-4 w-4" /> Ajouter une adresse
                </Button>
              </div>

              {affaireAdressesArray.fields.map((field, idx) => {
                const itemErrors = form.formState.errors.affaireIndividuelle?.adresses?.[idx];
                const currentVal = formValues.affaireIndividuelle?.adresses?.[idx];

                return (
                  <div key={field.id} className="p-4 bg-white rounded-lg border border-slate-200 space-y-3 w-full min-w-0">
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
                      <div className="space-y-1.5 md:col-span-1 min-w-0">
                        <Label className="font-semibold text-xs text-slate-800">
                          Type d&apos;adresse <span className="text-red-500">*</span>
                        </Label>
                        <Select
                          defaultValue={field.typeAdresse || "Entreprise - Adresse Principale"}
                          onValueChange={(v) => form.setValue(`affaireIndividuelle.adresses.${idx}.typeAdresse`, v as string, { shouldValidate: true })}
                        >
                          <SelectTrigger className={`${FIELD_H} ${getFieldBorderClass(!!itemErrors?.typeAdresse, !!currentVal?.typeAdresse)}`}>
                            <SelectValue />
                          </SelectTrigger>
                          <SelectContent>
                            <SelectItem value="Entreprise - Adresse Principale">Entreprise - Adresse Principale</SelectItem>
                            <SelectItem value="Entreprise - Adresse Secondaire">Entreprise - Adresse Secondaire</SelectItem>
                          </SelectContent>
                        </Select>
                        {itemErrors?.typeAdresse && (
                          <p className="text-xs text-red-500">{itemErrors.typeAdresse.message}</p>
                        )}
                      </div>

                      <div className="space-y-1.5 md:col-span-2 min-w-0">
                        <Label className="font-semibold text-xs text-slate-800">
                          Adresse complète <span className="text-red-500">*</span>
                        </Label>
                        <div className="flex items-center gap-2 w-full min-w-0">
                          <Controller
                            control={form.control}
                            name={`affaireIndividuelle.adresses.${idx}.adresseComplete` as const}
                            render={({ field: addrField }) => (
                              <Input
                                className={`${FIELD_H} flex-1 min-w-0 ${getFieldBorderClass(!!itemErrors?.adresseComplete, !!addrField.value)}`}
                                value={addrField.value || ""}
                                onChange={(e) => addrField.onChange(capitalizeWords(e.target.value))}
                                placeholder="Ex: Lot IV B 12, Antananarivo"
                              />
                            )}
                          />
                          {affaireAdressesArray.fields.length > 1 && (
                            <Button
                              type="button"
                              variant="ghost"
                              size="sm"
                              className="text-red-500 hover:text-red-700 h-10 w-10 shrink-0 px-2"
                              onClick={() => affaireAdressesArray.remove(idx)}
                            >
                              <Trash2 className="h-4 w-4" />
                            </Button>
                          )}
                        </div>
                        {itemErrors?.adresseComplete && (
                          <p className="text-xs text-red-500">{itemErrors.adresseComplete.message}</p>
                        )}
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>

            {/* Sous-section : Affaire individuelle / Identifiants */}
            <div className="border-t pt-4 space-y-4 w-full min-w-0">
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
                <div>
                  <h4 className="font-semibold text-sm text-slate-800">
                    Affaire individuelle / Identifiants <span className="text-red-500">*</span>
                  </h4>
                  {form.formState.errors.affaireIndividuelle?.identifiants?.root && (
                    <p className="text-xs text-red-500 mt-0.5">
                      {form.formState.errors.affaireIndividuelle.identifiants.root.message}
                    </p>
                  )}
                </div>
                <Button
                  type="button"
                  size="sm"
                  className="bg-black text-white hover:bg-black/90 font-medium w-full sm:w-auto shrink-0"
                  onClick={() =>
                    affaireIdentifiantsArray.append({
                      typeIdentifiant: "STAT",
                      numero: "",
                    })
                  }
                >
                  <Plus className="mr-1.5 h-4 w-4" /> Ajouter un identifiant
                </Button>
              </div>

              {affaireIdentifiantsArray.fields.map((field, idx) => {
                const itemErrors = form.formState.errors.affaireIndividuelle?.identifiants?.[idx];
                const currentVal = formValues.affaireIndividuelle?.identifiants?.[idx];

                return (
                  <div key={field.id} className="p-4 bg-white rounded-lg border border-slate-200 space-y-3 w-full min-w-0">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                      <div className="space-y-1.5 min-w-0">
                        <Label className="font-semibold text-xs text-slate-800">
                          Type d&apos;identifiant <span className="text-red-500">*</span>
                        </Label>
                        <Select
                          defaultValue={field.typeIdentifiant || "NIF"}
                          onValueChange={(v) => form.setValue(`affaireIndividuelle.identifiants.${idx}.typeIdentifiant`, v as string, { shouldValidate: true })}
                        >
                          <SelectTrigger className={`${FIELD_H} ${getFieldBorderClass(!!itemErrors?.typeIdentifiant, !!currentVal?.typeIdentifiant)}`}>
                            <SelectValue />
                          </SelectTrigger>
                          <SelectContent>
                            <SelectItem value="NIF">NIF</SelectItem>
                            <SelectItem value="STAT">STAT</SelectItem>
                            <SelectItem value="RCS">RCS</SelectItem>
                            <SelectItem value="Autre">Autre</SelectItem>
                          </SelectContent>
                        </Select>
                        {itemErrors?.typeIdentifiant && (
                          <p className="text-xs text-red-500">{itemErrors.typeIdentifiant.message}</p>
                        )}
                      </div>

                      <div className="space-y-1.5 min-w-0">
                        <Label className="font-semibold text-xs text-slate-800">
                          Numéro <span className="text-red-500">*</span>
                        </Label>
                        <div className="flex items-center gap-2 w-full min-w-0">
                          <Input
                            className={`${FIELD_H} flex-1 min-w-0 ${getFieldBorderClass(!!itemErrors?.numero, !!currentVal?.numero)}`}
                            {...form.register(`affaireIndividuelle.identifiants.${idx}.numero` as const)}
                            placeholder="Ex: 4001234567"
                          />
                          {affaireIdentifiantsArray.fields.length > 1 && (
                            <Button
                              type="button"
                              variant="ghost"
                              size="sm"
                              className="text-red-500 hover:text-red-700 h-10 w-10 shrink-0 px-2"
                              onClick={() => affaireIdentifiantsArray.remove(idx)}
                            >
                              <Trash2 className="h-4 w-4" />
                            </Button>
                          )}
                        </div>
                        {itemErrors?.numero && (
                          <p className="text-xs text-red-500">{itemErrors.numero.message}</p>
                        )}
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>

            {/* Sous-section : Affaire individuelle / Contacts */}
            <div className="border-t pt-4 space-y-4 w-full min-w-0">
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
                <div>
                  <h4 className="font-semibold text-sm text-slate-800">
                    Affaire individuelle / Contacts <span className="text-red-500">*</span>
                  </h4>
                  {form.formState.errors.affaireIndividuelle?.contacts?.root && (
                    <p className="text-xs text-red-500 mt-0.5">
                      {form.formState.errors.affaireIndividuelle.contacts.root.message}
                    </p>
                  )}
                </div>
                <Button
                  type="button"
                  size="sm"
                  className="bg-black text-white hover:bg-black/90 font-medium w-full sm:w-auto shrink-0"
                  onClick={() =>
                    affaireContactsArray.append({
                      typeContact: "Téléphone professionnel",
                      valeurContact: "",
                    })
                  }
                >
                  <Plus className="mr-1.5 h-4 w-4" /> Ajouter un contact
                </Button>
              </div>

              {affaireContactsArray.fields.map((field, idx) => {
                const itemErrors = form.formState.errors.affaireIndividuelle?.contacts?.[idx];
                const currentVal = formValues.affaireIndividuelle?.contacts?.[idx];

                return (
                  <div key={field.id} className="p-4 bg-white rounded-lg border border-slate-200 space-y-3 w-full min-w-0">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                      <div className="space-y-1.5 min-w-0">
                        <Label className="font-semibold text-xs text-slate-800">
                          Type de contact <span className="text-red-500">*</span>
                        </Label>
                        <Select
                          defaultValue={field.typeContact || "Téléphone professionnel"}
                          onValueChange={(v) => form.setValue(`affaireIndividuelle.contacts.${idx}.typeContact`, v as string, { shouldValidate: true })}
                        >
                          <SelectTrigger className={`${FIELD_H} ${getFieldBorderClass(!!itemErrors?.typeContact, !!currentVal?.typeContact)}`}>
                            <SelectValue />
                          </SelectTrigger>
                          <SelectContent>
                            <SelectItem value="Téléphone professionnel">Téléphone professionnel</SelectItem>
                            <SelectItem value="Téléphone portable">Téléphone portable</SelectItem>
                            <SelectItem value="Email">Email</SelectItem>
                            <SelectItem value="Fax">Fax</SelectItem>
                          </SelectContent>
                        </Select>
                        {itemErrors?.typeContact && (
                          <p className="text-xs text-red-500">{itemErrors.typeContact.message}</p>
                        )}
                      </div>

                      <div className="space-y-1.5 min-w-0">
                        <Label className="font-semibold text-xs text-slate-800">
                          Valeur contact <span className="text-red-500">*</span>
                        </Label>
                        <div className="flex items-center gap-2 w-full min-w-0">
                          <Controller
                            control={form.control}
                            name={`affaireIndividuelle.contacts.${idx}.valeurContact` as const}
                            render={({ field: contactField }) => {
                              const typeContact = currentVal?.typeContact || "Téléphone professionnel";
                              const isTel = !typeContact || typeContact.toLowerCase().includes("téléphone") || typeContact.toLowerCase().includes("tel");
                              return (
                                <Input
                                  className={`${FIELD_H} flex-1 min-w-0 ${getFieldBorderClass(!!itemErrors?.valeurContact, !!contactField.value)}`}
                                  value={contactField.value || ""}
                                  onChange={(e) => {
                                    const val = isTel ? formatTelephone(e.target.value) : e.target.value;
                                    contactField.onChange(val);
                                  }}
                                  placeholder={isTel ? "Ex: 034 14 329 43" : "Ex: contact@entreprise.mg"}
                                />
                              );
                            }}
                          />
                          {affaireContactsArray.fields.length > 1 && (
                            <Button
                              type="button"
                              variant="ghost"
                              size="sm"
                              className="text-red-500 hover:text-red-700 h-10 w-10 shrink-0 px-2"
                              onClick={() => affaireContactsArray.remove(idx)}
                            >
                              <Trash2 className="h-4 w-4" />
                            </Button>
                          )}
                        </div>
                        {itemErrors?.valeurContact && (
                          <p className="text-xs text-red-500">{itemErrors.valeurContact.message}</p>
                        )}
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </CardContent>
        </Card>
      )}

      <div className="flex justify-end pt-4">
        <Button
          type="submit"
          disabled={submitting}
          size="lg"
          className="bg-black text-white hover:bg-black/90 font-medium w-full sm:w-auto"
        >
          {submitting ? "Enregistrement..." : "Continuer vers la demande"}
        </Button>
      </div>
    </form>
  );
}
