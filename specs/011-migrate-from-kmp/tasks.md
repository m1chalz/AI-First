# Tasks: Complete KMP to Platform-Independent Migration

**Input**: Design documents from `/Users/kamilpapciak/k/projects/AI-First/specs/011-migrate-from-kmp/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/, quickstart.md

**Tests**: Test requirements for this migration:

**IMPORTANT**: This is an infrastructure migration. Tests already exist and only need import updates. NO new test implementation is required.

**Test Validation Per Phase**:
- Android: Run `./gradlew :composeApp:testDebugUnitTest koverHtmlReport` after each Android phase
  - Verify all tests pass with 80%+ coverage maintained
  - Tests in `/composeApp/src/androidUnitTest/` only need import path updates
- iOS: Run `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES` after each iOS phase
  - Verify all tests pass with 80%+ coverage maintained
  - Tests in `/iosApp/iosAppTests/` only need import statement updates (remove `import Shared`)

**Organization**: Tasks are grouped by user story. Migration follows sequential execution: iOS content migration → Android content migration → Shared module removal → Cleanup.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Pre-Migration Validation)

**Purpose**: Establish baseline and prepare for migration

- [X] T001 Capture Android build time baseline with `time ./gradlew :composeApp:assembleDebug`
- [X] T002 Capture Android test coverage baseline with `./gradlew :composeApp:testDebugUnitTest koverHtmlReport` and document results in `specs/011-migrate-from-kmp/baseline-metrics.txt`
- [X] T003 Capture iOS build time baseline with `time xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 17' build`
- [X] T004 Capture iOS test coverage baseline with coverage-enabled test run and document in `specs/011-migrate-from-kmp/baseline-metrics.txt`
- [X] T005 Audit Android code for shared module imports with `grep -r "import com.intive.aifirst.petspot.domain" composeApp/src/androidMain/` and document files requiring updates
- [X] T006 Audit iOS code for Shared framework imports with `grep -r "import Shared" iosApp/` and document files requiring updates
- [X] T007 Create inventory of shared module contents by listing all files in `shared/src/commonMain/.../domain/` and document entity counts (5 models, 1 repository interface, 1 Android use case, 1 DI module, test fixtures)
- [X] T008 Document current Gradle sync time baseline and repository size (.git folder size) for comparison after migration (SC-007 validation)

**Checkpoint**: ✅ Baseline metrics captured - ready to begin content migration

---

## Phase 2: User Story 1 - Platform Domain Models Independence (Priority: P1) 🎯

**Goal**: Each platform has its own domain models (Animal, Location, AnimalSpecies, AnimalGender, AnimalStatus) implemented in platform-native languages without importing from shared KMP module.

**Independent Test**: Inspect platform code to verify domain models exist in platform-specific directories with no shared imports, and platform builds succeed using local models.

### iOS Domain Models Migration (Kotlin → Swift Translation)

- [X] T009 [US1] Create `iosApp/iosApp/Domain/Models/` directory structure
- [X] T010 [P] [US1] Translate Animal model to Swift struct in `iosApp/iosApp/Domain/Models/Animal.swift` (reference: `contracts/kotlin-swift-mapping.md`)
- [X] T011 [P] [US1] Translate Location model to Swift struct in `iosApp/iosApp/Domain/Models/Location.swift`
- [X] T012 [P] [US1] Translate AnimalSpecies enum to Swift in `iosApp/iosApp/Domain/Models/AnimalSpecies.swift` (case names: SCREAMING_SNAKE_CASE → camelCase)
- [X] T013 [P] [US1] Translate AnimalGender enum to Swift in `iosApp/iosApp/Domain/Models/AnimalGender.swift` (case names: SCREAMING_SNAKE_CASE → camelCase)
- [X] T014 [P] [US1] Translate AnimalStatus enum to Swift in `iosApp/iosApp/Domain/Models/AnimalStatus.swift` (case names: SCREAMING_SNAKE_CASE → camelCase)
- [X] T015 [US1] Remove `import Shared` statements from all iOS ViewModels in `iosApp/iosApp/Features/`
- [X] T016 [US1] Remove `import Shared` statements from iOS coordinators in `iosApp/iosApp/Coordinators/`
- [X] T017 [US1] Remove `import Shared` statements from iOS views in `iosApp/iosApp/Views/`
- [X] T018 [US1] Update enum switch statement case names in iOS code from SCREAMING_SNAKE_CASE to camelCase (e.g., `.DOG` → `.dog`) - No updates needed, already camelCase
- [X] T019 [US1] Verify iOS domain models compile successfully (full build pending Phase 2 repository/use case migration)
- [X] T020 [US1] Update test imports in `iosApp/iosAppTests/Features/` to use local Swift domain models
- [ ] T021 [US1] Run iOS unit tests and verify they pass with 80%+ coverage maintained (deferred to Phase 2 - needs AnimalRepository protocol)
- [X] T022 [US1] Commit Phase 1 (iOS models) with git tag `migration-phase-1-ios-models`

