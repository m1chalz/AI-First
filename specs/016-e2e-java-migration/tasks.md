# Tasks: E2E Testing Stack Migration to Java/Maven/Selenium/Cucumber

**Input**: Design documents from `/specs/016-e2e-java-migration/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/, quickstart.md

**Tests**: This feature IS the testing infrastructure itself. Unit/integration tests for test code are NOT required.

**IMPORTANT STRATEGY NOTE**: This is infrastructure enablement, NOT mass test migration. The goal is to establish Java/Maven/Selenium/Appium/Cucumber infrastructure alongside existing TypeScript tests, enabling gradual, organic migration as developers work on feature branches. TypeScript tests remain functional until all active branches migrate.

**Organization**: Tasks are grouped by user story (US1: Web E2E, US2: Mobile E2E, US3: Maven Config) to enable independent implementation and testing of each capability.

**Total Tasks**: 101 (T001-T100 + T010.1)

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and Maven project structure establishment

**Note**: T010.1 validates TypeScript test preservation per FR-014 (dual test stack coexistence)

- [x] T001 Create Java E2E test project directory at `/e2e-tests/` (or move existing TypeScript to `/e2e-tests/typescript/` and create `/e2e-tests/java/` for coexistence)
- [x] T002 Create Maven project structure: `/e2e-tests/pom.xml`, `/e2e-tests/src/test/java/`, `/e2e-tests/src/test/resources/`
- [x] T003 [P] Create package structure in `/e2e-tests/src/test/java/com/intive/aifirst/petspot/e2e/` with subdirectories: `pages/`, `screens/`, `steps/`, `utils/`, `runners/`
- [x] T004 [P] Create resources structure in `/e2e-tests/src/test/resources/` with subdirectories: `features/web/`, `features/mobile/`
- [x] T005 [P] Add base Maven dependencies to `/e2e-tests/pom.xml`: Selenium 4.x, Appium 9.x, Cucumber-Java 7.x, JUnit 5, WebDriverManager
- [x] T006 [P] Configure Maven compiler plugin in `/e2e-tests/pom.xml` for Java 21 (source/target compatibility)
- [x] T007 [P] Configure Maven Surefire plugin in `/e2e-tests/pom.xml` for test execution
- [x] T008 [P] Add Cucumber plugin configuration to `/e2e-tests/pom.xml` for HTML report generation (separate reports for @web, @android, @ios)
- [x] T009 [P] Create `.gitignore` in `/e2e-tests/` to ignore Maven target directory and IDE files
- [x] T010 Verify Maven project builds successfully with `mvn clean install` (no tests yet)
- [x] T010.1 [P] Verify existing TypeScript E2E test infrastructure remains functional and document preservation strategy:
  - Locate existing TypeScript web tests (Playwright) and mobile tests (Appium+WebdriverIO) if they exist
  - Run existing TypeScript test commands to establish baseline functionality (e.g., `npm test` or equivalent)
  - Document current test execution commands for CI/CD reference in `/e2e-tests/README.md`
  - Verify no directory conflicts between planned Java structure (`/e2e-tests/` or `/e2e-tests/java/`) and existing TypeScript structure
  - Document directory coexistence strategy (e.g., `/e2e-tests/java/` + `/e2e-tests/typescript/` OR separate root directories)
  - Create "TypeScript Test Preservation Checklist" in `/e2e-tests/README.md` documenting which npm commands must continue to work
  - If no existing TypeScript tests exist, document this finding in README.md for clarity

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core test infrastructure utilities that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work (web/mobile test execution) can begin until this phase is complete

- [x] T011 [P] Create `WebDriverManager` utility in `/e2e-tests/src/test/java/com/intive/aifirst/petspot/e2e/utils/WebDriverManager.java` with ThreadLocal WebDriver management
- [x] T012 [P] Create `AppiumDriverManager` utility in `/e2e-tests/src/test/java/com/intive/aifirst/petspot/e2e/utils/AppiumDriverManager.java` with ThreadLocal AppiumDriver management and platform detection
- [x] T013 [P] Create `ScreenshotUtil` in `/e2e-tests/src/test/java/com/intive/aifirst/petspot/e2e/utils/ScreenshotUtil.java` for capturing screenshots on test failure
- [x] T014 [P] Create `TestConfig` in `/e2e-tests/src/test/java/com/intive/aifirst/petspot/e2e/utils/TestConfig.java` for loading configuration from properties (base URLs, timeouts, capabilities)
- [x] T015 [P] Create Cucumber hooks in `/e2e-tests/src/test/java/com/intive/aifirst/petspot/e2e/utils/Hooks.java` with @Before and @After for driver lifecycle and screenshot capture on failure
- [x] T016 [P] Create `cucumber.properties` in `/e2e-tests/src/test/resources/cucumber.properties` with Cucumber configuration (glue paths, plugin settings)
- [x] T017 [P] Create sample Page Object template from contracts in `/e2e-tests/src/test/java/com/intive/aifirst/petspot/e2e/pages/` (demonstrates @FindBy pattern with XPath)
- [x] T018 [P] Create sample Screen Object template from contracts in `/e2e-tests/src/test/java/com/intive/aifirst/petspot/e2e/screens/` (demonstrates dual annotations @AndroidFindBy + @iOSXCUITFindBy)
- [x] T019 Create README.md in `/e2e-tests/` documenting project structure, setup instructions, and execution commands (Maven commands for each platform)
- [x] T020 Add JavaDoc documentation to all utility classes (WebDriverManager, AppiumDriverManager, ScreenshotUtil, TestConfig) with examples

**Checkpoint**: Foundation ready - user story implementation (web/mobile test execution) can now begin in parallel

---

## Phase 3: User Story 1 - Web E2E Test Execution with Selenium (Priority: P1) 🎯 MVP

**Goal**: Enable QA engineers and developers to write and execute web E2E tests using Selenium WebDriver with Java and Cucumber

**Independent Test**: Create simple web E2E test (e.g., pet list view), write it in Gherkin with @web tag, implement step definitions and Page Object, run `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web"`, verify execution and HTML report generation at `/e2e-tests/target/cucumber-reports/web/index.html`

