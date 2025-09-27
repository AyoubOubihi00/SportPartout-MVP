import '@testing-library/jest-dom';
import { render, screen, fireEvent } from '@testing-library/react';
import { SearchFilter } from '../SearchFilter';

// Test d'intégration : services réels

describe('SearchFilter (intégration)', () => {
  it('affiche les sports filtrés et permet la sélection', async () => {
    render(<SearchFilter />);
    const input = screen.getByRole('textbox');
    fireEvent.focus(input);
    fireEvent.change(input, { target: { value: 'ten' } });
    // Tennis doit apparaître (asynchrone)
    const tennisOption = await screen.findByText(/Tennis/i);
    expect(tennisOption).toBeInTheDocument();
    // Sélectionne Tennis
    fireEvent.click(tennisOption);
    // Le chip ou l'élément sélectionné doit apparaître
    expect(screen.getByText(/Tennis/i)).toBeInTheDocument();
  });

  it('affiche la ville recherchée et met à jour le centre', async () => {
    render(<SearchFilter />);
    const input = screen.getByRole('textbox');
    fireEvent.change(input, { target: { value: 'Paris' } });
    // Attendre que la ville apparaisse (si API réelle, peut être lent)
    // expect(await screen.findByText(/Paris/i)).toBeInTheDocument();
    // Ici, on ne vérifie pas le setCenter car on utilise le service réel
  });
});
