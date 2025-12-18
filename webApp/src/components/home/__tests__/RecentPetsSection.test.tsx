import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { RecentPetsSection } from '../RecentPetsSection';
import type { Announcement } from '../../../types/announcement';

vi.mock('../../../hooks/use-announcement-list', () => ({
  useAnnouncementList: vi.fn()
}));

vi.mock('../../../contexts/GeolocationContext', () => ({
  useGeolocationContext: vi.fn(() => ({
    state: {
      coordinates: { lat: 52.23, lng: 21.01 },
      error: null,
      isLoading: false,
      permissionCheckCompleted: true
    }
  }))
}));

import { useAnnouncementList } from '../../../hooks/use-announcement-list';

const mockAnnouncements: Announcement[] = [
  {
    id: '1',
    photoUrl: '/images/pet1.jpg',
    status: 'MISSING',
    lastSeenDate: '2025-12-17',
    species: 'DOG',
    sex: 'MALE',
    petName: 'Buddy',
    breed: 'Golden Retriever',
    description: 'Friendly dog',
    locationLatitude: 52.2297,
    locationLongitude: 21.0122,
    phone: '123',
    email: 'mail@mail.com',
    microchipNumber: null,
    age: null,
    reward: null,
    createdAt: '2025-12-17T10:00:00Z',
    updatedAt: null
  },
  {
    id: '2',
    photoUrl: '/images/pet2.jpg',
    status: 'MISSING',
    lastSeenDate: '2025-12-16',
    species: 'CAT',
    sex: 'FEMALE',
    petName: 'Whiskers',
    breed: 'Siamese',
    description: 'Shy cat',
    locationLatitude: 52.23,
    locationLongitude: 21.015,
    phone: '123',
    email: 'mail@mail.com',
    microchipNumber: null,
    age: null,
    reward: null,
    createdAt: '2025-12-16T09:00:00Z',
    updatedAt: null
  },
  {
    id: '3',
    photoUrl: '',
    status: 'FOUND',
    lastSeenDate: '2025-12-15',
    species: 'DOG',
    sex: 'UNKNOWN',
    petName: null,
    breed: null,
    description: null,
    locationLatitude: 1,
    locationLongitude: 1,
    phone: '123',
    email: 'mail@mail.com',
    microchipNumber: null,
    age: null,
    reward: null,
    createdAt: '2025-12-15T08:00:00Z',
    updatedAt: null
  }
];

const renderSection = () =>
  render(
    <BrowserRouter>
      <RecentPetsSection />
    </BrowserRouter>
  );

describe('RecentPetsSection', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should display loading state while fetching data', () => {
    // given
    vi.mocked(useAnnouncementList).mockReturnValue({
      announcements: [],
      isLoading: true,
      error: null,
      isEmpty: false,
      loadAnnouncements: vi.fn(),
      geolocationError: null
    });

    // when
    renderSection();

    // then
    expect(screen.getByTestId('landing.recentPets.loading')).toBeDefined();
  });

  it('should display error state when fetch fails', () => {
    // given
    vi.mocked(useAnnouncementList).mockReturnValue({
      announcements: [],
      isLoading: false,
      error: 'Network error',
      isEmpty: false,
      loadAnnouncements: vi.fn(),
      geolocationError: null
    });

    // when
    renderSection();

    // then
    expect(screen.getByTestId('landing.recentPets.error')).toBeDefined();
    expect(screen.getByText(/Unable to load recent pets/)).toBeDefined();
  });

  it('should display empty state when no MISSING pets', () => {
    // given
    vi.mocked(useAnnouncementList).mockReturnValue({
      announcements: [],
      isLoading: false,
      error: null,
      isEmpty: true,
      loadAnnouncements: vi.fn(),
      geolocationError: null
    });

    // when
    renderSection();

    // then
    expect(screen.getByTestId('landing.recentPets.emptyState')).toBeDefined();
    expect(screen.getByText(/No recent lost pet reports/)).toBeDefined();
  });

  it('should display only MISSING pets filtered from announcements', () => {
    // given
    vi.mocked(useAnnouncementList).mockReturnValue({
      announcements: mockAnnouncements,
      isLoading: false,
      error: null,
      isEmpty: false,
      loadAnnouncements: vi.fn(),
      geolocationError: null
    });

    // when
    renderSection();

    // then
    expect(screen.getByTestId('landing.recentPets.petCard.1')).toBeDefined();
    expect(screen.getByTestId('landing.recentPets.petCard.2')).toBeDefined();
    expect(screen.queryByTestId('landing.recentPets.petCard.3')).toBeNull(); // FOUND status
  });

  it('should display max 5 pets sorted by createdAt descending', () => {
    // given
    const manyPets: Announcement[] = Array.from({ length: 10 }, (_, i) => ({
      ...mockAnnouncements[0],
      id: `pet-${i}`,
      createdAt: `2025-12-${String(17 - i).padStart(2, '0')}T10:00:00Z`
    }));

    vi.mocked(useAnnouncementList).mockReturnValue({
      announcements: manyPets,
      isLoading: false,
      error: null,
      isEmpty: false,
      loadAnnouncements: vi.fn(),
      geolocationError: null
    });

    // when
    renderSection();

    // then
    const cards = screen.getAllByTestId(/landing\.recentPets\.petCard\./);
    expect(cards).toHaveLength(5);
    expect(screen.getByTestId('landing.recentPets.petCard.pet-0')).toBeDefined(); // Most recent
  });

  it('should render section with heading and View all link', () => {
    // given
    vi.mocked(useAnnouncementList).mockReturnValue({
      announcements: mockAnnouncements,
      isLoading: false,
      error: null,
      isEmpty: false,
      loadAnnouncements: vi.fn(),
      geolocationError: null
    });

    // when
    renderSection();

    // then
    expect(screen.getByTestId('landing.recentPetsSection')).toBeDefined();
    expect(screen.getByTestId('landing.recentPets.heading')).toBeDefined();
    expect(screen.getByTestId('landing.recentPets.viewAllLink.click')).toBeDefined();
    expect(screen.getByText(/View all/)).toBeDefined();
  });

  it('should have View all link pointing to lost-pets page', () => {
    // given
    vi.mocked(useAnnouncementList).mockReturnValue({
      announcements: mockAnnouncements,
      isLoading: false,
      error: null,
      isEmpty: false,
      loadAnnouncements: vi.fn(),
      geolocationError: null
    });

    // when
    renderSection();

    // then
    const link = screen.getByTestId('landing.recentPets.viewAllLink.click');
    expect(link.getAttribute('href')).toBe('/lost-pets');
  });
});
