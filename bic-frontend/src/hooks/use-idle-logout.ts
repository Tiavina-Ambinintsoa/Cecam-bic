import { useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/context/AuthContext";

const DELAI_INACTIVITE_MS = 10 * 60 * 1000;

export function useIdleLogout() {
  const { nomUtilisateur, logout } = useAuth();
  const navigate = useNavigate();
  const minuteurRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  useEffect(() => {
    if (!nomUtilisateur) return;

    function reinitialiser() {
      if (minuteurRef.current) clearTimeout(minuteurRef.current);
      minuteurRef.current = setTimeout(() => {
        logout();
        navigate("/login");
      }, DELAI_INACTIVITE_MS);
    }

    const evenements = ["mousedown", "keydown", "scroll", "touchstart"];
    evenements.forEach((e) => window.addEventListener(e, reinitialiser));
    reinitialiser();

    return () => {
      evenements.forEach((e) => window.removeEventListener(e, reinitialiser));
      if (minuteurRef.current) clearTimeout(minuteurRef.current);
    };
  }, [nomUtilisateur, logout, navigate]);
}