import { fetchHandler, fetchMultipart } from "./fetch-handler.js";
import { SITE_CONFIG } from "../config/site-config.js";

export function getMyApplicationApi() {
  return fetchHandler(SITE_CONFIG.ENDPOINTS_CANDIDATE.MY_APPLICATION);
}

export function applyForJobApi(formData) {
  return fetchMultipart(SITE_CONFIG.ENDPOINTS_CANDIDATE.APPLY, formData);
}
