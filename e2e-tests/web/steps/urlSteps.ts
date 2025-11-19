import { Page } from '@playwright/test';

export async function navigateTo(page: Page, url: string): Promise<void> {
  await page.goto(url);
}

export async function waitForElement(page: Page, testId: string): Promise<void> {
  await page.getByTestId(testId).waitFor({ state: 'visible' });
}

