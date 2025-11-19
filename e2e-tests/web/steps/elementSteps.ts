import { Page } from '@playwright/test';

export async function getElementText(page: Page, testId: string): Promise<string> {
  return await page.getByTestId(testId).textContent() || '';
}

export async function fillInput(page: Page, testId: string, text: string): Promise<void> {
  await page.getByTestId(testId).fill(text);
}

