import { fetchHandler } from "./fetch-handler.js";
import { SITE_CONFIG } from "../config/site-config.js";

const E = SITE_CONFIG.ENDPOINTS_PANEL;

export function getMyInterviewsApi() {
  return fetchHandler(E.MY_INTERVIEWS);
}

export function getCandidateDetailApi(interviewId) {
  return fetchHandler(E.CANDIDATE_DETAIL(interviewId));
}

export function submitFeedbackApi(interviewId, body) {
  return fetchHandler(E.SUBMIT_FEEDBACK(interviewId), { method: "POST", body });
}
