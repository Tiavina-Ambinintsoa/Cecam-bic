export type OrigineDonnee =
  | "NON_RENSEIGNEE"
  | "RAPPORT_CRIF"
  | "DONNEE_FICTIVE";

export const ORIGINE_DONNEE_LABELS: Record<OrigineDonnee, string> = {
  NON_RENSEIGNEE: "Origine non renseignée",
  RAPPORT_CRIF: "Rapport CRIF",
  DONNEE_FICTIVE: "Donnée fictive de démonstration",
};
