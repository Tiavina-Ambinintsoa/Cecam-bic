export interface ClientInfo {
  codeClientCb: string;
  titre?: string;
  nomComplet: string;
  prenom: string;
  deuxiemePrenom?: string;
  nom: string;
  dateNaissance: string;
  villeNaissance?: string;
  paysNaissance?: string;
  genre: "FEMME" | "HOMME";
  nationalite: string;
  etatCivil?: string;
  telephone?: string;
  categorieTiersCode: string;
  dateDerniereModification: string;
  dateAdhesion?: string;
}

export interface Contact {
  typeContact: string;
  valeur: string;
}

export interface Adresse {
  typeAdresse: string;
  adresseComplete: string;
  numeroRue?: string;
  codePostal?: string;
  ville?: string;
  commune?: string;
  region?: string;
  pays?: string;
  dateDerniereModification: string;
}

export interface Identifiant {
  typeIdentifiant: string;
  numero: string;
}

export interface Emploi {
  statutEmploi?: string;
  nomEmployeur?: string;
  profession?: string;
  dateEmbauche?: string;
  revenuAnnuelTotal?: number;
  devise?: string;
}

export interface AffaireIndividuelle {
  nom: string;
  dateDerniereModification: string;
  adresses: Adresse[];
}

export interface DetailDemande {
  codeContratCb: string;
  role: string;
  typeRelationEntreprise?: string;
  typeContrat: string;
  typeCredit?: string;
  phaseDemande: string;
  devise: string;
  periodicitePaiement?: string;
  origineDonnee: string;
  montantFinance: number;
  montantEcheanceMensuelle?: number;
  nombreTotalEcheances: number | null;
  dateDemande?: string | null;
  valeurGarantie?: number | null;
  montantTotalDemandeInteret?: number | null;
  ratioCouvertureGarantie?: number | null;
  valeurPatrimoine?: number | null;
  margePatrimoine?: number | null;
  ratioCouverturePatrimoine?: number | null;
  totalDepot?: number | null;
  totalCreditActif?: number | null;
  tauxInteretMensuel?: number | null;
}

export interface Score {
  calculable: boolean;
  valeur?: number;
  intervalle?: string;
  categorieRisque?: string;
  couleur?: string;
  couleurHex?: string;
  message?: string;
  avertissement?: string;
}

export interface GrilleScoreItem {
  intervalle: string;
  categorieRisque: string;
  couleurHex: string;
}

export interface RepartitionLigne {
  categorie: string;
  demande: number;
  refuse: number;
  abandonne: number;
  actif: number;
  ferme: number;
}

export interface Synthese {
  nombreTotalContrat: number;
  nombreEtablissementsDeclarants: number;
  contratManquantReciprocite: string;
  devise: string;
  expositionPotentielle: number | null;
  montantTotalRestantDu: number | null;
  montantTotalImpayes: number | null;
  montantTotalDemandes: number | null;
  totalGarantieSignature: number | null;
  repartition: RepartitionLigne[];
}

export interface SyntheseCategorie {
  categorie: string;
  devise: string | null;
  contratsActifs: number;
  libelleMontantPrincipal: string;
  montantPrincipalTitulaire: number | null;
  montantPrincipalGarant: number | null;
  montantRestantDuTitulaire: number | null;
  montantRestantDuGarant: number | null;
  montantImpayesTitulaire: number | null;
  montantImpayesGarant: number | null;
}

export interface CelluleMois {
  mois: string;
  dansPeriode: boolean;
  montant?: number | null;
  statut?: string | null;
  libelle: string;
  couleurHex: string;
}

export interface LigneAnnee {
  annee: number;
  mois: CelluleMois[];
}

export interface CalendrierCredit {
  codeContratCb: string;
  codeContratEtablissement?: string;
  typeContrat: string;
  phase?: string | null;
  montantFinance: number;
  lignes: LigneAnnee[];
}

export interface EncoursCategorie {
  categorie: string;
  codeEtablissement?: string;
  devise?: string | null;
  lignes: LigneAnnee[];
}

