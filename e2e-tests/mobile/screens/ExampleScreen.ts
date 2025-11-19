/**
 * Screen Object Model for Example screen.
 * Provides test identifiers. Use step definitions for interactions.
 */
export class ExampleScreen {
  private driver: WebdriverIO.Browser;

  // Test IDs using pattern: {screen}.{element}.{action}
  readonly testIds = {
    title: 'example.title.display',
    input: 'example.input.text',
    submitButton: 'example.button.submit',
    result: 'example.result.display',
  };

  constructor(driver: WebdriverIO.Browser) {
    this.driver = driver;
  }
}

