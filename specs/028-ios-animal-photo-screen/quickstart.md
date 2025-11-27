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
   - Create `AnimalPhotoPickerView` that exposes `PhotosPicker(selection:matching:photoLibrary:)`.
   - Use `matching: .images` and `selectionBehavior: .default` to limit do JPG/PNG/HEIC/GIF/WEBP (validated via `UTType`).
   - Call `PhotosPickerItem.loadTransferable(type: Data.self)` (or a custom `Transferable`) to obtain bytes and metadata (filename via `itemIdentifier`, file size via `Data.count`, type via `UTType`).
   - Emit `PhotoAttachmentStatus.loading` → `confirmed` transitions while downloads (including iCloud) run; no explicit Photos permission request is necessary.
2. **Error handling**:
- Map `TransferError.userCancelled` to `PhotoAttachmentStatus.empty` (triggering the toast when Continue is tapped).
- Map `.notAvailable` / `.decodingFailed` / any download failure to `PhotoAttachmentStatus.empty` – simply remove the confirmation card and behave as if no photo was ever selected.  

### 5. Update PhotoViewModel & SwiftUI View

**File**: `/iosApp/iosApp/Features/ReportMissingPet/Views/PhotoViewModel.swift`

- Inject `PhotoAttachmentCacheProtocol` + `PhotoPickerCoordinating`.
- Publish raw state (`attachmentStatus: PhotoAttachmentStatus`, `showsMandatoryToast: Bool`).
- Handle intents: `browseTapped()`, `continueTapped()`, `removeTapped()`, `bannerActionTapped()`.
- Persist metadata inside FlowState and rehydrate on init (`photoAttachmentCache.loadCurrent()`).
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
   - Add `@ios @missingPetPhoto` scenario under `/e2e-tests/src/test/resources/features/mobile/missing_pet_photo.feature`.
   - Reuse Screen Objects to click Browse/Continue/Remove using identifiers defined above.
   - Execute:  
     ```bash
     mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@ios and @missingPetPhoto"
     ```

Successful completion of the steps above unlocks Phase 2 task breakdown.

