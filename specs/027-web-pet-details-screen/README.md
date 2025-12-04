# Spec 027: Pet Details Screen (Web UI)

**Based on**: Spec 010 (Pet Details Screen - Android UI)  
**Platform**: Web Application  
**Status**: Draft  
**Created**: November 27, 2025

## Overview

This specification defines the web application version of the pet details screen as a **modal overlay dialog**. The modal is opened from the **animal list page** when users click the **"Details" button** on any animal card. The modal displays comprehensive information about missing or found pets and appears centered over the list with a backdrop. The implementation focuses on responsive design, accessibility, and modern web standards.

### Integration with Animal List

- **Trigger**: "Details" button on each animal card in the list
- **Opening**: Modal opens when "Details" button is clicked
- **Data Passing**: Pet ID is passed from list to modal to fetch details
- **State Management**: Modal open/close state managed via React state or URL query parameter
- **Focus Management**: Focus moves to modal when opened, returns to "Details" button when closed

## Key Differences from Android Spec (010)

### Major Design Change

**Modal Dialog vs Full-Page View**: The web version uses a modal overlay instead of a full-page navigation. This provides a better user experience by keeping the list context visible and allowing quick return to browsing.

### Platform-Specific Additions

1. **Modal Behaviors** (FR-021, FR-022, FR-023, FR-026, FR-027, FR-028, FR-029, FR-030, FR-031, FR-032, User Story 1, User Story 7)
   - Opened from animal list via "Details" button click
   - Receives pet ID from list to fetch details
   - Close button (X) in top-left corner
   - Backdrop click to close
   - ESC key to close
   - Focus trap within modal
   - Body scroll lock when modal is open
   - Focus management (moves to modal on open, returns to button on close)
   - Full-screen on mobile, centered dialog on tablet/desktop

2. **Responsive Design** (FR-020, User Story 7)
   - Mobile (320px+): Full-screen modal (100% width/height)
   - Tablet (768px+): Centered dialog (max-width 640px) with backdrop
   - Desktop (1024px+): Centered dialog (max-width 768px) with backdrop and shadow
   - Responsive image loading with srcset

3. **Web Standards** (FR-024, FR-025)
   - Keyboard accessibility with focus management and focus trap
   - Modern browser compatibility (Chrome, Firefox, Safari, Edge - last 2 versions)
   - ARIA attributes for modal dialog semantics

4. **Performance & Accessibility** (SC-005, SC-006, SC-007)
   - Lighthouse accessibility score of 90+ with focus trap validation
   - Modal load within 2 seconds on standard broadband
   - WCAG AA compliance for color contrast and keyboard navigation
   - Proper modal UX behaviors (scroll lock, focus trap, ESC/backdrop close)

5. **Integration with Animal List** (FR-028, FR-029, FR-030, FR-031, FR-032)
   - Modal opened from "Details" button on animal cards
   - Pet ID passed from list to modal
   - State management for open/close state
   - Focus management (to modal on open, back to button on close)
   - Handling multiple rapid clicks
   - Handling modal open when another modal is already open

6. **Additional Edge Cases**
   - Modal backdrop click behavior
   - ESC key handling
   - Body scroll lock behavior
   - Focus trap validation
   - Rapid "Details" button clicks
   - Opening modal when another is already open
   - Invalid pet ID handling
   - JavaScript disabled scenario
   - Very narrow viewports (<320px)

### Web-Specific Implementation Notes

- **HTML Structure**: Modal dialog with semantic HTML5 (dialog element or div with ARIA roles)
- **Styling**: CSS Grid/Flexbox with media queries, backdrop overlay, box shadow
- **State Management**: Modal open/close state, loading, success, error states
- **Modal Library**: Can use React Portal for modal rendering, or custom implementation
- **Testing**: Playwright E2E tests, React Testing Library unit tests with modal-specific assertions
- **Attributes**: `data-testid` for all interactive elements, ARIA attributes for accessibility
- **Accessibility**: Focus trap library (e.g., focus-trap-react), body scroll lock library

## File Structure

```
027-web-pet-details-screen/
├── README.md                    # This file
├── spec.md                      # Full specification
├── animal-list-structure.md      # Animal list page structure (design reference)
├── checklists/
│   └── requirements.md          # Quality validation checklist
└── design/
    └── README.md                # Design reference and responsive guidelines
```

## Contents

### [spec.md](./spec.md)
Complete feature specification including:
- 7 user stories with acceptance scenarios (modal-based interaction, integration with list)
- 32 functional requirements (web-specific modal behaviors + list integration)
- 18 edge cases (including modal-specific scenarios + list integration scenarios)
- 7 measurable success criteria
- Key entities definition

### [checklists/requirements.md](./checklists/requirements.md)
Quality validation checklist confirming:
- ✅ All checklist items pass validation
- ✅ No implementation details in specification
- ✅ All requirements are testable
- ✅ Comprehensive edge case coverage
- ✅ Clear scope and dependencies

### [design/README.md](./design/README.md)
Design reference including:
- Responsive layout breakpoints (mobile/tablet/desktop)
- Design system specifications (typography, colors, spacing, icons)
- Interactive states and accessibility requirements
- Component structure guidelines
- Performance considerations
- Browser support matrix
- Animal list page structure and integration details

### [animal-list-structure.md](./animal-list-structure.md)
Detailed structure of the animal list page that contains "Details" buttons:
- Page layout (sidebar, header, filters, cards)
- Animal card structure and styling
- Filter section layout
- Typography and color specifications
- Integration with pet details modal

## Design Reference

**Figma**: https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=168-4985&m=dev

**Design Type**: Modal overlay (centered dialog over animal list)

**Parent Section**: MVP web app ([node-id=297-8719](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-8719&m=dev))

## Next Steps

1. **Planning Phase**: Create `plan.md`, `tasks.md`, `data-model.md`, `quickstart.md`, and `research.md`
2. **Implementation**: Execute tasks according to the generated task list
3. **Testing**: Implement E2E tests and verify all acceptance criteria

## Related Specifications

- **Spec 010**: Pet Details Screen (Android UI) - Original Android version
- **Spec 012**: iOS Pet Details Screen - iOS version
- **Spec 006**: Pets API - Backend API for pet data
- **Spec 013**: Animal List Screen (Android) - Android list implementation
- **Animal List Page (Web)**: The web animal list page that contains "Details" buttons opening this modal

## Assumptions

- Backend API endpoint exists for fetching pet details by ID (`GET /api/pets/:id`)
- Animal list page exists with "Details" buttons on each animal card
- "Details" button is implemented on animal cards (outlined blue style, 127px width, 44px height)
- Modal can be opened via state management (React state, URL query params, or route state)
- Pet ID is passed from list to modal component
- Map view feature exists or will be created for "Show on the map" button
- Focus trap and body scroll lock libraries are available or will be implemented
- Modal component/library is available or will be created (e.g., React Portal-based modal)
- List page maintains scroll position when modal opens/closes

