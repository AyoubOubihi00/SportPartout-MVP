// Service pour interroger le service planning via /planning/graphql
// Fournit un helper planningGraphqlFetch et une fonction fetchMySeances
import { API_BASE_URL } from "../config";

export interface SeanceWithAvailability {
  id: string;
  idClub: string;
  idTerrain: string;
  idCoach?: string | null;
  startTime: string; // ISO DateTime
  endTime: string; // ISO DateTime
  durationMinutes: number;
  capacite: number;
  capaSeance: number;
  remainingPlaces: number;
  full: boolean;
  idSport: string;
  nameSport: string;
  description?: string | null;
  status: string; // SeanceStatus enum name as string
  createdBy?: string | null;
}

export interface Seance {
  id: string;
  idClub: string;
  clubName: string;
  adressSeance: string;
  citySeance: string;
  idTerrain: string;
  terrainName: string;
  idCoach?: string | null;
  coachName?: string | null;
  startTime: string; // ISO DateTime
  endTime: string; // ISO DateTime
  capacite: number;
  capaSeance: number;
  idSport: string;
  nameSport: string;
  description?: string | null;
  status: string; // SeanceStatus enum name as string
  createdBy?: string | null;
}

export async function planningGraphqlFetch<T = SeanceWithAvailability>(
  query: string,
  variables?: Record<string, SeanceWithAvailability | Seance>,
): Promise<T> {
  const token =
    typeof localStorage !== "undefined"
      ? localStorage.getItem("authToken")
      : null;

  const resp = await fetch(`${API_BASE_URL}/planning/graphql`, {
    method: "POST",
    headers: {
      "content-type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify({ query, variables }),
  });

  if (!resp.ok) {
    const text = await resp.text().catch(() => "");
    throw new Error(`Planning GraphQL HTTP ${resp.status}: ${text}`);
  }

  const json = await resp.json();
  if (json.errors?.length) {
    const messages = json.errors
      .map((e: unknown) => {
        if (e && typeof e === "object" && "message" in e) {
          const maybe = (e as { message?: unknown }).message;
          if (typeof maybe === "string") return maybe;
        }
        try {
          return JSON.stringify(e);
        } catch {
          return String(e);
        }
      })
      .join("; ");
    throw new Error(messages);
  }
  return json.data as T;
}

export async function planningPublicGraphqlFetch<T = SeanceWithAvailability>(
  query: string,
  variables?: Record<string, SeanceWithAvailability>,
): Promise<T> {
  console.log(JSON.stringify({ query, variables }));
  const resp = await fetch(`${API_BASE_URL}/planning/public`, {
    method: "POST",
    headers: {
      "content-type": "application/json",
    },
    body: JSON.stringify({ query, variables }),
  });

  if (!resp.ok) {
    const text = await resp.text().catch(() => "");
    throw new Error(`Planning GraphQL HTTP ${resp.status}: ${text}`);
  }

  const json = await resp.json();
  if (json.errors?.length) {
    const messages = json.errors
      .map((e: unknown) => {
        if (e && typeof e === "object" && "message" in e) {
          const maybe = (e as { message?: unknown }).message;
          if (typeof maybe === "string") return maybe;
        }
        try {
          return JSON.stringify(e);
        } catch {
          return String(e);
        }
      })
      .join("; ");
    throw new Error(messages);
  }
  return json.data as T;
}

/**
 * Récupère les séances de l'utilisateur (mySeances).
 *
 * Par défaut, la sélection GraphQL minimale est `id`. Pour demander d'autres champs,
 * fournissez la chaîne `selection` (ex: `id startDate endDate availability { seats }`).
 *
 * Exemple d'appel:
 *   fetchMySeances()` => enverra `query MySeances { mySeances { id } }`
 *   fetchMySeances('id startDate endDate')`
 *
 */
export async function fetchMySeances<T = SeanceWithAvailability>(
  selection = "id",
  start: string | null = null,
  end: string | null = null,
): Promise<T[]> {
  console.log("ici");
  const query = `#graphql\n  query mySeances($start: DateTime, $end: DateTime) {\n    mySeances(start: $start, end: $end) { ${selection} }\n  }`;
  const variables = { start, end };

  console.log(variables as unknown as Record<string, SeanceWithAvailability>);
  const data = await planningGraphqlFetch<{ mySeances: T[] }>(
    query,
    variables as unknown as Record<string, SeanceWithAvailability>,
  );
  return data?.mySeances ?? [];
}

/**
 * Récupère les séances d'un club entre deux dates.
 *
 * - `clubId` : UUID du club
 * - `start` / `end` : DateTime ISO strings (ex: '2025-11-09T00:00:00Z')
 * - `selection` : champs GraphQL à récupérer pour chaque séance
 */
export async function seancesByClubBetween<T = SeanceWithAvailability>(
  clubId: string,
  start: string,
  end: string,
  selection = "id",
): Promise<T[]> {
  const query = `#graphql\n  query seancesWithAvailabilityByClubBetween($clubId: UUID!, $start: DateTime!, $end: DateTime!) {\n    seancesWithAvailabilityByClubBetween(clubId: $clubId, start: $start, end: $end) { ${selection} }\n  }`;

  const variables = { clubId, start, end };
  const data = await planningPublicGraphqlFetch<{
    seancesWithAvailabilityByClubBetween: T[];
  }>(query, variables as unknown as Record<string, SeanceWithAvailability>);

  return data?.seancesWithAvailabilityByClubBetween ?? [];
}

/**
 * Récupère une séance par son id via la query `seance(id: UUID!): Seance`.
 * Utilise le endpoint authentifié (planning/graphql) afin d'obtenir
 * éventuellement des champs réservés.
 *
 * @param id UUID de la séance
 * @param selection champs GraphQL souhaités (ex: 'id idClub nameSport startTime endTime')
 */
export async function fetchSeanceById<T = Seance>(
  id: string,
  selection = "id idClub clubName adressSeance citySeance idTerrain terrainName idCoach coachName startTime endTime capacite capaSeance idSport nameSport description status createdBy",
): Promise<T> {
  const query = `#graphql\n  query seance($id: UUID!) {\n    seance(id: $id) { ${selection} }\n  }`;

  const variables = { id };

  const data = await planningGraphqlFetch<{ seance: T }>(
    query,
    variables as unknown as Record<string, Seance>,
  );

  return data?.seance as T;
}

/**
 * Rejoint une séance (joinSeance mutation).
 *
 * - `id` : UUID de la séance
 * - `selection` : champs GraphQL à récupérer pour la séance retournée
 */
export async function joinSeance<T = Seance>(
  id: string,
  selection = "id",
): Promise<T> {
  const query = `#graphql\n  mutation JoinSeance($id: UUID!) {\n    joinSeance(id: $id) { ${selection} }\n  }`;

  const variables = { id };

  const data = await planningGraphqlFetch<{ joinSeance: T }>(
    query,
    variables as unknown as Record<string, SeanceWithAvailability>,
  );

  return data?.joinSeance as T;
}

/**
 * Quittter une séance (leaveSeance mutation).
 * - `id` : UUID de la séance
 * - `selection` : champs GraphQL à récupérer pour la séance retournée
 */

export async function leaveSeance<T = Seance>(
  id: string,
  selection = "id",
): Promise<T> {
  const query = `#graphql\n  mutation LeaveSeance($id: UUID!) {\n    leaveSeance(id: $id) { ${selection} }\n  }`;

  const variables = { id };

  const data = await planningGraphqlFetch<{ leaveSeance: T }>(
    query,
    variables as unknown as Record<string, SeanceWithAvailability>,
  );

  return data?.leaveSeance as T;
}

export default {
  planningGraphqlFetch,
  fetchMySeances,
  seancesByClubBetween,
  joinSeance,
  leaveSeance,
};
