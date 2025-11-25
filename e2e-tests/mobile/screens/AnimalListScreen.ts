/**
 * Screen Object Model for Animal List screen (mobile).
 * Contains ONLY test IDs and locator getters (no actions).
 */
export class AnimalListScreen {
    /**
     * Test IDs for animal list screen elements.
     * Updated to match new Figma design specification (spec.md).
     */
    readonly testIds = {
        listContainer: 'animalList.list',
        reportMissingButton: 'animalList.reportButton',
        searchPlaceholder: 'animalList.searchPlaceholder',
        animalCard: 'animalList.cardItem',
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
     * Cards now have a generic testTag 'animalList.cardItem' (not per-animal IDs).
     */
    async getAnimalCards() {
        return $$(`~${this.testIds.animalCard}`);
    }

    /**
     * Returns a specific animal card by index or first match.
     * Note: Cards now use generic testTag 'animalList.cardItem' for all animals.
     * To target a specific card, use index or list traversal.
     */
    getAnimalCard(id: string) {
        // First card with the generic testTag (or filter by list position)
        return $(`~${this.testIds.animalCard}`);
    }
}

