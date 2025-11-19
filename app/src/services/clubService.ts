import { API_BASE_URL, BFF_BASE_URL } from "../config";
import { ClubNear } from "../model/Club";
import adminService from "./adminService";

export interface Club {
  name: string;
  city: string;
}

export async function getAllClubs(token: string | null): Promise<Club[]> {
  const query = `query { clubs { name city } }`;
  const response = await fetch(`${API_BASE_URL}/graphql`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: JSON.stringify({ query }),
  });
  const result = await response.json();
  if (result.errors)
    throw new Error(result.errors[0]?.message || "Erreur API clubs");
  return result.data?.clubs || [];
}
// src/services/clubService.ts

export async function getClubsNear(
  latitude: number,
  longitude: number,
  radiusKm: number,
): Promise<ClubNear[]> {
  const query = `query ClubsNear($latitude: Float!, $longitude: Float!, $radiusKm: Float!) {\n  clubsNear(latitude: $latitude, longitude: $longitude, radiusKm: $radiusKm) {\n   id  name   city notation memberCount address country postalCode phoneNumber email typeIdSport typeNameSport longitude latitude  distanceKm\n  }\n}`;
  const variables = { latitude, longitude, radiusKm };
  const response = await fetch(`${API_BASE_URL}/findclub/graphql`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ query, variables }),
  });
  const result = await response.json();
  if (result.errors)
    throw new Error(result.errors[0]?.message || "Erreur API clubsNear");
  return result.data?.clubsNear || [];
}

// --- Manage club ----------------------------------------------------
export interface FullClub {
  id: string;
  name: string;
  typeId?: string | null;
  typeName?: string | null;
  city: string;
  address: string;
  country: string;
  phoneNumber?: string | null;
  postalCode?: string | null;
  email?: string | null;
  latitude: number;
  longitude: number;
  ownerId: string;
  createdAt: string;
  updatedAt: string;
  deletedAt?: string | null;
}

// Fetch a full club by id using the backend field `clubById(id: UUID!): FullClub`.
export async function getFullClub(
  id: string,
  token?: string | null,
): Promise<FullClub> {
  const query = `query FullClub($id: UUID!) {\n  clubById(id: $id) {\n    id name typeId typeName city address country phoneNumber postalCode email latitude longitude ownerId createdAt updatedAt deletedAt\n  }\n}`;
  const variables = { id };
  const response = await fetch(`${BFF_BASE_URL}/graphql`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: JSON.stringify({ query, variables }),
  });
  const result = await response.json();
  if (result.errors)
    throw new Error(
      result.errors[0]?.message ||
        "Erreur API clubById (BFF)\n" + JSON.stringify(result.errors),
    );
  const club: FullClub | undefined = result.data?.clubById;
  if (!club) throw new Error("Club introuvable");
  return club;
}

// --- Update club ----------------------------------------------------
export interface UpdateClubInput {
  name?: string;
  typeId?: string;
  typeName?: string;
  city?: string;
  address?: string;
  country?: string;
  phoneNumber?: string | null;
  postalCode?: string | null;
  email?: string | null;
  latitude?: number;
  longitude?: number;
  updatedAt?: string;
}

export async function updateFullClub(
  id: string,
  data: UpdateClubInput,
  ownerId: string,
  token?: string | null,
): Promise<FullClub> {
  const mutation = `mutation UpdateClub($id: UUID!, $data: UpdateClubInput!, $ownerId: UUID!) {\n  updateClub(id: $id, data: $data, ownerId: $ownerId) {\n    id name typeId typeName city address country phoneNumber postalCode email latitude longitude ownerId createdAt updatedAt deletedAt\n  }\n}`;
  console.log("data : ", data);
  let sportName: string | null = null;
  if (data.typeId) {
    try {
      const types = await adminService.fetchSportTypes();
      const found = types.find((t) => t.id === data.typeId);
      sportName = found?.name ?? null;
    } catch (err) {
      console.error("Erreur lors de la récupération des types de sport :", err);
      sportName = data.typeName ?? null;
    }
  } else {
    sportName = data.typeName ?? null;
  }

  const updatedData = {
    ...data,
    typeName: sportName,
    updatedAt: new Date().toISOString(),
  };
  console.log("updatedData : ", updatedData);
  const variables = { id, data: updatedData, ownerId };

  const response = await fetch(`${BFF_BASE_URL}/graphql`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: JSON.stringify({ query: mutation, variables }),
  });
  const result = await response.json();
  if (result.errors)
    throw new Error(result.errors[0]?.message || "Erreur API updateClub");
  const club: FullClub | undefined = result.data?.updateClub;
  if (!club) throw new Error("Club introuvable après mise à jour");
  return club;
}

export async function disableClub(
  id: string,
  ownerId: string,
  token?: string | null,
): Promise<boolean> {
  const mutation = `mutation DisableClub($id: UUID!, $ownerId: UUID!) {\n  disableClub(id: $id, ownerId: $ownerId)\n}`;
  const variables = { id, ownerId };
  const response = await fetch(`${BFF_BASE_URL}/graphql`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: JSON.stringify({ query: mutation, variables }),
  });
  const result = await response.json();
  if (result.errors)
    throw new Error(result.errors[0]?.message || "Erreur API disableClub");
  return Boolean(result.data?.disableClub);
}

export async function reactivateClub(
  id: string,
  ownerId: string,
  token?: string | null,
): Promise<boolean> {
  const mutation = `mutation ReactivateClub($id: UUID!, $ownerId: UUID!) {\n  reactivateClub(id: $id, ownerId: $ownerId)\n}`;
  const variables = { id, ownerId };
  const response = await fetch(`${BFF_BASE_URL}/graphql`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: JSON.stringify({ query: mutation, variables }),
  });
  const result = await response.json();
  if (result.errors)
    throw new Error(result.errors[0]?.message || "Erreur API reactivateClub");
  return Boolean(result.data?.reactivateClub);
}
