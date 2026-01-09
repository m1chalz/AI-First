---
description: "Dependency-ordered implementation tasks for KAN-34 (iOS-only)"
---

# Tasks: iOS Send report data to backend (status by flow)

**Input**: Design documents from `/specs/KAN-34-send-data-to-backend/`
**Prerequisites**: `plan.md` (required), `spec.md` (required), `research.md`, `data-model.md`, `contracts/`, `quickstart.md`

**Tests (MANDATORY - iOS Unit Tests)**:
- Location: `/iosApp/iosAppTests/`
- Coverage: 80% line + branch coverage (platform requirement)
- Run:
  - `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES`
- Convention: MUST follow Given-When-Then structure with descriptive test names

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Phase 1: Setup (Project Readiness)

**Purpose**: Confirm baseline and align on contract before changing code

- [X] T001 Confirm baseline iOS tests pass (run xcodebuild from `/iosApp/`)

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Implement shared status-by-flow plumbing required by BOTH user stories (compile-safe, no hardcoded status)

- [X] T002 Update domain model to include status in `iosApp/iosApp/Domain/Models/CreateAnnouncementData.swift`
- [X] T003 Update flow state contract to expose status in `iosApp/iosApp/Features/ReportMissingAndFoundPet/Common/Models/PetReportFlowStateProtocol.swift`
- [X] T004 [P] Implement Missing flow status (.active) in `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportMissingPet/Models/MissingPetReportFlowState.swift`
- [X] T005 [P] Implement Found flow status (.found) in `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/Models/FoundPetReportFlowState.swift`
- [X] T006 Pass flowState.status into CreateAnnouncementData in `iosApp/iosApp/Domain/Services/AnnouncementSubmissionService.swift`
- [X] T007 Remove hardcoded status; map from data.status in `iosApp/iosApp/Data/Mappers/CreateAnnouncementMapper.swift`

**Checkpoint**: App compiles; status is no longer hardcoded; mapper and service compile with new `status` field.

---

## Phase 3: User Story 1 - Submit a Missing report to backend (Priority: P1) 🎯 MVP

**Goal**: Missing flow submissions are sent with status representing “missing” (iOS domain: `.active` → backend `"MISSING"`).

**Independent Test**: With a Missing flow state, the built request DTO (and JSON) includes `"status": "MISSING"` and submission uses `.active` internally.

### Tests for User Story 1 (iOS Unit Tests) ✅

- [X] T008 [US1] Assert Missing flow passes `.active` into CreateAnnouncementData in `iosApp/iosAppTests/Features/ReportMissingAndFoundPet/Common/Services/AnnouncementSubmissionServiceTests.swift`
- [X] T009 [US1] Create mapper unit tests for dynamic status mapping in `iosApp/iosAppTests/Data/Mappers/CreateAnnouncementMapperTests.swift`

**Checkpoint**: Missing flow test(s) pass; mapper no longer hardcodes `.active`; status maps to backend `"MISSING"`.

---

## Phase 4: User Story 2 - Submit a Found report to backend (Priority: P1)

**Goal**: Found flow submissions are sent with status representing “found” (iOS domain: `.found` → backend `"FOUND"`) and payload contains ONLY backend-recognized fields.

**Independent Test**: With a Found flow state, the built request DTO (and JSON) includes `"status": "FOUND"` and does not include any iOS-only keys.

### Tests for User Story 2 (iOS Unit Tests) ✅

- [X] T010 [US2] Add Found-flow submission test asserting `.found` status is passed through in `iosApp/iosAppTests/Features/ReportMissingAndFoundPet/Common/Services/AnnouncementSubmissionServiceTests.swift`
- [X] T011 [US2] Extend mapper tests for `.found` → `"FOUND"` mapping in `iosApp/iosAppTests/Data/Mappers/CreateAnnouncementMapperTests.swift`
- [X] T012 [US2] Add strict payload-shape test (JSON keys == backend schema keys) in `iosApp/iosAppTests/Data/Mappers/CreateAnnouncementMapperTests.swift`

**Checkpoint**: Found flow test(s) pass; request JSON has only allowed keys; status maps to backend `"FOUND"`.

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Ensure test/coverage expectations are met and spec tables are updated post-tasking

- [X] T013 Run full iOS unit test suite with coverage and confirm ≥80% in `/iosApp/iosAppTests/`
- [X] T014 Update estimation tables after TASKS in `specs/KAN-34-send-data-to-backend/spec.md` and `specs/KAN-34-send-data-to-backend/plan.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3 & 4)**: Depend on Foundational completion
- **Polish (Phase 5)**: Depends on completion of desired user stories

### User Story Dependencies

- **US1 (P1)**: Depends on Phase 2 only
- **US2 (P1)**: Depends on Phase 2 only

### Parallel Opportunities

- Tasks marked **[P]** can be executed in parallel (different files, no dependencies once Phase 2 starts).

---

## Parallel Example: After Foundational (Phase 2)

```bash
# After T002-T003 are done, T004 and T005 can be implemented in parallel:
Task: "Implement Missing flow status in MissingPetReportFlowState.swift"
Task: "Implement Found flow status in FoundPetReportFlowState.swift"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1 and Phase 2
2. Complete Phase 3 (US1)
3. STOP and validate US1 independently (unit tests + optional manual backend verification)

### Incremental Delivery

1. Land Phase 2 once (shared refactor)
2. Validate US1 (Missing)
3. Validate US2 (Found + strict payload)
4. Close with coverage and docs (Phase 5)


