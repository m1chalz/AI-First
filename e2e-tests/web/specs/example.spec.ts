import { test, expect } from '@playwright/test';
import { ExamplePage } from '../pages/ExamplePage';

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
    await examplePage.waitForPageLoad();

    // When - Page loads
    const title = await examplePage.getTitle();

    // Then - Welcome message is displayed
    expect(title).toBeTruthy();
    expect(page.getByTestId('example.title.display')).toBeVisible();
  });

  test('should submit form and display result', async ({ page }) => {
    // Given - User is on the example page with loaded form
    await examplePage.navigate();
    await examplePage.waitForPageLoad();

    // When - User fills input and submits form
    const testInput = 'Hello PetSpot!';
    await examplePage.fillInput(testInput);
    await examplePage.clickSubmit();

    // Then - Result is displayed with correct content
    const result = await examplePage.getResult();
    expect(result).toContain(testInput);
  });

  test('should handle empty input submission', async ({ page }) => {
    // Given - User is on the example page
    await examplePage.navigate();
    await examplePage.waitForPageLoad();

    // When - User submits empty form
    await examplePage.clickSubmit();

    // Then - Appropriate validation or default message is shown
    const result = await examplePage.getResult();
    expect(result).toBeDefined();
  });
});

