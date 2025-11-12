import "@testing-library/jest-dom";
import { render, screen } from "@testing-library/react";
import { Provider } from "react-redux";
import { configureStore } from "@reduxjs/toolkit";
import rolesReducer from "../../../store/rolesSlice";
import { MemoryRouter } from "react-router-dom";
import type { RolesState } from "../../../store/rolesSlice";

const preloaded: RolesState = { roles: ["USER"], viewMode: "USER" };

// Stub CSS import used by ProfileLayout without changing Jest config
jest.mock("../ProfileCommon.css", () => ({}), { virtual: true });

// Mock NavBar to avoid router dependency here
jest.mock("../../NavBar/NavBar", () => ({
  __esModule: true,
  default: () => <div data-testid="navbar" />,
}));

import ProfileLayout from "../ProfileLayout";

function renderWithStoreAndRouter(ui: React.ReactNode) {
  const store = configureStore({
    reducer: { roles: rolesReducer },
    preloadedState: { roles: preloaded },
  });

  return render(
    <Provider store={store}>
      <MemoryRouter>{ui}</MemoryRouter>
    </Provider>,
  );
}

describe("ProfileLayout (unit)", () => {
  it("rend ses enfants et la barre de navigation", () => {
    renderWithStoreAndRouter(
      <ProfileLayout>
        <div data-testid="child">content</div>
      </ProfileLayout>,
    );

    expect(screen.getByTestId("child")).toBeInTheDocument();
    expect(screen.getByTestId("navbar")).toBeInTheDocument();
  });
});
