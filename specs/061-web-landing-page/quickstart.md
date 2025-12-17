# Quickstart: Web Application Landing Page

**Feature**: 061-web-landing-page  
**Date**: 2025-12-17

## Overview

This quickstart guide provides a step-by-step walkthrough for implementing the web application landing page feature. Follow this guide to understand the implementation flow and key decisions.

---

## Prerequisites

- Node.js v24 (LTS) installed
- Backend server running on `http://localhost:3000`
- Web app development server accessible
- Familiarity with React 18, TypeScript, and CSS modules

---

## Quick Start Steps

### 1. Understand the Architecture

**Landing Page Structure**:
```
LandingPage (Main Component)
├── HeroSection
│   ├── Heading + Description
│   └── FeatureCard (x4) - Static, informational only
├── RecentPetsSection
│   ├── Section Header
│   ├── LandingPageCard (max 5) - Dynamic from API
│   └── "View all →" link
└── Footer
    ├── Branding Column (logo + mission)
    ├── Quick Links Column
    └── Contact Column
```

**Data Flow**:
```
User navigates to "/" 
  → LandingPage component mounts
  → usePets() hook fetches /api/v1/announcements
  → Filter for status='MISSING', sort by createdAt, limit 5
  → Render pet cards
```

---

### 2. File Structure Overview

**Existing Files to Reuse**:
- `webApp/src/hooks/use-announcement-list.ts` - Already exists, fetches announcements with geolocation
- `webApp/src/services/announcement-service.ts` - Already exists, API service for announcements
- `webApp/src/types/announcement.ts` - Already exists, Announcement type definitions
- `webApp/src/contexts/GeolocationContext.tsx` - Already exists, provides user location

**New Files to Create**:

```
webApp/src/
├── lib/
│   ├── date-utils.ts                  # Date formatting utilities
│   ├── distance-utils.ts              # Distance formatting utilities
│   └── __test__/
│       ├── date-utils.test.ts         # Unit tests (TDD)
│       └── distance-utils.test.ts     # Unit tests (TDD)
├── components/
│   └── home/                          # Landing page components (organized together)
│       ├── LandingPage.tsx            # Main landing page component
│       ├── LandingPage.module.css     # Landing page styles
│       ├── HeroSection.tsx            # Hero section with feature cards
│       ├── HeroSection.module.css     # Hero section styles
│       ├── FeatureCard.tsx            # Individual feature card
│       ├── FeatureCard.module.css     # Feature card styles
│       ├── RecentPetsSection.tsx      # Recently lost pets section
│       ├── RecentPetsSection.module.css # Recent pets section styles
│       ├── LandingPageCard.tsx        # Simplified pet card for landing page
│       ├── LandingPageCard.module.css # Landing page card styles
│       ├── Footer.tsx                 # Footer component
│       ├── Footer.module.css          # Footer styles
│       └── __tests__/                 # Component tests (recommended)
│           ├── LandingPage.test.tsx
│           ├── HeroSection.test.tsx
│           ├── FeatureCard.test.tsx
│           ├── RecentPetsSection.test.tsx
│           └── Footer.test.tsx
└── pages/
    └── Home.tsx                       # Update to use LandingPage component

e2e-tests/java/src/test/
├── resources/features/web/
│   └── landing-page.feature           # Gherkin E2E scenarios
└── java/.../pages/
    ├── LandingPage.java               # Page Object Model
    └── steps-web/
        └── LandingPageSteps.java      # Step definitions
```

**Additional Existing Files to Reuse**:
- `webApp/src/utils/coordinate-formatter.ts` - Already exists (reuse for location formatting)
- `webApp/src/utils/pascal-case-formatter.ts` - Already exists (reuse for species/breed formatting)

---

### 3. Implementation Order (TDD Workflow)

Follow Test-Driven Development (Red-Green-Refactor) for all implementation tasks.

**Phase 1: Utilities (Pure Functions)**

1. **Date Utilities** (`lib/date-utils.ts`):
   - RED: Write failing tests for `formatRelativeDate()`
   - GREEN: Implement function (e.g., "2 days ago", "1 week ago")
   - REFACTOR: Simplify code
   - Verify: Tests pass, linting clean

2. **Distance Utilities** (`lib/distance-utils.ts`):
   - RED: Write failing tests for `formatDistance()`
   - GREEN: Implement function (e.g., "1.5 km away", "Location unknown")
   - REFACTOR: Simplify code
   - Verify: Tests pass, linting clean

**Phase 2: Presentational Components (UI)**

All components in `/webApp/src/components/home/` directory:

3. **FeatureCard Component** (`components/home/FeatureCard.tsx`):
   - RED: Write failing tests for rendering icon, title, description
   - GREEN: Implement component with props
   - REFACTOR: Simplify styles
   - Verify: Tests pass, linting clean

