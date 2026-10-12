import type { MotifCloture, PhaseDemande } from "@/types/contrat";

export type StatutDemandeModification = "EN_ATTENTE" | "APPROUVEE" | "REFUSEE";

export interface ModificationContratRequest {
  phaseDemande: PhaseDemande;
  motifCloture?: MotifCloture | null;
  codeContratEtablissement?: string | null;
  codeDemandeEtablissement?: string | null;
  montantEcheanceMensuelle: number | null;
  devise: string;
  justification: string;
}

export interface ModificationContratResponse extends ModificationContratRequest {
  id: number;
  contratId: number;
  codeContratCb: string;
  clientNomComplet: string;
  phaseActuelle: PhaseDemande;
  motifClotureActuel?: MotifCloture | null;
  codeContratEtablissementActuel?: string | null;
  codeDemandeEtablissementActuel?: string | null;
  montantEcheanceActuel?: number | null;
  deviseActuelle?: string | null;
  statut: StatutDemandeModification;
  soumisPar: string;
  dateSoumission: string;
  traitePar?: string | null;
  dateTraitement?: string | null;
  commentaireAdmin?: string | null;
}
