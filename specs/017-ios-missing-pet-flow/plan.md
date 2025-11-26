# Implementation Plan: Missing Pet Report Flow (iOS)

**Branch**: `017-ios-missing-pet-flow` | **Date**: 2025-11-26 | **Spec**: [spec.md](./spec.md)  
**Input**: Feature specification from `/specs/017-ios-missing-pet-flow/spec.md`

**Note**: This plan implements a modal coordinator with own UINavigationController for iOS ONLY.

## Summary

Implement a modal, multi-step flow for reporting missing pets on iOS. User taps "report missing animal" button on the animal list, which presents a modal UINavigationController managed by a dedicated `ReportMissingPetCoordinator`. The flow consists of:
- 4 data collection screens with progress indicators (1/4, 2/4, 3/4, 4/4)
- 1 summary screen without progress indicator

Each screen has a "next" button to proceed and custom back button for navigation. Flow state is preserved during navigation within session. This is UI-only implementation with no backend integration or data persistence.

**Technical Approach**: 
- New `ReportMissingPetCoordinator` (child coordinator) creates and presents modal `UINavigationController`
- Shared `FlowState` (ObservableObject) passed to all ViewModels
- Each screen follows MVVM-C with SwiftUI views wrapped in `NavigationBackHiding` + `UIHostingController`
- Custom UIKit navigation bar with progress indicator and chevron-left back button
- Parent coordinator (`AnimalListCoordinator`) manages child coordinator lifecycle

## Technical Context

**Language/Version**: Swift 5.9+  
**Primary Dependencies**: UIKit (navigation), SwiftUI (views), Foundation  
**Storage**: N/A (UI-only, no persistence)  
**Testing**: XCTest with Swift Concurrency, 80% coverage target  
**Target Platform**: iOS 15+  
**Project Type**: Mobile (iOS-only)  
**Performance Goals**: < 300ms screen transitions, 60fps animations  
**Constraints**: 
- Modal presentation with dedicated UINavigationController
- State preserved during forward/backward navigation within active session only
- State cleared when exiting flow (dismissing modal)  
**Scale/Scope**: 5 screens (4 data + 1 summary), 1 coordinator, 5 ViewModels, 1 FlowState

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is iOS-only UI feature. Android/Web/Backend checks marked as N/A.

### Platform Architecture Compliance

- [x] **Platform Independence**: iOS implements full stack independently
  - ✅ All code in `/iosApp/iosApp/Features/ReportMissing/`
  - ✅ NO shared compiled code with other platforms
  - Android/Web/Backend: N/A for this feature

- [x] **Android MVI Architecture**: N/A - iOS-only feature

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - ✅ `ReportMissingPetCoordinator` (child coordinator) presented modally with own `UINavigationController`
  - ✅ Child coordinator pattern: `AnimalListCoordinator` creates, presents, and manages lifecycle
  - ✅ Coordinator presents modal: `navigationController.present(modalNavController, animated: true)`
  - ✅ ViewModels conform to `ObservableObject` with `@Published` properties
  - ✅ ViewModels communicate with coordinator via closures (e.g., `onNext`, `onBack`, `onExit`)
  - ✅ SwiftUI views observe ViewModels (no navigation/business logic in views)
  - ✅ `NavigationBackHiding` wrapper used to hide default back button
  - ✅ Custom chevron-left back button in UIKit navigation bar
  - ✅ Progress indicator in UIKit navigation bar (custom view, right side)
  - ✅ Shared `FlowState` (ObservableObject class) owned by coordinator, passed to all ViewModels
  - ✅ Repository protocols use "Protocol" suffix (N/A - no repositories in UI-only feature)
  - ✅ ALL formatting logic in ViewModels/Models per v2.3.0 (no formatting in views)
  - ✅ Colors as hex strings in models, converted to `Color` in views per v2.3.0
  - ✅ SwiftGen for all localized strings per v2.3.0 (`L10n.*`)
  - ✅ Presentation extensions in `/Features/Shared/` if needed per v2.3.0

- [x] **Interface-Based Design**: N/A - UI-only feature with no repositories
  - Feature uses only ViewModels and coordinator communication via closures
  - No repository or service layer needed

- [x] **Dependency Injection**: Manual DI for iOS
  - ✅ Parent coordinator creates `ReportMissingPetCoordinator` via initializer
  - ✅ Coordinator creates `FlowState` object and injects into each ViewModel via initializer
  - ✅ Constructor injection pattern throughout
  - ✅ No ServiceContainer needed (coordinator manages simple dependencies)

- [x] **80% Test Coverage - Platform-Specific**: iOS unit tests planned
  - ✅ Tests in `/iosApp/iosAppTests/Features/ReportMissing/`
  - ✅ Run: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
  - ✅ Coverage target: 80% line + branch
  - ✅ Test scope:
    - All 5 ViewModels (state management, coordinator callbacks, flow state interactions)
    - FlowState object (state persistence, clearing, computed properties)
    - Coordinator may be integration-tested or have minimal unit tests (primarily orchestration)
  - Android/Web/Backend: N/A

- [x] **End-to-End Tests**: E2E tests for iOS flow planned
  - ✅ Appium tests in `/e2e-tests/mobile/specs/017-ios-missing-pet-flow.spec.ts`
  - ✅ TypeScript + WebdriverIO
  - ✅ Screen Object Model pattern
  - ✅ User Story 1: Complete flow navigation (all 5 screens)
  - ✅ User Story 2: Backward navigation, exit from step 1

- [x] **Asynchronous Programming Standards**: Swift Concurrency used
  - ✅ Swift `async`/`await` with `@MainActor` for ViewModels
  - ✅ No Combine, no callback patterns
  - ✅ `@Published` properties for reactive state updates

