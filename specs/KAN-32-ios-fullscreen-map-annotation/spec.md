# Feature Specification: iOS Fullscreen Map - Display Pin Annotation Details

**Feature Branch**: `KAN-32-ios-fullscreen-map-annotation`  
**Created**: 2025-01-08  
**Status**: Draft  
**Ticket**: KAN-32  
**Platform**: iOS only  
**Dependencies**: KAN-32-ios-fullscreen-map-fetch-pins (pins must be displayed on map first)  
**Input**: User description: "W tej części specyfikacji zrobimy wyświetlanie annotacji kiedy user kliknie na pinezkę. Link do szczegółów designu: https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=1192-5893&m=dev. W Jira w tickecie KAN-32 zobacz także attachment obrazka, który pokazuje jak to ma wyglądać w kontekscie (annotacja ma strzałkę na dole). Obsługujemy iOS 18+ więc możemy używać najnowszego API."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - View Missing Animal Details from Pin (Priority: P1)

A user wants to tap a pin on the map and see detailed information about the missing animal so they can recognize it and contact the owner.

**Why this priority**: This is the core interaction that makes the map actionable. Without annotation details, users can only see pin locations but cannot act on them (identify the animal, contact the owner). This directly enables the primary use case: helping reunite lost pets with owners.

**Independent Test**: Can be tested by opening fullscreen map with pins displayed, tapping any pin, and verifying annotation callout appears with all required information fields populated correctly.

**Acceptance Scenarios**:

1. **Given** pins are visible on the fullscreen map, **When** the user taps a pin, **Then** an annotation callout appears displaying the missing animal's details
2. **Given** an annotation is displayed, **When** the user views it, **Then** it shows at minimum: pet photo, pet name, species/breed, last-seen location name, last-seen date, owner email, owner phone, and description
3. **Given** an annotation is displayed, **When** the user taps elsewhere on the map or taps the same pin again, **Then** the annotation dismisses
4. **Given** an annotation is displayed, **When** the user taps a different pin, **Then** the previous annotation dismisses and the new annotation appears for the selected pin

---

### User Story 2 - View Animal Status Badge (Priority: P2)

A user wants to see the current status of the missing animal (e.g., "MISSING", "FOUND") in the annotation so they know if assistance is still needed.

**Why this priority**: Status information prevents users from wasting time on outdated announcements. If an animal has been found, users don't need to keep looking. This improves user experience and reduces unnecessary contact attempts.

**Independent Test**: Can be tested by tapping pins for animals with different statuses and verifying the status badge displays correctly with appropriate styling for each status type.

**Acceptance Scenarios**:

