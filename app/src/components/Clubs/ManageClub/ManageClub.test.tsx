import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import ManageClub from "./ManageClub";
import * as clubService from "../../../services/clubService";
import * as cityApiService from "../../../services/cityApiService";
import { useAuth } from "../../shared/useAuth";

jest.mock("../../../services/clubService");
jest.mock("../../shared/useAuth");
jest.mock("../../../services/cityApiService");

const mockClub = {
  id: "club-1",
  name: "Club de Foot",
  city: "Paris",
  address: "1 rue du Stade",
  country: "France",
  postalCode: "75000",
  email: "contact@clubfoot.fr",
  phoneNumber: "0123456789",
  latitude: 48.8566,
  longitude: 2.3522,
  typeId: "football",
  typeName: "Football",
  ownerId: "owner-1",
  createdAt: "2023-01-01T12:00:00Z",
  updatedAt: "2023-01-10T12:00:00Z",
  deletedAt: null,
};

describe("ManageClub", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (useAuth as jest.Mock).mockReturnValue({ token: "token" });
    (clubService.getFullClub as jest.Mock).mockResolvedValue({ ...mockClub });
    (clubService.updateFullClub as jest.Mock).mockResolvedValue({
      ...mockClub,
      name: "Nouveau nom",
    });
    (clubService.disableClub as jest.Mock).mockResolvedValue(true);
    (clubService.reactivateClub as jest.Mock).mockResolvedValue(true);
    (cityApiService.fetchAddress as jest.Mock).mockResolvedValue([]);
  });

  function renderWithRouter(state?: any) {
    return render(
      <MemoryRouter
        initialEntries={[
          {
            pathname: "/clubs/manage",
            state: { clubId: mockClub.id, ...state },
          },
        ]}
      >
        <Routes>
          <Route path="/clubs/manage" element={<ManageClub />} />
          <Route path="/clubs" element={<div>Clubs Page</div>} />
        </Routes>
      </MemoryRouter>,
    );
  }

  it("affiche les infos du club", async () => {
    renderWithRouter();
    expect(screen.getByText(/Gestion du club/i)).toBeInTheDocument();
    await waitFor(() =>
      expect(screen.getByText(/Club de Foot/)).toBeInTheDocument(),
    );
    expect(screen.getByText(/Paris/)).toBeInTheDocument();
    expect(screen.getByText(/Football/)).toBeInTheDocument();
    expect(screen.getByText(/contact@clubfoot.fr/)).toBeInTheDocument();
  });

  it("passe en mode édition et sauvegarde", async () => {
    renderWithRouter();
    await waitFor(() => screen.getByText(/Modifier/));
    fireEvent.click(screen.getByText(/Modifier/));
    const input = screen.getByPlaceholderText(/Nom du club/);
    fireEvent.change(input, { target: { value: "Nouveau nom" } });
    fireEvent.click(screen.getByText(/Enregistrer/));
    await waitFor(() => expect(clubService.updateFullClub).toHaveBeenCalled());
    expect(screen.getByText(/Modifications enregistrées/)).toBeInTheDocument();
  });

  it("désactive l'enregistrement tant que l'adresse n'est pas validée", async () => {
    renderWithRouter();
    await waitFor(() => screen.getByText(/Modifier/));
    fireEvent.click(screen.getByText(/Modifier/));
    const input = screen.getByPlaceholderText(/Rechercher une adresse.../);
    fireEvent.change(input, { target: { value: "Nouvelle adresse" } });
    const saveButton = screen.getByRole("button", { name: /Enregistrer/ });
    await waitFor(() => expect(saveButton).toBeDisabled());
    expect(clubService.updateFullClub).not.toHaveBeenCalled();
  });

  it("empêche l'enregistrement si email ou téléphone sont absents", async () => {
    renderWithRouter();
    await waitFor(() => screen.getByText(/Modifier/));
    fireEvent.click(screen.getByText(/Modifier/));
    fireEvent.change(screen.getByPlaceholderText(/Email/), {
      target: { value: "" },
    });
    fireEvent.change(screen.getByPlaceholderText(/Téléphone/), {
      target: { value: "" },
    });
    const saveButton = screen.getByRole("button", { name: /Enregistrer/ });
    fireEvent.click(saveButton);
    await waitFor(() => expect(saveButton).toBeDisabled());
    expect(clubService.updateFullClub).not.toHaveBeenCalled();
  });

  it("affiche une erreur lorsque le nom du club est vide", async () => {
    renderWithRouter();
    await waitFor(() => screen.getByText(/Modifier/));
    fireEvent.click(screen.getByText(/Modifier/));
    const nameInput = screen.getByPlaceholderText(/Nom du club/);
    fireEvent.change(nameInput, { target: { value: "" } });
    const saveButton = screen.getByRole("button", { name: /Enregistrer/ });
    await waitFor(() => expect(saveButton).toBeDisabled());
    expect(
      screen.getByText(/Le nom du club est obligatoire./),
    ).toBeInTheDocument();
    fireEvent.click(saveButton);
    expect(clubService.updateFullClub).not.toHaveBeenCalled();
  });

  it("désactive le club", async () => {
    renderWithRouter();
    await waitFor(() => screen.getByText(/Désactiver le club/));
    window.confirm = jest.fn(() => true);
    fireEvent.click(screen.getByText(/Désactiver le club/));
    await waitFor(() => expect(clubService.disableClub).toHaveBeenCalled());
    await waitFor(() =>
      expect(screen.queryByText(/Club désactivé/)).toBeInTheDocument(),
    );
  });

  it("réactive le club supprimé", async () => {
    (clubService.getFullClub as jest.Mock).mockResolvedValueOnce({
      ...mockClub,
      deletedAt: "2023-02-01T12:00:00Z",
    });
    renderWithRouter();
    await waitFor(() => screen.getByText(/Réactiver le club/));
    fireEvent.click(screen.getByText(/Réactiver le club/));
    await waitFor(() => expect(clubService.reactivateClub).toHaveBeenCalled());
    await waitFor(() =>
      expect(screen.queryByText(/Club réactivé/)).toBeInTheDocument(),
    );
  });

  it("redirige vers login si pas de token", async () => {
    (useAuth as jest.Mock).mockReturnValue({ token: null });
    renderWithRouter();
    await waitFor(() =>
      expect(screen.queryByText(/Gestion du club/)).not.toBeInTheDocument(),
    );
  });
});
