# Feature Specification: iOS Display Fullscreen Interactive Map with Legend

**Feature Branch**: `KAN-32-ios-display-fullscreen-map`  
**Created**: 2025-01-07  
**Status**: Draft  
**Ticket**: KAN-32  
**Platform**: iOS only  
**Dependencies**: KAN-32-ios-navigation-to-fullscreen-map (navigation to empty fullscreen map view must be implemented first)  
**Input**: User description: "Na podstawie specki 066-mobile-map-interactive (dostępna tylko na branchu) stwórz specyfikację tylko dla iOS. Ta specyfikacja będzie nałożona na KAN-32-ios-navigation-to-fullscreen-map, która zawiera stworzenie pustego widoku. W tej części specyfikacji zrobimy wyświetlenie legendy i mapy wewnątrz FullscreenMapView. Tylko wyświetlenie mapy i legendy, piny ogarniemy później."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Display Interactive Map (Priority: P1)

A user wants to see an interactive map rendered in the fullscreen map view with zoom and pan capabilities to explore the area.

**Why this priority**: This is the foundation for the map experience. The empty placeholder from the previous spec must be replaced with a functional map. This provides the core infrastructure for future features (pins, details).

**Independent Test**: Can be tested by opening fullscreen map view and verifying an interactive map is rendered; user can zoom in/out and pan to different areas.

**Acceptance Scenarios**:

1. **Given** the fullscreen map view is open, **When** the view loads, **Then** an interactive map is displayed centered on the user's location or default region
2. **Given** the map is displayed, **When** the user pinches to zoom in, **Then** the map zoom level increases and shows a smaller area with more detail
3. **Given** the map is displayed, **When** the user pinches to zoom out, **Then** the map zoom level decreases and shows a larger area
4. **Given** the map is displayed, **When** the user pans to another area, **Then** the visible map area changes accordingly with smooth animation
5. **Given** the map is displayed, **When** the user double-taps the map, **Then** the map zooms in on the tapped location

---

### User Story 2 - Display Map Legend (Priority: P2)

A user wants to see a legend explaining what map symbols and colors will represent (for future pin features) so they understand what visual elements mean.

**Why this priority**: A legend establishes the visual design language early, even before pins are implemented. This allows front-end development to proceed in parallel and ensures consistency when pins are added later.

**Independent Test**: Can be tested by opening fullscreen map and verifying legend is visible; legend accurately describes the visual elements that will be used on the map.

**Acceptance Scenarios**:

1. **Given** the fullscreen map is open, **When** the map is displayed, **Then** a legend is visible showing symbol/color meanings for future map elements
2. **Given** the legend is displayed, **When** the user views it, **Then** it shows at minimum placeholders for missing animal pin markers and their meanings
3. **Given** the legend is displayed, **When** the user interacts with the map, **Then** the legend remains visible and does not obstruct critical map areas

---

### Edge Cases

- **Memory pressure**: Map should handle memory warnings gracefully without crashing; consider reducing cached map tiles
- **Background/foreground**: Map state (zoom, center) should be preserved when app moves to background and returns
- **Orientation change**: Map should adapt to orientation changes without losing current viewport
- **Location permissions denied**: Map should still work with default region; user can manually pan to desired area
- **Slow map tile loading**: Map should show loading indicators for tiles that are still fetching
- **Legend positioning**: Legend should remain accessible and not obstruct map controls (zoom buttons, user location button)

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The fullscreen map view MUST render an interactive map using MapKit (MKMapView)
- **FR-002**: The map MUST support pinch-to-zoom gestures for zooming in and out
- **FR-003**: The map MUST support pan gestures to navigate to different areas
- **FR-004**: The map MUST support double-tap gesture to zoom in on a location
- **FR-005**: The map MUST be centered on the user's current location on initial load if location permissions are granted
- **FR-006**: If location permissions are not granted, the map MUST display a reasonable default region
- **FR-007**: The map view MUST display a legend showing the meaning of map symbols/colors (for future pin features)
- **FR-008**: The legend MUST be positioned to not obstruct critical map areas or map controls
- **FR-009**: The legend MUST show at minimum: missing animal pin marker symbol/color and its meaning (as placeholder for future implementation)
- **FR-010**: The map MUST preserve its state (zoom level, center) when the app moves to background and returns
- **FR-011**: The map MUST adapt to device orientation changes without losing current viewport

### Key Entities *(include if feature involves data)*

- **Interactive Map (MKMapView)**: Native iOS MapKit view providing map rendering and gesture-based interactions (zoom, pan)
- **Map Legend**: Visual guide explaining symbols and colors that will be used for map pins in future features

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can see an interactive map rendered within 2 seconds after opening fullscreen map view under normal conditions
- **SC-002**: Users can zoom and pan the map without perceivable lag or UI freezing during interaction
- **SC-003**: The map remains responsive when user performs multiple rapid zoom/pan gestures
- **SC-004**: Users can identify future pin meanings by viewing the legend without requiring external documentation

## Assumptions

- The previous spec (KAN-32-ios-navigation-to-fullscreen-map) has been implemented, providing the navigation infrastructure and empty fullscreen view
- The iOS app has location permissions handling implemented (either granted or denied gracefully)
- The application follows iOS MVVM-C architecture with coordinators for navigation and ViewModels for business logic
- SwiftUI views are wrapped in UIHostingController for integration with UIKit-based MapKit components
- Visual design for the legend (symbols, colors) will be finalized based on reference image in JIRA ticket KAN-32
- Future specifications will add pin display and interaction capabilities on top of this map foundation

## Notes

This specification builds upon KAN-32-ios-navigation-to-fullscreen-map by replacing the empty placeholder view with a functional MapKit-based map. The navigation infrastructure (back button, navigation bar with "Pet Locations" title) from the previous spec remains unchanged.

The legend is included as a preparatory UI element for future pin features. Its specific visual design (symbols, colors, layout) should reference the mockup image provided in JIRA ticket KAN-32.

Pin display and interaction will be implemented in a subsequent specification building upon this foundation.

## Clarifications

### Session 2026-01-07

- Q: Should pins be included in this spec? → A: No, only map display and legend. Pins will be in a separate spec.
- Q: What should the map center on if location permissions are denied? → A: Default region (e.g., city center or last known region)
- Q: What should the legend show if pins aren't implemented yet? → A: Placeholder symbols/colors for missing animal markers (based on design in KAN-32 mockup)
- Q: Should the legend be interactive (collapsible/expandable)? → A: Not specified; can be a static overlay that doesn't obstruct map interaction
