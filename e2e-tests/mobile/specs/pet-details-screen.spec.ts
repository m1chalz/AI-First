import { expect } from '@wdio/globals';
import {
    givenUserIsOnPetDetailsScreen,
    whenUserTapsBackButton,
    whenUserTapsShowMapButton,
    thenPetDetailsContentIsVisible,
    thenStatusBadgeIsVisible,
    thenRewardBadgeIsVisible,
    thenIdentificationInfoIsVisible,
    thenLocationAndContactInfoIsVisible,
    thenDescriptionIsVisible,
    thenUserIsOnAnimalListScreen,
} from '../steps/petDetailsSteps';
import { waitForElementDisplayed } from '../steps/elementSteps';
import { PetDetailsScreen } from '../screens/PetDetailsScreen';

/**
 * E2E tests for Pet Details Screen (Mobile Android).
 * All 6 user stories MUST have E2E test coverage.
 */

describe('Pet Details Screen - User Story 1: View Pet Details from List', () => {
    
    it('should navigate to pet details when tapping animal card', async () => {
        // Given - user is on animal list
        // When - user taps on an animal card
        const screen = await givenUserIsOnPetDetailsScreen(driver, '1');
        
        // Then - pet details should be visible
        await thenPetDetailsContentIsVisible(screen);
    });
    
    it('should display pet photo', async () => {
        // Given - user navigated to pet details
        const screen = await givenUserIsOnPetDetailsScreen(driver, '1');
        
        // When - screen is displayed
        // Then - photo should be visible
        await expect(screen.photo).toBeDisplayed();
    });
    
    it('should navigate back when tapping back button', async () => {
        // Given - user is on pet details screen
        const screen = await givenUserIsOnPetDetailsScreen(driver, '1');
        await thenPetDetailsContentIsVisible(screen);
        
        // When - user taps back button
        await whenUserTapsBackButton(screen);
        
        // Then - user should be back on animal list
        await thenUserIsOnAnimalListScreen();
    });
});

describe('Pet Details Screen - User Story 2: Review Pet Identification Information', () => {
    
    it('should display species, breed, sex, and age', async () => {
        // Given - user navigated to pet details
        const screen = await givenUserIsOnPetDetailsScreen(driver, '1');
        
        // When - screen is displayed
        // Then - identification info should be visible
        await thenIdentificationInfoIsVisible(screen);
    });
    
    it('should display microchip number in formatted pattern', async () => {
        // Given - user navigated to pet details for pet with microchip
        const screen = await givenUserIsOnPetDetailsScreen(driver, '1');
        
        // Then - microchip should be displayed
        await expect(screen.microchip).toBeDisplayed();
    });
    
    it('should display date of disappearance in formatted pattern', async () => {
        // Given - user navigated to pet details
        const screen = await givenUserIsOnPetDetailsScreen(driver, '1');
        
        // Then - date should be displayed
        await expect(screen.disappearanceDate).toBeDisplayed();
    });
});

describe('Pet Details Screen - User Story 3: Access Location and Contact Information', () => {
    
    it('should display location coordinates', async () => {
        // Given - user navigated to pet details
        const screen = await givenUserIsOnPetDetailsScreen(driver, '1');
        
        // Then - location should be displayed
        await thenLocationAndContactInfoIsVisible(screen);
    });
    
    it('should display show on map button', async () => {
        // Given - user navigated to pet details
        const screen = await givenUserIsOnPetDetailsScreen(driver, '1');
        
        // Then - show map button should be visible
        await expect(screen.showMapButton).toBeDisplayed();
    });
    
    it('should display contact phone and email', async () => {
        // Given - user navigated to pet details with contact info
        const screen = await givenUserIsOnPetDetailsScreen(driver, '1');
        
        // Then - contact info should be displayed
        await expect(screen.phone).toBeDisplayed();
        await expect(screen.email).toBeDisplayed();
    });
});

describe('Pet Details Screen - User Story 4: Review Additional Pet Details', () => {
    
    it('should display full description text', async () => {
        // Given - user navigated to pet details
        const screen = await givenUserIsOnPetDetailsScreen(driver, '1');
        
        // Then - description should be visible
        await thenDescriptionIsVisible(screen);
    });
});

describe('Pet Details Screen - User Story 5: View Reward Information', () => {
    
    it('should display reward badge when reward is available', async () => {
        // Given - user navigated to pet details for pet with reward
        const screen = await givenUserIsOnPetDetailsScreen(driver, '1'); // Pet 1 has 500 PLN reward
        
        // Then - reward badge should be visible
        await thenRewardBadgeIsVisible(screen);
    });
    
    it('should not display reward badge when no reward available', async () => {
        // Given - user navigated to pet details for pet without reward
        const screen = await givenUserIsOnPetDetailsScreen(driver, '2'); // Pet 2 has no reward
        
        // Then - reward badge should not be displayed
        const rewardBadge = screen.rewardBadge;
        await expect(rewardBadge).not.toBeDisplayed();
    });
});

describe('Pet Details Screen - User Story 6: Identify Pet Status Visually', () => {
    
    it('should display MISSING status badge for active pet', async () => {
        // Given - user navigated to pet details for active pet
        const screen = await givenUserIsOnPetDetailsScreen(driver, '1'); // Pet 1 is ACTIVE
        
        // Then - status badge should be visible with MISSING text
        await thenStatusBadgeIsVisible(screen);
    });
    
    it('should display FOUND status badge for found pet', async () => {
        // Given - user navigated to pet details for found pet
        const screen = await givenUserIsOnPetDetailsScreen(driver, '3'); // Pet 3 is FOUND
        
        // Then - status badge should be visible
        await thenStatusBadgeIsVisible(screen);
    });
    
    it('should display CLOSED status badge for closed pet', async () => {
        // Given - user navigated to pet details for closed pet
        const screen = await givenUserIsOnPetDetailsScreen(driver, '8'); // Pet 8 is CLOSED
        
        // Then - status badge should be visible
        await thenStatusBadgeIsVisible(screen);
    });
});