- [x] **Test Identifiers for UI Controls**: Accessibility identifiers planned
  - ✅ iOS: `.accessibilityIdentifier()` modifier on all interactive views
  - ✅ Naming: `{screen}.{element}.{action}` (e.g., `chipNumber.next.tap`, `chipNumber.back.tap`)
  - ✅ Progress indicator: `{screen}.progress.view` (e.g., `chipNumber.progress.view`)

- [x] **Public API Documentation**: SwiftDoc for non-obvious APIs
  - ✅ Swift: `/// ...` or `/** ... */` format
  - ✅ Documentation concise (1-3 sentences: WHAT/WHY, not HOW)
  - ✅ Document only when purpose not clear from name alone
  - ✅ Skip self-explanatory methods/properties

- [x] **Given-When-Then Test Structure**: All tests follow convention
  - ✅ Unit tests separate setup (Given), action (When), verification (Then)
  - ✅ Test names: `func testMethodName_whenCondition_shouldExpectedBehavior()`
  - ✅ Comments mark phases in complex tests

### Backend Architecture & Quality Standards

- [x] **All Backend Checks**: N/A - iOS UI-only feature, `/server` not affected

## Project Structure

### Documentation (this feature)

```text
specs/017-ios-missing-pet-flow/
├── plan.md              # This file
├── research.md          # Phase 0 output (next)
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
└── tasks.md             # Phase 2 output (separate command)
```

### Source Code (iOS)

```text
iosApp/iosApp/Features/ReportMissing/
├── Coordinators/
│   └── ReportMissingPetCoordinator.swift
├── Models/
│   └── FlowState.swift
├── Views/
│   ├── ChipNumber/
│   │   ├── ChipNumberView.swift
│   │   └── ChipNumberViewModel.swift
│   ├── Photo/
│   │   ├── PhotoView.swift
│   │   └── PhotoViewModel.swift
│   ├── Description/
│   │   ├── DescriptionView.swift
│   │   └── DescriptionViewModel.swift
│   ├── ContactDetails/
│   │   ├── ContactDetailsView.swift
│   │   └── ContactDetailsViewModel.swift
│   └── Summary/
│       ├── SummaryView.swift
│       └── SummaryViewModel.swift

iosApp/iosAppTests/Features/ReportMissing/
├── Models/
│   └── FlowStateTests.swift
└── Views/
    ├── ChipNumberViewModelTests.swift
    ├── PhotoViewModelTests.swift
    ├── DescriptionViewModelTests.swift
    ├── ContactDetailsViewModelTests.swift
    └── SummaryViewModelTests.swift

e2e-tests/mobile/
├── screens/
│   └── ReportMissingPetScreens.ts  (Page Object Model)
├── specs/
│   └── 017-ios-missing-pet-flow.spec.ts
└── steps/
    └── reportMissingPetSteps.ts  (reusable Given/When/Then)
```

**Structure Decision**: iOS feature-based structure with coordinator + models + views grouped by screen. This matches existing patterns from branch 012 (PetDetails feature). Each screen has dedicated directory with View + ViewModel pair. FlowState is shared model owned by coordinator.

## Complexity Tracking

> No violations - all constitution checks passed for iOS-only UI feature.

---

## Phase 0: Research & Unknowns

**Status**: Ready for research.md generation

### Research Topics

1. **Modal UINavigationController Presentation Pattern**
   - Decision needed: How to present modal with own nav controller
   - Investigation: UIKit modal presentation styles, dismiss handling, child coordinator lifecycle

2. **Progress Indicator in Navigation Bar**
   - Decision needed: Custom UIView implementation for "1/4" style indicator
   - Investigation: UIBarButtonItem with custom view, positioning, styling

3. **FlowState Lifecycle Management**
   - Decision needed: When to create/clear flow state
   - Investigation: Coordinator ownership, state persistence during navigation, cleanup on dismiss

4. **Back Button Behavior**
   - Decision needed: Custom back button vs system back button
   - Investigation: UIBarButtonItem customization, NavigationBackHiding wrapper, coordinator communication

5. **Photo Picker Integration**
   - Decision needed: iOS photo picker API
   - Investigation: PHPickerViewController vs UIImagePickerController, permissions, image handling

### Questions to Resolve

- Q: Should progress indicator animate when changing steps?
- Q: Should flow state persist if app backgrounds during flow?
- Q: How to handle device rotation during flow?
- Q: Should "next" button be disabled for invalid inputs?
- Q: How to style progress indicator badge (size, colors, font)?

**Output**: research.md with decisions, rationale, code samples

---

## Phase 1: Design & Contracts

**Status**: Pending (after research.md)

### Data Model Design

Will define in `data-model.md`:
- `FlowState` class (ObservableObject)
  - Properties for chip number, photo, description, contact details
  - Computed properties for validation state
  - Methods: `clear()`, validation helpers
- ViewModel state structures (if using enum-based state)
- Navigation event enums (if needed)

### API Contracts

N/A - UI-only feature, no backend integration

### Quickstart

Will create `quickstart.md` with:
- How to trigger flow from animal list
- How to add new step to flow
- How to test ViewModels
- How to run E2E tests

**Output**: data-model.md, quickstart.md

---

## Next Steps

1. ✅ Constitution Check complete
2. ⏳ Generate research.md (Phase 0)
3. ⏳ Generate data-model.md (Phase 1)
4. ⏳ Generate quickstart.md (Phase 1)
5. ⏳ Update agent context
6. ⏳ Generate tasks.md (separate command: `/speckit.tasks`)
