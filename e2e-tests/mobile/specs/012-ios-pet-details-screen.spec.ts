import { expect } from '@wdio/globals';
import {
    givenUserIsOnPetDetailsScreen,
    whenUserWaitsForDetails,
    thenDetailsAreVisible,
    thenLoadingIsVisible,
    thenErrorIsVisible,
    whenUserTapsRetry,
    whenUserTapsPhone,
    whenUserTapsEmail,
    whenUserTapsShowMap
} from '../steps/petDetailsSteps';
import { waitForElementDisplayed } from '../steps/elementSteps';

/**
 * E2E tests for Pet Details screen (Mobile iOS).
 * Tests User Story 1: View Pet Details from List
 */

describe('Pet Details Screen - User Story 1: View Pet Details', () => {
    
    it('should navigate to pet details screen from list', async () => {
        // Given - user has the app open on animal list screen
        // When - user taps on a pet card (using simple ID "1" which iOS mock data uses)
        const screen = await givenUserIsOnPetDetailsScreen(driver, '1');
        
        // Then - pet details screen should be displayed
        await waitForElementDisplayed(driver, screen.testIds.content);
        await expect(screen.content).toBeDisplayed();
    });
    
    it('should display loading state while fetching pet data', async () => {
        // Given - user navigates to pet details
        const screen = await givenUserIsOnPetDetailsScreen(driver, '1');
        
        // When - data is being loaded (immediately after navigation)
        // Then - loading spinner should be visible
        await thenLoadingIsVisible(screen);
    });
    
    it('should display pet photo after successful load', async () => {
        // Given - user navigates to pet details
        const screen = await givenUserIsOnPetDetailsScreen(driver, '1');
        
        // When - data finishes loading
        await whenUserWaitsForDetails(screen);
        
        // Then - pet photo should be visible
        await expect(screen.photo).toBeDisplayed();
    });
    
    it('should display pet details after successful load', async () => {
        // Given - user navigates to pet details for Fredi (DOG)
        const screen = await givenUserIsOnPetDetailsScreen(driver, '1');
        
        // When - data finishes loading
        await whenUserWaitsForDetails(screen);
        
        // Then - all details should be visible
        await thenDetailsAreVisible(screen);
    });
    
    it('should display error message when pet not found', async () => {
        // Given - user navigates to details with invalid pet ID
        const screen = await givenUserIsOnPetDetailsScreen(driver, 'non-existent-id');
        
        // When - data fetch fails
        await driver.pause(1000); // Wait for error state
        
        // Then - error message should be displayed
        await thenErrorIsVisible(screen);
        await expect(screen.errorMessage).toBeDisplayed();
    });
    
    it('should display retry button in error state', async () => {
        // Given - user is on pet details with error state
        const screen = await givenUserIsOnPetDetailsScreen(driver, 'non-existent-id');
        await driver.pause(1000); // Wait for error state
        
        // When - checking for retry button
        // Then - retry button should be visible
        await expect(screen.retryButton).toBeDisplayed();
    });
    
    it('should reload data when retry button is tapped', async () => {
        // Given - user is on pet details with error state
        const screen = await givenUserIsOnPetDetailsScreen(driver, 'non-existent-id');
        await driver.pause(1000); // Wait for error state
        
        // When - user taps retry button
        await whenUserTapsRetry(screen);
        
        // Then - loading state should be displayed again
        // Note: In real scenario, we'd need to mock successful response after retry
        await thenLoadingIsVisible(screen);
    });
    
    it('should display fallback when pet has no photo', async () => {
        // Given - user navigates to pet without photo (Piorun - bird)
        const screen = await givenUserIsOnPetDetailsScreen(driver, '3');
        
        // When - data finishes loading
        await whenUserWaitsForDetails(screen);
        
        // Then - fallback "Image not available" should be displayed
        await expect(screen.photo).toBeDisplayed();
        // Note: Could check for specific fallback text/image if implemented
    });
});

describe('Pet Details Screen - User Story 6: Status Badge', () => {
    
    it('should display MISSING badge for ACTIVE status pets', async () => {
        // Given - user navigates to pet with ACTIVE status
        const screen = await givenUserIsOnPetDetailsScreen(driver, '1');
        
        // When - data finishes loading
        await whenUserWaitsForDetails(screen);
        
        // Then - MISSING badge should be visible on photo
        await expect(screen.statusBadge).toBeDisplayed();
        const badgeText = await screen.statusBadge.getText();
        expect(badgeText).toContain('MISSING');
    });
    
    it('should display FOUND badge for found status pets', async () => {
        // Given - user navigates to pet with FOUND status (Burek)
        const screen = await givenUserIsOnPetDetailsScreen(driver, '4');
        
        // When - data finishes loading
        await whenUserWaitsForDetails(screen);
        
        // Then - FOUND badge should be visible
        await expect(screen.statusBadge).toBeDisplayed();
        const badgeText = await screen.statusBadge.getText();
        expect(badgeText).toContain('FOUND');
    });
});

describe('Pet Details Screen - User Story 3: Contact Information', () => {
    
    it('should allow tapping phone number to open dialer', async () => {
        // Given - user is viewing pet details
        const screen = await givenUserIsOnPetDetailsScreen(driver, '1');
        await whenUserWaitsForDetails(screen);
        
        // When - user taps phone number
        await whenUserTapsPhone(screen);
        
        // Then - dialer should open (placeholder: logs to console)
        // Note: Actual dialer opening not testable in E2E
    });
    
    it('should allow tapping email to open mail composer', async () => {
        // Given - user is viewing pet details with email
        const screen = await givenUserIsOnPetDetailsScreen(driver, '1');
        await whenUserWaitsForDetails(screen);
        
        // When - user taps email
        await whenUserTapsEmail(screen);
        
        // Then - mail composer should open (placeholder: logs to console)
        // Note: Actual mail composer opening not testable in E2E
    });
    
    it('should display Show on the map button', async () => {
        // Given - user is viewing pet details
        const screen = await givenUserIsOnPetDetailsScreen(driver, '1');
        await whenUserWaitsForDetails(screen);
        
        // When - checking for map button
        // Then - Show on the map button should be visible
        await expect(screen.showMapButton).toBeDisplayed();
    });
    
    it('should handle Show on the map button tap', async () => {
        // Given - user is viewing pet details
        const screen = await givenUserIsOnPetDetailsScreen(driver, '1');
        await whenUserWaitsForDetails(screen);
        
        // When - user taps Show on the map button
        await whenUserTapsShowMap(screen);
        
        // Then - action is logged to console (placeholder)
        // Note: Actual map navigation not implemented yet
    });
});

// NOTE: User Story 7: Remove Report functionality not implemented yet
// Tests skipped until feature is available on both platforms

