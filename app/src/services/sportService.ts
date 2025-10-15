import { Sport } from "../model/Sport";
import { sportTypes } from "../mock/sportType";

export function getSportById(id: string): Sport | undefined {
  return sportTypes.find((sport) => sport.id === id);
}

export function getSportsByName(name: string): Sport[] {
  return sportTypes.filter((sport) =>
    sport.name.toLowerCase().includes(name.toLowerCase()),
  );
}
