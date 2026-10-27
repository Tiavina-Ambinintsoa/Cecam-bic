import { axiosClient } from "./axiosClient";
import type { ClientRequest, ClientResponse } from "@/types/client";

/** Réponse paginée Spring Boot. */
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface ClientEnregistrementResponse {
  client: ClientResponse;
  clientTrouve: boolean;
}

export const clientApi = {
  /** Liste paginée des clients — admin uniquement. */
  lister: async (page = 0, size = 20, q?: string): Promise<PageResponse<ClientResponse>> => {
    const params: Record<string, string | number> = { page, size };
    if (q && q.trim()) params.q = q.trim();
    const { data } = await axiosClient.get<PageResponse<ClientResponse>>("/clients", { params });
    return data;
  },

  /** Obtenir un client par son identifiant ID */
  obtenirParId: async (id: number): Promise<ClientResponse> => {
    const { data } = await axiosClient.get<ClientResponse>(`/clients/${id}`);
    return data;
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

  /** Supprime un client — admin uniquement. Échoue si contrat actif. */
  supprimer: async (id: number): Promise<void> => {
    await axiosClient.delete(`/clients/${id}`);
  },

  rechercherParCin: async (numero: string): Promise<ClientResponse | null> => {
    try {
      const { data } = await axiosClient.get<ClientResponse>("/clients/recherche", { params: { typeIdentifiant: "CIN", numero } });
      return data;
    } catch {
      return null;
    }
  },
  rechercherParCodeClientCb: async (codeClientCb: string): Promise<ClientResponse | null> => {
    try {
      const { data } = await axiosClient.get<ClientResponse>("/clients/recherche", { params: { codeClientCb } });
      return data;
    } catch {
      return null;
    }
  },

  rechercherParCodeClientEtablissement: async (codeClientEtablissement: string): Promise<ClientResponse | null> => {
    try {
      const { data } = await axiosClient.get<ClientResponse>("/clients/recherche", { params: { codeClientEtablissement } });
      return data;
    } catch {
      return null;
    }
  },

};
