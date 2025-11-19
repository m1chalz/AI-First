export class ExampleScreen {
  private driver: WebdriverIO.Browser;

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

