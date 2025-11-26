# Data Model: E2E Test Infrastructure Entities

**Feature**: 016-e2e-java-migration  
**Date**: 2025-11-25  
**Phase**: 1 - Design & Contracts

## Overview

This document defines the core entity model for the Java/Maven/Cucumber E2E testing infrastructure. Unlike traditional application data models, this defines test infrastructure entities (Page Objects, Screen Objects, Step Definitions) that abstract and interact with the application under test.

## Core Testing Entities

### 1. Feature File (Gherkin Scenario)

**Purpose**: Defines user-facing test scenarios in natural language using Gherkin syntax

**Structure**:
- Feature: High-level feature description
- Scenario/Scenario Outline: Individual test case
- Given/When/Then steps: Test phases (setup, action, verification)
- Tags: Platform-specific execution control (@web, @android, @ios)
- Examples: Test data for data-driven scenarios (Scenario Outline)

**Attributes**:
| Attribute | Type | Required | Description |
|-----------|------|----------|-------------|
| feature_name | String | Yes | Human-readable feature name |
| scenarios | List<Scenario> | Yes | Collection of test scenarios |
| tags | List<String> | Yes | Platform tags for execution filtering |

**Example**:
```gherkin
@web
Feature: Pet List Management
  As a user
  I want to view and manage pet announcements
  So that I can find pets for adoption

  Scenario: View pet list
    Given I am on the pet list page
    When the page loads
    Then I should see the list of pets

  @smoke
  Scenario Outline: Search for pets by species
    Given I am on the pet list page
    When I search for "<species>"
    Then I should see only "<species>" pets
    
    Examples:
      | species |
      | dog     |
      | cat     |
```

**Relationships**:
- Referenced by Step Definitions (1 scenario → many steps)
- Executed by Cucumber Test Runner (1 runner → many features)

**Lifecycle**:
1. Written by QA engineer in Gherkin syntax
2. Parsed by Cucumber framework at test execution
3. Steps matched to Step Definition methods via regex/Cucumber expressions
4. Results reported in Cucumber HTML report

**Validation Rules**:
- Every scenario MUST have at least one Given, When, and Then step
- Every scenario MUST have exactly one platform tag (@web, @android, or @ios)
- Scenario names MUST be unique within a feature file

---

### 2. Page Object (Web)

**Purpose**: Encapsulates web page structure and interactions, abstracting WebDriver API

**Structure**:
- WebDriver instance for browser control
- WebElement fields with @FindBy XPath annotations
- Methods for user actions (click, type, verify)
- Constructor initializes PageFactory for annotation processing

**Attributes**:
| Attribute | Type | Required | Description |
|-----------|------|----------|-------------|
| driver | WebDriver | Yes | Selenium WebDriver instance for browser control |
| elements | Map<String, WebElement> | Yes | Annotated web elements on the page |
| page_url | String | Optional | Expected URL for navigation verification |

**Element Location Strategy**:
- Uses XPath selectors with data-testid attributes
- Pattern: `//*[@data-testid='screen.element.action']`
- Examples:
  - `//*[@data-testid='petList.addButton.click']`
  - `//*[@data-testid='petList.list']`
  - `//*[@data-testid='petList.item.123']`

**Example**:
```java
public class PetListPage {
    private WebDriver driver;
    
    @FindBy(xpath = "//*[@data-testid='petList.addButton.click']")
    private WebElement addButton;
    
    @FindBy(xpath = "//*[@data-testid='petList.list']")
    private WebElement petList;
    
    @FindBy(xpath = "//*[@data-testid='petList.searchInput']")
    private WebElement searchInput;
    
    public PetListPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    
    public void clickAddButton() {
        addButton.click();
    }
    
    public void searchForPet(String species) {
        searchInput.sendKeys(species);
        searchInput.sendKeys(Keys.ENTER);
    }
    
    public boolean isPetListDisplayed() {
        return petList.isDisplayed();
    }
    
    public int getPetCount() {
        List<WebElement> pets = driver.findElements(
            By.xpath("//*[starts-with(@data-testid, 'petList.item.')]")
        );
        return pets.size();
    }
}
```

**Relationships**:
- Instantiated by Step Definitions (web-specific)
- Uses WebDriver instance from WebDriverManager
- References platform code via data-testid attributes

