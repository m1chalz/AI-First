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
    // Wait for app to fully initialize and load data
    await driver.pause(1000);
    await screen.listContainer.waitForDisplayed({ timeout: 10000 });
    return screen;
}

/**
 * WHEN: User scrolls the animal list.
 * Uses Appium mobile: scroll command instead of scrollIntoView (not supported on Android).
 */
export async function whenUserScrollsList(screen: AnimalListScreen) {
    const list = screen.listContainer;
    // Perform swipe gesture to scroll down
    await driver.execute('mobile: swipeGesture', {
        left: 500,
        top: 1500,
        width: 100,
        height: 500,
        direction: 'up',
        percent: 0.75
    });
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

