# Data Model: Android Animal List Screen Layout Update

## Overview

This feature refreshes the UI presentation of the Animal List screen on Android to match the new Figma design ("Missing animals list app", node-id=297-7556). It reuses the existing domain entities and ViewModel state, focusing on how data is visually rendered in the card layout.

## Entities

### Animal (list view display)

**Purpose**: Represents an animal case as displayed in the Android Animal List screen. The card layout organizes fields into three visual sections: photo, info, and status.

**Fields displayed per card**:

| Field | Display Location | Typography | Color |
|-------|-----------------|------------|-------|
| **photoUrl** | Left section (64px circle) | N/A | Fallback: gray placeholder with initial |
| **location** | Middle, Row 1 | Arial 13px | #4A5565 (gray) |
| **distance** | Middle, Row 1 (after "•") | Arial 13px | #4A5565 (gray) |
| **species** | Middle, Row 2 | Arial 14px | #101828 (dark) |
| **breed** | Middle, Row 2 (after "•") | Arial 14px | #101828 (dark) |
| **status** | Right, Top | Arial 13px | White on badge (MISSING=#FF0000, FOUND=#155DFC) |
| **date** | Right, Bottom | Arial 14px | #6A7282 (gray) |

**Notes**:
- Location row format: `[icon] {location} • {distance}`
- Species/breed row format: `{species} • {breed}`
- Status badge is pill-shaped with rounded corners
- Date format: DD/MM/YYYY (e.g., "18/11/2025")

### Screen Header

**Purpose**: Displays the app branding at the top of the screen.

| Element | Value | Typography | Color |
|---------|-------|------------|-------|
| **title** | "PetSpot" | Hind Regular 32px | rgba(0,0,0,0.8) |

### Primary Button

**Purpose**: Floating call-to-action for reporting a missing animal.

| Element | Value | Typography | Color |
|---------|-------|------------|-------|
| **label** | "Report a Missing Animal" | Hind Regular 14px | White |
| **icon** | Report icon (trailing) | N/A | White |
| **background** | N/A | N/A | #155DFC (blue) |

## UI State (Android only)

The feature relies on the existing `AnimalListUiState` from the MVI pattern. No new state fields are added.

```kotlin
data class AnimalListUiState(
    val animals: List<Animal> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    val isEmpty: Boolean get() = animals.isEmpty() && !isLoading && error == null
}
```

**Visual mapping per state**:

| State | Visual Representation |
|-------|----------------------|
| `isLoading = true` | Centered loading indicator |
| `error != null` | Error message with retry button |
| `isEmpty = true` | Empty state message |
| `animals.isNotEmpty()` | Scrollable list of styled animal cards |

## Card Layout Dimensions

Based on Figma design (node-id=297-7556):

| Property | Value |
|----------|-------|
| Card height | 100px |
| Card corner radius | 14dp |
| Card border | 1px solid #E5E9EC |
| Card background | White |
| Photo size | 64px diameter (circular) |
| Card horizontal padding | 8dp (between photo and info) |
| Card vertical padding | 20dp top |
| List item spacing | 8dp between cards |
| List horizontal padding | 23dp |

## Button Dimensions

| Property | Value |
|----------|-------|
| Button corner radius | 22dp |
| Button padding | 21dp horizontal |
| Button shadow | 0px 4px 4px rgba(0,0,0,0.1) |
| Button background | #155DFC |

## Color Palette

| Name | Hex | Usage |
|------|-----|-------|
| Primary Blue | #155DFC | Button background, FOUND badge |
| Missing Red | #FF0000 | MISSING badge |
| Text Dark | #101828 | Species/breed text |
| Text Gray | #4A5565 | Location text |
| Text Light Gray | #6A7282 | Date text |
| Border Gray | #E5E9EC | Card border |
| Title Black | rgba(0,0,0,0.8) | Screen title |
