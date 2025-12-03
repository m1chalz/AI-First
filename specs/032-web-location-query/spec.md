# Feature Specification: Web Browser Location for Pet Listings

**Feature Branch**: `032-web-location-query`  
**Created**: 2025-11-29  
**Status**: Draft  
**Input**: User description: "Bazując na specyfikacji 026, przygotuj podobną dla aplikacji webowej. po pobraniu lokalizacji, ma być ona załączona do URL który zwraca listę ogłoszeń w formie ?lat=LATITUDE&lng=LONGITUDE. jeśli nie uda się pobrać lokalizacji, nie dołączamy tych parametrów. Odpuść user story 4, nie bierzemy go pod uwagę."

## Clarifications

### Session 2025-11-29

- Q: Should the specification explicitly require HTTPS for production deployment (browser Geolocation API requirement)? → A: No validation - if HTTP is used, location feature will not work and app will use fallback mode (unfiltered pet listings)
- Q: How should the blocked permission informational message be displayed across page visits? → A: Show banner every time the page loads when permission is blocked, displayed above pet listings with X button to close
- Q: What type of loading indicator should be displayed? → A: Full-page spinner/overlay blocking interaction until data loads
- Q: When should the browser permission prompt be triggered for first-time users? → A: Immediately on page load (automatic permission request)
- Q: How should location parameters be added to the API URL? → A: Use URLSearchParams to construct the URL
- Q: How should the app handle errors from the pet listings API? → A: Show error message with retry button (e.g., "Unable to load pets. Try again")
- Q: Should the banner provide browser-specific instructions or generic guidance? → A: Show generic instructions that work for all browsers, keep the message short
- Q: What should be displayed when no pets are available? → A: Show empty state message "No pets nearby"
- Q: How many decimal places should be sent to the API? → A: Round to 4 decimal places (11m accuracy, smaller data)

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Location-Aware Content for Location-Authorized Users (Priority: P1)

Users who have already granted location permissions in their browser should seamlessly receive location-aware pet listings when opening the app. This represents the primary happy path for returning users.

**Why this priority**: This is the most common scenario for returning users and delivers the core value proposition - showing relevant pets based on user location.

**Independent Test**: Can be fully tested by launching the web app with location permissions already granted in browser settings and verifying that current location is fetched and attached to the pet listings API query.

**Acceptance Scenarios**:

1. **Given** user has previously granted location permission in browser, **When** pet listings page loads, **Then** app fetches current location and queries server with `?lat=LATITUDE&lng=LONGITUDE` parameters
2. **Given** location fetch succeeds, **When** coordinates are obtained, **Then** pet listings are displayed with location-based filtering
3. **Given** user has granted location permission, **When** location fetch fails (timeout, GPS unavailable, error), **Then** app queries server without location parameters and displays all available pet listings (fallback mode)
4. **Given** location fetch is in progress, **When** coordinates are being obtained, **Then** full-page spinner/overlay is displayed blocking user interaction

---

### User Story 2 - First-Time Location Permission Request (Priority: P2)

First-time users who haven't been asked about location permissions should see a clear browser permission prompt and understand the value of granting access.

**Why this priority**: Critical for onboarding new users and establishing location-aware experience. High impact on user adoption and feature utilization.

**Independent Test**: Can be fully tested by opening app in incognito mode (permission not yet requested) and verifying browser permission prompt appears, then testing both acceptance and denial paths independently.

**Acceptance Scenarios**:

1. **Given** user hasn't been asked about location permissions (permission not yet requested), **When** pet listings page loads, **Then** browser permission prompt requesting location access is automatically displayed immediately
2. **Given** browser permission prompt is displayed, **When** user clicks "Allow", **Then** app fetches current location and queries server with `?lat=LATITUDE&lng=LONGITUDE` parameters
3. **Given** browser permission prompt is displayed, **When** user clicks "Block" or dismisses prompt, **Then** app queries server without location parameters and displays all available pet listings
4. **Given** user denied permission via browser prompt, **When** pet listings are displayed, **Then** user can still browse all available pets (no location filtering)

---

### User Story 3 - Recovery Path for Blocked Permissions (Priority: P3)

Users who previously blocked location access should have a clear path to enable it through browser settings, with helpful guidance about why location enhances their experience.

**Why this priority**: Important for user retention and re-engagement. Users may change their mind about location sharing, and we should make it easy to enable.

**Independent Test**: Can be fully tested by launching app with blocked permission status (user selected "Block" in browser prompt) and verifying informational message appears with clear instructions for enabling location in browser settings.

**Acceptance Scenarios**:

1. **Given** user previously blocked location permission, **When** pet listings page loads, **Then** informational banner is displayed above pet listings explaining that location access is blocked and how to enable it
2. **Given** location permission is blocked, **When** informational banner is displayed, **Then** banner includes short benefit-focused message (e.g., "See pets available near you for easier adoption"), generic instructions for enabling location, and X button to close
3. **Given** informational banner about blocked permissions is displayed, **When** user clicks X button to dismiss banner, **Then** banner closes and pet listings remain visible without location filtering
4. **Given** user dismissed informational banner, **When** user reloads page with blocked permissions, **Then** informational banner is displayed again (no persistence of dismissal across page loads)

