import {
  getCandidatesApi, getCandidateDetailApi,
  updateStageApi, rejectCandidateApi, selectCandidateApi,
  getInterviewFeedbackApi, createCandidateByHRApi
} from "../../api/hr-api.js";
import { getJobsApi } from "../../api/job-api.js";
import {
  validateEmail, validateName, validatePhone, validateDob
} from "../../utils/validation.js";
import { escHtml, fmtDate, statusBadgeClass, starHtml } from "../../utils/dom.js";
import { authGuard, initSidebar } from "./sidebar.js";

authGuard();
initSidebar();

const STAGES = ["PROFILING", "SCREENING", "L1", "L2", "HR"];
let allCandidates = [];
let jobs = [];
let currentDetailStage = "";
let jobMinExp = null;

window.loadCandidates = async function () {
  const wrap = document.getElementById("candidates-table-wrap");
  wrap.innerHTML = '<p class="hr-loading">Loading…</p>';
  try {
    const stage = document.getElementById("filter-stage").value;
    const status = document.getElementById("filter-status").value;
    allCandidates = await getCandidatesApi({ stage, status });
    if (!allCandidates || allCandidates.length === 0) {
      wrap.innerHTML = '<p class="hr-loading">No candidates found.</p>';
      return;
    }
    wrap.innerHTML = `
      <div style="overflow-x:auto;">
        <table>
          <thead>
            <tr>
              <th scope="col">App ID</th><th scope="col">Name</th><th scope="col">Email</th>
              <th scope="col">Job</th><th scope="col">Stage</th><th scope="col">Status</th>
              <th scope="col">Applied</th><th scope="col">Actions</th>
            </tr>
          </thead>
          <tbody>
            ${allCandidates.map(c => `
              <tr>
                <td>${c.applicationId}</td>
                <td><strong>${escHtml(c.fullName)}</strong></td>
                <td>${escHtml(c.email)}</td>
                <td>${escHtml(c.jobTitle)}</td>
                <td><span class="badge badge-applied">${escHtml(c.stage)}</span></td>
                <td><span class="badge ${statusBadgeClass(c.status)}">${escHtml(c.status)}</span></td>
                <td>${fmtDate(c.appliedAt)}</td>
                <td>
                  <div class="action-btns">
                    <button class="btn-sm btn-sm-info" onclick="viewDetail(${c.applicationId})" type="button">View</button>
                    ${c.status !== "REJECTED" && c.status !== "SELECTED"
                      ? `<button class="btn-sm btn-sm-danger" onclick="reject(${c.applicationId})" type="button">Reject</button>`
                      : ""}
                    ${c.stage === "HR" && c.status !== "SELECTED" && c.status !== "REJECTED"
                      ? `<button class="btn-sm btn-sm-success" onclick="select(${c.applicationId})" type="button">Select</button>`
                      : ""}
                  </div>
                </td>
              </tr>`).join("")}
          </tbody>
        </table>
      </div>`;
  } catch (err) {
    wrap.innerHTML = `<p class="hr-loading" style="color:var(--status-error);">Error: ${escHtml(err.message)}</p>`;
  }
};

