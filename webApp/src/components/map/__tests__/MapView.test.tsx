import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MapView } from '../MapView';

vi.mock('../../../hooks/use-map-state', () => ({
  useMapState: vi.fn()
}));

import { useMapState } from '../../../hooks/use-map-state';

const mockUseMapState = vi.mocked(useMapState);

vi.mock('react-leaflet', () => ({
  MapContainer: vi.fn(({ children, center, zoom, 'data-testid': testId }) => (
    <div data-testid={testId} data-center={JSON.stringify(center)} data-zoom={zoom}>
      {children}
    </div>
  )),
  TileLayer: vi.fn(() => <div data-testid="tile-layer" />)
}));

describe('MapView', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('when rendering with valid state', () => {
    it('should render section header with title and subtitle', () => {
      // given
      mockUseMapState.mockReturnValue({
        center: { lat: 52.2297, lng: 21.0122 },
        zoom: 13,
        isLoading: false,
        error: null,
        showPermissionPrompt: false,
        handleRequestPermission: vi.fn(),
        handleMapLoadError: vi.fn()
      });

      // when
      render(<MapView />);

      // then
      expect(screen.getByText('Pet Locations Map')).toBeDefined();
      expect(screen.getByText('Red markers indicate missing pets, blue markers indicate found pets')).toBeDefined();
    });

    it('should render MapContainer with correct center and zoom', () => {
      // given
      const center = { lat: 52.2297, lng: 21.0122 };
      const zoom = 13;
      mockUseMapState.mockReturnValue({
        center,
        zoom,
        isLoading: false,
        error: null,
        showPermissionPrompt: false,
        handleRequestPermission: vi.fn(),
        handleMapLoadError: vi.fn()
      });

      // when
      render(<MapView />);

      // then
      const mapContainer = screen.getByTestId('landingPage.map');
      expect(mapContainer).toBeDefined();
      expect(mapContainer.getAttribute('data-center')).toBe(JSON.stringify([center.lat, center.lng]));
      expect(mapContainer.getAttribute('data-zoom')).toBe(String(zoom));
    });
  });

  describe('when loading', () => {
    it('should display loading message when isLoading is true', () => {
      // given
      mockUseMapState.mockReturnValue({
        center: { lat: 0, lng: 0 },
        zoom: 13,
        isLoading: true,
        error: null,
        showPermissionPrompt: false,
        handleRequestPermission: vi.fn(),
        handleMapLoadError: vi.fn()
      });

      // when
      render(<MapView />);

      // then
      expect(screen.getByTestId('landingPage.map.loading')).toBeDefined();
    });
  });

  describe('when error occurs', () => {
    it('should display error banner when error has showFallbackMap true', () => {
      // given
      mockUseMapState.mockReturnValue({
        center: { lat: 51.1079, lng: 17.0385 },
        zoom: 13,
        isLoading: false,
        error: {
          type: 'LOCATION_UNAVAILABLE',
          message: 'Unable to get your location.',
          showFallbackMap: true
        },
        showPermissionPrompt: false,
        handleRequestPermission: vi.fn(),
        handleMapLoadError: vi.fn()
      });

      // when
      render(<MapView />);

      // then
      expect(screen.getByTestId('landingPage.map.errorBanner')).toBeDefined();
      expect(screen.getByTestId('landingPage.map')).toBeDefined();
    });
  });

  describe('when permission prompt should show', () => {
    it('should render MapPermissionPrompt when showPermissionPrompt is true', () => {
      // given
      mockUseMapState.mockReturnValue({
        center: { lat: 0, lng: 0 },
        zoom: 13,
        isLoading: false,
        error: null,
        showPermissionPrompt: true,
        handleRequestPermission: vi.fn(),
        handleMapLoadError: vi.fn()
      });

      // when
      render(<MapView />);

      // then
      expect(screen.getByTestId('landingPage.map.permissionPrompt')).toBeDefined();
      expect(screen.getByText('Location Access Required')).toBeDefined();
    });
  });
});
