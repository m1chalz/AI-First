# Implementation Plan: iOS Owner's Details Screen

**Branch**: `035-ios-owners-details-screen` | **Date**: 2025-12-02 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/035-ios-owners-details-screen/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Implement iOS Owner's Details screen (Step 4/4) in the Missing Pet flow by modifying existing placeholder files `ContactDetailsView` and `ContactDetailsViewModel`. The screen collects mandatory contact information (phone, email) and optional reward description, validates inputs on Continue tap, delegates to `AnnouncementSubmissionService` for 2-step backend submission (announcement creation + photo upload via extended `AnimalRepository`), and navigates to summary with managementPassword on success. Implementation focuses on SwiftUI UI, validation logic in ViewModel, business logic in Service layer, error handling with retry, and Polish/English localization using SwiftGen L10n.

## Technical Context

**Language/Version**: Swift 5.9+ (Xcode 15+)
**Primary Dependencies**: SwiftUI, UIKit (coordinators), SwiftGen (localization), URLSession (HTTP client)
**Storage**: In-memory session state (ReportMissingPetFlowState), PhotoAttachmentCache (disk cache for photo files)
**Testing**: XCTest with Swift Concurrency (async/await), 80% coverage target for ViewModels
**Target Platform**: iOS 15+
**Project Type**: Mobile (iOS) - modifying existing placeholder files in `/iosApp/iosApp/Features/ReportMissingPet/ContactDetails/`
**Performance Goals**: N/A (standard iOS app performance)
**Constraints**: Must support offline detection, maintain input state during submission, disable back navigation during 2-step submission
**Scale/Scope**: Single screen implementation (Step 4/4), modifying 2 existing placeholder files (ContactDetailsView, ContactDetailsViewModel) + extending AnimalRepository with announcement methods + creating AnnouncementSubmissionService + adding localization strings

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

**Pre-Design Check**: ✅ PASSED (2025-12-02)  
**Post-Design Check**: ✅ PASSED (2025-12-02)

> **Note**: This is an iOS-only feature (modifying ContactDetailsView/ContactDetailsViewModel), so Android MVI checks are marked N/A. Backend is not affected, so backend checks are N/A.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: N/A (iOS-only feature)
  - iOS: Implementation in `/iosApp/iosApp/Features/ReportMissingPet/ContactDetails/` (modifying existing placeholders)
  - Web: N/A (iOS-only feature)
  - Backend: N/A (backend API already exists per spec 009 and 021)
  - NO shared compiled code between platforms
  - Violation justification: _N/A - compliant_

