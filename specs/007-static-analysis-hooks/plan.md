# Implementation Plan: Static Analysis Git Hooks

**Branch**: `007-static-analysis-hooks` | **Date**: 2025-11-19 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/007-static-analysis-hooks/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Implement git pre-commit hooks that automatically run static analysis on Kotlin code changes in the shared module and Android platform. The feature enables developers to catch code quality issues at the earliest point in the development workflow using three complementary tools: Detekt (Kotlin code quality), ktlint (Kotlin formatting), and Android Lint (Android-specific checks for /composeApp). The approach includes: (1) manual installation script for hook setup, (2) baseline violation remediation before enforcement, (3) hard enforcement immediately after baseline cleanup, and (4) local-only scope with CI/CD explicitly out of scope.

## Technical Context

**Language/Version**: Kotlin 1.9.x (Multiplatform), Bash/Shell scripting for hooks  
**Primary Dependencies**: Detekt (Kotlin static analysis), ktlint (Kotlin code formatter/linter), Android Lint (Android-specific checks for /composeApp), Gradle plugins for analysis tools  
**Storage**: N/A (no persistent storage required, reports generated on-demand)  
**Testing**: Bash script testing (bats-core or manual validation), Gradle task testing  
**Target Platform**: Git repositories on developer machines (macOS, Linux, Windows with Git Bash)  
**Project Type**: Tooling/Infrastructure (affects `/shared` and `/composeApp` modules)  
**Performance Goals**: Analysis completes promptly for typical commits (2-10 changed files), ~6-15 seconds including Android Lint  
**Constraints**: Must work cross-platform, must not require CI/CD or network access, must support offline development  
**Scale/Scope**: Applies to ~50-100 Kotlin files across shared module and Android platform, expected team size 3-10 developers

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### KMP Architecture Compliance

- [x] **Thin Shared Layer**: Feature design keeps `/shared` limited to domain models, repository interfaces, and use cases
  - No UI components in `/shared`
  - No ViewModels in `/shared`
  - No platform-specific code in `commonMain`
  - Violation justification: N/A - Feature is tooling infrastructure, does not modify shared layer content

- [x] **Native Presentation**: Each platform implements its own presentation layer
  - Android ViewModels in `/composeApp`
  - iOS ViewModels in Swift in `/iosApp`
  - Web state management in React in `/webApp`
  - Violation justification: N/A - Feature is tooling infrastructure, does not add presentation logic

- [x] **Android MVI Architecture**: Android features follow the mandated Compose MVI loop
  - Single `StateFlow<UiState>` source of truth with immutable data classes
  - Sealed `UserIntent` and optional `UiEffect` types co-located with feature packages
  - Reducers implemented as pure functions (no side effects) and unit-tested
  - `dispatchIntent` entry wired from UI → ViewModel → reducer, with effects delivered via `SharedFlow`
  - Violation justification: N/A - Feature is tooling infrastructure, does not add Android presentation logic

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories
  - Repository interfaces in `/shared/src/commonMain/.../repositories/`
  - Implementations in platform-specific modules
  - Use cases reference interfaces, not concrete implementations
  - Violation justification: N/A - Feature is tooling infrastructure, does not add domain logic

- [x] **Dependency Injection**: Plan includes Koin setup for all platforms
  - Shared domain module defined in `/shared/src/commonMain/.../di/`
  - Android DI modules in `/composeApp/src/androidMain/.../di/`
  - iOS Koin initialization in `/iosApp/iosApp/DI/`
  - Web DI setup (if applicable) in `/webApp/src/di/`
  - Violation justification: N/A - Feature is tooling infrastructure, does not require DI

- [x] **80% Test Coverage - Shared Module**: Plan includes unit tests for shared domain logic
  - Tests located in `/shared/src/commonTest`
  - Coverage target: 80% line + branch coverage
  - Run command: `./gradlew :shared:test koverHtmlReport`
  - Tests use Koin Test for DI in tests
  - Violation justification: N/A - Feature improves test coverage by enforcing quality checks, but the tooling scripts themselves don't require 80% coverage (tested manually)

