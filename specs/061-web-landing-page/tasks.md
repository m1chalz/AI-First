# Tasks: Web Application Landing Page

**Feature**: 061-web-landing-page  
**Input**: Design documents from `/specs/061-web-landing-page/`  
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/api-contracts.md, quickstart.md

**Tests**: Test requirements for this project:

**MANDATORY - Web Unit Tests**:
- Location: `/webApp/src/lib/__test__/` (Vitest), 80% coverage
  - Scope: Pure utility functions (date formatting, distance formatting)
  - Run: `npm test --coverage` (from webApp/)
  - Convention: MUST follow Given-When-Then structure with descriptive names

**MANDATORY - Web Component Tests** (Recommended):
- Location: `/webApp/src/components/home/__tests__/` (Vitest + React Testing Library)
  - Scope: Component rendering and user interactions
  - Run: `npm test` (from webApp/)
  - Convention: MUST follow Given-When-Then structure

**MANDATORY - End-to-End Tests**:
- Web: `/e2e-tests/java/src/test/resources/features/web/landing-page.feature` (Java + Selenium + Cucumber)
- All user stories MUST have E2E test coverage
- Use Page Object Model pattern
- Convention: MUST structure scenarios with Given-When-Then phases
- Run: `mvn test -Dtest=WebTestRunner` (from e2e-tests/java/)

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `- [ ] [ID] [P?] [Story?] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2, US3, US4)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [X] T001 Review existing web app structure in `/webApp/src/` (components, hooks, services, lib, types)
- [X] T002 [P] Verify `react-icons` is installed in `/webApp/package.json` (Heroicons and Material Design icons)
- [X] T003 [P] Verify existing `useAnnouncementList()` hook in `/webApp/src/hooks/use-announcement-list.ts`
- [X] T004 [P] Verify existing `announcementService` in `/webApp/src/services/announcement-service.ts`
- [X] T005 [P] Verify existing `Announcement` type in `/webApp/src/types/announcement.ts`
- [X] T006 [P] Create `/webApp/src/components/home/` directory for landing page components

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T007 [P] Create `/webApp/src/lib/date-utils.ts` file (placeholder for date formatting utilities)
- [X] T008 [P] Create `/webApp/src/lib/distance-utils.ts` file (placeholder for distance formatting utilities)
- [X] T009 [P] Create E2E feature file `/e2e-tests/java/src/test/resources/features/web/landing-page.feature`
- [X] T010 [P] Create E2E Page Object `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/pages/web/LandingPage.java`
- [X] T011 [P] Create E2E step definitions `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/steps/web/LandingPageSteps.java`

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - View Landing Page on Web App Launch (Priority: P1) 🎯 MVP

**Goal**: Display a welcoming landing page with hero section, feature cards, recent lost pets, and footer when users open the web app

**Independent Test**: Navigate to web app root URL (`/`) and verify all landing page sections render correctly (hero, feature cards, recent pets, footer)

### Tests for User Story 1 (MANDATORY) ✅

> **TDD: Write these tests FIRST, ensure they FAIL before implementation**

**Web Unit Tests** (TDD: Red-Green-Refactor):
- [X] T012 [P] [US1] RED: Write failing unit test for `formatRelativeDate()` in `/webApp/src/lib/__test__/date-utils.test.ts` (Vitest, Given-When-Then: test "2 days ago", "1 week ago", "today", edge cases)
- [X] T013 [P] [US1] RED: Write failing unit test for `formatDistance()` in `/webApp/src/lib/__test__/distance-utils.test.ts` (Vitest, Given-When-Then: test "1.5 km away", "500 m away", "Location unknown" for undefined/null)

**Web Component Tests** (Recommended):
- [X] T014 [P] [US1] Write component test for `FeatureCard` in `/webApp/src/components/home/__tests__/FeatureCard.test.tsx` (Vitest + RTL: verify icon, title, description render)
- [X] T015 [P] [US1] Write component test for `HeroSection` in `/webApp/src/components/home/__tests__/HeroSection.test.tsx` (Vitest + RTL: verify heading, description, 4 feature cards)
- [X] T016 [P] [US1] Write component test for `Footer` in `/webApp/src/components/home/__tests__/Footer.test.tsx` (Vitest + RTL: verify branding, quick links, contact columns)
- [X] T017 [P] [US1] Write component test for `LandingPage` in `/webApp/src/components/home/__tests__/LandingPage.test.tsx` (Vitest + RTL: verify all sections render)

**End-to-End Tests**:
- [X] T018 [P] [US1] Write E2E Gherkin scenarios for US1 in `/e2e-tests/java/src/test/resources/features/web/landing-page.feature` (Given user navigates to "/" → Then landing page displays hero, feature cards, footer)
- [X] T019 [P] [US1] Implement Page Object methods for hero section in `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/pages/web/LandingPage.java` (findHeroHeading, findFeatureCards, etc.)
- [X] T020 [P] [US1] Implement step definitions for US1 in `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/steps/web/LandingPageSteps.java`

### Implementation for User Story 1

**Web Utilities** (TDD: Red-Green-Refactor):
- [X] T021 [P] [US1] GREEN: Implement `formatRelativeDate(dateString: string): string` in `/webApp/src/lib/date-utils.ts` (minimal code to pass test: "2 days ago", "1 week ago", "today")
- [X] T022 [US1] REFACTOR: Improve `formatRelativeDate()` code quality (extract helpers, simplify logic)
- [X] T023 [P] [US1] GREEN: Implement `formatDistance(distanceKm: number | undefined): string` in `/webApp/src/lib/distance-utils.ts` (minimal code to pass test: "1.5 km away", "Location unknown")
- [X] T024 [US1] REFACTOR: Improve `formatDistance()` code quality (extract helpers, handle edge cases)

**Web Components** (Bottom-Up):
- [X] T025 [P] [US1] Create `FeatureCard.tsx` in `/webApp/src/components/home/FeatureCard.tsx` (props: icon, iconColor, title, description)
- [X] T026 [P] [US1] Create `FeatureCard.module.css` in `/webApp/src/components/home/FeatureCard.module.css` (card styles, icon background, text styles)
- [X] T027 [US1] Create `HeroSection.tsx` in `/webApp/src/components/home/HeroSection.tsx` (heading, description, 4 feature cards in grid)
- [X] T028 [US1] Create `HeroSection.module.css` in `/webApp/src/components/home/HeroSection.module.css` (gradient background: linear-gradient(135deg, #EFF6FF, #F3E8FF), responsive grid)
- [X] T029 [US1] Define `FEATURE_CARDS` constant in `HeroSection.tsx` (4 cards: Search Database/blue, Report Missing/red, Found a Pet/green, Location Based/purple)
- [X] T030 [P] [US1] Create `Footer.tsx` in `/webApp/src/components/home/Footer.tsx` (3 columns: branding, quick links, contact info)
- [X] T031 [P] [US1] Create `Footer.module.css` in `/webApp/src/components/home/Footer.module.css` (dark background, 3-column layout, responsive)
- [X] T032 [US1] Define `FOOTER_QUICK_LINKS`, `FOOTER_LEGAL_LINKS`, `FOOTER_CONTACT` constants in `Footer.tsx`
- [X] T033 [US1] Create `LandingPage.tsx` in `/webApp/src/components/home/LandingPage.tsx` (compose HeroSection + RecentPetsSection + Footer)
- [X] T034 [P] [US1] Create `LandingPage.module.css` in `/webApp/src/components/home/LandingPage.module.css` (page layout, section spacing)
- [X] T035 [US1] Update `/webApp/src/pages/Home.tsx` to import and render `<LandingPage />` component

**Test Identifiers** (MANDATORY):
- [X] T036 [P] [US1] Add `data-testid="landing.heroSection"` to hero section in `HeroSection.tsx`
- [X] T037 [P] [US1] Add `data-testid="landing.hero.heading"` to hero heading in `HeroSection.tsx`
- [X] T038 [P] [US1] Add `data-testid="landing.hero.featureCard.{id}"` to each feature card in `FeatureCard.tsx`
- [X] T039 [P] [US1] Add `data-testid="landing.footer"` to footer in `Footer.tsx`
- [X] T040 [P] [US1] Add `data-testid="landing.footer.quickLink.{id}"` to quick links in `Footer.tsx`

**Quality Checks**:
- [X] T041 [US1] Run `npm test` from `/webApp/` and verify all US1 component tests pass
- [X] T042 [US1] Run `npm test --coverage` from `/webApp/` and verify 80%+ coverage for `date-utils.ts` and `distance-utils.ts`
- [X] T043 [P] [US1] Run `npm run lint` from `/webApp/` and fix ESLint violations
- [ ] T044 [US1] Run `mvn test -Dtest=WebTestRunner` from `/e2e-tests/java/` and verify US1 E2E scenarios pass

**Checkpoint**: At this point, User Story 1 should be fully functional - landing page displays with hero section (heading, description, 4 feature cards) and footer (branding, quick links, contact)

---

## Phase 4: User Story 2 - Understand Portal Features via Feature Cards (Priority: P1)

**Goal**: Display four informational feature cards (Search Database, Report Missing, Found a Pet, Location Based) in the hero section with visual distinction and descriptions

**Independent Test**: Verify that four feature cards are displayed in the hero section with appropriate icons, titles, descriptions, and colors. Cards are display-only (not interactive).

**Note**: User Story 2 is largely complete after US1 implementation (feature cards created in HeroSection). This phase adds any missing polish or tests.

### Tests for User Story 2 (MANDATORY) ✅

**End-to-End Tests**:
- [X] T045 [P] [US2] Write E2E Gherkin scenarios for US2 in `/e2e-tests/java/src/test/resources/features/web/landing-page.feature` (verify 4 cards visible, correct order, icons/titles/descriptions)
- [X] T046 [P] [US2] Implement Page Object methods for feature cards in `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/pages/web/LandingPage.java` (findFeatureCard by id, verifyFeatureCardContent)
- [X] T047 [P] [US2] Implement step definitions for US2 in `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/steps/web/LandingPageSteps.java`

### Implementation for User Story 2

**Polish**:
- [X] T048 [US2] Verify feature card order in `FEATURE_CARDS` constant: Search Database (blue), Report Missing (red), Found a Pet (green), Location Based (purple)
- [X] T049 [US2] Verify feature cards are NOT clickable (no onClick handlers, no cursor:pointer styles)
- [X] T050 [US2] Verify responsive layout for feature cards in `HeroSection.module.css` (4 columns desktop, 2x2 grid tablet)

**Quality Checks**:
- [ ] T051 [US2] Run `mvn test -Dtest=WebTestRunner` from `/e2e-tests/java/` and verify US2 E2E scenarios pass
- [ ] T052 [US2] Manual test: Navigate to `/` and verify 4 feature cards display in correct order with correct colors

**Checkpoint**: User Story 2 complete - feature cards display correctly with all required information

---

## Phase 5: User Story 3 - Browse Recently Lost Pets from Landing Page (Priority: P2)

**Goal**: Display up to 5 most recently reported MISSING pets in a dedicated section below the hero, with pet cards showing photo, location, type/breed, report date, and a "View all →" link

**Independent Test**: Verify the "Recently Lost Pets" section displays pet cards with correct data from backend API (filtered for MISSING status, sorted by date, limited to 5)

### Tests for User Story 3 (MANDATORY) ✅

> **TDD: Write these tests FIRST, ensure they FAIL before implementation**

**Web Component Tests** (Recommended):
- [X] T053 [P] [US3] Write component test for `LandingPageCard` in `/webApp/src/components/home/__tests__/LandingPageCard.test.tsx` (Vitest + RTL: verify photo, status badge, location, breed, date render)
- [X] T054 [P] [US3] Write component test for `RecentPetsSection` in `/webApp/src/components/home/__tests__/RecentPetsSection.test.tsx` (Vitest + RTL: verify loading state, error state, empty state, pet cards, "View all" link)

**End-to-End Tests**:
- [X] T055 [P] [US3] Write E2E Gherkin scenarios for US3 in `/e2e-tests/java/src/test/resources/features/web/landing-page.feature` (verify up to 5 MISSING pets display, "View all" link navigates correctly)
- [X] T056 [P] [US3] Implement Page Object methods for recent pets section in `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/pages/web/LandingPage.java` (findRecentPetsSection, findPetCards, findViewAllLink)
- [X] T057 [P] [US3] Implement step definitions for US3 in `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/steps/web/LandingPageSteps.java`

### Implementation for User Story 3

**Web Components**:
- [X] T058 [P] [US3] Create `LandingPageCard.tsx` in `/webApp/src/components/home/LandingPageCard.tsx` (props: announcement, onClick; display: photo, MISSING badge, location+distance, species/breed, date)
- [X] T059 [P] [US3] Create `LandingPageCard.module.css` in `/webApp/src/components/home/LandingPageCard.module.css` (vertical layout, photo at top, compact card for grid)
- [X] T060 [US3] Implement photo rendering in `LandingPageCard.tsx` (prepend `config.apiBaseUrl`, handle null with placeholder)
- [X] T061 [US3] Implement status badge in `LandingPageCard.tsx` (MISSING = red, use `ANNOUNCEMENT_STATUS_BADGE_COLORS` from types)
- [X] T062 [US3] Implement location display in `LandingPageCard.tsx` (use `formatDistance()` from lib, `MdLocationOn` icon)
- [X] T063 [US3] Implement species/breed display in `LandingPageCard.tsx` (use `toPascalCase()` from utils, format: "Dog • Golden Retriever")
- [X] T064 [US3] Implement date display in `LandingPageCard.tsx` (use `formatRelativeDate()` from lib, `HiOutlineCalendar` icon)
- [X] T065 [US3] Implement card click handler in `LandingPageCard.tsx` (navigate to pet details page)
- [X] T066 [US3] Create `RecentPetsSection.tsx` in `/webApp/src/components/home/RecentPetsSection.tsx` (use `useAnnouncementList()` hook)
- [X] T067 [P] [US3] Create `RecentPetsSection.module.css` in `/webApp/src/components/home/RecentPetsSection.module.css` (section layout, heading styles, cards grid)
- [X] T068 [US3] Implement filtering logic in `RecentPetsSection.tsx` (filter for `status === 'MISSING'`)
- [X] T069 [US3] Implement sorting logic in `RecentPetsSection.tsx` (sort by `createdAt` descending)
- [X] T070 [US3] Implement limiting logic in `RecentPetsSection.tsx` (slice to 5 items)
- [X] T071 [US3] Implement loading state in `RecentPetsSection.tsx` (display skeleton loaders or spinner with `data-testid="landing.recentPets.loading"`)
- [X] T072 [US3] Implement error state in `RecentPetsSection.tsx` (display "Unable to load recent pets. Please refresh the page to try again." with `data-testid="landing.recentPets.error"`)
- [X] T073 [US3] Implement empty state in `RecentPetsSection.tsx` (display "No recent lost pet reports. Check back soon!" with `data-testid="landing.recentPets.emptyState"`)
- [X] T074 [US3] Implement "View all →" link in `RecentPetsSection.tsx` (navigate to `/lost-pets` using `AppRoutes.lostPets`)
- [X] T075 [US3] Update `LandingPage.tsx` to include `<RecentPetsSection />` between HeroSection and Footer

**Test Identifiers** (MANDATORY):
- [X] T076 [P] [US3] Add `data-testid="landing.recentPetsSection"` to recent pets section in `RecentPetsSection.tsx`
- [X] T077 [P] [US3] Add `data-testid="landing.recentPets.heading"` to section heading in `RecentPetsSection.tsx`
- [X] T078 [P] [US3] Add `data-testid="landing.recentPets.petCard.${announcement.id}"` to each pet card in `LandingPageCard.tsx`
- [X] T079 [P] [US3] Add `data-testid="landing.recentPets.viewAllLink.click"` to "View all" link in `RecentPetsSection.tsx`

**Quality Checks**:
- [X] T080 [US3] Run `npm test` from `/webApp/` and verify all US3 component tests pass
- [X] T081 [P] [US3] Run `npm run lint` from `/webApp/` and fix ESLint violations
- [ ] T082 [US3] Run `mvn test -Dtest=WebTestRunner` from `/e2e-tests/java/` and verify US3 E2E scenarios pass
- [ ] T083 [US3] Manual test: Navigate to `/` and verify recent pets section displays up to 5 MISSING pets with correct data

**Checkpoint**: User Story 3 complete - recently lost pets section displays correctly with filtering, sorting, and limiting

---

## Phase 6: User Story 4 - Access Footer Information (Priority: P3)

**Goal**: Display footer information at the bottom of the landing page, including branding, quick links (with functional navigation), contact information, copyright notice, and legal links

**Independent Test**: Scroll to the bottom of the landing page and verify footer sections render correctly (branding, quick links with navigation, contact info)

**Note**: User Story 4 is largely complete after US1 implementation (footer created). This phase adds functional navigation and tests.

### Tests for User Story 4 (MANDATORY) ✅

**End-to-End Tests**:
- [X] T084 [P] [US4] Write E2E Gherkin scenarios for US4 in `/e2e-tests/java/src/test/resources/features/web/landing-page.feature` (verify footer columns, quick links navigation, contact info display)
- [X] T085 [P] [US4] Implement Page Object methods for footer in `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/pages/LandingPage.java` (findFooter, findQuickLink, findContactInfo)
- [X] T086 [P] [US4] Implement step definitions for US4 in `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/steps/web/LandingPageSteps.java`

### Implementation for User Story 4

**Functional Navigation**:
- [X] T087 [US4] Implement "Report Lost Pet" link navigation in `Footer.tsx` (use `AppRoutes.reportMissing.microchip` from routes)
- [X] T088 [US4] Implement "Report Found Pet" link as placeholder in `Footer.tsx` (use `href="#"` or disabled styling with `isPlaceholder: true`)
- [X] T089 [US4] Implement "Search Database" link as placeholder in `Footer.tsx` (use `href="#"` with `isPlaceholder: true` per spec)
- [X] T090 [US4] Verify footer legal links (Privacy Policy, Terms of Service, Cookie Policy) are placeholders with `href="#"`
- [X] T091 [US4] Verify footer contact information displays with icons (email: `HiOutlineMail`, phone: `HiOutlinePhone`, address: `HiOutlineLocationMarker`)

**Test Identifiers** (MANDATORY):
- [X] T092 [P] [US4] Add `data-testid="landing.footer.logo"` to footer logo in `Footer.tsx`
- [X] T093 [P] [US4] Add `data-testid="landing.footer.contact.email"` to email in `Footer.tsx`
- [X] T094 [P] [US4] Add `data-testid="landing.footer.contact.phone"` to phone in `Footer.tsx`
- [X] T095 [P] [US4] Add `data-testid="landing.footer.contact.address"` to address in `Footer.tsx`
- [X] T096 [P] [US4] Add `data-testid="landing.footer.copyright"` to copyright notice in `Footer.tsx`
- [X] T097 [P] [US4] Add `data-testid="landing.footer.legalLink.{id}"` to legal links in `Footer.tsx`

**Quality Checks**:
- [ ] T098 [US4] Run `mvn test -Dtest=WebTestRunner` from `/e2e-tests/java/` and verify US4 E2E scenarios pass
- [ ] T099 [US4] Manual test: Scroll to footer, verify branding, quick links (click "Report Lost Pet" navigates correctly), contact info display
- [ ] T100 [US4] Manual test: Verify "Report Found Pet" and "Search Database" links are non-functional placeholders

**Checkpoint**: User Story 4 complete - footer displays with functional "Report Lost Pet" link and proper placeholder styling for non-functional links

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T101 [P] Add JSDoc documentation to complex utility functions in `/webApp/src/lib/date-utils.ts` and `/webApp/src/lib/distance-utils.ts` (skip self-explanatory functions)
- [ ] T102 [P] Verify responsive layout for tablet (768px-1023px) in all component CSS modules (feature cards 2x2 grid, footer 2-column layout)
- [ ] T103 [P] Verify responsive layout for desktop (1024px+) in all component CSS modules (feature cards 4-column grid, footer 3-column layout)
- [ ] T104 [US1] Add loading="lazy" attribute to pet card photos in `LandingPageCard.tsx`
- [ ] T105 [P] Run Lighthouse accessibility audit on landing page (target: 90+ accessibility score)
- [ ] T106 [P] Run full E2E test suite: `mvn test -Dtest=WebTestRunner` from `/e2e-tests/java/` and verify all scenarios pass
- [ ] T107 [P] Run final coverage check: `npm test --coverage` from `/webApp/` and verify 80%+ for lib utilities
- [ ] T108 [P] Run final linting check: `npm run lint` from `/webApp/` and verify no violations
- [ ] T109 Manual browser testing (Chrome, Firefox, Safari on desktop and tablet viewports)
- [ ] T110 Update feature documentation if needed

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-6)**: All depend on Foundational phase completion
  - User Story 1 (P1) → User Story 2 (P1): US2 builds on US1 (feature cards in hero section)
  - User Story 3 (P2): Can start after Foundational - depends on US1 (LandingPage component)
  - User Story 4 (P3): Can start after Foundational - depends on US1 (Footer component)
- **Polish (Phase 7)**: Depends on all user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories. Creates foundation: `LandingPage`, `HeroSection`, `Footer` components
- **User Story 2 (P1)**: Depends on User Story 1 (feature cards created in `HeroSection`). Adds polish and E2E tests.
- **User Story 3 (P2)**: Depends on User Story 1 (`LandingPage` component exists). Adds `RecentPetsSection` and `LandingPageCard` components.
- **User Story 4 (P3)**: Depends on User Story 1 (`Footer` component exists). Adds functional navigation to footer links.

### Within Each User Story

- Tests (TDD) MUST be written and FAIL before implementation
- Utilities before components (date-utils, distance-utils before RecentPetsSection)
- Presentational components before container components (FeatureCard before HeroSection, LandingPageCard before RecentPetsSection)
- Child components before parent components (HeroSection, Footer before LandingPage)
- Story implementation before E2E tests
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Within each user story:
  - All unit tests marked [P] can run in parallel
  - All component tests marked [P] can run in parallel
  - All E2E test artifacts (feature file, Page Objects, step definitions) marked [P] can run in parallel
  - Utilities marked [P] can be implemented in parallel (date-utils, distance-utils)
  - Component CSS files marked [P] can be created in parallel

**Note**: User Story 2, 3, 4 have DEPENDENCIES on User Story 1 (they reuse/extend components created in US1), so they CANNOT start until US1 is complete.

---

## Parallel Example: User Story 1

```bash
# Phase 2: Foundational (All in parallel)
Task T007: Create /webApp/src/lib/date-utils.ts
Task T008: Create /webApp/src/lib/distance-utils.ts
Task T009: Create E2E feature file
Task T010: Create E2E Page Object
Task T011: Create E2E step definitions

