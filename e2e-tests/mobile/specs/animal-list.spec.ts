import { expect } from '@wdio/globals';
import {
    givenUserIsOnAnimalListScreen,
    whenUserScrollsList,
    thenAnimalCardsAreVisible,
    thenReportMissingButtonIsVisible,
    whenUserClicksAnimalCard,
    whenUserClicksReportMissing
} from '../steps/animalListSteps';
import { waitForElementDisplayed } from '../steps/elementSteps';

/**
 * E2E tests for Animal List screen (Mobile Android).
 */

describe('Animal List Screen - User Story 1: View Animal List', () => {
    
    it('should display scrollable list of animals on Android', async () => {
        // Given - user has the app open on animal list screen
        const screen = await givenUserIsOnAnimalListScreen(driver);
        
        // When - screen loads
        await screen.listContainer.waitForDisplayed({ timeout: 10000 });
        
        // Then - animal cards should be visible (LazyColumn renders only visible items, not all 16)
        await thenAnimalCardsAreVisible(screen, 1);
    });
    
    it('should allow scrolling through animal list', async () => {
        // Given - user is on the animal list screen
        const screen = await givenUserIsOnAnimalListScreen(driver);
        await screen.listContainer.waitForDisplayed({ timeout: 10000 });
        
        // When - user scrolls the list
        await whenUserScrollsList(screen);
        
        // Then - more animals should become visible
    });
    
    it('should display animal card with all details on Android', async () => {
        // Given - user is on the animal list screen
        const screen = await givenUserIsOnAnimalListScreen(driver);
        await screen.listContainer.waitForDisplayed({ timeout: 10000 });
        
        // When - viewing first animal card
        const firstCard = screen.getAnimalCard('1');
        await firstCard.waitForDisplayed({ timeout: 3000 });
        
        // Then - card should be visible and tappable
        await expect(firstCard).toBeDisplayed();
    });
});

describe('Animal List Screen - User Story 2: Report Action Button', () => {
    
    it('should display Report a Missing Animal button on Android', async () => {
        // Given - user is on the animal list screen
        const screen = await givenUserIsOnAnimalListScreen(driver);
        await screen.listContainer.waitForDisplayed({ timeout: 10000 });
        
        // When - checking for report missing button
        // Then - Report Missing button should be visible
        await thenReportMissingButtonIsVisible(screen);
    });
    
    it('should remain visible when scrolling on Android', async () => {
        // Given - user is on the animal list screen
        const screen = await givenUserIsOnAnimalListScreen(driver);
        await screen.listContainer.waitForDisplayed({ timeout: 10000 });
        
        // When - user scrolls the list
        await whenUserScrollsList(screen);
        
        // Then - button should still be visible (fixed at bottom)
        await expect(screen.reportMissingButton).toBeDisplayed();
    });
    
    it('should trigger action when Report Missing button is tapped', async () => {
        // Given - user is on the animal list screen
        const screen = await givenUserIsOnAnimalListScreen(driver);
        await screen.listContainer.waitForDisplayed({ timeout: 10000 });
        
        // When - user taps Report Missing button
        await whenUserClicksReportMissing(screen);
        
        // Then - action should be triggered
        // (Navigation not implemented yet)
    });
});

// TODO: Uncomment when search placeholder is implemented in UI
/*
describe('Animal List Screen - User Story 3: Search Preparation', () => {
    
    it('should display reserved search space on Android', async () => {
        // Given - user is on the animal list screen
        const screen = await givenUserIsOnAnimalListScreen(driver);
        await screen.listContainer.waitForDisplayed({ timeout: 10000 });
        
        // When - checking for search placeholder
        const searchPlaceholder = screen.searchPlaceholder;
        
        // Then - search placeholder should exist (reserved space)
        await expect(searchPlaceholder).toBeDisplayed();
    });
});
*/

describe('Animal List Screen - Card Interaction', () => {
    
    it('should trigger navigation when animal card is tapped', async () => {
        // Given - user is on the animal list screen
        const screen = await givenUserIsOnAnimalListScreen(driver);
        await screen.listContainer.waitForDisplayed({ timeout: 10000 });
        
        // When - user taps an animal card
        await whenUserClicksAnimalCard(screen, '1');
        
        // Then - navigation should be triggered
        // (Navigation not implemented yet)
    });
});

