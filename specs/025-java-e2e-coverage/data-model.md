# Data Model: Complete Java E2E Test Coverage

**Feature**: 025-java-e2e-coverage  
**Date**: 2025-11-26  
**Status**: Complete

## Overview

This document defines the data model for the E2E test infrastructure. Unlike application features, this test infrastructure has a meta-model describing test artifacts (scenarios, page objects, step definitions) rather than business domain entities.

## Core Entities

### Test Scenario

**Purpose**: Represents a single Gherkin scenario in a feature file with Given-When-Then steps.

**Attributes**:

| Attribute | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| featureFile | String (Path) | Yes | Path to .feature file | `/e2e-tests/java/src/test/resources/features/mobile/pet-details.feature` |
| scenarioName | String | Yes | Human-readable scenario title | "Navigate to pet details from list" |
| tags | List<String> | Yes | Platform and type tags | `["@ios", "@smoke"]` |
| steps | List<GherkinStep> | Yes | Ordered Given/When/Then steps | See GherkinStep below |
| status | Enum | Yes | PENDING \| PASSING \| FAILING | PASSING |
| executionTimeMs | Long | No | Scenario execution duration | 1250 |

**Relationships**:
- Belongs to one Feature File (composition)
- References Step Definitions (many-to-many via step pattern matching)
- Generates one HTML Report Entry (composition)

**Validation Rules**:
- Must have at least one Given, one When, one Then step
- Tags must be from allowed set: @web, @android, @ios, @smoke, @negative, @navigation
- Scenario name must be unique within feature file
- Steps must be ordered: Given(s), then When(s), then Then(s)

**State Transitions**:
```
PENDING → (test implemented) → PASSING | FAILING
PASSING → (code/app change) → FAILING
FAILING → (fix step definition/app) → PASSING
```

**Example**:
```gherkin
@ios @smoke
Scenario: Navigate to pet details from list
  Given I am on the pet list screen
  When I tap on the first pet in the list
  Then I should navigate to the pet details screen
  And the details view should be displayed
```

---

### GherkinStep

**Purpose**: Individual Given/When/Then step within a scenario.

**Attributes**:

| Attribute | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| keyword | Enum | Yes | Given \| When \| Then \| And \| But | When |
| text | String | Yes | Step phrase with parameters | "I tap on the first pet in the list" |
| parameters | Map<String, String> | No | Extracted parameters | `{"position": "first"}` |
| stepDefinition | StepDefinition | Yes | Java method implementing step | Reference to method |

**Relationships**:
- Belongs to one Test Scenario (composition)
- Implemented by one Step Definition (association)

**Validation Rules**:
- Text must match a step definition pattern
- Parameters must be extractable from text via regex
- Keyword must match position (Given before When before Then)

---

### Page Object / Screen Object

**Purpose**: Represents a screen or page in application with element locators and interaction methods.

**Attributes**:

| Attribute | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| className | String | Yes | Java class name | `PetDetailsScreen` |
| platform | Enum | Yes | WEB \| ANDROID \| IOS | IOS |
| elements | Map<String, WebElement> | Yes | Element name to locator mapping | `{"detailsView": <locator>}` |
| methods | List<Method> | Yes | Interaction method signatures | `clickAnimalCard(String id)` |
| annotationType | Enum | Yes | XPATH \| ACCESSIBILITY \| DUAL | DUAL |

**Relationships**:
- Used by Step Definitions (one-to-many)
- Contains WebElements (composition)

**Validation Rules**:
- Web platform: Must use @FindBy with XPath or CSS selectors
- Mobile platform: Must use dual annotations (@iOSXCUITFindBy, @AndroidFindBy)
- Method names must be descriptive actions (not generic "click()")
- Must not contain assertions (return values only)

**Example (Mobile)**:
```java
public class PetDetailsScreen {
    @iOSXCUITFindBy(accessibility = "petDetails.view")
    @AndroidFindBy(accessibility = "petDetails.view")
    private WebElement detailsView;
    
    public boolean isDisplayed() {
        return detailsView.isDisplayed();
    }
}
```

