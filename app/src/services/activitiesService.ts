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

export async function planningGraphqlFetch<T = SeanceWithAvailability>(
  query: string,
  variables?: Record<string, SeanceWithAvailability>,
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
    console.log("ici");
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
): Promise<T[]> {
  const query = `#graphql\n  query MySeances {\n    mySeances { ${selection} }\n  }`;

  const data = await planningGraphqlFetch<{ mySeances: T[] }>(query);
  return data?.mySeances ?? [];
}

export default {
  planningGraphqlFetch,
  fetchMySeances,
};
