import { useLocation, useNavigate } from "react-router-dom";
import {
  DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuSub,
  DropdownMenuSubContent, DropdownMenuSubTrigger, DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { ChevronRight } from "lucide-react";
import { useAuth } from "@/context/AuthContext";

function segmentsForPath(pathname: string): string[] {
  if (pathname.startsWith("/demande/nouvelle") || pathname.startsWith("/demande/rapport")) return ["Nouvelle demande", "Individu"];
  if (pathname.startsWith("/recherche/individu")) return ["Suivi client", "Recherche par identifiant"];
  if (pathname.startsWith("/demande/modifier")) return ["Mise à jour demande", "Saisie"];
  if (pathname.startsWith("/alertes")) return ["Sommaire des alertes", "Sommaire des alertes"];
  if (pathname.startsWith("/rapports")) return ["Historique des rapports"];
  if (pathname.startsWith("/audit")) return ["Journal d'audit"];
  if (pathname.startsWith("/admin/import")) return ["Importation de clients"];
  if (pathname.startsWith("/admin/demandes-modification")) return ["Demandes de mise à jour", "Validation admin"];
  if (pathname === "/admin" || pathname.startsWith("/admin/saisie-historique")) return ["Saisie d'historique"];
  return [];
}

export function AppBreadcrumb() {
  const location = useLocation();
  const navigate = useNavigate();
  const { role } = useAuth();
  const segments = segmentsForPath(location.pathname);

  return (
    <div className="flex items-center gap-2 border-b bg-slate-50 px-6 py-3 text-base">
      <DropdownMenu>
        <DropdownMenuTrigger className="font-medium text-slate-700 outline-none hover:text-slate-900">
          Credit Bureau avec Global Score
        </DropdownMenuTrigger>
        <DropdownMenuContent align="start">
          <DropdownMenuSub>
            <DropdownMenuSubTrigger>Nouvelle demande</DropdownMenuSubTrigger>
            <DropdownMenuSubContent>
              <DropdownMenuItem onClick={() => navigate("/demande/nouvelle/individu")}>Individu</DropdownMenuItem>
            </DropdownMenuSubContent>
          </DropdownMenuSub>
          <DropdownMenuSub>
            <DropdownMenuSubTrigger>Suivi client</DropdownMenuSubTrigger>
            <DropdownMenuSubContent>
              <DropdownMenuItem onClick={() => navigate("/recherche/individu")}>Recherche par identifiant</DropdownMenuItem>
            </DropdownMenuSubContent>
          </DropdownMenuSub>
          <DropdownMenuItem onClick={() => navigate("/demande/modifier")}>Mise à jour demande</DropdownMenuItem>
          <DropdownMenuItem onClick={() => navigate("/alertes")}>Sommaire des alertes</DropdownMenuItem>
          <DropdownMenuItem onClick={() => navigate("/rapports")}>Historique des rapports</DropdownMenuItem>
          {role === "ADMIN" && (
            <>
              <DropdownMenuItem onClick={() => navigate("/admin/import")}>Importation de clients (Admin)</DropdownMenuItem>
              <DropdownMenuItem onClick={() => navigate("/admin/demandes-modification")}>Demandes de mise à jour (Admin)</DropdownMenuItem>
              <DropdownMenuItem onClick={() => navigate("/admin/saisie-historique")}>Saisie d'historique (Admin)</DropdownMenuItem>
              <DropdownMenuItem onClick={() => navigate("/audit")}>Journal d'audit (Admin)</DropdownMenuItem>
            </>
          )}
        </DropdownMenuContent>
      </DropdownMenu>
      {segments.map((seg, i) => (
        <span key={i} className="flex items-center gap-2 text-slate-500">
          <ChevronRight className="size-3.5" />{seg}
        </span>
      ))}
    </div>
  );
}
