# Research: iOS Prepare Found Pet Flow as Missing Pet Copy

**Feature**: KAN-34-ios-prepare-found-flow-as-missing-copy  
**Date**: 2026-01-09  
**Status**: Complete (no unknowns)

## Summary

This feature is scaffolding-only with no technical unknowns. All decisions are predetermined by the existing codebase structure and the spec requirement to create an exact copy with renamed types.

## Research Tasks

### Task 1: Entry Point Wiring Analysis

**Question**: How is the Report Found Animal entry point prepared?

**Finding**: 
- Entry button is **already commented out** in `AnnouncementListView.swift` (lines 55-64)
- Button uses existing `FloatingActionButton` component with `.secondary` style
- AccessibilityIdentifier `animalList.reportFoundButton` already defined
- ViewModel closure `onReportFound` already wired
- Coordinator method `showReportFound()` exists as stub

**Decision**: Uncomment existing button code and implement `showReportFound()` coordinator method.

**Rationale**: Minimal changes - infrastructure already exists.

**Alternatives considered**: None - existing wiring is optimal.

---

### Task 2: Directory Structure Analysis

**Question**: What is the exact structure to copy?

**Finding**:
```
ReportMissingPet/
├── Coordinators/
│   └── MissingPetReportCoordinator.swift
├── Models/
│   └── MissingPetReportFlowState.swift
└── Views/
    ├── AnimalDescription/ (2 files)
    ├── ChipNumber/ (2 files)
    ├── ContactDetails/ (2 files)
    ├── Photo/ (2 files)
    └── Summary/ (3 files)
```

**Decision**: Mirror exact structure under `ReportFoundPet/` with `FoundPet*` prefix.

**Rationale**: Consistent naming pattern, clear ownership, no namespace collisions.

**Alternatives considered**: 
- Sharing code between flows via protocols → Rejected (spec explicitly requires copy for future divergence)

---

### Task 3: Test Structure Analysis

**Question**: What tests need to be copied?

**Finding**:
```
ReportMissingPet/
├── MissingPetChipNumberViewModelTests.swift
├── MissingPetPhotoViewModelTests.swift
├── MissingPetAnimalDescriptionViewModelTests.swift
├── MissingPetContactDetailsViewModelTests.swift
├── MissingPetContactDetailsViewModelValidationTests.swift
├── MissingPetContactDetailsViewModelErrorHandlingTests.swift
├── MissingPetContactDetailsViewModelRewardTests.swift
├── MissingPetSummaryViewModelTests.swift
└── Models/
    └── MissingPetReportFlowStateTests.swift
```

**Decision**: Copy all 9 test files with `FoundPet*` prefix.

**Rationale**: Maintains 80% coverage requirement; tests validate identical behavior.

**Alternatives considered**: None - test copy is mandatory per constitution.

---

### Task 4: Localization Reuse

**Question**: What localization keys are needed?

**Finding**:
- `L10n.AnnouncementList.Button.reportFound` exists ("Report Found Animal")
- All `L10n.ReportMissingPet.*` keys exist and will be reused in Found flow

**Decision**: Reuse existing Missing localization keys in Found flow (content unchanged per spec).

**Rationale**: Spec explicitly states "cały kontent widoków zostaje bez zmian" - no content changes.

**Alternatives considered**:
- Create `L10n.ReportFoundPet.*` duplicate keys → Rejected (unnecessary duplication, future work)

---

### Task 5: AccessibilityIdentifier Renaming

**Question**: What identifiers need renaming?

**Finding** (from `MissingPetReportCoordinator.swift`):
- `reportMissingPet.progressIndicator` → `reportFoundPet.progressIndicator`
- `reportMissingPet.backButton` → `reportFoundPet.backButton`
- `missingPet.microchip.backButton` → `foundPet.microchip.backButton`

Additional identifiers exist in views (discovered during copy).

**Decision**: Replace all `reportMissingPet.` and `missingPet.` prefixes with `reportFoundPet.` and `foundPet.` equivalents.

**Rationale**: FR-013 requires unique identifiers to avoid E2E test collisions.

**Alternatives considered**: None - requirement is explicit.

---

### Task 6: Common Components Reuse

**Question**: Can Common/ components be shared?

**Finding**:
```
Common/
├── Components/Form/     # CoordinateInputView, DateInputView, etc.
├── Components/Photo/    # AnimalPhotoBrowseView, etc.
├── Components/Toast/    # ToastView
├── Helpers/             # PhotoSelectionProcessor, etc.
├── Models/              # FormField, ValidationError, etc.
└── Services/            # PhotoAttachmentCache, ToastScheduler
```

**Decision**: Reuse all Common/ components without duplication.

**Rationale**: These are generic UI components and services with no Missing/Found-specific logic.

**Alternatives considered**: None - sharing is optimal.

---

## Dependencies Verified

| Dependency | Exists | Notes |
|------------|--------|-------|
| `LocationServiceProtocol` | ✅ | Existing protocol |
| `PhotoAttachmentCacheProtocol` | ✅ | Existing protocol |
| `AnnouncementSubmissionServiceProtocol` | ✅ | Existing protocol |
| `CoordinatorInterface` | ✅ | Base coordinator protocol |
| `NavigationBackHiding` | ✅ | SwiftUI wrapper |
| `FloatingActionButton` | ✅ | UI component |
| `L10n.AnnouncementList.Button.reportFound` | ✅ | Localization key |
| `L10n.ReportMissingPet.*` | ✅ | All keys available |

## Conclusion

No technical unknowns remain. Implementation is straightforward:
1. Copy 13 source files to `ReportFoundPet/`
2. Rename all `MissingPet*` → `FoundPet*` in copied files
3. Rename all `missingPet.*`/`reportMissingPet.*` identifiers
4. Copy 9 test files with same renames
5. Uncomment entry button in `AnnouncementListView.swift`
6. Implement `showReportFound()` in `AnnouncementListCoordinator.swift`
7. Add new files to Xcode project
8. Verify compilation and tests pass

