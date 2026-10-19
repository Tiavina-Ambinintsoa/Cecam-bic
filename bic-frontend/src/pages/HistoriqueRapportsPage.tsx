import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { axiosClient } from "@/api/axiosClient";
import { formatDateFr } from "@/lib/dates";

interface ContratListItem {
  id: number;
  codeContratCb: string;
  typeContrat: string;
  phaseDemande: string;
  dateDemande: string;
  client: { prenom: string; nom: string; codeClientCb: string };
}

export function HistoriqueRapportsPage() {
  const [contrats, setContrats] = useState<ContratListItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [erreur, setErreur] = useState<string | null>(null);

  useEffect(() => {
    axiosClient.get<ContratListItem[]>("/contrats")
      .then((res) => setContrats(res.data))
      .catch(() => setErreur("Erreur lors du chargement des contrats."))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="mx-auto max-w-5xl p-6 text-slate-500">Chargement...</div>;

  return (
    <div className="mx-auto max-w-5xl space-y-4 p-6">
      <h2 className="text-2xl font-semibold text-slate-900">Historique des rapports</h2>
      {erreur && <div className="rounded-md border border-red-200 bg-red-50 p-3 text-sm text-red-700">{erreur}</div>}
      <div className="overflow-hidden rounded-lg border bg-white">
        <table className="w-full text-base">
          <thead className="bg-slate-50 text-left text-sm text-slate-600">
            <tr>
              <th className="p-3">Client</th><th className="p-3">Code Client CB</th><th className="p-3">Contrat</th><th className="p-3">Date demande</th><th className="p-3">Phase</th><th className="p-3">Rapport</th>
            </tr>
          </thead>
          <tbody>
            {contrats.map((c) => (
              <tr key={c.id} className="border-t hover:bg-slate-50">
                <td className="p-3">
                  <Link to={`/demande/rapport/${c.id}`} className="font-medium text-slate-800 hover:underline">
                    {[c.client.prenom, c.client.nom].filter(Boolean).join(" ")}
                  </Link>
                </td>
                <td className="p-3">{c.client.codeClientCb}</td>
                <td className="p-3">{c.codeContratCb}</td>
                <td className="p-3">{formatDateFr(c.dateDemande)}</td>
                <td className="p-3">{c.phaseDemande}</td>
                <td className="p-3">
                  <Link to={`/demande/rapport/${c.id}`} className="font-medium text-primary underline underline-offset-2">
                    Ouvrir le rapport
                  </Link>
                </td>
              </tr>
            ))}
            {contrats.length === 0 && (
              <tr><td colSpan={6} className="p-6 text-center text-slate-500">Aucun contrat disponible.</td></tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
