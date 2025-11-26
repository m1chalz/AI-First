# Design Reference: Pet Details Screen

## Figma Design

**URL**: https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=179-8157&m=dev

**Node ID**: 179-8157

**Reference Frame**: "Pet detail streched" (this is the primary design reference for implementation)

## Design Overview

The pet details screen shows comprehensive information about a missing or found pet. The design follows a vertical scrolling layout with the following key sections:

### Header Section
- Standard iOS navigation bar with back button (chevron left)
- Navigation bar title (optional)
- Device status bar and notch

### Hero Image Section
- Full-width pet photo
- Status badge overlay (top-right): Red "MISSING" badge
- Reward badge overlay (left side): Money bag icon with reward amount (e.g., "500 PLN")

### Content Section (Scrollable)

All information is displayed in a card-like container with consistent spacing:

1. **Date Information**
   - Date of Disappearance: Nov 18, 2025

2. **Contact Information**
   - Two "Contact owner" fields:
     - Phone: + 48 ******** (masked)
     - Email: mail@email.com

3. **Identification Information**
   - Microchip number: 000-000-000-000
   - Animal Species and Animal Race (side-by-side): Dog | Doberman
   - Animal Sex and Animal Approx. Age (side-by-side): Male (with icon) | 3 years

4. **Location Information**
   - Place of Disappearance / City: Warsaw • ±15 km (with location icon)
   - "Show on the map" button (blue outlined, secondary style)

5. **Descriptive Information**
   - Animal Additional Description: Multi-line text paragraph

6. **Action Button**
   - "Remove Report" button (red, primary style, full width)

## Design System Notes

### Typography
- Font family: Arial
- Label text: 16px, #6A7282 (gray)
- Value text: 16px, #101828 (dark gray/black)
- Button text: 16px, white or brand color

### Colors
- Primary Red: #FB2C36 (status badge, remove button)
- Primary Blue: #155DFC (outlined button border and text)
- Gray shades: #6A7282 (labels), #101828 (values), #E8ECF0 (container border)
- Background: White
- Status badge: Red background, white text

### Spacing
- Container padding: 16px
- Section gaps: 20-24px
- Field gaps: 4-12px
- Border radius: 10px (buttons), 48px (container)

### Icons
- Male/Female symbols for sex
- Location pin icon
- Money bag icon for reward
- Standard iOS back chevron in navigation bar

## Implementation Notes for iOS

- Use SwiftUI for UI implementation
- All interactive elements must have accessibility identifiers: `petDetails.element`
- Implement proper scrolling behavior for content that exceeds screen height
- Handle image loading states (loading, error, success)
- Ensure responsive layout for various screen sizes (iPhone SE to iPad Pro)
- Follow iOS Human Interface Guidelines while maintaining design fidelity
- Create a reusable label-value component for displaying information pairs (e.g., label + value fields like "Date of Disappearance" with its date value)