- [x] **80% Test Coverage - ViewModels**: Plan includes unit tests for ViewModels on each platform
  - Android: Tests in `/composeApp/src/androidUnitTest/`, run `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
  - iOS: Tests in `/iosApp/iosAppTests/ViewModels/`, run via XCTest
  - Web: Tests in `/webApp/src/__tests__/hooks/`, run `npm test -- --coverage`
  - Coverage target: 80% line + branch coverage per platform
  - Violation justification: N/A - Feature is tooling infrastructure, does not add ViewModels

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Web: Playwright tests in `/e2e-tests/web/specs/[feature-name].spec.ts`
  - Mobile: Appium tests in `/e2e-tests/mobile/specs/[feature-name].spec.ts`
  - All tests written in TypeScript
  - Page Object Model / Screen Object Model used
  - Each user story has at least one E2E test
  - **Constitution Exception Justification**: E2E tests waived for developer tooling infrastructure with no user-facing UI. This feature provides git hooks and shell scripts for code quality enforcement, not user-facing functionality. Validation through manual installation tests (tasks.md T059-T070) and real commit attempts provides equivalent coverage for verifying hook behavior, cross-platform compatibility, and violation detection. Manual validation tests cover all acceptance scenarios from user stories without requiring E2E test infrastructure.

- [x] **Platform Independence**: Shared code uses expect/actual for platform dependencies
  - No direct UIKit/Android SDK/Browser API imports in `commonMain`
  - Platform-specific implementations in `androidMain`, `iosMain`, `jsMain`
  - Repository implementations provided via DI, not expect/actual
  - Violation justification: N/A - Feature is tooling infrastructure, does not add shared code

- [x] **Clear Contracts**: Repository interfaces and use cases have explicit APIs
  - Typed return values (`Result<T>`, sealed classes)
  - KDoc documentation for public APIs
  - `@JsExport` for web consumption where needed
  - Violation justification: N/A - Feature is tooling infrastructure with shell scripts and Gradle tasks, not domain APIs

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - Shared: Kotlin Coroutines with `suspend` functions
  - Android: Kotlin Coroutines (`viewModelScope`) + Flow for state
  - iOS: Swift Concurrency (`async`/`await`) with `@MainActor`
  - Web: Native `async`/`await` (no Promise chains)
  - No Combine, RxJava, RxSwift, or callback-based patterns for new code
  - Violation justification: N/A - Feature uses synchronous shell scripts and Gradle tasks, no async programming

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - Android: `testTag` modifier on all interactive composables
  - iOS: `accessibilityIdentifier` modifier on all interactive views
  - Web: `data-testid` attribute on all interactive elements
  - Naming convention: `{screen}.{element}.{action}` (e.g., `petList.addButton.click`)
  - List items use stable IDs (e.g., `petList.item.${id}`)
  - Violation justification: N/A - Feature is tooling infrastructure with no UI

- [x] **Public API Documentation**: Plan ensures all public APIs have documentation
  - Kotlin: KDoc format (`/** ... */`)
  - Swift: SwiftDoc format (`/// ...`)
  - TypeScript: JSDoc format (`/** ... */`)
  - Documentation must be concise and high-level (1-3 sentences: WHAT/WHY, not HOW)
  - All public classes, methods, and properties documented
  - Violation justification: N/A - Feature consists of shell scripts and configuration. Documentation provided in README and inline script comments.

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Unit tests clearly separate setup (Given), action (When), verification (Then)
  - ViewModel tests use Given-When-Then pattern with descriptive names
  - E2E tests structure scenarios with Given-When-Then phases
  - Test names follow platform conventions (backticks for Kotlin, camelCase_with_underscores for Swift, descriptive strings for TypeScript)
  - Comments mark test phases in complex tests
  - Violation justification: N/A - Feature is tooling infrastructure with manual validation, not programmatic tests

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: Plan uses modern Node.js stack for `/server` module
  - Violation justification: N/A - Feature does not affect `/server` module

- [x] **Backend Code Quality**: Plan enforces quality standards for `/server` code
  - Violation justification: N/A - Feature does not affect `/server` module

- [x] **Backend Dependency Management**: Plan minimizes dependencies in `/server/package.json`
  - Violation justification: N/A - Feature does not affect `/server` module

- [x] **Backend Directory Structure**: Plan follows standardized layout in `/server/src/`
  - Violation justification: N/A - Feature does not affect `/server` module

