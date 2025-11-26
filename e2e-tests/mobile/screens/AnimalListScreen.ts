/**
 * Screen Object Model for Animal List screen (mobile).
 * Contains ONLY test IDs and locator getters (no actions).
 * 
 * Note: Jetpack Compose doesn't export testTag/contentDescription as accessibility id for UiAutomator2.
 * Using XPath selectors with element attributes instead.
 */
export class AnimalListScreen {
    /**
     * Test IDs for animal list screen elements (kept for reference).
     * Using XPath locators instead due to Compose limitations.
     */
    readonly testIds = {
        listContainer: 'animalList.list',
        reportMissingButton: 'animalList.reportButton',
        searchPlaceholder: 'animalList.searchPlaceholder',
        animalCard: 'animalList.cardItem',
    };

    /**
     * Returns the main list container element (scrollable LazyColumn).
     * XPath: Find scrollable view that contains animal cards.
     */
    get listContainer() {
        return $('//android.view.View[@scrollable="true"]');
    }

    /**
     * Returns the "Report a Missing Animal" button.
     * XPath: Find TextView with "Report a Missing Animal" text (Compose Button renders as TextView).
     */
    get reportMissingButton() {
        return $('//android.widget.TextView[@text="Report a Missing Animal"]');
    }

    /**
     * Returns the search placeholder element.
     */
    get searchPlaceholder() {
        return $(`~${this.testIds.searchPlaceholder}`);
    }

    /**
     * Returns all animal card elements.
     * XPath: Find clickable+focusable views within scrollable container.
     */
    async getAnimalCards() {
        return $$('//android.view.View[@scrollable="true"]/android.view.View[@clickable="true" and @focusable="true"]');
    }

    /**
     * Returns a specific animal card by index.
     * XPath: Find first clickable card within scrollable container.
     */
    getAnimalCard(id: string) {
        return $('(//android.view.View[@scrollable="true"]/android.view.View[@clickable="true" and @focusable="true"])[1]');
    }
}

