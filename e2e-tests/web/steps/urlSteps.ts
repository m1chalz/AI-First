import { Page } from '@playwright/test';

/**
 * URL and navigation step definitions.
 * Handle page navigation and loading states.
 */

/**
 * Navigate to a specific URL.
 * @param page - Playwright page object
 * @param url - URL to navigate to
 */
export async function navigateTo(page: Page, url: string): Promise<void> {
  await page.goto(url);
}

/**
 * Wait for an element to be visible (indicates page/section loaded).
 * @param page - Playwright page object
 * @param testId - data-testid attribute value
 */
export async function waitForElement(page: Page, testId: string): Promise<void> {
  await page.getByTestId(testId).waitFor({ state: 'visible' });
}

