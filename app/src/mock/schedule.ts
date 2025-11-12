import type { TimeSlot } from "../components/Schedule/Schedule";
import { SeanceWithAvailability } from "../services/activitiesService";

/**
 * getScheduleForClubRemote
 * ------------------------
 * Fetches seances for a club from the planning service and maps them to
 * the local TimeSlot[] shape. This module is intentionally not providing
 * any deterministic/mock schedule; on error the function returns an empty
 * array (no fallback mock).
 */
export async function getScheduleForClubRemote(
  clubId: string,
  date: Date,
): Promise<TimeSlot[]> {
  const dayStart = new Date(
    date.getFullYear(),
    date.getMonth(),
    date.getDate(),
    0,
    0,
    0,
    0,
  );
  const dayEnd = new Date(dayStart);
  dayEnd.setDate(dayEnd.getDate() + 1);

  try {
    const { seancesByClubBetween } = await import(
      "../services/activitiesService"
    );
    const seances = await seancesByClubBetween(
      clubId,
      dayStart.toISOString(),
      dayEnd.toISOString(),
      "id startTime endTime nameSport capacite remainingPlaces status",
    );

    if (!Array.isArray(seances)) return [];

    const slots: TimeSlot[] = (seances as SeanceWithAvailability[])
      .filter((s) => s && s.startTime)
      .map((s) => {
        const start = new Date(s.startTime as string);
        const end = s.endTime
          ? new Date(s.endTime as string)
          : new Date(start.getTime() + 60 * 60000);
        const hour = start.getHours();
        const durationMinutes = Math.max(
          0,
          Math.round((end.getTime() - start.getTime()) / 60000),
        );
        const status: TimeSlot["status"] =
          s.status === "FULL"
            ? "booked"
            : s.status === "OPEN" || s.status === "PENDING"
              ? "session"
              : "available";

        return {
          id: s.id,
          startHour: hour,
          endHour: end.getHours(),
          status,
          title: `Séance de ${s.nameSport || "sport"}${durationMinutes ? ` (${durationMinutes} min)` : ""}`,
          subtitle: `Places restantes: ${s.remainingPlaces ?? "?"} / ${s.capacite ?? "?"}`,
        };
      });

    return slots;
  } catch (err) {
    console.error("getScheduleForClubRemote error", err);
    return [];
  }
}
