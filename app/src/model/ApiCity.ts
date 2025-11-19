export type ApiCity = {
  properties: {
    label: string;
    id: string;
    name: string;
    city: string;
    postcode: string;
    context?: string;
    country?: string;
    countrycode?: string;
  };
  geometry: {
    coordinates: [number, number];
  };
};
