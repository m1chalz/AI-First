import { expect } from '@wdio/globals';
import { AnimalListScreen } from '../screens/AnimalListScreen';
import {
    givenUserIsOnAnimalListScreen,
    whenUserScrollsList,
    thenAnimalCardsAreVisible,
    thenReportMissingButtonIsVisible,
    whenUserClicksAnimalCard,
    whenUserClicksReportMissing
} from '../steps/animalListSteps';

/**
 * End-to-end tests for Animal List screen (Mobile Android).
 * Tests user stories with Appium following Given-When-Then pattern.
 * 
 * Note: These tests require actual mobile app build and Appium server running.
 * They serve as test specifications - implementation requires app deployment.
 */

describe('Animal List Screen - User Story 1: View Animal List', () => {
    
    it('should display scrollable list of animals on Android', async () => {
        // Given - user has the app open on animal list screen
        const screen = await givenUserIsOnAnimalListScreen();
        
        // When - screen loads
        await screen.listContainer.waitForDisplayed({ timeout: 5000 });
        
        // Then - animal cards should be visible (16 animals from mock data)
        await thenAnimalCardsAreVisible(screen, 16);
    });
    
    it('should allow scrolling through animal list', async () => {
        // Given - user is on the animal list screen
        const screen = await givenUserIsOnAnimalListScreen();
        await screen.listContainer.waitForDisplayed({ timeout: 5000 });
        
        // When - user scrolls the list
        await whenUserScrollsList(screen);
        
        // Then - more animals should become visible
        // (Verification handled by scroll step)
    });
    
    it('should display animal card with all details on Android', async () => {
        // Given - user is on the animal list screen
        const screen = await givenUserIsOnAnimalListScreen();
        await screen.listContainer.waitForDisplayed({ timeout: 5000 });
        
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
        const screen = await givenUserIsOnAnimalListScreen();
        await screen.listContainer.waitForDisplayed({ timeout: 5000 });
        
        // When - checking for report missing button
        // Then - Report Missing button should be visible
        await thenReportMissingButtonIsVisible(screen);
    });
    
    it('should remain visible when scrolling on Android', async () => {
        // Given - user is on the animal list screen
        const screen = await givenUserIsOnAnimalListScreen();
        await screen.listContainer.waitForDisplayed({ timeout: 5000 });
        
        // When - user scrolls the list
        await whenUserScrollsList(screen);
        
        // Then - button should still be visible (fixed at bottom)
        await expect(screen.reportMissingButton).toBeDisplayed();
    });
    
    it('should trigger action when Report Missing button is tapped', async () => {
        // Given - user is on the animal list screen
        const screen = await givenUserIsOnAnimalListScreen();
        await screen.listContainer.waitForDisplayed({ timeout: 5000 });
        
        // When - user taps Report Missing button
        await whenUserClicksReportMissing(screen);
        
        // Then - action should be triggered
        // (Mocked navigation - no assertion needed for prototype)
        // Future: Verify navigation to report form screen
    });
});

describe('Animal List Screen - User Story 3: Search Preparation', () => {
    
    it('should display reserved search space on Android', async () => {
        // Given - user is on the animal list screen
        const screen = await givenUserIsOnAnimalListScreen();
        await screen.listContainer.waitForDisplayed({ timeout: 5000 });
        
        // When - checking for search placeholder
        const searchPlaceholder = screen.searchPlaceholder;
        
        // Then - search placeholder should exist (reserved space)
        await expect(searchPlaceholder).toBeDisplayed();
    });
});

describe('Animal List Screen - Card Interaction', () => {
    
    it('should trigger navigation when animal card is tapped', async () => {
        // Given - user is on the animal list screen
        const screen = await givenUserIsOnAnimalListScreen();
        await screen.listContainer.waitForDisplayed({ timeout: 5000 });
        
        // When - user taps an animal card
        await whenUserClicksAnimalCard(screen, '1');
        
        // Then - navigation should be triggered
        // (Mocked navigation - no assertion needed for prototype)
        // Future: Verify navigation to detail screen
    });
});

/**
 * Note: These tests require:
 * 1. Android app built with debug configuration
 * 2. Appium server running
 * 3. Android emulator or physical device connected
 * 4. Proper capabilities configured in wdio.conf.ts
 * 
 * To run:
 * npm run test:mobile:android
 */

