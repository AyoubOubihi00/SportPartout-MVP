import { useState, useRef, useEffect, useCallback } from 'react';
import { Input, Box, Button, Typography, CircularProgress } from '@mui/joy';
import { useTheme } from '@mui/joy/styles';
import { Portal } from '@mui/base';
import { Sport } from '../../model/Sport';
import { City } from '../../model/City';
import * as sportService from '../../services/sportService';
import { fetchCities } from '../../services/cityApiService';
import { mapCenterService } from '../../services/mapCenterService';

export function SearchFilter() {
  const theme = useTheme();
  const [isOpen, setIsOpen] = useState(false);
  const [searchValue, setSearchValue] = useState('');
  const [selectedSports, setSelectedSports] = useState<Sport[]>([]);
  const [selectedCity, setSelectedCity] = useState<City | null>(null);
  const [apiCities, setApiCities] = useState<City[]>([]);
  const [isLoadingCities, setIsLoadingCities] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const debounceTimerRef = useRef<NodeJS.Timeout | null>(null);

  const searchCities = useCallback(async (query: string) => {
    setIsLoadingCities(true);
    const cities = await fetchCities(query, selectedCity);
    setApiCities(cities);
    setIsLoadingCities(false);
  }, [selectedCity]);

  useEffect(() => {
    if (debounceTimerRef.current) clearTimeout(debounceTimerRef.current);
    debounceTimerRef.current = setTimeout(() => {
      if (searchValue.trim()) searchCities(searchValue.trim());
      else setApiCities([]);
    }, 300);
    return () => {
      if (debounceTimerRef.current) clearTimeout(debounceTimerRef.current);
    };
  }, [searchValue, searchCities]);

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(e.target as Node) &&
        !inputRef.current?.contains(e.target as Node)
      ) {
        inputRef.current?.blur();
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const filteredSports = sportService.getSportsByName(searchValue).slice(0, 3);

  const handleSportSelect = (sport: Sport) => {
    setSelectedSports([...selectedSports, sport]);
    setSearchValue('');
    setIsOpen(false);
  };

  const handleCitySelect = (city: City) => {
    setSelectedCity(city);
    setSearchValue('');
    setIsOpen(false);
    if (city.coordinates) {
      mapCenterService.setCenter(city.coordinates);
    }
  };

  const removeSport = (id: string) => setSelectedSports(selectedSports.filter(s => s.id !== id));
  const removeCity = () => setSelectedCity(null);

  const darkGradient = 'linear-gradient(135deg, #0f172a, #1e293b)';
  const lightGradient = 'linear-gradient(135deg, #f8fafc, #e2e8f0)';

  return (
    <Box
      sx={{
        width: '100%',
        maxWidth: 600,
        mx: 'auto',
        position: 'relative',
        zIndex: 1000,
      }}
    >
      <Input
        ref={inputRef}
        placeholder="Rechercher un sport / lieu ..."
        variant="solid"
        value={searchValue}
        onChange={(e) => setSearchValue(e.target.value)}
        onFocus={() => setIsOpen(true)}
        sx={{
          background: theme.palette.mode === 'dark' ? darkGradient : lightGradient,
          color: theme.palette.mode === 'dark' ? 'white' : 'black',
          '& input::placeholder': {
            color: theme.palette.mode === 'dark' ? 'grey.400' : 'grey.500',
          },
        }}
      />

      {isOpen && (searchValue || filteredSports.length > 0 || apiCities.length > 0) && (
        <Portal>
          <Box
            ref={dropdownRef}
            sx={{
              position: 'absolute',
              top: inputRef.current ? inputRef.current.getBoundingClientRect().bottom + window.scrollY : 0,
              left: inputRef.current ? inputRef.current.getBoundingClientRect().left + window.scrollX : 0,
              width: inputRef.current ? inputRef.current.offsetWidth : 300,
              borderRadius: 2,
              overflow: 'hidden',
              boxShadow: 4,
              background: theme.palette.mode === 'dark' ? darkGradient : lightGradient,
              color: theme.palette.mode === 'dark' ? 'white' : 'black',
              zIndex: 10000,
            }}
          >
            {/* Sports */}
            {filteredSports.length > 0 && (
              <Box sx={{ p: 2 }}>
                <Typography level="body-sm" mb={1}>Sports</Typography>
                {filteredSports.map(sport => (
                  <Button
                    key={sport.id}
                    variant="plain"
                    onClick={() => handleSportSelect(sport)}
                    sx={{
                      justifyContent: 'flex-start',
                      width: '100%',
                      color: theme.palette.mode === 'dark' ? 'white' : 'black',
                    }}
                  >
                    <span style={{ marginRight: 8 }}>{sport.icon}</span>
                    {sport.name}
                  </Button>
                ))}
              </Box>
            )}

            {/* Villes */}
            {apiCities.length > 0 && (
              <Box sx={{ p: 2, borderTop: '1px solid', borderColor: 'divider' }}>
                <Typography level="body-sm" mb={1}>Villes {isLoadingCities && '- Recherche...'}</Typography>
                {isLoadingCities ? (
                  <Box sx={{ display: 'flex', justifyContent: 'center', p: 1 }}>
                    <CircularProgress size="sm" />
                  </Box>
                ) : (
                  apiCities.map(city => (
                    <Button
                      key={city.id}
                      variant="plain"
                      onClick={() => handleCitySelect(city)}
                      sx={{
                        justifyContent: 'space-between',
                        width: '100%',
                        color: theme.palette.mode === 'dark' ? 'white' : 'black',
                      }}
                    >
                      <span>{city.name}</span>
                      <span style={{ fontSize: 12, opacity: 0.7 }}>{city.postalCode}</span>
                    </Button>
                  ))
                )}
              </Box>
            )}

            {/* Aucun résultat */}
            {searchValue && filteredSports.length === 0 && apiCities.length === 0 && !isLoadingCities && (
              <Box sx={{ p: 2, textAlign: 'center', opacity: 0.7 }}>
                Aucun résultat pour "{searchValue}"
              </Box>
            )}
          </Box>
        </Portal>
      )}

      {/* Sélections actuelles */}
      <Box sx={{ mt: 2, display: 'flex', flexWrap: 'wrap', gap: 1 }}>
        {selectedSports.map(sport => (
          <Button
            key={sport.id}
            variant="soft"
            color="primary"
            startDecorator={<span>{sport.icon}</span>}
            onClick={() => removeSport(sport.id)}
            sx={{
              background: theme.palette.mode === 'dark' ? darkGradient : lightGradient,
              color: theme.palette.mode === 'dark' ? 'white' : 'black',
            }}
          >
            {sport.name}
          </Button>
        ))}
      </Box>

      {selectedCity && (
        <Box sx={{ mt: 1 }}>
          <Button
            variant="soft"
            color="primary"
            startDecorator={<span>📍</span>}
            onClick={removeCity}
            sx={{
              background: theme.palette.mode === 'dark' ? darkGradient : lightGradient,
              color: theme.palette.mode === 'dark' ? 'white' : 'black',
            }}
          >
            {selectedCity.name} ({selectedCity.postalCode})
          </Button>
        </Box>
      )}
    </Box>
  );
}
