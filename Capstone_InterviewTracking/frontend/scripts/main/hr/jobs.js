import { getJobsForHRApi, createJobApi, updateJobApi, deleteJobApi, toggleJobApi } from "../../api/job-api.js";
import { validateSalaryRange, validateExperienceRange } from "../../utils/validation.js";
import { escHtml, fmtSalaryLPA } from "../../utils/dom.js";
import { authGuard, initSidebar } from "./sidebar.js";

authGuard();
initSidebar();

let hrJobs = [];
let editingJobId = null;

async function loadJobs() {
  const grid = document.getElementById("jobs-grid");
  grid.innerHTML = '<p class="hr-loading">Loading…</p>';
  try {
    hrJobs = await getJobsForHRApi();
    if (!hrJobs || hrJobs.length === 0) {
      grid.innerHTML = '<p class="hr-loading">No jobs found. Click "+ Create JD" to add one.</p>';
      return;
    }
    grid.innerHTML = `<div class="jobs-hr-grid">${hrJobs.map(j => `
      <article class="hr-job-card ${j.active ? "" : "hr-job-inactive"}">
        <header class="hr-job-card-header">
          <div>
            <h2 class="hr-job-title">${escHtml(j.title)}</h2>
            <span class="badge ${j.active ? "badge-selected" : "badge-rejected"}">${j.active ? "Active" : "Inactive"}</span>
          </div>
          <span class="hr-job-type">${escHtml((j.jobType || "").replace("_", " "))}</span>
        </header>
        <p class="hr-job-desc">${escHtml((j.description || "").substring(0, 120))}${(j.description || "").length > 120 ? "…" : ""}</p>
        <dl class="hr-job-meta">
          <dt class="sr-only">Location</dt><dd>📍 ${escHtml(j.location)}</dd>
          <dt class="sr-only">Salary range</dt><dd>💰 ${fmtSalaryLPA(j.minSalary)} – ${fmtSalaryLPA(j.maxSalary)}</dd>
          <dt class="sr-only">Experience range</dt><dd>🧑‍💼 ${j.minExperience || 0}–${j.maxExperience || 0} yrs</dd>
        </dl>
        <div class="hr-job-actions">
          <button class="btn-sm btn-sm-info" onclick="editJob(${j.id})" type="button">Edit</button>
          <button class="btn-sm ${j.active ? "btn-sm-warning" : "btn-sm-success"}" onclick="toggleJob(${j.id})" type="button">${j.active ? "Deactivate" : "Activate"}</button>
          <button class="btn-sm btn-sm-danger" onclick="deleteJob(${j.id})" type="button"
                  ${j.hasApplications ? 'disabled title="Cannot delete — candidates have applied for this job"' : ''}>
            Delete
          </button>
        </div>
      </article>`).join("")}</div>`;
  } catch (err) {
    grid.innerHTML = `<p class="hr-loading" style="color:var(--status-error);">Error: ${escHtml(err.message)}</p>`;
  }
}

window.openJDModal = function (job) {
  editingJobId = job ? job.id : null;
  document.getElementById("jd-modal-title").textContent = job ? "Edit Job" : "Create Job";
  document.getElementById("jd-btn").textContent = job ? "Update Job" : "Create Job";
  document.getElementById("jd-error").textContent = "";
  document.getElementById("jd-success").textContent = "";
  if (job) {
    document.getElementById("jd-title").value = job.title || "";
    document.getElementById("jd-description").value = job.description || "";
    document.getElementById("jd-skills").value = job.skills || "";
    document.getElementById("jd-location").value = job.location || "";
    document.getElementById("jd-jobtype").value = job.jobType || "";
    document.getElementById("jd-min-salary").value = job.minSalary != null ? (job.minSalary / 100000) : "";
    document.getElementById("jd-max-salary").value = job.maxSalary != null ? (job.maxSalary / 100000) : "";
    document.getElementById("jd-min-exp").value = job.minExperience != null ? job.minExperience : "";
    document.getElementById("jd-max-exp").value = job.maxExperience != null ? job.maxExperience : "";
  } else {
    document.getElementById("jd-form").reset();
  }
  document.getElementById("jd-overlay").classList.add("show");
  document.getElementById("jd-modal").classList.add("open");
};

