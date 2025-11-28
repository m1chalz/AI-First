---

description: "Task list for Animal Description Screen (iOS) implementation"
---

# Tasks: Animal Description Screen (iOS)

**Input**: Design documents from `/specs/031-ios-animal-description-screen/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/

**Tests**: Test requirements for this iOS-only project:

**MANDATORY - iOS Unit Tests**:
- Location: `/iosApp/iosAppTests/Features/ReportMissingPet/AnimalDescription/`
- Framework: XCTest with Swift Concurrency (async/await)
- Scope: Domain models, ViewModels, validation logic
- Coverage target: 80% line + branch coverage
- Run: `xcodebuild test -project iosApp/iosApp.xcodeproj -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
- Convention: MUST follow Given-When-Then structure with descriptive test names (e.g., `testMethodName_whenCondition_shouldResult()`)

**MANDATORY - End-to-End Tests**:
- Location: `/e2e-tests/mobile/specs/animal-description.spec.ts`
- Framework: Appium + WebdriverIO + TypeScript
- Coverage: All 3 user stories (P1: required fields, P2: GPS capture, P3: validation/persistence)
- Run: `npm run test:mobile:ios` (from repo root)
- Convention: MUST structure scenarios with Given-When-Then phases

**Note**: This is an iOS-only feature. Android, Web, and Backend platforms are NOT affected and have NO tasks in this implementation.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `- [ ] [ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- iOS app: `/iosApp/iosApp/`
- iOS tests: `/iosApp/iosAppTests/`
- E2E tests: `/e2e-tests/mobile/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [X] T001 Verify iOS project opens in Xcode 15+ without errors
- [X] T002 [P] Verify existing LocationService at `/iosApp/iosApp/Data/LocationService.swift` is accessible
- [X] T003 [P] Verify existing ReportMissingPetFlowState at `/iosApp/iosApp/Features/ReportMissingPet/Models/ReportMissingPetFlowState.swift` exists ✅ EXISTS
- [X] T004 [P] Verify existing ServiceContainer at `/iosApp/iosApp/DI/ServiceContainer.swift` exists
- [X] T005 [P] Create feature directory structure at `/iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/`
- [X] T006 [P] Create components subdirectory at `/iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/Components/`
- [X] T007 [P] Create test directory at `/iosApp/iosAppTests/Features/ReportMissingPet/AnimalDescription/`
- [X] T008 [P] Create test fakes directory at `/iosApp/iosAppTests/Features/ReportMissingPet/AnimalDescription/Fakes/`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T009 ✅ REUSE existing AnimalGender enum in `/iosApp/iosApp/Domain/Models/AnimalGender.swift` (already has male, female, unknown; presentation extension with displayName exists at `/iosApp/iosApp/Features/Shared/AnimalGender+Presentation.swift`)
- [X] T010 [P] Extend existing AnimalSpecies enum in `/iosApp/iosApp/Domain/Models/AnimalSpecies.swift` (ADD `.rodent` and `.reptile` cases to existing dog/cat/bird/rabbit/other; presentation extension with displayName already exists at `/iosApp/iosApp/Features/Shared/AnimalSpecies+Presentation.swift`)
- [X] T011 [P] Update AnimalSpecies+Presentation.swift to add displayName cases for `.rodent` and `.reptile` using L10n
- [X] T012 [P] Extend ReportMissingPetFlowState in `/iosApp/iosApp/Features/ReportMissingPet/Models/ReportMissingPetFlowState.swift` (REPLACE `@Published var description: String?` with structured animal description fields: `disappearanceDate: Date?`, `animalSpecies: AnimalSpecies?`, `animalRace: String?`, `animalGender: AnimalGender?`, `animalAge: Int?`, `animalLatitude: Double?`, `animalLongitude: Double?`, `animalAdditionalDescription: String?`)
- [X] T013 [P] Create ValidationError enum in `/iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/ValidationError.swift` (field-specific errors with messages and field mapping)
- [X] T014 [P] Create CoordinateValidationResult enum in `/iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/CoordinateValidationResult.swift` (valid or invalid with lat/long errors)
- [X] T015 [P] Create FormField enum in `/iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/FormField.swift` (field identifiers for validation)
- [X] T016 ✅ DONE - Covered by T012 (ReportMissingPetFlowState extended with animal description fields)
- [X] T017 [P] Add localization keys for Animal Description screen to `Localizable.strings` (labels, placeholders, error messages, button titles)
- [X] T018 Run SwiftGen to regenerate L10n accessors in `/iosApp/iosApp/Generated/Strings.swift`
- [X] T019 [P] Create E2E Screen Object Model at `/e2e-tests/mobile/screens/AnimalDescriptionScreen.ts` (base structure with accessibilityIdentifiers)
- [X] T020 Build iOS project to verify all foundational models compile successfully

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Provide animal context before contact (Priority: P1) 🎯 MVP

