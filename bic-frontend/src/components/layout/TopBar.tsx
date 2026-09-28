import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { cn } from "@/lib/utils";
import { useAuth } from "@/context/AuthContext";

export function TopBar() {
  const [type, setType] = useState<"individuel" | "entreprise">("individuel");
  const { nomUtilisateur, role, logout } = useAuth();
  const navigate = useNavigate();

  return (
    <header className="border-b border-border bg-card">
      <div className="mx-auto flex h-14 max-w-5xl items-center gap-6 px-6">
        <Link to="/" className="font-heading text-base font-semibold text-primary">
          BIC <span className="font-normal text-muted-foreground">CECAM</span>
        </Link>

        <nav className="flex items-center gap-1" aria-label="Type de dossier">
          <button
            type="button"
            onClick={() => setType("individuel")}
            aria-pressed={type === "individuel"}
            className={cn(
              "rounded-md px-3 py-1.5 text-sm transition-colors",
              type === "individuel"
                ? "bg-secondary font-medium text-secondary-foreground"
                : "text-muted-foreground hover:text-foreground",
            )}
          >
            Individuel
          </button>
          <button
            type="button"
            disabled
            title="Le module entreprise n'est pas encore ouvert"
            className="cursor-not-allowed rounded-md px-3 py-1.5 text-sm text-muted-foreground/50"
          >
            Entreprise
          </button>
        </nav>

        <div className="ml-auto flex items-center gap-4">
          <div className="hidden text-right leading-tight sm:block">
            <div className="text-sm text-foreground">{nomUtilisateur}</div>
            <div className="text-xs text-muted-foreground">
              {role === "ADMIN" ? "Administrateur" : "Agent de crédit"}
            </div>
          </div>
          <button
            type="button"
            onClick={() => { logout(); navigate("/login"); }}
            className="text-sm text-muted-foreground underline-offset-4 hover:text-foreground hover:underline"
          >
            Déconnexion
          </button>
        </div>
      </div>
    </header>
  );
}