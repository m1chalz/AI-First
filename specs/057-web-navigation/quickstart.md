# Quickstart Guide: Web App Navigation Bar

**Feature**: 057-web-navigation  
**Date**: 2025-12-16  
**Status**: Ready for implementation

## Overview

This guide provides step-by-step instructions for setting up the development environment, implementing the navigation bar feature, and running tests.

---

## Prerequisites

- **Node.js**: v24 (LTS) or higher
- **npm**: v10 or higher
- **Git**: Checkout branch `057-web-navigation`
- **Java**: JDK 21 (for E2E tests)
- **Maven**: 3.9+ (for E2E tests)

---

## Local Development Setup

### 1. Install Dependencies

```bash
# Navigate to webApp directory
cd /Users/pawelkedra/code/AI-First/webApp

# Install dependencies
npm install

# NOTE: react-icons v5.5.0 is already in package.json - no new dependencies needed!
```

### 2. Start Development Server

```bash
# From webApp/ directory
npm run start

# Server will start at http://localhost:5173 (Vite default)
```

### 3. Verify Existing Routes

Open browser and check:
- Current root: `http://localhost:5173/` (shows lost pets list - will move to `/lost-pets`)
- Check React Router setup in `src/App.tsx`

---

## Implementation Steps

### Phase 1: Create Static Navigation Component (TDD)

#### Step 1: Write Component Tests First (RED)

Create `/webApp/src/components/__tests__/NavigationBar.test.tsx`:

```typescript
import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { describe, it, expect } from 'vitest';
import { NavigationBar } from '../NavigationBar';

describe('NavigationBar', () => {
  it('should render all navigation items', () => {
    // given
    render(
      <BrowserRouter>
        <NavigationBar />
      </BrowserRouter>
    );

    // when
    const homeLink = screen.getByTestId('navigation.home.link');
    const lostPetLink = screen.getByTestId('navigation.lostPet.link');
    const foundPetLink = screen.getByTestId('navigation.foundPet.link');
    const contactLink = screen.getByTestId('navigation.contact.link');
    const accountLink = screen.getByTestId('navigation.account.link');

    // then
    expect(homeLink).toBeInTheDocument();
    expect(lostPetLink).toBeInTheDocument();
    expect(foundPetLink).toBeInTheDocument();
    expect(contactLink).toBeInTheDocument();
    expect(accountLink).toBeInTheDocument();
  });

  it('should render PetSpot logo', () => {
    // given
    render(
      <BrowserRouter>
        <NavigationBar />
      </BrowserRouter>
    );

    // when
    const logo = screen.getByAltText('PetSpot');

    // then
    expect(logo).toBeInTheDocument();
  });
});
```

Run tests (should FAIL):
```bash
npm test
```

#### Step 2: Implement NavigationBar Component (GREEN)

Create `/webApp/src/components/NavigationBar.tsx`:

```typescript
import { NavLink } from 'react-router-dom';
import { AiOutlineHome, BiSearch, AiOutlineHeart, AiOutlineMail, AiOutlineUser } from 'react-icons/ai';
import styles from './NavigationBar.module.css';

const NAVIGATION_ITEMS = [
  { id: 'home', label: 'Home', icon: AiOutlineHome, path: '/', testId: 'navigation.home.link' },
  { id: 'lostPet', label: 'Lost Pet', icon: BiSearch, path: '/lost-pets', testId: 'navigation.lostPet.link' },
  { id: 'foundPet', label: 'Found Pet', icon: AiOutlineHeart, path: '/found-pets', testId: 'navigation.foundPet.link' },
  { id: 'contact', label: 'Contact Us', icon: AiOutlineMail, path: '/contact', testId: 'navigation.contact.link' },
  { id: 'account', label: 'Account', icon: AiOutlineUser, path: '/account', testId: 'navigation.account.link' },
];

export function NavigationBar() {
  return (
    <nav className={styles.navigationBar}>
      <NavLink to="/" className={styles.logoLink} data-testid="navigation.logo.link">
        <img src="/logo.svg" alt="PetSpot" className={styles.logo} />
      </NavLink>
      <div className={styles.navigationItems}>
        {NAVIGATION_ITEMS.map((item) => (
          <NavLink
            key={item.id}
            to={item.path}
            data-testid={item.testId}
            className={({ isActive }) =>
              isActive ? styles.navigationItemActive : styles.navigationItem
            }
          >
            <item.icon className={styles.icon} size={20} />
            <span className={styles.label}>{item.label}</span>
          </NavLink>
        ))}
      </div>
    </nav>
  );
}
```