---

### Edge Cases

- What happens when location services are disabled system-wide (airplane mode, Location Services off)? → App queries server without location parameters and displays all available pet listings (fallback mode)
- How does system handle location fetch timeout or failure after permission is granted? → Implement 3-second timeout for location fetch. If timeout occurs or fetch fails, query server without location parameters (fallback mode)
- What happens if user navigates away while location is being fetched? → Cancel location fetch request to avoid unnecessary processing
- How does system handle location fetch when GPS signal is weak or unavailable? → App queries server without location parameters and displays all available pet listings (fallback mode)
- What happens on browsers that don't support Geolocation API? → App detects lack of support and queries server without location parameters (fallback mode, no error message needed)
- What happens when app is deployed on HTTP (non-HTTPS)? → Browser Geolocation API will be blocked by browser; app gracefully falls back to querying server without location parameters (shows all pets, no location filtering)
- How precise should the location be? → Round coordinates to 4 decimal places (provides ~11 meter accuracy, sufficient for pet location matching)
- What happens when pet listings API returns an error or times out? → Display error message with retry button allowing user to retry the query
- What happens when API succeeds but returns zero pets? → Display empty state message "No pets nearby"

## Out of Scope

- **Dynamic permission change handling**: App does not detect or respond to location permission changes made while the app is open (e.g., user changes settings in another tab). User must refresh the page to apply permission changes.
- **Continuous location tracking**: This feature only fetches location once on page load. Real-time location updates or tracking are not part of this feature.
- **Location-based push notifications**: Sending notifications based on user location is not part of this feature.
- **Location history or tracking**: The app does not store or track location history in browser storage or cookies; location is used only for the current query.
- **Custom location selection (map picker)**: User cannot manually select a different location; only device-reported location is used.
- **Location-based search radius control**: User cannot adjust the search radius; server determines filtering logic.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST detect current location permission status when pet listings page loads
- **FR-002**: System MUST fetch current user location using browser Geolocation API when permission is granted
- **FR-003**: System MUST automatically request location permission via browser native prompt immediately on page load when permission has not been requested yet
- **FR-004**: System MUST display informational banner when location permission is blocked, positioned above pet listings, with short benefit-focused message (e.g., "See pets available near you for easier adoption") and generic instructions for enabling location in browser settings
- **FR-004a**: Informational banner MUST be displayed every time the page loads when permission is blocked (no persistence of dismissal across page loads)
- **FR-004b**: Informational banner MUST include an X button allowing user to close/dismiss the banner
- **FR-005**: System MUST query server for pet listings regardless of location permission status
- **FR-006**: System MUST append `lat` and `lng` query parameters to pet listings API URL when location is successfully obtained, using URLSearchParams to properly construct the URL with existing parameters
- **FR-007**: System MUST query server without location parameters when location is unavailable, unauthorized, or fetch fails
- **FR-008**: System MUST implement 3-second timeout for location fetch. If timeout occurs or fetch fails, query server without location parameters
- **FR-009**: System MUST handle permission result after user responds to browser permission prompt (allow or block)
- **FR-010**: System MUST display pet listings after server query completes, regardless of whether location was included
- **FR-011**: Informational banner about blocked permissions MUST be dismissible via X button and not block user from viewing pet listings (banner positioned above listings)
- **FR-012**: System MUST display a full-page spinner/overlay blocking user interaction while fetching location and querying server, then display pet listings once data is ready
- **FR-013**: System MUST cancel location fetch request if user navigates away from page before location is obtained
- **FR-014**: System MUST detect browsers that don't support Geolocation API and query server without location parameters (fallback mode)
- **FR-015**: System MUST round location coordinates to 4 decimal places (provides ~11 meter accuracy, sufficient for pet location matching)
- **FR-016**: System MUST format location parameters as decimal degrees with 4 decimal places (e.g., `?lat=52.2297&lng=21.0122`)
- **FR-017**: System MUST display error message with retry button when pet listings API returns an error (e.g., 500, 503, timeout, network failure)
- **FR-018**: System MUST allow user to retry failed pet listings query by clicking retry button
- **FR-019**: System MUST display empty state message "No pets nearby" when pet listings API returns empty results (zero pets)

### Key Entities

- **Location Permission Status**: Represents current authorization state in browser (not yet requested, granted, blocked/denied)
- **User Location**: Geographic coordinates (latitude, longitude) obtained from browser Geolocation API when authorized
- **Pet Listings**: Collection of pet records returned from server query, potentially filtered by location when coordinates are provided in URL parameters

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of users can browse pet listings regardless of location permission status (no blocking UX)
- **SC-002**: Location fetch completes within 3 seconds or falls back to non-location mode in 100% of cases
- **SC-003**: Location coordinates are correctly formatted and appended to API URL when available (format: `?lat=XX.XXXX&lng=XX.XXXX` with 4 decimal places)
- **SC-004**: App handles all browser permission states (not requested, granted, blocked) without crashes or errors in 100% of test scenarios
