# Feature Specification: Animal List Screen Layout Update (Android)

**Feature Branch**: `013-animal-list-screen`  
**Created**: 2025-11-24  
**Status**: Draft  
**Input**: User description: "I'd like to update the @composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalListScreen.kt according to the new designs: https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=170-6166&m=dev . Back navigation is for now ignored. List/Map tab switcher is also out of context. Search & filters button too."

> **Platform Scope**: This specification applies **only** to the Android app’s Animal List screen (`AnimalListScreen` in the Compose UI layer). iOS and Web implementations MUST remain visually and behaviourally unchanged by this feature.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - View updated list of missing animals (Priority: P1)

**As a** Android app user  
**I can** see an updated list of missing animals on the main screen, presented according to the latest Figma design,  
**So that** I can quickly understand which animals are missing and scan key details at a glance.

**Why this priority**: This is the primary purpose of the screen and the first thing users see when they open the app. It must clearly present information in a way that matches the agreed design and supports future features like details, search and filters.

**Independent Test**: Can be fully tested by launching the app to the Animal List screen and visually confirming that:
- The screen title and list layout match the updated Figma design for the mobile "Missing animals list" frame  
- Each animal card shows the expected key information (name, location, species/breed, status, last seen date)  
- The list is scrollable when there are more animals than fit on screen

**Acceptance Scenarios**:

1. **Given** the user opens the app and lands on the Animal List screen,  
   **When** animals are available to display,  
   **Then** a vertically scrollable list of animal cards is shown, using the updated Figma layout (spacing, grouping of fields, and visual hierarchy).
2. **Given** there are more animals than fit on one screen,  
   **When** the user scrolls down,  
   **Then** additional animal cards appear and scrolling stops at the last available card.
3. **Given** an animal card is visible in the list,  
   **When** the user quickly scans the card,  
   **Then** they can clearly see the animal’s name, location, species/breed information, status, and last seen date without needing to open another screen.

---

### User Story 2 - Access primary call-to-action (Priority: P2)

**As a** Android app user  
**I can** always access a clear primary button to report a missing animal from the Animal List screen,  
**So that** I know how to start the process of reporting a missing pet at any time.

**Why this priority**: Reporting missing animals is a core purpose of the app. The main call-to-action must remain easy to find and use, while still respecting the new layout.

**Independent Test**: Can be fully tested by verifying that the primary button:
- Is visible on the screen without scrolling to a specific position  
- Uses the label and styling defined in the updated Figma design  
- Can be tapped to initiate the “report missing animal” flow (or the current placeholder action) without requiring any other navigation changes

**Acceptance Scenarios**:

1. **Given** the Animal List screen is displayed,  
   **When** the user looks at the bottom area of the content,  
   **Then** a primary button labelled “Report a Missing Animal” is visible and clearly distinguished as the main action.
2. **Given** the user scrolls the list of animals up and down,  
   **When** they reach any scroll position,  
   **Then** the primary “Report a Missing Animal” action remains accessible from the screen (no extra navigation required).
3. **Given** the primary button is visible,  
   **When** the user taps it,  
   **Then** the app starts the “report missing animal” flow or current placeholder behaviour, consistent with existing specifications for that flow.

---

### User Story 3 - Preserve behaviour while refreshing visuals (Priority: P3)

**As a** Android app user  
**I can** continue to use the Animal List screen in all its existing states (loaded, empty, error, loading)  
**So that** I benefit from the new visual design without losing any behaviour I already rely on.

**Why this priority**: The current logic and edge-case handling are already implemented. The goal of this story is to refresh the visual layer (cards, primary button, paddings/margins) while keeping behaviour unchanged.

**Independent Test**: Can be independently tested by exercising the existing flows that lead to populated, empty, loading, and error states and confirming that:
- The same messages and actions are still available as before  
- Only the visual styling (layout, spacing, typography, colours) of cards, button, and surrounding padding has changed to match the new design.

**Acceptance Scenarios**:

1. **Given** any existing state of the Animal List screen is triggered (for example, populated list, empty state, or error state),  
   **When** that state is displayed after the UI refresh,  
   **Then** the same information and actions remain available to the user, with only visual presentation updated to the new design.
2. **Given** an existing automated or manual test that exercises error or empty-state behaviour on the Animal List screen,  
   **When** those tests are run after the UI changes,  
   **Then** they still pass without modification to expectations about behaviour (only screenshots or visual references may need updating).

---

### Edge Cases

