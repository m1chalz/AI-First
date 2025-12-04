# Quickstart: iOS Animal Photo Screen

**Feature Branch**: `028-ios-animal-photo-screen`  
**Date**: 2025-11-27  
**Estimated Setup Time**: 30 minutes

## Prerequisites

- Xcode 15+ with iOS 15+ simulator
- Existing `ReportMissingPetCoordinator` + `ReportMissingPetFlowState`
- SwiftGen configured for localization (`L10n`)
- Appium+Cucumber Java stack (per constitution v2.3.0)

## Setup Steps

### 1. Add Localization Keys (Requirement FR-001/FR-006/FR-009)

**Files**:
- `/iosApp/iosApp/Resources/en.lproj/Localizable.strings`
- `/iosApp/iosApp/Resources/pl.lproj/Localizable.strings`

Add (translate for `pl`):

```strings
"animalPhoto.title" = "Add a photo";
"animalPhoto.helper.required" = "A recent photo is mandatory before you can continue.";
"animalPhoto.helper.pickerCancelled" = "Select a photo to continue – you can browse again anytime.";
"animalPhoto.helper.loading" = "Downloading from iCloud…";
"animalPhoto.alert.missing.title" = "Photo required";
"animalPhoto.alert.missing.message" = "Attach a JPG, PNG, HEIC, GIF or WEBP file before continuing.";
"animalPhoto.button.browse" = "Browse";
"animalPhoto.button.remove" = "Remove";
"animalPhoto.button.continue" = "Continue";
"animalPhoto.banner.settings" = "Open Settings";
"animalPhoto.banner.retry" = "Try again";
```

Regenerate SwiftGen:

```bash
cd iosApp
swiftgen
```

### 2. Extend Data Models & Flow State

**File**: `/iosApp/iosApp/Features/ReportMissingPet/Models/ReportMissingPetFlowState.swift`

- Add `@Published var photoAttachment: PhotoAttachmentMetadata?`
- Add `@Published var photoStatus: PhotoAttachmentStatus = .empty`
- Ensure `clear()` wipes the cached file via the cache service.

**File**: `/iosApp/iosApp/Features/ReportMissingPet/Models/PhotoAttachmentState.swift` (new)

Implement the structs/enums from `data-model.md` (metadata + status + helper/banners). Include validation helpers for supported UTIs and computed `formattedSize`.

### 3. Implement PhotoAttachmentCache

**File**: `/iosApp/iosApp/Features/ReportMissingPet/Services/PhotoAttachmentCache.swift`

Key responsibilities:
- Create namespace directory under `Library/Caches/PetSpot/ReportMissingPet/<uuid>/`.
- Provide async methods `save(data:metadata:)`, `loadCurrent()`, `fileExists(_:)`, `clearCurrent()`.
- Delete cached file during `clear()` and before writing a replacement.
- Surface typed errors (e.g., lowDiskSpace, unsupportedFormat, writeFailed) mapped to `PhotoAttachmentStatus.error`.

**DI**: Register service in `/iosApp/iosApp/DI/ServiceContainer.swift` so `PhotoViewModel` receives it via initializer.

### 4. Integrate SwiftUI PhotosPicker (iOS 16+ only)

1. **Control setup**:
   - `AnimalPhotoEmptyStateView` hosts `PhotosPicker(selection:matching:)` directly.
   - Restrict formats with `.any(of: .jpeg, .png, .heic, .gif, .webP)` (validated again in `PhotoAttachmentMetadata.isSupported`).
   - Convert `PhotosPickerItem` into the reusable `PhotoSelection` via `AnimalPhotoTransferable`.
   - Emit `.loading` → `.confirmed` via `PhotoViewModel` to keep UI responsive while iCloud downloads complete.
2. **Error handling & debug**:
   - Map `PhotosPickerItem.LoadTransferableError.userCancelled` to `helperMessage = L10n.AnimalPhoto.Helper.pickerCancelled` so QA sees the dedicated copy.
   - Map any transfer failure (`.notAvailable`, decoding issues, low disk) to `handleSelectionFailure()` which clears the confirmation card and replays the toast on Continue.
   - E2E runs enable invisible debug controls through the `UITEST_SHOW_PHOTO_DEBUG=1` environment variable (set in `AppiumDriverManager`). Tapping `animalPhoto.debug.cancel` or `animalPhoto.debug.fail` simulates the two main edge cases deterministically.

### 5. Update PhotoViewModel & SwiftUI View

**File**: `/iosApp/iosApp/Features/ReportMissingPet/Views/PhotoViewModel.swift`

- Inject `PhotoAttachmentCacheProtocol` + `ToastSchedulerProtocol`.
- Publish `attachmentStatus`, `helperMessage`, and `showsMandatoryToast`.
- Handle intents: selection success/failure, picker cancellation, remove, Continue, and Back.
- Persist metadata inside FlowState and rehydrate on init (`photoAttachmentCache.loadCurrent()` + file existence check).
- Communicate with coordinator via `onNext`/`onBack`, ensuring Continue only fires when `attachmentStatus == .confirmed`.

**File**: `/iosApp/iosApp/Features/ReportMissingPet/Views/PhotoView.swift`

- Replace placeholder UI with layout from Figma nodes `297:7991`/`297:8041` (empty + confirmation).
- Keep state host + stateless content split; add previews using `AnimalPhotoUiStateProvider`.
- Apply accessibility identifiers per constitution (`animalPhoto.browse`, `animalPhoto.continue`, `animalPhoto.remove`, `animalPhoto.toast`) with no `.tap` suffix.
- Show inline banner/toast logic directly from ViewModel’s published fields (spinner for `.loading`, confirmation card for `.confirmed`).

### 6. Testing & Verification

1. **Unit tests** (`/iosApp/iosAppTests/Features/ReportMissingPet/Views/PhotoViewModelTests.swift`):
   - Cover Given/When/Then scenarios for:
     - Browse → PhotosPicker cancellation → toast triggered on Continue.
     - Browse → loading → confirmed (Continue navigates).
     - Remove → state resets, Continue stays enabled, tapping it should emit the 3-second toast until another photo is attached.
     - Continue tapped without attachment → toast flag set.
     - Cache persistence after simulated background (load metadata on init).
2. **Service tests** (`PhotoAttachmentCacheTests.swift`):
   - Verify save/load/clear semantics and error mapping.
3. **Localization snapshot**:
   - Run SwiftUI previews or `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15'`.
4. **E2E**:
   - Scenario lives in `/e2e-tests/java/src/test/resources/features/mobile/missing_pet_photo.feature`.
   - `IosSimulatorMediaManager` (hooked from `Hooks.java`) seeds the simulator photo library via `xcrun simctl addmedia booted ...` before any `@missingPetPhoto` scenario runs.
   - `ReportMissingPetScreen` exposes helpers for Browse/Continue/Remove plus the debug identifiers (`animalPhoto.debug.cancel` / `animalPhoto.debug.fail`) so Appium can simulate cancellations and transfer failures without driving the native picker.
   - Execute:  
     ```bash
     mvn -f e2e-tests/java/pom.xml test -Dcucumber.filter.tags="@ios and @missingPetPhoto"
     ```

Successful completion of the steps above unlocks Phase 2 task breakdown.

