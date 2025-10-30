import { SportType } from "./SportType";

export type Club = {
  id: string;
  name: string;
  sportType: SportType;
  distance: number;
  notation: number;
  addresse: string;
  members: number;
  latitude?: number;
  longitude?: number;
  position: [number, number];

  // Données additionnelles (fixtures)
  city?: string;
  country?: string;
  postalCode?: string;
  phoneNumber?: string;
  email?: string;
  dateCreation?: string;
  description?: string;
};

export type ClubNear = {
  id: string;
  name: string;
  typeIdSport: string;
  typeNameSport: string;
  distanceKm: number;
  notation: number;
  address: string;
  memberCount: number;
  latitude: number;
  longitude: number;
  description: string;
  city: string;
  country: string;
  postalCode: string;
  phoneNumber: string;
  email: string;
  dateCreation: string;
};
