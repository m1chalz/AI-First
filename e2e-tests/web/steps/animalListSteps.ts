import { Page, expect } from '@playwright/test';
import { AnimalListPage } from '../pages/AnimalListPage';
import { clickElement } from './mouseSteps';
import { waitForElement } from './elementSteps';

/**
 * Reusable step definitions for Animal List E2E tests (web).
 * Follows Given-When-Then pattern for test readability.
 */

/**
 * GIVEN: User is on the animal list page.
 */
export async function givenUserIsOnAnimalListPage(page: Page): Promise<AnimalListPage> {
    const animalListPage = new AnimalListPage(page);
    await page.goto('/');
    await waitForElement(page, animalListPage.testIds.listContainer);
    return animalListPage;
}

/**
 * WHEN: User scrolls the animal list.
 */
export async function whenUserScrollsList(page: Page) {
    await page.evaluate(() => {
        // @ts-expect-error - window is available in browser context
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
    const card = animalListPage.getAnimalCard(animalId);
    await clickElement(card);
}

/**
 * WHEN: User clicks Report Missing button.
 */
export async function whenUserClicksReportMissing(animalListPage: AnimalListPage) {
    await clickElement(animalListPage.reportMissingButton);
}

/**
 * WHEN: User clicks Report Found button.
 */
export async function whenUserClicksReportFound(animalListPage: AnimalListPage) {
    await clickElement(animalListPage.reportFoundButton);
}

