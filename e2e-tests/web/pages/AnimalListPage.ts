import { Page, Locator } from '@playwright/test';

/**
 * Page Object Model for Animal List screen (web).
 * Provides locators and methods for interacting with the animal list UI.
 */
export class AnimalListPage {
    readonly page: Page;
    readonly listContainer: Locator;
    readonly reportMissingButton: Locator;
    readonly reportFoundButton: Locator;
    readonly searchPlaceholder: Locator;

    constructor(page: Page) {
        this.page = page;
        this.listContainer = page.locator('[data-testid="animalList.list"]');
        this.reportMissingButton = page.locator('[data-testid="animalList.reportMissingButton"]');
        this.reportFoundButton = page.locator('[data-testid="animalList.reportFoundButton"]');
        this.searchPlaceholder = page.locator('[data-testid="animalList.searchPlaceholder"]');
    }

    /**
     * Navigates to the animal list page.
     */
    async goto() {
        await this.page.goto('/');
    }

    /**
     * Returns all animal card elements.
     */
    getAnimalCards() {
        return this.page.locator('[data-testid^="animalList.item."]');
    }

    /**
     * Returns a specific animal card by ID.
     */
    getAnimalCard(id: string) {
        return this.page.locator(`[data-testid="animalList.item.${id}"]`);
    }

    /**
     * Clicks the "Report a Missing Animal" button.
     */
    async clickReportMissing() {
        await this.reportMissingButton.click();
    }

    /**
     * Clicks the "Report Found Animal" button.
     */
    async clickReportFound() {
        await this.reportFoundButton.click();
    }

    /**
     * Clicks on a specific animal card.
     */
    async clickAnimalCard(id: string) {
        await this.getAnimalCard(id).click();
    }
}

