import { test, expect } from '@playwright/test';
import { AnimalListPage } from '../pages/AnimalListPage';
import { givenUserIsOnAnimalListPage } from '../steps/animalListSteps';
import { waitForElement } from '../steps/elementSteps';
import { getAnimalById, testConstants } from '../fixtures/animal-data';

/**
 * E2E tests for Pet Details Modal (Web).
 * Tests User Story 1 (Open Modal) and User Story 2 (Identification Info)
 */

test.describe('Pet Details Modal - User Story 1: Open Pet Details Modal from List', () => {
    
    test('should open modal when Details button is clicked', async ({ page }) => {
        // Given - user is on the animal list page
        const animalListPage = await givenUserIsOnAnimalListPage(page);
        await waitForElement(page, animalListPage.testIds.listContainer);
        
        // When - user clicks the Details button on an animal card
        const detailsButton = page.locator('[data-testid="animalList.card.detailsButton.click"]').first();
        await detailsButton.click();
        
        // Then - modal should be visible
        const modal = page.locator('[data-testid="petDetails.modal"]');
        await expect(modal).toBeVisible();
    });
    
    test('should display pet information in modal', async ({ page }) => {
        // Given - user is on the animal list page
        const animalListPage = await givenUserIsOnAnimalListPage(page);
        await waitForElement(page, animalListPage.testIds.listContainer);
        
        // And - expected pet data
        const testPet = getAnimalById(testConstants.defaultTestAnimalId);
        expect(testPet).toBeDefined();
        
        // When - user opens the pet details modal
        const detailsButton = page.locator('[data-testid="animalList.card.detailsButton.click"]').first();
        await detailsButton.click();
        
        // Then - modal should contain pet name
        const modal = page.locator('[data-testid="petDetails.modal"]');
        await expect(modal).toBeVisible();
        if (testPet?.petName) {
            await expect(modal).toContainText(testPet.petName);
        }
    });
    
    test('should close modal when X button is clicked', async ({ page }) => {
        // Given - pet details modal is open
        const animalListPage = await givenUserIsOnAnimalListPage(page);
        await waitForElement(page, animalListPage.testIds.listContainer);
        const detailsButton = page.locator('[data-testid="animalList.card.detailsButton.click"]').first();
        await detailsButton.click();
        
        const modal = page.locator('[data-testid="petDetails.modal"]');
        await expect(modal).toBeVisible();
        
        // When - user clicks the close button
        const closeButton = page.locator('[data-testid="petDetails.closeButton.click"]');
        await closeButton.click();
        
        // Then - modal should be hidden
        await expect(modal).not.toBeVisible();
    });
    
    test('should close modal when ESC key is pressed', async ({ page }) => {
        // Given - pet details modal is open
        const animalListPage = await givenUserIsOnAnimalListPage(page);
        await waitForElement(page, animalListPage.testIds.listContainer);
        const detailsButton = page.locator('[data-testid="animalList.card.detailsButton.click"]').first();
        await detailsButton.click();
        
        const modal = page.locator('[data-testid="petDetails.modal"]');
        await expect(modal).toBeVisible();
        
        // When - user presses ESC
        await page.keyboard.press('Escape');
        
        // Then - modal should be hidden
        await expect(modal).not.toBeVisible();
    });
    
    test('should close modal when backdrop is clicked', async ({ page }) => {
        // Given - pet details modal is open
        const animalListPage = await givenUserIsOnAnimalListPage(page);
        await waitForElement(page, animalListPage.testIds.listContainer);
        const detailsButton = page.locator('[data-testid="animalList.card.detailsButton.click"]').first();
        await detailsButton.click();
        
        const modal = page.locator('[data-testid="petDetails.modal"]');
        await expect(modal).toBeVisible();
        
        // When - user clicks the backdrop
        const backdrop = page.locator('[data-testid="petDetails.backdrop"]');
        await backdrop.click();
        
        // Then - modal should be hidden
        await expect(modal).not.toBeVisible();
    });
});

