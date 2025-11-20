# Tasks: Static Analysis Git Hooks

**Input**: Design documents from `/specs/007-static-analysis-hooks/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md, contracts/

**Total Tasks**: 123 tasks (T001-T115 + 8 subtasks: T005a, T006a, T048a-d, T051a, T104a)

**Tests**: This feature is tooling infrastructure, not user-facing functionality. Testing is done through:
- **Manual Validation**: Installation tests, violation detection tests, bypass tests, cross-platform tests
- **Real Commit Attempts**: Testing hooks with actual code changes and intentional violations
- **Edge Case Validation**: Empty commits, file renames, large changesets, generated files

**Note**: E2E tests are explicitly out of scope per spec.md (constitution exception granted). Unit tests not required for shell scripts (validated manually).

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3, US4)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and Gradle plugin configuration

- [ ] T001 Add Detekt Gradle plugin to root `build.gradle.kts` (version 1.23.3+, apply false)
- [ ] T002 [P] Add ktlint Gradle plugin to root `build.gradle.kts` (version 11.6.1+, apply false)
- [ ] T003 [P] Apply Detekt and ktlint plugins in `shared/build.gradle.kts`
- [ ] T004 [P] Apply Detekt and ktlint plugins in `composeApp/build.gradle.kts`
- [ ] T005 [P] Configure ktlint for Android in `composeApp/build.gradle.kts` (android.set(true))
- [ ] T005a [P] Configure Android Lint in `composeApp/build.gradle.kts` (lintOptions, baseline file path, abortOnError = true)
- [ ] T006 Create `scripts/` directory at repository root
- [ ] T006a Create `scripts/lib/` directory for shared utilities
- [ ] T007 [P] Add `.gitattributes` entry for shell script line endings (*.sh text eol=lf)

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core configuration files that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T008 Create `detekt.yml` configuration file at repository root (copy from contracts/detekt-config.yml)
- [ ] T009 [P] Create `.editorconfig` configuration file at repository root (copy from contracts/ktlint-config.editorconfig)
- [ ] T010 [P] Create `composeApp/lint.xml` configuration file (copy from contracts/lint-config.xml)
- [ ] T011 Configure Detekt in `shared/build.gradle.kts` (buildUponDefaultConfig = true, config.setFrom)
- [ ] T012 [P] Configure Detekt in `composeApp/build.gradle.kts` (buildUponDefaultConfig = true, config.setFrom)
- [ ] T013 [P] Configure ktlint version in both modules (version.set("1.0.1"))
- [ ] T014 Test Gradle tasks work: run `./gradlew detekt ktlintCheck` and verify execution

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 2 - Fix Existing Code Quality Issues (Priority: P2) 🎯 MUST COMPLETE FIRST

**Goal**: Identify and fix all existing code quality issues in shared module and Android platform before enforcing commit hooks, so developers aren't blocked by legacy issues they didn't create.

**Why P2 First**: Although spec.md lists this as P2, it's a BLOCKING prerequisite for P1. We must clean the baseline before enforcing hooks, otherwise every commit will be blocked by pre-existing violations.

**Independent Test**: Run full codebase analysis with all three tools (Detekt, ktlint, Android Lint), verify zero baseline violations remain before proceeding to User Story 1.

### Baseline Analysis for User Story 2

- [ ] T015 [US2] Create `scripts/analyze-baseline.sh` script for full codebase analysis
- [ ] T016 [US2] Make `scripts/analyze-baseline.sh` executable (chmod +x)
- [ ] T017 [US2] Implement Detekt analysis in analyze-baseline.sh (./gradlew detekt for all modules, capture XML output from build/reports/detekt/detekt.xml)
- [ ] T018 [US2] Implement ktlint analysis in analyze-baseline.sh (./gradlew ktlintCheck for all modules, capture plain text stdout in format file:line:column: message)
- [ ] T019 [US2] Implement Android Lint analysis in analyze-baseline.sh (./gradlew :composeApp:lintDebug, capture XML/HTML output from composeApp/build/reports/lint/)
- [ ] T020 [US2] Add report generation to analyze-baseline.sh (save to build/reports/baseline-violations.txt)
- [ ] T021 [US2] Add summary statistics to analyze-baseline.sh (error count, warning count, files affected, and top 10 files by violation count per FR-017)
- [ ] T022 [US2] Run `scripts/analyze-baseline.sh` to generate initial baseline report

### Baseline Cleanup for User Story 2

- [ ] T023 [US2] Review baseline report and categorize violations by severity (critical, high, medium, low)
- [ ] T024 [US2] Generate Detekt baseline file: run `./gradlew detektBaseline` (creates detekt-baseline.xml at repository root)
- [ ] T025 [P] [US2] Generate Android Lint baseline file: run `./gradlew :composeApp:lintBaseline` (creates composeApp/lint-baseline.xml in Android module)
- [ ] T026 [US2] Fix or suppress all critical-severity violations in shared module (document suppressions with @Suppress and comments)
- [ ] T027 [P] [US2] Fix or suppress all critical-severity violations in composeApp module (document suppressions with @Suppress and comments)
- [ ] T028 [US2] Fix or suppress all high-severity violations in shared module
- [ ] T029 [P] [US2] Fix or suppress all high-severity violations in composeApp module
- [ ] T030 [US2] Run auto-fix for formatting: `./gradlew ktlintFormat` and commit changes
- [ ] T031 [US2] Verify zero baseline violations: run `scripts/analyze-baseline.sh` again and confirm all errors resolved
- [ ] T032 [US2] Remove baseline files if all violations fixed: delete detekt-baseline.xml and composeApp/lint-baseline.xml (or keep for incremental cleanup)

### Progress Tracking for User Story 2

- [ ] T033 [US2] Create `scripts/track-violations.sh` script for progress tracking
- [ ] T034 [US2] Make `scripts/track-violations.sh` executable (chmod +x)
- [ ] T035 [US2] Implement violation counting in track-violations.sh (parse Detekt, ktlint, Android Lint output)
- [ ] T036 [US2] Add historical comparison in track-violations.sh (track progress over time)
- [ ] T037 [US2] Add module-level breakdown in track-violations.sh (shared vs composeApp)
- [ ] T038 [US2] Add rule frequency statistics in track-violations.sh (top violated rules)
- [ ] T039 [US2] Test progress tracking: run `scripts/track-violations.sh` and verify accurate counts

**Checkpoint**: At this point, codebase should be clean with zero baseline violations. User Story 1 (hook enforcement) can now be implemented.

---

## Phase 4: User Story 1 - Automated Code Quality Checks on Commit (Priority: P1) 🎯 MVP

**Goal**: Automatically check code quality when developers commit changes to shared module or Android platform, catching issues early before code review.

**Independent Test**: Make a code change with intentional issues (e.g., formatting violation, complexity issue, Android API misuse), attempt to commit, verify commit is blocked with clear error messages identifying the issues.

### Hook Script Implementation for User Story 1

- [ ] T040 [US1] Create `scripts/analyze-staged.sh` script for analyzing staged files (core logic)
- [ ] T041 [US1] Make `scripts/analyze-staged.sh` executable (chmod +x)
- [ ] T042 [US1] Add POSIX-compliant shebang and repository root detection in analyze-staged.sh (#!/bin/sh, git rev-parse --show-toplevel)
- [ ] T043 [US1] Add staged file detection in analyze-staged.sh (git diff --cached --name-only --diff-filter=ACM)
- [ ] T044 [US1] Add Kotlin file filtering in analyze-staged.sh (grep '\.kt$')
- [ ] T045 [US1] Add Android file detection in analyze-staged.sh (check if any staged files in composeApp/)
- [ ] T046 [US1] Implement Detekt execution in analyze-staged.sh (./gradlew detekt --quiet, NOTE: duplicates T017 logic - see T104a)
- [ ] T047 [US1] Implement ktlint execution in analyze-staged.sh (./gradlew ktlintCheck --quiet, NOTE: duplicates T018 logic - see T104a)
- [ ] T048 [US1] Implement conditional Android Lint execution in analyze-staged.sh (only if composeApp files changed, NOTE: duplicates T019 logic - see T104a)
- [ ] T048a [US1] Add Detekt output parsing in analyze-staged.sh (parse XML format, extract file, line, rule, message)
- [ ] T048b [US1] Add ktlint output parsing in analyze-staged.sh (parse plain text format: file:line:column: message)
- [ ] T048c [US1] Add Android Lint output parsing in analyze-staged.sh (parse XML/text format, extract file, line, severity, issue)
- [ ] T048d [US1] Implement formatted output display in analyze-staged.sh (clear file names, line numbers, issue descriptions per FR-006)
- [ ] T049 [US1] Add error handling and exit codes in analyze-staged.sh (exit 0 for success, exit 1 for errors)
- [ ] T050 [US1] Add warning display in analyze-staged.sh (show warnings but allow commit)
- [ ] T051 [US1] Add bypass documentation in analyze-staged.sh (echo message about --no-verify)
- [ ] T051a [US1] Create `scripts/pre-commit-hook.sh` template that invokes `scripts/analyze-staged.sh` and passes exit code

### Hook Installation for User Story 1

- [ ] T052 [US1] Create `scripts/install-hooks.sh` installation script
- [ ] T053 [US1] Make `scripts/install-hooks.sh` executable (chmod +x)
- [ ] T054 [US1] Implement existing hook detection in install-hooks.sh (check if .git/hooks/pre-commit exists)
- [ ] T055 [US1] Implement backup mechanism in install-hooks.sh (offer to backup existing hook)
- [ ] T056 [US1] Implement hook installation in install-hooks.sh (copy pre-commit-hook.sh to .git/hooks/pre-commit)
- [ ] T057 [US1] Implement permission setting in install-hooks.sh (chmod +x .git/hooks/pre-commit)
- [ ] T058 [US1] Add success message in install-hooks.sh (display installation confirmation and bypass instructions)

### Manual Validation Tests for User Story 1

- [ ] T059 [US1] Installation Test: Run `scripts/install-hooks.sh` on clean repository, verify hook is installed at .git/hooks/pre-commit
- [ ] T060 [US1] Clean Commit Test: Make changes to clean files, verify commit succeeds without delays
- [ ] T061 [US1] Detekt Violation Test: Introduce complex method (>15 complexity), verify commit blocked with clear error
- [ ] T062 [US1] ktlint Violation Test: Introduce formatting issue (line too long), verify commit blocked with clear error
- [ ] T063 [US1] Android Lint Violation Test: Introduce Android API issue (wrong API level), verify commit blocked with clear error
- [ ] T064 [US1] Warning Display Test: Introduce warning-level issue (magic number), verify commit succeeds with warning displayed
- [ ] T065 [US1] Bypass Test: Test `git commit --no-verify` successfully bypasses hook
- [ ] T066 [US1] Empty Commit Test: Verify hook doesn't fail on commits with no Kotlin files changed
- [ ] T067 [US1] File Rename Test: Verify hook correctly handles renamed Kotlin files
- [ ] T068 [US1] Cross-Platform Test (macOS): Verify installation and execution on macOS
- [ ] T069 [P] [US1] Cross-Platform Test (Linux): Verify installation and execution on Linux (if available)
- [ ] T070 [P] [US1] Cross-Platform Test (Windows): Verify installation and execution on Windows Git Bash (if available)

**Checkpoint**: At this point, User Story 1 should be fully functional - hooks catch violations and block commits with clear messages.

---

## Phase 5: User Story 4 - Fast Feedback on Changed Files Only (Priority: P4)

**Goal**: Optimize static analysis to run quickly by only checking changed files, not the entire codebase, so commit workflow isn't significantly slowed down.

**Independent Test**: Measure commit time when changing 1-2 files versus 10+ files, confirm analysis time scales with changed files only and completes within performance targets (<10 seconds for 2-10 files).

### Performance Optimization for User Story 4

- [ ] T071 [US4] Add file counting to analyze-staged.sh (count staged Kotlin files)
- [ ] T072 [US4] Add large changeset warning in analyze-staged.sh (warn if >30 files, suggest --no-verify option)
- [ ] T073 [US4] Enable Gradle build cache in `gradle.properties` (org.gradle.caching=true)
- [ ] T074 [P] [US4] Enable Gradle configuration cache in `gradle.properties` (org.gradle.configuration-cache=true)
- [ ] T075 [P] [US4] Enable Gradle parallel execution in `gradle.properties` (org.gradle.parallel=true)
- [ ] T076 [US4] Add progress feedback in analyze-staged.sh (echo which files are being checked)
- [ ] T077 [US4] Configure Gradle incremental builds for Detekt in shared and composeApp modules

### Performance Validation for User Story 4

- [ ] T078 [US4] Small Commit Test (1-5 files): Measure analysis time, verify <3 seconds
- [ ] T079 [US4] Medium Commit Test (6-15 files): Measure analysis time, verify <10 seconds  
- [ ] T080 [US4] Large Commit Test (16-30 files): Measure analysis time, verify <20 seconds
- [ ] T081 [US4] Binary File Test: Verify binary files don't cause hook to fail
- [ ] T082 [US4] Generated Files Test: Verify generated files in /build are excluded from analysis

**Checkpoint**: At this point, User Story 4 should be complete - analysis is fast and provides good performance feedback.

---

## Phase 6: User Story 3 - Consistent Code Quality Standards (Priority: P3)

**Goal**: Ensure the same code quality standards are automatically enforced for all developers, maintaining consistent quality without manual enforcement.

**Independent Test**: Have multiple developers on different machines commit code with identical issues, verify they all receive the same analysis results.

### Documentation for User Story 3

- [ ] T083 [US3] Create `docs/static-analysis-setup.md` installation and usage guide
- [ ] T084 [US3] Document installation steps in static-analysis-setup.md (one-time manual script execution: ./scripts/install-hooks.sh, verification steps, expected output per FR-013)
- [ ] T085 [US3] Document usage examples in static-analysis-setup.md (normal commits, understanding output, bypassing hooks)
- [ ] T086 [US3] Document manual analysis commands in static-analysis-setup.md (./gradlew detekt, ktlintCheck, lintDebug)
- [ ] T087 [US3] Document baseline analysis process in static-analysis-setup.md (for project setup)
- [ ] T088 [US3] Document suppression guidelines in static-analysis-setup.md (when and how to suppress violations)
- [ ] T089 [US3] Document troubleshooting in static-analysis-setup.md (common issues and solutions)
- [ ] T090 [US3] Document best practices in static-analysis-setup.md (development workflow, team communication)
- [ ] T091 [US3] Add commands reference in static-analysis-setup.md (quick reference for all commands)
- [ ] T092 [US3] Update `README.md` with static analysis section (link to docs/static-analysis-setup.md)
- [ ] T093 [US3] Add installation instructions in README.md (run scripts/install-hooks.sh)
- [ ] T094 [US3] Add tool overview in README.md (Detekt, ktlint, Android Lint purposes)
- [ ] T095 [US3] Add bypass instructions in README.md (emergency --no-verify usage)

### Team Rollout for User Story 3

- [ ] T096 [US3] Create team communication about hook installation (email/slack message template)
- [ ] T097 [US3] Document Android Lint benefits in team communication (Android-specific checks explanation)
- [ ] T098 [US3] Share quickstart.md guide with team members
- [ ] T099 [US3] Add static analysis setup to onboarding checklist (for new team members)
- [ ] T100 [US3] Schedule team Q&A session for installation support (optional, coordinate with team lead)

### Consistency Validation for User Story 3

- [ ] T101 [US3] Multi-Developer Test: Have 2+ developers commit identical code with issues, verify identical feedback
- [ ] T102 [US3] Rule Consistency Test: Verify all developers see same rules applied (detekt.yml, .editorconfig, lint.xml)
- [ ] T103 [US3] New Developer Test: New team member runs install-hooks.sh, makes first commit, verify hook runs automatically

**Checkpoint**: At this point, User Story 3 should be complete - all team members have consistent quality standards enforced automatically.

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Final improvements and validation across all user stories

- [ ] T104 [P] Verify all configuration files committed to repository (detekt.yml, .editorconfig, composeApp/lint.xml)
- [ ] T104a [P] [M6 Refactoring] Create shared analysis utility in scripts/lib/run-analysis.sh to eliminate duplication between analyze-staged.sh and analyze-baseline.sh (REQUIRED to resolve duplication D1)
- [ ] T105 [P] Verify all scripts are executable and cross-platform compatible (scripts/*.sh)
- [ ] T106 [P] Verify .gitattributes entry exists for shell scripts (*.sh text eol=lf)
- [ ] T107 [P] Run quickstart.md validation: follow guide end-to-end on clean repository
- [ ] T108 Run full manual validation test suite (all tests from User Story 1 manual validation)
- [ ] T109 [P] Review and validate all suppressions have justification comments (grep @Suppress in codebase)
- [ ] T110 [P] Verify detekt.yml and .editorconfig match contracts/ specifications
- [ ] T111 [P] Verify lint.xml matches contracts/ specification
- [ ] T112 Test Tool Crash Scenario: Simulate analysis tool failure, verify graceful error message
- [ ] T113 [P] Collect feedback from team on rule configuration (first 2 weeks after rollout)
- [ ] T114 [P] Iterate on rule configuration based on team feedback (adjust detekt.yml, .editorconfig, lint.xml if needed)
- [ ] T115 Document any rule configuration changes in commit messages with rationale

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Story 2 - P2 (Phase 3)**: Depends on Foundational - MUST complete before US1 (baseline cleanup required)
- **User Story 1 - P1 (Phase 4)**: Depends on US2 completion (clean baseline required before enforcing hooks)
- **User Story 4 - P4 (Phase 5)**: Depends on US1 completion (optimization of existing hooks)
- **User Story 3 - P3 (Phase 6)**: Can start after US1 completion (documentation and rollout)
- **Polish (Phase 7)**: Depends on all user stories being complete

### User Story Dependencies

**IMPORTANT**: Normal priority would suggest P1 → P2 → P3 → P4, but this feature has a unique dependency:

- **User Story 2 (P2)**: MUST complete FIRST - clean baseline required before hook enforcement
- **User Story 1 (P1)**: Can start after US2 - implements hook enforcement on clean codebase
- **User Story 4 (P4)**: Can start after US1 - optimizes existing hooks
- **User Story 3 (P3)**: Can start after US1 - documents and rolls out existing functionality

**Execution Order**: Foundational → US2 (P2) → US1 (P1) → US4 (P4) + US3 (P3 in parallel) → Polish

### Within Each User Story

**User Story 2 (Baseline Cleanup)**:
1. Baseline analysis scripts
2. Generate baseline reports
3. Fix violations
4. Verify zero violations

**User Story 1 (Hook Implementation)**:
1. Hook script implementation
2. Installation script
3. Manual validation tests

**User Story 4 (Performance)**:
1. Performance optimizations
2. Performance validation tests

**User Story 3 (Documentation)**:
1. Documentation creation
2. Team rollout
3. Consistency validation

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel (T002, T003, T004, T005, T007)
- All Foundational tasks marked [P] can run in parallel (T009, T010, T012, T013)
- Within US2: Baseline generation tasks marked [P] can run in parallel (T025, T027, T029)
- Within US1: Cross-platform tests marked [P] can run in parallel (T069, T070)
- Within US4: Gradle config tasks marked [P] can run in parallel (T074, T075)
- Within US3: Documentation tasks can run in parallel with rollout tasks
- User Story 3 (P3) and User Story 4 (P4) can work in parallel after US1 completes

---

## Parallel Example: User Story 2 (Baseline Cleanup)

```bash
# After baseline analysis, these can run in parallel:
Task T025: "Generate Android Lint baseline file"
Task T027: "Fix critical violations in composeApp module"
Task T029: "Fix high-severity violations in composeApp module"

