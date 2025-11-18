import { Page, Locator } from '@playwright/test';
import { navigateTo, clickElement, fillInput, getElementText } from '../steps/commonSteps';

/**
 * Page Object Model for Example page.
 * Demonstrates POM pattern with test identifiers and reusable step definitions.
 */
export class ExamplePage {
  readonly page: Page;
  
  // Locators using data-testid pattern: {screen}.{element}.{action}
  readonly titleLocator: Locator;
  readonly inputLocator: Locator;
  readonly submitButtonLocator: Locator;
  readonly resultLocator: Locator;

  constructor(page: Page) {
    this.page = page;
    this.titleLocator = page.getByTestId('example.title.display');
    this.inputLocator = page.getByTestId('example.input.text');
    this.submitButtonLocator = page.getByTestId('example.button.submit');
    this.resultLocator = page.getByTestId('example.result.display');
  }

  /**
   * Navigate to the example page.
   */
  async navigate(): Promise<void> {
    await navigateTo(this.page, '/');
  }

  /**
   * Get the page title text.
   * @returns Title text
   */
  async getTitle(): Promise<string> {
    return await getElementText(this.page, 'example.title.display');
  }

  /**
   * Fill the input field with text.
   * @param text - Text to enter
   */
  async fillInput(text: string): Promise<void> {
    await fillInput(this.page, 'example.input.text', text);
  }

  /**
   * Click the submit button.
   */
  async clickSubmit(): Promise<void> {
    await clickElement(this.page, 'example.button.submit');
  }

  /**
   * Get the result text after submission.
   * @returns Result text
   */
  async getResult(): Promise<string> {
    return await getElementText(this.page, 'example.result.display');
  }

  /**
   * Wait for page to be fully loaded.
   */
  async waitForPageLoad(): Promise<void> {
    await this.titleLocator.waitFor({ state: 'visible' });
  }
}

