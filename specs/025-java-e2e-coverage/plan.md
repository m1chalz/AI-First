# Implementation Plan: Complete Java E2E Test Coverage

**Branch**: `025-java-e2e-coverage` | **Date**: 2025-11-26 | **Spec**: [spec.md](./spec.md)  
**Input**: Feature specification from `/specs/025-java-e2e-coverage/spec.md`

## Summary

This feature completes the Java/Maven/Cucumber E2E test infrastructure (initiated in Spec 016) by implementing missing test scenarios across web and mobile platforms. Current coverage gaps include: web Animal List (20% → target 90-100%), mobile Pet Details (0% → target 35-40%), and mobile Animal List button behaviors (60% → target 90%). Additionally, 4 invalid search scenarios testing non-existent functionality will be removed from the mobile test suite. The implementation focuses solely on test code (Gherkin scenarios, Page/Screen Objects, step definitions) without modifying application code.

## Technical Context

**Language/Version**: Java 21  
**Primary Dependencies**: Maven 3.9+, Selenium WebDriver 4.15+, Appium Java Client 9.0.0+, Cucumber 7.14.0, JUnit 5.10.1  
**Storage**: N/A (test infrastructure)  
**Testing**: Self (test infrastructure project)  
**Target Platform**: Cross-platform test execution (Web browsers via Selenium, iOS/Android simulators via Appium)  
**Project Type**: Test infrastructure (single Maven project at `/e2e-tests/java/`)  
**Performance Goals**: Full test suite execution under 5 minutes per platform  
**Constraints**: Tests must run in local development environment without external dependencies (mock data)  
**Scale/Scope**: ~23 new test scenarios (8 web, 12 mobile Pet Details, 3 mobile button), 4 test modifications (remove search), 3 new Java classes (PetDetailsScreen, PetDetailsMobileSteps, Page Object extensions)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This feature affects **only E2E test infrastructure** (`/e2e-tests/java/`) and does NOT modify platform application code (Android, iOS, Web, Backend). Therefore, platform architecture checks (Android MVI, iOS MVVM-C, Platform Independence) are marked N/A. Focus is on E2E Testing compliance.

### Platform Architecture Compliance

- [N/A] **Platform Independence**: Not applicable - no platform application code changes
  - Violation justification: Test infrastructure only

- [N/A] **Android MVI Architecture**: Not applicable - no Android application code changes
  - Violation justification: Test infrastructure only

- [N/A] **iOS MVVM-C Architecture**: Not applicable - no iOS application code changes
  - Violation justification: Test infrastructure only

### Testing & Quality Standards

- [x] **Test Identifiers for UI Controls**: Uses existing accessibility IDs from Spec 005 and Spec 012
  - Web: XPath locators + data-testid attributes
  - Mobile: Accessibility identifiers (e.g., `petDetails.view`, `animalList.item.${id}`)
  - All test identifiers already implemented in applications per specs

- [x] **E2E Testing (Principle XII)**: Implements Cucumber/Gherkin scenarios per constitution requirements
  - Feature files in `/e2e-tests/java/src/test/resources/features/{web,mobile}/`
  - Page Object Model for web (@FindBy XPath annotations)
  - Screen Object Model for mobile (dual @iOSXCUITFindBy/@AndroidFindBy annotations)
  - Maven execution: `mvn test -Dcucumber.filter.tags="@web|@android|@ios"`
  - Separate HTML reports per platform: `/e2e-tests/target/cucumber-reports/{web,android,ios}/index.html`

- [x] **Test Coverage Requirements**: Achieves constitution-mandated coverage targets
  - Unit test coverage: N/A (no application code changes)
  - E2E test coverage: Increases from current gaps (20% web, 0% mobile Pet Details) to 90%+ per spec
  - All test scenarios follow Given-When-Then structure

- [N/A] **Backend Clean Code & TDD**: Not applicable - no backend code changes
  - Violation justification: Test infrastructure only

### Dependency Injection & Architecture

