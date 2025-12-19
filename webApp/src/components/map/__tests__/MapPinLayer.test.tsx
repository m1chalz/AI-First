import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MapPinLayer } from '../MapPinLayer';
import type { Announcement } from '../../../types/announcement';

vi.mock('../../../hooks/use-map-pins', () => ({
  useMapPins: vi.fn()
}));

import { useMapPins } from '../../../hooks/use-map-pins';

const mockUseMapPins = vi.mocked(useMapPins);

vi.mock('react-leaflet', () => ({
  Marker: vi.fn(({ children, position, icon }) => (
    <div
      data-testid={`marker-${position[0]}-${position[1]}`}
      data-icon={icon?.options?.className}
    >
      {children}
    </div>
  )),
  Popup: vi.fn(({ children }) => <div data-testid="popup">{children}</div>)
}));

const mockPins: Announcement[] = [
  {
    id: 'pin-1',
    petName: 'Buddy',
    species: 'DOG',
    status: 'MISSING',
    sex: 'MALE',
    locationLatitude: 52.23,
    locationLongitude: 21.01,
    photoUrl: 'http://example.com/buddy.jpg',
    phone: '+48123456789',
    email: 'buddy@example.com',
    lastSeenDate: '2025-01-01',
    description: 'A friendly golden retriever',
    breed: null,
    microchipNumber: null,
    age: null,
    reward: null,
    createdAt: null,
    updatedAt: null
  },
  {
    id: 'pin-2',
    petName: 'Whiskers',
    species: 'CAT',
    status: 'FOUND',
    sex: 'FEMALE',
    locationLatitude: 52.24,
    locationLongitude: 21.02,
    photoUrl: 'http://example.com/whiskers.jpg',
    phone: '+48987654321',
    email: 'whiskers@example.com',
    lastSeenDate: '2025-01-02',
    description: 'Black cat with white paws',
    breed: null,
    microchipNumber: null,
    age: null,
    reward: null,
    createdAt: null,
    updatedAt: null
  }
];

describe('MapPinLayer', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders markers for each pin', () => {
    // given
    mockUseMapPins.mockReturnValue({
      pins: mockPins,
      loading: false,
      error: null
    });
    const userLocation = { lat: 52.2297, lng: 21.0122 };

    // when
    render(<MapPinLayer userLocation={userLocation} />);

    // then
    expect(screen.getByTestId('marker-52.23-21.01')).toBeDefined();
    expect(screen.getByTestId('marker-52.24-21.02')).toBeDefined();
  });

  it('calls useMapPins with userLocation', () => {
    // given
    mockUseMapPins.mockReturnValue({
      pins: [],
      loading: false,
      error: null
    });
    const userLocation = { lat: 52.2297, lng: 21.0122 };

    // when
    render(<MapPinLayer userLocation={userLocation} />);

    // then
    expect(mockUseMapPins).toHaveBeenCalledWith(userLocation);
  });

  it('renders loading overlay when loading is true', () => {
    // given
    mockUseMapPins.mockReturnValue({
      pins: [],
      loading: true,
      error: null
    });

    // when
    render(<MapPinLayer userLocation={{ lat: 52.2297, lng: 21.0122 }} />);

    // then
    expect(screen.getByTestId('landingPage.map.pinsLoading')).toBeDefined();
  });

  it('renders error overlay when error is present', () => {
    // given
    mockUseMapPins.mockReturnValue({
      pins: [],
      loading: false,
      error: new Error('Failed to load pins')
    });

    // when
    render(<MapPinLayer userLocation={{ lat: 52.2297, lng: 21.0122 }} />);

    // then
    expect(screen.getByTestId('landingPage.map.pinsError')).toBeDefined();
  });

  it('renders nothing when userLocation is null', () => {
    // given
    mockUseMapPins.mockReturnValue({
      pins: [],
      loading: false,
      error: null
    });

    // when
    const { container } = render(<MapPinLayer userLocation={null} />);

    // then
    expect(container.firstChild).toBeNull();
  });

  it('renders popup with correct data-testid for each pin', () => {
    // given
    mockUseMapPins.mockReturnValue({
      pins: mockPins,
      loading: false,
      error: null
    });

    // when
    render(<MapPinLayer userLocation={{ lat: 52.2297, lng: 21.0122 }} />);

    // then
    expect(screen.getByTestId('landingPage.map.popup.pin-1')).toBeDefined();
    expect(screen.getByTestId('landingPage.map.popup.pin-2')).toBeDefined();
  });

  describe('popup content', () => {
    it('renders popup with all pet details', () => {
      // given
      mockUseMapPins.mockReturnValue({
        pins: [mockPins[0]],
        loading: false,
        error: null
      });

      // when
      render(<MapPinLayer userLocation={{ lat: 52.2297, lng: 21.0122 }} />);

      // then
      expect(screen.getByTestId('landingPage.map.popup.pin-1')).toBeDefined();
      expect(screen.getByRole('img', { name: 'Buddy' })).toBeDefined();
      expect(screen.getByText('Buddy')).toBeDefined();
      expect(screen.getByText('Dog | Jan 1, 2025')).toBeDefined();
      expect(screen.getByText('MISSING')).toBeDefined();
      expect(screen.getByText('A friendly golden retriever')).toBeDefined();
      expect(screen.getByText('+48123456789 | buddy@example.com')).toBeDefined();
    });

    it('shows Unknown when pet name is null', () => {
      // given
      const pinWithoutName: Announcement = { ...mockPins[0], petName: null };
      mockUseMapPins.mockReturnValue({
        pins: [pinWithoutName],
        loading: false,
        error: null
      });

      // when
      render(<MapPinLayer userLocation={{ lat: 52.2297, lng: 21.0122 }} />);

      // then
      expect(screen.getByText('Unknown')).toBeDefined();
    });

    it('hides description when null', () => {
      // given
      const pinWithoutDescription: Announcement = { ...mockPins[0], description: null };
      mockUseMapPins.mockReturnValue({
        pins: [pinWithoutDescription],
        loading: false,
        error: null
      });

      // when
      render(<MapPinLayer userLocation={{ lat: 52.2297, lng: 21.0122 }} />);

      // then
      expect(screen.queryByTestId('landingPage.map.popup.description')).toBeNull();
    });
  });
});
