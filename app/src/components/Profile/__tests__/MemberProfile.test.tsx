import "@testing-library/jest-dom";
import { render, screen, fireEvent } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";

// Mock getMe to control fetched user data and avoid relying on any hardcoded UI strings
const getMeMock = jest.fn();
jest.mock("../../../services/auth/authService", () => ({
  __esModule: true,
  getMe: (...args: unknown[]) => getMeMock(...args),
}));

// Stable mocks for auth hooks so we can assert on logout and ensure a token is present
const loginMock = jest.fn();
const logoutMock = jest.fn();
jest.mock("../../shared/useAuth", () => ({
  useAuth: () => ({
    token: "fake-token",
    login: loginMock,
    logout: logoutMock,
  }),
}));

// Stub CSS import to avoid Jest parsing CSS without touching config
jest.mock("../ProfileCommon.css", () => ({}), { virtual: true });

// Mock ProfileLayout to isolate MemberProfile rendering (switch included)
jest.mock("../ProfileLayout", () => ({
  __esModule: true,
  default: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="profile-layout">{children}</div>
  ),
}));

// Mock useNavigate to assert navigation intent
jest.mock("react-router-dom", () => {
  const actualRouter = jest.requireActual("react-router-dom");
  return {
    __esModule: true,
    ...actualRouter,
    useNavigate: jest.fn(),
  };
});

import { MemberProfile } from "../MemberProfile/MemberProfile";
import { useNavigate } from "react-router-dom";

describe("MemberProfile (unit)", () => {
  beforeEach(() => {
    (useNavigate as unknown as jest.Mock).mockReturnValue(jest.fn());
    getMeMock.mockReset();
  });

  it("affiche les informations utilisateur récupérées (nom formaté, email, téléphone)", async () => {
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

    render(
      <MemoryRouter>
        <MemberProfile />
      </MemoryRouter>,
    );

    // Le nom et le prénom sont rendus dans deux éléments différents (grille label/valeur)
    expect(await screen.findByText("DOE")).toBeInTheDocument();
    expect(await screen.findByText("John")).toBeInTheDocument();
    expect(await screen.findByText(fakeUser.email)).toBeInTheDocument();
    expect(await screen.findByText(fakeUser.phone)).toBeInTheDocument();
  });

  it("navigue vers /editmemberprofile en cliquant sur 'Modifier'", async () => {
    const navigateMock = jest.fn();
    (useNavigate as unknown as jest.Mock).mockReturnValue(navigateMock);
    getMeMock.mockResolvedValue({
      id: "1",
      email: "x@y.z",
      createdAt: new Date().toISOString(),
      role: "USER",
      firstName: "x",
      lastName: "y",
      birthDate: "1999-01-01T00:00:00Z",
      phone: "0000000000",
    });

    render(
      <MemoryRouter>
        <MemberProfile />
      </MemoryRouter>,
    );

    const editBtn = await screen.findByRole("button", { name: /Modifier/i });
    fireEvent.click(editBtn);
    expect(navigateMock).toHaveBeenCalledWith("/editmemberprofile");
  });
});
