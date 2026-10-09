import axios from "axios";

export const axiosClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/api",
  headers: { "Content-Type": "application/json" },
});

axiosClient.interceptors.request.use((config) => {
  const token = localStorage.getItem("bic_token");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

axiosClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const url = String(error.config?.url ?? "");
    if (error.response?.status === 401 && !url.includes("/auth/login")) {
      localStorage.removeItem("bic_token");
      localStorage.removeItem("bic_user");
      localStorage.removeItem("bic_role");
      localStorage.removeItem("bic_last_activity");
      window.location.replace("/login");
    }
    return Promise.reject(error);
  }
);