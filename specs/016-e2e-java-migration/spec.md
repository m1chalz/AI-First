# Feature Specification: E2E Testing Stack Migration to Java/Maven/Selenium/Cucumber

**Feature Branch**: `016-e2e-java-migration`  
**Created**: 2025-11-25  
**Status**: Draft  
**Input**: User description: "Migrate E2E testing stack from TypeScript/Playwright to Java/Maven/Selenium/Cucumber"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Web E2E Test Execution with Selenium (Priority: P1)

QA engineers and developers need to write and execute end-to-end tests for the web application using Selenium WebDriver with Java and Cucumber. Tests must be defined in Gherkin syntax and executed via Maven commands with platform-specific tags.

**Why this priority**: Web E2E testing is the foundation of the new stack. Without this working, mobile tests cannot be validated using the same patterns and tooling.

**Independent Test**: Can be fully tested by creating a simple web E2E test (e.g., login flow), writing it in Gherkin with @web tag, implementing step definitions in Java, and running `mvn test -Dcucumber.filter.tags="@web"` to verify execution and HTML report generation.

**Acceptance Scenarios**:

1. **Given** a feature file exists with @web tag in `/e2e-tests/src/test/resources/features/web/`, **When** developer runs `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web"`, **Then** tests execute using Selenium WebDriver and generate HTML report at `/e2e-tests/target/cucumber-reports/web/index.html`
2. **Given** Page Object Model classes exist with @FindBy XPath annotations, **When** step definitions reference these page objects, **Then** tests interact with web elements correctly using XPath locators
3. **Given** existing Playwright web tests in TypeScript, **When** developer migrates tests for their feature branch, **Then** equivalent Java/Cucumber/Selenium tests provide same test coverage while TypeScript tests remain for other branches

---

### User Story 2 - Mobile E2E Test Execution with Appium (Priority: P2)

QA engineers need to write and execute end-to-end tests for Android and iOS mobile applications using Appium with Java and Cucumber. Screen Object Model classes must support both platforms using dual annotations.

**Why this priority**: Mobile testing depends on having the unified Maven/Cucumber infrastructure established in P1. It builds on the same patterns but adds platform-specific complexity.

**Independent Test**: Can be fully tested by creating a mobile E2E test (e.g., pet list view), writing it in Gherkin with @android and @ios tags, implementing screen objects with dual annotations, and running platform-specific Maven commands to verify execution on both Android and iOS.

**Acceptance Scenarios**:

1. **Given** a feature file exists with @android tag in `/e2e-tests/src/test/resources/features/mobile/`, **When** developer runs `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@android"`, **Then** tests execute on Android using Appium and generate HTML report at `/e2e-tests/target/cucumber-reports/android/index.html`
2. **Given** a feature file exists with @ios tag, **When** developer runs `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@ios"`, **Then** tests execute on iOS using Appium and generate HTML report at `/e2e-tests/target/cucumber-reports/ios/index.html`
3. **Given** Screen Object Model classes with @iOSXCUITFindBy and @AndroidFindBy annotations, **When** step definitions use these screen objects, **Then** tests work on both iOS and Android without duplicating code
4. **Given** existing Appium+WebdriverIO mobile tests in TypeScript, **When** developer migrates tests for their feature branch, **Then** equivalent Java/Cucumber/Appium tests provide same test coverage while TypeScript tests remain for other branches

---

### User Story 3 - Unified Maven Project Configuration (Priority: P3)

Developers need a single Maven pom.xml file that manages dependencies for Selenium, Appium, and Cucumber with proper plugin configuration for generating separate HTML reports per platform.

**Why this priority**: Project configuration is essential infrastructure but can be set up incrementally as tests are migrated. It's lower priority than having working test execution patterns.

**Independent Test**: Can be fully tested by verifying pom.xml contains correct dependencies (Selenium, Appium, Cucumber), running `mvn clean install` successfully, and confirming Maven Surefire/Cucumber plugins generate separate HTML reports for web/android/ios in expected directories.

**Acceptance Scenarios**:

1. **Given** pom.xml exists at `/e2e-tests/pom.xml`, **When** developer runs `mvn clean install`, **Then** all dependencies (Selenium, Appium, Cucumber, JUnit) are resolved and project builds successfully
2. **Given** Maven Cucumber plugin is configured in pom.xml, **When** tests are executed with different tags, **Then** separate HTML reports are generated at `/e2e-tests/target/cucumber-reports/{web|android|ios}/index.html`
3. **Given** shared test utilities exist in `/e2e-tests/src/test/java/.../utils/`, **When** tests from any platform import these utilities, **Then** code can be reused across web and mobile tests

