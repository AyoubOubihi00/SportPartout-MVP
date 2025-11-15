import { API_BASE_URL } from "../config";

export type GraphQLErrorLocation = {
  line: number;
  column: number;
};

export type GraphQLError = {
  message: string;
  locations?: GraphQLErrorLocation[];
  path?: (string | number)[];
  extensions?: Record<string, unknown>;
};

export function getGraphqlEndpoint(): string {
  const base = import.meta?.env?.VITE_CLUB_SERVICE_URL as string | undefined;
  // If a base is set, append /graphql if missing
  if (base) {
    return base.endsWith("/graphql")
      ? base
      : `${base.replace(/\/$/, "")}/graphql`;
  }
  const normalized = API_BASE_URL.replace(/\/$/, "");
  return `${normalized}/club/graphql`;
}

export async function graphqlFetch<T>(
  query: string,
  variables?: Record<string, string>,
): Promise<T> {
  const headers: Record<string, string> = {
    "content-type": "application/json",
  };
  const token = getAuthToken();
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  const resp = await fetch(getGraphqlEndpoint(), {
    method: "POST",
    headers,
    body: JSON.stringify({ query, variables }),
  });
  if (!resp.ok) {
    const text = await resp.text().catch(() => "");
    throw new Error(`GraphQL HTTP ${resp.status}: ${text}`);
  }
  const json = await resp.json();

  if (json.errors?.length) {
    throw new Error(json.errors.map((e: GraphQLError) => e.message).join("; "));
  }
  return json.data as T;
}

function getAuthToken(): string | null {
  if (typeof localStorage === "undefined") {
    return null;
  }
  try {
    return localStorage.getItem("authToken");
  } catch {
    return null;
  }
}
