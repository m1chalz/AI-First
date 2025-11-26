# Implementation Plan: E2E Testing Stack Migration to Java/Maven/Selenium/Cucumber

**Branch**: `016-e2e-java-migration` | **Date**: 2025-11-25 (Updated) | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/016-e2e-java-migration/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Establish Java/Maven/Selenium/Cucumber E2E testing infrastructure alongside existing TypeScript tests, enabling **gradual migration** as developers work on feature branches. The new Java-based stack will **coexist with TypeScript/Playwright (web) and TypeScript/WebdriverIO (mobile)** tests during an indefinite transition period. Developers working on feature branches can choose which test stack to use/update, with TypeScript tests remaining until all active branches migrate organically. **No forced migration timeline** - both test stacks will run independently without conflicts in CI/CD.

**Key Change from Previous Strategy**: Migration is no longer "big-bang" complete replacement. Instead, it's infrastructure enablement allowing gradual, branch-by-branch migration at developer discretion.

## Technical Context

**Language/Version**: Java 21 (LTS)  
**Primary Dependencies**: 
- Selenium WebDriver 4.x (web automation)
- Appium 9.x (mobile automation)
- Cucumber-Java 7.x (BDD test framework)
- JUnit 5 (test runner)
- Maven 3.6+ (build tool)

**Storage**: File-based test artifacts
- Cucumber HTML reports: `/e2e-tests/target/cucumber-reports/{web|android|ios}/`
- Screenshots on failure: `/e2e-tests/target/screenshots/`
- Execution logs: `/e2e-tests/target/logs/`

**Testing**: E2E tests only (no unit/integration tests in this project)
- Framework: Cucumber with Gherkin syntax
- Web: Selenium WebDriver with Page Object Model (XPath locators)
- Mobile: Appium with Screen Object Model (dual annotations for iOS/Android)
- Test execution via Maven with Cucumber tag filtering

**Target Platform**: 
- Web: Chrome browser (latest stable) on Linux/macOS/Windows
- Android: Android 14 (API 34) emulator
- iOS: iOS 17 simulator (Xcode 15+)
- CI/CD: Maven-based execution (alongside existing npm/TypeScript execution)

**Project Type**: Testing infrastructure (standalone E2E test project)

**Performance Goals**: 
- HTML report generation within 5 seconds of test completion
- Test execution time within 10% of equivalent TypeScript-based test execution time

**Constraints**: 
- Chrome-only support for web (no Firefox/Edge/Safari)
- Single OS version per mobile platform (Android 14, iOS 17)
- Emulators/simulators only (no real device testing)
- **Dual test stack coexistence** - TypeScript tests must remain functional
- No forced migration timeline - organic migration as developers update feature branches

**Scale/Scope**: 
- Establish Java/Cucumber test infrastructure for future tests
- Enable developers to migrate existing tests from TypeScript incrementally
- Support both test stacks running in parallel during transition

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is a testing infrastructure migration affecting only `/e2e-tests/` directory. Platform-specific code checks marked N/A where not applicable.

### Platform Architecture Compliance

- [x] **Platform Independence**: N/A - This feature does not modify platform-specific code
  - This feature only affects E2E testing infrastructure in `/e2e-tests/`
  - No changes to `/composeApp`, `/iosApp`, `/webApp`, or `/server` modules
  - Platform independence maintained as tests consume platform code via UI automation APIs

- [x] **Android MVI Architecture**: N/A - No Android platform code changes
  - E2E tests interact with Android UI via Appium (external to platform code)
  - Test identifiers (testTag) in Android code remain unchanged (FR-011)

- [x] **iOS MVVM-C Architecture**: N/A - No iOS platform code changes
  - E2E tests interact with iOS UI via Appium (external to platform code)
  - Test identifiers (accessibilityIdentifier) in iOS code remain unchanged (FR-011)

- [x] **Interface-Based Design**: Applied to test infrastructure
  - Page Object Model: Java classes with @FindBy annotations abstract web element location
  - Screen Object Model: Java classes with dual annotations (@iOSXCUITFindBy, @AndroidFindBy) abstract mobile element location
  - Step Definitions: Cucumber step classes interact with Page/Screen Objects (not directly with WebDriver/Appium)
  - Clear separation between test scenarios (Gherkin), step logic (Java), and element location (Page/Screen Objects)

- [x] **Dependency Injection**: Not applicable to Cucumber/Selenium/Appium test structure
  - Cucumber manages step definition instantiation automatically
  - Page/Screen Objects instantiated via standard constructor patterns in step definitions
  - No complex DI framework needed for test infrastructure

