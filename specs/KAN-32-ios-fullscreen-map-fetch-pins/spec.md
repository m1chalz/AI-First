# Feature Specification: iOS Fullscreen Map - Fetch and Display Pins from Server

**Feature Branch**: `KAN-32-ios-fullscreen-map-fetch-pins`  
**Created**: 2025-01-07  
**Status**: Draft  
**Ticket**: KAN-32  
**Platform**: iOS only  
**Dependencies**: KAN-32-ios-display-fullscreen-map (interactive map with legend must be implemented first)  
**Input**: User description: "na podstawie specki 066-mobile-map-interactive (dostępna tylko na branchu) stwórz specyfikację tylko dla iOS. To jest nadbudowa na pozostałe istniejące już specyfikacje zaczynające się od KAN-32. W tej części specyfikacji zrobimy fetchowanie danych z serwera - na podstawie center i radius. Przy załadowaniu widoku i po każdej skończonej akcji usera (move, pinch). Obsługujemy iOS 18+ więc możemy używać najnowszego API z kamerą."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Display Missing Animal Pins on Map Load (Priority: P1)

A user wants to see pins for missing animals when opening the fullscreen map so they can immediately spot cases in their area.

**Why this priority**: This is the core value proposition of the map feature. Without pins, the map is just an empty background. Users expect to see relevant data as soon as the map loads.

**Independent Test**: Can be tested by opening fullscreen map and verifying pins appear for missing animals in the initially visible area; verify pins are positioned correctly using last-seen coordinates.

**Acceptance Scenarios**:

1. **Given** the fullscreen map view is opening, **When** the map loads, **Then** the system fetches missing animal announcements for the visible area from the server
2. **Given** missing animal announcements exist in the visible area, **When** the data is received, **Then** pins are displayed at each missing animal's last-seen location
3. **Given** no missing animal announcements exist in the visible area, **When** the data is received, **Then** the map is shown without pins
4. **Given** pins are displayed, **When** the user views them, **Then** each pin visually matches the legend symbols defined in KAN-32-ios-display-fullscreen-map

---

### User Story 2 - Refresh Pins After Map Movement (Priority: P2)

A user wants pins to update automatically after moving or zooming the map so they always see relevant missing animals in the current view.

**Why this priority**: Dynamic pin updates are essential for map usability. Without this, users would only see pins from the initial load and miss animals in other areas.

**Independent Test**: Can be tested by panning/zooming the map and verifying pins refresh after the gesture ends; verify new pins appear and out-of-view pins are removed.

**Acceptance Scenarios**:

1. **Given** the fullscreen map is displayed with pins, **When** the user pans to a different area, **Then** the system fetches missing animal announcements for the newly visible area after the pan gesture completes
2. **Given** the fullscreen map is displayed with pins, **When** the user zooms in or out, **Then** the system fetches missing animal announcements for the newly visible area after the pinch gesture completes
3. **Given** the map region has changed, **When** new pins are loaded, **Then** pins from the previous region that are no longer in view are removed
4. **Given** the user is actively panning or zooming, **When** the gesture is in progress, **Then** pin fetch requests are NOT sent (wait until gesture ends)

---

### Edge Cases

- **High pin density**: Pins remain individually visible and tappable even when many pins are close together
- **Empty response**: Server returns empty array for visible region - map displays without pins
- **Failed pin fetch**: When pin loading fails (network error, server error, timeout), system silently fails and keeps existing pins displayed without showing error messages

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST fetch missing animal announcements from the server when the fullscreen map view loads
- **FR-002**: The system MUST use the map's center coordinates (latitude, longitude) and visible radius to query the server
- **FR-003**: The server request MUST use the endpoint `GET /announcements?lat={latitude}&lng={longitude}&range={radius_in_meters}`
- **FR-004**: The system MUST display pins for each missing animal announcement in the response with status "missing"
- **FR-005**: Pins MUST be positioned using each announcement's last-seen coordinates (latitude, longitude)
- **FR-006**: The system MUST maintain an internal loading state (e.g., isLoading flag) during pin fetch operations for future extensibility
- **FR-007**: The system MUST fetch updated pins after the user completes a pan gesture (region change)
- **FR-008**: The system MUST fetch updated pins after the user completes a pinch/zoom gesture (region change)
- **FR-009**: The system MUST NOT send fetch requests while pan or zoom gestures are in progress
- **FR-010**: The system MUST remove pins that are no longer in the visible region after a region change
- **FR-011**: When pin fetch fails, the system MUST silently fail and keep existing pins displayed without showing error messages
- **FR-012**: Pins MUST use the visual style (symbol, color) defined in the legend from KAN-32-ios-display-fullscreen-map
- **FR-013**: Only announcements with status "missing" MUST be displayed as pins

### Key Entities *(include if feature involves data)*

- **Missing Animal Pin**: A map annotation representing a missing animal announcement, positioned at last-seen coordinates
- **Pin Fetch Request**: Network request to server with center (lat, lng) and radius parameters
- **Map Region**: The currently visible area defined by center coordinates and radius/zoom level

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users see pins within 3 seconds after opening fullscreen map under normal network conditions
- **SC-002**: Users see updated pins within 3 seconds after completing a pan or zoom gesture under normal network conditions

