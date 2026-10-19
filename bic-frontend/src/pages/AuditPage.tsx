import { useEffect, useState } from "react";
import { axiosClient } from "@/api/axiosClient";
import { useAuth } from "@/context/AuthContext";
import { formatDateFr } from "@/lib/dates";

interface EntreeAudit {
  id: number;
  dateEvenement: string;
  utilisateur: string;
  entite: string;
  entiteId: number;
  action: string;
  detail: string;
}

export function AuditPage() {
  const { role } = useAuth();
  const [entrees, setEntrees] = useState<EntreeAudit[]>([]);
  const [loading, setLoading] = useState(true);
  const [erreur, setErreur] = useState<string | null>(null);

  useEffect(() => {
    if (role !== "ADMIN") return;
    axiosClient.get<EntreeAudit[]>("/audit")
      .then((res) => setEntrees(res.data))
      .catch(() => setErreur("Erreur lors du chargement du journal d'audit."))
      .finally(() => setLoading(false));
  }, [role]);

  if (role !== "ADMIN") {
    return <div className="mx-auto max-w-xl p-6 text-red-600">Accès réservé aux administrateurs.</div>;
  }
  if (loading) return <div className="mx-auto max-w-3xl p-6 text-slate-500">Chargement...</div>;

  return (
    <div className="mx-auto max-w-4xl space-y-4 p-6">
      <h2 className="text-lg font-semibold text-slate-900">Journal d'audit</h2>
      {erreur && <div className="rounded-md border border-red-200 bg-red-50 p-3 text-sm text-red-700">{erreur}</div>}
      <div className="overflow-hidden rounded-lg border bg-white">
        <table className="w-full text-sm">
          <thead className="bg-slate-50 text-left text-xs text-slate-500">
            <tr><th className="p-3">Date</th><th className="p-3">Utilisateur</th><th className="p-3">Entité</th><th className="p-3">ID</th><th className="p-3">Action</th><th className="p-3">Détail</th></tr>
          </thead>
          <tbody>
            {entrees.map((e) => (
              <tr key={e.id} className="border-t">
                <td className="whitespace-nowrap p-3">{formatDateFr(e.dateEvenement)}</td>
                <td className="p-3">{e.utilisateur}</td>
                <td className="p-3">{e.entite}</td>
                <td className="p-3">{e.entiteId}</td>
                <td className="p-3">{e.action}</td>
                <td className="p-3 text-slate-500">{e.detail}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}