import { render, screen, fireEvent, waitFor, within } from "@testing-library/react";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import ManageMembers from "./ManageMembers";
import * as membershipService from "../../../services/membershipService";
import * as clubService from "../../../services/clubService";
import { useAuth } from "../../shared/useAuth";

jest.mock("../../../services/membershipService");
jest.mock("../../../services/clubService");
jest.mock("../../shared/useAuth");

const mockClub = {
  id: "club-1",
  name: "Club de Foot",
  city: "Paris",
  typeName: "Football",
};

const mockMembers = [
  {
    userId: "user-1",
    clubId: "club-1",
    firstName: "Jean",
    lastName: "Dupont",
    email: "jean@email.com",
    phoneNumber: "0600000001",
    isActif: true,
    himself: false,
    dateSubscription: "2023-01-01T12:00:00Z",
  },
  {
    userId: "user-2",
    clubId: "club-1",
    firstName: "Alice",
    lastName: "Martin",
    email: "alice@email.com",
    phoneNumber: "0600000002",
    isActif: false,
    himself: true,
    dateSubscription: "2023-01-02T12:00:00Z",
    dateUnsubscription: "2023-02-01T12:00:00Z",
  },
  {
    userId: "user-3",
    clubId: "club-1",
    firstName: "Bob",
    lastName: "Durand",
    email: "bob@email.com",
    phoneNumber: "0600000003",
    isActif: false,
    himself: false,
    dateSubscription: "2023-01-03T12:00:00Z",
    dateUnsubscription: "2023-02-02T12:00:00Z",
  },
];

