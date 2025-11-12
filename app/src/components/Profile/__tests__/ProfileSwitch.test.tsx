import "@testing-library/jest-dom";
import { render, screen } from "@testing-library/react";
import { Provider } from "react-redux";
import { configureStore } from "@reduxjs/toolkit";
import rolesReducer, { type RolesState } from "../../../store/rolesSlice";
import { MemoryRouter } from "react-router-dom";
import ProfileSwitch from "../ProfileSwitch";

// Stub CSS import used by ProfileLayout to avoid Jest parsing CSS
jest.mock("../ProfileCommon.css", () => ({}), { virtual: true });

// Mock child profile components to keep the test focused and fast
jest.mock("../OwnerProfile/OwnerProfile", () => ({
  __esModule: true,
  default: () => <div>OwnerProfile</div>,
}));

jest.mock("../MemberProfile/MemberProfile", () => ({
  __esModule: true,
  MemberProfile: () => <div>MemberProfile</div>,
}));

function renderWithStore(preloaded: RolesState) {
  const store = configureStore({
    reducer: { roles: rolesReducer },
    preloadedState: { roles: preloaded },
  });
  return render(
    <Provider store={store}>
      <MemoryRouter>
        <ProfileSwitch />
      </MemoryRouter>
    </Provider>,
  );
}

describe("ProfileSwitch (unit)", () => {
  test("rend OwnerProfile quand roles inclut OWNER et viewMode=OWNER", () => {
    renderWithStore({ roles: ["OWNER", "USER"], viewMode: "OWNER" });
    expect(screen.getByText(/OwnerProfile/i)).toBeInTheDocument();
    expect(screen.queryByText(/MemberProfile/i)).not.toBeInTheDocument();
  });

  test("rend MemberProfile quand viewMode=USER même si roles inclut OWNER", () => {
    renderWithStore({ roles: ["OWNER", "USER"], viewMode: "USER" });
    expect(screen.getByText(/MemberProfile/i)).toBeInTheDocument();
    expect(screen.queryByText(/OwnerProfile/i)).not.toBeInTheDocument();
  });

  test("rend toujours MemberProfile si roles n'inclut pas OWNER, quel que soit viewMode", () => {
    renderWithStore({ roles: ["USER"], viewMode: "OWNER" });
    expect(screen.getByText(/MemberProfile/i)).toBeInTheDocument();
    expect(screen.queryByText(/OwnerProfile/i)).not.toBeInTheDocument();
  });
});
