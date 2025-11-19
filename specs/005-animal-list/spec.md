# Feature Specification: Animal List Screen

**Feature Branch**: `005-animal-list`  
**Created**: 2025-11-19  
**Status**: Draft  
**Input**: User description: "Chcemy stworzyć pierwszy ekran aplikacji. W Figma jest to layer Animal List. Na razie chcemy stworzyć tylko UI, wszelka interakcja z backendem będzie później, natomiast stwórz metody w viewmodelach i zamokuj wywołania. Nie mamy jeszcze listy, ale przygotuj widok, aby wyświetlał skolowalną listę. Przycisk na dole ma być poza listą i widoczny zawsze. U góry nad listą będzie komponent do searcha - nie mamy go jeszcze ale weź to pod uwagę."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - View Animal List (Priority: P1)

Users need to see a list of available animals as the main entry point to the application. This is the primary screen that users interact with when they open the app.

**Why this priority**: This is the core functionality of the screen and the foundation for all other features. Without the ability to display animals, no other interactions are possible.

**Independent Test**: Can be fully tested by opening the application and verifying that a scrollable list of animals is displayed with mock data. Delivers immediate value by showing users what animals are available in the system.

**Acceptance Scenarios**:

1. **Given** the application is launched, **When** the Animal List screen loads, **Then** a scrollable list of animals is displayed with mock data
2. **Given** the Animal List screen is displayed, **When** the user scrolls through the list, **Then** the list scrolls smoothly and all items are accessible
3. **Given** there are multiple animals in the list, **When** the user scrolls to the bottom, **Then** the scrolling stops at the last item
4. **Given** the Animal List screen is displayed, **When** the user views the screen, **Then** the layout matches the Figma design for "Animal List" layer
5. **Given** an animal card is displayed in the list, **When** the user taps on the card, **Then** the system responds with a mocked action (placeholder for future navigation to detail screen)

---

### User Story 2 - Report Missing Animal Action (Priority: P2)

Users need a way to report missing animals to the system. The "Report a Missing Animal" button to initiate this action should be prominently displayed and always accessible.

**Why this priority**: Reporting missing animals is a critical secondary function. While viewing the list is essential, users also need to be able to report new missing animals. This is prioritized after viewing because you need to see what exists before adding more.

**Independent Test**: Can be fully tested by verifying that the "Report a Missing Animal" button is visible at all times (even when scrolling the list) and that tapping it triggers the expected action (even if mocked). Delivers value by making it clear how to report missing animals.

**Acceptance Scenarios**:

1. **Given** the Animal List screen is displayed, **When** the user views the bottom of the screen, **Then** the "Report a Missing Animal" button is visible
2. **Given** the user is scrolling through the animal list, **When** the list scrolls up or down, **Then** the "Report a Missing Animal" button remains fixed at the bottom and visible at all times
3. **Given** the "Report a Missing Animal" button is visible, **When** the user taps the button, **Then** the system responds with a mocked action (placeholder for future navigation to report form screen)

---

### User Story 3 - Search Preparation (Priority: P3)

The screen layout must accommodate a future search component at the top of the list. While the search functionality is not yet implemented, the UI structure should reserve space and be designed with this future addition in mind.

**Why this priority**: This is preparatory work for a future feature. While important for the overall UX design, it doesn't deliver immediate user value. It ensures we don't need to redesign the layout later.

**Independent Test**: Can be tested by verifying that the layout has appropriate spacing at the top of the list where the search component will be added. Delivers value by preventing future UI refactoring.

**Acceptance Scenarios**:

1. **Given** the Animal List screen is displayed, **When** the user views the top of the screen, **Then** there is appropriate space reserved for a future search component
2. **Given** the screen layout includes space for search, **When** the user scrolls the list, **Then** the reserved space remains part of the scrollable content area (or fixed, depending on design)

---

### Edge Cases

- What happens when the list is empty (no animals available)? The screen should display the empty state message: "No animals reported yet. Tap 'Report a Missing Animal' to add the first one."
- What happens when there is only one animal in the list? The UI should still render correctly without layout issues.
- What happens when there are hundreds of animals? The list should remain performant and scrollable.
- What happens on different screen sizes (small phones, tablets)? The layout should be responsive and the "Report a Missing Animal" button should remain accessible.
- What happens when the user rapidly taps the "Report a Missing Animal" button? The system should prevent duplicate actions or handle them gracefully.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST display a scrollable list of animals with mock/placeholder data (8-12 items)
- **FR-002**: System MUST display the "Report a Missing Animal" button at the bottom of the screen
- **FR-003**: The "Report a Missing Animal" button MUST remain visible and accessible at all times, regardless of list scroll position
- **FR-004**: System MUST reserve appropriate space at the top of the screen for a future search component
- **FR-005**: The screen layout MUST match the "Animal List" design from Figma
- **FR-006**: System MUST provide business logic for loading animal data, even if currently using mock data sources
- **FR-007**: System MUST provide business logic for handling "Report a Missing Animal" button actions, even if currently mocked
- **FR-008**: The list MUST be scrollable when content exceeds the visible screen area
- **FR-009**: System MUST display an empty state when no animals are available with the message: "No animals reported yet. Tap 'Report a Missing Animal' to add the first one."
- **FR-010**: This screen MUST serve as the primary entry point of the application on all platforms (Android, iOS, Web)
- **FR-011**: System MUST provide business logic for handling animal card tap actions, even if currently mocked (will navigate to detail screen in future iterations)

