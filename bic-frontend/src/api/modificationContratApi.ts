import { axiosClient } from "./axiosClient";
import type { ModificationContratRequest, ModificationContratResponse } from "@/types/modificationContrat";

export const modificationContratApi = {
  soumettre: async (contratId: number, payload: ModificationContratRequest) => {
    const { data } = await axiosClient.post<ModificationContratResponse>(`/demandes-modification/contrats/${contratId}`, payload);
    return data;
  },
  listerMesDemandes: async () => {
    const { data } = await axiosClient.get<ModificationContratResponse[]>("/demandes-modification/mes-demandes");
    return data;
  },
  listerEnAttente: async () => {
    const { data } = await axiosClient.get<ModificationContratResponse[]>("/demandes-modification/en-attente");
    return data;
  },
  approuver: async (id: number) => {
    const { data } = await axiosClient.post<ModificationContratResponse>(`/demandes-modification/${id}/approuver`);
    return data;
  },
  refuser: async (id: number, commentaire: string) => {
    const { data } = await axiosClient.post<ModificationContratResponse>(`/demandes-modification/${id}/refuser`, { commentaire });
    return data;
  },
};