4. **HeroSection Component** (`components/home/HeroSection.tsx`):
   - RED: Write failing tests for heading, description, 4 feature cards
   - GREEN: Implement component with gradient background
   - REFACTOR: Extract feature cards data to constant
   - Verify: Tests pass, linting clean

5. **LandingPageCard Component** (`components/home/LandingPageCard.tsx`):
   - RED: Write failing tests for photo, status, location, breed, date
   - GREEN: Implement component with `Announcement` props
   - REFACTOR: Reuse utilities (`formatRelativeDate`, `formatDistance`)
   - Verify: Tests pass, linting clean

6. **RecentPetsSection Component** (`components/home/RecentPetsSection.tsx`):
   - RED: Write failing tests for heading, pet cards, "View all" link
   - GREEN: Implement component using `useAnnouncementList()` hook
   - REFACTOR: Extract filtering/sorting logic, simplify conditional rendering
   - Verify: Tests pass, linting clean
   - Note: Uses existing `useAnnouncementList()` hook, filters for MISSING status

7. **Footer Component** (`components/home/Footer.tsx`):
   - RED: Write failing tests for branding, links, contact columns
   - GREEN: Implement component with static data
   - REFACTOR: Extract footer data to constants
   - Verify: Tests pass, linting clean

8. **LandingPage Component** (`components/home/LandingPage.tsx`):
   - RED: Write failing tests for rendering all sections
   - GREEN: Implement component composing HeroSection, RecentPetsSection, Footer
   - REFACTOR: Ensure clean component composition
   - Verify: Tests pass, linting clean

9. **Update Home Page** (`pages/Home.tsx`):
   - Import: `import { LandingPage } from '../components/home/LandingPage';`
   - Replace placeholder content with `<LandingPage />`
   - Verify: Route `/` renders LandingPage

**Phase 3: End-to-End Tests (Java/Selenium/Cucumber)**

10. **E2E Tests** (`e2e-tests/java/`):
    - Write Gherkin scenarios for all user stories
    - Implement Page Object Model (`LandingPage.java`)
    - Implement step definitions (`LandingPageSteps.java`)
    - Run: `mvn test -Dtest=WebTestRunner` (from `e2e-tests/java/`)
    - Verify: All scenarios pass

---

### 4. Key Implementation Details

#### Color Palette (from Figma + Existing Code)

```css
:root {
  /* Primary Colors */
  --primary-blue: #155DFC;     /* Buttons, links, accents */
  --red: #FB2C36;              /* MISSING status, danger */
  --green: #10B981;            /* Success, FOUND status (Emerald) */
  --purple: #7C3AED;           /* Feature card accent (Violet) */

  /* Text Colors */
  --text-dark: #101828;        /* Primary headings */
  --text-secondary: #545F71;   /* Body text, descriptions */
  --text-tertiary: #93A2B4;    /* Muted text, placeholders */

  /* Background Colors */
  --bg-white: #FFFFFF;
  --bg-light: #FAFAFA;         /* Page background */
  --bg-gray: #F2F4F8;          /* Section backgrounds */

  /* Border Colors */
  --border-light: #E5E7EB;
  --border-gray: #D1D5DC;

  /* Hero Gradient */
  --hero-gradient: linear-gradient(135deg, #EFF6FF 0%, #F3E8FF 100%); /* Light blue to light purple */
}
```

#### Component Props Examples

All components located in `/webApp/src/components/home/`:

```typescript
// FeatureCard (components/home/FeatureCard.tsx)
interface FeatureCardProps {
  icon: React.ComponentType<{ className?: string; size?: number }>;
  iconColor: string;
  title: string;
  description: string;
}

// LandingPageCard (components/home/LandingPageCard.tsx)
interface LandingPageCardProps {
  announcement: Announcement;
  onClick?: () => void;
}

// RecentPetsSection (components/home/RecentPetsSection.tsx)
interface RecentPetsSectionProps {
  announcements: Announcement[];
  isLoading: boolean;
  error: string | null;
}
```

**Import Example** (from Home.tsx):
```typescript
import { LandingPage } from '../components/home/LandingPage';
```

#### Data-Testid Naming Convention

