# Feature Specification: iOS Landing Page - Embed Map View

**Feature Branch**: `KAN-30-embed-map-in-landing-page`  
**Created**: 2025-12-19  
**Status**: Draft  
**Input**: User description: "Embed interactive map component in iOS landing page - map view only, without pin placement functionality. Displays map preview between Description and Recently Lost Pets sections."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - View Map Preview on Landing Page (Priority: P1)

A user opens the iOS landing page and wants to quickly see a map representation of the nearby area. The landing page displays a map preview positioned between the Description panel and the Recently Lost Pets panel, providing spatial context for pet search activities.

**Why this priority**: This is the core value of the feature - providing immediate visual and spatial orientation on the landing page, helping users understand the geographic scope of the application.

**Independent Test**: Can be tested by opening the landing page with location permission granted and verifying that a map preview appears between Description and Recently Lost Pets panels, centered on user location with appropriate zoom level.

**Acceptance Scenarios**:

1. **Given** the landing page is displayed, **When** the user scrolls the main content, **Then** a map preview is visible between the Description panel and the Recently Lost Pets panel
2. **Given** the user has granted location permission, **When** the landing page loads, **Then** the map preview displays an area of approximately 10 km radius around the user's current location
3. **Given** the map preview is shown, **When** the user views it, **Then** the map displays standard map features (streets, landmarks, terrain) appropriate for the zoom level
4. **Given** the map preview is displayed with location permission granted, **When** the user taps the preview area, **Then** a callback is triggered in the landing page ViewModel (logged for debugging)
5. **Given** the map preview is in loading or error state, **When** the user taps the preview area, **Then** no callback is triggered (tap only works when map is successfully loaded)
6. **Given** the map preview is loading, **When** network is slow, **Then** a loading indicator is displayed in the map preview area
7. **Given** the map preview fails to load, **When** an error occurs, **Then** a user-friendly error message with retry option is displayed in the map preview area

---

### User Story 2 - React to Location Permission Status (Priority: P2)

A user who has not granted location permission sees appropriate UI state in the map preview area. The map component reads existing location permission state and reacts to changes when user grants or denies permission.

**Why this priority**: Location permission is essential for the map to provide value; the map must properly read and react to existing permission infrastructure.

**Independent Test**: Can be tested by opening the landing page with different permission states (notDetermined, denied, authorizedWhenInUse) and verifying the map component displays appropriate UI for each state.

**Acceptance Scenarios**:

1. **Given** the user has not granted location permission (.notDetermined or .denied), **When** the landing page loads, **Then** the map preview area displays information explaining that location consent is required with appropriate call-to-action
2. **Given** the map observes location permission changes, **When** the user grants permission through the system prompt (triggered elsewhere), **Then** the map preview loads automatically without requiring an app restart
3. **Given** the user has authorized location (.authorizedWhenInUse or .authorizedAlways), **When** the landing page loads, **Then** the map preview displays immediately centered on user location
4. **Given** the app returns from background, **When** the user has changed location permission in Settings, **Then** the map component detects the change and updates its UI state accordingly

---

### Edge Cases

