import { getCandidatesApi, getPanelsApi, scheduleInterviewApi } from "../../api/hr-api.js";
import { escHtml } from "../../utils/dom.js";
import { authGuard, initSidebar } from "./sidebar.js";

authGuard();
initSidebar();

let allCandidates = [];
let allPanels = [];
let candidatePickerData = [];

window.onRoundChange = function () {
  const round = document.getElementById("sch-round").value;
  const panelSection = document.getElementById("panel-section");
  const hrNote = document.getElementById("hr-round-note");
  if (round === "HR") {
    panelSection.style.display = "none";
    hrNote.style.display = "";
  } else {
    panelSection.style.display = "";
    hrNote.style.display = "none";
  }
};

async function loadCandidatePicker() {
  const list = document.getElementById("candidate-picker-list");
  list.innerHTML = '<p style="padding:12px;font-size:13px;color:var(--text-muted);">Loading…</p>';
  try {
    if (allCandidates.length === 0) allCandidates = await getCandidatesApi({});
    candidatePickerData = allCandidates.filter(c => c.status !== "REJECTED");
    renderCandidatePicker(candidatePickerData);
  } catch (err) {
    list.innerHTML = `<p style="padding:12px;font-size:13px;color:var(--status-error);">${escHtml(err.message)}</p>`;
  }
}

function renderCandidatePicker(data) {
  const list = document.getElementById("candidate-picker-list");
  if (!data || data.length === 0) {
    list.innerHTML = '<p style="padding:12px;font-size:13px;color:var(--text-muted);">No candidates found.</p>';
    return;
  }
  list.innerHTML = data.map(c => `
    <div class="candidate-picker-item" role="option"
         onclick="selectCandidateForSchedule(${c.applicationId}, '${c.fullName.replace(/'/g, "\\'")}')">
      <span>${escHtml(c.fullName)}</span>
      <span class="cpi-id">App #${c.applicationId}</span>
    </div>`).join("");
}

window.filterCandidatePicker = function () {
  const q = document.getElementById("candidate-search").value.toLowerCase();
  const filtered = candidatePickerData.filter(c =>
    c.fullName.toLowerCase().includes(q) || String(c.applicationId).includes(q)
  );
  renderCandidatePicker(filtered);
};

window.selectCandidateForSchedule = function (appId, name) {
  document.getElementById("sch-appId").value = appId;
  document.getElementById("candidate-search").value = "";
  document.getElementById("candidate-picker-list").style.display = "none";
  const sel = document.getElementById("candidate-selected");
  sel.style.display = "flex";
  document.getElementById("candidate-selected-text").textContent = `${name} (App #${appId})`;
};

window.clearCandidateSelection = function () {
  document.getElementById("sch-appId").value = "";
  document.getElementById("candidate-picker-list").style.display = "";
  document.getElementById("candidate-selected").style.display = "none";
  document.getElementById("candidate-search").value = "";
};

async function loadPanelCheckboxes() {
  const container = document.getElementById("panel-checkboxes");
  try {
    if (allPanels.length === 0) allPanels = await getPanelsApi();
    if (!allPanels || allPanels.length === 0) {
      container.innerHTML = '<p style="font-size:13px;color:var(--text-muted);">No panels available. Add a panel first.</p>';
      return;
    }
    container.innerHTML = allPanels.map(p => `
      <label>
        <input type="checkbox" name="panel" value="${p.id}">
        ${escHtml(p.fullName)} — ${escHtml(p.designation || p.organization || "")}
      </label>`).join("");
  } catch (err) {
    container.innerHTML = `<p style="color:var(--status-error);font-size:13px;">${escHtml(err.message)}</p>`;
  }
}

function setMinDatetime() {
  const now = new Date();
  const pad = n => String(n).padStart(2, "0");
  const minVal = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}T${pad(now.getHours())}:${pad(now.getMinutes())}`;
  document.getElementById("sch-datetime").min = minVal;
}

document.getElementById("schedule-form").addEventListener("submit", async (e) => {
  e.preventDefault();
  const errEl = document.getElementById("sch-error");
  const successEl = document.getElementById("sch-success");
  const btn = document.getElementById("sch-btn");
  errEl.textContent = ""; successEl.textContent = "";

  const appId = document.getElementById("sch-appId").value;
  if (!appId) { errEl.textContent = "Please select a candidate."; return; }

  const round = document.getElementById("sch-round").value;
  if (!round) { errEl.textContent = "Please select a round."; return; }

  const dt = document.getElementById("sch-datetime").value;
  if (!dt || new Date(dt) <= new Date()) {
    errEl.textContent = "Please select a future date and time."; return;
  }

  const isHrRound = round === "HR";
  let panelIds = [];

  if (!isHrRound) {
    const checked = [...document.querySelectorAll('input[name="panel"]:checked')];
    if (checked.length === 0 || checked.length > 2) {
      errEl.textContent = "Select 1 or 2 panel members."; return;
    }
    panelIds = checked.map(c => Number(c.value));
  }

  btn.disabled = true; btn.textContent = "Scheduling…";
  try {
    const body = {
      applicationId: Number(appId),
      round,
      interviewDateTime: dt,
      focusArea: document.getElementById("sch-focus").value.trim() || null,
    };
    if (!isHrRound) body.panelIds = panelIds;

    await scheduleInterviewApi(body);
    successEl.textContent = isHrRound
      ? "HR interview scheduled! Emails sent to you and the candidate."
      : "Interview scheduled! Email sent to candidate.";
    e.target.reset();
    clearCandidateSelection();
    setMinDatetime();
    onRoundChange();
  } catch (err) {
    errEl.textContent = err.message;
  } finally {
    btn.disabled = false; btn.textContent = "Schedule";
  }
});

loadCandidatePicker();
loadPanelCheckboxes();
setMinDatetime();
