# Feature Specification: iOS Announcements API Integration

**Feature Branch**: `036-ios-announcements-api`  
**Created**: 2025-12-01  
**Status**: Draft  
**Input**: User description: "Display data from backend API on Animal list and pet details ios screens. Connect to GET /api/v1/announcements and GET /api/v1/announcements/:id endpoints. ios platform only."

## Clarifications

### Session 2025-12-01

- Q: What happens when backend API returns malformed JSON or unexpected data structure? → A: Log error (print), show generic error message to user (no analytics tracking)
- Q: What happens when backend returns announcement with missing required fields? → A: Skip invalid items if detectable (Option B), otherwise fail entire list with error (Option D) when Codable cannot decode
- Q: How does the app handle extremely long description text (10,000+ characters)? → A: Out of scope - no UI changes in this story, existing UI handles text display as currently implemented
- Q: What happens if backend returns duplicate announcement IDs in the list response? → A: Deduplicate by ID (keep first or last occurrence depending on implementation simplicity), log warning (print)
- Q: How does the app handle photo URLs that point to non-existent images? → A: Out of scope - existing image loading components handle failures as currently implemented
- Q: What is the network timeout duration for API requests? → A: Use URLSession system default timeout value (no custom timeout configuration)
- Q: What happens if backend returns more than 1000 announcements in a single response? → A: Load all data into memory, iOS UI handles scrolling (backend should implement pagination/limits if needed)
- Q: How does the app handle announcements with coordinates outside valid ranges (latitude > 90°)? → A: Out of scope - backend validates coordinates, iOS does not validate
- Q: How does the app handle special characters and emojis in pet names and descriptions? → A: Display exactly as returned by backend (UTF-8, emoji supported natively by Swift/SwiftUI)
- Q: What happens when user rapidly switches between animal list and details screens (race conditions)? → A: Cancel previous request before starting new one (task cancellation via async/await)
- Q: Does backend API require HTTPS and authentication/authorization? → A: HTTP allowed (insecure), no authentication - development/testing environment only
- Q: Should the app automatically retry failed API requests due to transient network errors? → A: No retry strategy - show error message only, user must navigate away and return to retry
- Q: Does this feature require adding new accessibility identifiers to Animal List and Pet Details components? → A: Out of scope - existing UI components already have accessibility identifiers
- Q: What is the backend base URL and configuration strategy for different environments? → A: http://localhost:3000 (development), easily configurable from code

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Display Real Announcements on Animal List (Priority: P1)

Users browsing the animal list should see actual announcements from the backend database instead of hardcoded mock data. This ensures users see current, real-world pet reports.

**Why this priority**: This is the most critical feature as it connects the iOS app to live data, making the app functional for real users. Without this, users only see fake data.

**Independent Test**: Can be fully tested by launching the Animal List screen and verifying that displayed pets match the backend database content. Delivers immediate value by showing real pet announcements.

**Acceptance Scenarios**:

1. **Given** the backend has 5 pet announcements in the database, **When** user opens the Animal List screen, **Then** the app displays exactly those 5 pets with their correct details (name, species, status, photo, location)
2. **Given** user has location permissions granted, **When** user opens Animal List, **Then** the app sends user's coordinates to backend and displays location-filtered results
3. **Given** user has location permissions denied, **When** user opens Animal List, **Then** the app fetches all announcements without location filtering
4. **Given** the backend database is empty, **When** user opens Animal List, **Then** the app displays the empty state view with "No animals found" message
5. **Given** backend API is unavailable, **When** user opens Animal List, **Then** the app displays a clear error message explaining the connection problem

---

### User Story 2 - Display Real Pet Details (Priority: P2)

Users tapping on an animal card should see detailed information fetched from the backend API. This allows users to view complete pet information including contact details, description, and last seen date.

**Why this priority**: Essential for users to get complete information about a specific pet. Depends on P1 being functional but can be developed and tested independently with a known pet ID.

**Independent Test**: Can be tested by navigating to Pet Details screen with a valid pet ID from the backend and verifying all fields match the database record. Delivers value by showing complete pet information.

**Acceptance Scenarios**:

1. **Given** user selects an animal card from the list, **When** the details screen loads, **Then** the app displays complete pet information fetched from GET /api/v1/announcements/:id endpoint
2. **Given** pet has all optional fields populated (breed, microchip, email, reward), **When** details screen loads, **Then** all fields are displayed correctly
3. **Given** pet has some optional fields missing (no breed, no email), **When** details screen loads, **Then** the app displays "—" or appropriate placeholder for missing fields
4. **Given** backend API returns 404 for a pet ID, **When** details screen loads, **Then** the app displays "Pet not found" error with option to go back
5. **Given** backend API is slow to respond, **When** details screen is loading, **Then** the app shows a loading indicator until data arrives or timeout occurs

---

### User Story 3 - Refresh Data After Creating Announcement (Priority: P3)

Users who just submitted a new pet announcement through the report flow should see their announcement appear in the list when they return to the Animal List screen.

**Why this priority**: Provides immediate feedback and confirmation that the submission was successful. Enhances user experience but the app is functional without it.