1. **Given** an annotation is displayed, **When** the pet status is "MISSING", **Then** the annotation shows a status badge displaying "MISSING" with orange background (#FF9500) and white text
2. **Given** an annotation is displayed, **When** the pet status is "FOUND", **Then** the annotation shows a status badge displaying "FOUND" with blue background (#155DFC) and white text

---

### User Story 3 - Handle Missing or Invalid Data Gracefully (Priority: P3)

A user wants to see annotation details even when some information is missing or unavailable, without the UI breaking or becoming unusable.

**Why this priority**: Data integrity cannot always be guaranteed (user-generated content, optional fields, failed image uploads). Graceful degradation ensures the feature remains useful even with incomplete data.

**Independent Test**: Can be tested by creating test announcements with missing fields (no photo, no description, no phone, etc.) and verifying annotation displays correctly with placeholders or omits missing fields without crashing.

**Acceptance Scenarios**:

1. **Given** an announcement has no pet photo, **When** the annotation is displayed, **Then** a placeholder (circular pawprint icon on gray background, matching Announcement List) is shown instead of an empty space
2. **Given** an announcement has no description, **When** the annotation is displayed, **Then** the description field is omitted from the display
3. **Given** an announcement has no phone number, **When** the annotation is displayed, **Then** the phone field is omitted from the display
4. **Given** an announcement has no email address, **When** the annotation is displayed, **Then** the email field is omitted from the display

---

### Edge Cases

- **Pet photo fails to load**: Immediately display placeholder (circular pawprint icon on `#EEEEEE` background, matching Announcement List) without retry attempts; no loading spinner or broken image icon shown
- **Location display**: Display coordinates in the same format used in announcement list and pet details (no reverse geocoding)
- **Annotation positioning**: Annotation callout should position above the pin with a pointer/arrow pointing down to the pin location; if insufficient space above, position below with upward arrow

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: When the user taps a pin, the system MUST display an annotation callout for that pin
- **FR-002**: The annotation callout MUST be styled as a white card with rounded corners (12px border radius) and drop shadow matching Figma design, with fixed intrinsic width (value extracted from Figma) and internal text wrapping
- **FR-003**: The annotation callout MUST include a pointer/arrow pointing to the pin location
- **FR-004**: The annotation MUST display the following information fields:
  - Pet photo (120px height, 8px border radius)
  - Pet name (16px, bold, #333 color)
  - Species and breed formatted as "Species • Breed" (13px, #666 color)
  - Last-seen location coordinates with 📍 emoji prefix (13px, #666 color)
  - Last-seen date formatted in the same format as Pet Details (MMM dd, yyyy) with 📅 emoji prefix (13px, #666 color)
  - Owner email with 📧 emoji prefix (13px, #666 color)
  - Owner phone with 📞 emoji prefix (13px, #666 color)
  - Description text (14px, #444 color)
  - Status badge (rounded 12px, content-specific colors)
- **FR-005**: When the pet photo is missing or fails to load, the annotation MUST immediately display placeholder (no retry attempts) matching Announcement List: circular `pawprint.fill` icon (24pt, `#93A2B4` color) on `#EEEEEE` circle background (63pt diameter)
- **FR-006**: When the description is empty or null, the annotation MUST omit the description field entirely
- **FR-007**: When the phone number is missing, the annotation MUST omit the phone field entirely
- **FR-008**: When the email address is missing, the annotation MUST omit the email field entirely
- **FR-009**: The status badge MUST display the pet's current status (MISSING or FOUND) with appropriate colors:
  - MISSING: Orange background (#FF9500), white text
  - FOUND: Blue background (#155DFC), white text
- **FR-010**: When the user taps elsewhere on the map, the annotation MUST dismiss
- **FR-011**: When the user taps the same pin while its annotation is visible, the annotation MUST dismiss (toggle behavior)
- **FR-012**: When the user taps a different pin while an annotation is visible, the previous annotation MUST dismiss and the new annotation MUST appear
- **FR-013**: The annotation callout MUST position above the pin with a downward-pointing arrow by default
- **FR-014**: If insufficient space exists above the pin, the annotation MUST position below the pin with an upward-pointing arrow
- **FR-015**: The annotation MUST use MapKit's native annotation callout API (MKAnnotationView callout or custom annotation view)
- **FR-016**: Pet names and descriptions MUST be displayed in full without truncation
- **FR-017**: The location field MUST display coordinates in the exact same format (including decimal precision) used in announcement list and pet details for consistency
- **FR-018**: The last-seen date MUST be formatted using the same format as Pet Details screen (MMM dd, yyyy format, e.g., "Jan 15, 2025")

### Key Entities *(include if feature involves data)*

- **Annotation Callout**: Visual overlay displaying detailed information about a pet announcement when its pin is tapped; dismissible by tapping elsewhere
- **Status Badge**: Visual indicator showing the current status of the announcement (MISSING or FOUND) with status-specific colors

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can view annotation details by tapping a pin
- **SC-002**: Users can identify the animal's current status at a glance by viewing the status badge color and text
- **SC-003**: The annotation remains visually consistent with the Figma design (white card, rounded corners, drop shadow, pointer arrow)

## Assumptions

- The previous spec (KAN-32-ios-fullscreen-map-fetch-pins) has been implemented, providing pins on the map
- The backend API returns all necessary fields in the announcement response: pet name, species, breed, coordinates, last-seen date, owner email, owner phone, description, status, pet photo URL
- The iOS app targets iOS 18+, allowing use of latest MapKit annotation APIs
- Pet photo URLs are valid and hosted on a reliable server; placeholder uses system icon (`pawprint.fill`) matching Announcement List styling
- Status values from backend are "MISSING" or "FOUND" (as defined in backend validation schema)
- The map displays pins for both MISSING and FOUND announcements (departure from spec 066 which specified only MISSING)
- The annotation design follows the Figma mockup (node-id=1192:5893) with exact spacing, typography, and colors
- Location is displayed as coordinates (same format as announcement list and pet details) - no reverse geocoding
- Future specs may add additional interactions (tappable contact fields, share announcement, report sighting, navigate to location, text truncation)
- Accessibility features (VoiceOver support, Dynamic Type, accessibility labels) are deferred to a future enhancement
- Annotation appearance/dismissal uses MapKit default behavior (instant show/hide, no custom animation)
- Annotation callout has fixed intrinsic width (extracted from Figma design); long text fields wrap to multiple lines rather than truncating
- Pet photo loading failures result in immediate placeholder display (no retry attempts); assumes standard iOS image loading behavior with reasonable timeout

## Notes

This specification builds upon KAN-32-ios-fullscreen-map-fetch-pins by adding interactive annotation details to pins. The map and pin display from previous specs remain unchanged.

The annotation callout uses MapKit's native APIs for positioning and presentation. Custom styling (white card, shadow, pointer arrow, rounded corners) is achieved using custom annotation views or callout accessories.

The design matches the Figma mockup (https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=1192-5893&m=dev) exactly: white background, rounded corners (12px), drop shadow, emoji prefixes for location/date/contact, blue "FOUND" badge. Fields with missing data (description, phone, email) are omitted entirely rather than showing placeholder text.

Contact fields (phone, email) are displayed as text. Tappable interaction for initiating calls/emails may be added in a future enhancement.

Pet names and descriptions are displayed in full without truncation. If this creates layout issues in practice, text truncation may be added in a future enhancement.

Location is displayed as coordinates using the same format as announcement list and pet details (no reverse geocoding to location names).

Last-seen date is formatted identically to Pet Details screen (MMM dd, yyyy format, e.g., "Jan 15, 2025") to maintain consistency across the app.

Status badge colors are defined explicitly to ensure consistency across the app and match common semantic colors (orange for missing/warning, blue for found/informational). Only two status values exist in the system: MISSING and FOUND.

## Clarifications

### Session 2026-01-08

- Q: Should the annotation be dismissible by tapping a close button, or by tapping elsewhere on the map? → A: Dismiss by tapping elsewhere on map or tapping the same pin again (toggle behavior). No explicit close button needed.
- Q: Should very long names/descriptions be truncated? → A: No. Display in full without truncation. Truncation may be added in future enhancement if needed.
- Q: What should happen when description is missing? → A: Omit the field entirely. No placeholder messages shown.
- Q: What should happen when contact information (phone/email) is missing? → A: Omit the field entirely. No placeholder messages shown.
- Q: How should location be displayed? → A: Display coordinates in the same format used in announcement list and pet details. No reverse geocoding.
- Q: What date format should be used for last-seen date? → A: Same format as Pet Details screen (MMM dd, yyyy, e.g., "Jan 15, 2025").
- Q: What happens when user pans/zooms while annotation is visible? → A: Default MapKit behavior (annotation typically remains visible and moves with pin).
- Q: Should the annotation show the distance from user's current location? → A: Not in this spec. Focus on core annotation details. Distance could be added in future enhancement.
- Q: Should users be able to share the announcement from the annotation? → A: Not in this spec. Share functionality could be added in future enhancement.
- Q: Should phone/email be tappable to initiate calls/emails? → A: Not in this spec. Display as text only. Tappable interaction can be added later as enhancement.
- Q: What status values exist in the system? → A: Only MISSING and FOUND (per backend validation schema). No REUNITED status exists.
- Q: Should the annotation support VoiceOver and accessibility features? → A: No special accessibility handling in this spec (defer to future enhancement)
- Q: Should the annotation animate when appearing/dismissing? → A: Use MapKit default behavior (instant show/hide, no custom animation)
- Q: How should annotation callout width be determined? → A: Fixed intrinsic width (exact value to be extracted from Figma design) with text wrapping inside
- Q: What exact coordinate format should be used for location display? → A: Match existing format from announcement list and pet details (implementation determines exact decimal precision)
- Q: How should network image loading failures be handled? → A: Immediate fallback to placeholder (no retry) when load fails

## Design Deliverables *(mandatory for UI features)*

### Design Assets

| Asset | Status | Link |
|-------|--------|------|
| **Figma Design** | _Complete_ | https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=1192-5893&m=dev |
| **User Flow** | _Pending_ | FigJam diagram showing pin tap → annotation display → dismiss flow |
| **Wireframe** | _Pending_ | FigJam showing annotation positioning (above/below pin) |
| **Design Brief** | _Pending_ | Component specs: spacing, typography, colors, status badge styles |
| **Figma Make Prompt** | _N/A_ | Design already exists in Figma |
| **Visual Mockups** | _Complete_ | See Figma link above |

### Design Requirements

- [x] Visual mockups available in Figma
- [ ] User flow diagram created
- [ ] Wireframe layout created (annotation positioning logic)
- [ ] Design brief with component specs (spacing, typography, colors)
- [ ] All assets linked in Jira ticket KAN-32

---

## Estimation *(mandatory)*

### Initial Estimate

- **Story Points**: 2
- **Initial Budget**: 2 × 4 × 1.3 = 10.4 days
- **Confidence**: ±50%
- **Anchor Comparison**: Simpler than Pet Details Screen (3 SP). This feature involves custom MapKit annotation view with styling, data binding, contact action handling, and status badge logic. No network requests (data already available from pin fetch). Comparable to a single detailed view with interactive elements and custom styling, but less complex than full CRUD screen.

### Re-Estimation (Updated After Each Phase)

| Phase | SP | Days | Confidence | Key Discovery |
|-------|-----|------|------------|---------------|
| Initial | 2 | 10.4 | ±50% | Gut feel from feature title and Figma design |
| After SPEC | 2 | 10.4 | ±30% | Spec complete - iOS-only, no backend changes, extends existing FullscreenMap |
| After PLAN | 2 | 10.4 | ±20% | Plan complete - reuses existing patterns (placeholder, formatters), simple selection state |
| After TASKS | — | — | ±10-15% | [Update when tasks.md complete] |

### Per-Platform Breakdown (After TASKS)

| Platform | Tasks | Days | Notes |
|----------|-------|------|-------|
| Backend | — | — | [Fill after tasks.md - likely 0, no backend changes needed] |
| iOS | — | — | [Fill after tasks.md - full implementation here] |
| Android | — | — | [Fill after tasks.md - N/A, iOS only] |
| Web | — | — | [Fill after tasks.md - N/A, iOS only] |
| **Total** | | **—** | |

### Variance Tracking

| Metric | Initial | Final | Variance |
|--------|---------|-------|----------|
| **Story Points** | 2 SP | — | [Calculate: (Y - X) / X × 100%] |
| **Budget (days)** | 10.4 days | — | [Calculate: (Y - X) / X × 100%] |

**Variance Reasons**: [Why was estimate different? MapKit native annotation APIs? Custom view complexity? Contact action handling?]

**Learning for Future Estimates**: [What pattern should the team apply to similar custom MapKit annotation features?]