---

### Edge Cases

- What happens when a test runs without any matching tags (no @web, @android, or @ios)?
  - Maven should skip test execution or warn that no tests matched the filter
- What happens when Page Object elements cannot be located (XPath not found)?
  - Test should fail with clear error message indicating which element and XPath failed
- What happens when Screen Object dual annotations have conflicting locators between iOS and Android?
  - Step definitions should handle platform detection and use appropriate annotation dynamically
- What happens when Cucumber HTML reports fail to generate?
  - Maven build should fail or warn, indicating plugin configuration issue
- What happens when both TypeScript and Java tests exist for the same feature?
  - Both test stacks run independently - developers on feature branches choose which stack to use/update
  - TypeScript tests remain as fallback until feature branch migrates to Java tests
  - CI/CD may run both stacks during transition period to ensure parity
- What happens when screenshot capture fails during test failure?
  - Test execution continues and failure is logged, but screenshot absence should not cause secondary failure
- What happens to debugging artifacts (screenshots, logs) from previous test runs?
  - Artifacts should be cleaned/overwritten on each test run to prevent storage bloat
- What happens when a feature branch has migrated to Java tests but TypeScript tests still exist?
  - TypeScript tests remain in repository until all feature branches complete migration
  - Developers on migrated branches use Java tests exclusively
  - Old TypeScript tests can be removed only when no active feature branches depend on them

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: E2E test project MUST use single Maven pom.xml at `/e2e-tests/pom.xml` for dependency management
- **FR-002**: Web E2E tests MUST use Selenium WebDriver (Java) with Cucumber for scenario definitions
- **FR-003**: Mobile E2E tests MUST use Appium (Java) with Cucumber for scenario definitions
- **FR-004**: Test scenarios MUST be written in Gherkin syntax (.feature files) with platform-specific tags (@web, @android, @ios)
- **FR-005**: Web tests MUST use Page Object Model pattern with @FindBy XPath annotations for element location
- **FR-006**: Mobile tests MUST use Screen Object Model pattern with dual annotations (@iOSXCUITFindBy and @AndroidFindBy)
- **FR-007**: Step definitions MUST be organized by platform type: `/e2e-tests/src/test/java/.../steps/web/` and `/e2e-tests/src/test/java/.../steps/mobile/`
- **FR-008**: Tests MUST be executable via Maven commands:
  - Web: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web"`
  - Android: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@android"`
  - iOS: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@ios"`
- **FR-009**: Each platform MUST generate separate Cucumber HTML reports:
  - Web: `/e2e-tests/target/cucumber-reports/web/index.html`
  - Android: `/e2e-tests/target/cucumber-reports/android/index.html`
  - iOS: `/e2e-tests/target/cucumber-reports/ios/index.html`
- **FR-010**: Java/Cucumber test infrastructure MUST enable equivalent test coverage as TypeScript tests when developers migrate their feature tests
- **FR-011**: Test identifiers in platform code (testTag, accessibilityIdentifier, data-testid) MUST remain unchanged to work with new Java-based locators
- **FR-012**: CI/CD pipeline MUST support executing both TypeScript and Java E2E tests:
  - Maven commands for Java/Cucumber tests: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@platform"`
  - npm commands for TypeScript tests continue to work during migration period
  - Pipeline may run both test stacks to ensure test parity during transition
- **FR-013**: Test execution MUST capture debugging artifacts on failure:
  - Screenshot of browser/mobile screen at failure point
  - Console logs (browser console for web, device logs for mobile)
  - Selenium/Appium execution logs
  - Artifacts stored in `/e2e-tests/target/screenshots/` and `/e2e-tests/target/logs/`
- **FR-014**: Dual test stack coexistence MUST be supported:
  - TypeScript E2E tests (Playwright/WebdriverIO) continue to function during migration period
  - Java E2E tests (Selenium/Appium/Cucumber) coexist alongside TypeScript tests
  - Both test stacks can be executed independently without conflicts
  - Developers on feature branches choose which test stack to use/update
  - TypeScript test infrastructure remains until all feature branches migrate to Java

### Key Entities

