export function escHtml(text) {
  return String(text || "").replace(/[&<>"']/g, m =>
    ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#039;" }[m])
  );
}

export function fmtDate(dt) {
  return dt ? new Date(dt).toLocaleDateString("en-IN", { dateStyle: "medium" }) : "—";
}

export function fmtDatetime(dt) {
  return dt
    ? new Date(dt).toLocaleString("en-IN", { dateStyle: "medium", timeStyle: "short" })
    : "—";
}

export function statusBadgeClass(status) {
  const map = {
    SELECTED: "badge-selected",
    COMPLETED: "badge-completed",
    REJECTED: "badge-rejected",
    SCHEDULED: "badge-scheduled",
    APPLIED: "badge-applied",
  };
  return map[(status || "").toUpperCase()] || "badge-applied";
}

const _STAGES = ["PROFILING", "SCREENING", "L1", "L2", "HR"];

export function buildStageTrack(currentStage) {
  const idx = _STAGES.indexOf(currentStage);
  let html = "";
  _STAGES.forEach((s, i) => {
    const done   = i < idx;
    const active = i === idx;
    html += `
      <div class="stage-item">
        <div class="stage-dot ${active ? "active" : done ? "done" : ""}">
          ${done ? "✓" : i + 1}
        </div>
        <span class="stage-label ${active ? "active" : ""}">${s}</span>
      </div>`;
    if (i < _STAGES.length - 1) {
      html += `<div class="stage-connector ${done ? "done" : ""}"></div>`;
    }
  });
  return html;
}

export function fmtSalaryLPA(s) {
  if (s == null || s === 0) return "—";
  const lpa = s / 100000;
  return "₹" + (Number.isInteger(lpa) ? lpa : lpa.toFixed(1)) + "L";
}

export function starHtml(rating) {
  return "★".repeat(rating) + "☆".repeat(5 - rating);
}

export function togglePwd(id, btn) {
  const inp = document.getElementById(id);
  const isHidden = inp.type === "password";
  inp.type = isHidden ? "text" : "password";
  btn.querySelector(".eye-show").style.display = isHidden ? "none" : "";
  btn.querySelector(".eye-hide").style.display = isHidden ? "" : "none";
  btn.setAttribute("aria-label", isHidden ? "Hide password" : "Show password");
}

export function createStarRating() {
  let selectedRating = 0;
  const stars = document.querySelectorAll(".star");

  function render(rating) {
    stars.forEach(s => {
      const val = Number(s.dataset.value);
      s.classList.toggle("active", val <= rating);
      s.setAttribute("aria-checked", val <= rating ? "true" : "false");
    });
  }

  stars.forEach(star => {
    star.addEventListener("click", () => {
      selectedRating = Number(star.dataset.value);
      render(selectedRating);
    });
    star.addEventListener("mouseover", () => render(Number(star.dataset.value)));
    star.addEventListener("mouseout", () => render(selectedRating));
    star.addEventListener("keydown", e => {
      if (e.key === "Enter" || e.key === " ") {
        selectedRating = Number(star.dataset.value);
        render(selectedRating);
      }
    });
  });

  return {
    getRating: () => selectedRating,
    reset: () => { selectedRating = 0; render(0); },
  };
}

export function switchModalTab(tab) {
  document.querySelectorAll(".pd-tab-btn").forEach(b => {
    const isActive = b.id === "tab-btn-" + tab;
    b.classList.toggle("active", isActive);
    b.setAttribute("aria-selected", isActive ? "true" : "false");
  });
  document.querySelectorAll(".pd-tab-panel").forEach(p => p.classList.remove("active"));
  document.getElementById("modal-tab-" + tab).classList.add("active");
}
