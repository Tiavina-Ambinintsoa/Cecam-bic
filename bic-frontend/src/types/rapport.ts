// bic-frontend/src/types/rapport.ts  — RESYNCHRONISÉ
//
// L'interface avait divergé du backend : contacts, emploi, liens, alertes,
// syntheseParCategorie et detailContrats n'étaient pas déclarés, et
// ClientInfo n'avait ni codeClientCb ni telephone. TypeScript ne voyait
// donc pas la moitié de la réponse.
//
// À terme, générer ce fichier depuis la spec OpenAPI plutôt que de le
// maintenir à la main :
//   npx openapi-typescript http://localhost:8080/v3/api-docs -o src/types/api.ts

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

export interface DetailDemande {
  codeContratCb: string;
  role: string;
  typeRelationEntreprise?: string;
  typeContrat: string;
  phaseDemande: string;
  devise: string;
  periodicitePaiement?: string;
  montantFinance: number;
  montantEcheanceMensuelle?: number;
  nombreTotalEcheances: number;
  dateDemande: string;
}

export interface ScoreDetail {
  pointsPaiement: number;
  pointsEndettement: number;
  pointsExposition: number;
  pointsAnciennete: number;
  pointsNouveauxCredits: number;
  pointsMixite: number;
  malusSurendettement: number;
  secondaireBrut: number;
  plafondSecondaire: number;
  secondaireRetenu: number;
  echeancesEchues: number;
  tauxQualitePaiement: number;
  tauxEndettement?: number | null;
  tauxExposition?: number | null;
}

export interface Score {
  calculable: boolean;
  valeur?: number;
  intervalle?: string;
  categorieRisque?: string;
  couleur?: string;
  couleurHex?: string;
  message?: string;
  detail?: ScoreDetail | null;
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

/** Les montants sont nullables : « - » quand le client n'a aucun contrat. */
export interface Synthese {
  nombreTotalContrat: number;
  nombreEtablissementsDeclarants: number;
  contratManquantReciprocite: string;
  devise: string;
  expositionPotentielle: number | null;
  montantTotalRestantDu: number | null;
  montantTotalImpayes: number | null;
  montantTotalDemandes: number;
  totalGarantieSignature: number;
  repartition: RepartitionLigne[];
}

export interface SyntheseCategorie {
  categorie: string;
  contratsActifs: number;
  libelleMontantPrincipal: string;
  montantPrincipalTitulaire: number;
  montantPrincipalGarant: number;
  montantRestantDuTitulaire: number;
  montantRestantDuGarant: number;
  montantImpayesTitulaire: number;
  montantImpayesGarant: number;
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

/** Grille de statut mensuelle d'un contrat (OK / R / IMP). */
export interface CalendrierCredit {
  codeContratCb: string;
  codeContratEtablissement?: string;
  typeContrat: string;
  phase?: string | null;
  montantFinance: number;
  lignes: LigneAnnee[];
}

/** Grille d'encours mensuel par catégorie. */
export interface EncoursCategorie {
  categorie: string;
  codeEtablissement?: string;
  lignes: LigneAnnee[];
}

export interface HistoriquePaiementLigne {
  annee: number;
  mois: string;
  nombreEcheancesImpayees: number;
  joursDeRetard?: number | null;
  statut: string;
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
  nombreJoursRetard: number;
  maxNombreJoursRetard: number;
  dateMaxNombreJoursRetard?: string | null;
  pireStatut: string;
  datePireStatut?: string | null;
  codeRestructuration?: string;
  contratOrigine?: string;
  nouveauContrat?: string;
  garanties: Garantie[];
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

export interface RapportSolvabilite {
  identifiantRapport: string;
  dateRequete: string;
  statutClient: string;
  codeClientCb: string;
  client: ClientInfo;
  contacts: Contact[];
  adressesActuelles: Adresse[];
  adressesHistoriques: Adresse[];
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
  calendriers: CalendrierCredit[];
  detailContrats: DetailContrat[];
}