# Different modules, no file conflicts
```

---

## Implementation Strategy

### MVP First (User Story 2 + User Story 1)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 2 - Baseline Cleanup (CRITICAL - blocks US1)
4. Complete Phase 4: User Story 1 - Hook Enforcement (MVP!)
5. **STOP and VALIDATE**: Test hooks with real commits, verify violations are caught
6. Deploy/announce to team

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 2 (Baseline) → Test clean codebase → Ready for enforcement
3. Add User Story 1 (Hooks) → Test with real commits → Deploy/Announce (MVP!)
4. Add User Story 4 (Performance) → Test commit speed → Deploy optimization
5. Add User Story 3 (Documentation) → Rollout to team → Full feature complete

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Team completes User Story 2 (Baseline) together (fixing violations)
3. Once US2 done:
   - Developer A: User Story 1 (Hook Implementation)
   - Developer B: User Story 3 (Documentation) - can start early
4. Once US1 done:
   - Developer A: User Story 4 (Performance)
   - Developer B: Continue User Story 3 (Rollout)
5. Team completes Polish together

---

## Success Criteria Validation

From specification [spec.md](./spec.md):

- **SC-001** (Promptness): Validated through User Story 4 performance tests (T078-T080: <3s, <10s, <20s)
- **SC-002** (90% detection): Validated through User Story 2 baseline analysis (T022) showing comprehensive rule coverage
- **SC-003** (Zero false positives): Validated through User Story 1 manual tests (T059-T070) and team feedback
- **SC-004** (100% automatic config): Validated through User Story 1 installation test (T059) and User Story 3 documentation
- **SC-005** (Changed-files-only): Validated through User Story 4 staged file analysis (T043-T045 in pre-commit-hook.sh)
- **SC-006** (Bypass capability): Validated through User Story 1 bypass test (T065)
- **SC-007** (100% critical/high fixed): Validated through User Story 2 baseline cleanup (T026-T031)
- **SC-008** (Report generation): Validated through User Story 2 baseline report (T022)
- **SC-009** (All violations fixed/suppressed): Validated through User Story 2 final verification (T031)

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Manual validation tests replace automated unit/E2E tests for tooling infrastructure
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- **KEY DIFFERENCE**: User Story 2 (P2) must complete before User Story 1 (P1) due to baseline cleanup requirement

