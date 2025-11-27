# Feature Specification: Pet Details Screen

**Feature Branch**: `010-pet-details-screen`  
**Created**: November 21, 2025  
**Status**: Draft  
**Scope**: Multi-platform specification covering Android UI implementation (primary) and iOS implementation notes

> **Note**: This specification documents the Pet Details Screen feature across both Android and iOS platforms. Android implementation is the primary focus (branch 010-pet-details-screen). iOS implementation details are documented separately with platform-specific differences noted throughout.

## Clarifications

### Session 2025-11-26 (Android)

- Q: What should the error state UI look like when pet data fails to load? → A: Full-screen error view with message and "Try Again" button
- Q: What should happen when the user taps "Show on the map"? → A: Launch Google Maps (or other map app) via Android Intent with coordinates
- Q: Is the "Remove Report" button included in scope? → A: No, the Remove Report button has been removed from the design and is not in scope for Android

### Session 2025-11-21 (Shared)

- Q: What are all possible pet status values that need to be displayed? → A: MISSING, FOUND, CLOSED
- Q: What colors should be used for each status badge? → A: Red for MISSING, Blue for FOUND, Gray for CLOSED
- Q: Which fields are required vs optional? → A: Required: Photo, Status, Date of disappearance, Species, At least one of phone or email, Sex. All other fields are optional.
- Q: Should phone and email be masked? → A: No, display phone and email in full without masking (design shows masked phone as placeholder/mock data only)
- Q: How should long text be handled in different fields? → A: Truncate short data fields (species, breed, age, location) with ellipsis after 1-2 lines; allow full multi-line display for description field (Additional Description) with screen scrolling
- Q: What should be displayed as fallback when pet photo fails to load? → A: Gray box with "Image not available" text
- Q: What should be shown during initial data loading? → A: Full-screen spinner/progress indicator only
- Q: How should reward amount be formatted/displayed? → A: Display as-is (string field, no formatting needed)

## User Scenarios & Testing *(mandatory)*

### User Story 1 - View Pet Details from List (Priority: P1)

Users can tap on a pet listing in the animal list to view comprehensive details about a missing or found pet, including photos, identification data, location information, and contact details.

**Why this priority**: This is the core functionality that enables users to access detailed information about pets. Without this, users cannot learn more about pets they see in the list, making the app ineffective.

**Independent Test**: Can be fully tested by navigating from a mock pet list to the details screen with sample data and verifying all information fields display correctly.

**Acceptance Scenarios**:

1. **Given** a user is viewing the pet list, **When** they tap on any pet list item, **Then** they are navigated to the pet details screen showing that pet's full information
2. **Given** a user is on the pet details screen, **When** they tap the back button in the header, **Then** they return to the pet list screen
3. **Given** the pet details data has loaded successfully, **When** the user views the screen, **Then** they see the pet's photo, status badge, and all available information fields displayed

---

### User Story 2 - Review Pet Identification Information (Priority: P2)

Users can view critical identification information including microchip number, species, breed, sex, and approximate age to verify if a pet matches one they've found or lost.

**Why this priority**: Identification details are essential for verifying pet identity. This information helps users make informed decisions about contacting the owner or reporting a sighting.

**Independent Test**: Can be tested by loading the details screen with various identification data combinations and verifying each field displays with proper labels and formatting.

**Acceptance Scenarios**:

1. **Given** a pet has a microchip number, **When** viewing the details screen, **Then** the microchip number is displayed in the format "000-000-000-000"
2. **Given** a pet has species and breed information, **When** viewing the details screen, **Then** both are displayed side-by-side in a two-column layout
3. **Given** a pet has sex information, **When** viewing the details screen, **Then** the sex is displayed with an appropriate icon (male/female symbol)

---

### User Story 3 - Access Location and Contact Information (Priority: P2)

Users can view where the pet was last seen or found, including latitude and longitude coordinates, along with contact information for reaching the pet owner.

**Why this priority**: Location context helps users determine proximity and relevance. Contact information enables users to report sightings or arrange pet returns.

**Independent Test**: Can be tested by displaying location data with various coordinate values and verifying contact information is properly displayed.

**Acceptance Scenarios**:

1. **Given** a pet has a disappearance location, **When** viewing the details screen, **Then** the latitude and longitude coordinates are displayed (e.g., "52.2297° N, 21.0122° E") with a location icon
2. **Given** a pet listing includes contact information, **When** viewing the details screen, **Then** the phone number is displayed in full (e.g., "+48 123 456 789")
3. **Given** a pet listing includes an email, **When** viewing the details screen, **Then** the email is displayed in full (e.g., "owner@email.com") under "Contact owner"
4. **Given** a user wants to see the location on a map, **When** they tap the "Show on the map" button, **Then** an external map app (Google Maps or default) is launched via Android Intent with the disappearance coordinates

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

1. **Given** a pet listing has an associated reward, **When** viewing the details screen, **Then** a reward badge is overlaid on the pet photo showing the reward text as-is (e.g., "500 PLN") with a money bag icon
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

### Edge Cases

