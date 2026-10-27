import { axiosClient } from "./axiosClient";
import type { ContratRequest, ContratResponse, HistoriqueContratDetailResponse } from "@/types/contrat";

export const contratApi = {
  creer: async (payload: ContratRequest): Promise<ContratResponse> => {
    const { data } = await axiosClient.post<ContratResponse>("/contrats", payload);
    return data;
  },
  listerParClient: async (clientId: number) => {
    const { data } = await axiosClient.get<ContratResponse[]>("/contrats", { params: { clientId } });
    return data;
  },
  rechercherParIdentifiant: async (code: string): Promise<ContratResponse | null> => {
    try {
      const { data } = await axiosClient.get<ContratResponse>("/contrats/recherche", { params: { identifiantContrat: code } });
      return data;
    } catch {
      return null;
    }
  },
  changerPhase: async (contratId: number, phase: string, motif?: string) => {
    const { data } = await axiosClient.patch(`/contrats/${contratId}/phase`, { phase, motif: motif || null });
    return data;
  },
  changerEcheanceMensuelle: async (contratId: number, montantEcheanceMensuelle: number | null, devise: string) => {
    await axiosClient.patch(`/contrats/${contratId}/echeance-mensuelle`, { montantEcheanceMensuelle, devise });
  },
  changerCodesEtablissement: async (contratId: number, codeContratEtablissement: string, codeDemandeEtablissement: string) => {
    await axiosClient.patch(`/contrats/${contratId}/codes-etablissement`, { codeContratEtablissement, codeDemandeEtablissement });
  },
  supprimer: async (contratId: number): Promise<void> => {
    await axiosClient.delete(`/contrats/${contratId}`);
  },
  obtenirParId: async (contratId: number): Promise<ContratResponse> => {
    const { data } = await axiosClient.get<ContratResponse>(`/contrats/${contratId}`);
    return data;
  },
  obtenirDetailHistorique: async (contratId: number): Promise<HistoriqueContratDetailResponse> => {
    const { data } = await axiosClient.get<HistoriqueContratDetailResponse>(`/contrats/${contratId}/historique`);
    return data;
  },
  modifierHistorique: async (contratId: number, payload: unknown): Promise<void> => {
    await axiosClient.put(`/contrats/${contratId}/historique`, payload);
  },
};
