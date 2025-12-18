# Feature Specification: Mobile Landing Page Static Map Preview

**Feature Branch**: `062-mobile-map-view`  
**Created**: 2025-12-18  
**Status**: Draft  
**Input**: User description: "Split part 1/2: Add a static map preview component to the landing page between Description and Recently Lost Pets. Preview is non-interactive and gated by location permission. (Fullscreen interactive map + pins + pop-ups are defined in a separate spec.)"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - See Nearby Lost Pets (Preview on Landing Page) (Priority: P1)

A user opens the landing page and wants to quickly see whether there are missing pets nearby. The landing page contains a map preview placed between the Description panel and the Recently Lost Pets panel.

**Why this priority**: This is the core value of the feature on mobile: providing immediate, location-based awareness directly on the landing page.

**Independent Test**: Can be tested by opening the landing page with location permission granted and verifying that a map preview appears in the correct place and reflects a ~10 km radius around the user.

**Acceptance Scenarios**:

1. **Given** the landing page is displayed, **When** the user scrolls the main content, **Then** a map preview is displayed between the Description panel and the Recently Lost Pets panel
2. **Given** the user has granted location permission, **When** the landing page loads, **Then** the map preview represents an area of approximately 10 km radius around the user's current location
3. **Given** the map preview is shown, **When** the user views it, **Then** it is clearly identified as a static preview (non-interactive)

---

### User Story 2 - Grant Location Permission to View the Map (Priority: P2)

A user who has not granted location permission wants to understand why it is required and be able to grant permission so that the map preview can be displayed.

**Why this priority**: Without permission handling, the feature cannot deliver its location-based value for many users.

**Independent Test**: Can be tested by opening the landing page without location permission and verifying the permission explanation + consent action appears in the map area.

**Acceptance Scenarios**:

1. **Given** the user has not granted location permission, **When** the landing page loads, **Then** the map preview area shows information that the user must agree to share their location to display the map
2. **Given** the permission information is displayed, **When** the user taps the consent button, **Then** the system permission prompt is triggered
3. **Given** the user grants location permission, **When** they return to the landing page, **Then** the map preview is displayed without requiring an app restart
4. **Given** the user denies location permission, **When** they return to the landing page, **Then** the permission information remains displayed and the consent button is still available

### Edge Cases

- **No location available**: If device location services are disabled or unavailable, treat as “location not allowed” and show the permission/help information state
- **Slow network**: Show a loading indicator while the preview is loading
- **Failed preview load**: Show a user-friendly error state with retry in the preview area
- **User moves location**: The represented 10 km radius uses the location at the time of entering the landing page; it does not constantly update while the user remains on the landing page

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The landing page MUST display a map preview between the Description panel and the Recently Lost Pets panel
- **FR-002**: The map preview MUST be a static image (non-interactive)
- **FR-004**: When the landing page is entered and location permission is granted, the represented area MUST cover approximately a 10 km radius around the user's current location
- **FR-005**: If the user has not allowed the app to access location, the map preview area MUST display information that location consent is required to display the map
- **FR-006**: The location-consent information state MUST include a button that allows the user to grant consent
- **FR-007**: The preview area MUST clearly communicate that the preview is not interactive
- **FR-008**: When loading the map preview fails, the preview area MUST show a user-friendly error state with a retry action

### Key Entities *(include if feature involves data)*

- **Missing Pet Announcement**: A report of a missing animal with last-seen coordinates, identification details (photo, name/species), last-seen date, and owner contact information
- **Map Preview**: A non-interactive, static representation of a map area shown on the landing page, clearly identified as a preview (non-interactive)
- **Location Permission State**: The user’s current consent status for sharing location with the application (granted or not granted)

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can see the landing page map preview within 3 seconds of landing page load under normal network conditions
- **SC-002**: When location permission is granted, the initial represented area is centered on the user's location and covers ~10 km radius (verified in QA)
- **SC-003**: 90% of users in usability testing understand that the landing page map is a static preview (non-interactive)

## Assumptions

- Missing pet announcements include last-seen coordinates suitable for pin placement.
- The landing page already contains distinct Description and Recently Lost Pets panels.
- The default radius of 10 km is acceptable as a first version for “nearby” on mobile.
- This specification does not include fullscreen interactive map, pins, or pop-ups; those behaviors are defined in a separate specification.
