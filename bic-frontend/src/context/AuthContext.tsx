import { createContext, useCallback, useContext, useEffect, useState, type ReactNode } from "react";
import { authApi } from "@/api/authApi";

type Role = "AGENT_CREDIT" | "ADMIN";

interface AuthState {
  nomUtilisateur: string | null;
  role: Role | null;
  login: (nomUtilisateur: string, motDePasse: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthState | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [nomUtilisateur, setNomUtilisateur] = useState<string | null>(() =>
    localStorage.getItem("bic_token") ? localStorage.getItem("bic_user") : null,
  );
  const [role, setRole] = useState<Role | null>(() =>
    localStorage.getItem("bic_token") ? (localStorage.getItem("bic_role") as Role | null) : null,
  );

  async function login(username: string, password: string) {
    const res = await authApi.login(username, password);
    localStorage.setItem("bic_token", res.token);
    localStorage.setItem("bic_user", res.nomUtilisateur);
    localStorage.setItem("bic_role", res.role);
    localStorage.setItem("bic_last_activity", String(Date.now()));
    setNomUtilisateur(res.nomUtilisateur);
    setRole(res.role);
  }

  const logout = useCallback(() => {
    localStorage.removeItem("bic_token");
    localStorage.removeItem("bic_user");
    localStorage.removeItem("bic_role");
    localStorage.removeItem("bic_last_activity");
    setNomUtilisateur(null);
    setRole(null);
  }, []);

  useEffect(() => {
    function synchroniserSession(event: StorageEvent) {
      if (event.key !== "bic_token" && event.key !== "bic_user" && event.key !== "bic_role") return;
      const token = localStorage.getItem("bic_token");
      setNomUtilisateur(token ? localStorage.getItem("bic_user") : null);
      setRole(token ? (localStorage.getItem("bic_role") as Role | null) : null);
    }
    window.addEventListener("storage", synchroniserSession);
    return () => window.removeEventListener("storage", synchroniserSession);
  }, []);

  return <AuthContext.Provider value={{ nomUtilisateur, role, login, logout }}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth doit être utilisé dans AuthProvider");
  return ctx;
}