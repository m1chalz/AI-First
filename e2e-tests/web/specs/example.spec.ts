import { test, expect } from '@playwright/test';
import { ExamplePage } from '../pages/ExamplePage';
import { waitForElement } from '../steps/urlSteps';
import { getElementText, fillInput } from '../steps/elementSteps';
import { clickElement } from '../steps/mouseSteps';

test.describe('Example Feature', () => {
  let examplePage: ExamplePage;

  test.beforeEach(async ({ page }) => {
    examplePage = new ExamplePage(page);
  });

  test('should display welcome message on page load', async ({ page }) => {
    // Given
    await examplePage.navigate();
    await waitForElement(page, examplePage.testIds.title);

    // When
    const title = await getElementText(page, examplePage.testIds.title);

    // Then
    expect(title).toBeTruthy();
    expect(examplePage.titleLocator).toBeVisible();
  });

  test('should submit form and display result', async ({ page }) => {
    // Given
    await examplePage.navigate();
    await waitForElement(page, examplePage.testIds.title);

    // When
    const testInput = 'Hello PetSpot!';
    await fillInput(page, examplePage.testIds.input, testInput);
    await clickElement(page, examplePage.testIds.submitButton);

    // Then
    const result = await getElementText(page, examplePage.testIds.result);
    expect(result).toContain(testInput);
  });

  test('should handle empty input submission', async ({ page }) => {
    // Given
    await examplePage.navigate();
    await waitForElement(page, examplePage.testIds.title);

    // When
    await clickElement(page, examplePage.testIds.submitButton);

    // Then
    const result = await getElementText(page, examplePage.testIds.result);
    expect(result).toBeDefined();
  });
});

