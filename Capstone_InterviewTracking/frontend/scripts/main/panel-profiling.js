import { getCandidateDetailApi } from "../api/panel-api.js";
import { fmtDatetime, statusBadgeClass, buildStageTrack } from "../utils/dom.js";

const role = localStorage.getItem("role");
if (!localStorage.getItem("token") || (role || "").toLowerCase() !== "panel") {
  window.location.href = "../auth/login.html";
}

document.getElementById("nav-email").textContent = localStorage.getItem("email") || "";

const params      = new URLSearchParams(window.location.search);
const interviewId = params.get("interviewId");

if (!interviewId) {
  window.location.href = "dashboard.html";
}

function val(v, suffix) {
  if (v == null || v === "") return "—";
  return suffix ? v + " " + suffix : String(v);
}

function renderRoundCard(iv, isAssigned) {
  return `
    <article class="round-card ${isAssigned ? "round-card-assigned" : ""}">
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px;">
        <strong style="color:var(--primary);">${iv.round} Round</strong>
        <span class="badge ${statusBadgeClass(iv.status)}">${iv.status || "—"}</span>
      </div>
      <p style="font-size:13px;color:var(--text-muted);margin:3px 0;">
        <strong>Date:</strong> ${fmtDatetime(iv.interviewDateTime)}
      </p>
      ${iv.focusArea
        ? `<p style="font-size:13px;color:var(--text-muted);margin:3px 0;">
             <strong>Focus:</strong> ${iv.focusArea}
           </p>`
        : ""}
      ${iv.panels && iv.panels.length
        ? `<p style="font-size:12px;color:var(--text-muted);margin:6px 0 0;">
             Panel: ${iv.panels.map(p => p.name || p.email).join(", ")}
           </p>`
        : ""}
      ${isAssigned ? '<span class="assigned-tag">Your Interview</span>' : ""}
    </article>`;
}

async function load() {
  try {
    const d = await getCandidateDetailApi(interviewId);

    document.getElementById("loading").style.display       = "none";
    document.getElementById("profile-content").style.display = "block";

    const initials = (d.fullName || "?").split(" ").map(w => w[0]).join("").substring(0, 2).toUpperCase();
    document.getElementById("profile-avatar").textContent   = initials;
    document.getElementById("profile-name").textContent     = d.fullName || "—";
    document.getElementById("profile-job").textContent      = d.jobTitle || "";

    const statusBadge = document.getElementById("profile-status-badge");
    statusBadge.textContent = d.status || "—";
    statusBadge.className   = "badge " + statusBadgeClass(d.status);

    document.getElementById("profile-stage").textContent = d.stage || "—";

    if (d.resumeUrl) {
      const btn = document.getElementById("profile-resume-btn");
      btn.href         = d.resumeUrl;
      btn.style.display = "inline-block";
    }

    document.getElementById("stage-track").innerHTML = buildStageTrack(d.stage || "PROFILING");

    document.getElementById("p-email").textContent        = d.email || "—";
    document.getElementById("p-mobile").textContent       = val(d.mobile);
    document.getElementById("p-company").textContent      = val(d.currentCompany);
    document.getElementById("p-location").textContent     = val(d.preferredLocation);
    document.getElementById("p-total-exp").textContent    = d.totalExperience != null ? d.totalExperience + " yrs" : "—";
    document.getElementById("p-rel-exp").textContent      = d.relevantExperience != null ? d.relevantExperience + " yrs" : "—";
    document.getElementById("p-current-ctc").textContent  = d.currentCtc != null ? "₹" + d.currentCtc + " LPA" : "—";
    document.getElementById("p-expected-ctc").textContent = d.expectedCtc != null ? "₹" + d.expectedCtc + " LPA" : "—";
    document.getElementById("p-notice").textContent       = d.noticePeriod != null ? d.noticePeriod + " days" : "—";
    document.getElementById("p-source").textContent       = val(d.source);
    document.getElementById("p-applied").textContent      = fmtDatetime(d.appliedAt);

    document.getElementById("p-job-title").textContent    = val(d.jobTitle);
    document.getElementById("p-job-location").textContent = val(d.location);
    document.getElementById("p-job-type").textContent     = val(d.jobType);
    document.getElementById("p-skills").textContent       = val(d.skills);
    document.getElementById("p-job-desc").textContent     = d.jobDescription || "—";

    const thisIv = (d.interviews || []).find(iv => String(iv.id) === String(interviewId));
    if (thisIv) {
      document.getElementById("this-interview-body").innerHTML = `
        <p style="font-size:13px;color:var(--text-muted);margin-bottom:12px;">
          Details for the interview you are assigned to.
        </p>
        ${renderRoundCard(thisIv, true)}
        <a href="dashboard.html" class="btn"
           style="display:inline-block;margin-top:14px;padding:8px 18px;font-size:13px;
                  min-height:unset;text-decoration:none;">Submit Feedback</a>
      `;
    }

    const allInterviews = d.interviews || [];
    if (allInterviews.length === 0) {
      document.getElementById("interview-history").innerHTML = '<p class="no-data">No interviews scheduled yet.</p>';
    } else {
      document.getElementById("interview-history").innerHTML = allInterviews
        .map(iv => renderRoundCard(iv, String(iv.id) === String(interviewId)))
        .join("");
    }

  } catch (err) {
    document.getElementById("loading").style.display     = "none";
    document.getElementById("error-state").style.display = "block";
    document.getElementById("error-msg").textContent     = "Error: " + (err.message || "Failed to load profile.");
  }
}

load();
