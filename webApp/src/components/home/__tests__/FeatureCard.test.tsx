import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MdSearch } from 'react-icons/md';
import { FeatureCard } from '../FeatureCard';

describe('FeatureCard', () => {
  it('should render card with icon, title, and description', () => {
    // given
    const props = {
      id: 'search',
      icon: MdSearch,
      iconColor: '#3B82F6',
      title: 'Search Database',
      description: 'Browse through our database of lost and found pets in your area'
    };

    // when
    render(<FeatureCard {...props} />);

    // then
    const card = screen.getByTestId('landing.hero.featureCard.search');
    expect(card).toBeDefined();

    const iconContainer = card.querySelector('[class*="iconContainer"]');
    expect(iconContainer).not.toBeNull();
    expect(iconContainer?.getAttribute('style')).toContain('background-color: rgb(59, 130, 246)');
    expect(card.querySelector('svg')).not.toBeNull();

    expect(screen.getByText('Search Database')).toBeDefined();
    expect(screen.getByText('Browse through our database of lost and found pets in your area')).toBeDefined();
  });
});
