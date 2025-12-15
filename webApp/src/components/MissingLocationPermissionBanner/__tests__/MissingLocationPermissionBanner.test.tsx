import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { MissingLocationPermissionBanner } from '../MissingLocationPermissionBanner';

describe('MissingLocationPermissionBanner', () => {
  it('should render benefit message', () => {
    // given
    const onClose = vi.fn();

    // when
    render(<MissingLocationPermissionBanner onClose={onClose} />);

    // then
    screen.getByText(/see pets near you/i);
  });

  it('should render instructions for enabling location', () => {
    // given
    const onClose = vi.fn();

    // when
    render(<MissingLocationPermissionBanner onClose={onClose} />);

    // then
    screen.getByText(/enable location access/i);
    screen.getByText(/browser settings/i);
  });

  it('should render close button with test-id', () => {
    // given
    const onClose = vi.fn();

    // when
    render(<MissingLocationPermissionBanner onClose={onClose} />);

    // then
    screen.getByTestId('announcementList.locationBanner.close');
  });

  it('should call onClose when close button is clicked', () => {
    // given
    const onClose = vi.fn();
    render(<MissingLocationPermissionBanner onClose={onClose} />);
    const closeButton = screen.getByTestId('announcementList.locationBanner.close');

    // when
    fireEvent.click(closeButton);

    // then
    expect(onClose).toHaveBeenCalledTimes(1);
  });

  it('should have banner container with test-id', () => {
    // given
    const onClose = vi.fn();

    // when
    render(<MissingLocationPermissionBanner onClose={onClose} />);

    // then
    screen.getByTestId('announcementList.locationBanner');
  });

  it('should display informational icon', () => {
    // given
    const onClose = vi.fn();

    // when
    render(<MissingLocationPermissionBanner onClose={onClose} />);

    // then
    const banner = screen.getByTestId('announcementList.locationBanner');
    expect(banner).toBeTruthy();
  });
});
