# Research: iOS Project Structure Refactoring

**Feature**: KAN-34-ios-prepare-project-structure
**Date**: 2026-01-08

## Overview

This document resolves all technical clarifications needed before implementing the iOS project restructuring for Report Missing & Found Pet features.

## Research Questions & Decisions

### RQ-1: File Inventory - Which files belong where?

**Decision**: Create explicit mapping based on FR-005 and FR-006 rules:
- **ReportMissingPet/**: ONLY coordinator + full-screen views + their ViewModels
- **Common/**: ALL helpers, services, models, reusable components, form components

**Rationale**: This separation ensures future Report Found Pet feature can reuse Common/ components without duplication.

**File Inventory**:

| # | Current Path | Destination | Action |
|---|-------------|-------------|--------|
| **Coordinators** |||
| 1 | `Coordinators/ReportMissingPetCoordinator.swift` | `ReportMissingPet/Coordinators/MissingPetReportCoordinator.swift` | MOVE + RENAME |
| **Helpers** |||
| 2 | `Helpers/AnimalPhotoTransferable.swift` | `Common/Helpers/AnimalPhotoTransferable.swift` | MOVE |
| 3 | `Helpers/MicrochipNumberFormatter.swift` | `Common/Helpers/MicrochipNumberFormatter.swift` | MOVE |
| 4 | `Helpers/PhotoSelectionProcessor.swift` | `Common/Helpers/PhotoSelectionProcessor.swift` | MOVE |
| **Models** |||
| 5 | `Models/PhotoAttachmentMetadata+MimeType.swift` | `Common/Models/PhotoAttachmentMetadata+MimeType.swift` | MOVE |
| 6 | `Models/PhotoAttachmentState.swift` | `Common/Models/PhotoAttachmentState.swift` | MOVE |
| 7 | `Models/PhotoSelection.swift` | `Common/Models/PhotoSelection.swift` | MOVE |
| 8 | `Models/ReportMissingPetFlowState.swift` | `ReportMissingPet/Models/ReportMissingPetFlowState.swift` | STAY (flow-specific) |
| **Services** |||
| 9 | `Services/PhotoAttachmentCache.swift` | `Common/Services/PhotoAttachmentCache.swift` | MOVE |
| 10 | `Services/ToastScheduler.swift` | `Common/Services/ToastScheduler.swift` | MOVE |
| **Views - AnimalDescription (Full-Screen)** |||
| 11 | `Views/AnimalDescription/AnimalDescriptionView.swift` | `ReportMissingPet/Views/MissingPetAnimalDescriptionView.swift` | MOVE + RENAME |
| 12 | `Views/AnimalDescription/AnimalDescriptionViewModel.swift` | `ReportMissingPet/Views/MissingPetAnimalDescriptionViewModel.swift` | MOVE + RENAME |
| **Views - AnimalDescription/Components (Form Components → Common)** |||
| 13 | `Views/AnimalDescription/Components/CoordinateInputView.swift` | `Common/Components/Form/CoordinateInputView.swift` | MOVE |
| 14 | `Views/AnimalDescription/Components/CoordinateInputView_Model.swift` | `Common/Components/Form/CoordinateInputView_Model.swift` | MOVE |
| 15 | `Views/AnimalDescription/Components/DateInputView.swift` | `Common/Components/Form/DateInputView.swift` | MOVE |
| 16 | `Views/AnimalDescription/Components/DateInputView_Model.swift` | `Common/Components/Form/DateInputView_Model.swift` | MOVE |
| 17 | `Views/AnimalDescription/Components/DropdownView.swift` | `Common/Components/Form/DropdownView.swift` | MOVE |
| 18 | `Views/AnimalDescription/Components/DropdownView_Model.swift` | `Common/Components/Form/DropdownView_Model.swift` | MOVE |
| 19 | `Views/AnimalDescription/Components/SelectorView.swift` | `Common/Components/Form/SelectorView.swift` | MOVE |
| 20 | `Views/AnimalDescription/Components/SelectorView_Model.swift` | `Common/Components/Form/SelectorView_Model.swift` | MOVE |
| 21 | `Views/AnimalDescription/Components/TextAreaView.swift` | `Common/Components/Form/TextAreaView.swift` | MOVE |
| 22 | `Views/AnimalDescription/Components/TextAreaView_Model.swift` | `Common/Components/Form/TextAreaView_Model.swift` | MOVE |
| 23 | `Views/AnimalDescription/Components/ValidatedTextField.swift` | `Common/Components/Form/ValidatedTextField.swift` | MOVE |
| 24 | `Views/AnimalDescription/Components/ValidatedTextField_Model.swift` | `Common/Components/Form/ValidatedTextField_Model.swift` | MOVE |
| **Views - AnimalDescription Helper Types → Common** |||
| 25 | `Views/AnimalDescription/CoordinateValidationResult.swift` | `Common/Models/CoordinateValidationResult.swift` | MOVE |
| 26 | `Views/AnimalDescription/FormField.swift` | `Common/Models/FormField.swift` | MOVE |
| 27 | `Views/AnimalDescription/ValidationError.swift` | `Common/Models/ValidationError.swift` | MOVE |
| **Views - ChipNumber (Full-Screen)** |||
| 28 | `Views/ChipNumber/ChipNumberView.swift` | `ReportMissingPet/Views/MissingPetChipNumberView.swift` | MOVE + RENAME |
| 29 | `Views/ChipNumber/ChipNumberViewModel.swift` | `ReportMissingPet/Views/MissingPetChipNumberViewModel.swift` | MOVE + RENAME |
| **Views - Components (Toast → Common)** |||
| 30 | `Views/Components/ToastView.swift` | `Common/Components/Toast/ToastView.swift` | MOVE |
| 31 | `Views/Components/ToastView_Model.swift` | `Common/Components/Toast/ToastView_Model.swift` | MOVE |
| **Views - ContactDetails (Full-Screen)** |||
| 32 | `Views/ContactDetails/ContactDetailsView.swift` | `ReportMissingPet/Views/MissingPetContactDetailsView.swift` | MOVE + RENAME |
| 33 | `Views/ContactDetails/ContactDetailsViewModel.swift` | `ReportMissingPet/Views/MissingPetContactDetailsViewModel.swift` | MOVE + RENAME |
| **Views - Photo (Full-Screen)** |||
| 34 | `Views/Photo/PhotoView.swift` | `ReportMissingPet/Views/MissingPetPhotoView.swift` | MOVE + RENAME |
| 35 | `Views/Photo/PhotoViewModel.swift` | `ReportMissingPet/Views/MissingPetPhotoViewModel.swift` | MOVE + RENAME |
| **Views - Photo/Components (Photo Browser → Common)** |||
| 36 | `Views/Photo/Components/AnimalPhotoBrowseView.swift` | `Common/Components/Photo/AnimalPhotoBrowseView.swift` | MOVE |
| 37 | `Views/Photo/Components/AnimalPhotoItemView.swift` | `Common/Components/Photo/AnimalPhotoItemView.swift` | MOVE |
| 38 | `Views/Photo/Components/AnimalPhotoItemView_Model.swift` | `Common/Components/Photo/AnimalPhotoItemView_Model.swift` | MOVE |
| **Views - Summary (Full-Screen)** |||
| 39 | `Views/Summary/SummaryView.swift` | `ReportMissingPet/Views/MissingPetSummaryView.swift` | MOVE + RENAME |
| 40 | `Views/Summary/SummaryView+Constants.swift` | `ReportMissingPet/Views/MissingPetSummaryView+Constants.swift` | MOVE + RENAME |
| 41 | `Views/Summary/SummaryViewModel.swift` | `ReportMissingPet/Views/MissingPetSummaryViewModel.swift` | MOVE + RENAME |

**Total Files**: 41 files
- **Move to Common/**: 26 files (helpers, services, models, components)
- **Stay in ReportMissingPet/ (with rename where applicable)**: 15 files (coordinator, full-screen views, ViewModels, FlowState)

---

### RQ-2: Test File Inventory - Which test files need to move?

**Decision**: Mirror production structure in test directory.

| # | Current Test Path | Destination Test Path | Action |
|---|------------------|----------------------|--------|
| **Models Tests** |||
| 1 | `Features/ReportMissingPet/Models/PhotoAttachmentStateTests.swift` | `Features/ReportMissingAndFoundPet/Common/Models/PhotoAttachmentStateTests.swift` | MOVE |
| 2 | `Features/ReportMissingPet/Models/ReportMissingPetFlowStateTests.swift` | `Features/ReportMissingAndFoundPet/ReportMissingPet/Models/ReportMissingPetFlowStateTests.swift` | MOVE (flow-specific) |
| **Helpers Tests** |||
| 3 | `Features/ReportMissingPet/Helpers/MicrochipNumberFormatterTests.swift` | `Features/ReportMissingAndFoundPet/Common/Helpers/MicrochipNumberFormatterTests.swift` | MOVE |
| **Services Tests** |||
| 4 | `Features/ReportMissingPet/Services/PhotoAttachmentCacheTests.swift` | `Features/ReportMissingAndFoundPet/Common/Services/PhotoAttachmentCacheTests.swift` | MOVE |
| **ViewModel Tests (stay in ReportMissingPet)** |||
| 5 | `Features/ReportMissingPet/AnimalDescription/AnimalDescriptionViewModelTests.swift` | `Features/ReportMissingAndFoundPet/ReportMissingPet/MissingPetAnimalDescriptionViewModelTests.swift` | MOVE + RENAME |
| 6 | `Features/ReportMissingPet/Views/ChipNumberViewModelTests.swift` | `Features/ReportMissingAndFoundPet/ReportMissingPet/MissingPetChipNumberViewModelTests.swift` | MOVE + RENAME |
| 7 | `Features/ReportMissingPet/Views/PhotoViewModelTests.swift` | `Features/ReportMissingAndFoundPet/ReportMissingPet/MissingPetPhotoViewModelTests.swift` | MOVE + RENAME |
| 8 | `Features/ReportMissingPet/Views/SummaryViewModelTests.swift` | `Features/ReportMissingAndFoundPet/ReportMissingPet/MissingPetSummaryViewModelTests.swift` | MOVE + RENAME |
| 9 | `Features/ReportMissingPet/ContactDetails/ContactDetailsViewModelTests.swift` | `Features/ReportMissingAndFoundPet/ReportMissingPet/MissingPetContactDetailsViewModelTests.swift` | MOVE + RENAME |
| 10 | `Features/ReportMissingPet/ContactDetails/ContactDetailsViewModelValidationTests.swift` | `Features/ReportMissingAndFoundPet/ReportMissingPet/MissingPetContactDetailsViewModelValidationTests.swift` | MOVE + RENAME |
| 11 | `Features/ReportMissingPet/ContactDetails/ContactDetailsViewModelErrorHandlingTests.swift` | `Features/ReportMissingAndFoundPet/ReportMissingPet/MissingPetContactDetailsViewModelErrorHandlingTests.swift` | MOVE + RENAME |
| 12 | `Features/ReportMissingPet/ContactDetails/ContactDetailsViewModelRewardTests.swift` | `Features/ReportMissingAndFoundPet/ReportMissingPet/MissingPetContactDetailsViewModelRewardTests.swift` | MOVE + RENAME |
| 13 | `Features/ReportMissingPet/ContactDetails/AnnouncementSubmissionServiceTests.swift` | `Features/ReportMissingAndFoundPet/Common/Services/AnnouncementSubmissionServiceTests.swift` | MOVE |
| **Support Files (Fakes)** |||
| 14 | `Features/ReportMissingPet/Support/PhotoAttachmentCacheFake.swift` | `Features/ReportMissingAndFoundPet/Common/Support/PhotoAttachmentCacheFake.swift` | MOVE |
| 15 | `Features/ReportMissingPet/Support/ToastSchedulerFake.swift` | `Features/ReportMissingAndFoundPet/Common/Support/ToastSchedulerFake.swift` | MOVE |
| 16 | `Features/ReportMissingPet/Views/PhotoSelectionProcessorTests.swift` | `Features/ReportMissingAndFoundPet/Common/Helpers/PhotoSelectionProcessorTests.swift` | MOVE |

**Total Test Files**: 16 files

---

### RQ-3: Where should ReportMissingPetFlowState live?

**Decision**: STAY in `ReportMissingPet/Models/` - NOT moved to Common/.

**Rationale**: 
1. The flow state is specific to "Missing Pet" flow (contains `disappearanceDate`, specific step definitions)
2. "Found Pet" flow will need a separate `ReportFoundPetFlowState` with different fields
3. Same rule as coordinator and full-screen views: flow-specific → stays in feature directory
4. The name clearly indicates this is for "Missing Pet" flow

**Alternatives considered**:
- Move to `Common/Models/` - Rejected: FlowState is flow-specific, not shared
- Rename to generic `ReportPetFlowState` - Rejected: Over-engineering without Found Pet requirements

---

### RQ-4: Xcode Project File Handling Strategy

**Decision**: All file operations MUST be performed within Xcode (not filesystem-only).

**Rationale**: 
- Moving files via Finder/terminal breaks Xcode project references
- Xcode project file (`.pbxproj`) stores file references with UUIDs
- Manual editing of `.pbxproj` is error-prone and not recommended

**Approach**:
1. Use Xcode's "Move" functionality (right-click → Move to Trash, then re-add)
2. Or drag-and-drop within Xcode navigator to new locations
3. Verify all moved files show no "?" indicator (missing reference)
4. Build project after each major move to catch issues early

---

### RQ-5: Import Statement Update Strategy

**Decision**: Use Xcode's refactoring tools where possible, manual updates otherwise.

**Rationale**:
- Swift doesn't require explicit imports within the same module/target
- Files in `/iosApp/iosApp/` are all in same target (`iosApp`)
- Import updates needed primarily for class/struct name changes (MissingPet prefix)

**Affected References**:
- `ChipNumberView` → `MissingPetChipNumberView`
- `ChipNumberViewModel` → `MissingPetChipNumberViewModel`
- `PhotoView` → `MissingPetPhotoView`
- `PhotoViewModel` → `MissingPetPhotoViewModel`
- `AnimalDescriptionView` → `MissingPetAnimalDescriptionView`
- `AnimalDescriptionViewModel` → `MissingPetAnimalDescriptionViewModel`
- `ContactDetailsView` → `MissingPetContactDetailsView`
- `ContactDetailsViewModel` → `MissingPetContactDetailsViewModel`
- `SummaryView` → `MissingPetSummaryView`
- `SummaryViewModel` → `MissingPetSummaryViewModel`
- `ReportMissingPetCoordinator` → `MissingPetReportCoordinator`

---

### RQ-6: Git Commit Strategy (revised from IC-001)

**Decision**: Two atomic commits (revised from original 3-commit plan).

**Rationale for revision**: Xcode "Refactor → Rename" changes class name AND all references atomically. Separating rename from reference updates is impossible - code won't compile between steps.

**Commit Sequence**:

1. **Commit 1: Directory Structure** - Create new parent directory, move files
   - Create `ReportMissingAndFoundPet/` parent directory
   - Move `ReportMissingPet/` under it
   - Create `Common/` directory structure
   - Move shared files to Common/
   - Build + Test + Manual smoke test

2. **Commit 2: Class Renames + Reference Updates** - Add MissingPet prefix (all at once)
   - Use Xcode "Refactor → Rename" for each class
   - Rename coordinator: `ReportMissingPetCoordinator` → `MissingPetReportCoordinator`
   - Rename full-screen views with `MissingPet` prefix
   - Rename ViewModels with `MissingPet` prefix
   - Rename test files to match production
   - Build + Test + Manual smoke test

---

## Technical Decisions Summary

| Decision | Choice | Rationale |
|----------|--------|-----------|
| File inventory approach | Explicit mapping in research.md | Per FR-001 requirement |
| FlowState location | Stay in `ReportMissingPet/Models/` | Flow-specific, same rule as coordinator/views |
| Xcode operations | In-IDE only (no filesystem moves) | Preserve project references |
| Git strategy | 2 atomic commits (revised from 3) | Rename + references inseparable in Xcode |
| Subdirectory structure in Common/ | `Helpers/`, `Models/`, `Services/`, `Components/{Form,Toast,Photo}/` | Logical grouping matching source structure |

---

## Risk Assessment

| Risk | Likelihood | Impact | Mitigation |
|------|------------|--------|------------|
| Broken Xcode references after move | Medium | High | Perform all moves in Xcode IDE |
| Missed import/reference updates | Medium | Medium | Build + full test suite after each commit |
| Test failures due to name changes | Low | Medium | Update test files in same commit as production |
| Rollback needed | Low | Low | Atomic commits enable clean `git reset --hard HEAD~1` |

---

## Conclusion

All NEEDS CLARIFICATION items resolved. Ready to proceed with Phase 1 design and implementation planning.

