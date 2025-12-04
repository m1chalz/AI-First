package com.intive.aifirst.petspot.e2e.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * JUnit Platform Suite for running iOS E2E tests with Cucumber.
 * 
 * <p>This test runner configures Cucumber to execute only iOS-tagged scenarios:
 * <ul>
 *   <li>Filter: {@code @ios and not @pending} tag (executes only iOS scenarios)</li>
 *   <li>Features: {@code features/**/*.feature} files (both web and mobile directories)</li>
 *   <li>Step Definitions: {@code steps.mobile} package</li>
 *   <li>Hooks: {@code utils.Hooks} and {@code utils.CommonSteps} for lifecycle management</li>
 * </ul>
 * 
 * <h2>Prerequisites:</h2>
 * <ul>
 *   <li>iOS Simulator running (iOS 17.0, iPhone 15)</li>
 *   <li>Appium server running on http://127.0.0.1:4723</li>
 *   <li>App bundle available at {@code /apps/petspot-ios.app}</li>
 *   <li>Xcode 15+ installed (macOS only)</li>
 * </ul>
 * 
 * <h2>How to Run:</h2>
 * <pre>
 * # Start iOS Simulator (macOS only)
 * open -a Simulator
 * xcrun simctl boot "iPhone 15"
 * 
 * # Start Appium server
 * cd e2e-tests && npm run appium:start
 * 
 * # Run iOS tests
 * cd e2e-tests/java
 * mvn test -Dtest=IosTestRunner
 * 
 * # OR use Cucumber tag filtering
 * mvn test -Dcucumber.filter.tags="@ios"
 * 
 * # Run specific tag combination
 * mvn test -Dcucumber.filter.tags="@ios and @smoke"
 * </pre>
 * 
 * <h2>Reports Generated:</h2>
 * <ul>
 *   <li>HTML Report: {@code target/cucumber-reports/ios/cucumber.html}</li>
 *   <li>JSON Report: {@code target/cucumber-ios.json}</li>
 *   <li>JUnit XML: {@code target/cucumber-ios.xml}</li>
 *   <li>Console Output: Pretty-printed Gherkin execution log</li>
 * </ul>
 * 
 * <h2>Configuration:</h2>
 * <p>Additional configuration can be set in {@code src/test/resources/cucumber.properties}.
 * Command-line parameters override file-based configuration.
 * 
 * @see io.cucumber.junit.platform.engine.Cucumber
 * @see org.junit.platform.suite.api.Suite
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
    key = FILTER_TAGS_PROPERTY_NAME,
    value = "@ios and not @pending"
)
@ConfigurationParameter(
    key = GLUE_PROPERTY_NAME,
    value = "com.intive.aifirst.petspot.e2e.utils," +
            "com.intive.aifirst.petspot.e2e.steps.mobile"
)
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "pretty," +
            "html:target/cucumber-reports/ios/cucumber.html," +
            "json:target/cucumber-ios.json," +
            "junit:target/cucumber-ios.xml"
)
public class IosTestRunner {
    // No implementation needed - JUnit Platform Suite handles execution
    // This class serves as a configuration entry point for Cucumber tests
    
    // Platform is set via environment variable in hooks:
    // System.setProperty("PLATFORM", "iOS");
}

