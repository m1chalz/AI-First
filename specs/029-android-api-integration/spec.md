# Feature Specification: Android Backend API Integration

**Feature Branch**: `029-android-api-integration`  
**Created**: 2025-11-28  
**Status**: Draft  
**Input**: User description: "Display data from backend API on AnimalListScreen and PetDetailsScreen Android screens. Connect to GET /api/v1/announcements and GET /api/v1/announcements/:id endpoints. Android platform only."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - View List of Pet Announcements (Priority: P1)

A user opens the PetSpot Android app to browse available pet announcements. The app displays a scrollable list of all current pet listings (missing and found animals) retrieved from the backend server. Each listing shows essential information at a glance: pet photo, name, species, status badge, and last seen location.

**Why this priority**: This is the primary entry point to the app. Without the ability to view announcements from the backend, users cannot access any meaningful content. This provides the core value proposition.

**Independent Test**: Can be fully tested by launching the app and verifying that the list displays real data from the server with proper loading states.

**Acceptance Scenarios**:

1. **Given** the user opens the app with network connectivity, **When** the animal list screen loads, **Then** the system retrieves pet announcements from the backend and displays them in a scrollable list with photo, name, species, status badge, and location for each entry.

2. **Given** the backend has no announcements, **When** the animal list screen loads, **Then** the system displays an empty state message indicating no pets are currently listed.

---

### User Story 2 - View Pet Details (Priority: P1)

A user taps on a pet listing from the animal list to view comprehensive details about that specific announcement. The details screen shows all available information: full-size photo, pet identification details (name, species, breed, sex, age), microchip number if available, last seen date and location with map option, contact information, description, reward amount (if offered), and status.

**Why this priority**: Viewing details is essential for users to take action (contact owner, recognize a pet). Without details, the list alone doesn't provide enough information for meaningful engagement.

**Independent Test**: Can be tested by tapping any pet card and verifying all details are retrieved from the backend and displayed correctly.

**Acceptance Scenarios**:

1. **Given** the user is on the animal list screen, **When** the user taps on a pet card, **Then** the system navigates to the details screen and retrieves full pet information from the backend by announcement ID.

2. **Given** the user is viewing pet details, **When** the pet has a reward amount set, **Then** the reward information is prominently displayed.

3. **Given** the user is viewing pet details, **When** the pet has location coordinates available, **Then** the "Show on Map" button is enabled and functional.

4. **Given** the user is viewing pet details, **When** the pet has contact information (phone or email), **Then** the contact details are displayed for user action.

---

### User Story 3 - Handle Network Errors Gracefully (Priority: P2)

A user attempts to use the app when network connectivity is unavailable or the backend server is unreachable. The app displays appropriate error messages and provides retry functionality, maintaining a good user experience even during failures.

**Why this priority**: Network errors are common in mobile scenarios. Graceful error handling ensures users understand what's happening and can recover when connectivity is restored.

**Independent Test**: Can be tested by disabling network connectivity and verifying error states and retry functionality work correctly.

**Acceptance Scenarios**:

1. **Given** the user opens the app without network connectivity, **When** the animal list fails to load, **Then** the system displays a user-friendly error message with a retry button.

2. **Given** the user taps on a pet card, **When** the details request fails due to network error, **Then** the system displays an error state with a retry option.

3. **Given** an error state is displayed, **When** the user taps the retry button, **Then** the system attempts to fetch data again from the backend.

4. **Given** any HTTP error occurs (4xx or 5xx response), **When** the request fails, **Then** the system displays a generic error message without exposing technical details or distinguishing error types.

---

### User Story 4 - Experience Responsive Loading States (Priority: P3)

A user interacts with the app and sees appropriate loading indicators while data is being fetched from the backend, ensuring they understand the app is working and not frozen.

**Why this priority**: Loading states are important for perceived performance and user trust, but the app can function without polished loading states during initial development.

**Independent Test**: Can be tested by observing loading indicators during data fetch operations.

**Acceptance Scenarios**:

1. **Given** the app is fetching the announcements list, **When** the request is in progress, **Then** a loading indicator is displayed until data arrives or an error occurs.

2. **Given** the user navigates to pet details, **When** the details are being fetched, **Then** a loading indicator is shown until data is loaded.

---

### Edge Cases

- **Stale data (404)**: Treated as generic error - user sees error message with retry option (same as any HTTP error).
- **Large announcement lists**: Loaded all at once (no pagination in initial implementation).
- **Navigation during request**: Standard coroutine cancellation - request cancelled, no error shown.
- **Null optional fields**: Display fallback values or hide sections (e.g., no reward section if reward is null).
- **Invalid photo URL**: Display placeholder image (existing image loading handles this).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST retrieve the list of pet announcements from the backend server when the animal list screen is displayed.
- **FR-002**: System MUST retrieve detailed pet information from the backend server when a user navigates to the pet details screen.
- **FR-003**: System MUST display a loading indicator while data is being fetched from the backend.
- **FR-004**: System MUST display a user-friendly error message when backend requests fail.
- **FR-005**: System MUST provide retry functionality for failed requests.
- **FR-006**: System MUST map backend response fields to the existing domain model format used by the screens.
- **FR-007**: System MUST handle optional fields (petName, breed, age, microchipNumber, reward, description) gracefully when they are null or absent in API responses.
- **FR-008**: System MUST preserve all existing screen functionality (navigation, map integration, status badges).
- **FR-009**: System MUST NOT display the management password field returned by the API (security requirement per API documentation).
- **FR-010**: System MUST coerce any unknown backend announcement statuses to `AnimalStatus.MISSING` for display.

### Key Entities

- **Announcement**: Represents a pet listing (missing or found) with identification details, location, contact information, and status. Maps to the existing `Animal` domain model in the app.
- **Location**: Geographic coordinates (latitude, longitude) where the pet was last seen.
- **Status**: The current state of the announcement (MISSING or FOUND).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: All mock animals are replaced with real server data - no hardcoded data remains in the production app flow.
- **SC-002**: Error states display when network is unavailable.
- **SC-003**: Retry functionality successfully reloads data when network connectivity is restored.
- **SC-004**: All existing screen features (navigation, badges, map button, contact display) continue to work correctly with backend data.
- **SC-005**: Unit test coverage for data fetching and mapping logic meets the project's 80% threshold.

## Clarifications

### Session 2025-11-28

- Q: How should 4xx errors (including 404 for deleted announcements) be handled? → A: All 4xx errors treated uniformly as generic errors, same as 5xx - no differentiation in initial implementation.
- Q: How should announcements with statuses outside MISSING/FOUND be displayed? → A: Treat every unknown status as `MISSING` on Android until additional states are designed.

## Assumptions

- The backend server is available and running at a known base URL (configuration assumed to be provided).
- The existing Animal domain model structure is compatible with the API response structure (only mapping implementation needed).
- Network permissions are already configured in the Android app.
- The app's MVI architecture and dependency injection setup remain unchanged - only the repository implementation is replaced.
- Image loading from URLs is already handled by the existing photo display components.
