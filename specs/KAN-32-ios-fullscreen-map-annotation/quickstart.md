# Quickstart: iOS Fullscreen Map - Display Pin Annotation Details

**Branch**: `KAN-32-ios-fullscreen-map-annotation` | **Date**: 2025-01-08

## Prerequisites

- Xcode 16+ (iOS 18 SDK)
- iPhone 16 Simulator (or physical device with iOS 18+)
- Previous spec KAN-32-ios-fullscreen-map-fetch-pins implemented (pins displayed on map)

## Quick Setup

```bash
# 1. Checkout feature branch
git checkout KAN-32-ios-fullscreen-map-annotation

# 2. Open in Xcode
open iosApp/iosApp.xcodeproj

# 3. Build and run on iPhone 16 Simulator
# Xcode: Product → Run (⌘R)
```

## Development Workflow

### 1. Extend MapPin Model

**File**: `iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/MapPin.swift`

Add new fields for callout content:
```swift
struct MapPin: Identifiable, Equatable {
    let id: String
    let coordinate: CLLocationCoordinate2D
    let species: AnimalSpecies
    let status: AnnouncementStatus
    
    // NEW: Fields for annotation callout
    let name: String?
    let photoUrl: String
    let breed: String?
    let lastSeenDate: String
    let email: String?
    let phone: String?
    let description: String?
    
    init(from announcement: Announcement) {
        // ... existing mapping ...
        // Add new field mappings
    }
}
```

### 2. Create AnnotationCalloutView

**File**: `iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/AnnotationCalloutView.swift`

```swift
import SwiftUI

struct AnnotationCalloutView: View {
    let model: Model
    
    var body: some View {
        VStack(spacing: 0) {
            // Main card content
            cardContent
                .background(Color.white)
                .cornerRadius(12)
                .shadow(color: .black.opacity(0.4), radius: 7, x: 0, y: 3)
            
            // Pointer arrow at bottom (FR-003, FR-013)
            CalloutPointer()
                .fill(Color.white)
                .frame(width: 20, height: 10)
                .shadow(color: .black.opacity(0.2), radius: 2, x: 0, y: 2)
        }
        .accessibilityIdentifier(model.accessibilityId)
    }
    
    private var cardContent: some View {
        VStack(alignment: .leading, spacing: 0) {
            // Photo (120px height, 8px radius)
            photoView
            
            // Content
            VStack(alignment: .leading, spacing: 4) {
                // Pet name (16px bold, #333)
                Text(model.petName)
                    .font(.system(size: 16, weight: .bold))
                    .foregroundColor(Color(hex: "#333333"))
                
                // Species • Breed (13px, #666)
                Text(model.speciesAndBreed)
                    .font(.system(size: 13))
                    .foregroundColor(Color(hex: "#666666"))
                
                // Location (13px, #666)
                Text(model.locationText)
                    .font(.system(size: 13))
                    .foregroundColor(Color(hex: "#666666"))
                
                // Date (13px, #666)
                Text(model.dateText)
                    .font(.system(size: 13))
                    .foregroundColor(Color(hex: "#666666"))
                
                // Email (optional - FR-008)
                if let email = model.emailText {
                    Text(email)
                        .font(.system(size: 13))
                        .foregroundColor(Color(hex: "#666666"))
                }
                
                // Phone (optional - FR-007)
                if let phone = model.phoneText {
                    Text(phone)
                        .font(.system(size: 13))
                        .foregroundColor(Color(hex: "#666666"))
                }
                
                // Description (optional - FR-006)
                if let description = model.descriptionText {
                    Text(description)
                        .font(.system(size: 14))
                        .foregroundColor(Color(hex: "#444444"))
                }
                
                // Status badge (FR-009)
                statusBadge
            }
            .padding(.horizontal, 21)
            .padding(.vertical, 14)
        }
    }
    
    // ... photoView, statusBadge, CalloutPointer implementations
}

/// Pointer arrow shape for callout bubble
struct CalloutPointer: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        path.move(to: CGPoint(x: rect.midX - 10, y: 0))
        path.addLine(to: CGPoint(x: rect.midX, y: rect.height))
        path.addLine(to: CGPoint(x: rect.midX + 10, y: 0))
        path.closeSubpath()
        return path
    }
}
```

**File**: `iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/AnnotationCalloutView_Model.swift`

```swift
extension AnnotationCalloutView {
    struct Model {
        let photoUrl: String?
        let petName: String
        let speciesAndBreed: String
        let locationText: String
        let dateText: String
        let emailText: String?
        let phoneText: String?
        let descriptionText: String?
        let statusText: String
        let statusColorHex: String
        let accessibilityId: String
        
        init(from pin: MapPin) {
            // ... mapping implementation
        }
    }
    
    // TODO: Extract to shared utility in /iosApp/iosApp/FoundationAdditions/DateFormatting.swift
    static func formatDate(_ dateString: String) -> String {
        let inputFormatter = DateFormatter()
        inputFormatter.dateFormat = "yyyy-MM-dd"
        guard let date = inputFormatter.date(from: dateString) else { return dateString }
        let outputFormatter = DateFormatter()
        outputFormatter.dateFormat = "MMM dd, yyyy"
        return outputFormatter.string(from: date)
    }
    
    // TODO: Extract to shared utility in /iosApp/iosApp/FoundationAdditions/CoordinateFormatting.swift
    static func formatCoordinates(_ coordinate: CLLocationCoordinate2D) -> String {
        let latDir = coordinate.latitude >= 0 ? "N" : "S"
        let lonDir = coordinate.longitude >= 0 ? "E" : "W"
        return String(format: "%.4f° %@, %.4f° %@", 
                      abs(coordinate.latitude), latDir, 
                      abs(coordinate.longitude), lonDir)
    }
}
```

