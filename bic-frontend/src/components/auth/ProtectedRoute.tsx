import { Navigate } from "react-router-dom";
import { useAuth } from "@/context/AuthContext";
import type { ReactNode } from "react";

export function ProtectedRoute({ children }: { children: ReactNode }) {
  const { nomUtilisateur } = useAuth();
  if (!nomUtilisateur) return <Navigate to="/login" replace />;
  return <>{children}</>;
}

export function AdminRoute({ children }: { children: ReactNode }) {
  const { nomUtilisateur, role } = useAuth();
  if (!nomUtilisateur) return <Navigate to="/login" replace />;
  if (role !== "ADMIN") return <Navigate to="/" replace />;
  return <>{children}</>;
}