**Why P1**: Web E2E testing is the foundation of the new stack. Without this working, mobile tests cannot be validated using the same patterns and tooling.

### Implementation for User Story 1

**Feature File**:
- [x] T021 [US1] Create sample web feature file at `/e2e-tests/src/test/resources/features/web/pet-list.feature` with @web tag (demonstrates pet list viewing and search scenarios from contracts/sample-web.feature)
- [x] T022 [P] [US1] Add JSDoc-style comments to feature file explaining Gherkin syntax and @web tag usage

**Page Object Model**:
- [x] T023 [P] [US1] Create `PetListPage` Page Object in `/e2e-tests/src/test/java/com/intive/aifirst/petspot/e2e/pages/PetListPage.java` with @FindBy XPath annotations for web elements (addButton, petList, searchInput)
- [x] T024 [P] [US1] Add JavaDoc documentation to `PetListPage` explaining Page Object Model pattern and XPath locator strategy with data-testid attributes

**Step Definitions**:
- [x] T025 [US1] Create web step definitions in `/e2e-tests/src/test/java/com/intive/aifirst/petspot/e2e/steps/web/PetListWebSteps.java` implementing Given/When/Then steps from pet-list.feature
- [x] T026 [US1] Create common web step definitions in `/e2e-tests/src/test/java/com/intive/aifirst/petspot/e2e/steps/web/CommonWebSteps.java` for reusable steps (navigation, wait for page load)
- [x] T027 [P] [US1] Add JavaDoc documentation to step definition classes explaining Cucumber step pattern matching and Page Object usage

**Test Runner**:
- [x] T028 [US1] Create `WebTestRunner` in `/e2e-tests/src/test/java/com/intive/aifirst/petspot/e2e/runners/WebTestRunner.java` with @RunWith(Cucumber.class) and @CucumberOptions for @web tag filtering
- [x] T029 [P] [US1] Add JavaDoc documentation to `WebTestRunner` explaining Cucumber runner configuration and tag filtering

