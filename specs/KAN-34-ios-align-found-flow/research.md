# Research: iOS Align Found Flow (3-step)

**Branch**: `KAN-34-ios-align-found-flow` | **Date**: 2026-01-09

## Overview

This document resolves all technical questions for the iOS Found Pet flow restructuring from 4 steps to 3 steps.

---

## Decision 1: Flow Restructuring (4 → 3 steps)

### Current Flow (4 steps)
1. Chip Number (1/4) - entry point
2. Photo (2/4)
3. Description (3/4)
4. Contact Details (4/4)
5. Summary (post-submission)

### New Flow (3 steps per spec FR-001)
1. **Upload photo** (1/3) - new entry point
2. **Pet details** (2/3) - combines microchip + description fields + required location
3. **Contact information** (3/3) - adds optional caregiver phone + address
4. ~~Summary~~ **REMOVED** - flow exits immediately after successful submission

### Decision
Restructure coordinator navigation:
- Entry point changes from `navigateToChipNumber()` → `navigateToPhoto()`
- Merge chip number field into Pet details screen
- Rename "Description" to "Pet details"
- Rename "Contact Details" to "Contact information"
- Add new optional fields to Contact information
- **Remove Summary screen** - `exitFlow()` immediately after successful submission

### Rationale
- Simplifies user journey from 4 to 3 data-entry steps
- Groups related pet information together (species, breed, chip number, location)
- Separates pet data from contact data clearly
- Aligns with Figma designs

### Alternatives Considered
1. **Keep 4 steps, just reorder**: Rejected - spec explicitly requires 3 steps
2. **Merge Contact into Pet details**: Rejected - creates too long a form

---

## Decision 2: Collar Data (Microchip) Field Behavior

### Question
How should the "Collar data (optional)" field behave per FR-008?

### Decision
Use existing `MicrochipNumberFormatter` with these behaviors:
- **Input**: Digits only (0-9), non-digits rejected
- **Storage**: Digits-only string (no hyphens)
- **Display**: Formatted as `00000-00000-00000` (hyphens after 5th and 10th digit)
- **Limit**: Max 15 digits (hard cap)
- **Validation**: No length validation (1-14 digits allowed, empty allowed)
- **Navigation**: Does NOT block Continue when empty or partial

### Rationale
- Existing `MicrochipNumberFormatter` already implements this exact behavior
- Consistent with existing "Report Missing Pet" microchip screen
- Per clarification: "use the same behavior as the existing iOS Missing pet → Microchip number screen"

### Code Reference
```swift
// MicrochipNumberFormatter.swift - already implemented
static func format(_ input: String) -> String
static func extractDigits(_ input: String) -> String
```

---

## Decision 3: Location Validation (Required)

### Question
Per FR-007 and clarification, how should Found location validation work?

### Decision
Latitude and longitude are **REQUIRED** for Pet details:
- Users MUST provide both latitude and longitude before proceeding
- "Use current location" button populates both fields from GPS
- Manual entry allowed (numeric input, same validation as existing)
- Validation: latitude -90 to 90, longitude -180 to 180
- **Continue button blocked** if either field empty or invalid

### Rationale
- Per clarification: "Required; block 'Continue' until both are provided"
- Consistent with backend requirements for found pet reports
- Existing `CoordinateInputView` already handles display/input

### Implementation Note
Current `FoundPetAnimalDescriptionViewModel.validateCoordinates()` **already treats coordinates as required** (same as `MissingPetAnimalDescriptionViewModel`). Both have:
- Comment: `// Both required - must be filled and valid`
- Empty latitude → "Fill in latitude" error
- Empty longitude → "Fill in longitude" error
- Range validation: -90 to 90, -180 to 180

**No changes needed** - just reuse the existing validation logic in `FoundPetPetDetailsViewModel`.

---

## Decision 4: Phone Number Validation

### Question
What validation rule applies to "Your phone number" and "Caregiver phone number"?