**Goal**: Reporters must enter descriptive data (date, species, breed/race, gender) so responders understand the case before reading contact details.

**Independent Test**: On iOS, launch Step 3 directly via navigation from Step 2, populate the required fields, and advance to Step 4 without touching other flow steps.

### Tests for User Story 1 (MANDATORY) ✅

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

**iOS Unit Tests**:
- [X] T021 [P] [US1] Create FakeReportMissingPetFlowState in `/iosApp/iosAppTests/Features/ReportMissingPet/AnimalDescription/Fakes/FakeReportMissingPetFlowState.swift` (test double for flow state)
- [X] T022 ✅ Created FakeLocationService in `/iosAppTests/Fakes/FakeLocationService.swift` (test double for location service)
- [X] T023 ✅ SKIP - No separate AnimalDescriptionDetails struct (using flat FlowState properties)
- [X] T024 ✅ SKIP - Using existing AnimalSpecies enum (already tested if tests exist)
- [X] T025 ✅ VERIFY existing AnimalGender tests (enum and presentation extension already exist)
- [X] T026 [P] [US1] Create AnimalDescriptionViewModelTests in `/iosApp/iosAppTests/Features/ReportMissingPet/AnimalDescription/AnimalDescriptionViewModelTests.swift` (test setup with fakes)
- [X] T027 [P] [US1] Test: selectSpecies_whenSpeciesSelected_shouldUpdateFormDataAndClearRace (Given species selected, When selectSpecies called, Then selectedSpecies set and race cleared)
- [X] T027a [P] [US1] Test: selectSpecies_whenRacePreviouslyEntered_shouldClearRaceField (Given race "Labrador" entered and species "dog" selected, When selectSpecies called with "cat", Then race field cleared to empty string and race validation error cleared)
- [X] T028 [P] [US1] Test: selectGender_whenGenderSelected_shouldUpdateFormData (Given gender selected, When selectGender called, Then selectedGender set)
- [X] T029 [P] [US1] Test: onContinueTapped_whenRequiredFieldsEmpty_shouldShowValidationErrors (Given empty required fields, When Continue tapped, Then toast shown and errors set)
- [X] T030 [P] [US1] Test: onContinueTapped_whenRequiredFieldsValid_shouldUpdateSessionAndNavigate (Given valid required fields, When Continue tapped, Then session updated and onContinue called)
- [X] T031 [P] [US1] Test: raceTextFieldModel_whenSpeciesNotSelected_shouldBeDisabled (Given no species, When raceTextFieldModel accessed, Then isDisabled is true)
- [X] T032 [P] [US1] Test: raceTextFieldModel_whenSpeciesSelected_shouldBeEnabled (Given species selected, When raceTextFieldModel accessed, Then isDisabled is false)
- [X] T033 [P] [US1] Test: validateAllFields_whenDateMissing_shouldReturnMissingDateError (Given disappearanceDate nil, When validateAllFields called, Then missingDate error returned)
- [X] T034 [P] [US1] Test: validateAllFields_whenSpeciesMissing_shouldReturnMissingSpeciesError (Given selectedSpecies nil, When validateAllFields called, Then missingSpecies error returned)
- [X] T035 [P] [US1] Test: validateAllFields_whenRaceEmpty_shouldReturnMissingRaceError (Given race empty, When validateAllFields called, Then missingRace error returned)
- [X] T036 [P] [US1] Test: validateAllFields_whenGenderMissing_shouldReturnMissingGenderError (Given selectedGender nil, When validateAllFields called, Then missingGender error returned)
- [X] T037 [P] [US1] Test: validateAllFields_whenAllRequiredFieldsValid_shouldReturnEmptyErrors (Given all required fields valid, When validateAllFields called, Then empty array returned)

