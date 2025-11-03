import type { ActivityDto } from "../../types/activities";

// Données de démonstration (remplacées plus tard par un appel au back)
export const mockActivitiesDto: ActivityDto[] = [
  {
    id: "a1",
    title: "Séance — Entraînement U17",
    club: "Basketball Club Metz",
    sport: "Basketball",
    startIso: new Date().toISOString().slice(0, 10) + "T18:00:00Z",
    endIso: new Date().toISOString().slice(0, 10) + "T19:30:00Z",
    place: "Salle A",
    capacity: 16,
    booked: 9,
    coach: "M. Dupont",
    level: "U17",
    description:
      "Séance axée sur la vitesse et la précision au tir. Pensez à apporter de l'eau et votre serviette.",
  },
  {
    id: "a2",
    title: "Séance — Entraînement Seniors 1",
    club: "Tennis Club Nancy",
    sport: "Tennis",
    startIso:
      new Date(Date.now() + 86400000).toISOString().slice(0, 10) + "T10:00:00Z",
    endIso:
      new Date(Date.now() + 86400000).toISOString().slice(0, 10) + "T11:00:00Z",
    place: "Court 3",
    capacity: 8,
    booked: 6,
    coach: "Mme Martin",
    level: "Seniors 1",
    description:
      "Travail de service et retour. Préparation aux interclubs du week‑end.",
  },
  {
    id: "a3",
    title: "Séance — Cours découverte",
    club: "Metz Rugby",
    sport: "Rugby",
    startIso:
      new Date(Date.now() - 2 * 86400000).toISOString().slice(0, 10) +
      "T15:00:00Z",
    endIso:
      new Date(Date.now() - 2 * 86400000).toISOString().slice(0, 10) +
      "T16:00:00Z",
    capacity: 20,
    booked: 18,
    coach: "Staff club",
    level: "Découverte",
    description:
      "Initiation aux règles et postes. Ambiance conviviale, ouvert à tous.",
  },
  {
    id: "a4",
    title: "Séance — Fractionné",
    club: "Metz Athlé",
    sport: "Athlétisme",
    startIso:
      new Date(Date.now() + 2 * 86400000).toISOString().slice(0, 10) +
      "T19:00:00Z",
    endIso:
      new Date(Date.now() + 2 * 86400000).toISOString().slice(0, 10) +
      "T20:00:00Z",
    place: "Stade Saint‑Symphorien",
    capacity: 25,
    booked: 12,
    coach: "Coach Alex",
    level: "Tous niveaux",
    description: "Séance VMA 30/30, échauffement + retour au calme inclus.",
  },
  {
    id: "a5",
    title: "Séance — Technique",
    club: "FC Nancy",
    sport: "Football",
    startIso:
      new Date(Date.now() + 3 * 86400000).toISOString().slice(0, 10) +
      "T17:30:00Z",
    endIso:
      new Date(Date.now() + 3 * 86400000).toISOString().slice(0, 10) +
      "T19:00:00Z",
    place: "Terrain 2",
    capacity: 22,
    booked: 21,
    coach: "Coach N. Grom",
    level: "U19",
    description:
      "Conduite de balle, dribbles en 1v1, ateliers par petits groupes.",
  },
  {
    id: "a6",
    title: "Séance — PPG",
    club: "Nancy Handball",
    sport: "Handball",
    startIso:
      new Date(Date.now() - 1 * 86400000).toISOString().slice(0, 10) +
      "T18:30:00Z",
    endIso:
      new Date(Date.now() - 1 * 86400000).toISOString().slice(0, 10) +
      "T19:30:00Z",
    place: "Gymnase B",
    capacity: 18,
    booked: 18,
    coach: "Coach Hugo",
    level: "Prépa Physique",
    description:
      "Renforcement + cardio, travail du gainage, prévention blessures.",
  },
  {
    id: "a7",
    title: "Séance — Double mixte",
    club: "AS Badminton Metz",
    sport: "Badminton",
    startIso:
      new Date(Date.now() + 4 * 86400000).toISOString().slice(0, 10) +
      "T20:00:00Z",
    endIso:
      new Date(Date.now() + 4 * 86400000).toISOString().slice(0, 10) +
      "T22:00:00Z",
    place: "Salle C",
    capacity: 12,
    booked: 7,
    coach: "—",
    level: "Loisir",
    description: "Matches en rotation, volants fournis par le club.",
  },
];
