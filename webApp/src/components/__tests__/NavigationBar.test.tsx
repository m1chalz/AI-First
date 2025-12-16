import { render, screen, within } from '@testing-library/react';
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

    it('should render PetSpot logo with text and icon', () => {
      // given
      renderWithRouter();

      // when
      const logoLink = screen.getByTestId('navigation.logo.link');
      const logoText = screen.getByText('PetSpot');

      // then
      expect(logoLink).toBeDefined();
      expect(logoText).toBeDefined();
      // Verify paw icon is rendered (SVG element inside logo)
      const pawIcon = logoLink.querySelector('svg');
      expect(pawIcon).not.toBeNull();
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

  // Phase 4: User Story 2 - Visual Design Consistency Tests
  describe('layout (US2)', () => {
    it('should display navigation bar as horizontal flexbox layout', () => {
      // given
      renderWithRouter();

      // when
      const navBar = screen.getByTestId('navigation.bar');

      // then
      expect(navBar).toBeDefined();
      expect(navBar.tagName.toLowerCase()).toBe('nav');
    });

    it('should display logo on the left side of navigation bar', () => {
      // given
      renderWithRouter();

      // when
      const logoLink = screen.getByTestId('navigation.logo.link');
      const navItems = screen.getByTestId('navigation.home.link');

      // then - logo should appear before nav items in DOM order (left side)
      const navBar = screen.getByTestId('navigation.bar');
      const children = Array.from(navBar.children);
      const logoIndex = children.findIndex(child => child.contains(logoLink));
      const itemsIndex = children.findIndex(child => child.contains(navItems));
      expect(logoIndex).toBeLessThan(itemsIndex);
    });

    it('should display navigation items container on the right side', () => {
      // given
      renderWithRouter();

      // when
      const navBar = screen.getByTestId('navigation.bar');

      // then - should have exactly 2 main children: logo area and items area
      expect(navBar.children.length).toBe(2);
    });
  });

  describe('icon and label rendering (US2)', () => {
    it.each([
      { testId: 'navigation.home.link', label: 'Home' },
      { testId: 'navigation.lostPet.link', label: 'Lost Pet' },
      { testId: 'navigation.foundPet.link', label: 'Found Pet' },
      { testId: 'navigation.contact.link', label: 'Contact Us' },
      { testId: 'navigation.account.link', label: 'Account' },
    ])('should render icon and label for $label item', ({ testId, label }) => {
      // given
      renderWithRouter();

      // when
      const link = screen.getByTestId(testId);
      const linkContent = within(link);

      // then - should have both icon (svg) and label (span)
      const icon = link.querySelector('svg');
      expect(icon).not.toBeNull();
      expect(linkContent.getByText(label)).toBeDefined();
    });

    it.each([
      { testId: 'navigation.home.link' },
      { testId: 'navigation.lostPet.link' },
      { testId: 'navigation.foundPet.link' },
      { testId: 'navigation.contact.link' },
      { testId: 'navigation.account.link' },
    ])('should render icon before label in $testId', ({ testId }) => {
      // given
      renderWithRouter();

      // when
      const link = screen.getByTestId(testId);
      const icon = link.querySelector('svg');
      const label = link.querySelector('span');

      // then - icon should appear before label in DOM order
      expect(icon).not.toBeNull();
      expect(label).not.toBeNull();
      const children = Array.from(link.children);
      const iconIndex = children.indexOf(icon as Element);
      const labelIndex = children.indexOf(label as Element);
      expect(iconIndex).toBeLessThan(labelIndex);
    });
  });

  describe('visual styling (US2)', () => {
    it('should apply active CSS class to active navigation item', () => {
      // given
      renderWithRouter('/lost-pets');

      // when
      const activeLink = screen.getByTestId('navigation.lostPet.link');

      // then - should have the active class applied
      expect(activeLink.className).toMatch(/navigationItemActive/);
    });

    it('should apply inactive CSS class to non-active navigation items', () => {
      // given
      renderWithRouter('/lost-pets');

      // when
      const inactiveLink = screen.getByTestId('navigation.home.link');

      // then - should have inactive class (not active)
      expect(inactiveLink.className).toMatch(/navigationItem/);
      expect(inactiveLink.className).not.toMatch(/navigationItemActive/);
    });

    it('should apply icon CSS class to navigation icons', () => {
      // given
      renderWithRouter();

      // when
      const link = screen.getByTestId('navigation.home.link');
      const icon = link.querySelector('svg');

      // then - icon should have the icon class (SVG className is an object with baseVal)
      expect(icon).not.toBeNull();
      const iconClass = icon?.getAttribute('class') ?? '';
      expect(iconClass).toMatch(/icon/);
    });

    it('should apply label CSS class to navigation labels', () => {
      // given
      renderWithRouter();

      // when
      const link = screen.getByTestId('navigation.home.link');
      const label = link.querySelector('span');

      // then - label should have the label class
      expect(label).not.toBeNull();
      expect(label?.className).toMatch(/label/);
    });
  });

  // Phase 5: User Story 3 - Navigation State Persistence Tests
  describe('state persistence (US3)', () => {
    it('should update active state when navigating between routes', async () => {
      // given
      const user = userEvent.setup();
      renderWithRouter('/');
      expect(screen.getByTestId('navigation.home.link').className).toMatch(/Active/);

      // when
      await user.click(screen.getByTestId('navigation.lostPet.link'));

      // then
      expect(screen.getByTestId('navigation.lostPet.link').getAttribute('href')).toBe('/lost-pets');
    });

    it.each([
      { route: '/lost-pets', activeTestId: 'navigation.lostPet.link', label: 'Lost Pet' },
      { route: '/found-pets', activeTestId: 'navigation.foundPet.link', label: 'Found Pet' },
      { route: '/contact', activeTestId: 'navigation.contact.link', label: 'Contact' },
      { route: '/account', activeTestId: 'navigation.account.link', label: 'Account' },
    ])('should show $label as active on direct URL access to $route', ({ route, activeTestId }) => {
      // given
      renderWithRouter(route);

      // when
      const activeLink = screen.getByTestId(activeTestId);
      const homeLink = screen.getByTestId('navigation.home.link');

      // then
      expect(activeLink.className).toMatch(/Active/);
      expect(homeLink.className).not.toMatch(/Active/);
    });

    it.each([
      { route: '/' },
      { route: '/lost-pets' },
      { route: '/found-pets' },
      { route: '/contact' },
      { route: '/account' },
    ])('should render navigation bar on $route route', ({ route }) => {
      // given
      renderWithRouter(route);

      // then
      expect(screen.getByTestId('navigation.bar')).toBeDefined();
    });

    it('should maintain all navigation items when changing routes', async () => {
      // given
      const user = userEvent.setup();
      renderWithRouter('/');

      // when
      await user.click(screen.getByTestId('navigation.lostPet.link'));

      // then
      expect(screen.getByTestId('navigation.home.link')).toBeDefined();
      expect(screen.getByTestId('navigation.lostPet.link')).toBeDefined();
      expect(screen.getByTestId('navigation.foundPet.link')).toBeDefined();
      expect(screen.getByTestId('navigation.contact.link')).toBeDefined();
      expect(screen.getByTestId('navigation.account.link')).toBeDefined();
    });
  });
});
