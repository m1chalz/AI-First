# Data Model: iOS Fullscreen Map - Display Pin Annotation Details

**Branch**: `KAN-32-ios-fullscreen-map-annotation` | **Date**: 2025-01-08

## Entity Overview

This feature extends existing models without introducing new domain entities. The annotation callout is a presentation concern, not a domain concept.

## Modified Entities

### MapPin (Extended)

**Location**: `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/MapPin.swift`

**Current Fields**:
| Field | Type | Description |
|-------|------|-------------|
| `id` | `String` | Unique identifier from announcement |
| `coordinate` | `CLLocationCoordinate2D` | Pin position (latitude/longitude) |
| `species` | `AnimalSpecies` | Animal species for pin categorization |
| `status` | `AnnouncementStatus` | Status determines pin color |

**New Fields** (for callout display):
| Field | Type | Description |
|-------|------|-------------|
| `name` | `String?` | Pet name (nullable) |
| `photoUrl` | `String` | Photo URL or empty string |
| `breed` | `String?` | Breed name (nullable) |
| `lastSeenDate` | `String` | Date in YYYY-MM-DD format |
| `email` | `String?` | Owner email (nullable) |
| `phone` | `String?` | Owner phone (nullable) |
| `description` | `String?` | Additional description (nullable) |

**Mapping from Announcement**:
```swift
init(from announcement: Announcement) {
    self.id = announcement.id
    self.coordinate = CLLocationCoordinate2D(
        latitude: announcement.coordinate.latitude,
        longitude: announcement.coordinate.longitude
    )
    self.species = announcement.species
    self.status = announcement.status
    // NEW
    self.name = announcement.name
    self.photoUrl = announcement.photoUrl
    self.breed = announcement.breed
    self.lastSeenDate = announcement.lastSeenDate
    self.email = announcement.email
    self.phone = announcement.phone
    self.description = announcement.description
}
```

## New Presentation Models

### AnnotationCalloutView.Model

**Location**: `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/AnnotationCalloutView_Model.swift`

**Purpose**: Presentation-ready model for annotation callout card. Contains formatted strings ready for display.

| Field | Type | Description |
|-------|------|-------------|
| `photoUrl` | `String?` | Valid photo URL or nil for placeholder |
| `petName` | `String` | Pet name (never nil in display) |
| `speciesAndBreed` | `String` | Formatted as "Species • Breed" |
| `locationText` | `String` | Formatted as "📍 52.2297° N, 21.0122° E" |
| `dateText` | `String` | Formatted as "📅 Jan 08, 2025" |
| `emailText` | `String?` | Formatted as "📧 email@example.com" or nil |
| `phoneText` | `String?` | Formatted as "📞 (555) 123-4567" or nil |
| `descriptionText` | `String?` | Description text or nil |
| `statusText` | `String` | "MISSING" or "FOUND" |
| `statusColorHex` | `String` | "#FF9500" or "#155DFC" |
| `accessibilityId` | `String` | "fullscreenMap.annotation.{pinId}" |

**Factory Method**:
```swift
extension AnnotationCalloutView.Model {
    init(from pin: MapPin) {
        // Photo URL validation
        self.photoUrl = pin.photoUrl.isEmpty ? nil : pin.photoUrl
        
        // Pet name with fallback (L10n required)
        self.petName = pin.name ?? L10n.AnnotationCallout.unknownPet
        
        // Species • Breed formatting (omit breed if nil)
        let speciesName = pin.species.displayName
        if let breed = pin.breed {
            self.speciesAndBreed = "\(speciesName) • \(breed)"
        } else {
            self.speciesAndBreed = speciesName
        }
        
        // Location with emoji prefix
        // TODO: Extract formatCoordinates to shared utility in FoundationAdditions/
        self.locationText = "📍 \(Self.formatCoordinates(pin.coordinate))"
        
        // Date with emoji prefix
        // TODO: Extract formatDate to shared utility in FoundationAdditions/
        self.dateText = "📅 \(Self.formatDate(pin.lastSeenDate))"
        
        // Optional contact fields with emoji prefixes
        self.emailText = pin.email.map { "📧 \($0)" }
        self.phoneText = pin.phone.map { "📞 \($0)" }
        
        // Optional description (no prefix)
        self.descriptionText = pin.description
        
        // Status badge - reuse existing L10n via AnnouncementStatus+Presentation.displayName
        self.statusText = pin.status.displayName  // Uses L10n.AnnouncementStatus.active/found
        self.statusColorHex = pin.status.annotationBadgeColorHex  // Annotation-specific colors
        
        self.accessibilityId = "fullscreenMap.annotation.\(pin.id)"
    }
}
```

**Note**: Status text uses existing `displayName` property from `AnnouncementStatus+Presentation.swift` which already uses SwiftGen (`L10n.AnnouncementStatus.active`, `L10n.AnnouncementStatus.found`). Only the badge colors differ between annotation and list card.

