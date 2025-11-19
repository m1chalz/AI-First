import { getElementByTestId } from './urlSteps';

export async function clickElement(driver: WebdriverIO.Browser, testId: string): Promise<void> {
  const selector = getElementByTestId(testId);
  const element = await driver.$(selector);
  await element.click();
}

