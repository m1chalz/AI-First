# Feature Specification: iOS Landing Page Full Content Scroll

**Feature Branch**: `KAN-30-ios-landing-page-full-content-scroll`  
**Created**: 2025-12-19  
**Status**: Draft  
**Input**: User description: "Need to change landing page view architecture so the entire content is scrollable (not only the list as it is currently). iOS only. This is a partial scope extracted from KAN-30-ios-landing-page-map-view."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Scroll the Entire Landing Page Content (Priority: P1)

A user opens the iOS landing page on a smaller screen (or with larger text sizes) and needs to be able to scroll through all landing page sections as one continuous content area, instead of only a single subsection being scrollable.

**Why this priority**: If only one subsection scrolls, users can miss content, experience confusing nested scroll behavior, and have reduced accessibility/usability on smaller screens.

**Independent Test**: Can be tested by opening the landing page on multiple iPhone screen sizes and verifying that all sections can be reached by a single vertical scroll gesture and that there is no nested/competing scrolling.

**Acceptance Scenarios**:

1. **Given** the landing page is displayed, **When** the user scrolls vertically, **Then** the entire landing page content scrolls as one continuous page (all sections move together)
2. **Given** the landing page contains multiple sections above and below the list, **When** the user scrolls from the top to the bottom, **Then** each section becomes reachable without being blocked by a separately scrollable region
3. **Given** the user scrolls to the middle of the landing page, **When** they continue scrolling, **Then** scrolling remains smooth and consistent without “scroll lock” or unexpected handoff between nested scroll areas

---

### User Story 2 - Preserve Existing Interactions While Changing Scroll Behavior (Priority: P1)

A user interacts with elements on the landing page (buttons, list items, and other interactive controls) and expects those interactions to behave the same as before while the page becomes fully scrollable.

**Why this priority**: Scroll refactors commonly introduce regressions in tap handling, focus order, and navigation; this story ensures the refactor delivers value without breaking existing behavior.

**Independent Test**: Can be tested by performing key interactions on the landing page (tapping primary buttons and list items) before and after scroll changes and confirming the same navigation/actions occur.

**Acceptance Scenarios**:

1. **Given** the landing page is displayed, **When** the user taps any interactive element, **Then** the tap triggers the same action/navigation as it did prior to this change
2. **Given** the landing page list is visible, **When** the user scrolls and then taps a list item, **Then** the item can be selected reliably and leads to the expected result (no missed taps due to scroll conflicts)

### Edge Cases

- **Small screens**: On smaller devices, all landing page sections remain reachable via a single continuous scroll
- **Large text / Dynamic Type**: With larger text sizes, the content still scrolls as a whole and does not trap the user inside a subsection
- **Long list**: With many list items, the overall page remains scrollable without nested scrolling behavior
- **Short content**: If the content is shorter than the viewport, scrolling is not required and no blank space or jitter is introduced

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The iOS landing page MUST scroll vertically as a single continuous content area (no nested/independent scroll regions for subsections)
- **FR-002**: All landing page sections (including those above the list and the list itself) MUST be reachable via the same vertical scrolling behavior
- **FR-003**: Existing landing page interactions (taps, navigation, and actions) MUST remain functionally unchanged by this scroll behavior change
- **FR-004**: The scroll behavior MUST remain consistent across common device sizes and text size settings supported by the application
- **FR-005**: This specification MUST be implemented for iOS only (Android and Web are explicitly out of scope)
- **FR-006**: Loading, error, empty, and success states for the announcements area MUST be rendered inline as part of the same landing page vertical scroll (no full-screen overlays that detach the hero/header from the scrollable content)
- **FR-007**: The landing page MUST NOT use pinned/sticky sections for the hero panel or the list header; they MUST scroll together with the rest of the content

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: On supported iPhone screen sizes, users can reach every landing page section by a single continuous vertical scroll gesture (verified in QA)
- **SC-002**: In regression testing, key landing page interactions continue to succeed with no increase in interaction failures attributable to scrolling (verified in QA)

## Assumptions

- The iOS landing page is composed of multiple vertical sections, with at least one section currently using an independently scrollable region.
- This change is a layout/interaction behavior refactor and does not introduce new content sections by itself.
- Any iOS landing page content added by other KAN-30 sub-specifications (e.g., map preview) is expected to participate in the single continuous scroll behavior.

## Clarifications

### Session 2025-12-19

- Scope: This spec only changes landing page scroll behavior on iOS so that the whole landing page content scrolls (not only the list). All other KAN-30 sub-features remain out of scope here.
- Q: How should loading/error/empty/success states be rendered relative to the page scroll? → A: Inline in one ScrollView (hero + header + states + list as one continuous vertical scroll)
- Q: Should any sections be pinned/sticky while scrolling? → A: No pinned sections (hero and header scroll with the page)
- Q: Is VoiceOver support in-scope for this change? → A: No (VoiceOver out of scope)


