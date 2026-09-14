import { useState } from "react";
import { cn } from "@/lib/utils";
import { useAuth } from "@/context/AuthContext";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";

export function TopBar() {
  const [type, setType] = useState<"individuel" | "entreprise">("individuel");
  const { nomUtilisateur, role, logout } = useAuth();
  const navigate = useNavigate();

  return (
    <header className="border-b bg-white">
      <div className="flex items-center gap-3 text-sm text-slate-500">
  <span>{nomUtilisateur} ({role === "ADMIN" ? "Administrateur" : "Agent de crédit"})</span>
  <Button variant="outline" size="sm" onClick={() => { logout(); navigate("/login"); }}>Déconnexion</Button>
</div>
      <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-3">
        <h1 className="text-lg font-semibold text-slate-800">BIC — CECAM</h1>
        <div className="flex rounded-lg border p-1">
          <button
            className={cn("rounded-md px-4 py-1.5 text-sm font-medium", type === "individuel" ? "bg-slate-900 text-white" : "text-slate-600")}
            onClick={() => setType("individuel")}
          >
            BIC Individuel
          </button>
          <button
            className="rounded-md px-4 py-1.5 text-sm font-medium text-slate-400"
            disabled
            title="Module BIC Entreprise (à venir)"
          >
            BIC Entreprise
          </button>
        </div>
      </div>
    </header>
  );
}