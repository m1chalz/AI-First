# Tasks: Koin Dependency Injection for KMP

**Input**: Design documents from `/specs/001-koin-kmp/`
**Prerequisites**: plan.md ✅, spec.md ✅, research.md ✅, data-model.md ✅, contracts/ ✅, quickstart.md ✅

**Tests**: Per spec clarifications, DI infrastructure has **NO dedicated unit tests**. DI correctness is validated indirectly through component tests in future features. This feature includes only E2E smoke tests to verify initialization.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3, US4)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and dependency configuration

- [ ] T001 Add Koin version to `/Volumes/ExtSSD/Code/AI-First/gradle/libs.versions.toml` (koin = "3.5.3")
- [ ] T002 [P] Add Koin library definitions to `/Volumes/ExtSSD/Code/AI-First/gradle/libs.versions.toml` (koin-core, koin-android, koin-androidx-compose, koin-test)
- [ ] T003 Add Koin dependencies to shared module `/Volumes/ExtSSD/Code/AI-First/shared/build.gradle.kts` (commonMain: koin-core, commonTest: koin-test)
- [ ] T004 [P] Add Koin dependencies to Android module `/Volumes/ExtSSD/Code/AI-First/composeApp/build.gradle.kts` (androidMain: koin-android, koin-androidx-compose)
- [ ] T005 Create `/Volumes/ExtSSD/Code/AI-First/docs/adr/` directory for Architecture Decision Records
- [ ] T006 Create E2E test infrastructure directories `/Volumes/ExtSSD/Code/AI-First/e2e-tests/web/specs/` and `/Volumes/ExtSSD/Code/AI-First/e2e-tests/mobile/specs/`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core DI infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T007 Create domain module directory structure `/Volumes/ExtSSD/Code/AI-First/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/di/`
- [ ] T008 [P] Create Android DI directories `/Volumes/ExtSSD/Code/AI-First/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/di/`
- [ ] T009 [P] Create iOS DI directory `/Volumes/ExtSSD/Code/AI-First/iosApp/iosApp/DI/`
- [ ] T010 [P] Create Web DI directory `/Volumes/ExtSSD/Code/AI-First/webApp/src/di/`
- [ ] T011 Setup Playwright config for web E2E tests in `/Volumes/ExtSSD/Code/AI-First/e2e-tests/web/playwright.config.ts`
- [ ] T012 [P] Setup Appium config for mobile E2E tests in `/Volumes/ExtSSD/Code/AI-First/e2e-tests/mobile/wdio.conf.ts`

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Basic Dependency Injection Setup (Priority: P1) 🎯 MVP

**Goal**: Configure Koin DI framework across all three platforms (Android, iOS, Web) so that DI container initializes successfully at application startup without errors.

**Independent Test**: Launch each platform's application and verify DI container initializes without crashes. A simple test module can be defined and resolved.

**Why P1**: This is foundational infrastructure that enables all other DI-related work.

### E2E Smoke Tests for User Story 1 ✅

> **NOTE: These tests verify DI initialization (app starts without crashes)**

- [ ] T013 [P] [US1] Create web E2E smoke test in `/Volumes/ExtSSD/Code/AI-First/e2e-tests/web/specs/koin-initialization.spec.ts` (verify app loads without DI errors)
- [ ] T014 [P] [US1] Create mobile E2E smoke test in `/Volumes/ExtSSD/Code/AI-First/e2e-tests/mobile/specs/koin-initialization.spec.ts` (verify Android/iOS app launches without DI errors)

### Implementation for User Story 1

**Shared Module (Domain DI)**:

- [ ] T015 [P] [US1] Create empty domain module in `/Volumes/ExtSSD/Code/AI-First/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/di/DomainModule.kt` (empty module with KDoc per contract)
- [ ] T016 [P] [US1] Create Kotlin/JS Koin exports in `/Volumes/ExtSSD/Code/AI-First/shared/src/jsMain/kotlin/com/intive/aifirst/petspot/di/KoinJs.kt` (@JsExport startKoin, get, domainModule)
- [ ] T017 [P] [US1] Create Kotlin/Native Koin initialization in `/Volumes/ExtSSD/Code/AI-First/shared/src/iosMain/kotlin/com/intive/aifirst/petspot/di/KoinIos.kt` (initKoin function)

**Android Platform**:

- [ ] T018 [US1] Create empty Android data module in `/Volumes/ExtSSD/Code/AI-First/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/di/DataModule.kt` (empty module with KDoc per contract)
- [ ] T019 [US1] Create empty Android ViewModel module in `/Volumes/ExtSSD/Code/AI-First/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/di/ViewModelModule.kt` (empty module with KDoc per contract)
- [ ] T020 [US1] Create Android Application class in `/Volumes/ExtSSD/Code/AI-First/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/PetSpotApplication.kt` (startKoin in onCreate)
- [ ] T021 [US1] Update AndroidManifest.xml in `/Volumes/ExtSSD/Code/AI-First/composeApp/src/androidMain/AndroidManifest.xml` (add android:name=".PetSpotApplication")

**iOS Platform**:

- [ ] T022 [US1] Create iOS Koin initializer in `/Volumes/ExtSSD/Code/AI-First/iosApp/iosApp/DI/KoinInitializer.swift` (calls KoinKt.doInitKoin per contract)
- [ ] T023 [US1] Modify iOS app entry point in `/Volumes/ExtSSD/Code/AI-First/iosApp/iosApp/iOSApp.swift` (call KoinInitializer().initialize() in init())

**Web Platform**:

- [ ] T024 [US1] Create Web Koin setup in `/Volumes/ExtSSD/Code/AI-First/webApp/src/di/koinSetup.ts` (initializeKoin function per contract)
- [ ] T025 [US1] Modify Web app entry point in `/Volumes/ExtSSD/Code/AI-First/webApp/src/index.tsx` (call initializeKoin before ReactDOM.createRoot)

**Documentation**:

- [ ] T026 [P] [US1] Create Architecture Decision Record in `/Volumes/ExtSSD/Code/AI-First/docs/adr/001-koin-dependency-injection.md` (Context, Decision, Consequences, Implementation, Examples)

**Checkpoint**: At this point, all three platforms should launch without DI errors. User Story 1 is fully functional and testable independently.

---

## Phase 4: User Story 2 - Shared Module Dependencies (Priority: P2)

**Goal**: Enable developers to define dependencies in the shared module (repositories, use cases, domain services) so that these dependencies can be injected into platform-specific code without tight coupling.

**Independent Test**: Create a sample repository interface in shared module, register it in DI, and successfully inject it into a platform-specific component on at least one platform.

**Why P2**: This enables the core architecture pattern - shared business logic with platform-specific UI.

### Implementation for User Story 2

**Example Implementation (Demonstration)**:

- [ ] T027 [P] [US2] Create example Pet model in `/Volumes/ExtSSD/Code/AI-First/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/models/Pet.kt` (simple data class for demonstration)
- [ ] T028 [P] [US2] Create example PetRepository interface in `/Volumes/ExtSSD/Code/AI-First/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/repositories/PetRepository.kt` (suspend fun getPets(): Result<List<Pet>>)
- [ ] T029 [US2] Create example GetPetsUseCase in `/Volumes/ExtSSD/Code/AI-First/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/usecases/GetPetsUseCase.kt` (depends on PetRepository)
- [ ] T030 [US2] Add GetPetsUseCase to domain module in `/Volumes/ExtSSD/Code/AI-First/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/di/DomainModule.kt` (single { GetPetsUseCase(get()) })

**Android Example Implementation**:

- [ ] T031 [US2] Create example PetRepositoryImpl in `/Volumes/ExtSSD/Code/AI-First/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/data/repositories/PetRepositoryImpl.kt` (implements PetRepository with hardcoded data)
- [ ] T032 [US2] Register PetRepository in Android data module `/Volumes/ExtSSD/Code/AI-First/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/di/DataModule.kt` (single<PetRepository> { PetRepositoryImpl() })

**Verification**:

- [ ] T033 [US2] Create simple Android ViewModel in `/Volumes/ExtSSD/Code/AI-First/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/ui/viewmodels/PetListViewModel.kt` (injects GetPetsUseCase, verifies dependency chain)
- [ ] T034 [US2] Register ViewModel in Android ViewModel module `/Volumes/ExtSSD/Code/AI-First/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/di/ViewModelModule.kt` (viewModel { PetListViewModel(get()) })
- [ ] T035 [US2] Create simple Compose screen in `/Volumes/ExtSSD/Code/AI-First/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/ui/screens/PetListScreen.kt` (uses koinViewModel() to verify injection works)

