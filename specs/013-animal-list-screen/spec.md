# Feature Specification: Animal List Screen Layout Update (Android)

**Feature Branch**: `013-animal-list-screen`  
**Created**: 2025-11-24  
**Updated**: 2025-11-25  
**Status**: Draft  
**Figma Design**: [Missing animals list app](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-7556&m=dev)

> **Platform Scope**: This specification applies **only** to the Android app's Animal List screen (`AnimalListScreen` in the Compose UI layer). iOS and Web implementations MUST remain visually and behaviourally unchanged by this feature.

## Design Summary

The new Figma design ("Missing animals list app" frame, node-id=297-7556) introduces a refreshed visual layout for the Android Animal List screen:

### Screen Header
- App title "PetSpot" displayed left-aligned at the top
- Typography: Hind font, 32px, rgba(0,0,0,0.8)
- No back navigation, search, or filter controls (out of scope)

### Animal Card Layout
Each animal card (100px height, 14dp corner radius, 1px border #E5E9EC) displays:
- **Left section**: 64px circular pet photo with fallback placeholder
- **Middle section** (two rows):
  - Row 1: Location icon + location name + "•" separator + distance (gray, 13sp, system default sans-serif)
  - Row 2: Species + "•" separator + Breed (dark text #101828, 14sp, system default sans-serif)
- **Right section** (vertically stacked):
  - Status badge ("MISSING" red #FF0000, "FOUND" blue #155DFC) - pill-shaped, 13sp white text, system default sans-serif
  - Date below badge (gray #6A7282, 14sp, system default sans-serif)

### Primary Action Button
- Floating button at bottom: "Report a Missing Animal" with icon
- Blue background (#155DFC), white text (Hind 14px)
- 22dp corner radius, pill-shaped
- Horizontal padding 21px, shadow (0px 4px 4px rgba(0,0,0,0.1))

## User Scenarios & Testing *(mandatory)*

### User Story 1 - View updated list of missing animals (Priority: P1)

**As a** Android app user  
**I can** see an updated list of animals on the main screen, presented according to the latest Figma design,  
**So that** I can quickly understand which animals are missing/found and scan key details at a glance.

**Why this priority**: This is the primary purpose of the screen and the first thing users see when they open the app. It must clearly present information in a way that matches the agreed design and supports future features like details, search and filters.

**Independent Test**: Can be fully tested by launching the app to the Animal List screen and visually confirming that:
- The screen title "PetSpot" is displayed left-aligned with correct typography
- Each animal card shows: circular photo, location with distance, species/breed, status badge, and date
- The card layout matches the Figma design (photo left, info middle, status/date right)
- The list is scrollable when there are more animals than fit on screen

**Acceptance Scenarios**:

1. **Given** the user opens the app and lands on the Animal List screen,  
   **When** animals are available to display,  
   **Then** a vertically scrollable list of animal cards is shown with: "PetSpot" title, cards showing photo, location row, species/breed row, status badge, and date.
2. **Given** there are more animals than fit on one screen,  
   **When** the user scrolls down,  
   **Then** additional animal cards appear and scrolling stops at the last available card.
3. **Given** an animal card is visible in the list,  
   **When** the user quickly scans the card,  
   **Then** they can see: pet photo (or placeholder), location + distance, species + breed, colored status badge (MISSING/FOUND), and date.

---

### User Story 2 - Access primary call-to-action (Priority: P2)

**As a** Android app user  
**I can** always access a clear primary button to report a missing animal from the Animal List screen,  
**So that** I know how to start the process of reporting a missing pet at any time.

**Why this priority**: Reporting missing animals is a core purpose of the app. The main call-to-action must remain easy to find and use, while still respecting the new layout.

**Independent Test**: Can be fully tested by verifying that the primary button:
- Is visible as a floating pill-shaped button at the bottom of the screen
- Displays "Report a Missing Animal" text with an icon
- Uses blue background (#155DFC) and white text
- Can be tapped to initiate the "report missing animal" flow

**Acceptance Scenarios**:

1. **Given** the Animal List screen is displayed,  
   **When** the user looks at the bottom area of the content,  
   **Then** a floating blue pill-shaped button labelled "Report a Missing Animal" with an icon is visible.
2. **Given** the user scrolls the list of animals up and down,  
   **When** they reach any scroll position,  
   **Then** the primary "Report a Missing Animal" button remains visible at the bottom (floating position).
3. **Given** the primary button is visible,  
   **When** the user taps it,  
   **Then** the app starts the "report missing animal" flow, consistent with existing specifications.

---

### User Story 3 - Preserve behaviour while refreshing visuals (Priority: P3)

**As a** Android app user  
**I can** continue to use the Animal List screen in all its existing states (loaded, empty, error, loading)  
**So that** I benefit from the new visual design without losing any behaviour I already rely on.

**Why this priority**: The current logic and edge-case handling are already implemented. The goal of this story is to refresh the visual layer while keeping behaviour unchanged.

**Independent Test**: Can be independently tested by exercising the existing flows that lead to populated, empty, loading, and error states and confirming that:
- The same messages and actions are still available as before
- Only the visual styling has changed to match the new design

**Acceptance Scenarios**:

1. **Given** any existing state of the Animal List screen is triggered (populated list, empty state, or error state),  
   **When** that state is displayed after the UI refresh,  
   **Then** the same information and actions remain available to the user, with visual presentation updated to the new design.
2. **Given** an existing automated or manual test that exercises error or empty-state behaviour,  
   **When** those tests are run after the UI changes,  
   **Then** they still pass without modification to expectations about behaviour.

---

### Edge Cases

- **No animals returned**: Existing behaviour for the empty state remains unchanged; only visual presentation adjusts to the new paddings and typography.
- **Slow or flaky network**: Existing loading indicators and transitions remain unchanged in logic.
- **Large number of animals**: Card design and list spacing MUST remain visually consistent and free of clipping or overlapping.
- **Repeated taps on primary button**: Existing handling remains unchanged.
- **Out-of-scope visual controls**: Back navigation, List/Map tab switcher, and Search & Filters from the design are **not** implemented.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The Android `AnimalListScreen` MUST display "PetSpot" as the screen title, left-aligned, using Hind font at 32px with color rgba(0,0,0,0.8).
- **FR-002**: Each animal card MUST have 14dp corner radius, 1px solid border (#E5E9EC), 100px height, and white background.
- **FR-003**: Each card MUST display a 64px circular pet photo on the left with fallback to a placeholder initial.
- **FR-004**: Each card MUST display location information as: location icon + location name + "•" + distance (e.g., "Central Park • 2.5 km") in gray (#4A5565), 13sp, system default sans-serif.
- **FR-005**: Each card MUST display species/breed as: Species + "•" + Breed (e.g., "Dog • Golden Retriever") in dark (#101828), 14sp, system default sans-serif.
- **FR-006**: Each card MUST display a status badge on the right: "MISSING" (red #FF0000) or "FOUND" (blue #155DFC), pill-shaped, white text, 13sp, system default sans-serif.
- **FR-007**: Each card MUST display the date below the status badge, right-aligned, gray (#6A7282), 14sp, system default sans-serif.
- **FR-008**: The primary button MUST be a floating pill-shaped button at the bottom with:
  - Text: "Report a Missing Animal" with trailing icon (custom drawable exported from Figma design)
  - Background: Blue (#155DFC)
  - Corner radius: 22dp
  - Shadow: 0px 4px 4px rgba(0,0,0,0.1)
- **FR-009**: When the user taps the primary button, the existing "report missing animal" flow MUST be triggered without changes.
- **FR-010**: Existing empty, loading, and error states MUST continue to function; changes are limited to visual styling.
- **FR-011**: iOS and Web implementations MUST remain unaffected.

### Key Entities

- **Animal (list view)**: Represents an animal case as shown on the list screen:
  - **Photo URL**: URL to the pet's photo (falls back to initial-based placeholder).
  - **Location**: Human-readable location name (e.g., "Central Park").
  - **Distance**: Distance from user (e.g., "2.5 km").
  - **Species**: Type of animal (e.g., "Dog", "Cat").
  - **Breed**: Breed description (e.g., "Golden Retriever").
  - **Status**: Current status: "MISSING" or "FOUND".
  - **Date**: Date of the report/last seen (e.g., "18/11/2025").

### Dependencies & Assumptions

- **Design dependency**: The Figma frame "Missing animals list app" (node-id=297-7556) is the single source of truth for visual layout.
- **Existing behaviour**: This feature assumes existing behaviour for navigation and data loading.
- **Platform scope**: Only Android implementation is in scope.
- **Future work**: Search, filters, and List/Map toggle remain future work.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Visual review confirms the Animal List screen matches the Figma design for title, cards, and button.
- **SC-002**: QA can verify all core states (loading, populated, empty, error) display correctly.
- **SC-003**: No regressions in existing navigation flows.
- **SC-004**: At least 90% of test participants can identify the primary action within 5 seconds.

## Clarifications

### Session 2025-11-25 (Design Update)

- Q: What is the exact layout of information on the card? → A: Left: 64px circular photo. Middle: location row (icon + name + • + distance), species/breed row (species + • + breed). Right: status badge (top), date (bottom).
- Q: What colors are used for status badges? → A: MISSING = red (#FF0000), FOUND = blue (#155DFC).
- Q: What font is used for the title? → A: Hind Regular, 32px, rgba(0,0,0,0.8).
- Q: Should the button be fixed or floating? → A: Floating at bottom, pill-shaped with shadow.

### Session 2025-11-24

- Q: When creating new animal card composables and the primary button, should each card instance use a unique testTag per animal or a single generic testTag for all cards? → A: Use `animalList.cardItem` for cards, and `animalList.reportButton` for the primary button.
- Q: When the Animal List screen is in loading, empty, or error states, should the primary button remain visible/enabled? → A: Follow current implementation; preserve existing button visibility.

### Testing & Test Identifiers

- **Animal card list item**: `Modifier.testTag("animalList.cardItem")` on each card.
- **Primary button**: `Modifier.testTag("animalList.reportButton")`.
