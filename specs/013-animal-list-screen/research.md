# Research: Android Animal List Screen Layout Update

## Design Analysis (Figma node-id=297-7556)

The updated Figma design "Missing animals list app" introduces significant visual changes from the current implementation:

### Visual Comparison

| Element | Current Implementation | New Figma Design |
|---------|----------------------|------------------|
| **Screen Title** | "Missing animals list" (center-aligned) | "PetSpot" (left-aligned, 32px Hind) |
| **Card Layout** | Vertical stack: species/breed → location → status → date | Three-column: photo | info rows | status/date |
| **Card Border** | 4dp radius, 2dp elevation shadow | 14dp radius, 1px border, no shadow |
| **Card Height** | Dynamic | Fixed 100px |
| **Photo Size** | 63dp | 64dp |
| **Status Badge** | Below info in column | Right side, top of right column |
| **Date** | "Last seen: {date}" format | "{date}" format, below status badge |
| **Primary Button** | Full-width in bottom bar with shadow | Floating pill-shaped, centered |

### Typography Analysis

| Element | Font | Size | Weight | Color |
|---------|------|------|--------|-------|
| Screen title | Hind | 32px | Regular | rgba(0,0,0,0.8) |
| Location row | Arial | 13px | Regular | #4A5565 |
| Species/breed row | Arial | 14px | Regular | #101828 |
| Status badge | Arial | 13px | Regular | White |
| Date | Arial | 14px | Regular | #6A7282 |
| Button text | Hind | 14px | Regular | White |

## Decisions

### 1. Card layout restructuring

- **Decision**: Restructure the `AnimalCard` composable from a single-column layout to a three-column layout (photo | info | status).
- **Rationale**: The new design places status and date on the right side of the card instead of below other information. This requires a Row-based layout with distinct sections.
- **Alternatives considered**:
  - Keep the current vertical layout and add right-aligned elements (rejected: would not match the Figma visual hierarchy).
  - Create multiple card variants (rejected: adds unnecessary complexity for a single design).

### 2. Screen title change

- **Decision**: Replace `CenterAlignedTopAppBar` with "Missing animals list" to a simpler Column-based layout with left-aligned "PetSpot" text using Hind font.
- **Rationale**: The new design shows "PetSpot" as a brand title, not a functional screen title. It's left-aligned and uses a distinctive font (Hind at 32px).
- **Alternatives considered**:
  - Keep using TopAppBar with different styling (rejected: TopAppBar has built-in centering behavior that doesn't match the design).
  - Use a custom TopAppBar implementation (rejected: simpler to just use a Text composable in the content area).

### 3. Floating button instead of bottom bar

- **Decision**: Replace the `ReportMissingBottomBar` Surface with a floating Box-positioned button.
- **Rationale**: The Figma design shows the button as a floating element, not a full-width bottom bar. This provides a more modern appearance and keeps focus on the content.
- **Alternatives considered**:
  - Keep using Scaffold bottomBar with updated styling (rejected: bottom bars span full width, which doesn't match the pill-shaped centered design).
  - Use a FloatingActionButton (rejected: FAB has specific Material Design constraints that don't match the button label and icon layout).

### 4. Test identifiers

- **Decision**: Update test tags to `animalList.cardItem` for all card items and `animalList.reportButton` for the primary button.
- **Rationale**: Consistent with spec clarifications. The current implementation uses `animalList.item.${animal.id}` and `animalList.reportMissingButton` which need updating.
- **Alternatives considered**:
  - Keep per-item IDs like `animalList.cardItem.${id}` (rejected: spec explicitly requests generic `animalList.cardItem` for simplicity).

### 5. Behavior preservation

- **Decision**: Preserve all existing loading, empty, error, and success state handling. Only adjust visual presentation.
- **Rationale**: Edge cases and flows are already implemented and tested. Changing them would mix behavioral work into a UI-only ticket.
- **Alternatives considered**:
  - Change button visibility rules in special states (rejected: requires new product decisions).
  - Add new interactions like pull-to-refresh (rejected: out of scope for this visual refresh).

### 6. Font handling

- **Decision**: Use Hind font for title and button from Google Fonts, with Arial as fallback for card text.
- **Rationale**: The Figma design specifies Hind for key elements. Android supports loading custom fonts via `fontFamily`.
- **Alternatives considered**:
  - Use system fonts only (rejected: would not match the design's brand identity).
  - Use Hind for all text (rejected: Arial is specified for card details in the design).

### 7. Location icon

- **Decision**: Add a location pin icon before the location text in cards.
- **Rationale**: The Figma design shows a small location icon (16px) before the location name.
- **Alternatives considered**:
  - Skip the icon (rejected: would not match the design).
  - Use a custom icon (rejected: use Material Icons Location icon for consistency).

## Implementation Notes

### Required Changes to AnimalCard.kt

1. Change from vertical Column layout to horizontal Row with three sections
2. Adjust photo size from 63dp to 64dp
3. Update border: remove elevation, add 1px border with #E5E9EC
4. Update corner radius from 4dp to 14dp
5. Add location icon before location text
6. Move status badge and date to right section
7. Update typography for each text element
8. Update test tag from `animalList.item.${animal.id}` to `animalList.cardItem`

### Required Changes to AnimalListContent.kt

1. Remove TopAppBar, add "PetSpot" title in content area
2. Remove Scaffold bottomBar
3. Add floating button positioned at bottom-center
4. Update test tag for button from `animalList.reportMissingButton` to `animalList.reportButton`
5. Update list padding to match design (23dp horizontal)

### Required Assets

1. Location icon (16px) - use Material Icons `LocationOn` or `Place`
2. Report icon for button - requires asset from design (or use Material Icon placeholder)
3. Hind font family from Google Fonts