window.viewDetail = async function (applicationId) {
  const drawer = document.getElementById("candidate-drawer");
  const overlay = document.getElementById("drawer-overlay");
  const body = document.getElementById("drawer-body");
  body.innerHTML = '<p class="hr-loading">Loading…</p>';
  document.getElementById("drawer-name").textContent = "Loading…";
  document.getElementById("drawer-badges").innerHTML = "";
  overlay.classList.add("show");
  drawer.classList.add("open");

  try {
    const d = await getCandidateDetailApi(applicationId);
    document.getElementById("drawer-name").textContent = d.fullName;
    document.getElementById("drawer-badges").innerHTML = `
      <span class="badge ${statusBadgeClass(d.status)}">${escHtml(d.status)}</span>
      <span class="badge badge-applied">${escHtml(d.stage)}</span>
      ${d.resumeUrl ? `<a href="${d.resumeUrl}" target="_blank" rel="noopener" class="btn-sm btn-sm-info">Resume ↗</a>` : ""}
    `;

    const stageIdx = STAGES.indexOf(d.stage);
    currentDetailStage = d.stage;

    body.innerHTML = `
      <p class="hr-drawer-section-title">Update Stage</p>
      <div class="stage-track">
        ${STAGES.map((s, i) => {
          const done    = i < stageIdx;
          const current = i === stageIdx;
          const isNext  = i === stageIdx + 1;
          const locked  = !done && !current && !isNext;
          const dotTitle = done    ? "Stage already completed"
                         : current ? "Current stage"
                         : isNext  ? `Move to ${s}`
                         :           "Complete previous stages first";
          return `
          <div class="stage-node ${done ? "done" : ""} ${current ? "active" : ""}">
            <div class="stage-dot${done || locked ? " stage-dot-locked" : ""}"
              title="${dotTitle}"
              ${isNext ? `onclick="updateStage(${applicationId}, '${s}')" role="button" tabindex="0"` : ""}>
              ${done ? "✓" : i + 1}
            </div>
            <div class="stage-label">${escHtml(s)}</div>
          </div>`;
        }).join("")}
      </div>

      <p class="hr-drawer-section-title">Candidate Info</p>
      <dl class="info-grid">
        <div class="info-item"><dt>Email</dt><dd>${escHtml(d.email)}</dd></div>
        <div class="info-item"><dt>Mobile</dt><dd>${escHtml(d.mobile || "—")}</dd></div>
        <div class="info-item"><dt>Current Company</dt><dd>${escHtml(d.currentCompany || "—")}</dd></div>
        <div class="info-item"><dt>Experience</dt><dd>${d.totalExperience != null ? d.totalExperience + " yrs" : "—"}</dd></div>
        <div class="info-item"><dt>Current CTC</dt><dd>${d.currentCtc != null ? "₹" + d.currentCtc + " LPA" : "—"}</dd></div>
        <div class="info-item"><dt>Expected CTC</dt><dd>${d.expectedCtc != null ? "₹" + d.expectedCtc + " LPA" : "—"}</dd></div>
        <div class="info-item"><dt>Notice Period</dt><dd>${d.noticePeriod != null ? d.noticePeriod + " days" : "—"}</dd></div>
        <div class="info-item"><dt>Preferred Location</dt><dd>${escHtml(d.preferredLocation || "—")}</dd></div>
      </dl>

      <p class="hr-drawer-section-title">Job</p>
      <dl class="info-grid">
        <div class="info-item"><dt>Title</dt><dd>${escHtml(d.jobTitle)}</dd></div>
        <div class="info-item"><dt>Location</dt><dd>${escHtml(d.location || "—")}</dd></div>
        <div class="info-item"><dt>Skills</dt><dd>${escHtml(d.skills || "—")}</dd></div>
        <div class="info-item"><dt>Applied On</dt><dd>${fmtDate(d.appliedAt)}</dd></div>
      </dl>

      <p class="hr-drawer-section-title">Interviews</p>
      ${!d.interviews || d.interviews.length === 0
        ? '<p style="color:var(--text-muted);font-size:13px;margin-bottom:16px;">No interviews scheduled yet.</p>'
        : d.interviews.map(iv => `
          <article class="feedback-card">
            <strong>${escHtml(iv.round)}</strong> — ${new Date(iv.interviewDateTime).toLocaleString("en-IN", { dateStyle: "medium", timeStyle: "short" })}
            <span class="badge ${statusBadgeClass(iv.status)}" style="margin-left:8px;">${escHtml(iv.status)}</span>
            ${iv.focusArea ? `<br><small style="color:var(--text-muted);">Focus: ${escHtml(iv.focusArea)}</small>` : ""}
            <br><small style="color:var(--text-muted);">Panel: ${(iv.panels || []).map(p => escHtml(p.fullName)).join(", ") || "HR Team"}</small>
            <br><button class="btn-sm btn-sm-info" style="margin-top:6px;" onclick="loadFeedback(${iv.id}, this)" type="button">View Feedback</button>
            <div class="feedback-for-${iv.id}" style="margin-top:8px;"></div>
          </article>`).join("")}

      ${d.feedbacks && d.feedbacks.length > 0 ? `
        <p class="hr-drawer-section-title">All Feedback</p>
        ${d.feedbacks.map(f => `
          <article class="feedback-card">
            <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:6px;flex-wrap:wrap;gap:6px;">
              <strong>${escHtml(f.round)}</strong>
              <span class="stars" aria-label="${f.rating} stars">${starHtml(f.rating)}</span>
              <span class="badge ${statusBadgeClass(f.status)}">${escHtml(f.status)}</span>
            </div>
            <p style="font-size:12px;color:var(--text-muted);margin:0 0 4px;"><strong>Reviewer:</strong> ${escHtml(f.panelName)}</p>
            <p style="font-size:13px;margin:4px 0;"><strong>Comments:</strong> ${escHtml(f.comments)}</p>
            ${f.strengths ? `<p style="font-size:13px;margin:4px 0;"><strong>Strengths:</strong> ${escHtml(f.strengths)}</p>` : ""}
            ${f.weaknesses ? `<p style="font-size:13px;margin:4px 0;"><strong>Weaknesses:</strong> ${escHtml(f.weaknesses)}</p>` : ""}
            ${f.areasCovered ? `<p style="font-size:13px;margin:4px 0;"><strong>Areas Covered:</strong> ${escHtml(f.areasCovered)}</p>` : ""}
          </article>`).join("")}` : ""}
    `;
  } catch (err) {
    body.innerHTML = `<p class="hr-loading" style="color:var(--status-error);">Error: ${escHtml(err.message)}</p>`;
  }
};

