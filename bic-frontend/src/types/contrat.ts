export type ModeRattachement = "NOUVELLE_DEMANDE" | "DEMANDE_EXISTANTE";
export type RoleClient = "TITULAIRE" | "CO_TITULAIRE" | "GARANT";
export type PhaseDemande = "DEMANDE_EN_COURS" | "ACTIF" | "REFUSE" | "FERME" | "ABANDONNE";
export type MotifCloture = "FIN_A_TERME" | "FIN_ANTICIPEE";

export interface DernierContratRequest {
  typeCredit?: string;
  dateOctroi?: string;
  montant?: number;
  datePrevueRemboursement?: string;
  dateRemboursement?: string;
  statut?: string;
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
  typeCredit?: string;
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
  montantRestantDu?: number | null;
  montantImpayes?: number | null;
  nombreJoursRetard?: number | null;
  dateDebutContrat?: string | null;
  dateFinContrat?: string | null;
  devise?: string | null;
  motifCloture?: MotifCloture | null;
  client?: { id?: number; prenom: string; nom: string; codeClientCb?: string };
  scoreValeur?: number | null;
  scoreCategorie?: string | null;
};

export interface EcheanceDetailResponse {
  numeroEcheance: number;
  dateEcheance: string;
  montantDu: number;
  montantPaye?: number | null;
  datePaiement?: string | null;
  statut: string;
}

export interface MoisDetailResponse {
  mois: string;
  nombreEcheances?: number | null;
  nombreEcheancesImpayees?: number | null;
  joursDeRetard?: number | null;
  statut: string;
  detailsSource?: string | null;
}

export interface GarantieDetailResponse {
  nature: string;
  typeGarantie: string;
  codeEtablissementGarantie?: string | null;
  nomGarant?: string | null;
  codeClientCbGarant?: string | null;
  montantCouvert?: number | null;
  dateDebutValidite?: string | null;
  dateFinValidite?: string | null;
}

export interface ClientLieDetailResponse {
  codeClientCb: string;
  nomClient?: string | null;
  roleClient: RoleClient;
}

export interface BienLeasingDetailResponse {
  codeBien?: string | null;
  valeurBien?: number | null;
  etatBien?: string | null;
  marqueBien?: string | null;
  dateAcquisition?: string | null;
  immatriculation?: string | null;
}

export interface HistoriqueContratDetailResponse {
  id: number;
  clientId?: number | null;
  clientNom?: string | null;
  clientPrenom?: string | null;
  clientCin?: string | null;
  clientCodeCb?: string | null;
  codeContratCb?: string | null;
  codeContratEtablissement?: string | null;
  codeEtablissementDeclarant?: string | null;
  codeEtablissementCrypte?: string | null;
  typeContrat: string;
  typeRelationEntreprise?: string | null;
  roleClient: RoleClient;
  phaseDemande: PhaseDemande;
  motifCloture?: MotifCloture | null;
  dateDemande?: string | null;
  dateDebutContrat?: string | null;
  dateFinContrat?: string | null;
  datePremiereEcheance?: string | null;
  dateDerniereModificationSource?: string | null;
  montantFinance?: number | null;
  montantTotalDu?: number | null;
  montantEcheanceMensuelle?: number | null;
  nombreTotalEcheances?: number | null;
  devise: string;
  periodicitePaiement?: string | null;
  modeReglement?: string | null;
  montantProchaineEcheance?: number | null;
  dateProchaineEcheance?: string | null;
  dateDernierReglement?: string | null;
  nombreEcheancesRestantes?: number | null;
  montantRestantDu?: number | null;
  nombreEcheancesImpayees?: number | null;
  montantImpayes?: number | null;
  maxMontantImpayes?: number | null;
  maxNombreEcheancesImpayees?: number | null;
  dateMaxNombreEcheancesImpayees?: string | null;
  nombreJoursRetard?: number | null;
  maxNombreJoursRetard?: number | null;
  dateMaxNombreJoursRetard?: string | null;
  pireStatut?: string | null;
  datePireStatut?: string | null;
  codeRestructuration?: string | null;
  dateCodeRestructuration?: string | null;
  contratOrigine?: string | null;
  nouveauContrat?: string | null;
  note?: string | null;
  origineDonnee?: string | null;
  echeances: EcheanceDetailResponse[];
  historiquePaiementMensuel: MoisDetailResponse[];
  garanties: GarantieDetailResponse[];
  clientsLies: ClientLieDetailResponse[];
  bienLeasing?: BienLeasingDetailResponse | null;
}
