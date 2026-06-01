import { getMyInterviewsApi, getCandidateDetailApi, submitFeedbackApi } from "../api/panel-api.js";
import { clearAuth } from "../utils/storage.js";
import { escHtml, fmtDatetime, statusBadgeClass, createStarRating, switchModalTab } from "../utils/dom.js";

const role = localStorage.getItem("role");
if (!localStorage.getItem("token") || (role || "").toLowerCase() !== "panel") {
  window.location.href = "../auth/login.html";
}

const email = localStorage.getItem("email") || "";
document.getElementById("pd-user-email").textContent = email;
document.getElementById("pd-avatar").textContent = (email[0] || "P").toUpperCase();

window.openSidebar = function () {
  document.getElementById("pd-sidebar").classList.add("open");
  document.getElementById("pd-overlay").classList.add("show");
};

window.closeSidebar = function () {
  document.getElementById("pd-sidebar").classList.remove("open");
  document.getElementById("pd-overlay").classList.remove("show");
};

const starRating = createStarRating();

window.switchModalTab = switchModalTab;

window.openInterviewModal = function (interviewId, round) {
  document.getElementById("pd-modal-title").textContent = round + " Round";
  document.getElementById("fb-interview-id").value = interviewId;
  document.getElementById("candidate-detail-body").innerHTML =
    '<p class="loading-text">Loading...</p>';
  document.getElementById("fb-error").textContent = "";
  document.getElementById("fb-success").textContent = "";

  document.getElementById("feedback-form").reset();
  document.getElementById("fb-interview-id").value = interviewId;
  starRating.reset();

  switchModalTab("candidate");
  document.getElementById("pd-modal-overlay").classList.add("show");
  document.getElementById("pd-modal").classList.add("open");

  loadCandidateDetail(interviewId);
};

window.closeInterviewModal = function () {
  document.getElementById("pd-modal-overlay").classList.remove("show");
  document.getElementById("pd-modal").classList.remove("open");
};

async function loadCandidateDetail(interviewId) {
  try {
    const d = await getCandidateDetailApi(interviewId);
    const iv = (d.interviews || [])[0] || {};
    document.getElementById("candidate-detail-body").innerHTML = `
      <div style="margin-bottom:14px;display:flex;align-items:center;gap:12px;flex-wrap:wrap;">
        <strong style="font-size:15px;color:var(--text-dark);">${escHtml(d.fullName || "—")}</strong>
        ${d.resumeUrl
          ? `<a href="${d.resumeUrl}" target="_blank"
               style="font-size:12px;color:var(--primary);font-weight:700;text-decoration:none;
                      padding:3px 14px;border:1px solid var(--primary);border-radius:20px;">
               View Resume
             </a>`
          : ""}
      </div>
      <div class="info-grid">
        <div class="info-item"><label>Email</label><p>${escHtml(d.email || "—")}</p></div>
        <div class="info-item"><label>Mobile</label><p>${escHtml(d.mobile || "—")}</p></div>
        <div class="info-item"><label>Current Company</label><p>${escHtml(d.currentCompany || "—")}</p></div>
        <div class="info-item"><label>Experience</label><p>${d.totalExperience != null ? d.totalExperience + " yrs" : "—"}</p></div>
        <div class="info-item"><label>Preferred Location</label><p>${escHtml(d.preferredLocation || "—")}</p></div>
        <div class="info-item"><label>Notice Period</label><p>${d.noticePeriod != null ? d.noticePeriod + " days" : "—"}</p></div>
      </div>
      <p style="font-size:12px;font-weight:700;color:var(--primary);text-transform:uppercase;
                letter-spacing:.05em;margin:16px 0 8px;">Job Details</p>
      <div class="info-grid">
        <div class="info-item"><label>Job Title</label><p>${escHtml(d.jobTitle || "—")}</p></div>
        <div class="info-item"><label>Location</label><p>${escHtml(d.location || "—")}</p></div>
        <div class="info-item"><label>Skills Required</label><p>${escHtml(d.skills || "—")}</p></div>
        <div class="info-item"><label>Description</label><p>${escHtml((d.jobDescription || "—").substring(0, 120))}…</p></div>
      </div>
      ${iv.focusArea
        ? `<div style="background:var(--primary-light);padding:12px 16px;border-radius:8px;
                       border-left:3px solid var(--primary);margin-top:12px;">
             <p style="font-size:12px;font-weight:700;color:var(--primary);
                       text-transform:uppercase;letter-spacing:.05em;margin:0 0 4px;">
               Focus Area for this Interview
             </p>
             <p style="font-size:14px;margin:0;">${escHtml(iv.focusArea)}</p>
           </div>`
        : ""}
    `;
  } catch (err) {
    document.getElementById("candidate-detail-body").innerHTML =
      `<p class="no-data">Error loading candidate: ${escHtml(err.message)}</p>`;
  }
}

