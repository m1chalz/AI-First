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
 * Executes scenarios tagged with @android from all feature directories.
 * 
 * <p>Sets PLATFORM=Android system property to ensure Hooks.java uses correct driver.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
    key = FILTER_TAGS_PROPERTY_NAME,
    value = "@android and not @pending and not @pending-android and not @legacy"
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
    
    // Set PLATFORM when this runner class is loaded
    static {
        System.setProperty("PLATFORM", "Android");
        System.out.println("AndroidTestRunner: Set PLATFORM=Android");
    }
}