- What happens when the pet photo fails to load or is missing? (Display a gray box with "Image not available" text as placeholder)
- What happens when optional fields have no data? (Display a dash "—" for: breed, approximate age, microchip number, location, additional description. Hide reward badge when no reward. Display only the available contact method when one of phone/email is missing.)
- What happens when text fields contain very long text? (Truncate short data fields with ellipsis after 1-2 lines: species, breed, sex, age, microchip number, location coordinates. Allow full multi-line display with screen scrolling for: Additional Description.)
- What happens when the user has a slow network connection while loading the screen? (Show full-screen spinner/progress indicator until data loads)
- What happens if the user navigates to the details screen without selecting a specific pet? (Show full-screen error view with message and "Try Again" button)
- What happens when the API call fails (network error, timeout, server error)? (Show full-screen error view with error message and "Try Again" button that retries the data load)
- What happens when contact information format is invalid? (Display as-is with formatting errors, as validation is backend concern)
- What happens when location data is unavailable? (Disable the "Show on the map" button)
- What happens when user taps "Show on the map"? (Launch external map app via Android Intent with coordinates; if no map app is available, show a toast/snackbar message)

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Screen MUST display a hero image of the pet at the top of the screen, or a gray box with "Image not available" text if the photo fails to load
- **FR-002**: Screen MUST display a back button in the header that navigates to the previous screen
- **FR-003**: Screen MUST display a status badge (MISSING, FOUND, or CLOSED) overlaid on the pet photo with colors: red for MISSING, blue for FOUND, gray for CLOSED
- **FR-004**: Screen MUST display a reward badge on the pet photo when reward information is available, showing the reward text as-is without formatting
- **FR-005**: Screen MUST display date of disappearance in the format "MMM DD, YYYY" (e.g., "Nov 18, 2025")
- **FR-006**: Screen MUST display two contact fields labeled "Contact owner" showing phone number and email in full without masking
- **FR-007**: Screen MUST display microchip number in the format "000-000-000-000"
- **FR-008**: Screen MUST display animal species and breed in a two-column grid layout
- **FR-009**: Screen MUST display animal sex and approximate age in a two-column grid layout
- **FR-010**: Screen MUST display sex with an appropriate icon (male or female symbol)
- **FR-011**: Screen MUST display latitude and longitude coordinates (e.g., "52.2297° N, 21.0122° E") with a location icon
- **FR-012**: Screen MUST provide a "Show on the map" button below the location information that launches an external map app (Google Maps or device default) via Android Intent with the pet's coordinates
- **FR-013**: Screen MUST display multi-line animal additional description text without truncation (full display with screen scrolling)
- **FR-014**: Screen MUST truncate short data fields (species, breed, age, microchip, location coordinates) with ellipsis if text exceeds 1-2 lines
- **FR-015**: All interactive elements MUST have test identifiers in the format "petDetails.element"
- **FR-016**: Screen MUST handle scrolling when content exceeds screen height
- **FR-017**: Screen MUST maintain proper spacing and layout according to the design specifications
- **FR-018**: Screen MUST display a full-screen spinner/progress indicator while initial data is loading
- **FR-019**: Screen MUST display a full-screen error view with error message and "Try Again" button when data loading fails (network error, timeout, server error)

### Key Entities

- **PetDetails**: Represents all information about a pet including identification data, location, contact information, and descriptive details. Key attributes include:
  - Photo URL (required)
  - Status (required: MISSING, FOUND, or CLOSED)
  - Date of disappearance (required)
  - Species (required)
  - Sex (required)
  - Contact phone number (required: at least one of phone or email must be present)
  - Contact email (required: at least one of phone or email must be present)
  - Reward amount (optional, string field displayed as-is)
  - Microchip number (optional)
  - Breed (optional)
  - Approximate age (optional)
  - Disappearance location (optional: latitude, longitude coordinates)
  - Additional description (optional)

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: All pet information fields render correctly on screens ranging from small (320dp width) to extra-large (600dp+ width) Android devices
- **SC-002**: Screen is fully scrollable and all content is accessible without truncation or overlap
- **SC-003**: Screen layout matches the provided Figma design with pixel-perfect accuracy for spacing, typography, and component sizing
- **SC-004**: All interactive elements have proper test identifiers enabling automated UI testing

---

## Platform-Specific Implementation Notes

### Android Implementation (Primary - Branch 010)

**Scope Decisions**:
- ✅ Remove Report button: NOT IN SCOPE (removed from Android design)
- ✅ Contact phone: Display in FULL (unmasked)
- ✅ Location display: Latitude/Longitude coordinates format
- ✅ Map launch: Via Android Intent to Google Maps

**Technical Requirements**:
- Use Jetpack Compose for UI
- Implement MVI architecture with ViewModels
- Use Koin for dependency injection
- Coroutines + Flow for async operations
- JUnit + Kotlin Test + Turbine for unit tests

**Design Reference**: https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-7437&m=dev

### iOS Implementation (Branch 012)

**Scope Differences**:
- ✅ Remove Report button: IN SCOPE (included in iOS design)
- ✅ Contact phone: Display with MASKING for privacy
- ✅ Location display: Place name with radius (e.g., "Warsaw • ±15 km")
- ✅ Map launch: Via MapKit or system maps app

**Technical Requirements**:
- Use SwiftUI for UI
- Implement MVVM-C architecture with UIKit Coordinators
- Manual dependency injection with ServiceContainer
- Swift Concurrency (async/await)
- XCTest for unit tests

**Design Reference**: https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=179-8157&m=dev

### Shared Requirements (Both Platforms)

- Pet photo display with "Image not available" fallback
- Status badge (Red/MISSING, Blue/FOUND, Gray/CLOSED)
- Reward badge display (if present)
- All identification fields (species, breed, sex, age, microchip)
- Date of disappearance in format "MMM DD, YYYY"
- Loading state during data fetch
- Error state with retry capability
- Scrollable content layout for longer descriptions
- Test identifiers on all interactive elements
