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
 * Executes scenarios tagged with @ios from all feature directories.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
    key = FILTER_TAGS_PROPERTY_NAME,
    value = "@ios and not @pending and not @legacy"
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
}
