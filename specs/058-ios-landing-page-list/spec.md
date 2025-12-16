# Feature Specification: iOS Landing Page

**Feature Branch**: `058-ios-landing-page-list`  
**Created**: 2025-12-16  
**Status**: Draft  
**Input**: User description: "potrzebuję landing page, będzie to rozwijane w przyszłości, na tę chwilę będzie to ekran wyświetlający 5 pierwszych elementów pobranych z announcements. Elementy powinny wyglądac tak samo jak na announcements list, korzystać z tego samego endpointa co announcements list. Ekran wyświetla się na tabie home. Po kliknięciu w element listy, przenosimy się do taba z listą zaginionych zwierząt (lost pets, announcement list) i otwieramy szczegóły klikniętego zwierzęcia."

## Clarifications

### Session 2025-12-16

- Q: How should the iOS app limit results to 5 announcements - via backend query parameters or client-side filtering? → A: Option A - Backend zwraca wszystkie ogłoszenia, iOS filtruje i sortuje client-side (limit 5, sort by date)
- Q: What loading strategy should be used during navigation from Home tab to Pet Details? → A: Option B - Instant tab switch + loading indicator on detail screen (standard iOS pattern)
- Q: What elements should the empty state contain when no announcements are available? → A: Option B - Tekst + ilustracja/ikona pustego stanu
- Q: Should landing page support data refresh on tab return or pull-to-refresh? → A: No refresh functionality in this iteration - data loads only once on first appearance
- Q: Should the app prevent duplicate/rapid taps on announcement cards? → A: No duplicate tap prevention in this iteration

## User Scenarios & Testing *(mandatory)*

### User Story 1 - View Recent Pet Announcements on Home Tab (Priority: P1)

Users opening the app land on a Home tab that displays the 5 most recent pet announcements. This gives users immediate visibility into recent pet reports without requiring navigation to the full list.

**Why this priority**: This is the core landing page functionality that provides immediate value to users by showing recent activity. It's the first thing users see when opening the app and helps them quickly assess if there are relevant announcements.

**Independent Test**: Can be fully tested by launching the app, landing on the Home tab, and verifying that 5 most recent announcements are displayed with correct details (name, species, status, photo, location). Delivers value by providing quick access to recent pet reports.

**Acceptance Scenarios**:

1. **Given** the backend has 10 pet announcements in the database, **When** user opens the app and lands on Home tab, **Then** the app displays exactly the 5 most recent announcements sorted by creation date (newest first)
2. **Given** the backend has less than 5 announcements (e.g., 3 announcements), **When** user opens Home tab, **Then** the app displays all available announcements (3 in this case)
3. **Given** the backend database is empty, **When** user opens Home tab, **Then** the app displays an empty state view with "No recent announcements" message and an illustrative icon/image
4. **Given** backend API is unavailable, **When** user opens Home tab, **Then** the app displays a clear error message explaining the connection problem
5. **Given** user has location permissions granted, **When** Home tab loads, **Then** announcements are displayed with location coordinates (latitude/longitude)
6. **Given** user has location permissions denied, **When** Home tab loads, **Then** announcements are displayed without location coordinates

---

### User Story 2 - Navigate to Pet Details from Landing Page (Priority: P2)

Users can tap on any announcement card on the Home tab to view complete details about that pet. The navigation switches to the Lost Pets tab and opens the selected pet's detail screen, maintaining context.

**Why this priority**: Essential for users to act on interesting announcements from the landing page. Provides seamless navigation from discovery to detailed information without requiring manual tab switching.

**Independent Test**: Can be tested by tapping any announcement card on Home tab and verifying navigation switches to Lost Pets tab with the correct pet detail screen displayed. Delivers value by enabling quick access to full pet information.

**Acceptance Scenarios**:

1. **Given** user is viewing Home tab with announcements, **When** they tap on any announcement card, **Then** the app switches to Lost Pets tab and displays the detail screen for the selected pet
2. **Given** user navigated to pet details from Home tab, **When** they use system back gesture/button, **Then** they return to Lost Pets tab (showing full announcement list), NOT to Home tab
3. **Given** user navigated to pet details from Home tab, **When** they tap Home tab again, **Then** the app displays the landing page (announcement cards), not the detail screen
4. **Given** user taps an announcement card, **When** the detail screen is loading, **Then** tab switch happens instantly and existing Pet Details screen loading indicator displays during data fetch (no blocking overlay on Home tab)

---

### Edge Cases

