import { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { rapportApi } from "@/api/rapportApi";
import type { RapportSolvabilite } from "@/types/rapport";

export function RapportPage() {
  const { contratId } = useParams<{ contratId: string }>();
  const [rapport, setRapport] = useState<RapportSolvabilite | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);
  const [erreur, setErreur] = useState<string | null>(null);
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
      .catch(() => {
        if (!cancelled) {
          setErreur("Aucune demande trouvée pour cet identifiant. Vérifiez que c'est bien un ID de contrat (pas un ID client).");
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

  if (erreur) return <div className="mx-auto max-w-xl p-6 text-red-600">{erreur}</div>;
  if (!rapport || !previewUrl) return <div className="mx-auto max-w-3xl p-6 text-slate-500">Chargement du rapport...</div>;

  return (
    <div className="mx-auto max-w-4xl p-6">
      <div className="mb-4 flex justify-end gap-3">
        <Button onClick={() => rapportApi.ouvrirPdfNouvelOnglet(Number(contratId))}>
          Visualiser PDF
        </Button>
      </div>

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