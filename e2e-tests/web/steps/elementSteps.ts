import { Page } from '@playwright/test';

/**
 * Element interaction step definitions.
 * Handle reading and writing element content.
 */

/**
 * Get text content of an element.
 * @param page - Playwright page object
 * @param testId - data-testid attribute value
 * @returns Text content of the element
 */
export async function getElementText(page: Page, testId: string): Promise<string> {
  return await page.getByTestId(testId).textContent() || '';
}

/**
 * Fill a text input identified by test ID.
 * @param page - Playwright page object
 * @param testId - data-testid attribute value
 * @param text - Text to fill
 */
export async function fillInput(page: Page, testId: string, text: string): Promise<void> {
  await page.getByTestId(testId).fill(text);
}

