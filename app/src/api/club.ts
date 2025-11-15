import type { Club } from "../model/Club";
import type { SportType } from "../model/SportType";
import { graphqlFetch } from "./graphqlClient";

type GqlClub = {
  id: string;
  name: string;
  typeId?: string | null;
  typeName?: string | null;
  type?: SportType | null;
  city?: string | null;
  address?: string | null;
  country?: string | null;
  postalCode?: string | null;
  phoneNumber?: string | null;
  email?: string | null;
  latitude?: number | null;
  longitude?: number | null;
};

type ClubsQuery = { clubs: GqlClub[] };
type ClubByIdQuery = { clubById?: GqlClub | null };

const CLUB_FIELDS = `
      id
      name
      typeId
      typeName
      city
      address
      country
      postalCode
      phoneNumber
      email
      latitude
      longitude
`;

const CLUBS_QUERY = `#graphql
  query Clubs {
    clubs {
      ${CLUB_FIELDS}
    }
  }
`;

const CLUB_BY_ID_QUERY = `#graphql
  query ClubById($id: UUID!) {
    clubById(id: $id) {
      ${CLUB_FIELDS}
    }
  }
`;

export async function fetchClubs(): Promise<GqlClub[]> {
  const data = await graphqlFetch<ClubsQuery>(CLUBS_QUERY);
  return data.clubs ?? [];
}

export function toUiClub(
  c: GqlClub,
  distanceKm = 0,
  notation = 4.2,
  members = 0,
): Club {
  const sportType: SportType = {
    id: c.typeId ?? c.type?.id ?? "",
    name: c.typeName ?? c.type?.name ?? "",
  };

  const latitude = c.latitude ?? 0;
  const longitude = c.longitude ?? 0;

  return {
    id: c.id,
    name: c.name,
    sportType,
    distance: Number(distanceKm?.toFixed?.(1) ?? distanceKm) || 0,
    notation,
    addresse: c.address ?? "",
    members,
    position: [latitude, longitude],
    latitude,
    longitude,
    city: c.city ?? undefined,
    country: c.country ?? undefined,
    postalCode: c.postalCode ?? undefined,
    phoneNumber: c.phoneNumber ?? undefined,
    email: c.email ?? undefined,
  };
}

export async function fetchClubById(
  id: string,
  options?: { distanceKm?: number; notation?: number; members?: number },
): Promise<Club | null> {
  const data = await graphqlFetch<ClubByIdQuery>(CLUB_BY_ID_QUERY, { id });
  if (!data.clubById) {
    return null;
  }
  return toUiClub(
    data.clubById,
    options?.distanceKm ?? 0,
    options?.notation ?? 4.2,
    options?.members ?? 0,
  );
}