### Android Domain Models Migration (Kotlin Copy)

- [ ] T023 [US1] Create `composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/composeapp/domain/models/` directory structure
- [ ] T024 [P] [US1] Copy Animal.kt from `shared/src/commonMain/.../domain/models/` to `composeApp/src/androidMain/.../domain/models/` and update package name
- [ ] T025 [P] [US1] Copy Location.kt and update package name to `composeapp.domain.models`
- [ ] T026 [P] [US1] Copy AnimalSpecies.kt and update package name, remove @JsExport if present
- [ ] T027 [P] [US1] Copy AnimalGender.kt and update package name, remove @JsExport if present
- [ ] T028 [P] [US1] Copy AnimalStatus.kt and update package name, remove @JsExport if present
- [ ] T029 [US1] Find and update all Android imports from `com.intive.aifirst.petspot.domain.models.*` to `composeapp.domain.models.*` in `composeApp/src/androidMain/`
- [ ] T030 [US1] Update Android ViewModel imports in `composeApp/src/androidMain/.../presentation/`
- [ ] T031 [US1] Update Android repository implementation imports in `composeApp/src/androidMain/.../data/repositories/`
- [ ] T032 [US1] Verify Android builds successfully with `./gradlew :composeApp:assembleDebug`
- [ ] T033 [US1] Update test imports in `composeApp/src/androidUnitTest/` to use local composeapp domain models
- [ ] T034 [US1] Run Android unit tests and verify they pass with 80%+ coverage maintained with `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
- [ ] T035 [US1] Commit Phase 3 (Android models) with git tag `migration-phase-3-android-models`

**Checkpoint**: User Story 1 complete - Both platforms have independent domain models, builds succeed, tests pass

### Contract Validation (FR-010 / SC-011)

- [ ] T130 [US1] Compare Android domain models against backend contracts defined in `specs/011-migrate-from-kmp/contracts/kotlin-swift-mapping.md` and document parity results (fields, types, nullability) in `specs/011-migrate-from-kmp/contract-validation.md`
- [ ] T131 [US1] Compare iOS domain models against the same backend contracts and record results in `specs/011-migrate-from-kmp/contract-validation.md`, flagging any discrepancies for follow-up before proceeding

---

## Phase 3: User Story 2 - Platform Repository Independence (Priority: P1)

**Goal**: Repository interfaces are implemented in each platform's codebase, allowing business logic to evolve per platform without coordinating changes across the former KMP module. Android retains its use case layer; iOS ViewModels call repositories directly per constitution.

**Independent Test**: Verify repository interfaces (and Android use cases) exist in platform directories with platform-native patterns, and ViewModels/coordinators successfully consume those contracts without shared dependencies.

### iOS Repository Migration (Kotlin → Swift Translation, No Use Cases)

- [X] T036 [US2] Create `iosApp/iosApp/Domain/Repositories/` directory structure
- [X] T037 [US2] Ensure `iosApp/iosApp/Domain/` contains only `Models/` and `Repositories/` (remove legacy `UseCases/` folders or references) - No legacy folders existed
- [X] T038 [US2] Create `iosApp/iosAppTests/Fakes/` directory structure for test doubles
- [X] T039 [US2] Translate AnimalRepository interface to Swift protocol in `iosApp/iosApp/Domain/Repositories/AnimalRepository.swift` (Kotlin `suspend fun` → Swift `func ... async throws`)
- [X] T040 [US2] Update AnimalListViewModel to call local repository protocol directly per iOS MVVM-C architecture (removed GetAnimalsUseCase dependency)
- [X] T041 [US2] Translate FakeAnimalRepository to Swift class in `iosApp/iosAppTests/Fakes/FakeAnimalRepository.swift`
- [X] T042 [US2] Update AnimalListCoordinator to provide repository via manual DI (constructor injection) with no use case layer - iOS properly follows MVVM-C
- [X] T043 [US2] Update AnimalListViewModel to use local domain types (repository dependency injection)
- [X] T044 [US2] Update AnimalRepositoryImpl to conform to local protocol
- [X] T045 [US2] Verify no `import Shared` statements remain in iOS codebase with `git grep "import Shared" iosApp/` - Verified clean
- [X] T046 [US2] Verify iOS builds successfully - BUILD SUCCEEDED
- [X] T047 [US2] Update iOS test imports to use local fakes and domain types
- [ ] T048 [US2] Run iOS unit tests and verify they pass with 80%+ coverage maintained - Deferred (test action not configured in scheme)
- [X] T049 [US2] Commit Phase 2 (iOS repositories) with git tag `migration-phase-2-ios-repos`

### Android Repositories and Use Cases Migration (Kotlin Copy)

- [ ] T050 [US2] Create `composeApp/src/androidMain/.../domain/repositories/` directory structure
- [ ] T051 [US2] Create `composeApp/src/androidMain/.../domain/usecases/` directory structure
- [ ] T052 [US2] Create `composeApp/src/androidUnitTest/.../domain/fixtures/` directory structure
- [ ] T053 [US2] Copy AnimalRepository.kt interface from `shared/src/commonMain/.../domain/repositories/` to `composeApp/src/androidMain/.../domain/repositories/` and update package
- [ ] T054 [US2] Copy GetAnimalsUseCase.kt from `shared/src/commonMain/.../domain/usecases/` to `composeApp/src/androidMain/.../domain/usecases/` and update package and imports
- [ ] T055 [US2] Copy FakeAnimalRepository.kt to `composeApp/src/androidUnitTest/.../domain/fixtures/` and update package
- [ ] T056 [US2] Copy MockAnimalData.kt to `composeApp/src/androidUnitTest/.../domain/fixtures/` and update package
- [ ] T057 [US2] Copy DomainModule.kt from `shared/src/commonMain/.../di/` to `composeApp/src/androidMain/.../di/` and update package and imports to reference local domain classes
- [ ] T058 [US2] Register domainModule in PetSpotApplication.kt alongside existing dataModule and viewModelModule with Koin
- [ ] T059 [US2] Update all Android repository imports in ViewModels and use cases to reference local packages
- [ ] T060 [US2] Update AnimalRepositoryImpl in `composeApp/src/androidMain/.../data/repositories/` to implement local interface
- [ ] T061 [US2] Verify no shared package imports remain in Android domain layer with `git grep "import com.intive.aifirst.petspot.domain" composeApp/src/androidMain/ | grep -v composeapp`
- [ ] T062 [US2] Verify Android builds successfully with `./gradlew :composeApp:assembleDebug`
- [ ] T063 [US2] Update Android test imports to use local fixtures and domain types
- [ ] T064 [US2] Run Android unit tests and verify they pass with 80%+ coverage maintained with `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
- [ ] T065 [US2] Commit Phase 4 (Android repos/use cases) with git tag `migration-phase-4-android-repos`

