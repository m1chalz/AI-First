# Tasks: Android Animal Photo Screen

**Input**: Design documents from `/specs/040-android-animal-photo-screen/`  
**Prerequisites**: plan.md ✓, spec.md ✓, research.md ✓, data-model.md ✓, quickstart.md ✓

**Tests**: Test requirements for this feature:

**MANDATORY - Android Unit Tests**:
- Location: `/composeApp/src/androidUnitTest/`
- Framework: JUnit 6 + Kotlin Test + Turbine (Flow testing)
- Coverage target: 80% line + branch for new photo state, intents, and reducer logic
- Scope: PhotoAttachmentState, PhotoStatus, photo reducers, ViewModel photo handling
- Convention: MUST follow Given-When-Then structure with backtick test names

**MANDATORY - End-to-End Tests**:
- Location: `/e2e-tests/src/test/java/.../screens/` and `/e2e-tests/src/test/resources/features/mobile/`
- Framework: Appium + Cucumber (Java)
- Screen Object Model with `@AndroidFindBy` annotations
- All user stories MUST have E2E test coverage

**Organization**: Tasks grouped by user story to enable independent implementation and testing.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies on incomplete tasks)
- **[Story]**: Which user story this task belongs to (US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

```text
composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/
├── features/reportmissing/
│   ├── presentation/
│   │   ├── mvi/                    # MVI artifacts
│   │   ├── state/                  # FlowState
│   │   └── viewmodels/             # ViewModels
│   └── ui/photo/
│       ├── components/             # NEW: Photo-specific components
│       ├── PhotoScreen.kt
│       └── PhotoContent.kt
└── core/util/                      # NEW: Utilities

composeApp/src/androidUnitTest/kotlin/.../features/reportmissing/
├── presentation/mvi/               # Reducer tests
└── ui/photo/                       # ViewModel/Content tests
```

---

## Phase 1: Setup

**Purpose**: No new setup required - extends existing Android project infrastructure

**Status**: ✅ SKIP - Existing project with Koin DI, Navigation, and MVI patterns already configured

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core data structures and utilities that ALL user stories depend on

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

### Data Model Foundation

- [X] T001 [P] Create `PhotoStatus` enum (EMPTY, LOADING, CONFIRMED, ERROR) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/mvi/PhotoStatus.kt`
- [X] T002 [P] Create `PhotoAttachmentState` data class with uri, filename, sizeBytes, status fields and computed properties (hasPhoto, formattedSize, displayFilename) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/mvi/PhotoAttachmentState.kt`
- [X] T003 Extend `FlowData` with photoFilename and photoSizeBytes fields in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/state/ReportMissingFlowState.kt`
- [X] T004 Add `updatePhoto(uri, filename, sizeBytes)` and `clearPhoto()` methods to `ReportMissingFlowState` in same file

### Utility Foundation

- [X] T005 [P] Create `FileSizeFormatter` object with `format(bytes: Long): String` function (returns "X B", "X.X KB", or "X.X MB") in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/core/util/FileSizeFormatter.kt`
- [X] T006 [P] Write unit test for `FileSizeFormatter` covering bytes, KB, MB ranges in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/core/util/FileSizeFormatterTest.kt`

### MVI Foundation

- [X] T007 [P] Add photo intents to `ReportMissingIntent`: OpenPhotoPicker, PhotoSelected(uri), PhotoMetadataLoaded(uri, filename, sizeBytes), PhotoLoadFailed, RemovePhoto, PhotoPickerCancelled in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/mvi/ReportMissingIntent.kt`
- [X] T008 [P] Add photo effects to `ReportMissingEffect`: LaunchPhotoPicker, ShowToast(messageResId, duration) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/mvi/ReportMissingEffect.kt`
- [X] T009 Modify `ReportMissingUiState` to replace `photoUri: String?` with `photoAttachment: PhotoAttachmentState` field in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/mvi/ReportMissingUiState.kt`
- [X] T010 Update `ReportMissingUiStatePreviewProvider` with sample PhotoAttachmentState values (empty, loading, confirmed, long filename) in same file

### UI Component Foundation

- [X] T011 [P] Create `PhotoEmptyState` composable (placeholder icon, "Your pet's photo" title, helper text, Browse button with testTag `animalPhoto.browseButton`) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/photo/components/PhotoEmptyState.kt`
- [X] T012 [P] Create `PhotoConfirmationCard` composable (AsyncImage thumbnail, filename text, file size text, Remove X button, all with testTags: `animalPhoto.thumbnail`, `animalPhoto.filename`, `animalPhoto.fileSize`, `animalPhoto.removeButton`) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/photo/components/PhotoConfirmationCard.kt`
- [X] T013 [P] Create `PhotoLoadingState` composable (shimmer/spinner indicator with testTag `animalPhoto.loadingIndicator`) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/photo/components/PhotoLoadingState.kt`

### Test Foundation

- [X] T014 [P] Write unit tests for `PhotoAttachmentState` computed properties (hasPhoto, displayFilename truncation) in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/mvi/PhotoAttachmentStateTest.kt`

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Attach Photo via Photo Picker (Priority: P1) 🎯 MVP

**Goal**: Users can tap Browse, select a photo from Photo Picker, and see confirmation card with thumbnail, filename, and size

**Independent Test**: Launch flow → Navigate to Photo step → Tap Browse → Select image → Verify confirmation card displays

### Tests for User Story 1 ✅

- [ ] T015 [P] [US1] Write unit test for photo selection reducer: `PhotoSelected` → status=LOADING in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/mvi/PhotoReducerTest.kt`
- [ ] T016 [P] [US1] Write unit test for photo metadata reducer: `PhotoMetadataLoaded` → status=CONFIRMED with uri, filename, sizeBytes in same file
- [ ] T017 [P] [US1] Write unit test for state persistence: photoAttachment survives navigation to Description and back in same file
- [ ] T018 [P] [US1] Write ViewModel test for OpenPhotoPicker intent → emits LaunchPhotoPicker effect using Turbine in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/photo/PhotoViewModelTest.kt`
- [ ] T019 [P] [US1] Write ViewModel test for PhotoSelected → updates state to LOADING, extracts metadata, updates to CONFIRMED in same file

### Implementation for User Story 1

**Reducer & ViewModel**:

- [ ] T020 [US1] Implement photo selection reducers in `ReportMissingReducer`: handle PhotoSelected (set LOADING), PhotoMetadataLoaded (set CONFIRMED with data) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/mvi/ReportMissingReducer.kt`
- [ ] T021 [US1] Create `PhotoViewModel` following ChipNumberViewModel pattern: inject ReportMissingFlowState, callbacks for navigation in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/viewmodels/PhotoViewModel.kt`
- [ ] T022 [US1] Implement OpenPhotoPicker intent handler in PhotoViewModel: emit LaunchPhotoPicker effect
- [ ] T023 [US1] Implement PhotoSelected intent handler: set LOADING state, extract metadata using ContentResolver, dispatch PhotoMetadataLoaded
- [ ] T024 [US1] Implement metadata extraction: query OpenableColumns.DISPLAY_NAME and SIZE, take persistable URI permission
- [ ] T025 [US1] Save photo data to FlowState on successful selection using `flowState.updatePhoto(uri, filename, sizeBytes)`
- [ ] T026 [US1] Initialize PhotoViewModel state from FlowState for back navigation support

**UI Layer**:

- [ ] T027 [US1] Update `PhotoContent` to render PhotoEmptyState when status=EMPTY, PhotoLoadingState when status=LOADING, PhotoConfirmationCard when status=CONFIRMED in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/photo/PhotoContent.kt`
- [ ] T028 [US1] Update `PhotoScreen` to create photo picker launcher using `rememberLauncherForActivityResult(PickVisualMedia())` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/photo/PhotoScreen.kt`
- [ ] T029 [US1] Handle LaunchPhotoPicker effect in PhotoScreen: check Photo Picker availability, launch with ImageOnly filter
- [ ] T030 [US1] Implement photo picker fallback: if `isPhotoPickerAvailable()` returns false, use `GetContent` with "image/*" MIME type
- [ ] T031 [US1] Wire picker result to ViewModel: on success dispatch PhotoSelected(uri), on cancel dispatch PhotoPickerCancelled
- [ ] T032 [US1] Add testTags to PhotoContent: `animalPhoto.browseButton`, `animalPhoto.thumbnail`, `animalPhoto.filename`, `animalPhoto.fileSize`, `animalPhoto.removeButton`

**Previews**:

- [ ] T033 [P] [US1] Create `PhotoContentPreviewProvider` with empty, loading, confirmed (short name), confirmed (long name) states in PhotoContent.kt
- [ ] T034 [P] [US1] Add @Preview function for PhotoContent using @PreviewParameter with callback lambdas defaulted to no-ops

**String Resources**:

- [ ] T035 [P] [US1] Add string resources: `photo_screen_title`, `photo_helper_text`, `photo_browse_button` in `/composeApp/src/androidMain/res/values/strings.xml`

**Checkpoint**: User Story 1 complete - photo selection happy path works independently

---

## Phase 4: User Story 2 - Enforce Mandatory Photo (Priority: P2)

**Goal**: Tapping Continue without a photo shows the Android standard long toast (`Toast.LENGTH_LONG`) with "Photo is mandatory" and prevents navigation

**Independent Test**: Navigate to Photo step → Tap Continue without photo → Verify toast appears and navigation blocked

### Tests for User Story 2 ✅

- [ ] T036 [P] [US2] Write unit test for NavigateNext on PHOTO step without photo: should NOT emit NavigateToStep, should emit ShowToast in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/photo/PhotoViewModelTest.kt`
- [ ] T037 [P] [US2] Write unit test for NavigateNext on PHOTO step WITH photo: should emit NavigateToStep(DESCRIPTION) in same file
- [ ] T038 [P] [US2] Write unit test for RemovePhoto intent: clears photoAttachment, clears FlowState photo data in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/mvi/PhotoReducerTest.kt`

### Implementation for User Story 2

**Validation Logic**:

- [ ] T039 [US2] Implement RemovePhoto reducer: reset photoAttachment to Empty, clear FlowState via `flowState.clearPhoto()` in ReportMissingReducer.kt
- [ ] T040 [US2] Modify NavigateNext handling in PhotoViewModel: if currentStep==PHOTO && !photoAttachment.hasPhoto → emit ShowToast(R.string.photo_mandatory), block navigation
- [ ] T041 [US2] Implement Continue button validation: if photo present, save to FlowState and navigate; if not, show toast

**UI Layer**:

- [ ] T042 [US2] Handle ShowToast effect in PhotoScreen: `Toast.makeText(context, messageResId, duration).show()`
- [ ] T043 [US2] Wire Remove button in PhotoConfirmationCard to dispatch RemovePhoto intent
- [ ] T044 [US2] Add testTag to remove button: `animalPhoto.removeButton`

**String Resources**:

- [ ] T045 [P] [US2] Add string resource: `photo_mandatory` = "Photo is mandatory" in strings.xml

**Checkpoint**: User Story 2 complete - mandatory validation works independently

---

## Phase 5: User Story 3 - Recover from Picker Issues (Priority: P3)

**Goal**: Users who cancel picker or encounter load errors see guidance and can retry without losing other data

**Independent Test**: Tap Browse → Cancel picker → Verify empty state returns → Tap Continue → Verify toast

### Tests for User Story 3 ✅

- [ ] T046 [P] [US3] Write unit test for PhotoPickerCancelled: state remains EMPTY, other flow data preserved in PhotoReducerTest.kt
- [ ] T047 [P] [US3] Write unit test for PhotoLoadFailed: state reverts to EMPTY in PhotoReducerTest.kt

### Implementation for User Story 3

**Error Handling**:

- [ ] T048 [US3] Implement PhotoPickerCancelled reducer: keep status=EMPTY, preserve other state in ReportMissingReducer.kt
- [ ] T049 [US3] Implement PhotoLoadFailed reducer: reset to EMPTY status in ReportMissingReducer.kt
- [ ] T050 [US3] Handle metadata extraction failure in PhotoViewModel: wrap in try-catch, dispatch PhotoLoadFailed on exception
- [ ] T051 [US3] Handle picker cancellation in PhotoScreen: when result URI is null, dispatch PhotoPickerCancelled

**Permission Handling (Android 12 and below)**:

- [ ] T052 [P] [US3] Check READ_EXTERNAL_STORAGE permission before launching fallback picker (API < 33 only)
- [ ] T053 [US3] Handle permission denial: show guidance toast/snackbar with Settings deep link option

**Checkpoint**: User Story 3 complete - error recovery works independently

---

## Phase 6: Polish & Integration

**Purpose**: E2E tests, documentation, final verification

### E2E Tests

- [ ] T054 [P] Create Screen Object `AnimalPhotoScreen.java` with @AndroidFindBy annotations for browse, remove, continue, filename, filesize in `/e2e-tests/src/test/java/com/intive/aifirst/petspot/screens/AnimalPhotoScreen.java`
- [ ] T055 [P] Create step definitions `AnimalPhotoSteps.java` for photo selection scenarios in `/e2e-tests/src/test/java/com/intive/aifirst/petspot/steps/AnimalPhotoSteps.java`
- [ ] T056 [P] Create Gherkin feature file `android-animal-photo.feature` with scenarios for US1, US2, US3 in `/e2e-tests/src/test/resources/features/mobile/android-animal-photo.feature`

### Documentation & Cleanup

- [ ] T057 [P] Add KDoc to PhotoAttachmentState, PhotoStatus, FileSizeFormatter
- [ ] T058 [P] Add KDoc to PhotoViewModel public methods
- [ ] T059 Run `./gradlew :composeApp:testDebugUnitTest koverHtmlReport` and verify 80% coverage for new code
- [ ] T060 Run manual testing checklist from quickstart.md

---

## Dependencies & Execution Order

### Phase Dependencies

```
Phase 1 (Setup)      → SKIP (existing project)
         ↓
Phase 2 (Foundation) → BLOCKS all user stories
         ↓
    ┌────┴────┬────────────┐
    ↓         ↓            ↓
Phase 3    Phase 4     Phase 5
  (US1)      (US2)       (US3)
    ↓         ↓            ↓
    └────┬────┴────────────┘
         ↓
Phase 6 (Polish)
```

### User Story Dependencies

- **US1 (P1)**: Depends on Foundation only - Core photo selection
- **US2 (P2)**: Depends on Foundation + extends US1 validation logic
- **US3 (P3)**: Depends on Foundation + extends US1 error handling

### Task Dependencies Within Phases

**Foundation (Phase 2)**:
- T001-T002 (data classes) → T003-T004 (FlowState) → T009 (UiState)
- T005-T006 (FileSizeFormatter) independent
- T007-T008 (intents/effects) independent
- T011-T013 (UI components) depend on T001-T002

**US1 (Phase 3)**:
- T015-T019 (tests) parallel
- T020 (reducer) → T021-T026 (ViewModel) → T027-T032 (UI)
- T033-T035 (previews, strings) parallel after UI

**US2 (Phase 4)**:
- T036-T038 (tests) parallel
- T039-T041 (validation) → T042-T044 (UI)

**US3 (Phase 5)**:
- T046-T047 (tests) parallel
- T048-T051 (error handling) sequential
- T052-T053 (permissions) parallel

### Parallel Opportunities

```bash
# Foundation - run in parallel:
T001, T002, T005, T007, T008, T011, T012, T013 (all [P] tasks)

# US1 Tests - run in parallel:
T015, T016, T017, T018, T019 (all [P] tasks)

# US1 Implementation - some parallel:
T033, T034, T035 (previews/strings after UI complete)

# US2/US3 Tests - can run in parallel with implementation:
T036-T038, T046-T047

# E2E - all parallel:
T054, T055, T056
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 2: Foundation (T001-T014)
2. Complete Phase 3: User Story 1 (T015-T035)
3. **STOP and VALIDATE**: Test photo selection independently
4. Demo/deploy MVP if ready

### Incremental Delivery

1. Foundation → Ready for all stories
2. Add US1 → Photo selection works → Demo
3. Add US2 → Validation enforced → Demo
4. Add US3 → Error recovery → Demo
5. Polish → E2E tests, docs → Release

### Estimated Task Counts

| Phase | Task Count | Parallel Tasks |
|-------|------------|----------------|
| Foundation | 14 | 10 |
| US1 | 21 | 8 |
| US2 | 10 | 4 |
| US3 | 8 | 4 |
| Polish | 7 | 4 |
| **Total** | **60** | **30** |

---

## Notes

- All tasks assume existing Koin DI, Navigation Component, and MVI patterns from spec 018
- PhotoViewModel follows ChipNumberViewModel pattern for consistency
- State persists via ReportMissingFlowState (nav graph scoped), not SavedStateHandle
- Photo Picker fallback required for Android < 13 without Google Play Services backport
- Test tags follow `{screen}.{element}.{action}` convention per constitution

