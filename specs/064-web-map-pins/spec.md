# Feature Specification: Web Map Pins + Pet Details Pop-up

**Feature Branch**: `064-web-map-pins`  
**Created**: 2025-12-18  
**Status**: Draft  
**Dependencies**: 063-web-map-view (landing page map component must exist first)  
**Input**: User description: "Split part 2/2: Display pins for missing animals on the web landing page map and show a details pop-up when the user clicks a pin."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - See Missing Animals as Pins (Priority: P1)

A user viewing the landing page map wants to see pins for missing animals in the visible map area so they can quickly spot cases near them.

**Why this priority**: Without pins, the map is just a background. Pins provide the actual content signal.

**Independent Test**: Can be tested by opening the landing page with location permission granted and verifying pins appear for missing animals within the current viewport.

**Acceptance Scenarios**:

1. **Given** the landing page map is displayed, **When** missing animal announcements exist in the visible map area, **Then** pins are displayed at each missing animal’s last-seen location
2. **Given** the landing page map is displayed, **When** no missing animal announcements exist in the visible map area, **Then** the map displays without pins and shows an empty-state message
3. **Given** the user zooms or pans the map, **When** the visible area changes, **Then** pins update to reflect missing animals in the newly visible area
4. **Given** missing animal announcements have invalid or missing coordinates, **When** pins are rendered, **Then** those announcements are not displayed as pins

---

### User Story 2 - Open a Pet Details Pop-up (Priority: P2)

A user wants to click a pin and see a pop-up with details of the missing animal so they can recognize it and contact the owner.

**Why this priority**: Pins become actionable when they lead to the information needed to help.

**Independent Test**: Can be tested by clicking a pin and verifying a pop-up shows the required details and can be dismissed.

**Acceptance Scenarios**:

1. **Given** pins are visible on the map, **When** the user clicks a pin, **Then** a pop-up appears with details of the selected missing animal
2. **Given** a pop-up is shown, **When** the user views it, **Then** it includes at minimum: pet photo, pet name, species/type, last-seen date, description, and a way to contact the owner
3. **Given** a pop-up is shown, **When** the user closes it (close button or click outside), **Then** the pop-up is dismissed and the map remains visible
4. **Given** a pop-up is open for one pin, **When** the user clicks a different pin, **Then** the pop-up updates to show details for the newly selected animal

---

### User Story 3 - Handle Errors and Loading (Priority: P3)

A user wants the map to remain understandable when pins are loading or when loading pins fails, with clear retry behavior.

**Why this priority**: A map with silent failures is confusing; clear loading and error states prevent churn.

**Independent Test**: Can be tested by simulating slow network and server errors and verifying loading indicators and retry work.

**Acceptance Scenarios**:

1. **Given** pin data is being loaded, **When** the request is in progress, **Then** a loading indicator is shown in the map area
2. **Given** loading pins fails, **When** an error occurs, **Then** a user-friendly error state with a retry action is displayed
3. **Given** an error state is shown, **When** the user clicks retry, **Then** the system attempts to load pins again

### Edge Cases

- **High pin density**: Many pins close together remain selectable (no “dead” pins due to overlap)
- **Duplicate coordinates**: Multiple missing animals at the same coordinates remain discoverable (e.g., selection still works)
- **Stale announcements**: If an announcement is removed/marked found, its pin is not shown on next refresh/load
- **Slow images**: If the pet photo loads slowly, the pop-up still opens and shows a placeholder until the image loads

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST display pins for missing animals on the landing page map component
- **FR-002**: Pins MUST be positioned using each missing animal’s last-seen coordinates
- **FR-003**: Only active missing-animal announcements (status “missing”) MUST be shown as pins
- **FR-004**: When the visible map area changes (zoom/pan), the system MUST update pins to reflect missing animals in the visible area
- **FR-005**: When the user clicks a pin, the system MUST display a pop-up with details of the selected missing animal
- **FR-006**: The pop-up MUST include at minimum: pet photo, pet name, species/type, last-seen date, description, and a way to contact the owner
- **FR-007**: The pop-up MUST be dismissible and must not navigate away from the landing page by default
- **FR-008**: When pin loading is in progress, the system MUST show a loading indicator in the map area
- **FR-009**: When pin loading fails, the system MUST show a user-friendly error state with a retry action
- **FR-010**: When the user activates retry, the system MUST attempt to load pins again

### Key Entities *(include if feature involves data)*

- **Missing Animal Pin**: A map marker representing a missing animal announcement at its last-seen coordinates
- **Pet Details Pop-up**: A UI element that appears when a pin is selected and displays missing animal details

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can see pins (when available) within 3 seconds of the map being visible under normal network conditions
- **SC-002**: Users can open a pet details pop-up within 1 second of clicking a pin under normal conditions
- **SC-003**: Pins shown on the map match missing animal locations with 100% correctness in QA test data
- **SC-004**: When pin loading fails, an error state is displayed within 10 seconds and retry succeeds after connectivity is restored

## Assumptions

- Missing animal announcements include last-seen coordinates suitable for pin placement.
- The landing page map component exists and is stable (provided by 063-web-map-view).
