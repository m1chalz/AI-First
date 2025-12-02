---

description: "Task list for iOS Owner's Details Screen implementation"
---

# Tasks: iOS Owner's Details Screen

**Input**: Design documents from `/specs/035-ios-owners-details-screen/`
**Prerequisites**: plan.md, spec.md, data-model.md, contracts/api-contracts.md, research.md, quickstart.md

**Tests**: Test requirements for this project:

**MANDATORY - iOS Unit Tests**:
- Location: `/iosApp/iosAppTests/Features/ReportMissingPet/ContactDetails/`
- Framework: XCTest with Swift Concurrency (async/await)
- Coverage: 80% for ViewModels and Service layer
- Scope: ContactDetailsViewModel validation logic (phone, email), submission flow (2-step: announcement + photo), error handling, retry logic, loading states, AnnouncementSubmissionService orchestration
- Run: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES`
- Convention: MUST follow Given-When-Then structure with descriptive names (Swift convention: `testLoadPets_whenRepositorySucceeds_shouldUpdatePetsState`)

**MANDATORY - End-to-End Tests**:
- Mobile: `/e2e-tests/mobile/specs/owners-details.spec.ts` (Appium + TypeScript + Cucumber with @ios tag)
- Screen Object Model in `/e2e-tests/mobile/screens/ContactDetailsScreen.ts` with accessibilityIdentifiers
- All user stories from spec.md (US1-US4) have E2E test coverage
- Convention: MUST structure scenarios with Given-When-Then phases

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3, US4)
- Include exact file paths in descriptions

## Path Conventions

- iOS: `/iosApp/iosApp/` for source, `/iosAppTests/` for tests
- E2E: `/e2e-tests/mobile/` for mobile tests

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and localization setup

- [ ] T001 [P] Add English localization strings to `/iosApp/iosApp/Resources/en.lproj/Localizable.strings` (owners_details.* keys: screen_title, subtitle, phone.label, phone.placeholder, phone.error, email.label, email.placeholder, email.error, reward.label, reward.placeholder, continue.button, back.button, error.no_connection.title, error.no_connection.message, error.generic.title, error.generic.message, alert.try_again, alert.cancel)
- [ ] T002 [P] Add Polish localization strings to `/iosApp/iosApp/Resources/pl.lproj/Localizable.strings` (same keys as T001 with Polish translations per quickstart.md)
- [ ] T003 Run SwiftGen to generate L10n enum from Localizable.strings files (execute `swiftgen` in `/iosApp/` directory)

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

**Data Layer (DTOs for HTTP/API)**:
- [ ] T004 [P] Create CreateAnnouncementRequestDTO struct in `/iosApp/iosApp/Data/Models/CreateAnnouncementRequestDTO.swift` (Codable with CodingKeys for snake_case JSON mapping: species, sex, lastSeenDate, locationLatitude, locationLongitude, email, phone, status, microchipNumber, description, reward - used for POST /api/v1/announcements JSON body)
- [ ] T005 [P] Create AnnouncementResponseDTO struct in `/iosApp/iosApp/Data/Models/AnnouncementResponseDTO.swift` (Codable with CodingKeys for snake_case: id, managementPassword, species, sex, lastSeenDate, locationLatitude, locationLongitude, email, phone, status, microchipNumber, description, reward, photoUrl - used for HTTP 201 response parsing)
- [ ] T006 [P] Create PhotoUploadRequestDTO struct in `/iosApp/iosApp/Data/Models/PhotoUploadRequestDTO.swift` (contains announcementId: String, photo: PhotoAttachmentMetadata, managementPassword: String - internal DTO for uploadPhoto implementation)

**Domain Layer (Pure business models)**:
- [ ] T007 [P] Create AnnouncementResult struct in `/iosApp/iosApp/Domain/Models/AnnouncementResult.swift` (pure domain model with id: String, managementPassword: String - no Codable, no snake_case, business data only)
- [ ] T008 [P] Create CreateAnnouncementData struct in `/iosApp/iosApp/Domain/Models/CreateAnnouncementData.swift` (pure domain model for announcement creation: species: String, sex: String, lastSeenDate: Date, location: (latitude: Double, longitude: Double), contact: (email: String, phone: String), microchipNumber: String?, description: String?, reward: String? - used by Service to pass data to Repository)

**Mappers (DTO ↔ Domain conversion)**:
- [ ] T009 [P] Create AnnouncementMapper in `/iosApp/iosApp/Data/Mappers/AnnouncementMapper.swift` (static methods: toDomain(_ dto: AnnouncementResponseDTO) -> AnnouncementResult converts DTO to domain model; toDTO(_ data: CreateAnnouncementData) -> CreateAnnouncementRequestDTO converts domain to DTO)

**Repository Protocol & Implementation**:
- [ ] T010 Extend AnimalRepositoryProtocol in `/iosApp/iosApp/Domain/Repositories/AnimalRepositoryProtocol.swift` with new methods using pure domain models: createAnnouncement(data: CreateAnnouncementData) async throws -> AnnouncementResult, uploadPhoto(announcementId: String, photo: PhotoAttachmentMetadata, managementPassword: String) async throws
- [ ] T011 Implement createAnnouncement method in AnimalRepository at `/iosApp/iosApp/Data/Repositories/AnimalRepository.swift` (convert CreateAnnouncementData to CreateAnnouncementRequestDTO using AnnouncementMapper, POST /api/v1/announcements with JSON body, parse HTTP 201 response to AnnouncementResponseDTO, convert DTO to AnnouncementResult using AnnouncementMapper, return domain model)
- [ ] T012 Implement uploadPhoto method in AnimalRepository at `/iosApp/iosApp/Data/Repositories/AnimalRepository.swift` (POST /api/v1/announcements/:id/photos with multipart form-data, Basic auth header with base64(announcementId:managementPassword), load photo Data from PhotoAttachmentMetadata.cachedURL, return Void on success)

**Service Layer (Business logic orchestration)**:
- [ ] T013 Create AnnouncementSubmissionService in `/iosApp/iosApp/Domain/Services/AnnouncementSubmissionService.swift` (receives AnimalRepositoryProtocol in constructor, submitAnnouncement(flowState:) async throws -> String orchestrates 2-step submission: builds CreateAnnouncementData from FlowState, calls repository.createAnnouncement, receives AnnouncementResult, calls repository.uploadPhoto with id and managementPassword, returns managementPassword String)
- [ ] T014 Update ServiceContainer in `/iosApp/iosApp/DI/ServiceContainer.swift` to add announcementSubmissionService lazy property (inject animalRepository into service constructor)

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Enter contact information to finalize report (Priority: P1) 🎯 MVP

**Goal**: Users complete Steps 1-3 (chip, photo, description) and reach Owner's Details screen. They provide valid phone and email before submitting. Once both fields are valid, tapping Continue executes 2-step submission: (1) creates announcement and receives managementPassword, (2) uploads photo using managementPassword. After both succeed, user navigates to summary screen showing managementPassword.

**Independent Test**: Deep link to Step 4 with prior steps pre-populated, fill phone and email with valid values, tap Continue, verify 2-step backend submission (announcement + photo) and navigation to summary with managementPassword.

### Tests for User Story 1 (MANDATORY) ✅

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

**iOS Unit Tests**:
- [ ] T015 [P] [US1] Create FakeAnimalRepository in `/iosAppTests/Fakes/FakeAnimalRepository.swift` (extend existing fake or create new with announcement methods: createAnnouncement returns mock AnnouncementResult with id and managementPassword, uploadPhoto succeeds or throws error based on test scenario)
- [ ] T016 [P] [US1] Create FakeAnnouncementSubmissionService in `/iosAppTests/Fakes/FakeAnnouncementSubmissionService.swift` (mock service with submitAnnouncement method that returns mock managementPassword or throws error based on test scenario)
- [ ] T017 [P] [US1] Unit test for AnnouncementSubmissionService in `/iosAppTests/Features/ReportMissingPet/ContactDetails/AnnouncementSubmissionServiceTests.swift` (test builds CreateAnnouncementData from FlowState, test submitAnnouncement orchestrates 2-step flow, test returns managementPassword on success, test throws error on step 1 failure, test throws error on step 2 failure, test photo upload skipped when photoAttachment is nil)
- [ ] T018 [P] [US1] Unit test for ContactDetailsViewModel submission flow in `/iosAppTests/Features/ReportMissingPet/ContactDetails/ContactDetailsViewModelTests.swift` (test submitForm calls service with FlowState, test onReportSent closure invoked with managementPassword on success, test isSubmitting = true during submission, test isSubmitting = false after success)

**End-to-End Tests**:
- [ ] T019 [P] [US1] Create ContactDetailsScreen object in `/e2e-tests/mobile/screens/ContactDetailsScreen.ts` (Screen Object Model with methods: enterPhone, enterEmail, enterReward, tapContinue, getTitle, getPhoneError, getEmailError, isSubmitting, using accessibilityIdentifiers: ownersDetails.phoneInput, ownersDetails.emailInput, ownersDetails.rewardInput, ownersDetails.continueButton, ownersDetails.title)
- [ ] T020 [US1] Create E2E test spec in `/e2e-tests/mobile/specs/owners-details.spec.ts` (Given-When-Then scenarios for US1: Scenario 1 - valid phone and email, tap Continue, verify 2-step submission and navigation to summary with managementPassword, emit analytics event; Scenario 2 - navigate back from summary, verify phone and email persist, progress badge returns to "4/4")

### Implementation for User Story 1

**iOS** (Full Stack Implementation - NO use cases, ViewModels call services directly):
- [ ] T021 [P] [US1] Create OwnerContactDetails struct in `/iosApp/iosApp/Domain/Models/OwnerContactDetails.swift` or extend ReportMissingPetFlowState (struct with phone: String, email: String, rewardDescription: String?)
- [ ] T022 [US1] Modify ContactDetailsViewModel in `/iosApp/iosApp/Features/ReportMissingPet/ContactDetails/Views/ContactDetailsViewModel.swift` (replace placeholder with full implementation: @Published properties for phone, email, rewardDescription, phoneError, emailError, isSubmitting, alertMessage, showAlert; init with AnnouncementSubmissionService and ReportMissingPetFlowState via manual DI; computed properties isPhoneValid, isEmailValid; submitForm() async method delegates to service, sets isSubmitting = true during submission, invokes onReportSent(managementPassword) on success, calls handleSubmissionError on failure)
- [ ] T023 [US1] Modify ContactDetailsView in `/iosApp/iosApp/Features/ReportMissingPet/ContactDetails/Views/ContactDetailsView.swift` (replace placeholder with SwiftUI UI: VStack with title, subtitle, three ValidatedTextField components for phone/email/reward, Continue button with spinner when isSubmitting, alert popup for errors, all text uses L10n.tr() with owners_details.* keys)
- [ ] T024 [US1] Add accessibilityIdentifier to all interactive views in ContactDetailsView (ownersDetails.phoneInput, ownersDetails.emailInput, ownersDetails.rewardInput, ownersDetails.continueButton, ownersDetails.title, ownersDetails.subtitle, ownersDetails.backButton, ownersDetails.progressBadge)
- [ ] T025 [US1] Update ReportMissingPetCoordinator in `/iosApp/iosApp/Coordinators/ReportMissingPetCoordinator.swift` to inject AnnouncementSubmissionService from ServiceContainer into ContactDetailsViewModel during showContactDetails() method, set onReportSent closure to capture managementPassword and call showSummary(managementPassword:)
- [ ] T026 [P] [US1] Add SwiftDoc documentation to ContactDetailsViewModel.submitForm() method (explain 2-step submission logic: WHAT/WHY, not HOW, 1-3 sentences)
- [ ] T027 [P] [US1] Add SwiftDoc documentation to AnnouncementSubmissionService.submitAnnouncement() method (explain orchestration of announcement creation + photo upload)

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently - user can submit valid contact info and navigate to summary

---

## Phase 4: User Story 2 - Receive inline validation feedback for invalid inputs (Priority: P2)

**Goal**: Users who mistype contact details see immediate inline validation errors (red border + error text) when they tap Continue, and the Continue button remains disabled until both fields are valid. This prevents submission of malformed contact info.

**Independent Test**: Enter invalid email (e.g., "owner@"), tap Continue, observe inline validation error appears, correct email to valid format, tap Continue again, verify submission succeeds.

### Tests for User Story 2 (MANDATORY) ✅

**iOS Unit Tests**:
- [ ] T028 [P] [US2] Unit test for phone validation in ContactDetailsViewModelTests (test isPhoneValid = true for 7-11 digits, test isPhoneValid = false for < 7 digits, test accepts leading "+", test sanitizes spaces/dashes, test phoneError set when submitForm() called with invalid phone, test phoneError = nil when valid)
- [ ] T029 [P] [US2] Unit test for email validation in ContactDetailsViewModelTests (test isEmailValid = true for local@domain.tld, test isEmailValid = false for missing @, test case-insensitive, test trims whitespace, test emailError set when submitForm() called with invalid email, test emailError = nil when valid)

**End-to-End Tests**:
- [ ] T030 [US2] Add E2E test scenarios for US2 in `/e2e-tests/mobile/specs/owners-details.spec.ts` (Scenario 1 - enter invalid phone "123", tap Continue, verify red border and error text "Enter at least 7 digits", correct to valid phone, tap Continue, verify submission succeeds; Scenario 2 - enter invalid email "owner@", tap Continue, verify red border and error text "Enter a valid email address", correct to valid email, tap Continue, verify submission succeeds)

### Implementation for User Story 2

**iOS**:
- [ ] T031 [US2] Add validation logic to ContactDetailsViewModel.submitForm() method (before submission: check isPhoneValid, set phoneError = L10n.tr("owners_details.phone.error") if invalid, return early; check isEmailValid, set emailError = L10n.tr("owners_details.email.error") if invalid, return early; clear errors before validation)
- [ ] T032 [US2] Add computed properties isPhoneValid and isEmailValid to ContactDetailsViewModel (isPhoneValid: sanitize phone to digits and "+", count digits, return true if 7-11 digits; isEmailValid: use regex or NSPredicate to validate RFC 5322 basic format local@domain.tld)
- [ ] T033 [US2] Wire phoneError and emailError @Published properties to ValidatedTextField components in ContactDetailsView (ValidatedTextField displays red border and error text when error property is non-nil)

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently - validation prevents invalid submissions and guides users to correct inputs

---

## Phase 5: User Story 3 - Optionally add reward description with character limit (Priority: P3)

**Goal**: Some users want to offer a reward. They can enter free-text reward details (e.g., "$250 gift card + hugs") up to 120 characters. The field is optional and does not block Continue if left empty.

**Independent Test**: Enter "$250 gift card + hugs" in reward field, navigate back to Step 3, return to Step 4, confirm text persists and is editable.

### Tests for User Story 3 (MANDATORY) ✅

**iOS Unit Tests**:
- [ ] T034 [P] [US3] Unit test for reward persistence in ContactDetailsViewModelTests (test rewardDescription persists in FlowState.contactDetails when submitForm() succeeds, test rewardDescription = nil when field is empty, test rewardDescription stores text verbatim when non-empty)

**End-to-End Tests**:
- [ ] T035 [US3] Add E2E test scenarios for US3 in `/e2e-tests/mobile/specs/owners-details.spec.ts` (Scenario 1 - enter "$250 gift card + hugs" in reward, navigate away and back, verify text persists and can proceed with Continue if phone/email valid; Scenario 2 - enter 120 characters in reward, try to type 121st character, verify ValidatedTextField maxLength enforces limit and rejects additional character)

### Implementation for User Story 3

**iOS**:
- [ ] T036 [US3] Add ValidatedTextField for reward in ContactDetailsView with maxLength: 120 parameter (label: L10n.tr("owners_details.reward.label"), placeholder: L10n.tr("owners_details.reward.placeholder"), text: $viewModel.rewardDescription, error: nil, maxLength: 120)
- [ ] T037 [US3] Add rewardDescription @Published property to ContactDetailsViewModel (initialized from FlowState.contactDetails?.rewardDescription ?? "", saved to FlowState.contactDetails in submitForm() - store nil if empty, store text verbatim if non-empty)
- [ ] T038 [US3] Add accessibilityIdentifier "ownersDetails.rewardInput" to reward ValidatedTextField in ContactDetailsView

**Checkpoint**: All user stories (US1, US2, US3) should now be independently functional - users can optionally add reward without blocking submission

---

## Phase 6: User Story 4 - Handle submission failure gracefully (Priority: P2)

**Goal**: When either step of submission fails (offline, backend error, timeout), the app stays on Step 4, shows a popup alert with error message and "Try Again" / "Cancel" buttons, keeps all inputs intact, and allows user to retry full 2-step submission when ready.

**Independent Test**: Disable network, fill valid phone/email, tap Continue, observe popup alert with Try Again/Cancel buttons and no navigation, tap Cancel, re-enable network, tap Continue again, verify full 2-step submission succeeds.

### Tests for User Story 4 (MANDATORY) ✅

**iOS Unit Tests**:
- [ ] T039 [P] [US4] Unit test for network error handling in ContactDetailsViewModelTests (test submitForm() with service throwing URLError.notConnectedToInternet, verify isSubmitting = false, verify showAlert = true, verify alertMessage = L10n.tr("owners_details.error.no_connection.message"), verify inputs remain intact, verify validation errors remain cleared)
- [ ] T040 [P] [US4] Unit test for backend error handling in ContactDetailsViewModelTests (test submitForm() with service throwing generic error, verify isSubmitting = false, verify showAlert = true, verify alertMessage = L10n.tr("owners_details.error.generic.message"), verify inputs remain intact)
- [ ] T041 [P] [US4] Unit test for retry logic in ContactDetailsViewModelTests (test submitForm() called again after failure, verify isSubmitting = true again, verify full 2-step submission retried from step 1)

**End-to-End Tests**:
- [ ] T042 [US4] Add E2E test scenarios for US4 in `/e2e-tests/mobile/specs/owners-details.spec.ts` (Scenario 1 - disable network, enter valid phone/email, tap Continue, verify popup alert "No connection. Please check your network and try again." with "Try Again" and "Cancel" buttons, tap "Try Again", verify retry attempts submission, tap "Cancel", verify alert dismissed and inputs intact; Scenario 2 - simulate backend error, tap Continue, verify popup alert "Something went wrong. Please try again later.", tap "Try Again", verify full 2-step retry from step 1)

### Implementation for User Story 4

**iOS**:
- [ ] T043 [US4] Add error handling to ContactDetailsViewModel.submitForm() method (wrap service call in do-catch, catch URLError for network errors, catch generic Error for backend errors, call handleSubmissionError(error) in catch block, set isSubmitting = false in defer block)
- [ ] T044 [US4] Implement handleSubmissionError(_ error: Error) method in ContactDetailsViewModel (if error is URLError.notConnectedToInternet or URLError.timedOut: set alertMessage = L10n.tr("owners_details.error.no_connection.message"); else: set alertMessage = L10n.tr("owners_details.error.generic.message"); set showAlert = true)
- [ ] T045 [US4] Add alert modifier to ContactDetailsView (`.alert(isPresented: $viewModel.showAlert)` with title = alertMessage, primaryButton = "Try Again" calls submitForm(), secondaryButton = "Cancel" dismisses alert)
- [ ] T046 [US4] Add loading state to Continue button in ContactDetailsView (show ActivityIndicator spinner when isSubmitting = true, disable button when isSubmitting = true)
- [ ] T047 [US4] Add loading state to back button in ContactDetailsView (disable back button when isSubmitting = true to prevent navigation away mid-submission)

**Checkpoint**: All user stories should now be fully functional and independently testable - submission failures are handled gracefully with retry

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T048 [P] Run unit tests and verify 80% coverage for ContactDetailsViewModel and AnnouncementSubmissionService (execute `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES`, view coverage report in Xcode)
- [ ] T049 [P] Run E2E tests and verify all user stories pass (execute `npm run test:mobile:ios -- --spec owners-details` from `/e2e-tests/`)
- [ ] T050 [P] Verify localization for Polish and English (test device locale pl-PL displays Polish strings, test other locales display English strings)
- [ ] T051 [P] Test back navigation persistence (navigate to Step 4, fill inputs, go back to Step 3, return to Step 4, verify inputs persist exactly as entered)
- [ ] T052 [P] Test device rotation and app backgrounding (fill inputs, rotate device, verify inputs persist; fill inputs, background app, foreground app, verify inputs persist)
- [ ] T053 Verify keyboard handling (tap phone input, verify keyboard appears and Continue button remains accessible above keyboard; tap email input, verify inputs scroll into view)
- [ ] T054 [P] Test VoiceOver accessibility (enable VoiceOver, verify field labels, validation errors, and button states are announced correctly)
- [ ] T055 Run quickstart.md validation (follow quickstart.md steps to build and test the feature end-to-end)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-6)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (US1 → US2 → US3 → US4)
- **Polish (Phase 7)**: Depends on all user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - No dependencies on US1 (validation logic is independent), but builds on US1 ViewModel structure
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - No dependencies on US1/US2 (reward field is independent), but reuses ValidatedTextField from US1
- **User Story 4 (P2)**: Can start after Foundational (Phase 2) - No dependencies on US1/US2/US3 (error handling is orthogonal), but extends US1 submission logic with error handling

### Within Each User Story

- Tests (iOS unit tests, E2E tests) MUST be written and FAIL before implementation
- Models before services
- Services before ViewModels
- ViewModels before Views
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel (T001, T002 can run together)
- All Foundational tasks marked [P] can run in parallel (T004, T005, T006, T007 can run together; T008, T009 sequential after T007; T010 depends on T009; T011 depends on T010)
- Once Foundational phase completes, US2, US3, US4 can start in parallel with US1 if team capacity allows (US2 validation, US3 reward field, US4 error handling are mostly independent)
- All tests for a user story marked [P] can run in parallel (unit tests, E2E tests for same story)
- Platform implementations for the same user story can be worked on in parallel by different team members (iOS only in this spec)

---

## Parallel Example: User Story 1

```bash
# Launch all tests for User Story 1 together:
Task T015: "Create FakeAnimalRepository"
Task T016: "Create FakeAnnouncementSubmissionService"
Task T017: "Unit test for AnnouncementSubmissionService"
Task T018: "Unit test for ContactDetailsViewModel submission flow"
Task T019: "Create ContactDetailsScreen object (E2E)"
# After tests fail, launch implementation tasks:
Task T021: "Create OwnerContactDetails struct"
Task T022: "Modify ContactDetailsViewModel (ViewModel layer)"
Task T023: "Modify ContactDetailsView (UI layer)"
# Then integration:
Task T024: "Add accessibilityIdentifier to all interactive views"
Task T025: "Update ReportMissingPetCoordinator (navigation integration)"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

