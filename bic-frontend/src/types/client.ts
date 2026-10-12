export type Genre = "FEMME" | "HOMME";

export interface AdresseRequest {
  typeAdresse: string;
  adresseComplete: string;
  numeroRue?: string;
  codePostal?: string;
  ville?: string;
  commune?: string;
  region?: string;
  pays?: string;
}

export interface IdentifiantRequest { typeIdentifiant: string; numero: string; }

export interface EmploiRequest {
  statutEmploi?: string;
  nomEmployeur?: string;
  profession?: string;
  dateEmbauche?: string;
  revenuAnnuelTotal?: number;
  devise?: string;
}

export interface AffaireIndividuelleAdresseRequest {
  adresseComplete: string;
  numeroRue?: string;
  codePostal?: string;
  ville?: string;
  commune?: string;
  region?: string;
  pays?: string;
}

export interface AffaireIndividuelleRequest {
  nom: string;
  adresses?: AffaireIndividuelleAdresseRequest[];
}

export interface ClientRequest {
  titre?: string;
  categorieTiersCode: string;
  prenom: string;
  deuxiemePrenom?: string;
  nom: string;
  dateNaissance: string;
  villeNaissance?: string;
  paysNaissance?: string;
  genre: Genre;
  nationalite: string;
  etatCivil?: string;
  codeClientEtablissement?: string;
  telephone?: string;
  adresses: AdresseRequest[];
  identifiants: IdentifiantRequest[];
  emploi?: EmploiRequest;
  affaireIndividuelle?: AffaireIndividuelleRequest;
}

export interface ClientResponse extends ClientRequest {
  id: number;
  codeClientCb: string;
}
