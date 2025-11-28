# Figma Design Context: Animal Description Screen (iOS)

**Source**: [Figma Node 297:8209](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-8209&m=dev)  
**Extracted**: 2025-11-28  
**Screenshot**: See attached (297-8209-screenshot.png)

## Screen Structure

### Header
- Back button (circular, 40x40px, border #e5e9ec)
- Title: "Animal description" (Hind Regular 14px, #2d2d2d, centered)
- Progress indicator: Circular badge with "3/4" (Plus Jakarta Sans Bold 11.667px)

### Content Area

#### Title Section
- **Main heading**: "Your pet's details" (Inter Regular 32px, #2d2d2d)
- **Subtitle**: "Fill out the details about the missing animal." (Inter Regular 16px, #545f71)
- **Spacing**: 16px gap between title and subtitle, 24px gap after subtitle

#### Form Fields (with 24px vertical spacing between fields)

1. **Date of disappearance**
   - Label: Hind Regular 16px, #364153
   - Input: 49.333px height, rounded 10px, border #d1d5dc (0.667px)
   - Placeholder/value: "18/11/2025" (Hind Regular 16px, #364153)
   - Icon: Calendar icon (24x24px) aligned right
   - Gap: 8px between label and input

2. **Animal species** (Dropdown)
   - Label: Hind Regular 16px, #364153
   - Input: 41.323px height, rounded 10px, border #d1d5dc
   - Background: #f3f3f5 (disabled state)
   - Placeholder: "Select an option" (Hind Regular 16px, rgba(10,10,10,0.5))
   - Icon: Caret down (20x20px) aligned right
   - Gap: 8px between label and input

3. **Animal race** (Text input - initially disabled)
   - Label: Hind Regular 16px, #93a2b4 (disabled color)
   - Input: 41px height, rounded 10px, border #d1d5dc
   - Background: #f9f9fa with 50% opacity (disabled state)
   - Icon: Down arrow (20x20px) aligned right when disabled
   - Gap: 8px between label and input

4. **Gender selector** (Radio buttons - horizontal)
   - Two cards side by side with 8px gap
   - Card: 57.333px height, rounded 4px, border #e5e9ec (0.667px)
   - Radio icon: 24x24px (checked/unchecked variants)
   - Label: "Female" / "Male" (Hind Regular 16px, #545f71)
   - Padding: 8.667px horizontal, 0.667px vertical

5. **Animal age (optional)**
   - Label: Hind Regular 16px, #364153
   - Input: 41.323px height, rounded 10px, border #d1d5dc
   - Placeholder: "-" (Hind Regular 16px, rgba(10,10,10,0.5))
   - Gap: 8px between label and input

6. **Request GPS position** (Button - outline style)
   - Height: 40px, rounded 10px
   - Border: 2px solid #155dfc
   - Text: "Request GPS position" (Hind Regular 16px, #155dfc, centered)
   - Background: white

7. **Lat / Long** (Two text inputs side by side)
   - Label: Hind Regular 16px, #364153 (shared for both fields)
   - Each input: 147-158px width, 41px height, rounded 10px, border #d1d5dc
   - Gap: 10px between the two inputs
   - Placeholder: "00000" (Hind Regular 16px, rgba(10,10,10,0.5))
   - Gap: 8px between label and inputs (32.04px from label top to input top)

8. **Animal additional description (optional)**
   - Label: Hind Regular 16px, #364153
   - Textarea: 96px height, 328px width, rounded 10px, border #d1d5dc
   - Gap: 8px between label and textarea (32px from label top to textarea top)

#### Footer
- **Continue button** (Primary CTA)
  - Position: Fixed at bottom, 22px from left, 999px from top
  - Size: 327px width, 52px height, rounded 10px
  - Background: #155dfc
  - Text: "Continue" (Hind Regular 18px, white, centered)
  - Padding: 24px horizontal, 12px vertical

## Design Tokens

### Colors
```swift
// Primary
let primaryBlue = "#155dfc"

// Text
let textPrimary = "#2d2d2d"
let textSecondary = "#545f71"
let textTertiary = "#364153"
let textDisabled = "#93a2b4"
let textPlaceholder = "rgba(10,10,10,0.5)" // or "#0a0a0a" with 50% opacity

// Backgrounds
let backgroundWhite = "#ffffff"
let backgroundDisabled = "#f3f3f5"
let backgroundDisabledAlt = "#f9f9fa"

// Borders
let borderDefault = "#d1d5dc"
let borderLight = "#e5e9ec"
let borderPrimary = "#155dfc"
```

### Typography
```swift
// Fonts
let fontInter = "Inter"
let fontHind = "Hind"
let fontPlusJakartaSans = "Plus Jakarta Sans"

// Sizes
let textXLarge: CGFloat = 32  // Title
let textLarge: CGFloat = 18   // Primary button
let textMedium: CGFloat = 16  // Labels, inputs, secondary button
let textSmall: CGFloat = 14   // Header title
let textXSmall: CGFloat = 11.667 // Progress indicator

// Weights
let weightRegular = Font.Weight.regular // 400
let weightBold = Font.Weight.bold
```

### Spacing
```swift
// Gaps
let gapXLarge: CGFloat = 24  // Between form fields
let gapLarge: CGFloat = 16   // Title to subtitle
let gapMedium: CGFloat = 12  // Inside radio buttons
let gapSmall: CGFloat = 10   // Between lat/long inputs
let gapXSmall: CGFloat = 8   // Label to input

// Padding
let paddingXLarge: CGFloat = 24 // Button horizontal
let paddingLarge: CGFloat = 16  // Input horizontal
let paddingMedium: CGFloat = 12 // Button vertical
let paddingSmall: CGFloat = 8   // Input vertical, radio button horizontal

// Margins
let marginHorizontal: CGFloat = 22  // Screen edges
let marginTop: CGFloat = 71         // From header to content
```

### Dimensions
```swift
// Input heights
let inputHeightDefault: CGFloat = 41
let inputHeightTall: CGFloat = 49.333 // Date picker
let inputHeightTextarea: CGFloat = 96

// Button heights
let buttonHeightDefault: CGFloat = 40  // GPS button
let buttonHeightPrimary: CGFloat = 52  // Continue button

// Widths
let screenWidth: CGFloat = 375
let contentWidth: CGFloat = 328 // screenWidth - (marginHorizontal * 2)
let latLongWidth: CGFloat = 147-158 // Each field (approximate)

// Icon sizes
let iconSizeDefault: CGFloat = 20
let iconSizeLarge: CGFloat = 24

// Border radius
let radiusLarge: CGFloat = 10  // Inputs, buttons
let radiusSmall: CGFloat = 4   // Radio buttons
```

### Border Widths
```swift
let borderThin: CGFloat = 0.667
let borderMedium: CGFloat = 2  // GPS button border
```

## Component States

### Species Dropdown (Disabled → Enabled)
- **Disabled**: Background #f3f3f5, cursor not allowed
- **Enabled**: Background white, interactive

### Race Text Field (Disabled → Enabled)
- **Disabled**: Background #f9f9fa with 50% opacity, label color #93a2b4, icon visible
- **Enabled**: Background white, label color #364153, icon hidden (becomes regular text input)

### Radio Buttons
- **Unchecked**: Empty circle icon
- **Checked**: Filled circle with dot icon

### Validation States (Not shown in design, inferred from spec)
- **Error**: Red border (#FF0000 or similar), inline error text below field
- **Success**: Default border (no special styling in design)

## Accessibility Identifiers (Must be added)

Following `{screen}.{element}.{action}` convention:

```swift
"animalDescription.backButton.tap"
"animalDescription.datePicker.tap"
"animalDescription.speciesDropdown.tap"
"animalDescription.raceTextField.input"
"animalDescription.genderFemale.tap"
"animalDescription.genderMale.tap"
"animalDescription.ageTextField.input"
"animalDescription.requestGPSButton.tap"
"animalDescription.latitudeTextField.input"
"animalDescription.longitudeTextField.input"
"animalDescription.descriptionTextArea.input"
"animalDescription.continueButton.tap"
```

## Design Notes

1. **No helper text shown in default state**: Error messages and helper text will appear inline below fields when validation fails (per spec FR-012)

2. **Character counter for description**: Not visible in design - must be added per spec FR-011 (500 char limit with live counter)

3. **GPS success helper text**: Not visible in default state - will appear after successful GPS capture per spec FR-009

4. **Toast notifications**: Not visible in design - will be triggered by validation errors per spec FR-012

5. **Loading states**: Not shown in design - spinner or loading indicator needed for GPS request

6. **Lat/Long field widths**: Design shows approximate ~147-158px each with 10px gap to fit 328px container width

7. **Age field placeholder**: Shows "-" but spec doesn't mandate this - could use "" or "Age in years"

## Assets Required

The following image assets are referenced in the Figma design (URLs valid for 7 days):

- Header status bar icons (battery, signal, wifi)
- Back arrow icon
- Calendar icon (date picker)
- Caret down icon (dropdowns)
- Radio button checked icon
- Radio button unchecked icon
- Progress indicator background

**Note**: Replace with local SF Symbols or custom assets in iOS implementation:
- Back arrow: `chevron.left` SF Symbol
- Calendar: `calendar` SF Symbol
- Caret down: `chevron.down` SF Symbol
- Radio: Native iOS radio button or custom component

## Localization Keys Needed

Based on visible text in design:

```
L10n.animalDescriptionTitle = "Animal description"
L10n.animalDescriptionProgress = "3/4"
L10n.yourPetsDetails = "Your pet's details"
L10n.fillOutDetails = "Fill out the details about the missing animal."
L10n.dateOfDisappearance = "Date of disappearance"
L10n.animalSpecies = "Animal species"
L10n.animalRace = "Animal race"
L10n.selectAnOption = "Select an option"
L10n.genderFemale = "Female"
L10n.genderMale = "Male"
L10n.animalAgeOptional = "Animal age (optional)"
L10n.requestGPSPosition = "Request GPS position"
L10n.latLong = "Lat / Long"
L10n.animalDescriptionOptional = "Animal additional description (optional)"
L10n.continueButton = "Continue"
```

Additional keys needed (not visible in design but required by spec):

```
L10n.missingDateError = "Date of disappearance is required"
L10n.missingSpeciesError = "Please select an animal species"
L10n.missingRaceError = "Please enter the animal race"
L10n.missingGenderError = "Please select a gender"
L10n.invalidAgeError = "Age must be between 0 and 40"
L10n.invalidLatitudeError = "Latitude must be between -90 and 90"
L10n.invalidLongitudeError = "Longitude must be between -180 and 180"
L10n.validationToast = "Please correct the errors below"
L10n.gpsPermissionDeniedTitle = "Location Permission Denied"
L10n.gpsPermissionDeniedMessage = "Allow location access in Settings to use GPS position"
L10n.gpsPermissionCancel = "Cancel"
L10n.gpsPermissionSettings = "Go to Settings"
L10n.gpsSuccessHelper = "GPS position captured successfully"
L10n.characterCount = "%d/500" // Format string for character counter
```

## Implementation Priority

1. **Phase 1**: Basic layout and static UI (all fields visible, no interaction)
2. **Phase 2**: Field interactions (date picker, dropdown, text inputs, radio buttons)
3. **Phase 3**: GPS button and location services integration
4. **Phase 4**: Validation logic and error states
5. **Phase 5**: Session persistence and navigation

## Design Decisions Not Explicitly Shown

These must be decided during implementation (refer to spec):

1. **Date picker style**: Use iOS native sheet-style picker (per spec FR-003)
2. **Species dropdown**: Use native Picker or custom modal (decide in Phase 2)
3. **Keyboard types**: Numeric for age/lat/long, default for race/description
4. **Focus behavior**: Auto-focus on first empty required field when returning from navigation?
5. **Scroll behavior**: ScrollView with keyboard avoidance (standard iOS pattern)