- [N/A] **Dependency Injection**: Not applicable - test infrastructure does not use DI
  - Violation justification: Test infrastructure uses Page/Screen Object pattern

### Documentation & Code Quality

- [x] **Documentation Requirements**: Test scenarios serve as executable documentation
  - Gherkin feature files provide human-readable test documentation
  - Step definitions include clear assertion messages per FR-018
  - README updates not required (test execution documented in constitution)

- [x] **Test Conventions**: Follows Given-When-Then (Arrange-Act-Assert) structure per FR-016
  - All new scenarios use Gherkin Given-When-Then format
  - Cucumber step definitions implement AAA pattern

### Constitution Compliance Summary

**Status**: ✅ COMPLIANT

**Rationale**: This feature implements test infrastructure only, with no platform application code changes. All applicable constitution requirements are met:
- E2E testing follows Principle XII (Cucumber/Gherkin, Page/Screen Objects, Maven execution)
- Uses existing test identifiers from Spec 005/012 (Principle VI)
- Achieves constitution-mandated test coverage targets
- Follows Given-When-Then conventions

**Re-evaluation after Phase 1**: N/A (no design changes required beyond test scenarios)

## Phase 0: Research

### Research Findings

**Decision 1: Test Scenario Prioritization Strategy**

**Decision**: Implement MVP coverage (10-12 scenarios) for Pet Details rather than full 30 scenarios from Spec 012

**Rationale**:
- Core flows (navigation, loading, error, contact, badges, remove button) provide 35-40% coverage
- Allows faster delivery of critical test coverage gap (currently 0%)
- Full coverage can be added incrementally in future iterations
- Aligns with spec clarification session (2025-11-26)

**Alternatives Considered**:
- Full 30-scenario implementation: Rejected due to time constraints and diminishing returns
- Minimal 5-scenario coverage: Rejected as insufficient for production readiness

---

**Decision 2: Invalid Search Test Handling**

**Decision**: Comment out (not delete) 4 search scenarios with TODO markers

**Rationale**:
- Preserves scenarios for when search functionality is implemented
- Clear TODO markers explain why tests are disabled
- Prevents confusion from failing tests for non-existent features
- Aligns with spec clarification session (2025-11-26)

**Alternatives Considered**:
- Complete deletion: Rejected as scenarios will be needed when search is implemented
- Keep failing tests: Rejected as creates noise in test reports

---

**Decision 3: Page Object Model Extensions**

**Decision**: Extend existing `PetListPage.java` with 8 new methods rather than creating new class

**Rationale**:
- Maintains single responsibility (one Page Object per page)
- Existing class already has basic structure from Spec 016
- New methods support missing scenarios (scroll, card interaction, button checks)
- Follows existing Page Object pattern

**Alternatives Considered**:
- Separate utility class: Rejected as breaks Page Object cohesion
- Inline methods in step definitions: Rejected as violates Page Object pattern

---

**Decision 4: Screen Object Dual Annotations**

**Decision**: Use Appium dual annotations (@iOSXCUITFindBy, @AndroidFindBy) for mobile Pet Details

**Rationale**:
- Enables future Android support without code duplication
- Follows pattern established in Spec 016
- iOS-first implementation (Android annotations as placeholders)
- Constitution Principle XII mandates dual annotation approach

**Alternatives Considered**:
- iOS-only annotations: Rejected as requires future refactoring for Android
- Separate iOS/Android classes: Rejected as creates duplication

---

**Decision 5: Test Execution Strategy**

**Decision**: Execute tests via Maven with Cucumber tag filtering

**Rationale**:
- Constitution Principle XII mandates: `mvn test -Dcucumber.filter.tags="@platform"`
- Separate HTML reports per platform per constitution
- Tags enable selective execution (@smoke, @web, @android, @ios)
- Integrates with existing CI/CD from Spec 016

**Alternatives Considered**:
- JUnit runner: Rejected as doesn't provide Cucumber HTML reports
- Gradle: Rejected as constitution standardizes on Maven