**Example (Web)**:
```java
public class PetListPage {
    @FindBy(xpath = "//div[@data-testid='animal-list']")
    private WebElement listContainer;
    
    public void scrollToBottom() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollTop = arguments[0].scrollHeight", listContainer);
    }
}
```

---

### Step Definition

**Purpose**: Java method annotated with Cucumber that implements Gherkin step logic.

**Attributes**:

| Attribute | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| cucumberAnnotation | Enum | Yes | @Given \| @When \| @Then | @When |
| stepPattern | String (Regex) | Yes | Pattern matching Gherkin text | `"I tap on the {word} pet in the list"` |
| methodName | String | Yes | Java method name | `whenUserTapsPetInList` |
| parameters | List<Parameter> | No | Method parameters from pattern | `(String position)` |
| pageObjects | List<PageObject> | Yes | Page/Screen Objects used | `[PetListScreen]` |
| assertions | List<String> | No | Expected outcomes to verify | `["details view displayed"]` |

**Relationships**:
- Implements one or more Gherkin Step Patterns (one-to-many)
- Uses one or more Page/Screen Objects (many-to-many)
- Belongs to one Step Definition Class (composition)

**Validation Rules**:
- Must include clear assertion messages per FR-018
- Must use Page/Screen Object methods (no direct WebDriver calls in step body)
- Pattern must uniquely match Gherkin text (no ambiguous steps)
- @Given methods must not perform actions (only setup)
- @Then methods must include assertions

**Example**:
```java
@When("I tap on the {word} pet in the list")
public void whenUserTapsPetInList(String position) {
    PetListScreen screen = new PetListScreen();
    String petId = position.equals("first") ? "1" : position;
    screen.tapAnimalCard(petId);
}

@Then("the details view should be displayed")
public void thenDetailsViewDisplayed() {
    PetDetailsScreen screen = new PetDetailsScreen();
    assertTrue(screen.isDisplayed(), "Pet Details view should be displayed but was not found");
}
```

---

### Feature File

**Purpose**: Cucumber feature file grouping related scenarios.

**Attributes**:

| Attribute | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| filePath | String (Path) | Yes | Path to .feature file | `/features/mobile/pet-details.feature` |
| featureName | String | Yes | Feature title | "Pet Details Screen (iOS)" |
| description | String | No | Feature description | "As a user I want to view pet details..." |
| background | List<GherkinStep> | No | Common setup steps | `["Given I have launched the app"]` |
| scenarios | List<TestScenario> | Yes | Test scenarios | See TestScenario |
| tags | List<String> | Yes | Feature-level tags | `["@mobile", "@ios"]` |

**Relationships**:
- Contains Test Scenarios (composition, one-to-many)
- References Step Definitions (many-to-many)

**Validation Rules**:
- Must have at least one scenario
- Feature-level tags apply to all scenarios
- Background steps run before each scenario
- File must be in `/features/{web|mobile}/` directory

**Example**:
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
    ...