- **No location available**: If iOS Location Services are disabled system-wide, display the permission information state as if permission was not granted
- **Slow network**: Show a loading indicator for up to 10 seconds; if map doesn't load within this time, show error state
- **Failed map load**: Display retry button that re-attempts loading without requiring app restart
- **Location accuracy issues**: Use best available location data; map will center on approximate location even if accuracy is low
- **User moves location**: Map displays location from the time of landing page entry; does not update dynamically while user remains on landing page
- **Background/foreground transitions**: Map state is preserved when app goes to background and returns
- **Tap during loading/error**: Tapping the map preview during loading or error states does not trigger the callback (safe no-op)

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The landing page MUST display a map preview component positioned between the Description panel and the Recently Lost Pets panel
- **FR-001a**: The map preview MUST have a 16:9 aspect ratio and span the full width of the screen (respecting safe area insets)
- **FR-002**: The map preview MUST be a static UIImage generated by MKMapSnapshotter displaying standard map features (streets, landmarks, terrain)
- **FR-002a**: The map snapshot generation MUST be performed asynchronously to avoid blocking the main thread
- **FR-003**: When location permission is granted, the map preview MUST center on the user's current location and display approximately a 10 km radius area
- **FR-004**: The map preview MUST NOT support zoom, pan, or other map-specific gestures - it remains a static preview
- **FR-004a**: When the map preview is successfully loaded and displayed, tapping it MUST trigger a callback to the landing page ViewModel
- **FR-004b**: The tap callback MUST be logged for debugging purposes (print statement)
- **FR-004c**: Tapping the map preview during loading or error states MUST NOT trigger the callback (callback only fires when map is successfully displayed)
- **FR-005**: The map component MUST read location permission status from the existing LocationPermissionHandler infrastructure
- **FR-006**: When the user has not granted location permission (.notDetermined or .denied), the map preview area MUST display information explaining that location consent is required
- **FR-007**: The map component MUST observe location permission changes using LocationPermissionHandler's observation mechanism
- **FR-008**: When loading the map preview, the component MUST display a loading indicator to provide user feedback
- **FR-009**: When map preview loading fails, the component MUST display a user-friendly error message with a "Retry" button
- **FR-010**: When the user activates the retry action, the system MUST re-attempt loading the map preview without requiring an app restart
- **FR-011**: This specification MUST be implemented for iOS only (Android and Web are explicitly out of scope)
- **FR-012**: The map preview MUST NOT display any pins, markers, or annotations (pin functionality is out of scope for this specification)
- **FR-013**: The map component MUST NOT trigger location permission requests - it only reads and observes existing permission state

### Key Entities *(include if feature involves data)*

- **Map Preview**: A non-interactive, static visual representation of a geographic area displayed on the landing page between Description and Recently Lost Pets panels
- **Location Permission State**: The user's current consent status for sharing location with the application (granted, denied, or not determined)
- **User Location**: Geographic coordinates representing the user's current position, used to center the map preview

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can see the map preview within 3 seconds of landing page load under normal network conditions
- **SC-002**: When location permission is granted, the map preview accurately centers on the user's location with ~10 km radius coverage (verified through QA testing)
- **SC-003**: 95% of map preview load attempts succeed on the first try under normal network conditions
- **SC-004**: Map preview responds to tap gestures when successfully loaded (verified through manual testing and console logs)
- **SC-005**: Users understand the map is a static preview without zoom/pan capabilities (no confusion during usability testing)

## Assumptions

- The landing page already has distinct Description and Recently Lost Pets panel sections
- Location permission infrastructure (LocationService, LocationPermissionHandler, LocationPermissionStatus) is already implemented and available for use
- The map component will consume existing location permission APIs without modifying them
- Apple MapKit framework will be used for generating static map previews (native iOS framework, zero external dependencies, no licensing costs)
- MKMapSnapshotter will generate static UIImage; future pin placement features will require manual Core Graphics overlay (pins are not auto-rendered by snapshotter)
- The 10 km radius provides appropriate spatial context for the "nearby area" concept
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
- Q: What should happen when the user taps the map during loading or error states? → A: Nothing - the tap callback only fires when the map is successfully displayed.
- Q: Should the map component trigger location permission requests? → A: No - it only reads and observes permission state from existing LocationPermissionHandler infrastructure.
- Q: Which library/service should be used for rendering the map preview? → A: Apple MapKit
- Q: What dimensions should the map preview have on screen? → A: 16:9 aspect ratio, full width
- Q: Should VoiceOver accessibility be implemented for the map preview? → A: Out of scope
- Q: What format/implementation should be used for the map preview? → A: MKMapSnapshotter generating static UIImage
- Q: Should Dynamic Type support be implemented for error/loading messages? → A: No - use fixed font sizes consistent with existing iOS app pattern (.system(size: 16) for messages)