---

**Decision 6: Accessibility Identifier Usage**

**Decision**: Use existing accessibility IDs from application code (no new IDs)

**Rationale**:
- Spec 005 and Spec 012 already define all required test identifiers
- Constitution Principle VI mandates format: `{screen}.{element}.{action}`
- Applications already implement these IDs (validated in earlier specs)
- No application code changes needed

**Alternatives Considered**:
- Add new test IDs: Rejected as applications already compliant
- XPath-only for mobile: Rejected as constitution requires accessibility IDs

## Phase 1: Design & Contracts

### Data Model

**Entity**: Test Scenario

**Purpose**: Represents a single Gherkin scenario in a feature file

**Attributes**:
- **featureFile** (String): Path to .feature file (e.g., `/e2e-tests/java/src/test/resources/features/mobile/pet-details.feature`)
- **scenarioName** (String): Human-readable scenario title (e.g., "Navigate to pet details from list")
- **tags** (List<String>): Platform and type tags (e.g., `["@ios", "@smoke"]`)
- **steps** (List<GherkinStep>): Ordered list of Given/When/Then steps
- **status** (Enum): PENDING | PASSING | FAILING

**Relationships**:
- Belongs to one Feature File
- References Step Definitions (many-to-many)
- Generates one HTML Report Entry

**Validation Rules**:
- Must have at least one Given, one When, one Then step
- Tags must be valid (@web, @android, @ios, @smoke, @negative, @navigation)
- Scenario name must be unique within feature file

**State Transitions**:
```
PENDING → (test implemented) → PASSING | FAILING
PASSING → (code change) → FAILING
FAILING → (fix) → PASSING
```

---

**Entity**: Page Object / Screen Object

**Purpose**: Represents a screen or page in application with element locators and interaction methods

**Attributes**:
- **className** (String): Java class name (e.g., `PetDetailsScreen`, `PetListPage`)
- **elements** (Map<String, WebElement>): Locators for UI elements with accessibility IDs
- **methods** (List<String>): Interaction methods (e.g., `clickAnimalCard(String id)`, `scrollToBottom()`)
- **platform** (Enum): WEB | ANDROID | IOS

**Relationships**:
- Used by Step Definitions (one-to-many)
- Contains WebElements (composition)

**Validation Rules**:
- Web: Must use @FindBy with XPath locators
- Mobile: Must use dual annotations (@iOSXCUITFindBy, @AndroidFindBy)
- Method names must be descriptive actions (no generic "click()")

---

**Entity**: Step Definition

**Purpose**: Java method implementing Gherkin step logic

**Attributes**:
- **cucumberAnnotation** (Enum): @Given | @When | @Then
- **stepPattern** (String): Regex pattern matching Gherkin text (e.g., "I am on the pet list page")
- **methodName** (String): Java method name (e.g., `givenUserIsOnPetListPage()`)
- **assertions** (List<String>): Expected outcomes to verify
- **pageObject** (PageObject): Reference to Page/Screen Object used

**Relationships**:
- Implements one Gherkin Step Pattern
- Uses one or more Page/Screen Objects
- Belongs to one Step Definition Class

**Validation Rules**:
- Must include clear assertion messages (FR-018)
- Must use Page/Screen Object methods (no direct WebDriver calls)
- Pattern must uniquely match Gherkin text

---

**Entity**: Coverage Gap

**Purpose**: Tracks difference between required scenarios (spec) and implemented scenarios (code)

**Attributes**:
- **spec** (String): Source spec ID (e.g., "Spec 005", "Spec 012")
- **userStory** (String): User Story reference (e.g., "User Story 1: View Animal List")
- **requiredScenarios** (Integer): Total scenarios defined in spec
- **implementedScenarios** (Integer): Scenarios with passing tests
- **coveragePercentage** (Double): (implemented / required) * 100

**Relationships**:
- Tracks Test Scenarios (one-to-many)
- References Spec Requirements

