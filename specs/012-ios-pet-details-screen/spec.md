# Feature Specification: Pet Details Screen (iOS UI)

**Feature Branch**: `012-ios-pet-details-screen`  
**Created**: November 24, 2025  
**Status**: Draft  
**Input**: User description: "I want to have an animal details screen to which user can navigate by interacting with list item. In scope there is only UI part. Use this design: https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=179-8157&m=dev focusing on iOS platform. This spec is only for iOS."

## Clarifications

### Session 2025-11-21

- Q: What are all possible pet status values that need to be displayed? → A: MISSING, FOUND, CLOSED
- Q: What colors should be used for each status badge? → A: Red for MISSING, Blue for FOUND, Gray for CLOSED
- Q: Which fields are required vs optional? → A: Required: Photo, Status, Date of disappearance, Species, At least one of phone or email, Sex. All other fields are optional.
- Q: Should phone and email be masked? → A: No, display phone and email in full without masking
- Q: Should the "Remove Report" button be visible for all users or only for owners? → A: Show button always (permission handling will be done by backend)
- Q: How should long text be handled in different fields? → A: Truncate short data fields (species, breed, age, location) with ellipsis after 1-2 lines; allow full multi-line display for description field (Additional Description) with screen scrolling
- Q: What should be displayed as fallback when pet photo fails to load? → A: Gray box with "Image not available" text
- Q: What should be shown during initial data loading? → A: Full-screen spinner/progress indicator only
- Q: How should reward amount be formatted/displayed? → A: Display as-is (string field, no formatting needed)

### Session 2025-11-24

- Q: What are the performance constraints for screen loading time? → A: No efficiency constraints defined
- Q: Should the screen support iOS VoiceOver (screen reader for blind users)? → A: No, beyond standard test identifiers
- Q: What should happen when user taps "Show on the map" button? → A: Button enabled, prints tap to console (placeholder implementation)
- Q: What should happen when user taps "Remove Report" button? → A: Print tap to console (placeholder implementation)
- Q: How should the error state look when pet details fail to load? → A: Full-screen error message with retry button
- Q: How should the pet identifier (pet ID) reach the details screen? → A: Coordinator passes pet ID as String parameter during navigation initialization; ViewModel requests full details from repository in async init task
- Q: How should phone numbers be formatted for display? → A: Display as-is without formatting (valueProcessor = nil in reusable label-value component); no masking per earlier clarification
- Q: How should status values be mapped from API to display text? → A: ViewModel maps String status directly to display text (ACTIVE→"MISSING", FOUND→"FOUND", CLOSED→"CLOSED"); no domain enum needed
- Q: How should ViewModel structure the UI state for loading/loaded/error scenarios? → A: Use Swift enum with cases: .loading, .loaded(PetDetails), .error(String) (sealed state pattern)
- Q: Should phone and email be interactive (tappable to open dialer/mail app)? → A: Yes, make them tappable via optional onTap closure in label-value component (phone opens dialer, email opens mail composer)
- Q: How should retry button communicate with ViewModel to reload data? → A: Retry button calls ViewModel.loadPetDetails() method; same method used in init for DRY principle
- Q: How should the reusable label-value component receive its configuration? → A: Component accepts a model struct (defined as nested type in extension) passed via constructor; model contains label, value, optional valueProcessor, optional onTap closure
- Q: Should retry attempts be limited when pet details fail to load? → A: Unlimited retries - user can tap retry button indefinitely
- Q: Which icons should be used to represent male and female sex? → A: Use simple text symbols: "♂" (U+2642) for male, "♀" (U+2640) for female
- Q: What title should be displayed in the navigation bar? → A: Empty (no title)
- Q: What text should be displayed in the error message when pet details fail to load? → A: Unable to load pet details
- Q: Where should the reward badge be positioned on the pet photo? → A: Lower left corner; pet photo with overlays (status + reward badges) should be a separate reusable component with its own model struct and convenience init extension accepting PetDetails object

## User Scenarios & Testing *(mandatory)*

### User Story 1 - View Pet Details from List (Priority: P1)

Users can tap on a pet listing in the animal list to view comprehensive details about a missing or found pet, including photos, identification data, location information, and contact details.

**Why this priority**: This is the core functionality that enables users to access detailed information about pets. Without this, users cannot learn more about pets they see in the list, making the app ineffective.

**Independent Test**: Can be fully tested by navigating from a mock pet list to the details screen with sample data and verifying all information fields display correctly.

