import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { CharacterCounter } from '../CharacterCounter';

describe('CharacterCounter', () => {
  it('should display current/max format', () => {
    render(<CharacterCounter current={250} max={500} isExceeded={false} />);
    
    expect(screen.getByText('250/500 characters')).toBeDefined();
  });

  it.each([
    [0, 500, '0/500 characters'],
    [250, 500, '250/500 characters'],
    [500, 500, '500/500 characters'],
    [501, 500, '501/500 characters'],
  ])('should display %d/%d as "%s"', (current, max, expected) => {
    render(<CharacterCounter current={current} max={max} isExceeded={current > max} />);
    
    expect(screen.getByText(expected)).toBeDefined();
  });

  it.each([
    ['apply', 'over limit', 501, 500, true, true],
    ['not apply', 'within limit', 250, 500, false, false],
    ['not apply', 'exactly at limit', 500, 500, false, false],
  ])('should %s exceeded class when %s', (_action, _scenario, current, max, isExceeded, shouldContainClass) => {
    const { container } = render(<CharacterCounter current={current} max={max} isExceeded={isExceeded} />);
    
    const counter = container.querySelector('[data-testid="character-counter"]');
    if (shouldContainClass) {
      expect(counter?.className).toContain('exceeded');
    } else {
      expect(counter?.className).not.toContain('exceeded');
    }
  });
});

