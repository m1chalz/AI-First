import { getElementByTestId } from './urlSteps';

/**
 * Element interaction step definitions for mobile.
 * Handle reading and writing element content.
 */

/**
 * Get text content of an element.
 * @param driver - WebdriverIO driver instance
 * @param testId - Accessibility identifier value
 * @returns Text content of the element
 */
export async function getElementText(driver: WebdriverIO.Browser, testId: string): Promise<string> {
  const selector = getElementByTestId(testId);
  const element = await driver.$(selector);
  return await element.getText();
}

/**
 * Fill a text input identified by test ID.
 * @param driver - WebdriverIO driver instance
 * @param testId - Accessibility identifier value
 * @param text - Text to fill
 */
export async function fillInput(driver: WebdriverIO.Browser, testId: string, text: string): Promise<void> {
  const selector = getElementByTestId(testId);
  const element = await driver.$(selector);
  await element.setValue(text);
}

/**
 * Check if element is displayed.
 * @param driver - WebdriverIO driver instance
 * @param testId - Accessibility identifier value
 * @returns True if element is displayed
 */
export async function isElementDisplayed(driver: WebdriverIO.Browser, testId: string): Promise<boolean> {
  const selector = getElementByTestId(testId);
  const element = await driver.$(selector);
  return await element.isDisplayed();
}

