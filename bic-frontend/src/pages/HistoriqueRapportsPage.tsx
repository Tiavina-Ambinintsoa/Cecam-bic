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
  SlidersHorizontal,
  ShieldCheck,
} from "lucide-react";
import { axiosClient } from "@/api/axiosClient";
import { rapportApi } from "@/api/rapportApi";
import { formatDateFr } from "@/lib/dates";
import { Button, buttonVariants } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { cn } from "@/lib/utils";

interface ContratListItem {
  id: number;
  codeContratCb: string;
  codeContratEtablissement?: string;
  codeDemandeEtablissement?: string;
  typeContrat: string;
  typeCredit?: string;
  phaseDemande: "DEMANDE_EN_COURS" | "ACTIF" | "FERME" | "ABANDONNE" | "REFUSE" | string;
  dateDemande?: string;
  dateDebutContrat?: string;
  montantFinance?: number;
  devise?: string;
  scoreValeur?: number | null;
  scoreCategorie?: string | null;
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

function formatMontant(montant?: number | null, devise = "Ar"): string {
  if (montant == null) return "-";
  let unite = "Ar";
  if (devise) {
    const d = devise.trim().toLowerCase();
    if (d === "ariary malgache" || d === "ariary" || d === "mga") {
      unite = "Ar";
    } else {
      unite = devise;
    }
  }
  return (
    new Intl.NumberFormat("fr-FR", {
      maximumFractionDigits: 0,
    }).format(montant) +
    " " +
    unite
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

function getScoreBadge(score?: number | null, categorie?: string | null) {
  if (score == null) {
    return (
      <span className="inline-flex items-center rounded-md bg-slate-100 px-2 py-0.5 text-xs font-medium text-slate-500">
        Non calculé
      </span>
    );
  }

  let colorClass = "bg-slate-100 text-slate-800 border-slate-200";
  let lettre = "C";

  if (score >= 530) {
    colorClass = "bg-emerald-50 text-emerald-700 border-emerald-200";
    lettre = "A";
  } else if (score >= 500) {
    colorClass = "bg-lime-50 text-lime-800 border-lime-200";
    lettre = "B";
  } else if (score >= 460) {
    colorClass = "bg-amber-50 text-amber-700 border-amber-200";
    lettre = "C";
  } else if (score >= 400) {
    colorClass = "bg-orange-50 text-orange-700 border-orange-200";
    lettre = "D";
  } else {
    colorClass = "bg-red-50 text-red-700 border-red-200";
    lettre = "E";
  }

  return (
    <div className="inline-flex flex-col items-center">
      <span className={cn("inline-flex items-center gap-1 rounded-full border px-2.5 py-0.5 text-xs font-bold", colorClass)}>
        <ShieldCheck className="size-3" />
        {score} pts ({lettre})
      </span>
      {categorie && (
        <span className="mt-0.5 text-[11px] font-medium text-slate-500">
          {categorie}
        </span>
      )}
    </div>
  );
}

export function HistoriqueRapportsPage() {
  const [contrats, setContrats] = useState<ContratListItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [erreur, setErreur] = useState<string | null>(null);
  const [erreurPdf, setErreurPdf] = useState<string | null>(null);

  // Filtres
  const [recherche, setRecherche] = useState("");
  const [phaseFiltre, setPhaseFiltre] = useState<string>("TOUTES");
  const [montantMin, setMontantMin] = useState<string>("");
  const [montantMax, setMontantMax] = useState<string>("");
  const [scoreFiltre, setScoreFiltre] = useState<string>("TOUS");
  const [typeCreditFiltre, setTypeCreditFiltre] = useState<string>("TOUS");
  const [periodeFiltre, setPeriodeFiltre] = useState<string>("TOUTES");
  const [filtresAvancesOuverts, setFiltresAvancesOuverts] = useState(false);

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

  const reinitialiserFiltres = () => {
    setRecherche("");
    setPhaseFiltre("TOUTES");
    setMontantMin("");
    setMontantMax("");
    setScoreFiltre("TOUS");
    setTypeCreditFiltre("TOUS");
    setPeriodeFiltre("TOUTES");
  };

  const nbFiltresActifs = useMemo(() => {
    let count = 0;
    if (phaseFiltre !== "TOUTES") count++;
    if (montantMin) count++;
    if (montantMax) count++;
    if (scoreFiltre !== "TOUS") count++;
    if (typeCreditFiltre !== "TOUS") count++;
    if (periodeFiltre !== "TOUTES") count++;
    return count;
  }, [phaseFiltre, montantMin, montantMax, scoreFiltre, typeCreditFiltre, periodeFiltre]);

  // Filtrage multi-critères
  const contratsFiltres = useMemo(() => {
    const q = recherche.trim().toLowerCase();
    const minVal = montantMin ? Number(montantMin.replace(/\s/g, "")) : null;
    const maxVal = montantMax ? Number(montantMax.replace(/\s/g, "")) : null;

    const now = new Date();
    const todayStr = now.toISOString().split("T")[0];
    const sevenDaysAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
    const thirtyDaysAgo = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);

    return contrats.filter((c) => {
      // 1. Filtre de phase
      if (phaseFiltre !== "TOUTES" && c.phaseDemande !== phaseFiltre) {
        return false;
      }

      // 2. Marge de somme (Montant min / max)
      if (minVal != null && !isNaN(minVal) && (c.montantFinance == null || c.montantFinance < minVal)) {
        return false;
      }
      if (maxVal != null && !isNaN(maxVal) && (c.montantFinance == null || c.montantFinance > maxVal)) {
        return false;
      }

      // 3. Catégorie de score
      if (scoreFiltre !== "TOUS") {
        if (scoreFiltre === "NON_CALCULE") {
          if (c.scoreValeur != null) return false;
        } else if (scoreFiltre === "A") {
          if (c.scoreValeur == null || c.scoreValeur < 530) return false;
        } else if (scoreFiltre === "B") {
          if (c.scoreValeur == null || c.scoreValeur < 500 || c.scoreValeur >= 530) return false;
        } else if (scoreFiltre === "C") {
          if (c.scoreValeur == null || c.scoreValeur < 460 || c.scoreValeur >= 500) return false;
        } else if (scoreFiltre === "D") {
          if (c.scoreValeur == null || c.scoreValeur < 400 || c.scoreValeur >= 460) return false;
        } else if (scoreFiltre === "E") {
          if (c.scoreValeur == null || c.scoreValeur >= 400) return false;
        }
      }

      // 4. Type de crédit
      if (typeCreditFiltre !== "TOUS") {
        const typeC = (c.typeCredit || c.typeContrat || "").toUpperCase();
        if (!typeC.includes(typeCreditFiltre.toUpperCase())) {
          return false;
        }
      }

      // 5. Période
      if (periodeFiltre !== "TOUTES") {
        const rawDate = c.dateDemande || c.dateDebutContrat;
        if (!rawDate) return false;
        const d = new Date(rawDate);
        if (periodeFiltre === "TODAY") {
          if (!rawDate.startsWith(todayStr)) return false;
        } else if (periodeFiltre === "7D") {
          if (d < sevenDaysAgo) return false;
        } else if (periodeFiltre === "30D") {
          if (d < thirtyDaysAgo) return false;
        }
      }

      // 6. Recherche texte
      if (q) {
        const nom = (c.client?.nom || "").toLowerCase();
        const prenom = (c.client?.prenom || "").toLowerCase();
        const nomComplet = `${nom} ${prenom}`;
        const codeClientCb = (c.client?.codeClientCb || "").toLowerCase();
        const codeClientEtab = (c.client?.codeClientEtablissement || "").toLowerCase();
        const codeContratCb = (c.codeContratCb || "").toLowerCase();
        const codeContratEtab = (c.codeContratEtablissement || "").toLowerCase();
        const typeContrat = (c.typeContrat || "").toLowerCase();
        const typeCredit = (c.typeCredit || "").toLowerCase();
        const identifiants = (c.client?.identifiants || [])
          .map((id) => id.numero?.toLowerCase() || "")
          .join(" ");

        const matchRecherche =
          nom.includes(q) ||
          prenom.includes(q) ||
          nomComplet.includes(q) ||
          codeClientCb.includes(q) ||
          codeClientEtab.includes(q) ||
          codeContratCb.includes(q) ||
          codeContratEtab.includes(q) ||
          typeContrat.includes(q) ||
          typeCredit.includes(q) ||
          identifiants.includes(q);

        if (!matchRecherche) return false;
      }

      return true;
    });
  }, [contrats, recherche, phaseFiltre, montantMin, montantMax, scoreFiltre, typeCreditFiltre, periodeFiltre]);

  // Pagination
  const totalPages = Math.max(1, Math.ceil(contratsFiltres.length / lignesParPage));
  const contratsAffiches = useMemo(() => {
    const start = (page - 1) * lignesParPage;
    return contratsFiltres.slice(start, start + lignesParPage);
  }, [contratsFiltres, page, lignesParPage]);

  // Réinitialiser la page quand les filtres changent
  useEffect(() => {
    setPage(1);
  }, [recherche, phaseFiltre, montantMin, montantMax, scoreFiltre, typeCreditFiltre, periodeFiltre]);

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
              {contrats.length} rapport{contrats.length > 1 ? "s" : ""} enregistré{contrats.length > 1 ? "s" : ""}
            </span>
          </div>
          <p className="mt-1 text-sm text-slate-500">
            Consultez en ligne ou téléchargez en PDF l&apos;ensemble des rapports de solvabilité générés, avec calcul du score, filtres par montant et niveau de risque.
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

      {/* Cartes de statistiques / filtres de phase rapides */}
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
          <span className="text-xs font-medium text-blue-600">Demandes en cours</span>
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

      {/* Barre de recherche et panneau de filtres avancés */}
      <div className="space-y-3 rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
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

          <div className="flex items-center gap-2">
            <Button
              type="button"
              variant={filtresAvancesOuverts || nbFiltresActifs > 0 ? "secondary" : "outline"}
              size="sm"
              onClick={() => setFiltresAvancesOuverts(!filtresAvancesOuverts)}
              className="flex items-center gap-1.5"
            >
              <SlidersHorizontal className="size-3.5" />
              <span>Filtres</span>
              {nbFiltresActifs > 0 && (
                <span className="ml-1 inline-flex size-5 items-center justify-center rounded-full bg-primary text-[10px] font-bold text-white">
                  {nbFiltresActifs}
                </span>
              )}
            </Button>

            {(recherche || nbFiltresActifs > 0) && (
              <Button
                type="button"
                variant="ghost"
                size="sm"
                onClick={reinitialiserFiltres}
                className="text-xs text-slate-500 hover:text-slate-700"
              >
                Réinitialiser
              </Button>
            )}

            <div className="hidden sm:block text-xs text-slate-500 pl-2">
              <strong>{contratsFiltres.length}</strong> dossier{contratsFiltres.length > 1 ? "s" : ""}
            </div>
          </div>
        </div>

        {/* Panneau de filtres déroulant */}
        {filtresAvancesOuverts && (
          <div className="grid grid-cols-1 gap-4 border-t border-slate-100 pt-3 md:grid-cols-4">
            {/* 1. Marge de somme (Montant min / max) */}
            <div className="space-y-1.5 md:col-span-2">
              <label className="text-xs font-semibold uppercase tracking-wider text-slate-600">
                Marge de montant (MGA)
              </label>
              <div className="flex items-center gap-2">
                <Input
                  type="number"
                  placeholder="Min (ex. 1 000 000)"
                  value={montantMin}
                  onChange={(e) => setMontantMin(e.target.value)}
                  className="h-9 text-xs"
                />
                <span className="text-xs text-slate-400">à</span>
                <Input
                  type="number"
                  placeholder="Max (ex. 20 000 000)"
                  value={montantMax}
                  onChange={(e) => setMontantMax(e.target.value)}
                  className="h-9 text-xs"
                />
              </div>
              <div className="flex flex-wrap gap-1 pt-1">
                <button
                  type="button"
                  onClick={() => { setMontantMin(""); setMontantMax("5000000"); }}
                  className="rounded bg-slate-100 px-2 py-0.5 text-[11px] text-slate-600 hover:bg-slate-200"
                >
                  &lt; 5M
                </button>
                <button
                  type="button"
                  onClick={() => { setMontantMin("5000000"); setMontantMax("20000000"); }}
                  className="rounded bg-slate-100 px-2 py-0.5 text-[11px] text-slate-600 hover:bg-slate-200"
                >
                  5M - 20M
                </button>
                <button
                  type="button"
                  onClick={() => { setMontantMin("20000000"); setMontantMax("50000000"); }}
                  className="rounded bg-slate-100 px-2 py-0.5 text-[11px] text-slate-600 hover:bg-slate-200"
                >
                  20M - 50M
                </button>
                <button
                  type="button"
                  onClick={() => { setMontantMin("50000000"); setMontantMax(""); }}
                  className="rounded bg-slate-100 px-2 py-0.5 text-[11px] text-slate-600 hover:bg-slate-200"
                >
                  &gt; 50M
                </button>
              </div>
            </div>

            {/* 2. Catégorie de Score */}
            <div className="space-y-1.5">
              <label className="text-xs font-semibold uppercase tracking-wider text-slate-600">
                Catégorie de Score
              </label>
              <Select value={scoreFiltre} onValueChange={(val) => setScoreFiltre(val ?? "TOUS")}>
                <SelectTrigger className="h-9 text-xs">
                  <SelectValue placeholder="Toutes catégories" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="TOUS">Toutes les catégories</SelectItem>
                  <SelectItem value="A">A - Très faible risque (≥ 530)</SelectItem>
                  <SelectItem value="B">B - Faible risque (500 - 529)</SelectItem>
                  <SelectItem value="C">C - Risque moyen (460 - 499)</SelectItem>
                  <SelectItem value="D">D - Risque élevé (400 - 459)</SelectItem>
                  <SelectItem value="E">E - Risque très élevé (&lt; 400)</SelectItem>
                  <SelectItem value="NON_CALCULE">Non calculé / En attente</SelectItem>
                </SelectContent>
              </Select>
            </div>

            {/* 3. Type de crédit */}
            <div className="space-y-1.5">
              <label className="text-xs font-semibold uppercase tracking-wider text-slate-600">
                Type de crédit
              </label>
              <Select value={typeCreditFiltre} onValueChange={(val) => setTypeCreditFiltre(val ?? "TOUS")}>
                <SelectTrigger className="h-9 text-xs">
                  <SelectValue placeholder="Tous les types" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="TOUS">Tous types de crédit</SelectItem>
                  <SelectItem value="RFR">RFR (2.75%)</SelectItem>
                  <SelectItem value="PRO">PRO (3.5%)</SelectItem>
                  <SelectItem value="GCV">GCV (3.0%)</SelectItem>
                  <SelectItem value="LVM">LVM (2.75%)</SelectItem>
                  <SelectItem value="CIM">CIM (2.75%)</SelectItem>
                </SelectContent>
              </Select>
            </div>

            {/* 4. Période */}
            <div className="space-y-1.5">
              <label className="text-xs font-semibold uppercase tracking-wider text-slate-600">
                Période
              </label>
              <Select value={periodeFiltre} onValueChange={(val) => setPeriodeFiltre(val ?? "TOUTES")}>
                <SelectTrigger className="h-9 text-xs">
                  <SelectValue placeholder="Toutes dates" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="TOUTES">Toutes les dates</SelectItem>
                  <SelectItem value="TODAY">Aujourd&apos;hui</SelectItem>
                  <SelectItem value="7D">7 derniers jours</SelectItem>
                  <SelectItem value="30D">30 derniers jours</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
        )}
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
                <th className="p-3.5">Type de crédit</th>
                <th className="p-3.5 text-right">Montant</th>
                <th className="p-3.5 text-center">Score & Risque</th>
                <th className="p-3.5 text-center">Phase</th>
                <th className="p-3.5 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading && contrats.length === 0 ? (
                <tr>
                  <td colSpan={8} className="p-12 text-center text-slate-500">
                    <div className="flex flex-col items-center justify-center gap-2">
                      <Loader2 className="size-6 animate-spin text-primary" />
                      <p className="text-base font-medium">Chargement de l&apos;historique des rapports...</p>
                    </div>
                  </td>
                </tr>
              ) : contratsAffiches.length === 0 ? (
                <tr>
                  <td colSpan={8} className="p-12 text-center text-slate-500">
                    <div className="flex flex-col items-center justify-center gap-2">
                      <FileText className="size-8 text-slate-300" />
                      <p className="text-base font-medium text-slate-700">Aucun rapport correspondant</p>
                      <p className="text-sm text-slate-500">
                        {recherche || nbFiltresActifs > 0
                          ? "Essayez de modifier votre recherche ou de réinitialiser les filtres."
                          : "Aucune demande de crédit ou rapport n'a encore été enregistré."}
                      </p>
                      {(recherche || nbFiltresActifs > 0) && (
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={reinitialiserFiltres}
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

                  const typeAffiche = c.typeCredit || c.typeContrat || "Crédit";

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

                      {/* Type de contrat / crédit */}
                      <td className="p-3.5 text-slate-700">
                        <span className="inline-block max-w-[180px] truncate font-medium" title={typeAffiche}>
                          {typeAffiche}
                        </span>
                      </td>

                      {/* Montant */}
                      <td className="whitespace-nowrap p-3.5 text-right font-medium text-slate-900">
                        {formatMontant(c.montantFinance, c.devise)}
                      </td>

                      {/* Score & Catégorie */}
                      <td className="whitespace-nowrap p-3.5 text-center">
                        {getScoreBadge(c.scoreValeur, c.scoreCategorie)}
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
