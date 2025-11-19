import { Page, expect } from '@playwright/test';
import { AnimalListPage } from '../pages/AnimalListPage';

/**
 * Reusable step definitions for Animal List E2E tests (web).
 * Follows Given-When-Then pattern for test readability.
 */

/**
 * GIVEN: User is on the animal list page.
 */
export async function givenUserIsOnAnimalListPage(page: Page): Promise<AnimalListPage> {
    const animalListPage = new AnimalListPage(page);
    await animalListPage.goto();
    return animalListPage;
}

/**
 * WHEN: User scrolls the animal list.
 */
export async function whenUserScrollsList(page: Page) {
    await page.evaluate(() => {
        window.scrollBy(0, window.innerHeight);
    });
}

/**
 * THEN: Animal cards are visible.
 */
export async function thenAnimalCardsAreVisible(animalListPage: AnimalListPage, expectedCount: number) {
    const cards = animalListPage.getAnimalCards();
    await expect(cards).toHaveCount(expectedCount);
}

/**
 * THEN: Report button is visible.
 */
export async function thenReportMissingButtonIsVisible(animalListPage: AnimalListPage) {
    await expect(animalListPage.reportMissingButton).toBeVisible();
}

/**
 * WHEN: User clicks on an animal card.
 */
export async function whenUserClicksAnimalCard(animalListPage: AnimalListPage, animalId: string) {
    await animalListPage.clickAnimalCard(animalId);
}

/**
 * WHEN: User clicks Report Missing button.
 */
export async function whenUserClicksReportMissing(animalListPage: AnimalListPage) {
    await animalListPage.clickReportMissing();
}

