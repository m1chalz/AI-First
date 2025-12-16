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
 * Executes scenarios tagged with @web from all feature directories.
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
 *   <li>HTML Report: {@code target/cucumber-reports/web/cucumber.html}</li>
 *   <li>JSON Report: {@code target/cucumber-web.json}</li>
 *   <li>JUnit XML: {@code target/cucumber-web.xml}</li>
 * </ul>
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
    key = FILTER_TAGS_PROPERTY_NAME,
    value = "@web and not @pending and not @pending-web and not @legacy"
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
    
    // Set skip.app.build BEFORE any hooks run
    // This static initializer executes when the class is loaded by JUnit
    static {
        System.setProperty("skip.app.build", "true");
        System.out.println("WebTestRunner: Set skip.app.build=true (web tests don't need mobile apps)");
    }
}