Run tests (should PASS):
```bash
npm test
```

#### Step 3: Add CSS Styles (REFACTOR)

Create `/webApp/src/components/NavigationBar.module.css`:

```css
.navigationBar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 2rem;
  background-color: #ffffff;
  border-bottom: 1px solid #e5e7eb;
}

.logoLink {
  display: flex;
  align-items: center;
  text-decoration: none;
}

.logo {
  height: 2.5rem;
  width: auto;
}

.navigationItems {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.navigationItem,
.navigationItemActive {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border-radius: 0.5rem;
  text-decoration: none;
  font-weight: 500;
  transition: background-color 0.2s;
}

.navigationItem {
  background-color: transparent;
  color: #4a5565;
}

.navigationItem:hover {
  background-color: #f3f4f6;
}

.navigationItemActive {
  background-color: #eff6ff;
  color: #155dfc;
}

.icon {
  flex-shrink: 0;
}

.label {
  white-space: nowrap;
}

/* Hide navigation on mobile/tablet */
@media (max-width: 767px) {
  .navigationBar {
    display: none;
  }
}
```

---

### Phase 2: Create Route Placeholders

#### Step 1: Create Placeholder Components

Create `/webApp/src/routes/Home.tsx`:
```typescript
export function Home() {
  return (
    <div style={{ padding: '2rem' }}>
      <h1>Welcome to PetSpot</h1>
      <p>Home landing page - Coming soon</p>
    </div>
  );
}
```

Create `/webApp/src/routes/FoundPets.tsx`:
```typescript
export function FoundPets() {
  return (
    <div style={{ padding: '2rem' }}>
      <h1>Found Pets</h1>
      <p>Coming soon</p>
    </div>
  );
}
```

Create `/webApp/src/routes/Contact.tsx`:
```typescript
export function Contact() {
  return (
    <div style={{ padding: '2rem' }}>
      <h1>Contact Us</h1>
      <p>Coming soon</p>
    </div>
  );
}
```

Create `/webApp/src/routes/Account.tsx`:
```typescript
export function Account() {
  return (
    <div style={{ padding: '2rem' }}>
      <h1>Account</h1>
      <p>Authentication and account management - Coming soon</p>
    </div>
  );
}
```

#### Step 2: Move Lost Pets to `/lost-pets`

Create `/webApp/src/routes/LostPets.tsx` and move existing logic from root route.

---

### Phase 3: Update React Router Configuration

Update `/webApp/src/App.tsx`:

```typescript
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { NavigationBar } from './components/NavigationBar';
import { Home } from './routes/Home';
import { LostPets } from './routes/LostPets';
import { FoundPets } from './routes/FoundPets';
import { Contact } from './routes/Contact';
import { Account } from './routes/Account';

function App() {
  return (
    <BrowserRouter>
      <NavigationBar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/lost-pets" element={<LostPets />} />
        <Route path="/found-pets" element={<FoundPets />} />
        <Route path="/contact" element={<Contact />} />
        <Route path="/account" element={<Account />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
```

---

## Testing

### Unit/Component Tests

```bash
# Run all tests
cd /Users/pawelkedra/code/AI-First/webApp
npm test

# Run tests with coverage
npm test --coverage

# Check coverage report
open coverage/index.html

# Expected: 80%+ coverage for NavigationBar component
```

