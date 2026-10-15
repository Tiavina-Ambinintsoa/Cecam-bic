import { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { isAxiosError } from "axios";
import { Button } from "@/components/ui/button";
import { rapportApi } from "@/api/rapportApi";
import type { RapportSolvabilite } from "@/types/rapport";

export function RapportPage() {
  const { contratId } = useParams<{ contratId: string }>();
  const [rapport, setRapport] = useState<RapportSolvabilite | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);
  const [erreur, setErreur] = useState<string | null>(null);
  const [erreurPdf, setErreurPdf] = useState<string | null>(null);
  const [telechargementPdf, setTelechargementPdf] = useState(false);
  const [iframeHeight, setIframeHeight] = useState(900);
  const iframeRef = useRef<HTMLIFrameElement>(null);

  useEffect(() => {
    if (!contratId) return;
    let blobUrl: string | null = null;
    let cancelled = false;

    Promise.all([
      rapportApi.obtenir(Number(contratId)),
      rapportApi.obtenirHtmlBlobUrl(Number(contratId)),
    ])
      .then(([data, url]) => {
        if (cancelled) return;
        setRapport(data);
        blobUrl = url;
        setPreviewUrl(url);
      })
      .catch((error: unknown) => {
        if (!cancelled) {
          setErreur(isAxiosError(error) && error.response?.status === 404
            ? "Aucun contrat trouvé pour cet identifiant."
            : "Impossible de charger le rapport pour le moment. Vérifiez que le serveur fonctionne, puis réessayez.");
        }
      });

    return () => {
      cancelled = true;
      if (blobUrl) URL.revokeObjectURL(blobUrl);
    };
  }, [contratId]);

  function handleIframeLoad() {
    const doc = iframeRef.current?.contentWindow?.document;
    if (doc) setIframeHeight(doc.documentElement.scrollHeight + 40);
  }

  async function telechargerEtVisualiserPdf() {
    if (!contratId || telechargementPdf) return;
    setErreurPdf(null);
    setTelechargementPdf(true);
    try {
      await rapportApi.telechargerEtVisualiserPdf(Number(contratId));
    } catch {
      setErreurPdf("Impossible de générer ou d’ouvrir le PDF. Vérifiez que le serveur fonctionne, puis réessayez.");
    } finally {
      setTelechargementPdf(false);
    }
  }

  if (erreur) return <div className="mx-auto max-w-xl p-6 text-red-600">{erreur}</div>;
  if (!rapport || !previewUrl) return <div className="mx-auto max-w-3xl p-6 text-slate-500">Chargement du rapport...</div>;

  return (
    <div className="mx-auto max-w-4xl p-6">
      <div className="mb-4 flex justify-end gap-3">
        <Button onClick={() => void telechargerEtVisualiserPdf()} disabled={telechargementPdf}>
          {telechargementPdf ? "Génération du PDF..." : "Télécharger et visualiser le PDF"}
        </Button>
      </div>
      {erreurPdf && <p role="alert" className="mb-4 text-sm text-red-600">{erreurPdf}</p>}

      <iframe
        ref={iframeRef}
        src={previewUrl}
        title="Aperçu du rapport de solvabilité"
        onLoad={handleIframeLoad}
        style={{ height: iframeHeight }}
        className="w-full rounded-lg border border-slate-200 bg-white shadow-sm"
      />
    </div>
  );
}