document.getElementById("feedback-form").addEventListener("submit", async (e) => {
  e.preventDefault();

  const errEl     = document.getElementById("fb-error");
  const successEl = document.getElementById("fb-success");
  const btn       = document.getElementById("fb-btn");
  errEl.textContent     = "";
  successEl.textContent = "";

  if (!starRating.getRating()) {
    errEl.textContent = "Please select a rating (1–5 stars).";
    return;
  }

  const status = document.getElementById("fb-status").value;
  if (!status) {
    errEl.textContent = "Please select a recommendation (Selected / Rejected).";
    return;
  }

  const areasCovered = document.getElementById("fb-areas").value.trim();
  if (!areasCovered) {
    errEl.textContent = "Areas Covered is required.";
    return;
  }

  const comments = document.getElementById("fb-comments").value.trim();
  if (!comments) {
    errEl.textContent = "Overall Comments is required.";
    return;
  }

  const interviewId = document.getElementById("fb-interview-id").value;
  btn.disabled     = true;
  btn.textContent  = "Submitting...";

  try {
    await submitFeedbackApi(interviewId, {
      rating       : starRating.getRating(),
      status,
      areasCovered,
      comments,
      strengths  : document.getElementById("fb-strengths").value.trim()  || null,
      weaknesses : document.getElementById("fb-weaknesses").value.trim() || null,
    });

    successEl.textContent = "Feedback submitted successfully!";
    starRating.reset();
    document.getElementById("feedback-form").reset();
    document.getElementById("fb-interview-id").value = interviewId;

    loadInterviews();
    setTimeout(closeInterviewModal, 1500);
  } catch (err) {
    errEl.textContent = err.message || "Failed to submit feedback.";
  } finally {
    btn.disabled    = false;
    btn.textContent = "Submit Feedback";
  }
});

async function loadInterviews() {
  const grid = document.getElementById("interviews-grid");
  grid.innerHTML = '<p class="loading-text">Loading...</p>';
  try {
    const data = await getMyInterviewsApi();
    if (!data || data.length === 0) {
      grid.innerHTML = '<p class="no-data">No interviews assigned to you yet.</p>';
      return;
    }
    grid.innerHTML = data.map(iv => `
      <div class="pd-interview-card">
        <div class="pd-interview-round">${escHtml(iv.round)} Round</div>
        <span class="badge ${statusBadgeClass(iv.status)}">${escHtml(iv.status)}</span>
        <p class="pd-interview-meta" style="margin-top:6px;">
          <strong>Date:</strong> ${fmtDatetime(iv.interviewDateTime)}
        </p>
        ${iv.focusArea
          ? `<p class="pd-interview-meta"><strong>Focus:</strong> ${escHtml(iv.focusArea)}</p>`
          : ""}
        <div class="pd-card-actions">
          <button class="btn-sm btn-sm-info"
                  onclick="openInterviewModal(${iv.id}, '${escHtml(iv.round)}')">
            Submit Feedback
          </button>
          <a href="profiling.html?interviewId=${iv.id}" class="btn-sm btn-sm-primary">
            View Full Profile
          </a>
        </div>
      </div>
    `).join("");
  } catch (err) {
    grid.innerHTML = `<p class="no-data">Error: ${escHtml(err.message)}</p>`;
  }
}

window.panelLogout = function () {
  clearAuth();
  window.location.href = "../auth/login.html";
};

loadInterviews();
