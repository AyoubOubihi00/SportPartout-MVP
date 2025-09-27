export type ApiCity = {
  properties: {
    label: string;
    id: string;
    name: string;
    city: string;
    postcode: string;
  };
  geometry: {
    coordinates: [number, number];
  };
}
