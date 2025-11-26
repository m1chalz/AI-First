package com.intive.aifirst.petspot.e2e.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * JUnit Platform Suite for running web E2E tests with Cucumber.
 * 
 * <p>This test runner configures Cucumber to execute only web-tagged scenarios:
 * <ul>
 *   <li>Filter: {@code @web} tag (executes only web scenarios)</li>
 *   <li>Features: {@code features/web/*.feature} files</li>
 *   <li>Step Definitions: {@code steps.web} package</li>
 *   <li>Hooks: {@code utils.Hooks} for lifecycle management</li>
 * </ul>
 * 
 * <h2>How to Run:</h2>
 * <pre>
 * # Run all web tests
 * mvn test -Dtest=WebTestRunner
 * 
 * # Run from IDE
 * Right-click this class → Run 'WebTestRunner'
 * 
 * # Run specific tag (via Maven)
 * mvn test -Dcucumber.filter.tags="@web and @smoke"
 * </pre>
 * 
 * <h2>Reports Generated:</h2>
 * <ul>
 *   <li>HTML Report: {@code target/cucumber-reports/web/index.html}</li>
 *   <li>JSON Report: {@code target/cucumber-web.json}</li>
 *   <li>JUnit XML: {@code target/cucumber-web.xml}</li>
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
@SelectClasspathResource("features/web")
@ConfigurationParameter(
    key = FILTER_TAGS_PROPERTY_NAME,
    value = "@web"
)
@ConfigurationParameter(
    key = GLUE_PROPERTY_NAME,
    value = "com.intive.aifirst.petspot.e2e.utils," +
            "com.intive.aifirst.petspot.e2e.steps.web"
)
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "pretty," +
            "html:target/cucumber-reports/web/cucumber.html," +
            "json:target/cucumber-web.json," +
            "junit:target/cucumber-web.xml"
)
public class WebTestRunner {
    // No implementation needed - JUnit Platform Suite handles execution
    // This class serves as a configuration entry point for Cucumber tests
}

