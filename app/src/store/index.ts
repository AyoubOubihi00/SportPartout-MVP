import { configureStore } from "@reduxjs/toolkit";
import rolesReducer, { type RolesState } from "./rolesSlice";

// Simple persistence of roles in localStorage so navigating directly to routes or refreshing
// doesn't clear the Redux state.
const STORAGE_KEY = "roles";

function loadPersistedRoles(): RolesState | undefined {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return undefined;
    const parsed = JSON.parse(raw) as Partial<RolesState> | undefined;
    if (parsed && Array.isArray(parsed.roles)) {
      // Back-compat: compute default viewMode from roles if missing
      const persistedViewMode = parsed.viewMode;
      const ownerCapable = parsed.roles.includes("OWNER");
      const viewMode =
        persistedViewMode === "OWNER" || persistedViewMode === "USER"
          ? persistedViewMode
          : ownerCapable
            ? "OWNER"
            : "USER";
      return { roles: parsed.roles, viewMode } as RolesState;
    }
    return undefined;
  } catch {
    return undefined;
  }
}

const preloadedState = {
  roles: loadPersistedRoles() ?? { roles: [], viewMode: "USER" },
};

export const store = configureStore({
  reducer: {
    roles: rolesReducer,
  },
  preloadedState,
});

// Persist roles on any change
store.subscribe(() => {
  try {
    const state = store.getState();
    const rolesState = (state as { roles: RolesState }).roles;
    localStorage.setItem(STORAGE_KEY, JSON.stringify(rolesState));
  } catch {
    // ignore persistence errors
  }
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

// Typed hooks helpers (optional)
// import { useDispatch, useSelector, TypedUseSelectorHook } from "react-redux";
// export const useAppDispatch: () => AppDispatch = useDispatch;
// export const useAppSelector: TypedUseSelectorHook<RootState> = useSelector;
