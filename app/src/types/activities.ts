import { Seance } from "../services/activitiesService";

export type ActivityStatus = "upcoming" | "past";

export interface Activity {
  id: string;
  title: string;
  club: string;
  sport: string;
  date: string; // YYYY-MM-DD
  start: string; // HH:mm
  end: string; // HH:mm
  place?: string;
  status: ActivityStatus;
  imageUrl?: string;
  capacity?: number;
  booked?: number;
  coach?: string;
  level?: string;
  description?: string;
}

// DTO proposé pour un futur back (REST/GraphQL)
export interface ActivityDto {
  id: string;
  title: string;
  club: string;
  sport: string;
  startIso: string; // 2025-10-18T10:00:00Z
  endIso: string; // 2025-10-18T11:00:00Z
  place?: string;
  imageUrl?: string;
  capacity?: number;
  booked?: number;
  coach?: string;
  level?: string;
  description?: string;
}

export interface ActivityFull {
  id: string;
  title: string;
  club: string;
  sport: string;
  startIso: string;
  endIso: string;
  place?: string;
  imageUrl?: string;
  capacity?: number;
  booked?: number;
  coach?: string;
  level?: string;
  description?: string;
  status: string;
}

function pad2(n: number): string {
  return String(n).padStart(2, "0");
}

export function mapDtoToActivity(
  dto: ActivityDto,
  now: Date = new Date(),
): Activity {
  const start = new Date(dto.startIso);
  const end = new Date(dto.endIso);
  const status: ActivityStatus =
    end.getTime() < now.getTime() ? "past" : "upcoming";
  const date = `${start.getFullYear()}-${pad2(start.getMonth() + 1)}-${pad2(start.getDate())}`;
  const startLabel = `${pad2(start.getHours())}:${pad2(start.getMinutes())}`;
  const endLabel = `${pad2(end.getHours())}:${pad2(end.getMinutes())}`;

  return {
    id: dto.id,
    title: dto.title,
    club: dto.club,
    sport: dto.sport,
    date,
    start: startLabel,
    end: endLabel,
    place: dto.place,
    status,
    imageUrl: dto.imageUrl,
    capacity: dto.capacity,
    booked: dto.booked,
    coach: dto.coach,
    level: dto.level,
    description: dto.description,
  };
}

export function mapSeanceToActivity(seance: Seance): ActivityFull {
  const now = new Date();
  const end = new Date(seance.endTime);
  const status: ActivityStatus =
    end.getTime() < now.getTime() ? "past" : "upcoming";
  return {
    id: seance.id,
    title: seance.nameSport ?? "Séance",
    coach: seance.coachName ?? "Coach non indiqué",
    club: seance.clubName ?? "Club",
    place: `${seance.adressSeance ?? ""}, ${seance.citySeance ?? ""}`,
    sport: seance.nameSport ?? "Sport",
    startIso: seance.startTime,
    endIso: seance.endTime,
    capacity: seance.capacite,
    booked: seance.capaSeance,
    description: seance.description ?? "",
    status: status,
  };
}