**Lifecycle**:
1. Instantiated in Step Definition constructor or @Before hook
2. Elements initialized by PageFactory.initElements()
3. Methods called by Step Definition implementations
4. Disposed when WebDriver quits (end of scenario)

**Validation Rules**:
- Every Page Object MUST have a WebDriver field
- Every interactive element MUST be annotated with @FindBy
- XPath selectors MUST use data-testid attributes (no text/class-based selectors)
- Methods MUST represent user actions, not technical WebDriver operations

---

### 3. Screen Object (Mobile)

**Purpose**: Encapsulates mobile screen structure and interactions, abstracting Appium API with dual iOS/Android support

**Structure**:
- AppiumDriver instance for mobile device control
- WebElement fields with dual annotations (@AndroidFindBy + @iOSXCUITFindBy)
- Methods for user actions (tap, swipe, verify)
- Constructor initializes AppiumFieldDecorator for annotation processing

**Attributes**:
| Attribute | Type | Required | Description |
|-----------|------|----------|-------------|
| driver | AppiumDriver | Yes | Appium driver instance for device control |
| elements | Map<String, WebElement> | Yes | Dual-annotated mobile elements on screen |
| platform | String | Optional | Detected platform (Android/iOS) for debugging |

**Element Location Strategy**:
- Android: Uses UiAutomator selectors with resourceId
  - Pattern: `new UiSelector().resourceId("screen.element.action")`
- iOS: Uses XCUITest identifiers with accessibilityIdentifier
  - Pattern: `id = "screen.element.action"`

**Example**:
```java
public class PetListScreen {
    private AppiumDriver driver;
    
    @AndroidFindBy(uiAutomator = "new UiSelector().resourceId(\"petList.addButton.click\")")
    @iOSXCUITFindBy(id = "petList.addButton.click")
    private WebElement addButton;
    
    @AndroidFindBy(uiAutomator = "new UiSelector().resourceId(\"petList.list\")")
    @iOSXCUITFindBy(id = "petList.list")
    private WebElement petList;
    
    @AndroidFindBy(uiAutomator = "new UiSelector().resourceId(\"petList.searchInput\")")
    @iOSXCUITFindBy(id = "petList.searchInput")
    private WebElement searchInput;
    
    public PetListScreen(AppiumDriver driver) {
        this.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }
    
    public void tapAddButton() {
        addButton.click();
    }
    
    public void searchForPet(String species) {
        searchInput.sendKeys(species);
        driver.hideKeyboard();
    }
    
    public boolean isPetListDisplayed() {
        return petList.isDisplayed();
    }
    
    public int getPetCount() {
        // Platform-specific logic if needed
        if (driver.getPlatformName().equalsIgnoreCase("Android")) {
            return driver.findElements(
                AppiumBy.androidUIAutomator("new UiSelector().resourceIdMatches(\"petList\\\\.item\\\\..*\")")
            ).size();
        } else {
            return driver.findElements(
                AppiumBy.iOSClassChain("**/XCUIElementTypeCell[`name BEGINSWITH 'petList.item.'`]")
            ).size();
        }
    }
}
```

**Relationships**:
- Instantiated by Step Definitions (mobile-specific)
- Uses AppiumDriver instance from AppiumDriverManager
- References platform code via testTag (Android) and accessibilityIdentifier (iOS)

**Lifecycle**:
1. Instantiated in Step Definition constructor or @Before hook
2. Elements initialized by PageFactory with AppiumFieldDecorator
3. Decorator resolves correct annotation based on driver platform
4. Methods called by Step Definition implementations
5. Disposed when AppiumDriver quits (end of scenario)

**Validation Rules**:
- Every Screen Object MUST have an AppiumDriver field
- Every interactive element MUST have BOTH @AndroidFindBy AND @iOSXCUITFindBy annotations
- Android locators MUST use resourceId with testTag values
- iOS locators MUST use accessibilityIdentifier values
- Methods MUST represent user actions, not technical Appium operations

---

### 4. Step Definition (Cucumber Glue Code)

**Purpose**: Bridges Gherkin natural language steps to Page/Screen Object method calls

**Structure**:
- Page/Screen Object instances (injected or instantiated)
- Methods annotated with @Given, @When, @Then matching Gherkin steps
- Cucumber expressions or regex for parameter extraction
- Assertions for Then steps (verification)

