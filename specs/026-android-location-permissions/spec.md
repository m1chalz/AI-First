# Feature Specification: Android Location Permissions Handling

**Feature Branch**: `026-android-location-permissions`  
**Created**: 2025-11-27  
**Status**: Draft  
**Input**: User description: "Android location permissions handling on startup screen (adapted from iOS spec)"

## Clarifications

### Session 2025-11-27

- Q: When user has granted permissions but location fetch fails (timeout, GPS unavailable, error), how should app behave? → A: App queries server without coordinates and displays animal listings (fallback mode)
- Q: When permission status changes during an active server query, how should app behave? → A: Complete current query and ignore permission change (very unlikely scenario)
- Q: When user backgrounds app while custom permission rationale is displayed, how should app behave? → A: Follow Android system default behavior (no special handling required)
- Q: What timeout duration should be used before falling back to no-location mode? → A: 10 seconds
- Q: What should the user see while location is being fetched? → A: Loading indicator while fetching location, then show animal listings
- Q: What messaging approach should the rationale dialogs use? → A: Benefit-focused ("See pets available near you for easier adoption")
- Q: Should permission events be tracked for analytics? → A: No tracking - privacy-first approach
- Q: Should background location (ACCESS_BACKGROUND_LOCATION) be explicitly out of scope? → A: Yes - background location is explicitly out of scope for this feature

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Location-Aware Content for Location-Authorized Users (Priority: P1)

Users who have already granted location permissions should seamlessly receive location-aware animal listings when opening the app. This represents the primary happy path for returning users.

**Why this priority**: This is the most common scenario for returning users and delivers the core value proposition - showing relevant pets based on user location.

**Independent Test**: Can be fully tested by launching the app with location permissions already granted (ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION) and verifying that current location is fetched and used for the animal query.

**Acceptance Scenarios**:

1. **Given** user has previously granted location permission (ACCESS_FINE_LOCATION), **When** startup screen appears, **Then** app fetches current location and queries server for animal listings
2. **Given** user has previously granted location permission (ACCESS_COARSE_LOCATION only), **When** startup screen appears, **Then** app fetches current location (approximate) and queries server for animal listings
3. **Given** location fetch succeeds, **When** coordinates are obtained, **Then** animal listings are displayed on startup screen
4. **Given** user has granted location permission, **When** location fetch fails (timeout, GPS unavailable, error), **Then** app queries server without coordinates and displays animal listings (fallback mode)

---

### User Story 2 - First-Time Location Permission Request (Priority: P2)

First-time users who haven't been asked about location permissions should see a clear system permission request and understand the value of granting access.

**Why this priority**: Critical for onboarding new users and establishing location-aware experience. High impact on user adoption and feature utilization.

**Independent Test**: Can be fully tested by installing fresh app (permission not yet requested) and verifying system dialog appears, then testing both acceptance and denial paths independently.

**Acceptance Scenarios**:

1. **Given** user hasn't been asked about location permissions (permission not yet requested), **When** startup screen appears, **Then** Android system dialog requesting location permission is displayed
2. **Given** system permission dialog is displayed, **When** user taps "Allow" (precise or approximate), **Then** app fetches current location and queries server for animal listings
3. **Given** system permission dialog is displayed, **When** user taps "Don't Allow", **Then** app queries server without location coordinates and displays animal listings
4. **Given** user denied permission via system dialog, **When** animal listings are displayed, **Then** user can still browse available animals (no location filtering)

---

### User Story 3 - Recovery Path for Denied Permissions (Priority: P3)

Users who previously denied location access should have a clear path to enable it through device Settings, with helpful guidance about why location enhances their experience.

**Why this priority**: Important for user retention and re-engagement. Users may change their mind about location sharing, and we should make it easy to enable.

**Independent Test**: Can be fully tested by launching app with denied permission status (user selected "Don't Allow" or "Don't ask again") and verifying custom rationale dialog appears with working Settings navigation and Cancel fallback.

**Acceptance Scenarios**:

1. **Given** user previously denied location permission (denied status), **When** startup screen appears, **Then** custom informational rationale dialog is displayed explaining permission status
2. **Given** user previously denied with "Don't ask again" checked, **When** startup screen appears, **Then** custom informational rationale dialog is displayed explaining permission status
3. **Given** custom permission rationale is displayed, **When** user taps "Go to Settings", **Then** Android Settings app opens to this app's permission screen (via Intent.ACTION_APPLICATION_DETAILS_SETTINGS)
4. **Given** custom permission rationale is displayed, **When** user taps "Cancel", **Then** dialog closes and app queries server without location coordinates
5. **Given** user dismissed rationale with "Cancel", **When** animal listings load, **Then** user can browse available animals without location filtering

---

### User Story 4 - Permission Rationale Before System Dialog (Priority: P4)

When Android's `shouldShowRequestPermissionRationale` returns true, app should show educational rationale before triggering the system permission dialog to increase grant rates.

**Why this priority**: Enhances permission grant rates by educating users about the value of location access. Lower priority as it only applies when rationale should be shown.

**Independent Test**: Can be fully tested by denying permission once (without "Don't ask again"), reopening app, and verifying rationale appears before system dialog.

**Acceptance Scenarios**:

