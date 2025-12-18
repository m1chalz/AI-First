# Feature Specification: Mobile Map View (Landing Preview + Fullscreen Map)

**Feature Branch**: `062-mobile-map-view`  
**Created**: 2025-12-18  
**Status**: Draft  
**Input**: User description: "Mobile map view: landing page map preview between Description and Recently Lost Pets; pins for missing animals; initial radius 3 km around user; permission gating; landing shows static picture with “Tap to view interactive map”; tap opens fullscreen interactive map with zoom/pan; back arrow returns to landing; pin tap shows pet details."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - See Nearby Lost Pets (Preview on Landing Page) (Priority: P1)

A user opens the landing page and wants to quickly see whether there are missing pets nearby. The landing page contains a map preview placed between the Description panel and the Recently Lost Pets panel.

**Why this priority**: This is the core value of the feature on mobile: providing immediate, location-based awareness directly on the landing page.

**Independent Test**: Can be tested by opening the landing page with location permission granted and verifying that a map preview appears in the correct place and reflects a ~3 km radius around the user.

**Acceptance Scenarios**:

1. **Given** the landing page is displayed, **When** the user scrolls the main content, **Then** a map preview is displayed between the Description panel and the Recently Lost Pets panel
2. **Given** the user has granted location permission, **When** the landing page loads, **Then** the map preview represents an area of approximately 3 km radius around the user's current location
3. **Given** there are missing pet announcements within the represented area, **When** the map preview is shown, **Then** pins are visible for those missing pets
4. **Given** there are no missing pet announcements within the represented area, **When** the map preview is shown, **Then** an empty-state message is shown within the map preview area
5. **Given** the map preview is shown, **When** the user views it, **Then** it contains the message “Tap to view interactive map”

---

### User Story 2 - Grant Location Permission to View the Map (Priority: P2)

A user who has not granted location permission wants to understand why it is required and be able to grant permission so that the map preview and interactive map can be displayed.

**Why this priority**: Without permission handling, the feature cannot deliver its location-based value for many users.

**Independent Test**: Can be tested by opening the landing page without location permission and verifying the permission explanation + consent action appears in the map area.

**Acceptance Scenarios**:

1. **Given** the user has not granted location permission, **When** the landing page loads, **Then** the map preview area shows information that the user must agree to share their location to display the map
2. **Given** the permission information is displayed, **When** the user taps the consent button, **Then** the system permission prompt is triggered
3. **Given** the user grants location permission, **When** they return to the landing page, **Then** the map preview is displayed without requiring an app restart
4. **Given** the user denies location permission, **When** they return to the landing page, **Then** the permission information remains displayed and the consent button is still available

---

### User Story 3 - Open Fullscreen Interactive Map (Priority: P3)

A user taps the map preview on the landing page and wants to open an interactive, fullscreen map to explore a wider area and interact with pins.

**Why this priority**: The preview is intentionally non-interactive; fullscreen map enables the full experience (zoom/pan, deeper exploration).

**Independent Test**: Can be tested by tapping the preview and verifying a fullscreen interactive map opens with a back arrow that returns to landing.

**Acceptance Scenarios**:

1. **Given** the map preview is displayed, **When** the user taps the preview, **Then** the application opens the interactive map in fullscreen mode
2. **Given** the fullscreen interactive map is open, **When** the user taps the back arrow, **Then** the interactive map closes and the landing page is displayed
3. **Given** the fullscreen interactive map is open, **When** the user zooms in or out, **Then** the map zoom level changes accordingly
4. **Given** the fullscreen interactive map is open, **When** the user pans the map, **Then** the visible area changes to the new region

---

### User Story 4 - View Missing Pet Details from a Pin (Priority: P4)

A user wants to tap a pin on the interactive map and see details of the missing animal so they can recognize it and contact the owner.

**Why this priority**: Pins are only useful if they can lead to actionable information.

**Independent Test**: Can be tested by tapping a pin in interactive mode and verifying a pop-up with the missing pet details appears.

