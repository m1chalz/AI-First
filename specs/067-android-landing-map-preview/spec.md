# Feature Specification: Android Landing Page Map Preview Component

**Feature Branch**: `067-android-landing-map-preview`  
**Created**: 2025-12-19  
**Status**: Draft  
**Platform**: Android  
**Jira Ticket**: [KAN-31](https://ai-first-intive.atlassian.net/browse/KAN-31) - Android implementation - Landing Page Map Preview  
**Parent Ticket**: [KAN-22](https://ai-first-intive.atlassian.net/browse/KAN-22) - Map View of All Lost Pets on Mobile  
**Base Spec**: 062-mobile-map-view  
**Constitution**: `.specify/memory/constitution-android.md` - Android MVI Architecture, Koin DI, Composable Patterns  
**Figma**: [Landing Page](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=1071-4251) | [Map Component](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=1071-4268)  
**Input**: Android landing page map preview component - static map preview on landing page showing nearby lost/found pets with location permission handling. Based on KAN-22 and spec 062. Full interactive map is out of scope.

## Scope Definition

### In Scope
- Static map preview component displayed on the landing page
- Map preview showing pins for Missing (red) and Found (blue) pets
- Legend indicating pin colors and their meaning
- "Tap to view interactive map" overlay message (visual only)
- Location permission handling with consent UI

### Out of Scope
- Full-screen interactive map view (separate specification)
- Tap action / navigation to interactive map (will be implemented in full map spec)
- Zoom/pan functionality
- Pet detail pop-ups when tapping pins
- Map marker clustering logic

## User Scenarios & Testing *(mandatory)*

### User Story 1 - View Map Preview on Landing Page (Priority: P1)

A user opens the landing page on their Android device and sees a map preview section between the hero/description area and the Recent Reports list. The map preview shows a static image of the area around the user's location with pins indicating where missing and found pets were last seen.

**Why this priority**: This is the core value - providing immediate visual context about nearby pet reports without requiring additional navigation.

**Independent Test**: Can be tested by opening the landing page with location permission granted and verifying that a map preview appears in the correct position with visible pins.

**Acceptance Scenarios**:

1. **Given** the landing page is displayed and location permission is granted, **When** the page loads, **Then** a map preview section is displayed between the hero section and Recent Reports
2. **Given** the map preview is visible, **When** the user views it, **Then** a "Map View" header is displayed above the map
3. **Given** the map preview is visible, **When** the user views it, **Then** a legend shows red dot labeled "Missing" and blue dot labeled "Found"
4. **Given** the user has granted location permission, **When** the map preview loads, **Then** it displays a 10 km radius area centered on the user's current location
5. **Given** missing pet announcements exist within the preview area, **When** the map is shown, **Then** red pins are displayed at the last-seen locations
6. **Given** found pet announcements exist within the preview area, **When** the map is shown, **Then** blue pins are displayed at the found locations
7. **Given** the map preview is displayed, **When** the user views the center area, **Then** a white pill-shaped overlay displays "Tap to view interactive map"

---

### User Story 2 - Grant Location Permission (Priority: P2)

A user who has not granted location permission sees an informational state in the map preview area explaining that location access is required, with an option to grant permission.

**Why this priority**: Without location permission, the map cannot be centered on the user's location, reducing feature value significantly.

**Independent Test**: Can be tested by opening the landing page without location permission and verifying the consent UI appears.

**Acceptance Scenarios**:

1. **Given** the user has not granted location permission, **When** the landing page loads, **Then** the map preview area displays a permission request state instead of the map
2. **Given** the permission request state is displayed, **When** the user views it, **Then** it shows a message explaining that location is required to display nearby pets
3. **Given** the permission request state is displayed, **When** the user views it, **Then** it shows an "Enable Location" button
4. **Given** the permission request state is displayed, **When** the user taps "Enable Location", **Then** the Android system permission dialog is shown
5. **Given** the user grants location permission from the system dialog, **When** they return to the app, **Then** the map preview is displayed without requiring an app restart
6. **Given** the user denies location permission, **When** they return to the app, **Then** the permission request state remains displayed

### Edge Cases

- **Location services disabled on device**: Treat as "permission not granted" - show the permission request state with guidance to enable location services
- **Slow network**: Show a loading indicator (shimmer or spinner) while the map preview is loading
- **Map preview load failure**: Show an error state with "Unable to load map" message and a "Retry" button
- **No announcements in area**: Show the map preview without pins (no special empty state)
- **Many overlapping pins**: Display all pins within the 10km radius; no limit on pin count; visual overlap is acceptable for the static preview
- **Location obtained after initial load**: If permission is granted mid-session, refresh the map preview without requiring page reload
- **Data refresh**: Pin data is fetched only on landing page entry; no auto-refresh while the page is visible

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The landing page MUST display a "Map View" section between the hero/description section and the "Recent Reports" section
- **FR-002**: The map view section MUST include a header titled "Map View"
- **FR-003**: The map view section MUST include a legend with a red dot labeled "Missing" and a blue dot labeled "Found"
- **FR-004**: The map preview MUST display a static map image (non-interactive within the preview itself)
- **FR-005**: When location permission is granted, the map preview MUST display an area covering approximately 10 km radius around the user's current location
- **FR-006**: The map preview MUST display red pins at locations of missing pet announcements within the visible area
- **FR-007**: The map preview MUST display blue pins at locations of found pet announcements within the visible area
- **FR-008**: The map preview MUST display a centered overlay with the text "Tap to view interactive map"
- **FR-009**: The overlay MUST be styled as a white pill-shaped container with shadow
- **FR-010**: If location permission is not granted, the map preview area MUST display a permission request state
- **FR-011**: The permission request state MUST include informational text explaining why location is needed
- **FR-012**: The permission request state MUST include an "Enable Location" button that triggers the Android `ACCESS_COARSE_LOCATION` permission dialog
- **FR-013**: After granting permission, the map preview MUST refresh automatically without requiring app restart
- **FR-014**: While the map preview is loading, a loading indicator MUST be displayed
- **FR-015**: If map preview loading fails, an error state with a "Retry" button MUST be displayed
- **FR-016**: The map preview container MUST have a subtle border matching the design (light gray, 0.667px)

### Key Entities

- **Map Preview**: A static image representation of a geographic area with pet announcement pins, displayed on the landing page
- **Pet Pin**: A visual marker on the map indicating the location of a pet announcement (red for missing, blue for found)
- **Permission State**: The user's current location permission status (granted, denied, or not yet requested)

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can see the map preview within 2 seconds of landing page load under normal network conditions (3G or better)
- **SC-002**: The permission grant flow completes successfully in 90% of cases without app crash or navigation issues
- **SC-003**: Map preview correctly displays pins within 10 km radius with 99% accuracy when compared to backend data

## Clarifications

### Session 2025-12-19

- Q: Which Android location permission should be requested? → A: `ACCESS_COARSE_LOCATION` (city-block level accuracy)
- Q: How does the map preview get pet announcement data? → A: Use existing announcements API with location filter
- Q: Which map SDK will be used for rendering the static preview? → A: Google Maps SDK (Static Maps API)
- Q: When should the map preview refresh its pin data? → A: Only on landing page entry (no auto-refresh)
- Q: Should there be a limit on pins displayed in the preview? → A: No limit - show all pins in 10km radius

## Architectural Constraints (from Constitution)

The following constraints are mandated by the PetSpot Project Constitution and MUST be followed during implementation:

### MVI Architecture (Principle X - NON-NEGOTIABLE)
- Screen MUST follow Model-View-Intent pattern with unidirectional data flow
- Required components: `UiState` (immutable), `UserIntent` (sealed class), `UiEffect` (one-off events), `Reducer` (pure function)
- ViewModel exposes `state: StateFlow<UiState>`, `effects: SharedFlow<UiEffect>`, and `dispatchIntent()`
- UI collects state via `collectAsStateWithLifecycle()` and renders purely from `UiState`

### Two-Layer Composable Pattern (Principle X - NON-NEGOTIABLE)
1. **State Host Composable** (e.g., `MapPreviewSection`): Collects state from ViewModel, observes effects, dispatches intents, contains NO UI logic
2. **Stateless Composable** (e.g., `MapPreviewContent`): Accepts `UiState` as parameter, callback lambdas for interactions, contains ALL UI logic, NO ViewModel dependency

### Preview Requirements (MANDATORY)
- Stateless composable MUST have at least one `@Preview` function
- Preview data MUST use `@PreviewParameter` with `PreviewParameterProvider<UiState>`
- Provider MUST include loading, success, error, and permission states

### Dependency Injection (Principle IV - NON-NEGOTIABLE)
- MUST use Koin for dependency injection
- ViewModel MUST be injected via `koinViewModel()`
- Repository and use cases MUST be defined in Koin modules

## Assumptions

- The landing page already exists with hero section and Recent Reports list
- Backend API provides announcements with location coordinates (latitude/longitude) via existing announcements endpoint with location filter parameter
- Google Maps SDK (Static Maps API) will be used for map rendering
- The 10 km radius is measured from the user's current GPS location
- Pin icons/assets for missing (red) and found (blue) states are available or will be provided
- Tap action and navigation to interactive map will be implemented in the full map specification

## Design References

### Visual Specifications (from Figma)

- **Map View Header**: 16px font, color #101828, left-aligned
- **Legend dots**: 12px diameter circles (red: #FB2C36, blue: #2B7FFF)
- **Legend text**: 14px font, color #4A5565
- **Overlay pill**: White background, rounded (full pill shape), shadow (10px blur, 15px offset), 14px text color #101828
- **Container border**: 0.667px solid #E5E7EB
- **Map preview height**: ~320px (responsive)
