import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { describe, it, expect } from 'vitest';
import { NavigationBar } from '../NavigationBar';

const renderWithRouter = (initialRoute = '/') =>
  render(
    <MemoryRouter initialEntries={[initialRoute]}>
      <NavigationBar />
    </MemoryRouter>
  );

describe('NavigationBar', () => {
  describe('rendering', () => {
    it.each([
      { testId: 'navigation.home.link', label: 'Home' },
      { testId: 'navigation.lostPet.link', label: 'Lost Pet' },
      { testId: 'navigation.foundPet.link', label: 'Found Pet' },
      { testId: 'navigation.contact.link', label: 'Contact Us' },
      { testId: 'navigation.account.link', label: 'Account' },
    ])('should render $label navigation item', ({ testId, label }) => {
      // given
      renderWithRouter();

      // when
      const link = screen.getByTestId(testId);

      // then
      expect(link).toBeDefined();
      expect(link.textContent).toContain(label);
    });

    it('should render PetSpot logo', () => {
      // given
      renderWithRouter();

      // when
      const logo = screen.getByAltText('PetSpot');

      // then
      expect(logo).toBeDefined();
    });
  });

  describe('navigation', () => {
    it.each([
      { testId: 'navigation.home.link', expectedPath: '/' },
      { testId: 'navigation.lostPet.link', expectedPath: '/lost-pets' },
      { testId: 'navigation.foundPet.link', expectedPath: '/found-pets' },
      { testId: 'navigation.contact.link', expectedPath: '/contact' },
      { testId: 'navigation.account.link', expectedPath: '/account' },
    ])('should have correct href for $testId', ({ testId, expectedPath }) => {
      // given
      renderWithRouter();

      // when
      const link = screen.getByTestId(testId);

      // then
      expect(link.getAttribute('href')).toBe(expectedPath);
    });

    it('should call navigation when item is clicked', async () => {
      // given
      const user = userEvent.setup();
      renderWithRouter('/');

      // when
      const lostPetLink = screen.getByTestId('navigation.lostPet.link');
      await user.click(lostPetLink);

      // then
      expect(lostPetLink.getAttribute('href')).toBe('/lost-pets');
    });
  });

  describe('active state', () => {
    it.each([
      { route: '/', activeTestId: 'navigation.home.link', activeLabel: 'Home' },
      { route: '/lost-pets', activeTestId: 'navigation.lostPet.link', activeLabel: 'Lost Pet' },
      { route: '/found-pets', activeTestId: 'navigation.foundPet.link', activeLabel: 'Found Pet' },
      { route: '/contact', activeTestId: 'navigation.contact.link', activeLabel: 'Contact Us' },
      { route: '/account', activeTestId: 'navigation.account.link', activeLabel: 'Account' },
    ])('should highlight $activeLabel as active on $route', ({ route, activeTestId }) => {
      // given
      renderWithRouter(route);

      // when
      const activeLink = screen.getByTestId(activeTestId);

      // then
      expect(activeLink.className).toMatch(/Active/);
    });

    it.each([
      { route: '/', inactiveTestIds: ['navigation.lostPet.link', 'navigation.foundPet.link', 'navigation.contact.link', 'navigation.account.link'] },
      { route: '/lost-pets', inactiveTestIds: ['navigation.home.link', 'navigation.foundPet.link', 'navigation.contact.link', 'navigation.account.link'] },
    ])('should not highlight inactive items on $route', ({ route, inactiveTestIds }) => {
      // given
      renderWithRouter(route);

      // then
      inactiveTestIds.forEach((testId) => {
        const link = screen.getByTestId(testId);
        expect(link.className).not.toMatch(/Active/);
      });
    });
  });
});
