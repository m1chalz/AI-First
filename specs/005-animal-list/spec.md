# Feature Specification: Animal List Screen

**Feature Branch**: `005-animal-list`  
**Created**: 2025-11-19  
**Status**: Draft  
**Input**: User description: "Chcemy stworzyć pierwszy ekran aplikacji. W Figma jest to layer Animal List. Na razie chcemy stworzyć tylko UI, wszelka interakcja z backendem będzie później, natomiast stwórz metody w viewmodelach i zamokuj wywołania. Nie mamy jeszcze listy, ale przygotuj widok, aby wyświetlał skolowalną listę. Przycisk na dole ma być poza listą i widoczny zawsze. U góry nad listą będzie komponent do searcha - nie mamy go jeszcze ale weź to pod uwagę."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - View Animal List (Priority: P1)

**As a** user, **I can** view a scrollable list of available animals, **so that** I know which pets are missing in my area and can identify animals I may have seen.

**Why this priority**: This is the core functionality of the screen and the foundation for all other features. Without the ability to display animals, no other interactions are possible.

**Independent Test**: Can be fully tested by opening the application and verifying that a scrollable list of animals is displayed with mock data. Delivers immediate value by showing users what animals are available in the system.

**Acceptance Scenarios**:

1. **Given** the application is launched, **When** the Animal List screen loads, **Then** a scrollable list of animals is displayed with mock data
2. **Given** the Animal List screen is displayed, **When** the user scrolls through the list, **Then** the list scrolls smoothly and all items are accessible
3. **Given** there are multiple animals in the list, **When** the user scrolls to the bottom, **Then** the scrolling stops at the last item
4. **Given** the Animal List screen is displayed, **When** the user views the screen, **Then** the layout matches the Figma design for "Animal List" layer
5. **Given** an animal card is displayed in the list, **When** the user taps on the card, **Then** the system responds with a mocked action (console.log with animal ID OR placeholder navigation - future iteration will navigate to detail screen)

---

### User Story 2 - Report Missing Animal Action (Priority: P2)

Users need a way to report missing animals to the system. The "Report a Missing Animal" button to initiate this action should be prominently displayed and always accessible.

**Why this priority**: Reporting missing animals is a critical secondary function. While viewing the list is essential, users also need to be able to report new missing animals. This is prioritized after viewing because you need to see what exists before adding more.

**Independent Test**: Can be fully tested by verifying that the "Report a Missing Animal" button is visible at all times (even when scrolling the list) and that tapping it triggers the expected action (even if mocked). Delivers value by making it clear how to report missing animals.

**Acceptance Scenarios**:

1. **Given** the Animal List screen is displayed, **When** the user views the bottom of the screen, **Then** the "Report a Missing Animal" button is visible
2. **Given** the user is scrolling through the animal list, **When** the list scrolls up or down, **Then** the "Report a Missing Animal" button remains fixed at the bottom and visible at all times
3. **Given** the "Report a Missing Animal" button is visible, **When** the user taps the button, **Then** the system responds with a mocked action (console.log OR placeholder navigation - future iteration will navigate to report form screen)

---

### User Story 3 - Search Preparation (Priority: P3)

The screen layout must accommodate a future search component at the top of the list. While the search functionality is not yet implemented, the UI structure should reserve space and be designed with this future addition in mind.

**Why this priority**: This is preparatory work for a future feature. While important for the overall UX design, it doesn't deliver immediate user value. It ensures we don't need to redesign the layout later.

**Independent Test**: Can be tested by verifying that the layout has appropriate spacing at the top of the list where the search component will be added. Delivers value by preventing future UI refactoring.

**Acceptance Scenarios**:

1. **Given** the Animal List screen is displayed, **When** the user views the top of the screen, **Then** there is appropriate space reserved for a future search component
2. **Given** the screen layout includes space for search, **When** the user scrolls the list, **Then** the reserved search space remains fixed at the top of the screen (above the scrollable list)

---

### Edge Cases

- What happens when the list is empty (no animals available)? The screen should display the empty state message: "No animals reported yet. Tap 'Report a Missing Animal' to add the first one."
  - **Note**: In MVP with mock data, app always launches with 16 mock animals (simulating successful backend response). Empty state will activate when real backend integration returns empty list. For QA testing, empty state verified by temporarily modifying mock repository to return emptyList().
- What happens when there is only one animal in the list? The UI should still render correctly without layout issues.
- What happens when there are many animals (20-30+ items)? The list should remain performant and scrollable with smooth 60 FPS scrolling (best practice, not strict requirement).