**Checkpoint**: At this point, shared dependencies can be defined once and used across platforms. User Story 2 is fully functional and testable independently.

---

## Phase 5: User Story 3 - Platform-Specific Dependencies (Priority: P3)

**Goal**: Enable platform-specific features (Android ViewModels, iOS ViewModels, Web state management) to inject both shared and platform-specific dependencies for testable access to business logic and platform services.

**Independent Test**: Create a ViewModel on one platform that depends on both a shared repository and a platform-specific service, and verify successful injection.

**Why P3**: Completes the DI setup by enabling platform-specific code to use DI. Less critical than P1/P2 because it builds on top of them.

### Implementation for User Story 3

**iOS Example Implementation**:

- [ ] T036 [P] [US3] Create iOS ViewModel in `/Volumes/ExtSSD/Code/AI-First/iosApp/iosApp/ViewModels/PetListViewModel.swift` (uses KoinKt.get() to inject GetPetsUseCase per contract)
- [ ] T037 [P] [US3] Create iOS SwiftUI view in `/Volumes/ExtSSD/Code/AI-First/iosApp/iosApp/Views/PetListView.swift` (uses @StateObject ViewModel with Koin injection)

**Web Example Implementation**:

- [ ] T038 [P] [US3] Create Web custom hook in `/Volumes/ExtSSD/Code/AI-First/webApp/src/hooks/usePets.ts` (uses get<GetPetsUseCase>() from shared per contract)
- [ ] T039 [P] [US3] Create React component in `/Volumes/ExtSSD/Code/AI-First/webApp/src/components/PetList/PetList.tsx` (uses usePets hook to demonstrate injection)

**Documentation Update**:

- [ ] T040 [US3] Update quickstart guide validation in `/Volumes/ExtSSD/Code/AI-First/specs/001-koin-kmp/quickstart.md` (verify all platforms can inject dependencies per documentation)

**Checkpoint**: All platforms can now inject both shared and platform-specific dependencies. User Story 3 is fully functional and testable independently.

---

## Phase 6: User Story 4 - Test Support with Mock Dependencies (Priority: P4)

**Goal**: Enable developers writing unit tests to replace real dependencies with test doubles (mocks, fakes) in the DI container for isolated component testing.

**Independent Test**: Write a unit test that overrides a production dependency with a mock implementation and verify the test passes with the mock behavior.

**Why P4**: Testing is important but can be done manually without DI test support initially. Enables better test quality but isn't blocking for basic functionality.

### Implementation for User Story 4

**Shared Module Test Examples**:

- [ ] T041 [P] [US4] Create fake PetRepository in `/Volumes/ExtSSD/Code/AI-First/shared/src/commonTest/kotlin/com/intive/aifirst/petspot/domain/repositories/FakePetRepository.kt` (implements PetRepository with test data)
- [ ] T042 [P] [US4] Create GetPetsUseCase unit test in `/Volumes/ExtSSD/Code/AI-First/shared/src/commonTest/kotlin/com/intive/aifirst/petspot/domain/usecases/GetPetsUseCaseTest.kt` (uses KoinTest + FakePetRepository)

**Android Test Examples**:

- [ ] T043 [P] [US4] Create PetListViewModel unit test in `/Volumes/ExtSSD/Code/AI-First/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/ui/viewmodels/PetListViewModelTest.kt` (uses KoinTest + fake use case)

**iOS Test Examples**:

- [ ] T044 [P] [US4] Create iOS ViewModel unit test in `/Volumes/ExtSSD/Code/AI-First/iosApp/iosAppTests/ViewModels/PetListViewModelTests.swift` (injects fake use case via init parameter per contract)

**Web Test Examples**:

- [ ] T045 [P] [US4] Create Web hook unit test in `/Volumes/ExtSSD/Code/AI-First/webApp/src/__tests__/hooks/usePets.test.ts` (mocks get() function from 'shared')

**Checkpoint**: All platforms can now write isolated unit tests with test doubles. User Story 4 is fully functional and testable independently.

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Final validation and documentation

