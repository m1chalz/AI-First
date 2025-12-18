import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { Footer } from '../Footer';

const renderFooter = () =>
  render(
    <BrowserRouter>
      <Footer />
    </BrowserRouter>
  );

describe('Footer', () => {
  it('should render footer with logo and tagline', () => {
    // when
    renderFooter();

    // then
    expect(screen.getByTestId('landing.footer')).toBeDefined();
    const logo = screen.getByTestId('landing.footer.logo');
    expect(logo).toBeDefined();
    expect(logo.textContent).toContain('PetSpot');
    expect(screen.getByText(/Reuniting pets with their families/)).toBeDefined();
  });

  it.each([
    { id: 'reportLost', label: 'Report Lost Pet' },
    { id: 'reportFound', label: 'Report Found Pet' },
    { id: 'search', label: 'Search Database' }
  ])('should render quick link "$label"', ({ id, label }) => {
    // when
    renderFooter();

    // then
    expect(screen.getByTestId(`landing.footer.quickLink.${id}`)).toBeDefined();
    expect(screen.getByText(label)).toBeDefined();
  });

  it('should render contact info with icons', () => {
    // when
    renderFooter();

    // then
    const emailElement = screen.getByTestId('landing.footer.contact.email');
    const phoneElement = screen.getByTestId('landing.footer.contact.phone');
    const addressElement = screen.getByTestId('landing.footer.contact.address');

    expect(emailElement.querySelector('svg')).not.toBeNull();
    expect(phoneElement.querySelector('svg')).not.toBeNull();
    expect(addressElement.querySelector('svg')).not.toBeNull();
  });

  it('should render copyright and legal links', () => {
    // when
    renderFooter();

    // then
    expect(screen.getByTestId('landing.footer.copyright')).toBeDefined();
    expect(screen.getByText(/© 2025 PetSpot/)).toBeDefined();
    expect(screen.getByTestId('landing.footer.legalLink.privacy')).toBeDefined();
    expect(screen.getByTestId('landing.footer.legalLink.terms')).toBeDefined();
    expect(screen.getByTestId('landing.footer.legalLink.cookies')).toBeDefined();
  });
});