**Mock Data Configuration**:
- **MVP default**: 16 items (fixed) - standard app launch, ensures scrolling behavior visible
- **Performance testing**: 20-30 items - verify smooth 60 FPS scrolling
- **Stress testing**: 100+ items - verify no crashes or memory issues
- **Implementation**: Mock repository generates 16 animals by default. For testing scenarios, repository can be configured with optional count parameter: `generateAnimals(count: Int = 16)`
- What happens on different screen sizes (small phones, tablets)? The layout should be responsive and the "Report a Missing Animal" button should remain accessible.
- What happens when the user rapidly taps the "Report a Missing Animal" button? The system should prevent duplicate actions or handle them gracefully.

**Platform Button Differences** (see L168-L171 for full design specs):

| Platform | Button(s) | Position | Always Visible |
|----------|-----------|----------|----------------|
| Mobile (Android/iOS) | Single: "Report a Missing Animal" | Bottom (fixed, outside scroll area) | ✅ Yes |
| Web | Two: "Report a Missing Animal" (primary) + "Report Found Animal" (secondary) | Top-right (fixed at top of content area) | ✅ Yes |

Note: Mobile design (Figma node 48:6096) intentionally shows only the primary action button. Web design (Figma node 71:9154) provides both actions at the top for desktop UX patterns.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST display a scrollable list of animals with mock data (16 items fixed for MVP)
- **FR-002**: System MUST display the "Report a Missing Animal" button at the bottom of the screen (mobile: single button; web: two buttons at top-right per L168-L171)
- **FR-003**: The "Report a Missing Animal" button MUST remain visible and accessible at all times, regardless of list scroll position (mobile: fixed at bottom; web: fixed at top-right)
- **FR-004**: System MUST reserve appropriate space at the top of the screen for a future search component (Android: 48-56dp height with 16dp padding; iOS: 48-56pt height with 16pt padding; web: 64px height full-width with search bar 582px + filters per L168)
- **FR-005**: The screen layout MUST match the "Animal List" design from Figma (mobile platforms use node 48:6096; web platform uses node 71:9154 per L100-L102 and L154-L171)
- **FR-006**: System MUST provide ViewModel/presentation logic for loading animal data, even if currently using mock data sources
- **FR-007**: System MUST provide ViewModel/presentation logic for handling "Report a Missing Animal" button actions, even if currently mocked
- **FR-008**: The list MUST be scrollable when content exceeds the visible screen area
- **FR-009**: System MUST display an empty state when no animals are available with the message: "No animals reported yet. Tap 'Report a Missing Animal' to add the first one."
  - **Note**: Mock data (16 items) simulates successful backend response. Empty state will display when real backend returns empty list (future integration). Current MVP always shows mock animals; empty state tested by temporarily clearing repository data during QA.
- **FR-010**: This screen MUST serve as the primary entry point of the application on all platforms (Android, iOS, Web)
- **FR-011**: System MUST provide ViewModel/presentation logic for handling animal card tap actions, even if currently mocked (will navigate to detail screen in future iterations)

### Key Entities *(include if feature involves data)*

- **Animal**: Represents an animal in the system. Based on the Figma design, each animal includes:
  - Name: name of the animal (e.g., "Fluffy", "Rex") - **REQUIRED**
  - Photo URL: URL or placeholder for animal photo (circular placeholder in mock version) - **OPTIONAL**
  - Location: city name with radius (e.g., "Pruszkow, +5km") - **REQUIRED**
  - Species: animal type (e.g., "Cat", "Dog") - **REQUIRED**
  - Breed: specific breed name (e.g., "Maine Coon", "German Shepherd") - **REQUIRED**
  - Gender: male or female (shown as icon on web version) - **REQUIRED**
  - Status: "Active", "Found", or "Closed" - **REQUIRED**
  - Last Seen Date: date when animal was last seen or found (format: DD/MM/YYYY) - **REQUIRED**
  - Description: detailed text description (visible on web version) - **OPTIONAL**
  - Email: contact email (optional, shown in detail view only) - **OPTIONAL**
  - Phone: contact phone number (optional, shown in detail view only) - **OPTIONAL**

### Dependencies

- **Figma Design**: The "Animal List" layer in Figma ([link](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=48-6096)) serves as the source of truth for visual design
  - Mobile version: node-id=48:6096
  - Web version: node-id=71:9154
- **Mock Data**: Initial implementation uses mock data (8-12 animals with varied species, breeds, statuses) until backend integration is completed. UI placeholders (e.g., circular image placeholders) are visual elements, not data.

### Assumptions

