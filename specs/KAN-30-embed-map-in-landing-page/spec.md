# Feature Specification: iOS Landing Page - Embed Map View

**Feature Branch**: `KAN-30-embed-map-in-landing-page`  
**Created**: 2025-12-19  
**Status**: Draft  
**Input**: User description: "Embed interactive map component in iOS landing page - map view only, without pin placement functionality. Displays map preview between Hero panel and Recent Reports section."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Map Preview Display & Interaction (Priority: P1)

A user with location permission already granted opens the iOS landing page and sees a map preview showing the nearby area. The map provides spatial context for pet search activities and can be tapped for future functionality.

**Why this priority**: This is the core value of the feature - providing immediate visual and spatial orientation on the landing page, helping users understand the geographic scope of the application.

**Independent Test**: Can be tested by opening the landing page with location permission granted and location available, verifying that a map preview appears with correct placement, aspect ratio, and responds to tap gestures.

**Acceptance Scenarios**:

1. **Given** the landing page is displayed with location available, **When** the user scrolls the main content, **Then** a map preview is visible between the Hero panel and the Recent Reports section
2. **Given** location is available, **When** the landing page loads, **Then** the map preview displays an area of approximately 10 km radius around the user's current location
3. **Given** the map preview is shown, **When** the user views it, **Then** the map displays default Apple Maps style (standard road map with streets, labels, landmarks, and terrain)
4. **Given** the map preview has a 16:9 aspect ratio, **When** the user views it on any device, **Then** it spans the full width minus horizontal padding
5. **Given** the map preview is displayed, **When** the user taps the preview area, **Then** a callback is triggered in the landing page ViewModel (logged to console for debugging)
6. **Given** the map has no zoom/pan gestures enabled, **When** the user attempts to pinch or drag, **Then** the map does not respond to these gestures

---

### User Story 2 - Location Permission State Integration (Priority: P2)

The map component reads existing location permission state from LocationPermissionHandler and updates its UI based on permission status. When permission is not granted, appropriate UI is shown. When permission changes (granted through system prompt or Settings), the map updates automatically.

**Why this priority**: Location permission is essential for the map to provide value; the map must properly integrate with existing permission infrastructure without triggering new permission requests.

**Independent Test**: Can be tested by opening the landing page with different permission states (notDetermined, denied, authorizedWhenInUse) and verifying the map component displays appropriate UI for each state. Test permission changes by granting permission in Settings and returning to app.

**Acceptance Scenarios**:

1. **Given** the user has not granted location permission (.notDetermined or .denied), **When** the landing page loads, **Then** the map preview area displays information explaining that location consent is required with "Go to Settings" button
2. **Given** the user has authorized location (.authorizedWhenInUse or .authorizedAlways), **When** the landing page loads, **Then** the map preview displays immediately centered on user location
3. **Given** the map observes location permission changes, **When** the user grants permission through the system prompt (triggered elsewhere in the app), **Then** the map preview loads automatically without requiring an app restart
4. **Given** the app returns from background, **When** the user has changed location permission in Settings, **Then** the map component detects the change and updates its UI state accordingly
5. **Given** the map preview is in loading state (determining location), **When** the user taps the preview area, **Then** no callback is triggered (tap only works when map is successfully loaded)
6. **Given** the map preview is determining initial location, **When** this process is in progress, **Then** a loading indicator is displayed in the map preview area

---

### Edge Cases

- **No location available**: If iOS Location Services are disabled system-wide, display the permission information state as if permission was not granted
- **Location accuracy issues**: Use best available location data; map will center on approximate location even if accuracy is low
- **User moves location**: Map displays location from the time of landing page entry; does not update dynamically while user remains on landing page
- **Background/foreground transitions**: Map state is preserved when app goes to background and returns
- **Tap during loading**: Tapping the map preview during loading state does not trigger the callback (safe no-op)

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The landing page MUST display a map preview component positioned between the Hero panel and the Recent Reports section
- **FR-001a**: The map preview MUST have a 16:9 aspect ratio and span the full width of the screen (respecting safe area insets)
- **FR-002**: The map preview MUST be rendered using SwiftUI Map view with disabled interactions, displaying default Apple Maps style (standard road map with streets, labels, landmarks, and terrain)
- **FR-003**: When location permission is granted, the map preview MUST center on the user's current location and display approximately a 10 km radius area
- **FR-004**: The map preview MUST NOT support zoom, pan, or other map-specific gestures - it remains a static preview
- **FR-004a**: When the map preview is successfully loaded and displayed, tapping it MUST trigger a callback to the landing page ViewModel
- **FR-004b**: The tap callback MUST be logged for debugging purposes (print statement)
- **FR-004c**: Tapping the map preview during loading states MUST NOT trigger the callback (callback only fires when map is successfully displayed)
- **FR-005**: The map component MUST read location permission status from the existing LocationPermissionHandler infrastructure
- **FR-006**: When the user has not granted location permission (.notDetermined or .denied), the map preview area MUST display localized message (L10n.MapPreview.Permission.message) explaining that location consent is required, with a "Go to Settings" button (L10n.MapPreview.Permission.settingsButton)
- **FR-007**: The map component MUST observe location permission changes using LocationPermissionHandler's observation mechanism
- **FR-008**: When loading initial location data, the component MUST display a loading indicator to provide user feedback
- **FR-009**: This specification MUST be implemented for iOS only (Android and Web are explicitly out of scope)
- **FR-010**: The map preview MUST NOT display any pins, markers, or annotations (pin functionality is out of scope for this specification)
- **FR-011**: The map component MUST NOT trigger location permission requests - it only reads and observes existing permission state