window.closeDrawer = function () {
  document.getElementById("candidate-drawer").classList.remove("open");
  document.getElementById("drawer-overlay").classList.remove("show");
};

window.loadFeedback = async function (interviewId, btn) {
  const container = document.querySelector(`.feedback-for-${interviewId}`);
  btn.disabled = true;
  try {
    const feedbacks = await getInterviewFeedbackApi(interviewId);
    if (!feedbacks || feedbacks.length === 0) {
      container.innerHTML = '<p style="font-size:12px;color:var(--text-muted);">No feedback yet.</p>';
      return;
    }
    container.innerHTML = feedbacks.map(f => `
      <div style="border-top:1px solid var(--border-light);padding-top:8px;margin-top:8px;">
        <div style="display:flex;gap:8px;align-items:center;margin-bottom:4px;flex-wrap:wrap;">
          <strong style="font-size:12px;">${escHtml(f.panelName)}</strong>
          <span class="stars" style="font-size:13px;" aria-label="${f.rating} stars">${starHtml(f.rating)}</span>
          <span class="badge ${statusBadgeClass(f.status)}" style="font-size:10px;">${escHtml(f.status)}</span>
        </div>
        <p style="font-size:12px;margin:2px 0;">${escHtml(f.comments)}</p>
        ${f.areasCovered ? `<p style="font-size:11px;color:var(--text-muted);margin:2px 0;">Areas: ${escHtml(f.areasCovered)}</p>` : ""}
      </div>`).join("");
  } catch (err) {
    container.innerHTML = `<p style="font-size:12px;color:var(--status-error);">${escHtml(err.message)}</p>`;
  }
};

window.updateStage = async function (applicationId, stage) {
  const currentIdx = STAGES.indexOf(currentDetailStage);
  const targetIdx  = STAGES.indexOf(stage);
  if (targetIdx !== currentIdx + 1) {
    alert(`Stage must progress sequentially. Next stage: ${STAGES[currentIdx + 1] || "none"}`);
    return;
  }
  if (!confirm(`Move candidate to stage: ${stage}?`)) return;
  try {
    await updateStageApi(applicationId, { stage });
    currentDetailStage = stage;
    loadCandidates();
    viewDetail(applicationId);
  } catch (err) {
    alert("Error: " + err.message);
  }
};

window.reject = async function (applicationId) {
  if (!confirm("Reject this candidate?")) return;
  try {
    await rejectCandidateApi(applicationId);
    closeDrawer();
    loadCandidates();
  } catch (err) {
    alert("Error: " + err.message);
  }
};

window.select = async function (applicationId) {
  if (!confirm("Select this candidate for final offer?")) return;
  try {
    await selectCandidateApi(applicationId);
    closeDrawer();
    loadCandidates();
  } catch (err) {
    alert("Error: " + err.message);
  }
};

window.openOnboardModal = async function () {
  document.getElementById("onboard-overlay").classList.add("show");
  document.getElementById("onboard-modal").classList.add("open");
  const yesterday = new Date();
  yesterday.setDate(yesterday.getDate() - 1);
  document.getElementById("ob-dob").max = yesterday.toISOString().split("T")[0];
  if (jobs.length === 0) {
    try {
      jobs = await getJobsApi();
      const sel = document.getElementById("ob-job");
      sel.innerHTML = '<option value="">Select job…</option>' +
        jobs.map(j => `<option value="${j.id}">${escHtml(j.title)} (ID: ${j.id})</option>`).join("");
    } catch {  }
  }
};

window.closeOnboardModal = function () {
  document.getElementById("onboard-overlay").classList.remove("show");
  document.getElementById("onboard-modal").classList.remove("open");
};

document.getElementById("ob-job").addEventListener("change", function () {
  const relExpEl = document.getElementById("ob-rel-exp");
  const selectedId = this.value;
  if (!selectedId) {
    jobMinExp = null;
    relExpEl.value = "";
    return;
  }
  const job = jobs.find(j => String(j.id) === selectedId);
  if (job && job.minExperience != null) {
    jobMinExp = Number(job.minExperience);
    relExpEl.value = job.minExperience;
  } else {
    jobMinExp = null;
    relExpEl.value = "";
  }
});

document.getElementById("ob-resume").addEventListener("change", function () {
  const errEl = document.getElementById("ob-resume-error");
  errEl.textContent = "";
  const file = this.files[0];
  if (!file) return;
  const isPdf = file.type === "application/pdf" || file.name.toLowerCase().endsWith(".pdf");
  if (!isPdf) { errEl.textContent = "Only PDF files are allowed."; this.value = ""; return; }
  if (file.size > 5 * 1024 * 1024) { errEl.textContent = "File must be under 5 MB."; this.value = ""; }
});