window.closeJDModal = function () {
  document.getElementById("jd-overlay").classList.remove("show");
  document.getElementById("jd-modal").classList.remove("open");
  editingJobId = null;
};

window.editJob = function (id) {
  const job = hrJobs.find(j => j.id === id);
  if (job) openJDModal(job);
};

window.deleteJob = async function (id) {
  if (!confirm("Delete this job? This action cannot be undone.")) return;
  try {
    await deleteJobApi(id);
    loadJobs();
  } catch (err) {
    alert("Error deleting job: " + err.message);
  }
};

window.toggleJob = async function (id) {
  try {
    await toggleJobApi(id);
    loadJobs();
  } catch (err) {
    alert("Error toggling job status: " + err.message);
  }
};

document.getElementById("jd-form").addEventListener("submit", async (e) => {
  e.preventDefault();
  const errEl = document.getElementById("jd-error");
  const successEl = document.getElementById("jd-success");
  const btn = document.getElementById("jd-btn");
  errEl.textContent = ""; successEl.textContent = "";

  const title = document.getElementById("jd-title").value.trim();
  const description = document.getElementById("jd-description").value.trim();
  const skills = document.getElementById("jd-skills").value.trim();
  const location = document.getElementById("jd-location").value.trim();
  const jobType = document.getElementById("jd-jobtype").value;

  if (!title) { errEl.textContent = "Job title is required."; return; }
  if (!description) { errEl.textContent = "Job description is required."; return; }
  if (!skills) { errEl.textContent = "Skills are required."; return; }
  if (!location) { errEl.textContent = "Location is required."; return; }
  if (!jobType) { errEl.textContent = "Please select a job type."; return; }

  const minSalRaw = document.getElementById("jd-min-salary").value;
  const maxSalRaw = document.getElementById("jd-max-salary").value;
  const minExpRaw = document.getElementById("jd-min-exp").value;
  const maxExpRaw = document.getElementById("jd-max-exp").value;

  if (minSalRaw === "") { errEl.textContent = "Minimum salary is required."; return; }
  if (maxSalRaw === "") { errEl.textContent = "Maximum salary is required."; return; }
  if (minExpRaw === "") { errEl.textContent = "Minimum experience is required."; return; }
  if (maxExpRaw === "") { errEl.textContent = "Maximum experience is required."; return; }

  const salErrors = validateSalaryRange(minSalRaw, maxSalRaw);
  if (salErrors.length) { errEl.textContent = salErrors[0]; return; }
  const expErrors = validateExperienceRange(minExpRaw, maxExpRaw);
  if (expErrors.length) { errEl.textContent = expErrors[0]; return; }

  const body = {
    title, description, skills, location, jobType,
    minSalary: parseFloat(minSalRaw) * 100000,
    maxSalary: parseFloat(maxSalRaw) * 100000,
    minExperience: parseInt(minExpRaw),
    maxExperience: parseInt(maxExpRaw),
  };

  btn.disabled = true;
  btn.textContent = editingJobId ? "Updating…" : "Creating…";
  try {
    if (editingJobId) {
      await updateJobApi(editingJobId, body);
      successEl.textContent = "Job updated successfully!";
    } else {
      await createJobApi(body);
      successEl.textContent = "Job created successfully!";
    }
    loadJobs();
    setTimeout(closeJDModal, 1500);
  } catch (err) {
    errEl.textContent = err.message;
  } finally {
    btn.disabled = false;
    btn.textContent = editingJobId ? "Update Job" : "Create Job";
  }
});

loadJobs();
