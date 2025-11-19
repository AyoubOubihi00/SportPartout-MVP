import { SportType } from "../model/SportType";
import adminService from "./adminService";

export async function getSportById(id: string): Promise<SportType | undefined> {
  const types = await adminService.fetchSportTypes();
  return types.find((t) => t.id === id) ?? undefined;
}

export function getSportsByName(name: string): Promise<SportType[]> {
  return adminService
    .fetchSportTypes()
    .then((types) =>
      types.filter((t) => t.name.toLowerCase().includes(name.toLowerCase())),
    );
}