**Independent Test**: Can be tested by completing the "Report Missing Pet" flow and verifying the new announcement appears in the refreshed animal list. Delivers value by confirming successful submission.

**Acceptance Scenarios**:

1. **Given** user successfully submits a new pet announcement, **When** user returns to Animal List screen, **Then** the app automatically refreshes and displays the new announcement
2. **Given** user creates multiple announcements, **When** viewing Animal List, **Then** all user's announcements are visible in the list
3. **Given** user's new announcement is outside the location filter radius, **When** viewing Animal List with location filtering, **Then** the announcement may not appear (correct behavior - respects location filter)

---

### Edge Cases

- **Malformed JSON or unexpected data structure**: App logs error (print statement), displays generic error message to user ("Unable to load data. Please try again later."), does not crash
- **Missing required fields in announcement**: If Codable can decode partial list, skip invalid items and show valid announcements only (log error for invalid items). If Codable fails on entire response, fail list loading and show error message to user
- **Extremely long description text (10,000+ characters)**: Out of scope - existing UI handles display, no changes to UI in this feature
- **Duplicate announcement IDs in list response**: Deduplicate by ID (keep first or last occurrence, whichever is simpler to implement), log warning (print)
- **Photo URLs pointing to non-existent images**: Out of scope - existing image loading components handle failures as currently implemented
- **Network timeout or connection drop during fetch**: URLSession will use system default timeout and return error; app displays generic error message to user with no automatic retry (user must navigate away and return to retry manually)
- **Large response with 1000+ announcements**: iOS loads all data into memory, LazyVStack/List handles scrolling performance; backend responsible for pagination if needed
- **Invalid coordinates (latitude > 90° or longitude > 180°)**: Out of scope - backend validates and ensures valid coordinate ranges, iOS does not perform validation
- **Special characters and emojis in pet names/descriptions**: Display exactly as returned by backend; Swift String and SwiftUI Text natively support UTF-8 and emoji rendering
- **Rapid screen switching / race conditions**: When user quickly switches between screens (e.g., Animal List → Pet Details → another Pet Details), cancel previous API request before starting new one using async/await task cancellation to prevent stale data display
- **Backend base URL configuration**: Default to `http://localhost:3000` for development; HTTP allowed due to local development environment (iOS ATS exception required); base URL must be easily changeable from code for different environments

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: iOS app MUST call GET /api/v1/announcements endpoint to fetch the list of pet announcements instead of using mock data
- **FR-002**: iOS app MUST call GET /api/v1/announcements/:id endpoint to fetch detailed information for a specific pet instead of using mock data
- **FR-003**: When location permissions are granted, the app MUST send user's latitude and longitude as query parameters (lat, lng) to the announcements endpoint
- **FR-004**: When location permissions are denied or unavailable, the app MUST call the announcements endpoint without location parameters
- **FR-005**: The app MUST map backend API response fields to iOS domain models (Animal, PetDetails)
- **FR-006**: The app MUST handle HTTP error responses (400, 404, 500) and display appropriate error messages to users
- **FR-007**: The app MUST show loading indicators while waiting for API responses
- **FR-008**: The app MUST handle missing optional fields in API responses (breed, email, microchipNumber, reward) by displaying placeholders
- **FR-009**: The app MUST validate that required fields exist in API responses before attempting to create domain models
- **FR-010**: The app MUST use the existing AnimalRepositoryProtocol interface without modifying the protocol signature
- **FR-011**: The app MUST handle network timeout scenarios using URLSession system default timeout (typically 60 seconds for resource, 7 days for request)
- **FR-012**: The app MUST parse photo URLs from backend and pass them to image loading components
- **FR-013**: iOS platform only - Android and Web platforms are NOT in scope
- **FR-014**: The app MUST allow HTTP connections (insecure transport) for development/testing environment without authentication headers
- **FR-015**: The app MUST use `http://localhost:3000` as default backend base URL, with easy code-level configurability for changing the URL without recompilation

### Key Entities *(include if feature involves data)*

- **Announcement (Backend)**: Represents a pet announcement stored in backend database, includes all pet information, contact details, and metadata (createdAt, updatedAt)
- **Animal (iOS Domain)**: Client-side model representing an animal for list display, contains essential fields like name, species, status, photo URL, location
- **PetDetails (iOS Domain)**: Client-side model representing complete pet information for detail view, includes all fields from Animal plus additional details like microchip, reward, coordinates
- **API Response Wrapper**: Backend wraps list endpoint response in `{ data: [...] }` structure, while detail endpoint returns single object directly

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users see real pet announcements from the backend database within 2 seconds of opening the Animal List screen (under normal network conditions)
- **SC-002**: Users can view complete details of any pet by tapping its card, with data loaded within 1.5 seconds
- **SC-003**: 100% of required fields from backend API are correctly displayed in both list and detail views
- **SC-004**: Users receive clear, actionable error messages when backend is unavailable or data cannot be loaded
- **SC-005**: The app remains responsive and does not freeze or crash when fetching data from the backend
- **SC-006**: Location-aware filtering works correctly - users with location enabled see nearby announcements, users without location see all announcements
- **SC-007**: Users who submit new announcements see their submissions appear in the list when they return to the Animal List screen

