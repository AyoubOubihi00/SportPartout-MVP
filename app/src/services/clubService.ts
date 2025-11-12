import { API_BASE_URL } from "../config";
import { ClubNear } from "../model/Club";

export interface Club {
  name: string;
  city: string;
}

export async function getAllClubs(token: string | null): Promise<Club[]> {
  const query = `query { clubs { name city } }`;
  const response = await fetch(`${API_BASE_URL}/findclub/graphql`, {
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
