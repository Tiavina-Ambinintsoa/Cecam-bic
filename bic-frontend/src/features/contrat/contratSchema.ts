import { z } from "zod";

export const contratSchema = z.object({
  modeRattachement: z.enum(["NOUVELLE_DEMANDE", "DEMANDE_EXISTANTE", "ENTREPRISE"]),
  typeContrat: z.string().min(1),
  typeRelationEntreprise: z.string().optional(),
  codeClientCbCible: z.string().optional(),
  roleClient: z.enum(["TITULAIRE", "CO_TITULAIRE", "GARANT"], { message: "Le rôle client est requis" }),
  dateDemande: z.string().min(1),
  montantFinance: z.coerce.number().positive(),
  montantEcheanceMensuelle: z.coerce.number().optional(),
  nombreTotalEcheances: z.coerce.number().int().positive(),
  devise: z.string().min(1),
  periodicitePaiement: z.string().optional(),
  origineDonnee: z.enum(["NON_RENSEIGNEE", "RAPPORT_CRIF", "DONNEE_FICTIVE"])
    .default("NON_RENSEIGNEE"),
}).refine((v) => v.modeRattachement !== "DEMANDE_EXISTANTE" || !!v.codeClientCbCible, {
  message: "Le code client CB est requis pour ce mode de rattachement",
  path: ["codeClientCbCible"],
});

export type ContratFormValues = z.infer<typeof contratSchema>;
export type ContratFormInput = z.input<typeof contratSchema>;
