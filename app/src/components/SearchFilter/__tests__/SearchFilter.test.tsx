import '@testing-library/jest-dom';
import { render, screen, fireEvent } from '@testing-library/react';
import { SearchFilter } from '../SearchFilter';
import * as mapCenterService from '../../../services/mapCenterService';
import * as sportService from '../../../services/sportService';

jest.mock('../../../services/sportService');
jest.mock('../../../services/mapCenterService');


const mockSports = [
  { id: '1', name: 'Basket', icon: 'basket-icon' },
  { id: '2', name: 'Foot', icon: 'foot-icon' }
];

jest.spyOn(sportService, 'getSportsByName').mockReturnValue(mockSports);

jest.spyOn(mapCenterService.mapCenterService, 'setCenter').mockImplementation(jest.fn());
jest.spyOn(mapCenterService.mapCenterService, 'onCenterChange').mockImplementation(jest.fn());

describe('SearchFilter', () => {
  it('appelle setCenter du service quand une ville est sélectionnée', () => {
    render(<SearchFilter />);
    const city = { id: 'nancy', name: 'Nancy', postalCode: '54000', coordinates: [48.692054, 6.184417] };
    const input = screen.getByRole('textbox');
    fireEvent.change(input, { target: { value: 'Nancy' } });
    jest.spyOn(require('../../../services/cityApiService'), 'fetchCities').mockResolvedValue([city]);
    fireEvent.change(input, { target: { value: 'Nancy' } });
    setTimeout(() => {
      const cityOption = screen.getByText('Nancy');
      fireEvent.click(cityOption);
      expect(mapCenterService.mapCenterService.setCenter).toHaveBeenCalledWith([48.692054, 6.184417]);
    }, 0);
  });

  // Ajoute d'autres tests pour le style, le thème, etc.
});
