import { Link, useNavigate, useLocation } from "react-router-dom";
import { FileText, Contact, Home } from "lucide-react";
import { cn } from "@/lib/utils";
import { useAuth } from "@/context/AuthContext";

export function TopBar() {
  const { nomUtilisateur, role, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const isRapports = location.pathname.startsWith("/rapports");
  const isClients = location.pathname.startsWith("/admin/clients");

  return (
    <header className="border-b border-border bg-card shadow-xs">
      <div className="mx-auto flex h-16 max-w-7xl items-center gap-6 px-6">
        <Link to="/" className="flex flex-col leading-tight font-heading text-base font-bold text-primary tracking-tight">
          <div className="flex items-center gap-2">
            <span className="text-base font-extrabold text-foreground tracking-tight">Scoring CECAM</span>
            <span className="rounded bg-sky-50 text-primary border border-sky-200 px-1.5 py-0.5 text-[9px] font-semibold uppercase tracking-wider">Interne</span>
          </div>
          <span className="text-[11px] font-normal text-muted-foreground hidden sm:inline">Système d'évaluation de solvabilité</span>
        </Link>

        <nav className="flex items-center gap-1" aria-label="Navigation principale">
          <Link
            to="/"
            className={cn(
              "flex items-center gap-1.5 rounded-md px-3 py-2 text-sm transition-colors",
              location.pathname === "/"
                ? "bg-secondary font-medium text-secondary-foreground"
                : "text-muted-foreground hover:text-foreground hover:bg-secondary/60",
            )}
          >
            <Home className="size-4" />
            <span className="hidden md:inline">Accueil</span>
          </Link>
          <Link
            to="/rapports"
            className={cn(
              "flex items-center gap-1.5 rounded-md px-3 py-2 text-sm transition-colors",
              isRapports
                ? "bg-secondary font-medium text-secondary-foreground"
                : "text-muted-foreground hover:text-foreground hover:bg-secondary/60",
            )}
          >
            <FileText className="size-4" />
            <span>Historique des rapports</span>
          </Link>
          {role === "ADMIN" && (
            <Link
              to="/admin/clients"
              className={cn(
                "flex items-center gap-1.5 rounded-md px-3 py-2 text-sm transition-colors",
                isClients
                  ? "bg-secondary font-medium text-secondary-foreground"
                  : "text-muted-foreground hover:text-foreground hover:bg-secondary/60",
              )}
            >
              <Contact className="size-4" />
              <span>Sociétaires</span>
            </Link>
          )}
        </nav>

        <div className="ml-auto flex items-center gap-4">
          <div className="hidden text-right leading-tight sm:block">
            <div className="text-sm font-medium text-foreground">{nomUtilisateur}</div>
            <div className="text-xs text-muted-foreground">
              {role === "ADMIN" ? "Administrateur" : "Agent de crédit"}
            </div>
          </div>
          <button
            type="button"
            onClick={() => { logout(); navigate("/login"); }}
            className="text-sm text-muted-foreground underline-offset-4 hover:text-foreground hover:underline cursor-pointer"
          >
            Déconnexion
          </button>
        </div>
      </div>
    </header>
  );
}