**Attributes**:
| Attribute | Type | Required | Description |
|-----------|------|----------|-------------|
| page_objects | Map<String, PageObject> | For web steps | Page Objects used by this step definition |
| screen_objects | Map<String, ScreenObject> | For mobile steps | Screen Objects used by this step definition |
| test_context | TestContext | Optional | Shared state between steps (scenario-scoped) |

**Example (Web Steps)**:
```java
public class PetListWebSteps {
    private WebDriver driver;
    private PetListPage petListPage;
    
    public PetListWebSteps() {
        this.driver = WebDriverManager.getDriver();
        this.petListPage = new PetListPage(driver);
    }
    
    @Given("I am on the pet list page")
    public void navigateToPetListPage() {
        driver.get("http://localhost:3000/pets");
    }
    
    @When("the page loads")
    public void waitForPageLoad() {
        petListPage.waitForPetListVisible(10);
    }
    
    @Then("I should see the list of pets")
    public void verifyPetListDisplayed() {
        assertTrue(petListPage.isPetListDisplayed(), 
            "Pet list should be displayed");
        assertTrue(petListPage.getPetCount() > 0, 
            "Pet list should contain at least one pet");
    }
    
    @When("I search for {string}")
    public void searchForSpecies(String species) {
        petListPage.searchForPet(species);
    }
    
    @Then("I should see only {string} pets")
    public void verifyFilteredPets(String species) {
        List<WebElement> pets = petListPage.getVisiblePets();
        for (WebElement pet : pets) {
            String petSpecies = pet.findElement(
                By.xpath(".//*[@data-testid$='.species']")
            ).getText();
            assertEquals(species, petSpecies.toLowerCase(),
                "All visible pets should be " + species);
        }
    }
}
```

**Example (Mobile Steps)**:
```java
public class PetListMobileSteps {
    private AppiumDriver driver;
    private PetListScreen petListScreen;
    
    public PetListMobileSteps() {
        this.driver = AppiumDriverManager.getDriver();
        this.petListScreen = new PetListScreen(driver);
    }
    
    @Given("I am on the pet list screen")
    public void navigateToPetListScreen() {
        // App should launch to pet list by default
        petListScreen.waitForScreenVisible(10);
    }
    
    @When("the screen loads")
    public void waitForScreenLoad() {
        petListScreen.waitForPetListVisible(10);
    }
    
    @Then("I should see the list of pets")
    public void verifyPetListDisplayed() {
        assertTrue(petListScreen.isPetListDisplayed(),
            "Pet list should be displayed");
        assertTrue(petListScreen.getPetCount() > 0,
            "Pet list should contain at least one pet");
    }
    
    @When("I search for {string}")
    public void searchForSpecies(String species) {
        petListScreen.searchForPet(species);
    }
}
```

**Relationships**:
- Consumes Feature File steps (matches via Cucumber expressions)
- Instantiates and uses Page/Screen Objects
- Uses WebDriver/AppiumDriver from manager classes
- Reports results to Cucumber framework

**Lifecycle**:
1. Instantiated once per scenario by Cucumber framework
2. Methods called in order matching Gherkin step sequence
3. State can be shared between steps within same scenario
4. Disposed after scenario completes

**Validation Rules**:
- Every step MUST be annotated with @Given, @When, or @Then
- Step definition method MUST match exactly one Gherkin step pattern
- Given steps MUST set up state (no assertions)
- When steps MUST perform actions (no assertions)
- Then steps MUST verify outcomes (assertions required)
- NO business logic duplication - delegate to Page/Screen Objects

---

### 5. Driver Manager (Utility)

**Purpose**: Manages WebDriver/AppiumDriver lifecycle with ThreadLocal isolation for parallel execution

**Structure**:
- ThreadLocal storage for driver instances
- Driver initialization methods (web/Android/iOS)
- Driver cleanup methods
- Configuration loading from properties

**Attributes**:
| Attribute | Type | Required | Description |
|-----------|------|----------|-------------|
| driver_type | Enum (WEB, ANDROID, IOS) | Yes | Type of driver to manage |
| capabilities | Map<String, Object> | Yes | Driver-specific capabilities |
| implicit_wait | int | No | Default implicit wait timeout (seconds) |

