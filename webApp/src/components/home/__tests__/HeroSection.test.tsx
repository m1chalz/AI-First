import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { HeroSection } from '../HeroSection';

describe('HeroSection', () => {
  it('should render hero section with heading and description', () => {
    // when
    render(<HeroSection />);

    // then
    expect(screen.getByTestId('landing.heroSection')).toBeDefined();
    expect(screen.getByTestId('landing.hero.heading')).toBeDefined();
    expect(screen.getByText(/Reuniting Lost Pets/)).toBeDefined();
    expect(screen.getByText(/Our portal helps connect/)).toBeDefined();
  });

  it.each([
    { id: 'search', title: 'Search Database' },
    { id: 'report', title: 'Report Missing' },
    { id: 'found', title: 'Found a Pet' },
    { id: 'location', title: 'Location Based' }
  ])('should render feature card "$title"', ({ id, title }) => {
    // when
    render(<HeroSection />);

    // then
    expect(screen.getByTestId(`landing.hero.featureCard.${id}`)).toBeDefined();
    expect(screen.getByText(title)).toBeDefined();
  });

  it('should render 4 feature cards in correct order', () => {
    // when
    render(<HeroSection />);

    // then
    const cards = screen.getAllByTestId(/landing\.hero\.featureCard\./);
    expect(cards).toHaveLength(4);
    expect(cards[0].getAttribute('data-testid')).toBe('landing.hero.featureCard.search');
    expect(cards[1].getAttribute('data-testid')).toBe('landing.hero.featureCard.report');
    expect(cards[2].getAttribute('data-testid')).toBe('landing.hero.featureCard.found');
    expect(cards[3].getAttribute('data-testid')).toBe('landing.hero.featureCard.location');
  });
});
