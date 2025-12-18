# Feature Specification: Web Map Component on Landing Page

**Feature Branch**: `063-web-map-view`  
**Created**: 2025-12-18  
**Status**: Draft  
**Input**: User description: "Split part 1/2: Add interactive map component to landing page (between Description and Recently Lost Pets) with location permission gating, initial ~3 km viewport around user, and zoom controls. (Pins and pop-ups moved to a separate spec.)"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - See Map on Landing Page (Priority: P1)

A user visiting the landing page wants to see an interactive map placed between the Description panel and the Recently Lost Pets panel, so they can orient themselves to the location-based experience.

**Why this priority**: This establishes the landing-page layout and map experience entry point. Without the map component in place, pins/details cannot be delivered.

**Independent Test**: Can be tested by opening the landing page with location permission granted and verifying that the map is positioned correctly, centers on the user, and supports zooming.

**Acceptance Scenarios**:

1. **Given** the landing page is displayed, **When** the user views the main content, **Then** an interactive map is displayed between the Description panel and the Recently Lost Pets panel
2. **Given** the user has granted location permission, **When** the landing page loads, **Then** the map is centered on the user's current location
3. **Given** the landing page loads with location permission granted, **When** the map finishes loading, **Then** the initial viewport covers approximately a 3 km radius around the user's location
4. **Given** the map is displayed, **When** the user uses zoom controls, **Then** the map zoom level changes accordingly

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
- **Location unavailable**: If location retrieval fails (timeout/GPS off), show the informational state or a user-friendly fallback message
- **Slow network**: Show a loading indicator while map and/or pins are loading
- **Failed map load**: Show a user-friendly error state with retry action in the map area
 - **Landing page layout**: Map retains its placement between the Description and Recently Lost Pets panels across common screen sizes

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The landing page MUST display an interactive map between the Description panel and the Recently Lost Pets panel
- **FR-003**: When the landing page is entered and location permission is granted, the map MUST center on the user's current location
- **FR-004**: The initial map viewport MUST cover approximately a 3 km radius around the user's current location
- **FR-005**: If the user has not allowed location access, the map area MUST display an informational message explaining that location consent is required to display the map
- **FR-006**: The informational message MUST include a consent button allowing the user to grant location permission
- **FR-007**: Users MUST be able to zoom in and zoom out of the map
- **FR-008**: When loading the map fails, the map area MUST display a user-friendly error state with a retry action

### Key Entities *(include if feature involves data)*

- **Landing Page Map Component**: The interactive map displayed on the landing page between Description and Recently Lost Pets panels
- **Location Permission State**: The current permission state for accessing the user’s location in the browser

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users with location permission can see the map within 3 seconds of landing page load under normal network conditions
- **SC-002**: The initial viewport centers on the user and covers ~3 km radius (verified in QA)
- **SC-003**: Users can zoom/pan without perceivable UI freezing during interaction

## Assumptions

- Lost pet announcements include last-seen coordinates suitable for pin placement.
- Only “missing” announcements are shown on the map for the initial version.
- The landing page already contains distinct Description and Recently Lost Pets panels.