**End-to-End Tests**:
- [ ] T038 [P] [US1] E2E test: Complete animal description with valid required data in `/e2e-tests/mobile/specs/animal-description.spec.ts` (Given Step 3 loaded, When fill date/species/race/gender and tap Continue, Then navigate to Step 4)
- [ ] T039 [P] [US1] E2E test: Submit form with missing required fields (Given Step 3 loaded, When tap Continue without filling, Then toast and inline errors shown)
- [ ] T040 [P] [US1] Update Screen Object Model with required field locators in `/e2e-tests/mobile/screens/AnimalDescriptionScreen.ts`

### Implementation for User Story 1

**Domain Models** (already created in Foundational phase):
- ✅ T009-T016: Gender, SpeciesTaxonomyOption, SpeciesTaxonomy, AnimalDescriptionDetails, ValidationError, CoordinateValidationResult, FormField

**Reusable Form Components**:
- [X] T041 [P] [US1] Create ValidatedTextField component in `/iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/Components/ValidatedTextField.swift` (Model pattern: label, placeholder, errorMessage, isDisabled, keyboardType, accessibilityID)
- [X] T042 [P] [US1] Create DropdownView component in `/iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/Components/DropdownView.swift` (Model pattern: label, placeholder, options as [String], errorMessage, accessibilityID)
- [X] T043 [P] [US1] Create SelectorView component in `/iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/Components/SelectorView.swift` (Model pattern: label, options as [String], errorMessage, accessibilityIDPrefix)

**ViewModel**:
- [X] T044 [US1] Create AnimalDescriptionViewModel in `/iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/AnimalDescriptionViewModel.swift` (ObservableObject with @MainActor, grouped state: FormData, ValidationErrors, UIState)
- [X] T045 [US1] Implement init(flowState:locationService:) with constructor injection and flow state data loading (populate from flowState.disappearanceDate, .animalSpecies, .animalRace, etc.)
- [X] T046 [US1] Implement computed property raceTextFieldModel (return ValidatedTextField.Model with isDisabled based on selectedSpecies)
- [X] T047 [US1] Implement computed property speciesDropdownModel (return DropdownView.Model with mapped display names)
- [X] T048 [US1] Implement computed property genderSelectorModel (return SelectorView.Model with mapped display names)
- [X] T049 [US1] Implement selectSpecies(_:) method (update formData.selectedSpecies, clear formData.race, clear validationErrors.race)
- [X] T050 [US1] Implement selectGender(_:) method (update formData.selectedGender, clear validationErrors.gender)
- [X] T051 [US1] Implement validateAllFields() private method (validate date, species, race, gender - return [ValidationError])
- [X] T052 [US1] Implement clearValidationErrors() private method (reset validationErrors to .clear)
- [X] T053 [US1] Implement applyValidationErrors(_:) private method (map ValidationError array to validationErrors struct properties)
- [X] T054 [US1] Implement updateFlowState() private method (assign formData values to flowState.disappearanceDate, .animalSpecies, .animalRace, .animalGender, .animalAge, .animalLatitude, .animalLongitude, .animalAdditionalDescription)
- [X] T055 [US1] Implement onContinueTapped() method (clearValidationErrors → validateAllFields → if valid: updateFlowState + onContinue, else: showToast + applyValidationErrors)
- [X] T056 [US1] Implement onBackTapped() method (call onBack closure without updating flow state)
- [ ] T057 [P] [US1] Add SwiftDoc documentation to complex ViewModel methods (validateAllFields, applyValidationErrors - skip self-explanatory properties)

