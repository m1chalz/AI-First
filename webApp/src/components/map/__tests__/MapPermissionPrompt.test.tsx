import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MapPermissionPrompt } from '../MapPermissionPrompt';

describe('MapPermissionPrompt', () => {
  it('should display location access message with browser settings instruction', () => {
    // when
    render(<MapPermissionPrompt />);

    // then
    expect(screen.getByText('Location Access Required')).toBeDefined();
    expect(screen.getByText(/enable location access.*browser settings/i)).toBeDefined();
    expect(screen.getByTestId('landingPage.map.permissionPrompt')).toBeDefined();
  });
});
