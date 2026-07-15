import { useEffect, useState } from "react";
import { AuthContext } from "./authContext";
import { getProfile } from "../api/authApi";
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

  // The token only carries the email, so the display name comes from the API.
  useEffect(() => {
    if (!user || user.fullName) {
      return;
    }

    const loadName = async () => {
      try {
        const response = await getProfile();
        setUser((prev) =>
          prev ? { ...prev, fullName: response.data.data.full_name } : prev
        );
      } catch {
        // The email stays as the fallback display name.
      }
    };

    loadName();
  }, [user]);

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
