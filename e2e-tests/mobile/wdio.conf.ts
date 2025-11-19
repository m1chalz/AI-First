import type { Options } from '@wdio/types';

/**
 * WebdriverIO configuration for PetSpot mobile E2E tests (Appium).
 * @see https://webdriver.io/docs/configurationfile
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
    './specs/**/*.spec.ts'
  ],
  
  // Patterns to exclude
  exclude: [],
  
  //
  // ============
  // Capabilities
  // ============
  maxInstances: 1,
  
  capabilities: [{
    // Android capabilities
    platformName: 'Android',
    'appium:deviceName': 'Android Emulator',
    'appium:platformVersion': '14',
    'appium:automationName': 'UiAutomator2',
    'appium:app': '../../composeApp/build/outputs/apk/debug/composeApp-debug.apk',
    'appium:appWaitActivity': 'com.intive.aifirst.petspot.MainActivity',
    'appium:newCommandTimeout': 240,
  }],
  
  //
  // ===================
  // Test Configurations
  // ===================
  logLevel: 'info',
  bail: 0,
  waitforTimeout: 10000,
  connectionRetryTimeout: 120000,
  connectionRetryCount: 3,
  
  //
  // Test runner services
  // Services take over a specific job you don't want to take care of. They enhance
  // your test setup with almost no effort.
  services: ['appium'],
  
  // Framework you want to run your specs with
  framework: 'mocha',
  
  //
  // Test reporter for stdout
  reporters: ['spec'],
  
  //
  // Options to be passed to Mocha
  mochaOpts: {
    ui: 'bdd',
    timeout: 60000
  },
  
  //
  // =====
  // Hooks
  // =====
  /**
   * Gets executed before test execution begins. At this point you can access all global
   * variables, such as `browser`. It is the perfect place to define custom commands.
   */
  before: function () {
    // Set implicit wait
    browser.setTimeout({ 'implicit': 10000 });
  },
};

/**
 * iOS-specific configuration override.
 * Run with: npm run test:mobile:ios
 */
export const iosConfig: Options.Testrunner = {
  ...config,
  capabilities: [{
    platformName: 'iOS',
    'appium:deviceName': 'iPhone 15',
    'appium:platformVersion': '17.0',
    'appium:automationName': 'XCUITest',
    'appium:app': '../../iosApp/build/Build/Products/Debug-iphonesimulator/iosApp.app',
    'appium:newCommandTimeout': 240,
  }],
};

