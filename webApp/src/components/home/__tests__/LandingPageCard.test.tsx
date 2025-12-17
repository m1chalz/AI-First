import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { LandingPageCard } from '../LandingPageCard';
import type { Announcement } from '../../../types/announcement';
import type { Coordinates } from '../../../types/location';

const mockAnnouncement: Announcement = {
  id: 'test-123',
  photoUrl: '/images/pet.jpg',
  status: 'MISSING',
  lastSeenDate: '2025-12-15',
  species: 'DOG',
  sex: 'MALE',
  petName: 'Buddy',
  breed: 'Golden Retriever',
  description: 'Friendly dog',
  locationLatitude: 52.2297,
  locationLongitude: 21.0122,
  phone: '+48 123 456 789',
  email: 'owner@example.com',
  microchipNumber: null,
  age: 3,
  reward: null,
  createdAt: '2025-12-15T10:00:00Z',
  updatedAt: null
};

const mockUserCoordinates: Coordinates = { lat: 52.23, lng: 21.01 };

const renderCard = (
  announcement = mockAnnouncement,
  userCoordinates: Coordinates | null = mockUserCoordinates,
  onClick = vi.fn()
) =>
  render(
    <BrowserRouter>
      <LandingPageCard announcement={announcement} userCoordinates={userCoordinates} onClick={onClick} />
    </BrowserRouter>
  );

describe('LandingPageCard', () => {
  it.each([
    {
      description: 'should render pet card with photo, status badge, breed',
      announcement: mockAnnouncement,
      expectations: {
        testId: 'landing.recentPets.petCard.test-123',
        statusText: 'MISSING',
        species: 'Dog',
        breed: 'Golden Retriever',
        hasPhoto: true
      }
    },
    {
      description: 'should render placeholder when photo is null',
      announcement: { ...mockAnnouncement, photoUrl: '' },
      expectations: {
        testId: 'landing.recentPets.petCard.test-123',
        statusText: 'MISSING',
        species: 'Dog',
        breed: 'Golden Retriever',
        hasPhoto: false
      }
    },
    {
      description: 'should render species only when breed is null',
      announcement: { ...mockAnnouncement, breed: null },
      expectations: {
        testId: 'landing.recentPets.petCard.test-123',
        statusText: 'MISSING',
        species: 'Dog',
        breed: null,
        hasPhoto: true
      }
    }
  ])('$description', ({ announcement, expectations }) => {
    // when
    renderCard(announcement);

    // then
    const card = screen.getByTestId(expectations.testId);
    expect(card).toBeDefined();
    expect(screen.getByText(expectations.statusText)).toBeDefined();
    expect(screen.getByText(expectations.species)).toBeDefined();

    if (expectations.breed) {
      expect(screen.getByText(expectations.breed)).toBeDefined();
    }

    if (expectations.hasPhoto) {
      const img = card.querySelector('img');
      expect(img).not.toBeNull();
    }
  });

  it('should display distance when user coordinates are available', () => {
    // when
    renderCard(mockAnnouncement, mockUserCoordinates);

    // then
    const locationText = screen.getByText(/\d+(\.\d+)? (km|m) away/);
    expect(locationText).toBeDefined();
  });

  it('should display coordinates when user coordinates are not available', () => {
    // when
    renderCard(mockAnnouncement, null);

    // then
    const locationText = screen.getByText(/52\.22.*, 21\.01.*/);
    expect(locationText).toBeDefined();
  });

  it('should display "Location unknown" when announcement has no location', () => {
    // given
    const noLocationAnnouncement = { ...mockAnnouncement, locationLatitude: null, locationLongitude: null };

    // when
    renderCard(noLocationAnnouncement, mockUserCoordinates);

    // then
    expect(screen.getByText('Location unknown')).toBeDefined();
  });

  it('should display date in DD/MM/YYYY format', () => {
    // when
    renderCard();

    // then
    const dateText = screen.getByText(/\d{2}\/\d{2}\/\d{4}/);
    expect(dateText).toBeDefined();
  });

  it('should call onClick when card is clicked', () => {
    // given
    const onClick = vi.fn();

    // when
    renderCard(mockAnnouncement, mockUserCoordinates, onClick);
    const card = screen.getByTestId('landing.recentPets.petCard.test-123');
    fireEvent.click(card);

    // then
    expect(onClick).toHaveBeenCalledWith('test-123');
  });

  it('should have correct status badge color for MISSING status', () => {
    // when
    renderCard();

    // then
    const badge = screen.getByText('MISSING');
    expect(badge.getAttribute('style')).toContain('background-color');
  });
});