## ViewModel State Changes

### FullscreenMapViewModel (Extended)

**New Published Properties**:
| Property | Type | Description |
|----------|------|-------------|
| `selectedPinId` | `String?` | Currently selected pin ID (nil = no selection) |

**New Methods**:
| Method | Parameters | Return | Description |
|--------|------------|--------|-------------|
| `calloutModel(for:)` | `pin: MapPin` | `AnnotationCalloutView.Model` | Creates callout model for given pin |
| `selectPin(_:)` | `pinId: String` | `Void` | Toggle/replace pin selection (FR-011, FR-012) |
| `deselectPin()` | - | `Void` | Clear selection on map tap (FR-010) |

**Note**: `calloutModel(for:)` is a method (not computed property) because it's called inside `ForEach` loop for each pin. The View checks `selectedPinId == pin.id` first, then calls this method only for the selected pin.

## Validation Rules

### From Spec Requirements

| Field | Rule | Source |
|-------|------|--------|
| `photoUrl` | Empty/invalid → placeholder | FR-005 |
| `description` | Nil → omit field | FR-006 |
| `phone` | Nil → omit field | FR-007 |
| `email` | Nil → omit field | FR-008 |
| `status` | Only MISSING or FOUND | FR-009 |
| `lastSeenDate` | Format: MMM dd, yyyy | FR-018 |
| `coordinate` | Format: same as Pet Details | FR-017 |

## State Transitions

### Selection State Machine

```
┌─────────────────────────────────────────────────────────────┐
│                      No Selection                            │
│                   selectedPinId = nil                        │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           │ User taps Pin A
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    Pin A Selected                            │
│                selectedPinId = "A"                           │
│              Callout displayed for Pin A                     │
└──────────────────────────┬──────────────────────────────────┘
                           │
          ┌────────────────┼────────────────┐
          │                │                │
          │ Tap Pin A      │ Tap Pin B      │ Tap Map
          │ (toggle)       │ (replace)      │ (dismiss)
          ▼                ▼                ▼
┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│  No Selection   │ │  Pin B Selected │ │  No Selection   │
│  selectedPinId  │ │  selectedPinId  │ │  selectedPinId  │
│      = nil      │ │      = "B"      │ │      = nil      │
└─────────────────┘ └─────────────────┘ └─────────────────┘
```

## Relationship Diagram

```
┌──────────────────────────────────────────────────────────────────┐
│                         Announcement                              │
│  (Domain Model - existing, unchanged)                             │
│                                                                   │
│  id, name, photoUrl, coordinate, species, breed, gender,         │
│  status, lastSeenDate, description, email, phone                 │
└──────────────────────────────┬───────────────────────────────────┘
                               │
                               │ MapPin.init(from:)
                               ▼
┌──────────────────────────────────────────────────────────────────┐
│                            MapPin                                 │
│  (Extended Presentation Model)                                    │
│                                                                   │
│  id, coordinate, species, status                                 │
│  + name, photoUrl, breed, lastSeenDate, email, phone, description│
└──────────────────────────────┬───────────────────────────────────┘
                               │
                               │ AnnotationCalloutView.Model.init(from:)
                               ▼
┌──────────────────────────────────────────────────────────────────┐
│                  AnnotationCalloutView.Model                      │
│  (Formatted Presentation Model)                                   │
│                                                                   │
│  photoUrl?, petName, speciesAndBreed, locationText, dateText,    │
│  emailText?, phoneText?, descriptionText?, statusText,           │
│  statusColorHex, accessibilityId                                 │
└──────────────────────────────────────────────────────────────────┘
```

## Data Flow

```
Repository.getAnnouncements()
         │
         ▼
┌─────────────────┐
│  [Announcement] │
└────────┬────────┘
         │
         │ FullscreenMapViewModel.fetchPins()
         │ maps to MapPin array
         ▼
┌─────────────────┐
│    [MapPin]     │  ← @Published pins
└────────┬────────┘
         │
         │ User taps pin → selectPin(id)
         │ selectedPinId set to pin.id
         ▼
┌─────────────────┐
│  selectedPinId  │  ← @Published (String?)
└────────┬────────┘
         │
         │ View checks: selectedPinId == pin.id
         │ If true → calls calloutModel(for: pin)
         ▼
┌──────────────────────────────┐
│ AnnotationCalloutView.Model  │  ← Created on demand
└────────┬─────────────────────┘
         │
         │ SwiftUI renders callout inside Annotation
         │ (above pin in ZStack)
         ▼
┌──────────────────────────────┐
│   AnnotationCalloutView      │  ← Inside Annotation content
└──────────────────────────────┘
```

**Why inside Annotation (not overlay)?**
- Callout automatically follows pin position during map gestures
- Pointer arrow naturally points to pin below
- No complex coordinate conversion needed

