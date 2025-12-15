package com.intive.aifirst.petspot.features.animallist.presentation.mvi

/**
 * Sealed interface for user intents in Animal List screen.
 * Represents all possible user actions in the MVI loop.
 * Extended with location permission and fetch intents.
 */
sealed interface AnimalListIntent {
    /**
     * User wants to refresh the animal list.
     */
    data object Refresh : AnimalListIntent

    /**
     * User selected an animal card to view details.
     */
    data class SelectAnimal(val id: String) : AnimalListIntent

    /**
     * User tapped "Report a Missing Animal" button.
     */
    data object ReportMissing : AnimalListIntent

    /**
     * User tapped "Report Found Animal" button.
     */
    data object ReportFound : AnimalListIntent

    // ========================================
    // Location Permission Intents (US1-US5)
    // ========================================

    /**
     * Check current permission status on app launch.
     */
    data object CheckPermission : AnimalListIntent

    /**
     * Permission result received from Accompanist/system dialog.
     * @param isFromSystemDialog When true, skip rationale dialog (per US2: just load animals after system dialog denial)
     */
    data class PermissionResult(
        val granted: Boolean,
        val fineLocation: Boolean = false,
        val coarseLocation: Boolean = false,
        val shouldShowRationale: Boolean = false,
        val isFromSystemDialog: Boolean = false,
    ) : AnimalListIntent

    /**
     * Location fetch completed successfully.
     */
    data class LocationFetched(
        val latitude: Double,
        val longitude: Double,
    ) : AnimalListIntent

    /**
     * Location fetch failed or timed out.
     */
    data object LocationFetchFailed : AnimalListIntent

    /**
     * Permission state changed dynamically (e.g., user returned from Settings).
     */
    data class PermissionStateChanged(
        val granted: Boolean,
        val shouldShowRationale: Boolean,
    ) : AnimalListIntent

    // ========================================
    // Rationale Dialog Intents (US3, US4)
    // ========================================

    /**
     * User dismissed the rationale dialog (tapped Cancel or Not Now).
     */
    data object RationaleDismissed : AnimalListIntent

    /**
     * User requested to open device Settings from informational rationale dialog.
     */
    data object OpenSettingsRequested : AnimalListIntent

    /**
     * User tapped Continue on educational rationale dialog.
     * Should trigger system permission request.
     */
    data object RationaleContinue : AnimalListIntent
}