**Validation**:
- [x] T030 [US1] Run web tests with `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web"` and verify tests execute successfully
- [x] T031 [US1] Verify Cucumber HTML report is generated at `/e2e-tests/target/cucumber-reports/web/index.html` with correct test results
- [x] T032 [US1] Verify screenshots are captured on test failure in `/e2e-tests/target/screenshots/` (intentionally fail a test to validate)
- [x] T033 [US1] Verify execution logs are written to `/e2e-tests/target/logs/` with Selenium WebDriver logs

**Documentation**:
- [x] T034 [P] [US1] Update `/e2e-tests/README.md` with web test execution instructions and example commands
- [x] T035 [P] [US1] Update `/specs/016-e2e-java-migration/quickstart.md` with complete web test example walkthrough (from writing feature file to viewing report)

**Checkpoint**: At this point, web E2E test execution with Selenium should be fully functional and independently testable

---

## Phase 4: User Story 2 - Mobile E2E Test Execution with Appium (Priority: P2)

**Goal**: Enable QA engineers to write and execute mobile E2E tests for Android and iOS using Appium with Java and Cucumber

**Independent Test**: Create mobile E2E test (e.g., pet list view), write it in Gherkin with @android and @ios tags, implement screen objects with dual annotations, run platform-specific Maven commands, verify execution on both Android and iOS

**Why P2**: Mobile testing depends on unified Maven/Cucumber infrastructure established in P1. It builds on the same patterns but adds platform-specific complexity (dual annotations).

**Assumptions**: Android Emulator (Android 14) and iOS Simulator (iOS 17) are configured, Appium server is running on http://127.0.0.1:4723

### Implementation for User Story 2

**Feature Files**:
- [x] T036 [US2] Create sample mobile feature file at `/e2e-tests/src/test/resources/features/mobile/pet-list.feature` with @android and @ios tags (demonstrates pet list viewing and search scenarios)
- [x] T037 [P] [US2] Add JSDoc-style comments to feature file explaining mobile-specific tags (@android, @ios) and dual-platform execution

**Screen Object Model**:
- [x] T038 [P] [US2] Create `PetListScreen` Screen Object in `/e2e-tests/src/test/java/com/intive/aifirst/petspot/e2e/screens/PetListScreen.java` with dual annotations (@AndroidFindBy with UiAutomator, @iOSXCUITFindBy with accessibility identifiers)
- [x] T039 [P] [US2] Add JavaDoc documentation to `PetListScreen` explaining Screen Object Model pattern, dual annotation strategy, and platform-specific locators (testTag for Android, accessibilityIdentifier for iOS)

**Step Definitions**:
- [x] T040 [US2] Create mobile step definitions in `/e2e-tests/src/test/java/com/intive/aifirst/petspot/e2e/steps/mobile/PetListMobileSteps.java` implementing Given/When/Then steps from pet-list.feature
- [x] T041 [US2] Create common mobile step definitions in `/e2e-tests/src/test/java/com/intive/aifirst/petspot/e2e/steps/mobile/CommonMobileSteps.java` for reusable steps (app launch, wait for screen load, platform detection)
- [x] T042 [P] [US2] Add JavaDoc documentation to mobile step definition classes explaining Cucumber step pattern matching, Screen Object usage, and platform-agnostic implementation

**Test Runners**:
- [x] T043 [US2] Create `AndroidTestRunner` in `/e2e-tests/src/test/java/com/intive/aifirst/petspot/e2e/runners/AndroidTestRunner.java` with @CucumberOptions for @android tag filtering
- [x] T044 [US2] Create `IosTestRunner` in `/e2e-tests/src/test/java/com/intive/aifirst/petspot/e2e/runners/IosTestRunner.java` with @CucumberOptions for @ios tag filtering
- [x] T045 [P] [US2] Add JavaDoc documentation to mobile test runners explaining tag filtering and platform-specific configuration

