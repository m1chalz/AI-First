# Feature Specification: iOS Map Preview - Display Missing Pet Pins

**Feature Branch**: `KAN-30-ios-show-pins-on-the-map`  
**Created**: 2025-12-19  
**Status**: Draft  
**Input**: User description: "Create iOS map view functionality to display pins for missing pet announcements on the landing page map preview. This is a subset of KAN-30-ios-landing-page-map-view, focusing only on the pin display functionality."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - View Missing Pet Pins on Map Preview (Priority: P1)

A user opens the iOS landing page with location permission granted and wants to see at a glance where missing pets were last seen in their area. The map preview displays visual pins at the approximate locations of missing pet announcements within the represented ~10 km radius (pins are derived from the landing page announcements fetch results).

**Why this priority**: This is the core value proposition - enabling users to quickly identify missing pets in their vicinity through visual location markers on a map.

**Independent Test**: Can be tested by opening the landing page with location permission granted and missing pet announcements in the database within 10 km radius. Verify that pins appear on the map preview at the correct approximate locations. Pins are derived from the landing page announcements fetch results.

**Acceptance Scenarios**:

1. **Given** the landing page is displayed with location permission granted, **When** missing pet announcements exist within the ~10 km radius of user's location, **Then** pins are visible on the map preview at the approximate last-seen locations of those pets (for the announcements returned by the landing page fetch)
2. **Given** pins are displayed on the map preview, **When** the user views the pins, **Then** each pin represents one missing pet announcement with visually distinct markers
3. **Given** multiple missing pet announcements exist in close proximity, **When** their pins would overlap on the map preview, **Then** the pins are displayed (they may overlap visually) without requiring any interaction to separate them
4. **Given** the map preview is displayed, **When** no missing pet announcements exist within the represented area, **Then** the map preview is shown without pins and without an empty-state message

---

### User Story 2 - Understand Pin Representation is Static (Priority: P2)

A user sees pins on the map preview and may wonder if they can interact with them. The preview is explicitly non-interactive - tapping pins or the preview itself performs no action.

**Why this priority**: Setting correct user expectations prevents confusion and aligns with the design decision that this is a preview, not a full interactive map.

**Independent Test**: Can be tested by tapping on pins or any part of the map preview and verifying that no action occurs (no navigation, no pop-ups, no details).

**Acceptance Scenarios**:

1. **Given** pins are visible on the map preview, **When** the user taps a pin, **Then** nothing happens (no-op) and no details are opened
2. **Given** the map preview is displayed, **When** the user taps anywhere on the preview (not on a pin), **Then** nothing happens (no-op) and the preview remains static
3. **Given** the map preview is displayed, **When** the user views it, **Then** there is no instructional text suggesting interactivity (preview is visual-only)

---

### Edge Cases