- [x] **80% Test Coverage - Platform-Specific**: N/A - E2E tests are not unit-tested
  - E2E tests themselves are validation artifacts, not production code requiring unit tests
  - Coverage requirement applies to platform code being tested, not test code itself

- [x] **End-to-End Tests**: This feature IS the E2E test infrastructure migration
  - Establishing new Java/Cucumber E2E test infrastructure
  - Maintaining existing TypeScript E2E tests during transition
  - Enabling gradual migration of test coverage from TypeScript to Java
  - Web tests: Selenium + Cucumber + Page Object Model
  - Mobile tests: Appium + Cucumber + Screen Object Model

- [x] **Asynchronous Programming Standards**: N/A - Selenium/Appium are synchronous blocking APIs
  - Selenium WebDriver operations are synchronous (Java blocks until action completes)
  - Appium operations are synchronous (Java blocks until action completes)
  - No async/await, coroutines, or reactive patterns needed in test code

- [x] **Test Identifiers for UI Controls**: Required - Platform code must maintain test identifiers
  - Android: Existing `testTag` modifiers MUST remain unchanged (FR-011)
  - iOS: Existing `accessibilityIdentifier` modifiers MUST remain unchanged (FR-011)
  - Web: Existing `data-testid` attributes MUST remain unchanged (FR-011)
  - Java test code will reference these stable identifiers via XPath/Appium locators

- [x] **Public API Documentation**: Applied selectively to test infrastructure
  - Page Object classes: Document purpose of each page and key interaction methods
  - Screen Object classes: Document purpose of each screen and platform-specific behaviors
  - Complex step definitions: Document business logic when not obvious from Gherkin
  - Utility classes: Document reusable helpers and configuration classes
  - Format: Standard JavaDoc (`/** ... */`)

- [x] **Given-When-Then Test Structure**: Enforced by Cucumber framework
  - All test scenarios written in Gherkin syntax with explicit Given/When/Then keywords
  - Step definitions implement each phase with clear separation
  - Test reports show Given-When-Then phases explicitly
  - Convention mandated by Cucumber framework structure

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: N/A - No `/server` module changes
  - This feature affects only `/e2e-tests/` directory
  - Backend tests may be added later but backend code remains unchanged

- [x] **Backend Code Quality**: N/A - No `/server` module changes

- [x] **Backend Dependency Management**: N/A - No `/server` module changes

- [x] **Backend Directory Structure**: N/A - No `/server` module changes

- [x] **Backend TDD Workflow**: N/A - E2E tests validate entire system end-to-end

- [x] **Backend Testing Strategy**: N/A - No `/server` module changes

## Project Structure

### Documentation (this feature)

