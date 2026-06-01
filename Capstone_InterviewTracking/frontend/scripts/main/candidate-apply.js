import { applyForJobApi } from "../api/candidate-api.js";
import { fetchHandler } from "../api/fetch-handler.js";
import { SITE_CONFIG } from "../config/site-config.js";

const token = localStorage.getItem("token");
const role = localStorage.getItem("role");
const email = localStorage.getItem("email");

if (!token || (role || "").toLowerCase() !== "candidate") {
  window.location.href = "../auth/login.html";
}

const params = new URLSearchParams(window.location.search);
const jobId = params.get("jobId");

if (!jobId) {
  window.location.href = "dashboard.html";
}

const yesterday = new Date();
yesterday.setDate(yesterday.getDate() - 1);
document.getElementById("dob").max = yesterday.toISOString().split("T")[0];

document.getElementById("email").value = email || "";

const storedName = localStorage.getItem("candidateName");
const storedMobile = localStorage.getItem("candidateMobile");
const storedDob = localStorage.getItem("candidateDob");

if (storedName) {
  document.getElementById("fullName").value = storedName;
  document.getElementById("fullName").readOnly = true;
}
if (storedMobile) {
  document.getElementById("mobile").value = storedMobile;
  document.getElementById("mobile").readOnly = true;
}
if (storedDob) {
  document.getElementById("dob").value = storedDob;
  document.getElementById("dob").readOnly = true;
}

document.getElementById("back-btn").addEventListener("click", () => {
  if (document.referrer) {
    history.back();
  } else {
    window.location.href = "dashboard.html";
  }
});

let jobMinExp = null;

async function loadJobInfo() {
  try {
    const jobs = await fetchHandler(SITE_CONFIG.ENDPOINTS_JOBS.LIST);
    const job = (jobs || []).find(j => String(j.id) === String(jobId));
    if (job) {
      document.getElementById("banner-job-title").textContent = job.title;
      const parts = [
        job.location,
        job.jobType,
        job.minExperience != null
          ? job.minExperience + (job.maxExperience ? "–" + job.maxExperience : "+") + " yrs exp"
          : null,
      ].filter(Boolean);
      document.getElementById("banner-job-meta").textContent =
        "Interview Tracking" + (parts.length ? " · " + parts.join(" · ") : "");

      if (job.minExperience != null) {
        jobMinExp = Number(job.minExperience);
        document.getElementById("relevantExperience").value = job.minExperience;
      }
    }
  } catch (_) {}
}

loadJobInfo();

const resumeInput = document.getElementById("resume");
const resumePreview = document.getElementById("resume-preview");
const resumeLabel = document.getElementById("resume-label");
const resumeFileName = document.getElementById("resume-file-name");
const resumeFileSize = document.getElementById("resume-file-size");

function formatBytes(bytes) {
  if (bytes < 1024) return bytes + " B";
  if (bytes < 1048576) return (bytes / 1024).toFixed(1) + " KB";
  return (bytes / 1048576).toFixed(2) + " MB";
}

resumeInput.addEventListener("change", () => {
  const file = resumeInput.files[0];
  if (!file) return;

  const isPdf = file.type === "application/pdf" || file.name.toLowerCase().endsWith(".pdf");
  if (!isPdf) {
    resumeInput.value = "";
    document.getElementById("error-msg").textContent = "Only PDF files are allowed.";
    return;
  }

  if (file.size > 5 * 1024 * 1024) {
    resumeInput.value = "";
    document.getElementById("error-msg").textContent = "Resume file is too large. Maximum allowed size is 5 MB.";
    return;
  }

  document.getElementById("error-msg").textContent = "";
  resumeFileName.textContent = file.name;
  resumeFileSize.textContent = formatBytes(file.size);
  resumeLabel.style.display = "none";
  resumePreview.classList.add("show");
});

document.getElementById("resume-clear").addEventListener("click", () => {
  resumeInput.value = "";
  resumeLabel.style.display = "";
  resumePreview.classList.remove("show");
});