- **Many overlapping pins**: If multiple missing pet announcements exist in close proximity, pins can overlap visually on the preview. No special handling (clustering, separation) is required since the preview is non-interactive.
- **No announcements within radius**: Show the map preview without pins and without an empty-state message - just an empty map.
- **Backend API failure**: When the backend API fails to return data (network error, timeout, 5xx error), display the map preview without pins (same behavior as 0 results) - no error message shown.
- **Pin data missing coordinates**: If a missing pet announcement lacks valid last-seen coordinates, do not display a pin for that announcement.
- **Dynamic location updates**: The pin positions are calculated based on the user's location at the time of landing page load. Pins do not update if the user's location changes while on the landing page.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The map preview MUST display visual pins for missing pet announcements derived from the landing page announcements fetch results (iOS MUST request announcements using the user's current location and `range=10` km)
- **FR-002**: Each pin MUST represent one missing pet announcement at its last-seen coordinates
- **FR-003**: Pins MUST be visually distinct markers (standard map pin style or similar visual indicator)
- **FR-004**: When missing pet announcements have overlapping or very close coordinates, the pins MAY overlap visually on the preview (no clustering or separation required)
- **FR-005**: The map preview MUST NOT display pins for missing pet announcements that lack valid last-seen coordinates
- **FR-006**: When no missing pet announcements exist within the represented area OR when the backend API fails to return data, the map preview MUST be displayed without pins and without an empty-state message
- **FR-007**: Tapping on a pin MUST perform no action (no-op) - no navigation, no pop-ups, no details display
- **FR-008**: Tapping anywhere on the map preview MUST perform no action (no-op) - the preview remains static
- **FR-009**: The map preview MUST NOT contain any instructional text suggesting interactivity (e.g., "Tap to see details")
- **FR-010**: Pin positions MUST be calculated based on the user's location at the time the landing page loads
- **FR-011**: Pin positions MUST NOT dynamically update if the user's location changes while the user remains on the landing page
- **FR-012**: Pins MUST automatically update whenever missing pet announcement data is fetched or refreshed (reactive data binding) - the data refresh mechanism itself is out of scope
- **FR-013**: This specification MUST be implemented for iOS only (Android and Web are explicitly out of scope)
- **FR-014**: Pins MUST be shown only for announcements with backend status `MISSING` and `FOUND` (announcements with backend status `CLOSED` MUST NOT be shown as pins). iOS domain maps backend `MISSING` to `AnnouncementStatus.active`.
- **FR-015**: Pins MUST be built from the same announcements payload used by the landing page announcements list (i.e., no additional dedicated fetch for map pins in this spec)

### Key Entities *(include if feature involves data)*

- **Missing Pet Announcement**: A report of a missing animal including last-seen coordinates (latitude/longitude), identification details (photo, name, species), last-seen date, and owner contact information. For pin display: only backend `MISSING` and `FOUND` are pin-eligible; backend `CLOSED` is excluded. iOS domain maps backend `MISSING` to `AnnouncementStatus.active`.
- **Pin**: A visual marker displayed on the map preview at a specific coordinate, representing one missing pet announcement's last-seen location
- **Map Preview**: A non-interactive, static representation of a map area (~10 km radius) displayed on the landing page, showing geographical context with pins overlaid

## Assumptions

- Missing pet announcements in the system include valid last-seen coordinates (latitude/longitude) suitable for pin placement
- The landing page already contains a map preview component (from parent specification KAN-30-ios-landing-page-map-view) where pins can be rendered
- The ~10 km radius is already determined and the map preview area is already configured (from parent specification)
- Pin visual design (color, icon, size) follows iOS/app design system standards and does not require separate design specification
- The backend API supports location filtering using `lat`, `lng`, and `range` query parameters; iOS will request `range=10` km for the map preview pins use case
- Location permission handling is already implemented (from parent specification KAN-30-ios-landing-page-map-view)

## Dependencies

- **Parent Specification**: KAN-30-ios-landing-page-map-view (provides the map preview component and location permission handling)
- **Backend API**: Requires endpoint to fetch missing pet announcements filtered by geographic area (lat/lng + `range` km)
- **Location Services**: Requires user's current location to be available (provided by iOS Location Services with user permission)

## Clarifications

### Session 2025-12-19

- Q: How should pins be styled (color, icon)? → A: Use iOS standard map pin style (red pin marker) or app design system default pin style
- Q: Should pins show any preview information on hover/tap? → A: No, pins are completely non-interactive (no hover, no tap response)
- Q: How many announcements should be loaded? → A: Use the same announcements fetch used by the landing page list (limited results), and build pins from that payload
- Q: Should pins be animated when they appear? → A: No animation required; pins appear when map preview loads
- Q: What is the maximum number of pins that iOS should render on the map preview? → A: No separate max; render pins only for the announcements returned by the landing page announcements fetch
- Q: What should happen when the backend API fails to return pin data (network error, timeout, 5xx)? → A: Display empty map without pins (same as 0 results) - no error message shown
- Q: Should iOS explicitly set map radius via API query parameter? → A: Yes - iOS MUST send `range=10` (km) when requesting announcements for map preview pins
- Q: Which announcement statuses should be shown as pins? → A: Show pins for backend `MISSING` and `FOUND`; do not show pins for backend `CLOSED`
- Q: Should pins be built from the same fetch as the landing page announcements list? → A: Yes - use the landing page announcements fetch results to build pins (no dedicated pins fetch in this spec)
- Q: How should backend `status` values map to iOS domain? → A: Backend uses `MISSING|FOUND|CLOSED`; iOS domain maps `MISSING -> AnnouncementStatus.active`
- Q: How should pins be refreshed when announcement data updates? → A: Pins update automatically whenever announcement data is fetched/refreshed (reactive binding) - data refresh mechanism is out of scope for this spec
- Q: Should pin display have dedicated logging/metrics for observability? → A: No dedicated logging - rely on standard iOS system logs only
- Q: Should Success Criteria (SC-001, SC-002, SC-003) be included for pin accuracy and behavior validation? → A: Remove all Success Criteria from specification
