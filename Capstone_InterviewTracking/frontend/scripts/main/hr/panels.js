import { getPanelsApi, createPanelApi } from "../../api/hr-api.js";
import { validateEmail, validateName, validatePhone } from "../../utils/validation.js";
import { escHtml } from "../../utils/dom.js";
import { authGuard, initSidebar } from "./sidebar.js";

authGuard();
initSidebar();

async function loadPanels() {
  const list = document.getElementById("panels-list");
  list.innerHTML = '<p class="hr-loading">Loading…</p>';
  try {
    const panels = await getPanelsApi();
    if (!panels || panels.length === 0) {
      list.innerHTML = '<p class="hr-loading">No panel members yet.</p>';
      return;
    }
    list.innerHTML = `
      <div style="overflow-x:auto;">
        <table>
          <thead>
            <tr>
              <th scope="col">Name</th><th scope="col">Email</th>
              <th scope="col">Phone</th><th scope="col">Organization</th>
              <th scope="col">Designation</th>
            </tr>
          </thead>
          <tbody>
            ${panels.map(p => `
              <tr>
                <td><strong>${escHtml(p.fullName)}</strong></td>
                <td>${escHtml(p.email)}</td>
                <td>${escHtml(p.phone || "—")}</td>
                <td>${escHtml(p.organization || "—")}</td>
                <td>${escHtml(p.designation || "—")}</td>
              </tr>`).join("")}
          </tbody>
        </table>
      </div>`;
  } catch (err) {
    list.innerHTML = `<p class="hr-loading" style="color:var(--status-error);">Error: ${escHtml(err.message)}</p>`;
  }
}

window.openPanelModal = function () {
  document.getElementById("pan-error").textContent = "";
  document.getElementById("pan-success").textContent = "";
  document.getElementById("panel-modal-overlay").classList.add("show");
  document.getElementById("panel-modal").classList.add("open");
};

window.closePanelModal = function () {
  document.getElementById("panel-modal-overlay").classList.remove("show");
  document.getElementById("panel-modal").classList.remove("open");
};

document.getElementById("panel-form").addEventListener("submit", async (e) => {
  e.preventDefault();
  const errEl = document.getElementById("pan-error");
  const successEl = document.getElementById("pan-success");
  const btn = document.getElementById("pan-btn");
  errEl.textContent = ""; successEl.textContent = "";

  const fullName = document.getElementById("pan-name").value.trim();
  const email = document.getElementById("pan-email").value.trim();
  const phone = document.getElementById("pan-phone").value.trim();
  const organization = document.getElementById("pan-org").value.trim();
  const designation = document.getElementById("pan-designation").value.trim();

  const nameErr = validateName(fullName);
  if (nameErr) { errEl.textContent = nameErr; return; }
  const emailErr = validateEmail(email);
  if (emailErr) { errEl.textContent = emailErr; return; }
  const phoneErr = validatePhone(phone);
  if (phoneErr) { errEl.textContent = phoneErr; return; }
  if (!organization) { errEl.textContent = "Organization is required."; return; }
  if (!designation) { errEl.textContent = "Designation is required."; return; }

  btn.disabled = true; btn.textContent = "Adding…";
  try {
    await createPanelApi({ fullName, email, phone, organization, designation });
    successEl.textContent = "Panel member added. Setup email sent.";
    e.target.reset();
    loadPanels();
    setTimeout(closePanelModal, 1500);
  } catch (err) {
    errEl.textContent = err.message;
  } finally {
    btn.disabled = false; btn.textContent = "Add Panel Member";
  }
});

loadPanels();
