import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { Home } from '../Home';
import { Account } from '../Account';
import { Contact } from '../Contact';
import { FoundPets } from '../FoundPets';

vi.mock('../../hooks/use-announcement-list', () => ({
  useAnnouncementList: vi.fn(() => ({
    announcements: [],
    isLoading: false,
    error: null,
    isEmpty: true,
    loadAnnouncements: vi.fn(),
    geolocationError: null
  }))
}));

vi.mock('../../contexts/GeolocationContext', () => ({
  useGeolocationContext: vi.fn(() => ({
    state: {
      coordinates: { lat: 52.23, lng: 21.01 },
      error: null,
      isLoading: false,
      permissionCheckCompleted: true
    }
  }))
}));

describe('Home', () => {
  it('should render landing page with hero section and recent pets section', () => {
    // when
    render(
      <BrowserRouter>
        <Home />
      </BrowserRouter>
    );

    // then
    expect(screen.getByTestId('landing.heroSection')).toBeDefined();
    expect(screen.getByTestId('landing.recentPetsSection')).toBeDefined();
    expect(screen.getByTestId('landing.footer')).toBeDefined();
  });
});

describe('Account', () => {
  it('should render account heading', () => {
    // Given/When
    render(<Account />);

    // Then
    expect(screen.getByText('Account')).toBeTruthy();
    expect(screen.getByText('Coming soon')).toBeTruthy();
  });
});

describe('Contact', () => {
  it('should render contact heading', () => {
    // Given/When
    render(<Contact />);

    // Then
    expect(screen.getByText('Contact Us')).toBeTruthy();
    expect(screen.getByText('Coming soon')).toBeTruthy();
  });
});

describe('FoundPets', () => {
  it('should render found pets heading', () => {
    // Given/When
    render(<FoundPets />);

    // Then
    expect(screen.getByText('Found Pets')).toBeTruthy();
    expect(screen.getByText('Coming soon')).toBeTruthy();
  });
});
