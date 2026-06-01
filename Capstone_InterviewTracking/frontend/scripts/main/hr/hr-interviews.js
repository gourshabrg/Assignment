import { getHRInterviewsApi, getHRInterviewCandidateApi, submitHRFeedbackApi } from "../../api/hr-api.js";
import { escHtml, fmtDatetime, statusBadgeClass, createStarRating, switchModalTab } from "../../utils/dom.js";
import { authGuard, initSidebar } from "./sidebar.js";

authGuard();
initSidebar();

const starRating = createStarRating();

window.switchModalTab = switchModalTab;

window.openInterviewModal = function (interviewId, candidateName) {
  document.getElementById("hr-modal-title").textContent = `HR Interview — ${escHtml(candidateName)}`;
  document.getElementById("fb-interview-id").value = interviewId;
  document.getElementById("candidate-detail-body").innerHTML = '<p class="hr-loading">Loading…</p>';
  document.getElementById("fb-error").textContent = "";
  document.getElementById("fb-success").textContent = "";
  document.getElementById("feedback-form").reset();
  document.getElementById("fb-interview-id").value = interviewId;
  starRating.reset();
  switchModalTab("candidate");
  document.getElementById("hr-modal-overlay").classList.add("show");
  document.getElementById("hr-modal").classList.add("open");
  loadCandidateDetail(interviewId);
};

window.closeInterviewModal = function () {
  document.getElementById("hr-modal-overlay").classList.remove("show");
  document.getElementById("hr-modal").classList.remove("open");
};

async function loadCandidateDetail(interviewId) {
  try {
    const d = await getHRInterviewCandidateApi(interviewId);
    document.getElementById("candidate-detail-body").innerHTML = `
      <div style="margin-bottom:14px;display:flex;align-items:center;gap:12px;flex-wrap:wrap;">
        <strong style="font-size:15px;color:var(--text-dark);">${escHtml(d.fullName || "—")}</strong>
        ${d.resumeUrl
          ? `<a href="${d.resumeUrl}" target="_blank" rel="noopener"
               style="font-size:12px;color:var(--primary);font-weight:700;text-decoration:none;
                      padding:3px 10px;border:1px solid var(--primary);border-radius:6px;">
               View Resume
             </a>`
          : ""}
      </div>
      <dl class="info-grid">
        <div class="info-item"><dt>Email</dt><dd>${escHtml(d.email || "—")}</dd></div>
        <div class="info-item"><dt>Mobile</dt><dd>${escHtml(d.mobile || "—")}</dd></div>
        <div class="info-item"><dt>Current Company</dt><dd>${escHtml(d.currentCompany || "—")}</dd></div>
        <div class="info-item"><dt>Experience</dt><dd>${d.totalExperience != null ? d.totalExperience + " yrs" : "—"}</dd></div>
        <div class="info-item"><dt>Preferred Location</dt><dd>${escHtml(d.preferredLocation || "—")}</dd></div>
        <div class="info-item"><dt>Notice Period</dt><dd>${d.noticePeriod != null ? d.noticePeriod + " days" : "—"}</dd></div>
        <div class="info-item"><dt>Current CTC</dt><dd>${d.currentCtc != null ? "₹" + d.currentCtc + " LPA" : "—"}</dd></div>
        <div class="info-item"><dt>Expected CTC</dt><dd>${d.expectedCtc != null ? "₹" + d.expectedCtc + " LPA" : "—"}</dd></div>
      </dl>
      <p style="font-size:12px;font-weight:700;color:var(--primary);text-transform:uppercase;letter-spacing:.05em;margin:16px 0 8px;">Job Details</p>
      <dl class="info-grid">
        <div class="info-item"><dt>Job Title</dt><dd>${escHtml(d.jobTitle || "—")}</dd></div>
        <div class="info-item"><dt>Location</dt><dd>${escHtml(d.location || "—")}</dd></div>
        <div class="info-item"><dt>Stage</dt><dd>${escHtml(d.stage || "—")}</dd></div>
        <div class="info-item"><dt>Status</dt><dd>${escHtml(d.status || "—")}</dd></div>
      </dl>
    `;
  } catch (err) {
    document.getElementById("candidate-detail-body").innerHTML =
      `<p class="hr-loading" style="color:var(--status-error);">Error: ${escHtml(err.message)}</p>`;
  }
}

document.getElementById("feedback-form").addEventListener("submit", async (e) => {
  e.preventDefault();
  const errEl = document.getElementById("fb-error");
  const successEl = document.getElementById("fb-success");
  const btn = document.getElementById("fb-btn");
  errEl.textContent = ""; successEl.textContent = "";

  if (!starRating.getRating()) { errEl.textContent = "Please select a rating (1–5 stars)."; return; }

  const status = document.getElementById("fb-status").value;
  if (!status) { errEl.textContent = "Please select a recommendation."; return; }

  const areasCovered = document.getElementById("fb-areas").value.trim();
  if (!areasCovered) { errEl.textContent = "Areas Covered is required."; return; }

  const comments = document.getElementById("fb-comments").value.trim();
  if (!comments) { errEl.textContent = "Overall Comments is required."; return; }

  const interviewId = document.getElementById("fb-interview-id").value;
  btn.disabled = true; btn.textContent = "Submitting…";

  try {
    await submitHRFeedbackApi(interviewId, {
      rating: starRating.getRating(),
      status,
      areasCovered,
      comments,
      strengths: document.getElementById("fb-strengths").value.trim() || null,
      weaknesses: document.getElementById("fb-weaknesses").value.trim() || null,
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
    btn.disabled = false; btn.textContent = "Submit Feedback";
  }
});

async function loadInterviews() {
  const grid = document.getElementById("hr-interviews-grid");
  grid.innerHTML = '<p class="hr-loading">Loading…</p>';
  try {
    const data = await getHRInterviewsApi();
    if (!data || data.length === 0) {
      grid.innerHTML = '<p class="hr-loading">No HR round interviews scheduled yet.</p>';
      return;
    }
    grid.innerHTML = data.map(iv => `
      <article class="pd-interview-card">
        <p class="pd-interview-round">HR Round</p>
        <span class="badge ${statusBadgeClass(iv.status)}">${escHtml(iv.status)}</span>
        <p class="pd-interview-meta" style="margin-top:8px;">
          <strong>Candidate:</strong> ${escHtml(iv.candidateName || "—")}
        </p>
        <p class="pd-interview-meta">
          <strong>Date:</strong> ${fmtDatetime(iv.interviewDateTime)}
        </p>
        ${iv.focusArea
          ? `<p class="pd-interview-meta"><strong>Focus:</strong> ${escHtml(iv.focusArea)}</p>`
          : ""}
        <div class="pd-card-actions">
          <button class="btn-sm btn-sm-info"
                  onclick="openInterviewModal(${iv.id}, '${escHtml(iv.candidateName || "Candidate")}')"
                  type="button">
            View &amp; Feedback
          </button>
        </div>
      </article>`).join("");
  } catch (err) {
    grid.innerHTML = `<p class="hr-loading" style="color:var(--status-error);">Error: ${escHtml(err.message)}</p>`;
  }
}

loadInterviews();