document.getElementById("apply-form").addEventListener("submit", async (e) => {
  e.preventDefault();
  const errorEl = document.getElementById("error-msg");
  errorEl.textContent = "";

  const fullName = document.getElementById("fullName").value.trim();
  if (!fullName) {
    errorEl.textContent = "Full name is required.";
    document.getElementById("fullName").focus();
    return;
  }

  const mobile = document.getElementById("mobile").value.trim();
  if (!mobile) {
    errorEl.textContent = "Mobile number is required.";
    document.getElementById("mobile").focus();
    return;
  }
  if (!/^\d{10}$/.test(mobile)) {
    errorEl.textContent = "Please enter a valid 10-digit mobile number.";
    document.getElementById("mobile").focus();
    return;
  }

  const dob = document.getElementById("dob").value;
  if (!dob) {
    errorEl.textContent = "Date of birth is required.";
    document.getElementById("dob").focus();
    return;
  }
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  if (new Date(dob) >= today) {
    errorEl.textContent = "Date of birth cannot be today or a future date.";
    document.getElementById("dob").focus();
    return;
  }

  const currentCompany = document.getElementById("currentCompany").value.trim();
  if (!currentCompany) {
    errorEl.textContent = "Current company is required.";
    document.getElementById("currentCompany").focus();
    return;
  }

  const source = document.getElementById("source").value.trim();
  if (!source) {
    errorEl.textContent = "Source is required (e.g. LinkedIn, Referral).";
    document.getElementById("source").focus();
    return;
  }

  const totalExpRaw = document.getElementById("totalExperience").value;
  if (totalExpRaw === "") {
    errorEl.textContent = "Total experience is required.";
    document.getElementById("totalExperience").focus();
    return;
  }
  const totalExp = parseFloat(totalExpRaw);
  if (isNaN(totalExp) || totalExp < 0) {
    errorEl.textContent = "Total experience must be a valid non-negative number.";
    document.getElementById("totalExperience").focus();
    return;
  }
  if (jobMinExp !== null && totalExp < jobMinExp) {
    errorEl.textContent = `Total experience must be at least ${jobMinExp} year(s) as required for this role.`;
    document.getElementById("totalExperience").focus();
    return;
  }

  const currentCtcRaw = document.getElementById("currentCtc").value;
  if (currentCtcRaw === "") {
    errorEl.textContent = "Current CTC is required.";
    document.getElementById("currentCtc").focus();
    return;
  }
  const currentCtc = parseFloat(currentCtcRaw);
  if (isNaN(currentCtc) || currentCtc < 0) {
    errorEl.textContent = "Current CTC must be a valid non-negative number.";
    document.getElementById("currentCtc").focus();
    return;
  }

  const expectedCtcRaw = document.getElementById("expectedCtc").value;
  if (expectedCtcRaw === "") {
    errorEl.textContent = "Expected CTC is required.";
    document.getElementById("expectedCtc").focus();
    return;
  }
  const expectedCtc = parseFloat(expectedCtcRaw);
  if (isNaN(expectedCtc) || expectedCtc < 0) {
    errorEl.textContent = "Expected CTC must be a valid non-negative number.";
    document.getElementById("expectedCtc").focus();
    return;
  }

  const noticePeriodRaw = document.getElementById("noticePeriod").value;
  if (noticePeriodRaw === "") {
    errorEl.textContent = "Notice period is required.";
    document.getElementById("noticePeriod").focus();
    return;
  }
  const noticePeriod = parseInt(noticePeriodRaw, 10);
  if (isNaN(noticePeriod) || noticePeriod < 0) {
    errorEl.textContent = "Notice period must be a valid non-negative whole number.";
    document.getElementById("noticePeriod").focus();
    return;
  }

  const resumeFile = resumeInput.files[0];
  if (!resumeFile) {
    errorEl.textContent = "Please upload your resume (PDF).";
    return;
  }
  const isPdf = resumeFile.type === "application/pdf" || resumeFile.name.toLowerCase().endsWith(".pdf");
  if (!isPdf) {
    errorEl.textContent = "Only PDF files are allowed.";
    return;
  }
  if (resumeFile.size > 5 * 1024 * 1024) {
    errorEl.textContent = "Resume file is too large. Maximum allowed size is 5 MB.";
    return;
  }

  const formData = new FormData();
  formData.append("fullName", fullName);
  formData.append("email", email);
  formData.append("mobile", mobile);
  formData.append("dob", dob);
  formData.append("currentCompany", currentCompany);
  formData.append("source", source);
  formData.append("totalExperience", totalExp);
  const relExp = document.getElementById("relevantExperience").value;
  if (relExp) formData.append("relevantExperience", relExp);
  formData.append("currentCtc", currentCtc);
  formData.append("expectedCtc", expectedCtc);
  formData.append("noticePeriod", noticePeriod);
  const preferredLocation = document.getElementById("preferredLocation").value.trim();
  if (preferredLocation) formData.append("preferredLocation", preferredLocation);
  formData.append("resume", resumeFile);
  formData.append("jobId", jobId);

  const overlay = document.getElementById("submit-overlay");
  overlay.classList.add("show");
  setFormDisabled(true);

  try {
    await applyForJobApi(formData);
    window.location.href = "dashboard.html#application";
  } catch (err) {
    overlay.classList.remove("show");
    setFormDisabled(false);
    errorEl.textContent = err.message || "Submission failed. Please try again.";
  }
});

function setFormDisabled(disabled) {
  document.getElementById("apply-form")
    .querySelectorAll("input, select, button")
    .forEach(el => { el.disabled = disabled; });

  const submitBtn = document.getElementById("submit-btn");
  const backBtn = document.getElementById("back-btn");
  if (submitBtn) submitBtn.disabled = disabled;
  if (backBtn) backBtn.disabled = disabled;
}
