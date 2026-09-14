import { axiosClient } from "./axiosClient";

export interface LoginResponse {
  token: string;
  nomUtilisateur: string;
  role: "AGENT_CREDIT" | "ADMIN";
}

export const authApi = {
  login: async (nomUtilisateur: string, motDePasse: string): Promise<LoginResponse> => {
    const { data } = await axiosClient.post<LoginResponse>("/auth/login", { nomUtilisateur, motDePasse });
    return data;
  },
};