import { useRef, useState, type FormEvent, type KeyboardEvent } from "react";
import { useNavigate } from "react-router-dom";
import { Eye, EyeOff } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { useAuth } from "@/context/AuthContext";

export function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [nomUtilisateur, setNomUtilisateur] = useState("");
  const [motDePasse, setMotDePasse] = useState("");
  const [afficherMotDePasse, setAfficherMotDePasse] = useState(false);
  const [erreur, setErreur] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const motDePasseRef = useRef<HTMLInputElement>(null);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setErreur(null);
    setSubmitting(true);
    try {
      await login(nomUtilisateur, motDePasse);
      navigate("/");
    } catch {
      setErreur("Identifiants invalides.");
    } finally {
      setSubmitting(false);
    }
  }

  function onIdentifiantKeyDown(e: KeyboardEvent<HTMLInputElement>) {
    if (e.key === "Enter") { e.preventDefault(); motDePasseRef.current?.focus(); }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-50/70 p-4">
      <Card className="w-full max-w-sm shadow-xs border border-border">
        <CardHeader className="text-center pb-2 pt-6">
          <CardTitle className="text-2xl font-extrabold text-foreground tracking-tight">Scoring CECAM</CardTitle>
          <p className="text-xs text-muted-foreground mt-1">Système interne d'évaluation de solvabilité</p>
        </CardHeader>
        <CardContent>
          <form onSubmit={onSubmit} className="space-y-4">
            <div className="space-y-1.5">
              <Label>Nom d'utilisateur</Label>
              <Input value={nomUtilisateur} onChange={(e) => setNomUtilisateur(e.target.value)} onKeyDown={onIdentifiantKeyDown} autoFocus />
            </div>
            <div className="space-y-1.5">
              <Label>Mot de passe</Label>
              <div className="relative">
                <Input ref={motDePasseRef} type={afficherMotDePasse ? "text" : "password"} value={motDePasse}
                  onChange={(e) => setMotDePasse(e.target.value)} className="pr-9" />
                <button type="button" onClick={() => setAfficherMotDePasse((v) => !v)}
                  className="absolute right-2 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600" tabIndex={-1}
                  aria-label={afficherMotDePasse ? "Masquer le mot de passe" : "Afficher le mot de passe"}>
                  {afficherMotDePasse ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
                </button>
              </div>
            </div>
            {erreur && <p className="text-sm text-red-500">{erreur}</p>}
            <Button type="submit" className="w-full" disabled={submitting}>{submitting ? "Connexion..." : "Se connecter"}</Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}