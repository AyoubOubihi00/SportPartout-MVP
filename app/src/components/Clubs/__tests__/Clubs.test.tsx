import { render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import Clubs from "../Clubs";
import { useAuth } from "../../shared/useAuth";
import { getMe } from "../../../services/auth/authService";
import { fetchMyClubs, fetchManagedClubs, type MemberWithClub } from "../../../services/membershipService";
import { getFullClub } from "../../../services/clubService";

// Mocks
jest.mock("../../shared/useAuth");
jest.mock("../../../services/auth/authService");
jest.mock("../../../services/membershipService");
jest.mock("../../../services/clubService");

const mockUseAuth = useAuth as jest.Mock;
const mockGetMe = getMe as jest.Mock;
const mockFetchMyClubs = fetchMyClubs as jest.Mock;
const mockFetchManagedClubs = fetchManagedClubs as jest.Mock;
const mockGetFullClub = getFullClub as jest.Mock;

const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockNavigate,
  useLocation: () => ({ key: "test-location" }),
}));

const memberProfile = { id: "user-1", role: "MEMBER" };
const ownerProfile = { id: "owner-1", role: "OWNER" };

const memberClubs: MemberWithClub[] = [
  {
    clubId: "club-1",
    userId: "user-1",
    firstName: "Jean",
    lastName: "Dupont",
    email: "jean.dupont@email.com",
    club: {
      id: "club-1",
      name: "Club de Foot",
      city: "Paris",
      typeName: "Football",
    },
  },
];

const managedClubs = [
  { id: "club-managed-1", name: "Club de Basket Géré", city: "Lyon" },
];

const fullClub = {
  id: "club-managed-1",
  name: "Club de Basket Géré",
  city: "Lyon",
  deletedAt: null,
};

const renderComponent = () => {
  return render(
    <MemoryRouter>
      <Clubs />
    </MemoryRouter>
  );
};

describe("Clubs Component", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockUseAuth.mockReturnValue({ token: "fake-token" });
  });

  it("should redirect to login if not authenticated", () => {
    mockUseAuth.mockReturnValue({ token: null });
    renderComponent();
    expect(mockNavigate).toHaveBeenCalledWith("/login");
  });

  it("should show loading state initially", () => {
    mockGetMe.mockResolvedValue(memberProfile);
    mockFetchMyClubs.mockReturnValue(new Promise(() => {}));
    renderComponent();
    expect(screen.getByRole("progressbar")).toBeInTheDocument();
  });

  it("should show an error message if fetching fails", async () => {
    const errorMessage = "Erreur réseau";
    mockGetMe.mockRejectedValue(new Error(errorMessage));
    renderComponent();
    await waitFor(() => {
      expect(screen.getByText(errorMessage)).toBeInTheDocument();
    });
  });

  it("should display the empty state if user has no clubs", async () => {
    mockGetMe.mockResolvedValue(memberProfile);
    mockFetchMyClubs.mockResolvedValue([]);
    mockFetchManagedClubs.mockResolvedValue([]);
    renderComponent();
    await waitFor(() => {
      expect(screen.getByText(/rejoint.*club.*pour le moment/i)).toBeInTheDocument();
    });
  });

  it("should display member clubs for a standard user", async () => {
    mockGetMe.mockResolvedValue(memberProfile);
    mockFetchMyClubs.mockResolvedValue(memberClubs);
    mockFetchManagedClubs.mockResolvedValue([]);
    renderComponent();
    await waitFor(() => {
      expect(screen.getByText("Club de Foot")).toBeInTheDocument();
      expect(screen.queryByText(/Clubs gérés/i)).not.toBeInTheDocument();
    });
  });

  it("should display managed clubs for an owner", async () => {
    mockGetMe.mockResolvedValue(ownerProfile);
    mockFetchMyClubs.mockResolvedValue([]);
    mockFetchManagedClubs.mockResolvedValue(managedClubs);
    mockGetFullClub.mockResolvedValue(fullClub);

    renderComponent();

    await waitFor(() => {
      expect(screen.getByText(/Clubs gérés/i)).toBeInTheDocument();
      expect(screen.getByText("Club de Basket Géré")).toBeInTheDocument();
    });
  });

  it("should display both member and managed clubs for an owner who is also a member", async () => {
    mockGetMe.mockResolvedValue(ownerProfile);
    mockFetchMyClubs.mockResolvedValue(memberClubs);
    mockFetchManagedClubs.mockResolvedValue(managedClubs);
    mockGetFullClub.mockResolvedValue(fullClub);

    renderComponent();

    await waitFor(() => {
      expect(screen.getByText("Club de Foot")).toBeInTheDocument();
      expect(screen.getByText(/Clubs gérés/i)).toBeInTheDocument();
      expect(screen.getByText("Club de Basket Géré")).toBeInTheDocument();
    });
  });
});