# Quickstart: Complete Java E2E Test Coverage

**Feature**: 025-java-e2e-coverage  
**Date**: 2025-11-26

## Prerequisites

Ensure the following are installed and running:

**Required Software**:
- ✅ Maven 3.9+ (`mvn --version`)
- ✅ Java 21 JDK (`java --version`)
- ✅ ChromeDriver (for web tests) - auto-managed by WebDriverManager
- ✅ Appium server for mobile tests (`npm install -g appium`)

**Running Services**:
- ✅ Web application on http://localhost:8080 (`cd webApp && npm start`)
- ✅ Backend API on http://localhost:3000 (`cd server && npm run dev`)
- ✅ Appium server on localhost:4723 (`appium --address localhost --port 4723`)

**Mobile Testing**:
- ✅ iOS simulator running (iPhone 16, iOS 18.1+)
- ✅ iOS PetSpot app installed on simulator (bundle ID: `com.intive.aifirst.petspot.PetSpot`)

## Quick Commands

### Run All Tests
```bash
cd /Users/szymon.wagner/projects/INTIVE/AI-First/e2e-tests/java
mvn clean test
```

### Run Web Tests Only
```bash
mvn test -Dcucumber.filter.tags="@web"
```

### Run iOS Tests Only
```bash
mvn test -Dcucumber.filter.tags="@ios"
```

### Run Smoke Tests (Fast Subset)
```bash
mvn test -Dcucumber.filter.tags="@smoke"
```

### Run Specific Feature File
```bash
mvn test -Dcucumber.features="src/test/resources/features/mobile/pet-details.feature"
```

### View HTML Reports
```bash
# Web report
open target/cucumber-reports/web/index.html

# iOS report
open target/cucumber-reports/ios/index.html

# Android report (when implemented)
open target/cucumber-reports/android/index.html
```

## Verify Coverage Improvements

### Before Feature 025

**Web** (Animal List):
```bash
mvn test -Dcucumber.filter.tags="@web"
# Expected: 2/10 scenarios passing (20% coverage)
```

**Mobile** (Pet Details):
```bash
mvn test -Dcucumber.filter.tags="@ios" -Dcucumber.features="**/pet-details.feature"
# Expected: 0 scenarios (feature file doesn't exist)
```

**Mobile** (Animal List):
```bash
mvn test -Dcucumber.filter.tags="@ios" -Dcucumber.features="**/pet-list.feature"
# Expected: 6/10 scenarios passing, 4 failing (invalid search tests)
```

### After Feature 025

**Web** (Animal List):
```bash
mvn test -Dcucumber.filter.tags="@web"
# Expected: 10/10 scenarios passing (100% coverage)
```

**Mobile** (Pet Details):
```bash
mvn test -Dcucumber.filter.tags="@ios" -Dcucumber.features="**/pet-details.feature"
# Expected: 10-12 scenarios passing (35-40% coverage)
```

**Mobile** (Animal List):
```bash
mvn test -Dcucumber.filter.tags="@ios" -Dcucumber.features="**/pet-list.feature"
# Expected: 9/10 scenarios passing (90% coverage, search tests commented out)
```

## Development Workflow

### Adding a New Test Scenario

**Step 1: Write Gherkin Scenario**

Edit feature file (e.g., `/src/test/resources/features/mobile/pet-details.feature`):
```gherkin
@ios @smoke
Scenario: Navigate to pet details from list
  Given I am on the pet list screen
  When I tap on the first pet in the list
  Then I should navigate to the pet details screen
  And the details view should be displayed
```

**Step 2: Run Tests to See Undefined Steps**
```bash
mvn test -Dcucumber.filter.tags="@ios"
```

Output will show:
```
You can implement missing steps with the snippets below:

@Given("I am on the pet list screen")
public void i_am_on_the_pet_list_screen() {
    // Write code here that turns the phrase above into concrete actions
}

@When("I tap on the first pet in the list")
public void i_tap_on_the_first_pet_in_the_list() {
    // Write code here that turns the phrase above into concrete actions
}
...
```

**Step 3: Implement Step Definitions**

In `/src/test/java/.../steps/mobile/PetDetailsMobileSteps.java`:
```java
@Given("I am on the pet list screen")
public void givenUserIsOnPetListScreen() {
    PetListScreen screen = new PetListScreen();
    assertTrue(screen.isDisplayed(), "Pet list screen should be displayed");
}

@When("I tap on the first pet in the list")
public void whenUserTapsPetInList() {
    PetListScreen screen = new PetListScreen();
    screen.tapAnimalCard("1"); // First pet has ID "1"
}

@Then("I should navigate to the pet details screen")
public void thenNavigateToPetDetails() {
    PetDetailsScreen screen = new PetDetailsScreen();
    assertTrue(screen.isDisplayed(), "Pet details screen should be displayed after navigation");
}

@Then("the details view should be displayed")
public void thenDetailsViewDisplayed() {
    PetDetailsScreen screen = new PetDetailsScreen();
    WebElement detailsView = screen.getDetailsView();
    assertTrue(detailsView.isDisplayed(), "Details view element should be visible");
}
```

**Step 4: Add Page/Screen Object Methods (If Needed)**

