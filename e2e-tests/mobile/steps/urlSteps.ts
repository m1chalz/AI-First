/**
 * URL and navigation step definitions for mobile.
 * Handle screen navigation and loading states.
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
 * Wait for an element to be visible (indicates screen loaded).
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

