import type { Options } from '@wdio/types';

/**
 * WebdriverIO configuration for PetSpot E2E mobile tests.
 * See https://webdriver.io/docs/configurationfile
 */
export const config: Options.Testrunner = {
  //
  // ====================
  // Runner Configuration
  // ====================
  runner: 'local',
  
  //
  // ==================
  // Specify Test Files
  // ==================
  specs: [
    './mobile/specs/**/*.spec.ts'
  ],
  
  // Patterns to exclude.
  exclude: [
    // 'path/to/excluded/files'
  ],
  
  //
  // ============
  // Capabilities
  // ============
  maxInstances: 1,
  
  capabilities: [{
    platformName: 'Android',
    'appium:deviceName': 'Android Emulator',
    'appium:platformVersion': '13.0',
    'appium:automationName': 'UiAutomator2',
    // Path to your app (update this when you have the app)
    // 'appium:app': path.join(process.cwd(), '../composeApp/build/outputs/apk/debug/composeApp-debug.apk'),
    'appium:appPackage': 'com.intive.aifirst.petspot',
    'appium:appActivity': '.MainActivity',
    'appium:noReset': false,
    'appium:fullReset': false,
    'appium:newCommandTimeout': 240,
  }],
  
  //
  // ===================
  // Test Configurations
  // ===================
  logLevel: 'info',
  bail: 0,
  baseUrl: 'http://localhost',
  waitforTimeout: 10000,
  connectionRetryTimeout: 120000,
  connectionRetryCount: 3,
  
  //
  // Test runner services
  // Services take over a specific job you don't want to take care of. They enhance
  // your test setup with almost no effort. Unlike plugins, they don't add new
  // commands. Instead, they hook themselves up into the test process.
  services: [
    ['appium', {
      // Appium service options
      args: {
        // Appium server arguments
        address: 'localhost',
        port: 4723,
      },
      logPath: './mobile/logs/',
    }]
  ],
  
  //
  // Framework you want to run your specs with.
  framework: 'mocha',
  
  //
  // Test reporter for stdout.
  reporters: [
    'spec',
    ['junit', {
      outputDir: './mobile/reports/junit',
      outputFileFormat: (options) => {
        return `results-${options.cid}.xml`;
      }
    }]
  ],
  
  //
  // Options to be passed to Mocha.
  mochaOpts: {
    ui: 'bdd',
    timeout: 60000
  },
  
  //
  // =====
  // Hooks
  // =====
  /**
   * Gets executed once before all workers get launched.
   */
  // onPrepare: function (config, capabilities) {
  // },
  
  /**
   * Gets executed before a worker process is spawned and can be used to initialize specific service
   * for that worker as well as modify runtime environments in an async fashion.
   */
  // onWorkerStart: function (cid, caps, specs, args, execArgv) {
  // },
  
  /**
   * Gets executed just after a worker process has exited.
   */
  // onWorkerEnd: function (cid, exitCode, specs, retries) {
  // },
  
  /**
   * Gets executed just before initialising the webdriver session and test framework. It allows you
   * to manipulate configurations depending on the capability or spec.
   */
  // beforeSession: function (config, capabilities, specs) {
  // },
  
  /**
   * Gets executed before test execution begins. At this point you can access to all global
   * variables like `browser`. It is the perfect place to define custom commands.
   */
  before: function (capabilities, specs) {
    // Set implicit wait
    // driver.setImplicitTimeout(5000);
  },
  
  /**
   * Runs before a WebdriverIO command gets executed.
   */
  // beforeCommand: function (commandName, args) {
  // },
  
  /**
   * Runs after a WebdriverIO command gets executed
   */
  // afterCommand: function (commandName, args, result, error) {
  // },
  
  /**
   * Gets executed after all tests are done. You still have access to all global variables from
   * the test.
   */
  // after: function (result, capabilities, specs) {
  // },
  
  /**
   * Gets executed right after terminating the webdriver session.
   */
  // afterSession: function (config, capabilities, specs) {
  // },
  
  /**
   * Gets executed after all workers got shut down and the process is about to exit. An error
   * thrown in the onComplete hook will result in the test run failing.
   */
  // onComplete: function(exitCode, config, capabilities, results) {
  // },
  
  /**
  * Gets executed when a refresh happens.
  */
  // onReload: function(oldSessionId, newSessionId) {
  // }
};

