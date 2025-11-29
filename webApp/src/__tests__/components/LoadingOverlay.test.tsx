import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { LoadingOverlay } from '../../components/LoadingOverlay/LoadingOverlay';

describe('LoadingOverlay', () => {
  it('should render spinner', () => {
    // given
    // when
    const { container } = render(<LoadingOverlay />);

    // then
    expect(screen.getByTestId('petList.loading.spinner')).toBeTruthy();
    expect(container.querySelector('.loading-overlay')).toBeTruthy();
  });

  it('should display optional message', () => {
    // given
    const message = 'Loading pets...';

    // when
    render(<LoadingOverlay message={message} />);

    // then
    expect(screen.getByText(message)).toBeTruthy();
  });
});

