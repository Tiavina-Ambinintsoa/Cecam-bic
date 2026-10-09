import { useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/context/AuthContext";

const DELAI_INACTIVITE_MS = 10 * 60 * 1000;
const CLE_DERNIERE_ACTIVITE = "bic_last_activity";

export function useIdleLogout() {
  const { nomUtilisateur, logout } = useAuth();
  const navigate = useNavigate();
  const minuteurRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const derniereEcritureRef = useRef(0);

  useEffect(() => {
    if (!nomUtilisateur || !localStorage.getItem("bic_token")) return;

    function effacerMinuteur() {
      if (minuteurRef.current) clearTimeout(minuteurRef.current);
      minuteurRef.current = null;
    }

    function programmerExpiration() {
      effacerMinuteur();
      const derniereActivite = Number(localStorage.getItem(CLE_DERNIERE_ACTIVITE)) || Date.now();
      const restant = DELAI_INACTIVITE_MS - (Date.now() - derniereActivite);
      if (restant <= 0) {
        logout();
        navigate("/login", { replace: true });
        return;
      }
      minuteurRef.current = setTimeout(programmerExpiration, restant);
    }

    function enregistrerActivite() {
      const maintenant = Date.now();
      if (maintenant - derniereEcritureRef.current >= 1_000) {
        localStorage.setItem(CLE_DERNIERE_ACTIVITE, String(maintenant));
        derniereEcritureRef.current = maintenant;
      }
      programmerExpiration();
    }

    function surChangementStockage(event: StorageEvent) {
      if (event.key === CLE_DERNIERE_ACTIVITE) programmerExpiration();
      if (event.key === "bic_token" && !event.newValue) {
        logout();
        navigate("/login", { replace: true });
      }
    }

    const evenements: Array<keyof WindowEventMap> = ["pointerdown", "keydown", "scroll", "touchstart"];
    evenements.forEach((nom) => window.addEventListener(nom, enregistrerActivite, { passive: true }));
    window.addEventListener("storage", surChangementStockage);
    document.addEventListener("visibilitychange", programmerExpiration);
    if (!localStorage.getItem(CLE_DERNIERE_ACTIVITE)) {
      localStorage.setItem(CLE_DERNIERE_ACTIVITE, String(Date.now()));
    }
    programmerExpiration();

    return () => {
      evenements.forEach((nom) => window.removeEventListener(nom, enregistrerActivite));
      window.removeEventListener("storage", surChangementStockage);
      document.removeEventListener("visibilitychange", programmerExpiration);
      effacerMinuteur();
    };
  }, [nomUtilisateur, logout, navigate]);
}