**MVP = Phase 1 + Phase 2 + Phase 3 (User Story 1 only)**

1. Complete Phase 1: Setup (T001-T003: localization strings)
2. Complete Phase 2: Foundational (T004-T014: DTOs in Data layer, domain models in Domain layer, Mappers, Repository protocol with domain signatures, Repository implementation with DTO conversion, Service, DI) - CRITICAL - blocks all stories
3. Complete Phase 3: User Story 1 (T015-T027: tests, ViewModel, View, coordinator integration, documentation)
4. **STOP and VALIDATE**: Test User Story 1 independently (E2E test with valid phone/email, verify 2-step submission and navigation to summary)
5. Deploy/demo if ready

**MVP Scope (27 tasks total)**:
- User can fill valid phone and email
- User can submit report (2-step: announcement + photo)
- User sees managementPassword on summary screen
- All US1 tests pass (80% coverage)
- Clean Architecture: DTOs in Data/, domain models in Domain/, Mappers convert between layers

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready (strict layer separation established)
2. Add User Story 1 (P1) → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 (P2) → Test independently → Deploy/Demo (validation feedback)
4. Add User Story 4 (P2) → Test independently → Deploy/Demo (error handling)
5. Add User Story 3 (P3) → Test independently → Deploy/Demo (optional reward)
6. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers (iOS-only feature):

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1 (core submission flow)
   - Developer B: User Story 2 (validation logic)
   - Developer C: User Story 4 (error handling)
   - Developer D: User Story 3 (reward field) + E2E tests
