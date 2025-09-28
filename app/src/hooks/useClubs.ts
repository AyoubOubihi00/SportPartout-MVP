import { useEffect, useMemo, useState } from "react"
import type { Club } from "../model/Club"
import { fetchClubs, toUiClub } from "../api/club"

function haversineKm(a: [number, number], b: [number, number]): number {
  const R = 6371
  const dLat = ((b[0] - a[0]) * Math.PI) / 180
  const dLon = ((b[1] - a[1]) * Math.PI) / 180
  const lat1 = (a[0] * Math.PI) / 180
  const lat2 = (b[0] * Math.PI) / 180
  const sinDlat = Math.sin(dLat / 2)
  const sinDlon = Math.sin(dLon / 2)
  const h = sinDlat * sinDlat + Math.cos(lat1) * Math.cos(lat2) * sinDlon * sinDlon
  const c = 2 * Math.atan2(Math.sqrt(h), Math.sqrt(1 - h))
  return R * c
}

export default function useClubs(userPos?: [number, number] | null) {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [clubs, setClubs] = useState<Club[]>([])

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    setError(null)
    fetchClubs()
      .then((gqlClubs) => {
        if (cancelled) return
        const list = gqlClubs.map((c) => {
          const distance = userPos ? haversineKm(userPos, [c.latitude, c.longitude]) : 0
          return toUiClub(c, distance)
        })
        setClubs(list)
      })
      .catch((e) => !cancelled && setError(e?.message || String(e)))
      .finally(() => !cancelled && setLoading(false))
    return () => {
      cancelled = true
    }
  }, [userPos?.[0], userPos?.[1]])

  const byDistance = useMemo(() => {
    return [...clubs].sort((a, b) => a.distance - b.distance)
  }, [clubs])

  return { loading, error, clubs: byDistance }
}