# Phase 3: US1 Tests (All in parallel after Phase 2)
Task T012: RED test for formatRelativeDate()
Task T013: RED test for formatDistance()
Task T014: Component test for FeatureCard
Task T015: Component test for HeroSection
Task T016: Component test for Footer
Task T017: Component test for LandingPage
Task T018: E2E Gherkin scenarios
Task T019: E2E Page Object methods
Task T020: E2E step definitions

# Phase 3: US1 Implementation (Utilities in parallel)
Task T021: GREEN implement formatRelativeDate()
Task T023: GREEN implement formatDistance()
# Then refactor utilities sequentially
Task T022: REFACTOR formatRelativeDate()
Task T024: REFACTOR formatDistance()

# Phase 3: US1 Components (Some in parallel)
Task T025: Create FeatureCard.tsx
Task T026: Create FeatureCard.module.css
Task T030: Create Footer.tsx
Task T031: Create Footer.module.css
# Then compose into parent components
Task T027: Create HeroSection.tsx (depends on FeatureCard)
Task T033: Create LandingPage.tsx (depends on HeroSection, Footer)
```

---

## Implementation Strategy

### MVP First (User Story 1 + User Story 2 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1 (landing page with hero section and footer)
4. Complete Phase 4: User Story 2 (feature cards polish and E2E tests)
5. **STOP and VALIDATE**: Test landing page with hero section and footer independently
6. Deploy/demo if ready (MVP: landing page with static content)

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 + User Story 2 → Test independently → Deploy/Demo (MVP: landing page with hero and footer)
3. Add User Story 3 → Test independently → Deploy/Demo (landing page with dynamic pet data)
4. Add User Story 4 → Test independently → Deploy/Demo (footer with functional navigation)
5. Each story adds value without breaking previous stories

### Recommended Approach

Since User Stories 2, 3, 4 all depend on User Story 1, implement **sequentially in priority order**:

1. Setup + Foundational (parallel tasks within each phase)
2. User Story 1 (P1) - Creates foundation components
3. User Story 2 (P1) - Builds on US1 (feature cards)
4. User Story 3 (P2) - Extends US1 (adds recent pets section)
5. User Story 4 (P3) - Enhances US1 (functional footer links)
6. Polish - Cross-cutting improvements

**Why Sequential**: US2/US3/US4 reuse and extend components from US1, so parallel development would create merge conflicts and dependencies.

---

## Summary

- **Total Tasks**: 110
- **User Story 1**: 33 tasks (foundation: utilities, hero, footer, landing page)
- **User Story 2**: 8 tasks (feature cards polish and E2E tests)
- **User Story 3**: 31 tasks (recent pets section with dynamic data)
- **User Story 4**: 17 tasks (footer functional navigation)
- **Setup + Foundational**: 11 tasks
- **Polish**: 10 tasks

**Parallel Opportunities**: 
- Within foundational phase: 6 parallel tasks
- Within each user story: tests, utilities, CSS files can be parallelized
- Limitation: User Stories 2-4 depend on User Story 1 completion

**Independent Test Criteria**:
- **US1**: Navigate to `/` → Verify hero section (heading, 4 feature cards) and footer (branding, links, contact) display
- **US2**: Navigate to `/` → Verify 4 feature cards display in correct order with correct colors (not clickable)
- **US3**: Navigate to `/` → Verify recent pets section displays up to 5 MISSING pets with "View all" link
- **US4**: Navigate to `/` → Verify footer quick links navigate correctly ("Report Lost Pet" functional, others placeholders)

**MVP Scope**: User Story 1 + User Story 2 (landing page with static content: hero section with feature cards and footer)

**Format Validation**: ✅ All tasks follow checklist format: `- [ ] [TaskID] [P?] [Story?] Description with file path`

