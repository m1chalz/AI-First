import type { Options } from '@wdio/types';
import path from 'path';

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
  // =====================
  // TypeScript Compilation
  // =====================
  autoCompileOpts: {
    autoCompile: true,
    tsNodeOpts: {
      transpileOnly: true,
      project: './tsconfig.json'
    }
  },
  
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
  
  // Filter capabilities based on PLATFORM env variable
  capabilities: (() => {
    const platform = process.env.PLATFORM?.toLowerCase();
    
    const androidConfig = {
      platformName: 'Android' as const,
      'appium:deviceName': 'Android Emulator',
      // Empty string = auto-detect Android version from connected device/emulator
      'appium:platformVersion': '',
      'appium:automationName': 'UiAutomator2',
      // Path to your app
      'appium:app': path.join(process.cwd(), '../composeApp/build/outputs/apk/debug/composeApp-debug.apk'),
      'appium:appPackage': 'com.intive.aifirst.petspot',
      'appium:appActivity': '.MainActivity',
      'appium:noReset': false,
      'appium:fullReset': false,
      'appium:newCommandTimeout': 240,
      // Increase timeout for UIAutomator2 initialization on first run
      'appium:uiautomator2ServerInstallTimeout': 60000,
    };
    
    const iosConfig = {
      platformName: 'iOS' as const,
      'appium:deviceName': 'iPhone 16',
      'appium:platformVersion': '18.1',
      'appium:automationName': 'XCUITest',
      // Path to your app (update this when you have the app)
      // 'appium:app': path.join(process.cwd(), '../iosApp/build/Release-iphonesimulator/iosApp.app'),
      'appium:bundleId': 'com.intive.aifirst.petspot.PetSpot',
      'appium:noReset': false,
      'appium:fullReset': false,
      'appium:newCommandTimeout': 240,
    };
    
    // Return only the specified platform, or all if not specified
    if (platform === 'android') return [androidConfig];
    if (platform === 'ios') return [iosConfig];
    return [androidConfig, iosConfig]; // Default: both platforms
  })(),
  
  //
  // ===================
  // Test Configurations
  // ===================
  logLevel: 'info',
  bail: 0,
  baseUrl: 'http://localhost',
  waitforTimeout: 3000,
  connectionRetryTimeout: 120000,
  connectionRetryCount: 3,
  
  //
  // Test runner services
  // NOTE: Appium service is commented out due to issues with auto-start.
  // Instead, manually start Appium server before running tests:
  //   cd e2e-tests && ANDROID_HOME=$HOME/Library/Android/sdk npx appium
  // Or use the helper script: npm run appium:start (from e2e-tests directory)
  services: [
    // ['appium', {
    //   args: {
    //     address: 'localhost',
    //     port: 4723,
    //     relaxedSecurity: true,
    //   },
    //   logPath: './mobile/logs/',
    //   env: {
    //     ...process.env,
    //     ANDROID_HOME: process.env.ANDROID_HOME || `${process.env.HOME}/Library/Android/sdk`,
    //   },
    // }]
  ],
  
  // Connection configuration for externally-started Appium server
  hostname: 'localhost',
  port: 4723,
  path: '/',
  
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
   * Gets executed before each test (in Mocha/Jasmine).
   */
  beforeTest: async function (test, context) {
    // Reset app state between tests to ensure test isolation
    // Terminate and relaunch app to start fresh on home screen
    const bundleId = 'com.intive.aifirst.petspot.PetSpot';
    try {
      await driver.execute('mobile: terminateApp', { bundleId });
      await driver.pause(500); // Brief pause for clean termination
      await driver.execute('mobile: launchApp', { bundleId });
      await driver.pause(1500); // Wait for app to fully launch and load animal list
    } catch (error) {
      console.warn('Failed to reset app between tests:', error);
    }
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

