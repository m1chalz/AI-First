# Migration Guide: TypeScript E2E Tests → Java/Maven/Cucumber

**Feature**: 016-e2e-java-migration  
**Date**: 2025-11-26  
**Status**: ACTIVE - Dual test stack coexistence enabled

## Table of Contents

1. [Overview](#overview)
2. [When to Migrate](#when-to-migrate)
3. [Web Tests Migration (Playwright → Selenium)](#web-tests-migration)
4. [Mobile Tests Migration (Appium+WebdriverIO → Appium+Cucumber)](#mobile-tests-migration)
5. [Common Pitfalls](#common-pitfalls)
6. [Troubleshooting](#troubleshooting)

---

## Overview

This guide helps you migrate existing TypeScript E2E tests to the new Java/Maven/Cucumber stack. **Migration is optional and gradual** - both test stacks coexist and you can choose when to migrate based on your feature branch needs.

**No Forced Timeline**: TypeScript tests remain functional until all active branches organically migrate to Java.

---

## When to Migrate

Use this decision framework to choose between TypeScript and Java for your E2E tests:

| Situation | Recommended Stack | Why |
|-----------|------------------|-----|
| **Writing E2E test for NEW feature** | Java/Cucumber | Fresh start, learn new stack without migration burden |
| **Updating existing E2E test** | TypeScript (current) OR Java (migrate) | Your choice - quick fix or migration opportunity |
| **Bug fix in existing E2E test** | TypeScript (current) | Quick fix in familiar stack, migrate later if desired |
| **Large E2E test refactor** | Java/Cucumber | Good opportunity to migrate while restructuring |
| **Working on feature branch with TypeScript tests** | TypeScript | No pressure - focus on feature development |

---

## Web Tests Migration

### Playwright → Selenium + Cucumber

#### Step 1: Convert Test Scenario to Gherkin

**TypeScript (Playwright)**:
```typescript
// web/specs/pet-list.spec.ts
import { test, expect } from '@playwright/test';

test('view pet list', async ({ page }) => {
  await page.goto('http://localhost:3000/pets');
  await expect(page.locator('[data-testid="petList.list"]')).toBeVisible();
  
  const pets = page.locator('[data-testid^="petList.item."]');
  await expect(pets).toHaveCountGreaterThan(0);
});
```

**Java (Cucumber Gherkin)**:
```gherkin
# e2e-tests/java/src/test/resources/features/web/pet-list.feature
@web
Feature: Pet List Management (Web)
  As a user browsing the PetSpot web application
  I want to view pet announcements
  
  @smoke
  Scenario: View pet list on web
    Given I am on the pet list page
    And the page has loaded completely
    When I view the pet list
    Then I should see at least one pet announcement
```

#### Step 2: Convert Page Object

**TypeScript (Playwright POM)**:
```typescript
// web/pages/PetListPage.ts
export class PetListPage {
  constructor(private page: Page) {}
  
  async goto() {
    await this.page.goto('http://localhost:3000/pets');
  }
  
  async isPetListVisible() {
    return await this.page.locator('[data-testid="petList.list"]').isVisible();
  }
  
  async getPetCount() {
    return await this.page.locator('[data-testid^="petList.item."]').count();
  }
}
```

**Java (Selenium POM)**:
```java
// e2e-tests/java/src/test/java/.../pages/PetListPage.java
@FindBy(xpath = "//*[@data-testid='petList.list']")
private WebElement petList;

public boolean isPetListDisplayed() {
    try {
        return petList.isDisplayed();
    } catch (Exception e) {
        return false;
    }
}

public int getPetCount() {
    return driver.findElements(
        By.xpath("//*[starts-with(@data-testid, 'petList.item.')]")
    ).size();
}
```

#### Step 3: Implement Step Definitions

**Java (Cucumber Steps)**:
```java
// e2e-tests/java/src/test/java/.../steps/web/PetListWebSteps.java
@Given("I am on the pet list page")
public void navigateToPetListPage() {
    String baseUrl = TestConfig.getWebBaseUrl();
    driver.get(baseUrl + "/pets");
}

@Then("I should see at least one pet announcement")
public void shouldSeeAtLeastOnePet() {
    assertTrue(petListPage.isPetListDisplayed());
    assertTrue(petListPage.getPetCount() > 0);
}
```

#### Step 4: Run the Test

**TypeScript**:
```bash
npm run test:web
```

**Java**:
```bash
cd e2e-tests/java
mvn test -Dtest=WebTestRunner
```

---

## Mobile Tests Migration

### Appium+WebdriverIO → Appium+Cucumber

#### Step 1: Convert Test Scenario to Gherkin with Platform Tags

**TypeScript (WebdriverIO)**:
```typescript
// mobile/specs/pet-list.spec.ts
describe('Pet List', () => {
  it('should display pet list on Android', async () => {
    const petList = await $('~petList.list');
    await petList.waitForDisplayed();
    
    const pets = await $$('~petList.item.');
    expect(pets.length).toBeGreaterThan(0);
  });
});
```

**Java (Cucumber Gherkin)**:
```gherkin
# e2e-tests/java/src/test/resources/features/mobile/pet-list.feature
@mobile @android @smoke
Scenario: View pet list on Android
  Given I have launched the mobile app
  And I am on the pet list screen
  When I view the pet list
  Then I should see at least one pet announcement
```

#### Step 2: Convert Screen Object with Dual Annotations

**TypeScript (WebdriverIO Screen Object)**:
```typescript
// mobile/screens/PetListScreen.ts
export class PetListScreen {
  get petList() {
    return $('~petList.list');
  }
  
  async isPetListVisible() {
    return await this.petList.isDisplayed();
  }
  
  async getPetCount() {
    const pets = await $$('~petList.item.');
    return pets.length;
  }
}
```

**Java (Appium Screen Object with Dual Annotations)**:
```java
// e2e-tests/java/src/test/java/.../screens/PetListScreen.java
@AndroidFindBy(accessibility = "petList.list")
@iOSXCUITFindBy(accessibility = "petList.list")
private WebElement petList;

public boolean isPetListDisplayed() {
    try {
        return petList.isDisplayed();
    } catch (Exception e) {
        return false;
    }
}

public int getPetCount() {
    return driver.findElements(
        io.appium.java_client.AppiumBy.xpath(
            "//*[contains(@content-desc, 'petList.item.') or contains(@name, 'petList.item.')]"
        )
    ).size();
}
```

#### Step 3: Implement Step Definitions

**Java (Cucumber Steps - Platform Agnostic)**:
```java
// e2e-tests/java/src/test/java/.../steps/mobile/PetListMobileSteps.java
@Given("I have launched the mobile app")
public void launchMobileApp() {
    String platform = detectPlatformFromEnvironment(); // "Android" or "iOS"
    this.driver = AppiumDriverManager.getDriver(platform);
    this.petListScreen = new PetListScreen(driver);
}

@Then("I should see at least one pet announcement")
public void shouldSeeAtLeastOnePet() {
    assertTrue(petListScreen.isPetListDisplayed());
    assertTrue(petListScreen.getPetCount() > 0);
}
```

#### Step 4: Run the Tests

**TypeScript**:
```bash
npm run appium:start  # Start Appium server
npm run test:mobile:android
npm run test:mobile:ios
```

**Java**:
```bash
npm run appium:start  # Start Appium server (reuse TypeScript npm script)
cd e2e-tests/java
mvn test -Dtest=AndroidTestRunner
mvn test -Dtest=IosTestRunner
```

---

## Common Pitfalls

### 1. **Locator Strategy Differences**

**❌ TypeScript CSS Selector**:
```typescript
await page.locator('div.pet-item').click();
```

**✅ Java XPath with data-testid**:
```java
@FindBy(xpath = "//*[@data-testid='petList.item']")
```

**Why**: Java Selenium/Appium best practices use XPath with data-testid for stability.

---

### 2. **Async/Await vs Implicit Waits**

**❌ TypeScript - Async/Await Everywhere**:
```typescript
await page.locator('[data-testid="button"]').click();
```

**✅ Java - Implicit Waits + Explicit Waits**:
```java
// Implicit wait configured in driver setup (10s default)
button.click(); // No await needed

// Explicit wait for specific condition
WaitUtil.waitForElementClickable(driver, locator, 10);
```

---

### 3. **Mobile Platform Detection**

**❌ Hardcoding Platform**:
```java
AppiumDriver driver = AppiumDriverManager.getDriver("Android"); // Hardcoded
```

**✅ Dynamic Platform Detection**:
```java
String platform = detectPlatformFromEnvironment(); // From PLATFORM env var or Cucumber tags
AppiumDriver driver = AppiumDriverManager.getDriver(platform);
```

---

### 4. **Test Organization**

**TypeScript**: Tests, Page Objects, and Utils in same directory
```
web/
  specs/pet-list.spec.ts
  pages/PetListPage.ts
```

**Java**: Strict Maven directory structure
```
src/test/java/.../pages/PetListPage.java
src/test/java/.../steps/web/PetListWebSteps.java
src/test/resources/features/web/pet-list.feature
```

---

## Troubleshooting

### Migration Issue: Tests Pass in TypeScript but Fail in Java

**Symptoms**: Equivalent test works in TypeScript but fails in Java with element not found errors.

**Diagnosis**:
1. Check if implicit wait is configured (should be 10s in WebDriverManager/AppiumDriverManager)
2. Verify locator strategy matches (XPath with data-testid)
3. Add explicit waits using WaitUtil for dynamic elements

**Solution**:
```java
// Add explicit wait before interaction
WebElement element = WaitUtil.waitForElementClickable(driver, locator, 10);
element.click();
```

---

### Migration Issue: Cucumber Reports Not Generated

**Symptoms**: Tests run but no HTML report at `target/cucumber-reports/web/cucumber.html`.

**Diagnosis**:
1. Check if test runner uses correct tag (`@web`, `@android`, `@ios`)
2. Verify Maven Cucumber reporting plugin configured in pom.xml
3. Check for JSON report file (`target/cucumber-web.json`)

**Solution**:
```bash
# Run with specific test runner
mvn test -Dtest=WebTestRunner

# Verify report generation
ls -lh target/cucumber-reports/web/cucumber.html
```

---

### Migration Issue: Appium 9.x API Changes

**Symptoms**: Compilation errors like "hideKeyboard() not found" or "rotate() not found".

**Diagnosis**: Appium 9.x moved some methods to platform-specific driver classes.

**Solution**:
```java
// ❌ Old (Appium 8.x)
driver.hideKeyboard();

// ✅ New (Appium 9.x)
if (driver instanceof AndroidDriver) {
    ((AndroidDriver) driver).hideKeyboard();
} else if (driver instanceof IOSDriver) {
    ((IOSDriver) driver).hideKeyboard();
}
```

---

## Next Steps

1. **Start Small**: Migrate one simple test as a proof-of-concept
2. **Learn Patterns**: Study existing Java tests in `/e2e-tests/java/src/test/`
3. **Ask Questions**: Consult `/specs/016-e2e-java-migration/quickstart.md` for examples
4. **No Rush**: TypeScript tests continue to work - migrate when convenient

**Questions?** Check the [quickstart guide](./quickstart.md) or [comparison document](./COMPARISON.md) for more examples.

