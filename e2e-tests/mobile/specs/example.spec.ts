import { ExampleScreen } from '../screens/ExampleScreen';

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
    await exampleScreen.waitForScreenLoad();

    // When - Screen loads
    const isTitleVisible = await exampleScreen.isTitleDisplayed();
    const title = await exampleScreen.getTitle();

    // Then - Welcome message is displayed
    expect(isTitleVisible).toBe(true);
    expect(title).toBeTruthy();
  });

  it('should submit form and display result', async () => {
    // Given - User is on the example screen with loaded form
    await exampleScreen.waitForScreenLoad();

    // When - User fills input and submits form
    const testInput = 'Hello PetSpot Mobile!';
    await exampleScreen.fillInput(testInput);
    await exampleScreen.clickSubmit();

    // Then - Result is displayed with correct content
    const result = await exampleScreen.getResult();
    expect(result).toContain(testInput);
  });

  it('should handle empty input submission', async () => {
    // Given - User is on the example screen
    await exampleScreen.waitForScreenLoad();

    // When - User submits empty form
    await exampleScreen.clickSubmit();

    // Then - Appropriate validation or default message is shown
    const isResultDisplayed = await exampleScreen.isResultDisplayed();
    expect(isResultDisplayed).toBe(true);
    
    const result = await exampleScreen.getResult();
    expect(result).toBeDefined();
  });

  it('should handle platform-specific behavior', async () => {
    // Given - User is on the example screen
    await exampleScreen.waitForScreenLoad();

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

