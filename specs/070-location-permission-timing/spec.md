# Feature Specification: Location Permission Request Timing

**Feature Branch**: `070-location-permission-timing`  
**Created**: 2026-01-08  
**Status**: Draft  
**Jira Ticket**: [KAN-33](https://ai-first-intive.atlassian.net/browse/KAN-33)  
**Design**: N/A (no UI changes required)  
**Input**: User description: "Change the way and time when app asks for location permissions. Move from lost pets tab to main screen with bottom navigation."

## Background & Context

Currently, location permissions are requested at different times across platforms:

### iOS (Current - Correct Timing)
- Location permission is requested in `SceneDelegate.swift` at app launch (fire-and-forget)
- Request happens when the main tab bar screen is shown
- This is the **desired behavior** that Android should match

### Android (Current - Incorrect Timing)
- Location permission is requested in `AnimalListScreen.kt` when the Lost Pet tab is opened
- Permission handling is tied to `AnimalListViewModel` and executed via `LaunchedEffect`
- This was an artifact of the implementation order - permissions were added before tab navigation was implemented

### Why This Matters
1. **User Experience**: Users should be asked about location when they first see the app, not when navigating to a specific tab
2. **Consistency**: Both platforms should request permissions at the same point in the user journey
3. **Feature Expansion**: Other tabs (Home with map, Found Pet) will also use location - requesting on Lost Pet tab is no longer appropriate

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Location Permission on App Launch (Priority: P1)

When a user launches the app and the main screen with bottom navigation appears, the app should request location permissions. This ensures location is available for all location-dependent features regardless of which tab the user navigates to.

**Why this priority**: This is the core change - moving permission request to the correct timing point.

**Independent Test**: Can be fully tested by launching the app (fresh install or with notDetermined/not-requested permission status) and verifying the permission dialog appears when the main screen with bottom navigation is displayed, not when navigating to a specific tab.

**Acceptance Scenarios**:

1. **Given** user hasn't been asked about location permissions (permission not yet requested), **When** main screen with bottom navigation appears, **Then** system permission dialog for location is displayed
2. **Given** user launches app for the first time, **When** main navigation screen appears, **Then** location permission is requested before user navigates to any specific tab
3. **Given** permission dialog is displayed on main screen, **When** user grants permission, **Then** location is fetched and available for all tabs (Lost Pet, Home, Found Pet)
4. **Given** permission dialog is displayed on main screen, **When** user denies permission, **Then** app continues without location and all tabs function in fallback mode

---

### User Story 2 - Lost Pet Tab Without Permission Request (Priority: P2)

The Lost Pet tab should no longer trigger location permission requests. It should use whatever permission status was determined at app launch.

**Why this priority**: Essential to verify the permission logic has been removed from the Lost Pet tab screen.

**Independent Test**: Can be fully tested by launching app, handling permission on main screen, then navigating to Lost Pet tab and verifying no additional permission requests are triggered.

**Acceptance Scenarios**:

1. **Given** user has granted location permission at app launch, **When** user navigates to Lost Pet tab, **Then** no permission dialog is displayed and location data is used
2. **Given** user has denied location permission at app launch, **When** user navigates to Lost Pet tab, **Then** no permission dialog is displayed and tab operates in fallback mode
3. **Given** user dismissed permission dialog at app launch, **When** user navigates to Lost Pet tab, **Then** no permission dialog is displayed

---

### User Story 3 - Existing Permission Behavior Preserved (Priority: P3)

All existing permission behaviors (rationale dialogs, Settings navigation, permission change detection) should continue working. Rationale dialogs remain in feature screens (e.g., AnimalListScreen) where they are contextually relevant - only the initial permission request moves to MainScaffold.

**Why this priority**: Ensures no regression in existing permission UX flows.

**Independent Test**: Can be tested by verifying rationale dialogs, Settings navigation, and permission change detection work from the Lost Pet tab context after initial permission request on main screen.

**Acceptance Scenarios**:

1. **Given** user previously denied permission (shouldShowRationale = true), **When** user navigates to Lost Pet tab, **Then** educational rationale dialog is displayed (Android only)
2. **Given** user denied with "Don't ask again" (shouldShowRationale = false), **When** user navigates to Lost Pet tab, **Then** informational rationale with "Go to Settings" option is displayed
3. **Given** user changes permission in Settings, **When** user returns to app, **Then** app detects the change and updates accordingly

---

### Edge Cases

- What happens when permission is requested but user backgrounded the app before responding? → Permission request remains pending, normal Android/iOS behavior
- How does app behave if location permission was already granted on previous launch? → No permission dialog shown, location is fetched silently when needed
- What happens on Android if permission is requested on MainScaffold but user immediately navigates to Lost Pet tab? → Lost Pet tab uses current permission status, no duplicate request. Note: No race condition is possible because MainScaffold's `LaunchedEffect(Unit)` executes during composition before NavHost renders child screens.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Android app MUST request location permission when MainScaffold (main screen with bottom navigation) is first displayed
- **FR-002**: Android app MUST NOT request location permission when AnimalListScreen (Lost Pet tab) is displayed
- **FR-003**: All existing permission handling logic (rationale dialogs, Settings navigation, permission callbacks) MUST continue to function but be triggered from MainScaffold
- **FR-004**: iOS app permission timing MUST remain unchanged (already at app launch in SceneDelegate)
- **FR-005**: Permission request on MainScaffold MUST use the same Accompanist MultiplePermissionsState pattern currently used in AnimalListScreen
- **FR-006**: Lost Pet tab (AnimalListScreen) MUST check permission status via system APIs (Accompanist/ContextCompat) rather than triggering its own initial permission request
- **FR-007**: Permission status MUST be available to all tabs via system APIs - each tab checks the Android permission system directly (fire-and-forget pattern)
- **FR-008**: Web app permission timing MUST remain unchanged (no changes required)

### Key Entities

- **MainScaffold**: The main Composable containing bottom navigation - new location for initial permission request (fire-and-forget)
- **AnimalListScreen**: Lost Pet tab screen - initial permission request logic to be removed; rationale dialog logic remains
- **AnimalListViewModel**: ViewModel managing Lost Pet list - unchanged; receives permission status from screen's Accompanist state
- **Android Permission System**: Source of truth for permission status - each screen checks via Accompanist/ContextCompat (no app-level shared state needed)

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Location permission dialog appears when main screen is shown, not when navigating to Lost Pet tab
- **SC-002**: All existing permission test scenarios pass (rationale dialogs, Settings navigation, permission change detection)
- **SC-003**: No duplicate permission requests occur when navigating between tabs
- **SC-004**: iOS behavior remains unchanged (existing tests continue to pass)

## Design Deliverables *(optional - no UI changes)*

This feature does not require design deliverables as it only changes the timing of when an existing system dialog is shown. No new UI elements are introduced.

## Assumptions

1. The existing permission handling logic in `AnimalListScreen` is correct and well-tested - only the initial request needs to move
2. Fire-and-forget pattern (matching iOS) is sufficient - no app-level shared state needed since Android system maintains permission status
3. `AnimalListViewModel` does not need refactoring - it already receives permission status from the screen's Accompanist state via intents
4. Rationale dialogs should remain in feature screens where they are contextually relevant (e.g., Lost Pet tab explains why location helps find pets)

## Estimation *(mandatory)*

### Initial Estimate

- **Story Points**: 1
- **Initial Budget**: 1 × 4 × 1.3 = 5.2 days
- **Confidence**: ±30%
- **Anchor Comparison**: Much simpler than Pet Details (3 SP) - this is ~25 lines of code change with no new files

### Re-Estimation (Updated After Each Phase)

| Phase | SP | Days | Confidence | Key Discovery |
|-------|-----|------|------------|---------------|
| Initial | 2 | 10.4 | ±50% | Moving permission logic from screen to scaffold level |
| After SPEC | 2 | 10.4 | ±30% | iOS already correct, Android-only change, well-scoped refactoring |
| After PLAN | 1 | 5.2 | ±20% | Fire-and-forget pattern like iOS - no state sharing, no new ViewModel, ~25 lines changed |
| After TASKS | 1 | 5.2 | ±15% | 19 tasks, ~0.5 days actual implementation |

### Per-Platform Breakdown (After TASKS)

| Platform | Tasks | Days | Notes |
|----------|-------|------|-------|
| Backend | 0 | 0 | No backend changes needed |
| iOS | 0 | 0 | Already at correct timing |
| Android | 19 | ~0.5 | 2 files modified, ~25 lines changed |
| Web | 0 | 0 | No web changes needed |
| **Total** | 19 | **~0.5** | Well within 5.2 day budget |

### Variance Tracking

| Metric | Initial | After PLAN | Variance |
|--------|---------|------------|----------|
| **Story Points** | 2 SP | 1 SP | -50% (simplified approach) |
| **Budget (days)** | 10.4 days | 5.2 days | -50% |

**Variance Reasons**: _To be filled after completion_

**Learning for Future Estimates**: _To be filled after completion_
