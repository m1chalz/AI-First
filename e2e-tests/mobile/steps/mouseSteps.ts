import type { ChainablePromiseElement } from 'webdriverio';

export async function clickElement(element: ChainablePromiseElement<WebdriverIO.Element>): Promise<void> {
  await element.click();
}

