export type OrigineDonnee =
  | "NON_RENSEIGNEE"
  | "RAPPORT_CRIF"
  | "DONNEE_FICTIVE";

export const ORIGINE_DONNEE_LABELS: Record<OrigineDonnee, string> = {
  NON_RENSEIGNEE: "Origine non renseignée",
  RAPPORT_CRIF: "Historique externe / Centrale",
  DONNEE_FICTIVE: "Donnée de démonstration",
};
