# Feature Specification: iOS Announcements API Integration

**Feature Branch**: `036-ios-announcements-api`  
**Created**: 2025-12-01  
**Status**: Draft  
**Input**: User description: "Display data from backend API on Animal list and pet details ios screens. Connect to GET /api/v1/announcements and GET /api/v1/announcements/:id endpoints. ios platform only."

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

- What happens when backend API returns malformed JSON or unexpected data structure?
- What happens when backend returns announcement with missing required fields?
- How does the app handle extremely long description text (10,000+ characters)?
- What happens if backend returns duplicate announcement IDs in the list response?
- How does the app handle photo URLs that point to non-existent images?
- What happens when user's internet connection drops during data fetch?
- How does the app handle announcements with coordinates outside valid ranges (latitude > 90°)?
- What happens if backend returns more than 1000 announcements in a single response?
- How does the app handle special characters and emojis in pet names and descriptions?
- What happens when user rapidly switches between animal list and details screens (race conditions)?

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
- **FR-011**: The app MUST handle network timeout scenarios (no response after reasonable time)
- **FR-012**: The app MUST parse photo URLs from backend and pass them to image loading components
- **FR-013**: iOS platform only - Android and Web platforms are NOT in scope

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

