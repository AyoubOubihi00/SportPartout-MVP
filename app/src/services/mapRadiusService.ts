// src/services/mapRadiusService.ts

/**
 * Calcule la distance (en kilomètres) entre deux points GPS (lat, lng) avec la formule de Haversine.
 */
export function haversineDistance(
  lat1: number,
  lng1: number,
  lat2: number,
  lng2: number,
): number {
  const R = 6371; // Rayon de la Terre en km
  const dLat = deg2rad(lat2 - lat1);
  const dLng = deg2rad(lng2 - lng1);
  const a =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(deg2rad(lat1)) *
      Math.cos(deg2rad(lat2)) *
      Math.sin(dLng / 2) *
      Math.sin(dLng / 2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return R * c;
}

function deg2rad(deg: number): number {
  return deg * (Math.PI / 180);
}

/**
 * Calcule le rayon estimé de la carte (distance entre le centre et un coin)
 * @param center [lat, lng]
 * @param corner [lat, lng]
 * @returns rayon en kilomètres
 */
export function estimateMapRadius(
  center: [number, number],
  corner: [number, number],
): number {
  return haversineDistance(center[0], center[1], corner[0], corner[1]);
}
