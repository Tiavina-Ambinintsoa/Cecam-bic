import type { OrigineDonnee } from "./origineDonnee";

export type ModeRattachement = "NOUVELLE_DEMANDE" | "DEMANDE_EXISTANTE" | "ENTREPRISE";
export type RoleClient = "TITULAIRE" | "CO_TITULAIRE" | "GARANT";

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
  origineDonnee?: OrigineDonnee;
  typeRelationEntreprise?: string;
}

export type ContratResponse = Omit<ContratRequest, "montantEcheanceMensuelle" | "devise"> & {
  id: number;
  codeContratCb: string;
  phaseDemande: string;
  codeContratEtablissement?: string | null;
  codeDemandeEtablissement?: string | null;
  montantEcheanceMensuelle?: number | null;
  devise?: string | null;
};
