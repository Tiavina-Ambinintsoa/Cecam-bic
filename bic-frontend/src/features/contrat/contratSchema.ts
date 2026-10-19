import { z } from "zod";

const montantOptionnel = z.preprocess(
  (value) => value === "" || value === null ? undefined : value,
  z.coerce.number().nonnegative().optional(),
);

export const contratSchema = z.object({
  modeRattachement: z.enum(["NOUVELLE_DEMANDE", "DEMANDE_EXISTANTE"]),
  typeContrat: z.string().min(1),
  codeClientCbCible: z.string().optional(),
  roleClient: z.enum(["TITULAIRE", "CO_TITULAIRE", "GARANT"], { message: "Le rôle client est requis" }),
  dateDemande: z.string().min(1),
  montantFinance: z.coerce.number().positive(),
  montantEcheanceMensuelle: z.coerce.number().optional(),
  nombreTotalEcheances: z.coerce.number().int().positive(),
  devise: z.string().min(1),
  periodicitePaiement: z.string().optional(),
  periodiciteBudget: z.string().optional(),
  montantRecette: z.coerce.number({ message: "Le montant des revenus/recettes est requis" }).positive("Le montant des recettes doit être supérieur à 0"),
  montantDepense: z.coerce.number({ message: "Le montant des dépenses est requis" }).nonnegative("Le montant des dépenses doit être positif"),
  typeGarantie: z.string().optional(),
  valeurGarantie: montantOptionnel,
  valeurPatrimoine: montantOptionnel,
  totalDepot: montantOptionnel,
  totalCreditActif: montantOptionnel,
  revenusComplementaires: z.array(z.object({
    typeRevenu: z.string().optional(),
    description: z.string().optional(),
    montantAnnuel: montantOptionnel,
    devise: z.string().optional(),
  })).optional(),
}).refine((v) => v.modeRattachement !== "DEMANDE_EXISTANTE" || !!v.codeClientCbCible, {
  message: "Le code client CB est requis pour ce mode de rattachement",
  path: ["codeClientCbCible"],
});

export type ContratFormValues = z.infer<typeof contratSchema>;
export type ContratFormInput = z.input<typeof contratSchema>;
