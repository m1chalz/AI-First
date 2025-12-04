# Data Model: iOS Animal Photo Screen

**Feature Branch**: `028-ios-animal-photo-screen`  
**Date**: 2025-11-27  
**Platform**: iOS (Swift / SwiftUI + MVVM-C)

## Overview

The Animal Photo step extends the existing `ReportMissingPetFlowState` with disk-backed attachment metadata, PhotosPicker state, and UI data required to enforce the mandatory photo rule. All models are Swift-native value types (structs/enums) except for `ReportMissingPetFlowState`, which remains an `ObservableObject` owned by the coordinator.

## Domain Entities

### 1. PhotoAttachmentMetadata (Struct)

**Purpose**: Persist normalized information about the selected image so the confirmation card can render without rehydrating binary data.

**Fields**:
| Field | Type | Description |
| --- | --- | --- |
| `id` | `UUID` | Stable identifier for cache file |
| `fileName` | `String` | Original filename surfaced in confirmation card |
| `fileSizeBytes` | `Int` | Raw byte size (converted to KB/MB for UI) |
| `uti` | `String` | Uniform Type Identifier (e.g., `public.heic`, `public.jpeg`) for validation |
| `pixelWidth` / `pixelHeight` | `Int` | Dimensions for optional display copy/logging |
| `assetIdentifier` | `String?` | `PHPickerResult` asset identifier (retained to detect external deletion) |
| `cachedURL` | `URL` | Absolute path in `Library/Caches/PetSpot/ReportMissingPet/` where blob is stored |
| `savedAt` | `Date` | Timestamp used for expiration heuristics (e.g., >1h background resume requirement) |

**Validation Rules**:
- `uti` must resolve to one of the supported formats: JPG, PNG, HEIC, GIF, WEBP (per FR-002). Unsupported UTIs are rejected before we persist metadata.
- `fileSizeBytes` must be greater than zero. (The backend for this step does not impose an upper limit; if one ever appears, server-side validation will handle it.)
- `cachedURL` must point to the caches directory; no documents/shared containers to avoid iCloud backups.

**Relationships**:
- Stored inside `ReportMissingPetFlowState.photoAttachment`.
- Referenced by `PhotoAttachmentStatus.confirmed(metadata)` to drive SwiftUI confirmation card.

### 2. PhotoAttachmentStatus (Enum)

**Purpose**: Single source of truth for UI/logic around the attachment lifecycle.

```swift
enum PhotoAttachmentStatus: Equatable {
    case empty
    case loading
    case confirmed(metadata: PhotoAttachmentMetadata)
}
```

**Rules & Transitions**:
- Initial state: `.empty` – Continue button remains tappable, but `PhotoViewModel` treats taps as non-navigable so the toast defined in FR-004 is displayed instead of advancing.
- `empty → loading`: user taps Browse or PhotosPicker is downloading from iCloud.
- `loading → confirmed`: `PhotoAttachmentCache` reports success.
- `loading → empty`: PhotoKit download failure, unsupported format, storage error, or low disk resets state to empty without additional inline messaging (behaves as if no photo was ever selected).
- `confirmed → empty`: user taps Remove or FlowState is reset (e.g., cancel/submit).
- `empty (after failure)` → loading: user retries selection; state machine stays identical to the initial path.

### 3. ReportMissingPetFlowState (ObservableObject)

**New/Updated Fields**:
| Field | Type | Notes |
| --- | --- | --- |
| `@Published var photoAttachment: PhotoAttachmentMetadata?` | Optional metadata persisted across steps/backgrounding. |
| `@Published var photoStatus: PhotoAttachmentStatus` | Mirrors ViewModel state for previews/tests. Defaults to `.empty`. |

**Persistence Rules**:
- `photoAttachment` is set when cache save succeeds and cleared when user removes attachment or `clear()` is invoked (exit flow).
- `clear()` deletes cached file via `PhotoAttachmentCache` before resetting properties.

**Relationship Diagram**:
```
ReportMissingPetFlowState
├─ photoAttachment: PhotoAttachmentMetadata?
└─ photoStatus: PhotoAttachmentStatus
      ↑
PhotoViewModel maps Picker + Permission events → status updates
```


- Concrete implementation manages a single cached attachment for the active draft and handles cleanup logic.
- Throws typed errors (enum) that map to `PhotoAttachmentStatus.error`.

## Validation & Business Rules Summary

- A valid attachment exists when `photoAttachment` is non-nil **and** the cache file still exists on disk. Continue navigation logic uses this invariant to decide whether to advance or surface the “Photo is mandatory” toast.
- Removing or replacing a photo must delete the previous cache file before writing the new one to avoid stale storage growth.
- When the app resumes after >30 minutes (edge case), FlowState reloads metadata from cache to satisfy SC-002 (95% persistence).
- VoiceOver identifiers follow `{screen}.{element}` (e.g., `animalPhoto.browse`) to satisfy FR-009 and Constitution Principle VI.

## State Machine (Textual)

```
Attachment Status:
empty --(Browse tapped & asset chosen)--> loading
loading --(success)--> confirmed(metadata)
loading --(failure)--> empty
confirmed --(Remove tapped)--> empty
confirmed --(Flow submitted/cancelled)--> empty
empty --(Retry browse)--> loading
```

These state transitions must be unit-tested inside `PhotoViewModelTests` to maintain deterministic UI behavior when the picker, toast logic, or cache fail.

