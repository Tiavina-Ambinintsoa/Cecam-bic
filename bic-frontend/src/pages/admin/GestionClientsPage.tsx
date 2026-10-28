import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { clientApi, type PageResponse } from "@/api/clientApi";
import { contratApi } from "@/api/contratApi";
import type { ClientResponse, ClientRequest } from "@/types/client";
import type { ContratResponse } from "@/types/contrat";
import {
  Search,
  Trash2,
  ChevronLeft,
  ChevronRight,
  Users,
  Eye,
  Pencil,
  X,
  CheckCircle2,
  AlertCircle,
  Phone,
  Calendar,
  Building,
  CreditCard,
  FileText,
  Clock,
  PlusCircle,
  RefreshCw,
  FolderOpen,
} from "lucide-react";

/**
 * Page d'administration : Répertoire centralisé des sociétaires CECAM
 * et Gestion CRUD complète de l'historique des contrats associés.
 */
export function GestionClientsPage() {
  const [clients, setClients] = useState<PageResponse<ClientResponse> | null>(null);
  const [page, setPage] = useState(0);
  const [recherche, setRecherche] = useState("");
  const [rechercheActive, setRechercheActive] = useState("");
  const [chargement, setChargement] = useState(false);
  const [erreur, setErreur] = useState<string | null>(null);
  const [succes, setSucces] = useState<string | null>(null);
  const [suppressionEnCours, setSuppressionEnCours] = useState<number | null>(null);

  // Modale de visualisation / édition sociétaire & contrats
  const [clientSelectionne, setClientSelectionne] = useState<ClientResponse | null>(null);
  const [ongletModal, setOngletModal] = useState<"fiche" | "contrats">("fiche");
  const [modeEdition, setModeEdition] = useState(false);
  const [sauvegardeEnCours, setSauvegardeEnCours] = useState(false);

  // Contrats du client sélectionné
  const [contratsClient, setContratsClient] = useState<ContratResponse[]>([]);
  const [chargementContrats, setChargementContrats] = useState(false);
  const [suppressionContratId, setSuppressionContratId] = useState<number | null>(null);

  // Mini-modale de changement de phase
  const [modalPhase, setModalPhase] = useState<{
    ouvert: boolean;
    contratId: number | null;
    codeContrat: string;
    phase: string;
    motif: string;
  }>({
    ouvert: false,
    contratId: null,
    codeContrat: "",
    phase: "ACTIF",
    motif: "",
  });

  // Formulaire d'édition rapide client
  const [editForm, setEditForm] = useState<{
    nom: string;
    prenom: string;
    telephone: string;
    codeClientEtablissement: string;
    etatCivil: string;
  }>({
    nom: "",
    prenom: "",
    telephone: "",
    codeClientEtablissement: "",
    etatCivil: "",
  });

  const charger = useCallback(async () => {
    setChargement(true);
    setErreur(null);
    try {
      const data = await clientApi.lister(page, 15, rechercheActive || undefined);
      setClients(data);
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : "Erreur lors du chargement des clients.";
      setErreur(msg);
    } finally {
      setChargement(false);
    }
  }, [page, rechercheActive]);

  useEffect(() => {
    charger();
  }, [charger]);

  const chargerContrats = useCallback(async (cId: number) => {
    setChargementContrats(true);
    try {
      const liste = await contratApi.listerParClient(cId);
      setContratsClient(liste);
    } catch {
      setContratsClient([]);
    } finally {
      setChargementContrats(false);
    }
  }, []);

  const lancerRecherche = () => {
    setPage(0);
    setRechercheActive(recherche);
  };

  const ouvrirDetails = (c: ClientResponse, edition = false, onglet: "fiche" | "contrats" = "fiche") => {
    setClientSelectionne(c);
    setModeEdition(edition);
    setOngletModal(onglet);
    setEditForm({
      nom: c.nom ?? "",
      prenom: c.prenom ?? "",
      telephone: c.telephone ?? "",
      codeClientEtablissement: c.codeClientEtablissement ?? "",
      etatCivil: c.etatCivil ?? "",
    });
    chargerContrats(c.id);
  };

  const fermerModal = () => {
    setClientSelectionne(null);
    setModeEdition(false);
    setContratsClient([]);
  };

  const sauvegarderModification = async () => {
    if (!clientSelectionne) return;
    setSauvegardeEnCours(true);
    setErreur(null);
    try {
      const payload: ClientRequest = {
        titre: clientSelectionne.titre,
        categorieTiersCode: clientSelectionne.categorieTiersCode,
        prenom: editForm.prenom.trim(),
        deuxiemePrenom: clientSelectionne.deuxiemePrenom,
        nom: editForm.nom.trim(),
        dateNaissance: clientSelectionne.dateNaissance,
        villeNaissance: clientSelectionne.villeNaissance,
        paysNaissance: clientSelectionne.paysNaissance,
        genre: clientSelectionne.genre,
        nationalite: clientSelectionne.nationalite,
        etatCivil: editForm.etatCivil || undefined,
        telephone: editForm.telephone.trim() || undefined,
        codeClientEtablissement: editForm.codeClientEtablissement.trim() || undefined,
        dateAdhesion: clientSelectionne.dateAdhesion,
        adresses: clientSelectionne.adresses ?? [],
        identifiants: clientSelectionne.identifiants ?? [],
      };

      await clientApi.mettreAJour(clientSelectionne.id, payload);
      setSucces(`Le sociétaire ${payload.nom} a été mis à jour avec succès.`);
      fermerModal();
      await charger();
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : "Erreur lors de la mise à jour.";
      setErreur(msg);
    } finally {
      setSauvegardeEnCours(false);
    }
  };

  const supprimerClient = async (id: number, nomComplet: string) => {
    if (
      !confirm(
        `Êtes-vous sûr de vouloir supprimer définitivement le sociétaire « ${nomComplet} » ?\nCette opération est irréversible.`
      )
    ) {
      return;
    }
    setSuppressionEnCours(id);
    setErreur(null);
    try {
      await clientApi.supprimer(id);
      setSucces(`Le sociétaire ${nomComplet} a été supprimé.`);
      await charger();
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : "Erreur lors de la suppression.";
      if (msg.includes("409") || msg.includes("actif")) {
        setErreur("Impossible de supprimer ce client : un contrat de crédit est actuellement actif.");
      } else {
        setErreur(msg);
      }
    } finally {
      setSuppressionEnCours(null);
    }
  };

  const supprimerContrat = async (contratId: number, codeContrat: string) => {
    if (
      !confirm(
        `Êtes-vous sûr de vouloir supprimer définitivement le contrat ${codeContrat} et tout son historique ?\nCette action est irréversible.`
      )
    ) {
      return;
    }
    setSuppressionContratId(contratId);
    try {
      await contratApi.supprimer(contratId);
      setSucces(`Contrat ${codeContrat} supprimé avec succès.`);
      if (clientSelectionne) {
        await chargerContrats(clientSelectionne.id);
      }
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : "Erreur lors de la suppression du contrat.";
      setErreur(msg);
    } finally {
      setSuppressionContratId(null);
    }
  };

  const validerChangementPhase = async () => {
    if (!modalPhase.contratId) return;
    try {
      await contratApi.changerPhase(
        modalPhase.contratId,
        modalPhase.phase,
        modalPhase.phase === "FERME" ? modalPhase.motif : undefined
      );
      setSucces(`Statut du contrat mis à jour vers ${modalPhase.phase}.`);
      setModalPhase({ ouvert: false, contratId: null, codeContrat: "", phase: "ACTIF", motif: "" });
      if (clientSelectionne) {
        await chargerContrats(clientSelectionne.id);
      }
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : "Erreur lors du changement de phase.";
      setErreur(msg);
    }
  };

  const totalPages = clients?.totalPages ?? 0;

  const badgePhase = (phase: string) => {
    switch (phase) {
      case "ACTIF":
        return <span className="inline-flex items-center rounded-full bg-emerald-50 px-2.5 py-0.5 text-xs font-semibold text-emerald-700 border border-emerald-200">Actif</span>;
      case "DEMANDE_EN_COURS":
        return <span className="inline-flex items-center rounded-full bg-amber-50 px-2.5 py-0.5 text-xs font-semibold text-amber-700 border border-amber-200">Demande en cours</span>;
      case "FERME":
        return <span className="inline-flex items-center rounded-full bg-slate-100 px-2.5 py-0.5 text-xs font-semibold text-slate-700 border border-slate-300">Fermé</span>;
      case "REFUSE":
        return <span className="inline-flex items-center rounded-full bg-red-50 px-2.5 py-0.5 text-xs font-semibold text-red-700 border border-red-200">Refusé</span>;
      case "ABANDONNE":
        return <span className="inline-flex items-center rounded-full bg-orange-50 px-2.5 py-0.5 text-xs font-semibold text-orange-700 border border-orange-200">Abandonné</span>;
      default:
        return <span className="inline-flex items-center rounded-full bg-slate-50 px-2.5 py-0.5 text-xs font-medium text-slate-600">{phase}</span>;
    }
  };

  return (
    <div className="mx-auto w-full max-w-7xl px-4 sm:px-6 py-8">
      {/* En-tête */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
        <div>
          <div className="flex items-center gap-3">
            <div className="rounded-lg bg-primary/10 p-2 text-primary">
              <Users className="size-6" />
            </div>
            <div>
              <h1 className="font-heading text-2xl font-bold text-foreground">
                Gestion des sociétaires (Clients)
              </h1>
              <p className="text-sm text-muted-foreground mt-0.5">
                Répertoire centralisé des adhérents CECAM, suivi des dossiers et historique des crédits.
              </p>
            </div>
          </div>
        </div>
        <div className="flex items-center gap-2">
          <Link
            to="/admin/saisie-historique"
            className="inline-flex items-center justify-center gap-2 rounded-lg border border-border bg-white px-3.5 py-2 text-sm font-medium text-foreground hover:bg-slate-50 transition-colors shadow-xs"
          >
            <Clock className="size-4 text-indigo-600" />
            Saisie historique
          </Link>
          <Link
            to="/demande/nouvelle/individu"
            className="inline-flex items-center justify-center gap-2 rounded-lg bg-primary px-4 py-2 text-sm font-medium text-primary-foreground hover:bg-primary/90 transition-colors shadow-xs"
          >
            <PlusCircle className="size-4" />
            Nouveau sociétaire
          </Link>
        </div>
      </div>

      {/* Messages de retour */}
      {succes && (
        <div className="mb-4 flex items-center justify-between rounded-lg border border-emerald-200 bg-emerald-50 p-3 text-sm text-emerald-800">
          <div className="flex items-center gap-2">
            <CheckCircle2 className="size-4 shrink-0" />
            <span>{succes}</span>
          </div>
          <button onClick={() => setSucces(null)} className="text-emerald-700 hover:text-emerald-900">
            <X className="size-4" />
          </button>
        </div>
      )}
      {erreur && (
        <div className="mb-4 flex items-center justify-between rounded-lg border border-red-200 bg-red-50 p-3 text-sm text-red-800">
          <div className="flex items-center gap-2">
            <AlertCircle className="size-4 shrink-0" />
            <span>{erreur}</span>
          </div>
          <button onClick={() => setErreur(null)} className="text-red-700 hover:text-red-900">
            <X className="size-4" />
          </button>
        </div>
      )}

      {/* Barre de recherche */}
      <div className="mb-6 flex flex-col sm:flex-row items-stretch sm:items-center gap-3">
        <div className="relative flex-1">
          <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 size-4 text-muted-foreground" />
          <input
            type="text"
            placeholder="Rechercher par nom, prénom, Code Client CB ou Code CECAM…"
            className="w-full rounded-lg border border-border bg-background py-2.5 pl-10 pr-4 text-sm focus:outline-none focus:ring-2 focus:ring-primary/20"
            value={recherche}
            onChange={(e) => setRecherche(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && lancerRecherche()}
          />
        </div>
        <button
          onClick={lancerRecherche}
          className="rounded-lg bg-secondary px-5 py-2.5 text-sm font-medium text-foreground hover:bg-secondary/80 transition-colors"
        >
          Filtrer
        </button>
        {rechercheActive && (
          <button
            onClick={() => {
              setRecherche("");
              setRechercheActive("");
              setPage(0);
            }}
            className="text-xs text-muted-foreground hover:text-foreground underline px-2 py-2"
          >
            Réinitialiser
          </button>
        )}
      </div>

      {/* Tableau principal des sociétaires */}
      <div className="overflow-hidden rounded-xl border border-border bg-white shadow-sm">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-border bg-slate-50/80 text-left text-xs font-semibold text-muted-foreground uppercase tracking-wider">
                <th className="px-4 py-3.5">Code CB</th>
                <th className="px-4 py-3.5">Code CECAM</th>
                <th className="px-4 py-3.5">Sociétaire</th>
                <th className="px-4 py-3.5">Genre</th>
                <th className="px-4 py-3.5">Téléphone</th>
                <th className="px-4 py-3.5">Adhésion</th>
                <th className="px-4 py-3.5 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {chargement ? (
                <tr>
                  <td colSpan={7} className="px-4 py-12 text-center text-muted-foreground">
                    Chargement des sociétaires…
                  </td>
                </tr>
              ) : !clients?.content.length ? (
                <tr>
                  <td colSpan={7} className="px-4 py-12 text-center text-muted-foreground">
                    Aucun sociétaire ne correspond aux critères.
                  </td>
                </tr>
              ) : (
                clients.content.map((c) => (
                  <tr key={c.id} className="hover:bg-slate-50/60 transition-colors">
                    <td className="px-4 py-3 font-mono text-xs font-semibold text-primary">
                      {c.codeClientCb}
                    </td>
                    <td className="px-4 py-3 text-xs text-muted-foreground">
                      {c.codeClientEtablissement ? (
                        <span className="inline-flex items-center gap-1 font-mono text-foreground">
                          <Building className="size-3 text-slate-400" />
                          {c.codeClientEtablissement}
                        </span>
                      ) : (
                        <span className="text-slate-300">-</span>
                      )}
                    </td>
                    <td className="px-4 py-3 font-medium text-foreground">
                      {c.nom?.toUpperCase()} {c.prenom}
                    </td>
                    <td className="px-4 py-3 text-xs">
                      <span
                        className={`inline-block px-2 py-0.5 rounded-full font-medium ${
                          c.genre === "HOMME"
                            ? "bg-blue-50 text-blue-700"
                            : "bg-rose-50 text-rose-700"
                        }`}
                      >
                        {c.genre === "HOMME" ? "Homme" : "Femme"}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-xs text-muted-foreground">
                      {c.telephone ? (
                        <span className="inline-flex items-center gap-1">
                          <Phone className="size-3 text-slate-400" />
                          {c.telephone}
                        </span>
                      ) : (
                        <span className="text-slate-300">-</span>
                      )}
                    </td>
                    <td className="px-4 py-3 text-xs text-muted-foreground">
                      {c.dateAdhesion ? (
                        <span className="inline-flex items-center gap-1">
                          <Calendar className="size-3 text-slate-400" />
                          {c.dateAdhesion}
                        </span>
                      ) : (
                        <span className="text-slate-300">-</span>
                      )}
                    </td>
                    <td className="px-4 py-3 text-right">
                      <div className="inline-flex items-center gap-1">
                        <button
                          onClick={() => ouvrirDetails(c, false, "fiche")}
                          className="inline-flex items-center gap-1 rounded-md px-2 py-1 text-xs font-medium text-slate-600 hover:bg-slate-100 transition-colors"
                          title="Voir la fiche détaillée"
                        >
                          <Eye className="size-3.5" />
                          <span className="hidden md:inline">Voir</span>
                        </button>
                        <button
                          onClick={() => ouvrirDetails(c, false, "contrats")}
                          className="inline-flex items-center gap-1 rounded-md px-2 py-1 text-xs font-semibold text-indigo-700 bg-indigo-50 hover:bg-indigo-100 transition-colors"
                          title="Gérer l'historique des contrats"
                        >
                          <Clock className="size-3.5" />
                          <span>Contrats</span>
                        </button>
                        <button
                          onClick={() => ouvrirDetails(c, true, "fiche")}
                          className="inline-flex items-center gap-1 rounded-md px-2 py-1 text-xs font-medium text-slate-600 hover:bg-slate-100 transition-colors"
                          title="Modifier les coordonnées"
                        >
                          <Pencil className="size-3.5" />
                          <span className="hidden lg:inline">Éditer</span>
                        </button>
                        <Link
                          to={`/demande/nouvelle/contrat/${c.id}`}
                          className="inline-flex items-center gap-1 rounded-md px-2 py-1 text-xs font-medium text-emerald-700 hover:bg-emerald-50 transition-colors"
                          title="Ouvrir un nouveau dossier crédit"
                        >
                          <CreditCard className="size-3.5" />
                          <span className="hidden lg:inline">+ Crédit</span>
                        </Link>
                        <button
                          onClick={() => supprimerClient(c.id, `${c.nom} ${c.prenom}`)}
                          disabled={suppressionEnCours === c.id}
                          className="inline-flex items-center gap-1 rounded-md px-2 py-1 text-xs font-medium text-red-600 hover:bg-red-50 transition-colors disabled:opacity-40"
                          title="Supprimer définitivement le sociétaire"
                        >
                          <Trash2 className="size-3.5" />
                          <span className="hidden xl:inline">
                            {suppressionEnCours === c.id ? "…" : "Suppr."}
                          </span>
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Footer pagination */}
        <div className="flex items-center justify-between border-t border-border px-4 py-3 bg-slate-50/50">
          <p className="text-xs text-muted-foreground">
            {clients ? (
              <>
                Total : <strong className="text-foreground">{clients.totalElements}</strong> sociétaire(s)
                {totalPages > 1 && ` — Page ${page + 1} sur ${totalPages}`}
              </>
            ) : null}
          </p>
          {totalPages > 1 && (
            <div className="flex items-center gap-2">
              <button
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                disabled={page === 0}
                className="inline-flex items-center gap-1 rounded-lg border border-border bg-white px-2.5 py-1 text-xs font-medium text-foreground hover:bg-slate-50 disabled:opacity-40 transition-colors"
              >
                <ChevronLeft className="size-3.5" /> Précédent
              </button>
              <button
                onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                disabled={page >= totalPages - 1}
                className="inline-flex items-center gap-1 rounded-lg border border-border bg-white px-2.5 py-1 text-xs font-medium text-foreground hover:bg-slate-50 disabled:opacity-40 transition-colors"
              >
                Suivant <ChevronRight className="size-3.5" />
              </button>
            </div>
          )}
        </div>
      </div>

      {/* MODALE PRINCIPALE : Fiche + Historique des contrats */}
      {clientSelectionne && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4 backdrop-blur-xs">
          <div className="w-full max-w-4xl rounded-xl border border-border bg-white shadow-2xl overflow-hidden animate-in fade-in zoom-in-95 duration-150">
            {/* Header de la modale */}
            <div className="flex items-center justify-between border-b border-border px-6 py-4 bg-slate-50">
              <div className="flex items-center gap-3">
                <div className="rounded-lg bg-primary/10 p-2 text-primary">
                  <Users className="size-5" />
                </div>
                <div>
                  <h3 className="font-heading text-lg font-bold text-foreground">
                    {clientSelectionne.nom?.toUpperCase()} {clientSelectionne.prenom}
                  </h3>
                  <div className="flex items-center gap-2 mt-0.5 text-xs text-muted-foreground">
                    <span className="font-mono bg-slate-200/70 text-slate-800 px-1.5 py-0.5 rounded font-semibold">
                      CB: {clientSelectionne.codeClientCb}
                    </span>
                    {clientSelectionne.codeClientEtablissement && (
                      <span className="font-mono bg-slate-100 text-slate-700 px-1.5 py-0.5 rounded">
                        CECAM: {clientSelectionne.codeClientEtablissement}
                      </span>
                    )}
                  </div>
                </div>
              </div>

              {/* Onglets Fiche / Contrats */}
              <div className="flex items-center gap-1 rounded-lg bg-slate-200/60 p-1">
                <button
                  type="button"
                  onClick={() => setOngletModal("fiche")}
                  className={`rounded-md px-3 py-1.5 text-xs font-semibold transition-all ${
                    ongletModal === "fiche"
                      ? "bg-white text-foreground shadow-xs"
                      : "text-muted-foreground hover:text-foreground"
                  }`}
                >
                  Fiche sociétaire
                </button>
                <button
                  type="button"
                  onClick={() => setOngletModal("contrats")}
                  className={`inline-flex items-center gap-1.5 rounded-md px-3 py-1.5 text-xs font-semibold transition-all ${
                    ongletModal === "contrats"
                      ? "bg-white text-indigo-700 shadow-xs"
                      : "text-muted-foreground hover:text-foreground"
                  }`}
                >
                  <Clock className="size-3.5" />
                  <span>Dossiers & Contrats ({contratsClient.length})</span>
                </button>
              </div>

              <button
                onClick={fermerModal}
                className="rounded-md p-1.5 text-muted-foreground hover:bg-slate-200 hover:text-foreground transition-colors"
              >
                <X className="size-5" />
              </button>
            </div>

            {/* Corps de la modale */}
            <div className="p-6 max-h-[75vh] overflow-y-auto">
              {ongletModal === "fiche" ? (
                /* ONGLET 1 : FICHE SOCIÉTAIRE */
                <div className="space-y-4">
                  {modeEdition ? (
                    <div className="space-y-4">
                      <div className="grid grid-cols-2 gap-3">
                        <div>
                          <label className="text-xs font-medium text-slate-700">Nom *</label>
                          <input
                            type="text"
                            className="mt-1 w-full rounded-lg border border-border p-2 text-sm focus:ring-2 focus:ring-primary/20"
                            value={editForm.nom}
                            onChange={(e) => setEditForm({ ...editForm, nom: e.target.value })}
                          />
                        </div>
                        <div>
                          <label className="text-xs font-medium text-slate-700">Prénom *</label>
                          <input
                            type="text"
                            className="mt-1 w-full rounded-lg border border-border p-2 text-sm focus:ring-2 focus:ring-primary/20"
                            value={editForm.prenom}
                            onChange={(e) => setEditForm({ ...editForm, prenom: e.target.value })}
                          />
                        </div>
                      </div>

                      <div className="grid grid-cols-2 gap-3">
                        <div>
                          <label className="text-xs font-medium text-slate-700">Téléphone</label>
                          <input
                            type="text"
                            className="mt-1 w-full rounded-lg border border-border p-2 text-sm focus:ring-2 focus:ring-primary/20"
                            value={editForm.telephone}
                            onChange={(e) => setEditForm({ ...editForm, telephone: e.target.value })}
                          />
                        </div>
                        <div>
                          <label className="text-xs font-medium text-slate-700">Code Client CECAM</label>
                          <input
                            type="text"
                            className="mt-1 w-full rounded-lg border border-border p-2 text-sm focus:ring-2 focus:ring-primary/20"
                            value={editForm.codeClientEtablissement}
                            onChange={(e) =>
                              setEditForm({ ...editForm, codeClientEtablissement: e.target.value })
                            }
                          />
                        </div>
                      </div>

                      <div>
                        <label className="text-xs font-medium text-slate-700">État civil</label>
                        <select
                          className="mt-1 w-full rounded-lg border border-border p-2 text-sm focus:ring-2 focus:ring-primary/20 bg-white"
                          value={editForm.etatCivil}
                          onChange={(e) => setEditForm({ ...editForm, etatCivil: e.target.value })}
                        >
                          <option value="">Non renseigné</option>
                          <option value="Célibataire">Célibataire</option>
                          <option value="Marié(e)">Marié(e)</option>
                          <option value="Veuf/Veuve">Veuf/Veuve</option>
                          <option value="Divorcé(e)">Divorcé(e)</option>
                        </select>
                      </div>
                    </div>
                  ) : (
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-4 text-sm bg-slate-50/60 p-4 rounded-lg border border-slate-100">
                      <div>
                        <span className="text-xs text-muted-foreground block">Nom & Prénom</span>
                        <span className="font-semibold text-foreground">
                          {clientSelectionne.nom?.toUpperCase()} {clientSelectionne.prenom}
                        </span>
                      </div>
                      <div>
                        <span className="text-xs text-muted-foreground block">Genre & Nationalité</span>
                        <span className="text-foreground">
                          {clientSelectionne.genre === "HOMME" ? "Homme" : "Femme"} • {clientSelectionne.nationalite}
                        </span>
                      </div>
                      <div>
                        <span className="text-xs text-muted-foreground block">Date de naissance</span>
                        <span className="text-foreground">{clientSelectionne.dateNaissance ?? "-"}</span>
                      </div>
                      <div>
                        <span className="text-xs text-muted-foreground block">Lieu de naissance</span>
                        <span className="text-foreground">
                          {clientSelectionne.villeNaissance
                            ? `${clientSelectionne.villeNaissance} (${clientSelectionne.paysNaissance ?? "Madagascar"})`
                            : "-"}
                        </span>
                      </div>
                      <div>
                        <span className="text-xs text-muted-foreground block">Téléphone</span>
                        <span className="text-foreground">{clientSelectionne.telephone ?? "-"}</span>
                      </div>
                      <div>
                        <span className="text-xs text-muted-foreground block">État civil</span>
                        <span className="text-foreground">{clientSelectionne.etatCivil ?? "-"}</span>
                      </div>
                      <div>
                        <span className="text-xs text-muted-foreground block">Date d'adhésion CECAM</span>
                        <span className="text-foreground">{clientSelectionne.dateAdhesion ?? "-"}</span>
                      </div>
                      <div>
                        <span className="text-xs text-muted-foreground block">Catégorie Tiers</span>
                        <span className="font-mono text-xs text-foreground">
                          {clientSelectionne.categorieTiersCode ?? "-"}
                        </span>
                      </div>
                    </div>
                  )}
                </div>
              ) : (
                /* ONGLET 2 : GESTION DES DOSSIERS & HISTORIQUE DES CONTRATS */
                <div className="space-y-4">
                  {/* Actions haut de l'onglet contrats */}
                  <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 pb-3 border-b">
                    <div>
                      <h4 className="text-sm font-semibold text-foreground">
                        Historique des contrats & dossiers de crédit
                      </h4>
                      <p className="text-xs text-muted-foreground">
                        Antécédents déclarés, contrats actifs et historiques de paiement pour ce sociétaire.
                      </p>
                    </div>
                    <div className="flex items-center gap-2">
                      <Link
                        to={`/admin/saisie-historique?clientId=${clientSelectionne.id}`}
                        className="inline-flex items-center gap-1.5 rounded-lg bg-indigo-600 px-3 py-1.5 text-xs font-semibold text-white hover:bg-indigo-700 transition-colors shadow-xs"
                      >
                        <PlusCircle className="size-3.5" />
                        + Contrat historique
                      </Link>
                      <Link
                        to={`/demande/nouvelle/contrat/${clientSelectionne.id}`}
                        className="inline-flex items-center gap-1.5 rounded-lg bg-emerald-600 px-3 py-1.5 text-xs font-semibold text-white hover:bg-emerald-700 transition-colors shadow-xs"
                      >
                        <CreditCard className="size-3.5" />
                        + Nouveau crédit
                      </Link>
                      <button
                        onClick={() => clientSelectionne && chargerContrats(clientSelectionne.id)}
                        className="p-1.5 text-slate-500 hover:text-slate-900 rounded border hover:bg-slate-50"
                        title="Rafraîchir les contrats"
                      >
                        <RefreshCw className="size-3.5" />
                      </button>
                    </div>
                  </div>

                  {chargementContrats ? (
                    <div className="py-12 text-center text-xs text-muted-foreground">
                      Chargement de l'historique des contrats…
                    </div>
                  ) : contratsClient.length === 0 ? (
                    <div className="rounded-lg border border-dashed border-slate-200 p-8 text-center">
                      <FolderOpen className="mx-auto size-8 text-slate-300" />
                      <p className="mt-2 text-sm font-medium text-slate-700">
                        Aucun contrat enregistré pour ce sociétaire.
                      </p>
                      <p className="text-xs text-muted-foreground mt-0.5">
                        Vous pouvez ajouter un contrat historique ou enregistrer un nouveau crédit CECAM.
                      </p>
                      <div className="mt-4 flex justify-center gap-3">
                        <Link
                          to={`/admin/saisie-historique?clientId=${clientSelectionne.id}`}
                          className="inline-flex items-center gap-1.5 rounded-lg border border-indigo-200 bg-indigo-50 px-3 py-1.5 text-xs font-semibold text-indigo-700 hover:bg-indigo-100"
                        >
                          <PlusCircle className="size-3.5" />
                          Ajouter un contrat historique
                        </Link>
                      </div>
                    </div>
                  ) : (
                    <div className="space-y-3">
                      {contratsClient.map((c) => (
                        <div
                          key={c.id}
                          className="flex flex-col md:flex-row md:items-center justify-between gap-3 p-4 rounded-xl border border-slate-200 bg-slate-50/40 hover:bg-slate-50 hover:border-slate-300 transition-all shadow-2xs"
                        >
                          <div className="space-y-1.5">
                            <div className="flex flex-wrap items-center gap-2">
                              <span className="font-mono text-xs font-bold text-primary">
                                {c.codeContratCb}
                              </span>
                              {c.codeContratEtablissement && (
                                <span className="font-mono text-xs bg-slate-200/70 text-slate-700 px-1.5 py-0.5 rounded">
                                  {c.codeContratEtablissement}
                                </span>
                              )}
                              {badgePhase(c.phaseDemande)}
                              {c.scoreValeur != null && (
                                <span className="text-xs font-bold bg-slate-900 text-white px-2 py-0.5 rounded-md">
                                  Score : {c.scoreValeur} ({c.scoreCategorie ?? "N/A"})
                                </span>
                              )}
                            </div>

                            <div className="flex flex-wrap items-center gap-x-4 gap-y-1 text-xs text-muted-foreground">
                              <span>
                                Type : <strong className="text-foreground">{c.typeContrat}</strong>
                              </span>
                              <span>
                                Montant :{" "}
                                <strong className="text-foreground">
                                  {c.montantFinance?.toLocaleString()} {c.devise ?? "MGA"}
                                </strong>
                              </span>
                              {c.montantRestantDu != null && (
                                <span>
                                  Restant :{" "}
                                  <strong className="text-foreground">
                                    {c.montantRestantDu.toLocaleString()} {c.devise ?? "MGA"}
                                  </strong>
                                </span>
                              )}
                              {c.nombreJoursRetard != null && c.nombreJoursRetard > 0 && (
                                <span className="text-red-600 font-semibold">
                                  Retard : {c.nombreJoursRetard} j
                                </span>
                              )}
                              {c.dateDemande && <span>Date : {c.dateDemande}</span>}
                            </div>
                          </div>

                          {/* Boutons actions du contrat */}
                          <div className="flex items-center gap-1.5 self-end md:self-center shrink-0">
                            <Link
                              to={`/demande/rapport/${c.id}`}
                              className="inline-flex items-center gap-1 rounded-lg border border-slate-200 bg-white px-2.5 py-1.5 text-xs font-medium text-slate-700 hover:bg-slate-100 transition-colors"
                              title="Consulter le rapport de scoring et solvabilité"
                            >
                              <FileText className="size-3.5 text-primary" />
                              <span>Rapport</span>
                            </Link>

                            <Link
                              to={`/admin/saisie-historique?contratId=${c.id}&clientId=${clientSelectionne.id}`}
                              className="inline-flex items-center gap-1 rounded-lg border border-indigo-200 bg-indigo-50 px-2.5 py-1.5 text-xs font-semibold text-indigo-700 hover:bg-indigo-100 transition-colors"
                              title="Modifier l'historique de ce contrat"
                            >
                              <Pencil className="size-3.5" />
                              <span>Modifier</span>
                            </Link>

                            <button
                              onClick={() =>
                                setModalPhase({
                                  ouvert: true,
                                  contratId: c.id,
                                  codeContrat: c.codeContratCb,
                                  phase: c.phaseDemande,
                                  motif: c.motifCloture ?? "",
                                })
                              }
                              className="inline-flex items-center gap-1 rounded-lg border border-slate-200 bg-white px-2.5 py-1.5 text-xs font-medium text-slate-700 hover:bg-slate-100 transition-colors"
                              title="Changer la phase du contrat"
                            >
                              <RefreshCw className="size-3.5 text-slate-500" />
                              <span>Phase</span>
                            </button>

                            <button
                              onClick={() => supprimerContrat(c.id, c.codeContratCb)}
                              disabled={suppressionContratId === c.id}
                              className="inline-flex items-center gap-1 rounded-lg border border-red-200 bg-red-50 px-2.5 py-1.5 text-xs font-semibold text-red-600 hover:bg-red-100 transition-colors disabled:opacity-40"
                              title="Supprimer définitivement ce contrat et son historique"
                            >
                              <Trash2 className="size-3.5" />
                              <span>{suppressionContratId === c.id ? "…" : "Suppr."}</span>
                            </button>
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              )}
            </div>

            {/* Footer de la modale */}
            <div className="flex items-center justify-between border-t border-border px-6 py-4 bg-slate-50">
              {ongletModal === "fiche" && modeEdition ? (
                <>
                  <button
                    onClick={() => setModeEdition(false)}
                    className="text-xs font-medium text-slate-700 bg-white border border-border px-3 py-2 rounded-lg hover:bg-slate-50 transition-colors"
                  >
                    Annuler
                  </button>
                  <button
                    onClick={sauvegarderModification}
                    disabled={sauvegardeEnCours || !editForm.nom.trim() || !editForm.prenom.trim()}
                    className="inline-flex items-center gap-1.5 text-xs font-semibold text-white bg-primary px-4 py-2 rounded-lg hover:bg-primary/90 transition-colors disabled:opacity-50"
                  >
                    {sauvegardeEnCours ? "Enregistrement…" : "Enregistrer les modifications"}
                  </button>
                </>
              ) : (
                <>
                  <div className="text-xs text-muted-foreground">
                    Sociétaire actif dans le système Scoring CECAM
                  </div>
                  <div className="flex items-center gap-2">
                    {ongletModal === "fiche" && !modeEdition && (
                      <button
                        onClick={() => setModeEdition(true)}
                        className="inline-flex items-center gap-1.5 text-xs font-medium text-primary border border-primary/20 bg-primary/5 px-3 py-2 rounded-lg hover:bg-primary/10 transition-colors"
                      >
                        <Pencil className="size-3.5" />
                        Modifier la fiche
                      </button>
                    )}
                    <button
                      onClick={fermerModal}
                      className="text-xs font-medium text-slate-700 bg-white border border-border px-4 py-2 rounded-lg hover:bg-slate-50 transition-colors"
                    >
                      Fermer
                    </button>
                  </div>
                </>
              )}
            </div>
          </div>
        </div>
      )}

      {/* MODALE DIALOGUE : Changement de phase du contrat */}
      {modalPhase.ouvert && (
        <div className="fixed inset-0 z-60 flex items-center justify-center bg-black/60 p-4 backdrop-blur-xs">
          <div className="w-full max-w-md rounded-xl border border-border bg-white shadow-2xl p-6 space-y-4 animate-in fade-in zoom-in-95 duration-100">
            <div>
              <h4 className="font-heading text-base font-bold text-foreground">
                Changer la phase du contrat
              </h4>
              <p className="text-xs text-muted-foreground mt-0.5">
                Contrat réf : <span className="font-mono font-semibold">{modalPhase.codeContrat}</span>
              </p>
            </div>

            <div className="space-y-3">
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">
                  Nouvelle phase réglementaire *
                </label>
                <select
                  className="w-full rounded-lg border border-border bg-white p-2 text-sm focus:ring-2 focus:ring-primary/20"
                  value={modalPhase.phase}
                  onChange={(e) => setModalPhase({ ...modalPhase, phase: e.target.value })}
                >
                  <option value="DEMANDE_EN_COURS">Demande en cours</option>
                  <option value="ACTIF">Actif (Décaissement validé)</option>
                  <option value="FERME">Fermé (Clôture)</option>
                  <option value="REFUSE">Refusé</option>
                  <option value="ABANDONNE">Abandonné</option>
                </select>
              </div>

              {modalPhase.phase === "FERME" && (
                <div>
                  <label className="text-xs font-semibold text-slate-700 block mb-1">
                    Motif de clôture
                  </label>
                  <select
                    className="w-full rounded-lg border border-border bg-white p-2 text-sm focus:ring-2 focus:ring-primary/20"
                    value={modalPhase.motif}
                    onChange={(e) => setModalPhase({ ...modalPhase, motif: e.target.value })}
                  >
                    <option value="">Sélectionnez un motif…</option>
                    <option value="FIN_A_TERME">Fin à terme normale</option>
                    <option value="FIN_ANTICIPEE">Remboursement anticipé</option>
                  </select>
                </div>
              )}
            </div>

            <div className="flex justify-end gap-2 pt-2 border-t">
              <button
                type="button"
                onClick={() =>
                  setModalPhase({ ouvert: false, contratId: null, codeContrat: "", phase: "ACTIF", motif: "" })
                }
                className="rounded-lg border border-border px-3 py-1.5 text-xs font-medium text-slate-700 hover:bg-slate-50"
              >
                Annuler
              </button>
              <button
                type="button"
                onClick={validerChangementPhase}
                className="rounded-lg bg-primary px-4 py-1.5 text-xs font-semibold text-white hover:bg-primary/90"
              >
                Valider la transition
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