test.describe('Pet Details Modal - User Story 2: Review Pet Identification Information', () => {
    
    test('should display microchip number with proper formatting', async ({ page }) => {
        // Given - pet details modal is open
        const animalListPage = await givenUserIsOnAnimalListPage(page);
        await waitForElement(page, animalListPage.testIds.listContainer);
        const detailsButton = page.locator('[data-testid="animalList.card.detailsButton.click"]').first();
        await detailsButton.click();
        
        const modal = page.locator('[data-testid="petDetails.modal"]');
        await expect(modal).toBeVisible();
        
        // When - modal is displayed
        // Then - microchip label should be visible
        await expect(modal).toContainText('Microchip number');
        
        // And - if microchip exists, it should be formatted as XXXXX-XXXXX-XXXXX
        const microchipValue = modal.locator('[data-testid="petDetails.microchip.value"]');
        const text = await microchipValue.textContent();
        if (text && text !== '—') {
            expect(text).toMatch(/^\d{5}-\d{5}-\d{5}$/);
        }
    });
    
    test('should display Animal Species and Animal Race in two-column layout', async ({ page }) => {
        // Given - pet details modal is open
        const animalListPage = await givenUserIsOnAnimalListPage(page);
        await waitForElement(page, animalListPage.testIds.listContainer);
        const detailsButton = page.locator('[data-testid="animalList.card.detailsButton.click"]').first();
        await detailsButton.click();
        
        const modal = page.locator('[data-testid="petDetails.modal"]');
        await expect(modal).toBeVisible();
        
        // When - modal is displayed
        // Then - both labels should be visible
        await expect(modal).toContainText('Animal Species');
        await expect(modal).toContainText('Animal Race');
        
        // And - both values should be present
        const speciesValue = modal.locator('[data-testid="petDetails.species.value"]');
        const breedValue = modal.locator('[data-testid="petDetails.breed.value"]');
        
        await expect(speciesValue).toBeVisible();
        await expect(breedValue).toBeVisible();
    });
    
    test('should display Animal Sex with appropriate symbol', async ({ page }) => {
        // Given - pet details modal is open
        const animalListPage = await givenUserIsOnAnimalListPage(page);
        await waitForElement(page, animalListPage.testIds.listContainer);
        const detailsButton = page.locator('[data-testid="animalList.card.detailsButton.click"]').first();
        await detailsButton.click();
        
        const modal = page.locator('[data-testid="petDetails.modal"]');
        await expect(modal).toBeVisible();
        
        // When - modal is displayed
        // Then - sex label should be visible
        await expect(modal).toContainText('Animal Sex');
        
        // And - value should contain either ♂, ♀, or —
        const sexValue = modal.locator('[data-testid="petDetails.sex.value"]');
        const text = await sexValue.textContent();
        expect(text).toMatch(/^(.*♂.*|.*♀.*|—)$/);
    });
    
    test('should display Animal Approx. Age in years', async ({ page }) => {
        // Given - pet details modal is open
        const animalListPage = await givenUserIsOnAnimalListPage(page);
        await waitForElement(page, animalListPage.testIds.listContainer);
        const detailsButton = page.locator('[data-testid="animalList.card.detailsButton.click"]').first();
        await detailsButton.click();
        
        const modal = page.locator('[data-testid="petDetails.modal"]');
        await expect(modal).toBeVisible();
        
        // When - modal is displayed
        // Then - age label should be visible
        await expect(modal).toContainText('Animal Approx. Age');
        
        // And - value should be in "X years" format or "—"
        const ageValue = modal.locator('[data-testid="petDetails.age.value"]');
        const text = await ageValue.textContent();
        expect(text).toMatch(/^(\d+ years|—)$/);
    });
    
    test('should display all identification fields together', async ({ page }) => {
        // Given - pet details modal is open
        const animalListPage = await givenUserIsOnAnimalListPage(page);
        await waitForElement(page, animalListPage.testIds.listContainer);
        const detailsButton = page.locator('[data-testid="animalList.card.detailsButton.click"]').first();
        await detailsButton.click();
        
        const modal = page.locator('[data-testid="petDetails.modal"]');
        await expect(modal).toBeVisible();
        
        // When - modal is displayed
        // Then - all identification labels should be visible
        await expect(modal).toContainText('Microchip number');
        await expect(modal).toContainText('Animal Species');
        await expect(modal).toContainText('Animal Race');
        await expect(modal).toContainText('Animal Sex');
        await expect(modal).toContainText('Animal Approx. Age');
        
        // And - all value fields should exist
        await expect(modal.locator('[data-testid="petDetails.microchip.value"]')).toBeVisible();
        await expect(modal.locator('[data-testid="petDetails.species.value"]')).toBeVisible();
        await expect(modal.locator('[data-testid="petDetails.breed.value"]')).toBeVisible();
        await expect(modal.locator('[data-testid="petDetails.sex.value"]')).toBeVisible();
        await expect(modal.locator('[data-testid="petDetails.age.value"]')).toBeVisible();
    });
});

