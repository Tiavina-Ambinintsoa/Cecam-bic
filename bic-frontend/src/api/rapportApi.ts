import { axiosClient } from "./axiosClient";
import type { RapportSolvabilite } from "@/types/rapport";

export const rapportApi = {
  obtenir: async (contratId: number): Promise<RapportSolvabilite> => {
    const { data } = await axiosClient.get<RapportSolvabilite>(`/rapports/contrat/${contratId}`);
    return data;
  },

  obtenirHtmlBlobUrl: async (contratId: number): Promise<string> => {
    const { data } = await axiosClient.get(`/rapports/contrat/${contratId}/html`, { responseType: "text" });
    return URL.createObjectURL(new Blob([data], { type: "text/html" }));
  },

  ouvrirHtmlNouvelOnglet: async (contratId: number) => {
    const url = await rapportApi.obtenirHtmlBlobUrl(contratId);
    window.open(url, "_blank");
  },

  telechargerEtVisualiserPdf: async (contratId: number) => {
    const fenetre = window.open("about:blank", "_blank");
    if (fenetre) fenetre.opener = null;
    const { data } = await axiosClient.get(`/rapports/contrat/${contratId}/pdf`, { responseType: "blob" });
    const url = URL.createObjectURL(new Blob([data], { type: "application/pdf" }));
    if (fenetre) fenetre.location.replace(url);
    const lien = document.createElement("a");
    lien.href = url;
    lien.download = `rapport-solvabilite-${contratId}.pdf`;
    document.body.appendChild(lien);
    lien.click();
    lien.remove();
    window.setTimeout(() => URL.revokeObjectURL(url), 60_000);
  },
};