- [x] **Backend TDD Workflow**: Plan follows Test-Driven Development (Red-Green-Refactor)
  - Violation justification: N/A - Feature does not affect `/server` module

- [x] **Backend Testing Strategy**: Plan includes comprehensive test coverage for `/server`
  - Violation justification: N/A - Feature does not affect `/server` module

## Project Structure

### Documentation (this feature)

```text
specs/007-static-analysis-hooks/
├── plan.md              # This file (/speckit.plan command output)
├── spec.md              # Feature specification (already exists)
├── research.md          # Phase 0 output (tool research and decisions)
├── data-model.md        # Phase 1 output (analysis result data structures)
├── quickstart.md        # Phase 1 output (installation and usage guide)
├── contracts/           # Phase 1 output (analysis rule configurations)
│   ├── detekt-config.yml       # Detekt static analysis rules
│   ├── ktlint-config.editorconfig  # ktlint formatting rules
│   └── lint-config.xml         # Android Lint rules for /composeApp
├── checklists/          # Quality validation checklists
│   └── requirements.md  # Specification quality checklist (already exists)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
# Git hooks and tooling scripts
.git/hooks/
└── pre-commit           # Git pre-commit hook (installed by setup script)

scripts/
├── install-hooks.sh     # Manual installation script for git hooks
├── analyze-baseline.sh  # Full codebase analysis for baseline assessment
├── analyze-staged.sh    # Staged files analysis (called by pre-commit hook)
└── track-violations.sh  # Progress tracking for baseline remediation

# Gradle configuration (existing, modified)
build.gradle.kts         # Root Gradle build file (add detekt/ktlint plugins)
shared/build.gradle.kts  # Shared module build file (configure analysis for shared)
composeApp/build.gradle.kts  # Android app build file (configure analysis for Android)

# Static analysis configuration
detekt.yml               # Detekt configuration file (root level)
.editorconfig            # ktlint/EditorConfig rules (root level)
composeApp/lint.xml      # Android Lint configuration (Android module only)
composeApp/lint-baseline.xml  # Android Lint baseline (generated, for migration)

# Documentation
docs/
└── static-analysis-setup.md  # Installation and usage guide (referenced from README)

README.md                # Updated with static analysis setup instructions
```

**Structure Decision**: This feature is tooling infrastructure that adds git hooks and Gradle plugins to the existing Kotlin Multiplatform project. The structure includes:
1. **Git hooks**: Shell scripts in `.git/hooks/` (installed via manual script)
2. **Gradle plugins**: Detekt and ktlint plugins configured in existing `build.gradle.kts` files; Android Lint configured in `composeApp/build.gradle.kts`
3. **Configuration files**: Root-level `detekt.yml` and `.editorconfig` for Kotlin analysis; `composeApp/lint.xml` for Android-specific rules
4. **Installation scripts**: Shell scripts in `/scripts` directory for setup and baseline analysis
5. **Documentation**: Setup guide in `/docs` and updated README

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

No violations requiring justification. All constitution checks pass or are marked N/A for tooling infrastructure that doesn't introduce presentation logic, domain models, or backend code.

## Phase 0: Research & Technology Selection

### Research Tasks

The following unknowns from Technical Context need to be resolved through research:

1. **Static Analysis Tool Selection for Kotlin**
   - Question: Which static analysis tool(s) to use for Kotlin code (Detekt, ktlint, Android Lint, or combination)?
   - Research needed: Compare features, performance, rule coverage, Kotlin Multiplatform support, and community adoption

2. **Gradle Plugin Integration**
   - Question: How to integrate static analysis tools into existing Gradle build for shared and Android modules?
   - Research needed: Best practices for plugin configuration, incremental analysis, and CI/CD readiness

3. **Cross-Platform Git Hook Compatibility**
   - Question: How to ensure git hooks work consistently across macOS, Linux, and Windows (Git Bash)?
   - Research needed: Shell scripting best practices for cross-platform compatibility, path handling, line endings

4. **Baseline Violation Handling Strategy**
   - Question: What's the best approach for managing existing violations during rollout (baseline files, suppression, batch fixes)?
   - Research needed: Detekt baseline feature, suppression comment formats, migration strategies from other projects