### Decision
Use existing validation (per clarification):
```swift
// Existing validation in FoundPetContactDetailsViewModel
private var isPhoneValid: Bool {
    let sanitized = phone.filter { $0.isNumber || $0 == "+" }
    let digitCount = sanitized.filter { $0.isNumber }.count
    return digitCount >= 7 && digitCount <= 11
}
```

**Your phone number**: Required, 7-11 digits, allow `+`
**Caregiver phone number**: Optional, but if non-empty: 7-11 digits, allow `+`

### Rationale
- Per clarification: "Use the existing iOS report flow rule: 7-11 digits (allow +, ignore other chars)"
- Caregiver phone validates only when non-empty (optional field)
- Consistent behavior across all phone fields in the app

---

## Decision 5: New Optional Fields (iOS-only)

### Question
How to handle `caregiverPhoneNumber` and `currentPhysicalAddress` per FR-015, FR-016?

### Decision
**Storage**: Add to `FoundPetReportFlowState`:
```swift
@Published var caregiverPhoneNumber: String?
@Published var currentPhysicalAddress: String?
```

**Backend**: Per FR-016, these fields are **NOT** sent to backend:
- iOS-only fields for UI purposes
- Backend API schema doesn't support them
- Keep in flow state for display during active session
- Cleared when flow exits

### Rationale
- Per clarification: "iOS-only fields (kept in flow state/UI); do not send to backend"
- Allows future backend support without iOS code changes
- Clear separation of concerns

---

## Decision 6: Current Physical Address Field

### Question
What constraints apply to "Current physical address (optional)"?

### Decision
- **Type**: Multiline text area (like existing additional description)
- **Max length**: 500 characters (consistent with description)
- **Validation**: None (optional, any text accepted)
- **Placeholder**: "Full address where the animal is currently located"

### Rationale
- Per spec: "multiline like animal additional description"
- Reuse existing `TextAreaView` component
- 500 chars sufficient for detailed addresses

---

## Decision 7: Localization Strings

### Question
Which new L10n strings are needed per FR-017?

### New Strings Required

```swift
// Step 1: Upload photo (1/3)
"reportFoundPet.photo.screenTitle" = "Upload photo";
"reportFoundPet.photo.heading" = "Upload photo";
"reportFoundPet.photo.body" = "Upload a clear photo of the pet you found. This helps the owner recognize them quickly.";
"reportFoundPet.photo.uploadEmptyPrimary" = "Tap to upload a photo";
"reportFoundPet.photo.uploadEmptySecondary" = "PNG or JPG up to 10 MB";
"reportFoundPet.photo.continueButton" = "Continue";

// Step 2: Pet details (2/3)
"reportFoundPet.petDetails.screenTitle" = "Pet details";
"reportFoundPet.petDetails.heading" = "Pet details";
"reportFoundPet.petDetails.body" = "Tell us what you know about the animal you found.";
"reportFoundPet.petDetails.dateFoundLabel" = "Date found";
"reportFoundPet.petDetails.dateFoundPlaceholder" = "DD/MM/YYYY";
"reportFoundPet.petDetails.collarDataLabel" = "Collar data (optional)";
"reportFoundPet.petDetails.collarDataPlaceholder" = "00000-00000-00000";
"reportFoundPet.petDetails.additionalDescriptionLabel" = "Additional description (optional)";
"reportFoundPet.petDetails.additionalDescriptionPlaceholder" = "Any details that could help identify the pet...";
"reportFoundPet.petDetails.gpsButton" = "Use current location";
"reportFoundPet.petDetails.continueButton" = "Continue";

// Step 3: Contact information (3/3)
"reportFoundPet.contactInfo.screenTitle" = "Contact info";
"reportFoundPet.contactInfo.heading" = "Contact information";
"reportFoundPet.contactInfo.body" = "Share your contact details so the pet's owner can reach you.";
"reportFoundPet.contactInfo.yourPhoneLabel" = "Your phone number";
"reportFoundPet.contactInfo.yourPhonePlaceholder" = "+1 (555) 000-0000";
"reportFoundPet.contactInfo.yourEmailLabel" = "Your email";
"reportFoundPet.contactInfo.yourEmailPlaceholder" = "email@example.com";
"reportFoundPet.contactInfo.caregiverPhoneLabel" = "Caregiver phone number (optional)";
"reportFoundPet.contactInfo.caregiverPhonePlaceholder" = "+1 (555) 000-0000";
"reportFoundPet.contactInfo.currentAddressLabel" = "Current physical address (optional)";
"reportFoundPet.contactInfo.currentAddressPlaceholder" = "Full address where the animal is currently located";
"reportFoundPet.contactInfo.submitButton" = "Submit report";
```

