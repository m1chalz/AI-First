# Quickstart Guide: E2E Testing with Java/Maven/Selenium/Cucumber

**Feature**: 016-e2e-java-migration  
**Date**: 2025-11-25 (Updated for Dual-Stack Coexistence)  
**Phase**: 1 - Design & Contracts

## Overview

This guide helps developers set up and run the **new Java-based E2E testing infrastructure** for PetSpot alongside the existing TypeScript tests. Both test stacks coexist and you can choose which to use based on your feature branch needs.

**Important**: TypeScript tests (Playwright/WebdriverIO) continue to work - you're not required to migrate immediately. Use Java tests when starting new features or when updating existing tests provides a good migration opportunity.

After completing this guide, you'll be able to write, run, and debug E2E tests for web (Selenium) and mobile (Appium) platforms using Java/Cucumber.

## Which Test Stack Should I Use?

Both TypeScript and Java test stacks are fully supported. Choose based on your situation:

| Situation | Recommended Stack | Why |
|-----------|------------------|-----|
| **Writing E2E test for new feature** | Java/Cucumber | Learn new stack with fresh context, no migration burden |
| **Updating existing E2E test** | TypeScript (current) OR Java (migrate) | Your choice - maintain consistency or take opportunity to migrate |
| **Bug fix in existing E2E test** | TypeScript (current stack) | Quick fix in familiar stack, optionally migrate afterwards |
| **Large E2E test refactor** | Java/Cucumber | Good opportunity to migrate while restructuring anyway |
| **Working on feature branch with TypeScript tests** | TypeScript | No pressure to migrate - focus on feature development |

**No forced timeline**: TypeScript tests will be removed only when all active feature branches have migrated organically. There's no deadline or pressure to switch immediately.

## Prerequisites

Before starting, ensure you have:

### Required Software

1. **Java JDK 21** (LTS)
   ```bash
   # Verify installation
   java -version
   # Should output: openjdk version "21.x.x"
   ```

2. **Maven 3.6+**
   ```bash
   # Verify installation
   mvn -version
   # Should output: Apache Maven 3.6.x or higher
   ```

3. **Google Chrome** (latest stable)
   ```bash
   # Verify installation
   google-chrome --version
   # Should output: Google Chrome 120.x or higher
   ```

4. **Android Studio** (for Android testing)
   - Download from: https://developer.android.com/studio
   - Install Android SDK Platform 34 (Android 14)
   - Create Android Emulator with Android 14

5. **Xcode 15+** (for iOS testing, macOS only)
   ```bash
   # Verify installation
   xcodebuild -version
   # Should output: Xcode 15.x or higher
   ```
   - Install iOS 17 Simulator via Xcode → Settings → Platforms

6. **Appium Server 9.x**
   ```bash
   # Install via npm
   npm install -g appium@9
   
   # Verify installation
   appium -v
   # Should output: 9.x.x
   
   # Install Appium drivers
   appium driver install uiautomator2  # For Android
   appium driver install xcuitest      # For iOS
   ```

## Project Setup

### Step 1: Clone and Navigate

```bash
cd /path/to/AI-First
git checkout 015-e2e-java-migration
cd e2e-tests
```

### Step 2: Verify Maven Dependencies

```bash
# Download all dependencies and compile project
mvn clean install -DskipTests

# Expected output:
# [INFO] BUILD SUCCESS
# [INFO] Total time: XX s
```

### Step 3: Configure Test Environment

Create `src/test/resources/test.properties` (if not exists):

```properties
# Web Testing Configuration
web.base.url=http://localhost:3000
web.browser=chrome
web.headless=false
web.implicit.wait=10

# Mobile Testing Configuration
appium.server.url=http://127.0.0.1:4723
android.platform.version=14
android.device.name=Android Emulator
android.app.path=${user.dir}/apps/petspot-android.apk
ios.platform.version=17.0
ios.device.name=iPhone 15
ios.app.path=${user.dir}/apps/petspot-ios.app

# Screenshot Configuration
screenshot.on.failure=true
screenshot.directory=${user.dir}/target/screenshots

# Logging Configuration
log.level=INFO
log.directory=${user.dir}/target/logs
```

## Running Tests

### Web Tests (Selenium + Chrome)

#### 1. Start the Web Application

```bash
# Terminal 1: Start backend server
cd server
npm install
npm run dev
# Server should start on http://localhost:3000

# Terminal 2: Start web app (if separate)
cd webApp
npm install
npm start
# Web app should be accessible at http://localhost:3000
```

#### 2. Run Web E2E Tests

