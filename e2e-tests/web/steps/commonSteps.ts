import { Page } from '@playwright/test';

/**
 * Common step definitions for web E2E tests.
 * These reusable steps follow Given-When-Then pattern.
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
 * Click an element identified by test ID.
 * @param page - Playwright page object
 * @param testId - data-testid attribute value
 */
export async function clickElement(page: Page, testId: string): Promise<void> {
  await page.getByTestId(testId).click();
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

/**
 * Wait for an element to be visible.
 * @param page - Playwright page object
 * @param testId - data-testid attribute value
 */
export async function waitForElement(page: Page, testId: string): Promise<void> {
  await page.getByTestId(testId).waitFor({ state: 'visible' });
}

/**
 * Get text content of an element.
 * @param page - Playwright page object
 * @param testId - data-testid attribute value
 * @returns Text content of the element
 */
export async function getElementText(page: Page, testId: string): Promise<string> {
  return await page.getByTestId(testId).textContent() || '';
}