### Key Entities *(include if feature involves data)*

- **Animal**: Represents an animal in the system. Based on the Figma design, each animal includes:
  - Name: name of the animal (e.g., "Fluffy", "Rex")
  - Photo URL: URL or placeholder for animal photo (circular placeholder in mock version)
  - Location: city name with radius (e.g., "Pruszkow, +5km")
  - Species: animal type (e.g., "Cat", "Dog")
  - Breed: specific breed name (e.g., "Maine Coon", "German Shepherd")
  - Gender: male or female (shown as icon on web version)
  - Status: "Active", "Found", or "Closed"
  - Last Seen Date: date when animal was last seen or found (format: DD/MM/YYYY)
  - Description: detailed text description (visible on web version)
  - Email: contact email (optional, shown in detail view only)
  - Phone: contact phone number (optional, shown in detail view only)

### Dependencies

- **Figma Design**: The "Animal List" layer in Figma ([link](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=48-6096)) serves as the source of truth for visual design
  - Mobile version: node-id=48:6096
  - Web version: node-id=71:9154
- **Mock Data**: Initial implementation uses placeholder/mock data (8-12 animals with varied species, breeds, statuses) until backend integration is completed

### Assumptions

- Users are familiar with standard list navigation patterns (scrolling, tapping)
- The "Report a Missing Animal" button will navigate to a report form screen in future iterations (currently mocked)
- Tapping on an animal card will navigate to a detail screen in future iterations (currently mocked; push navigation on mobile, analogous pattern on web)
- Search functionality will be added in a future sprint, but the current layout accommodates its addition without major restructuring
- Performance targets (60 FPS, 2-second load time) are based on standard mobile/web application expectations

## Clarifications

### Session 2025-11-19

- Q: "Add button" terminology - which button(s) does this refer to (Report a Missing Animal, Report Found Animal, or both)? → A: Option A - "Add button" refers ONLY to "Report a Missing Animal" (primary button)
- Q: What should the empty state message say when no animals are in the list? → A: "No animals reported yet. Tap 'Report a Missing Animal' to add the first one."
- Q: How many animals should be displayed in the mock data list? → A: Option C - 8-12 items (forces scrolling, tests different statuses)
- Q: What happens when user taps on an animal card in the list? → A: Option A - Navigate to detail screen (pushed on mobile, analogous navigation on web; implemented in future iteration)

## Design Specifications *(from Figma)*

### Visual Design Details

**Color Palette:**
- Primary text: #2D2D2D
- Secondary text: #545F71
- Tertiary text: #93A2B4
- Background white: #FAFAFA
- Light gray (placeholders): #EEEEEE
- Status "Active": #FF0000 (red badge)
- Status "Found": #0074FF (blue badge)
- Status "Closed": #93A2B4 (gray badge)
- Primary action button: #2D2D2D (dark)
- Secondary action button: #E5E9EC (light gray)

**Typography:**
- Screen title: Inter 24px Regular
- Card animal name: Inter 16px Regular
- Card species/breed: Inter 14px Regular
- Card location: Inter 13px Regular
- Status badge: Roboto 12px Regular
- Button labels: Inter 16px Regular

**Layout & Spacing:**
- Card spacing: 8px gap between items
- Card padding: 16px horizontal, varies vertical
- Card border radius: 4px
- Card shadow: 0px 1px 4px 0px rgba(0,0,0,0.05)
- Status badge radius: 10px
- Button border radius: 2px
- Image placeholder: circular (large border-radius)

**Mobile Specific (375px width):**
- Screen title: "Missing animals list" (centered, 24px)
- Search/Filters button: 186px wide, outlined style
- Cards: full-width (328px), horizontal layout
- Each card shows: circular image placeholder (63x63), animal name, location with pin icon, species | breed, status badge, last seen date
- Floating action buttons at bottom (outside scroll area):
  - "Report Found Animal" (light background, secondary)
  - "Report a Missing Animal" (dark background, primary)

**Web Specific (1440px width):**
- Sidebar navigation: 219px wide, dark background (#4F3C4C)
- Content area: 1181px wide
- Screen title aligned left with action buttons on right
- Two action buttons at top: "Report a Missing Animal" (primary), "Report Found Animal" (secondary)
- Search/Filters area: full-width with search bar on left (582px) and filter controls on right
- Cards: full-width, table-like layout with additional description text column, gender icons, and more options menu (3 dots)

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: The "Report a Missing Animal" button is accessible within a single tap from any list scroll position
- **SC-002**: The screen layout matches the Figma "Animal List" design with 100% visual accuracy:
  - All colors match the specified hex values exactly
  - Typography uses Inter and Roboto fonts at specified sizes and weights
  - Spacing matches the 8px card gap, 16px padding, and other specified measurements
  - Shadows, border radius, and other visual effects replicate the Figma design
- **SC-003**: The empty state displays the message "No animals reported yet. Tap 'Report a Missing Animal' to add the first one." and is immediately understandable to users