**Appium Configuration**:
- [x] T046 [US2] Update `AppiumDriverManager` to load platform-specific capabilities from TestConfig (device name, platform version, app path, automation name)
- [x] T047 [US2] Add Android-specific capabilities in TestConfig: platformName=Android, platformVersion=14, deviceName=Android Emulator, automationName=UiAutomator2, app path
- [x] T048 [US2] Add iOS-specific capabilities in TestConfig: platformName=iOS, platformVersion=17.0, deviceName=iPhone 15, automationName=XCUITest, app path
- [x] T049 [P] [US2] Add JavaDoc documentation to TestConfig explaining Appium capabilities and platform-specific configuration options

**Validation**:
- [x] T050 [US2] Run Android tests with `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@android"` and verify tests execute on Android Emulator
- [x] T051 [US2] Verify Android Cucumber HTML report is generated at `/e2e-tests/target/cucumber-reports/android/index.html`
- [x] T052 [US2] Run iOS tests with `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@ios"` and verify tests execute on iOS Simulator
- [x] T053 [US2] Verify iOS Cucumber HTML report is generated at `/e2e-tests/target/cucumber-reports/ios/index.html`
- [x] T054 [US2] Verify screenshots are captured on mobile test failure in `/e2e-tests/target/screenshots/` (platform-specific filenames)
- [x] T055 [US2] Verify Appium execution logs are written to `/e2e-tests/target/logs/` with device logs

**Documentation**:
- [x] T056 [P] [US2] Update `/e2e-tests/README.md` with mobile test execution instructions (prerequisites: Appium server, emulator/simulator setup)
- [x] T057 [P] [US2] Update `/specs/016-e2e-java-migration/quickstart.md` with complete mobile test example walkthrough (dual-platform feature file, Screen Object with dual annotations, execution commands)

**Checkpoint**: At this point, mobile E2E test execution with Appium should be fully functional for both Android and iOS independently

---

## Phase 5: User Story 3 - Unified Maven Project Configuration (Priority: P3)

**Goal**: Ensure Maven pom.xml manages all dependencies correctly, plugins generate separate HTML reports per platform, and shared utilities are reusable across web and mobile tests

**Independent Test**: Verify pom.xml contains correct dependencies (Selenium, Appium, Cucumber), run `mvn clean install` successfully, confirm Maven plugins generate separate HTML reports for web/android/ios in expected directories, verify shared utilities can be imported by both web and mobile tests

**Why P3**: Project configuration is essential infrastructure but can be validated incrementally as tests are developed. It's lower priority than having working test execution patterns (US1, US2).

### Implementation for User Story 3

**Maven Dependencies**:
- [x] T058 [P] [US3] Verify all required dependencies in `/e2e-tests/pom.xml`: Selenium 4.15.0, Appium 9.0.0, Cucumber-Java 7.14.0, JUnit 5.10.1, WebDriverManager 5.6.2
- [x] T059 [P] [US3] Add dependency comments in pom.xml explaining purpose of each dependency (Selenium for web, Appium for mobile, Cucumber for BDD, JUnit for test runner, WebDriverManager for automatic driver setup)
- [x] T060 [P] [US3] Add properties section in pom.xml with version variables for major dependencies (easier updates: selenium.version, appium.version, cucumber.version)

**Maven Plugins**:
- [x] T061 [US3] Configure Maven Cucumber plugin in pom.xml with separate executions for web/android/ios reports (plugin id: net.masterthought:maven-cucumber-reporting)
- [x] T062 [US3] Configure web report execution in pom.xml: outputDirectory=${project.build.directory}/cucumber-reports/web, jsonFiles=**/cucumber-web.json
- [x] T063 [US3] Configure android report execution in pom.xml: outputDirectory=${project.build.directory}/cucumber-reports/android, jsonFiles=**/cucumber-android.json
- [x] T064 [US3] Configure ios report execution in pom.xml: outputDirectory=${project.build.directory}/cucumber-reports/ios, jsonFiles=**/cucumber-ios.json
- [x] T065 [P] [US3] Add plugin comments in pom.xml explaining Maven Cucumber reporting plugin configuration and tag-based report generation

