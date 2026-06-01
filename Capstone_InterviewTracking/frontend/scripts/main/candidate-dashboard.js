import { getMyApplicationApi } from "../api/candidate-api.js";
import { fetchHandler }         from "../api/fetch-handler.js";
import { clearAuth }            from "../utils/storage.js";
import { SITE_CONFIG }          from "../config/site-config.js";
import { fmtDatetime, fmtDate, statusBadgeClass, buildStageTrack } from "../utils/dom.js";

const role = localStorage.getItem("role");
if (!localStorage.getItem("token") || (role || "").toLowerCase() !== "candidate") {
  window.location.href = "../auth/login.html";
}

const name  = localStorage.getItem("candidateName") || "";
const email = localStorage.getItem("email") || "";

const initials = name
  ? name.split(" ").map(w => w[0]).join("").substring(0, 2).toUpperCase()
  : (email[0] || "?").toUpperCase();

document.getElementById("cd-avatar").textContent    = initials;
document.getElementById("cd-user-name").textContent = name || email;
document.getElementById("cd-user-email").textContent = email;

window.logout = function () {
  clearAuth();
  window.location.href = "../auth/login.html";
};

window.toggleSidebar = function () {
  const sidebar  = document.getElementById("cd-sidebar");
  const overlay  = document.getElementById("cd-overlay");
  const menuBtn  = document.getElementById("cd-menu-btn");
  const isOpen   = sidebar.classList.toggle("open");
  overlay.classList.toggle("show", isOpen);
  menuBtn.setAttribute("aria-expanded", isOpen ? "true" : "false");
  menuBtn.setAttribute("aria-label", isOpen ? "Close navigation menu" : "Open navigation menu");
};

window.closeSidebar = function () {
  document.getElementById("cd-sidebar").classList.remove("open");
  document.getElementById("cd-overlay").classList.remove("show");
  const btn = document.getElementById("cd-menu-btn");
  btn.setAttribute("aria-expanded", "false");
  btn.setAttribute("aria-label", "Open navigation menu");
};

const SECTIONS = ["jobs", "application", "interviews"];
const TITLES   = { jobs: "Browse Jobs", application: "My Application", interviews: "Interview Schedule" };

let currentSection = "jobs";

window.switchSection = function (section) {
  SECTIONS.forEach(s => {
    document.getElementById("section-" + s).style.display = s === section ? "" : "none";
    const btn = document.getElementById("nav-" + s);
    if (btn) {
      btn.classList.toggle("active", s === section);
      btn.setAttribute("aria-pressed", s === section ? "true" : "false");
    }
  });
  document.getElementById("cd-page-title").textContent = TITLES[section] || "";
  currentSection = section;
  closeSidebar();
};

const JOBS_PER_PAGE = 9;

let hasActiveApplication = false;

function updateApplyButtons() {
  document.querySelectorAll(".cd-job-apply-btn").forEach(btn => {
    btn.disabled     = hasActiveApplication;
    btn.textContent  = hasActiveApplication ? "Applied" : "Apply";
    btn.title        = hasActiveApplication ? "You already have an active application" : "";
  });
}

function val(v, suffix) {
  if (v == null || v === "") return "—";
  return suffix ? v + " " + suffix : String(v);
}

let allJobs    = [];
let currentPage = 1;

async function loadJobs() {
  const grid       = document.getElementById("jobs-grid");
  const loading    = document.getElementById("jobs-loading");
  const errorEl    = document.getElementById("jobs-error");
  const pagination = document.getElementById("jobs-pagination");
  const countEl    = document.getElementById("jobs-count");

  try {
    const data = await fetchHandler(SITE_CONFIG.ENDPOINTS_JOBS.LIST);
    allJobs = (data || []).filter(j => j.active !== false);
    loading.style.display = "none";

    if (allJobs.length === 0) {
      errorEl.textContent = "No active job openings at the moment.";
      errorEl.style.display = "";
      return;
    }

    countEl.textContent = allJobs.length + " open " + (allJobs.length === 1 ? "role" : "roles");
    grid.style.display       = "";
    pagination.style.display = "";
    renderJobs(1);
  } catch (err) {
    loading.style.display = "none";
    errorEl.textContent   = "Could not load jobs: " + err.message;
    errorEl.style.display = "";
  }
}

