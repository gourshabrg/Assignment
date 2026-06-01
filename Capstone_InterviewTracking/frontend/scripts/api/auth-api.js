import { fetchHandler } from "./fetch-handler.js";
import { SITE_CONFIG } from "../config/site-config.js";

export function loginApi(data) {
  return fetchHandler(SITE_CONFIG.ENDPOINTS_AUTH.LOGIN, {
    method: "POST",
    body: data,
  });
}

export function signupApi(data) {
  return fetchHandler(SITE_CONFIG.ENDPOINTS_AUTH.SIGNUP, {
    method: "POST",
    body: data,
  });
}

export function setPasswordApi(data) {
  return fetchHandler(SITE_CONFIG.ENDPOINTS_AUTH.SET_PASSWORD, {
    method: "POST",
    body: data,
  });
}