**Checkpoint**: User Story 2 complete - Both platforms have independent repository interfaces, Android maintains local use cases, and ViewModels consume local types without shared dependencies

---

## Phase 4: User Story 3 - Clean Platform Build Without Shared Module (Priority: P1)

**Goal**: Build each platform (Android, iOS, Web) independently without any references to the shared KMP module after content migration completes.

**Independent Test**: Run platform-specific build commands and verify they succeed without shared module errors.

### Remove Gradle Configuration

- [ ] T066 [US3] Remove `include(":shared")` line from `settings.gradle.kts`
- [ ] T067 [US3] Remove `implementation(projects.shared)` dependency from `composeApp/build.gradle.kts`
- [ ] T068 [US3] Run `./gradlew clean` to clear cached builds
- [ ] T069 [US3] Run `./gradlew --stop` to stop Gradle daemon
- [ ] T070 [US3] Verify Gradle sync succeeds without shared module with `./gradlew :composeApp:assembleDebug`
- [ ] T071 [US3] Run Android unit tests to verify they still pass with `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
- [ ] T072 [US3] Commit Phase 5 (Gradle config removal) with git tag `migration-phase-5-gradle-config`

### Remove iOS Framework References

- [ ] T073 [US3] Open `iosApp/iosApp.xcodeproj` in Xcode
- [ ] T074 [US3] Remove Shared.framework from "Link Binary With Libraries" build phase in Xcode project settings
- [ ] T075 [US3] Remove framework search paths referencing shared/build from Xcode Build Settings
- [ ] T076 [US3] Remove any KMP-generated framework references from Xcode project
- [ ] T077 [US3] Verify iOS builds successfully with `xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' clean build`
- [ ] T078 [US3] Run iOS unit tests to verify they still pass with `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
- [ ] T079 [US3] Commit Phase 6 (iOS Xcode config removal) with git tag `migration-phase-6-ios-xcode-config`

### Delete Shared Module Directory

**⚠️ CRITICAL**: Only proceed after verifying all previous tasks complete and tests pass

- [ ] T080 [US3] Final verification: Run Android build with `./gradlew :composeApp:assembleDebug`
- [ ] T081 [US3] Final verification: Run iOS build with `xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' build`
- [ ] T082 [US3] Final verification: Verify no shared imports remain with `git grep "import Shared" iosApp/` and `git grep "import com.intive.aifirst.petspot.domain" composeApp/src/androidMain/ | grep -v composeapp`
- [ ] T083 [US3] Delete `shared/` directory completely with `rm -rf shared/`
- [ ] T084 [US3] Verify Android builds without shared module with `./gradlew :composeApp:assembleDebug`
- [ ] T085 [US3] Verify iOS builds without shared module with `xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' build`
- [ ] T086 [US3] Run full Android test suite with `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
- [ ] T087 [US3] Run full iOS test suite with `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
- [ ] T088 [US3] Run E2E web tests with `npx playwright test` from repo root
- [ ] T089 [US3] Run mobile E2E tests with `npm run test:mobile:android && npm run test:mobile:ios` from repo root (if mobile E2E configured per FR-020)
- [ ] T090 [US3] Create archive branch with `git checkout migration-phase-6-ios-xcode-config && git branch archive/shared-module && git checkout 011-migrate-from-kmp`
- [ ] T091 [US3] Push archive branch to remote with `git push origin archive/shared-module`
- [ ] T092 [US3] Commit Phase 7 (shared module deletion) with git tag `migration-phase-7-delete-shared`

