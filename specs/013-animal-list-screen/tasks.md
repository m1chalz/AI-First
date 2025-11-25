# Tasks: Android Animal List Screen Layout Update

**Input**: Design documents from `/specs/013-animal-list-screen/`  
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md  
**Figma Design**: [Missing animals list app](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-7556&m=dev)

**Tests**: This feature is a UI visual refresh. Existing unit tests should continue to pass. E2E test selectors need updating to use new test tags.

**Organization**: Tasks are organized by user story to enable independent implementation and testing.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: User Story 1 - View updated list of missing animals (Priority: P1) 🎯 MVP

**Goal**: Update the Android Animal List screen to match the new Figma design with "PetSpot" title and restructured card layout.

**Independent Test**: Launch the app and verify:
- "PetSpot" title is left-aligned at top
- Cards show photo (left), location/species info (middle), status/date (right)
- List is scrollable with proper spacing

### Implementation for User Story 1

**Resources & Fonts**:
- [ ] T001 [P] [US1] Download Hind Regular font from Google Fonts and add to `/composeApp/src/androidMain/res/font/hind_regular.ttf`
- [ ] T002 [P] [US1] Create `HindFontFamily` constant in a new file `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/ui/theme/Typography.kt` or existing theme file

**AnimalCard Restructure**:
- [ ] T003 [US1] Update `AnimalCard` border: change from `RoundedCornerShape(4.dp)` to `RoundedCornerShape(14.dp)` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalCard.kt`
- [ ] T004 [US1] Update `AnimalCard` border: add `border = BorderStroke(1.dp, Color(0xFFE5E9EC))` and remove `elevation` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalCard.kt`
- [ ] T005 [US1] Update `AnimalCard` height: set fixed height of 100.dp via `Modifier.height(100.dp)` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalCard.kt`
- [ ] T006 [US1] Update photo size from 63.dp to 64.dp in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalCard.kt`
- [ ] T007 [US1] Restructure `AnimalCard` layout to three-column Row: photo (left), info (middle), status/date (right) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalCard.kt`
- [ ] T008 [US1] Create location row: add `Icon(Icons.Default.LocationOn)` + location + "•" + distance with color #4A5565, 13sp in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalCard.kt`
- [ ] T009 [US1] Create species/breed row: Species + "•" + Breed with color #101828, 14sp in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalCard.kt`
- [ ] T010 [US1] Move status badge to right column with updated colors: MISSING=#FF0000, FOUND=#155DFC, pill shape, white text 13sp in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalCard.kt`
- [ ] T011 [US1] Move date below status badge, right-aligned, color #6A7282, 14sp, format "DD/MM/YYYY" in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalCard.kt`
- [ ] T012 [US1] Update test tag from `animalList.item.${animal.id}` to `animalList.cardItem` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalCard.kt`

**AnimalListContent Updates**:
- [ ] T013 [US1] Remove `CenterAlignedTopAppBar` composable and `AnimalListTopBar` function in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalListContent.kt`
- [ ] T014 [US1] Replace Scaffold topBar with Box layout containing Column for title and list in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalListContent.kt`
- [ ] T015 [US1] Add "PetSpot" title: left-aligned, Hind font 32sp, color rgba(0,0,0,0.8) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalListContent.kt`
- [ ] T016 [US1] Update list horizontal padding from 16.dp to 23.dp in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalListContent.kt`
- [ ] T017 [US1] Add 24.dp spacer between title and list in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalListContent.kt`
- [ ] T018 [US1] Update list item spacing from 8.dp (keep as is - matches design) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalListContent.kt`

**Previews**:
- [ ] T019 [P] [US1] Update `AnimalCardPreview` to show new card layout in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalCard.kt`
- [ ] T020 [P] [US1] Update `AnimalListContentPreview` to reflect new title and layout in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalListContent.kt`

**Checkpoint**: User Story 1 complete - Screen shows "PetSpot" title and restructured cards

---

## Phase 2: User Story 2 - Access primary call-to-action (Priority: P2)

**Goal**: Replace the full-width bottom bar with a floating pill-shaped button.

**Independent Test**: Verify that:
- Button is floating at bottom-center of screen
- Button shows "Report a Missing Animal" with icon
- Button has blue background (#155DFC) and pill shape
- Tapping button triggers existing flow

### Implementation for User Story 2

**Button Redesign**:
- [ ] T021 [US2] Remove `ReportMissingBottomBar` composable and Scaffold bottomBar in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalListContent.kt`
- [ ] T022 [US2] Create `FloatingReportButton` composable with pill shape (22.dp radius), blue background (#155DFC), shadow in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalListContent.kt`
- [ ] T023 [US2] Add button text "Report a Missing Animal" with Hind font 14sp, white color in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalListContent.kt`
- [ ] T024 [US2] Add trailing icon to button (custom drawable exported from Figma design) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalListContent.kt`
- [ ] T025 [US2] Position button at bottom-center using `Box` with `Alignment.BottomCenter` and bottom padding in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalListContent.kt`
- [ ] T026 [US2] Update test tag from `animalList.reportMissingButton` to `animalList.reportButton` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalListContent.kt`
- [ ] T027 [US2] Verify button onClick still calls `onReportMissing` callback without changes in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalListContent.kt`

**Checkpoint**: User Story 2 complete - Floating button works correctly

---

## Phase 3: User Story 3 - Preserve behaviour while refreshing visuals (Priority: P3)

**Goal**: Ensure all existing states (loading, empty, error) work with updated layout.

**Independent Test**: Trigger each state and verify:
- Loading indicator appears centered
- Empty state message displays
- Error state with retry button works
- Button remains accessible during all states

### Implementation for User Story 3

**State Verification**:
- [ ] T028 [US3] Update loading indicator positioning to work with new Box-based layout (centered in content area) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalListContent.kt`
- [ ] T029 [US3] Update empty state styling to align with new padding (23.dp horizontal) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalListContent.kt`
- [ ] T030 [US3] Update error state styling to align with new padding in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalListContent.kt`
- [ ] T031 [US3] Verify floating button remains visible/accessible during loading, empty, and error states in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalListContent.kt`
- [ ] T032 [P] [US3] Update `PreviewParameterProvider` to include all states with new visual styling in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalListContent.kt`

**Checkpoint**: User Story 3 complete - All states work with updated visuals

---

## Phase 4: Polish & Cross-Cutting Concerns

**Purpose**: Final validation, testing, and documentation

### Testing
- [ ] T033 [P] Run Android unit tests: `./gradlew :composeApp:testDebugUnitTest` and verify all tests pass
- [ ] T034 [P] Run Android coverage report: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport` and verify coverage maintained
- [ ] T035 [P] Update mobile E2E test selectors to use `animalList.cardItem` and `animalList.reportButton` in `/e2e-tests/mobile/specs/` or `/e2e-tests/mobile/screens/`
- [ ] T036 [P] Run mobile E2E tests: `npm run test:mobile:android` and verify Animal List scenarios pass

### Visual Review
- [ ] T037 [P] Compare implemented screen against Figma design (node-id=297-7556) for title styling
- [ ] T038 [P] Compare implemented cards against Figma design for layout and colors
- [ ] T039 [P] Compare implemented button against Figma design for shape and positioning

### Manual Testing
- [ ] T040 [P] Verify "PetSpot" title displays correctly
- [ ] T041 [P] Verify card layout: photo, location row, species row, status badge, date
- [ ] T042 [P] Verify card styling: border radius, border color, no shadow
- [ ] T043 [P] Verify button: floating, pill-shaped, blue, icon
- [ ] T044 [P] Verify all screen states (loading, populated, empty, error)
- [ ] T045 [P] Verify scrolling behavior with many items
- [ ] T046 [P] Verify navigation to animal details still works
- [ ] T047 [P] Verify navigation to report missing still works
- [ ] T048 [P] Verify iOS and Web remain unaffected

### Documentation
- [ ] T049 [P] Update KDoc comments in `AnimalCard.kt` to reflect new design specifications
- [ ] T050 [P] Update KDoc comments in `AnimalListContent.kt` if needed

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (US1)**: Can start immediately - includes font resources and card/layout changes
- **Phase 2 (US2)**: Can start after Scaffold removal in US1, or in parallel with card work
- **Phase 3 (US3)**: Should start after US1 and US2 layout changes are complete
- **Phase 4 (Polish)**: Depends on all user stories being complete

### Task Dependencies within Phases

**Phase 1**:
- T001-T002 (fonts) can run in parallel with all other tasks
- T003-T012 (card) must be sequential (layout restructure)
- T013-T018 (content) must be sequential (Scaffold → Box)
- T019-T020 (previews) can run in parallel after main changes

**Phase 2**:
- T021 must complete before T022-T027
- T022-T026 can be done sequentially in one commit
- T027 verification can be last

**Phase 3**:
- T028-T031 depend on new Box layout from US1/US2
- T032 can run in parallel

### Parallel Opportunities

- T001, T002 (fonts) ↔ T003-T012 (card changes)
- T019, T020 (previews) ↔ T021-T027 (button changes)
- All T033-T050 (polish) can run in parallel

---

## Implementation Strategy

### MVP First (User Story 1)

1. Complete T001-T002: Add Hind font
2. Complete T003-T012: Restructure AnimalCard
3. Complete T013-T018: Update AnimalListContent layout
4. **STOP and VALIDATE**: Visual review, scrolling, test tags
5. Deploy/demo MVP

### Incremental Delivery

1. Add US1 → Visual review → Demo (MVP!)
2. Add US2 → Verify button → Demo
3. Add US3 → Verify states → Demo
4. Polish → Final validation → Deploy

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story
- UI-only refresh - no ViewModel, repository, or backend changes
- All existing behaviour must be preserved
- Required test tags: `animalList.cardItem`, `animalList.reportButton`
- Hind font required for title and button text
- Verify iOS and Web remain unaffected throughout implementation
- Commit after each phase checkpoint
