import { z } from "zod";

const adresseSchema = z.object({
  typeAdresse: z.string().min(1, "Le type d'adresse est requis"),
  adresseComplete: z.string().min(1, "L'adresse complète est requise"),
  numeroRue: z.string().optional(),
  codePostal: z.string().optional(),
  ville: z.string().optional(),
  commune: z.string().optional(),
  region: z.string().optional(),
  pays: z.string().optional(),
});

const contactSchema = z.object({
  typeContact: z.string().min(1, "Type de contact requis"),
  valeurContact: z.string().optional(),
});

const identifiantSchema = z.object({
  typeIdentifiant: z.string().min(1, "Type de document requis"),
  numero: z.string().min(1, "Numéro de document requis"),
  dateDelivrance: z.string().optional(),
  dateExpiration: z.string().optional(),
  paysDelivrance: z.string().optional(),
});

const emploiSchema = z.object({
  statutEmploi: z.string().optional(),
  nomEmployeur: z.string().optional(),
  profession: z.string().optional(),
  dateEmbauche: z.string().optional(),
  revenuAnnuelTotal: z.number().optional(),
  devise: z.string().optional(),
});

const affaireIndividuelleAdresseSchema = z.object({
  typeAdresse: z.string().optional(),
  adresseComplete: z.string().optional(),
});

const affaireIndividuelleIdentifiantSchema = z.object({
  typeIdentifiant: z.string().optional(),
  numero: z.string().optional(),
});

const affaireIndividuelleContactSchema = z.object({
  typeContact: z.string().optional(),
  valeurContact: z.string().optional(),
});

const affaireIndividuelleSchema = z.object({
  nom: z.string().optional(),
  adresses: z.array(affaireIndividuelleAdresseSchema).optional(),
  identifiants: z.array(affaireIndividuelleIdentifiantSchema).optional(),
  contacts: z.array(affaireIndividuelleContactSchema).optional(),
});

export const clientSchema = z.object({
  titre: z.string().optional(),
  categorieTiersCode: z.string().min(1, "La catégorie tiers est requise"),
  prenom: z.string().min(1, "Le prénom est requis"),
  deuxiemePrenom: z.string().optional(),
  nom: z.string().min(1, "Le nom est requis"),
  dateNaissance: z.string().min(1, "La date de naissance est requise"),
  villeNaissance: z.string().optional(),
  paysNaissance: z.string().optional(),
  genre: z.enum(["FEMME", "HOMME"], { message: "Le genre est requis" }),
  nationalite: z.string().min(1, "La nationalité est requise"),
  etatCivil: z.string().optional(),
  codeClientEtablissement: z.string().optional(),
  telephone: z.string().optional(),
  contacts: z.array(contactSchema).optional(),
  adresses: z.array(adresseSchema).min(1, "Au moins une adresse est requise"),
  identifiants: z.array(identifiantSchema).min(1, "Au moins un document d'identification est requis"),
  emploi: emploiSchema.optional(),
  ajouterAffaireIndividuelle: z.boolean().optional(),
  affaireIndividuelle: affaireIndividuelleSchema.optional(),
}).superRefine((data, ctx) => {
  if (data.ajouterAffaireIndividuelle && !data.affaireIndividuelle?.nom?.trim()) {
    ctx.addIssue({
      code: "custom",
      path: ["affaireIndividuelle", "nom"],
      message: "Le nom de l'affaire individuelle est requis.",
    });
  }
});

export type ClientFormValues = z.infer<typeof clientSchema>;
export type ClientFormInput = z.input<typeof clientSchema>;