```text
specs/016-e2e-java-migration/
├── plan.md              # This file (/speckit.plan command output) - UPDATED for gradual migration
├── research.md          # Phase 0 output - UPDATED for dual-stack coexistence
├── data-model.md        # Phase 1 output (Page/Screen Object entity definitions)
├── quickstart.md        # Phase 1 output - UPDATED for both test stacks
├── contracts/           # Phase 1 output (Sample .feature files, Page/Screen Object templates)
│   ├── sample-web.feature
│   ├── sample-mobile.feature
│   ├── PageObjectTemplate.java
│   └── ScreenObjectTemplate.java
├── checklists/
│   └── requirements.md  # Spec quality checklist (completed)
├── spec.md              # Feature specification (input) - UPDATED with new migration strategy
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
/e2e-tests/                           # New unified Java/Maven E2E test project
├── pom.xml                           # Maven project configuration with all dependencies
├── src/
│   ├── test/
│   │   ├── java/
│   │   │   └── com/intive/aifirst/petspot/e2e/
│   │   │       ├── pages/           # Web Page Object Model classes
│   │   │       │   ├── PetListPage.java
│   │   │       │   ├── PetDetailsPage.java
│   │   │       │   └── CreateAnnouncementPage.java
│   │   │       ├── screens/         # Mobile Screen Object Model classes
│   │   │       │   ├── PetListScreen.java
│   │   │       │   ├── PetDetailsScreen.java
│   │   │       │   └── CreateAnnouncementScreen.java
│   │   │       ├── steps/           # Cucumber step definitions
│   │   │       │   ├── web/         # Web-specific step definitions
│   │   │       │   │   ├── PetListWebSteps.java
│   │   │       │   │   └── CommonWebSteps.java
│   │   │       │   └── mobile/      # Mobile-specific step definitions
│   │   │       │       ├── PetListMobileSteps.java
│   │   │       │       └── CommonMobileSteps.java
│   │   │       ├── utils/           # Shared utilities (drivers, configs, helpers)
│   │   │       │   ├── WebDriverManager.java
│   │   │       │   ├── AppiumDriverManager.java
│   │   │       │   ├── ScreenshotUtil.java
│   │   │       │   └── TestConfig.java
│   │   │       └── runners/         # Cucumber test runners
│   │   │           ├── WebTestRunner.java
│   │   │           ├── AndroidTestRunner.java
│   │   │           └── IosTestRunner.java
│   │   └── resources/
│   │       ├── features/            # Gherkin feature files
│   │       │   ├── web/
│   │       │   │   ├── pet-list.feature
│   │       │   │   └── create-announcement.feature
│   │       │   └── mobile/
│   │       │       ├── pet-list.feature
│   │       │       └── create-announcement.feature
│   │       └── cucumber.properties  # Cucumber configuration
└── target/                          # Maven output directory (generated)
    ├── cucumber-reports/            # Cucumber HTML reports
    │   ├── web/
    │   ├── android/
    │   └── ios/
    ├── screenshots/                 # Screenshots on test failure
    └── logs/                        # Execution logs

# COEXISTING TypeScript test infrastructure (NOT REMOVED):
/e2e-tests-legacy/                   # OR keep in /e2e-tests/ with different structure
├── web/                             # Existing Playwright tests (TypeScript) - REMAINS
│   ├── specs/
│   ├── pages/
│   └── playwright.config.ts
├── mobile/                          # Existing Appium+WebdriverIO tests (TypeScript) - REMAINS
│   ├── specs/
│   ├── screens/
│   └── wdio.conf.ts
└── package.json                     # npm dependencies - REMAINS FUNCTIONAL

# Alternative: Keep both in /e2e-tests/ with subdirectories:
/e2e-tests/
├── java/                            # New Maven/Java/Cucumber tests
│   ├── pom.xml
│   └── src/...
└── typescript/                      # Existing TypeScript tests (renamed for clarity)
    ├── web/
    ├── mobile/
    └── package.json
```

**Structure Decision**: 

This is a **testing infrastructure enablement project** establishing Java/Maven/Selenium/Appium/Cucumber alongside existing TypeScript tests. The new Java stack will coexist with TypeScript/Playwright (web) and TypeScript/WebdriverIO (mobile) during an indefinite transition period.

Key design decisions for **dual-stack coexistence**:
- **Separate directories**: Java tests in `/e2e-tests/java/` (or new `/e2e-tests/`), TypeScript tests remain in existing location or moved to `/e2e-tests/typescript/` for clarity
- **Independent execution**: Maven commands for Java tests, npm commands for TypeScript tests - no interference
- **CI/CD support**: Pipeline updated to support both test stacks, may run both during transition to ensure parity
- **No forced migration**: Developers on feature branches choose which stack to use/update
- **Organic removal**: TypeScript tests removed only when all active feature branches no longer depend on them

## Complexity Tracking

> No constitution violations requiring justification. All checks passed or marked N/A appropriately.

This feature maintains architectural compliance by:
- Not modifying any platform-specific code (Android/iOS/Web/Backend modules untouched)
- Following interface-based design principles in test infrastructure (Page/Screen Objects abstract element location)
- Maintaining existing test identifiers in platform code (FR-011)
- Using Cucumber's Given-When-Then structure by default
- Applying documentation standards to test infrastructure classes
- Supporting dual test stack coexistence without architectural conflicts

The migration is isolated to `/e2e-tests/` directory and introduces **gradual enablement** rather than forced replacement, eliminating migration risk and complexity.

## Migration Strategy Notes

**Previous Plan (Deprecated)**: Complete replacement with no transition period
**Updated Plan (Current)**: Gradual migration with indefinite dual-stack coexistence

**Rationale for Change**:
- Developers work on feature branches with existing tests
- Forcing immediate migration creates unnecessary churn and risk
- Gradual adoption allows validation and confidence-building
- No business pressure to remove TypeScript tests prematurely
- Both stacks can run in CI/CD to ensure parity during transition

**Implementation Approach**:
1. Establish Java/Cucumber infrastructure (pom.xml, utilities, templates)
2. Create example tests demonstrating Java stack capabilities
3. Document migration guide for developers
4. Update CI/CD to support both test stacks
5. Allow organic migration as developers update feature branches
6. Monitor adoption and provide migration support
7. Remove TypeScript infrastructure only when all active branches migrated (no fixed timeline)
