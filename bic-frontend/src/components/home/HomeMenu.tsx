import { Link } from "react-router-dom";
import { FilePlus2, Search, PencilLine, Bell, History, Upload, ClipboardList, ShieldCheck, type LucideIcon } from "lucide-react";
import { useAuth } from "@/context/AuthContext";

interface Tache {
  label: string;
  description: string;
  to: string;
  icon: LucideIcon;
}

const tachesCommunes: Tache[] = [
  {
    label: "Nouvelle demande — individu",
    description: "Saisir un sociétaire et sa demande de crédit, puis générer son rapport.",
    to: "/demande/nouvelle/individu",
    icon: FilePlus2,
  },
  {
    label: "Rechercher un sociétaire",
    description: "Retrouver un dossier par Code Client CB, Code Client CECAM ou CIN.",
    to: "/recherche/individu",
    icon: Search,
  },
  {
    label: "Modifier une demande",
    description: "Envoyer une demande de correction à valider par l’administrateur.",
    to: "/demande/modifier",
    icon: PencilLine,
  },
  {
    label: "Alertes",
    description: "Contrats présentant des échéances en retard ou impayées.",
    to: "/alertes",
    icon: Bell,
  },
  { label: "Historique des rapports", description: "Consulter les rapports déjà générés.", to: "/rapports", icon: History },
];

export function HomeMenu() {
  const { role } = useAuth();
  const taches = role === "ADMIN" ? [
    ...tachesCommunes,
    { label: "Demandes de mise à jour", description: "Approuver ou refuser les changements soumis par les agents.", to: "/admin/demandes-modification", icon: ClipboardList },
    { label: "Importation de clients", description: "Charger les clients depuis un fichier CSV.", to: "/admin/import", icon: Upload },
    { label: "Saisie d'historique", description: "Saisir ou corriger les contrats historiques.", to: "/admin/saisie-historique", icon: FilePlus2 },
    { label: "Journal d'audit", description: "Consulter les actions réalisées dans le système.", to: "/audit", icon: ShieldCheck },
  ] : tachesCommunes;

  return (
    <div className="mx-auto w-full max-w-5xl px-6 py-12">
      <h1 className="font-heading text-3xl text-foreground">Que voulez-vous faire ?</h1>
      <p className="mt-2 text-base text-muted-foreground">
        Toute consultation d'un dossier est enregistrée dans le journal d'audit.
      </p>

      <ul className="mt-8 divide-y divide-border border-y border-border">
        {taches.map((t) => {
          const Icone = t.icon;
          const contenu = (
            <>
              <Icone className="mt-0.5 size-6 text-primary" aria-hidden />
              <span className="min-w-0">
                <span className="block text-lg font-medium text-foreground">{t.label}</span>
                <span className="mt-1 block text-base leading-relaxed text-muted-foreground">
                  {t.description}
                </span>
              </span>
            </>
          );

          return (
            <li key={t.label}>
              <Link to={t.to} className="flex items-start gap-4 py-5 transition-colors hover:bg-secondary/60">
                {contenu}
              </Link>
            </li>
          );
        })}
      </ul>
    </div>
  );
}
