# Feature Specification: Android Fullscreen Interactive Map

**Feature Branch**: `071-android-fullscreen-map`  
**Created**: 2026-01-08  
**Status**: Draft  
**Platform**: Android  
**Jira Ticket**: [KAN-35](https://ai-first-intive.atlassian.net/browse/KAN-35) - Android Fullscreen Interactive Map  
**Parent Ticket**: [KAN-22](https://ai-first-intive.atlassian.net/browse/KAN-22) - Map View of All Lost Pets on Mobile  
**Base Spec**: 066-mobile-map-interactive  
**Related**: 067-android-landing-map-preview (prerequisite - landing page map preview)  
**Constitution**: `.specify/memory/constitution-android.md` - Android MVI Architecture, Koin DI, Composable Patterns  
**Figma**: [Interactive Map](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=1071-4268)  
**Input**: Android fullscreen interactive map implementation based on generic spec 066, as a subtask of KAN-22. Includes zoom/pan functionality, pins for missing animals, details pop-up on pin tap, and back navigation to landing page.

## Scope Definition

### In Scope
- Fullscreen interactive map opened from landing page preview
- Zoom and pan functionality
- Pins for missing (red) and found (blue) pets displayed on the map
- Pet details pop-up when tapping a pin
- Back arrow navigation to return to landing page
- Loading and error states for pin data
- Location-based initial viewport (centered on user location)

### Out of Scope
- Static map preview on landing page (covered by 067-android-landing-map-preview)
- Map marker clustering logic
- Offline map support
- Turn-by-turn navigation to pet location
- Multiple map styles/themes
- Pet search/filtering within fullscreen map

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Open Fullscreen Interactive Map (Priority: P1)

A user on the landing page sees the static map preview and wants to explore the area in more detail. They tap the preview to open a fullscreen interactive map experience, then return to the landing page when done.

**Why this priority**: This is the entry point for all interactive map functionality. Without reliable fullscreen open/close flow, no other map features can be used effectively.

**Independent Test**: Can be tested by tapping the landing-page map preview and verifying fullscreen map opens; tapping back arrow closes it and returns to landing page with state preserved.

**Acceptance Scenarios**:

1. **Given** the landing page is displayed with the map preview visible, **When** the user taps the map preview, **Then** the fullscreen interactive map opens
2. **Given** the fullscreen interactive map is open, **When** the user taps the back arrow in the top-left corner, **Then** the fullscreen map closes and the landing page is displayed
3. **Given** the user opened fullscreen map from landing page, **When** the user returns to landing page via back arrow, **Then** the landing page scroll position and content remain intact
4. **Given** the fullscreen map is open, **When** the user presses the Android system back button, **Then** the fullscreen map closes and the landing page is displayed

---

### User Story 2 - Navigate the Fullscreen Map (Zoom + Pan) (Priority: P2)

A user wants to explore the map beyond their immediate location. They use pinch-to-zoom and drag gestures to navigate to other areas and see different zoom levels.

**Why this priority**: Navigation is fundamental to map usability; without zoom and pan, the fullscreen map offers no advantage over the static preview.

**Independent Test**: Can be tested by opening fullscreen map and using zoom/pan gestures; verify the visible area updates accordingly.

**Acceptance Scenarios**:

1. **Given** the fullscreen map is open, **When** the user pinches to zoom in, **Then** the map zoom level increases and shows a smaller area with more detail
2. **Given** the fullscreen map is open, **When** the user pinches to zoom out, **Then** the map zoom level decreases and shows a larger area
3. **Given** the fullscreen map is open, **When** the user drags the map, **Then** the visible map area pans in the drag direction
4. **Given** the fullscreen map is open, **When** the user double-taps the map, **Then** the map zooms in at the tap location

---

### User Story 3 - View Pet Pins on the Map (Priority: P3)

A user wants to see where lost and found pets have been reported. Pins are displayed on the map showing the locations of all pet announcements within the visible area.

**Why this priority**: Pins represent the core value - connecting users with pet reports in their area. Without pins, the map is just a background.

**Independent Test**: Can be tested by opening fullscreen map in an area with pet announcements and verifying pins appear at correct locations.

**Acceptance Scenarios**:

1. **Given** the fullscreen map is open, **When** missing pet announcements exist in the visible area, **Then** red pins are displayed at each missing pet's last-seen location
2. **Given** the fullscreen map is open, **When** found pet announcements exist in the visible area, **Then** blue pins are displayed at each found pet's location
3. **Given** the fullscreen map is open, **When** no pet announcements exist in the visible area, **Then** the map is shown without pins (no empty-state message)
4. **Given** the user changes the visible area (pan/zoom), **When** the gesture completes, **Then** pins update to reflect pet announcements in the new visible area
5. **Given** pins are loading, **When** the map is displayed, **Then** a loading indicator is shown over the map

---

### User Story 4 - View Pet Details from a Pin (Priority: P4)

A user sees a pin on the map and wants to know more about the pet. They tap the pin to view details about the pet and how to contact the owner.

**Why this priority**: Details make the map actionable; users need context and contact information to help reunite pets with owners.

**Independent Test**: Can be tested by tapping a pin and verifying a details pop-up appears with required pet information.

**Acceptance Scenarios**:

1. **Given** pins are visible on the map, **When** the user taps a pin, **Then** a details pop-up appears for the selected pet
2. **Given** a details pop-up is shown, **When** the user views it, **Then** it displays: pet photo, pet name, species, last-seen date, description, and owner contact information
3. **Given** a details pop-up is shown, **When** the user taps outside the pop-up or swipes it away, **Then** the pop-up dismisses and the map remains visible
4. **Given** a details pop-up is shown, **When** the user taps a different pin, **Then** the pop-up updates to show the newly selected pet's details
5. **Given** the pet photo fails to load, **When** the pop-up is displayed, **Then** a placeholder image is shown instead

### Edge Cases

- **Failed pins load**: Show an error state with "Unable to load pets" message and a "Retry" button; map background remains visible
- **Slow network**: Show a loading indicator (shimmer or spinner) overlay while pins are being fetched
- **Invalid coordinates**: Announcements with missing or invalid coordinates are not shown as pins (filtered silently)
- **High pin density**: Pins remain tappable even when close together; no clustering in this version
- **Duplicate coordinates**: If multiple pets have the same coordinates, tapping shows one pet at a time; user can dismiss and tap again to see others
- **Location permission denied**: Open fullscreen map centered on a default location (city center or last known position)
- **Data refresh on return**: When returning to fullscreen map from background, pin data is refreshed automatically

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The application MUST open fullscreen interactive map when the user taps the landing-page map preview
- **FR-002**: The fullscreen map MUST include a back arrow button in the top-left corner
- **FR-003**: Tapping the back arrow MUST close fullscreen mode and return to the landing page
- **FR-004**: The Android system back button MUST close fullscreen mode and return to the landing page
- **FR-005**: The fullscreen map MUST support pinch-to-zoom gestures
- **FR-006**: The fullscreen map MUST support pan (drag) gestures
- **FR-007**: The fullscreen map MUST support double-tap to zoom in
- **FR-008**: The fullscreen map MUST display red pins for missing pet announcements in the visible area
- **FR-009**: The fullscreen map MUST display blue pins for found pet announcements in the visible area
- **FR-010**: Pins MUST be positioned using each pet's last-seen/found coordinates
- **FR-011**: When the visible map area changes, pins MUST update after the gesture completes
- **FR-012**: Tapping a pin MUST show a details pop-up for that pet
- **FR-013**: The details pop-up MUST include: pet photo, pet name, species, last-seen/found date, description, and owner contact information (phone and email when available)
- **FR-014**: The details pop-up MUST be dismissible by tapping outside or swiping
- **FR-015**: If the pet photo fails to load, the pop-up MUST display a placeholder image
- **FR-016**: While pins are loading, a loading indicator MUST be displayed
- **FR-017**: If loading pins fails, an error state with "Retry" button MUST be displayed
- **FR-018**: The "Retry" button MUST re-attempt loading pins for the current viewport
- **FR-019**: The fullscreen map MUST initially center on the user's current location (if permission granted)
- **FR-020**: If location permission is not granted, the map MUST center on a default location

### Key Entities

- **Fullscreen Interactive Map**: A full-window map experience that supports zoom, pan, and pin interaction; closed via back arrow or system back button
- **Pet Pin**: A visual marker on the map indicating the location of a pet announcement (red for missing, blue for found)
- **Pet Details Pop-up**: A modal overlay that shows pet details and owner contact information after selecting a pin

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can open fullscreen map within 2 seconds of tapping the preview under normal network conditions
- **SC-002**: Users can zoom and pan the map without perceivable UI freezing during interaction
- **SC-003**: Users can see pins within 3 seconds of map becoming visible under normal network conditions
- **SC-004**: Users can view pet details within 1 second of tapping a pin
- **SC-005**: 95% of users successfully complete the open-explore-close flow without errors

## Architectural Constraints (from Constitution)

The following constraints are mandated by the PetSpot Android Constitution and MUST be followed during implementation:

### MVI Architecture (NON-NEGOTIABLE)
- Screen MUST follow Model-View-Intent pattern with unidirectional data flow
- Required components: `UiState` (immutable), `UserIntent` (sealed class), `UiEffect` (one-off events), `Reducer` (pure function)
- ViewModel exposes `state: StateFlow<UiState>`, `effects: SharedFlow<UiEffect>`, and `dispatchIntent()`
- UI collects state via `collectAsStateWithLifecycle()` and renders purely from `UiState`

### Two-Layer Composable Pattern (NON-NEGOTIABLE)
1. **State Host Composable** (e.g., `FullscreenMapScreen`): Collects state from ViewModel, observes effects, dispatches intents, contains NO UI logic
2. **Stateless Composable** (e.g., `FullscreenMapContent`): Accepts `UiState` as parameter, callback lambdas for interactions, contains ALL UI logic, NO ViewModel dependency

### Preview Requirements (MANDATORY)
- Stateless composable MUST have at least one `@Preview` function
- Preview data MUST use `@PreviewParameter` with `PreviewParameterProvider<UiState>`
- Provider MUST include loading, success with pins, error, and pop-up visible states

### Navigation Requirements
- MUST use Jetpack Navigation Component for fullscreen map navigation
- Back navigation MUST use `UiEffect.NavigateBack` pattern, not direct `NavController` calls in ViewModel
- System back button handling MUST be coordinated with Navigation Component

### Dependency Injection (NON-NEGOTIABLE)
- MUST use Koin for dependency injection
- ViewModel MUST be injected via `koinViewModel()`
- Repository and use cases MUST be defined in Koin modules

### Test Identifiers (NON-NEGOTIABLE)
- Map container: `fullscreenMap.container`
- Back button: `fullscreenMap.backButton`
- Loading indicator: `fullscreenMap.loading`
- Error state: `fullscreenMap.error`
- Retry button: `fullscreenMap.retryButton`
- Pin (dynamic): `fullscreenMap.pin.${petId}`
- Pop-up: `fullscreenMap.petPopup`
- Pop-up close: `fullscreenMap.petPopup.close`

## Assumptions

- Landing page map preview (spec 067) exists and provides the entry point to fullscreen mode
- Backend API provides pet announcements with coordinates via existing announcements endpoint with location filter
- Google Maps SDK will be used for interactive map rendering
- Pin icons/assets for missing (red) and found (blue) states are available or will be provided
- Pet data includes: id, name, species, photo URL, description, last-seen/found date, owner phone, owner email
- Location permission is already requested on landing page (spec 067); fullscreen map uses the result

## Clarifications

### Session 2026-01-08

- Q: When should pins refresh in fullscreen mode? → A: After the user finishes zoom/pan gesture, not during the gesture
- Q: What happens when no pins exist in visible area? → A: Show map without pins, no empty-state message needed
- Q: How to handle overlapping pins? → A: No clustering for now; pins remain individually tappable even when overlapping
- Q: What if user taps pin while pop-up is open? → A: Pop-up updates to show the newly tapped pet
- Q: Default location if permission denied? → A: Use last known location if available, otherwise city center (configurable)

## Design References

### Visual Specifications (from KAN-22 attachments)

- **Back arrow**: Standard Material Design back arrow, white with shadow for visibility over map
- **Pin colors**: Red (#FB2C36) for missing, Blue (#2B7FFF) for found
- **Pop-up**: Bottom sheet style, rounded corners, white background, max 60% screen height
- **Loading**: Circular progress indicator centered over map
- **Error state**: Card with error icon, message, and "Retry" button

## Estimation *(mandatory)*

### Initial Estimate

- **Story Points**: 5
- **Initial Budget**: 5 × 4 × 1.3 = 26 days
- **Confidence**: ±50%
- **Anchor Comparison**: More complex than Pet Details (3 SP) because: interactive map integration, gesture handling, dynamic pin updates, pop-up state management, and location-based viewport

### Re-Estimation (Updated After Each Phase)

| Phase       | SP | Days | Confidence | Key Discovery                          |
| ----------- | -- | ---- | ---------- | -------------------------------------- |
| Initial     | 5  | 26   | ±50%       | Interactive map with pop-ups           |
| After SPEC  | 5  | 26   | ±30%       | Scope aligned with 066 base spec       |
| After PLAN  | —  | —    | ±20%       | [Update when plan.md complete]         |
| After TASKS | —  | —    | ±15%       | [Update when tasks.md complete]        |

### Per-Platform Breakdown (After TASKS)

| Platform    | Tasks | Days | Notes                    |
| ----------- | ----- | ---- | ------------------------ |
| Backend     | —     | —    | Existing API (no change) |
| iOS         | —     | —    | N/A (Android only spec)  |
| Android     | —     | —    | [Fill after tasks.md]    |
| Web         | —     | —    | N/A (Android only spec)  |
| **Total**   |       | **—**|                          |

### Variance Tracking

| Metric             | Initial | Final | Variance |
| ------------------ | ------- | ----- | -------- |
| **Story Points**   | 5 SP    | — SP  | —        |
| **Budget (days)**  | 26 days | — days| —        |

**Variance Reasons**: [To be filled after implementation]

**Learning for Future Estimates**: [To be filled after implementation]