**View**:
- [X] T058 [US1] Create AnimalDescriptionView in `/iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/AnimalDescriptionView.swift` (ObservedObject viewModel)
- [X] T059 [US1] Add date picker section (native DatePicker with `in: ...Date()` to block future dates, accessibilityIdentifier "animalDescription.datePicker.tap")
- [X] T060 [US1] Add species dropdown (DropdownView with viewModel.speciesDropdownModel, onChange to call viewModel.selectSpecies)
- [X] T061 [US1] Add race text field (ValidatedTextField with viewModel.raceTextFieldModel, binding to viewModel.formData.race)
- [X] T062 [US1] Add gender selector (SelectorView with viewModel.genderSelectorModel, onChange to call viewModel.selectGender)
- [X] T063 [US1] Add Continue button (action: viewModel.onContinueTapped(), accessibilityIdentifier "animalDescription.continueButton.tap")
- [X] T064 [US1] Add toast modifier (isPresented: $viewModel.uiState.showToast, message: viewModel.uiState.toastMessage)

**Coordinator Integration**:
- [X] T065 ✅ VERIFY existing navigateToDescription() method in ReportMissingPetCoordinator at `/iosApp/iosApp/Features/ReportMissingPet/Coordinators/ReportMissingPetCoordinator.swift` (already exists - need to update ViewModel creation with animal description ViewModel instead of placeholder DescriptionViewModel)
- [X] T066 [US1] Update navigateToDescription() to create AnimalDescriptionViewModel (inject flowState + ServiceContainer.shared.locationService)
- [X] T067 ✅ VERIFY coordinator callbacks pattern (onNext/onBack already set for DescriptionViewModel - need to apply same pattern to AnimalDescriptionViewModel)
- [X] T068 ✅ VERIFY existing DescriptionView wrapper in UIHostingController with NavigationBackHiding (already exists at line 129-131 - need to replace DescriptionView with AnimalDescriptionView)

