# Animal List Page Structure (Web)

**Design Reference**: https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=168-4656&m=dev

**Node ID**: 168-4656 (Missing animals list web ver.2)

## Page Layout

### Overall Structure

- **Sidebar**: Left sidebar (219px width, dark background #4F3C4C) for navigation
- **Content Area**: Main content area (1181px width) containing header, filters, and list
- **Total Width**: 1440px (219px sidebar + 1181px content + margins)

### Header Section

- **Left**: "PetSpot" title (Hind Regular, 24px, #2D2D2D)
- **Right**: "Report a Missing Animal" button (Primary blue #155DFC, 44px height, white text)
- **Position**: Fixed at top of content area, always visible

### Filters Section

**Input Fields** (arranged in grid, 283px width each):
1. **Microchip number (optional)**: Text input with placeholder "00000-00000-00000"
2. **Date of disappearance**: Date picker with "Select date" placeholder
3. **Animal species**: Dropdown with "Select an option" placeholder
4. **Animal approx. age (optional)**: Dropdown with "Select an option" placeholder
5. **Animal race**: Dropdown with "Select an option" placeholder (disabled/grayed out)
6. **Place of disappearance**: Text input with placeholder "Your location"

**Action Buttons** (right side):
- **Clear**: Outlined button (blue border #155DFC, 127px width, 44px height)
- **Search**: Primary button (blue #155DFC, 138px width, 44px height)

**Filter Chips** (below filters):
- Display active filters as chips (e.g., "Male" with checkmark icon)
- Chips show selected filter values
- Can be clicked to remove filter

### Animal Cards List

**Card Structure** (1180px width, 136px height, 14px border-radius, 1px border #E5E9EC):

**Left Section (192px)**:
- Pet photo (192px × 136px) with fallback placeholder
- Full height image, object-fit cover

**Middle Section (flexible width, ~792px)**:
- **Row 1**: Location icon (16px) + coordinates (e.g., "52.2297° N, 21.0122° E")
  - Font: Arial Regular, 16px, color #4A5565 (gray)
  - Icon and text aligned horizontally
- **Row 2**: Species + "•" separator + Breed + Gender icon
  - Font: Arial Regular, 16px, color #101828 (dark)
  - Format: "Dog • Golden Retriever ♂"
  - Gender icon (16px) after breed
- **Row 3**: Description text (truncated if long)
  - Font: Arial Regular, 16px, color #4A5565 (gray)
  - Multi-line text, truncated with ellipsis if exceeds available space

**Right Section (144px)**:
- **Top**: Status badge (pill-shaped, rounded corners)
  - "MISSING": Red background (#FF0000), white text, 16px font
  - "FOUND": Blue background (#155DFC), white text, 16px font
  - "CLOSED": Gray background, white text, 16px font
- **Middle**: Date with calendar icon (16px)
  - Format: "Nov 18, 2025"
  - Font: Arial Regular, 16px, color #6A7282 (gray)
  - Icon and text aligned horizontally
- **Bottom**: **"Details" button** (outlined blue style)
  - Border: 2px solid #155DFC
  - Text: "Details" (Hind Regular, 16px, #155DFC)
  - Size: 127px width × 44px height
  - Border-radius: 10px
  - **Action**: Opens pet details modal (spec 027)

**Card Spacing**:
- Gap between cards: 8px
- Padding within card: 20px vertical, 20px horizontal

## Typography

- **Title**: Hind Regular, 24px, #2D2D2D
- **Button text**: Hind Regular, 16px
- **Card text**: Arial Regular, 16px
- **Location/Description**: #4A5565 (gray)
- **Species/Breed**: #101828 (dark)
- **Date**: #6A7282 (gray)
- **Status badge**: White text on colored background

## Colors

- **Primary Blue**: #155DFC (buttons, status badge for FOUND)
- **Red**: #FF0000 (status badge for MISSING)
- **Gray shades**: 
  - #4A5565 (location, description)
  - #6A7282 (date)
  - #E5E9EC (borders, filter chips background)
- **Sidebar**: #4F3C4C (dark background)
- **Card background**: White (#FFFFFF)
- **Page background**: White

## Responsive Behavior

### Desktop (1024px+)
- Full layout with sidebar (219px) and content area (1181px)
- All filters visible in grid layout
- Cards display full structure

### Tablet (768px - 1023px)
- Sidebar may collapse or reduce width
- Filters may stack or reduce columns
- Cards maintain structure

### Mobile (320px - 767px)
- Sidebar hidden or collapsible
- Filters stack vertically
- Cards adapt to full width
- "Details" button may adjust size

## Integration with Pet Details Modal

- **Trigger**: "Details" button on each animal card
- **Button Style**: Outlined blue (#155DFC border, transparent background, blue text)
- **Button Size**: 127px × 44px
- **Button Position**: Right section of card, bottom area
- **Action**: Opens pet details modal (spec 027) with pet ID
- **Focus Management**: Focus moves to modal when opened, returns to button when closed