**Acceptance Scenarios**:

1. **Given** a user is viewing the pet list, **When** they tap on any pet list item, **Then** the coordinator navigates to the pet details screen passing the pet ID as a String parameter, and the ViewModel immediately begins fetching that pet's full details from the repository using the provided ID
2. **Given** a user is on the pet details screen, **When** they tap the back button in the navigation bar, **Then** they return to the pet list screen
3. **Given** the pet details data has loaded successfully, **When** the user views the screen, **Then** they see the pet's photo, status badge, and all available information fields displayed
4. **Given** the pet details screen is loading data, **When** the repository is fetching pet information, **Then** the user sees a full-screen loading indicator
5. **Given** the repository fails to fetch pet details, **When** the error occurs, **Then** the user sees a full-screen error message "Unable to load pet details" with a retry button
6. **Given** the user sees an error state, **When** they tap the retry button, **Then** the button calls ViewModel.loadPetDetails() method to re-attempt fetching pet details with unlimited retry attempts available

---

### User Story 2 - Review Pet Identification Information (Priority: P2)

Users can view critical identification information including microchip number, species, breed, sex, and approximate age to verify if a pet matches one they've found or lost.

**Why this priority**: Identification details are essential for verifying pet identity. This information helps users make informed decisions about contacting the owner or reporting a sighting.

**Independent Test**: Can be tested by loading the details screen with various identification data combinations and verifying each field displays with proper labels and formatting.

**Acceptance Scenarios**:

1. **Given** a pet has a microchip number, **When** viewing the details screen, **Then** the microchip number is displayed in the format "000-000-000-000"
2. **Given** a pet has species and breed information, **When** viewing the details screen, **Then** both are displayed side-by-side in a two-column layout
3. **Given** a pet has sex information, **When** viewing the details screen, **Then** the sex is displayed with a text symbol icon ("♂" for male or "♀" for female)

---

### User Story 3 - Access Location and Contact Information (Priority: P2)

Users can view where the pet was last seen or found, including city and radius, along with contact information for reaching the pet owner.

**Why this priority**: Location context helps users determine proximity and relevance. Contact information enables users to report sightings or arrange pet returns.

**Independent Test**: Can be tested by displaying location data with various city names and distances, and verifying contact information is properly displayed.

**Acceptance Scenarios**:

1. **Given** a pet has a disappearance location, **When** viewing the details screen, **Then** the city name and approximate radius are displayed (e.g., "Warsaw • ±15 km") with a location icon
2. **Given** a pet listing includes contact information, **When** viewing the details screen, **Then** the phone number is displayed in full (e.g., "+48 123 456 789") as a tappable link
3. **Given** a user taps on the phone number, **When** the tap occurs, **Then** iOS dialer opens with the phone number pre-filled
4. **Given** a pet listing includes an email, **When** viewing the details screen, **Then** the email is displayed in full (e.g., "owner@email.com") under "Contact owner" as a tappable link
5. **Given** a user taps on the email address, **When** the tap occurs, **Then** iOS mail composer opens with the email address pre-filled in the "To" field
6. **Given** a user wants to see the location on a map, **When** they tap the "Show on the map" button, **Then** the tap event is logged to console (placeholder for future map navigation)

---

### User Story 4 - Review Additional Pet Details (Priority: P3)

Users can read additional descriptive information about the pet, including physical description and behavior traits that help identify the animal.

**Why this priority**: Descriptive details provide context beyond structured data, helping users make accurate identifications. This is lower priority as core identification can happen through photos and basic data.

**Independent Test**: Can be tested by loading pets with various text descriptions and verifying multi-line text display and empty state handling.

**Acceptance Scenarios**:

1. **Given** a pet has an additional description, **When** viewing the details screen, **Then** the full multi-line description text is displayed under "Animal Additional Description"

---

### User Story 5 - View Reward Information (Priority: P3)

Users can see if a reward is offered for finding the pet, displayed prominently on the pet's photo.

**Why this priority**: Reward information can motivate users to help find missing pets. This is lower priority as it's supplementary information that doesn't affect core identification functionality.

**Independent Test**: Can be tested by displaying pets with and without rewards and verifying the reward badge appears correctly when present.

**Acceptance Scenarios**:

