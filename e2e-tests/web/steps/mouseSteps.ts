import { Page } from '@playwright/test';

export async function clickElement(page: Page, testId: string): Promise<void> {
  await page.getByTestId(testId).click();
}