### Rationale
- Exact copy from spec FR-017
- Separate namespace (`reportFoundPet.*`) from existing `reportMissingPet.*`
- Allows Found flow to have distinct messaging

---

## Decision 8: Component Reuse

### Question
Which existing components can be reused?

### Decision

| Component | Reuse | Notes |
|-----------|-------|-------|
| `ValidatedTextField` | ✅ Yes | For all text inputs |
| `TextAreaView` | ✅ Yes | For additional description + current address |
| `DropdownView` | ✅ Yes | For species selection |
| `SelectorView` | ✅ Yes | For gender selection |
| `DateInputView` | ✅ Yes | For date found |
| `CoordinateInputView` | ✅ Yes | For lat/long |
| `AnimalPhotoItemView` | ✅ Yes | For photo display |
| `ToastView` | ✅ Yes | For validation errors |
| `MicrochipNumberFormatter` | ✅ Yes | For collar data formatting |

### Rationale
- Maximum code reuse
- Consistent UI patterns
- Existing components already tested

---

## Decision 9: Files to Modify vs Create

### Files to Modify
1. `FoundPetReportFlowState.swift` - add new fields
2. `FoundPetReportCoordinator.swift` - restructure navigation
3. `FoundPetPhotoView.swift` - update copy (heading, body)
4. `FoundPetPhotoViewModel.swift` - update step indicator (1/3)
5. `FoundPetContactDetailsView.swift` - add new fields
6. `FoundPetContactDetailsViewModel.swift` - handle new fields + validation
7. `Localizable.strings` (en/pl) - add new strings

### Files to Create
1. `FoundPetPetDetailsView.swift` - new combined step 2
2. `FoundPetPetDetailsViewModel.swift` - new ViewModel

### Files to Delete/Deprecate
1. `FoundPetChipNumberView.swift` - merge into Pet details
2. `FoundPetChipNumberViewModel.swift` - merge into Pet details
3. `FoundPetAnimalDescriptionView.swift` - replace with Pet details
4. `FoundPetAnimalDescriptionViewModel.swift` - replace with Pet details
5. `FoundPetSummaryView.swift` - Summary screen removed
6. `FoundPetSummaryView+Constants.swift` - Summary screen removed
7. `FoundPetSummaryViewModel.swift` - Summary screen removed

### Rationale
- Clean slate for step 2 (Pet details) combining chip + description
- Minimal changes to step 1 (Photo) and step 3 (Contact)
- Avoid dead code in codebase

---

## Summary

All technical questions resolved. Ready for Phase 1 design artifacts.

| # | Decision | Status |
|---|----------|--------|
| 1 | Flow restructuring (4→3 steps) | ✅ Resolved |
| 2 | Collar data behavior | ✅ Resolved |
| 3 | Location validation (required) | ✅ Resolved |
| 4 | Phone validation | ✅ Resolved |
| 5 | iOS-only fields | ✅ Resolved |
| 6 | Address field constraints | ✅ Resolved |
| 7 | Localization strings | ✅ Resolved |
| 8 | Component reuse | ✅ Resolved |
| 9 | Files to modify/create | ✅ Resolved |

