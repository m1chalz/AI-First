import { Page, Locator } from '@playwright/test';

export async function getElementText(page: Page, testId: string): Promise<string> {
  return await page.getByTestId(testId).textContent() || '';
}

export async function fillInput(page: Page, testId: string, text: string): Promise<void> {
  await page.getByTestId(testId).fill(text);
}

export async function waitForElement(page: Page, testId: string): Promise<void> {
  await page.getByTestId(testId).waitFor({ state: 'visible' });
}

export async function scrollToElement(locator: Locator): Promise<void> {
  await locator.scrollIntoViewIfNeeded();
}

