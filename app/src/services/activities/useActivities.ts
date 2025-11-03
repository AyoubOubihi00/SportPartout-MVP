import { useMemo } from "react";
import type { Activity, ActivityDto } from "../../types/activities";
import { mapDtoToActivity } from "../../types/activities";
import { mockActivitiesDto } from "./mock";

export type UseActivitiesResult = {
  all: Activity[];
  upcoming: Activity[];
  past: Activity[];
};

/**
 * Hook léger prêt pour brancher un back plus tard.
 * Aujourd'hui: utilise un mock local, mappe via mapDtoToActivity (typé).
 * futur: remplacer `source` par un fetch/GraphQL puis `mapDtoToActivity`.
 */
export function useActivities(source?: ActivityDto[]): UseActivitiesResult {
  const dtos = source ?? mockActivitiesDto;
  const all = useMemo(() => dtos.map((d) => mapDtoToActivity(d)), [dtos]);
  const upcoming = useMemo(
    () => all.filter((a) => a.status === "upcoming"),
    [all],
  );
  const past = useMemo(() => all.filter((a) => a.status === "past"), [all]);
  return { all, upcoming, past };
}

/*
// Exemple de fetcher (REST) 100% typé à brancher plus tard:
export async function fetchActivities(): Promise<ActivityDto[]> {
  const res = await fetch("/api/activities");
  if (!res.ok) throw new Error("Failed to load activities");
  const data: ActivityDto[] = await res.json();
  return data;
}

// Exemple avec TanStack Query:
// import { useQuery } from "@tanstack/react-query";
// export function useActivitiesQuery() {
//   return useQuery({ queryKey: ["activities"], queryFn: fetchActivities, staleTime: 60_000 });
// }
*/
