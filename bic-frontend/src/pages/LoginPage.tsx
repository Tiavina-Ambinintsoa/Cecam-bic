import { useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { motion } from "motion/react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useAuth } from "@/context/AuthContext";
import { messageErreur, codeErreur } from "@/lib/apiError";

const EASE = [0.22, 1, 0.36, 1] as const;

export function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [nomUtilisateur, setNomUtilisateur] = useState("");
  const [motDePasse, setMotDePasse] = useState("");
  const [erreur, setErreur] = useState<string | null>(null);
  const [bloque, setBloque] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [connecte, setConnecte] = useState(false);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setErreur(null);
    setSubmitting(true);
    try {
      await login(nomUtilisateur, motDePasse);
      setConnecte(true);
    } catch (err) {
      setErreur(messageErreur(err, "connexion"));
      setBloque(codeErreur(err) === "TROP_DE_TENTATIVES");
      setSubmitting(false);
    }
  }

  return (
    <div className="grid min-h-screen overflow-hidden lg:grid-cols-[1.1fr_1fr]">
      {}
      <motion.aside
        className="relative hidden flex-col justify-between bg-primary p-12 text-primary-foreground lg:flex"
        initial={{ x: "-8%", opacity: 0 }}
        animate={connecte ? { x: "-100%", opacity: 0 } : { x: 0, opacity: 1 }}
        transition={{ duration: connecte ? 0.5 : 0.45, ease: EASE }}
        onAnimationComplete={() => {
          if (connecte) navigate("/");
        }}
      >
        <div className="text-sm tracking-wide opacity-80">CECAM</div>

        <div className="max-w-md">
          <h1 className="font-heading text-4xl leading-tight">
            Bureau d'information sur le crédit
          </h1>
          <p className="mt-5 text-base leading-relaxed opacity-80">
            Consultez la situation d'un sociétaire avant d'engager la caisse :
            contrats en cours, historique de remboursement, score de solvabilité.
          </p>
        </div>

        <p className="text-xs leading-relaxed opacity-70">
          Les consultations sont journalisées. Les données des sociétaires sont
          confidentielles et ne quittent pas la caisse.
        </p>
      </motion.aside>

      <motion.main
        className="flex items-center justify-center px-6 py-16"
        initial={{ x: "8%", opacity: 0 }}
        animate={connecte ? { x: "100%", opacity: 0 } : { x: 0, opacity: 1 }}
        transition={{ duration: connecte ? 0.5 : 0.45, ease: EASE, delay: connecte ? 0.05 : 0.1 }}
      >
        <div className="w-full max-w-sm">
          <h2 className="font-heading text-2xl text-foreground">Connexion</h2>
          <p className="mt-1.5 text-sm text-muted-foreground">
            Utilisez le compte fourni par votre administrateur de caisse.
          </p>

          <form onSubmit={onSubmit} className="mt-8 space-y-5" noValidate>
            <div className="space-y-2">
              <Label htmlFor="nomUtilisateur">Nom d'utilisateur</Label>
              <Input
                id="nomUtilisateur"
                name="username"
                autoComplete="username"
                value={nomUtilisateur}
                onChange={(e) => setNomUtilisateur(e.target.value)}
                aria-invalid={!!erreur}
                disabled={submitting}
                autoFocus
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="motDePasse">Mot de passe</Label>
              <Input
                id="motDePasse"
                name="password"
                type="password"
                autoComplete="current-password"
                value={motDePasse}
                onChange={(e) => setMotDePasse(e.target.value)}
                aria-invalid={!!erreur}
                disabled={submitting}
              />
            </div>

            {erreur && (
              <div
                role="alert"
                className="border-l-2 border-destructive bg-accent/40 px-3 py-2.5 text-sm text-foreground"
              >
                {erreur}
              </div>
            )}

            <Button type="submit" className="w-full" disabled={submitting || bloque}>
              {submitting ? "Connexion en cours…" : "Se connecter"}
            </Button>
          </form>
        </div>
      </motion.main>
    </div>
  );
}