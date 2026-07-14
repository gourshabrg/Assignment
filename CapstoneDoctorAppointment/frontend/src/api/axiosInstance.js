import axios from "axios";
import { TOKEN_STORAGE_KEY } from "../utils/constants";

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL
});

axiosInstance.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_STORAGE_KEY);

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem(TOKEN_STORAGE_KEY);

      if (window.location.pathname !== "/login") {
        window.location.href = "/login?session_expired=true";
      }
    }

    return Promise.reject(error);
  }
);

export default axiosInstance;