1. **Given** a pet listing has an associated reward, **When** viewing the details screen, **Then** a reward badge is overlaid in the lower left corner of the pet photo showing the reward text as-is (e.g., "500 PLN") with a money bag icon
2. **Given** a pet listing has no reward, **When** viewing the details screen, **Then** no reward badge is displayed

---

### User Story 6 - Identify Pet Status Visually (Priority: P2)

Users can immediately identify the pet's status (MISSING, FOUND, or CLOSED) through a prominent status badge displayed on the pet photo.

**Why this priority**: Status context is important for users to quickly understand the type of listing. Visual prominence ensures users don't miss this critical information.

**Independent Test**: Can be tested by displaying pets with all three status values (MISSING, FOUND, CLOSED) and verifying the correct badge color and text appear.

**Acceptance Scenarios**:

1. **Given** a pet is marked as MISSING, **When** viewing the details screen, **Then** a red "MISSING" badge is displayed in the upper right corner of the pet photo
2. **Given** a pet is marked as FOUND, **When** viewing the details screen, **Then** a blue "FOUND" badge is displayed in the upper right corner of the pet photo
3. **Given** a pet is marked as CLOSED, **When** viewing the details screen, **Then** a gray "CLOSED" badge is displayed in the upper right corner of the pet photo

---

### User Story 7 - Remove Pet Report (Priority: P3)

Users see a "Remove Report" button that will enable removal functionality in the future. Currently it serves as a UI placeholder.

**Why this priority**: This is an administrative action that's important for data hygiene but not part of the core viewing experience. Currently implemented as placeholder only (UI-only scope).

**Independent Test**: Can be tested by tapping the "Remove Report" button and verifying it logs the tap event to console.

**Acceptance Scenarios**:

1. **Given** a user is viewing any pet report, **When** they tap the "Remove Report" button, **Then** the tap event is logged to console (placeholder for future removal functionality)

---

### Edge Cases

