# Research: iOS Fullscreen Map - Display Pin Annotation Details

**Branch**: `KAN-32-ios-fullscreen-map-annotation` | **Date**: 2025-01-08

## Research Tasks

### 1. iOS 18 MapKit Selection API for Custom Annotations

**Decision**: Use SwiftUI `Map` with custom `Annotation` content containing both pin and callout. Track selection in ViewModel via `@Published var selectedPinId: String?`.

**Rationale**:
- iOS 17+ introduced `Map` with `selection` binding for `MapSelection<T>` where T is `Identifiable`
- iOS 18+ improved annotation selection with `mapFeatureSelectionContent` modifier
- However, for fully custom callouts (not using MKMapItem), the recommended approach is:
  1. Track selected pin ID in ViewModel via `@Published var selectedPinId: String?`
  2. Add `onTapGesture` to pin annotations to trigger selection
  3. **Embed callout inside Annotation** - this ensures callout appears directly above the pin and moves with it during map gestures

**Why embed callout in Annotation (not overlay)?**:
- `.overlay(alignment: .top)` places callout at fixed screen position, not relative to pin
- Embedding in `Annotation` makes callout follow pin position automatically
- Pointer arrow naturally points to pin below
- SwiftUI handles z-ordering (selected annotation renders on top)

**Alternatives considered**:
- `MKAnnotationView` with `calloutAccessoryView` (UIKit-based, requires `UIViewRepresentable` wrapping)
- `mapItemDetailPopover` modifier (only works with `MKMapItem`, not custom data)
- `mapFeatureSelectionContent` (designed for system map features, not custom content)
- `.overlay()` on Map (callout doesn't follow pin position - rejected)
- `GeometryReader` + coordinate conversion (complex, fragile with map transforms - rejected)

**Implementation Pattern**:
```swift
// ViewModel tracks selection
@Published var selectedPinId: String?

// Callout embedded in Annotation - appears above pin
Map(initialPosition: .region(viewModel.mapRegion)) {
    ForEach(viewModel.pins) { pin in
        Annotation("", coordinate: pin.coordinate, anchor: .bottom) {
            ZStack(alignment: .bottom) {
                // Callout appears ABOVE pin when selected
                if viewModel.selectedPinId == pin.id,
                   let model = viewModel.calloutModel(for: pin) {
                    AnnotationCalloutView(model: model)
                        .offset(y: -10) // Gap between callout and pin
                }
                
                // Pin always visible
                TeardropPin(color: pin.pinColor)
                    .onTapGesture {
                        viewModel.selectPin(pin.id)
                    }
            }
        }
        .accessibilityIdentifier("fullscreenMap.pin.\(pin.id)")
    }
}
.onTapGesture { _ in
    viewModel.deselectPin() // Dismiss on map tap
}
```

**Note on anchor**: Using `.bottom` anchor means the coordinate points to bottom of the ZStack (pin tip). Callout stacks above via ZStack alignment.

### 2. Custom Callout Positioning with Pointer Arrow

**Decision**: Create custom `AnnotationCalloutView` with integrated pointer arrow at bottom. Embed callout inside `Annotation` content (above pin in ZStack) for automatic positioning.

**Rationale**:
- Figma design (node 1192:5893) shows white card with specific styling:
  - 12px border radius
  - Drop shadow: `0px 3px 14px rgba(0,0,0,0.4)`
  - Pointer arrow pointing down to pin
- By embedding callout in `Annotation` ZStack:
  - Callout automatically positions above the pin
  - Callout moves with pin during pan/zoom gestures
  - No complex coordinate calculations needed
  - Pointer arrow in callout naturally points to pin below

**Design Specifications** (from Figma):
- Container: White background, 12px corner radius
- Width: Fixed intrinsic width ~262px (21px left padding + 216px content + 25px right padding)
- Shadow: 0px 3px 14px with 40% opacity black
- Photo: 216px width × 120px height, 8px border radius
- Pet name: 16px bold, #333333
- Info fields: 13px regular, #666666 (with emoji prefixes)
- Description: 14px regular, #444444
- Status badge: 12px bold white text, 12px border radius, background per status
- Pointer arrow: Triangle at bottom center pointing down

**Pointer Arrow Implementation**:
```swift
// Bottom pointer arrow as part of callout
struct CalloutPointer: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        path.move(to: CGPoint(x: rect.midX - 10, y: 0))
        path.addLine(to: CGPoint(x: rect.midX, y: 10))
        path.addLine(to: CGPoint(x: rect.midX + 10, y: 0))
        path.closeSubpath()
        return path
    }
}

// Usage in AnnotationCalloutView
VStack(spacing: 0) {
    cardContent
    CalloutPointer()
        .fill(Color.white)
        .frame(width: 20, height: 10)
        .shadow(color: .black.opacity(0.4), radius: 7, x: 0, y: 3)
}
```

**Edge Case - Insufficient Space Above**:
- Per FR-014, if insufficient space above pin, callout should position below with upward arrow
- For MVP: Accept that callout may extend beyond screen edges at map boundaries
- Future enhancement: Detect screen edge proximity and flip callout orientation

**Alternatives considered**:
- Using `GeometryReader` to calculate absolute pin position (complex, breaks with map transforms)
- `MKAnnotationView` with custom callout (requires UIKit bridge, less SwiftUI-native)
- Popover modifier (doesn't support custom arrow styling or positioning)
- `.overlay()` on Map container (callout doesn't follow pin - rejected)

### 3. Callout Dismissal Behavior

**Decision**: Implement three dismissal triggers per FR-010 through FR-012:
1. Tap elsewhere on map → dismiss callout
2. Tap same pin again → toggle (dismiss) callout
3. Tap different pin → replace callout with new pin's callout

**Rationale**:
- Standard map callout UX pattern familiar to users
- Toggle behavior prevents accidental double-tap issues
- Replacement behavior is more intuitive than requiring dismiss then select

**Implementation**:
```swift
func selectPin(_ pinId: String) {
    if selectedPinId == pinId {
        // FR-011: Toggle - same pin tapped again
        selectedPinId = nil
    } else {
        // FR-012: Replace - different pin tapped
        selectedPinId = pinId
    }
}

func deselectPin() {
    // FR-010: Dismiss on map tap
    selectedPinId = nil
}
```

### 4. Pet Photo Placeholder Handling

**Decision**: Reuse existing placeholder pattern from `AnnouncementCardView` for consistency.

**Rationale**:
- FR-005 specifies placeholder matching Announcement List: circular pawprint icon (24pt, #93A2B4) on #EEEEEE circle (63pt)
- Existing code in `AnnouncementCardView.swift` already implements this exact pattern
- Annotation callout uses same immediate fallback (no retry) per FR-005

**Existing Pattern** (from `AnnouncementCardView`):
```swift
private var placeholderImage: some View {
    ZStack {
        Circle()
            .fill(Color(hex: "#EEEEEE"))
            .frame(width: 63, height: 63)
        Image(systemName: "pawprint.fill")
            .font(.system(size: 24))
            .foregroundColor(Color(hex: "#93A2B4"))
    }
}
```

**Adaptation for Annotation**:
- Annotation photo is 216×120px rectangular, not 63pt circular
- Use rounded rectangle (8px radius) instead of circle
- Scale pawprint icon proportionally (24pt → larger for 120px height)

### 5. Status Badge Colors

**Decision**: Use spec-defined colors, not existing `AnnouncementStatus+Presentation` colors.

**Rationale**:
- FR-009 specifies explicit colors:
  - MISSING: Orange background (#FF9500), white text
  - FOUND: Blue background (#155DFC), white text
- Existing `AnnouncementStatus+Presentation.swift` has different colors:
  - `.active` (MISSING): #FF0000 (red)
  - `.found`: #0074FF (different blue)
- Spec takes precedence - annotation may use different styling than list cards

**Implementation**:
Create annotation-specific color extension in `AnnotationCalloutView.Model`. Use existing `L10n` for status text (SwiftGen required per constitution):
```swift
extension AnnouncementStatus {
    /// Badge color specific to annotation callout (differs from list card colors)
    var annotationBadgeColorHex: String {
        switch self {
        case .active: return "#FF9500"  // Orange per FR-009
        case .found: return "#155DFC"   // Blue per FR-009
        case .closed: return "#93A2B4"  // Gray (not in spec, defensive)
        }
    }
}

// In AnnotationCalloutView.Model init:
// Use existing L10n.AnnouncementStatus.active / .found for status text
self.statusText = pin.status.displayName  // Already uses L10n via AnnouncementStatus+Presentation
```

**Note**: `AnnouncementStatus+Presentation.swift` already provides `displayName` property using `L10n.AnnouncementStatus.active` / `.found`. Reuse this for annotation badge text.

### 6. Date and Coordinate Formatting

**Decision**: Duplicate formatting logic from `PetDetailsViewModel` in `AnnotationCalloutView.Model` with TODO comment for future extraction.

**Rationale**:
- FR-017: Location coordinates in exact same format as announcement list and pet details
- FR-018: Date formatted as MMM dd, yyyy (same as Pet Details)
- `PetDetailsViewModel` already has `formatDate` and `formatCoordinates` functions
- For now: duplicate to avoid coupling, mark with TODO for future refactoring

**Existing Formatters** (from `PetDetailsViewModel`):
```swift
// Date: "yyyy-MM-dd" → "MMM dd, yyyy"
func formatDate(_ dateString: String) -> String

// Coordinates: "52.2297° N, 21.0122° E"
func formatCoordinates(latitude: Double, longitude: Double) -> String
```

**Implementation** (in `AnnotationCalloutView_Model.swift`):
```swift
extension AnnotationCalloutView.Model {
    // TODO: Extract to shared utility in /iosApp/iosApp/FoundationAdditions/DateFormatting.swift
    static func formatDate(_ dateString: String) -> String {
        let inputFormatter = DateFormatter()
        inputFormatter.dateFormat = "yyyy-MM-dd"
        
        guard let date = inputFormatter.date(from: dateString) else {
            return dateString
        }
        
        let outputFormatter = DateFormatter()
        outputFormatter.dateFormat = "MMM dd, yyyy"
        return outputFormatter.string(from: date)
    }
    
    // TODO: Extract to shared utility in /iosApp/iosApp/FoundationAdditions/CoordinateFormatting.swift
    static func formatCoordinates(_ coordinate: CLLocationCoordinate2D) -> String {
        let latDirection = coordinate.latitude >= 0 ? "N" : "S"
        let lonDirection = coordinate.longitude >= 0 ? "E" : "W"
        let lat = abs(coordinate.latitude)
        let lon = abs(coordinate.longitude)
        return String(format: "%.4f° %@, %.4f° %@", lat, latDirection, lon, lonDirection)
    }
}
```

**Future Refactoring**: Extract both formatters to `/iosApp/iosApp/FoundationAdditions/` and update `PetDetailsViewModel` and `AnnotationCalloutView.Model` to use shared implementation.

### 7. MapPin Model Extension

**Decision**: Extend `MapPin` to include all announcement fields needed for callout display.

**Rationale**:
- Current `MapPin` has only: id, coordinate, species, status
- Callout needs: name, photoUrl, breed, lastSeenDate, email, phone, description
- Extending `MapPin` keeps data in sync with pin lifecycle

**Extended Model**:
```swift
struct MapPin: Identifiable, Equatable {
    let id: String
    let coordinate: CLLocationCoordinate2D
    let species: AnimalSpecies
    let status: AnnouncementStatus
    
    // NEW fields for annotation callout
    let name: String?
    let photoUrl: String
    let breed: String?
    let lastSeenDate: String
    let email: String?
    let phone: String?
    let description: String?
}
```

### 8. Localization Strategy

**Decision**: Reuse existing localization strings, add minimal new strings only if needed.

**Rationale**:
- Constitution requires SwiftGen for all user-facing strings
- Annotation uses emoji prefixes (📍📅📧📞) which are consistent across languages
- Status badge text reuses existing `L10n.AnnouncementStatus.active` / `.found`

**Existing Strings to Reuse**:
```swift
// Already defined in AnnouncementStatus+Presentation via L10n:
L10n.AnnouncementStatus.active  // "MISSING" or localized equivalent
L10n.AnnouncementStatus.found   // "FOUND" or localized equivalent
```

**New Strings** (in `Localizable.strings`) - only if pet name is nil:
```
// Fallback for missing pet name
"annotationCallout.unknownPet" = "Unknown Pet";
```

**Note**: All other text in annotation comes from announcement data (name, breed, description, contact info) which is user-generated content, not localized UI strings.

## Summary of Decisions

| Topic | Decision | Key Rationale |
|-------|----------|---------------|
| Selection API | `onTapGesture` + ViewModel state | Full control over custom callout UI |
| Callout Positioning | Inside `Annotation` content (ZStack) | Callout follows pin during pan/zoom |
| Dismissal | Triple trigger (map tap, same pin, different pin) | Standard UX + spec requirements (FR-010, FR-011, FR-012) |
| Photo Placeholder | Rounded rectangle with pawprint | Consistency with FR-005 |
| Status Colors | #FF9500 (MISSING), #155DFC (FOUND) | Per FR-009 specification |
| Status Text | Reuse `L10n.AnnouncementStatus.*` via `displayName` | SwiftGen required per constitution |
| Date/Coord Format | Duplicate with TODO for extraction | FR-017, FR-018 consistency, avoid coupling |
| MapPin Extension | Add all announcement fields | Single data source for callout |
| Localization | Emoji prefixes + L10n for status/fallbacks | Constitution compliance |

