import { expect } from '@wdio/globals';
import { AnimalListScreen } from '../screens/AnimalListScreen';
import { waitForElementDisplayed } from './elementSteps';
import { clickElement } from './mouseSteps';

/**
 * Reusable step definitions for Animal List E2E tests (mobile).
 * Follows Given-When-Then pattern for test readability.
 */

/**
 * GIVEN: User has the app open and is on the animal list screen.
 */
export async function givenUserIsOnAnimalListScreen(driver: WebdriverIO.Browser): Promise<AnimalListScreen> {
    const screen = new AnimalListScreen();
    await waitForElementDisplayed(driver, screen.testIds.listContainer);
    return screen;
}

/**
 * WHEN: User scrolls the animal list.
 */
export async function whenUserScrollsList(screen: AnimalListScreen) {
    const cards = await screen.getAnimalCards();
    if (cards.length > 0) {
        const lastCard = cards[cards.length - 1];
        await lastCard.scrollIntoView();
    }
}

/**
 * THEN: Animal cards are visible.
 */
export async function thenAnimalCardsAreVisible(screen: AnimalListScreen, expectedCount: number) {
    const cards = await screen.getAnimalCards();
    expect(cards.length).toBeGreaterThanOrEqual(expectedCount);
}

/**
 * THEN: Report button is visible.
 */
export async function thenReportMissingButtonIsVisible(screen: AnimalListScreen) {
    await expect(screen.reportMissingButton).toBeDisplayed();
}

/**
 * WHEN: User clicks on an animal card.
 */
export async function whenUserClicksAnimalCard(screen: AnimalListScreen, animalId: string) {
    const card = screen.getAnimalCard(animalId);
    await clickElement(card);
}

/**
 * WHEN: User clicks Report Missing button.
 */
export async function whenUserClicksReportMissing(screen: AnimalListScreen) {
    await clickElement(screen.reportMissingButton);
}

