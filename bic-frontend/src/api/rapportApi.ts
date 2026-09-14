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

  ouvrirPdfNouvelOnglet: async (contratId: number) => {
    const { data } = await axiosClient.get(`/rapports/contrat/${contratId}/pdf`, { responseType: "blob" });
    const url = URL.createObjectURL(new Blob([data], { type: "application/pdf" }));
    window.open(url, "_blank");
  },
};