- [ ] T046 [P] Verify all platform apps launch without DI errors (Android Studio, Xcode, npm run start)
- [ ] T047 [P] Run E2E smoke tests on all platforms (web: npx playwright test, mobile: npm run test:mobile:android, npm run test:mobile:ios)
- [ ] T048 Verify quickstart guide accuracy by following 15-minute guide with example Pet dependency
- [ ] T049 [P] Add KDoc documentation validation (all Koin modules have complete documentation)
- [ ] T050 [P] Code review of all DI modules for consistency with contracts
- [ ] T051 Update constitution compliance check in `/Volumes/ExtSSD/Code/AI-First/specs/001-koin-kmp/plan.md` (mark as implemented)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Story 1 (Phase 3)**: Depends on Foundational completion - Can proceed independently
- **User Story 2 (Phase 4)**: Depends on User Story 1 completion - Builds on basic DI setup
- **User Story 3 (Phase 5)**: Depends on User Story 2 completion - Requires shared dependencies to exist
- **User Story 4 (Phase 6)**: Depends on User Story 2 completion - Can run in parallel with User Story 3
- **Polish (Phase 7)**: Depends on all desired user stories being complete

### User Story Dependencies

```
US1 (Basic DI Setup)
  ↓
US2 (Shared Dependencies) ─────┬─→ US3 (Platform-Specific)
                                └─→ US4 (Test Support)
```

- **User Story 1**: No dependencies on other stories (foundational)
- **User Story 2**: Depends on User Story 1 (needs basic DI to register dependencies)
- **User Story 3**: Depends on User Story 2 (needs shared dependencies to exist)
- **User Story 4**: Depends on User Story 2 (needs dependencies to mock), can run parallel with US3

### Within Each User Story

- Documentation before implementation (ADR, contracts)
- Shared module before platform modules (domain → Android → iOS → Web)
- E2E tests after implementation (verify initialization works)
- Tests for US4 require dependencies from US2 to exist

### Parallel Opportunities

**Phase 1 (Setup)**:
- T002, T004 can run in parallel (different files)

**Phase 2 (Foundational)**:
- T008, T009, T010 can run in parallel (different platforms)
- T011, T012 can run in parallel (different test frameworks)

**Phase 3 (User Story 1)**:
- T013, T014 can run in parallel (different E2E tests)
- T015, T016, T017 can run in parallel (different platform source sets)
- T026 can run in parallel with implementation tasks (documentation)

**Phase 4 (User Story 2)**:
- T027, T028 can run in parallel (different model/interface)

**Phase 5 (User Story 3)**:
- T036, T037 can run in parallel (iOS ViewModel + View)
- T038, T039 can run in parallel (Web hook + component)

**Phase 6 (User Story 4)**:
- T041, T042, T043, T044, T045 can ALL run in parallel (different platform tests)

**Phase 7 (Polish)**:
- T046, T047, T049, T050 can ALL run in parallel (validation tasks)

---

## Parallel Example: User Story 1 (Basic DI Setup)

```bash
# After Phase 1 & 2 complete, launch all US1 E2E tests together:
T013: "Create web E2E smoke test" (web/specs/)
T014: "Create mobile E2E smoke test" (mobile/specs/)

# Launch all US1 shared module tasks together:
T015: "Create domain module" (commonMain/)
T016: "Create Kotlin/JS exports" (jsMain/)
T017: "Create Kotlin/Native init" (iosMain/)

# Launch all US1 documentation tasks in parallel with implementation:
T026: "Create ADR" (docs/adr/)
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test DI initialization on all platforms
5. Deploy/demo if ready

**Result**: All three platforms have working DI framework (empty modules, but infrastructure complete)

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (DI infrastructure ready!)
3. Add User Story 2 → Test independently → Deploy/Demo (shared dependencies working!)
4. Add User Story 3 → Test independently → Deploy/Demo (platform injection working!)
5. Add User Story 4 → Test independently → Deploy/Demo (test support complete!)
6. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational done:
   - Developer A: User Story 1 (DI initialization)
3. Once US1 done:
   - Developer A: User Story 2 (shared dependencies)
4. Once US2 done (branches):
   - Developer A: User Story 3 (platform-specific)
   - Developer B: User Story 4 (test support) ← **CAN RUN PARALLEL**
5. Stories complete and integrate independently

---

## Notes

- **[P] tasks** = different files, no dependencies
- **[Story] label** maps task to specific user story for traceability
- Each user story should be independently completable and testable
- No dedicated unit tests for DI modules (validated indirectly per spec)
- E2E smoke tests verify initialization only (app launches without crashes)
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- **Empty modules are intentional**: Domain module, data modules, and ViewModel modules start empty and will be populated by future features
- All KDoc comments must follow format in contracts/
- All initialization must follow patterns in contracts/

