import type { TimeSlot } from "../components/Schedule/Schedule";

// Simple fixtures by club id for today's schedule
// You can adapt to fetch from API later.
export function getScheduleForClub(clubId: string, date: Date): TimeSlot[] {
  // Deterministic sample based on club id + date
  const daySeed =
    date.getFullYear() * 1000 + date.getMonth() * 50 + date.getDate();
  const base = Math.abs(hashCode(clubId) + daySeed) % 4; // 0..3
  const slots: TimeSlot[] = [];

  const sessionDetails = [
    "Entraînement U17",
    "Entraînement Seniors 1",
    "Entraînement Seniors 2",
    "Cours découverte",
    "Atelier technique",
  ];

  for (let h = 8; h < 20; h++) {
    const mod = (h + base) % 8;
    if (mod === 0) {
      slots.push({
        hour: h,
        status: "booked",
        title: "Privé",
        subtitle: "Salle réservée",
      });
    } else if (mod === 2 || mod === 5) {
      const detail = sessionDetails[(h + base) % sessionDetails.length];
      slots.push({
        hour: h,
        status: "session",
        title: "Séance",
        subtitle: detail,
      });
    } else if (mod === 3 || mod === 6) {
      slots.push({
        hour: h,
        status: "available",
        title: "Disponible",
        subtitle: "Créneau libre",
      });
    } else {
      slots.push({ hour: h, status: "closed" });
    }
  }
  return slots;
}

function hashCode(s: string): number {
  let h = 0;
  for (let i = 0; i < s.length; i++)
    h = (Math.imul(31, h) + s.charCodeAt(i)) | 0;
  return h;
}