```bash
# Terminal 3: Run web tests
cd e2e-tests/java

# Run all web tests (via WebTestRunner)
mvn test -Dtest=WebTestRunner

# OR use Cucumber tag filtering
mvn test -Dcucumber.filter.tags="@web"

# Run smoke tests only
mvn test -Dcucumber.filter.tags="@web and @smoke"

# Run specific scenario tag
mvn test -Dcucumber.filter.tags="@web and @navigation"

# Expected output (when server is running):
# Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
# [INFO] BUILD SUCCESS
#
# If server NOT running (for infrastructure testing):
# Tests run: 7, Failures: 6, Errors: 1, Skipped: 0
# ERR_CONNECTION_REFUSED on http://localhost:3000/pets
# (This validates that infrastructure works - tests execute and fail gracefully)
```

**Example Execution Log**:
```
========================================
Starting scenario: View pet list on web
Tags: [@web, @smoke]
========================================
Navigated to: http://localhost:3000/pets
Pet list should be visible after page load
Screenshot saved: target/screenshots/view_pet_list_on_web_2025-11-26_08-43-47.png
Quit WebDriver for scenario: View pet list on web
========================================
Finished scenario: View pet list on web
Status: FAILED
========================================
```

#### 3. View Test Reports

```bash
# Open Cucumber HTML report in browser
open target/cucumber-reports/web/cucumber.html

# Or manually navigate to: e2e-tests/java/target/cucumber-reports/web/cucumber.html
```

**Generated Reports**:
- **Cucumber HTML**: `target/cucumber-reports/web/cucumber.html` (human-readable with screenshots)
- **Cucumber JSON**: `target/cucumber-web.json` (machine-readable for CI/CD integration)
- **JUnit XML**: `target/cucumber-web.xml` (for test result aggregation)
- **Surefire Reports**: `target/surefire-reports/*.xml` (Maven test execution logs)

#### 4. View Failure Screenshots

If tests fail, screenshots are automatically captured:

```bash
# List captured screenshots
ls -lh target/screenshots/

# Example output:
# view_pet_list_on_web_2025-11-26_08-43-47.png
# search_for_specific_species_on_web_2025-11-26_08-44-08.png
# filter_pets_by_multiple_species_2025-11-26_08-44-29.png

# Open a screenshot (macOS)
open target/screenshots/view_pet_list_on_web_2025-11-26_08-43-47.png
```

### Mobile Tests (Appium)

#### 1. Start Appium Server

```bash
# Terminal 1: Start Appium server
appium
# Server should start on http://127.0.0.1:4723
# Keep this terminal running during test execution
```

#### 2. Start Mobile Emulator/Simulator

**For Android:**
```bash
# Terminal 2: List available emulators
emulator -list-avds

# Start specific emulator
emulator -avd <emulator_name>
# Example: emulator -avd Pixel_6_API_34

# Wait for emulator to fully boot (check with: adb devices)
```

**For iOS (macOS only):**
```bash
# Terminal 2: List available simulators
xcrun simctl list devices

# Boot specific simulator
xcrun simctl boot "iPhone 15"

# Open Simulator app
open -a Simulator
```

#### 3. Build and Install Mobile App

**For Android:**
```bash
# Build Android APK
cd composeApp
./gradlew assembleDebug

# Copy APK to e2e-tests directory
cp build/outputs/apk/debug/composeApp-debug.apk ../e2e-tests/apps/petspot-android.apk

# Verify APK exists
ls -lh e2e-tests/apps/petspot-android.apk
```

**For iOS:**
```bash
# Build iOS app for simulator
cd iosApp
xcodebuild -scheme iosApp -configuration Debug -sdk iphonesimulator -derivedDataPath build

# Copy app bundle to e2e-tests directory
cp -r build/Build/Products/Debug-iphonesimulator/iosApp.app ../e2e-tests/apps/petspot-ios.app

# Verify app bundle exists
ls -lh e2e-tests/apps/petspot-ios.app
```

#### 4. Run Mobile E2E Tests

```bash
# Terminal 3: Run mobile tests
cd e2e-tests

# Run Android tests
mvn test -Dcucumber.filter.tags="@android"

# Run iOS tests (macOS only)
mvn test -Dcucumber.filter.tags="@ios"

# Run smoke tests for Android
mvn test -Dcucumber.filter.tags="@android and @smoke"

# Run specific feature file for mobile
mvn test -Dcucumber.filter.tags="@mobile" -Dcucumber.features="src/test/resources/features/mobile/pet-list.feature"

# Expected output:
# Tests run: X, Failures: 0, Errors: 0, Skipped: 0
# [INFO] BUILD SUCCESS
```

#### 5. View Test Reports

```bash
# Android report
open target/cucumber-reports/android/index.html

# iOS report
open target/cucumber-reports/ios/index.html
```

## Writing Your First Test

### Step 1: Create Feature File

Create `src/test/resources/features/web/my-first-test.feature`:

```gherkin
@web
Feature: My First E2E Test
  As a developer learning E2E testing
  I want to write a simple test
  So that I understand the testing framework

  @smoke
  Scenario: Visit homepage
    Given I navigate to the homepage
    When the page loads
    Then I should see the PetSpot logo
```

### Step 2: Create Page Object

Create `src/test/java/com/intive/aifirst/petspot/e2e/pages/HomePage.java`:

