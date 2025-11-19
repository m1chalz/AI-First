import { browser } from '@wdio/globals';

/**
 * Screen Object Model for Animal List screen (mobile).
 * Provides selectors and methods for interacting with the animal list UI.
 */
export class AnimalListScreen {
    /**
     * Returns the main list container element.
     */
    get listContainer() {
        return $('~animalList.list');
    }

    /**
     * Returns the "Report a Missing Animal" button.
     */
    get reportMissingButton() {
        return $('~animalList.reportMissingButton');
    }

    /**
     * Returns the search placeholder element.
     */
    get searchPlaceholder() {
        return $('~animalList.searchPlaceholder');
    }

    /**
     * Returns all animal card elements.
     */
    async getAnimalCards() {
        // Note: WebdriverIO doesn't have direct prefix selector
        // This will be implemented when actual mobile app is available
        return $$('[name^="animalList.item."]');
    }

    /**
     * Returns a specific animal card by ID.
     */
    getAnimalCard(id: string) {
        return $(`~animalList.item.${id}`);
    }

    /**
     * Clicks the "Report a Missing Animal" button.
     */
    async clickReportMissing() {
        await this.reportMissingButton.click();
    }

    /**
     * Clicks on a specific animal card.
     */
    async clickAnimalCard(id: string) {
        await this.getAnimalCard(id).click();
    }

    /**
     * Waits for the list to be displayed.
     */
    async waitForDisplayed() {
        await this.listContainer.waitForDisplayed({ timeout: 5000 });
    }

    /**
     * Scrolls the list to bring an element into view.
     */
    async scrollToElement(element: WebdriverIO.Element) {
        await element.scrollIntoView();
    }
}

