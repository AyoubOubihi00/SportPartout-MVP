import { createSlice, PayloadAction } from "@reduxjs/toolkit";

export type RolesState = {
  roles: string[];
  // Mode d'affichage courant (ne modifie pas les rôles effectifs)
  viewMode: "OWNER" | "USER";
};

const initialState: RolesState = {
  roles: [],
  viewMode: "USER",
};

// Optionnel: type pour la charge utile reçue via l'API Gateway
export type GatewayAuthPayload = {
  sub: string;
  roles?: string[];
  iat?: number;
  exp?: number;
};

function normalizeRoles(input?: string[] | string): string[] {
  if (!input) return [];
  const arr = Array.isArray(input) ? input : [input];
  return Array.from(
    new Set(
      arr
        .map((r) => String(r).trim().toUpperCase())
        .filter((r) => r.length > 0),
    ),
  );
}

const rolesSlice = createSlice({
  name: "roles",
  initialState,
  reducers: {
    setRoles: (state: RolesState, action: PayloadAction<string[]>) => {
      state.roles = action.payload;
    },
    setViewMode: (
      state: RolesState,
      action: PayloadAction<"OWNER" | "USER">,
    ) => {
      // Le mode d'affichage n'impacte pas les rôles réels
      state.viewMode = action.payload;
    },
    clearRoles: (state: RolesState) => {
      state.roles = [];
      state.viewMode = "USER";
    },
    // Nouveau: mise à jour directe depuis la payload de l'API Gateway
    setFromGatewayPayload: (
      state: RolesState,
      action: PayloadAction<GatewayAuthPayload>,
    ) => {
      const nextRoles = normalizeRoles(action.payload.roles);
      state.roles = nextRoles;
      const ownerCapable = nextRoles.includes("OWNER");
      // Par défaut, si le compte est gérant et qu'aucun choix préalable n'existe, on reste sur OWNER
      if (
        ownerCapable &&
        state.viewMode !== "OWNER" &&
        state.viewMode !== "USER"
      ) {
        state.viewMode = "OWNER";
      }
      if (!ownerCapable) {
        state.viewMode = "USER";
      }
    },
  },
});

export const { setRoles, setViewMode, clearRoles, setFromGatewayPayload } =
  rolesSlice.actions;
export default rolesSlice.reducer;

// Selectors
export const selectRoles = (state: { roles: RolesState }) => state.roles.roles;
export const selectIsOwner = (state: { roles: RolesState }) =>
  state.roles.roles.includes("OWNER");
export const selectIsUser = (state: { roles: RolesState }) =>
  state.roles.roles.includes("USER");
export const selectViewMode = (state: { roles: RolesState }) =>
  state.roles.viewMode;
