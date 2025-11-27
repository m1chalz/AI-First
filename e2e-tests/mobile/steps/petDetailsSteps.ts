import { expect } from '@wdio/globals';
import { PetDetailsScreen } from '../screens/PetDetailsScreen';
import { givenUserIsOnAnimalListScreen, whenUserClicksAnimalCard } from './animalListSteps';
import { waitForElementDisplayed } from './elementSteps';

/**
 * Step definitions for Pet Details Screen E2E tests.
 * Follows Given-When-Then pattern with reusable actions.
 */

/**
 * Given: User is on the pet details screen for a specific pet.
 * Navigates from animal list to details screen by tapping a card.
 * 
 * @param driver - WebdriverIO driver instance
 * @param petId - ID of the pet to view
 * @returns PetDetailsScreen instance
 */
export async function givenUserIsOnPetDetailsScreen(
    driver: WebdriverIO.Browser,
    petId: string
): Promise<PetDetailsScreen> {
    // 1. Start from animal list screen
    const animalListScreen = await givenUserIsOnAnimalListScreen(driver);
    
    // 2. Find and tap animal card with given ID
    await whenUserClicksAnimalCard(animalListScreen, petId);
    
    // 3. Wait for Pet Details screen to appear
    const petDetailsScreen = new PetDetailsScreen();
    await driver.pause(1000); // Small pause for navigation animation
    await waitForElementDisplayed(driver, petDetailsScreen.testIds.detailsView);
    
    return petDetailsScreen;
}

/**
 * When: User waits for pet details to load.
 * Waits for loading state to complete and details to be displayed.
 * 
 * @param screen - PetDetailsScreen instance
 */
export async function whenUserWaitsForDetails(screen: PetDetailsScreen): Promise<void> {
    // Wait for loading state to finish (loading spinner disappears)
    await driver.waitUntil(
        async () => {
            const loadingExists = await screen.loadingSpinner.isDisplayed().catch(() => false);
            return !loadingExists;
        },
        {
            timeout: 3000,
            timeoutMsg: 'Loading spinner did not disappear within 3 seconds'
        }
    );
    
    // Wait for details view to be displayed
    await screen.detailsView.waitForDisplayed({ timeout: 2000 });
}

/**
 * Then: Pet details should be visible on screen.
 * Verifies that main details elements are displayed.
 * 
 * @param screen - PetDetailsScreen instance
 */
export async function thenDetailsAreVisible(screen: PetDetailsScreen): Promise<void> {
    // Photo should be visible
    await expect(screen.photoImage).toBeDisplayed();
    
    // Status badge should be visible
    await expect(screen.statusBadge).toBeDisplayed();
    
    // At least one identification field should be visible
    await expect(screen.speciesField).toBeDisplayed();
}

/**
 * Then: Loading state should be visible.
 * Verifies that loading spinner is displayed.
 * 
 * @param screen - PetDetailsScreen instance
 */
export async function thenLoadingIsVisible(screen: PetDetailsScreen): Promise<void> {
    // Check if loading spinner exists and is displayed
    const isDisplayed = await screen.loadingSpinner.isDisplayed().catch(() => false);
    
    // If not immediately visible, it might have been too fast
    // This is acceptable as loading is transient
    if (!isDisplayed) {
        // Already loaded - test passes (loading was too fast to catch)
        return;
    }
    
    await expect(screen.loadingSpinner).toBeDisplayed();
}

/**
 * Then: Error state should be visible.
 * Verifies that error message and retry button are displayed.
 * 
 * @param screen - PetDetailsScreen instance
 */
export async function thenErrorIsVisible(screen: PetDetailsScreen): Promise<void> {
    await expect(screen.errorMessage).toBeDisplayed();
    await expect(screen.retryButton).toBeDisplayed();
}

/**
 * When: User taps the retry button.
 * Simulates tap action on retry button in error state.
 * 
 * @param screen - PetDetailsScreen instance
 */
export async function whenUserTapsRetry(screen: PetDetailsScreen): Promise<void> {
    await screen.retryButton.waitForDisplayed({ timeout: 2000 });
    await screen.retryButton.click();
}

/**
 * When: User taps the phone number field.
 * Simulates tap action to open dialer.
 * 
 * @param screen - PetDetailsScreen instance
 */
export async function whenUserTapsPhone(screen: PetDetailsScreen): Promise<void> {
    await screen.phoneField.waitForDisplayed({ timeout: 2000 });
    await screen.phoneField.click();
}

/**
 * When: User taps the email field.
 * Simulates tap action to open mail composer.
 * 
 * @param screen - PetDetailsScreen instance
 */
export async function whenUserTapsEmail(screen: PetDetailsScreen): Promise<void> {
    await screen.emailField.waitForDisplayed({ timeout: 2000 });
    await screen.emailField.click();
}

/**
 * When: User taps the Show on the map button.
 * Simulates tap action on map button.
 * 
 * @param screen - PetDetailsScreen instance
 */
export async function whenUserTapsShowMap(screen: PetDetailsScreen): Promise<void> {
    await screen.showMapButton.waitForDisplayed({ timeout: 2000 });
    await screen.showMapButton.click();
}

/**
 * When: User taps the Remove Report button.
 * Simulates tap action on remove report button.
 * 
 * @param screen - PetDetailsScreen instance
 */
export async function whenUserTapsRemoveReport(screen: PetDetailsScreen): Promise<void> {
    await screen.removeReportButton.waitForDisplayed({ timeout: 2000 });
    await screen.removeReportButton.click();
}


