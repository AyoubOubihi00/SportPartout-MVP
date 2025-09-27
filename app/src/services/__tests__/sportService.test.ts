import { getSportsByName, SPORTS } from '../sportService';

describe('sportService', () => {
  it('getSportsByName retourne les sports correspondants', () => {
    const result = getSportsByName('Bas');
    expect(result.some(sport => sport.name === 'Basketball')).toBe(true);
  });

  it('getSportsByName retourne un tableau vide si aucun sport ne correspond', () => {
    const result = getSportsByName('Inexistant');
    expect(result).toEqual([]);
  });

  it('SPORTS contient des sports avec id, name et icon', () => {
    expect(Array.isArray(SPORTS)).toBe(true);
    expect(SPORTS[0]).toHaveProperty('id');
    expect(SPORTS[0]).toHaveProperty('name');
    expect(SPORTS[0]).toHaveProperty('icon');
  });
});
