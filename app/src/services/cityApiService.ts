import { City } from "../model/City";
import { ApiCity } from "../model/ApiCity";

const API_URL = "https://data.geopf.fr/geocodage/search";
const DEFAULT_COUNTRY = "France";

export async function fetchCities(
  query: string,
  selectedCity?: City | null,
): Promise<City[]> {
  if (query.length < 2) return [];
  const url = `${API_URL}?q=${encodeURIComponent(query)}&autocomplete=1&index=address&limit=3&returntruegeometry=false&type=municipality`;
  try {
    const response = await fetch(url);
    if (!response.ok) return [];
    const data = await response.json();
    const cities: City[] = data.features.map((feature: ApiCity) => ({
      id: feature.properties.id,
      name: feature.properties.city || feature.properties.name,
      postalCode: feature.properties.postcode,
      coordinates: feature.geometry.coordinates
        ? [feature.geometry.coordinates[1], feature.geometry.coordinates[0]]
        : undefined,
    }));
    return selectedCity
      ? cities.filter((city) => city.id !== selectedCity.id)
      : cities;
  } catch {
    return [];
  }
}

export async function fetchAddress(query: string): Promise<City[]> {
  if (query.length < 2) return [];
  const url = `${API_URL}?q=${encodeURIComponent(query)}&autocomplete=1&index=address&limit=5`;
  try {
    const response = await fetch(url);
    if (!response.ok) return [];
    const data = await response.json();
    return (data.features || []).map((feature: ApiCity) => ({
      id: feature.properties.id,
      name: feature.properties.label,
      city: feature.properties.city,
      postalCode: feature.properties.postcode,
      country:
        feature.properties.country ??
        feature.properties.countrycode ??
        DEFAULT_COUNTRY,
      coordinates: feature.geometry.coordinates
        ? [feature.geometry.coordinates[1], feature.geometry.coordinates[0]]
        : undefined,
    })) as City[];
  } catch {
    return [];
  }
}
