import { ExampleScreen } from '../screens/ExampleScreen';
import { waitForElement } from '../steps/urlSteps';
import { getElementText, fillInput, isElementDisplayed } from '../steps/elementSteps';
import { clickElement } from '../steps/mouseSteps';

/**
 * Example E2E test suite demonstrating:
 * - Screen Object Model pattern
 * - Given-When-Then structure
 * - Test identifier usage (testTag/accessibilityIdentifier)
 * - Reusable step definitions
 * - Cross-platform support (Android/iOS)
 */

describe('Example Feature - Mobile', () => {
  let exampleScreen: ExampleScreen;

  beforeEach(async () => {
    exampleScreen = new ExampleScreen(driver);
  });

  it('should display welcome message on screen load', async () => {
    // Given - App launches and user is on example screen
    await waitForElement(driver, exampleScreen.testIds.title);

    // When - Screen loads
    const isTitleVisible = await isElementDisplayed(driver, exampleScreen.testIds.title);
    const title = await getElementText(driver, exampleScreen.testIds.title);

    // Then - Welcome message is displayed
    expect(isTitleVisible).toBe(true);
    expect(title).toBeTruthy();
  });

  it('should submit form and display result', async () => {
    // Given - User is on the example screen with loaded form
    await waitForElement(driver, exampleScreen.testIds.title);

    // When - User fills input and submits form
    const testInput = 'Hello PetSpot Mobile!';
    await fillInput(driver, exampleScreen.testIds.input, testInput);
    await clickElement(driver, exampleScreen.testIds.submitButton);

    // Then - Result is displayed with correct content
    await waitForElement(driver, exampleScreen.testIds.result);
    const result = await getElementText(driver, exampleScreen.testIds.result);
    expect(result).toContain(testInput);
  });

  it('should handle empty input submission', async () => {
    // Given - User is on the example screen
    await waitForElement(driver, exampleScreen.testIds.title);

    // When - User submits empty form
    await clickElement(driver, exampleScreen.testIds.submitButton);

    // Then - Appropriate validation or default message is shown
    const isResultDisplayed = await isElementDisplayed(driver, exampleScreen.testIds.result);
    expect(isResultDisplayed).toBe(true);
    
    const result = await getElementText(driver, exampleScreen.testIds.result);
    expect(result).toBeDefined();
  });

  it('should handle platform-specific behavior', async () => {
    // Given - User is on the example screen
    await waitForElement(driver, exampleScreen.testIds.title);

    // When - Checking platform
    const platform = driver.capabilities.platformName;

    // Then - Platform is correctly identified
    expect(['Android', 'iOS']).toContain(platform);

    // Platform-specific assertions can be added here
    if (platform === 'Android') {
      // Android-specific test logic
      expect(driver.capabilities.automationName).toBe('UiAutomator2');
    } else if (platform === 'iOS') {
      // iOS-specific test logic
      expect(driver.capabilities.automationName).toBe('XCUITest');
    }
  });
});

