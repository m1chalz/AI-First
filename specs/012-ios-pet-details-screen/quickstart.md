# Quickstart Guide: Pet Details Screen (iOS)

**Feature**: 012-ios-pet-details-screen  
**Date**: November 24, 2025  
**Platform**: iOS  
**Branch**: `012-ios-pet-details-screen`

## Overview

This guide helps developers set up, build, test, and debug the Pet Details Screen feature on iOS. Follow these steps to get started quickly.

## Prerequisites

- **Xcode**: 15.0 or later
- **iOS Simulator**: iPhone 15 (iOS 17.0+) or physical device running iOS 15+
- **Git**: Feature branch `012-ios-pet-details-screen` checked out
- **Dependencies**: All iOS dependencies installed (no additional packages required for this feature)

---

## Quick Start (5 minutes)

### 1. Open Project in Xcode

```bash
cd /Users/msz/dev/ai-first/AI-First
open iosApp/iosApp.xcodeproj
```

**Note**: If you're using CocoaPods or SPM, ensure dependencies are resolved first.

### 2. Build and Run

1. Select target: `iosApp`
2. Select destination: `iPhone 15` simulator (or your preferred device)
3. Press `Cmd+R` to build and run

**Expected behavior**:
- App launches to pet list screen (existing functionality)
- Tap any pet item → navigates to pet details screen (new feature)
- See pet photo, status badge, identification info, location, contact details

### 3. Verify UI Components

On the Pet Details Screen, verify:
- ✅ Pet photo with status badge (upper right corner)
- ✅ Reward badge (lower left corner, if pet has reward)
- ✅ Date of disappearance formatted as "MMM DD, YYYY"
- ✅ Microchip number formatted as "000-000-000-000"
- ✅ Species and breed in two-column layout
- ✅ Sex displayed with symbol (♂ for male, ♀ for female)
- ✅ Location with radius (e.g., "Warsaw • ±15 km")
- ✅ Contact information (phone, email) as tappable links
- ✅ "Show on the map" button (tapping prints to console)
- ✅ "Remove Report" button (tapping prints to console)
- ✅ Back button in navigation bar navigates to list

---

## Project Structure

**Key files added/modified**:

```text
iosApp/iosApp/
├── Domain/
│   ├── Models/
│   │   └── PetDetails.swift              # NEW: Domain model
│   └── Repositories/
│       └── PetRepository.swift           # MODIFIED: Add getPetDetails(id:) method
│
├── Data/
│   └── Repositories/
│       └── PetRepositoryImpl.swift       # MODIFIED: Add mock getPetDetails implementation
│
├── Coordinators/
│   ├── PetListCoordinator.swift          # MODIFIED: Add navigation to details
│   └── PetDetailsCoordinator.swift       # NEW: Coordinator for details screen
│
├── ViewModels/
│   └── PetDetailsViewModel.swift         # NEW: ViewModel with @Published state
│
├── Views/
│   ├── PetDetailsView.swift              # NEW: Main details screen
│   └── Components/
│       ├── PetPhotoWithBadges.swift      # NEW: Reusable photo + badges component
│       └── LabelValueRow.swift           # NEW: Reusable label-value row component
│
└── DI/
    └── ServiceContainer.swift            # MODIFIED: Extend with pet details dependencies

iosApp/iosAppTests/
├── ViewModels/
│   └── PetDetailsViewModelTests.swift    # NEW: Unit tests for ViewModel
└── Components/
    ├── PetPhotoWithBadgesTests.swift     # NEW: Component tests
    └── LabelValueRowTests.swift          # NEW: Component tests
```

---

## Running Tests

### Unit Tests (XCTest)

**Run all tests**:
```bash
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15'
```

**Run with coverage**:
```bash
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES
```

**View coverage report**:
1. In Xcode: `View > Navigators > Reports`
2. Select latest test run
3. Click `Coverage` tab
4. Verify `PetDetailsViewModel` has ≥80% coverage

**Expected test cases**:
- `testLoadPetDetails_whenRepositorySucceeds_shouldUpdateStateToLoaded()`
- `testLoadPetDetails_whenRepositoryFails_shouldUpdateStateToError()`
- `testRetry_whenInErrorState_shouldTransitionToLoading()`
- `testPetPhotoWithBadgesModel_whenStatusIsActive_shouldMapToMissing()`
- `testLabelValueRow_whenOnTapProvided_shouldBeTappable()`

### End-to-End Tests (Appium)

**Prerequisites**:
- Appium server running
- iOS simulator or device connected
- Test app build installed

**Run E2E tests** (from repo root):
```bash
npm run test:mobile:ios
```

**Test scenarios**:
- Navigate from pet list to details screen
- Verify all UI elements present and correctly formatted
- Tap phone number → opens dialer
- Tap email → opens mail composer
- Tap "Show on the map" button → logs to console
- Tap "Remove Report" button → logs to console
- Tap back button → returns to list

---

## Development Workflow

### Adding New Fields

If backend adds new fields (e.g., `weight`, `color`):

1. **Update domain model** (`PetDetails.swift`):
   ```swift
   let weight: String?
   let color: String?
   ```

2. **Update repository** (`PetRepositoryImpl.swift`):
   ```swift
   // Add to mock data
   weight: "25 kg",
   color: "Golden"
   ```

