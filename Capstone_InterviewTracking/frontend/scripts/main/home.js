import { getJobsApi } from "../api/job-api.js";
import { getMyApplicationApi } from "../api/candidate-api.js";
import { escHtml } from "../utils/dom.js";

const jobsGrid          = document.getElementById("jobsGrid");
const jobModal          = document.getElementById("jobModal");
const jobDetailsContent = document.getElementById("jobDetailsContent");

let hasActiveApplication = false;

document.addEventListener("DOMContentLoaded", function () {
  loadJobs();
  setupScrollBehavior();
  updateNavForAuth();
  checkActiveApplication();
});

async function loadJobs() {
  if (!jobsGrid) return;
  try {
    jobsGrid.innerHTML = "<div class='loading'>Loading jobs...</div>";
    const jobs = (await getJobsApi()) || [];
    jobsGrid.innerHTML = "";
    if (jobs.length === 0) {
      jobsGrid.innerHTML = "<div class='loading'>No jobs available</div>";
      return;
    }
    jobs.forEach(job => jobsGrid.appendChild(createJobCard(job)));
  } catch (error) {
    jobsGrid.innerHTML = "<div class='loading'>Error loading jobs</div>";
  }
}

function createJobCard(job) {
  const div = document.createElement("article");
  div.className = "job-card card-hover";
  div.onclick   = () => openJobModal(job);

  const salaryRange = `₹${formatSalary(job.minSalary)} – ₹${formatSalary(job.maxSalary)}`;
  const experience  = `${job.minExperience} – ${job.maxExperience} yrs`;

  div.innerHTML = `
    <div class="job-card-header">
      <h3 class="job-title">${escHtml(job.title)}</h3>
      <p class="job-company">Interview Platform</p>
    </div>
    <div class="job-meta">
      <div class="meta-item">
        <div class="meta-label">Location</div>
        <div class="meta-value">${escHtml(job.location)}</div>
      </div>
      <div class="meta-item">
        <div class="meta-label">Experience</div>
        <div class="meta-value">${experience}</div>
      </div>
    </div>
    <p class="job-description">${escHtml((job.description || "").substring(0, 100))}...</p>
    <div class="job-footer">
      <div class="job-salary">${salaryRange}</div>
      <div class="job-card-action"><a href="#" aria-label="View ${escHtml(job.title)} details">View More →</a></div>
    </div>
  `;
  return div;
}

async function checkActiveApplication() {
  const token = localStorage.getItem("token");
  const role  = (localStorage.getItem("role") || "").toLowerCase();
  if (!token || role !== "candidate") return;
  try {
    const data = await getMyApplicationApi();
    hasActiveApplication = !!data && !!data.status && data.status.toUpperCase() !== "REJECTED";
  } catch {
    hasActiveApplication = false;
  }
}

function openJobModal(job) {
  const salaryRange = `₹${formatSalary(job.minSalary)} – ₹${formatSalary(job.maxSalary)}`;
  const experience  = `${job.minExperience} – ${job.maxExperience} yrs`;

  const loggedInRole = (localStorage.getItem("role") || "").toLowerCase();
  const isViewOnly   = !!localStorage.getItem("token") && loggedInRole !== "candidate";
  const applyDisabled = hasActiveApplication || isViewOnly ? "disabled" : "";
  const applyLabel    = hasActiveApplication ? "Applied" : isViewOnly ? "View Only" : "Apply Now";
  const applyTitle    = isViewOnly
    ? 'title="Only candidates can apply for jobs"'
    : hasActiveApplication ? 'title="You already have an active application"' : "";

  jobDetailsContent.innerHTML = `
    <div>
      <h2 id="job-modal-title">${escHtml(job.title)}</h2>
      <p style="color:var(--primary);font-weight:700;margin-bottom:8px;">
        Interview Platform • ${escHtml(job.location)}
      </p>
      <div style="background:var(--bg-main);padding:16px;border-radius:6px;margin:20px 0;">
        <div style="display:grid;grid-template-columns:1fr 1fr;gap:16px;">
          <div>
            <p style="font-weight:700;font-size:13px;">Salary Range</p>
            <p style="font-weight:700;color:var(--primary);">${salaryRange}</p>
          </div>
          <div>
            <p style="font-weight:700;font-size:13px;">Experience</p>
            <p style="font-weight:700;">${experience}</p>
          </div>
        </div>
      </div>
      <h3>Description</h3>
      <p>${escHtml(job.description || "")}</p>
      <h3>Skills</h3>
      <p>${escHtml(job.skills || "Not specified")}</p>
      <div style="margin-top:24px;display:flex;gap:16px;align-items:center;flex-wrap:wrap;">
        <button class="btn" onclick="applyForJob(${job.id})" ${applyDisabled} ${applyTitle}
          style="min-height:unset;margin-top:0;padding:10px 24px;${hasActiveApplication || isViewOnly ? "opacity:0.5;cursor:not-allowed;" : ""}">${applyLabel}</button>
        <button class="btn-secondary" onclick="closeJobModal()" type="button"
          style="min-height:unset;margin-top:0;padding:10px 24px;">Close</button>
      </div>
    </div>
  `;

  jobModal.classList.add("show");
  jobModal.setAttribute("aria-hidden", "false");
  jobModal.querySelector(".modal-close").focus();
}

