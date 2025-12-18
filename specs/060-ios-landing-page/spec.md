# Feature Specification: iOS Landing Page - Top Panel

**Feature Branch**: `060-ios-landing-page`  
**Created**: 2025-12-15  
**Status**: Draft  
**Dependencies**: 054-ios-tab-navigation, 058-ios-landing-page-list  
**Figma Design**: [PetSpot wireframes - Landing page](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=974-4667&m=dev)  
**Input**: User description: "mamy zrobioną listę, tego nie ruszamy. Mamy zaimplementowane klikanie w listę i przejście do szczegółów. Chodzi o dorobienie górnego panelu z tym co jest ponad listą na designie."

## Scope

### In Scope

- Add UI **above the existing Home list** (as in Figma node `974:4667`):
  - **Hero panel** with title and two primary actions: **Lost Pet** and **Found Pet**
  - **List section header row** above the list: title “Recent Reports” + action “View All”

### Out of Scope (MUST NOT change)

- The existing Home list and its behavior (loading, empty/error states, “View all”, item tap, navigation to details).
- Backend/API behavior and list sorting/filtering logic already implemented in `058-ios-landing-page-list`.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - See top panel on Home (Priority: P1)

When a user opens the app and lands on the Home tab, they immediately see the top panel above the existing list, so they understand the screen context and have clear “Lost Pet” / “Found Pet” entry points.

**Why this priority**: This is the first impression users get of the Home tab. Without a top panel, the screen feels incomplete and users don’t have clear primary actions aligned with the product’s core flows.

**Independent Test**: Can be tested by opening the app (or switching to Home tab) and verifying the header + hero panel render above the list without affecting list behavior.

**Acceptance Scenarios**:

1. **Given** the Home tab is displayed, **When** the user looks above the list, **Then** the hero section shows the title “Find Your Pet” and two buttons “Lost Pet” and “Found Pet”
2. **Given** the Home tab is displayed, **When** the user looks at the row directly above the list, **Then** they see a section title “Recent Reports” and a “View All” action
3. **Given** the Home list is already implemented, **When** the top UI is added, **Then** the list content and behavior remain unchanged (no regressions in loading, item tap, and navigation to details)

---

### User Story 2 - Use top panel actions to reach core flows (Priority: P1)

Users can tap **Lost Pet** or **Found Pet** from the hero panel to quickly reach the relevant tab/flow, without scrolling or manually finding navigation.

**Why this priority**: These are the primary user intents on this screen and should be accessible in one tap.

**Independent Test**: Can be tested by tapping each button and verifying the app navigates to the correct tab without changing the Home list state/behavior.

**Acceptance Scenarios**:

1. **Given** the Home tab is displayed, **When** the user taps “Lost Pet”, **Then** the app navigates to the Lost Pet tab
2. **Given** the Home tab is displayed, **When** the user taps “Found Pet”, **Then** the app navigates to the Found Pet tab
3. **Given** the user navigates to Lost/Found via the hero buttons, **When** they tap back to Home tab, **Then** the Home screen shows the same list state and behavior as before (no reset beyond what the existing implementation does)
4. **Given** the Home tab is displayed, **When** the user taps “View All”, **Then** the app navigates to the full announcements list screen (existing implementation)

---

### Edge Cases

