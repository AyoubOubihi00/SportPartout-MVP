// src/services/jwtUtils.ts

/**
 * Décode un JWT (JSON Web Token) et retourne son payload (objet).
 */

interface JwtPayload {
  exp?: number;
}

export function decodeJwt(token: string): JwtPayload | null {
  try {
    const payload = token.split(".")[1];
    const decoded = atob(payload.replace(/-/g, "+").replace(/_/g, "/"));
    return JSON.parse(decodeURIComponent(escape(decoded)));
  } catch {
    return null;
  }
}

/**
 * Vérifie si un JWT est expiré (en se basant sur le champ exp du payload).
 */
export function isJwtExpired(token: string): boolean {
  const payload = decodeJwt(token);
  console.log("JWT Payload:", payload);
  if (!payload || !payload.exp) return true;
  const now = Math.floor(Date.now() / 1000);
  return payload.exp < now;
}
