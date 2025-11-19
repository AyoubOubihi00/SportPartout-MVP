import { API_BASE_URL } from "../config";
import { SportType } from "../model/SportType";

function getAuthToken(): string | null {
  return typeof localStorage !== "undefined"
    ? localStorage.getItem("authToken")
    : null;
}

async function executeAdminGraphql<T>(
  query: string,
  variables?: Record<string, unknown>,
): Promise<T> {
  const token = getAuthToken();
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
  };
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  const resp = await fetch(`${API_BASE_URL.replace(/\/$/, "")}/admin/graphql`, {
    method: "POST",
    headers,
    body: JSON.stringify({ query, variables }),
  });

  if (!resp.ok) {
    const text = await resp.text().catch(() => "");
    throw new Error(`HTTP ${resp.status}: ${text}`);
  }

  const json = await resp.json();
  if (json.errors?.length) {
    throw new Error(
      json.errors.map((e: { message: string }) => e.message).join("; "),
    );
  }

  return json.data as T;
}

export async function fetchSportTypes(): Promise<SportType[]> {
  const query = `query SportTypes {\n  sportTypes {\n    id\n    name\n  }\n}`;
  try {
    const data = await executeAdminGraphql<{ sportTypes?: SportType[] }>(query);
    return data.sportTypes ?? [];
  } catch (e) {
    console.error("[adminService] fetchSportTypes failed:", e);
    return [];
  }
}

export default {
  fetchSportTypes,
};
