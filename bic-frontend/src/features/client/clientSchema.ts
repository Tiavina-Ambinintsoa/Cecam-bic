import { z } from "zod";

const adresseSchema = z.object({
  typeAdresse: z.string().min(1),
  adresseComplete: z.string().min(1, "Adresse requise"),
  numeroRue: z.string().optional(),
  codePostal: z.string().optional(),
  ville: z.string().optional(),
  commune: z.string().optional(),
  region: z.string().optional(),
  pays: z.string().optional(),
});

const emploiSchema = z.object({
  statutEmploi: z.string().optional(),
  nomEmployeur: z.string().optional(),
  profession: z.string().optional(),
  dateEmbauche: z.string().optional(),
  revenuAnnuelTotal: z.number().optional(),
  devise: z.string().optional(),
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
  telephone: z.string().optional(),
  adresses: z.array(adresseSchema).min(1),
  identifiants: z.array(z.object({ typeIdentifiant: z.string().min(1), numero: z.string().min(1, "Numéro requis") })).min(1),
  emploi: emploiSchema.optional(),
});

export type ClientFormValues = z.infer<typeof clientSchema>;