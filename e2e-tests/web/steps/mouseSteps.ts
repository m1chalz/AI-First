import { Page } from '@playwright/test';

/**
 * Mouse interaction step definitions.
 * Handle click, hover, and other mouse actions.
 */

/**
 * Click an element identified by test ID.
 * @param page - Playwright page object
 * @param testId - data-testid attribute value
 */
export async function clickElement(page: Page, testId: string): Promise<void> {
  await page.getByTestId(testId).click();
}