**Verification**:
- [ ] T069 [US1] Run unit tests for US1: `xcodebuild test -project iosApp/iosApp.xcodeproj -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
- [ ] T070 [US1] Verify 80%+ code coverage for AnimalDescriptionViewModel
- [ ] T071 [US1] Manual test: Launch Step 3, fill required fields, tap Continue, verify navigation to Step 4
- [ ] T072 [US1] Manual test: Launch Step 3, leave fields empty, tap Continue, verify toast and inline errors appear
- [ ] T073 [US1] Run E2E tests for US1: `npm run test:mobile:ios` (from repo root)

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently. This is the MVP deliverable.

---

## Phase 4: User Story 2 - Capture last known location details (Priority: P2)

**Goal**: Caregivers may not remember precise latitude/longitude, so they need a "Request GPS position" shortcut plus manual editing of latitude and longitude fields.

**Independent Test**: On an iOS device with mock GPS data, tap the Request GPS position button, verify permissions and auto-fill, modify the coordinates manually, and confirm validation before proceeding.

### Tests for User Story 2 (MANDATORY) ✅

**iOS Unit Tests**:
- [X] T074 [P] [US2] Test: requestGPSPosition_whenPermissionNotDetermined_shouldRequestPermissionAndFetchLocation (Given .notDetermined status, When requestGPSPosition called, Then permission requested and location fetched)
- [X] T075 [P] [US2] Test: requestGPSPosition_whenPermissionAuthorized_shouldFetchLocation (Given .authorizedWhenInUse status, When requestGPSPosition called, Then location fetched and fields populated)
- [X] T076 [P] [US2] Test: requestGPSPosition_whenPermissionDenied_shouldShowAlert (Given .denied status, When requestGPSPosition called, Then showPermissionDeniedAlert set to true)
- [X] T077 [P] [US2] Test: fetchLocation_whenLocationServiceReturnsCoordinates_shouldPopulateLatLongFields (Given location service returns coordinates, When fetchLocation called, Then latitude and longitude strings formatted to 5 decimals)
- [X] T078 [P] [US2] Test: validateCoordinates_whenBothEmpty_shouldReturnValid (Given latitude and longitude empty, When validateCoordinates called, Then .valid returned)
- [X] T079 [P] [US2] Test: validateCoordinates_whenLatitudeOutOfRange_shouldReturnInvalidLatError (Given latitude > 90, When validateCoordinates called, Then .invalid with latError returned)
- [X] T080 [P] [US2] Test: validateCoordinates_whenLongitudeOutOfRange_shouldReturnInvalidLongError (Given longitude > 180, When validateCoordinates called, Then .invalid with longError returned)
- [X] T081 [P] [US2] Test: validateCoordinates_whenBothInRange_shouldReturnValid (Given latitude -90 to 90 and longitude -180 to 180, When validateCoordinates called, Then .valid returned)
- [X] T082 [P] [US2] Test: validateCoordinates_whenInvalidFormat_shouldReturnFormatError (Given non-numeric string, When validateCoordinates called, Then .invalid with format error returned)
- [X] T083 [P] [US2] Test: onContinueTapped_withInvalidCoordinates_shouldShowInlineErrors (Given invalid coordinates, When Continue tapped, Then coordinate errors shown in validationErrors)

**End-to-End Tests**:
- [ ] T084 [P] [US2] E2E test: Capture GPS coordinates with permission granted (Given Step 3 loaded and permission granted, When tap GPS button, Then lat/long fields populated)
- [ ] T085 [P] [US2] E2E test: GPS permission denied shows alert with options (Given Step 3 loaded and permission denied, When tap GPS button, Then alert with Cancel/Go Settings shown)
- [ ] T086 [P] [US2] E2E test: Manual coordinate entry with valid values (Given Step 3 loaded, When manually enter valid lat/long and tap Continue, Then navigate to Step 4)
- [ ] T087 [P] [US2] E2E test: Manual coordinate entry with invalid latitude (Given Step 3 loaded, When enter latitude 100 and tap Continue, Then inline error shown)
- [ ] T088 [P] [US2] E2E test: Manual coordinate entry with invalid longitude (Given Step 3 loaded, When enter longitude 200 and tap Continue, Then inline error shown)

### Implementation for User Story 2

**Reusable Form Components**:
- [X] T089 [P] [US2] Create LocationCoordinateView component in `/iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/Components/LocationCoordinateView.swift` (composes 2x ValidatedTextField + GPS button, Model pattern with latitudeField, longitudeField, gpsButtonTitle, gpsButtonAccessibilityID, helperText)

**ViewModel Extensions**:
- [X] T090 [US2] Implement computed property locationCoordinateModel (return LocationCoordinateView.Model with two ValidatedTextField.Model instances for lat/long)
- [X] T091 [US2] Implement validateCoordinates() private method (parse strings, validate ranges: lat -90 to 90, long -180 to 180, return CoordinateValidationResult)
- [X] T092 [US2] Update validateAllFields() to include coordinate validation (call validateCoordinates and append errors if invalid)
- [X] T093 [US2] Implement requestGPSPosition() async method (check LocationService authorizationStatus, request if .notDetermined, fetch location if authorized, show alert if denied/restricted)
- [X] T094 [US2] Implement fetchLocation() async private method (call locationService.requestLocation(), format to 5 decimals, populate formData.latitude and formData.longitude strings, set gpsHelperText)
- [ ] T095 ✅ REUSE pattern from AnimalListCoordinator (method openAppSettings() at line 151-156 in `/iosApp/iosApp/Features/AnimalList/Coordinators/AnimalListCoordinator.swift` - use same pattern: `UIApplication.shared.open(URL(string: UIApplication.openSettingsURLString))`)
- [ ] T096 [P] [US2] Add SwiftDoc documentation to GPS-related methods (requestGPSPosition, validateCoordinates) if behavior non-obvious from method names

**View Extensions**:
- [X] T097 [US2] Add LocationCoordinateView to AnimalDescriptionView (above additional description section, bindings to viewModel.formData.location.latitude and longitude, onGPSButtonTap closure calling viewModel.requestGPSPosition)
- [X] T098 [US2] Add permission denied alert modifier (isPresented: $viewModel.uiState.showPermissionDeniedAlert, Cancel button, Go to Settings button calling viewModel.openSettings)

**Coordinator Extensions**:
- [ ] T099 ✅ IMPLEMENT in coordinator - Add private openAppSettings() method (copy pattern from AnimalListCoordinator line 151-156), set viewModel.onOpenAppSettings closure to call this method

**Verification**:
- [ ] T100 [US2] Run unit tests for US2: `xcodebuild test -project iosApp/iosApp.xcodeproj -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
- [ ] T101 [US2] Verify 80%+ code coverage for GPS-related methods
- [ ] T102 [US2] Manual test: Tap GPS button, grant permission, verify lat/long fields populated
- [ ] T103 [US2] Manual test: Tap GPS button, deny permission, verify alert with Cancel/Go Settings shown
- [ ] T104 [US2] Manual test: Manually enter valid coordinates, tap Continue, verify navigation to Step 4
- [ ] T105 [US2] Manual test: Manually enter invalid latitude (e.g., 100), tap Continue, verify inline error shown
- [ ] T106 [US2] Run E2E tests for US2: `npm run test:mobile:ios` (from repo root)

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently. GPS capture and manual coordinate entry are functional.

