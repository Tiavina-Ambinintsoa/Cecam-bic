import { axiosClient } from "./axiosClient";
import type { ContratRequest, ContratResponse } from "@/types/contrat";

export const contratApi = {
  creer: async (payload: ContratRequest): Promise<ContratResponse> => {
    const { data } = await axiosClient.post<ContratResponse>("/contrats", payload);
    return data;
  },
  listerParClient: async (clientId: number) => {
  const { data } = await axiosClient.get<import("@/types/contrat").ContratResponse[]>("/contrats", { params: { clientId } });
  return data;
},
changerPhase: async (contratId: number, phase: string) => {
  const { data } = await axiosClient.patch(`/contrats/${contratId}/phase`, { phase });
  return data;
},
};

