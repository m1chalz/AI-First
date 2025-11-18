import { getElementByTestId } from './urlSteps';

/**
 * Touch/tap interaction step definitions for mobile.
 * Handle tap, swipe, and other touch gestures.
 */

/**
 * Click (tap) an element identified by test ID.
 * @param driver - WebdriverIO driver instance
 * @param testId - Accessibility identifier value
 */
export async function clickElement(driver: WebdriverIO.Browser, testId: string): Promise<void> {
  const selector = getElementByTestId(testId);
  const element = await driver.$(selector);
  await element.click();
}

