# Tasks: iOS Microchip Number Screen

**Feature Branch**: `019-ios-chip-number-screen`  
**Status**: Ready for Implementation  
**Spec**: [spec.md](./spec.md)  
**Plan**: [plan.md](./plan.md)

## Phase 1: Setup
*Goal: Prepare project structure for the new feature components.*

- [X] T001 Create `Helpers` directory in `iosApp/iosApp/Features/ReportMissingPet/`
- [X] T002 Create `Helpers` directory in `iosApp/iosAppTests/Features/ReportMissingPet/`

## Phase 2: Foundational
*Goal: Implement the core business logic for microchip number formatting independently of UI.*
*Independent Test Criteria: Formatter tests pass with 100% success rate.*

- [X] T003 [P] Implement `MicrochipNumberFormatter` in `iosApp/iosApp/Features/ReportMissingPet/Helpers/MicrochipNumberFormatter.swift`
- [X] T004 [P] Implement `MicrochipNumberFormatterTests` in `iosApp/iosAppTests/Features/ReportMissingPet/Helpers/MicrochipNumberFormatterTests.swift`
- [ ] T005 Run unit tests for `MicrochipNumberFormatter` to verify formatting logic

## Phase 3: User Story 1 - Input & Formatting
*Goal: Allow user to enter a microchip number with automatic formatting.*
*Independent Test Criteria: User can type digits and see them formatted as 00000-00000-00000.*

- [X] T006 [US1] Add localization keys to `iosApp/iosApp/Resources/en.lproj/Localizable.strings` (title, placeholder, description, etc.)
- [X] T007 [US1] Expand `ChipNumberViewModel` in `iosApp/iosApp/Features/ReportMissingPet/Views/ChipNumberViewModel.swift` with `chipNumber` property and `formatChipNumber` method
- [X] T008 [US1] Expand `ChipNumberViewModelTests` in `iosApp/iosAppTests/Features/ReportMissingPet/Views/ChipNumberViewModelTests.swift` to test initial state and formatting calls
- [X] T009 [US1] Implement `ChipNumberView` UI in `iosApp/iosApp/Features/ReportMissingPet/Views/ChipNumberView.swift` using `TextField` with `.numberPad`, `onChange` handler, and `accessibilityIdentifier("missingPet.microchip.input")` on the input field
- [ ] T010 [US1] Verify formatting behavior in Simulator (manual test)

## Phase 4: User Story 2, 3 & 4 - State & Navigation
*Goal: Persist data within flow and handle navigation correctly.*
*Independent Test Criteria: Data persists when navigating forward/back; back button dismisses flow.*

- [X] T011 [US2] [US4] Update `ChipNumberViewModel` in `iosApp/iosApp/Features/ReportMissingPet/Views/ChipNumberViewModel.swift` to save digits to `flowState` on continue and restore on init
- [X] T012 [US2] [US4] Add state persistence tests to `ChipNumberViewModelTests` in `iosApp/iosAppTests/Features/ReportMissingPet/Views/ChipNumberViewModelTests.swift`
- [X] T013 [US3] Verify back button action in `ChipNumberView` calls `viewModel.handleBack()` correctly and that the UIKit back button created by `ReportMissingPetCoordinator` uses `accessibilityIdentifier("missingPet.microchip.backButton")`
- [X] T014 [US3] Verify continue button action in `ChipNumberView` calls `viewModel.handleNext()` correctly and that the Continue button in SwiftUI has `accessibilityIdentifier("missingPet.microchip.continueButton")`

## Phase 5: Polish & Cross-Cutting Concerns
*Goal: Ensure quality standards and final polish.*

- [ ] T015 Verify 80% test coverage for `ChipNumberViewModel` and `MicrochipNumberFormatter`
- [ ] T016 Verify UI matches Figma design (spacing, fonts, colors)
- [ ] T017 Run `xcodebuild test` for the specific scheme to ensure no regressions

## Phase 6: E2E Scenarios (Mobile - iOS)
*Goal: Cover microchip number user stories with Java + Cucumber E2E tests (Appium).*
*Independent Test Criteria: Cucumber scenarios for microchip screen pass when run with `@ios` tag.*

- [X] T018 [US1] [US2] [US4] Add or update Cucumber scenarios in `e2e-tests/java/src/test/resources/features/mobile/report-missing-pet.feature` to cover: entering a microchip number, leaving it empty, and resuming flow with previously entered data
- [X] T019 [US1] [US2] [US4] Implement or extend Java step definitions in `e2e-tests/java/src/test/java/.../steps/mobile/` for interacting with the microchip screen (typing, clearing, and verifying formatted value)
- [X] T020 [US1] [US2] [US3] Ensure the mobile Screen Object for the microchip screen in `e2e-tests/java/src/test/java/.../screens/MicrochipNumberScreen.java` exposes locators bound to iOS `accessibilityIdentifier`s for input, Continue button, and back button
- [ ] T021 [US1] [US2] [US3] Run `mvn -f e2e-tests/java/pom.xml test -Dcucumber.filter.tags="@ios and @missing-pet"` and verify all microchip-related scenarios pass

## Dependencies

1. **T001, T002** (Setup) must complete before **T003, T004**.
2. **T003** (Formatter) must complete before **T007** (ViewModel usage).
3. **T006** (Strings) should complete before **T009** (View UI).
4. **T007** (ViewModel Logic) must complete before **T009** (View Binding).
5. **T011** (Persistence) depends on **T007** (ViewModel Property).
6. **T018–T021** (E2E) depend on UI and ViewModel being stable (T007–T014).

## Parallel Execution Examples

- **Formatter & Strings**: T003 (Formatter) and T006 (Strings) can be done in parallel.
- **Tests & Implementation**: T004 (Formatter Tests) can be written in parallel with T003 (Formatter Impl) if interface is agreed.
- **ViewModel & View**: T008 (VM Tests) and T009 (View UI) can be worked on simultaneously once T007 (VM Logic) is stable.

## Implementation Strategy

1. **Core Logic First**: We start with `MicrochipNumberFormatter` because it's a pure function with no dependencies, easy to test and essential for the UI.
2. **ViewModel Second**: We build the presentation logic and state management, ensuring it's testable before attaching a View.
3. **UI Last**: We build the SwiftUI view last, binding it to the robust ViewModel.
4. **Integration**: Finally, we verify the flow state persistence and navigation wiring.

