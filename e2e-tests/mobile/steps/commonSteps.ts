/**
 * Common step definitions for mobile E2E tests.
 * These reusable steps follow Given-When-Then pattern.
 */

/**
 * Find element by accessibility ID (test tag).
 * @param testId - Accessibility identifier (testTag value)
 * @returns Element selector
 */
export function getElementByTestId(testId: string): string {
  return `~${testId}`;
}

/**
 * Click an element identified by test ID.
 * @param driver - WebdriverIO driver instance
 * @param testId - Accessibility identifier value
 */
export async function clickElement(driver: WebdriverIO.Browser, testId: string): Promise<void> {
  const selector = getElementByTestId(testId);
  const element = await driver.$(selector);
  await element.click();
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
 * Wait for an element to be visible.
 * @param driver - WebdriverIO driver instance
 * @param testId - Accessibility identifier value
 * @param timeout - Optional timeout in milliseconds (default: 5000)
 */
export async function waitForElement(
  driver: WebdriverIO.Browser,
  testId: string,
  timeout: number = 5000
): Promise<void> {
  const selector = getElementByTestId(testId);
  const element = await driver.$(selector);
  await element.waitForDisplayed({ timeout });
}

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