```

---

### Coverage Gap

**Purpose**: Tracks difference between required scenarios (spec) and implemented scenarios (code).

**Attributes**:

| Attribute | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| spec | String | Yes | Source spec ID | "Spec 005" |
| userStory | String | Yes | User Story reference | "User Story 1: View Animal List" |
| requiredScenarios | Integer | Yes | Total scenarios in spec | 10 |
| implementedScenarios | Integer | Yes | Scenarios with passing tests | 2 |
| coveragePercentage | Double | Yes | (implemented / required) * 100 | 20.0 |
| status | Enum | Yes | IDENTIFIED \| REDUCED \| CLOSED | IDENTIFIED |

**Relationships**:
- Tracks Test Scenarios (one-to-many)
- References Spec Requirements (external reference)

**Validation Rules**:
- Coverage percentage must be 0-100
- Required scenarios must be >= implemented scenarios
- Status CLOSED only when coverage >= target (web: 90%, mobile Pet Details: 35%, mobile Animal List: 90%)

**State Transitions**:
```
IDENTIFIED → (scenarios implemented) → REDUCED
REDUCED → (all scenarios pass, coverage >= target) → CLOSED
CLOSED → (spec updated with new requirements) → IDENTIFIED
```

**Success Criteria**:
- Spec 005 Animal List (Web): 90-100% coverage
- Spec 012 Pet Details (iOS): 35-40% coverage
- Spec 005 Animal List (Mobile): 90% coverage

**Example**:
```
Coverage Gap: Spec 005 Animal List (Web)
- Required: 10 scenarios
- Implemented: 2 scenarios (before this feature)
- Coverage: 20%
- Target: 90%
- Status: IDENTIFIED

After Feature 025:
- Implemented: 10 scenarios
- Coverage: 100%
- Status: CLOSED
```

---

### Test Report

**Purpose**: HTML document showing test execution results per platform.

**Attributes**:

| Attribute | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| platform | Enum | Yes | WEB \| ANDROID \| IOS | IOS |
| reportPath | String (Path) | Yes | Path to HTML file | `/target/cucumber-reports/ios/index.html` |
| totalScenarios | Integer | Yes | Total scenarios executed | 12 |
| passedScenarios | Integer | Yes | Scenarios that passed | 11 |
| failedScenarios | Integer | Yes | Scenarios that failed | 1 |
| skippedScenarios | Integer | Yes | Scenarios skipped | 0 |
| executionTimeMs | Long | Yes | Total execution time | 45000 |
| timestamp | DateTime | Yes | Report generation time | 2025-11-26T10:30:00Z |

**Relationships**:
- Contains Test Scenario Results (composition, one-to-many)
- References Feature Files (many-to-many)

**Validation Rules**:
- Generated via Maven Cucumber plugin per constitution
- Separate report per platform (web, android, ios)
- Must show: feature names, scenario names, pass/fail status, execution time, error messages

**Report Sections**:
1. **Summary**: Total scenarios, pass/fail counts, execution time
2. **Feature List**: All features with scenario counts
3. **Scenario Details**: Individual scenario results with steps
4. **Failed Scenarios**: Error messages and stack traces
5. **Screenshots**: Attached screenshots (if configured)

**Example Output**:
```
Platform: iOS
Total Scenarios: 12
Passed: 11 (91.7%)
Failed: 1 (8.3%)
Execution Time: 45s

Failed:
- "Display error state with retry" - Element not found: petDetails.error
```

---

## Relationships Diagram

```
Feature File (1) ──┐
                   │
                   ├─> Test Scenario (N) ──┐
                   │                        │
                   │                        ├─> Gherkin Step (N) ──> Step Definition (1)
                   │                        │
                   │                        └─> Test Report Entry (1)
                   │
                   └─> Background Steps (N)

Step Definition (N) ──> Page/Screen Object (N)

Page/Screen Object (1) ──> WebElement (N)

Coverage Gap (1) ──> Test Scenario (N)
Coverage Gap (1) ──> Spec Requirement (1)

