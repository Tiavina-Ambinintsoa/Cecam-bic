export type ModeRattachement = "NOUVELLE_DEMANDE" | "DEMANDE_EXISTANTE";
export type RoleClient = "TITULAIRE" | "CO_TITULAIRE" | "GARANT";
export type PhaseDemande = "DEMANDE_EN_COURS" | "ACTIF" | "REFUSE" | "FERME" | "ABANDONNE";
export type MotifCloture = "FIN_A_TERME" | "FIN_ANTICIPEE";

export interface DernierContratRequest {
  dateOctroi?: string;
  montant?: number;
  datePrevueRemboursement?: string;
  dateRemboursement?: string;
}

export interface ContratRequest {
  clientId: number;
  modeRattachement: ModeRattachement;
  typeContrat: string;
  roleClient: RoleClient;
  dateDemande: string;
  montantFinance: number;
  montantEcheanceMensuelle?: number;
  nombreTotalEcheances: number;
  devise: string;
  periodicitePaiement?: string;
  periodiciteBudget?: string;
  montantRecette?: number;
  montantDepense?: number;
  typeGarantie?: string;
  valeurGarantie?: number;
  valeurPatrimoine?: number;
  totalDepot?: number;
  totalCreditActif?: number;
  tauxInteretMensuel?: number;
  derniersContrats?: DernierContratRequest[];
  revenusComplementaires?: {
    typeRevenu?: string;
    description?: string;
    montantAnnuel?: number;
    devise?: string;
  }[];
}

export type ContratResponse = Omit<ContratRequest, "montantEcheanceMensuelle" | "devise"> & {
  id: number;
  codeContratCb: string;
  phaseDemande: string;
  codeContratEtablissement?: string | null;
  codeDemandeEtablissement?: string | null;
  montantEcheanceMensuelle?: number | null;
  devise?: string | null;
  motifCloture?: MotifCloture | null;
  client?: { prenom: string; nom: string; codeClientCb?: string };
};