```typescript
// Landing page sections
"landing.heroSection"
"landing.recentPetsSection"
"landing.footer"

// Hero section elements
"landing.hero.heading"
"landing.hero.description"
"landing.hero.featureCard.search"
"landing.hero.featureCard.reportMissing"
"landing.hero.featureCard.foundPet"
"landing.hero.featureCard.locationBased"

// Recent pets section
"landing.recentPets.heading"
"landing.recentPets.viewAllLink.click"
"landing.recentPets.petCard.${petId}"
"landing.recentPets.loading"
"landing.recentPets.error"
"landing.recentPets.emptyState"

// Footer elements
"landing.footer.logo"
"landing.footer.quickLink.${linkId}"
"landing.footer.legalLink.${linkId}"
"landing.footer.contact.email"
"landing.footer.contact.phone"
"landing.footer.contact.address"
"landing.footer.copyright"
```

---

### 5. Running the Application

**Start Backend Server**:
```bash
cd server
npm run dev
# Server running on http://localhost:3000
```

**Start Web Dev Server**:
```bash
cd webApp
npm run start
# Web app running on http://localhost:5173 (Vite default)
```

**Navigate to Landing Page**:
```
Open browser: http://localhost:5173/
Should see landing page with hero, feature cards, recent pets, footer
```

---

### 6. Testing Commands

**Run Unit Tests** (hooks + lib):
```bash
cd webApp
npm test                    # Run all tests
npm test --coverage         # Run with coverage report
```

**View Coverage Report**:
```bash
open webApp/coverage/index.html
# Ensure 80%+ line and branch coverage for hooks and lib
```

**Run E2E Tests**:
```bash
cd e2e-tests/java
mvn test -Dtest=WebTestRunner
# View report: e2e-tests/java/target/cucumber-reports/web/index.html
```

**Run Linter**:
```bash
cd webApp
npm run lint                # Check for linting issues
```

---

### 7. Debugging Tips

**Backend API not returning data**:
- Check backend server is running: `curl http://localhost:3000/api/v1/announcements`
- Verify database has MISSING announcements (not just FOUND)
- Check browser console for CORS errors

**Pet cards not displaying**:
- Check filtering logic: `announcements.filter(a => a.status === 'MISSING')`
- Verify sorting: `sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))`
- Check state update: `console.log(announcements)` in component

**Images not loading**:
- Verify `photoUrl` is prepended with `config.apiBaseUrl`
- Check backend `/public/images/` directory has image files
- Use placeholder image if `photoUrl` is null

**Styles not applying**:
- Verify CSS module import: `import styles from './Component.module.css'`
- Check className usage: `<div className={styles.container}>`
- Inspect element in browser DevTools to verify classes applied

---

### 8. Common Pitfalls

1. **Forgetting to filter for MISSING status**: Backend returns both MISSING and FOUND
2. **Not limiting to 5 items**: Frontend must slice array after sorting
3. **Incorrect date parsing**: Use `new Date()` constructor with ISO 8601 strings
4. **Photo URL not absolute**: Prepend `config.apiBaseUrl` before rendering
5. **Missing data-testid attributes**: E2E tests rely on these for element selection
6. **Deep nesting**: Extract nested logic to helper functions (max 3 levels)
7. **Inline styles in JSX**: Use CSS modules, not inline `style={{}}` prop
8. **Missing test coverage**: Ensure 80%+ coverage for hooks and lib functions

---

### 9. Next Steps After Implementation

1. **Manual Testing**: Navigate to landing page, verify all sections render
2. **Accessibility**: Run Lighthouse audit (target 90+ accessibility score)
3. **Responsive Testing**: Test on tablet (768px) and desktop (1024px+) viewports
4. **Browser Testing**: Test in Chrome, Firefox, Safari
5. **E2E Testing**: Run full E2E suite to verify user scenarios
6. **Code Review**: Submit PR for review with checklist:
   - [ ] All tests passing
   - [ ] 80%+ coverage for hooks and lib
   - [ ] Linting clean
   - [ ] E2E tests passing
   - [ ] Manual testing complete
   - [ ] Accessibility score 90+

---

## Summary

The landing page feature is a web-only implementation consuming an existing backend API and reusing the existing `useAnnouncementList()` hook. Follow TDD workflow (Red-Green-Refactor) for all tasks. Focus on:

1. **Utilities first** (pure functions, easy to test)
2. **Components from bottom-up** (FeatureCard → HeroSection → LandingPageCard → RecentPetsSection → Footer → LandingPage)
3. **Reuse existing hook** (`useAnnouncementList()` with filtering/sorting in component)
4. **E2E tests last** (verify full user scenarios)

Refer to `data-model.md` for entity definitions and `api-contracts.md` for API documentation.

---

## Quick Reference Commands

```bash
# Run backend (from /server)
npm run dev

# Run web app (from /webApp)
npm run start

# Run unit tests (from /webApp)
npm test
npm test --coverage

# Run E2E tests (from /e2e-tests/java)
mvn test -Dtest=WebTestRunner

# Lint (from /webApp)
npm run lint
```

Happy coding! 🚀

