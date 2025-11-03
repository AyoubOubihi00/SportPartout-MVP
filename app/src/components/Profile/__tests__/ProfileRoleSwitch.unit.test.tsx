import "@testing-library/jest-dom";
import { render, screen, fireEvent } from "@testing-library/react";
import { Provider } from "react-redux";
import { configureStore } from "@reduxjs/toolkit";
import rolesReducer, { type RolesState } from "../../../store/rolesSlice";
import ProfileRoleSwitch from "../ProfileRoleSwitch";

function setup(preloaded: RolesState) {
  const store = configureStore({
    reducer: { roles: rolesReducer },
    preloadedState: { roles: preloaded },
  });
  const utils = render(
    <Provider store={store}>
      <ProfileRoleSwitch />
    </Provider>
  );
  return { store, ...utils };
}

describe("ProfileRoleSwitch (unit)", () => {
  test("n'affiche rien si l'utilisateur n'a pas OWNER", () => {
    setup({ roles: ["USER"], viewMode: "USER" });
    expect(screen.queryByRole("button", { name: /Mode Membre/i })).not.toBeInTheDocument();
    expect(screen.queryByRole("button", { name: /Mode Gérant/i })).not.toBeInTheDocument();
  });

  test("affiche les boutons et change viewMode au clic", () => {
    const { store } = setup({ roles: ["OWNER", "USER"], viewMode: "OWNER" });

    const btnMember = screen.getByRole("button", { name: /Mode Membre/i });
    const btnOwner = screen.getByRole("button", { name: /Mode Gérant/i });

    expect(btnMember).toBeInTheDocument();
    expect(btnOwner).toBeInTheDocument();

    // Passe en mode membre
    fireEvent.click(btnMember);
    expect(store.getState().roles.viewMode).toBe("USER");

    // Repasser en mode gérant
    fireEvent.click(btnOwner);
    expect(store.getState().roles.viewMode).toBe("OWNER");
  });
});