- What happens when the pet photo fails to load or is missing? (Display a gray box with "Image not available" text as placeholder)
- What happens when optional fields have no data? (Display a dash "—" for: breed, approximate age, microchip number, location, additional description. Hide reward badge when no reward. Display only the available contact method when one of phone/email is missing.)
- What happens when text fields contain very long text? (Truncate short data fields with ellipsis after 1-2 lines: species, breed, sex, age, microchip number, location city. Allow full multi-line display with screen scrolling for: Additional Description.)
- What happens when the user has a slow network connection while loading the screen? (Show full-screen spinner/progress indicator until data loads)
- What happens if the user navigates to the details screen without selecting a specific pet? (Show error state or redirect to list)
- What happens when contact information format is invalid? (Display as-is with formatting errors, as validation is backend concern)
- What happens when location data is unavailable? (Button remains enabled but prints "No location data" to console)
- What happens when the repository fails to fetch pet details? (Show full-screen error message "Unable to load pet details" with retry button below navigation bar; unlimited retry attempts allowed)
- What happens when API returns ACTIVE status instead of MISSING? (ViewModel maps ACTIVE → "MISSING" display text; repository returns raw API status as String)

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Screen MUST display a hero image of the pet at the top of the screen, or a gray box with "Image not available" text if the photo fails to load
- **FR-002**: Screen MUST display a standard iOS navigation bar with back button that navigates to the previous screen; navigation bar MUST have empty title (no text)
- **FR-003**: Screen MUST display a status badge overlaid on the pet photo; ViewModel MUST map API status String to display text (ACTIVE→"MISSING", FOUND→"FOUND", CLOSED→"CLOSED") and corresponding badge colors (red for MISSING, blue for FOUND, gray for CLOSED)
- **FR-004**: Screen MUST display a reward badge in the lower left corner of the pet photo when reward information is available, showing the reward text as-is without formatting
- **FR-005**: Screen MUST display date of disappearance in the format "MMM DD, YYYY" (e.g., "Nov 18, 2025")
- **FR-006**: Screen MUST display two contact fields labeled "Contact owner" showing phone number and email in full without masking; phone number MUST be tappable to open iOS dialer; email MUST be tappable to open mail composer
- **FR-007**: Screen MUST display microchip number in the format "000-000-000-000"
- **FR-008**: Screen MUST display animal species and breed in a two-column grid layout
- **FR-009**: Screen MUST display animal sex and approximate age in a two-column grid layout
- **FR-010**: Screen MUST display sex with text symbol icon: "♂" (U+2642) for male or "♀" (U+2640) for female
- **FR-011**: Screen MUST display place of disappearance with city name, radius (e.g., "±15 km"), and location icon
- **FR-012**: Screen MUST provide a "Show on the map" button below the location information that prints tap event to console (placeholder implementation)
- **FR-013**: Screen MUST display multi-line animal additional description text without truncation (full display with screen scrolling)
- **FR-014**: Screen MUST truncate short data fields (species, breed, age, microchip, location city) with ellipsis if text exceeds 1-2 lines
- **FR-015**: Screen MUST display a "Remove Report" button at the bottom of the screen that prints tap event to console (placeholder implementation)
- **FR-016**: All interactive elements MUST have accessibility identifiers in the format "petDetails.element" (e.g., "petDetails.phone.tap", "petDetails.email.tap", "petDetails.showMap.button", "petDetails.removeReport.button", "petDetails.retry.button")
- **FR-017**: Screen MUST handle scrolling when content exceeds screen height
- **FR-018**: Screen MUST maintain proper spacing and layout according to the design specifications
- **FR-019**: ViewModel MUST expose UI state as Swift enum with cases: .loading (triggers full-screen spinner), .loaded(PetDetails) (displays content), .error(String) (shows error message with retry button)
- **FR-020**: Coordinator MUST pass pet ID as String parameter during navigation initialization; ViewModel MUST expose loadPetDetails() method that requests full pet details from repository using the provided ID; method called in async init task and by retry button
- **FR-021**: Repository WILL return mocked data for Phase 1 development (backend endpoint `GET /api/v1/announcements/:id` already exists, but integration is deferred to Phase 2)
- **FR-022**: Screen MUST use a reusable label-value component for displaying information pairs (e.g., "Date of Disappearance" + value, "Microchip number" + value); component MUST accept a model struct (defined as nested type in extension) via constructor containing: label: String, value: String, valueProcessor: ((String) -> String)? = nil, onTap: (() -> Void)? = nil; phone field uses onTap to open dialer, email field uses onTap to open mail composer; other fields use nil for non-interactive display
- **FR-023**: Screen MUST display a full-screen error message "Unable to load pet details" with retry button when pet details fail to load from repository; retry button MUST call ViewModel.loadPetDetails() method to re-attempt loading (same method used in init); retry button MUST remain enabled for unlimited retry attempts
- **FR-024**: Pet photo with overlaid badges (status + reward) MUST be implemented as a separate reusable SwiftUI view component; component MUST accept a model struct (defined as nested type in extension) via constructor containing: imageUrl: String, status: String, rewardText: String? (nil when no reward); component MUST provide a convenience init extension accepting PetDetails object to map properties automatically

### Key Entities

- **PetDetails**: Represents all information about a pet including identification data, location, contact information, and descriptive details. Key attributes include:
  - Photo URL (required) → maps to API `photoUrl`
  - Status (required String: API returns ACTIVE, FOUND, or CLOSED) → maps to API `status`; ViewModel transforms ACTIVE to "MISSING" display text
  - Date of disappearance (required) → maps to API `lastSeenDate`
  - Species (required) → maps to API `species`
  - Sex (required) → maps to API `gender`
  - Contact phone number (required: at least one of phone or email must be present) → maps to API `phone`
  - Contact email (required: at least one of phone or email must be present) → maps to API `email`
  - Reward amount (optional, string field displayed as-is) → NOT available in current API, will be mocked
  - Microchip number (optional) → NOT available in current API, will be mocked
  - Breed (optional) → maps to API `breed`
  - Approximate age (optional) → NOT available in current API, will be mocked
  - Disappearance location (optional: city, coordinates, radius) → maps to API `location` and `locationRadius` (coordinates not available in API)
  - Additional description (optional) → maps to API `description`
  
  **Note**: Backend endpoint `GET /api/v1/announcements/:id` already exists on main branch. In Phase 1, the iOS repository will provide mocked data structured according to this contract to enable independent UI development. Fields not available in the backend API (microchipNumber, approximateAge, reward) will be mocked locally. Phase 2 will integrate with the real API.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: All pet information fields render correctly on screens ranging from iPhone SE to iPad Pro
- **SC-002**: Screen is fully scrollable and all content is accessible without truncation or overlap
- **SC-003**: Screen layout matches the provided Figma design with pixel-perfect accuracy for spacing, typography, and component sizing
- **SC-004**: All interactive elements have proper accessibility identifiers enabling automated UI testing
