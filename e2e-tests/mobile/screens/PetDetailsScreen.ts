/**
 * Screen Object Model for Pet Details screen (mobile iOS).
 * Contains ONLY test IDs and locator getters (no actions).
 */
export class PetDetailsScreen {
    /**
     * Test IDs for pet details screen elements.
     */
    readonly testIds = {
        // Screen container
        detailsView: 'petDetails.view',
        
        // Loading and error states
        loadingSpinner: 'petDetails.loading',
        errorMessage: 'petDetails.error.message',
        retryButton: 'petDetails.retry.button',
        
        // Photo and badges
        photoImage: 'petDetails.photo.image',
        statusBadge: 'petDetails.status.badge',
        rewardBadge: 'petDetails.reward.badge',
        
        // Identification fields
        microchipField: 'petDetails.microchip.field',
        speciesField: 'petDetails.species.field',
        breedField: 'petDetails.breed.field',
        sexField: 'petDetails.sex.field',
        ageField: 'petDetails.age.field',
        dateField: 'petDetails.date.field',
        
        // Location and contact
        locationField: 'petDetails.location.field',
        radiusField: 'petDetails.radius.field',
        showMapButton: 'petDetails.showMap.button',
        phoneField: 'petDetails.phone.tap',
        emailField: 'petDetails.email.tap',
        
        // Description
        descriptionText: 'petDetails.description.text',
        
        // Actions
        removeReportButton: 'petDetails.removeReport.button',
    };

    /**
     * Returns the main details view container.
     */
    get detailsView() {
        return $(`~${this.testIds.detailsView}`);
    }

    /**
     * Returns the loading spinner element.
     */
    get loadingSpinner() {
        return $(`~${this.testIds.loadingSpinner}`);
    }

    /**
     * Returns the error message text element.
     */
    get errorMessage() {
        return $(`~${this.testIds.errorMessage}`);
    }

    /**
     * Returns the retry button (visible in error state).
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
     * Returns the status badge (MISSING, FOUND, CLOSED).
     */
    get statusBadge() {
        return $(`~${this.testIds.statusBadge}`);
    }

    /**
     * Returns the reward badge (if present).
     */
    get rewardBadge() {
        return $(`~${this.testIds.rewardBadge}`);
    }

    /**
     * Returns the microchip number field.
     */
    get microchipField() {
        return $(`~${this.testIds.microchipField}`);
    }

    /**
     * Returns the species field.
     */
    get speciesField() {
        return $(`~${this.testIds.speciesField}`);
    }

    /**
     * Returns the breed field.
     */
    get breedField() {
        return $(`~${this.testIds.breedField}`);
    }

    /**
     * Returns the sex field.
     */
    get sexField() {
        return $(`~${this.testIds.sexField}`);
    }

    /**
     * Returns the age field.
     */
    get ageField() {
        return $(`~${this.testIds.ageField}`);
    }

    /**
     * Returns the date of disappearance field.
     */
    get dateField() {
        return $(`~${this.testIds.dateField}`);
    }

    /**
     * Returns the location field.
     */
    get locationField() {
        return $(`~${this.testIds.locationField}`);
    }

    /**
     * Returns the radius field.
     */
    get radiusField() {
        return $(`~${this.testIds.radiusField}`);
    }

    /**
     * Returns the "Show on the map" button.
     */
    get showMapButton() {
        return $(`~${this.testIds.showMapButton}`);
    }

    /**
     * Returns the phone field (tappable).
     */
    get phoneField() {
        return $(`~${this.testIds.phoneField}`);
    }

    /**
     * Returns the email field (tappable).
     */
    get emailField() {
        return $(`~${this.testIds.emailField}`);
    }

    /**
     * Returns the description text element.
     */
    get descriptionText() {
        return $(`~${this.testIds.descriptionText}`);
    }

    /**
     * Returns the "Remove Report" button.
     */
    get removeReportButton() {
        return $(`~${this.testIds.removeReportButton}`);
    }
}

