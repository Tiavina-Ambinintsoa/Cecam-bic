import { axiosClient } from "./axiosClient";
import type { ClientRequest, ClientResponse } from "@/types/client";

export interface ClientEnregistrementResponse {
  client: ClientResponse;
  clientTrouve: boolean;
}

export const clientApi = {
    rechercherParCodeCb: async (codeClientCb: string): Promise<ClientResponse | null> => {
    try {
      const { data } = await axiosClient.get<ClientResponse>("/clients/recherche", { params: { codeClientCb } });
      return data;
    } catch {
      return null;
    }
  },
  creer: async (payload: ClientRequest): Promise<ClientEnregistrementResponse> => {
    const { data } = await axiosClient.post<ClientEnregistrementResponse>("/clients", payload);
    return data;
  
  },

  mettreAJour: async (id: number, payload: ClientRequest) => {
  const { data } = await axiosClient.put<ClientResponse>(`/clients/${id}`, payload);
  return data;
},
ajouterAdresse: async (id: number, payload: import("@/types/client").AdresseRequest) => {
  const { data } = await axiosClient.post(`/clients/${id}/adresses`, payload);
  return data;
},

  rechercherParCin: async (numero: string): Promise<ClientResponse | null> => {
    try {
      const { data } = await axiosClient.get<ClientResponse>("/clients/recherche", { params: { typeIdentifiant: "CIN", numero } });
      return data;
    } catch {
      return null;
    }
  },
};
