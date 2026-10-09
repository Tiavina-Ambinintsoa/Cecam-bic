import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { axiosClient } from "@/api/axiosClient";
import { DateInput } from "@/components/ui/date-input";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { formatDateFr } from "@/lib/dates";

interface Alerte {
  clientId: number; codeClientCb: string; nomComplet: string; contratId: number;
  codeContratCb: string; typeContrat: string; dateAlerte: string;
  description: string; niveau: string; agence?: string | null; utilisateur?: string | null;
  echeancesImpayees: number; echeancesEnRetard: number;
  retard30a59: number; retard60a89: number; retard90a119: number;
  retard120a149: number; retard150a179: number; retard180Plus: number; maxJoursRetard: number;
}

type TypeTelechargement = "TOUTES" | "NOUVELLES";

function champCsv(value: unknown): string {
  let s = String(value ?? "");
  if (/^[=+@-]/.test(s)) s = "'" + s;
  return `"${s.replace(/"/g, '""')}"`;
}

export function AlertesPage() {
  const [alertes, setAlertes] = useState<Alerte[]>([]);
  const [loading, setLoading] = useState(true);
  const [erreur, setErreur] = useState<string | null>(null);
  const [dateDebut, setDateDebut] = useState("");
  const [dateFin, setDateFin] = useState("");
  const [recherche, setRecherche] = useState("");
  const [typeTelechargement, setTypeTelechargement] = useState<TypeTelechargement>("TOUTES");
  const [niveau, setNiveau] = useState("TOUS");
  const [agence, setAgence] = useState("");
  const [utilisateur, setUtilisateur] = useState("");

  useEffect(() => {
    axiosClient.get<Alerte[]>("/alertes").then((res) => setAlertes(res.data))
      .catch(() => setErreur("Impossible de charger le sommaire des alertes."))
      .finally(() => setLoading(false));
  }, []);

  const filtrees = useMemo(() => alertes.filter((a) => {
    const q = recherche.trim().toLocaleLowerCase();
    if (q && ![a.nomComplet, a.codeClientCb, a.codeContratCb, a.typeContrat].some((v) => v?.toLocaleLowerCase().includes(q))) return false;
    if (dateDebut && a.dateAlerte < dateDebut) return false;
    if (dateFin && a.dateAlerte > dateFin) return false;
    if (niveau !== "TOUS" && a.niveau !== niveau) return false;
    if (agence && !(a.agence ?? "").toLocaleLowerCase().includes(agence.toLocaleLowerCase())) return false;
    if (utilisateur && !(a.utilisateur ?? "").toLocaleLowerCase().includes(utilisateur.toLocaleLowerCase())) return false;
    if (typeTelechargement === "NOUVELLES" && dateDebut && a.dateAlerte < dateDebut) return false;
    return true;
  }), [alertes, dateDebut, dateFin, recherche, niveau, agence, utilisateur, typeTelechargement]);

  function telechargerCsv() {
    const colonnes = ["Date début", "Date fin", "Nom / Raison sociale", "Code Client CB", "Code Contrat CB", "Type contrat", "Type de téléchargement", "Description alerte", "Niveau", "Agence / Direction", "Utilisateur déclarant", "Impayées", "Retards 30-59", "60-89", "90-119", "120-149", "150-179", "180+ jours"];
    const lignes = filtrees.map((a) => [dateDebut, dateFin, a.nomComplet, a.codeClientCb, a.codeContratCb, a.typeContrat,
      typeTelechargement === "NOUVELLES" ? "Nouvelles alertes" : "Toutes les alertes", a.description, a.niveau, a.agence, a.utilisateur,
      a.echeancesImpayees, a.retard30a59, a.retard60a89, a.retard90a119, a.retard120a149, a.retard150a179, a.retard180Plus]);
    const contenu = [colonnes, ...lignes].map((row) => row.map(champCsv).join(";")).join("\r\n");
    const url = URL.createObjectURL(new Blob(["\uFEFF", contenu], { type: "text/csv;charset=utf-8" }));
    const lien = document.createElement("a"); lien.href = url; lien.download = "sommaire-alertes.csv"; lien.click();
    window.setTimeout(() => URL.revokeObjectURL(url), 10_000);
  }

  if (loading) return <div className="mx-auto max-w-6xl p-6 text-slate-500">Chargement...</div>;
  return <div className="mx-auto max-w-7xl space-y-5 p-6">
    <div><h2 className="text-lg font-semibold text-slate-900">Credit Bureau avec Global Score / Sommaire des alertes</h2>
      <p className="text-sm text-slate-500">Les seuils de retard sont calculés à partir des échéances datées disponibles.</p></div>
    {erreur && <p className="text-sm text-red-600">{erreur}</p>}
    <section className="grid grid-cols-1 gap-4 rounded-lg border bg-white p-4 md:grid-cols-3 lg:grid-cols-4">
      <div className="space-y-1.5"><Label>Date début</Label><DateInput value={dateDebut} onValueChange={setDateDebut} /></div>
      <div className="space-y-1.5"><Label>Date fin</Label><DateInput value={dateFin} onValueChange={setDateFin} /></div>
      <div className="space-y-1.5"><Label>Nom / Raison sociale, codes CB</Label><Input value={recherche} onChange={(e) => setRecherche(e.target.value)} placeholder="Nom, Code Client CB ou Contrat CB" /></div>
      <div className="space-y-1.5"><Label>Type de téléchargement</Label>
        <Select value={typeTelechargement} onValueChange={(v) => { if (v) setTypeTelechargement(v as TypeTelechargement); }}>
          <SelectTrigger><SelectValue /></SelectTrigger><SelectContent>
            <SelectItem value="TOUTES">Toutes les alertes filtrées</SelectItem>
            <SelectItem value="NOUVELLES">Nouvelles détectées dans la période</SelectItem>
          </SelectContent>
        </Select>
      </div>
      <div className="space-y-1.5"><Label>Niveau</Label><Select value={niveau} onValueChange={(v) => { if (v) setNiveau(v); }}>
        <SelectTrigger><SelectValue /></SelectTrigger><SelectContent><SelectItem value="TOUS">Tous</SelectItem><SelectItem value="CRITIQUE">Critique — impayés</SelectItem><SelectItem value="VIGILANCE">Vigilance — retard</SelectItem></SelectContent></Select></div>
      <div className="space-y-1.5"><Label>Agence / Direction</Label><Input value={agence} onChange={(e) => setAgence(e.target.value)} placeholder="Code agence" /></div>
      <div className="space-y-1.5"><Label>Utilisateur déclarant</Label><Input value={utilisateur} onChange={(e) => setUtilisateur(e.target.value)} placeholder="Agent" /></div>
      <div className="flex items-end justify-between gap-3"><span className="text-sm text-slate-600">{filtrees.length} alerte(s)</span><Button onClick={telechargerCsv} disabled={!filtrees.length}>Télécharger CSV</Button></div>
    </section>

    {filtrees.length === 0 && <p className="text-sm text-slate-500">Aucune alerte pour les filtres choisis.</p>}
    {filtrees.length > 0 && <div className="overflow-x-auto rounded-lg border bg-white">
      <table className="min-w-[1150px] w-full text-left text-sm">
        <thead className="bg-slate-50 text-xs uppercase text-slate-600"><tr>
          <th className="p-3">Date</th><th className="p-3">Nom / Raison sociale</th><th className="p-3">Code Client CB</th><th className="p-3">Code Contrat CB</th><th className="p-3">Description alerte</th><th className="p-3">Niveau</th><th className="p-3">Retard 30/60/90/120/150/180+</th><th className="p-3">Agence / Direction</th><th className="p-3">Utilisateur déclarant</th>
        </tr></thead><tbody>
          {filtrees.map((a) => <tr key={a.contratId} className="border-t">
            <td className="whitespace-nowrap p-3">{formatDateFr(a.dateAlerte)}</td>
            <td className="p-3">{a.nomComplet}</td><td className="p-3">{a.codeClientCb}</td>
            <td className="p-3"><Link className="text-primary underline" to={`/demande/rapport/${a.contratId}`}>{a.codeContratCb}</Link></td>
            <td className="p-3">{a.description}</td><td className="p-3">{a.niveau}</td>
            <td className="p-3">{[a.retard30a59,a.retard60a89,a.retard90a119,a.retard120a149,a.retard150a179,a.retard180Plus].join(" / ")}</td>
            <td className="p-3">{a.agence || "—"}</td><td className="p-3">{a.utilisateur || "—"}</td>
          </tr>)}
        </tbody>
      </table>
    </div>}
  </div>;
}
