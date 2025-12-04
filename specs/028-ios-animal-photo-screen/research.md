# Research: iOS Animal Photo Screen

**Feature Branch**: `028-ios-animal-photo-screen`  
**Date**: 2025-11-27  
**Purpose**: Resolve open questions from Technical Context and capture best practices for key dependencies/integrations.

## Research Tasks

1. Research storage approach for the photo draft session so the confirmation card survives navigation/backgrounding (Technical Context unknown).  
2. Find best practices for integrating SwiftUI `PhotosPicker` inside an MVVM-C SwiftUI screen.  
3. Confirm whether `PhotosPicker` requires Photos Library permission and adjust UX accordingly.  
4. Identify a resilient pattern for iCloud-backed assets (progress + retry) when loading via PhotoKit.  
5. Document Appium+Cucumber considerations for validating the mandatory photo rule on iOS.

---

### 1. Draft Attachment Persistence
- **Decision**: Introduce a `PhotoAttachmentCacheProtocol` that writes the picked image data to `FileManager`’s `.cachesDirectory/PetSpot/ReportMissingPet/` using a UUID filename and persists lightweight metadata (`assetIdentifier`, `fileName`, `fileSize`, `localURL`, `savedAt`) inside `ReportMissingPetFlowState`. The cache exposes `save(data:metadata:)`, `load()` and `clear()` so FlowState can hydrate on launch and purge on submit/cancel.
- **Rationale**: `FileManager` cache avoids CoreData complexity and matches the short-lived lifespan of draft sessions. Writing to caches keeps data outside backups and meets privacy expectations; storing metadata w FlowState ensures SwiftUI re-renders without reloading binary data.
- **Alternatives considered**:
  - CoreData/Realm store: unnecessary schema + migration overhead for a single attachment.
  - `UserDefaults` blob: not suited for multi‑MB images and would hurt launch time.
  - Keeping `UIImage` only in memory: fails FR-002/FR-003 because the card would disappear after navigation/backgrounding.

- **Decision**: Use SwiftUI `PhotosPicker` (available iOS 16+) as the only picker control so we can leverage the built-in sheet + Transferable API. Devices below iOS 16 remain unsupported for this feature. The wrapper converts `PhotosPickerItem` into `PhotoAttachmentStatus` updates and stores metadata in FlowState.
- **Rationale**: `PhotosPicker` feels native inside SwiftUI, supports async `loadTransferable`, and automatically scopes to allowed media types without dealing with UIKit delegates. Dropping the fallback simplifies the code path and clearly communicates the minimum OS version.
- **Alternatives considered**:
  - PHPicker fallback: requires a `UIViewControllerRepresentable` wrapper and doubles the test surface; dropped due to limited scope.
  - `UIImagePickerController`: deprecated look/feel, lacks iCloud progress APIs, and demands manual permission handling.
  - Custom document browser: significantly more work without UX parity.

### 3. Permission Requirements for PhotosPicker
- **Decision**: Do **not** request `PHPhotoLibrary` authorization directly. According to Apple’s PhotosUI documentation (WWDC20 Session 10152, “Meet the new Photos picker”) and the `PhotosPicker` API reference, the picker grants temporary scoped access to user-selected items without surfacing the system permission alert. Therefore, the UX should rely on picker callbacks (`TransferError.userCancelled`, `.notAvailable`) instead of inline permission banners.
- **Rationale**: Removing bespoke permission flows keeps the screen simpler and matches Apple guidance (“PHPickerViewController does not require Photo Library permission”). We still need to handle cancellation and iCloud download failures, but not manual Settings deep-links.
- **Alternatives considered**:
  - Forcing `PHPhotoLibrary.requestAuthorization`: redundant, violates Apple’s privacy guidance for PHPicker-based experiences.
  - Custom alerts instructing users to change permissions: unnecessary since PhotosPicker mediates access.

### 4. iCloud Asset Download & Retry
- **Decision**: Use `PHImageManager.default().requestImageDataAndOrientation(for:options:)` with `PHImageRequestOptions` configured for `isNetworkAccessAllowed = true`, `deliveryMode = .highQualityFormat`, and `progressHandler` to emit determinate progress (0→1). The ViewModel exposes `PhotoAttachmentState.Status` (`empty`, `loading(progress)`, `confirmed`, `error(message)`) so the UI can show loading bars or retry banners without losing the previous selection.
- **Rationale**: `PHImageManager` handles iCloud downloads transparently and exposes progress/error callbacks, satisfying FR-007 and iCloud edge cases. Keeping status inside the attachment state lets us replay the confirmation card once download completes.
- **Alternatives considered**:
  - `PHAssetResourceManager` manual streaming: more code, no added value for single-asset download.
  - Blocking UI until download completes: violates UX requirement for non-blocking indicators.
  - Copying only the UIImage: loses filename/size metadata mandated by Figma confirmation card.

### 5. Appium+Cucumber Test Strategy
- **Decision**: Add `missing_pet_photo.feature` under `/e2e-tests/src/test/resources/features/mobile/` with the `@ios @missingPetPhoto` tag. Steps reuse Screen Objects with unified identifiers (`animalPhoto.browse`, `animalPhoto.remove`, `animalPhoto.continue`), stub the picker via iOS Simulator media injection, and validate toast/mandatory gating. Use hooks to pre-populate the simulator photo library once to keep runs deterministic.
- **Rationale**: Aligns with constitution v2.3.0 (Appium + Java + Cucumber). Injecting photos via simulator command avoids interacting with real picker UI while still exercising the screen logic end-to-end, covering FR-001/FR-002/FR-009.
- **Alternatives considered**:
  - Reusing legacy Playwright mobile scripts: obsolete per constitution migration.
  - Skipping E2E because backend unchanged: spec explicitly mandates UI behavior; gating must be verified.
  - Mocking picker entirely in Appium: would not validate accessibility identifiers or Continue gating.

---

**Outcome**: Storage approach is resolved (FileManager cache + metadata) and all dependency/integration best practices are defined, clearing Phase 0 blockers.

