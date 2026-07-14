import { useState } from "react";
import { AuthContext } from "./authContext";
import { decodeJwt, isTokenExpired } from "../utils/jwt";
import { TOKEN_STORAGE_KEY } from "../utils/constants";

const buildUserFromToken = (token) => {
  const claims = decodeJwt(token);

  return {
    id: claims.sub,
    email: claims.email,
    role: claims.role
  };
};

const loadInitialUser = () => {
  const token = localStorage.getItem(TOKEN_STORAGE_KEY);

  if (token && !isTokenExpired(token)) {
    return buildUserFromToken(token);
  }

  localStorage.removeItem(TOKEN_STORAGE_KEY);

  return null;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(loadInitialUser);

  const login = (token) => {
    localStorage.setItem(TOKEN_STORAGE_KEY, token);
    setUser(buildUserFromToken(token));
  };

  const logout = () => {
    localStorage.removeItem(TOKEN_STORAGE_KEY);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};
