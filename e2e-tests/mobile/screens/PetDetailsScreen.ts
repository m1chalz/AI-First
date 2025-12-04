/**
 * Screen Object Model for Pet Details screen (mobile).
 * Contains ONLY test IDs and locator getters (no actions).
 */
export class PetDetailsScreen {
    /**
     * Test IDs for pet details screen elements.
     */
    readonly testIds = {
        // Main screen elements
        content: 'petDetails.content',
        loading: 'petDetails.loading',
        error: 'petDetails.error',
        errorMessage: 'petDetails.error.message',
        retryButton: 'petDetails.retryButton',
        
        // Pet photo section
        photo: 'petDetails.photo',
        statusBadge: 'petDetails.statusBadge',
        rewardBadge: 'petDetails.rewardBadge',
        
        // Pet info section
        name: 'petDetails.name',
        species: 'petDetails.species',
        breed: 'petDetails.breed',
        sex: 'petDetails.sex',
        age: 'petDetails.age',
        microchip: 'petDetails.microchip',
        disappearanceDate: 'petDetails.disappearanceDate',
        
        // Location section
        location: 'petDetails.location',
        showMapButton: 'petDetails.showMapButton',
        
        // Contact section
        phone: 'petDetails.phone',
        email: 'petDetails.email',
        
        // Description section
        description: 'petDetails.description',
    };

    /**
     * Returns the main content container.
     */
    get content() {
        return $(`~${this.testIds.content}`);
    }

    /**
     * Returns the loading indicator element.
     */
    get loading() {
        return $(`~${this.testIds.loading}`);
    }

    /**
     * Returns the error container element.
     */
    get error() {
        return $(`~${this.testIds.error}`);
    }

    /**
     * Returns the error message text element.
     */
    get errorMessage() {
        return $(`~${this.testIds.errorMessage}`);
    }

    /**
     * Returns the retry button element.
     */
    get retryButton() {
        return $(`~${this.testIds.retryButton}`);
    }

    /**
     * Returns the pet photo element.
     */
    get photo() {
        return $(`~${this.testIds.photo}`);
    }

    /**
     * Returns the status badge element.
     */
    get statusBadge() {
        return $(`~${this.testIds.statusBadge}`);
    }

    /**
     * Returns the reward badge element.
     */
    get rewardBadge() {
        return $(`~${this.testIds.rewardBadge}`);
    }

    /**
     * Returns the pet name element.
     */
    get name() {
        return $(`~${this.testIds.name}`);
    }

    /**
     * Returns the species element.
     */
    get species() {
        return $(`~${this.testIds.species}`);
    }

    /**
     * Returns the breed element.
     */
    get breed() {
        return $(`~${this.testIds.breed}`);
    }

    /**
     * Returns the sex element.
     */
    get sex() {
        return $(`~${this.testIds.sex}`);
    }

    /**
     * Returns the age element.
     */
    get age() {
        return $(`~${this.testIds.age}`);
    }

    /**
     * Returns the microchip element.
     */
    get microchip() {
        return $(`~${this.testIds.microchip}`);
    }

    /**
     * Returns the disappearance date element.
     */
    get disappearanceDate() {
        return $(`~${this.testIds.disappearanceDate}`);
    }

    /**
     * Returns the location element.
     */
    get location() {
        return $(`~${this.testIds.location}`);
    }

    /**
     * Returns the show map button element.
     */
    get showMapButton() {
        return $(`~${this.testIds.showMapButton}`);
    }

    /**
     * Returns the phone element.
     */
    get phone() {
        return $(`~${this.testIds.phone}`);
    }

    /**
     * Returns the email element.
     */
    get email() {
        return $(`~${this.testIds.email}`);
    }

    /**
     * Returns the description element.
     */
    get description() {
        return $(`~${this.testIds.description}`);
    }
}


