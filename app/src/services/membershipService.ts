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
  }
}`;

  const data = await executeGraphql<{ managedClubs?: ClubSnapshot[] }>(
    query,
    { ownerId },
    token,
  );

  return data.managedClubs ?? [];
}
