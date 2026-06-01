import { loginApi, signupApi, setPasswordApi } from "../api/auth-api.js";
import { saveAuthUser, saveToken } from "../utils/storage.js";
import { validatePassword, validateEmail, validateName, validatePhone, validateDob } from "../utils/validation.js";
import { togglePwd } from "../utils/dom.js";

window.togglePwd = togglePwd;

function encodeBase64(str) {
  return btoa(unescape(encodeURIComponent(str)));
}

(function redirectIfLoggedIn() {
  if (window.location.pathname.includes("set-password")) return;
  const token = localStorage.getItem("token");
  if (!token) return;
  const role = (localStorage.getItem("role") || "").toLowerCase();
  const dest = role === "hr"
    ? "../hr/dashboard.html"
    : role === "panel"
      ? "../panel/dashboard.html"
      : "../index.html";
  window.location.replace(dest);
})();

const loginForm = document.getElementById("login-form");
const signupBtn = document.getElementById("signup-btn");
const loader = document.getElementById("loader");
const btnText = document.getElementById("btn-text");
const formContainer = document.querySelector(".form-container");

if (loginForm) {
  loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;
    const error = document.getElementById("error");
    error.innerText = "";
    if (!email || !password) {
      error.innerText = "Email and password are required";
      return;
    }
    const emailErr = validateEmail(email);
    if (emailErr) { error.innerText = emailErr; return; }

    try {
      const encodedPassword = encodeBase64(password);
      const data = await loginApi({ email, password: encodedPassword });
      saveToken(data.token);
      saveAuthUser({ email: data.email, role: data.role });

      const role = (data.role || "").toLowerCase();
      if (role === "hr") {
        window.location.replace("../hr/dashboard.html");
      } else if (role === "panel") {
        window.location.replace("../panel/dashboard.html");
      } else {
        window.location.replace("../index.html");
      }
    } catch (err) {
      console.error(err);
      error.innerText = err.message;
    }
  });
}

const signupForm = document.getElementById("signup-form");

if (signupForm) {
  signupForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    const name   = document.getElementById("name").value.trim();
    const email  = document.getElementById("email").value.trim();
    const phone  = document.getElementById("phone").value.trim();
    const dob    = document.getElementById("dob").value;
    const gender = document.getElementById("gender").value;
    const error  = document.getElementById("error");
    error.innerText = "";

    const nameErr  = validateName(name);
    if (nameErr)  { error.innerText = nameErr; return; }

    const emailErr = validateEmail(email);
    if (emailErr) { error.innerText = emailErr; return; }

    const phoneErr = validatePhone(phone);
    if (phoneErr) { error.innerText = phoneErr; return; }

    const dobErr = validateDob(dob, 18);
    if (dobErr)   { error.innerText = dobErr; return; }

    if (!gender)  { error.innerText = "Please select a gender."; return; }

    try {

      loader.classList.remove("hidden");
      btnText.textContent = "Sending...";
      signupBtn.disabled = true;
      formContainer.classList.add("blur");

      await signupApi({ fullName: name, email, phone, dob, gender });

      localStorage.setItem("candidateName", name);
      localStorage.setItem("candidateMobile", phone);
      localStorage.setItem("candidateDob", dob);

      window.location.href = "login.html";
    } catch (err) {
      console.error(err);
      if (err.message) {
        error.innerText = err.message;
      } else {
        error.innerText = "Something went wrong";
      }
    } finally {
      loader.classList.add("hidden");
      btnText.textContent = "Send setup link";
      signupBtn.disabled = false;
      formContainer.classList.remove("blur");
    }
  });
}

const loginLink = document.getElementById("loginLink");
if (loginLink) {
  loginLink.addEventListener("click", () => {
    window.location.href = "login.html";
  });
}

const setPasswordForm = document.getElementById("set-password-form");

if (setPasswordForm) {
  setPasswordForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirm-password").value;
    const error = document.getElementById("error");

    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get("token");

    error.innerText = "";

    if (!token) {
      error.innerText = "Invalid or missing token";
      return;
    }

    const errors = validatePassword(password);
    if (errors.length > 0) {
      error.innerText = errors.join("\n");
      return;
    }
    if (password !== confirmPassword) {
      error.innerText = "Passwords do not match";
      return;
    }

    try {
      const encodedPassword = encodeBase64(password);
      await setPasswordApi({ token, password: encodedPassword });
      window.location.href = "login.html";
    } catch (err) {
      console.error(err);
      error.innerText = err.message;
    }
  });
}
