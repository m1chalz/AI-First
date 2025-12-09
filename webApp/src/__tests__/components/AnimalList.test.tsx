import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { AnimalList } from '../../components/AnimalList/AnimalList';
import * as useAnimalListModule from '../../hooks/use-animal-list';
import { Animal } from '../../types/animal';

// Mock react-router-dom
vi.mock('react-router-dom', () => ({
  useNavigate: vi.fn(() => vi.fn())
}));

// Mock the hooks
vi.mock('../../hooks/use-animal-list', () => ({
  useAnimalList: vi.fn(() => ({
    animals: [],
    isLoading: false,
    error: null,
    isEmpty: true,
    loadAnimals: vi.fn(),
    geolocationError: null
  }))
}));

vi.mock('../../hooks/use-modal', () => ({
  useModal: vi.fn(() => ({
    isOpen: false,
    selectedPetId: null,
    openModal: vi.fn(),
    closeModal: vi.fn()
  }))
}));

describe('AnimalList - Location Banner Integration', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should show LocationBanner when permission is denied (error code 1)', () => {
    // given
    vi.spyOn(useAnimalListModule, 'useAnimalList').mockReturnValue({
      animals: [],
      isLoading: false,
      error: null,
      isEmpty: true,
      loadAnimals: vi.fn(),
      geolocationError: { code: 1, message: 'Permission denied' } as GeolocationPositionError
    });

    // when
    render(<AnimalList />);

    // then
    screen.getByTestId('petList.locationBanner');
    screen.getByText(/see pets near you/i);
  });

  it('should NOT show LocationBanner when permission is granted', () => {
    // given
    vi.spyOn(useAnimalListModule, 'useAnimalList').mockReturnValue({
      animals: [],
      isLoading: false,
      error: null,
      isEmpty: true,
      loadAnimals: vi.fn(),
      geolocationError: null
    });

    // when
    render(<AnimalList />);

    // then
    expect(screen.queryByTestId('petList.locationBanner')).toBeNull();
  });

  it('should NOT show LocationBanner for non-permission errors (e.g., timeout)', () => {
    // given
    vi.spyOn(useAnimalListModule, 'useAnimalList').mockReturnValue({
      animals: [],
      isLoading: false,
      error: null,
      isEmpty: true,
      loadAnimals: vi.fn(),
      geolocationError: { code: 3, message: 'Timeout' } as GeolocationPositionError
    });

    // when
    render(<AnimalList />);

    // then
    expect(screen.queryByTestId('petList.locationBanner')).toBeNull();
  });

  it('should hide LocationBanner when close button is clicked', () => {
    // given
    vi.spyOn(useAnimalListModule, 'useAnimalList').mockReturnValue({
      animals: [],
      isLoading: false,
      error: null,
      isEmpty: true,
      loadAnimals: vi.fn(),
      geolocationError: { code: 1, message: 'Permission denied' } as GeolocationPositionError
    });
    render(<AnimalList />);
    const closeButton = screen.getByTestId('petList.locationBanner.close');

    // when
    fireEvent.click(closeButton);

    // then
    expect(screen.queryByTestId('petList.locationBanner')).toBeNull();
  });

  it('should show pets list alongside LocationBanner when permission denied', () => {
    // given
    const mockAnimals: Animal[] = [
      {
        id: '1',
        petName: 'Fluffy',
        species: 'CAT',
        breed: 'Maine Coon',
        locationLatitude: 52.0,
        locationLongitude: 21.0,
        sex: 'MALE',
        status: 'MISSING',
        lastSeenDate: '2025-11-18',
        description: 'Test',
        email: null,
        phone: null,
        photoUrl: 'placeholder',
        age: null,
        microchipNumber: null,
        reward: null,
        createdAt: null,
        updatedAt: null
      }
    ];

    vi.spyOn(useAnimalListModule, 'useAnimalList').mockReturnValue({
      animals: mockAnimals,
      isLoading: false,
      error: null,
      isEmpty: false,
      loadAnimals: vi.fn(),
      geolocationError: { code: 1, message: 'Permission denied' } as GeolocationPositionError
    });

    // when
    render(<AnimalList />);

    // then
    screen.getByTestId('petList.locationBanner');
    screen.getByTestId('animalList.list');
  });
});