1. **Given** shouldShowRequestPermissionRationale returns true, **When** startup screen appears, **Then** custom educational rationale is displayed before system permission dialog
2. **Given** custom rationale is displayed (shouldShowRequestPermissionRationale case), **When** user taps "Continue", **Then** Android system permission dialog is displayed
3. **Given** custom rationale is displayed (shouldShowRequestPermissionRationale case), **When** user taps "Not Now", **Then** app queries server without coordinates and displays animal listings

---

### User Story 5 - Dynamic Permission Change Handling (Priority: P5)

Users who change location permissions while the app is open (e.g., returning from Settings) should see the app respond without requiring restart.

**Why this priority**: Enhances user experience by eliminating friction and confusion when users modify permissions. Lower priority as it's less common, but important for polish.

**Independent Test**: Can be fully tested by changing location permission (via Settings) while app is on startup screen and verifying app reacts appropriately without restart.

**Acceptance Scenarios**:

1. **Given** app is observing location permission changes, **When** user grants permission (e.g., returns from Settings after enabling), **Then** app automatically fetches location and updates animal listings
2. **Given** app is on startup screen with denied permissions, **When** user grants permission via Settings and returns to app, **Then** location is fetched and animal query is executed with coordinates
3. **Given** app is observing permission changes, **When** permission status changes to denied, **Then** app continues operating in fallback mode without location
4. **Given** permission changes from granted to denied while on startup screen, **When** next animal query occurs, **Then** query executes without location coordinates

---

### Edge Cases

- What happens when location services are disabled system-wide (airplane mode, Location in Settings off)? → App queries server without coordinates and displays animal listings (fallback mode)
- How does system handle location fetch timeout or failure after permission is granted? → App queries server without coordinates and displays animal listings (fallback mode)
- How does app behave when permission changes occur during an active server query? → Complete current query and ignore permission change (very unlikely scenario)
- What happens if user backgrounds app while custom permission rationale is displayed? → Follow Android system default behavior (no special handling required)
- How does system handle location fetch when GPS signal is weak or unavailable? → App queries server without coordinates and displays animal listings (fallback mode)
- What happens on Android 10+ when user selects "Only this time" permission? → App fetches location for current session, but will need to request again on next cold start
- What happens on Android 12+ when user selects "Approximate" instead of "Precise" location? → App uses approximate location coordinates for server query (coarse location is acceptable)

## Out of Scope

- **Background location (ACCESS_BACKGROUND_LOCATION)**: This feature only handles foreground location permission. Background location tracking is explicitly out of scope and would require separate feature specification, additional Play Store justification, and privacy review.
- **Location-based push notifications**: Sending notifications based on user location is not part of this feature.
- **Location history or tracking**: The app does not store or track location history; location is used only for the current query.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST detect current location permission status on startup screen appearance
- **FR-002**: System MUST fetch current user location when ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION permission is granted
- **FR-003**: System MUST display Android system permission dialog when permission has not been requested yet
- **FR-004**: System MUST display custom informational rationale when permission is denied or when user selected "Don't ask again", using benefit-focused messaging (e.g., "See pets available near you for easier adoption")
- **FR-005**: System MUST display custom educational rationale before system dialog when shouldShowRequestPermissionRationale returns true, using benefit-focused messaging
- **FR-006**: Custom permission rationale (denied case) MUST include "Go to Settings" option that opens Android Settings app to this app's permission screen
- **FR-007**: Custom permission rationale MUST include "Cancel" or "Not Now" option that dismisses dialog and continues without location
- **FR-008**: System MUST query server for animal listings regardless of location permission status
- **FR-009**: System MUST include location coordinates in server query when location is successfully obtained
- **FR-010**: System MUST query server without location coordinates when location is unavailable or unauthorized
- **FR-011**: System MUST query server without location coordinates and display animal listings when location fetch fails after permission is granted (timeout after 10 seconds, GPS unavailable, or error)
- **FR-012**: System MUST handle permission result callback after user responds to system permission dialog
- **FR-013**: System MUST automatically fetch location and refresh animal listings when permission is granted via Settings while app is in foreground
- **FR-014**: System MUST display animal listings on startup screen after server query completes
- **FR-015**: Custom permission rationale MUST be displayed once per app session when permission status is denied (not on every screen appearance)
- **FR-016**: System MUST complete current server query if permission status changes during an active query (ignore permission change until query completes)
- **FR-017**: System MUST handle "Only this time" permission grant by fetching location for current session
- **FR-018**: System MUST handle "Approximate" location permission (ACCESS_COARSE_LOCATION without ACCESS_FINE_LOCATION) as valid for server query
- **FR-019**: System MUST display a loading indicator while fetching location and querying server, then display animal listings once data is ready
- **FR-020**: System MUST NOT track or log permission-related events for analytics purposes (privacy-first approach)

### Key Entities

- **Location Permission Status**: Represents current authorization state (not yet requested, granted, denied by user, denied with "Don't ask again")
- **shouldShowRequestPermissionRationale**: Android-specific flag indicating whether educational rationale should be shown before system dialog
- **User Location**: Geographic coordinates (latitude, longitude) obtained from device location services when authorized (precise or approximate)
- **Animal Listings**: Collection of animal records returned from server query, potentially filtered by location when coordinates are provided

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of users can browse animal listings regardless of location permission status (no blocking UX)
- **SC-002**: App handles location permission changes without crashes or UI inconsistencies in 100% of test scenarios
- **SC-003**: App correctly handles all Android permission states including "Only this time" and "Approximate" location options