In `/src/test/java/.../screens/PetDetailsScreen.java`:
```java
public class PetDetailsScreen {
    @iOSXCUITFindBy(accessibility = "petDetails.view")
    @AndroidFindBy(accessibility = "petDetails.view")
    private WebElement detailsView;
    
    public WebElement getDetailsView() {
        return detailsView;
    }
    
    public boolean isDisplayed() {
        try {
            return detailsView.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
```

**Step 5: Re-run Tests**
```bash
mvn test -Dcucumber.filter.tags="@ios"
```

**Step 6: Review HTML Report**
```bash
open target/cucumber-reports/ios/index.html
```

### Modifying Existing Tests

**Commenting Out Search Scenarios**:

In `/src/test/resources/features/mobile/pet-list.feature`:
```gherkin
# TODO: Uncomment when search functionality is implemented (not in Spec 005 - future work)
# @android
# Scenario: Search for specific species on Android
#   When I tap on the search input
#   And I enter "dog" in the search field
#   Then I should see only dog announcements
#   And the Android keyboard should be hidden
```

**Adding Button Scenarios**:

In `/src/test/resources/features/mobile/pet-list.feature`:
```gherkin
@android @ios
Scenario: Report button visible at bottom
  Given I am on the pet list screen
  Then the "Report a Missing Animal" button should be visible

@android @ios
Scenario: Report button remains visible during scroll
  Given I am on the pet list screen
  When I scroll down the pet list
  Then the "Report a Missing Animal" button should remain visible
```

## Troubleshooting

### Web Tests

**Problem**: Tests fail with "ChromeDriver not found"
```
Solution: WebDriverManager should auto-download. If fails, manually install:
brew install chromedriver  # macOS
```

**Problem**: Tests fail with "Connection refused to localhost:8080"
```
Solution: Start web application:
cd /Users/szymon.wagner/projects/INTIVE/AI-First/webApp
npm install
npm start
```

**Problem**: Element not found by XPath
```
Solution: Verify element exists in web app and XPath is correct:
- Open http://localhost:8080 in browser
- Inspect element and copy XPath
- Update Page Object locator
```

### Mobile Tests

**Problem**: Tests fail with "Could not connect to Appium server"
```
Solution: Start Appium server:
npm install -g appium
appium --address localhost --port 4723
```

**Problem**: Tests fail with "No simulators available"
```
Solution: Launch iOS simulator:
open -a Simulator
# Or via command line:
xcrun simctl boot "iPhone 16"
```

**Problem**: Tests fail with "App not installed"
```
Solution: Build and install iOS app:
cd /Users/szymon.wagner/projects/INTIVE/AI-First/iosApp
xcodebuild -scheme iosApp -configuration Debug -destination 'platform=iOS Simulator,name=iPhone 16' build
xcrun simctl install booted <path-to-app>
```

**Problem**: Element not found by accessibility identifier
```
Solution: Verify accessibility ID in iOS app:
- Check Spec 005/012 for expected ID format
- Verify app code has .accessibilityIdentifier() modifier
- Common IDs: petDetails.view, animalList.item.1, etc.
```

**Problem**: Tests fail with "Session not created"
```
Solution: Check Appium capabilities match simulator:
- Device name: "iPhone 16"
- Platform version: "18.1"
- Bundle ID: "com.intive.aifirst.petspot.PetSpot"
```

### General

**Problem**: HTML reports not generated
```
Solution: Check Maven Cucumber plugin in pom.xml:
- Verify <plugin> configuration for net.masterthought:maven-cucumber-reporting
- Check <outputDirectory> paths exist
- Run: mvn clean test (clean removes stale reports)
```

**Problem**: All tests failing with "No tests found"
```
Solution: Verify Maven Surefire plugin configuration:
<includes>
    <include>**/*Runner.java</include>
    <include>**/*Test.java</include>
</includes>
```

## Testing Checklist

Before running tests:
- [ ] Web app running on localhost:8080
- [ ] Backend API running on localhost:3000
- [ ] Appium server running on localhost:4723 (mobile only)
- [ ] iOS simulator running with app installed (mobile only)
- [ ] ChromeDriver available (web only)

After implementation:
- [ ] All new scenarios pass
- [ ] HTML reports generated for each platform
- [ ] Coverage metrics meet targets (web: 90%, mobile Pet Details: 35%, mobile Animal List: 90%)
- [ ] No search-related failures in mobile tests
- [ ] Test execution time under 5 minutes per platform

## References

- [Spec 005: Animal List Screen](/Users/szymon.wagner/projects/INTIVE/AI-First/specs/005-animal-list/spec.md)
- [Spec 012: iOS Pet Details Screen](/Users/szymon.wagner/projects/INTIVE/AI-First/specs/012-ios-pet-details-screen/spec.md)
- [Spec 016: E2E Java Migration](/Users/szymon.wagner/projects/INTIVE/AI-First/specs/016-e2e-java-migration/spec.md)
- [Constitution: E2E Testing](/.specify/memory/constitution.md#principle-xii-end-to-end-testing)
- [Cucumber Documentation](https://cucumber.io/docs/cucumber/)
- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)

