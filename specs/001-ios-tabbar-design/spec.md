# Feature Specification: iOS tab bar design update

**Feature Branch**: `[001-ios-tabbar-design]`  
**Created**: 2025-12-18  
**Status**: Draft  
**Input**: Jira `KAN-23` — “Update visual style of tabbar to match designs”. Design reference: Figma link included in the ticket. User description: "to jest storka tylko na iOS. Stwórz ją bazując na tickecie w jira"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Navigate with refreshed tab bar (Priority: P1)

As an iOS user, I can use the bottom tab bar to switch between the main app sections, and the tab bar looks consistent with the latest product designs.

**Why this priority**: The tab bar is present across the core app experience; visual inconsistency is highly noticeable and reduces perceived quality and trust.

**Independent Test**: Can be fully tested by opening the app and switching between all main tabs while visually validating the tab bar against the referenced design.

**Acceptance Scenarios**:

1. **Given** the iOS app is opened on a screen that uses the bottom tab bar, **When** the user views the tab bar, **Then** its visual style matches the referenced design for the default appearance mode.
2. **Given** the user is on any tab, **When** the user taps a different tab, **Then** the selected and unselected states update to match the referenced design and the user is taken to the expected destination for that tab.

---

### User Story 2 - No functional regressions while updating visuals (Priority: P2)

As an iOS user, I can continue to rely on the tab bar for navigation without any unexpected behavior changes after the visual update.

**Why this priority**: A visual change must not break navigation; regressions here would immediately impact core usability.

**Independent Test**: Can be tested by repeating the current navigation flows (switching tabs, returning to previously visited tabs) and verifying expected destinations and stable behavior.

**Acceptance Scenarios**:

1. **Given** the user can navigate using the tab bar today, **When** the visual update is applied, **Then** all existing tab destinations and navigation behavior remain unchanged.

---

### Edge Cases

- How does the tab bar render on different supported iPhone screen sizes (e.g., smaller screens vs larger screens) without clipping, overlap, or truncation?
- How does the tab bar behave with system accessibility settings (e.g., larger text) so labels remain readable and tappable?
- If the app supports multiple appearance modes (e.g., light/dark), does the tab bar remain readable and consistent with the referenced design for each supported mode?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The iOS app MUST update the bottom tab bar visual style to match the referenced design (including the appearance of selected and unselected tab states).
- **FR-002**: The iOS app MUST preserve the existing tab destinations and navigation behavior while updating the tab bar visuals.
- **FR-003**: The iOS app MUST present a clear, visually distinct selected tab state so users can identify the current section at a glance.
- **FR-004**: The iOS app MUST display the tab bar without overlapping or obscuring content, across supported devices and orientations.
- **FR-005**: The iOS app MUST keep tab bar labels and icons legible and tappable under supported accessibility settings.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: The tab bar passes an internal QA + design review with **0 high-severity visual deviations** from the referenced design across the supported iPhone device set.
- **SC-002**: The primary navigation scenarios for each tab complete with **0 blocker issues** during QA verification (switching tabs always reaches the expected section).
- **SC-003**: In accessibility verification on supported devices, tab labels/icons remain readable and tappable (no clipping/overlap) in the maximum supported text size used for testing.

## Assumptions

- The tab bar navigation structure already exists; this feature updates **visual style only** and does not add/remove tabs.
- The feature applies to **iOS only** (no Android/Web changes).
- The app should continue to behave correctly in all appearance/accessibility modes currently supported by the product.

## Dependencies

- Figma design reference (source of truth for the updated tab bar visuals): `https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=974-4861&m=dev`

## Out of Scope

- Changing tab destinations, navigation rules, or information architecture.
- Any updates to Android or Web navigation components.
