export function getElementByTestId(testId: string): string {
  return `~${testId}`;
}

export async function waitForElement(
  driver: WebdriverIO.Browser,
  testId: string,
  timeout: number = 5000
): Promise<void> {
  const selector = getElementByTestId(testId);
  const element = await driver.$(selector);
  await element.waitForDisplayed({ timeout });
}

