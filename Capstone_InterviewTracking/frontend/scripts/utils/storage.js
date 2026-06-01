export function saveToken(token) {
  localStorage.setItem("token", token);
}

export function getToken() {
  return localStorage.getItem("token");
}

export function saveAuthUser(user) {
  localStorage.setItem("email", user.email);
  localStorage.setItem("role", user.role);
}

export function getAuthRole() {
  return localStorage.getItem("role");
}

export function clearAuth() {
  localStorage.removeItem("token");
  localStorage.removeItem("email");
  localStorage.removeItem("role");
}
