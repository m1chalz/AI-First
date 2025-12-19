import { vi } from 'vitest';

const mockDivIcon = vi.fn((options?: { html?: string; className?: string; iconSize?: [number, number]; iconAnchor?: [number, number]; popupAnchor?: [number, number] }) => ({
  options: options || {},
  _setIconStyles: vi.fn(),
  createIcon: vi.fn(() => {
    const div = document.createElement('div');
    if (options?.html) {
      div.innerHTML = options.html;
    }
    if (options?.className) {
      div.className = options.className;
    }
    return div;
  })
}));

const mockMarker = vi.fn((latlng: [number, number], options?: { icon?: unknown }) => ({
  latlng,
  options: options || {},
  addTo: vi.fn(),
  remove: vi.fn(),
  setIcon: vi.fn(),
  setLatLng: vi.fn(),
  bindPopup: vi.fn(),
  openPopup: vi.fn(),
  closePopup: vi.fn(),
  on: vi.fn(),
  off: vi.fn()
}));

const mockMap = vi.fn(() => ({
  setView: vi.fn(),
  addLayer: vi.fn(),
  removeLayer: vi.fn(),
  on: vi.fn(),
  off: vi.fn(),
  getBounds: vi.fn(() => ({
    getNorthEast: () => ({ lat: 52.52, lng: 13.40 }),
    getSouthWest: () => ({ lat: 52.51, lng: 13.38 })
  })),
  getCenter: vi.fn(() => ({ lat: 52.5170, lng: 13.3900 })),
  getZoom: vi.fn(() => 13)
}));

const mockTileLayer = vi.fn(() => ({
  addTo: vi.fn(),
  remove: vi.fn()
}));

globalThis.L = {
  divIcon: mockDivIcon,
  marker: mockMarker,
  map: mockMap,
  tileLayer: mockTileLayer,
  icon: vi.fn(),
  latLng: vi.fn((lat: number, lng: number) => ({ lat, lng })),
  latLngBounds: vi.fn()
} as never;

vi.mock('leaflet', () => ({
  default: globalThis.L
}));

vi.mock('react-leaflet', () => ({
  MapContainer: ({ children }: { children: React.ReactNode }) => <div data-testid="map-container">{children}</div>,
  TileLayer: () => <div data-testid="tile-layer" />,
  Marker: ({ children }: { children?: React.ReactNode }) => <div data-testid="marker">{children}</div>,
  Popup: ({ children }: { children: React.ReactNode }) => <div data-testid="popup">{children}</div>,
  useMap: vi.fn(() => mockMap()),
  useMapEvents: vi.fn()
}));

