import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { Home } from '../Home';
import { Account } from '../Account';
import { Contact } from '../Contact';
import { FoundPets } from '../FoundPets';

describe('Home', () => {
  it('should render welcome message', () => {
    // Given/When
    render(<Home />);

    // Then
    expect(screen.getByText('Welcome to PetSpot')).toBeTruthy();
    expect(screen.getByText('Help reunite lost pets with their families')).toBeTruthy();
  });
});

describe('Account', () => {
  it('should render account heading', () => {
    // Given/When
    render(<Account />);

    // Then
    expect(screen.getByText('Account')).toBeTruthy();
    expect(screen.getByText('Coming soon')).toBeTruthy();
  });
});

describe('Contact', () => {
  it('should render contact heading', () => {
    // Given/When
    render(<Contact />);

    // Then
    expect(screen.getByText('Contact Us')).toBeTruthy();
    expect(screen.getByText('Coming soon')).toBeTruthy();
  });
});

describe('FoundPets', () => {
  it('should render found pets heading', () => {
    // Given/When
    render(<FoundPets />);

    // Then
    expect(screen.getByText('Found Pets')).toBeTruthy();
    expect(screen.getByText('Coming soon')).toBeTruthy();
  });
});