document.getElementById("onboard-form").addEventListener("submit", async (e) => {
  e.preventDefault();
  const errEl = document.getElementById("ob-error");
  const successEl = document.getElementById("ob-success");
  const btn = document.getElementById("ob-btn");
  errEl.textContent = ""; successEl.textContent = "";

  const name = document.getElementById("ob-name").value.trim();
  const email = document.getElementById("ob-email").value.trim();
  const mobile = document.getElementById("ob-mobile").value.trim();
  const dob = document.getElementById("ob-dob").value;

  const nameErr = validateName(name);
  if (nameErr) { errEl.textContent = nameErr; return; }
  const emailErr = validateEmail(email);
  if (emailErr) { errEl.textContent = emailErr; return; }
  const mobileErr = validatePhone(mobile);
  if (mobileErr) { errEl.textContent = mobileErr; return; }

  const dobErr = validateDob(dob, 18);
  if (dobErr) { errEl.textContent = dobErr; return; }

  if (!document.getElementById("ob-job").value) { errEl.textContent = "Please select a job."; return; }

  const company = document.getElementById("ob-company").value.trim();
  if (!company) { errEl.textContent = "Current company is required."; return; }

  const totalExpRaw = document.getElementById("ob-total-exp").value;
  if (totalExpRaw === "") { errEl.textContent = "Total experience is required."; return; }
  const totalExp = parseFloat(totalExpRaw);
  if (isNaN(totalExp) || totalExp < 0) { errEl.textContent = "Total experience must be a valid non-negative number."; return; }
  if (totalExp > 50) { errEl.textContent = "Total experience cannot exceed 50 years."; return; }
  if (jobMinExp !== null && totalExp < jobMinExp) {
    errEl.textContent = `Total experience must be at least ${jobMinExp} year(s) as required for this role.`;
    return;
  }

  const relExp = document.getElementById("ob-rel-exp").value;

  const curCtcRaw = document.getElementById("ob-current-ctc").value;
  if (curCtcRaw === "") { errEl.textContent = "Current CTC is required."; return; }
  const curCtc = parseFloat(curCtcRaw);
  if (isNaN(curCtc) || curCtc < 0) { errEl.textContent = "Current CTC must be a valid non-negative number."; return; }

  const expCtcRaw = document.getElementById("ob-expected-ctc").value;
  if (expCtcRaw === "") { errEl.textContent = "Expected CTC is required."; return; }
  const expCtc = parseFloat(expCtcRaw);
  if (isNaN(expCtc) || expCtc < 0) { errEl.textContent = "Expected CTC must be a valid non-negative number."; return; }

  const noticeRaw = document.getElementById("ob-notice").value;
  if (noticeRaw === "") { errEl.textContent = "Notice period is required."; return; }
  const notice = parseInt(noticeRaw, 10);
  if (isNaN(notice) || notice < 0) { errEl.textContent = "Notice period must be a valid non-negative whole number."; return; }

  const resumeFile = document.getElementById("ob-resume").files[0];
  if (!resumeFile) { errEl.textContent = "Please select a resume PDF."; return; }
  const isPdf = resumeFile.type === "application/pdf" || resumeFile.name.toLowerCase().endsWith(".pdf");
  if (!isPdf) { document.getElementById("ob-resume-error").textContent = "Only PDF files are allowed."; return; }
  if (resumeFile.size > 5 * 1024 * 1024) { document.getElementById("ob-resume-error").textContent = "File must be under 5 MB."; return; }

  const fd = new FormData();
  fd.append("fullName", name);
  fd.append("email", email);
  fd.append("mobile", mobile);
  fd.append("dob", dob);
  fd.append("resume", resumeFile);
  fd.append("jobId", document.getElementById("ob-job").value);
  fd.append("currentCompany", company);
  fd.append("totalExperience", totalExp);
  if (relExp) fd.append("relevantExperience", relExp);
  fd.append("currentCtc", curCtc);
  fd.append("expectedCtc", expCtc);
  fd.append("noticePeriod", notice);
  const loc = document.getElementById("ob-location").value.trim();
  if (loc) fd.append("preferredLocation", loc);
  const src = document.getElementById("ob-source").value.trim();
  if (src) fd.append("source", src);

  btn.disabled = true; btn.textContent = "Onboarding…";
  try {
    await createCandidateByHRApi(fd);
    successEl.textContent = "Candidate onboarded! Setup email sent.";
    e.target.reset();
    jobMinExp = null;
    allCandidates = [];
    loadCandidates();
    setTimeout(closeOnboardModal, 1800);
  } catch (err) {
    errEl.textContent = err.message;
  } finally {
    btn.disabled = false; btn.textContent = "Onboard";
  }
});

loadCandidates();
