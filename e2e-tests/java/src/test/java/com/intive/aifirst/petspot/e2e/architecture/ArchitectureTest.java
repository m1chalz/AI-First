package com.intive.aifirst.petspot.e2e.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Architecture tests for E2E test infrastructure.
 * 
 * <p>Uses ArchUnit to enforce Page Object Model architectural rules:
 * <ul>
 *   <li>Page Objects should not depend on Step Definitions</li>
 *   <li>Step Definitions should use Page Objects, not WebDriver directly</li>
 *   <li>Naming conventions for Pages and Screens</li>
 *   <li>Package structure and layering</li>
 * </ul>
 * 
 * <p>These tests run as part of the test suite and fail the build if
 * architectural rules are violated.
 * 
 * @see <a href="https://www.archunit.org/">ArchUnit Documentation</a>
 */
@DisplayName("E2E Architecture Tests")
class ArchitectureTest {

    private static JavaClasses allClasses;

    @BeforeAll
    static void importClasses() {
        allClasses = new ClassFileImporter()
            .importPackages("com.intive.aifirst.petspot.e2e");
    }

    // ============================================
    // Page Object Model Rules
    // ============================================

    @Test
    @DisplayName("Page Objects should not depend on Step Definitions")
    void pageObjectsShouldNotDependOnStepDefinitions() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..pages..")
            .should().dependOnClassesThat().resideInAPackage("..steps..");

        rule.check(allClasses);
    }

    @Test
    @DisplayName("Screen Objects should not depend on Step Definitions")
    void screenObjectsShouldNotDependOnStepDefinitions() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..screens..")
            .should().dependOnClassesThat().resideInAPackage("..steps..");

        rule.check(allClasses);
    }

    @Test
    @DisplayName("Step Definitions should not use WebDriver directly (should use Page Objects)")
    void stepDefinitionsShouldNotUseWebDriverDirectly() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..steps..")
            .and().haveSimpleNameNotContaining("Common")  // Allow CommonSteps to use drivers
            .should().dependOnClassesThat().haveNameMatching(".*WebDriver.*")
            .orShould().dependOnClassesThat().haveNameMatching(".*AppiumDriver.*");

        rule.check(allClasses);
    }

    @Test
    @DisplayName("Step Definitions should not use Selenium classes directly")
    void stepDefinitionsShouldNotUseSeleniumDirectly() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..steps..")
            .and().haveSimpleNameNotContaining("Common")  // Allow CommonSteps
            .should().dependOnClassesThat().resideInAPackage("org.openqa.selenium..")
            .as("Step Definitions should use Page Objects instead of Selenium classes directly");

        rule.check(allClasses);
    }

    // ============================================
    // Naming Convention Rules
    // ============================================

    @Test
    @DisplayName("Classes in 'pages' package should have 'Page' suffix")
    void pageClassesShouldHavePageSuffix() {
        ArchRule rule = classes()
            .that().resideInAPackage("..pages..")
            .and().areNotInterfaces()
            .and().haveSimpleNameNotEndingWith("Template")  // Exclude templates
            .should().haveSimpleNameEndingWith("Page");

        rule.check(allClasses);
    }

    @Test
    @DisplayName("Classes in 'screens' package should have 'Screen' suffix")
    void screenClassesShouldHaveScreenSuffix() {
        ArchRule rule = classes()
            .that().resideInAPackage("..screens..")
            .and().areNotInterfaces()
            .and().haveSimpleNameNotEndingWith("Template")  // Exclude templates
            .should().haveSimpleNameEndingWith("Screen");

        rule.check(allClasses);
    }

    @Test
    @DisplayName("Classes in 'steps' package should have 'Steps' suffix")
    void stepClassesShouldHaveStepsSuffix() {
        ArchRule rule = classes()
            .that().resideInAPackage("..steps..")
            .and().areNotInterfaces()
            .should().haveSimpleNameEndingWith("Steps");

        rule.check(allClasses);
    }

    @Test
    @DisplayName("Classes in 'runners' package should have 'Runner' or 'Test' suffix")
    void runnerClassesShouldHaveRunnerSuffix() {
        ArchRule rule = classes()
            .that().resideInAPackage("..runners..")
            .and().areNotInterfaces()
            .should().haveSimpleNameEndingWith("Runner")
            .orShould().haveSimpleNameEndingWith("Test");

        rule.check(allClasses);
    }

    // ============================================
    // Layered Architecture Rules
    // ============================================

    @Test
    @DisplayName("E2E infrastructure should follow layered architecture")
    void shouldFollowLayeredArchitecture() {
        layeredArchitecture()
            .consideringAllDependencies()
            .layer("Steps").definedBy("..steps..")
            .layer("Pages").definedBy("..pages..")
            .layer("Screens").definedBy("..screens..")
            .layer("Utils").definedBy("..utils..")
            .layer("Runners").definedBy("..runners..")
            
            .whereLayer("Steps").mayNotBeAccessedByAnyLayer()
            .whereLayer("Pages").mayOnlyBeAccessedByLayers("Steps", "Runners")
            .whereLayer("Screens").mayOnlyBeAccessedByLayers("Steps", "Runners")
            // Utils can be accessed by anyone - no restriction needed
            
            .check(allClasses);
    }

    // ============================================
    // Utility and Manager Rules
    // ============================================

    @Test
    @DisplayName("Utility classes should reside in 'utils' package")
    void utilityClassesShouldBeInUtilsPackage() {
        ArchRule rule = classes()
            .that().haveSimpleNameContaining("Util")
            .or().haveSimpleNameContaining("Manager")
            .or().haveSimpleNameContaining("Helper")
            .should().resideInAPackage("..utils..")
            .as("Utility/Manager/Helper classes should be in utils package");

        rule.check(allClasses);
    }

    @Test
    @DisplayName("WebDriverManager and AppiumDriverManager should only be accessed by Common steps and Hooks")
    void driverManagersShouldOnlyBeAccessedByInfrastructure() {
        ArchRule rule = classes()
            .that().haveSimpleNameEndingWith("DriverManager")
            .should().onlyBeAccessed().byClassesThat()
            .haveSimpleNameContaining("Common")
            .orShould().onlyBeAccessed().byClassesThat()
            .haveSimpleName("Hooks")
            .as("Driver managers should only be accessed by infrastructure classes");

        rule.check(allClasses);
    }
}

