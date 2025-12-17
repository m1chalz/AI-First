# PetSpot Project Constitution

<!--
Sync Impact Report:
Version change: 2.5.10 → 3.0.0
MAJOR: Constitution modularized into platform-specific files for optimized context loading

Changes (v3.0.0):
- ADDED: Platform-Specific Reading Guide - routing instructions for single-platform tasks
- ADDED: constitution-ios.md - complete iOS architecture (MVVM-C, SwiftUI, XCTest)
- ADDED: constitution-android.md - complete Android architecture (MVI, Compose, Koin)
- ADDED: constitution-web.md - complete Web architecture (React 18, Vitest, Clean Code)
- ADDED: constitution-backend.md - complete Backend architecture (Express, TDD, Vitest)
- RESTRUCTURED: Main constitution now contains shared principles and routing guide only
- UPDATED: Platform-specific content moved to dedicated files for reduced token consumption

Rationale:
- Multi-platform project with 2400+ lines constitution was too large for single-platform tasks
- Modular approach allows agents to load only relevant platform context
- Reduces token consumption significantly for platform-specific tasks
- Maintains full compliance while improving agent performance

Templates requiring updates:
- ✅ .specify/templates/plan-template.md - no changes needed (references constitution generically)
- ✅ .specify/templates/spec-template.md - no changes needed
- ✅ .specify/templates/tasks-template.md - no changes needed

Previous version (v2.5.10):
PATCH: Added code formatting requirement for backend and webapp
-->

## Platform-Specific Reading Guide

> **IMPORTANT**: For single-platform tasks, read ONLY the relevant platform constitution file.
> This reduces context size and improves response quality.

### Routing Instructions

**For iOS-only tasks** (`/iosApp`):
- Read: `constitution-ios.md`
- Skip: This file (after this section), other platform files

**For Android-only tasks** (`/composeApp`):
- Read: `constitution-android.md`
- Skip: This file (after this section), other platform files

**For Web-only tasks** (`/webApp`):
- Read: `constitution-web.md`
- Skip: This file (after this section), other platform files

**For Backend-only tasks** (`/server`):
- Read: `constitution-backend.md`
- Skip: This file (after this section), other platform files

**For cross-platform tasks** (multiple platforms, architecture decisions, E2E tests):
- Read: This file (full constitution)
- May also need: Relevant platform-specific files

### Platform Files Summary

| Platform | File | Architecture | Key Tech |
|----------|------|--------------|----------|
| iOS | `constitution-ios.md` | MVVM-C | Swift, SwiftUI, XCTest |
| Android | `constitution-android.md` | MVI | Kotlin, Compose, Koin |
| Web | `constitution-web.md` | Clean Code + Hooks | React 18, TypeScript, Vitest |
| Backend | `constitution-backend.md` | Clean Code + TDD | Express, TypeScript, Vitest |

---

## Project Overview

**PetSpot** is a multi-platform pet adoption application with independent platform implementations consuming a shared Node.js backend API.

### Build & Test Commands (Quick Reference)

| Platform | Build | Test | Coverage Report |
|----------|-------|------|-----------------|
| Android | `./gradlew :composeApp:assembleDebug` | `./gradlew :composeApp:testDebugUnitTest koverHtmlReport` | `composeApp/build/reports/kover/html/index.html` |
| iOS | Xcode build | `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES` | Xcode coverage report |
| Web | `npm run build` (webApp/) | `npm test --coverage` (webApp/) | `webApp/coverage/index.html` |
| Backend | `npm start` (server/) | `npm test --coverage` (server/) | `server/coverage/index.html` |
| E2E Web | - | `mvn test -Dtest=WebTestRunner` (e2e-tests/java/) | `target/cucumber-reports/web/index.html` |
| E2E Android | - | `mvn test -Dtest=AndroidTestRunner` (e2e-tests/java/) | `target/cucumber-reports/android/index.html` |
| E2E iOS | - | `mvn test -Dtest=IosTestRunner` (e2e-tests/java/) | `target/cucumber-reports/ios/index.html` |

## Core Principles (Shared)

These principles apply across ALL platforms. Platform-specific details are in dedicated constitution files.

### I. Platform Independence (NON-NEGOTIABLE)

