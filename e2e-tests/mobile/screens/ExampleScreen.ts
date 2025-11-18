import { clickElement, fillInput, getElementText, waitForElement, isElementDisplayed } from '../steps/commonSteps';

/**
 * Screen Object Model for Example screen.
 * Demonstrates SOM pattern with test identifiers (testTag) and reusable step definitions.
 */
export class ExampleScreen {
  private driver: WebdriverIO.Browser;

  // Test IDs using pattern: {screen}.{element}.{action}
  private readonly testIds = {
    title: 'example.title.display',
    input: 'example.input.text',
    submitButton: 'example.button.submit',
    result: 'example.result.display',
  };

  constructor(driver: WebdriverIO.Browser) {
    this.driver = driver;
  }

  /**
   * Wait for screen to be fully loaded.
   */
  async waitForScreenLoad(): Promise<void> {
    await waitForElement(this.driver, this.testIds.title);
  }

  /**
   * Get the screen title text.
   * @returns Title text
   */
  async getTitle(): Promise<string> {
    return await getElementText(this.driver, this.testIds.title);
  }

  /**
   * Check if title is displayed.
   * @returns True if title is visible
   */
  async isTitleDisplayed(): Promise<boolean> {
    return await isElementDisplayed(this.driver, this.testIds.title);
  }

  /**
   * Fill the input field with text.
   * @param text - Text to enter
   */
  async fillInput(text: string): Promise<void> {
    await fillInput(this.driver, this.testIds.input, text);
  }

  /**
   * Click the submit button.
   */
  async clickSubmit(): Promise<void> {
    await clickElement(this.driver, this.testIds.submitButton);
  }

  /**
   * Get the result text after submission.
   * @returns Result text
   */
  async getResult(): Promise<string> {
    await waitForElement(this.driver, this.testIds.result);
    return await getElementText(this.driver, this.testIds.result);
  }

  /**
   * Check if result is displayed.
   * @returns True if result is visible
   */
  async isResultDisplayed(): Promise<boolean> {
    return await isElementDisplayed(this.driver, this.testIds.result);
  }
}

