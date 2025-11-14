# Implementation Plan: [FEATURE]

**Branch**: `[###-feature-name]` | **Date**: [DATE] | **Spec**: [link]
**Input**: Feature specification from `/specs/[###-feature-name]/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

[Extract from feature spec: primary requirement + technical approach from research]

## Technical Context

<!--
  ACTION REQUIRED: Replace the content in this section with the technical details
  for the project. The structure here is presented in advisory capacity to guide
  the iteration process.
-->

**Language/Version**: [e.g., Python 3.11, Swift 5.9, Rust 1.75 or NEEDS CLARIFICATION]  
**Primary Dependencies**: [e.g., FastAPI, UIKit, LLVM or NEEDS CLARIFICATION]  
**Storage**: [if applicable, e.g., PostgreSQL, CoreData, files or N/A]  
**Testing**: [e.g., pytest, XCTest, cargo test or NEEDS CLARIFICATION]  
**Target Platform**: [e.g., Linux server, iOS 15+, WASM or NEEDS CLARIFICATION]
**Project Type**: [single/web/mobile - determines source structure]  
**Performance Goals**: [domain-specific, e.g., 1000 req/s, 10k lines/sec, 60 fps or NEEDS CLARIFICATION]  
**Constraints**: [domain-specific, e.g., <200ms p95, <100MB memory, offline-capable or NEEDS CLARIFICATION]  
**Scale/Scope**: [domain-specific, e.g., 10k users, 1M LOC, 50 screens or NEEDS CLARIFICATION]

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### KMP Architecture Compliance

- [ ] **Thin Shared Layer**: Feature design keeps `/shared` limited to domain models, repository interfaces, and use cases
  - No UI components in `/shared`
  - No ViewModels in `/shared`
  - No platform-specific code in `commonMain`
  - Violation justification: _[Required if not compliant]_

- [ ] **Native Presentation**: Each platform implements its own presentation layer
  - Android ViewModels in `/composeApp`
  - iOS ViewModels in Swift in `/iosApp`
  - Web state management in React in `/webApp`
  - Violation justification: _[Required if not compliant]_

- [ ] **Interface-Based Design**: Domain logic uses interfaces for repositories
  - Repository interfaces in `/shared/src/commonMain/.../repositories/`
  - Implementations in platform-specific modules
  - Use cases reference interfaces, not concrete implementations
  - Violation justification: _[Required if not compliant]_

- [ ] **Dependency Injection**: Plan includes Koin setup for all platforms
  - Shared domain module defined in `/shared/src/commonMain/.../di/`
  - Android DI modules in `/composeApp/src/androidMain/.../di/`
  - iOS Koin initialization in `/iosApp/iosApp/DI/`
  - Web DI setup (if applicable) in `/webApp/src/di/`
  - Violation justification: _[Required if DI not using Koin]_

- [ ] **80% Test Coverage - Shared Module**: Plan includes unit tests for shared domain logic
  - Tests located in `/shared/src/commonTest`
  - Coverage target: 80% line + branch coverage
  - Run command: `./gradlew :shared:test koverHtmlReport`
  - Tests use Koin Test for DI in tests
  - Violation justification: _[Required if coverage < 80%]_

- [ ] **80% Test Coverage - ViewModels**: Plan includes unit tests for ViewModels on each platform
  - Android: Tests in `/composeApp/src/androidUnitTest/`, run `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
  - iOS: Tests in `/iosApp/iosAppTests/ViewModels/`, run via XCTest
  - Web: Tests in `/webApp/src/__tests__/hooks/`, run `npm test -- --coverage`
  - Coverage target: 80% line + branch coverage per platform
  - Violation justification: _[Required if coverage < 80%]_

- [ ] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Web: Playwright tests in `/e2e-tests/web/specs/[feature-name].spec.ts`
  - Mobile: Appium tests in `/e2e-tests/mobile/specs/[feature-name].spec.ts`
  - All tests written in TypeScript
  - Page Object Model / Screen Object Model used
  - Each user story has at least one E2E test
  - Violation justification: _[Required if E2E tests missing]_

- [ ] **Platform Independence**: Shared code uses expect/actual for platform dependencies
  - No direct UIKit/Android SDK/Browser API imports in `commonMain`
  - Platform-specific implementations in `androidMain`, `iosMain`, `jsMain`
  - Repository implementations provided via DI, not expect/actual
  - Violation justification: _[Required if not compliant]_

- [ ] **Clear Contracts**: Repository interfaces and use cases have explicit APIs
  - Typed return values (`Result<T>`, sealed classes)
  - KDoc documentation for public APIs
  - `@JsExport` for web consumption where needed
  - Violation justification: _[Required if not compliant]_

- [ ] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - Shared: Kotlin Coroutines with `suspend` functions
  - Android: Kotlin Coroutines (`viewModelScope`) + Flow for state
  - iOS: Swift Concurrency (`async`/`await`) with `@MainActor`
  - Web: Native `async`/`await` (no Promise chains)
  - No Combine, RxJava, RxSwift, or callback-based patterns for new code
  - Violation justification: _[Required if using prohibited patterns]_

- [ ] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - Android: `testTag` modifier on all interactive composables
  - iOS: `accessibilityIdentifier` modifier on all interactive views
  - Web: `data-testid` attribute on all interactive elements
  - Naming convention: `{screen}.{element}.{action}` (e.g., `petList.addButton.click`)
  - List items use stable IDs (e.g., `petList.item.${id}`)
  - Violation justification: _[Required if testIDs missing]_

- [ ] **Public API Documentation**: Plan ensures all public APIs have documentation
  - Kotlin: KDoc format (`/** ... */`)
  - Swift: SwiftDoc format (`/// ...`)
  - TypeScript: JSDoc format (`/** ... */`)
  - Documentation must be concise and high-level (1-3 sentences: WHAT/WHY, not HOW)
  - All public classes, methods, and properties documented
  - Violation justification: _[Required if documentation missing]_

## Project Structure

### Documentation (this feature)

```text
specs/[###-feature]/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)
<!--
  ACTION REQUIRED: Replace the placeholder tree below with the concrete layout
  for this feature. Delete unused options and expand the chosen structure with
  real paths (e.g., apps/admin, packages/something). The delivered plan must
  not include Option labels.
-->

```text
# [REMOVE IF UNUSED] Option 1: Single project (DEFAULT)
src/
├── models/
├── services/
├── cli/
└── lib/

tests/
├── contract/
├── integration/
└── unit/

# [REMOVE IF UNUSED] Option 2: Web application (when "frontend" + "backend" detected)
backend/
├── src/
│   ├── models/
│   ├── services/
│   └── api/
└── tests/

frontend/
├── src/
│   ├── components/
│   ├── pages/
│   └── services/
└── tests/

# [REMOVE IF UNUSED] Option 3: Mobile + API (when "iOS/Android" detected)
api/
└── [same as backend above]

ios/ or android/
└── [platform-specific structure: feature modules, UI flows, platform tests]
```

**Structure Decision**: [Document the selected structure and reference the real
directories captured above]

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| [e.g., 4th project] | [current need] | [why 3 projects insufficient] |
| [e.g., Repository pattern] | [specific problem] | [why direct DB access insufficient] |
