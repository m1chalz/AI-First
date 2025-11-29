import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { AnimalList } from '../../components/AnimalList/AnimalList';

// Mock the hooks
vi.mock('../../hooks/use-animal-list', () => ({
  useAnimalList: vi.fn(() => ({
    animals: [],
    isLoading: false,
    error: null,
    isEmpty: true,
    reportMissing: vi.fn(),
  }))
}));

vi.mock('../../hooks/use-modal', () => ({
  useModal: vi.fn(() => ({
    isOpen: false,
    selectedPetId: null,
    openModal: vi.fn(),
    closeModal: vi.fn(),
  }))
}));

vi.mock('../../hooks/use-geolocation', () => ({
  useGeolocation: vi.fn(() => ({
    coordinates: null,
    error: null,
    isLoading: false,
  }))
}));

import * as useGeolocationModule from '../../hooks/use-geolocation';

describe('AnimalList - Location Banner Integration', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should show LocationBanner when permission is denied (error code 1)', () => {
    // given
    vi.spyOn(useGeolocationModule, 'useGeolocation').mockReturnValue({
      coordinates: null,
      error: { code: 1, message: 'Permission denied' } as GeolocationPositionError,
      isLoading: false,
    });

    // when
    render(<AnimalList />);

    // then
    screen.getByTestId('petList.locationBanner');
    screen.getByText(/see pets near you/i);
  });

  it('should NOT show LocationBanner when permission is granted', () => {
    // given
    vi.spyOn(useGeolocationModule, 'useGeolocation').mockReturnValue({
      coordinates: { lat: 52.0, lng: 21.0 },
      error: null,
      isLoading: false,
    });

    // when
    render(<AnimalList />);

    // then
    expect(screen.queryByTestId('petList.locationBanner')).toBeNull();
  });

  it('should NOT show LocationBanner for non-permission errors (e.g., timeout)', () => {
    // given
    vi.spyOn(useGeolocationModule, 'useGeolocation').mockReturnValue({
      coordinates: null,
      error: { code: 3, message: 'Timeout' } as GeolocationPositionError,
      isLoading: false,
    });

    // when
    render(<AnimalList />);

    // then
    expect(screen.queryByTestId('petList.locationBanner')).toBeNull();
  });

  it('should hide LocationBanner when close button is clicked', () => {
    // given
    vi.spyOn(useGeolocationModule, 'useGeolocation').mockReturnValue({
      coordinates: null,
      error: { code: 1, message: 'Permission denied' } as GeolocationPositionError,
      isLoading: false,
    });
    render(<AnimalList />);
    const closeButton = screen.getByTestId('petList.locationBanner.close');

    // when
    fireEvent.click(closeButton);

    // then
    expect(screen.queryByTestId('petList.locationBanner')).toBeNull();
  });

  it('should show pets list alongside LocationBanner when permission denied', async () => {
    // given
    const mockAnimals = [
      { id: '1', petName: 'Fluffy', species: 'CAT', breed: 'Maine Coon', locationLatitude: 52.0, locationLongitude: 21.0, sex: 'MALE', status: 'MISSING', lastSeenDate: '2025-11-18', description: 'Test', email: null, phone: null, photoUrl: 'placeholder', age: null, microchipNumber: null, reward: null, createdAt: null, updatedAt: null },
    ];
    
    vi.spyOn(useGeolocationModule, 'useGeolocation').mockReturnValue({
      coordinates: null,
      error: { code: 1, message: 'Permission denied' } as GeolocationPositionError,
      isLoading: false,
    });

    const useAnimalListModule = await import('../../hooks/use-animal-list');
    vi.spyOn(useAnimalListModule, 'useAnimalList').mockReturnValue({
      animals: mockAnimals,
      isLoading: false,
      error: null,
      isEmpty: false,
      loadAnimals: vi.fn(),
      reportMissing: vi.fn(),
      reportFound: vi.fn(),
      selectAnimal: vi.fn(),
    });

    // when
    render(<AnimalList />);

    // then
    screen.getByTestId('petList.locationBanner');
    screen.getByTestId('animalList.list');
  });
});

