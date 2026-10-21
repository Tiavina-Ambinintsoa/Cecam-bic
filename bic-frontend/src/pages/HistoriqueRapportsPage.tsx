import { useEffect, useState, useMemo } from "react";
import { Link } from "react-router-dom";
import {
  FileText,
  Search,
  Download,
  ExternalLink,
  RotateCw,
  Loader2,
  X,
  AlertCircle,
  FileCheck2,
  Clock,
  CheckCircle2,
  Archive,
  Ban,
} from "lucide-react";
import { axiosClient } from "@/api/axiosClient";
import { rapportApi } from "@/api/rapportApi";
import { formatDateFr } from "@/lib/dates";
import { Button, buttonVariants } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { cn } from "@/lib/utils";

interface ContratListItem {
  id: number;
  codeContratCb: string;
  codeContratEtablissement?: string;
  codeDemandeEtablissement?: string;
  typeContrat: string;
  phaseDemande: "DEMANDE_EN_COURS" | "ACTIF" | "FERME" | "ABANDONNE" | "REFUSE" | string;
  dateDemande?: string;
  dateDebutContrat?: string;
  montantFinance?: number;
  devise?: string;
  client: {
    id: number;
    prenom: string;
    nom: string;
    codeClientCb: string;
    codeClientEtablissement?: string;
    cin?: string;
    identifiants?: Array<{ typeIdentifiant: string; numero: string }>;
  };
}

function formatMontant(montant?: number | null, devise = "MGA"): string {
  if (montant == null) return "-";
  return (
    new Intl.NumberFormat("fr-FR", {
      maximumFractionDigits: 0,
    }).format(montant) +
    " " +
    devise
  );
}

function getPhaseBadge(phase: string) {
  switch (phase) {
    case "DEMANDE_EN_COURS":
      return {
        label: "Demande en cours",
        className: "bg-blue-50 text-blue-700 border-blue-200",
        icon: Clock,
      };
    case "ACTIF":
      return {
        label: "Actif",
        className: "bg-emerald-50 text-emerald-700 border-emerald-200",
        icon: CheckCircle2,
      };
    case "FERME":
      return {
        label: "Fermé",
        className: "bg-slate-100 text-slate-700 border-slate-200",
        icon: Archive,
      };
    case "REFUSE":
      return {
        label: "Refusé",
        className: "bg-red-50 text-red-700 border-red-200",
        icon: Ban,
      };
    case "ABANDONNE":
      return {
        label: "Abandonné",
        className: "bg-amber-50 text-amber-700 border-amber-200",
        icon: Ban,
      };
    default:
      return {
        label: phase,
        className: "bg-slate-50 text-slate-600 border-slate-200",
        icon: FileText,
      };
  }
}

