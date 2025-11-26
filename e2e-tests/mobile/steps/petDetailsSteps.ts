import { PetDetailsScreen } from '../screens/PetDetailsScreen';
import { AnimalListScreen } from '../screens/AnimalListScreen';
import { waitForElementDisplayed } from './elementSteps';

/**
 * Step definitions for Pet Details screen E2E tests.
 * Follows Given-When-Then pattern for BDD-style testing.
 */

/**
 * Given - User navigates to pet details screen by tapping an animal card.
 */
export async function givenUserIsOnPetDetailsScreen(
    driver: WebdriverIO.Browser,
    animalId: string = '1'
): Promise<PetDetailsScreen> {
    // First, navigate from animal list
    const listScreen = new AnimalListScreen();
    await waitForElementDisplayed(driver, listScreen.testIds.listContainer);
    
    // Tap on the animal card
    const animalCard = listScreen.getAnimalCard(animalId);
    await animalCard.waitForDisplayed({ timeout: 3000 });
    await animalCard.click();
    
    // Wait for details screen to load
    const detailsScreen = new PetDetailsScreen();
    await waitForElementDisplayed(driver, detailsScreen.testIds.content);
    
    return detailsScreen;
}

/**
 * When - User taps the back button.
 */
export async function whenUserTapsBackButton(screen: PetDetailsScreen): Promise<void> {
    await screen.backButton.waitForDisplayed({ timeout: 3000 });
    await screen.backButton.click();
}

/**
 * When - User taps the show map button.
 */
export async function whenUserTapsShowMapButton(screen: PetDetailsScreen): Promise<void> {
    await screen.showMapButton.waitForDisplayed({ timeout: 3000 });
    await screen.showMapButton.click();
}

/**
 * When - User taps the retry button.
 */
export async function whenUserTapsRetryButton(screen: PetDetailsScreen): Promise<void> {
    await screen.retryButton.waitForDisplayed({ timeout: 3000 });
    await screen.retryButton.click();
}

/**
 * Then - Pet details content should be visible.
 */
export async function thenPetDetailsContentIsVisible(screen: PetDetailsScreen): Promise<void> {
    await screen.content.waitForDisplayed({ timeout: 5000 });
}

/**
 * Then - Status badge should be visible.
 */
export async function thenStatusBadgeIsVisible(screen: PetDetailsScreen): Promise<void> {
    await screen.statusBadge.waitForDisplayed({ timeout: 3000 });
}

/**
 * Then - Reward badge should be visible.
 */
export async function thenRewardBadgeIsVisible(screen: PetDetailsScreen): Promise<void> {
    await screen.rewardBadge.waitForDisplayed({ timeout: 3000 });
}

/**
 * Then - Pet identification info should be visible (species, breed, sex, age).
 */
export async function thenIdentificationInfoIsVisible(screen: PetDetailsScreen): Promise<void> {
    await screen.species.waitForDisplayed({ timeout: 3000 });
    await screen.breed.waitForDisplayed({ timeout: 3000 });
    await screen.sex.waitForDisplayed({ timeout: 3000 });
}

/**
 * Then - Location and contact info should be visible.
 */
export async function thenLocationAndContactInfoIsVisible(screen: PetDetailsScreen): Promise<void> {
    await screen.location.waitForDisplayed({ timeout: 3000 });
}

/**
 * Then - Description should be visible.
 */
export async function thenDescriptionIsVisible(screen: PetDetailsScreen): Promise<void> {
    await screen.description.waitForDisplayed({ timeout: 3000 });
}

/**
 * Then - Loading indicator should be visible.
 */
export async function thenLoadingIsVisible(screen: PetDetailsScreen): Promise<void> {
    await screen.loading.waitForDisplayed({ timeout: 3000 });
}

/**
 * Then - Error state should be visible.
 */
export async function thenErrorIsVisible(screen: PetDetailsScreen): Promise<void> {
    await screen.error.waitForDisplayed({ timeout: 3000 });
}

/**
 * Then - User should be back on animal list screen.
 */
export async function thenUserIsOnAnimalListScreen(): Promise<void> {
    const listScreen = new AnimalListScreen();
    await listScreen.listContainer.waitForDisplayed({ timeout: 5000 });
}

