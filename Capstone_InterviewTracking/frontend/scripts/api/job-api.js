import { fetchHandler } from "./fetch-handler.js";

export function getJobsApi() {
  return fetchHandler("/jobs");
}

export function getJobsForHRApi() {
  return fetchHandler("/jobs/hr");
}

export function createJobApi(body) {
  return fetchHandler("/jobs", { method: "POST", body });
}

export function updateJobApi(id, body) {
  return fetchHandler(`/jobs/${id}`, { method: "PUT", body });
}

export function deleteJobApi(id) {
  return fetchHandler(`/jobs/${id}`, { method: "DELETE" });
}

export function toggleJobApi(id) {
  return fetchHandler(`/jobs/${id}/toggle`, { method: "PUT" });
}
