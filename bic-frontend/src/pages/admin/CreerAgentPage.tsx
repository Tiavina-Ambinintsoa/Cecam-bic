import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useToast } from "@/hooks/use-toast";
import { utilisateurApi, type UtilisateurRequest } from "@/api/utilisateurApi";
import { UserPlus, ArrowLeft, ShieldCheck, CheckCircle2 } from "lucide-react";

export function CreerAgentPage() {
  const navigate = useNavigate();
  const { toast } = useToast();

  const [nomUtilisateur, setNomUtilisateur] = useState("");
  const [motDePasse, setMotDePasse] = useState("");
  const [confirmationMotDePasse, setConfirmationMotDePasse] = useState("");
  const [actif, setActif] = useState(true);
  const [soumissionEnCours, setSoumissionEnCours] = useState(false);
  const [erreur, setErreur] = useState<string | null>(null);
  const [succes, setSucces] = useState<string | null>(null);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setErreur(null);
    setSucces(null);

    const nomTrimmed = nomUtilisateur.trim();
    if (!nomTrimmed) {
      setErreur("Le nom d'utilisateur est obligatoire.");
      return;
    }

    if (!motDePasse) {
      setErreur("Le mot de passe est obligatoire.");
      return;
    }

    if (motDePasse.length < 4) {
      setErreur("Le mot de passe doit comporter au moins 4 caractères.");
      return;
    }

    if (motDePasse !== confirmationMotDePasse) {
      setErreur("La confirmation du mot de passe ne correspond pas.");
      return;
    }

    setSoumissionEnCours(true);
    try {
      const payload: UtilisateurRequest = {
        nomUtilisateur: nomTrimmed,
        motDePasse,
        role: "AGENT_CREDIT",
        actif,
      };

      const resultat = await utilisateurApi.creer(payload);
      toast({
        title: `Compte agent « ${resultat.nomUtilisateur} » créé avec succès.`,
      });
      setSucces(`Le compte agent « ${resultat.nomUtilisateur} » a été créé avec succès.`);
      setNomUtilisateur("");
      setMotDePasse("");
      setConfirmationMotDePasse("");
      setActif(true);
    } catch (err: unknown) {
      const error = err as { response?: { status?: number; data?: { message?: string } } };
      if (
        error.response?.status === 409 ||
        (error.response?.status === 400 &&
          error.response?.data?.message?.toLowerCase().includes("existe"))
      ) {
        setErreur(`Le nom d'utilisateur « ${nomTrimmed} » est déjà utilisé.`);
      } else {
        setErreur(
          error.response?.data?.message ||
            "Une erreur est survenue lors de la création du compte agent."
        );
      }
    } finally {
      setSoumissionEnCours(false);
    }
  }

  return (
    <div className="mx-auto max-w-2xl space-y-6 p-6">
      <div className="flex items-center justify-between">
        <Link
          to="/admin/utilisateurs"
          className="inline-flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground"
        >
          <ArrowLeft className="size-4" />
          Retour à la gestion des utilisateurs
        </Link>
      </div>

      <Card className="border-border bg-card shadow-sm">
        <CardHeader>
          <div className="flex items-center gap-2 text-primary">
            <UserPlus className="size-5" />
            <CardTitle>Créer un nouveau compte agent</CardTitle>
          </div>
          <CardDescription>
            Enregistrez un agent de crédit avec ses identifiants de connexion.
          </CardDescription>
        </CardHeader>
        <CardContent>
          {erreur && (
            <div className="mb-4 rounded-lg border border-red-300 bg-red-50 p-4 text-sm text-red-800">
              <p className="font-semibold">Erreur</p>
              <p>{erreur}</p>
            </div>
          )}

          {succes && (
            <div className="mb-4 rounded-lg border border-green-300 bg-green-50 p-4 text-sm text-green-800">
              <div className="flex items-center gap-2">
                <CheckCircle2 className="size-4 text-green-600" />
                <p className="font-semibold">Succès</p>
              </div>
              <p className="mt-1">{succes}</p>
              <div className="mt-3 flex gap-2">
                <Button
                  size="sm"
                  variant="outline"
                  onClick={() => navigate("/admin/utilisateurs")}
                >
                  Voir tous les utilisateurs
                </Button>
                <Button
                  size="sm"
                  variant="ghost"
                  onClick={() => setSucces(null)}
                >
                  Créer un autre agent
                </Button>
              </div>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-1.5">
              <Label htmlFor="nomUtilisateur" className="font-semibold">
                Nom d'utilisateur <span className="text-red-500">*</span>
              </Label>
              <Input
                id="nomUtilisateur"
                value={nomUtilisateur}
                onChange={(e) => setNomUtilisateur(e.target.value)}
                placeholder="ex. agent2 ou jean.dupont"
                required
                autoFocus
              />
            </div>

            <div className="space-y-1.5">
              <Label htmlFor="motDePasse" className="font-semibold">
                Mot de passe <span className="text-red-500">*</span>
              </Label>
              <Input
                id="motDePasse"
                type="password"
                value={motDePasse}
                onChange={(e) => setMotDePasse(e.target.value)}
                placeholder="••••••••"
                required
              />
            </div>

            <div className="space-y-1.5">
              <Label htmlFor="confirmationMotDePasse" className="font-semibold">
                Confirmer le mot de passe <span className="text-red-500">*</span>
              </Label>
              <Input
                id="confirmationMotDePasse"
                type="password"
                value={confirmationMotDePasse}
                onChange={(e) => setConfirmationMotDePasse(e.target.value)}
                placeholder="••••••••"
                required
              />
            </div>

            <div className="rounded-lg border bg-slate-50 p-3 space-y-1">
              <div className="flex items-center gap-2">
                <ShieldCheck className="size-4 text-primary" />
                <span className="text-sm font-medium text-foreground">Rôle affecté : Agent de crédit</span>
              </div>
              <p className="text-xs text-muted-foreground">
                Ce compte aura accès aux fonctionnalités d'enregistrement de dossiers, recherche, consultation de rapports et alertes.
              </p>
            </div>

            <div className="flex items-center space-x-3 pt-2">
              <label htmlFor="actif-checkbox" className="flex cursor-pointer items-center gap-2 text-sm font-medium">
                <input
                  id="actif-checkbox"
                  type="checkbox"
                  checked={actif}
                  onChange={(e) => setActif(e.target.checked)}
                  className="size-4 rounded border-gray-300 text-primary focus:ring-primary"
                />
                <span>Activer le compte dès sa création</span>
              </label>
            </div>

            <div className="flex justify-end gap-2 pt-4">
              <Button
                type="button"
                variant="outline"
                onClick={() => navigate("/admin/utilisateurs")}
                disabled={soumissionEnCours}
              >
                Annuler
              </Button>
              <Button type="submit" disabled={soumissionEnCours}>
                {soumissionEnCours ? "Création en cours..." : "Créer le compte agent"}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