function closeJobModal() {
  jobModal.classList.remove("show");
  jobModal.setAttribute("aria-hidden", "true");
  jobDetailsContent.innerHTML = "";
}

function applyForJob(jobId) {
  const token   = localStorage.getItem("token");
  const userRole = localStorage.getItem("role");
  if (!token) {
    alert("Please sign up or log in to apply for jobs");
    window.location.href = "./auth/login.html";
    return;
  }
  if (!userRole || userRole.toLowerCase() !== "candidate") {
    alert("Only candidates can apply for jobs");
    return;
  }
  window.location.href = `./candidate/apply.html?jobId=${jobId}`;
}

function scrollToJobs() {
  const jobsSection = document.getElementById("jobs");
  if (jobsSection) jobsSection.scrollIntoView({ behavior: "smooth" });
}

function setupScrollBehavior() {
  document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener("click", function (e) {
      e.preventDefault();
      const target = document.querySelector(this.getAttribute("href"));
      if (target) target.scrollIntoView({ behavior: "smooth" });
    });
  });
}

function formatSalary(salary) {
  return new Intl.NumberFormat("en-US", { style: "decimal", maximumFractionDigits: 0 })
    .format(salary / 1000) + "L";
}

let _dashboardUrl = "./candidate/dashboard.html";

function updateNavForAuth() {
  const token = localStorage.getItem("token");
  const role  = (localStorage.getItem("role") || "").toLowerCase();
  if (!token) return;

  document.getElementById("nav-guest").style.display = "none";
  document.getElementById("nav-user").style.display  = "flex";

  const rawName  = localStorage.getItem("candidateName");
  const rawEmail = localStorage.getItem("email") || "";
  const name     = rawName && rawName !== "undefined" ? rawName.trim() : "";
  const email    = rawEmail && rawEmail !== "undefined" ? rawEmail.trim() : "";
  const display  = name || email;

  document.getElementById("nav-username").textContent = display;
  const initials = name
    ? name.split(" ").map(w => w[0]).join("").substring(0, 2).toUpperCase()
    : (email[0] || "?").toUpperCase();
  document.getElementById("nav-avatar").textContent = initials;

  const dashboardMap = {
    candidate: "./candidate/dashboard.html",
    hr:        "./hr/dashboard.html",
    panel:     "./panel/dashboard.html",
  };
  _dashboardUrl = dashboardMap[role] || "./candidate/dashboard.html";
}

window.addEventListener("click", function (e) {
  if (e.target === jobModal) closeJobModal();
});

window.goToDashboard  = function () { window.location.href = _dashboardUrl; };
window.homeLogout     = function () {
  ["token","role","email","candidateName","candidateMobile","candidateDob"]
    .forEach(k => localStorage.removeItem(k));
  window.location.reload();
};
window.scrollToJobs   = scrollToJobs;
window.closeJobModal  = closeJobModal;
window.applyForJob    = applyForJob;
