# Tasks: Location Permission Request Timing

**Input**: Design documents from `/specs/070-location-permission-timing/`
**Prerequisites**: plan.md ✓, spec.md ✓, research.md ✓, data-model.md ✓, quickstart.md ✓

**Tests**: Per plan.md, no new tests are required for this feature:
- **Android Unit Tests**: No new tests needed - existing `AnimalListViewModel` tests remain valid
- **E2E Tests**: Not required - minimal code change, existing E2E tests validate behavior
- **Manual Testing**: Verify permission dialog timing via manual testing (see Phase 4)

**Organization**: Tasks organized by user story to enable independent verification.

## Format: `[ID] [P?] [Story?] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2, US3)

## Scope

**Platform**: Android only (iOS already has correct timing)
**Files Changed**: 2 files
**Lines Changed**: ~15 added, ~10 removed
**New Files**: 0
**New Tests**: 0 (existing tests sufficient)

---

## Phase 1: Setup (Verification)

**Purpose**: Verify prerequisites and build environment before making changes

- [ ] T001 Verify Android project builds successfully: `./gradlew :composeApp:assembleDebug`
- [ ] T002 Verify existing unit tests pass: `./gradlew :composeApp:testDebugUnitTest`
- [ ] T003 [P] Create feature branch `070-location-permission-timing` from main

**Checkpoint**: Build passes, existing tests green, ready for implementation

---

## Phase 2: User Story 1 - Location Permission on App Launch (Priority: P1) 🎯 MVP

**Goal**: Request location permission when MainScaffold (main screen with bottom navigation) first appears, matching iOS behavior.

**Independent Test**: Launch app (fresh install or reset permissions) → permission dialog appears when main screen loads, before navigating to any tab.

### Implementation for User Story 1

**Android**:

- [ ] T004 [US1] Add Accompanist permissions imports to `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/ui/navigation/MainScaffold.kt`
- [ ] T005 [US1] Add `@OptIn(ExperimentalPermissionsApi::class)` annotation to MainScaffold function
- [ ] T006 [US1] Add `rememberMultiplePermissionsState` for location permissions at start of MainScaffold
- [ ] T007 [US1] Add fire-and-forget permission request logic using LaunchedEffect in MainScaffold
- [ ] T008 [US1] Verify MainScaffold compiles with new permission logic: `./gradlew :composeApp:assembleDebug`

**Checkpoint**: MainScaffold now requests permission on first composition. User Story 1 independently testable.

---

## Phase 3: User Story 2 - Lost Pet Tab Without Permission Request (Priority: P2)

**Goal**: Remove initial permission request from AnimalListScreen since MainScaffold now handles it.

**Independent Test**: After granting/denying permission on main screen, navigate to Lost Pet tab → no additional permission dialog appears.

### Implementation for User Story 2

**Android**:

