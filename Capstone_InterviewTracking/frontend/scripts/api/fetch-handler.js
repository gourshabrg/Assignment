import { getToken, clearAuth } from "../utils/storage.js";
import { SITE_CONFIG } from "../config/site-config.js";

function handleUnauthorized() {
  clearAuth();
  const url = window.location.href;
  const pagesIdx = url.indexOf("/pages/");
  window.location.replace(
    pagesIdx !== -1
      ? url.substring(0, pagesIdx) + "/pages/auth/login.html"
      : "./auth/login.html"
  );
}

export async function fetchHandler(path, options = {}) {
  const headers = new Headers(options.headers || {});
  const token = getToken();

  if (!headers.has("Content-Type") && options.body) {
    headers.set("Content-Type", "application/json");
  }

  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  const url = `${SITE_CONFIG.API_URL}${path}`;

  const response = await fetch(url, {
    ...options,
    headers,
    body: options.body ? JSON.stringify(options.body) : null,
  });

  const data = await readResponse(response);

  if (response.status === 401) {
    handleUnauthorized();
    throw new Error("Session expired. Please log in again.");
  }

  if (!response.ok) {
    throw new Error(data?.message || "Request failed");
  }

  return data?.data ?? data;
}

export async function fetchMultipart(path, formData) {
  const token = getToken();
  const headers = new Headers();
  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  const url = `${SITE_CONFIG.API_URL}${path}`;
  const response = await fetch(url, { method: "POST", headers, body: formData });
  const data = await readResponse(response);

  if (response.status === 401) {
    handleUnauthorized();
    throw new Error("Session expired. Please log in again.");
  }

  if (!response.ok) {
    throw new Error(data?.message || "Request failed");
  }

  return data?.data ?? data;
}

async function readResponse(response) {
  const contentType = response.headers.get("content-type") || "";

  if (contentType.includes("application/json")) {
    return response.json();
  }

  const text = await response.text();
  return text ? { message: text } : null;
}
