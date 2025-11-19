import "@testing-library/jest-dom";
import { render, screen, fireEvent } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";

// Mock getMe to control fetched user data
const getMeMock = jest.fn();
jest.mock("../../../services/auth/authService", () => ({
  __esModule: true,
  getMe: (...args: unknown[]) => getMeMock(...args),
}));

// Mock ProfileLayout to isolate OwnerProfile rendering
jest.mock("../ProfileLayout", () => ({
  __esModule: true,
  default: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="profile-layout">{children}</div>
  ),
}));

// Mock useAuth to provide a token and a spyable logout
const loginMock = jest.fn();
const logoutMock = jest.fn();
jest.mock("../../shared/useAuth", () => ({
  useAuth: () => ({
    token: "fake-token",
    login: loginMock,
    logout: logoutMock,
  }),
}));

// Mock useNavigate to assert navigation
jest.mock("react-router-dom", () => {
  const actualRouter = jest.requireActual("react-router-dom");
  return {
    __esModule: true,
    ...actualRouter,
    useNavigate: jest.fn(),
  };
});

import OwnerProfile from "../OwnerProfile/OwnerProfile";
import { useNavigate } from "react-router-dom";

describe("OwnerProfile (unit)", () => {
  beforeEach(() => {
    (useNavigate as unknown as jest.Mock).mockReturnValue(jest.fn());
    getMeMock.mockReset();
  });

  it("navigue vers /editmemberprofile en cliquant sur 'Modifier'", async () => {
    const navigateMock = jest.fn();
    (useNavigate as unknown as jest.Mock).mockReturnValue(navigateMock);
    getMeMock.mockResolvedValue({
      id: "1",
      email: "owner@example.com",
      createdAt: new Date("2024-01-10").toISOString(),
      role: "OWNER",
      firstName: "Pierre",
      lastName: "Martin",
      birthDate: "1990-01-01T00:00:00Z",
      phone: "0000000000",
    });

    render(
      <MemoryRouter>
        <OwnerProfile />
      </MemoryRouter>,
    );

    const editBtn = await screen.findByRole("button", { name: /Modifier/i });
    fireEvent.click(editBtn);
    expect(navigateMock).toHaveBeenCalledWith("/editmemberprofile");
  });
});