**Acceptance Scenarios**:

1. **Given** the fullscreen interactive map displays pins, **When** the user taps a pin, **Then** a pop-up appears with details of the missing animal
2. **Given** a pop-up is shown, **When** the user dismisses it, **Then** the interactive map remains open and usable
3. **Given** multiple pins are visible, **When** the user taps a different pin, **Then** the pop-up updates to show details for the selected pin

### Edge Cases

- **No location available**: If device location services are disabled or unavailable, treat as “location not allowed” and show the permission/help information state
- **Slow network**: Show a loading indicator for the map preview and/or pins while data is being fetched
- **Failed fetch of missing pets**: Show a user-friendly error state with retry (both for preview and fullscreen map)
- **Invalid/missing coordinates**: Announcements with missing/invalid coordinates are not shown as pins
- **Many pins**: The interactive map remains usable when many pins are in view (e.g., clustering/overlap does not prevent tapping pins)
- **User moves location**: The initial 3 km radius uses the current location at the time of entering the landing page; subsequent movement does not unexpectedly recenter while the user is interacting

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The landing page MUST display a map preview between the Description panel and the Recently Lost Pets panel
- **FR-002**: The map preview MUST be a static image (non-interactive) and MUST show the message “Tap to view interactive map”
- **FR-003**: Pins representing missing animals MUST be visible on the map preview when missing pet announcements exist in the represented area
- **FR-004**: When the landing page is entered and location permission is granted, the represented area MUST cover approximately a 3 km radius around the user's current location
- **FR-005**: If the user has not allowed the app to access location, the map preview area MUST display information that location consent is required to display the map
- **FR-006**: The location-consent information state MUST include a button that allows the user to grant consent
- **FR-007**: When the user taps the static map preview, the application MUST open an interactive map in fullscreen mode
- **FR-008**: The fullscreen interactive map MUST allow zooming in and zooming out
- **FR-009**: The fullscreen interactive map MUST allow panning/moving the map to display another area
- **FR-010**: The fullscreen interactive map MUST include a back arrow that closes interactive mode and returns the user to the landing page
- **FR-011**: Pins representing missing animals MUST be visible on the fullscreen interactive map for missing pet announcements in the visible area
- **FR-012**: When the user taps a pin in fullscreen interactive mode, a pop-up MUST appear with details of the missing animal
- **FR-013**: Both the preview and fullscreen map MUST only display missing animals (announcements with status “missing”)
- **FR-014**: When loading missing animal pins fails, the map preview/fullscreen map MUST show a user-friendly error state with a retry action

### Key Entities *(include if feature involves data)*

- **Missing Pet Announcement**: A report of a missing animal with last-seen coordinates, identification details (photo, name/species), last-seen date, and owner contact information
- **Map Preview**: A non-interactive, static representation of a map area shown on the landing page, including a “Tap to view interactive map” affordance
- **Interactive Map View**: A fullscreen, interactive map experience that supports zoom/pan and pin interaction; closed via back arrow
- **Location Permission State**: The user’s current consent status for sharing location with the application (granted or not granted)

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can see the landing page map preview within 3 seconds of landing page load under normal network conditions
- **SC-002**: When location permission is granted, the initial represented area is centered on the user's location and covers ~3 km radius (verified in QA)
- **SC-003**: 90% of users in usability testing understand that the landing page map is a preview and that tapping opens interactive mode (based on the “Tap to view interactive map” message)
- **SC-004**: Users can open fullscreen interactive map mode within 2 seconds after tapping the preview under normal conditions
- **SC-005**: Users can zoom/pan the fullscreen map without perceivable UI freezing during interaction
- **SC-006**: Users can open a missing pet details pop-up within 1 second of tapping a pin under normal conditions

## Assumptions

- Missing pet announcements include last-seen coordinates suitable for pin placement.
- The landing page already contains distinct Description and Recently Lost Pets panels.
- The default radius of 3 km is acceptable as a first version for “nearby” on mobile.
