export const decodeJwt = (token) => {
  const payload = token.split(".")[1];
  const decoded = atob(payload.replace(/-/g, "+").replace(/_/g, "/"));

  return JSON.parse(decoded);
};

export const isTokenExpired = (token) => {
  const { exp } = decodeJwt(token);

  return Date.now() >= exp * 1000;
};
