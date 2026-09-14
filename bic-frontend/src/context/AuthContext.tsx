import { createContext, useContext, useState, type ReactNode } from "react";
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
  const [nomUtilisateur, setNomUtilisateur] = useState<string | null>(localStorage.getItem("bic_user"));
  const [role, setRole] = useState<Role | null>(localStorage.getItem("bic_role") as Role | null);

  async function login(username: string, password: string) {
    const res = await authApi.login(username, password);
    localStorage.setItem("bic_token", res.token);
    localStorage.setItem("bic_user", res.nomUtilisateur);
    localStorage.setItem("bic_role", res.role);
    setNomUtilisateur(res.nomUtilisateur);
    setRole(res.role);
  }

  function logout() {
    localStorage.removeItem("bic_token");
    localStorage.removeItem("bic_user");
    localStorage.removeItem("bic_role");
    setNomUtilisateur(null);
    setRole(null);
  }

  return <AuthContext.Provider value={{ nomUtilisateur, role, login, logout }}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth doit être utilisé dans AuthProvider");
  return ctx;
}