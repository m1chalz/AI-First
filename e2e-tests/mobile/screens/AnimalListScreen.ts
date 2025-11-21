/**
 * Screen Object Model for Animal List screen (mobile).
 * Contains ONLY test IDs and locator getters (no actions).
 */
export class AnimalListScreen {
    /**
     * Test IDs for animal list screen elements.
     */
    readonly testIds = {
        listContainer: 'animalList.list',
        reportMissingButton: 'animalList.reportMissingButton',
        searchPlaceholder: 'animalList.searchPlaceholder',
        animalCard: (id: string) => `animalList.item.${id}`,
    };

    /**
     * Returns the main list container element.
     */
    get listContainer() {
        return $(`~${this.testIds.listContainer}`);
    }

    /**
     * Returns the "Report a Missing Animal" button.
     */
    get reportMissingButton() {
        return $(`~${this.testIds.reportMissingButton}`);
    }

    /**
     * Returns the search placeholder element.
     */
    get searchPlaceholder() {
        return $(`~${this.testIds.searchPlaceholder}`);
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
        return $(`~${this.testIds.animalCard(id)}`);
    }
}