### Key Entities *(include if feature involves data)*

- **Map Preview**: A non-interactive, static visual representation of a geographic area displayed on the landing page between Hero panel and Recent Reports section
- **Location Permission State**: The user's current consent status for sharing location with the application (granted, denied, or not determined)
- **User Location**: Geographic coordinates representing the user's current position, used to center the map preview

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can see the map preview within 3 seconds of landing page load (verified through manual testing on LTE and WiFi networks)
- **SC-002**: When location permission is granted, the map preview accurately centers on the user's location with ~10 km radius coverage (verified through QA testing)
- **SC-003**: Map preview responds to tap gestures when successfully loaded (verified through manual testing and console logs)
- **SC-004**: Users understand the map is a static preview without zoom/pan capabilities (no confusion during usability testing)

## Assumptions

- The landing page already has distinct Hero panel and Recent Reports sections
- Location permission infrastructure (LocationService, LocationPermissionHandler, LocationPermissionStatus) is already implemented and available for use
- The map component will consume existing location permission APIs without modifying them
- SwiftUI Map framework will be used for rendering the map preview (native iOS 14+, zero external dependencies, no licensing costs)
- SwiftUI Map renders live map tiles from Apple Maps; future pin placement features can be added via Map annotations
- The 10 km radius provides appropriate spatial context for the "nearby area" concept (matches typical walking/cycling range for pet search)
- Users are familiar with standard map visualizations and require no additional instructional text
- This feature focuses solely on map display; pin placement and interactive map features are defined in separate specifications
- VoiceOver accessibility support is out of scope for this feature (may be addressed in future iterations)

## Existing Infrastructure

The following iOS components are already implemented and ready to use:

### LocationPermissionStatus (Domain Model)
- Enum with states: `.notDetermined`, `.authorizedWhenInUse`, `.authorizedAlways`, `.denied`, `.restricted`
- Property `isAuthorized: Bool` - indicates if location can be accessed
- Location: `iosApp/Domain/LocationPermissionStatus.swift`

### LocationServiceProtocol (Domain Interface)
- `authorizationStatus: LocationPermissionStatus { get async }` - current permission status
- `authorizationStatusStream: AsyncStream<LocationPermissionStatus>` - reactive stream of permission changes
- `requestWhenInUseAuthorization() async -> LocationPermissionStatus` - requests permission (triggers system prompt)
- `requestLocation() async -> Coordinate?` - fetches current device location
- Location: `iosApp/Domain/LocationServiceProtocol.swift`

### LocationService (Data Implementation)
- Actor-based implementation using CoreLocation framework
- Implements LocationServiceProtocol
- Thread-safe with Swift Concurrency
- Location: `iosApp/Data/LocationService.swift`

### LocationPermissionHandler (Business Logic)
- Reusable handler for location permission flow across ViewModels
- `requestLocationWithPermissions() async -> LocationRequestResult` - handles full permission + location flow
- `checkPermissionStatusChange() async -> (status, didBecomeAuthorized)` - detects permission changes
- `startObservingLocationPermissionChanges(onStatusChange:)` - observes both real-time stream and app foreground events
- `stopObservingLocationPermissionChanges()` - cleanup method
- Location: `iosApp/Domain/Services/LocationPermissionHandler.swift`

### Usage Pattern
The map component should:
1. Inject `LocationPermissionHandler` via dependency injection
2. Call `startObservingLocationPermissionChanges()` to monitor permission state
3. React to status changes in callback (update UI state accordingly)
4. Call `stopObservingLocationPermissionChanges()` in deinit/cleanup
5. Use `requestLocationWithPermissions()` to get current location when permission is granted
6. Provide a tap callback to the landing page ViewModel when the map preview is successfully displayed

**Important**: 
- The map component does NOT trigger permission requests - it only reads and observes permission state
- The tap callback is for future functionality (e.g., fullscreen map) - in this iteration it only logs to console

## Clarifications

### Session 2025-12-19

- Q: Should the map be completely non-interactive? → A: The map should not support zoom/pan gestures, but when successfully loaded, tapping it should trigger a callback to the landing page ViewModel (with console log). This prepares for future fullscreen map functionality.
- Q: What should happen when the user taps the map during loading states? → A: Nothing - the tap callback only fires when the map is successfully displayed.
- Q: Should the map component trigger location permission requests? → A: No - it only reads and observes permission state from existing LocationPermissionHandler infrastructure.
- Q: Which library/service should be used for rendering the map preview? → A: Apple MapKit
- Q: What dimensions should the map preview have on screen? → A: 16:9 aspect ratio, full width
- Q: Should VoiceOver accessibility be implemented for the map preview? → A: Out of scope
- Q: What format/implementation should be used for the map preview? → A: SwiftUI Map view with disabled interactions (simpler than MKMapSnapshotter, avoids async complexity)
- Q: Should Dynamic Type support be implemented for permission messages? → A: No - use fixed font sizes consistent with existing iOS app pattern (.system(size: 16) for messages)