3. **Update UI** (`PetDetailsView.swift`):
   ```swift
   LabelValueRow(model: .init(label: "Weight", value: petDetails.weight ?? "—"))
   LabelValueRow(model: .init(label: "Color", value: petDetails.color ?? "—"))
   ```

4. **Update tests**: Add test cases for new fields

### Switching from Mock to Real API

When backend implements `GET /api/v1/announcements/:id`:

1. **Update repository implementation** (`PetRepositoryImpl.swift`):
   ```swift
   func getPetDetails(id: String) async throws -> PetDetails {
       // Replace mock data with HTTP call
       let url = URL(string: "\(baseUrl)/api/v1/announcements/\(id)")!
       let (data, response) = try await httpClient.data(from: url)
       
       guard let httpResponse = response as? HTTPURLResponse,
             httpResponse.statusCode == 200 else {
           throw RepositoryError.serverError
       }
       
       return try JSONDecoder().decode(PetDetails.self, from: data)
   }
   ```

2. **Update error handling**: Add specific error cases (404, 500, network errors)

3. **Update unit tests**: Mock HTTP client instead of repository

4. **Update E2E tests**: Use real backend with seeded test data

### Debugging Tips

**Problem**: Pet details screen shows loading spinner indefinitely

**Solution**:
- Check repository method is being called (add breakpoint in `PetRepositoryImpl.getPetDetails`)
- Verify async task is started in ViewModel (check `loadPetDetails()` method)
- Ensure `@MainActor` is applied to ViewModel class

**Problem**: Status badge shows "ACTIVE" instead of "MISSING"

**Solution**:
- Verify `PetPhotoWithBadgesModel` convenience init maps ACTIVE → MISSING
- Check mapping logic: `self.status = petDetails.status == "ACTIVE" ? "MISSING" : petDetails.status`

**Problem**: Phone/email tap doesn't open dialer/mail app

**Solution**:
- Verify `onTap` closure is provided in `LabelValueRowModel`
- Check closure implementation calls `UIApplication.shared.open(URL(string: "tel:..."))`
- Ensure simulator/device has phone/mail app available

**Problem**: Unit test fails with "async test timed out"

**Solution**:
- Use `await` in test methods: `func testLoadPets() async { ... }`
- Remove `XCTestExpectation` (not needed for async tests)
- Ensure fake repository completes immediately (no artificial delays)

---

## Useful Commands

**Build iOS app**:
```bash
xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15'
```

**Run iOS tests with coverage**:
```bash
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES
```

**Check Swift version**:
```bash
swift --version
```

**List available simulators**:
```bash
xcrun simctl list devices
```

**Reset simulator** (if UI behaves unexpectedly):
```bash
xcrun simctl erase all
```

---

## Accessibility Identifiers

All interactive elements have accessibility identifiers for E2E testing:

| Element | Identifier | Notes |
|---------|-----------|-------|
| Phone number link | `petDetails.phone.tap` | Opens dialer |
| Email link | `petDetails.email.tap` | Opens mail composer |
| Show on map button | `petDetails.showMap.button` | Logs to console (placeholder) |
| Remove report button | `petDetails.removeReport.button` | Logs to console (placeholder) |
| Retry button (error state) | `petDetails.retry.button` | Retries loading |
| Back button | System default | Standard navigation bar back button |

**Usage in tests**:
```swift
let phoneButton = app.buttons["petDetails.phone.tap"]
XCTAssertTrue(phoneButton.exists)
phoneButton.tap()
```

---

## Related Documentation

- **Feature Specification**: [spec.md](./spec.md) - Complete feature requirements
- **Data Model**: [data-model.md](./data-model.md) - Domain model definitions
- **API Contracts**: [contracts/README.md](./contracts/README.md) - Mock API structure
- **Research**: [research.md](./research.md) - Technology decisions and patterns
- **Implementation Plan**: [plan.md](./plan.md) - Architecture and compliance checks

---

## Troubleshooting

### Xcode Build Errors

**Error**: `Cannot find 'PetDetails' in scope`

**Solution**: Ensure `PetDetails.swift` is added to Xcode project target (not just filesystem)

**Error**: `Missing required module 'iosApp'`

**Solution**: Clean build folder (`Cmd+Shift+K`) and rebuild (`Cmd+B`)

### Simulator Issues

**Problem**: Simulator crashes on launch

**Solution**:
```bash
# Reset simulator
xcrun simctl erase all

# Restart Xcode
killall Xcode Simulator
open -a Xcode
```

### Test Failures

**Problem**: E2E tests can't find accessibility identifiers

**Solution**:
- Verify identifiers are set in SwiftUI views: `.accessibilityIdentifier("petDetails.phone.tap")`
- Ensure Appium inspector can see identifiers (use Appium Desktop)
- Check iOS version compatibility (accessibility identifiers work on iOS 13+)

---

## Getting Help

- **Architecture questions**: See [constitution.md](/.specify/memory/constitution.md)
- **Feature requirements**: See [spec.md](./spec.md)
- **iOS best practices**: See [research.md](./research.md)
- **Testing issues**: Check [iosAppTests/](../../iosApp/iosAppTests/) for examples

---

**Last Updated**: November 24, 2025  
**Maintainer**: iOS Team

