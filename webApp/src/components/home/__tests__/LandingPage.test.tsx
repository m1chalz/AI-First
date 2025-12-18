import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { LandingPage } from '../LandingPage';

vi.mock('../../../hooks/use-announcement-list', () => ({
  useAnnouncementList: vi.fn(() => ({
    announcements: [],
    isLoading: false,
    error: null,
    isEmpty: true,
    loadAnnouncements: vi.fn(),
    geolocationError: null
  }))
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

const renderLandingPage = () =>
  render(
    <BrowserRouter>
      <LandingPage />
    </BrowserRouter>
  );

describe('LandingPage', () => {
  it('should render all sections in correct order', () => {
    // when
    renderLandingPage();

    // then
    const heroSection = screen.getByTestId('landing.heroSection');
    const recentPetsSection = screen.getByTestId('landing.recentPetsSection');
    const footer = screen.getByTestId('landing.footer');

    expect(heroSection).toBeDefined();
    expect(recentPetsSection).toBeDefined();
    expect(footer).toBeDefined();

    // Verify order: hero -> recentPets -> footer
    expect(heroSection.compareDocumentPosition(recentPetsSection)).toBe(Node.DOCUMENT_POSITION_FOLLOWING);
    expect(recentPetsSection.compareDocumentPosition(footer)).toBe(Node.DOCUMENT_POSITION_FOLLOWING);

    const cards = screen.getAllByTestId(/landing\.hero\.featureCard\./);
    expect(cards).toHaveLength(4);

    expect(screen.getByTestId('landing.footer.quickLink.reportLost')).toBeDefined();
    expect(screen.getByTestId('landing.footer.quickLink.reportFound')).toBeDefined();
    expect(screen.getByTestId('landing.footer.quickLink.search')).toBeDefined();
  });
});
