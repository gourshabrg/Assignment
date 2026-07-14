import { ROLES } from "./constants";

export const getRoleLandingPath = (role) => {
  if (role === ROLES.DOCTOR) {
    return "/doctor/dashboard";
  }

  if (role === ROLES.ADMIN) {
    return "/admin/dashboard";
  }

  return "/doctors";
};
