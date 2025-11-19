import { ExampleScreen } from '../screens/ExampleScreen';
import { waitForElement } from '../steps/urlSteps';
import { getElementText, fillInput, isElementDisplayed } from '../steps/elementSteps';
import { clickElement } from '../steps/mouseSteps';

describe('Example Feature - Mobile', () => {
  let exampleScreen: ExampleScreen;

  beforeEach(async () => {
    exampleScreen = new ExampleScreen(driver);
  });

  it('should display welcome message on screen load', async () => {
    // Given
    await waitForElement(driver, exampleScreen.testIds.title);

    // When
    const isTitleVisible = await isElementDisplayed(driver, exampleScreen.testIds.title);
    const title = await getElementText(driver, exampleScreen.testIds.title);

    // Then
    expect(isTitleVisible).toBe(true);
    expect(title).toBeTruthy();
  });

  it('should submit form and display result', async () => {
    // Given
    await waitForElement(driver, exampleScreen.testIds.title);

    // When
    const testInput = 'Hello PetSpot Mobile!';
    await fillInput(driver, exampleScreen.testIds.input, testInput);
    await clickElement(driver, exampleScreen.testIds.submitButton);

    // Then
    await waitForElement(driver, exampleScreen.testIds.result);
    const result = await getElementText(driver, exampleScreen.testIds.result);
    expect(result).toContain(testInput);
  });

  it('should handle empty input submission', async () => {
    // Given
    await waitForElement(driver, exampleScreen.testIds.title);

    // When
    await clickElement(driver, exampleScreen.testIds.submitButton);

    // Then
    const isResultDisplayed = await isElementDisplayed(driver, exampleScreen.testIds.result);
    expect(isResultDisplayed).toBe(true);
    
    const result = await getElementText(driver, exampleScreen.testIds.result);
    expect(result).toBeDefined();
  });

  it('should handle platform-specific behavior', async () => {
    // Given
    await waitForElement(driver, exampleScreen.testIds.title);

    // When
    const platform = driver.capabilities.platformName;

    // Then
    expect(['Android', 'iOS']).toContain(platform);

    if (platform === 'Android') {
      expect(driver.capabilities.automationName).toBe('UiAutomator2');
    } else if (platform === 'iOS') {
      expect(driver.capabilities.automationName).toBe('XCUITest');
    }
  });
});

