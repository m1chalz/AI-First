# Animal List Screen - Design Reference

This directory contains design assets and references for the Animal List Screen feature.

## Figma Links

**Main Figma File**: https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes

### Specific Designs

- **Mobile Version**: https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=48-6096
  - Node ID: `48:6096`
  - Frame: "Missing animals list Mobile"
  - Dimensions: 375px width
  
- **Web Version**: https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=71-9154
  - Node ID: `71:9154`
  - Frame: "Missing animals list web"
  - Dimensions: 1440px width

## Design System Reference

### Colors

| Element | Hex Code | Usage |
|---------|----------|-------|
| Primary text | `#2D2D2D` | Main headings, body text |
| Secondary text | `#545F71` | Supporting text, labels |
| Tertiary text | `#93A2B4` | Dividers, disabled states |
| Background white | `#FAFAFA` | Card backgrounds |
| Light gray | `#EEEEEE` | Placeholders, disabled backgrounds |
| Status "Missing" | `#FF0000` | Missing animal badge |
| Status "Found" | `#0074FF` | Found animal badge |
| Primary action | `#2D2D2D` | Primary buttons |
| Secondary action | `#E5E9EC` | Secondary buttons |
| Web sidebar | `#4F3C4C` | Navigation sidebar (web only) |

### Typography

**Note**: Values shown in pixels (px) from Figma designs. For implementation, use platform-specific units: sp (Android), pt (iOS), px (Web).

| Element | Font | Size (Figma) | Platform Units | Weight | Line Height |
|---------|------|--------------|----------------|--------|-------------|
| Screen title | Inter | 24px | 24sp (Android), 24pt (iOS), 24px (Web) | Regular (400) | 100% |
| Card species | Inter | 16px | 16sp (Android), 16pt (iOS), 16px (Web) | Regular (400) | 100% |
| Card breed | Inter | 14px | 14sp (Android), 14pt (iOS), 14px (Web) | Regular (400) | 100% |
| Card location | Inter | 13px | 13sp (Android), 13pt (iOS), 13px (Web) | Regular (400) | 100% |
| Button label | Inter | 16px | 16sp (Android), 16pt (iOS), 16px (Web) | Regular (400) | 100% |
| Status badge | Roboto | 12px | 12sp (Android), 12pt (iOS), 12px (Web) | Regular (400) | 140% |

### Spacing & Layout

**Note**: Values shown in pixels (px) from Figma designs. For implementation, use platform-specific units: dp (Android), pt (iOS), px (Web).

| Element | Figma Value | Platform Units | Description |
|---------|-------------|----------------|-------------|
| Card gap | 8px | 8dp (Android), 8pt (iOS), 8px (Web) | Vertical spacing between list items |
| Card padding | 16px (horizontal) | 16dp (Android), 16pt (iOS), 16px (Web) | Internal card padding |
| Card radius | 4px | 4dp (Android), 4pt (iOS), 4px (Web) | Border radius for cards |
| Badge radius | 10px | 10dp (Android), 10pt (iOS), 10px (Web) | Border radius for status badges |
| Button radius | 2px | 2dp (Android), 2pt (iOS), 2px (Web) | Border radius for action buttons |
| Image size | 63x63px | 63x63dp (Android), 63x63pt (iOS), 63x63px (Web) | Circular animal image placeholder |
| Mobile width | 328px | match_parent with 24dp padding (Android), full width with 24pt padding (iOS), 328px (Web mobile) | Content width on mobile |
| Web content | 1181px | N/A (Android/iOS), 1181px (Web) | Main content area width on web |
| Web sidebar | 219px | N/A (Android/iOS), 219px (Web) | Navigation sidebar width on web |

### Shadows

| Element | Shadow Value |
|---------|-------------|
| Cards | `0px 1px 4px 0px rgba(0,0,0,0.05)` |

## Component Structure

### Mobile Card

**Note**: On Android and iOS, cards use full screen width with horizontal padding. Only web designs show fixed widths. Values below are from Figma (px); use dp (Android), pt (iOS), px (Web) in implementation.

```
[Card Container - full width with padding, 4dp/pt/px radius, shadow]
  ├─ [Circular Image - 63x63dp/pt/px, gray placeholder]
  ├─ [Content Column]
  │  ├─ [Location Row - icon + text]
  │  └─ [Species | Breed Row]
  └─ [Right Column]
     ├─ [Status Badge - "Missing"/"Found"]
     └─ [Date - DD/MM/YYYY]
```

### Web Card

```
[Card Container - full width, 4px radius, shadow]
  ├─ [Circular Image - 162px section]
  ├─ [Info Column - 197px]
  │  ├─ [Location Row]
  │  └─ [Species | Breed | Gender Icon]
  ├─ [Description Text - flex-grow]
  ├─ [Status Column]
  │  ├─ [Status Badge]
  │  └─ [Date]
  └─ [Actions - 3-dot menu]
```

## Notes for Implementation

1. **Scrolling**: The list should be independently scrollable. On mobile, action buttons float outside the scroll area.
2. **Search Placeholder**: The search/filters area is currently a placeholder button on mobile and a larger area on web.
3. **Empty State**: Design does not show empty state explicitly - implementation should create an appropriate message.
4. **Icons**: 
   - **Android**: Material Design icons (24dp) - use Material Icons library
   - **iOS**: SF Symbols (24pt equivalent) - use built-in SF Symbols catalog
   - **Web**: Material Design icons or custom SVG icons
   - Icon types: location pin, gender symbols (male/female), search/filter
5. **Image Placeholders**: Use a generic animal/pet icon until real images are available.
6. **Responsive Behavior**: Layout changes significantly between mobile (vertical cards) and web (horizontal table-like cards).

## Asset URLs (Valid for 7 days from 2025-11-19)

Mobile design code and assets were generated from Figma on 2025-11-19. Image asset URLs expire after 7 days and will need to be regenerated from Figma if needed after that period.

- Animal icon placeholder: Generated from Figma node
- Location pin icon: Material Design `location_on_24dp`
- Gender icons: Material Design `male_24dp` and `female_24dp`
- Search icon: Material Design `page_info_24dp`
- More options: Material Design `more_vert_24dp`

