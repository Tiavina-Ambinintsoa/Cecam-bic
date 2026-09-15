import { useEffect, useState } from "react";
import { axiosClient } from "@/api/axiosClient";
import { formatDateFr } from "@/lib/dates";

interface AuditEntry {
  id: number; dateEvenement: string; utilisateur: string;
  entite: string; entiteId: number; action: string; detail: string;
}

export function JournalAuditPage() {
  const [entries, setEntries] = useState<AuditEntry[]>([]);

  useEffect(() => {
    axiosClient.get<AuditEntry[]>("/audit").then((res) => setEntries(res.data));
  }, []);

  return (
    <div className="mx-auto max-w-4xl space-y-4 p-6">
      <h2 className="text-lg font-semibold text-slate-900">Journal d'audit</h2>
      <div className="overflow-hidden rounded-lg border bg-white">
        <table className="w-full text-sm">
          <thead className="bg-slate-50 text-left text-xs text-slate-500">
            <tr><th className="p-2">Date</th><th>Utilisateur</th><th>Entité</th><th>Action</th><th>Détail</th></tr>
          </thead>
          <tbody>
            {entries.map((e) => (
              <tr key={e.id} className="border-t">
                <td className="p-2">{formatDateFr(e.dateEvenement)}</td>
                <td>{e.utilisateur}</td>
                <td>{e.entite} #{e.entiteId}</td>
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