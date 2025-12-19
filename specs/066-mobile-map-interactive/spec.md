# Feature Specification: Mobile Fullscreen Interactive Map (Pins + Details Pop-up)

**Feature Branch**: `066-mobile-map-interactive`  
**Created**: 2025-12-18  
**Status**: Draft  
**Dependencies**: 062-mobile-map-view (static landing-page map preview must be implemented first)  
**Input**: User description: "Split part 2/2: Fullscreen interactive map view on mobile with zoom/pan, pins for missing animals, and a details pop-up after tapping a pin. Back arrow closes fullscreen map and returns to landing page."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Open Fullscreen Interactive Map (Priority: P1)

A user wants to open a fullscreen interactive map from the landing page preview and be able to return back to the landing page when done.

**Why this priority**: This is the entry point for the interactive experience. Without a reliable fullscreen flow, pins and details cannot be used effectively.

**Independent Test**: Can be tested by tapping the landing-page preview and verifying fullscreen map opens; tapping back arrow closes it and returns to landing.

**Acceptance Scenarios**:

1. **Given** the landing page preview is visible, **When** the user taps the preview, **Then** the fullscreen interactive map opens
2. **Given** the fullscreen interactive map is open, **When** the user taps the back arrow, **Then** the fullscreen map closes and the landing page is displayed
3. **Given** the fullscreen map is open, **When** the user returns to landing, **Then** the landing page scroll position and other content remain usable

---

### User Story 2 - Navigate the Fullscreen Map (Zoom + Pan) (Priority: P2)

A user wants to zoom and pan the fullscreen map to explore other areas beyond the initial view.

**Why this priority**: Navigation is fundamental to map usability; without it, the fullscreen map is not meaningfully interactive.

**Independent Test**: Can be tested by opening fullscreen map and using zoom/pan gestures; verify the visible area updates.

**Acceptance Scenarios**:

1. **Given** the fullscreen map is open, **When** the user zooms in, **Then** the map zoom level increases and shows a smaller area with more detail
2. **Given** the fullscreen map is open, **When** the user zooms out, **Then** the map zoom level decreases and shows a larger area
3. **Given** the fullscreen map is open, **When** the user pans to another area, **Then** the visible map area changes accordingly

---

### User Story 3 - See Missing Animals as Pins (Priority: P3)

A user wants to see pins for missing animals in the visible map area so they can spot cases near them.

**Why this priority**: Pins represent the actual missing-animal data. Without pins, the map is only a background.

**Independent Test**: Can be tested by opening fullscreen map and verifying pins appear for missing animals in view and update after pan/zoom.

**Acceptance Scenarios**:

1. **Given** the fullscreen map is open, **When** missing animal announcements exist in the visible area, **Then** pins are displayed at each missing animal’s last-seen location
2. **Given** the fullscreen map is open, **When** no missing animal announcements exist in the visible area, **Then** the map is shown without pins and without an empty-state message
3. **Given** the user changes the visible area (pan/zoom), **When** the user finishes the interaction, **Then** pins update to reflect missing animals in the visible area

---

### User Story 4 - View Missing Animal Details from a Pin (Priority: P4)

A user wants to tap a pin and see a pop-up with details of the missing animal so they can recognize it and contact the owner.

**Why this priority**: Details make the map actionable; users need context and contact info to help.

**Independent Test**: Can be tested by tapping a pin and verifying a details pop-up appears with required information and can be dismissed.

**Acceptance Scenarios**:

1. **Given** pins are visible, **When** the user taps a pin, **Then** a details pop-up appears for the selected missing animal
2. **Given** a details pop-up is shown, **When** the user views it, **Then** it includes at minimum: pet photo, pet name, species/type, last-seen date, description, and a way to contact the owner
3. **Given** a details pop-up is shown, **When** the user dismisses it, **Then** the fullscreen map remains visible and usable
4. **Given** the user taps a different pin, **When** a pop-up is already open, **Then** the pop-up updates to show the newly selected animal

### Edge Cases

- **Failed pins load**: Show a user-friendly error state with retry in fullscreen mode
- **Slow network**: Show loading indicator while pins are being fetched
- **Invalid coordinates**: Announcements with missing/invalid coordinates are not shown as pins
- **High pin density**: Pins remain tappable even when close together
- **Duplicate coordinates**: Multiple animals at the same coordinates remain discoverable (selection does not block access)
- **Missing images**: If a pet photo is missing or fails to load, the details pop-up shows a placeholder image and remains usable

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The application MUST open the fullscreen interactive map when the user taps the landing-page map preview
- **FR-002**: The fullscreen interactive map MUST include a back arrow that closes fullscreen mode and returns to the landing page
- **FR-003**: The fullscreen interactive map MUST support zoom in and zoom out
- **FR-004**: The fullscreen interactive map MUST support panning to display another area
- **FR-005**: The fullscreen interactive map MUST display pins for missing animals in the visible map area
- **FR-006**: Pins MUST be positioned using each missing animal’s last-seen coordinates
- **FR-007**: When the visible map area changes (zoom/pan), pins MUST update after the user finishes the interaction to reflect missing animals in the visible area
- **FR-008**: Only missing-animal announcements (status “missing”) MUST be displayed as pins
- **FR-009**: When the user taps a pin, the application MUST show a details pop-up for that missing animal
- **FR-010**: The details pop-up MUST include at minimum: pet photo, pet name, species/type, last-seen date, description, and a way to contact the owner
- **FR-014**: The pop-up contact information MUST include phone number and email address when available
- **FR-015**: If the pet photo is missing or fails to load, the details pop-up MUST display a placeholder image
- **FR-011**: The details pop-up MUST be dismissible and must not close fullscreen mode
- **FR-012**: When loading pins fails, fullscreen mode MUST show a user-friendly error state with a retry action
- **FR-013**: When retry is activated, the system MUST re-attempt loading pins for the current map viewport without requiring an app restart

### Key Entities *(include if feature involves data)*

- **Fullscreen Interactive Map**: A full-window map experience that supports zoom/pan and pin interaction; closed via back arrow
- **Missing Animal Pin**: A marker representing a missing animal announcement at its last-seen location
- **Details Pop-up**: A UI overlay that shows missing animal details after selecting a pin

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can open fullscreen interactive map mode within 2 seconds after tapping the landing preview under normal conditions
- **SC-002**: Users can zoom and pan the fullscreen map without perceivable UI freezing during interaction
- **SC-003**: Users can see pins (when available) within 3 seconds after fullscreen map is displayed under normal network conditions
- **SC-004**: Users can open a details pop-up within 1 second of tapping a pin under normal conditions

## Assumptions

- Missing animal announcements include last-seen coordinates suitable for pin placement.
- The landing page preview (spec 062) exists and provides the entry point into fullscreen mode.

## Clarifications

### Session 2025-12-18

- Q: When should pins refresh after pan/zoom in fullscreen mode? → A: Refresh after the user finishes the interaction (not continuously during pan/zoom).
- Q: What should be shown when there are no pins in the visible area? → A: Show the map without pins and without an empty-state message.
- Q: What should Retry do after pin loading fails? → A: Retry reloads pins for the current viewport without restarting the app.
- Q: What contact information should be displayed in the pop-up? → A: Phone number and email address.
- Q: How should missing pet photos be handled in the pop-up? → A: Show a placeholder image.
