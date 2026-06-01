import { clearAuth } from "../../utils/storage.js";

export function initSidebar() {
  const email = localStorage.getItem("email") || "";
  const nameEl = document.getElementById("hr-user-name");
  const emailEl = document.getElementById("hr-user-email");
  const avatarEl = document.getElementById("hr-avatar");
  if (nameEl) nameEl.textContent = email.split("@")[0] || "HR";
  if (emailEl) emailEl.textContent = email;
  if (avatarEl) avatarEl.textContent = (email[0] || "H").toUpperCase();
}

export function authGuard() {
  const role = localStorage.getItem("role");
  if (!localStorage.getItem("token") || (role || "").toLowerCase() !== "hr") {
    window.location.href = "../auth/login.html";
  }
}

window.toggleSidebar = function () {
  document.getElementById("hr-sidebar").classList.toggle("open");
  document.getElementById("hr-overlay").classList.toggle("show");
};

window.closeSidebar = function () {
  document.getElementById("hr-sidebar").classList.remove("open");
  document.getElementById("hr-overlay").classList.remove("show");
};

window.logout = function () {
  clearAuth();
  window.location.href = "../auth/login.html";
};
