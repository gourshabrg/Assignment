import { fetchHandler, fetchMultipart } from "./fetch-handler.js";
import { SITE_CONFIG } from "../config/site-config.js";

const E = SITE_CONFIG.ENDPOINTS_HR;

export function getCandidatesApi(params = {}) {
  const query = new URLSearchParams();
  if (params.stage) query.set("stage", params.stage);
  if (params.status) query.set("status", params.status);
  if (params.jobId) query.set("jobId", params.jobId);
  const qs = query.toString();
  return fetchHandler(E.CANDIDATES + (qs ? `?${qs}` : ""));
}

export function getCandidateDetailApi(applicationId) {
  return fetchHandler(E.CANDIDATE_DETAIL(applicationId));
}

export function getPanelsApi() {
  return fetchHandler(E.PANELS);
}

export function createPanelApi(body) {
  return fetchHandler(E.CREATE_PANEL, { method: "POST", body });
}

export function scheduleInterviewApi(body) {
  return fetchHandler(E.SCHEDULE_INTERVIEW, { method: "POST", body });
}

export function getHRInterviewsApi() {
  return fetchHandler(E.HR_INTERVIEWS);
}

export function getHRInterviewCandidateApi(interviewId) {
  return fetchHandler(E.HR_INTERVIEW_CANDIDATE(interviewId));
}

export function submitHRFeedbackApi(interviewId, body) {
  return fetchHandler(E.SUBMIT_HR_FEEDBACK(interviewId), { method: "POST", body });
}

export function getInterviewFeedbackApi(interviewId) {
  return fetchHandler(E.INTERVIEW_FEEDBACK(interviewId));
}

export function updateStageApi(applicationId, body) {
  return fetchHandler(E.UPDATE_STAGE(applicationId), { method: "PUT", body });
}

export function rejectCandidateApi(applicationId) {
  return fetchHandler(E.REJECT(applicationId), { method: "PUT" });
}

export function selectCandidateApi(applicationId) {
  return fetchHandler(E.SELECT(applicationId), { method: "PUT" });
}

export function createCandidateByHRApi(formData) {
  return fetchMultipart(E.CANDIDATES, formData);
}
