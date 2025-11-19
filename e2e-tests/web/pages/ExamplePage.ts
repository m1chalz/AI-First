import { Page, Locator } from '@playwright/test';
import { navigateTo } from '../steps/urlSteps';

export class ExamplePage {
  readonly page: Page;
  
  readonly testIds = {
    title: 'example.title.display',
    input: 'example.input.text',
    submitButton: 'example.button.submit',
    result: 'example.result.display',
  };

  readonly titleLocator: Locator;
  readonly inputLocator: Locator;
  readonly submitButtonLocator: Locator;
  readonly resultLocator: Locator;

  constructor(page: Page) {
    this.page = page;
    this.titleLocator = page.getByTestId(this.testIds.title);
    this.inputLocator = page.getByTestId(this.testIds.input);
    this.submitButtonLocator = page.getByTestId(this.testIds.submitButton);
    this.resultLocator = page.getByTestId(this.testIds.result);
  }

  async navigate(): Promise<void> {
    await navigateTo(this.page, '/');
  }
}

