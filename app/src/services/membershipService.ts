import { BFF_BASE_URL } from "../config";

export interface UserInput {
  userId: string;
  firstName: string;
  lastName: string;
  birthDate: string;
  email: string;
  phoneNumber?: string | null;
}

export interface ClubSnapshot {
  id: string;
  ownerId?: string | null;
  name?: string | null;
  typeId?: string | null;
  typeName?: string | null;
  city?: string | null;
  address?: string | null;
  country?: string | null;
  postalCode?: string | null;
  phoneNumber?: string | null;
  email?: string | null;
  latitude?: number | null;
  longitude?: number | null;
  createdAt?: string | null;
  updatedAt?: string | null;
  deletedAt?: string | null;
  isActive?: boolean | null;
}

export interface MemberWithClub {
  clubId: string;
  userId: string;
  firstName: string;
  lastName: string;
  birthDate?: string | null;
  email: string;
  phoneNumber?: string | null;
  isActif?: boolean | null;
  dateSubscription?: string | null;
  dateUnsubscription?: string | null;
  himself?: boolean | null;
  club?: ClubSnapshot | null;
}

export interface SubscribeResponse {
  success: boolean;
  data?: MemberWithClub;
  message?: string;
}

function getAuthToken(): string | null {
  return typeof localStorage !== "undefined"
    ? localStorage.getItem("authToken")
    : null;
}

async function executeGraphql<T>(
  query: string,
  variables: Record<string, unknown>,
  token: string,
): Promise<T> {
  const response = await fetch(`${BFF_BASE_URL}/graphql`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify({ query, variables }),
  });

  if (!response.ok) {
    const payload = await response.text().catch(() => "");
    throw new Error(`HTTP ${response.status}: ${payload}`);
  }

  const json = await response.json();
  if (json.errors?.length) {
    throw new Error(json.errors[0]?.message || "Erreur GraphQL");
  }

  return json.data as T;
}

export async function subscribeToClub(
  clubId: string,
  newMember: UserInput,
): Promise<SubscribeResponse> {
  const token = getAuthToken();
  if (!token) {
    return { success: false, message: "Utilisateur non authentifie" };
  }

  const query = `mutation SubscribeToClub($clubId: UUID!, $newMember: UserInput!) {
  subscribeToClub(clubId: $clubId, newMember: $newMember) {
    clubId
    userId
    firstName
    lastName
    birthDate
    email
    phoneNumber
    isActif
    dateSubscription
    dateUnsubscription
    himself
    club {
      id
      ownerId
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
      createdAt
      updatedAt
      deletedAt
    }
  }
}`;

  try {
    const data = await executeGraphql<{ subscribeToClub?: MemberWithClub }>(
      query,
      { clubId, newMember },
      token,
    );
    if (!data.subscribeToClub) {
      return { success: false, message: "Reponse inattendue du serveur" };
    }
    return { success: true, data: data.subscribeToClub };
  } catch (e) {
    const message = e instanceof Error ? e.message : String(e);
    return { success: false, message };
  }
}

export async function fetchMyClubs(userId: string): Promise<MemberWithClub[]> {
  const token = getAuthToken();
  if (!token) {
    throw new Error("Utilisateur non authentifie");
  }

  const query = `query MyClubs($userId: UUID!) {
  myClubs(userId: $userId) {
    clubId
    userId
    firstName
    lastName
    birthDate
    email
    phoneNumber
    isActif
    dateSubscription
    dateUnsubscription
    himself
    club {
      id
      ownerId
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
      createdAt
      updatedAt
      deletedAt
    }
  }
}`;

  const data = await executeGraphql<{ myClubs?: MemberWithClub[] }>(
    query,
    { userId },
    token,
  );

  return data.myClubs ?? [];
}

export async function unsubscribeFromClub(
  clubId: string,
  memberId: string,
): Promise<boolean> {
  const token = getAuthToken();
  if (!token) {
    throw new Error("Utilisateur non authentifie");
  }

  const mutation = `mutation UnsubscribeFromClub($clubId: UUID!, $memberId: UUID!) {
  unsubscribeFromClub(clubId: $clubId, memberId: $memberId)
}`;

  const data = await executeGraphql<{ unsubscribeFromClub?: boolean }>(
    mutation,
    { clubId, memberId },
    token,
  );

  return Boolean(data.unsubscribeFromClub);
}

export async function fetchManagedClubs(
  ownerId: string,
): Promise<ClubSnapshot[]> {
  const token = getAuthToken();
  if (!token) {
    throw new Error("Utilisateur non authentifie");
  }

  const query = `query ManagedClubs($ownerId: UUID!) {
  managedClubs(ownerId: $ownerId) {
    id
    ownerId
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
    deletedAt
    isActive
  }
}`;

  const data = await executeGraphql<{ managedClubs?: ClubSnapshot[] }>(
    query,
    { ownerId },
    token,
  );

  return data.managedClubs ?? [];
}

export async function fetchClubMembers(
  clubId: string,
  token?: string | null,
): Promise<MemberWithClub[]> {
  const authToken = token || getAuthToken();
  if (!authToken) {
    throw new Error("Utilisateur non authentifie");
  }

  const query = `query ClubMembersByClubId($clubId: UUID!) {
  clubMembersByClubId(clubId: $clubId) {
    userId
    clubId
    firstName
    lastName
    birthDate
    email
    phoneNumber
    isActif
    dateSubscription
    dateUnsubscription
    himself
    club {
      id
      ownerId
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
      createdAt
      updatedAt
      deletedAt
    }
  }
}`;

  const variables = { clubId };
  const response = await fetch(`${BFF_BASE_URL}/graphql`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${authToken}`,
    },
    body: JSON.stringify({ query, variables }),
  });

  if (!response.ok) {
    const payload = await response.text().catch(() => "");
    throw new Error(`HTTP ${response.status}: ${payload}`);
  }

  const result = await response.json();
  if (process.env.NODE_ENV !== "production") {
    try {
      const sample = (result?.data?.AllClubMembersByClubId ?? []).slice(0, 3);
      console.log("[DEBUG] Raw GraphQL members sample:", sample);
      if (sample[0]) {
        console.log("[DEBUG] First member keys:", Object.keys(sample[0]));
      }
    } catch (err) {
      console.warn("[DEBUG] Failed to introspect GraphQL result:", err);
    }
  }
  if (result.errors?.length) {
    throw new Error(result.errors[0]?.message || "Erreur API fetchClubMembers");
  }

  return result.data?.clubMembersByClubId ?? [];
}

export async function forcedUnsubscribe(
  clubId: string,
  memberId: string,
): Promise<number> {
  const authToken = getAuthToken();
  if (!authToken) {
    throw new Error("Utilisateur non authentifie");
  }

  console.log("[BFF] forcedUnsubscribe clubId=", clubId, "memberId=", memberId);
  const mutation =
    "mutation ForcedUnsubscribe($clubId: UUID!, $memberId: UUID!) {\n  forcedUnsubscribe(clubId: $clubId, memberId: $memberId)\n}";
  const response = await fetch(`${BFF_BASE_URL}/graphql`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${authToken}`,
    },
    body: JSON.stringify({ query: mutation, variables: { clubId, memberId } }),
  });
  const result = await response.json();
  if (result.errors) {
    throw new Error(result.errors[0].message);
  }
  return result.data?.forcedUnsubscribe ?? 0;
}
