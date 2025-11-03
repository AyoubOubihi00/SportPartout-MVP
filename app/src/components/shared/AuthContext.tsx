import React, { useState, useCallback, useEffect } from "react";
import { AuthContext } from "./AuthContextContext";
import { isJwtExpired } from "../../services/jwtUtils";
import {
  login as loginService,
  LoginPayload,
  LoginResponse,
} from "../../services/auth/authService";
import { getMe } from "../../services/auth/authService";
import { useDispatch } from "react-redux";
import { clearRoles, setRoles, setViewMode } from "../../store/rolesSlice";

export interface AuthContextType {
  token: string | null;
  login: (payload: LoginPayload) => Promise<LoginResponse>;
  logout: () => void;
}

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const dispatch = useDispatch();
  // Récupérer le token depuis localStorage au chargement
  const [token, setToken] = useState<string | null>(() => {
    const stored = localStorage.getItem("authToken");
    if (stored && isJwtExpired(stored)) {
      localStorage.removeItem("authToken");
      return null;
    }
    return stored;
  });

  // Helper: hydrate Redux roles from the backend profile
  const hydrateRoles = useCallback(
    async (currentToken: string, initializeView: boolean) => {
      try {
        const me = await getMe(currentToken);
        // Derive roles: all accounts are at least USER; OWNER implies both
        const baseRoles: string[] = ["USER"];
        const roles =
          me?.role?.toUpperCase() === "OWNER"
            ? ["OWNER", ...baseRoles]
            : baseRoles;
        dispatch(setRoles(roles));
        if (initializeView) {
          // On premier login (ou changement de token), si OWNER est possible, démarre en OWNER
          if (roles.includes("OWNER")) {
            dispatch(setViewMode("OWNER"));
          } else {
            dispatch(setViewMode("USER"));
          }
        }
      } catch {
        // En cas d'erreur, on nettoie les rôles pour éviter un état incohérent
        dispatch(setRoles([]));
        if (initializeView) dispatch(setViewMode("USER"));
      }
    },
    [dispatch],
  );

  const login = useCallback(
    async (payload: LoginPayload) => {
      const result = await loginService(payload);
      if (result.success && result.token) {
        setToken(result.token);
        localStorage.setItem("authToken", result.token);
        // Hydrate roles and initialize view mode right after login
        await hydrateRoles(result.token, true);
      }
      return result;
    },
    [hydrateRoles],
  );

  const logout = useCallback(() => {
    setToken(null);
    localStorage.removeItem("authToken");
    dispatch(clearRoles());
  }, [dispatch]);

  // When a persisted token exists or changes (e.g., refresh), hydrate roles without forcing view mode
  useEffect(() => {
    if (token) {
      void hydrateRoles(token, false);
    } else {
      // No token: ensure roles are cleared
      dispatch(clearRoles());
    }
  }, [token, hydrateRoles, dispatch]);

  return (
    <AuthContext.Provider value={{ token, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};
