import { AxiosError } from "axios";

interface ProblemDetail {
  title?: string;
  detail?: string;
  code?: string;
  status?: number;
}

export function messageErreur(erreur: unknown, contexte: "connexion" | "general" = "general"): string {
  const ax = erreur as AxiosError<ProblemDetail>;

  if (ax?.code === "ERR_NETWORK" || ax?.message === "Network Error") {
    return "Le serveur est injoignable. Vérifiez votre connexion, ou que l'application est bien démarrée.";
  }
  if (ax?.code === "ECONNABORTED") {
    return "Le serveur met trop de temps à répondre. Réessayez dans un instant.";
  }

  const probleme = ax?.response?.data;
  if (probleme?.detail) return probleme.detail;

  switch (ax?.response?.status) {
    case 400: return "La saisie contient une erreur. Vérifiez les champs signalés.";
    case 401: return contexte === "connexion"
      ? "Nom d'utilisateur ou mot de passe incorrect."
      : "Votre session a expiré. Reconnectez-vous.";
    case 403: return "Vous n'avez pas les droits pour cette action.";
    case 404: return "Cet élément n'existe pas ou a été supprimé.";
    case 409: return "Cette opération entre en conflit avec des données existantes.";
    case 429: return "Trop de tentatives. Patientez quelques minutes.";
    case 500:
    case 502:
    case 503: return "Le serveur a rencontré un problème. Réessayez, puis signalez-le si cela persiste.";
    default:  return "Une erreur inattendue s'est produite.";
  }
}

export function codeErreur(erreur: unknown): string | undefined {
  return (erreur as AxiosError<ProblemDetail>)?.response?.data?.code;
}