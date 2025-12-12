import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { Header } from '../Header';

describe('Header', () => {
  it('renders title and progress', () => {
    // given
    const onBack = vi.fn();

    // when
    render(<Header title="Test Title" progress="1/4" onBack={onBack} />);

    // then
    expect(screen.getByTestId('newAnnouncement.header.title')).toBeTruthy();
    expect(screen.getByText('Test Title')).toBeTruthy();
    expect(screen.getByTestId('newAnnouncement.header.progress')).toBeTruthy();
    expect(screen.getByText('1/4')).toBeTruthy();
  });

  it('renders back button', () => {
    // given
    const onBack = vi.fn();

    // when
    render(<Header title="Test Title" progress="1/4" onBack={onBack} />);

    // then
    const backButton = screen.getByTestId('newAnnouncement.header.backButton.click');
    expect(backButton).toBeTruthy();
    expect(backButton.getAttribute('aria-label')).toBe('Go back');
  });

  it('calls onBack when back button clicked', () => {
    // given
    const onBack = vi.fn();
    render(<Header title="Test Title" progress="1/4" onBack={onBack} />);

    // when
    const backButton = screen.getByTestId('newAnnouncement.header.backButton.click');
    fireEvent.click(backButton);

    // then
    expect(onBack).toHaveBeenCalledTimes(1);
  });

  it('renders with different title and progress values', () => {
    // given
    const onBack = vi.fn();

    // when
    render(<Header title="Photo Upload" progress="2/4" onBack={onBack} />);

    // then
    expect(screen.getByText('Photo Upload')).toBeTruthy();
    expect(screen.getByText('2/4')).toBeTruthy();
  });
});