### 3. Update FullscreenMapViewModel

**File**: `iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapViewModel.swift`

```swift
@MainActor
class FullscreenMapViewModel: ObservableObject {
    // Existing properties...
    
    // NEW: Selection state
    @Published private(set) var selectedPinId: String?
    
    // NEW: Get callout model for specific pin (used in ForEach)
    func calloutModel(for pin: MapPin) -> AnnotationCalloutView.Model {
        AnnotationCalloutView.Model(from: pin)
    }
    
    // NEW: Selection handlers
    func selectPin(_ pinId: String) {
        if selectedPinId == pinId {
            selectedPinId = nil  // Toggle off (FR-011)
        } else {
            selectedPinId = pinId  // Select/replace (FR-012)
        }
    }
    
    func deselectPin() {
        selectedPinId = nil  // Dismiss on map tap (FR-010)
    }
}
```

### 4. Update FullscreenMapView

**File**: `iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapView.swift`

```swift
struct FullscreenMapView: View {
    @ObservedObject var viewModel: FullscreenMapViewModel
    
    var body: some View {
        VStack(spacing: 0) {
            MapSectionHeaderView(model: viewModel.legendModel)
            
            Map(initialPosition: .region(viewModel.mapRegion)) {
                ForEach(viewModel.pins) { pin in
                    Annotation("", coordinate: pin.coordinate, anchor: .bottom) {
                        // ZStack: callout above, pin below
                        ZStack(alignment: .bottom) {
                            // Callout appears ABOVE pin when selected
                            if viewModel.selectedPinId == pin.id {
                                AnnotationCalloutView(model: viewModel.calloutModel(for: pin))
                                    .offset(y: -10) // Gap between callout arrow and pin
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
            .accessibilityIdentifier("fullscreenMap.map")
            .onTapGesture { _ in
                viewModel.deselectPin()  // FR-010: Dismiss on map tap
            }
            .task {
                await viewModel.loadPins()
            }
            .onMapCameraChange(frequency: .onEnd) { context in
                Task {
                    await viewModel.handleRegionChange(context.region)
                }
            }
        }
        .accessibilityIdentifier("fullscreenMap.container")
    }
}
```

**Key Points**:
- Callout is **inside** `Annotation` content, not in separate overlay
- `ZStack(alignment: .bottom)` places callout above pin
- `anchor: .bottom` means coordinate points to pin tip
- Callout automatically follows pin during pan/zoom
- Pointer arrow in callout naturally points to pin below

## Testing

### Run Unit Tests

```bash
# Run all iOS tests
xcodebuild test \
  -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 16' \
  -enableCodeCoverage YES
```

### Test Coverage Check

After running tests, check coverage in Xcode:
1. Product → Show Build Folder in Finder
2. Open `Logs/Test/*.xcresult`
3. Verify 80%+ coverage for:
   - `FullscreenMapViewModel`
   - `AnnotationCalloutView.Model`

### Manual Testing Checklist

- [ ] Tap pin → callout appears with all fields
- [ ] Tap same pin → callout dismisses (toggle)
- [ ] Tap different pin → callout switches
- [ ] Tap map background → callout dismisses
- [ ] Missing photo → placeholder shown
- [ ] Missing description → field omitted
- [ ] Missing phone → field omitted
- [ ] Missing email → field omitted
- [ ] MISSING status → orange badge
- [ ] FOUND status → blue badge

## Key Files

| File | Purpose |
|------|---------|
| `MapPin.swift` | Extended with callout data fields |
| `AnnotationCalloutView.swift` | Custom callout card UI |
| `AnnotationCalloutView_Model.swift` | Presentation model with formatting |
| `FullscreenMapViewModel.swift` | Selection state management |
| `FullscreenMapView.swift` | Pin tap handling + callout overlay |

## Design Reference

- **Figma**: [Node 1192:5893](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=1192-5893&m=dev)
- **Status Colors**: 
  - MISSING: `#FF9500` (orange)
  - FOUND: `#155DFC` (blue)
- **Card Styling**:
  - Corner radius: 12px
  - Shadow: `0px 3px 14px rgba(0,0,0,0.4)`
  - Photo: 216×120px, 8px radius

## Troubleshooting

### Callout not appearing
1. Check `selectedPinId` is being set in ViewModel
2. Verify pin has valid `id` matching announcement
3. Ensure callout is **inside** `Annotation` content (not in separate overlay)
4. Check `ZStack(alignment: .bottom)` wraps both callout and pin
5. Verify `selectedPinId == pin.id` condition is correct

### Callout not following pin during pan/zoom
1. Callout MUST be inside `Annotation` content, not in `.overlay()` on Map
2. Using `.overlay()` places callout at fixed screen position - this is incorrect

### Photo placeholder not showing
1. Verify `photoUrl` is empty string or nil
2. Check `AsyncImage` failure case renders placeholder
3. Verify placeholder view has correct colors

### Pointer arrow not visible
1. Check `CalloutPointer` shape is added below card in VStack
2. Verify shadow doesn't obscure the arrow
3. Check arrow fill color matches card background (white)

### Tests failing
1. Ensure `@testable import PetSpot` (not `iosApp`)
2. Check ViewModel is marked `@MainActor`
3. Use `await` for async test methods

