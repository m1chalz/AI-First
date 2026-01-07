import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MapPinLayer } from '../MapPinLayer';
import type { Announcement } from '../../../types/announcement';

vi.mock('leaflet', () => ({
  default: {
    divIcon: vi.fn(() => ({ options: { className: 'pet-pin-marker' } }))
  }
}));

vi.mock('../../../hooks/use-announcement-list', () => ({
  useAnnouncementList: vi.fn()
}));

vi.mock('react-leaflet', () => ({
  Marker: vi.fn(({ children, position, icon }) => (
    <div data-testid={`marker-${position[0]}-${position[1]}`} data-icon={icon?.options?.className}>
      {children}
    </div>
  )),
  Popup: vi.fn(({ children }) => <div data-testid="popup">{children}</div>)
}));

import { useAnnouncementList } from '../../../hooks/use-announcement-list';

const mockUseAnnouncementList = vi.mocked(useAnnouncementList);

const createAnnouncement = (overrides = {}): Announcement => ({
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
  updatedAt: null,
  ...overrides
});

const mockAnnouncements: Announcement[] = [
  createAnnouncement(),
  createAnnouncement({
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
    description: 'Black cat with white paws'
  })
];

describe('MapPinLayer', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders markers for each announcement', () => {
    // given
    mockUseAnnouncementList.mockReturnValue({
      announcements: mockAnnouncements,
      isLoading: false,
      error: null,
      isEmpty: false,
      loadAnnouncements: vi.fn(),
      geolocationError: null
    });

    // when
    render(<MapPinLayer />);

    // then
    expect(screen.getByTestId('marker-52.23-21.01')).toBeDefined();
    expect(screen.getByTestId('marker-52.24-21.02')).toBeDefined();
  });

  it('renders loading overlay when isLoading is true', () => {
    // given
    mockUseAnnouncementList.mockReturnValue({
      announcements: [],
      isLoading: true,
      error: null,
      isEmpty: false,
      loadAnnouncements: vi.fn(),
      geolocationError: null
    });

    // when
    render(<MapPinLayer />);

    // then
    expect(screen.getByTestId('landingPage.map.pinsLoading')).toBeDefined();
  });

  it('renders error overlay when error is present', () => {
    // given
    mockUseAnnouncementList.mockReturnValue({
      announcements: [],
      isLoading: false,
      error: 'Failed to load',
      isEmpty: false,
      loadAnnouncements: vi.fn(),
      geolocationError: null
    });

    // when
    render(<MapPinLayer />);

    // then
    expect(screen.getByTestId('landingPage.map.pinsError')).toBeDefined();
  });

  it('renders popup with correct data-testid for each pin', () => {
    // given
    mockUseAnnouncementList.mockReturnValue({
      announcements: mockAnnouncements,
      isLoading: false,
      error: null,
      isEmpty: false,
      loadAnnouncements: vi.fn(),
      geolocationError: null
    });

    // when
    render(<MapPinLayer />);

    // then
    expect(screen.getByTestId('landingPage.map.popup.pin-1')).toBeDefined();
    expect(screen.getByTestId('landingPage.map.popup.pin-2')).toBeDefined();
  });

  it('filters out CLOSED announcements', () => {
    // given
    mockUseAnnouncementList.mockReturnValue({
      announcements: [
        createAnnouncement({ id: '1', status: 'MISSING' }),
        createAnnouncement({ id: '2', status: 'CLOSED', locationLatitude: 52.25, locationLongitude: 21.03 })
      ],
      isLoading: false,
      error: null,
      isEmpty: false,
      loadAnnouncements: vi.fn(),
      geolocationError: null
    });

    // when
    render(<MapPinLayer />);

    // then
    expect(screen.getByTestId('landingPage.map.popup.1')).toBeDefined();
    expect(screen.queryByTestId('landingPage.map.popup.2')).toBeNull();
  });

  describe('popup content', () => {
    it('renders popup with pet name, species, date, and status', () => {
      // given
      mockUseAnnouncementList.mockReturnValue({
        announcements: [mockAnnouncements[0]],
        isLoading: false,
        error: null,
        isEmpty: false,
        loadAnnouncements: vi.fn(),
        geolocationError: null
      });

      // when
      render(<MapPinLayer />);

      // then
      expect(screen.getByTestId('landingPage.map.popup.pin-1')).toBeDefined();
      expect(screen.getByRole('img', { name: 'Buddy' })).toBeDefined();
      expect(screen.getByText('Buddy')).toBeDefined();
      expect(screen.getByText('Dog')).toBeDefined();
      expect(screen.getByText('Jan 1, 2025')).toBeDefined();
      expect(screen.getByText('MISSING')).toBeDefined();
    });

    it('shows species with breed when breed is provided', () => {
      // given
      mockUseAnnouncementList.mockReturnValue({
        announcements: [createAnnouncement({ breed: 'Golden Retriever' })],
        isLoading: false,
        error: null,
        isEmpty: false,
        loadAnnouncements: vi.fn(),
        geolocationError: null
      });

      // when
      render(<MapPinLayer />);

      // then
      expect(screen.getByText('Dog • Golden Retriever')).toBeDefined();
    });

    it('shows Unknown when pet name is null', () => {
      // given
      mockUseAnnouncementList.mockReturnValue({
        announcements: [createAnnouncement({ petName: null })],
        isLoading: false,
        error: null,
        isEmpty: false,
        loadAnnouncements: vi.fn(),
        geolocationError: null
      });

      // when
      render(<MapPinLayer />);

      // then
      expect(screen.getByText('Unknown')).toBeDefined();
    });
  });
});
