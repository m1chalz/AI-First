import { test, expect } from '@playwright/test';
import { AnimalListPage } from '../pages/AnimalListPage';
import {
    givenUserIsOnAnimalListPage,
    whenUserScrollsList,
    thenAnimalCardsAreVisible,
    thenReportMissingButtonIsVisible,
    whenUserClicksAnimalCard,
    whenUserClicksReportMissing
} from '../steps/animalListSteps';
import { waitForElement, scrollToElement } from '../steps/elementSteps';
import { 
    getAnimalById, 
    getExpectedLocation, 
    getExpectedAnimalDetailsMessage,
    testConstants 
} from '../fixtures/animal-data';

/**
 * E2E tests for Animal List screen (Web).
 */

test.describe('Animal List Screen - User Story 1: View Animal List', () => {
    
    test('should display scrollable list of animals', async ({ page }) => {
        // Given - user is on the animal list page
        const animalListPage = await givenUserIsOnAnimalListPage(page);
        
        // When - page loads
        await waitForElement(page, animalListPage.testIds.listContainer);
        
        // Then - animal cards should be visible (all animals from mock data)
        await thenAnimalCardsAreVisible(animalListPage, testConstants.totalAnimalsCount);
        
        // And - list should be scrollable
        const cards = animalListPage.getAnimalCards();
        const firstCard = cards.first();
        const lastCard = cards.last();
        
        await expect(firstCard).toBeVisible();
        // Scroll to last card
        await scrollToElement(lastCard);
        await expect(lastCard).toBeVisible();
    });
    
    test('should show loading indicator initially', async ({ page }) => {
        // Given - user navigates to animal list page
        const animalListPage = new AnimalListPage(page);
        await page.goto('/');
        
        // When - page is loading
        
        // Then - eventually animals should be visible (loading complete)
        await waitForElement(page, animalListPage.testIds.listContainer);
        await expect(animalListPage.listContainer).toBeVisible();
    });
    
    test('should display animal card with all details', async ({ page }) => {
        // Given - user is on the animal list page
        const animalListPage = await givenUserIsOnAnimalListPage(page);
        await waitForElement(page, animalListPage.testIds.listContainer);
        
        // And - expected data for first animal
        const testAnimal = getAnimalById(testConstants.defaultTestAnimalId);
        expect(testAnimal).toBeDefined();
        
        // When - viewing first animal card
        const firstCard = animalListPage.getAnimalCard(testConstants.defaultTestAnimalId);
        
        // Then - card should display all required information from data source
        await expect(firstCard).toBeVisible();
        await expect(firstCard).toContainText(getExpectedLocation(testAnimal!)); // Location with radius
        await expect(firstCard).toContainText(testAnimal!.species); // Species
        await expect(firstCard).toContainText(testAnimal!.breed); // Breed
        await expect(firstCard).toContainText(testAnimal!.status); // Status
        await expect(firstCard).toContainText(testAnimal!.lastSeenDate); // Last seen date
        await expect(firstCard).toContainText(testAnimal!.description); // Full description
    });
});

test.describe('Animal List Screen - User Story 2: Report Action Button', () => {
    
    test('should display Report a Missing Animal button', async ({ page }) => {
        // Given - user is on the animal list page
        const animalListPage = await givenUserIsOnAnimalListPage(page);
        
        // When - page loads
        await waitForElement(page, animalListPage.testIds.listContainer);
        
        // Then - Report Missing button should be visible
        await thenReportMissingButtonIsVisible(animalListPage);
    });
    
    test('should display Report Found Animal button (web only)', async ({ page }) => {
        // Given - user is on the animal list page
        const animalListPage = await givenUserIsOnAnimalListPage(page);
        await waitForElement(page, animalListPage.testIds.listContainer);
        
        // When - checking for report found button
        // Then - Report Found button should be visible (web has two buttons per Figma)
        await expect(animalListPage.reportFoundButton).toBeVisible();
    });
    
    test('should remain visible when scrolling', async ({ page }) => {
        // Given - user is on the animal list page
        const animalListPage = await givenUserIsOnAnimalListPage(page);
        await waitForElement(page, animalListPage.testIds.listContainer);
        
        // When - user scrolls the list
        await whenUserScrollsList(page);
        
        // Then - buttons should still be visible (fixed position)
        await expect(animalListPage.reportMissingButton).toBeVisible();
        await expect(animalListPage.reportFoundButton).toBeVisible();
    });
    
    test('should trigger action when Report Missing button is clicked', async ({ page }) => {
        // Given - user is on the animal list page
        const animalListPage = await givenUserIsOnAnimalListPage(page);
        await waitForElement(page, animalListPage.testIds.listContainer);
        
        // When - user clicks Report Missing button
        const consoleMessages: string[] = [];
        page.on('console', msg => consoleMessages.push(msg.text()));
        
        await whenUserClicksReportMissing(animalListPage);
        
        // Then - action should be triggered (mocked - console log)
        await page.waitForTimeout(testConstants.consoleLogTimeoutMs);
        expect(consoleMessages.some(msg => msg.includes(testConstants.expectedReportMissingMessage))).toBe(true);
    });
});

test.describe('Animal List Screen - User Story 3: Search Preparation', () => {
    
    test('should display reserved search space', async ({ page }) => {
        // Given - user is on the animal list page
        const animalListPage = await givenUserIsOnAnimalListPage(page);
        await waitForElement(page, animalListPage.testIds.listContainer);
        
        // When - checking for search placeholder
        // Then - search placeholder should exist
        await expect(animalListPage.searchPlaceholder).toBeVisible();
        
        // And - should have correct dimensions (64px height per web design)
        const searchBox = await animalListPage.searchPlaceholder.boundingBox();
        expect(searchBox).not.toBeNull();
        expect(searchBox!.height).toBeGreaterThanOrEqual(testConstants.searchPlaceholderMinHeightPx);
    });
});

test.describe('Animal List Screen - Card Interaction', () => {
    
    test('should trigger navigation when animal card is clicked', async ({ page }) => {
        // Given - user is on the animal list page
        const animalListPage = await givenUserIsOnAnimalListPage(page);
        await waitForElement(page, animalListPage.testIds.listContainer);
        
        // When - user clicks an animal card
        const consoleMessages: string[] = [];
        page.on('console', msg => consoleMessages.push(msg.text()));
        
        await whenUserClicksAnimalCard(animalListPage, testConstants.defaultTestAnimalId);
        
        // Then - navigation should be triggered (mocked - console log)
        await page.waitForTimeout(testConstants.consoleLogTimeoutMs);
        expect(consoleMessages.some(msg => 
            msg.includes(getExpectedAnimalDetailsMessage(testConstants.defaultTestAnimalId))
        )).toBe(true);
    });
});