Test Report (1) ──> Test Scenario Result (N)
```

---

## Accessibility Identifier Format

**Source**: Constitution Principle VI

**Format**: `{screen}.{element}.{action}`

**Web Animal List (Spec 005)**:
- `animalList.item.${id}` - Animal card
- `animalList.reportButton` - Report Missing button
- `animalList.listContainer` - List container

**Mobile Animal List (Spec 005)**:
- `animalList.item.${id}` - Animal card
- `animalList.reportButton` - Report Missing button

**Mobile Pet Details (Spec 012)**:
- `petDetails.view` - Main details view
- `petDetails.loading` - Loading spinner
- `petDetails.error` - Error view
- `petDetails.error.retry` - Retry button
- `petDetails.photo.image` - Pet photo
- `petDetails.phone.tap` - Phone number tap target
- `petDetails.email.tap` - Email tap target
- `petDetails.status.badge` - Status badge
- `petDetails.removeReport.button` - Remove Report button
- `petDetails.name.field` - Name field
- `petDetails.species.field` - Species field
- `petDetails.breed.field` - Breed field
- `petDetails.date.field` - Date field

---

## Mock Data Structure

**Source**: Spec 005, Spec 012

**Web Application** (16 mock animals):
- Animals with IDs "1" through "16"
- Each has: name, species, breed, status, lastSeenDate, location, description
- Mix of ACTIVE (MISSING) and FOUND statuses

**iOS Application** (16 mock animals in list, 4 with details):
- Animal List: IDs "1" through "16" with basic info
- Pet Details: Full data only for IDs "1", "2", "3", "4"
- IDs "5"-"16" return 404 (test error handling)

**Data Fields**:
```typescript
interface Animal {
  id: string;
  name: string;
  species: string; // "Dog", "Cat", "Bird", etc.
  breed: string;
  status: "ACTIVE" | "FOUND" | "CLOSED";
  lastSeenDate: string; // "DD/MM/YYYY"
  location: string; // "City • ±15 km"
  description?: string;
}

interface PetDetails extends Animal {
  phone: string;
  email?: string;
  microchip?: string; // "000-000-000-000"
  sex?: "MALE" | "FEMALE";
  age?: string;
  reward?: string;
  photoUrl?: string;
}
```

---

## File Structure

```
e2e-tests/java/
├── pom.xml                                    # Maven configuration
├── src/
│   └── test/
│       ├── java/com/intive/aifirst/petspot/e2e/
│       │   ├── pages/                         # Web Page Objects
│       │   │   ├── PageObjectTemplate.java   # Existing template
│       │   │   └── PetListPage.java          # EXTEND: Add 8 methods
│       │   ├── screens/                       # Mobile Screen Objects
│       │   │   ├── ScreenObjectTemplate.java # Existing template
│       │   │   ├── PetListScreen.java        # Existing
│       │   │   └── PetDetailsScreen.java     # NEW: Create with dual annotations
│       │   ├── steps/
│       │   │   ├── web/
│       │   │   │   ├── CommonWebSteps.java   # Existing
│       │   │   │   └── PetListWebSteps.java  # EXTEND: Add step definitions
│       │   │   └── mobile/
│       │   │       ├── CommonMobileSteps.java # Existing
│       │   │       ├── PetListMobileSteps.java # EXTEND: Add button steps
│       │   │       └── PetDetailsMobileSteps.java # NEW: Create
│       │   └── utils/                         # Existing utilities
│       │       ├── WaitUtil.java
│       │       ├── ScreenshotUtil.java
│       │       └── TestConfig.java
│       └── resources/
│           └── features/
│               ├── web/
│               │   └── pet-list.feature       # EXTEND: Add 8 scenarios
│               └── mobile/
│                   ├── pet-list.feature       # MODIFY: Comment search, add button scenarios
│                   └── pet-details.feature    # NEW: Create with 10-12 scenarios
└── target/
    └── cucumber-reports/                      # Generated HTML reports
        ├── web/index.html
        ├── android/index.html
        └── ios/index.html
```

---

## Summary

This data model defines the test infrastructure meta-model with 8 core entities:
1. **Test Scenario** - Gherkin scenarios
2. **GherkinStep** - Individual Given/When/Then steps
3. **Page/Screen Object** - Element locators and interaction methods
4. **Step Definition** - Java methods implementing steps
5. **Feature File** - Cucumber feature files
6. **Coverage Gap** - Tracking spec vs implementation
7. **Test Report** - HTML execution results
8. **Mock Data** - Test data structure

All entities follow validation rules ensuring constitution compliance (Principle XII: E2E Testing).