**Validation Rules**:
- Coverage percentage must be 0-100
- Success criteria: Web 90%+, Mobile Pet Details 35%+, Mobile Animal List 90%

**State Transitions**:
```
IDENTIFIED → (scenarios implemented) → REDUCED
REDUCED → (all scenarios pass) → CLOSED
```

### Contracts

**Contract 1: Web Pet List Feature File**

**File**: `/e2e-tests/java/src/test/resources/features/web/pet-list.feature`

**Purpose**: Cucumber feature file defining web Animal List test scenarios

**Content Structure**:
```gherkin
@web
Feature: Pet List Management (Web)
  As a user browsing the PetSpot web application
  I want to view and interact with pet announcements
  So that I can find pets and report missing animals

  Background:
    Given I am on the pet list page
    And the page has loaded completely

  @smoke
  Scenario: View pet list on web
    When I view the pet list
    Then I should see at least one pet announcement
    And the list should contain animal cards

  @smoke
  Scenario: Verify UI elements are present
    Then the add button should be visible
    And the add button should have text "Report a Missing Animal"

  # NEW SCENARIOS (8 total):

  Scenario: List scrolls smoothly
    When I scroll down the pet list
    Then the list should scroll smoothly
    And all animal cards should be accessible

  Scenario: Scrolling stops at last item
    Given there are multiple animals in the list
    When I scroll to the bottom of the list
    Then the list should stop at the last animal card
    And I should not be able to scroll further

  Scenario: Animal card tap triggers navigation
    When I click on an animal card with ID "1"
    Then the system should trigger navigation to animal details
    And I should see a console log with animal ID

  Scenario: Button remains visible during scroll
    When I scroll up and down the animal list
    Then the "Report a Missing Animal" button should remain visible at all times

  Scenario: Report button tap action
    When I click the "Report a Missing Animal" button
    Then the system should trigger the report missing animal flow
    And I should see a console log confirming action

  Scenario: Animal card details display
    Given I am on the pet list page with loaded animals
    When I view an animal card with ID "1"
    Then the card should display species, breed, status, date, and location
    And the card should match the expected data from the backend

  Scenario: Loading state displayed
    Given I navigate to the pet list page
    When the page is loading
    Then a loading indicator should be displayed
    And the list should eventually appear

  Scenario: Report Found Animal button (web only)
    Then the "Report Found Animal" button should be visible
    And the button should be positioned at the top-right

  Scenario: Reserved search space
    When I view the top of the screen
    Then there should be space reserved for a search component
    And the space should have minimum height of 64px
```

**Validation**: 
- Must have @web tag
- Must follow Given-When-Then structure
- Must reference existing accessibility IDs from Spec 005

---

**Contract 2: Mobile Pet Details Feature File**

**File**: `/e2e-tests/java/src/test/resources/features/mobile/pet-details.feature`

**Purpose**: Cucumber feature file defining iOS Pet Details test scenarios

**Content Structure**:
```gherkin
@mobile @ios
Feature: Pet Details Screen (iOS)
  As a user using the PetSpot mobile application
  I want to view detailed information about a pet
  So that I can contact the owner and verify pet identity

  Background:
    Given I have launched the mobile app
    And I am on the pet list screen

  @smoke
  Scenario: Navigate to pet details from list
    When I tap on the first pet in the list
    Then I should navigate to the pet details screen
    And the details view should be displayed

  @smoke
  Scenario: Display loading state
    Given I navigate to pet details for pet "1"
    When data is being fetched
    Then I should see a loading indicator

  @smoke
  Scenario: Display pet details after loading
    Given I navigate to pet details for pet "1"
    When the details finish loading
    Then I should see pet photo, name, species, breed, and status

  Scenario: Display error state with retry
    Given I navigate to pet details for invalid pet
    When data fetch fails
    Then I should see error message and retry button

  Scenario: Retry button reloads data
    Given I am on pet details with error state
    When I tap retry button
    Then loading state should be displayed again

  Scenario: Contact owner via phone
    Given I am on pet details screen
    When I tap on the phone number
    Then iOS dialer should open with number

  Scenario: Contact owner via email
    Given I am on pet details screen
    When I tap on the email address
    Then iOS mail composer should open

  Scenario: Display MISSING status badge
    Given I am on pet details for a missing pet
    Then I should see red MISSING badge

  Scenario: Display FOUND status badge
    Given I am on pet details for a found pet
    Then I should see blue FOUND badge

  Scenario: Display Remove Report button
    Given I am on pet details screen
    Then I should see Remove Report button at bottom

  Scenario: Handle Remove Report button tap
    Given I am on pet details screen
    When I tap Remove Report button
    Then action should be logged

  Scenario: Display fallback when no photo
    Given I am on pet details for pet without photo
    Then I should see photo placeholder with "Image not available" text
```