Each platform MUST implement its full technology stack independently:

- **Android** (`/composeApp`): Kotlin, Jetpack Compose, MVI architecture, Koin DI
- **iOS** (`/iosApp`): Swift, SwiftUI, MVVM-C architecture, Manual DI
- **Web** (`/webApp`): TypeScript, React 18, Clean Code + Hooks, Context DI
- **Backend** (`/server`): TypeScript, Express.js, Clean Code + TDD, Manual DI

**Architecture Rules**:
- Platforms MUST NOT share compiled code (no Kotlin Multiplatform, no shared libraries)
- Platforms MAY share design patterns and architectural conventions
- Platforms MUST consume backend APIs via HTTP/REST (common integration point)
- Each platform MUST be independently buildable, testable, and deployable
- Domain models MAY differ between platforms based on platform-specific needs
- Business logic MUST be implemented per platform (no shared use cases)

**Rationale**: Platform independence maximizes flexibility, allows each platform to leverage native frameworks and idioms without compromise, eliminates cross-platform build complexity, and enables independent team scaling.

### II. 80% Unit Test Coverage (NON-NEGOTIABLE)

Each platform MUST maintain minimum 80% line + branch coverage for business logic and presentation logic.

**Testing Requirements** (all platforms):
- MUST test happy path, error cases, and edge cases
- MUST follow Given-When-Then structure
- MUST use descriptive test names following platform conventions
- MUST test behavior, not implementation details
- MUST use test doubles (fakes, mocks) for dependencies

See platform-specific constitution files for detailed testing standards.

### III. Interface-Based Design (NON-NEGOTIABLE)

All domain logic classes MUST follow interface-based design:

- Repository pattern with interfaces/protocols per platform
- Implementations in data layer
- Clear contracts between layers

**Benefits**:
- Enables test doubles without mocking frameworks
- Improves testability and maintains 80% coverage requirement
- Clear boundaries between domain and infrastructure concerns

### IV. Dependency Injection (NON-NEGOTIABLE)

Each platform MUST use dependency injection:

| Platform | DI Approach | Status |
|----------|-------------|--------|
| Android | Koin | MANDATORY |
| iOS | Manual DI (constructor injection) | MANDATORY |
| Web | React Context | RECOMMENDED |
| Backend | Manual DI (factory functions) | MANDATORY |

### V. Asynchronous Programming (NON-NEGOTIABLE)

All asynchronous operations MUST follow platform-specific async patterns:

| Platform | Async Pattern | Prohibited |
|----------|---------------|------------|
| Android | Kotlin Coroutines + Flow | RxJava, LiveData |
| iOS | Swift Concurrency (async/await) | Combine, RxSwift |
| Web | Native async/await | Promise.then(), RxJS |
| Backend | Native async/await | Callbacks, Promise chains |

### VI. Test Identifiers (NON-NEGOTIABLE)

All interactive UI elements MUST have stable test identifiers for E2E testing:

| Platform | Attribute | Example |
|----------|-----------|---------|
| Android | `Modifier.testTag()` | `petList.addButton.click` |
| iOS | `.accessibilityIdentifier()` | `petList.addButton` |
| Web | `data-testid` | `petList.addButton.click` |

**Naming Convention**: `{screen}.{element}.{action?}`

### VII. Public API Documentation (NON-NEGOTIABLE)

Public APIs MUST have concise documentation when purpose is not clear from naming:

- MUST skip documentation for self-explanatory names
- MUST use platform-native format (KDoc, SwiftDoc, JSDoc)
- MUST be 1-3 sentences (WHAT/WHY, not HOW)
- Backend and WebApp: Minimal documentation policy - document only when genuinely unclear

### VIII. Given-When-Then Test Convention (NON-NEGOTIABLE)

All tests MUST follow Given-When-Then (Arrange-Act-Assert) structure:

- Clear separation of setup (Given), action (When), verification (Then)
- Descriptive test names explaining the scenario
- One behavior per test case

**Backend/WebApp Comment Format**:
- Use `// given`, `// when`, `// then` (lowercase, no additional text)
- Reuse variables from `// given` in `// then` phase

### IX-XIV. Platform-Specific Architecture