**Shared Test Utilities**:
- [x] T066 [US3] Verify `TestConfig` utility in `/e2e-tests/src/test/java/com/intive/aifirst/petspot/e2e/utils/TestConfig.java` can be imported by both web and mobile step definitions
- [x] T067 [US3] Verify `ScreenshotUtil` utility in `/e2e-tests/src/test/java/com/intive/aifirst/petspot/e2e/utils/ScreenshotUtil.java` works for both WebDriver (web) and AppiumDriver (mobile) screenshots
- [x] T068 [US3] Create `WaitUtil` in `/e2e-tests/src/test/java/com/intive/aifirst/petspot/e2e/utils/WaitUtil.java` with reusable explicit wait methods for both WebDriver and AppiumDriver (wait for element visible, clickable, text to appear)
- [x] T069 [P] [US3] Add JavaDoc documentation to `WaitUtil` explaining explicit wait strategies and timeout configuration

**Validation**:
- [x] T070 [US3] Run `mvn -f e2e-tests/pom.xml clean install` and verify project builds successfully with all dependencies resolved
- [x] T071 [US3] Run all tests with `mvn -f e2e-tests/pom.xml test` (no tag filter) and verify execution
- [x] T072 [US3] Verify separate HTML reports are generated at `/e2e-tests/target/cucumber-reports/web/index.html`, `/e2e-tests/target/cucumber-reports/android/index.html`, `/e2e-tests/target/cucumber-reports/ios/index.html`
- [x] T073 [US3] Verify shared utilities (TestConfig, ScreenshotUtil, WaitUtil) are successfully imported and used by both web and mobile step definitions
- [x] T074 [US3] Run `mvn -f e2e-tests/pom.xml dependency:tree` and document dependency tree for troubleshooting version conflicts

**Documentation**:
- [x] T075 [P] [US3] Update `/e2e-tests/README.md` with complete Maven project configuration section (dependencies, plugins, execution commands, report locations)
- [x] T076 [P] [US3] Add troubleshooting section to README.md with common Maven issues (dependency conflicts, plugin execution failures, report generation errors)
- [x] T077 [P] [US3] Update `/specs/016-e2e-java-migration/quickstart.md` with Maven project setup and dependency management instructions

**Checkpoint**: All user stories (web, mobile, Maven config) should now be independently functional and validated

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: CI/CD integration, developer documentation, migration guide, and dual-stack coexistence enablement

**CRITICAL - Dual Test Stack Coexistence**:
- [x] T078 Update CI/CD pipeline configuration (e.g., GitHub Actions, Jenkins) to support both TypeScript and Java E2E tests
- [x] T079 [P] Add CI/CD job for TypeScript web tests: `cd e2e-tests/typescript/web && npm install && npm run test:web` (or keep existing npm commands if not renamed)
- [x] T080 [P] Add CI/CD job for TypeScript mobile tests: `cd e2e-tests/typescript/mobile && npm install && npm run test:mobile` (or keep existing npm commands if not renamed)
- [x] T081 [P] Add CI/CD job for Java web tests: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web"`
- [x] T082 [P] Add CI/CD job for Java Android tests: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@android"`
- [x] T083 [P] Add CI/CD job for Java iOS tests: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@ios"`
- [x] T084 Configure CI/CD to run both test stacks in parallel (TypeScript and Java) during transition period to ensure test parity
- [x] T085 [P] Configure CI/CD artifact upload for both TypeScript reports (Playwright/Appium HTML reports) and Java reports (Cucumber HTML reports)

**Developer Documentation & Migration Guide**:
- [x] T086 [P] Create comprehensive migration guide in `/specs/016-e2e-java-migration/MIGRATION_GUIDE.md` explaining:
  - When to use Java tests vs TypeScript tests (decision framework from quickstart.md)
  - How to migrate existing TypeScript test to Java/Cucumber equivalent
  - Step-by-step example: TypeScript Playwright test → Java Selenium + Cucumber test
  - Step-by-step example: TypeScript Appium test → Java Appium + Cucumber test with dual annotations
  - Common pitfalls and troubleshooting
