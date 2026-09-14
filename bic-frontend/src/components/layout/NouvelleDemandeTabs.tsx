import { useLocation, Link } from "react-router-dom";
import { cn } from "@/lib/utils";
import { useDemandeEnCours } from "@/context/DemandeEnCoursContext";

export function NouvelleDemandeTabs() {
  const location = useLocation();
  const { clientId, contratId } = useDemandeEnCours();

  const tabs = [
    { label: "Données individu", to: "/demande/nouvelle/individu", active: (p: string) => p.startsWith("/demande/nouvelle/individu"), enabled: true },
    { label: "Demande et contrat", to: clientId ? `/demande/nouvelle/contrat/${clientId}` : undefined, active: (p: string) => p.startsWith("/demande/nouvelle/contrat"), enabled: !!clientId },
    { label: "Rapport", to: contratId ? `/demande/rapport/${contratId}` : undefined, active: (p: string) => p.startsWith("/demande/rapport"), enabled: !!contratId },
  ];

  const dansLeParcours = tabs.some((t) => t.active(location.pathname));
  if (!dansLeParcours) return null;

  return (
    <div className="flex gap-1 border-b bg-white px-6">
      {tabs.map((tab) => {
        const isActive = tab.active(location.pathname);
        const content = (
          <span
            className={cn(
              "inline-block border-b-2 px-4 py-2.5 text-sm font-medium transition-colors",
              isActive ? "border-slate-900 text-slate-900" : "border-transparent text-slate-400",
              !tab.enabled && !isActive && "cursor-not-allowed opacity-50"
            )}
          >
            {tab.label}
          </span>
        );
        return tab.enabled && tab.to ? <Link key={tab.label} to={tab.to}>{content}</Link> : <span key={tab.label}>{content}</span>;
      })}
    </div>
  );
}