**Validation**:
- Must have @ios tag
- Must follow Given-When-Then structure
- Must reference existing accessibility IDs from Spec 012

---

**Contract 3: Page Object Model Extensions**

**File**: `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/pages/PetListPage.java`

**Purpose**: Page Object Model for web Animal List with extended methods

**Required New Methods**:

```java
/**
 * Scrolls to the bottom of the pet list
 */
public void scrollToBottom();

/**
 * Checks if the pet list is scrollable
 * @return true if list can be scrolled, false otherwise
 */
public boolean isScrollable();

/**
 * Checks if list can scroll further down
 * @return true if more content below, false if at bottom
 */
public boolean canScrollFurther();

/**
 * Clicks on an animal card by ID
 * @param animalId The animal ID to click
 */
public void clickAnimalCard(String animalId);

/**
 * Checks if Report button is visible after scrolling
 * @return true if button visible, false otherwise
 */
public boolean isButtonVisibleAfterScroll();

/**
 * Clicks the "Report a Missing Animal" button
 */
public void clickReportMissingButton();

/**
 * Gets the height of the search placeholder
 * @return Height in pixels
 */
public int getSearchPlaceholderHeight();

/**
 * Gets the Report Found Animal button (web only)
 * @return WebElement for Report Found button
 */
public WebElement getReportFoundButton();
```

**Validation**:
- Must use @FindBy annotations with XPath locators
- Must follow existing Page Object pattern from Spec 016
- Must not contain assertions (those belong in step definitions)

---

**Contract 4: Screen Object Model for Pet Details**

**File**: `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/screens/PetDetailsScreen.java`

**Purpose**: Screen Object Model for mobile Pet Details with dual annotations

**Required Elements and Methods**:

```java
public class PetDetailsScreen {
    // Elements with dual annotations
    @iOSXCUITFindBy(accessibility = "petDetails.view")
    @AndroidFindBy(accessibility = "petDetails.view")
    private WebElement detailsView;
    
    @iOSXCUITFindBy(accessibility = "petDetails.loading")
    @AndroidFindBy(accessibility = "petDetails.loading")
    private WebElement loadingSpinner;
    
    @iOSXCUITFindBy(accessibility = "petDetails.error")
    @AndroidFindBy(accessibility = "petDetails.error")
    private WebElement errorView;
    
    @iOSXCUITFindBy(accessibility = "petDetails.error.retry")
    @AndroidFindBy(accessibility = "petDetails.error.retry")
    private WebElement retryButton;
    
    @iOSXCUITFindBy(accessibility = "petDetails.photo.image")
    @AndroidFindBy(accessibility = "petDetails.photo.image")
    private WebElement petPhoto;
    
    @iOSXCUITFindBy(accessibility = "petDetails.phone.tap")
    @AndroidFindBy(accessibility = "petDetails.phone.tap")
    private WebElement phoneNumber;
    
    @iOSXCUITFindBy(accessibility = "petDetails.email.tap")
    @AndroidFindBy(accessibility = "petDetails.email.tap")
    private WebElement emailAddress;
    
    @iOSXCUITFindBy(accessibility = "petDetails.status.badge")
    @AndroidFindBy(accessibility = "petDetails.status.badge")
    private WebElement statusBadge;
    
    @iOSXCUITFindBy(accessibility = "petDetails.removeReport.button")
    @AndroidFindBy(accessibility = "petDetails.removeReport.button")
    private WebElement removeReportButton;
    
    // Methods
    public boolean isDisplayed();
    public boolean isLoadingDisplayed();
    public boolean isErrorDisplayed();
    public void tapRetryButton();
    public void tapPhoneNumber();
    public void tapEmailAddress();
    public void tapRemoveReportButton();
    public String getStatusBadgeText();
    public String getStatusBadgeColor();
}
```

