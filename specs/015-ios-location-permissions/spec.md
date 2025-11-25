# Feature Specification: iOS Location Permissions Handling

**Feature Branch**: `015-ios-location-permissions`  
**Created**: 2025-11-25  
**Status**: Draft  
**Input**: User description: "iOS location permissions handling on startup screen"

## Clarifications

### Session 2025-11-25

- Q: When user has granted permissions but location fetch fails (timeout, GPS unavailable, error), how should app behave? → A: App queries server without coordinates and displays animal listings (fallback mode)
- Q: When permission status changes during an active server query, how should app behave? → A: Complete current query and ignore permission change (very unlikely scenario)
- Q: When user backgrounds app while custom permission popup is displayed, how should app behave? → A: Follow iOS system default behavior (no special handling required)

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Location-Aware Content for Location-Authorized Users (Priority: P1)

Users who have already granted location permissions should seamlessly receive location-aware animal listings when opening the app. This represents the primary happy path for returning users.

**Why this priority**: This is the most common scenario for returning users and delivers the core value proposition - showing relevant pets based on user location.

**Independent Test**: Can be fully tested by launching the app with location permissions already granted (authorizedWhenInUse or authorizedAlways) and verifying that current location is fetched and used for the animal query.

**Acceptance Scenarios**:

1. **Given** user has previously granted "While Using App" location permission, **When** startup screen appears, **Then** app fetches current location and queries server for animal listings
2. **Given** user has previously granted "Always" location permission, **When** startup screen appears, **Then** app fetches current location and queries server for animal listings
3. **Given** location fetch succeeds, **When** coordinates are obtained, **Then** animal listings are displayed on startup screen
4. **Given** user has granted location permission, **When** location fetch fails (timeout, GPS unavailable, error), **Then** app queries server without coordinates and displays animal listings (fallback mode)

---

### User Story 2 - First-Time Location Permission Request (Priority: P2)

First-time users who haven't been asked about location permissions should see a clear system permission request and understand the value of granting access.

**Why this priority**: Critical for onboarding new users and establishing location-aware experience. High impact on user adoption and feature utilization.

**Independent Test**: Can be fully tested by installing fresh app (notDetermined status) and verifying system alert appears, then testing both acceptance and denial paths independently.

**Acceptance Scenarios**:

1. **Given** user hasn't been asked about location permissions (notDetermined status), **When** startup screen appears, **Then** iOS system alert requesting location permission is displayed
2. **Given** system permission alert is displayed, **When** user taps "Allow While Using App", **Then** app fetches current location and queries server for animal listings
3. **Given** system permission alert is displayed, **When** user taps "Don't Allow", **Then** app queries server without location coordinates and displays animal listings
4. **Given** user denied permission via system alert, **When** animal listings are displayed, **Then** user can still browse available animals (no location filtering)

---

### User Story 3 - Recovery Path for Denied Permissions (Priority: P3)

Users who previously denied location access should have a clear path to enable it through device Settings, with helpful guidance about why location enhances their experience.

**Why this priority**: Important for user retention and re-engagement. Users may change their mind about location sharing, and we should make it easy to enable.

**Independent Test**: Can be fully tested by launching app with denied/restricted permission status and verifying custom popup appears with working Settings navigation and Cancel fallback.

**Acceptance Scenarios**:

1. **Given** user previously denied location permission (denied status), **When** startup screen appears, **Then** custom informational popup is displayed explaining permission status
2. **Given** user previously denied location permission (restricted status), **When** startup screen appears, **Then** custom informational popup is displayed explaining permission status
3. **Given** custom permission popup is displayed, **When** user taps "Go to Settings", **Then** iOS Settings app opens to this app's permission screen
4. **Given** custom permission popup is displayed, **When** user taps "Cancel", **Then** popup closes and app queries server without location coordinates
5. **Given** user dismissed popup with "Cancel", **When** animal listings load, **Then** user can browse available animals without location filtering

---

### User Story 4 - Dynamic Permission Change Handling (Priority: P4)

Users who change location permissions while the app is open (e.g., returning from Settings) should see the app respond without requiring restart.

**Why this priority**: Enhances user experience by eliminating friction and confusion when users modify permissions. Lower priority as it's less common, but important for polish.

**Independent Test**: Can be fully tested by changing location permission (via Settings or system alert) while app is on startup screen and verifying app reacts appropriately without restart.

**Acceptance Scenarios**:

1. **Given** app is observing location permission changes, **When** user grants permission (e.g., returns from Settings after enabling), **Then** app automatically fetches location and updates animal listings
2. **Given** app is on startup screen with denied permissions, **When** user grants permission via Settings and returns to app, **Then** location is fetched and animal query is executed with coordinates
3. **Given** app is observing permission changes, **When** permission status changes to denied/restricted, **Then** app continues operating in fallback mode without location
4. **Given** permission changes from granted to denied while on startup screen, **When** next animal query occurs, **Then** query executes without location coordinates

---

### Edge Cases

- What happens when location services are disabled system-wide (airplane mode, Location Services off in Settings)? → App queries server without coordinates and displays animal listings (fallback mode)
- How does system handle location fetch timeout or failure after permission is granted? → App queries server without coordinates and displays animal listings (fallback mode)
- How does app behave when permission changes occur during an active server query? → Complete current query and ignore permission change (very unlikely scenario)
- What happens if user backgrounds app while custom permission popup is displayed? → Follow iOS system default behavior (no special handling required)
- How does system handle location fetch when GPS signal is weak or unavailable? → App queries server without coordinates and displays animal listings (fallback mode)

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST detect current location permission status on startup screen appearance
- **FR-002**: System MUST fetch current user location when permission status is authorizedWhenInUse or authorizedAlways
- **FR-003**: System MUST display iOS system permission alert when permission status is notDetermined
- **FR-004**: System MUST display custom informational popup when permission status is denied or restricted
- **FR-005**: Custom permission popup MUST include "Go to Settings" option that opens iOS Settings app to this app's permission screen
- **FR-006**: Custom permission popup MUST include "Cancel" option that dismisses popup and continues without location
- **FR-007**: System MUST query server for animal listings regardless of location permission status
- **FR-008**: System MUST include location coordinates in server query when location is successfully obtained
- **FR-009**: System MUST query server without location coordinates when location is unavailable or unauthorized
- **FR-014**: System MUST query server without location coordinates and display animal listings when location fetch fails after permission is granted (timeout, GPS unavailable, or error)
- **FR-010**: System MUST observe location permission status changes in real-time
- **FR-011**: System MUST automatically fetch location and refresh animal listings when permission changes from denied/notDetermined to authorized while on startup screen
- **FR-012**: System MUST display animal listings on startup screen after server query completes
- **FR-013**: Custom permission popup MUST be displayed once per app session when permission status is denied/restricted (not on every screen appearance)
- **FR-015**: System MUST complete current server query if permission status changes during an active query (ignore permission change until query completes)

### Key Entities

- **Location Permission Status**: Represents current authorization state (not yet asked, authorized for app use, always authorized, denied by user, restricted by system policies)
- **User Location**: Geographic coordinates (latitude, longitude) obtained from device location services when authorized
- **Animal Listings**: Collection of animal records returned from server query, potentially filtered by location when coordinates are provided

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of users can browse animal listings regardless of location permission status (no blocking UX)
- **SC-002**: App handles location permission changes without crashes or UI inconsistencies in 100% of test scenarios
