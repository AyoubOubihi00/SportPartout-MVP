import React, { useState, useCallback } from "react";
import { AuthContext } from "./AuthContextContext";
import { isJwtExpired } from "../../services/jwtUtils";
import {
  login as loginService,
  LoginPayload,
  LoginResponse,
} from "../../services/auth/authService";

export interface AuthContextType {
  token: string | null;
  login: (payload: LoginPayload) => Promise<LoginResponse>;
  logout: () => void;
}

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  // Récupérer le token depuis localStorage au chargement
  const [token, setToken] = useState<string | null>(() => {
    const stored = localStorage.getItem("authToken");
    if (stored && isJwtExpired(stored)) {
      localStorage.removeItem("authToken");
      return null;
    }
    return stored;
  });

  const login = useCallback(async (payload: LoginPayload) => {
    const result = await loginService(payload);
    if (result.success && result.token) {
      setToken(result.token);
      localStorage.setItem("authToken", result.token);
    }
    return result;
  }, []);

  const logout = useCallback(() => {
    setToken(null);
    localStorage.removeItem("authToken");
  }, []);

  return (
    <AuthContext.Provider value={{ token, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};