- [x] T087 [P] Create visual comparison document in `/specs/016-e2e-java-migration/COMPARISON.md` showing side-by-side examples:
  - TypeScript Playwright Page Object vs Java Selenium Page Object (@FindBy pattern)
  - TypeScript Appium Screen Object vs Java Appium Screen Object (dual annotations)
  - TypeScript test scenario vs Gherkin feature file
  - Test execution commands (npm vs mvn)
  - Report formats (Playwright HTML vs Cucumber HTML)
- [x] T088 [P] Update `/specs/016-e2e-java-migration/quickstart.md` with section on dual-stack coexistence explaining:
  - Directory structure options (both test stacks side-by-side)
  - How to run both test stacks locally
  - CI/CD behavior during transition period
  - No forced migration timeline - gradual, organic adoption

**Code Quality & Cleanup**:
- [x] T089 [P] Run ESLint/Checkstyle (if configured) on Java test code and fix violations
- [x] T090 [P] Review all JavaDoc documentation for completeness and accuracy
- [x] T091 [P] Run all Java tests (`mvn test`) and verify 100% pass rate
- [x] T092 [P] Verify all Maven plugins execute successfully without warnings or errors
- [x] T093 Cleanup any temporary files or test artifacts in `/e2e-tests/target/` directory

**Feature Validation & Demo**:
- [x] T094 Run complete validation: execute all web tests, all Android tests, all iOS tests, verify all HTML reports generated successfully
- [x] T095 Validate dual-stack coexistence: run TypeScript tests and Java tests back-to-back without conflicts
- [x] T096 Create demo recording showing:
  - Writing a new Gherkin feature file
  - Implementing Page/Screen Object with annotations
  - Implementing step definitions
  - Running tests with Maven command
  - Viewing Cucumber HTML report
- [x] T097 [P] Update main project README.md (repository root) with section on E2E testing, linking to Java and TypeScript test documentation
- [x] T098 Present quickstart.md demo to team showing developer workflow for new Java tests

**Final Documentation Updates**:
- [x] T099 [P] Update `/specs/016-e2e-java-migration/spec.md` with "IMPLEMENTATION_COMPLETE.md" reference documenting final implementation details and any deviations from original spec
- [x] T100 [P] Create `/specs/016-e2e-java-migration/IMPLEMENTATION_COMPLETE.md` summarizing:
  - Implementation timeline and milestones
  - Final directory structure (show actual paths for both test stacks)
  - Test coverage achieved (number of demo tests implemented)
  - Known limitations or future improvements
  - Links to key documentation (README, quickstart, migration guide)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories (US1, US2, US3)
- **User Stories (Phase 3-5)**: All depend on Foundational phase completion
  - **US1 (Web E2E)**: Can start after Foundational - No dependencies on US2/US3
  - **US2 (Mobile E2E)**: Can start after Foundational - US1 patterns provide reference but NOT blocking dependency
  - **US3 (Maven Config)**: Can start after Foundational - Incremental validation alongside US1/US2 development
- **Polish (Phase 6)**: Depends on all user stories (US1, US2, US3) being complete

### User Story Dependencies

- **User Story 1 (P1 - Web E2E)**: Can start after Foundational (Phase 2) - Independently testable with `mvn test -Dcucumber.filter.tags="@web"`
- **User Story 2 (P2 - Mobile E2E)**: Can start after Foundational (Phase 2) - Independently testable with platform-specific commands (`mvn test -Dcucumber.filter.tags="@android|@ios"`)
- **User Story 3 (P3 - Maven Config)**: Can start after Foundational (Phase 2) - Validates configuration as US1/US2 are developed

### Within Each User Story

- **US1**: Feature file → Page Objects → Step Definitions → Test Runner → Validation → Documentation
- **US2**: Feature files → Screen Objects (with dual annotations) → Step Definitions → Test Runners → Appium config → Validation → Documentation
- **US3**: Maven dependencies → Maven plugins → Shared utilities → Validation → Documentation

### Parallel Opportunities

- **All Setup tasks (Phase 1) marked [P]** can run in parallel (different files)
- **All Foundational tasks (Phase 2) marked [P]** can run in parallel (utilities are independent)
- **Once Foundational completes**:
  - US1 (Web), US2 (Mobile), US3 (Maven Config) can all be worked on in parallel by different team members
  - Web Page Objects ([P] tasks in US1) and Mobile Screen Objects ([P] tasks in US2) can be developed simultaneously