- Users are familiar with standard list navigation patterns (scrolling, tapping)
- App launches with splash screen (existing), then navigates to Animal List as the main screen (no login/onboarding required in MVP)
- The "Report a Missing Animal" button will navigate to a report form screen in future iterations (NOT IMPLEMENTED in this feature - button action is currently mocked with console.log or placeholder navigation)
- Tapping on an animal card will navigate to a detail screen in future iterations (currently mocked; push navigation on mobile, React Router in-app navigation on web using navigate(`/animal/${id}`) - no modal, no new tab)
- Search functionality will be added in a future sprint, but the current layout accommodates its addition without major restructuring

## Clarifications

### Session 2025-11-19

- Q: "Add button" terminology - which button(s) does this refer to (Report a Missing Animal, Report Found Animal, or both)? → A: Option A - "Add button" refers ONLY to "Report a Missing Animal" (primary button)
- Q: What should the empty state message say when no animals are in the list? → A: "No animals reported yet. Tap 'Report a Missing Animal' to add the first one."
- Q: How many animals should be displayed in the mock data list? → A: 16 items (fixed for MVP - forces scrolling, tests different statuses, ensures sufficient content visibility)
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

**Typography (platform-specific units):**
- Screen title: Inter 24sp (Android), 24pt (iOS), 24px (Web) Regular
- Card animal name: Inter 16sp (Android), 16pt (iOS), 16px (Web) Regular
- Card species/breed: Inter 14sp (Android), 14pt (iOS), 14px (Web) Regular
- Card location: Inter 13sp (Android), 13pt (iOS), 13px (Web) Regular
- Status badge: Roboto 12sp (Android), 12pt (iOS), 12px (Web) Regular
- Button labels: Inter 16sp (Android), 16pt (iOS), 16px (Web) Regular

**Layout & Spacing (platform-specific units):**
- Card spacing: 8dp (Android), 8pt (iOS), 8px (Web)
- Card padding: 16dp horizontal (Android), 16pt (iOS), 16px (Web), varies vertical
- Card border radius: 4dp (Android), 4pt (iOS), 4px (Web)
- Card shadow: elevation 2dp (Android), 1pt offset with 4pt blur (iOS), 0px 1px 4px rgba(0,0,0,0.05) (Web)
- Status badge radius: 10dp (Android), 10pt (iOS), 10px (Web)
- Button border radius: 2dp (Android), 2pt (iOS), 2px (Web)
- Image placeholder: circular (border-radius: 50% for perfect circle; Android: 63dp, iOS: 63pt, Web: 63px)

**Mobile Specific:**
- **Android**: Layout uses density-independent pixels (dp) - Search/Filters button: 186dp wide, Cards: full-width with 16dp horizontal margins, circular image placeholder (63dp), 8dp card spacing
- **iOS**: Layout uses points (pt) - Search/Filters button: 186pt wide, Cards: full-width with 16pt horizontal margins, circular image placeholder (63pt), 8pt card spacing
- Screen title: "Missing animals list" (centered, 24sp Android / 24pt iOS) - Note: Internal feature name is "Animal List Screen", displayed title is "Missing animals list" per Figma
- Cards: horizontal layout showing circular image placeholder, animal name, location with pin icon, species | breed, status badge, last seen date
- Floating action button at bottom (outside scroll area):
  - "Report a Missing Animal" (dark background, primary) - **Fixed at bottom, always visible**
  - Note: "Report Found Animal" button not present in initial mobile design

**Web Specific (1440px width):**
- Sidebar navigation: 219px wide, dark background (#4F3C4C)
- Content area: 1181px wide
- Screen title aligned left with action buttons on right
- Two action buttons at top-right: "Report a Missing Animal" (primary), "Report Found Animal" (secondary) - **Fixed at top, always visible**
- Search/Filters area: full-width with search bar on left (582px) and filter controls on right
- Cards: full-width, table-like layout with additional description text column, gender icons, and more options menu (3 dots)
- Note: Web layout differs from mobile - buttons positioned at top of content area, not bottom

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: The "Report a Missing Animal" button is accessible within a single tap from any list scroll position
- **SC-002**: The screen layout matches the Figma "Animal List" design with 100% visual accuracy:
  - All colors match the specified hex values exactly
  - Typography uses Inter and Roboto fonts at specified sizes and weights (sp for Android, pt for iOS, px for Web)
  - Spacing matches the specified measurements (8dp card gap Android, 16dp padding Android; 8pt/16pt iOS; 8px/16px Web)
  - Shadows, border radius, and other visual effects replicate the Figma design per platform guidelines
- **SC-003**: The empty state displays the message "No animals reported yet. Tap 'Report a Missing Animal' to add the first one." and is immediately understandable to users
