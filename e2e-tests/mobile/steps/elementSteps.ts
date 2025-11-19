import { getElementByTestId } from './urlSteps';

export async function getElementText(driver: WebdriverIO.Browser, testId: string): Promise<string> {
  const selector = getElementByTestId(testId);
  const element = await driver.$(selector);
  return await element.getText();
}

export async function fillInput(driver: WebdriverIO.Browser, testId: string, text: string): Promise<void> {
  const selector = getElementByTestId(testId);
  const element = await driver.$(selector);
  await element.setValue(text);
}

export async function isElementDisplayed(driver: WebdriverIO.Browser, testId: string): Promise<boolean> {
  const selector = getElementByTestId(testId);
  const element = await driver.$(selector);
  return await element.isDisplayed();
}

