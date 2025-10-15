import type { Club } from "../model/Club";
import type { SportType } from "../model/SportType";
import { graphqlFetch } from "./graphqlClient";

type GqlClub = {
  id: string;
  name: string;
  type: SportType;
  city: string;
  address: string;
  country: string;
  postalCode?: string | null;
  phoneNumber?: string | null;
  email?: string | null;
  latitude: number;
  longitude: number;
};

type ClubsQuery = { clubs: GqlClub[] };

const CLUBS_QUERY = `#graphql
  query Clubs {
    clubs {
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
  return {
    id: c.id,
    name: c.name,
    sportType: c.type,
    distance: Number(distanceKm?.toFixed?.(1) ?? distanceKm) || 0,
    notation,
    addresse: c.address,
    members,
    position: [c.latitude, c.longitude],
  };
}
