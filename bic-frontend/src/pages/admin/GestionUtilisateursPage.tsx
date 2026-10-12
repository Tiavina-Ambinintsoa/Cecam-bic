import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { useToast } from "@/hooks/use-toast";
import { useAuth } from "@/context/AuthContext";
import { utilisateurApi, type UtilisateurResponse, type UtilisateurRequest } from "@/api/utilisateurApi";
import { UserPlus, Pencil, Trash2, Shield, User, RefreshCw, Power } from "lucide-react";

export function GestionUtilisateursPage() {
  const { toast } = useToast();
  const { nomUtilisateur: currentUserName } = useAuth();

  const [utilisateurs, setUtilisateurs] = useState<UtilisateurResponse[]>([]);
  const [chargement, setChargement] = useState(true);

  // Formulaire d'ajout / modification
  const [formulaireOuvert, setFormulaireOuvert] = useState(false);
  const [utilisateurEnEdition, setUtilisateurEnEdition] = useState<UtilisateurResponse | null>(null);

  const [formNomUtilisateur, setFormNomUtilisateur] = useState("");
  const [formMotDePasse, setFormMotDePasse] = useState("");
  const [formRole, setFormRole] = useState<"ADMIN" | "AGENT_CREDIT">("AGENT_CREDIT");
  const [formActif, setFormActif] = useState(true);
  const [soumissionEnCours, setSoumissionEnCours] = useState(false);

  // Action en cours par ID d'utilisateur
  const [actionEnCoursId, setActionEnCoursId] = useState<number | null>(null);
  const [idASupprimer, setIdASupprimer] = useState<number | null>(null);

  async function chargerUtilisateurs() {
    setChargement(true);
    try {
      const data = await utilisateurApi.lister();
      setUtilisateurs(data);
    } catch {
      toast({ title: "Impossible de charger la liste des utilisateurs.", variant: "destructive" });
    } finally {
      setChargement(false);
    }
  }

  useEffect(() => {
    void chargerUtilisateurs();
  }, []);

  function ouvrirCreation() {
    setUtilisateurEnEdition(null);
    setFormNomUtilisateur("");
    setFormMotDePasse("");
    setFormRole("AGENT_CREDIT");
    setFormActif(true);
    setFormulaireOuvert(true);
  }

  function ouvrirEdition(user: UtilisateurResponse) {
    setUtilisateurEnEdition(user);
    setFormNomUtilisateur(user.nomUtilisateur);
    setFormMotDePasse(""); // Mot de passe facultatif en édition
    setFormRole(user.role);
    setFormActif(user.actif);
    setFormulaireOuvert(true);
  }

  function fermerFormulaire() {
    setFormulaireOuvert(false);
    setUtilisateurEnEdition(null);
    setFormNomUtilisateur("");
    setFormMotDePasse("");
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();

    const nomTrimmed = formNomUtilisateur.trim();
    if (!nomTrimmed) {
      toast({ title: "Le nom d'utilisateur est requis.", variant: "destructive" });
      return;
    }

    if (!utilisateurEnEdition && !formMotDePasse.trim()) {
      toast({ title: "Le mot de passe est obligatoire pour un nouvel utilisateur.", variant: "destructive" });
      return;
    }

    // Vérification préventive de doublon local
    const existeDeja = utilisateurs.some(
      (u) =>
        u.nomUtilisateur.toLowerCase() === nomTrimmed.toLowerCase() &&
        (!utilisateurEnEdition || u.id !== utilisateurEnEdition.id)
    );
    if (existeDeja) {
      toast({ title: "Ce nom d'utilisateur est déjà utilisé.", variant: "destructive" });
      return;
    }

    setSoumissionEnCours(true);
    try {
      const payload: UtilisateurRequest = {
        nomUtilisateur: nomTrimmed,
        role: formRole,
        actif: formActif,
      };

      if (formMotDePasse.trim()) {
        payload.motDePasse = formMotDePasse;
      }

      if (utilisateurEnEdition) {
        await utilisateurApi.modifier(utilisateurEnEdition.id, payload);
        toast({ title: "Utilisateur modifié avec succès." });
      } else {
        await utilisateurApi.creer(payload);
        toast({ title: "Utilisateur créé avec succès." });
      }

      fermerFormulaire();
      await chargerUtilisateurs();
    } catch (err: unknown) {
      const error = err as { response?: { status?: number; data?: { message?: string } } };
      if (
        error.response?.status === 409 ||
        (error.response?.status === 400 &&
          error.response?.data?.message?.toLowerCase().includes("existe"))
      ) {
        toast({ title: "Ce nom d'utilisateur est déjà utilisé.", variant: "destructive" });
      } else {
        toast({
          title: error.response?.data?.message || "Une erreur est survenue lors de l'enregistrement.",
          variant: "destructive",
        });
      }
    } finally {
      setSoumissionEnCours(false);
    }
  }

  async function handleToggleActif(user: UtilisateurResponse) {
    if (user.nomUtilisateur === currentUserName && user.actif) {
      toast({ title: "Vous ne pouvez pas désactiver votre propre compte.", variant: "destructive" });
      return;
    }

    setActionEnCoursId(user.id);
    try {
      await utilisateurApi.basculerActif(user.id);
      toast({ title: user.actif ? "Compte utilisateur désactivé." : "Compte utilisateur activé." });
      await chargerUtilisateurs();
    } catch {
      toast({ title: "Impossible de modifier le statut de l'utilisateur.", variant: "destructive" });
    } finally {
      setActionEnCoursId(null);
    }
  }

  async function handleSupprimer(user: UtilisateurResponse) {
    if (user.nomUtilisateur === currentUserName) {
      toast({ title: "Vous ne pouvez pas supprimer votre propre compte.", variant: "destructive" });
      setIdASupprimer(null);
      return;
    }

    setActionEnCoursId(user.id);
    try {
      await utilisateurApi.supprimer(user.id);
      toast({ title: "Utilisateur supprimé avec succès." });
      setIdASupprimer(null);
      await chargerUtilisateurs();
    } catch {
      toast({ title: "Impossible de supprimer l'utilisateur.", variant: "destructive" });
    } finally {
      setActionEnCoursId(null);
    }
  }

  return (
    <div className="mx-auto max-w-5xl space-y-6 p-6">
      {/* En-tête */}
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-2xl font-semibold">Gestion des utilisateurs</h1>
          <p className="mt-1 text-base text-muted-foreground">
            Créer, modifier ou désactiver les comptes agents et administrateurs.
          </p>
        </div>
        <div className="flex items-center gap-2">
          <Button variant="outline" onClick={() => void chargerUtilisateurs()} disabled={chargement}>
            <RefreshCw className={`size-4 ${chargement ? "animate-spin" : ""}`} />
            Actualiser
          </Button>
          <Button onClick={ouvrirCreation}>
            <UserPlus className="size-4" />
            Nouvel utilisateur
          </Button>
        </div>
      </div>

      {/* Formulaire Modal-like / Inline Card */}
      {formulaireOuvert && (
        <Card className="border-primary/20 bg-card shadow-sm">
          <CardHeader>
            <CardTitle>
              {utilisateurEnEdition
                ? `Modifier l'utilisateur : ${utilisateurEnEdition.nomUtilisateur}`
                : "Créer un nouvel utilisateur"}
            </CardTitle>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
                <div className="space-y-1.5">
                  <Label htmlFor="nomUtilisateur">
                    Nom d'utilisateur <span className="text-red-500">*</span>
                  </Label>
                  <Input
                    id="nomUtilisateur"
                    value={formNomUtilisateur}
                    onChange={(e) => setFormNomUtilisateur(e.target.value)}
                    placeholder="ex. jdupont"
                    required
                  />
                </div>

                <div className="space-y-1.5">
                  <Label htmlFor="motDePasse">
                    Mot de passe{" "}
                    {utilisateurEnEdition ? (
                      <span className="text-xs text-muted-foreground">(laisser vide pour conserver)</span>
                    ) : (
                      <span className="text-red-500">*</span>
                    )}
                  </Label>
                  <Input
                    id="motDePasse"
                    type="password"
                    value={formMotDePasse}
                    onChange={(e) => setFormMotDePasse(e.target.value)}
                    placeholder={utilisateurEnEdition ? "•••••••• (inchangé)" : "••••••••"}
                    required={!utilisateurEnEdition}
                  />
                </div>

                <div className="space-y-1.5">
                  <Label htmlFor="role">
                    Rôle <span className="text-red-500">*</span>
                  </Label>
                  <Select
                    value={formRole}
                    onValueChange={(val) => setFormRole((val as "ADMIN" | "AGENT_CREDIT") || "AGENT_CREDIT")}
                  >
                    <SelectTrigger id="role" className="w-full">
                      <SelectValue>
                        {formRole === "ADMIN" ? "Administrateur" : "Agent de crédit"}
                      </SelectValue>
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="AGENT_CREDIT">Agent de crédit</SelectItem>
                      <SelectItem value="ADMIN">Administrateur</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <div className="flex items-center space-x-3 pt-6">
                  <label htmlFor="actif-checkbox" className="flex cursor-pointer items-center gap-2 text-base font-medium">
                    <input
                      id="actif-checkbox"
                      type="checkbox"
                      checked={formActif}
                      onChange={(e) => setFormActif(e.target.checked)}
                      className="size-4 rounded border-gray-300 text-primary focus:ring-primary"
                    />
                    <span>Compte actif</span>
                  </label>
                </div>
              </div>

              <div className="flex justify-end gap-2 pt-2">
                <Button type="button" variant="outline" onClick={fermerFormulaire} disabled={soumissionEnCours}>
                  Annuler
                </Button>
                <Button type="submit" disabled={soumissionEnCours}>
                  {soumissionEnCours ? "Enregistrement..." : utilisateurEnEdition ? "Enregistrer" : "Créer"}
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>
      )}

      {/* Boîte de confirmation de suppression */}
      {idASupprimer !== null && (
        <Card className="border-red-200 bg-red-50/50">
          <CardContent className="flex flex-wrap items-center justify-between gap-4 py-4">
            <div>
              <p className="font-medium text-red-900">Confirmer la suppression</p>
              <p className="text-sm text-red-700">
                Êtes-vous sûr de vouloir supprimer l'utilisateur{" "}
                <strong>
                  {utilisateurs.find((u) => u.id === idASupprimer)?.nomUtilisateur}
                </strong>{" "}
                ? Cette action est irréversible.
              </p>
            </div>
            <div className="flex items-center gap-2">
              <Button
                variant="outline"
                size="sm"
                onClick={() => setIdASupprimer(null)}
                disabled={actionEnCoursId !== null}
              >
                Annuler
              </Button>
              <Button
                variant="destructive"
                size="sm"
                onClick={() => {
                  const user = utilisateurs.find((u) => u.id === idASupprimer);
                  if (user) void handleSupprimer(user);
                }}
                disabled={actionEnCoursId !== null}
              >
                {actionEnCoursId === idASupprimer ? "Suppression..." : "Confirmer la suppression"}
              </Button>
            </div>
          </CardContent>
        </Card>
      )}

      {/* Tableau des utilisateurs */}
      <Card>
        <CardHeader>
          <CardTitle>Comptes utilisateurs ({utilisateurs.length})</CardTitle>
        </CardHeader>
        <CardContent className="p-0">
          {chargement ? (
            <div className="py-12 text-center text-base text-muted-foreground">
              Chargement des utilisateurs...
            </div>
          ) : utilisateurs.length === 0 ? (
            <div className="py-12 text-center text-base text-muted-foreground">
              Aucun utilisateur enregistré.
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-sm">
                <thead className="border-b bg-slate-50 text-xs font-semibold text-slate-600">
                  <tr>
                    <th className="px-4 py-3">Nom d'utilisateur</th>
                    <th className="px-4 py-3">Rôle</th>
                    <th className="px-4 py-3">Statut</th>
                    <th className="px-4 py-3 text-right">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-border">
                  {utilisateurs.map((user) => {
                    const isSelf = user.nomUtilisateur === currentUserName;
                    const isActionEnCours = actionEnCoursId === user.id;

                    return (
                      <tr key={user.id} className="transition-colors hover:bg-slate-50/50">
                        <td className="px-4 py-3.5 font-medium text-foreground">
                          <div className="flex items-center gap-2">
                            <span>{user.nomUtilisateur}</span>
                            {isSelf && (
                              <span className="rounded bg-primary/10 px-1.5 py-0.5 text-xs font-normal text-primary">
                                Vous
                              </span>
                            )}
                          </div>
                        </td>
                        <td className="px-4 py-3.5">
                          <span className="inline-flex items-center gap-1.5 text-muted-foreground">
                            {user.role === "ADMIN" ? (
                              <>
                                <Shield className="size-4 text-amber-600" />
                                <span>Administrateur</span>
                              </>
                            ) : (
                              <>
                                <User className="size-4 text-blue-600" />
                                <span>Agent de crédit</span>
                              </>
                            )}
                          </span>
                        </td>
                        <td className="px-4 py-3.5">
                          {user.actif ? (
                            <span className="inline-flex items-center rounded-full bg-emerald-50 px-2.5 py-0.5 text-xs font-medium text-emerald-700 ring-1 ring-inset ring-emerald-600/20">
                              Actif
                            </span>
                          ) : (
                            <span className="inline-flex items-center rounded-full bg-slate-100 px-2.5 py-0.5 text-xs font-medium text-slate-600 ring-1 ring-inset ring-slate-500/20">
                              Inactif
                            </span>
                          )}
                        </td>
                        <td className="px-4 py-3.5 text-right">
                          <div className="flex items-center justify-end gap-1.5">
                            <Button
                              variant="outline"
                              size="sm"
                              onClick={() => ouvrirEdition(user)}
                              disabled={isActionEnCours}
                              title="Modifier"
                            >
                              <Pencil className="size-3.5" />
                              <span className="sr-only sm:not-sr-only">Modifier</span>
                            </Button>

                            <Button
                              variant="outline"
                              size="sm"
                              onClick={() => void handleToggleActif(user)}
                              disabled={isActionEnCours || (isSelf && user.actif)}
                              title={
                                isSelf && user.actif
                                  ? "Vous ne pouvez pas désactiver votre propre compte"
                                  : user.actif
                                  ? "Désactiver le compte"
                                  : "Activer le compte"
                              }
                            >
                              <Power className={`size-3.5 ${user.actif ? "text-amber-600" : "text-emerald-600"}`} />
                              <span className="sr-only sm:not-sr-only">
                                {user.actif ? "Désactiver" : "Activer"}
                              </span>
                            </Button>

                            <Button
                              variant="destructive"
                              size="sm"
                              onClick={() => setIdASupprimer(user.id)}
                              disabled={isSelf || isActionEnCours}
                              title={
                                isSelf
                                  ? "Vous ne pouvez pas supprimer votre propre compte"
                                  : "Supprimer l'utilisateur"
                              }
                            >
                              <Trash2 className="size-3.5" />
                              <span className="sr-only sm:not-sr-only">Supprimer</span>
                            </Button>
                          </div>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
