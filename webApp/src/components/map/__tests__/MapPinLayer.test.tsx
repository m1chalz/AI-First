import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MapPinLayer } from '../MapPinLayer';
import type { MapPin } from '../../../hooks/use-map-pins';

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

const mockPins: MapPin[] = [
  {
    id: 'pin-1',
    name: 'Buddy',
    species: 'DOG',
    status: 'MISSING',
    latitude: 52.23,
    longitude: 21.01,
    photoUrl: 'http://example.com/buddy.jpg',
    phoneNumber: '+48123456789',
    email: 'buddy@example.com',
    createdAt: '2025-01-01T10:00:00Z'
  },
  {
    id: 'pin-2',
    name: 'Whiskers',
    species: 'CAT',
    status: 'FOUND',
    latitude: 52.24,
    longitude: 21.02,
    photoUrl: 'http://example.com/whiskers.jpg',
    phoneNumber: '+48987654321',
    email: 'whiskers@example.com',
    createdAt: '2025-01-02T10:00:00Z'
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

  it('renders markers with correct data-testid for each pin', () => {
    // given
    mockUseMapPins.mockReturnValue({
      pins: mockPins,
      loading: false,
      error: null
    });

    // when
    render(<MapPinLayer userLocation={{ lat: 52.2297, lng: 21.0122 }} />);

    // then
    expect(screen.getByTestId('landingPage.map.pin.pin-1')).toBeDefined();
    expect(screen.getByTestId('landingPage.map.pin.pin-2')).toBeDefined();
  });
});