5. **Performance Optimization for Large Changesets**
   - Question: How to keep analysis fast when many files are changed (parallel execution, caching)?
   - Research needed: Gradle build cache, detekt performance tuning, git diff strategies for file filtering

### Research Output

See [research.md](./research.md) for detailed findings and decisions.

## Phase 1: Design & Contracts

### Data Model

See [data-model.md](./data-model.md) for complete data structures.

Key entities:
- **Analysis Result**: Structure of static analysis output (file, line, rule, severity, message)
- **Violation Report**: Aggregated violations with file-level and project-level statistics
- **Rule Configuration**: Structure of detekt.yml and .editorconfig files

### API Contracts

See [contracts/](./contracts/) directory for:
- `detekt-config.yml`: Detekt rule configuration with severity levels and exclusions
- `ktlint-config.editorconfig`: ktlint formatting rules aligned with project style

### Installation & Usage Guide

See [quickstart.md](./quickstart.md) for:
- One-time installation instructions for developers
- Commands for baseline analysis and progress tracking
- Bypass mechanism for emergency situations
- Troubleshooting common issues

## Implementation Phases

### Phase 0: Research (Completed Above)
- Tool selection research
- Cross-platform compatibility research
- Baseline handling strategy research

### Phase 1: Configuration & Baseline
1. Add Detekt and ktlint Gradle plugins to build files
2. Configure Android Lint in `composeApp/build.gradle.kts`
3. Create configuration files:
   - `detekt.yml` (root level)
   - `.editorconfig` (root level)
   - `composeApp/lint.xml` (Android Lint rules)
4. Run full codebase analysis to generate baseline reports:
   - Detekt baseline for all modules
   - ktlint check for all modules
   - Android Lint baseline for composeApp only
5. Review and prioritize violations by severity across all three tools
6. Fix or suppress all critical and high-severity violations
7. Document acceptable suppressions with justifications

### Phase 2: Hook Implementation
1. Create `scripts/analyze-staged.sh` to analyze only staged files
   - Detect Kotlin files → run Detekt + ktlint
   - Detect Android files (composeApp/) → also run Android Lint
2. Create `scripts/install-hooks.sh` for manual git hook installation
3. Create `.git/hooks/pre-commit` hook script template
4. Test hook on sample commits with various scenarios:
   - Pure Kotlin changes (shared module only)
   - Android-specific changes (composeApp only)
   - Mixed changes (both shared and composeApp)
   - Android resource changes (XML, manifests)
5. Document bypass mechanism (`git commit --no-verify`)

### Phase 3: Baseline Tooling
1. Create `scripts/analyze-baseline.sh` for full codebase analysis
   - Include Android Lint baseline generation for composeApp
2. Create `scripts/track-violations.sh` for progress tracking
   - Track violations across all three tools (Detekt, ktlint, Android Lint)
3. Add Gradle tasks for running analysis:
   - `./gradlew detekt` (all modules)
   - `./gradlew ktlintCheck` (all modules)
   - `./gradlew :composeApp:lintDebug` (Android-specific)
4. Test baseline analysis and reporting for all tools

### Phase 4: Documentation & Rollout
1. Create `docs/static-analysis-setup.md` installation guide
   - Include Android Lint setup and configuration
2. Update README with static analysis section
   - Document all three tools (Detekt, ktlint, Android Lint)
3. Create team communication about hook installation
   - Explain Android Lint benefits for Android-specific code
4. Support team members during initial setup
5. Monitor and address any issues during rollout

## Testing Strategy

### Manual Validation Tests
1. **Installation Test**: Run `scripts/install-hooks.sh` on clean repository, verify hook is installed
2. **Clean Commit Test**: Make changes to clean files, verify commit succeeds without delays
3. **Violation Detection Test**: Introduce intentional violations, verify commit is blocked with clear messages
4. **Warning Display Test**: Introduce warning-level issues, verify commit succeeds with warnings displayed
5. **Bypass Test**: Test `git commit --no-verify` successfully bypasses hook
6. **Cross-Platform Test**: Verify installation and execution on macOS, Linux, and Windows (Git Bash)
7. **Baseline Analysis Test**: Run `scripts/analyze-baseline.sh`, verify report is generated
8. **Progress Tracking Test**: Run `scripts/track-violations.sh`, verify violation counts are accurate
9. **Android Lint Test**: Introduce Android-specific issue (e.g., wrong API level usage), verify detection and blocking