---

## Phase 5: User Story 3 - Maintain validation, persistence, and safe exits (Priority: P3)

**Goal**: Reporters might step away or return to previous steps; Step 3 must preserve their entries, explain optional fields, and prevent them from advancing with incomplete required data.

**Independent Test**: On iOS, populate some fields, navigate backward to Step 2 via the header arrow, re-enter Step 3, confirm values persist, clear a required field, tap Continue, and verify a toast plus inline error messaging appear and block navigation until the issues are resolved.

### Tests for User Story 3 (MANDATORY) ✅

**iOS Unit Tests**:
- [X] T107 [P] [US3] Test: init_whenFlowStateHasExistingData_shouldPopulateFormData (Given flowState has animal description fields set, When ViewModel initialized, Then formData populated from flowState)
- [X] T108 [P] [US3] Test: init_whenFlowStateEmpty_shouldUseDefaultFormData (Given flowState animal description fields nil, When ViewModel initialized, Then formData set to initial values with today's date)
- [X] T109 [P] [US3] Test: updateFlowState_whenCalled_shouldSaveFormDataToFlowState (Given valid formData, When updateFlowState called, Then flowState animal description fields updated)
- [X] T110 [P] [US3] Test: onBackTapped_whenCalled_shouldNotUpdateFlowState (Given formData modified, When onBackTapped called, Then flowState unchanged and onBack closure called)
- [X] T111 [P] [US3] Test: validateAllFields_whenAgeBelowZero_shouldReturnInvalidAgeError (Given age "-5", When validateAllFields called, Then invalidAge error returned)
- [X] T112 [P] [US3] Test: validateAllFields_whenAgeAbove40_shouldReturnInvalidAgeError (Given age "50", When validateAllFields called, Then invalidAge error returned)
- [X] T113 [P] [US3] Test: validateAllFields_whenAgeEmpty_shouldReturnValid (Given age empty string, When validateAllFields called, Then no age error returned)
- [X] T114 [P] [US3] Test: characterCountText_whenDescriptionEmpty_shouldReturn0Slash500 (Given additionalDescription empty, When characterCountText accessed, Then "0/500" returned)
- [X] T115 [P] [US3] Test: characterCountText_whenDescription123Chars_shouldReturn123Slash500 (Given additionalDescription 123 chars, When characterCountText accessed, Then "123/500" returned)
- [X] T116 [P] [US3] Test: characterCountColor_whenNearLimit_shouldReturnWarningColor (Given additionalDescription 490+ chars, When characterCountColor accessed, Then warning color returned)

**End-to-End Tests**:
- [ ] T117 [P] [US3] E2E test: Navigation back preserves Step 3 data (Given Step 3 filled, When tap Back to Step 2 then forward to Step 3, Then all fields retain previous values)
- [ ] T118 [P] [US3] E2E test: Changing species clears race field (Given Step 3 loaded and race entered, When change species, Then race field cleared)
- [ ] T119 [P] [US3] E2E test: Optional age field accepts valid range 0-40 (Given Step 3 loaded, When enter age "5" and tap Continue, Then navigate to Step 4)
- [ ] T120 [P] [US3] E2E test: Optional age field rejects invalid values (Given Step 3 loaded, When enter age "50" and tap Continue, Then inline error shown)
- [ ] T121 [P] [US3] E2E test: Description text area enforces 500 character limit (Given Step 3 loaded, When type 600 characters, Then only first 500 characters accepted)

### Implementation for User Story 3

**Reusable Form Components**:
- [X] T122 [P] [US3] Create TextAreaView component in `/iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/Components/TextAreaView.swift` (TextEditor wrapper with character counter, Model pattern with label, placeholder, maxLength, characterCountText, characterCountColor, accessibilityID)

**ViewModel Extensions**:
- [X] T123 [US3] Implement computed property ageTextFieldModel (return ValidatedTextField.Model with numeric keyboard and age validation error)
- [X] T124 [US3] Implement computed property descriptionTextAreaModel (return TextAreaView.Model with characterCountText and characterCountColor)
- [X] T125 [US3] Implement computed property characterCountText (format as "X/500" where X is additionalDescription.count)
- [X] T126 [US3] Implement computed property characterCountColor (return .red if > 480, .orange if > 450, else .secondary)
- [X] T127 [US3] Update validateAllFields() to include age validation (if not empty: parse Int, validate 0-40 range)
- [X] T128 [US3] Update init(flowState:locationService:) to load existing flow state data (if flowState has animal description fields, populate all formData fields including age, location, additionalDescription)
- [ ] T129 [P] [US3] Add SwiftDoc documentation to flow state-related methods (init with flow state loading logic, updateFlowState)

**View Extensions**:
- [X] T130 [US3] Add age text field to AnimalDescriptionView (ValidatedTextField with viewModel.ageTextFieldModel, binding to viewModel.formData.age, keyboardType .numberPad)
- [X] T131 [US3] Add description text area to AnimalDescriptionView (TextAreaView with viewModel.descriptionTextAreaModel, binding to viewModel.formData.additionalDescription, hard limit: prevent input at 500 chars, truncate pasted text via onChange)
- [X] T132 [US3] Add back button handler (call viewModel.onBackTapped)

**Verification**:
- [ ] T133 [US3] Run unit tests for US3: `xcodebuild test -project iosApp/iosApp.xcodeproj -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
- [ ] T134 [US3] Verify 80%+ code coverage overall for AnimalDescriptionViewModel
- [ ] T135 [US3] Manual test: Fill Step 3, tap Back, return to Step 3, verify all fields preserved
- [ ] T136 [US3] Manual test: Select species, enter race, change species, verify race cleared
- [ ] T137 [US3] Manual test: Enter age "5", tap Continue, verify navigation succeeds
- [ ] T138 [US3] Manual test: Enter age "50", tap Continue, verify inline error shown
- [ ] T139 [US3] Manual test: Type 600 characters in description, verify only 500 accepted
- [ ] T140 [US3] Run E2E tests for US3: `npm run test:mobile:ios` (from repo root)

**Checkpoint**: All user stories should now be independently functional. Validation, persistence, and safe navigation are complete.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T141 [P] Run full unit test suite with coverage: `xcodebuild test -project iosApp/iosApp.xcodeproj -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
- [ ] T142 Verify overall 80%+ line + branch coverage in Xcode coverage report (Product → Show Code Coverage)
- [ ] T143 [P] Run full E2E test suite: `npm run test:mobile:ios` (from repo root)
- [ ] T144 [P] Manual testing checklist from quickstart.md (all 18 acceptance scenarios)
- [ ] T145 [P] Verify all interactive UI elements have accessibilityIdentifier attributes
- [ ] T146 [P] Verify all localized strings use L10n accessors (no hardcoded strings)
- [ ] T147 [P] Code review: Verify SwiftDoc documentation added to complex methods (skip self-explanatory)
- [ ] T148 [P] Code review: Verify all components follow Model pattern (no @Published in components)
- [ ] T149 [P] Code review: Verify ViewModel follows MVVM-C pattern (no navigation logic in ViewModel, coordinator callbacks only)
- [ ] T150 [P] Performance check: Test on physical iOS device (verify no UI lag on form interactions)
- [ ] T151 [P] Update documentation: Add Animal Description screen to `/iosApp/README.md` if needed
- [ ] T152 Final build verification: Build iOS project in Release configuration
- [ ] T153 Validate quickstart.md: Follow all setup steps from clean checkout to verify accuracy

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-5)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3)
- **Polish (Phase 6)**: Depends on all user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Reuses components from US1 (ValidatedTextField) but independently testable
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - Integrates with US1/US2 features but independently testable

