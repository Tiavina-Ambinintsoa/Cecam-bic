import { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { rapportApi } from "@/api/rapportApi";
import type { RapportSolvabilite } from "@/types/rapport";

function buildMailHref(rapport: RapportSolvabilite) {
  const subject = encodeURIComponent(`Rapport de solvabilité — ${rapport.client.nomComplet}`);
  const body = encodeURIComponent(
    `Bonjour,\n\nMerci de consulter le rapport de solvabilité de ${rapport.client.nomComplet} ` +
    `(code client ${rapport.codeClientCb}) directement depuis l'application BIC CECAM.\n\nCordialement,`
  );
  return `mailto:?subject=${subject}&body=${body}`;
}

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
        <a href={buildMailHref(rapport)} className="inline-flex h-9 items-center rounded-md border px-4 text-sm font-medium hover:bg-slate-50">
          Envoyer par email
        </a>
        <Button variant="outline" onClick={() => rapportApi.ouvrirHtmlNouvelOnglet(Number(contratId))}>
          Visualiser PDF HTML
        </Button>
        <Button onClick={() => rapportApi.ouvrirPdfNouvelOnglet(Number(contratId))}>
          Visualiser PDF
        </Button>
      </div>

      {/* Aperçu = exactement le même HTML que celui utilisé pour générer le PDF */}
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