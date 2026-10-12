import { axiosClient } from "./axiosClient";

export interface UtilisateurResponse {
  id: number;
  nomUtilisateur: string;
  role: "ADMIN" | "AGENT_CREDIT";
  actif: boolean;
}

export interface UtilisateurRequest {
  nomUtilisateur: string;
  motDePasse?: string;
  role: "ADMIN" | "AGENT_CREDIT";
  actif?: boolean;
}

export const utilisateurApi = {
  async lister(): Promise<UtilisateurResponse[]> {
    const res = await axiosClient.get<UtilisateurResponse[]>("/utilisateurs");
    return res.data;
  },

  async obtenir(id: number): Promise<UtilisateurResponse> {
    const res = await axiosClient.get<UtilisateurResponse>(`/utilisateurs/${id}`);
    return res.data;
  },

  async creer(payload: UtilisateurRequest): Promise<UtilisateurResponse> {
    const res = await axiosClient.post<UtilisateurResponse>("/utilisateurs", payload);
    return res.data;
  },

  async modifier(id: number, payload: UtilisateurRequest): Promise<UtilisateurResponse> {
    const res = await axiosClient.put<UtilisateurResponse>(`/utilisateurs/${id}`, payload);
    return res.data;
  },

  async basculerActif(id: number): Promise<UtilisateurResponse> {
    const res = await axiosClient.patch<UtilisateurResponse>(`/utilisateurs/${id}/activer`);
    return res.data;
  },

  async supprimer(id: number): Promise<void> {
    await axiosClient.delete(`/utilisateurs/${id}`);
  },
};
