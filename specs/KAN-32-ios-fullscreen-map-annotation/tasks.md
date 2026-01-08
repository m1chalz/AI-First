# Tasks: iOS Fullscreen Map - Display Pin Annotation Details

**Feature Branch**: `KAN-32-ios-fullscreen-map-annotation`  
**Input**: Design documents from `/specs/KAN-32-ios-fullscreen-map-annotation/`  
**Prerequisites**: spec.md (user stories), plan.md (tech approach), research.md (technical decisions), data-model.md (entities), quickstart.md (test scenarios)

**Platform Scope**: iOS only (no backend, Android, or Web changes)

**Tests**: Required per iOS constitution. Add/adjust unit tests for ViewModels and presentation-model mapping, and ensure >=80% line+branch coverage for changed/new iOS logic.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story. Each user story can be deployed as an independent increment after Phase 2 completion.

## Format: `[ID] [P?] [Story] Description`

- **Checkbox**: `- [ ]` (markdown checkbox)
- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Minimal project setup for annotation feature

- [ ] T001 [P] Add localization string for missing pet name fallback in `/iosApp/iosApp/Resources/en.lproj/Localizable.strings` ("annotationCallout.unknownPet" = "Unknown Pet")
- [ ] T002 [P] Run SwiftGen to generate `L10n.AnnotationCallout.unknownPet` in `/iosApp/iosApp/Generated/Strings.swift`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T003 Extend `MapPin` struct with callout data fields in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/MapPin.swift` (add: `petName: String?`, `photoUrl: String?`, `breed: String?`, `lastSeenDate: String`, `ownerEmail: String?`, `ownerPhone: String?`, `petDescription: String?`)
- [ ] T004 Update `MapPin.init(from: Announcement)` to map new callout fields from announcement in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/MapPin.swift` (treat empty photoUrl as nil)
- [ ] T005 Add `@Published private(set) var selectedPinId: String?` to `FullscreenMapViewModel` in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapViewModel.swift`
- [ ] T006 [P] Implement `selectPin(_ pinId: String)` method in `FullscreenMapViewModel` (toggle if same pin, replace if different - FR-011, FR-012)
- [ ] T007 [P] Implement `deselectPin()` method in `FullscreenMapViewModel` (clear selection - FR-010)
- [ ] T008 Create `AnnotationCalloutView_Model.swift` with `struct Model` containing presentation fields in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/AnnotationCalloutView_Model.swift`
- [ ] T009 Implement `AnnotationCalloutView.Model.init(from pin: MapPin)` factory method with field mapping and formatting logic in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/AnnotationCalloutView_Model.swift`
- [ ] T010 [P] Add static `formatDate(_ dateString: String) -> String` helper in `AnnotationCalloutView.Model` extension (temporary duplication of `PetDetailsViewModel` formatting; yyyy-MM-dd → MMM dd, yyyy; include TODO for Phase 6 extraction)
- [ ] T011 [P] Add static `formatCoordinates(_ coordinate: CLLocationCoordinate2D) -> String` helper in `AnnotationCalloutView.Model` extension (temporary duplication of `PetDetailsViewModel` formatting; "52.2297° N, 21.0122° E"; include TODO for Phase 6 extraction)
- [ ] T012 [P] Add `annotationBadgeColorHex` computed property to `AnnouncementStatus` extension returning spec colors (#FF9500 for .active, #155DFC for .found, #8E8E93 for .closed) - create new file or extend existing `AnnouncementStatus+Presentation.swift` (Note: `.closed` exists in iOS `AnnouncementStatus` and is localized as CLOSED; handle as gray badge as an edge case outside current spec assumptions.)

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - View Missing Animal Details from Pin (Priority: P1) 🎯 MVP

**Goal**: Enable users to tap a pin and see detailed information about the missing animal (photo, name, breed, location, date, contact, description, status)

**Independent Test**: Open fullscreen map with pins, tap any pin, verify annotation callout appears with all required information fields populated correctly (manual test from quickstart.md)

**Acceptance Criteria**:
- FR-001: Tap pin → callout appears
- FR-002: Callout styled as white card, 12px radius, drop shadow
- FR-003: Callout includes pointer arrow to pin
- FR-004: Displays pet photo, name, species/breed, location, date, email, phone, description, status badge
- FR-010: Tap map elsewhere → callout dismisses
- FR-011: Tap same pin → callout toggles off
- FR-012: Tap different pin → previous callout dismisses, new appears

### Implementation for User Story 1

**iOS** (Full Stack Implementation):

- [ ] T013 [US1] Create `CalloutPointer` shape struct in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/AnnotationCalloutView.swift` (triangle pointing down: 20pt wide, 10pt tall)
- [ ] T014 [US1] Create `AnnotationCalloutView` SwiftUI view in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/AnnotationCalloutView.swift` with `model: Model` parameter
- [ ] T015 [US1] Implement `cardContent` computed property with VStack layout containing all fields from Figma design (pet photo 216×120px/8px radius, name 16px bold #333, species/breed 13px #666, location/date/email/phone 13px #666 with emoji prefixes, description 14px #444, status badge)
- [ ] T016 [US1] Implement `photoView` computed property with `AsyncImage` showing pet photo or placeholder (120px height, 8px border radius - FR-004)
- [ ] T017 [US1] Implement placeholder for missing photo: rounded rectangle with `pawprint.fill` icon (24pt scaled, #93A2B4) on #EEEEEE background matching Announcement List style (FR-005)
- [ ] T018 [US1] Implement `statusBadge` computed property displaying status text with colored background (12px radius, status-specific colors from `model.statusColorHex` - FR-009)
- [ ] T019 [US1] Assemble `body` view with VStack: `cardContent` with white background, 12px corner radius, shadow (0/3/14/0.4), followed by `CalloutPointer` below (FR-002, FR-003)
- [ ] T020 [US1] Add `.accessibilityIdentifier(model.accessibilityId)` to callout root view
- [ ] T021 [US1] Add `calloutModel(for pin: MapPin) -> AnnotationCalloutView.Model` method to `FullscreenMapViewModel` in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapViewModel.swift` (creates model on demand for selected pin)
- [ ] T022 [US1] Update `FullscreenMapView` Map ForEach to embed callout inside `Annotation` content in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapView.swift` (use `ZStack(alignment: .bottom)` with callout above pin, conditional on `selectedPinId == pin.id`)
- [ ] T023 [US1] Add `.onTapGesture { viewModel.selectPin(pin.id) }` to `TeardropPin` in `FullscreenMapView` (FR-001, FR-011, FR-012)
- [ ] T024 [US1] Add `.onTapGesture { viewModel.deselectPin() }` to Map in `FullscreenMapView` (FR-010)
- [ ] T025 [US1] Set `Annotation` anchor to `.bottom` so coordinate points to pin tip in `FullscreenMapView` (FR-013)
- [ ] T026 [US1] Add `.offset(y: -10)` to callout in ZStack for gap between callout arrow and pin top in `FullscreenMapView`
- [ ] T027 [P] [US1] Add SwiftDoc comments to `AnnotationCalloutView`, `AnnotationCalloutView.Model.init`, and ViewModel selection methods (only if purpose not clear from name)

**Checkpoint**: At this point, User Story 1 should be fully functional - users can tap pins, see callout with all details, and dismiss via tap

---

## Phase 4: User Story 2 - View Animal Status Badge (Priority: P2)

**Goal**: Display current status (MISSING or FOUND) with appropriate color badge so users know if assistance is still needed

**Independent Test**: Tap pins for animals with different statuses, verify status badge displays with correct text and color (MISSING = orange #FF9500, FOUND = blue #155DFC)

**Acceptance Criteria**:
- FR-009: Status badge shows "MISSING" with orange background (#FF9500) or "FOUND" with blue background (#155DFC), white text

### Implementation for User Story 2

**Note**: User Story 2 is already implemented in User Story 1 (status badge is part of callout content). This phase validates the status-specific behavior.

**iOS**:

- [ ] T028 [US2] Verify `statusText` in `AnnotationCalloutView.Model` uses existing `pin.status.displayName` (reuses `L10n.AnnouncementStatus.active`/`.found` via `AnnouncementStatus+Presentation.swift`)
- [ ] T029 [US2] Verify `statusColorHex` in `AnnotationCalloutView.Model` uses `pin.status.annotationBadgeColorHex` returning spec colors (#FF9500 for MISSING, #155DFC for FOUND per FR-009)
- [ ] T030 [US2] Verify `statusBadge` view in `AnnotationCalloutView` applies background color from `model.statusColorHex` with white text and 12px border radius
- [ ] T031 [US2] Manual test: Create/fetch announcements with MISSING status, tap pin, verify orange badge with "MISSING" text
- [ ] T032 [US2] Manual test: Create/fetch announcements with FOUND status, tap pin, verify blue badge with "FOUND" text

**Checkpoint**: Status badge displays correctly with status-specific colors and text per FR-009

---

## Phase 5: User Story 3 - Handle Missing or Invalid Data Gracefully (Priority: P3)

**Goal**: Display annotation details gracefully when some information is missing, without UI breaking or becoming unusable

**Independent Test**: Create test announcements with missing fields (no photo, no description, no phone, no email), tap pins, verify annotation displays correctly with placeholders or omitted fields without crashing

**Acceptance Criteria**:
- FR-005: No pet photo → circular pawprint placeholder on gray background
- FR-006: No description → description field omitted
- FR-007: No phone → phone field omitted
- FR-008: No email → email field omitted

### Implementation for User Story 3

**Note**: User Story 3 graceful degradation is partially implemented in User Story 1. This phase adds explicit nil handling and validation.

**iOS**:

- [ ] T033 [US3] Update `AnnotationCalloutView.Model.init` to set `photoUrl = nil` when `pin.photoUrl.isEmpty` in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/AnnotationCalloutView_Model.swift` (FR-005)
- [ ] T034 [US3] Update `AnnotationCalloutView.Model.init` to use `pin.name ?? L10n.AnnotationCallout.unknownPet` for `petName` in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/AnnotationCalloutView_Model.swift`
- [ ] T035 [US3] Update `AnnotationCalloutView.Model.init` to handle nil breed: if `pin.breed == nil`, set `speciesAndBreed = speciesName` (omit breed part) in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/AnnotationCalloutView_Model.swift`
- [ ] T036 [US3] Update `AnnotationCalloutView.Model.init` to map optional fields: `emailText = pin.email.map { "📧 \($0)" }`, `phoneText = pin.phone.map { "📞 \($0)" }`, `descriptionText = pin.description` in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/AnnotationCalloutView_Model.swift` (FR-007, FR-008)
- [ ] T037 [US3] Verify `photoView` in `AnnotationCalloutView` shows placeholder when `model.photoUrl == nil` (AsyncImage failure case, FR-005)
- [ ] T038 [US3] Verify `cardContent` in `AnnotationCalloutView` conditionally renders email field only if `model.emailText != nil` (FR-008)
- [ ] T039 [US3] Verify `cardContent` in `AnnotationCalloutView` conditionally renders phone field only if `model.phoneText != nil` (FR-007)
- [ ] T040 [US3] Verify `cardContent` in `AnnotationCalloutView` conditionally renders description field only if `model.descriptionText != nil` (FR-006)
- [ ] T041 [US3] Manual test: Mock announcement with `photoUrl = ""`, verify placeholder (circular pawprint on #EEEEEE) displays immediately (no retry, no spinner)
- [ ] T042 [US3] Manual test: Mock announcement with `description = nil`, verify description field is omitted from callout
- [ ] T043 [US3] Manual test: Mock announcement with `phone = nil`, verify phone field is omitted from callout
- [ ] T044 [US3] Manual test: Mock announcement with `email = nil`, verify email field is omitted from callout
- [ ] T045 [US3] Manual test: Mock announcement with all optional fields nil, verify callout renders with only required fields (photo placeholder, name, species, location, date, status)

**Checkpoint**: All user stories (US1, US2, US3) should work independently and handle edge cases gracefully

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories or enhance code quality

- [ ] T046 [P] Extract `formatDate` from `AnnotationCalloutView.Model` to shared utility in `/iosApp/iosApp/FoundationAdditions/DateFormatting.swift`
- [ ] T047 [P] Extract `formatCoordinates` from `AnnotationCalloutView.Model` to shared utility in `/iosApp/iosApp/FoundationAdditions/CoordinateFormatting.swift`
- [ ] T048 Update `PetDetailsViewModel` to use shared `formatDate` and `formatCoordinates` utilities in `/iosApp/iosApp/Features/PetDetails/PetDetailsViewModel.swift` (remove duplicated formatters)
- [ ] T049 Update `AnnotationCalloutView.Model` to use shared `DateFormatting.formatDate` and `CoordinateFormatting.formatCoordinates` in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/AnnotationCalloutView_Model.swift`
- [ ] T050 [P] Run quickstart.md manual testing checklist (tap pin, toggle, switch, dismiss, missing fields, status badges)
- [ ] T051 [P] Add unit test for `FullscreenMapViewModel.selectPin` toggle behavior in `/iosApp/iosAppTests/Features/LandingPage/Views/FullscreenMap/FullscreenMapViewModelTests.swift` (Given selected pin A, When tap pin A, Then selectedPinId = nil)
- [ ] T052 [P] Add unit test for `FullscreenMapViewModel.selectPin` replace behavior in `/iosApp/iosAppTests/Features/LandingPage/Views/FullscreenMap/FullscreenMapViewModelTests.swift` (Given selected pin A, When tap pin B, Then selectedPinId = B)
- [ ] T053 [P] Add unit test for `AnnotationCalloutView.Model.init` field mapping in `/iosApp/iosAppTests/Features/LandingPage/Views/FullscreenMap/AnnotationCalloutViewModelTests.swift` (Given announcement data, When init model, Then fields formatted correctly)
- [ ] T054 [P] Add unit test for graceful nil handling in `AnnotationCalloutView.Model.init` in `/iosApp/iosAppTests/Features/LandingPage/Views/FullscreenMap/AnnotationCalloutViewModelTests.swift` (Given nil email/phone/description, When init model, Then optional fields = nil)
- [ ] T055 [P] Update `MIGRATION-COMPLETE.md` or add feature completion note (if tracking enabled)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-5)**: All depend on Foundational phase completion
  - Once Phase 2 is done, user stories CAN proceed sequentially (P1 → P2 → P3)
  - However, US2 and US3 are actually validation/edge-case phases extending US1