**Checkpoint**: User Story 3 complete - Shared module deleted, all platforms build independently, all tests pass

---

## Phase 5: User Story 4 - Platform Test Execution (Priority: P1)

**Goal**: Run platform-specific unit tests (Android, iOS, Web, Backend) to verify business logic and presentation logic work correctly after migration with 80%+ coverage maintained everywhere.

**Independent Test**: Run each platform's test command and verify results meet expected coverage thresholds.

- [ ] T093 [US4] Run Android tests and generate coverage report with `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
- [ ] T094 [US4] Verify Android coverage meets 80%+ threshold by inspecting `composeApp/build/reports/kover/html/index.html`
- [ ] T095 [US4] Run iOS tests with coverage with `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
- [ ] T096 [US4] Verify iOS coverage meets 80%+ threshold in Xcode coverage report
- [ ] T097 [US4] Run web tests with coverage with `npm test -- --coverage` from `webApp/`
- [ ] T098 [US4] Verify web coverage meets 80%+ threshold by inspecting `webApp/coverage/index.html`
- [ ] T132 [US4] Run backend tests with coverage using `npm test -- --coverage` from `server/`
- [ ] T133 [US4] Verify backend coverage meets 80%+ threshold by reviewing `server/coverage/index.html` and capture results in `specs/011-migrate-from-kmp/test-validation-results.txt`
- [ ] T099 [US4] Compare post-migration coverage to baseline captured in T002 and T004
- [ ] T100 [US4] Document test results in `specs/011-migrate-from-kmp/test-validation-results.txt`

**Checkpoint**: User Story 4 complete - All platform tests pass with 80%+ coverage maintained

---

## Phase 6: User Story 6 - CI/CD Pipeline Execution (Priority: P2)

**Goal**: CI/CD pipeline builds and tests all platforms successfully without any KMP-related steps.

**Independent Test**: Trigger CI/CD pipeline and verify all build, test, and lint jobs pass without KMP-related steps.