- **Feature File**: Gherkin scenario definitions with platform-specific tags (@web, @android, @ios) stored in `/e2e-tests/src/test/resources/features/{web|mobile}/`
- **Page Object**: Java class representing web page with @FindBy XPath annotations for element location
- **Screen Object**: Java class representing mobile screen with dual annotations (@iOSXCUITFindBy, @AndroidFindBy) supporting both iOS and Android
- **Step Definition**: Java class implementing Cucumber steps (Given/When/Then) that interact with Page Objects or Screen Objects
- **Cucumber Report**: HTML report generated per platform showing test results, pass/fail status, and execution details

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Java/Cucumber test infrastructure is established and functional, enabling developers to write new E2E tests without TypeScript dependency
- **SC-002**: Developers on feature branches can successfully migrate their existing tests from TypeScript to Java with equivalent coverage
- **SC-003**: Single Maven command executes all tests for a given platform (web, android, or ios) and generates HTML report within 5 seconds of test completion
- **SC-004**: Both TypeScript and Java test stacks can coexist and execute independently without conflicts
- **SC-005**: Test execution time for Java tests remains within 10% of equivalent TypeScript-based test execution time
- **SC-006**: CI/CD pipeline successfully runs Java/Maven E2E tests on all platforms with zero configuration errors
- **SC-007**: Test failure rates for migrated Java tests remain consistent (within 5% variance) compared to equivalent TypeScript tests, indicating migration accuracy

## Clarifications

### Session 2025-11-25

- Q: Should testing framework versions (Selenium, Appium, Cucumber) be pinned to specific versions or use latest stable? → A: Use latest stable LTS/major versions at migration time (Selenium 4.x latest, Appium 9.x latest, Cucumber 7.x latest)
- Q: What migration strategy should be used for transitioning from TypeScript to Java test stack? → A: Gradual migration with dual test stack coexistence - TypeScript tests remain until all feature branches migrate to Java tests
- Q: Which browsers must be supported for Selenium web E2E tests? → A: Chrome only (latest stable)
- Q: Which Android/iOS OS versions and device types must be supported for Appium tests? → A: Single version - Android 14 / iOS 17 on emulators/simulators only
- Q: What debugging artifacts should be captured when tests fail? → A: Screenshots on failure + execution logs (console, Selenium/Appium logs)

## Dependencies

- Maven 3.6+ installed on development machines and CI/CD environment
- Java JDK 21 installed on development machines and CI/CD environment
- Selenium WebDriver 4.x (latest stable) with ChromeDriver for Chrome browser (latest stable)
- Google Chrome browser (latest stable) installed on development machines and CI/CD environment
- Appium 9.x (latest stable) server configured for Android and iOS test execution
- Android Emulator with Android 14 (API level 34) configured for Android testing
- iOS Simulator with iOS 17 (Xcode 15+) configured for iOS testing
- Cucumber-Java 7.x (latest stable) for BDD test execution
- Existing test identifiers (testTag, accessibilityIdentifier, data-testid) remain in platform code
- TypeScript E2E test infrastructure (Playwright/WebdriverIO) remains functional during migration period

## Assumptions

- Developers and QA engineers have basic Java knowledge or can learn Java syntax for test writing
- Migration will be gradual with dual test stack coexistence - TypeScript and Java tests coexist until all feature branches migrate
- Developers working on feature branches will migrate their tests from TypeScript to Java incrementally
- Test data and fixtures used by TypeScript tests can be reused or easily adapted for Java tests
- XPath locators will provide sufficient reliability for web element location (compared to Playwright's auto-wait mechanisms)
- Screen Object dual annotations will correctly handle platform differences between iOS and Android
- Maven Cucumber plugins support generating separate HTML reports per tag/platform
- Both test stacks (TypeScript and Java) can coexist in the repository without conflicts
- TypeScript test infrastructure will be removed only when all active feature branches have migrated to Java tests

## Out of Scope

- Migrating unit tests or integration tests (only E2E tests are affected)
- Changing test identifiers in platform code (testTag, accessibilityIdentifier, data-testid must remain stable)
- Performance optimization of test execution speed (migration focuses on feature parity)
- Visual regression testing or screenshot comparison features
- Parallel test execution across multiple devices/browsers (can be added later if needed)
- Automated migration scripts/tools to convert TypeScript tests to Java (manual migration by developers on feature branches)
- Multi-browser support (Firefox, Edge, Safari) - initial migration targets Chrome only
- Multiple Android/iOS OS versions - testing limited to Android 14 and iOS 17
- Real device testing - using emulators/simulators only for initial migration
- Forced migration timeline - TypeScript tests removal happens organically as feature branches migrate

