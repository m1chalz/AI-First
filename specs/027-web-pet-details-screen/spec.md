# Feature Specification: Pet Details Screen (Web UI)

**Feature Branch**: `027-web-pet-details-screen`  
**Created**: November 27, 2025  
**Status**: Draft  
**Input**: Create a web application version of the pet details screen that displays comprehensive pet information in a modal overlay. The modal is opened from the animal list page when users click the "Details" button on any animal card. This spec covers only the UI part, using the design at https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=168-4985&m=dev (from MVP web app section node-id=297-8719).

## Animal List Page Context

This modal is opened from the **Animal List Page** (design: node-id=168-4656). The list page structure includes:

### Page Layout
- **Sidebar**: Left sidebar (219px width, dark background #4F3C4C) for navigation
- **Content Area**: Main content (1181px width) containing header, filters, and cards list
- **Total Width**: 1440px

### Header
- **Left**: "PetSpot" title (Hind Regular, 24px, #2D2D2D)
- **Right**: "Report a Missing Animal" button (Primary blue #155DFC, 44px height, white text, always visible)

### Filters Section
- **Input Fields** (grid layout, 283px width each): Microchip number, Date of disappearance, Animal species, Animal approx. age, Animal race, Place of disappearance
- **Action Buttons**: "Clear" (outlined blue, 127px × 44px) and "Search" (primary blue, 138px × 44px)
- **Filter Chips**: Active filters displayed as chips below filters (e.g., "Male" with checkmark icon)

### Animal Cards
Each card (1180px width, 136px height, 14px border-radius, 1px border #E5E9EC) displays:
- **Left (192px)**: Pet photo (192px × 136px)
- **Middle (~792px)**: 
  - Location coordinates with icon (e.g., "52.2297° N, 21.0122° E") - Arial 16px, #4A5565
  - Species • Breed with gender icon (e.g., "Dog • Golden Retriever ♂") - Arial 16px, #101828
  - Description text (truncated) - Arial 16px, #4A5565
- **Right (144px)**:
  - Status badge (MISSING=red #FF0000, FOUND=blue #155DFC, pill-shaped, white text)
  - Date with calendar icon (e.g., "Nov 18, 2025") - Arial 16px, #6A7282
  - **"Details" button** (outlined blue #155DFC, 127px × 44px, border-radius 10px) - **opens this modal**

**See [animal-list-structure.md](./animal-list-structure.md) for complete list page structure details.**

## Clarifications

### Session 2025-11-27

- Q: What are all possible pet status values that need to be displayed? → A: MISSING, FOUND, CLOSED
- Q: What colors should be used for each status badge? → A: Red for MISSING, Blue for FOUND, Gray for CLOSED
- Q: Which fields are required vs optional? → A: Required: Photo, Status, Date of disappearance, Species, At least one of phone or email, Sex. All other fields are optional.
- Q: Should phone and email be masked? → A: No, display phone and email in full without masking
- Q: Should the "Remove Report" button be visible for all users or only for owners? → A: Show button always (permission handling will be done by backend)
- Q: How should long text be handled in different fields? → A: Truncate short data fields (species, breed, age, location) with CSS ellipsis after 1-2 lines; allow full multi-line display for description field (Additional Description) with page scrolling
- Q: What should be displayed as fallback when pet photo fails to load? → A: Gray box with "Image not available" text
- Q: What should be shown during initial data loading? → A: Full-page spinner/progress indicator only
- Q: How should reward amount be formatted/displayed? → A: Display as-is (string field, no formatting needed)
- Q: What browser compatibility is required? → A: Modern browsers (Chrome, Firefox, Safari, Edge - last 2 versions)
- Q: Should the page be responsive? → A: Yes, support mobile (320px+), tablet (768px+), and desktop (1024px+) viewports
- Q: How should navigation work? → A: Modal is opened from animal list page via "Details" button click, managed by React state (useState)
- Q: How is the modal opened from the animal list? → A: Each animal card has a "Details" button (outlined blue style) that opens the modal with that pet's information
- Q: What happens when user clicks "Details" button? → A: Modal opens over the list page, displaying comprehensive pet details (this spec)
- Q: How should the modal handle different API errors when fetching pet details? → A: Display one generic error message for all errors (e.g., "Failed to load pet details") with a "Retry" button
- Q: How long should the modal wait for API response before showing timeout error? → A: 10 seconds timeout before showing error
- Q: How should retry logic work after API error? → A: Unlimited retry attempts (user can retry multiple times, can always close modal)
- Q: How should the "Show on the map" button work? → A: Opens external map (Google Maps/OpenStreetMap) in new tab with coordinates marked
- Q: What should happen when pet photo fails to load (404, CORS, timeout)? → A: Display gray box with "Image not available" text (already specified in FR-001)
- Q: Should modal state be managed via React state or URL query parameter? → A: React state (useState) - simpler implementation, no URL changes, better for modals
- Q: How should phone number be displayed when not masked? → A: Display exactly as received from API (no additional formatting)
- Q: How should geographic coordinates be formatted for display? → A: Format in component (API returns raw numbers: latitude/longitude, component formats to "XX.XXXX° N/S, XX.XXXX° E/W")
- Q: How should microchip number be formatted for display? → A: Format in component (API returns raw number as string, component adds dashes to format "000-000-000-000")
- Q: How should date be formatted for display? → A: Format in component (API returns ISO 8601 date, component formats to "MMM DD, YYYY")

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Open Pet Details Modal from List (Priority: P1)

Users can click the "Details" button on any animal card in the list to view comprehensive details about that pet in a modal overlay, including photos, identification data, location information, and contact details.

**Why this priority**: This is the core functionality that enables users to access detailed information about pets. Without this, users cannot learn more about pets they see in the list, making the app ineffective.

**Independent Test**: Can be fully tested by clicking the "Details" button on a mock pet list card and verifying the modal opens with correct pet information.

**Acceptance Scenarios**:

1. **Given** a user is viewing the animal list page, **When** they click the "Details" button on any animal card, **Then** a modal dialog opens displaying that pet's full information over the list
2. **Given** the "Details" button is clicked, **When** the modal opens, **Then** the pet ID is passed to the modal component and pet details are fetched/displayed
3. **Given** a user is viewing the pet details modal, **When** they click the close button (X) or click outside the modal, **Then** the modal closes and they return to the animal list
4. **Given** a user is viewing the pet details modal, **When** they press the ESC key, **Then** the modal closes and they return to the animal list
5. **Given** the pet details data has loaded successfully, **When** the user views the modal, **Then** they see the pet's photo, status badge, and all available information fields displayed in a scrollable container
6. **Given** the modal is already open, **When** the user clicks another "Details" button, **Then** the current modal closes and a new modal opens with the new pet's details

---

### User Story 2 - Review Pet Identification Information (Priority: P2)

Users can view critical identification information including microchip number, species, breed, sex, and approximate age to verify if a pet matches one they've found or lost.

**Why this priority**: Identification details are essential for verifying pet identity. This information helps users make informed decisions about contacting the owner or reporting a sighting.

**Independent Test**: Can be tested by loading the details page with various identification data combinations and verifying each field displays with proper labels and formatting.

**Acceptance Scenarios**:

1. **Given** a pet has a microchip number, **When** viewing the details modal, **Then** the microchip number is formatted and displayed in the format "000-000-000-000" (component formats raw string from API by adding dashes)
2. **Given** a pet has species and breed information, **When** viewing the details modal, **Then** both are displayed side-by-side in a two-column layout (labeled "Animal Species" and "Animal Race")
3. **Given** a pet has sex information, **When** viewing the details modal, **Then** the sex is displayed with an appropriate icon (male/female symbol)

---

### User Story 3 - Access Location and Contact Information (Priority: P2)

Users can view where the pet was last seen or found, including precise geographic coordinates (latitude and longitude), along with contact information for reaching the pet owner.

**Why this priority**: Precise location coordinates help users determine exact proximity and relevance. Contact information enables users to report sightings or arrange pet returns. Geographic coordinates provide more accurate location data than city/radius approximations.

**Independent Test**: Can be tested by displaying location data with various coordinate values, and verifying contact information is properly displayed.

**Acceptance Scenarios**:

1. **Given** a pet has a disappearance location, **When** viewing the details modal, **Then** the latitude and longitude coordinates are displayed in the format "XX.XXXX° N/S, XX.XXXX° E/W" (e.g., "52.2297° N, 21.0122° E") with a location icon
2. **Given** a pet listing includes contact information, **When** viewing the details modal, **Then** the phone number is displayed exactly as received from API (may be masked by backend, e.g., "+ 48 ********") in the header row with phone icon
3. **Given** a pet listing includes an email, **When** viewing the details modal, **Then** the email is displayed in full (e.g., "mail@email.com") in the header row with email icon under "Contact owner"
4. **Given** a user wants to see the location on a map, **When** they click the "Show on the map" button next to coordinates, **Then** an external map (Google Maps/OpenStreetMap) opens in a new browser tab with the exact coordinates marked
5. **Given** a pet has no location coordinates available, **When** viewing the details modal, **Then** the location section is hidden or the "Show on the map" button is disabled

---

### User Story 4 - Review Additional Pet Details (Priority: P3)

Users can read additional descriptive information about the pet, including physical description and behavior traits that help identify the animal.

**Why this priority**: Descriptive details provide context beyond structured data, helping users make accurate identifications. This is lower priority as core identification can happen through photos and basic data.

**Independent Test**: Can be tested by loading pets with various text descriptions and verifying multi-line text display and empty state handling.

**Acceptance Scenarios**:

1. **Given** a pet has an additional description, **When** viewing the details modal, **Then** the full multi-line description text is displayed under "Animal Additional Description"
2. **Given** a pet has special features information, **When** viewing the details modal, **Then** the special features are displayed; if none, a dash "—" is shown

---

### User Story 5 - View Reward Information (Priority: P3)

Users can see if a reward is offered for finding the pet, displayed prominently on the pet's photo.

**Why this priority**: Reward information can motivate users to help find missing pets. This is lower priority as it's supplementary information that doesn't affect core identification functionality.

**Independent Test**: Can be tested by displaying pets with and without rewards and verifying the reward badge appears correctly when present.

**Acceptance Scenarios**:

1. **Given** a pet listing has an associated reward, **When** viewing the details modal, **Then** a reward badge is overlaid on the pet photo (left side) showing the reward text as-is (e.g., "Reward 500 PLN") with a money bag icon
2. **Given** a pet listing has no reward, **When** viewing the details modal, **Then** no reward badge is displayed

---

### User Story 6 - Identify Pet Status Visually (Priority: P2)

Users can immediately identify the pet's status (MISSING, FOUND, or CLOSED) through a prominent status badge displayed on the pet photo.

**Why this priority**: Status context is important for users to quickly understand the type of listing. Visual prominence ensures users don't miss this critical information.

**Independent Test**: Can be tested by displaying pets with all three status values (MISSING, FOUND, CLOSED) and verifying the correct badge color and text appear.

**Acceptance Scenarios**:

1. **Given** a pet is marked as MISSING, **When** viewing the details modal, **Then** a red "MISSING" badge is displayed in the upper right corner of the pet photo
2. **Given** a pet is marked as FOUND, **When** viewing the details modal, **Then** a blue "FOUND" badge is displayed in the upper right corner of the pet photo
3. **Given** a pet is marked as CLOSED, **When** viewing the details modal, **Then** a gray "CLOSED" badge is displayed in the upper right corner of the pet photo

---

### User Story 7 - Responsive Modal Adaptation (Priority: P1)

Users can access the pet details modal on any device (mobile, tablet, desktop) and experience an optimized layout for their screen size.

**Why this priority**: Web modals must be responsive to accommodate users on different devices. This is essential for accessibility and user experience across all platforms.

**Independent Test**: Can be tested by opening the modal at different viewport sizes (320px, 768px, 1024px, 1440px) and verifying layout adapts appropriately.

**Acceptance Scenarios**:

1. **Given** a user opens the modal on a mobile device (320px-767px), **When** the modal displays, **Then** it fills the entire screen (100% width/height) with full-screen layout and no backdrop visible
2. **Given** a user opens the modal on a tablet (768px-1023px), **When** the modal displays, **Then** it appears as a centered dialog (max-width 640px) with semi-transparent backdrop
3. **Given** a user opens the modal on a desktop (1024px+), **When** the modal displays, **Then** it appears as a centered dialog (max-width 768px) with semi-transparent backdrop and box shadow
4. **Given** a user clicks outside the modal or presses ESC key, **When** the modal is open, **Then** the modal closes and returns to the pet list

---

### Edge Cases

- What happens when the pet photo fails to load or is missing? (Display a gray box with "Image not available" text as placeholder in modal)
- What happens when optional fields have no data? (Display a dash "—" for: special features. Hide reward badge when no reward. Display only the available contact method when one of phone/email is missing.)
- What happens when text fields contain very long text? (Truncate short data fields with CSS ellipsis: species, race, age. Allow full multi-line display with modal scrolling for: Additional Description.)
- What happens when the user has a slow network connection while loading the modal? (Show spinner/progress indicator within modal until data loads or 10-second timeout is reached, then show error message with "Retry" button)
- What happens if modal is triggered without a valid pet ID? (Show generic error message "Failed to load pet details" with "Retry" button within modal)
- What happens when API request fails (404, 500, network error)? (Show generic error message "Failed to load pet details" with "Retry" button that attempts to reload pet data)
- What happens when contact information format is invalid? (Display as-is with formatting errors, as validation is backend concern)
- What happens when location coordinates are unavailable? (Hide or disable the "Show on the map" button)
- What happens when JavaScript is disabled? (Modal cannot open; fallback should direct users to enable JavaScript)
- What happens on very narrow viewports (<320px)? (Modal fills screen, maintain minimum usability with horizontal scroll if necessary)
- What happens when user clicks modal backdrop? (Modal closes and returns to pet list)
- What happens when user presses ESC key while modal is open? (Modal closes and returns to pet list)
- What happens to body scroll when modal is open? (Body scroll is locked to prevent background scrolling)
- What happens when modal content exceeds viewport height? (Modal content becomes scrollable while header remains fixed)
- What happens when user rapidly clicks multiple "Details" buttons? (Prevent duplicate modal opens, handle gracefully - close current and open new, or prevent action)
- What happens when modal is already open and user clicks another "Details" button? (Close current modal and open new one with new pet's details, or prevent action)
- What happens when pet ID is invalid or pet not found? (Show generic error message "Failed to load pet details" with "Retry" button)
- What happens when user clicks "Details" button while modal is loading? (Prevent opening new modal until current one finishes loading or closes)

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Modal MUST display a hero image of the pet at the top, or a gray box with "Image not available" text if the photo fails to load
- **FR-002**: Modal MUST display a close button (X icon) in the top-left corner that closes the modal
- **FR-003**: Modal MUST display a status badge (MISSING, FOUND, or CLOSED) overlaid on the pet photo with colors: red for MISSING, blue for FOUND, gray for CLOSED
- **FR-004**: Modal MUST display a reward badge on the pet photo (left side overlay) when reward information is available, showing "Reward [amount]" text with money bag icon
- **FR-005**: Modal MUST display date of disappearance formatted from ISO 8601 date (API returns ISO 8601 date, component formats to "MMM DD, YYYY" format, e.g., "Nov 18, 2025") with calendar icon in header row
- **FR-006**: Modal MUST display two contact fields in header row labeled "Contact owner": phone number (displayed exactly as received from API, may be masked by backend) with phone icon, and email with email icon
- **FR-007**: Modal MUST display microchip number formatted from raw string (API returns raw microchip number as string, component formats to "000-000-000-000" by adding dashes) as full-width field
- **FR-008**: Modal MUST display "Animal Species" and "Animal Race" in a two-column grid layout
- **FR-009**: Modal MUST display "Animal Sex" and "Animal Approx. Age" in a two-column grid layout
- **FR-010**: Modal MUST display sex with an appropriate icon (male or female symbol)
- **FR-011**: Modal MUST display latitude/longitude coordinates formatted from raw numbers (API returns latitude/longitude as numbers, component formats to "XX.XXXX° N/S, XX.XXXX° E/W" format, e.g., "52.2297° N, 21.0122° E") with location icon
- **FR-012**: Modal MUST provide a "Show on the map" button next to location coordinates (blue outlined, secondary style) that opens external map (Google Maps/OpenStreetMap) in a new browser tab with coordinates marked
- **FR-013**: Modal MUST display multi-line animal additional description text without truncation (full display with modal scrolling)
- **FR-014**: Modal MUST display "Special Features" field with content or dash "—" if empty
- **FR-015**: Modal MUST truncate short data fields (species, race, age) with CSS ellipsis if text exceeds available space
- **FR-016**: All interactive elements MUST have test identifiers using data-testid attribute in the format "petDetails.element"
- **FR-017**: Modal content MUST be scrollable when exceeding modal height while keeping header fixed
- **FR-018**: Modal MUST maintain proper spacing and layout according to the design specifications with appropriate padding (16-24px)
- **FR-019**: Modal MUST display a spinner/progress indicator while initial data is loading
- **FR-020**: Modal MUST be responsive and adapt layout for mobile (full-screen), tablet (centered, max-width 640px), and desktop (centered, max-width 768px) viewports
- **FR-021**: Modal MUST close when clicking the backdrop (outside modal content)
- **FR-022**: Modal MUST close when pressing the ESC key
- **FR-023**: Modal MUST trap keyboard focus within modal when open (tab navigation cycles within modal)
- **FR-024**: Modal MUST be keyboard accessible with proper focus management for all interactive elements
- **FR-025**: Modal MUST support modern browsers (Chrome, Firefox, Safari, Edge - last 2 versions)
- **FR-026**: Modal MUST prevent body scroll when open (body scroll lock)
- **FR-027**: Modal MUST display semi-transparent backdrop on tablet/desktop; full-screen on mobile
- **FR-028**: Modal MUST be opened from the animal list page when user clicks the "Details" button on any animal card
- **FR-029**: Modal MUST receive pet ID from the list page to fetch and display the correct pet's details
- **FR-030**: Modal MUST be controlled by React state (useState) to handle open/close state and selected pet ID (no URL query parameter changes)
- **FR-031**: When modal opens, focus MUST be moved to the modal content (first focusable element or modal container)
- **FR-032**: When modal closes, focus MUST return to the "Details" button that triggered the modal opening
- **FR-033**: When API request fails (404, 500, network error), modal MUST display a generic error message "Failed to load pet details" with a "Retry" button that attempts to reload pet data
- **FR-034**: Error state MUST replace modal content (hide spinner and content, show error message and "Retry" button)
- **FR-035**: API request MUST timeout after 10 seconds if no response is received, displaying the generic error message with "Retry" button
- **FR-036**: "Retry" button MUST allow unlimited retry attempts (user can retry multiple times, can always close modal via close button or ESC key)

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
  - Disappearance location (optional: latitude and longitude coordinates as numbers, formatted in component for display)
  - Additional description (optional)

- **AnimalListPage**: The page that displays the list of animal announcements and contains the "Details" buttons that open this modal. Key structure includes:
  - **Sidebar**: Left sidebar (219px width, dark background #4F3C4C) for navigation
  - **Header**: "PetSpot" title (Hind Regular, 24px, left) + "Report a Missing Animal" button (Primary blue #155DFC, 44px height, right, top-right area, always visible)
  - **Filters Section**: Input fields arranged in grid (283px width each):
    - Microchip number (optional) - Text input
    - Date of disappearance - Date picker
    - Animal species - Dropdown
    - Animal approx. age (optional) - Dropdown
    - Animal race - Dropdown (may be disabled)
    - Place of disappearance - Text input
    - "Clear" button (outlined blue, 127px × 44px)
    - "Search" button (primary blue, 138px × 44px)
  - **Filter Chips**: Active filters displayed as chips below filters (e.g., "Male" with checkmark icon, "Missing", "Found")
  - **Animal Cards**: Scrollable list of cards (1180px width, 136px height, 14px border-radius, 1px border #E5E9EC, 8px gap), each containing:
    - **Left Section (192px)**: Pet photo (192px × 136px) with fallback placeholder
    - **Middle Section (~792px)**: 
      - Row 1: Location icon (16px) + coordinates (e.g., "52.2297° N, 21.0122° E") - Arial 16px, #4A5565
      - Row 2: Species + "•" + Breed + Gender icon (e.g., "Dog • Golden Retriever ♂") - Arial 16px, #101828
      - Row 3: Description text (truncated if long) - Arial 16px, #4A5565
    - **Right Section (144px)**:
      - Status badge (MISSING=red #FF0000, FOUND=blue #155DFC, pill-shaped, white text, 16px)
      - Date with calendar icon (e.g., "Nov 18, 2025") - Arial 16px, #6A7282
      - **"Details" button** (outlined blue #155DFC, 127px × 44px, border-radius 10px, Hind Regular 16px) - opens this modal

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: All pet information fields render correctly in modal across mobile (320px+), tablet (768px+), and desktop (1024px+) viewports with appropriate responsive behaviors
- **SC-002**: Modal content is fully scrollable and all content is accessible without truncation or overlap on all supported viewport sizes
- **SC-003**: Modal layout matches the provided Figma design with high fidelity for spacing, typography, component sizing, and backdrop/shadow effects
- **SC-004**: All interactive elements have proper test identifiers enabling automated UI testing
- **SC-005**: Modal achieves a Lighthouse accessibility score of 90+ for keyboard navigation, focus trap, and screen reader compatibility
- **SC-006**: Modal opens and displays initial content within 2 seconds on a standard broadband connection (10 Mbps)
- **SC-007**: Modal properly locks body scroll, traps focus, and closes via ESC key or backdrop click on all supported browsers

