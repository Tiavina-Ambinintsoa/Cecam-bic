import { Link } from "react-router-dom";
import { FilePlus2, Search, PencilLine, Bell, type LucideIcon } from "lucide-react";

interface Tache {
  label: string;
  description: string;
  to?: string;
  icon: LucideIcon;
  indisponible?: boolean;
}

const taches: Tache[] = [
  {
    label: "Nouvelle demande — individu",
    description: "Saisir un sociétaire et sa demande de crédit, puis générer son rapport.",
    to: "/demande/nouvelle/individu",
    icon: FilePlus2,
  },
  {
    label: "Rechercher un sociétaire",
    description: "Retrouver un dossier par CIN, nom ou code client.",
    to: "/recherche/individu",
    icon: Search,
  },
  {
    label: "Modifier une demande",
    description: "Corriger les montants, la phase ou l'échéancier d'une demande existante.",
    to: "/demande/modifier",
    icon: PencilLine,
  },
  {
    label: "Alertes",
    description: "Contrats présentant des échéances en retard ou impayées.",
    to: "/alertes",
    icon: Bell,
  },
  {
    label: "Nouvelle demande — entreprise",
    description: "Le module entreprise n'est pas encore ouvert.",
    icon: FilePlus2,
    indisponible: true,
  },
];

export function HomeMenu() {
  return (
    <div className="mx-auto w-full max-w-3xl px-6 py-12">
      <h1 className="font-heading text-2xl text-foreground">Que voulez-vous faire ?</h1>
      <p className="mt-1.5 text-sm text-muted-foreground">
        Toute consultation d'un dossier est enregistrée dans le journal d'audit.
      </p>

      <ul className="mt-8 divide-y divide-border border-y border-border">
        {taches.map((t) => {
          const Icone = t.icon;
          const contenu = (
            <>
              <Icone
                className={t.indisponible ? "size-5 text-muted-foreground/40" : "size-5 text-primary"}
                aria-hidden
              />
              <span className="min-w-0">
                <span className="block text-[0.95rem] text-foreground">{t.label}</span>
                <span className="mt-0.5 block text-sm leading-relaxed text-muted-foreground">
                  {t.description}
                </span>
              </span>
            </>
          );

          return (
            <li key={t.label}>
              {t.indisponible ? (
                <div className="flex items-start gap-4 py-4 opacity-45">{contenu}</div>
              ) : (
                <Link
                  to={t.to!}
                  className="flex items-start gap-4 py-4 transition-colors hover:bg-secondary/60"
                >
                  {contenu}
                </Link>
              )}
            </li>
          );
        })}
      </ul>
    </div>
  );
}