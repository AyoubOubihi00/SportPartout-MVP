export interface UserProfile {
  id: string;
  email: string;
  createdAt: string;
  role: string;
  firstName: string;
  lastName: string;
  birthDate: string;
  phone: string;
}

export async function getMe(token: string): Promise<UserProfile | null> {
  const query =
    "query Me { me { id email createdAt role firstName lastName birthDate phone } }";
  try {
    const response = await fetch(`${API_BASE_URL}/auth/graphql`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({ query }),
    });
    const result = await response.json();
    if (result.errors) {
      return result.errors;
    }
    return result.data?.me ?? null;
  } catch {
    return null;
  }
}

export interface LoginPayload {
  email: string;
  password: string;
}

export interface LoginResponse {
  success: boolean;
  token?: string;
  message?: string;
}

export async function login(payload: LoginPayload): Promise<LoginResponse> {
  const query =
    "mutation Login($input: LoginInput!) {\n  login(input: $input) {\n    token\n     }\n}";
  const variables = {
    input: {
      email: payload.email,
      password: payload.password,
    },
  };
  try {
    const response = await fetch(`${API_BASE_URL}/auth/public`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ query, variables }),
    });
    const result = await response.json();
    if (result.errors) {
      return {
        success: false,
        message: result.errors[0]?.message || "Erreur lors de la connexion",
      };
    }
    const data = result.data?.login;
    if (data?.token) {
      return {
        success: true,
        token: data.token,
      };
    }
    return {
      success: false,
      message: data?.message || "Erreur lors de la connexion",
    };
  } catch (error) {
    if (error instanceof Error) {
      return {
        success: false,
        message: error.message || "Erreur lors de la connexion",
      };
    }
    return {
      success: false,
      message: "Erreur lors de la connexion",
    };
  }
}

import { API_BASE_URL } from "../../config";

export interface RegisterPayload {
  email: string;
  password: string;
  lastName: string;
  firstName: string;
  birthDate: string; // format: YYYY-MM-DDTHH:mm:ssZ
  phone: string;
}

export interface RegisterResponse {
  success: boolean;
  message?: string;
  data?: unknown;
}

export async function register(
  payload: RegisterPayload,
): Promise<RegisterResponse> {
  const query =
    "mutation Register($input: RegisterInput!) {\n  register(input: $input) {\n    message\n  }\n}";
  const variables = {
    input: {
      email: payload.email,
      password: payload.password,
      lastName: payload.lastName,
      firstName: payload.firstName,
      birthDate: payload.birthDate,
      phone: payload.phone,
    },
  };
  try {
    const response = await fetch(`${API_BASE_URL}/auth/public`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ query, variables }),
    });
    const result = await response.json();
    if (result.errors) {
      return {
        success: false,
        message: result.errors[0]?.message || "Erreur lors de l’inscription",
      };
    }
    return {
      success: true,
      data: result.data?.register,
    };
  } catch (error) {
    if (error instanceof Error) {
      return {
        success: false,
        message: error.message || "Erreur lors de l’inscription",
      };
    }
    return {
      success: false,
      message: "Erreur lors de l’inscription",
    };
  }
}

// --- Update User ---
export interface UpdateUserInput {
  firstName?: string;
  lastName?: string;
  birthDate?: string; // ISO string (DateTime)
  phone?: string;
}

export interface UpdateUserResponse {
  message?: string;
}

/**
 * Met à jour le profil utilisateur via l'API GraphQL.
 * Utilise le token stocké dans localStorage (clé `authToken`) si présent.
 */
export async function updateUser(
  input: UpdateUserInput,
): Promise<UpdateUserResponse> {
  // try to get token from localStorage
  const token =
    typeof localStorage !== "undefined"
      ? localStorage.getItem("authToken")
      : null;

  if (!token) {
    return { message: "Utilisateur non authentifié" };
  }

  const query =
    "mutation UpdateUser($input: UpdateUserInput!) {\n  updateUser(input: $input) {\n  message\n   }\n}";

  const variables = { input };

  try {
    const response = await fetch(`${API_BASE_URL}/auth/graphql`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({ query, variables }),
    });

    const result = await response.json();

    if (result.errors) {
      return {
        message: result.errors[0]?.message || "Erreur lors de la mise à jour",
      };
    }

    const payload = result.data?.updateUser;
    if (!payload) {
      return { message: "Réponse inattendue du serveur" };
    }

    return {
      message: payload.message,
    };
  } catch (error) {
    if (error instanceof Error) {
      return { message: error.message };
    }
    return { message: "Erreur réseau lors de la mise à jour" };
  }
}