export function HistoriqueRapportsPage() {
  const [contrats, setContrats] = useState<ContratListItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [erreur, setErreur] = useState<string | null>(null);
  const [erreurPdf, setErreurPdf] = useState<string | null>(null);
  const [recherche, setRecherche] = useState("");
  const [phaseFiltre, setPhaseFiltre] = useState<string>("TOUTES");
  const [telechargementIds, setTelechargementIds] = useState<Record<number, boolean>>({});
  const [page, setPage] = useState(1);
  const lignesParPage = 12;

  const chargerContrats = () => {
    setLoading(true);
    setErreur(null);
    axiosClient
      .get<ContratListItem[]>("/contrats")
      .then((res) => {
        setContrats(res.data);
      })
      .catch(() => setErreur("Erreur lors du chargement de l'historique des contrats et rapports."))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    chargerContrats();
  }, []);

  const telechargerPdf = async (contratId: number, e: React.MouseEvent) => {
    e.stopPropagation();
    if (telechargementIds[contratId]) return;

    setErreurPdf(null);
    setTelechargementIds((prev) => ({ ...prev, [contratId]: true }));
    try {
      await rapportApi.telechargerEtVisualiserPdf(contratId);
    } catch {
      setErreurPdf(`Impossible de télécharger le PDF du contrat #${contratId}. Vérifiez le serveur.`);
    } finally {
      setTelechargementIds((prev) => ({ ...prev, [contratId]: false }));
    }
  };

  // Filtrage
  const contratsFiltres = useMemo(() => {
    const q = recherche.trim().toLowerCase();
    return contrats.filter((c) => {
      // Filtre de phase
      if (phaseFiltre !== "TOUTES" && c.phaseDemande !== phaseFiltre) {
        return false;
      }

      // Si pas de texte de recherche
      if (!q) return true;

      // Correspondances multi-critères
      const nom = (c.client?.nom || "").toLowerCase();
      const prenom = (c.client?.prenom || "").toLowerCase();
      const nomComplet = `${nom} ${prenom}`;
      const codeClientCb = (c.client?.codeClientCb || "").toLowerCase();
      const codeClientEtab = (c.client?.codeClientEtablissement || "").toLowerCase();
      const codeContratCb = (c.codeContratCb || "").toLowerCase();
      const codeContratEtab = (c.codeContratEtablissement || "").toLowerCase();
      const typeContrat = (c.typeContrat || "").toLowerCase();
      const identifiants = (c.client?.identifiants || [])
        .map((id) => id.numero?.toLowerCase() || "")
        .join(" ");

      return (
        nom.includes(q) ||
        prenom.includes(q) ||
        nomComplet.includes(q) ||
        codeClientCb.includes(q) ||
        codeClientEtab.includes(q) ||
        codeContratCb.includes(q) ||
        codeContratEtab.includes(q) ||
        typeContrat.includes(q) ||
        identifiants.includes(q)
      );
    });
  }, [contrats, recherche, phaseFiltre]);

  // Pagination
  const totalPages = Math.max(1, Math.ceil(contratsFiltres.length / lignesParPage));
  const contratsAffiches = useMemo(() => {
    const start = (page - 1) * lignesParPage;
    return contratsFiltres.slice(start, start + lignesParPage);
  }, [contratsFiltres, page, lignesParPage]);

  // Réinitialiser la page quand la recherche ou le filtre change
  useEffect(() => {
    setPage(1);
  }, [recherche, phaseFiltre]);

  // Statistiques rapides
  const stats = useMemo(() => {
    const total = contrats.length;
    const demandes = contrats.filter((c) => c.phaseDemande === "DEMANDE_EN_COURS").length;
    const actifs = contrats.filter((c) => c.phaseDemande === "ACTIF").length;
    const fermes = contrats.filter((c) => c.phaseDemande === "FERME").length;
    return { total, demandes, actifs, fermes };
  }, [contrats]);

  return (
    <div className="mx-auto max-w-7xl space-y-6 p-6">
      {/* En-tête de la page */}
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <div className="flex items-center gap-3">
            <h1 className="text-2xl font-bold tracking-tight text-slate-900">
              Historique des rapports
            </h1>
            <span className="inline-flex items-center rounded-full bg-primary/10 px-2.5 py-0.5 text-xs font-semibold text-primary">
              {contrats.length} rapport{contrats.length > 1 ? "s" : ""} généré{contrats.length > 1 ? "s" : ""}
            </span>
          </div>
          <p className="mt-1 text-sm text-slate-500">
            Consultez en ligne ou téléchargez en PDF l'ensemble des rapports de solvabilité générés, classés du plus récent au plus ancien.
          </p>
        </div>

        <div className="flex items-center gap-2">
          <Button
            variant="outline"
            size="sm"
            onClick={chargerContrats}
            disabled={loading}
            className="flex items-center gap-1.5"
            title="Rafraîchir la liste"
          >
            <RotateCw className={`size-3.5 ${loading ? "animate-spin" : ""}`} />
            <span>Actualiser</span>
          </Button>

          <Link
            to="/demande/nouvelle/individu"
            className={cn(buttonVariants({ size: "sm" }), "flex items-center gap-1.5")}
          >
            <FileCheck2 className="size-4" />
            <span>Nouveau rapport</span>
          </Link>
        </div>
      </div>

      {/* Cartes de statistiques / filtres rapides */}
      <div className="grid grid-cols-2 gap-3 sm:grid-cols-4">
        <button
          type="button"
          onClick={() => setPhaseFiltre("TOUTES")}
          className={`flex flex-col rounded-lg border p-3.5 text-left transition-all ${
            phaseFiltre === "TOUTES"
              ? "border-primary bg-primary/5 shadow-sm ring-1 ring-primary"
              : "border-slate-200 bg-white hover:border-slate-300"
          }`}
        >
          <span className="text-xs font-medium text-slate-500">Tous les rapports</span>
          <span className="mt-1 text-xl font-bold text-slate-900">{stats.total}</span>
        </button>

        <button
          type="button"
          onClick={() => setPhaseFiltre("DEMANDE_EN_COURS")}
          className={`flex flex-col rounded-lg border p-3.5 text-left transition-all ${
            phaseFiltre === "DEMANDE_EN_COURS"
              ? "border-blue-500 bg-blue-50/50 shadow-sm ring-1 ring-blue-500"
              : "border-slate-200 bg-white hover:border-slate-300"
          }`}
        >
          <span className="text-xs font-medium text-blue-600">Demande en cours</span>
          <span className="mt-1 text-xl font-bold text-blue-900">{stats.demandes}</span>
        </button>

        <button
          type="button"
          onClick={() => setPhaseFiltre("ACTIF")}
          className={`flex flex-col rounded-lg border p-3.5 text-left transition-all ${
            phaseFiltre === "ACTIF"
              ? "border-emerald-500 bg-emerald-50/50 shadow-sm ring-1 ring-emerald-500"
              : "border-slate-200 bg-white hover:border-slate-300"
          }`}
        >
          <span className="text-xs font-medium text-emerald-600">Contrats Actifs</span>
          <span className="mt-1 text-xl font-bold text-emerald-900">{stats.actifs}</span>
        </button>

        <button
          type="button"
          onClick={() => setPhaseFiltre("FERME")}
          className={`flex flex-col rounded-lg border p-3.5 text-left transition-all ${
            phaseFiltre === "FERME"
              ? "border-slate-500 bg-slate-100/50 shadow-sm ring-1 ring-slate-500"
              : "border-slate-200 bg-white hover:border-slate-300"
          }`}
        >
          <span className="text-xs font-medium text-slate-600">Clôturés / Fermés</span>
          <span className="mt-1 text-xl font-bold text-slate-900">{stats.fermes}</span>
        </button>
      </div>

      {/* Barre de recherche et statut */}
      <div className="flex flex-col gap-3 rounded-lg border border-slate-200 bg-white p-4 shadow-sm sm:flex-row sm:items-center sm:justify-between">
        <div className="relative w-full max-w-md">
          <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-slate-400" />
          <Input
            value={recherche}
            onChange={(e) => setRecherche(e.target.value)}
            placeholder="Rechercher par nom, CIN, code CB, n° contrat..."
            className="pl-9 pr-8"
          />
          {recherche && (
            <button
              type="button"
              onClick={() => setRecherche("")}
              className="absolute right-2.5 top-1/2 -translate-y-1/2 rounded p-0.5 text-slate-400 hover:text-slate-600"
            >
              <X className="size-4" />
            </button>
          )}
        </div>

        <div className="flex items-center gap-3 text-sm text-slate-600">
          <span>
            {contratsFiltres.length} dossier{contratsFiltres.length > 1 ? "s" : ""} trouvé{contratsFiltres.length > 1 ? "s" : ""}
          </span>
          {recherche && (
            <span className="inline-flex items-center gap-1 text-xs text-slate-500">
              pour « <strong className="text-slate-700">{recherche}</strong> »
            </span>
          )}
        </div>
      </div>

      {/* Messages d'erreur */}
      {erreur && (
        <div className="flex items-center justify-between rounded-lg border border-red-200 bg-red-50 p-4 text-sm text-red-700">
          <div className="flex items-center gap-2">
            <AlertCircle className="size-5 shrink-0 text-red-600" />
            <span>{erreur}</span>
          </div>
          <Button variant="outline" size="xs" onClick={chargerContrats}>
            Réessayer
          </Button>
        </div>
      )}

      {erreurPdf && (
        <div className="flex items-center justify-between rounded-lg border border-red-200 bg-red-50 p-4 text-sm text-red-700">
          <div className="flex items-center gap-2">
            <AlertCircle className="size-5 shrink-0 text-red-600" />
            <span>{erreurPdf}</span>
          </div>
          <Button variant="outline" size="xs" onClick={() => setErreurPdf(null)}>
            Fermer
          </Button>
        </div>
      )}

      {/* Tableau des rapports */}
      <div className="overflow-hidden rounded-lg border border-slate-200 bg-white shadow-sm">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="border-b border-slate-200 bg-slate-50/80 text-xs font-semibold uppercase tracking-wider text-slate-600">
              <tr>
                <th className="p-3.5">Date</th>
                <th className="p-3.5">Client (Sociétaire)</th>
                <th className="p-3.5">Identifiants CB</th>
                <th className="p-3.5">Type de contrat</th>
                <th className="p-3.5 text-right">Montant</th>
                <th className="p-3.5 text-center">Phase</th>
                <th className="p-3.5 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading && contrats.length === 0 ? (
                <tr>
                  <td colSpan={7} className="p-12 text-center text-slate-500">
                    <div className="flex flex-col items-center justify-center gap-2">
                      <Loader2 className="size-6 animate-spin text-primary" />
                      <p className="text-base font-medium">Chargement de l'historique des rapports...</p>
                    </div>
                  </td>
                </tr>
              ) : contratsAffiches.length === 0 ? (
                <tr>
                  <td colSpan={7} className="p-12 text-center text-slate-500">
                    <div className="flex flex-col items-center justify-center gap-2">
                      <FileText className="size-8 text-slate-300" />
                      <p className="text-base font-medium text-slate-700">Aucun rapport correspondant</p>
                      <p className="text-sm text-slate-500">
                        {recherche || phaseFiltre !== "TOUTES"
                          ? "Essayez de modifier votre recherche ou de réinitialiser les filtres."
                          : "Aucune demande de crédit ou rapport n'a encore été enregistré."}
                      </p>
                      {(recherche || phaseFiltre !== "TOUTES") && (
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => {
                            setRecherche("");
                            setPhaseFiltre("TOUTES");
                          }}
                          className="mt-2"
                        >
                          Réinitialiser les filtres
                        </Button>
                      )}
                    </div>
                  </td>
                </tr>
              ) : (
                contratsAffiches.map((c) => {
                  const phaseInfo = getPhaseBadge(c.phaseDemande);
                  const PhaseIcon = phaseInfo.icon;
                  const isTelechargement = !!telechargementIds[c.id];
                  const nomAffichage = [c.client?.nom?.toUpperCase(), c.client?.prenom]
                    .filter(Boolean)
                    .join(" ");

                  return (
                    <tr
                      key={c.id}
                      className="group transition-colors hover:bg-slate-50/70"
                    >
                      {/* Date */}
                      <td className="whitespace-nowrap p-3.5 font-medium text-slate-700">
                        {formatDateFr(c.dateDemande || c.dateDebutContrat)}
                      </td>

                      {/* Client */}
                      <td className="p-3.5">
                        <Link
                          to={`/demande/rapport/${c.id}`}
                          className="font-semibold text-slate-900 group-hover:text-primary hover:underline"
                        >
                          {nomAffichage || "Client non nommé"}
                        </Link>
                        {c.client?.codeClientEtablissement && (
                          <div className="text-xs text-slate-500">
                            CECAM: {c.client.codeClientEtablissement}
                          </div>
                        )}
                      </td>

                      {/* Identifiants CB */}
                      <td className="p-3.5">
                        <div className="font-mono text-xs font-medium text-slate-800">
                          Contrat: {c.codeContratCb || `#${c.id}`}
                        </div>
                        <div className="font-mono text-xs text-slate-500">
                          Client: {c.client?.codeClientCb || "—"}
                        </div>
                      </td>

                      {/* Type de contrat */}
                      <td className="p-3.5 text-slate-700">
                        <span className="inline-block max-w-[200px] truncate" title={c.typeContrat}>
                          {c.typeContrat || "Crédit"}
                        </span>
                      </td>

                      {/* Montant */}
                      <td className="whitespace-nowrap p-3.5 text-right font-medium text-slate-900">
                        {formatMontant(c.montantFinance, c.devise)}
                      </td>

                      {/* Phase */}
                      <td className="whitespace-nowrap p-3.5 text-center">
                        <span
                          className={`inline-flex items-center gap-1.5 rounded-full border px-2.5 py-0.5 text-xs font-semibold ${phaseInfo.className}`}
                        >
                          <PhaseIcon className="size-3" />
                          {phaseInfo.label}
                        </span>
                      </td>

                      {/* Actions */}
                      <td className="whitespace-nowrap p-3.5 text-right">
                        <div className="flex items-center justify-end gap-1.5">
                          {/* Bouton Consulter le rapport */}
                          <Link
                            to={`/demande/rapport/${c.id}`}
                            className={cn(
                              buttonVariants({ variant: "outline", size: "sm" }),
                              "h-8 gap-1 border-slate-200 text-xs font-medium hover:border-primary hover:bg-primary/5 hover:text-primary"
                            )}
                          >
                            <ExternalLink className="size-3.5" />
                            <span>Consulter</span>
                          </Link>

                          {/* Bouton Télécharger PDF */}
                          <Button
                            variant="secondary"
                            size="sm"
                            onClick={(e) => telechargerPdf(c.id, e)}
                            disabled={isTelechargement}
                            className="h-8 gap-1 text-xs font-medium"
                            title="Télécharger et ouvrir le rapport en PDF"
                          >
                            {isTelechargement ? (
                              <Loader2 className="size-3.5 animate-spin" />
                            ) : (
                              <Download className="size-3.5" />
                            )}
                            <span>PDF</span>
                          </Button>
                        </div>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>

        {/* Pied de tableau avec pagination */}
        {totalPages > 1 && (
          <div className="flex items-center justify-between border-t border-slate-200 bg-slate-50/50 px-4 py-3 text-sm text-slate-600">
            <div>
              Affichage de{" "}
              <strong className="text-slate-900">
                {(page - 1) * lignesParPage + 1}
              </strong>{" "}
              à{" "}
              <strong className="text-slate-900">
                {Math.min(page * lignesParPage, contratsFiltres.length)}
              </strong>{" "}
              sur <strong className="text-slate-900">{contratsFiltres.length}</strong> dossiers
            </div>

            <div className="flex items-center gap-1.5">
              <Button
                variant="outline"
                size="xs"
                onClick={() => setPage((p) => Math.max(1, p - 1))}
                disabled={page === 1}
              >
                Précédent
              </Button>
              <span className="px-2 text-xs font-medium">
                Page {page} sur {totalPages}
              </span>
              <Button
                variant="outline"
                size="xs"
                onClick={() => setPage((p) => Math.min(totalPages, p + 1))}
                disabled={page === totalPages}
              >
                Suivant
              </Button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
