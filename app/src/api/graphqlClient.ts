export function getGraphqlEndpoint(): string {
  const base = import.meta?.env?.VITE_CLUB_SERVICE_URL as string | undefined
  // If a base is set, append /graphql if missing
  if (base) {
    return base.endsWith("/graphql") ? base : `${base.replace(/\/$/, "")}/graphql`
  }
  // Default to same-origin /graphql and rely on Vite dev proxy
  return "/graphql"
}

export async function graphqlFetch<T>(query: string, variables?: Record<string, any>): Promise<T> {
  const resp = await fetch(getGraphqlEndpoint(), {
    method: "POST",
    headers: {
      "content-type": "application/json",
    },
    body: JSON.stringify({ query, variables }),
  })
  if (!resp.ok) {
    const text = await resp.text().catch(() => "")
    throw new Error(`GraphQL HTTP ${resp.status}: ${text}`)
  }
  const json = await resp.json()
  if (json.errors?.length) {
    throw new Error(json.errors.map((e: any) => e.message).join("; "))
  }
  return json.data as T
}

