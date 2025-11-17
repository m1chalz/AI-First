---

description: "Task list template for feature implementation"
---

# Tasks: [FEATURE NAME]

**Input**: Design documents from `/specs/[###-feature-name]/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Test requirements for this KMP project:

**MANDATORY - Shared Module Unit Tests**:
- Location: `/shared/src/commonTest`
- Coverage: 80% line + branch coverage
- Framework: Kotlin Test
- Scope: Domain models, use cases, business logic
- Convention: MUST follow Given-When-Then structure

**MANDATORY - ViewModel Unit Tests** (per platform):
- Android: `/composeApp/src/androidUnitTest/` (JUnit + Turbine), 80% coverage
- iOS: `/iosApp/iosAppTests/ViewModels/` (XCTest), 80% coverage  
- Web: `/webApp/src/__tests__/hooks/` (Vitest + RTL), 80% coverage
- Convention: MUST follow Given-When-Then structure with descriptive names

**MANDATORY - Backend Unit Tests** (if `/server` affected):
- Services: `/server/src/services/__test__/` (Vitest), 80% coverage
- Utilities: `/server/src/lib/__test__/` (Vitest), 80% coverage
- Framework: Vitest
- Scope: Business logic and utility functions
- TDD Workflow: Red-Green-Refactor cycle (write failing test, minimal implementation, refactor)
- Convention: MUST follow Given-When-Then structure with descriptive test names

**MANDATORY - Backend Integration Tests** (if `/server` affected):
- Location: `/server/src/__test__/`
- Coverage: 80% for REST API endpoints
- Framework: Vitest + SuperTest
- Scope: End-to-end API tests (request → response)
- Convention: MUST follow Given-When-Then structure

**MANDATORY - End-to-End Tests**:
- Web: `/e2e-tests/web/specs/[feature-name].spec.ts` (Playwright + TypeScript)
- Mobile: `/e2e-tests/mobile/specs/[feature-name].spec.ts` (Appium + TypeScript)
- All user stories MUST have E2E test coverage
- Use Page Object Model / Screen Object Model pattern
- Convention: MUST structure scenarios with Given-When-Then phases

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- **Single project**: `src/`, `tests/` at repository root
- **Web app**: `backend/src/`, `frontend/src/`
- **Mobile**: `api/src/`, `ios/src/` or `android/src/`
- Paths shown below assume single project - adjust based on plan.md structure

<!-- 
  ============================================================================
  IMPORTANT: The tasks below are SAMPLE TASKS for illustration purposes only.
  
  The /speckit.tasks command MUST replace these with actual tasks based on:
  - User stories from spec.md (with their priorities P1, P2, P3...)
  - Feature requirements from plan.md
  - Entities from data-model.md
  - Endpoints from contracts/
  
  Tasks MUST be organized by user story so each story can be:
  - Implemented independently
  - Tested independently
  - Delivered as an MVP increment
  
  DO NOT keep these sample tasks in the generated tasks.md file.
  ============================================================================
-->

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 Create project structure per implementation plan
- [ ] T002 Add Koin dependencies to shared module `build.gradle.kts`
- [ ] T003 [P] Add Koin dependencies to Android module `build.gradle.kts`
- [ ] T004 [P] Configure linting and formatting tools

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

Examples of foundational tasks (adjust based on your project):

- [ ] T005 Setup E2E test infrastructure (Playwright config, Appium setup, TypeScript configs)
- [ ] T006 [P] Create base Page Objects for web in `/e2e-tests/web/pages/base/`
- [ ] T007 [P] Create base Screen Objects for mobile in `/e2e-tests/mobile/screens/base/`
- [ ] T008 Create domain module in `/shared/src/commonMain/.../di/DomainModule.kt`
- [ ] T009 Initialize Koin in Android Application class
- [ ] T010 [P] Initialize Koin in iOS app entry point
- [ ] T011 [P] Setup shared error handling and Result types
- [ ] T012 Setup environment configuration management
- [ ] T012a [P] Setup backend ESLint config in `/server/.eslintrc.js` (@typescript-eslint/eslint-plugin)
- [ ] T012b [P] Setup backend Vitest config in `/server/vitest.config.ts` (coverage thresholds: 80%)
- [ ] T012c [P] Setup backend database config in `/server/src/database/config.ts` (Knex + SQLite)
- [ ] T012d [P] Create initial database migration setup script

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - [Title] (Priority: P1) 🎯 MVP

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 1 (MANDATORY) ✅

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

**Shared Module Unit Tests**:
- [ ] T013 [P] [US1] Unit test for [UseCase] in `/shared/src/commonTest/.../[UseCase]Test.kt` (use Koin Test)
- [ ] T014 [P] [US1] Unit test for [Model] in `/shared/src/commonTest/.../[Model]Test.kt`
- [ ] T015 [P] [US1] Create fake [Repository] in `/shared/src/commonTest/.../fakes/Fake[Repository].kt`

**ViewModel Unit Tests**:
- [ ] T016 [P] [US1] Android ViewModel test in `/composeApp/src/androidUnitTest/.../[ViewModel]Test.kt`
- [ ] T017 [P] [US1] iOS ViewModel test in `/iosApp/iosAppTests/ViewModels/[ViewModel]Tests.swift`
- [ ] T018 [P] [US1] Web hook test in `/webApp/src/__tests__/hooks/use[Feature].test.ts`

**Backend Unit Tests** (TDD: Red-Green-Refactor):
- [ ] T018a [P] [US1] Unit test for service in `/server/src/services/__test__/[Service].test.ts` (Vitest, Given-When-Then)
- [ ] T018b [P] [US1] Unit test for utility in `/server/src/lib/__test__/[util].test.ts` (Vitest, Given-When-Then)

**Backend Integration Tests** (TDD: Red-Green-Refactor):
- [ ] T018c [P] [US1] Integration test for API endpoint in `/server/src/__test__/[endpoint].test.ts` (Vitest + SuperTest, Given-When-Then)

**End-to-End Tests**:
- [ ] T019 [P] [US1] Web E2E test in `/e2e-tests/web/specs/[feature-name].spec.ts`
- [ ] T020 [P] [US1] Mobile E2E test in `/e2e-tests/mobile/specs/[feature-name].spec.ts`
- [ ] T021 [P] [US1] Page Objects for US1 in `/e2e-tests/web/pages/[Feature]Page.ts`
- [ ] T022 [P] [US1] Screen Objects for US1 in `/e2e-tests/mobile/screens/[Feature]Screen.ts`

### Implementation for User Story 1

**Shared Module**:
- [ ] T023 [P] [US1] Create [Entity] model in `/shared/src/commonMain/.../models/[Entity].kt`
- [ ] T024 [P] [US1] Create [Repository] interface in `/shared/src/commonMain/.../repositories/[Repository].kt`
- [ ] T025 [US1] Implement [UseCase] in `/shared/src/commonMain/.../usecases/[UseCase].kt`
- [ ] T026 [US1] Add use case to domain DI module in `/shared/src/commonMain/.../di/DomainModule.kt`
- [ ] T027 [P] [US1] Add KDoc documentation to all public APIs in shared module (models, repositories, use cases)

**Android**:
- [ ] T027 [US1] Implement repository in `/composeApp/src/androidMain/.../data/[Repository]Impl.kt`
- [ ] T028 [US1] Add repository to Android DI module in `/composeApp/src/androidMain/.../di/DataModule.kt`
- [ ] T029 [US1] Create ViewModel in `/composeApp/src/androidMain/.../viewmodels/[Feature]ViewModel.kt`
- [ ] T030 [US1] Add ViewModel to DI module in `/composeApp/src/androidMain/.../di/ViewModelModule.kt`
- [ ] T031 [US1] Create Composable UI in `/composeApp/src/androidMain/.../ui/[Feature]Screen.kt`
- [ ] T032 [US1] Add testTag modifiers to all interactive composables in [Feature]Screen (e.g., `Modifier.testTag("[screen].[element].[action]")`)
- [ ] T033 [P] [US1] Add KDoc documentation to Android ViewModel and repository public methods

**iOS**:
- [ ] T034 [US1] Implement repository in `/iosApp/iosApp/Repositories/[Repository].swift`
- [ ] T035 [US1] Add repository to iOS DI module in `/iosApp/iosApp/DI/DataModule.swift`
- [ ] T036 [US1] Create ViewModel in `/iosApp/iosApp/ViewModels/[Feature]ViewModel.swift`
- [ ] T037 [US1] Create SwiftUI view in `/iosApp/iosApp/Views/[Feature]View.swift`
- [ ] T038 [US1] Add accessibilityIdentifier to all interactive views in [Feature]View (e.g., `.accessibilityIdentifier("[screen].[element].[action]")`)
- [ ] T039 [P] [US1] Add SwiftDoc documentation to iOS ViewModel and repository public methods

**Web**:
- [ ] T040 [US1] Create service consuming shared in `/webApp/src/services/[feature]Service.ts`
- [ ] T041 [US1] Create custom hook in `/webApp/src/hooks/use[Feature].ts`
- [ ] T042 [US1] Create React component in `/webApp/src/components/[Feature]/[Feature].tsx`
- [ ] T043 [US1] Add data-testid attributes to all interactive elements in [Feature] component (e.g., `data-testid="[screen].[element].[action]"`)
- [ ] T044 [P] [US1] Add JSDoc documentation to Web service and hook public functions

**Backend** (TDD: Red-Green-Refactor):
- [ ] T045 [P] [US1] RED: Write failing unit test for [Service] in `/server/src/services/__test__/[Service].test.ts`
- [ ] T046 [US1] GREEN: Implement [Service] in `/server/src/services/[Service].ts` (minimal code to pass test)
- [ ] T047 [US1] REFACTOR: Improve [Service] code quality (extract helpers, apply Clean Code principles)
- [ ] T048 [P] [US1] RED: Write failing unit test for utility in `/server/src/lib/__test__/[util].test.ts`
- [ ] T049 [P] [US1] GREEN: Implement utility in `/server/src/lib/[util].ts` (minimal code to pass test)
- [ ] T050 [US1] Create database repository in `/server/src/database/repositories/[Repository].ts` (Knex queries)
- [ ] T051 [US1] Create Express router in `/server/src/routes/[feature]Routes.ts` (endpoint definitions)
- [ ] T052 [US1] RED: Write failing integration test for endpoint in `/server/src/__test__/[endpoint].test.ts` (SuperTest)
- [ ] T053 [US1] GREEN: Wire up route to service in `/server/src/app.ts` (minimal code to pass test)
- [ ] T054 [US1] REFACTOR: Add error handling middleware for [feature] routes
- [ ] T055 [P] [US1] Add JSDoc documentation to all public functions (services, lib, repositories)
- [ ] T056 [US1] Run `npm test -- --coverage` and verify 80% coverage for services and lib
- [ ] T057 [P] [US1] Run `npm run lint` and fix ESLint violations

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - [Title] (Priority: P2)

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 2 (MANDATORY) ✅

**Shared Module Unit Tests**:
- [ ] T039 [P] [US2] Unit test for [UseCase] in `/shared/src/commonTest/.../[UseCase]Test.kt` (use Koin Test)

**ViewModel Unit Tests**:
- [ ] T040 [P] [US2] Android ViewModel test in `/composeApp/src/androidUnitTest/.../[ViewModel]Test.kt`
- [ ] T041 [P] [US2] iOS ViewModel test in `/iosApp/iosAppTests/ViewModels/[ViewModel]Tests.swift`
- [ ] T042 [P] [US2] Web hook test in `/webApp/src/__tests__/hooks/use[Feature].test.ts`

**End-to-End Tests**:
- [ ] T043 [P] [US2] Web E2E test in `/e2e-tests/web/specs/[feature-name].spec.ts`
- [ ] T044 [P] [US2] Mobile E2E test in `/e2e-tests/mobile/specs/[feature-name].spec.ts`

### Implementation for User Story 2

- [ ] T045 [P] [US2] Create [Entity] model in `/shared/src/commonMain/.../models/[Entity].kt`
- [ ] T046 [P] [US2] Create [Repository] interface in `/shared/src/commonMain/.../repositories/[Repository].kt`
- [ ] T047 [US2] Implement [UseCase] in `/shared/src/commonMain/.../usecases/[UseCase].kt`
- [ ] T048 [US2] Add to domain DI module
- [ ] T049 [P] [US2] Add KDoc documentation to all US2 shared APIs
- [ ] T050 [US2] Implement Android repository + DI
- [ ] T051 [US2] Implement Android ViewModel + DI + UI
- [ ] T052 [US2] Add testTag modifiers to all Android UI elements for US2
- [ ] T053 [P] [US2] Add KDoc documentation to US2 Android APIs
- [ ] T054 [US2] Implement iOS repository + DI + ViewModel + SwiftUI
- [ ] T055 [US2] Add accessibilityIdentifier to all iOS UI elements for US2
- [ ] T056 [P] [US2] Add SwiftDoc documentation to US2 iOS APIs
- [ ] T057 [US2] Implement Web service + hook + React component
- [ ] T058 [US2] Add data-testid attributes to all Web UI elements for US2
- [ ] T059 [P] [US2] Add JSDoc documentation to US2 Web APIs

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - [Title] (Priority: P3)

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 3 (OPTIONAL - only if tests requested) ⚠️

- [ ] T024 [P] [US3] Contract test for [endpoint] in tests/contract/test_[name].py
- [ ] T025 [P] [US3] Integration test for [user journey] in tests/integration/test_[name].py

### Implementation for User Story 3

- [ ] T026 [P] [US3] Create [Entity] model in src/models/[entity].py
- [ ] T027 [US3] Implement [Service] in src/services/[service].py
- [ ] T028 [US3] Implement [endpoint/feature] in src/[location]/[file].py

**Checkpoint**: All user stories should now be independently functional

---

[Add more user story phases as needed, following the same pattern]

---

## Phase N: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] TXXX [P] Documentation updates in docs/
- [ ] TXXX Code cleanup and refactoring
- [ ] TXXX Performance optimization across all stories
- [ ] TXXX [P] Additional unit tests (if requested) in tests/unit/
- [ ] TXXX Security hardening
- [ ] TXXX Run quickstart.md validation

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3)
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - May integrate with US1 but should be independently testable
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - May integrate with US1/US2 but should be independently testable

### Within Each User Story

- Tests (if included) MUST be written and FAIL before implementation
- Models before services
- Services before endpoints
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Once Foundational phase completes, all user stories can start in parallel (if team capacity allows)
- All tests for a user story marked [P] can run in parallel
- Models within a story marked [P] can run in parallel
- Different user stories can be worked on in parallel by different team members

---

## Parallel Example: User Story 1

```bash
# Launch all tests for User Story 1 together (if tests requested):
Task: "Contract test for [endpoint] in tests/contract/test_[name].py"
Task: "Integration test for [user journey] in tests/integration/test_[name].py"

# Launch all models for User Story 1 together:
Task: "Create [Entity1] model in src/models/[entity1].py"
Task: "Create [Entity2] model in src/models/[entity2].py"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test User Story 1 independently
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → Test independently → Deploy/Demo
4. Add User Story 3 → Test independently → Deploy/Demo
5. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1
   - Developer B: User Story 2
   - Developer C: User Story 3
3. Stories complete and integrate independently

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests fail before implementing
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