**Example (WebDriverManager)**:
```java
public class WebDriverManager {
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final int DEFAULT_TIMEOUT = 10;
    
    public static WebDriver getDriver() {
        if (driver.get() == null) {
            initializeDriver();
        }
        return driver.get();
    }
    
    private static void initializeDriver() {
        io.github.bonigarcia.wdm.WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        
        WebDriver webDriver = new ChromeDriver(options);
        webDriver.manage().timeouts().implicitlyWait(
            Duration.ofSeconds(DEFAULT_TIMEOUT)
        );
        
        driver.set(webDriver);
    }
    
    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}
```

**Example (AppiumDriverManager)**:
```java
public class AppiumDriverManager {
    private static ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();
    private static final String APPIUM_SERVER_URL = "http://127.0.0.1:4723";
    
    public static AppiumDriver getDriver(String platform) {
        if (driver.get() == null) {
            initializeDriver(platform);
        }
        return driver.get();
    }
    
    private static void initializeDriver(String platform) {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        
        if (platform.equalsIgnoreCase("Android")) {
            capabilities.setCapability("platformName", "Android");
            capabilities.setCapability("platformVersion", "14");
            capabilities.setCapability("deviceName", "Android Emulator");
            capabilities.setCapability("automationName", "UiAutomator2");
            capabilities.setCapability("app", System.getProperty("user.dir") + 
                "/apps/petspot-android.apk");
        } else if (platform.equalsIgnoreCase("iOS")) {
            capabilities.setCapability("platformName", "iOS");
            capabilities.setCapability("platformVersion", "17.0");
            capabilities.setCapability("deviceName", "iPhone 15");
            capabilities.setCapability("automationName", "XCUITest");
            capabilities.setCapability("app", System.getProperty("user.dir") + 
                "/apps/petspot-ios.app");
        }
        
        try {
            AppiumDriver appiumDriver = new AppiumDriver(
                new URL(APPIUM_SERVER_URL), capabilities
            );
            driver.set(appiumDriver);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Appium server URL", e);
        }
    }
    
    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}
```

**Relationships**:
- Used by Step Definitions to obtain driver instances
- Consumed by Page/Screen Object constructors
- Cleaned up by Cucumber hooks (@After)

**Lifecycle**:
1. First access initializes driver for current thread
2. Driver reused for all steps in same scenario
3. Quit called by @After hook at end of scenario
4. ThreadLocal cleared to prevent memory leaks

**Validation Rules**:
- Driver MUST be initialized before first use
- Driver MUST be quit after scenario completes
- ThreadLocal MUST be cleared after driver quit
- Only one driver instance per thread at any time

---

## Entity Relationships Diagram

```text
┌─────────────────┐
│  Feature File   │
│   (.feature)    │
└────────┬────────┘
         │ contains
         ▼
┌─────────────────┐
│    Scenario     │
│   (Gherkin)     │
└────────┬────────┘
         │ matched by
         ▼
┌─────────────────┐         uses         ┌──────────────────┐
│ Step Definition │────────────────────▶│  Page Object     │
│   (@Given,      │                     │  (Web)           │
│    @When,       │                     └──────────────────┘
│    @Then)       │
└────────┬────────┘         uses         ┌──────────────────┐
         │                               │  Screen Object   │
         └───────────────────────────────▶│  (Mobile)        │
         │                               └──────────────────┘
         │ obtains driver from
         ▼
┌─────────────────────────┐
│   Driver Manager        │
│ (WebDriverManager /     │
│  AppiumDriverManager)   │
└─────────────────────────┘
         │ provides
         ▼
┌─────────────────────────┐
│   WebDriver /           │
│   AppiumDriver          │
│ (Selenium / Appium API) │
└─────────────────────────┘
```

## Summary

The E2E test infrastructure follows a layered architecture:

1. **Presentation Layer**: Feature Files (Gherkin) - Human-readable test scenarios
2. **Glue Layer**: Step Definitions - Cucumber annotations bridging Gherkin to code
3. **Abstraction Layer**: Page/Screen Objects - Encapsulate UI structure and interactions
4. **Infrastructure Layer**: Driver Managers - Handle WebDriver/AppiumDriver lifecycle
5. **Execution Layer**: Cucumber Runner + Maven Surefire - Execute tests and generate reports

This separation ensures:
- Test scenarios remain readable and maintainable by non-programmers
- UI changes only affect Page/Screen Objects (not step definitions)
- Driver management is centralized and thread-safe
- Tests can execute in parallel without driver collisions
- Platform-specific logic is isolated (web vs. mobile)

