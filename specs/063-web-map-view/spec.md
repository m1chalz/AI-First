# Feature Specification: Web Map Component on Landing Page

**Feature Branch**: `063-web-map-view`  
**Created**: 2025-12-18  
**Status**: Draft  
**Input**: User description: "Split part 1/2: Add interactive map component to landing page (between Description and Recently Lost Pets) with location permission gating, initial ~10 km viewport around user, and zoom controls."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - See Map on Landing Page (Priority: P1)

A user visiting the landing page wants to see an interactive map placed between the Description panel and the Recently Lost Pets panel, so they can orient themselves to the location-based experience.

**Why this priority**: This establishes the landing-page layout and map experience entry point.

**Independent Test**: Can be tested by opening the landing page with location permission granted and verifying that the map is positioned correctly, centers on the user, and supports zooming.

**Acceptance Scenarios**:

1. **Given** the landing page is displayed, **When** the user views the main content, **Then** an interactive map is displayed between the Description panel and the Recently Lost Pets panel
2. **Given** the user has granted location permission and location is successfully obtained, **When** the landing page loads, **Then** the map is centered on the user's current location
3. **Given** the landing page loads and location is successfully obtained, **When** the map finishes loading, **Then** the initial viewport covers approximately a 10 km radius around the user's location
4. **Given** the map is displayed, **When** the user uses zoom controls, **Then** the map zoom level changes accordingly
5. **Given** the map is displayed, **When** the user drags the map, **Then** the map pans to show a different area

---

### User Story 2 - Grant Location Permission (Priority: P2)

A user who has not granted location permission wants to understand why it is required and be able to grant permission so the landing page can display the map.

**Why this priority**: Without a clear permission flow, many users cannot access the map’s core value.

**Independent Test**: Can be tested by loading the map view with location permission denied/blocked and verifying an informational state with a clear action to grant permission is shown.

**Acceptance Scenarios**:

1. **Given** the user has not allowed location access, **When** the landing page loads, **Then** the map area is replaced by an informational message explaining that location consent is required to display the map
2. **Given** the informational message is displayed, **When** the user clicks the consent button, **Then** the browser permission prompt is triggered
3. **Given** the user grants location permission, **When** they return to the landing page, **Then** the map is displayed and centered on the user's location without requiring a manual page refresh
4. **Given** the user denies location permission, **When** they return to the landing page, **Then** the informational message remains displayed and the consent button is still available

---

### Edge Cases

- **No geolocation support**: If the browser does not support location, show the informational state (no crash)
- **Location unavailable**: If location retrieval fails (timeout/GPS off), show the map in fallback mode (Wrocław, PL). Show a user-friendly message "Unable to get location. Please refresh the page to try again."
- **Slow network**: Show a loading indicator while the map is loading
- **Failed map load**: Show a user-friendly error state with message "Failed to load map. Please refresh the page to try again."
 - **Landing page layout**: Map retains its placement between the Description and Recently Lost Pets panels across common screen sizes

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The landing page MUST display an interactive map between the Description panel and the Recently Lost Pets panel
- **FR-002**: When the landing page is entered and the user's location is successfully obtained (permission granted), the map MUST center on the user's current location
- **FR-003**: The initial map viewport MUST cover approximately a 10 km radius around the user's current location using zoom level 13
- **FR-004**: If the user has not allowed location access, the map area MUST display an informational message explaining that location consent is required to display the map
- **FR-005**: The informational message MUST include a consent button allowing the user to grant location permission
- **FR-006**: Users MUST be able to zoom in/out and pan (drag) the map
- **FR-007**: When loading the map fails, the map area MUST display a user-friendly error state with message "Failed to load map. Please refresh the page to try again."
- **FR-008**: When location permission is granted but the user's location cannot be obtained (e.g., timeout or location services disabled), the system MUST display the map in fallback mode (centered on Wrocław, PL) and show a user-friendly message "Unable to get location. Please refresh the page to try again."
- **FR-010**: In fallback mode, the map MUST center on the user's most recently known location if available; otherwise it MUST center on a default fallback location
- **FR-011**: The default fallback location MUST be Wrocław, PL (coordinates: 51.1079, 17.0385)
- **FR-012**: The map component MUST have a fixed height of 400px
- **FR-013**: The map MUST display attribution/copyright notices for OpenStreetMap and Leaflet (required by OSM license)

### Key Entities *(include if feature involves data)*

- **Landing Page Map Component**: The interactive map displayed on the landing page between Description and Recently Lost Pets panels
- **Location Permission State**: The current permission state for accessing the user’s location in the browser

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: The initial viewport centers on the user and covers ~10 km radius (verified in QA)
- **SC-002**: Users can zoom/pan without perceivable UI freezing during interaction

## Clarifications

### Session 2025-12-18

- Q: Should the map allow panning (dragging), or only zoom? → A: Zoom + pan.
- Q: What should happen after a map load failure? → A: Show error message "Failed to load map. Please refresh the page to try again."
- Q: What should happen when location is unavailable even though permission is granted? → A: Show the map in fallback mode with message "Unable to get location. Please refresh the page to try again."
- Q: What should the map center on in fallback mode when location is unavailable? → A: Center on the user's most recently known location if available; otherwise use a default fallback location.
- Q: What is the default fallback location when no last-known location exists? → A: Wrocław, PL.
- Q: Which map library/provider should be used for the interactive map component? → A: Leaflet.js + OpenStreetMap
- Q: What should be the height of the map component on the landing page? → A: 400px fixed height
- Q: What zoom level should the map use to display approximately a 10 km radius viewport? → A: Zoom level 13
- Q: Should the map display attribution/copyright notices for OpenStreetMap and Leaflet? → A: Yes, display attribution
- Q: What are the exact coordinates for the default fallback location (Wrocław, PL)? → A: 51.1079, 17.0385

## Assumptions

- The landing page already contains distinct Description and Recently Lost Pets panels.
- This specification includes only the map component; no pins or pin-driven pop-ups are displayed as part of this feature.
- The map implementation will use Leaflet.js library with OpenStreetMap tiles (open source, no API key required, good React integration via react-leaflet).
