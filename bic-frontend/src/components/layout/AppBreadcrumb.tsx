import { useLocation, useNavigate } from "react-router-dom";
import {
  DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuSub,
  DropdownMenuSubContent, DropdownMenuSubTrigger, DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { ChevronRight } from "lucide-react";
import { useAuth } from "@/context/AuthContext";

function segmentsForPath(pathname: string): string[] {
  if (pathname.startsWith("/demande/nouvelle") || pathname.startsWith("/demande/rapport")) return ["Nouvelle demande", "Individu"];
  if (pathname.startsWith("/recherche/individu")) return ["Recherche par individu"];
  if (pathname.startsWith("/demande/modifier")) return ["Mise à jour demande"];
  if (pathname.startsWith("/alertes")) return ["Alertes"];
  if (pathname.startsWith("/rapports")) return ["Historique des rapports"];
  if (pathname.startsWith("/audit")) return ["Journal d'audit"];
  return [];
}

export function AppBreadcrumb() {
  const location = useLocation();
  const navigate = useNavigate();
  const { role } = useAuth();
  const segments = segmentsForPath(location.pathname);

  return (
    <div className="flex items-center gap-2 border-b bg-slate-50 px-6 py-2 text-sm">
      <DropdownMenu>
        <DropdownMenuTrigger className="font-medium text-slate-700 outline-none hover:text-slate-900">
          Credit Bureau avec Global Score
        </DropdownMenuTrigger>
        <DropdownMenuContent align="start">
          <DropdownMenuSub>
            <DropdownMenuSubTrigger>Nouvelle demande</DropdownMenuSubTrigger>
            <DropdownMenuSubContent>
              <DropdownMenuItem onClick={() => navigate("/demande/nouvelle/individu")}>Individu</DropdownMenuItem>
              <DropdownMenuItem disabled>Entreprise (à venir)</DropdownMenuItem>
            </DropdownMenuSubContent>
          </DropdownMenuSub>
          <DropdownMenuItem onClick={() => navigate("/recherche/individu")}>Recherche par individu</DropdownMenuItem>
          <DropdownMenuItem onClick={() => navigate("/demande/modifier")}>Mise à jour demande</DropdownMenuItem>
          <DropdownMenuItem onClick={() => navigate("/alertes")}>Alertes</DropdownMenuItem>
          <DropdownMenuItem onClick={() => navigate("/rapports")}>Historique des rapports</DropdownMenuItem>
          {role === "ADMIN" && (
            <DropdownMenuItem onClick={() => navigate("/audit")}>Journal d'audit</DropdownMenuItem>
          )}
        </DropdownMenuContent>
      </DropdownMenu>

      {segments.map((seg, i) => (
        <span key={i} className="flex items-center gap-2 text-slate-500">
          <ChevronRight className="size-3.5" />
          {seg}
        </span>
      ))}
    </div>
  );
}