### Within Each User Story

- Tests MUST be written and FAIL before implementation
- Components before ViewModel (ViewModel depends on component Model structs)
- ViewModel before View (View observes ViewModel)
- View before Coordinator integration (Coordinator creates View with ViewModel)
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel (T001-T008)
- All Foundational tasks marked [P] can run in parallel (T009-T019)
- Within each user story:
  - All unit tests marked [P] can run in parallel
  - All E2E tests marked [P] can run in parallel
  - All component creation tasks marked [P] can run in parallel (different files)
  - Documentation tasks marked [P] can run in parallel (different files)
- Once Foundational phase completes, all three user stories CAN start in parallel (if team capacity allows) since each story is independently testable

---

## Parallel Example: User Story 1

```bash
# Launch all unit tests for User Story 1 together:
Task T023: "Unit test for AnimalDescriptionDetails"
Task T024: "Unit test for SpeciesTaxonomyOption"
Task T025: "Unit test for Gender enum"
Task T027-T037: "All ViewModel unit tests (each tests one method/behavior)"

# Launch all component creation tasks together:
Task T041: "Create ValidatedTextField component"
Task T042: "Create DropdownView component"
Task T043: "Create SelectorView component"

# Launch all E2E tests for User Story 1 together:
Task T038: "E2E test: Complete form with valid data"
Task T039: "E2E test: Submit with missing fields"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (T001-T008)
2. Complete Phase 2: Foundational (T009-T020) - CRITICAL - blocks all stories
3. Complete Phase 3: User Story 1 (T021-T073)
4. **STOP and VALIDATE**: Test User Story 1 independently
5. Deploy/demo if ready

This MVP delivers the core functionality: reporters can enter required animal description data (date, species, race, gender) and proceed to contact details (Step 4).

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP! ✅)
3. Add User Story 2 → Test independently → Deploy/Demo (GPS capture added)
4. Add User Story 3 → Test independently → Deploy/Demo (Validation polish + persistence)
5. Each story adds value without breaking previous stories

### Parallel Team Strategy

With 2-3 developers available:

1. Team completes Setup + Foundational together (T001-T020)
2. Once Foundational is done:
   - Developer A: User Story 1 - Core form fields (T021-T073)
   - Developer B: User Story 2 - GPS capture (T074-T106) - starts after US1 components ready
   - Developer C: User Story 3 - Validation polish (T107-T140) - starts after US1 ViewModel ready
3. Stories integrate naturally (each extends ViewModel and View)

OR sequential (recommended for iOS-only feature):

1. One developer completes phases sequentially: Setup → Foundational → US1 → US2 → US3 → Polish
2. Each phase fully tested before proceeding to next
3. MVP deployed after US1, incremental releases after US2 and US3

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Tests MUST be written FIRST and FAIL before implementation (TDD approach)
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- This is an iOS-only feature - no Android, Web, or Backend tasks included
- Reuses existing LocationService and MissingPetFlowSession from previous specs
- All components follow Model pattern (pure presentation, no @Published properties)
- ViewModel follows MVVM-C pattern (ObservableObject, coordinator callbacks for navigation)
- 80% code coverage required for ViewModel and validation logic
- All UI elements require accessibilityIdentifier for E2E testing