- **No animals returned**: Existing behaviour for the empty state (message and available actions) remains unchanged; only the visual presentation of the surrounding layout should adjust to the new paddings and typography if needed.
- **Slow or flaky network**: Existing loading indicators and transitions remain unchanged in logic; any new spacing or background changes MUST NOT obscure or remove them.
- **Large number of animals**: When many animals are available (for example, dozens or more), the updated card design and list spacing MUST remain visually consistent and free of clipping or overlapping, while preserving current performance characteristics.
- **Repeated taps on primary button**: Existing handling of repeated taps remains unchanged; the updated button visuals MUST NOT introduce duplicate tap handling or change debouncing logic.
- **Out-of-scope visual controls**: Back navigation icon, List/Map tab switcher, and Search & Filters button from the updated design are **not** introduced or made interactive in this feature; they are treated as out-of-scope elements for both behaviour and implementation.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The Android `AnimalListScreen` MUST present a vertically scrollable list of missing animals using the updated mobile “Missing animals list” layout from Figma, including a clear screen title and visually grouped content.
- **FR-002**: Each animal card in the list MUST display, at minimum, the animal’s name, approximate location, species/breed information, status, and last seen date, using visual styling (typography, spacing, grouping) that follows the updated card design.
- **FR-003**: When there are more animals than fit on a single screen, the list MUST allow vertical scrolling and MUST stop at the last available item, with the updated card styling applied uniformly to all visible and off-screen items.
- **FR-004**: The screen MUST include a clearly identified primary button labelled “Report a Missing Animal”, positioned and styled according to the updated design (colour, shape, paddings, and alignment).
- **FR-005**: When the user taps the primary “Report a Missing Animal” button, the app MUST continue to trigger the existing “report missing animal” flow or placeholder action exactly as before; this feature MUST NOT change the underlying behaviour.
- **FR-006**: Existing empty, loading, and error states on the Android Animal List screen MUST continue to function as they do today; any changes introduced by this feature are limited to harmonising their visual appearance (spacing, background, typography) with the new overall layout.
- **FR-007**: This feature MUST NOT introduce new behaviours for back navigation, List/Map view switching, or Search & Filters; those elements from the updated design remain out of scope and are not implemented or made interactive.
- **FR-008**: The Android Animal List screen MUST continue to function as the primary entry point into the app’s “missing animals” experience, preserving existing navigation flows into and out of the screen.
- **FR-009**: iOS and Web Animal List implementations MUST remain unaffected by this feature; no visual, layout, or behavioural changes are introduced on those platforms as part of this work.

### Key Entities

- **Animal (list view)**: Represents a missing animal as shown on the list screen, focusing on the fields users need to quickly identify a case:
  - **Name**: A short display name for the animal.
  - **Location**: A human-readable area description (for example, city name and nearby area) that helps users understand where the animal was last seen.
  - **Species/Breed**: A concise description of what kind of animal it is (for example, “Dog · Labrador”).
  - **Status**: A label indicating the current status of the case (for example, “Missing”, “Found”, or “Closed”), visually emphasised in the card.
  - **Last Seen Date**: The date the animal was last seen or reported, giving users a sense of recency.

### Dependencies & Assumptions

- **Design dependency**: The updated mobile “Missing animals list” frame from Figma (`https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=170-6166&m=dev`) is the single source of truth for visual layout and hierarchy on the **Android** Animal List screen.
- **Existing behaviour**: This feature assumes existing behaviour for the “Report a Missing Animal” flow and overall navigation on Android, and does not redefine those flows.
- **Platform scope**: Only the Android implementation (`AnimalListScreen` and related Compose UI elements) is in scope. iOS and Web implementations are explicitly out of scope and must not change as a result of this feature.
- **Search and filters**: Search and filters remain future work. The current feature does not require fully implemented search or filter interactions on Android, even if the design shows related controls.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: In usability checks, at least 90% of test participants are able to correctly describe what the screen shows and identify the primary “Report a Missing Animal” action within 5 seconds of seeing the updated Animal List screen.
- **SC-002**: Visual review against the updated Figma design confirms that the Animal List screen matches the intended layout and hierarchy for the title, list, cards, and primary button with no material discrepancies that would confuse users.
- **SC-003**: QA can independently verify all core states of the screen (loading, populated list, empty state, and error with retry) without needing knowledge of internal implementation details.
- **SC-004**: No regressions are observed in existing navigation flows into and out of the Animal List screen; users can still reach this screen as their primary entry point to the missing animals experience, and existing downstream flows (such as reporting) continue to work as before.