- **Polish (Phase 6)**: Depends on all user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - Core feature, no dependencies
- **User Story 2 (P2)**: Extends User Story 1 - validates status badge behavior (already implemented in US1)
- **User Story 3 (P3)**: Extends User Story 1 - validates graceful degradation (partially implemented in US1)

### Within Each User Story

- User Story 1: Build callout component → integrate with ViewModel → wire to View
- User Story 2: Validate existing status badge implementation
- User Story 3: Add explicit nil handling → validate edge cases

### Parallel Opportunities

- All Setup tasks (T001-T002) can run in parallel
- Foundational tasks with [P] marker (T006-T007, T010-T011, T012) can run in parallel
- Polish tasks with [P] marker (T046-T047, T050-T054) can run in parallel
- **Note**: User Stories 2 and 3 are validation phases, not independent features, so they follow US1 sequentially

---

## Parallel Example: Foundational Phase

```bash
# These foundational tasks can run simultaneously (different files):
T006: Implement selectPin method in FullscreenMapViewModel.swift
T007: Implement deselectPin method in FullscreenMapViewModel.swift
T010: Add formatDate helper in AnnotationCalloutView_Model.swift
T011: Add formatCoordinates helper in AnnotationCalloutView_Model.swift
T012: Add annotationBadgeColorHex to AnnouncementStatus extension

# Sequential dependencies:
T003 → T004 (same file: MapPin.swift)
T008 → T009 (same file: AnnotationCalloutView_Model.swift)
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (localization strings) → ~15 min
2. Complete Phase 2: Foundational (MapPin extension, ViewModel selection, Model factory) → ~2-3 hours
3. Complete Phase 3: User Story 1 (AnnotationCalloutView, integration) → ~4-6 hours
4. **STOP and VALIDATE**: Manual test checklist from quickstart.md
5. Ready to demo/deploy: Users can tap pins and see callout with all details

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready (~3 hours)
2. Add User Story 1 → Test independently → Deploy/Demo (MVP - core interaction) (~6 hours)
3. Add User Story 2 → Validate status badges → Deploy/Demo (~30 min)
4. Add User Story 3 → Validate edge cases → Deploy/Demo (~1 hour)
5. Polish → Extract shared utilities, optional tests (~2 hours)

**Total Estimated Time**: 10-12 hours (iOS only, single developer)

### Single Developer Strategy

**Day 1** (Setup + Foundational + Core Implementation):
1. Morning: Phase 1 + Phase 2 (T001-T012) → Foundation complete
2. Afternoon: Phase 3 User Story 1 implementation (T013-T027) → Core feature complete
3. End of day: Manual test checklist → MVP validation

**Day 2** (Validation + Polish):
1. Morning: Phase 4 User Story 2 (T028-T032) → Status validation
2. Morning: Phase 5 User Story 3 (T033-T045) → Edge case handling
3. Afternoon: Phase 6 Polish (T046-T055) → Code cleanup, optional tests

---

## Notes

- [P] tasks = different files, no dependencies, can run in parallel
- [Story] label maps task to specific user story for traceability
- iOS-only feature: no backend, Android, or Web tasks
- Tests are OPTIONAL (not explicitly requested in spec) - Phase 6 includes optional test tasks
- Manual testing checklist available in quickstart.md
- Commit after each logical group of tasks (per phase or per component)
- Stop at any checkpoint to validate story independently
- SwiftGen required for localization strings (constitution compliance)
- TODO comments for future formatter extraction (avoid coupling during initial implementation)

