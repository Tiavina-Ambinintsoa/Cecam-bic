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
};
