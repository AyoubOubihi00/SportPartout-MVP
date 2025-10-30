// src/services/distanceService.ts

/**
 * Calcule la distance haversine (en kilomètres) entre deux points [lat, lng].
 */
export function haversineDistance(
  pos1: [number, number],
  pos2: [number, number],
): number {
  const toRad = (deg: number) => (deg * Math.PI) / 180;
  const R = 6371; // Rayon de la Terre en km
  const dLat = toRad(pos2[0] - pos1[0]);
  const dLng = toRad(pos2[1] - pos1[1]);
  const lat1 = toRad(pos1[0]);
  const lat2 = toRad(pos2[0]);

  const a =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.sin(dLng / 2) * Math.sin(dLng / 2) * Math.cos(lat1) * Math.cos(lat2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return R * c;
}
