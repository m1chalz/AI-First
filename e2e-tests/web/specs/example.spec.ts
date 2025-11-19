import { test, expect } from '@playwright/test';
import { ExamplePage } from '../pages/ExamplePage';
import { waitForElement } from '../steps/urlSteps';
import { getElementText, fillInput } from '../steps/elementSteps';
import { clickElement } from '../steps/mouseSteps';

/**
 * Example E2E test suite demonstrating:
 * - Page Object Model pattern
 * - Given-When-Then structure
 * - Test identifier usage (data-testid)
 * - Reusable step definitions
 */

test.describe('Example Feature', () => {
  let examplePage: ExamplePage;

  test.beforeEach(async ({ page }) => {
    examplePage = new ExamplePage(page);
  });

  test('should display welcome message on page load', async ({ page }) => {
    // Given - User navigates to the example page
    await examplePage.navigate();
    await waitForElement(page, examplePage.testIds.title);

    // When - Page loads
    const title = await getElementText(page, examplePage.testIds.title);

    // Then - Welcome message is displayed
    expect(title).toBeTruthy();
    expect(examplePage.titleLocator).toBeVisible();
  });

  test('should submit form and display result', async ({ page }) => {
    // Given - User is on the example page with loaded form
    await examplePage.navigate();
    await waitForElement(page, examplePage.testIds.title);

    // When - User fills input and submits form
    const testInput = 'Hello PetSpot!';
    await fillInput(page, examplePage.testIds.input, testInput);
    await clickElement(page, examplePage.testIds.submitButton);

    // Then - Result is displayed with correct content
    const result = await getElementText(page, examplePage.testIds.result);
    expect(result).toContain(testInput);
  });

  test('should handle empty input submission', async ({ page }) => {
    // Given - User is on the example page
    await examplePage.navigate();
    await waitForElement(page, examplePage.testIds.title);

    // When - User submits empty form
    await clickElement(page, examplePage.testIds.submitButton);

    // Then - Appropriate validation or default message is shown
    const result = await getElementText(page, examplePage.testIds.result);
    expect(result).toBeDefined();
  });
});