### Edge Case Validation
1. **Empty Commit**: Verify hook doesn't fail on commits with no Kotlin files changed
2. **File Rename**: Verify hook correctly handles renamed Kotlin files
3. **Large Changeset**: Test with 20+ changed files, verify performance is acceptable
4. **Generated Files**: Verify generated files in `/build` are excluded from analysis
5. **Binary Files**: Verify binary files don't cause hook to fail
6. **Tool Crash**: Simulate analysis tool failure, verify graceful error message

## Success Criteria Mapping

From specification [spec.md](./spec.md):

- **SC-001**: Promptness validated through manual testing with typical 2-10 file commits
- **SC-002**: 90% detection rate validated through baseline analysis showing comprehensive rule coverage
- **SC-003**: Zero false positives validated through review of detekt/ktlint rules and suppressions
- **SC-004**: 100% automatic configuration validated through installation script and documentation
- **SC-005**: Changed-files-only validated through `analyze-staged.sh` git diff filtering
- **SC-006**: Bypass capability validated through `--no-verify` documentation and testing
- **SC-007**: 100% critical/high violations resolved validated through baseline report before rollout
- **SC-008**: Report generation validated through `analyze-baseline.sh` script
- **SC-009**: All violations fixed/suppressed validated through final baseline scan showing zero violations

## Risks & Mitigations

| Risk | Impact | Likelihood | Mitigation |
|------|--------|-----------|------------|
| Developers forget to install hook | High | Medium | Add installation reminder to README, mention in team communication, consider auto-check in CI (future) |
| Hook too slow, disrupts workflow | High | Medium | Analyze only changed files, optimize detekt configuration, provide performance monitoring |
| Too many baseline violations, overwhelming | Medium | High | Prioritize by severity, fix incrementally, allow suppressions with justification |
| Cross-platform incompatibilities | Medium | Low | Test on all platforms early, use standard shell constructs, avoid platform-specific commands |
| False positives block valid commits | High | Low | Carefully configure rules, provide bypass mechanism, iterate based on team feedback |
| Hook conflicts with existing git hooks | Low | Low | Check for existing hooks during installation, provide merge instructions |

## Dependencies

### External Tools
- Detekt (Kotlin static analysis tool)
- ktlint (Kotlin code formatter/linter)
- Android Lint (Android-specific static analysis, built into Android Gradle Plugin)
- Git (already required for development)
- Bash/Shell (available on all platforms)

### Gradle Plugins
- `io.gitlab.arturbosch.detekt` (Detekt Gradle plugin)
- `org.jlleitschuh.gradle.ktlint` (ktlint Gradle plugin)
- Android Gradle Plugin (includes Android Lint, already present)

### No New Runtime Dependencies
This feature adds development tooling only, with no impact on application runtime or user-facing functionality.

## Rollout Plan

### Pre-Rollout (Week 1)
1. Complete research and tool selection
2. Configure Detekt, ktlint, and Android Lint with appropriate rules
3. Run baseline analysis on shared module and Android platform (all three tools)
4. Review violations with team, prioritize fixes
5. Fix or suppress all critical and high-severity violations
6. Verify zero baseline violations remain across all tools

### Initial Rollout (Week 2)
1. Implement and test git hook scripts
2. Create installation documentation
3. Announce feature to team with installation instructions
4. Support team members during installation
5. Collect feedback on rule configuration and performance

### Post-Rollout (Week 3+)
1. Monitor for issues or complaints about false positives
2. Iterate on rule configuration based on team feedback
3. Document common issues and solutions
4. Consider future enhancements (auto-install, CI/CD integration)

## Future Enhancements (Out of Scope)

The following items are explicitly out of scope for this feature but may be considered in future work:

1. **CI/CD Integration**: Running static analysis in continuous integration pipeline
2. **Automated CI Checks**: Blocking pull requests based on static analysis results
3. **Auto-Install on Clone**: Automatically installing hooks on repository clone
4. **IDE Integration**: Configuring IntelliJ IDEA or Android Studio to use same rules
5. **Custom Rule Development**: Creating project-specific detekt or ktlint rules
6. **Metrics Dashboard**: Visualizing code quality trends over time
