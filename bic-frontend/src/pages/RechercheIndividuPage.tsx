import { useState } from "react";
import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { clientApi } from "@/api/clientApi";
import { contratApi } from "@/api/contratApi";
import type { ClientResponse } from "@/types/client";
import type { ContratResponse } from "@/types/contrat";

export function RechercheIndividuPage() {
  const [codeCb, setCodeCb] = useState("");
  const [codeEtablissement, setCodeEtablissement] = useState("");
  const [numero, setNumero] = useState("");
  const [client, setClient] = useState<ClientResponse | null>(null);
  const [contrats, setContrats] = useState<ContratResponse[]>([]);
  const [recherche, setRecherche] = useState(false);
  const [introuvable, setIntrouvable] = useState(false);
  const [erreur, setErreur] = useState<string | null>(null);

  async function rechercher() {
    setRecherche(true); setIntrouvable(false); setClient(null); setContrats([]); setErreur(null);
    try {
      const found = codeCb.trim()
        ? await clientApi.rechercherParCodeClientCb(codeCb.trim())
        : codeEtablissement.trim()
          ? await clientApi.rechercherParCodeClientEtablissement(codeEtablissement.trim())
          : numero.trim() ? await clientApi.rechercherParCin(numero.trim()) : null;
      if (!found) { setIntrouvable(true); return; }
      setClient(found);
      setContrats(await contratApi.listerParClient(found.id));
    } catch {
      setErreur("Erreur lors de la recherche. Vérifiez votre connexion et réessayez.");
    } finally { setRecherche(false); }
  }

  function onKeyDown(e: React.KeyboardEvent) { if (e.key === "Enter" && recherchePossible && !recherche) rechercher(); }

  const recherchePossible = Boolean(codeCb.trim() || codeEtablissement.trim() || numero.trim());

  return (
    <div className="mx-auto max-w-5xl space-y-6 p-6">
      <Card>
        <CardHeader><CardTitle>Suivi client — Recherche par identifiant</CardTitle></CardHeader>
        <CardContent className="grid grid-cols-1 items-end gap-4 md:grid-cols-2">
          <div className="space-y-1.5"><Label>Code Client CB</Label><Input value={codeCb} onChange={(e) => setCodeCb(e.target.value)} onKeyDown={onKeyDown} /></div>
          <div className="space-y-1.5"><Label>Code Client CECAM</Label><Input value={codeEtablissement} onChange={(e) => setCodeEtablissement(e.target.value)} onKeyDown={onKeyDown} /></div>
          <div className="space-y-1.5"><Label>CIN</Label><Input value={numero} onChange={(e) => setNumero(e.target.value)} onKeyDown={onKeyDown} /></div>
          <Button onClick={rechercher} disabled={!recherchePossible || recherche}>{recherche ? "Recherche…" : "Rechercher"}</Button>
        </CardContent>
      </Card>

      {erreur && <div className="rounded-md border border-red-200 bg-red-50 p-3 text-sm text-red-700">{erreur}</div>}
      {introuvable && <p className="text-base text-slate-600">Aucun client trouvé avec cet identifiant.</p>}
      {client && (
        <Card>
          <CardHeader><CardTitle>{client.prenom} {client.nom} — {client.codeClientCb}</CardTitle></CardHeader>
          <CardContent className="space-y-3">
            <p className="text-base text-slate-600">{contrats.length} contrat(s) dans son historique :</p>
            {contrats.map((c) => (
              <Link key={c.id} to={`/demande/rapport/${c.id}`} className="flex items-center justify-between rounded-md border p-4 text-base hover:bg-slate-50">
                <span>{c.codeContratCb} — {c.typeContrat}</span><span className="text-slate-500">{c.phaseDemande}</span>
              </Link>
            ))}
          </CardContent>
        </Card>
      )}
    </div>
  );
}
