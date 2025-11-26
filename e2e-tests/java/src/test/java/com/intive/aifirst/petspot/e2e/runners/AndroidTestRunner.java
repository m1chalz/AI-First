package com.intive.aifirst.petspot.e2e.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * JUnit Platform Suite for running Android E2E tests with Cucumber.
 * 
 * <p>This test runner configures Cucumber to execute only Android-tagged scenarios:
 * <ul>
 *   <li>Filter: {@code @android} tag (executes only Android scenarios)</li>
 *   <li>Features: {@code features/mobile/*.feature} files</li>
 *   <li>Step Definitions: {@code steps.mobile} package</li>
 *   <li>Hooks: {@code utils.Hooks} for lifecycle management</li>
 * </ul>
 * 
 * <h2>Prerequisites:</h2>
 * <ul>
 *   <li>Android Emulator running (Android 14, API 34)</li>
 *   <li>Appium server running on http://127.0.0.1:4723</li>
 *   <li>App APK available at {@code /apps/petspot-android.apk}</li>
 * </ul>
 * 
 * <h2>How to Run:</h2>
 * <pre>
 * # Start Android Emulator
 * emulator -avd Android_14_Emulator
 * 
 * # Start Appium server
 * cd e2e-tests && npm run appium:start
 * 
 * # Run Android tests
 * cd e2e-tests/java
 * mvn test -Dtest=AndroidTestRunner
 * 
 * # OR use Cucumber tag filtering
 * mvn test -Dcucumber.filter.tags="@android"
 * 
 * # Run specific tag combination
 * mvn test -Dcucumber.filter.tags="@android and @smoke"
 * </pre>
 * 
 * <h2>Reports Generated:</h2>
 * <ul>
 *   <li>HTML Report: {@code target/cucumber-reports/android/cucumber.html}</li>
 *   <li>JSON Report: {@code target/cucumber-android.json}</li>
 *   <li>JUnit XML: {@code target/cucumber-android.xml}</li>
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
@SelectClasspathResource("features/mobile")
@ConfigurationParameter(
    key = FILTER_TAGS_PROPERTY_NAME,
    value = "@android"
)
@ConfigurationParameter(
    key = GLUE_PROPERTY_NAME,
    value = "com.intive.aifirst.petspot.e2e.utils," +
            "com.intive.aifirst.petspot.e2e.steps.mobile"
)
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "pretty," +
            "html:target/cucumber-reports/android/cucumber.html," +
            "json:target/cucumber-android.json," +
            "junit:target/cucumber-android.xml"
)
public class AndroidTestRunner {
    // No implementation needed - JUnit Platform Suite handles execution
    // This class serves as a configuration entry point for Cucumber tests
    
    // Platform is set via environment variable in hooks:
    // System.setProperty("PLATFORM", "Android");
}