- [x] **Android MVI Architecture**: N/A (iOS-only feature)
  - Violation justification: _N/A - not applicable to iOS implementation_

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - UIKit-based coordinators manage navigation and create `UIHostingController` instances (already implemented in ReportMissingPetCoordinator per spec 017)
  - ViewModels conform to `ObservableObject` with `@Published` properties (ContactDetailsViewModel will have @Published phone, email, rewardDescription, phoneError, emailError, isSubmitting, alertMessage, showAlert)
  - ViewModels communicate with coordinators via closures (onReportSent: (String) -> Void already present in placeholder)
  - SwiftUI views observe ViewModels (ContactDetailsView will observe ContactDetailsViewModel, no business logic in view)
  - SwiftUI views wrapped in NavigationBackHiding then UIHostingController (already implemented by coordinator)
  - **Service layer for business logic**: ContactDetailsViewModel calls AnnouncementSubmissionService (NOT repository directly). Service encapsulates 2-step submission logic and calls AnimalRepository.
  - Manual DI with constructor injection (ViewModel receives service, service receives repository via initializer)
  - SwiftGen for localization (all strings use L10n.tr() with owners_details.* keys)
  - Presentation model extensions in Features/Shared/ (not needed for this simple form)
  - Colors as hex strings in models, converted to Color in views (not needed - using design system colors)
  - Data formatting in ViewModels/Models (validation logic in ViewModel, views only display)
  - Repository protocols use "Protocol" suffix (AnimalRepositoryProtocol - extended with announcement methods)
  - Violation justification: _N/A - compliant with MVVM-C requirements_

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - Android: N/A (iOS-only feature)
  - iOS: Will extend existing AnimalRepositoryProtocol (with "Protocol" suffix per constitution) in `/iosApp/iosApp/Domain/Repositories/`, implementation in AnimalRepository (already exists in `/iosApp/iosApp/Data/Repositories/`)
  - Web: N/A (iOS-only feature)
  - Backend: N/A (backend already exists)
  - Service layer will reference protocol, not concrete implementation
  - Violation justification: _N/A - compliant_

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - Android: N/A (iOS-only feature)
  - iOS: MUST use manual DI - Three-layer injection:
    - AnimalRepository receives HTTPClient via initializer (already exists)
    - AnnouncementSubmissionService receives AnimalRepositoryProtocol via initializer
    - ContactDetailsViewModel receives AnnouncementSubmissionService via initializer
    - Coordinator injects service (from ServiceContainer) into ViewModel during initialization
  - Web: N/A (iOS-only feature)
  - Backend: N/A (backend not affected)
  - Violation justification: _N/A - compliant with manual DI requirement_

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - Android: N/A (iOS-only feature)
  - iOS: Tests in `/iosApp/iosAppTests/Features/ReportMissingPet/ContactDetails/`, run via XCTest with coverage enabled, coverage target 80% line + branch
    - Test scope: ContactDetailsViewModel validation logic (phone, email), submission flow (2-step: announcement + photo), error handling, retry logic, loading states
  - Web: N/A (iOS-only feature)
  - Backend: N/A (backend not affected)
  - Coverage target: 80% line + branch coverage for iOS ViewModels
  - Violation justification: _N/A - compliant_

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Web: N/A (iOS-only feature)
  - Mobile: E2E tests for iOS Owner's Details screen will be added in `/e2e-tests/mobile/specs/owners-details.spec.ts` (Appium + TypeScript + Cucumber with @ios tag)
  - Screen Object Model in `/e2e-tests/mobile/screens/ContactDetailsScreen.ts` with accessibilityIdentifiers
  - Each user story from spec (US1-US4) has E2E test coverage
  - Violation justification: _N/A - E2E tests planned_

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - Android: N/A (iOS-only feature)
  - iOS: Swift Concurrency (`async`/`await`) with `@MainActor` for ViewModel - submitForm() method will use async/await for repository calls
  - Web: N/A (iOS-only feature)
  - Backend: N/A (backend not affected)
  - No Combine, RxJava, RxSwift, or callback-based patterns for new code
  - Violation justification: _N/A - compliant with Swift Concurrency requirement_

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - Android: N/A (iOS-only feature)
  - iOS: `.accessibilityIdentifier()` modifier on all interactive views
    - ownersDetails.backButton
    - ownersDetails.phoneInput
    - ownersDetails.emailInput
    - ownersDetails.rewardInput
    - ownersDetails.continueButton
    - ownersDetails.progressBadge
    - ownersDetails.title
    - ownersDetails.subtitle
  - Web: N/A (iOS-only feature)
  - Naming convention: `{screen}.{element}` (e.g., `ownersDetails.continueButton`) - iOS uses camelCase without action suffix
  - Violation justification: _N/A - compliant_

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Kotlin: N/A (iOS-only feature)
  - Swift: SwiftDoc format (`/// ...`) for ContactDetailsViewModel public methods
    - Document submitForm() method (complex 2-step submission logic)
    - Document validation computed properties (isPhoneValid, isEmailValid) if logic is non-obvious
    - Skip documentation for simple @Published properties (phone, email, rewardDescription) - names are self-explanatory
  - TypeScript: N/A (iOS-only feature)
  - Documentation must be concise and high-level (1-3 sentences: WHAT/WHY, not HOW)
  - Violation justification: _N/A - compliant_

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Unit tests clearly separate setup (Given), action (When), verification (Then)
  - ViewModel tests use Given-When-Then pattern with descriptive names (Swift convention: testLoadPets_whenRepositorySucceeds_shouldUpdatePetsState)
  - E2E tests structure scenarios with Given-When-Then phases (Cucumber Gherkin format)
  - Test names follow Swift convention: camelCase_with_underscores (e.g., testSubmitForm_whenPhoneInvalid_shouldShowValidationError)
  - Comments mark test phases in complex tests (// Given, // When, // Then)
  - Violation justification: _N/A - compliant_

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: N/A (backend not affected - using existing APIs from spec 009 and 021)
  - Violation justification: _N/A - backend already exists and is not modified in this spec_

- [x] **Backend Code Quality**: N/A (backend not affected)
  - Violation justification: _N/A - backend not modified_

- [x] **Backend Dependency Management**: N/A (backend not affected)
  - Violation justification: _N/A - backend not modified_

- [x] **Backend Directory Structure**: N/A (backend not affected)
  - Violation justification: _N/A - backend not modified_

- [x] **Backend TDD Workflow**: N/A (backend not affected)
  - Violation justification: _N/A - backend not modified_

- [x] **Backend Testing Strategy**: N/A (backend not affected)
  - Violation justification: _N/A - backend not modified_

## Project Structure

### Documentation (this feature)

```text
specs/035-ios-owners-details-screen/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command) - references to existing spec 009/021 contracts
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
iosApp/iosApp/
├── Features/
│   └── ReportMissingPet/
│       └── ContactDetails/
│           ├── Views/
│           │   ├── ContactDetailsView.swift           # MODIFY - SwiftUI view (placeholder → full implementation)
│           │   └── ContactDetailsViewModel.swift      # MODIFY - ViewModel (placeholder → full implementation)
│           └── (Navigation already handled by ReportMissingPetCoordinator from spec 017)
├── Domain/
│   ├── Services/
│   │   └── AnnouncementSubmissionService.swift        # CREATE - business logic for 2-step submission
│   └── Repositories/
│       └── AnimalRepositoryProtocol.swift             # MODIFY - ADD methods: createAnnouncement, uploadPhoto
├── Data/
│   └── Repositories/
│       └── AnimalRepository.swift                     # MODIFY - IMPLEMENT new methods (already exists)
└── Resources/
    └── en.lproj/
        └── Localizable.strings                        # ADD - localization strings for owners_details.* keys
    └── pl.lproj/
        └── Localizable.strings                        # ADD - Polish translations

iosAppTests/
└── Features/
    └── ReportMissingPet/
        └── ContactDetails/
            ├── ContactDetailsViewModelTests.swift     # CREATE - unit tests for ViewModel (80% coverage)
            ├── AnnouncementSubmissionServiceTests.swift # CREATE - unit tests for Service (80% coverage)
            └── Fakes/
                ├── FakeAnimalRepository.swift         # MODIFY/CREATE - extend existing fake or create new with announcement methods
                └── FakeAnnouncementSubmissionService.swift # CREATE - fake service for ViewModel tests

e2e-tests/
├── mobile/
│   ├── specs/
│   │   └── owners-details.spec.ts                    # CREATE - E2E tests for iOS Owner's Details (Appium + Cucumber @ios)
│   └── screens/
│       └── ContactDetailsScreen.ts                   # CREATE - Screen Object Model with accessibilityIdentifiers
```

**Structure Decision**: iOS mobile application (Option 3 from template). This feature modifies existing placeholder files (`ContactDetailsView.swift`, `ContactDetailsViewModel.swift`) in `/iosApp/iosApp/Features/ReportMissingPet/ContactDetails/Views/` and adds localization strings, tests, and potentially repository protocol/implementation if they don't exist. Navigation integration with `ReportMissingPetCoordinator` is already implemented per spec 017.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

N/A - No violations. All Constitution Check items are compliant or not applicable to this iOS-only feature.
