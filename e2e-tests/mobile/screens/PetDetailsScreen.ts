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
        view: 'petDetails.view',
        loading: 'petDetails.loading',
        error: 'petDetails.error',
        errorMessage: 'petDetails.error.message',
        retryButton: 'petDetails.error.retry.button',
        
        // Pet photo section
        photoImage: 'petDetails.photo.image',
        statusBadge: 'petDetails.status.badge',
        rewardBadge: 'petDetails.reward.badge',
        
        // Pet info section
        name: 'petDetails.name.field',
        species: 'petDetails.species.field',
        breed: 'petDetails.breed.field',
        sex: 'petDetails.sex.field',
        age: 'petDetails.age.field',
        microchip: 'petDetails.microchip.field',
        disappearanceDate: 'petDetails.date.field',
        
        // Location section
        location: 'petDetails.location.field',
        showMapButton: 'petDetails.showMap.button',
        
        // Contact section
        phone: 'petDetails.phone.tap',
        email: 'petDetails.email.tap',
        
        // Description section
        description: 'petDetails.description.text',
        
        // Actions
        removeReportButton: 'petDetails.removeReport.button',
    };

    /**
     * Returns the main details view container.
     */
    get detailsView() {
        return $(`~${this.testIds.view}`);
    }

    /**
     * Returns the loading indicator element.
     */
    get loadingSpinner() {
        return $(`~${this.testIds.loading}`);
    }

    /**
     * Returns the error container element.
     */
    get errorView() {
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
     * Returns the pet photo image element.
     */
    get photoImage() {
        return $(`~${this.testIds.photoImage}`);
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
     * Returns the pet name field element.
     */
    get nameField() {
        return $(`~${this.testIds.name}`);
    }

    /**
     * Returns the species field element.
     */
    get speciesField() {
        return $(`~${this.testIds.species}`);
    }

    /**
     * Returns the breed field element.
     */
    get breedField() {
        return $(`~${this.testIds.breed}`);
    }

    /**
     * Returns the sex field element.
     */
    get sexField() {
        return $(`~${this.testIds.sex}`);
    }

    /**
     * Returns the age field element.
     */
    get ageField() {
        return $(`~${this.testIds.age}`);
    }

    /**
     * Returns the microchip field element.
     */
    get microchipField() {
        return $(`~${this.testIds.microchip}`);
    }

    /**
     * Returns the disappearance date field element.
     */
    get disappearanceDateField() {
        return $(`~${this.testIds.disappearanceDate}`);
    }

    /**
     * Returns the location field element.
     */
    get locationField() {
        return $(`~${this.testIds.location}`);
    }

    /**
     * Returns the show map button element.
     */
    get showMapButton() {
        return $(`~${this.testIds.showMapButton}`);
    }

    /**
     * Returns the phone field element (tappable).
     */
    get phoneField() {
        return $(`~${this.testIds.phone}`);
    }

    /**
     * Returns the email field element (tappable).
     */
    get emailField() {
        return $(`~${this.testIds.email}`);
    }

    /**
     * Returns the description text element.
     */
    get descriptionText() {
        return $(`~${this.testIds.description}`);
    }

    /**
     * Returns the remove report button element.
     */
    get removeReportButton() {
        return $(`~${this.testIds.removeReportButton}`);
    }
}


