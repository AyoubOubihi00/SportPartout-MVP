import { Sport } from '../model/Sport';

export const SPORTS: Sport[] = [
  { id: 'football', name: 'Football', icon: '⚽' },
  { id: 'basketball', name: 'Basketball', icon: '🏀' },
  { id: 'tennis', name: 'Tennis', icon: '🎾' },
  { id: 'natation', name: 'Natation', icon: '🏊' },
  { id: 'running', name: 'Running', icon: '🏃' },
  { id: 'volleyball', name: 'Volleyball', icon: '🏐' },
  { id: 'cyclisme', name: 'Cyclisme', icon: '🚴' },
  { id: 'musculation', name: 'Musculation', icon: '💪' },
  { id: 'yoga', name: 'Yoga', icon: '🧘' },
  { id: 'boxe', name: 'Boxe', icon: '🥊' },
];

export function getSportById(id: string): Sport | undefined {
  return SPORTS.find(sport => sport.id === id);
}

export function getSportsByName(name: string): Sport[] {
  return SPORTS.filter(sport => sport.name.toLowerCase().includes(name.toLowerCase()));
}