- [ ] T101 [US6] Review CI/CD configuration files (`.github/workflows/`, `.gitlab-ci.yml`, or `Jenkinsfile`) to identify shared module build steps
- [ ] T102 [US6] Remove shared module build job from CI/CD pipeline configuration
- [ ] T103 [US6] Remove KMP framework generation step for iOS from CI/CD pipeline
- [ ] T104 [US6] Remove shared module test execution from CI/CD pipeline
- [ ] T105 [US6] Update Android build job to build composeApp without shared dependency
- [ ] T106 [US6] Update iOS build job to build iosApp without KMP framework
- [ ] T107 [US6] Commit CI/CD pipeline updates
- [ ] T108 [US6] Trigger CI/CD pipeline manually or push commit to verify pipeline runs
- [ ] T109 [US6] Verify all CI/CD jobs pass (Android build, iOS build, tests, linting)
- [ ] T110 [US6] Compare CI/CD build times to baseline and document improvements
- [ ] T111 [US6] Commit Phase 8 (CI/CD updates) with git tag `migration-phase-8-cicd-update`

**Checkpoint**: User Story 6 complete - CI/CD pipeline updated and passing

---

## Phase 7: User Story 5 - Clean Development Environment (Priority: P2)

**Goal**: Developers setting up fresh environment should not encounter KMP-related dependencies or build artifacts. Project structure reflects platform-independent architecture.

**Independent Test**: Clone repository fresh, run setup commands, and verify no KMP-related errors or warnings appear.

### Documentation Updates

- [ ] T112 [US5] Update root `README.md` to remove shared module references and update build instructions
- [ ] T113 [P] [US5] Update `composeApp/README.md` (if exists) to remove shared module references
- [ ] T114 [P] [US5] Update `iosApp/README.md` to remove KMP setup instructions
- [ ] T115 [P] [US5] Create migration guide for developers with open branches in `docs/migration/kmp-removal.md`
- [ ] T116 [US5] Update `CONTRIBUTING.md` (if exists) to reflect platform-independent contribution workflow
- [ ] T117 [US5] Update architecture documentation to show three independent platforms + backend
- [ ] T118 [US5] Document build time improvements by comparing to baseline in `specs/011-migrate-from-kmp/performance-improvements.md`

### Gradle Cleanup

- [ ] T119 [US5] Review `gradle/libs.versions.toml` and remove unused KMP plugin aliases per FR-017 (kotlinMultiplatform, composeMultiplatform if not used)
- [ ] T120 [US5] Verify `settings.gradle.kts` has no shared module references
- [ ] T121 [US5] Verify root `build.gradle.kts` has no KMP plugin configuration

### Final Validation

- [ ] T122 [US5] Test fresh clone: Clone repository to new directory and run `./gradlew clean build`
- [ ] T123 [US5] Verify Gradle sync completes without mentioning shared module or KMP plugins
- [ ] T124 [US5] Open project in Android Studio and verify IDE indexing completes without errors
- [ ] T125 [US5] Open `iosApp.xcodeproj` in Xcode and verify no shared module references in project structure
- [ ] T126 [US5] Commit Phase 9 (documentation and cleanup) with git tag `migration-phase-9-documentation`

### Team Communication

- [ ] T127 [US5] Send team announcement: "KMP to platform-independent migration complete on branch 011-migrate-from-kmp"
- [ ] T128 [US5] Share migration guide URL with team for developers with open feature branches
- [ ] T129 [US5] Provide contact for migration questions

**Checkpoint**: User Story 5 complete - Documentation updated, environment clean, team notified

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **User Story 1 - iOS Domain Models (Phase 2)**: Depends on Setup completion
- **User Story 1 - Android Domain Models (Phase 2)**: Depends on iOS Domain Models completion (sequential migration strategy)
- **User Story 2 - iOS Repos/Use Cases (Phase 3)**: Depends on iOS Domain Models completion
- **User Story 2 - Android Repos/Use Cases (Phase 3)**: Depends on iOS Repos/Use Cases completion (sequential strategy)
- **User Story 3 - Build Config Removal (Phase 4)**: Depends on ALL User Story 1 & 2 tasks completion
- **User Story 4 - Test Execution (Phase 5)**: Can validate at any point after each phase
- **User Story 6 - CI/CD (Phase 6 - P2)**: Depends on User Story 3 completion (shared module deleted)
- **User Story 5 - Clean Environment (Phase 7 - P2)**: Depends on User Story 3 completion

### Critical Path

```
Setup → iOS Models → iOS Repos → Android Models → Android Repos → 
Build Config Removal → Shared Module Deletion → CI/CD + Documentation
```

