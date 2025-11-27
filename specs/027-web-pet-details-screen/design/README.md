# Design Reference: Pet Details Screen (Web)

## Figma Design

**URL**: https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=168-4985&m=dev

**Node ID**: 168-4985 (Missing animal detail card)

**Parent Section**: MVP web app (node-id=297-8719)

## Design Overview

The pet details modal shows comprehensive information about a missing or found pet in an overlay dialog. The design follows a vertical scrolling modal layout adapted for web with responsive behavior. The modal is **opened from the animal list page** when users click the **"Details" button** on any animal card. The modal appears over the animal list page as a centered overlay with backdrop.

### Integration with Animal List

- **Trigger**: "Details" button on each animal card in the list (outlined blue style, 127px width, 44px height)
- **Location**: Right section of each animal card, below status badge and date
- **Action**: Clicking the button opens this modal with the corresponding pet's details
- **State**: Modal open/close state is managed (React state or URL query parameter)
- **Data Flow**: Pet ID is passed from list to modal, modal fetches pet details via API

### Animal List Page Structure

**Design Reference**: https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=168-4656&m=dev

**Page Layout**:
- **Sidebar**: Left sidebar (219px width, dark background #4F3C4C) for navigation
- **Content Area**: Main content (1181px width) with header, filters, and cards list
- **Total Width**: 1440px

**Header**:
- **Left**: "PetSpot" title (Hind Regular, 24px, #2D2D2D)
- **Right**: "Report a Missing Animal" button (Primary blue #155DFC, 44px height, white text)
- **Position**: Fixed at top, always visible

**Filters Section**:
- **Input Fields** (grid layout, 283px width each):
  - Microchip number (optional) - Text input
  - Date of disappearance - Date picker
  - Animal species - Dropdown
  - Animal approx. age (optional) - Dropdown
  - Animal race - Dropdown (may be disabled/grayed)
  - Place of disappearance - Text input
- **Action Buttons**:
  - "Clear" button (outlined blue, 127px × 44px)
  - "Search" button (primary blue, 138px × 44px)
- **Filter Chips**: Active filters displayed as chips below filters (e.g., "Male" with checkmark)

**Animal Cards** (1180px width, 136px height, 14px border-radius, 1px border #E5E9EC, 8px gap):
- **Left Section (192px)**: Pet photo (192px × 136px) with fallback
- **Middle Section (~792px)**:
  - Row 1: Location icon (16px) + coordinates (e.g., "52.2297° N, 21.0122° E") - Arial 16px, #4A5565
  - Row 2: Species + "•" + Breed + Gender icon (e.g., "Dog • Golden Retriever ♂") - Arial 16px, #101828
  - Row 3: Description text (truncated) - Arial 16px, #4A5565
- **Right Section (144px)**:
  - Status badge (MISSING=red #FF0000, FOUND=blue #155DFC, pill-shaped, white text)
  - Date with calendar icon (e.g., "Nov 18, 2025") - Arial 16px, #6A7282
  - **"Details" button** (outlined blue #155DFC, 127px × 44px, 10px border-radius) - opens modal

### Modal Structure
- **Backdrop**: Semi-transparent overlay (#000000 with 50% opacity) covering entire viewport
- **Modal Container**: White background, centered, with border-radius (16px on tablet/desktop)
- **Close Button**: Circular button (44x44px) with X icon, positioned absolute top-left (16px from edges)
- **Close Methods**: Click X button, click backdrop, or press ESC key
- **Z-index**: Modal should appear above all other content (typically z-index: 1000+)
- **Animation**: Optional fade-in/out and scale transitions for modal appearance

### Hero Image Section
- Full-width pet photo at top of modal (with responsive sizing)
- Status badge overlay (top-right): Red "MISSING" badge
- Reward badge overlay (left side over image): Money bag icon with reward amount (e.g., "Reward 500 PLN")

### Content Section (Scrollable within modal)

All information is displayed in a white card container with consistent padding and spacing:

1. **Header Row (3 columns)**
   - Date of Disappearance: Nov 18, 2025 (with calendar icon)
   - Contact owner: + 48 ******** (with phone icon)
   - Contact owner: mail@email.com (with email icon)

2. **Identification Information**
   - Microchip number: 000-000-000-000 (full width)

3. **Pet Details (2-column grid)**
   - Animal Species: Dog | Animal Race: Doberman
   - Animal Sex: Male (with icon) | Animal Approx. Age: 3 years

4. **Location Information**
   - Lat / Long: 52.2297° N, 21.0122° E (with location icon) + "Show on the map" button (blue outlined)

5. **Descriptive Information**
   - Animal Additional Description: Multi-line text paragraph
   - Special Features: — (dash if empty)

## Responsive Modal Layout Breakpoints

### Mobile (320px - 767px)
- Modal: 100% width, 100% height (full-screen on mobile)
- No backdrop visible (modal takes full screen)
- Hero image: 100% width, max-height: 320px
- Header row stacks vertically (3 rows: date, phone, email)
- Two-column grids become single column (species/race, sex/age)
- 16px side padding within modal
- Close button: Fixed in top-left

### Tablet (768px - 1023px)
- Modal: 90% width, max-width: 640px, centered
- Semi-transparent backdrop visible
- Hero image: 100% of modal width, max-height: 320px
- Header row: 3 columns maintained
- Two-column grids maintained
- 24px side padding within modal
- Close button: Absolute positioned top-left of modal
- Border-radius: 16px

### Desktop (1024px+)
- Modal: max-width: 768px, centered
- Semi-transparent backdrop visible (#000 with 50% opacity)
- Hero image: 100% of modal width, max-height: 320px
- Header row: 3 columns maintained
- Two-column grids maintained
- 24px side padding within modal
- Close button: Absolute positioned top-left of modal
- Border-radius: 16px
- Box shadow for depth

## Design System Notes

### Typography
- Font family: System font stack (e.g., -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial)
- Label text: 16px, #6A7282 (gray), font-weight: 400
- Value text: 16px, #101828 (dark gray/black), font-weight: 500
- Button text: 16px, font-weight: 600, white or brand color
- Heading text: 18px-20px, font-weight: 600

### Colors
- Primary Red: #FB2C36 (status badge, remove button)
- Primary Blue: #155DFC (outlined button border and text)
- Gray shades: #6A7282 (labels), #101828 (values), #E8ECF0 (container border)
- Background: White (#FFFFFF)
- Surface: #F9FAFB (optional for card backgrounds)
- Status badge: Red background, white text
- Focus outline: Blue (#155DFC) 2px solid for keyboard navigation

### Spacing
- Container padding: 16px (mobile), 24px (tablet), 32px (desktop)
- Section gaps: 20px (mobile), 24px (tablet/desktop)
- Field gaps: 4-12px
- Border radius: 10px (buttons), 16px (container/cards)

### Icons
- Male/Female symbols for sex (Unicode or icon font)
- Location pin icon (SVG)
- Money bag icon for reward (SVG)
- Back (X) icon in header (SVG)
- Icon size: 20-24px
- Icon color: Inherit from text or #6A7282

### Interactive States

**Buttons**:
- Default: Defined colors with border-radius
- Hover: Darken by 10% (desktop only)
- Focus: 2px solid blue outline with offset
- Active: Darken by 15%
- Disabled: 50% opacity, no pointer events

**Links/Clickable areas**:
- Default: Underline or color indicator
- Hover: Cursor pointer, subtle color change
- Focus: 2px solid blue outline
- Active: Pressed state

### Accessibility Requirements

- All interactive elements must be keyboard accessible
- Tab order must follow logical reading flow
- Focus indicators must be clearly visible (2px blue outline)
- Color contrast must meet WCAG AA standards (4.5:1 for text)
- Images must have alt text
- Form labels must be properly associated with inputs
- ARIA labels for icon-only buttons

## Implementation Notes for Web

### Modal Component Structure
- Use React Portal to render modal outside main DOM hierarchy (prevents z-index issues)
- Semantic HTML: `<dialog>` element (with polyfill) or `<div role="dialog" aria-modal="true">`
- Modal wrapper: Backdrop + Modal container
- Close button: Positioned absolute, accessible with keyboard
- Content area: Scrollable container with max-height constraint
- Implement responsive layout with CSS Grid or Flexbox
- Use CSS media queries for breakpoints
- Lazy load hero image for performance
- Implement error boundaries for graceful failure

### Component Hierarchy
```
Modal (Portal)
├── Backdrop (clickable overlay)
└── ModalContainer
    ├── CloseButton (X icon)
    ├── HeroImage (with status badge + reward badge overlays)
    └── ContentArea (scrollable)
        ├── HeaderRow (3 columns: date, phone, email)
        ├── MicrochipNumber (full width)
        ├── PetDetailsGrid (2 columns: species/race, sex/age)
        ├── LocationSection (coordinates + map button)
        ├── AdditionalDescription (multi-line)
        └── SpecialFeatures (optional)
```

### State Management
- **Modal open/close state**: Controlled by animal list page component (React state or URL query parameter)
- **Selected pet ID**: Passed from list page when "Details" button is clicked
- **Loading state**: Spinner within modal while fetching pet details
- **Success state**: Display pet data in modal
- **Error state**: Error message with retry option within modal (e.g., "Pet not found" or API error)
- **Empty state**: For missing optional fields, display dash "—"

### Integration with Animal List Page

**Opening Flow**:
1. User clicks "Details" button on animal card in list
2. List component sets selected pet ID and opens modal state
3. Modal component receives pet ID as prop
4. Modal fetches pet details via API (`GET /api/pets/:id`)
5. Modal displays pet information

**Closing Flow**:
1. User clicks X button, backdrop, or presses ESC
2. Modal component closes modal state
3. List component clears selected pet ID
4. Focus returns to "Details" button that triggered the modal
5. List page remains visible in background (dimmed by backdrop)

**State Management Pattern**:
```typescript
// Example state structure (not implementation requirement)
{
  isModalOpen: boolean,
  selectedPetId: string | null,
  petDetails: PetDetails | null,
  loading: boolean,
  error: string | null
}
```

### Modal Behaviors
- **Focus Trap**: Use library (e.g., `focus-trap-react`) or custom implementation
- **Body Scroll Lock**: Use library (e.g., `body-scroll-lock`) to prevent background scrolling
- **ESC Key Handler**: Add event listener for Escape key to close modal
- **Backdrop Click**: Close modal when clicking outside content area
- **Focus Management**: 
  - When modal opens: Move focus to first focusable element in modal (close button or modal container)
  - When modal closes: Return focus to the "Details" button that triggered the modal opening
  - Store reference to trigger button element for focus return

### Performance Considerations
- Optimize images (responsive images with srcset for hero image)
- Minimize CSS bundle size
- Code splitting for modal component (lazy load when needed)
- Implement skeleton screens during loading (optional enhancement)
- Prevent unnecessary re-renders with React.memo for modal content

### Testing Requirements
- All interactive elements must have data-testid attributes: `petDetails.element`
- Modal-specific test IDs: `petDetails.modal`, `petDetails.closeButton`, `petDetails.backdrop`
- Support E2E testing with Playwright (test modal open/close, backdrop click, ESC key)
- Ensure component is unit testable with React Testing Library
- Test focus trap behavior (tab navigation stays within modal)
- Test body scroll lock (background doesn't scroll when modal open)
- Test responsive behavior at all breakpoints
- Test keyboard navigation and screen reader compatibility
- Test ESC key and backdrop click behaviors

### Browser Support
- Chrome (last 2 versions)
- Firefox (last 2 versions)
- Safari (last 2 versions)
- Edge (last 2 versions)
- No IE11 support required

## Visual Design Adaptations from Mobile to Web

### Key Differences
1. **Layout Type**: Modal overlay instead of full-page navigation
2. **Mobile**: Full-screen modal (100% width/height) with no backdrop visible
3. **Tablet/Desktop**: Centered dialog (max-width 640px/768px) with semi-transparent backdrop
4. **Grid columns**: Maintained on tablet/desktop, stacked on mobile
5. **Spacing**: Consistent padding (16-24px) within modal container
6. **Hero image**: Responsive sizing with aspect ratio preservation, max-height: 320px
7. **Close interaction**: X button, backdrop click, or ESC key (vs back button on mobile)

### Responsive Image Strategy
```html
<!-- Example structure (not implementation) -->
<img 
  srcset="image-320w.jpg 320w, image-640w.jpg 640w, image-1024w.jpg 1024w"
  sizes="(max-width: 767px) 100vw, (max-width: 1023px) 640px, 768px"
  src="image-640w.jpg"
  alt="Pet photo"
/>
```

## Design Tokens (Optional Enhancement)

Consider implementing design tokens for:
- Color palette
- Typography scale
- Spacing scale
- Border radius values
- Shadow definitions
- Transition durations

This enables consistent styling and easier theme customization.

