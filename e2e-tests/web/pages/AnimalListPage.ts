import { Page, Locator } from '@playwright/test';

/**
 * Page Object Model for Animal List screen (web).
 * Contains ONLY test IDs and locator getters (no actions).
 */
export class AnimalListPage {
    readonly page: Page;

    /**
     * Test IDs for animal list page elements.
     */
    readonly testIds = {
        listContainer: 'animalList.list',
        reportMissingButton: 'animalList.reportMissingButton',
        reportFoundButton: 'animalList.reportFoundButton',
        searchPlaceholder: 'animalList.searchPlaceholder',
        animalCard: (id: string) => `animalList.item.${id}`,
    };

    readonly listContainer: Locator;
    readonly reportMissingButton: Locator;
    readonly reportFoundButton: Locator;
    readonly searchPlaceholder: Locator;

    constructor(page: Page) {
        this.page = page;
        this.listContainer = page.getByTestId(this.testIds.listContainer);
        this.reportMissingButton = page.getByTestId(this.testIds.reportMissingButton);
        this.reportFoundButton = page.getByTestId(this.testIds.reportFoundButton);
        this.searchPlaceholder = page.getByTestId(this.testIds.searchPlaceholder);
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
        return this.page.getByTestId(this.testIds.animalCard(id));
    }
}