3. Stories complete and integrate independently

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability (US1, US2, US3, US4)
- Each user story should be independently completable and testable
- Verify tests fail before implementing
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- This is an iOS-only feature - Android, Web, Backend are not affected
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
- All tests MUST follow Given-When-Then (Arrange-Act-Assert) structure with descriptive names
- All user-facing text MUST use SwiftGen L10n.tr() with owners_details.* keys (no hardcoded strings)
- All interactive views MUST have accessibilityIdentifier for E2E testing (ownersDetails.* convention)
- Manual DI with constructor injection: Repository → Service → ViewModel (three-layer injection chain)
- ViewModel delegates submission to Service, NOT Repository directly (Service encapsulates 2-step orchestration logic)
- Reuse existing components: ValidatedTextField (from spec 031), PhotoAttachmentMetadata (from spec 028), ReportMissingPetFlowState (from spec 017)
- 2-step submission: (1) POST /api/v1/announcements → receive id + managementPassword, (2) POST /api/v1/announcements/:id/photos with Basic auth → both must succeed for navigation to summary
- Submission failure handling: Generic popup alert with "Try Again" / "Cancel" buttons, retry full 2-step submission from step 1 on "Try Again"
- Loading state during submission: Continue button shows spinner and disables, back button disables, input fields remain editable but not submittable
- Validation timing: Only on Continue tap (not on blur, not on keystroke) - matches pattern from spec 031
- Constitution compliance: iOS MVVM-C architecture, manual DI, Swift Concurrency (async/await), SwiftGen localization, 80% test coverage, accessibilityIdentifier on all interactive views
- **Clean Architecture (STRICT)**: DTOs with Codable/snake_case in `/Data/Models/` (T004-T006), pure domain models in `/Domain/Models/` (T007-T008), Mappers in `/Data/Mappers/` (T009) convert between layers, Repository Protocol uses domain models only (T010), Repository Implementation converts domain → DTO → HTTP → DTO → domain (T011-T012), Service uses domain models only (T013), ViewModel uses domain models only (T022)
- **Layer separation**: ViewModel never sees DTOs (receives String/domain objects), Service sees domain models only (AnnouncementResult, CreateAnnouncementData), Repository Protocol signature uses domain models only, Repository Implementation uses DTOs internally and Mappers for conversion, DTOs are encapsulated in Data layer and never leak to Domain/Presentation
- Total tasks: 55 (Setup: 3, Foundational: 11, US1: 13, US2: 6, US3: 5, US4: 9, Polish: 8)
- MVP tasks: 27 (Phase 1 + Phase 2 + Phase 3)

