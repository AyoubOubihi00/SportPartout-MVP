import { useMemo } from "react";
import type { Activity, ActivityDto } from "../../types/activities";
import { mapDtoToActivity } from "../../types/activities";

export type UseActivitiesResult = {
  all: Activity[];
  upcoming: Activity[];
  past: Activity[];
};

export function useActivities(source?: ActivityDto[]): UseActivitiesResult {
  const all = useMemo(() => {
    const dtos = source ?? [];
    return dtos.map((d) => mapDtoToActivity(d));
  }, [source]);
  const upcoming = useMemo(
    () => all.filter((a) => a.status === "upcoming"),
    [all],
  );
  const past = useMemo(() => all.filter((a) => a.status === "past"), [all]);
  return { all, upcoming, past };
}
