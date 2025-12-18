# Feature Specification: Web Map View of Lost Pets (Landing Page)

**Feature Branch**: `063-web-map-view`  
**Created**: 2025-12-18  
**Status**: Draft  
**Input**: User description: "web map view: map added to landing page between Description and Recently Lost Pets; pins for missing animals; initial radius 3 km around user; permission gating with message + consent button; zoom in/out; pin click shows missing animal details pop-up"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - See Map on Landing Page (Priority: P1)

A user visiting the landing page wants to see an interactive map placed between the Description panel and the Recently Lost Pets panel, so they can quickly identify missing animals nearby.

**Why this priority**: This is the core value: showing nearby missing animals with spatial context directly where users start (landing page).

**Independent Test**: Can be tested by opening the landing page with location permission granted and verifying that the map is positioned correctly, centered on the user, and shows pins for missing animals.

**Acceptance Scenarios**:

1. **Given** the landing page is displayed, **When** the user views the main content, **Then** an interactive map is displayed between the Description panel and the Recently Lost Pets panel
2. **Given** the user has granted location permission, **When** the landing page loads, **Then** the map is centered on the user's current location
3. **Given** the landing page loads with location permission granted, **When** the map finishes loading, **Then** the initial viewport covers approximately a 3 km radius around the user's location
4. **Given** there are missing animal announcements within the initial viewport, **When** the map is shown, **Then** pins are displayed at each missing animal's last-seen location
5. **Given** there are no missing animal announcements within the initial viewport, **When** the map is shown, **Then** an empty-state message is displayed in the map area

---

### User Story 2 - Grant Location Permission (Priority: P2)

A user who has not granted location permission wants to understand why it is required and be able to grant permission so the landing page can display the map and nearby pins.

**Why this priority**: Without a clear permission flow, many users cannot access the map’s core value.

**Independent Test**: Can be tested by loading the map view with location permission denied/blocked and verifying an informational state with a clear action to grant permission is shown.

**Acceptance Scenarios**:

1. **Given** the user has not allowed location access, **When** the landing page loads, **Then** the map area is replaced by an informational message explaining that location consent is required to display the map
2. **Given** the informational message is displayed, **When** the user clicks the consent button, **Then** the browser permission prompt is triggered
3. **Given** the user grants location permission, **When** they return to the landing page, **Then** the map is displayed and centered on the user's location without requiring a manual page refresh
4. **Given** the user denies location permission, **When** they return to the landing page, **Then** the informational message remains displayed and the consent button is still available

---

### User Story 3 - Zoom and Inspect Pins (Priority: P3)

A user wants to zoom in/out the map and click a pin to see details of a missing animal so they can recognize it and contact the owner.

**Why this priority**: Map navigation and pin details turn the map into an actionable tool instead of a static visualization.

**Independent Test**: Can be tested by zooming/panning the map and clicking a pin to verify a pet details pop-up appears.

**Acceptance Scenarios**:

1. **Given** the map is displayed, **When** the user zooms in or out, **Then** the map zoom level changes accordingly
2. **Given** the map is displayed, **When** the user clicks a pin, **Then** a pop-up appears showing details of the missing animal
3. **Given** a pop-up is shown, **When** the user closes it, **Then** the map remains visible and usable

### Edge Cases

- **No geolocation support**: If the browser does not support location, show the informational state (no crash)
- **Location unavailable**: If location retrieval fails (timeout/GPS off), show the informational state or a user-friendly fallback message
- **Slow network**: Show a loading indicator while map and/or pins are loading
- **Failed fetch of lost pets**: Show a user-friendly error state with retry action in the map area
- **Invalid/missing coordinates**: Announcements with invalid coordinates are not shown as pins
- **Many pins**: Map remains usable when many pins are visible (pin overlap should not prevent selecting a pin)
 - **Landing page layout**: Map retains its placement between the Description and Recently Lost Pets panels across common screen sizes

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The landing page MUST display an interactive map between the Description panel and the Recently Lost Pets panel
- **FR-002**: Pins representing missing animals MUST be displayed on the map at each missing animal's last-seen location
- **FR-003**: When the landing page is entered and location permission is granted, the map MUST center on the user's current location
- **FR-004**: The initial map viewport MUST cover approximately a 3 km radius around the user's current location
- **FR-005**: If the user has not allowed location access, the map area MUST display an informational message explaining that location consent is required to display the map
- **FR-006**: The informational message MUST include a consent button allowing the user to grant location permission
- **FR-007**: Users MUST be able to zoom in and zoom out of the map
- **FR-008**: When the user clicks a pin, the system MUST display a pop-up with details of the missing animal
- **FR-009**: The pop-up MUST include at minimum: pet photo, pet name, species/type, last-seen date, and a way to contact the owner
- **FR-010**: When loading missing animal pins fails, the map area MUST display a user-friendly error state with a retry action
- **FR-011**: The map MUST only display active missing-animal announcements (status “missing”) by default

### Key Entities *(include if feature involves data)*

- **Lost Pet Announcement**: A missing animal report with last-seen coordinates, identification details, and owner contact information
- **Map Pin**: A marker representing a lost pet’s last-seen location
- **Location Permission State**: The current permission state for accessing the user’s location in the browser

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users with location permission can see the map and pins (when available) within 3 seconds of view load under normal network conditions
- **SC-002**: The initial viewport centers on the user and covers ~3 km radius (verified in QA)
- **SC-003**: Users can zoom/pan without perceivable UI freezing during interaction
- **SC-004**: Users can open a pet details pop-up within 1 second of clicking a pin under normal conditions

## Assumptions

- Lost pet announcements include last-seen coordinates suitable for pin placement.
- Only “missing” announcements are shown on the map for the initial version.
- The landing page already contains distinct Description and Recently Lost Pets panels.
