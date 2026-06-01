export const SITE_CONFIG = {
  API_URL: "http://localhost:8080",

  ENDPOINTS_AUTH: {
    LOGIN: "/auth/login",
    SIGNUP: "/auth/signup",
    SET_PASSWORD: "/auth/set-password",
  },

  ENDPOINTS_JOBS: {
    LIST: "/jobs",
  },

  ENDPOINTS_CANDIDATE: {
    APPLY: "/candidate/apply",
    MY_APPLICATION: "/candidate/my-application",
  },

  ENDPOINTS_HR: {
    CANDIDATES: "/hr/candidates",
    CANDIDATE_DETAIL: (id) => `/hr/candidates/${id}`,
    CREATE_PANEL: "/hr/create-panel",
    PANELS: "/hr/panels",
    SCHEDULE_INTERVIEW: "/hr/interviews/schedule",
    HR_INTERVIEWS: "/hr/interviews",
    HR_INTERVIEW_CANDIDATE: (id) => `/hr/interviews/${id}/candidate`,
    SUBMIT_HR_FEEDBACK: (id) => `/hr/interviews/${id}/feedback`,
    INTERVIEW_FEEDBACK: (id) => `/hr/interviews/${id}/feedback`,
    UPDATE_STAGE: (id) => `/hr/applications/${id}/stage`,
    REJECT: (id) => `/hr/applications/${id}/reject`,
    SELECT: (id) => `/hr/applications/${id}/select`,
  },

  ENDPOINTS_PANEL: {
    MY_INTERVIEWS: "/panel/interviews",
    CANDIDATE_DETAIL: (id) => `/panel/interviews/${id}/candidate`,
    SUBMIT_FEEDBACK: (id) => `/panel/interviews/${id}/feedback`,
  },
};
