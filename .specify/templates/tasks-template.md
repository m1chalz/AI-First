---

description: "Task list template for feature implementation"
---

# Tasks: [FEATURE NAME]

**Input**: Design documents from `/specs/[###-feature-name]/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Test requirements for this project:

**MANDATORY - Platform-Specific Unit Tests** (per platform):
- Android: `/composeApp/src/androidUnitTest/` (JUnit + Turbine), 80% coverage
  - Scope: Domain models, use cases, ViewModels (MVI architecture)
  - Run: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
- iOS: `/iosApp/iosAppTests/` (XCTest), 80% coverage
  - Scope: Domain models, use cases, ViewModels (ObservableObject)
  - Run: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
- Web: `/webApp/src/__tests__/` (Vitest + RTL), 80% coverage
  - Scope: Domain models, services, custom hooks, state management
  - Run: `npm test -- --coverage` (from webApp/)
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
- [ ] T002 [P] Add Android dependencies to `/composeApp/build.gradle.kts`
- [ ] T003 [P] Add iOS dependencies to `/iosApp/Podfile` or Swift Package Manager
- [ ] T004 [P] Add Web dependencies to `/webApp/package.json`
- [ ] T005 [P] Configure linting and formatting tools per platform

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

Examples of foundational tasks (adjust based on your project):

- [ ] T006 Setup E2E test infrastructure (Playwright config, Appium setup, TypeScript configs)
- [ ] T007 [P] Create base Page Objects for web in `/e2e-tests/web/pages/base/`
- [ ] T008 [P] Create base Screen Objects for mobile in `/e2e-tests/mobile/screens/base/`
- [ ] T009 [P] Setup Android Koin DI modules in `/composeApp/src/androidMain/.../di/` (Koin mandatory)
- [ ] T010 [P] Setup Android Navigation Component in `/composeApp/src/androidMain/.../navigation/` (NavHost with declarative graph)
- [ ] T011 [P] Setup iOS manual DI (ServiceContainer with constructor injection) in `/iosApp/iosApp/DI/`
- [ ] T012 [P] Setup Web DI infrastructure in `/webApp/src/di/` (React Context recommended)
- [ ] T013 [P] Setup platform-specific error handling and Result types (per platform)
- [ ] T014 [P] Setup environment configuration management per platform
- [ ] T015 [P] Setup backend ESLint config in `/server/.eslintrc.js` (@typescript-eslint/eslint-plugin)
- [ ] T016 [P] Setup backend Vitest config in `/server/vitest.config.ts` (coverage thresholds: 80%)
- [ ] T017 [P] Setup backend database config in `/server/src/database/config.ts` (Knex + SQLite)
- [ ] T018 [P] Create initial database migration setup script

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - [Title] (Priority: P1) 🎯 MVP

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 1 (MANDATORY) ✅

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

**Android Unit Tests**:
- [ ] T019 [P] [US1] Unit test for [UseCase] in `/composeApp/src/androidUnitTest/.../domain/usecases/[UseCase]Test.kt`
- [ ] T020 [P] [US1] Unit test for [Model] in `/composeApp/src/androidUnitTest/.../domain/models/[Model]Test.kt`
- [ ] T021 [P] [US1] Create fake [Repository] in `/composeApp/src/androidUnitTest/.../fakes/Fake[Repository].kt`
- [ ] T022 [P] [US1] Android MVI ViewModel tests in `/composeApp/src/androidUnitTest/.../presentation/[ViewModel]Test.kt` (assert `StateFlow<UiState>` + `SharedFlow<UiEffect>` with Turbine)

**iOS Unit Tests**:
- [ ] T023 [P] [US1] Unit test for [Model] in `/iosApp/iosAppTests/Domain/Models/[Model]Tests.swift`
- [ ] T024 [P] [US1] Create fake [Repository] in `/iosApp/iosAppTests/Fakes/Fake[Repository].swift`
- [ ] T025 [P] [US1] iOS ViewModel test in `/iosApp/iosAppTests/ViewModels/[ViewModel]Tests.swift` (test repository calls directly)

**Web Unit Tests**:
- [ ] T026 [P] [US1] Unit test for service in `/webApp/src/__tests__/services/[Service].test.ts`
- [ ] T027 [P] [US1] Unit test for model in `/webApp/src/__tests__/models/[Model].test.ts`
- [ ] T028 [P] [US1] Web hook test in `/webApp/src/__tests__/hooks/use[Feature].test.ts`

**Backend Unit Tests** (TDD: Red-Green-Refactor):
- [ ] T029 [P] [US1] Unit test for service in `/server/src/services/__test__/[Service].test.ts` (Vitest, Given-When-Then)
- [ ] T030 [P] [US1] Unit test for utility in `/server/src/lib/__test__/[util].test.ts` (Vitest, Given-When-Then)

**Backend Integration Tests** (TDD: Red-Green-Refactor):
- [ ] T031 [P] [US1] Integration test for API endpoint in `/server/src/__test__/[endpoint].test.ts` (Vitest + SuperTest, Given-When-Then)

**End-to-End Tests**:
- [ ] T032 [P] [US1] Web E2E test in `/e2e-tests/web/specs/[feature-name].spec.ts`
- [ ] T033 [P] [US1] Mobile E2E test in `/e2e-tests/mobile/specs/[feature-name].spec.ts`
- [ ] T034 [P] [US1] Page Objects for US1 in `/e2e-tests/web/pages/[Feature]Page.ts`
- [ ] T035 [P] [US1] Screen Objects for US1 in `/e2e-tests/mobile/screens/[Feature]Screen.ts`

### Implementation for User Story 1

> **Note**: For backend-only features, SKIP Android, iOS, and Web sections below. Only implement Backend tasks and backend-specific tests. Update E2E tests only if API endpoints need E2E coverage.

**Android** (Full Stack Implementation):
- [ ] T036 [P] [US1] Create [Entity] model in `/composeApp/src/androidMain/.../domain/models/[Entity].kt`
- [ ] T037 [P] [US1] Create [Repository] interface in `/composeApp/src/androidMain/.../domain/repositories/[Repository].kt`
- [ ] T038 [US1] Implement [UseCase] in `/composeApp/src/androidMain/.../domain/usecases/[UseCase].kt`
- [ ] T039 [US1] Implement repository in `/composeApp/src/androidMain/.../data/repositories/[Repository]Impl.kt`
- [ ] T040 [US1] Add repository to Android DI module in `/composeApp/src/androidMain/.../di/DataModule.kt`
- [ ] T041 [US1] Create MVI artifacts (immutable `UiState`, sealed `UserIntent`, optional `UiEffect`, reducer) and ViewModel in `/composeApp/src/androidMain/.../presentation/[Feature]/`
- [ ] T042 [US1] Add ViewModel to DI module in `/composeApp/src/androidMain/.../di/ViewModelModule.kt`
- [ ] T043 [US1] Create state host composable `[Feature]Screen` in `/composeApp/src/androidMain/.../ui/[Feature]Screen.kt` (collects state, dispatches intents)
- [ ] T044 [US1] Create stateless composable `[Feature]Content` in same file (pure presentation, no ViewModel dependency)
- [ ] T045 [US1] Create `PreviewParameterProvider<[Feature]UiState>` with sample states (loading, success, error)
- [ ] T046 [US1] Add `@Preview` function for `[Feature]Content` using `@PreviewParameter` (light mode only, callbacks defaulted to no-ops)
- [ ] T047 [US1] Add Navigation Component route for [Feature] in `/composeApp/src/androidMain/.../navigation/NavGraph.kt` (NavHost configuration)
- [ ] T048 [US1] Add testTag modifiers to all interactive composables in [Feature]Content (e.g., `Modifier.testTag("[screen].[element].[action]")`)
- [ ] T049 [P] [US1] Add KDoc documentation to complex Android APIs (skip self-explanatory methods/properties)

**iOS** (Full Stack Implementation - NO use cases, ViewModels call repositories directly):
- [ ] T050 [P] [US1] Create [Entity] model in `/iosApp/iosApp/Domain/Models/[Entity].swift`
- [ ] T051 [P] [US1] Create [Repository] protocol in `/iosApp/iosApp/Domain/Repositories/[Repository].swift`
- [ ] T052 [US1] Implement repository in `/iosApp/iosApp/Data/Repositories/[Repository]Impl.swift`
- [ ] T053 [US1] Add repository to iOS manual DI in `/iosApp/iosApp/DI/ServiceContainer.swift`
- [ ] T054 [US1] Create ViewModel in `/iosApp/iosApp/ViewModels/[Feature]ViewModel.swift` (inject repository, NO use case)
- [ ] T055 [US1] Create Coordinator in `/iosApp/iosApp/Coordinators/[Feature]Coordinator.swift` (manual DI: inject repository)
- [ ] T056 [US1] Create SwiftUI view in `/iosApp/iosApp/Views/[Feature]View.swift`
- [ ] T057 [US1] Add accessibilityIdentifier to all interactive views in [Feature]View (e.g., `.accessibilityIdentifier("[screen].[element].[action]")`)
- [ ] T058 [P] [US1] Add SwiftDoc documentation to complex iOS APIs (skip self-explanatory methods/properties)

**Web** (Full Stack Implementation):
- [ ] T059 [P] [US1] Create TypeScript domain models in `/webApp/src/models/[Model].ts`
- [ ] T060 [P] [US1] Create service interface in `/webApp/src/services/[Service].ts`
- [ ] T061 [US1] Implement HTTP service consuming backend API in `/webApp/src/services/[Service]Impl.ts`
- [ ] T062 [US1] Add service to Web DI in `/webApp/src/di/ServiceProvider.tsx` (React Context or other DI pattern)
- [ ] T063 [US1] Create custom hook in `/webApp/src/hooks/use[Feature].ts`
- [ ] T064 [US1] Create React component in `/webApp/src/components/[Feature]/[Feature].tsx`
- [ ] T065 [US1] Add data-testid attributes to all interactive elements in [Feature] component (e.g., `data-testid="[screen].[element].[action]"`)
- [ ] T066 [P] [US1] Add JSDoc documentation to complex Web APIs (skip self-explanatory functions)

**Backend** (TDD: Red-Green-Refactor):
- [ ] T067 [P] [US1] RED: Write failing unit test for [Service] in `/server/src/services/__test__/[Service].test.ts`
- [ ] T068 [US1] GREEN: Implement [Service] in `/server/src/services/[Service].ts` (minimal code to pass test)
- [ ] T069 [US1] REFACTOR: Improve [Service] code quality (extract helpers, apply Clean Code principles)
- [ ] T070 [P] [US1] RED: Write failing unit test for utility in `/server/src/lib/__test__/[util].test.ts`
- [ ] T071 [P] [US1] GREEN: Implement utility in `/server/src/lib/[util].ts` (minimal code to pass test)
- [ ] T072 [US1] Create database repository in `/server/src/database/repositories/[Repository].ts` (Knex queries)
- [ ] T073 [US1] Create Express router in `/server/src/routes/[feature]Routes.ts` (endpoint definitions)
- [ ] T074 [US1] RED: Write failing integration test for endpoint in `/server/src/__test__/[endpoint].test.ts` (SuperTest)
- [ ] T075 [US1] GREEN: Wire up route to service in `/server/src/app.ts` (minimal code to pass test)
- [ ] T076 [US1] REFACTOR: Add error handling middleware for [feature] routes
- [ ] T077 [P] [US1] Add JSDoc documentation to complex backend APIs (services, lib - skip obvious functions)
- [ ] T078 [US1] Run `npm test -- --coverage` and verify 80% coverage for services and lib
- [ ] T079 [P] [US1] Run `npm run lint` and fix ESLint violations

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - [Title] (Priority: P2)

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 2 (MANDATORY) ✅

**Android Unit Tests**:
- [ ] T077 [P] [US2] Unit test for [UseCase] in `/composeApp/src/androidUnitTest/.../domain/usecases/[UseCase]Test.kt`
- [ ] T078 [P] [US2] Android ViewModel test in `/composeApp/src/androidUnitTest/.../presentation/[ViewModel]Test.kt`

**iOS Unit Tests**:
- [ ] T079 [P] [US2] iOS ViewModel test in `/iosApp/iosAppTests/ViewModels/[ViewModel]Tests.swift` (test repository calls directly)

**Web Unit Tests**:
- [ ] T080 [P] [US2] Web hook test in `/webApp/src/__tests__/hooks/use[Feature].test.ts`

**End-to-End Tests**:
- [ ] T081 [P] [US2] Web E2E test in `/e2e-tests/web/specs/[feature-name].spec.ts`
- [ ] T082 [P] [US2] Mobile E2E test in `/e2e-tests/mobile/specs/[feature-name].spec.ts`

### Implementation for User Story 2

> **Note**: For backend-only features, SKIP Android, iOS, and Web sections. Only implement Backend tasks.

**Android** (Full Stack):
- [ ] T083 [P] [US2] Create [Entity] model in `/composeApp/src/androidMain/.../domain/models/[Entity].kt`
- [ ] T084 [P] [US2] Create [Repository] interface in `/composeApp/src/androidMain/.../domain/repositories/[Repository].kt`
- [ ] T085 [US2] Implement [UseCase] in `/composeApp/src/androidMain/.../domain/usecases/[UseCase].kt`
- [ ] T086 [US2] Implement repository + DI + ViewModel
- [ ] T087 [US2] Create state host composable `[Feature]Screen` (stateful)
- [ ] T088 [US2] Create stateless composable `[Feature]Content` (pure presentation)
- [ ] T089 [US2] Create `PreviewParameterProvider` and `@Preview` for stateless composable
- [ ] T090 [US2] Add Navigation Component route for US2 in NavGraph
- [ ] T091 [US2] Add testTag modifiers to all Android UI elements for US2
- [ ] T092 [P] [US2] Add KDoc documentation to complex US2 Android APIs (skip self-explanatory)

**iOS** (Full Stack - NO use cases):
- [ ] T093 [P] [US2] Create [Entity] model in `/iosApp/iosApp/Domain/Models/[Entity].swift`
- [ ] T094 [P] [US2] Create [Repository] protocol in `/iosApp/iosApp/Domain/Repositories/[Repository].swift`
- [ ] T095 [US2] Implement repository + manual DI + ViewModel (calls repository directly) + Coordinator + SwiftUI view
- [ ] T096 [US2] Add accessibilityIdentifier to all iOS UI elements for US2
- [ ] T097 [P] [US2] Add SwiftDoc documentation to complex US2 iOS APIs (skip self-explanatory)

**Web** (Full Stack):
- [ ] T098 [P] [US2] Create TypeScript models for US2 in `/webApp/src/models/`
- [ ] T099 [US2] Implement HTTP service + DI + hook + React component
- [ ] T100 [US2] Add data-testid attributes to all Web UI elements for US2
- [ ] T101 [P] [US2] Add JSDoc documentation to complex US2 Web APIs (skip self-explanatory)

**Backend** (TDD):
- [ ] T102 [P] [US2] RED-GREEN-REFACTOR: Implement backend service, routes, tests for US2
- [ ] T103 [US2] Run `npm test -- --coverage` and verify 80% coverage
- [ ] T104 [P] [US2] Run `npm run lint` and fix ESLint violations

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - [Title] (Priority: P3)

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 3 (OPTIONAL - only if tests requested) ⚠️

- [ ] T105 [P] [US3] Platform-specific unit tests (follow same pattern as US1/US2)
- [ ] T106 [P] [US3] E2E tests for US3

### Implementation for User Story 3

- [ ] T107 [P] [US3] Android implementation (domain + data + presentation + stateless composable + preview + Navigation Component route)
- [ ] T108 [P] [US3] iOS implementation (domain + data + presentation)
- [ ] T109 [P] [US3] Web implementation (models + services + UI)
- [ ] T110 [P] [US3] Backend implementation (TDD workflow)

**Checkpoint**: All user stories should now be independently functional

---

[Add more user story phases as needed, following the same pattern]

---

## Phase N: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] TXXX [P] Documentation updates in docs/
- [ ] TXXX Code cleanup and refactoring per platform
- [ ] TXXX Performance optimization across all stories
- [ ] TXXX [P] Additional unit tests (if requested) per platform
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
- Models before services/use cases
- Services/use cases before ViewModels
- ViewModels before UI
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Once Foundational phase completes, all user stories can start in parallel (if team capacity allows)
- All tests for a user story marked [P] can run in parallel
- Models within a story marked [P] can run in parallel
- Platform implementations for the same user story can be worked on in parallel by different team members (Android, iOS, Web independently)

---

## Parallel Example: User Story 1

```bash
# Launch all tests for User Story 1 together:
Task: "Android ViewModel test in /composeApp/src/androidUnitTest/.../[ViewModel]Test.kt"
Task: "iOS ViewModel test in /iosApp/iosAppTests/ViewModels/[ViewModel]Tests.swift"
Task: "Web hook test in /webApp/src/__tests__/hooks/use[Feature].test.ts"

# Launch all platform implementations for User Story 1 together:
Task: "Android domain + data + presentation implementation"
Task: "iOS domain + data + presentation implementation"
Task: "Web models + services + UI implementation"
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
   - Developer A: User Story 1 (Android + iOS + Web + Backend)
   - Developer B: User Story 2 (Android + iOS + Web + Backend)
   - Developer C: User Story 3 (Android + iOS + Web + Backend)
3. Stories complete and integrate independently

OR with platform-focused teams:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Android Team: All user stories for Android
   - iOS Team: All user stories for iOS
   - Web Team: All user stories for Web
   - Backend Team: All user stories for Backend
3. Platforms develop independently, integrate via REST API

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable per platform
- Verify tests fail before implementing
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