function renderJobs(page) {
  currentPage  = page;
  const grid       = document.getElementById("jobs-grid");
  const pagination = document.getElementById("jobs-pagination");

  const totalPages = Math.ceil(allJobs.length / JOBS_PER_PAGE);
  const start = (page - 1) * JOBS_PER_PAGE;
  const slice = allJobs.slice(start, start + JOBS_PER_PAGE);

  grid.innerHTML = slice.map(job => {
    const tags      = [job.jobType, job.location].filter(Boolean);
    const salaryStr = job.minSalary != null
      ? "₹" + job.minSalary + (job.maxSalary ? "–" + job.maxSalary : "+") + " LPA"
      : null;
    const expStr = job.minExperience != null
      ? job.minExperience + (job.maxExperience ? "–" + job.maxExperience : "+") + " yrs exp"
      : null;

    return `
      <article class="cd-job-card">
        <p class="cd-job-title">${job.title}</p>
        ${tags.length ? `<div class="cd-job-tags">${tags.map(t => `<span class="cd-job-tag">${t}</span>`).join("")}</div>` : ""}
        ${expStr ? `<p class="cd-job-meta">${expStr}</p>` : ""}
        ${salaryStr ? `<p class="cd-job-salary">${salaryStr}</p>` : ""}
        ${job.skills ? `<p class="cd-job-meta" style="font-size:11px;margin-top:4px;">${job.skills}</p>` : ""}
        <div class="cd-job-spacer"></div>
        <button class="cd-job-apply-btn" type="button" onclick="applyJob(${job.id})"
                aria-label="Apply for ${job.title}">Apply</button>
      </article>`;
  }).join("");

  updateApplyButtons();

  if (totalPages <= 1) {
    pagination.style.display = "none";
    return;
  }

  let html = `<button class="cd-page-btn" type="button" onclick="renderJobs(${page - 1})"
    ${page === 1 ? "disabled" : ""} aria-label="Previous page">&#8249;</button>`;
  for (let p = 1; p <= totalPages; p++) {
    html += `<button class="cd-page-btn ${p === page ? "active" : ""}" type="button"
      onclick="renderJobs(${p})" aria-label="Page ${p}" ${p === page ? 'aria-current="true"' : ""}>${p}</button>`;
  }
  html += `<button class="cd-page-btn" type="button" onclick="renderJobs(${page + 1})"
    ${page === totalPages ? "disabled" : ""} aria-label="Next page">&#8250;</button>`;
  pagination.innerHTML = html;
  pagination.style.display = "";
}

window.applyJob    = function (jobId) { window.location.href = "apply.html?jobId=" + jobId; };
window.renderJobs  = renderJobs;

async function loadApplication() {
  const appLoading = document.getElementById("app-loading");
  const ivLoading  = document.getElementById("iv-loading");

  try {
    const data = await getMyApplicationApi();

    appLoading.style.display = "none";
    document.getElementById("app-content").style.display = "";

    hasActiveApplication = !!data.status && data.status.toUpperCase() !== "REJECTED";
    updateApplyButtons();

    document.getElementById("app-job-title").textContent = data.jobTitle || "—";

    const metaParts = [
      data.location,
      data.jobType,
      data.appliedAt ? "Applied " + fmtDate(data.appliedAt) : null,
    ].filter(Boolean);
    document.getElementById("app-meta").textContent = metaParts.join(" · ");

    const statusEl   = document.getElementById("app-status");
    statusEl.textContent = data.status || "—";
    statusEl.className   = "badge " + statusBadgeClass(data.status);

    const resumeLink = document.getElementById("app-resume-link");
    if (data.resumeUrl) {
      resumeLink.href = data.resumeUrl;
      resumeLink.style.display = "";
    }

    document.getElementById("stage-track").innerHTML = buildStageTrack(data.stage || "PROFILING");

    document.getElementById("p-fullname").textContent    = val(data.fullName);
    document.getElementById("p-email").textContent       = val(data.email);
    document.getElementById("p-mobile").textContent      = val(data.mobile);
    document.getElementById("p-company").textContent     = val(data.currentCompany);
    document.getElementById("p-total-exp").textContent   = data.totalExperience != null ? data.totalExperience + " yrs" : "—";
    document.getElementById("p-rel-exp").textContent     = data.relevantExperience != null ? data.relevantExperience + " yrs" : "—";
    document.getElementById("p-current-ctc").textContent = data.currentCtc != null ? "₹" + data.currentCtc + " LPA" : "—";
    document.getElementById("p-expected-ctc").textContent = data.expectedCtc != null ? "₹" + data.expectedCtc + " LPA" : "—";
    document.getElementById("p-notice").textContent      = data.noticePeriod != null ? data.noticePeriod + " days" : "—";
    document.getElementById("p-location").textContent    = val(data.preferredLocation);

    ivLoading.style.display = "none";
    const interviews = data.interviews || [];

    if (interviews.length === 0) {
      document.getElementById("iv-none").style.display = "";
    } else {
      const ivList = document.getElementById("iv-list");
      ivList.style.display = "";
      ivList.innerHTML = interviews.map(iv => `
        <article class="iv-card">
          <div class="iv-card-header">
            <span class="iv-round">${iv.round} Round</span>
            <span class="badge ${statusBadgeClass(iv.status)}">${iv.status || "—"}</span>
          </div>
          <p class="iv-meta"><strong>Date &amp; Time:</strong> ${fmtDatetime(iv.interviewDateTime)}</p>
          ${iv.focusArea ? `<p class="iv-meta"><strong>Focus Area:</strong> ${iv.focusArea}</p>` : ""}
          ${iv.panels && iv.panels.length
            ? `<p class="iv-panel"><strong>Panel:</strong> ${iv.panels.map(p => p.name || p.fullName || p.email).join(", ")}</p>`
            : ""}
        </article>`).join("");
    }

  } catch (err) {
    appLoading.style.display = "none";
    ivLoading.style.display  = "none";

    if (err.message && err.message.toLowerCase().includes("not found")) {
      hasActiveApplication = false;
      updateApplyButtons();
      document.getElementById("app-none").style.display = "";
      document.getElementById("iv-none").style.display  = "";
    } else {
      const noneEl = document.getElementById("app-none");
      noneEl.style.display = "";
      noneEl.querySelector("h3").textContent = "Error loading application";
      noneEl.querySelector("p").textContent  = err.message;
      document.getElementById("iv-none").style.display = "";
    }
  }
}

function resolveInitialSection() {
  const hash = window.location.hash.replace("#", "");
  if (SECTIONS.includes(hash)) switchSection(hash);
}

loadJobs();
loadApplication();
resolveInitialSection();