describe("ManageMembers", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (useAuth as jest.Mock).mockReturnValue({ token: "token" });
    (clubService.getFullClub as jest.Mock).mockResolvedValue({ ...mockClub });
    (membershipService.fetchClubMembers as jest.Mock).mockResolvedValue([...mockMembers]);
    (membershipService.forcedUnsubscribe as jest.Mock).mockResolvedValue(true);
  });

  function renderWithRouter(state?: any) {
    return render(
      <MemoryRouter initialEntries={[{ pathname: "/clubs/manage/members", state: { clubId: mockClub.id, ...state } }]}> 
        <Routes>
          <Route path="/clubs/manage/members" element={<ManageMembers />} />
          <Route path="/clubs/manage" element={<div>Retour Club</div>} />
        </Routes>
      </MemoryRouter>
    );
  }

  it("affiche les membres et les stats", async () => {
    renderWithRouter();
    await waitFor(() => screen.getByText(/Gestion des membres/));
    // Attendre que les boutons "Gérer" soient rendus (un par membre)
    await waitFor(() => expect(screen.getAllByText(/Gérer/).length).toBeGreaterThan(0));
    // Récupérer les lignes du tableau (skip header)
    const rows = screen.getAllByRole('row');
    const dataRows = rows.slice(1);
    const rowTexts = dataRows.map((r) => r.textContent?.replace(/\s+/g, " ").trim() ?? "");
    // Par défaut le filtre est 'active' : seul Jean Dupont est visible
    expect(rowTexts.some((t) => t.includes("Jean Dupont"))).toBeTruthy();
    // Vérifier les compteurs (texte fragmenté possible)
    expect(screen.getByText((content) => /3\s*membre/.test(content))).toBeInTheDocument();
    expect(screen.getByText((content) => /2\s*inactif/.test(content))).toBeInTheDocument();
    expect(screen.getByText((content) => /1\s*actif/.test(content))).toBeInTheDocument();
  });

  it("filtre les membres actifs/inactifs", async () => {
    renderWithRouter();
    await waitFor(() => screen.getByText(/Actifs/));
    fireEvent.click(screen.getByText(/Inactifs/));
    expect(screen.getByText((content) => content.replace(/\s+/g, " ").includes("Alice Martin"))).toBeInTheDocument();
    expect(screen.getByText((content) => content.replace(/\s+/g, " ").includes("Bob Durand"))).toBeInTheDocument();
    expect(screen.queryByText((content) => content.replace(/\s+/g, " ").includes("Jean Dupont"))).not.toBeInTheDocument();
    fireEvent.click(screen.getByText(/Actifs/));
    expect(screen.getByText((content) => content.replace(/\s+/g, " ").includes("Jean Dupont"))).toBeInTheDocument();
    expect(screen.queryByText((content) => content.replace(/\s+/g, " ").includes("Alice Martin"))).not.toBeInTheDocument();
  });

  it("recherche un membre par nom ou email", async () => {
    renderWithRouter();
    // Basculer sur les inactifs pour trouver Alice
    fireEvent.click(await screen.findByText(/Inactifs/));
    // Il y a parfois plusieurs inputs (un dans le layout, un dans la modale).
    // Prendre celui qui n'est pas dans un dialog pour interagir avec la recherche principale.
    const inputs = await screen.findAllByPlaceholderText(/Rechercher un membre/);
    const input = inputs.find((i) => i.closest('[role="dialog"]') === null) || inputs[0];
    fireEvent.change(input, { target: { value: "alice" } });
    // attendre que la liste soit filtrée et contienne Alice
    await waitFor(() => expect(screen.getAllByRole('row').length).toBeGreaterThan(1));
    const filteredRows = screen.getAllByRole('row').slice(1);
    const texts = filteredRows.map((r) => r.textContent?.replace(/\s+/g, " ").trim() ?? "");
    expect(texts.some((t) => t.includes("Alice Martin"))).toBeTruthy();
    expect(texts.some((t) => t.includes("Jean Dupont"))).toBeFalsy();
  });

  it("ouvre la modale de détails et désabonne un membre actif", async () => {
    renderWithRouter();
    await waitFor(() => expect(screen.getAllByText(/Gérer/).length).toBeGreaterThan(0));
    // s'assurer que confirm est mocké (jsdom ne l'implémente pas)
    (window as any).confirm = jest.fn(() => true);
    const allRowsBefore = screen.getAllByRole('row').slice(1);
    const firstManage = within(allRowsBefore[0]).getByText(/Gérer/);
    fireEvent.click(firstManage);
    // La modale s'ouvre dans un dialog — scoped queries évitent les collisions
    const dialog = await screen.findByRole('dialog');
    const modal = within(dialog);
    expect(modal.getByRole('heading', { name: /Jean Dupont/i })).toBeInTheDocument();
    expect(modal.getByText(/Inscrit le/)).toBeInTheDocument();
    expect(modal.getByText(/Actif/)).toBeInTheDocument();
    modal.getByRole('button', { name: /Désabonner/i });
    fireEvent.click(modal.getByRole('button', { name: /Désabonner/i }));
    await waitFor(() => expect(membershipService.forcedUnsubscribe).toHaveBeenCalled());
  });

  it("affiche la modale pour un membre inactif (désinscrit)", async () => {
    renderWithRouter();
    await waitFor(() => expect(screen.getAllByText(/Gérer/).length).toBeGreaterThan(0));
    // Afficher les membres inactifs puis ouvrir la modale du deuxième inactif
    fireEvent.click(await screen.findByText(/Inactifs/));
    const allRowsAfter = screen.getAllByRole('row').slice(1);
    const secondManage = within(allRowsAfter[1]).getByText(/Gérer/);
    fireEvent.click(secondManage);
    const dialog = await screen.findByRole('dialog');
    const modal = within(dialog);
    expect(modal.getByRole('heading', { name: /Alice Martin/i })).toBeInTheDocument();
    expect(modal.getByText(/Désinscrit le/)).toBeInTheDocument();
    expect(modal.getByText(/Inactif/)).toBeInTheDocument();
    expect(modal.getByText(/lui-même/)).toBeInTheDocument();
  });

  it("redirige vers login si pas de token", async () => {
    (useAuth as jest.Mock).mockReturnValue({ token: null });
    renderWithRouter();
    await waitFor(() => expect(screen.queryByText(/Gestion des membres/)).not.toBeInTheDocument());
  });
});
