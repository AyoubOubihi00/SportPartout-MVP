import "@testing-library/jest-dom";
import { render, screen, fireEvent } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { BubbleProvider } from "../../shared/SuccessBubbleContext";
import { Provider } from "react-redux";
import { configureStore } from "@reduxjs/toolkit";
import rolesReducer, { type RolesState } from "../../../store/rolesSlice";

// Mock getMe to control fetched user data
const getMeMock = jest.fn();
jest.mock("../../../services/auth/authService", () => ({
  __esModule: true,
  getMe: (...args: unknown[]) => getMeMock(...args),
}));

// Stable mocks for auth hooks so we can ensure a token is present
jest.mock("../../shared/useAuth", () => ({
  useAuth: () => ({
    token: "fake-token",
  }),
}));

// Stub CSS import used by ProfileLayout to avoid Jest parsing CSS
jest.mock("../../Profile/ProfileCommon.css", () => ({}), { virtual: true });

// Mock useNavigate to assert navigation intent
jest.mock("react-router-dom", () => {
  const actualRouter = jest.requireActual("react-router-dom");
  return {
    __esModule: true,
    ...actualRouter,
    useNavigate: jest.fn(),
  };
});

import { EditMemberProfile } from "../MemberProfile/EditMemberProfile";
import { useNavigate } from "react-router-dom";

describe("EditMemberProfile (unit)", () => {
  function createTestStore() {
    const preloadedState: { roles: RolesState } = {
      roles: { roles: ["USER"], viewMode: "USER" },
    };
    return configureStore({
      reducer: { roles: rolesReducer },
      preloadedState,
    });
  }

  beforeEach(() => {
    (useNavigate as unknown as jest.Mock).mockReturnValue(jest.fn());
    getMeMock.mockReset();
  });

  it("pré-remplit les champs avec les données de l'utilisateur et cache le switch", async () => {
    const fakeUser = {
      id: "123",
      email: "john.doe@example.com",
      createdAt: new Date("2024-01-15T12:00:00Z").toISOString(),
      role: "USER",
      firstName: "john",
      lastName: "doe",
      birthDate: "1990-05-20T00:00:00Z",
      phone: "06 12 34 56 78",
    };
    getMeMock.mockResolvedValue(fakeUser);

    const store = createTestStore();

    render(
      <Provider store={store}>
        <MemoryRouter>
          <BubbleProvider>
            <EditMemberProfile />
          </BubbleProvider>
        </MemoryRouter>
      </Provider>,
    );

    // Le switch ne doit pas apparaître sur l'écran d'édition
    await screen.findByText(/Modifier mon profil/i);
    expect(screen.queryByText(/Mode Membre/i)).not.toBeInTheDocument();
    expect(screen.queryByText(/Mode Gérant/i)).not.toBeInTheDocument();

    // Champs pré-remplis
    const lastNameInput = await screen.findByPlaceholderText("Votre nom");
    const firstNameInput = await screen.findByPlaceholderText("Votre prénom");
    const emailInput = await screen.findByPlaceholderText("Email");
    const phoneInput = await screen.findByPlaceholderText("06 12 34 56 78");

    expect((lastNameInput as HTMLInputElement).value).toBe("doe");
    expect((firstNameInput as HTMLInputElement).value).toBe("john");
    expect((emailInput as HTMLInputElement).value).toBe("john.doe@example.com");
    expect((phoneInput as HTMLInputElement).value).toBe("06 12 34 56 78");
  });

  it("affiche une erreur si les champs requis sont vides lors de l'enregistrement", async () => {
    const fakeUser = {
      id: "123",
      email: "john.doe@example.com",
      createdAt: new Date("2024-01-15T12:00:00Z").toISOString(),
      role: "USER",
      firstName: "john",
      lastName: "doe",
      birthDate: "1990-05-20T00:00:00Z",
      phone: "06 12 34 56 78",
    };
    getMeMock.mockResolvedValue(fakeUser);

    const store = createTestStore();

    render(
      <Provider store={store}>
        <MemoryRouter>
          <BubbleProvider>
            <EditMemberProfile />
          </BubbleProvider>
        </MemoryRouter>
      </Provider>,
    );

    const lastNameInput = await screen.findByPlaceholderText("Votre nom");
    const firstNameInput = await screen.findByPlaceholderText("Votre prénom");

    // Vider les champs requis et soumettre
    fireEvent.change(lastNameInput, { target: { value: "" } });
    fireEvent.change(firstNameInput, { target: { value: "" } });

    const saveBtn = await screen.findByRole("button", { name: /Enregistrer/i });
    fireEvent.click(saveBtn);

    const matches = await screen.findAllByText(
      "Le prénom, le nom et l’email sont obligatoires.",
    );
    expect(matches.length).toBeGreaterThan(0);
  });

  it("navigue vers /profile au clic sur Annuler et après un submit valide", async () => {
    const navigateMock = jest.fn();
    (useNavigate as unknown as jest.Mock).mockReturnValue(navigateMock);
    const fakeUser = {
      id: "123",
      email: "john.doe@example.com",
      createdAt: new Date("2024-01-15T12:00:00Z").toISOString(),
      role: "USER",
      firstName: "john",
      lastName: "doe",
      birthDate: "1990-05-20T00:00:00Z",
      phone: "06 12 34 56 78",
    };
    getMeMock.mockResolvedValue(fakeUser);

    const store = createTestStore();

    render(
      <Provider store={store}>
        <MemoryRouter>
          <BubbleProvider>
            <EditMemberProfile />
          </BubbleProvider>
        </MemoryRouter>
      </Provider>,
    );

    // Annuler
    const cancelBtn = await screen.findByRole("button", { name: /Annuler/i });
    fireEvent.click(cancelBtn);
    expect(navigateMock).toHaveBeenCalledWith("/profile");

    // Submit valide (les champs requis sont déjà pré-remplis par getMe)
    const saveBtn = await screen.findByRole("button", { name: /Enregistrer/i });
    fireEvent.click(saveBtn);
    expect(navigateMock).toHaveBeenCalledWith("/profile");
  });
});