**Validation**:
- Must use dual annotations per Constitution Principle XII
- Must follow accessibility ID naming from Spec 012
- Must not contain test logic (only element access and basic interactions)

---

**Contract 5: Test Report Structure**

**Purpose**: Maven Cucumber HTML reports per platform

**Generated Paths**:
- Web: `/e2e-tests/target/cucumber-reports/web/index.html`
- iOS: `/e2e-tests/target/cucumber-reports/ios/index.html`
- Android: `/e2e-tests/target/cucumber-reports/android/index.html`

**Report Contents**:
- Feature file name and description
- Scenario pass/fail status
- Execution time per scenario
- Step details with parameters
- Error messages and stack traces (if failed)
- Screenshot attachments (optional)

**Validation**:
- Must be generated via Maven Cucumber plugin
- Must be separate per platform (per Constitution Principle XII)
- Must show coverage metrics (scenarios passed/total)

### Quickstart

**Prerequisites**:
- Maven 3.9+ installed
- Java 21 JDK available
- ChromeDriver installed for web tests
- Appium server running on localhost:4723 for mobile tests
- iOS simulator with iOS 18.1+ or Android emulator with API 34+
- Web application running on http://localhost:8080
- iOS app installed on simulator

**Run All Tests**:
```bash
cd /e2e-tests/java
mvn clean test
```

**Run Web Tests Only**:
```bash
mvn test -Dcucumber.filter.tags="@web"
```

**Run iOS Tests Only**:
```bash
mvn test -Dcucumber.filter.tags="@ios"
```

**Run Smoke Tests (Fast Subset)**:
```bash
mvn test -Dcucumber.filter.tags="@smoke"
```

**View HTML Reports**:
```bash
# Web report
open target/cucumber-reports/web/index.html

# iOS report
open target/cucumber-reports/ios/index.html
```

**Verify Coverage Increase**:
- Check web report: Should show 10/10 scenarios passing (was 2/10)
- Check iOS Pet Details: Should show 10-12 scenarios passing (was 0)
- Check iOS Animal List: Should show 9/10 scenarios passing (was 6/10)

**Development Workflow**:
1. Create/modify `.feature` file in `/src/test/resources/features/{web|mobile}/`
2. Run tests: `mvn test -Dcucumber.filter.tags="@platform"` to see undefined steps
3. Implement step definitions in `/src/test/java/.../steps/{web|mobile}/`
4. Add Page/Screen Object methods as needed
5. Re-run tests until passing
6. Review HTML report for coverage

**Troubleshooting**:
- If web tests fail: Verify app running on localhost:8080 and ChromeDriver installed
- If mobile tests fail: Verify Appium server running and simulator launched
- If element not found: Check accessibility ID matches Spec 005/012 conventions
- If report not generated: Check Maven Cucumber plugin configuration in pom.xml

### Agent Context Update

**Note**: This section will be filled after Phase 1 completion by running:
```bash
.specify/scripts/bash/update-agent-context.sh cursor-agent
```

The script will add:
- Java 21
- Maven 3.9+
- Selenium WebDriver 4.15+
- Appium 9.0.0+
- Cucumber 7.14.0

to the appropriate agent context file while preserving existing content.