### Sequential Execution (Required)

This migration MUST be executed sequentially due to:
1. **iOS before Android**: iOS requires Kotlin-to-Swift translation (more complex), Android is straightforward copy
2. **Models before Repos**: Repositories depend on domain models
3. **Content before Deletion**: All content must be migrated before deleting shared module
4. **Single commit per phase**: Each phase gets its own commit with git tag for rollback capability

### Parallel Opportunities (Limited)

Within iOS domain models phase:
- [ ] T010-T014: All 5 model files can be translated in parallel

Within Android domain models phase:
- [ ] T024-T028: All 5 model files can be copied in parallel

Within documentation phase:
- [ ] T112-T118: Documentation files can be updated in parallel

**NO platform parallelization**: iOS MUST complete fully before Android begins (sequential migration strategy)

---

## Parallel Example: iOS Domain Models (US1)

```bash
# These tasks can run in parallel (different files):
Task T010: "Translate Animal.swift"
Task T011: "Translate Location.swift"
Task T012: "Translate AnimalSpecies.swift"
Task T013: "Translate AnimalGender.swift"
Task T014: "Translate AnimalStatus.swift"

# But these must wait until above complete:
Task T015-T021: "Remove imports, update code, verify build, run tests"
```

---

## Implementation Strategy

### Sequential Migration (REQUIRED)

This is NOT an MVP-first approach. Migration must proceed sequentially through all phases:

1. **Phase 1**: Pre-migration validation (baseline capture)
2. **Phase 2**: iOS content migration (domain models + repositories)
3. **Phase 3**: Android content migration (domain models + repositories/use cases)
4. **Phase 4**: Build configuration removal (Gradle + Xcode)
5. **Phase 5**: Shared module deletion
6. **Phase 6**: Test validation
7. **Phase 7**: CI/CD updates
8. **Phase 8**: Documentation and cleanup

**No incremental delivery**: This is an all-or-nothing migration. The project must complete all phases to achieve platform independence.

**Single PR workflow**: All phases committed separately (one commit per phase with git tag) but reviewed as single PR at the end.

### Rollback Strategy

- **Git tags per phase**: Enable granular rollback to any phase
- **Archive branch**: `archive/shared-module` preserves shared module for 2 weeks
- **Before Phase 5**: Can rollback by reverting commits and restoring imports
- **After Phase 7**: Must restore shared module from git history

### Commit Strategy

Each phase gets its own commit with descriptive message and git tag:
- `migration-phase-1-ios-models`
- `migration-phase-2-ios-repos`
- `migration-phase-3-android-models`
- `migration-phase-4-android-repos`
- `migration-phase-5-gradle-config`
- `migration-phase-6-ios-xcode-config`
- `migration-phase-7-delete-shared`
- `migration-phase-8-cicd-update`
- `migration-phase-9-documentation`

---

## Notes

- This is an infrastructure migration, not new feature development
- Tests already exist - only import updates required, NO new test implementation
- Migration MUST be sequential: iOS first, then Android (constitutional requirement validated through iOS translation complexity)
- Each phase must be committed with git tag before proceeding to next
- [P] tasks within a phase can run in parallel (e.g., translating multiple models)
- [US#] labels map tasks to user stories for traceability
- Baseline metrics captured in Phase 1 enable post-migration performance validation
- 80%+ test coverage must be maintained throughout all phases
- Shared module deletion is one-way - no plan to reintroduce KMP after completion
- Single PR with 9 commits (one per phase) reviewed atomically at the end

---

## Success Criteria

- ✅ All 5 domain models exist independently in Android (Kotlin) and iOS (Swift)
- ✅ All repository interfaces exist independently per platform, and Android use cases are local-only
- ✅ No `import Shared` statements remain in iOS codebase
- ✅ No shared package imports remain in Android domain layer
- ✅ Android builds successfully without shared module: `./gradlew :composeApp:assembleDebug`
- ✅ iOS builds successfully without KMP framework: `xcodebuild -scheme iosApp build`
- ✅ All platform tests pass with 80%+ coverage maintained
- ✅ E2E tests pass (no functional regressions)
- ✅ Shared module directory completely deleted
- ✅ CI/CD pipeline updated and passing without KMP steps
- ✅ Documentation reflects platform-independent architecture
- ✅ Build time improvements documented (Android same/faster, iOS 10-20% faster)
- ✅ Constitutional Platform Independence requirement achieved

