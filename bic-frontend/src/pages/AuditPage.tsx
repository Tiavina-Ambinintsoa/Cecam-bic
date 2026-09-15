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

  useEffect(() => {
    if (role !== "ADMIN") return;
    axiosClient.get<EntreeAudit[]>("/audit").then((res) => setEntrees(res.data)).finally(() => setLoading(false));
  }, [role]);

  if (role !== "ADMIN") {
    return <div className="mx-auto max-w-xl p-6 text-red-600">Accès réservé aux administrateurs.</div>;
  }
  if (loading) return <div className="mx-auto max-w-3xl p-6 text-slate-500">Chargement...</div>;

  return (
    <div className="mx-auto max-w-4xl space-y-4 p-6">
      <h2 className="text-lg font-semibold text-slate-900">Journal d'audit</h2>
      <div className="overflow-hidden rounded-lg border bg-white">
        <table className="w-full text-sm">
          <thead className="bg-slate-50 text-left text-xs text-slate-500">
            <tr><th className="p-3">Date</th><th>Utilisateur</th><th>Entité</th><th>ID</th><th>Action</th><th>Détail</th></tr>
          </thead>
          <tbody>
            {entrees.map((e) => (
              <tr key={e.id} className="border-t">
                <td className="whitespace-nowrap p-3">{formatDateFr(e.dateEvenement)}</td>
                <td>{e.utilisateur}</td>
                <td>{e.entite}</td>
                <td>{e.entiteId}</td>
                <td>{e.action}</td>
                <td className="text-slate-500">{e.detail}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}