- **Phase 6 (Polish) tasks marked [P]** can run in parallel (documentation, CI/CD jobs, cleanup are independent)

---

## Parallel Example: After Foundational Phase Completes

```bash
# Three team members can work in parallel on different user stories:

Team Member A (US1 - Web E2E):
Task: "Create PetListPage Page Object with @FindBy XPath annotations"
Task: "Create web step definitions implementing pet-list.feature scenarios"
Task: "Create WebTestRunner with @web tag filtering"

Team Member B (US2 - Mobile E2E):
Task: "Create PetListScreen Screen Object with dual annotations"
Task: "Create mobile step definitions implementing pet-list.feature scenarios"
Task: "Create AndroidTestRunner and IosTestRunner with platform tags"

Team Member C (US3 - Maven Config):
Task: "Configure Maven Cucumber plugin with separate report executions"
Task: "Create WaitUtil with reusable wait methods for WebDriver and AppiumDriver"
Task: "Verify shared utilities work across web and mobile tests"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (Maven project structure) → **Milestone: Maven builds successfully**
2. Complete Phase 2: Foundational (driver managers, utilities) → **Milestone: Foundation ready**
3. Complete Phase 3: User Story 1 (Web E2E) → **Milestone: Web tests execute with Selenium**
4. **STOP and VALIDATE**: Run `mvn test -Dcucumber.filter.tags="@web"` and verify HTML report
5. Demo web test execution capability to stakeholders

### Incremental Delivery

1. Complete Setup + Foundational → **Foundation ready**
2. Add User Story 1 (Web E2E) → Test independently with Maven command → **Demo web capability**
3. Add User Story 2 (Mobile E2E) → Test independently with platform commands → **Demo mobile capability**
4. Add User Story 3 (Maven Config) → Validate configuration → **Demo unified Maven project**
5. Complete Phase 6 (Polish) → CI/CD integration + documentation → **Infrastructure complete and production-ready**

### Parallel Team Strategy

With multiple developers (recommended for faster delivery):

1. Team completes Setup + Foundational together → **Foundation ready checkpoint**
2. Once Foundational is done:
   - Developer A: User Story 1 (Web E2E) - Selenium + Page Objects + Web steps
   - Developer B: User Story 2 (Mobile E2E) - Appium + Screen Objects + Mobile steps
   - Developer C: User Story 3 (Maven Config) - Plugin configuration + shared utilities
3. Stories complete and integrate independently
4. Team collaborates on Phase 6 (Polish) - CI/CD, documentation, migration guide

### Sequential Strategy (Single Developer)

1. Phase 1: Setup → Phase 2: Foundational
2. Phase 3: User Story 1 (Web E2E) → Validate → Demo
3. Phase 4: User Story 2 (Mobile E2E) → Validate → Demo
4. Phase 5: User Story 3 (Maven Config) → Validate → Demo
5. Phase 6: Polish → CI/CD + Documentation → Production-ready

---

## Notes

- **Total tasks**: 101 (T001-T100 + T010.1 for TypeScript validation)
- **[P] tasks** = different files, no dependencies, safe to parallelize (48 parallelizable tasks)
- **[Story] label** maps task to specific user story for traceability (US1, US2, US3)
- **Tests are NOT mandatory** - this feature IS the test infrastructure itself (no unit tests for test code)
- **Dual-stack coexistence** is critical - TypeScript tests must remain functional (T010.1 validation + Phase 6 CI/CD tasks)
- **No mass migration** - implementation focuses on infrastructure enablement and demo tests, not migrating all existing tests
- Each user story should be independently completable and testable via Maven commands
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently before proceeding
- Avoid: vague tasks, same file conflicts, assuming TypeScript test removal

**Critical Success Factor**: After Phase 2 (Foundational), developers can write new E2E tests in Java without dependency on TypeScript. Both test stacks coexist and can be executed independently.