export interface HistoriquePaiementLigne {
  annee: number;
  mois: string;
  nombreEcheances?: number | null;
  nombreEcheancesImpayees: number;
  joursDeRetard?: number | null;
  statut: string;
  detailsSource?: string | null;
  bucketRetard?: string | null;
}

export interface Garantie {
  nature: string;
  typeGarantie: string;
  codeEtablissementGarantie?: string;
  nomGarant?: string;
  codeClientCbGarant?: string;
  montantCouvert?: number;
  dateDebutValidite?: string | null;
  dateFinValidite?: string | null;
}

export interface DetailContrat {
  codeContratCb: string;
  codeContratEtablissement?: string;
  codeEtablissementDeclarant?: string;
  codeEtablissementCrypte?: string;
  typeContrat: string;
  phase: string;
  role: string;
  devise: string;
  dateDemande: string;
  dateDebutContrat?: string | null;
  dateFinContrat?: string | null;
  datePremiereEcheance?: string | null;
  dateDerniereModification?: string | null;
  montantFinance: number;
  montantTotalDu: number;
  montantEcheanceMensuelle?: number | null;
  nombreTotalEcheances: number;
  periodicitePaiement?: string;
  modeReglement?: string;
  montantProchaineEcheance?: number | null;
  dateProchaineEcheance?: string | null;
  dateDernierReglement?: string | null;
  historiquePaiement: HistoriquePaiementLigne[];
  nombreEcheancesRestantes: number;
  montantRestantDu: number;
  nombreEcheancesImpayees: number;
  montantImpayes: number;
  maxMontantImpayes: number;
  maxNombreEcheancesImpayees: number;
  dateMaxNombreEcheancesImpayees?: string | null;
  nombreJoursRetard: number;
  maxNombreJoursRetard: number;
  dateMaxNombreJoursRetard?: string | null;
  dateCodeRestructuration?: string | null;
  note?: string;
  pireStatut: string;
  datePireStatut?: string | null;
  codeRestructuration?: string;
  contratOrigine?: string;
  nouveauContrat?: string;
  bienLeasing?: BienLeasing;
  clientsLies?: ContratClientLie[];
  garanties: Garantie[];
  origineDonnee: string;
}

export interface BienLeasing {
  codeBien?: string;
  valeurBien?: number | null;
  etatBien?: string;
  marqueBien?: string;
  dateAcquisition?: string | null;
  immatriculation?: string;
}

export interface ContratClientLie {
  codeClientCb: string;
  nom?: string;
  typeRelation: string;
}

export interface LienClient {
  codeClientCb: string;
  nom?: string;
  typeRelation: string;
  occurrences?: number;
  etablissement?: string;
  dateDerniereModification?: string | null;
  flagParent: boolean;
}

export interface Alerte {
  clientId: number;
  codeClientCb: string;
  nomComplet: string;
  contratId: number;
  codeContratCb: string;
  echeancesImpayees: number;
  echeancesEnRetard: number;
}

export interface SituationFinanciereMensuelle {
  mois: string; categorie: string; devise: string; encours: number; impayes: number; contratsAvecEcheances: number;
}

export interface DernierContrat {
  dateOctroi?: string;
  montant?: number;
  datePrevueRemboursement?: string;
  dateRemboursement?: string;
  joursRetard?: number;
  statut?: string;
  libelleAvance?: string;
}

export interface RapportSolvabilite {
  identifiantRapport: string;
  dateRequete: string;
  statutClient: string;
  codeClientCb: string;
  client: ClientInfo;
  contacts: Contact[];
  adressesActuelles: Adresse[];
  adressesHistoriques: Adresse[];
  affaireIndividuelle: AffaireIndividuelle | null;
  identifiants: Identifiant[];
  detailDemande: DetailDemande;
  emploi: Emploi | null;
  liens: LienClient[];
  alertes: Alerte[];
  score: Score;
  grille: GrilleScoreItem[];
  synthese: Synthese;
  syntheseParCategorie: SyntheseCategorie[];
  encoursParCategorie: EncoursCategorie[];
  impayesParCategorie?: EncoursCategorie[];
  situationFinanciereMensuelle?: SituationFinanciereMensuelle[];
  calendriers: CalendrierCredit[];
  detailContrats: DetailContrat[];
  derniersContrats?: DernierContrat[];
}
