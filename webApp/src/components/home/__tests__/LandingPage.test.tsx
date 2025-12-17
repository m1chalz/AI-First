import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { LandingPage } from '../LandingPage';

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
    const footer = screen.getByTestId('landing.footer');

    expect(heroSection).toBeDefined();
    expect(footer).toBeDefined();
    expect(heroSection.compareDocumentPosition(footer)).toBe(Node.DOCUMENT_POSITION_FOLLOWING);

    const cards = screen.getAllByTestId(/landing\.hero\.featureCard\./);
    expect(cards).toHaveLength(4);

    expect(screen.getByTestId('landing.footer.quickLink.reportLost')).toBeDefined();
    expect(screen.getByTestId('landing.footer.quickLink.reportFound')).toBeDefined();
    expect(screen.getByTestId('landing.footer.quickLink.search')).toBeDefined();
  });
});
