import { useEffect, useMemo, useState } from "react";
import type { Club } from "../model/Club";
import { fetchClubs, toUiClub } from "../api/club";

function haversineKm(a: [number, number], b: [number, number]): number {
  const R = 6371;
  const dLat = ((b[0] - a[0]) * Math.PI) / 180;
  const dLon = ((b[1] - a[1]) * Math.PI) / 180;
  const lat1 = (a[0] * Math.PI) / 180;
  const lat2 = (b[0] * Math.PI) / 180;
  const sinDlat = Math.sin(dLat / 2);
  const sinDlon = Math.sin(dLon / 2);
  const h =
    sinDlat * sinDlat + Math.cos(lat1) * Math.cos(lat2) * sinDlon * sinDlon;
  const c = 2 * Math.atan2(Math.sqrt(h), Math.sqrt(1 - h));
  return R * c;
}

export default function useClubs(userPos?: [number, number] | null) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [clubs, setClubs] = useState<Club[]>([]);

  useEffect(() => {
    if (!userPos) return;

    let cancelled = false;
    setLoading(true);
    setError(null);

    fetchClubs()
      .then((gqlClubs) => {
        if (cancelled) return;

        const list = gqlClubs
          // On ne garde que les clubs avec latitude/longitude valides
          .filter(
            (c): c is typeof c & { latitude: number; longitude: number } =>
              typeof c.latitude === "number" && typeof c.longitude === "number",
          )
          .map((c) => {
            const distance = haversineKm(userPos, [c.latitude, c.longitude]);
            return toUiClub(c, distance);
          });

        setClubs(list);
      })
      .catch((e) => {
        if (!cancelled) {
          setError(e?.message || String(e));
        }
      })
      .finally(() => {
        if (!cancelled) {
          setLoading(false);
        }
      });

    return () => {
      cancelled = true;
    };
  }, [userPos]);

  const byDistance = useMemo(() => {
    return [...clubs].sort((a, b) => a.distance - b.distance);
  }, [clubs]);

  return { loading, error, clubs: byDistance };
}