## Assumptions

- The previous spec (KAN-32-ios-display-fullscreen-map) has been implemented, providing the interactive map and legend
- The backend API endpoint `GET /announcements?lat=X&lng=Y&range=Z` is already implemented and returns announcements in expected format
- Missing animal announcements include `lastSeenLatitude` and `lastSeenLongitude` fields suitable for pin placement
- Missing animal announcements include a `status` field with value "missing" for lost pets
- The iOS app uses URLSession or similar networking stack for API requests
- The iOS app follows MVVM-C architecture: ViewModel handles networking and business logic, View displays pins
- The iOS app targets iOS 18+, allowing use of latest MapKit APIs including camera-based region management
- Pin loading failures are handled silently without user-facing error messages (design decision)
- Pin tap interaction and details pop-up will be implemented in a subsequent specification

## Clarifications

### Session 2026-01-07

No clarifications needed - requirements are clear based on existing backend API and previous KAN-32 specs.

## Notes

This specification builds upon KAN-32-ios-display-fullscreen-map by adding data fetching and pin display. The interactive map and legend from the previous spec remain unchanged.

This spec focuses exclusively on fetching and displaying pins. Pin tap interaction and details pop-up will be implemented in a subsequent specification.

The iOS 18+ requirement enables use of MapKit's latest camera APIs for precise region calculation, but standard MKMapView region-based APIs are also acceptable if they meet requirements.

Pin visual style (symbol, color) must match the legend defined in KAN-32-ios-display-fullscreen-map to maintain consistency.

**Error Handling Philosophy**: Pin loading failures are handled silently (no error messages, no retry buttons). If a fetch fails, the existing pins remain displayed, and the user can continue interacting with the map. The next pan/zoom will trigger a new fetch attempt. This approach keeps the UI clean and avoids interrupting the user experience with transient network issues.

**Implementation Note**: The specification intentionally does not include requirements for canceling in-flight requests during rapid map movements or filtering invalid coordinates. These concerns are delegated to the repository layer implementation. The ViewModel/View layer focuses on user interaction patterns (fetch after gesture ends, update pins on success).

## Estimation *(mandatory)*

### Initial Estimate

- **Story Points**: 2
- **Initial Budget**: 2 × 4 × 1.3 = 10.4 days
- **Confidence**: ±50%
- **Anchor Comparison**: Simpler than Pet Details (3 SP). This feature includes network integration, MapKit region tracking, gesture handling, and loading indicator. No complex error UI or retry logic (silent failures). Involves async coordination and map-specific logic but reduced scope compared to full CRUD screen.

### Re-Estimation (Updated After Each Phase)

| Phase | SP | Days | Confidence | Key Discovery |
|-------|-----|------|------------|---------------|
| Initial | 2 | 10.4 | ±50% | Gut feel from feature title |
| After SPEC | — | — | ±30% | [Update when spec.md complete] |
| After PLAN | — | — | ±20% | [Update when plan.md complete] |
| After TASKS | — | — | ±15% | [Update when tasks.md complete] |

### Per-Platform Breakdown (After TASKS)

| Platform | Tasks | Days | Notes |
|----------|-------|------|-------|
| Backend | — | — | [Fill after tasks.md - likely 0, API exists] |
| iOS | — | — | [Fill after tasks.md - full implementation here] |
| Android | — | — | [Fill after tasks.md - N/A, iOS only] |
| Web | — | — | [Fill after tasks.md - N/A, iOS only] |
| **Total** | | **—** | |

### Variance Tracking

| Metric | Initial | Final | Variance |
|--------|---------|-------|----------|
| **Story Points** | 2 SP | — | [Calculate: (Y - X) / X × 100%] |
| **Budget (days)** | 10.4 days | — | [Calculate: (Y - X) / X × 100%] |

**Variance Reasons**: [Why was estimate different? Backend API reuse? MapKit native APIs?]

**Learning for Future Estimates**: [What pattern should the team apply to similar features?]

## Design Deliverables *(mandatory for UI features)*

### Design Assets

| Asset | Status | Link |
|-------|--------|------|
| **User Flow** | _Pending_ | FigJam diagram showing pin fetch flow on load/pan/zoom |
| **Wireframe** | _Pending_ | FigJam showing pin placement, loading states |
| **Design Brief** | _Pending_ | Pin visual specs (inherited from legend) |
| **Figma Make Prompt** | _Pending_ | AI prompt for generating loading UI |
| **Visual Mockups** | _Pending_ | High-fidelity screens showing pins, loading indicator |

### Design Requirements

- [ ] User flow diagram created (pin fetch lifecycle)
- [ ] Wireframe layout created (pin states, loading indicator)
- [ ] Design brief with pin UI specs
- [ ] Figma Make prompt ready
- [ ] All assets linked in Jira ticket
- [ ] Visual mockups approved (if applicable)

