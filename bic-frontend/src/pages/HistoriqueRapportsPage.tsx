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

  useEffect(() => {
    axiosClient.get<ContratListItem[]>("/contrats").then((res) => setContrats(res.data)).finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="mx-auto max-w-3xl p-6 text-slate-500">Chargement...</div>;

  return (
    <div className="mx-auto max-w-3xl space-y-4 p-6">
      <h2 className="text-lg font-semibold text-slate-900">Historique des rapports</h2>
      <div className="overflow-hidden rounded-lg border bg-white">
        <table className="w-full text-sm">
          <thead className="bg-slate-50 text-left text-xs text-slate-500">
            <tr>
              <th className="p-3">Client</th><th>Code Client CB</th><th>Contrat</th><th>Date demande</th><th>Phase</th>
            </tr>
          </thead>
          <tbody>
            {contrats.map((c) => (
              <tr key={c.id} className="border-t hover:bg-slate-50">
                <td className="p-3">
                  <Link to={`/demande/rapport/${c.id}`} className="font-medium text-slate-800 hover:underline">
                    {c.client.prenom} {c.client.nom}
                  </Link>
                </td>
                <td>{c.client.codeClientCb}</td>
                <td>{c.codeContratCb}</td>
                <td>{formatDateFr(c.dateDemande)}</td>
                <td>{c.phaseDemande}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}