### Manual Testing (Browser)

1. Start dev server: `npm run start`
2. Open `http://localhost:5173`
3. Test checklist:
   - [ ] Navigation bar visible at top
   - [ ] Logo displays on left
   - [ ] All 5 navigation items visible
   - [ ] Home item is active (blue background) on `/`
   - [ ] Click "Lost Pet" → navigates to `/lost-pets`
   - [ ] Lost Pet becomes active (blue)
   - [ ] Click "Found Pet" → navigates to `/found-pets`
   - [ ] Click "Contact Us" → navigates to `/contact`
   - [ ] Click "Account" → navigates to `/account`
   - [ ] Resize window to <768px → navigation disappears
   - [ ] Resize window to ≥768px → navigation reappears
   - [ ] Browser back button → active state updates correctly

### E2E Tests (Selenium/Cucumber)

Create `/e2e-tests/java/src/test/resources/features/web/057-navigation.feature`:

```gherkin
Feature: Web Navigation Bar

  @web
  Scenario: User navigates to Lost Pet announcements
    Given user is on the Home page
    When user clicks "Lost Pet" in the navigation bar
    Then user should be on the Lost Pet announcements page
    And "Lost Pet" navigation item should be highlighted

  @web
  Scenario: Active navigation state updates on direct URL access
    Given user directly accesses "/lost-pets" URL
    When the page loads
    Then "Lost Pet" navigation item should be highlighted
    And other navigation items should not be highlighted
```

Run E2E tests:
```bash
cd /Users/pawelkedra/code/AI-First/e2e-tests/java
mvn test -Dtest=WebTestRunner

# View report
open target/cucumber-reports/web/index.html
```

---

## Troubleshooting

### Issue: Navigation items not highlighting

**Solution**: Check React Router integration - ensure `BrowserRouter` wraps entire app and `NavLink` components receive correct `to` prop.

### Issue: Icons not displaying

**Solution**: Verify `react-icons` is installed (should be in package.json v5.5.0). Check import: `import { AiOutlineHome } from 'react-icons/ai'`

### Issue: Navigation visible on mobile

**Solution**: Check CSS media query in `NavigationBar.module.css`. Ensure `@media (max-width: 767px)` has `display: none`.

### Issue: Tests fail with "not wrapped in Router"

**Solution**: Wrap test component in `<BrowserRouter>` in test file:
```typescript
render(
  <BrowserRouter>
    <NavigationBar />
  </BrowserRouter>
);
```

---

## Verification Checklist

Before merging to main:

- [ ] All unit/component tests pass (`npm test`)
- [ ] Test coverage ≥80% (`npm test --coverage`)
- [ ] All E2E tests pass (`mvn test -Dtest=WebTestRunner`)
- [ ] Manual testing complete (all scenarios)
- [ ] Navigation visible on desktop (≥768px)
- [ ] Navigation hidden on mobile (<768px)
- [ ] All navigation items functional
- [ ] Active state updates correctly
- [ ] No console errors in browser
- [ ] ESLint passes (`npm run lint` - if configured)
- [ ] Code formatted (`npm run format` - if configured)

---

## Next Steps

After implementation:
1. Run `/speckit.tasks` to break plan into atomic tasks
2. Implement tasks following TDD workflow (Red-Green-Refactor)
3. Commit changes to `057-web-navigation` branch
4. Open pull request for code review
5. Merge to main after approval and CI passes

---

## References

- Feature Spec: [spec.md](./spec.md)
- Implementation Plan: [plan.md](./plan.md)
- Research: [research.md](./research.md)
- Data Model: [data-model.md](./data-model.md)
- Contracts: [contracts/](./contracts/)
- react-icons: https://react-icons.github.io/react-icons/
- React Router v6: https://reactrouter.com/en/main
- Vitest: https://vitest.dev
- React Testing Library: https://testing-library.com/react