- [ ] T009 [US2] Simplify initial permission check LaunchedEffect in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalListScreen.kt`
- [ ] T010 [US2] Remove the `else` branch that calls `locationPermissionState.launchMultiplePermissionRequest()` for first-time requests
- [ ] T011 [US2] Update LaunchedEffect to always dispatch current permission status to ViewModel
- [ ] T012 [US2] Verify AnimalListScreen compiles with simplified logic: `./gradlew :composeApp:assembleDebug`

**Checkpoint**: AnimalListScreen no longer requests permission on its own. User Story 2 independently testable.

---

## Phase 4: User Story 3 - Existing Permission Behavior Preserved (Priority: P3)

**Goal**: Verify all existing permission behaviors continue working (rationale dialogs, Settings navigation, permission change detection).

**Independent Test**: Test rationale dialogs and Settings navigation flow work from Lost Pet tab context.

### Verification for User Story 3

**Note**: No code changes needed - this story is about verifying existing behavior is preserved.

- [ ] T013 [P] [US3] Verify educational rationale dialog appears when `shouldShowRationale = true` (user previously denied)
- [ ] T014 [P] [US3] Verify informational rationale with "Go to Settings" appears when `shouldShowRationale = false` after denial
- [ ] T015 [P] [US3] Verify permission change detection works when user changes permission in Settings and returns to app

**Checkpoint**: All existing rationale flows continue working. User Story 3 verified.

---

## Phase 5: Polish & Validation

**Purpose**: Final verification and cleanup

- [ ] T016 [P] Run full test suite: `./gradlew :composeApp:testDebugUnitTest`
- [ ] T017 [P] Run linter: `./gradlew :composeApp:lint`
- [ ] T018 [P] Verify test coverage maintained: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
- [ ] T019 Complete manual testing checklist from quickstart.md:
  - [ ] Fresh install: Permission dialog appears when main screen loads
  - [ ] Fresh install: Navigating to Lost Pet tab does NOT show another permission dialog
  - [ ] After denial: Rationale dialog appears on Lost Pet tab (as before)
  - [ ] After "Don't ask again": Informational dialog with Settings link appears
  - [ ] Permission grant: Location is used for animal listings

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - start immediately
- **User Story 1 (Phase 2)**: Depends on Setup completion
- **User Story 2 (Phase 3)**: Depends on User Story 1 completion (permission must be requested in MainScaffold first)
- **User Story 3 (Phase 4)**: Depends on User Story 1 & 2 completion
- **Polish (Phase 5)**: Depends on all user stories complete

### Task Dependencies Within Phases

- **Phase 2**: T004 → T005 → T006 → T007 → T008 (sequential - same file)
- **Phase 3**: T009 → T010 → T011 → T012 (sequential - same file)
- **Phase 4**: T013 [P], T014 [P], T015 [P] - can verify in any order (independent manual verifications)
- **Phase 5**: T016 [P], T017 [P], T018 [P] - can run in parallel (independent Gradle tasks), T019 is manual

### Parallel Opportunities

- Phase 1 tasks T001 and T002 can run in parallel
- Phase 4 verification tasks can be done in any order
- Phase 5 automated tasks (T016, T017, T018) can run in parallel

---

## Implementation Notes

### MainScaffold.kt Changes (T004-T007)

```kotlin
// Add imports
import android.Manifest
import androidx.compose.runtime.mutableStateOf
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

// Add at start of MainScaffold function
val permissionState = rememberMultiplePermissionsState(
    permissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )
)

var hasRequestedPermission by remember { mutableStateOf(false) }
LaunchedEffect(Unit) {
    val notDetermined = !permissionState.allPermissionsGranted && 
                       !permissionState.shouldShowRationale
    if (!hasRequestedPermission && notDetermined) {
        hasRequestedPermission = true
        permissionState.launchMultiplePermissionRequest()
    }
}
```

### AnimalListScreen.kt Changes (T009-T011)

**Before**: Initial LaunchedEffect had 3 branches (granted, shouldShowRationale, else-request)

**After**: Simplify to always dispatch current permission status:

```kotlin
LaunchedEffect(anyGranted, shouldShowRationale) {
    if (hasHandledInitialPermission) return@LaunchedEffect
    hasHandledInitialPermission = true

    viewModel.dispatchIntent(
        AnimalListIntent.PermissionResult(
            granted = anyGranted,
            fineLocation = fineGranted,
            coarseLocation = coarseGranted,
            shouldShowRationale = shouldShowRationale,
        ),
    )
}
```

---

## Summary

| Metric | Value |
|--------|-------|
| Total Tasks | 19 |
| Phase 1 (Setup) | 3 tasks |
| Phase 2 (US1 - MVP) | 5 tasks |
| Phase 3 (US2) | 4 tasks |
| Phase 4 (US3) | 3 tasks (verification only) |
| Phase 5 (Polish) | 4 tasks |
| Files Modified | 2 |
| New Files | 0 |
| New Tests | 0 |
| Parallel Opportunities | Limited (most work is on same 2 files) |
| MVP Scope | Phase 1 + Phase 2 (8 tasks total) |

---

## Estimation Update

| Platform | Tasks | Days | Notes |
|----------|-------|------|-------|
| Backend | 0 | 0 | No backend changes needed |
| iOS | 0 | 0 | Already at correct timing |
| Android | 19 | ~0.5 | Simple code movement, no new tests |
| Web | 0 | 0 | No web changes needed |
| **Total** | 19 | **~0.5 days** | |

**Final Estimate**: 1 SP / ~0.5 days implementation (well within 5.2 day budget with 1.3x buffer)