- **What happens when announcements are loading?**: Display loading indicator (spinner or skeleton) until data arrives or timeout occurs
- **How does the landing page handle very long pet names or descriptions?**: Announcement cards truncate text at appropriate length (same behavior as full announcement list)
- **What happens when photo URLs are broken or images fail to load?**: Display placeholder image (same behavior as full announcement list)
- **What happens when user returns to Home tab (from any state)?**: Home tab always displays landing page with cached announcement cards. Detail screens never appear on Home tab (they open on Lost Pets tab)
- **What happens when announcements are filtered by location but user's location changes?**: Landing page continues to show announcements based on location at time of initial load. No automatic update until app restart
- **What happens when backend returns announcements with malformed data?**: Skip invalid items and display only valid announcements (same error handling as full announcement list)

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Home tab MUST display a landing page showing the 5 most recent pet announcements from the backend
- **FR-002**: Landing page MUST use the same API endpoint (GET /api/v1/announcements) as the full announcement list screen
- **FR-002a**: iOS app MUST perform client-side sorting (by creation date descending) and filtering (limit to first 5) of announcements returned from backend
- **FR-003**: Announcement cards on landing page MUST display the same information and visual design as cards in the full announcement list (name, species, status, photo, location coordinates)
- **FR-004**: Landing page MUST sort announcements by creation date with newest announcements first
- **FR-005**: Landing page MUST limit display to exactly 5 announcements, even if backend returns more
- **FR-006**: When backend has fewer than 5 announcements, landing page MUST display all available announcements
- **FR-007**: When backend has no announcements, landing page MUST display an empty state with "No recent announcements" message and an illustrative icon or image
- **FR-008**: When user taps an announcement card on landing page, system MUST switch to Lost Pets tab and display the detail screen for the selected pet
- **FR-008a**: Tab switch MUST happen instantly (non-blocking); Pet Details screen handles its own loading state during data fetch
- **FR-009**: When user navigates back from pet details (opened from landing page), system MUST return to Lost Pets tab showing the full announcement list, NOT to Home tab
- **FR-010**: When user taps Home tab after viewing pet details from landing page, system MUST display the landing page (announcement cards), NOT the detail screen
- **FR-011**: Landing page MUST display loading indicators while fetching data from backend
- **FR-012**: Landing page MUST handle API errors (network unavailable, timeout, server errors) by displaying clear error messages
- **FR-013**: When user has location permissions granted, landing page MUST display location coordinates for each announcement (same as full list)
- **FR-014**: When user has location permissions denied, landing page MUST display announcements without location coordinates (same as full list)
- **FR-015**: Landing page MUST reuse existing announcement list UI components to ensure visual consistency
- **FR-016**: Landing page MUST handle broken photo URLs, malformed data, and missing optional fields the same way as the full announcement list

### Key Entities

- **Announcement**: Represents a pet announcement fetched from backend, includes pet information (name, species, status, photo URL), location data (coordinates: latitude/longitude), contact details, and metadata (creation date, update date)
- **Landing Page**: Represents the Home tab root screen, contains a limited subset (first 5) of announcements sorted by creation date
- **Navigation Context**: Tracks the user's navigation path, ensures that navigating to pet details from landing page places the user in the Lost Pets tab context (affects back navigation behavior)

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Home tab displays exactly 5 most recent announcements when backend has 5 or more announcements available
- **SC-002**: Home tab displays correct number of announcements when backend has fewer than 5 announcements
- **SC-003**: Announcement cards on landing page are visually identical to cards in full announcement list (same layout, information, styling)
- **SC-004**: 100% of announcement card taps on landing page successfully navigate to Lost Pets tab with correct pet details displayed
- **SC-005**: Users navigating back from pet details (opened from landing page) return to Lost Pets tab full list, maintaining proper navigation context
- **SC-006**: Landing page displays appropriate loading states, empty states (with text and icon/illustration), and error messages that are clear and actionable

## Assumptions

- **API Compatibility**: The existing GET /api/v1/announcements endpoint returns all announcements. iOS app performs client-side filtering and sorting to limit display to 5 most recent items sorted by creation date descending
- **Component Reusability**: Announcement list UI components will be refactored into reusable autonomous components (AnnouncementCardsListView with its own ViewModel) to enable sharing between full list and landing page while maintaining separation of concerns
- **Navigation System**: iOS coordinator-based navigation system supports programmatic tab switching and navigation stack manipulation (switch to tab + push detail screen)
- **Existing Pet Details Screen**: Pet Details screen already exists with its own loading states, error handling, and UI components. Landing page navigation reuses this existing screen without modifications
- **Default Tab**: Home tab is already configured as the default tab when app launches (as per feature 054-ios-tab-navigation)
- **Location Permission State**: App has existing location permission handling logic that can be reused for landing page distance calculations
- **Visual Design**: Landing page uses the same visual design system (colors, typography, spacing) as the full announcement list, no custom design required for this iteration
- **Future Expansion**: Landing page is designed to be extended in future iterations (e.g., adding featured announcements, filters, "See All" button, categories) but current scope is limited to displaying 5 most recent announcements
- **Data Freshness**: Landing page data is fetched only on first Home tab appearance after app launch. No pull-to-refresh or automatic refresh functionality in this iteration. Data persists in memory until app restart
- **Performance**: Displaying 5 announcements on landing page is expected to be fast (under 1 second on typical network) due to small data size, no explicit performance optimization required
