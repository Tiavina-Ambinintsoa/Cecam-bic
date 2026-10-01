import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { axiosClient } from "@/api/axiosClient";

interface ResultatImport { lignesLues: number; lignesImportees: number; lignesEnErreur: number; erreurs: string[]; }

export function ImportPage() {
  const [fichier, setFichier] = useState<File | null>(null);
  const [resultat, setResultat] = useState<ResultatImport | null>(null);
  const [enCours, setEnCours] = useState(false);

  async function telechargerTemplate() {
    const res = await axiosClient.get("/import/template", { responseType: "blob" });
    const url = URL.createObjectURL(new Blob([res.data], { type: "text/csv" }));
    const a = document.createElement("a");
    a.href = url; a.download = "template-import-clients.csv"; a.click();
  }

  async function lancerImport() {
    if (!fichier) return;
    setEnCours(true);
    try {
      const formData = new FormData();
      formData.append("fichier", fichier);
      const res = await axiosClient.post<ResultatImport>("/import/clients", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      setResultat(res.data);
    } finally { setEnCours(false); }
  }

  return (
    <div className="mx-auto max-w-2xl space-y-6 p-6">
      <Card>
        <CardHeader><CardTitle>Import en masse — Clients CECAM</CardTitle></CardHeader>
        <CardContent className="space-y-4">
          <p className="text-sm text-slate-500">
            Téléchargez d'abord le modèle, remplissez-le selon vos données existantes, puis importez-le ici.
            Chaque ligne est traitée indépendamment : une erreur sur une ligne n'empêche pas l'import des autres.
          </p>
          <Button variant="outline" onClick={telechargerTemplate}>Télécharger le modèle CSV</Button>
          <div className="flex items-center gap-3">
            <input type="file" accept=".csv" onChange={(e) => setFichier(e.target.files?.[0] ?? null)} />
            <Button onClick={lancerImport} disabled={!fichier || enCours}>{enCours ? "Import en cours..." : "Importer"}</Button>
          </div>
        </CardContent>
      </Card>

      {resultat && (
        <Card className="animate-in fade-in slide-in-from-top-2 duration-300">
          <CardHeader><CardTitle>Résultat</CardTitle></CardHeader>
          <CardContent className="space-y-2 text-sm">
            <p>{resultat.lignesLues} ligne(s) lue(s), <span className="text-green-600">{resultat.lignesImportees} importée(s)</span>, <span className="text-red-600">{resultat.lignesEnErreur} en erreur</span>.</p>
            {resultat.erreurs.length > 0 && (
              <ul className="list-disc space-y-1 pl-5 text-red-600">
                {resultat.erreurs.map((e, i) => <li key={i}>{e}</li>)}
              </ul>
            )}
          </CardContent>
        </Card>
      )}
    </div>
  );
}