- **Small screens / dynamic type**: Title and buttons remain visible above the list without overlapping; content should wrap/truncate gracefully.
- **Layout priority when space is tight**: The list is scrollable, so the UI should reduce the visible list area first (keeping the hero + “Recent Reports / View All” row visible) rather than shrinking the hero controls into an unusable size. The top panel stays pinned while the list scrolls.
- **Safe area**: Header must not collide with the iOS status bar / notch.
- **Button tap spam**: Rapid repeated taps should not crash or create inconsistent navigation state (idempotent tab switch).
- **View All availability**: “View All” is always visible and tappable; if the destination is unavailable for any reason, show existing navigation error handling (no crash).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: iOS MUST display the Home screen with the top panel above the existing list (no changes to list behavior).
- **FR-002**: The top panel MUST include a hero section with title text “Find Your Pet”.
- **FR-003**: The hero section MUST include two primary buttons: “Lost Pet” and “Found Pet”.
- **FR-004**: Tapping “Lost Pet” MUST navigate the user to the Lost Pet tab (tab switch only; no report form is opened automatically).
- **FR-005**: Tapping “Found Pet” MUST navigate the user to the Found Pet tab (tab switch only; no report form is opened automatically).
- **FR-006**: The row directly above the list MUST show:
  - title text “Recent Reports”
  - action “View All”
- **FR-007**: Tapping “View All” MUST navigate to the full announcements list screen (existing implementation) by switching to the Lost Pet tab root list.
- **FR-008**: The top panel and list header row MUST match the information hierarchy and placement shown in the Figma node `974:4667` (hero above the list header row, list header row directly above the list).
- **FR-008a**: When vertical space is constrained, the visible **list** area MUST shrink first (it remains scrollable) while keeping the hero controls and the “Recent Reports / View All” row visible and tappable.
- **FR-008b**: The top panel (hero + “Recent Reports / View All” row) MUST remain pinned above the list; only the existing list content scrolls.
- **FR-009**: The new UI MUST be accessible:
  - elements have VoiceOver-friendly labels
  - focus order is title → Lost Pet → Found Pet → Recent Reports → View All → (then existing list)
- **FR-010**: Provide deterministic test identifiers for the new elements:
  - `.accessibilityIdentifier("home.hero.title")`
  - `.accessibilityIdentifier("home.hero.lostPetButton")`
  - `.accessibilityIdentifier("home.hero.foundPetButton")`
  - `.accessibilityIdentifier("home.recentReports.title")`
  - `.accessibilityIdentifier("home.recentReports.viewAll")`

### Key Entities

- **Home Screen**: The Home tab root surface that contains the existing list plus the new top panel above it.
- **Tab Navigation**: Existing navigation system that supports switching to Lost Pet / Found Pet tabs.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: On Home tab, the top panel renders above the list and matches the Figma hierarchy (visual QA pass).
- **SC-002**: “Lost Pet” button navigates to Lost Pet tab in 100% of manual/automated checks.
- **SC-003**: “Found Pet” button navigates to Found Pet tab in 100% of manual/automated checks.
- **SC-004**: “View All” navigates to the full announcements list in 100% of manual/automated checks.
- **SC-005**: Regression check: existing Home list tap-to-details flow continues to work exactly as before (no behavior changes introduced by the top UI).

## Clarifications

### Session 2025-12-17

- Q: What is the scope of changes? → A: Only the top panel above the list from the Figma design; the list and its navigation are already implemented and must not be changed.
- Q: What should the hero buttons do? → A: They should navigate to the corresponding tabs: Lost Pet and Found Pet.
- Q: Do we implement AppHeader from Figma? → A: No, AppHeader is out of scope for this iteration.
- Q: What is missing above the list? → A: The section header row: “Recent Reports” title + “View All” action.
- Q: Should the top panel scroll with the list? → A: No — the top panel stays pinned; only the list scrolls.
- Q: Where should “View All” navigate? → A: Switch to the Lost Pet tab root list (full announcements list screen).
- Q: What should the hero buttons do beyond tab navigation? → A: Only switch tabs (Lost Pet / Found Pet); do not open report forms automatically.

## Assumptions

- **Home list exists**: The Home list (top 5 announcements) and its navigation to details are already implemented (per `058-ios-landing-page-list`) and will remain unchanged.
- **Tab navigation exists**: The iOS app already has tabs for Home, Lost Pet, and Found Pet (per `054-ios-tab-navigation`).
- **View All destination**: “View All” switches to the Lost Pet tab and shows the full announcements list screen there (existing implementation).