Detailed architecture patterns are in platform-specific files:

- **IX. Backend Architecture**: See `constitution-backend.md`
- **X. Android MVI Architecture**: See `constitution-android.md`
- **XI. iOS MVVM-C Architecture**: See `constitution-ios.md`
- **XII. E2E Testing**: See below
- **XIII. Web Architecture**: See `constitution-web.md`
- **XIV. Performance Not a Concern**: Performance verification is NOT required for this project

## Platform Architecture Summary

```
┌─────────────────────────────────────────────────────────────┐
│  Android (composeApp) - INDEPENDENT PLATFORM                │
│  Kotlin + Jetpack Compose + MVI + Koin                      │
└──────────────────────────────┬──────────────────────────────┘
                               │ HTTP requests
                               ▼
                    ┌────────────────────────┐
                    │  /server (Node.js)     │
                    │  Express + TypeScript  │
                    │  SQLite → PostgreSQL   │
                    └────────────┬───────────┘
                                 │ HTTP requests
          ┌──────────────────────┴─────────────────────────┐
          │                                                 │
          ▼                                                 ▼
┌───────────────────────────────────┐   ┌──────────────────────────────────┐
│  iOS (iosApp) - INDEPENDENT       │   │  Web (webApp) - INDEPENDENT      │
│  Swift + SwiftUI + MVVM-C         │   │  React 18 + TypeScript           │
│  Manual DI                        │   │  Hooks + Context DI              │
└───────────────────────────────────┘   └──────────────────────────────────┘
```

## End-to-End Testing (NON-NEGOTIABLE)

### Unified E2E Project (`/e2e-tests/java`)

- **Stack**: Java 21 + Maven + Cucumber + Selenium/Appium
- **Features**: `/e2e-tests/java/src/test/resources/features/`
- Platform execution controlled by Cucumber tags: `@web`, `@android`, `@ios`

### Test Organization

| Type | Location | Pattern |
|------|----------|---------|
| Web Pages | `.../pages/` | Page Object Model with XPath |
| Mobile Screens | `.../screens/` | Screen Object Model (unified iOS/Android) |
| Web Steps | `.../steps/web/` | Web-specific step definitions |
| Mobile Steps | `.../steps/mobile/` | Mobile step definitions (iOS/Android) |

### Run Commands

```bash
# From e2e-tests/java/
mvn test -Dtest=WebTestRunner      # Web tests (@web tag)
mvn test -Dtest=AndroidTestRunner  # Android tests (@android tag)
mvn test -Dtest=IosTestRunner      # iOS tests (@ios tag)
```

### Reports

- Web: `target/cucumber-reports/web/index.html`
- Android: `target/cucumber-reports/android/index.html`
- iOS: `target/cucumber-reports/ios/index.html`

## Governance

This constitution supersedes all other architectural guidelines.

### Amendment Process

1. Propose amendment with rationale and migration plan
2. Review impact on existing features
3. Update constitution version (see semantic versioning below)
4. Update all affected templates and documentation
5. Communicate changes to all contributors

### Versioning

- **MAJOR**: Breaking changes to core principles, significant restructuring
- **MINOR**: New principle added or significant clarification
- **PATCH**: Typo fixes, wording improvements, non-semantic changes

### Compliance

All pull requests MUST verify compliance with platform-specific constitution files.

**Cross-Platform Requirements**:
- Verify platform-specific code resides in correct modules (no shared compiled code)
- Run platform-specific unit tests and ensure 80%+ coverage
- Verify all new interactive UI elements have test identifiers
- Verify all new tests follow Given-When-Then structure

See platform-specific constitution files for detailed compliance checklists.

### Living Documentation

This constitution guides runtime development. For command-specific workflows,
see `.specify/templates/commands/*.md` files (if present).

**Platform Constitution Files**:
- `constitution-ios.md` - iOS (MVVM-C, SwiftUI, XCTest)
- `constitution-android.md` - Android (MVI, Compose, Koin)
- `constitution-web.md` - Web (React 18, Vitest, Clean Code)
- `constitution-backend.md` - Backend (Express, TDD, Vitest)

---

**Version**: 3.0.0 | **Ratified**: 2025-11-14 | **Last Amended**: 2025-12-17
