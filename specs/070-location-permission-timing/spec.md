# Feature Specification: Location Permission Request Timing

**Feature Branch**: `070-location-permission-timing`  
**Created**: 2026-01-08  
**Status**: Draft  
**Jira Ticket**: _Pending sync_  
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

All existing permission behaviors (rationale dialogs, Settings navigation, permission change detection) should continue working but be triggered from the main screen instead of the Lost Pet tab.

**Why this priority**: Ensures no regression in existing permission UX flows.

**Independent Test**: Can be tested by verifying rationale dialogs, Settings navigation, and permission change detection work from the main screen context.

**Acceptance Scenarios**:

1. **Given** user previously denied permission (shouldShowRationale = true), **When** main screen appears, **Then** educational rationale dialog is displayed (Android only)
2. **Given** user denied with "Don't ask again" (shouldShowRationale = false), **When** main screen appears, **Then** informational rationale with "Go to Settings" option is displayed
3. **Given** user changes permission in Settings, **When** user returns to app, **Then** app detects the change and updates accordingly

---

### Edge Cases

- What happens when permission is requested but user backgrounded the app before responding? → Permission request remains pending, normal Android/iOS behavior
- How does app behave if location permission was already granted on previous launch? → No permission dialog shown, location is fetched silently when needed
- What happens on Android if permission is requested on MainScaffold but user immediately navigates to Lost Pet tab? → Lost Pet tab uses current permission status, no duplicate request

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Android app MUST request location permission when MainScaffold (main screen with bottom navigation) is first displayed
- **FR-002**: Android app MUST NOT request location permission when AnimalListScreen (Lost Pet tab) is displayed
- **FR-003**: All existing permission handling logic (rationale dialogs, Settings navigation, permission callbacks) MUST continue to function but be triggered from MainScaffold
- **FR-004**: iOS app permission timing MUST remain unchanged (already at app launch in SceneDelegate)
- **FR-005**: Permission request on MainScaffold MUST use the same Accompanist MultiplePermissionsState pattern currently used in AnimalListScreen
- **FR-006**: Lost Pet tab (AnimalListScreen) MUST receive permission status from a shared source rather than managing its own permission state
- **FR-007**: Permission status MUST be available to all tabs that need location data (Lost Pet, Home, Found Pet)
- **FR-008**: Web app permission timing MUST remain unchanged (no changes required)

### Key Entities

- **MainScaffold**: The main Composable containing bottom navigation - new location for permission request
- **AnimalListScreen**: Lost Pet tab screen - permission request logic to be removed
- **AnimalListViewModel**: ViewModel managing Lost Pet list - will receive permission status instead of requesting it
- **Location Permission Status**: Shared state accessible by all feature screens

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Location permission dialog appears when main screen is shown, not when navigating to Lost Pet tab
- **SC-002**: All existing permission test scenarios pass (rationale dialogs, Settings navigation, permission change detection)
- **SC-003**: No duplicate permission requests occur when navigating between tabs
- **SC-004**: iOS behavior remains unchanged (existing tests continue to pass)

## Design Deliverables *(optional - no UI changes)*

This feature does not require design deliverables as it only changes the timing of when an existing system dialog is shown. No new UI elements are introduced.

## Assumptions

1. The existing permission handling logic in `AnimalListScreen` is correct and well-tested - it just needs to be moved
2. A shared permission state can be hoisted to `MainScaffold` level without breaking existing screen functionality
3. `AnimalListViewModel` can be refactored to accept permission status as input rather than managing permission flow
4. The permission-related rationale dialogs should appear at app launch, not be deferred to tab navigation

## Estimation *(mandatory)*

### Initial Estimate

- **Story Points**: 2
- **Initial Budget**: 2 × 4 × 1.3 = 10.4 days
- **Confidence**: ±50%
- **Anchor Comparison**: Simpler than Pet Details (3 SP) - this is primarily moving existing code and adjusting the flow, no new features or complex logic

### Re-Estimation (Updated After Each Phase)

| Phase | SP | Days | Confidence | Key Discovery |
|-------|-----|------|------------|---------------|
| Initial | 2 | 10.4 | ±50% | Moving permission logic from screen to scaffold level |
| After SPEC | 2 | 10.4 | ±30% | iOS already correct, Android-only change, well-scoped refactoring |
| After PLAN | — | — | ±20% | [Update when plan.md complete] |
| After TASKS | — | — | ±15% | [Update when tasks.md complete] |

### Per-Platform Breakdown (After TASKS)

| Platform | Tasks | Days | Notes |
|----------|-------|------|-------|
| Backend | 0 | 0 | No backend changes needed |
| iOS | 0 | 0 | Already at correct timing |
| Android | — | — | [Fill after tasks.md] |
| Web | 0 | 0 | No web changes needed |
| **Total** | | **—** | |

### Variance Tracking

| Metric | Initial | Final | Variance |
|--------|---------|-------|----------|
| **Story Points** | 2 SP | — | — |
| **Budget (days)** | 10.4 days | — | — |

**Variance Reasons**: _To be filled after completion_

**Learning for Future Estimates**: _To be filled after completion_
