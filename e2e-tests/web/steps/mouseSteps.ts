import { Locator } from '@playwright/test';

export async function clickElement(locator: Locator): Promise<void> {
  await locator.click();
}