```java
package com.intive.aifirst.petspot.e2e.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage {
    private WebDriver driver;
    
    @FindBy(xpath = "//*[@data-testid='header.logo']")
    private WebElement logo;
    
    public HomePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    
    public boolean isLogoDisplayed() {
        return logo.isDisplayed();
    }
}
```

### Step 3: Create Step Definitions

Create `src/test/java/com/intive/aifirst/petspot/e2e/steps/web/MyFirstTestSteps.java`:

```java
package com.intive.aifirst.petspot.e2e.steps.web;

import com.intive.aifirst.petspot.e2e.pages.HomePage;
import com.intive.aifirst.petspot.e2e.utils.WebDriverManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MyFirstTestSteps {
    private WebDriver driver;
    private HomePage homePage;
    
    public MyFirstTestSteps() {
        this.driver = WebDriverManager.getDriver();
        this.homePage = new HomePage(driver);
    }
    
    @Given("I navigate to the homepage")
    public void navigateToHomepage() {
        driver.get("http://localhost:3000");
    }
    
    @When("the page loads")
    public void waitForPageLoad() {
        // Wait for page to be fully loaded
        try {
            Thread.sleep(2000); // Replace with proper explicit wait
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @Then("I should see the PetSpot logo")
    public void verifyLogoDisplayed() {
        assertTrue(homePage.isLogoDisplayed(), "PetSpot logo should be visible");
    }
}
```

### Step 4: Run Your Test

```bash
cd e2e-tests
mvn test -Dcucumber.filter.tags="@web and @smoke"

# Check report
open target/cucumber-reports/web/index.html
```

## Debugging Tests

### View Screenshots on Failure

```bash
# Screenshots automatically captured on test failure
ls -lh target/screenshots/

# View specific screenshot
open target/screenshots/Visit_homepage_2025-11-25_14-30-45.png
```

### View Execution Logs

```bash
# Console logs captured in target/logs/
cat target/logs/test-execution.log

# Selenium/Appium logs
cat target/logs/webdriver.log
cat target/logs/appium.log
```

### Debug in IDE (IntelliJ IDEA)

1. Open `e2e-tests` directory in IntelliJ
2. Install Cucumber for Java plugin
3. Open feature file
4. Click green arrow next to scenario
5. Select "Debug '[Scenario Name]'"
6. Set breakpoints in Step Definition methods

### Common Issues

**Issue**: `ChromeDriver not found`
- **Solution**: WebDriverManager should auto-download. If not, manually download ChromeDriver matching your Chrome version from https://chromedriver.chromium.org/

**Issue**: `Appium server not reachable`
- **Solution**: Ensure Appium server is running on http://127.0.0.1:4723 (check terminal output)

**Issue**: `Element not found`
- **Solution**: Check that data-testid/testTag/accessibilityIdentifier exists in platform code and matches XPath/locator exactly

**Issue**: `Test timeout`
- **Solution**: Increase wait timeout in Page/Screen Object methods or check if element selector is correct

## Next Steps

1. **Read the Data Model**: Review `data-model.md` to understand Page/Screen Object patterns
2. **Explore Sample Tests**: Check `contracts/sample-web.feature` and `contracts/sample-mobile.feature`
3. **Review Templates**: Use `contracts/PageObjectTemplate.java` and `contracts/ScreenObjectTemplate.java` as starting points
4. **Migrate Existing Tests**: Follow migration checklist in `research.md`
5. **Run Full Test Suite**: Execute all tests with `mvn test` (no tag filter)

## Useful Commands Cheat Sheet

```bash
# Web Tests
mvn test -Dcucumber.filter.tags="@web"                    # All web tests
mvn test -Dcucumber.filter.tags="@web and @smoke"         # Web smoke tests
mvn test -Dcucumber.filter.tags="@web and not @skip"      # Web tests (exclude @skip)

# Mobile Tests
mvn test -Dcucumber.filter.tags="@android"                # Android tests
mvn test -Dcucumber.filter.tags="@ios"                    # iOS tests
mvn test -Dcucumber.filter.tags="@mobile and @smoke"      # Mobile smoke tests

# Combined
mvn test -Dcucumber.filter.tags="@web or @android"        # Web + Android
mvn test -Dcucumber.filter.tags="@smoke"                  # All smoke tests

# Build & Test
mvn clean install                                         # Clean, compile, run all tests
mvn clean test -DskipTests                                # Compile only (skip tests)
mvn test -Dcucumber.features="path/to/feature"            # Run specific feature

# Reports
mvn surefire-report:report                                # Generate Surefire HTML report
open target/cucumber-reports/{web|android|ios}/index.html # View Cucumber report
```

## Support

For questions or issues:
- Check `research.md` for technology decisions and best practices
- Review `data-model.md` for entity definitions and patterns
- Refer to constitution.md for architectural compliance
- Ask team members familiar with E2E testing migration

Happy Testing! 🧪

