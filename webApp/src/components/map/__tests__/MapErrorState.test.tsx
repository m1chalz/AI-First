import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MapErrorState } from '../MapErrorState';
import type { MapError } from '../../../hooks/use-map-state';

describe('MapErrorState', () => {
  it('should display error message and have correct testid', () => {
    // given
    const error: MapError = {
      type: 'MAP_LOAD_FAILED',
      message: 'Failed to load map. Please refresh the page to try again.',
      showFallbackMap: false
    };

    // when
    render(<MapErrorState error={error} />);

    // then
    expect(screen.getByText(error.message)).toBeDefined();
    expect(screen.getByTestId('landingPage.map.error')).toBeDefined();
  });
});
