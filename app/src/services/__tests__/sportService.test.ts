import { getSportsByName } from "../sportService";
import { sportTypes } from "../../mock/sportType";
import adminService from "../adminService";

jest.spyOn(adminService, "fetchSportTypes").mockResolvedValue(sportTypes as any);

describe("sportService", () => {
  it("getSportsByName retourne les sports correspondants", async () => {
    const result = await getSportsByName("Bas");
    expect(result.some((sport) => sport.name === "Basketball")).toBe(true);
  });

  it("getSportsByName retourne un tableau vide si aucun sport ne correspond", async () => {
    const result = await getSportsByName("Inexistant");
    expect(result).toEqual([]);
  });

  it("SPORTS contient des sports avec id, name et icon", () => {
    expect(Array.isArray(sportTypes)).toBe(true);
    expect(sportTypes[0]).toHaveProperty("id");
    expect(sportTypes[0]).toHaveProperty("name");
    expect(sportTypes[0]).toHaveProperty("icon");
  });
});
