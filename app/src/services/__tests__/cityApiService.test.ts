import { fetchCities } from "../cityApiService";

describe("cityApiService", () => {
  it("fetchCities retourne un tableau vide si la requête est trop courte", async () => {
    const result = await fetchCities("a");
    expect(result).toEqual([]);
  });

  it("fetchCities retourne des villes pour une requête valide (mock)", async () => {
    global.fetch = jest.fn().mockResolvedValue({
      ok: true,
      json: async () => ({
        features: [
          {
            properties: { id: "1", city: "Paris", postcode: "75000" },
            geometry: { coordinates: [2.3522, 48.8566] },
          },
        ],
      }),
    });
    const result = await fetchCities("Paris");
    expect(result[0].name).toBe("Paris");
    expect(result[0].coordinates).toEqual([48.8566, 2.3522]);
  });
});
