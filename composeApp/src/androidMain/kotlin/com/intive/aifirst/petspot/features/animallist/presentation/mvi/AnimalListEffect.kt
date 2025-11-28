package com.intive.aifirst.petspot.features.animallist.presentation.mvi

import com.intive.aifirst.petspot.domain.models.RationaleDialogType

/**
 * Sealed interface for one-off effects in Animal List screen.
 * Represents side effects like navigation that should happen only once.
 * Extended with location permission effects.
 */
sealed interface AnimalListEffect {
    /**
     * Navigate to animal detail screen.
     */
    data class NavigateToDetails(val animalId: String) : AnimalListEffect

    /**
     * Navigate to Report Missing Animal form.
     */
    data object NavigateToReportMissing : AnimalListEffect

    /**
     * Navigate to Report Found Animal form.
     */
    data object NavigateToReportFound : AnimalListEffect

    // ========================================
    // Location Permission Effects (US1-US5)
    // ========================================

    /**
     * Check current permission status.
     * UI should query Accompanist permission state.
     */
    data object CheckPermissionStatus : AnimalListEffect

    /**
     * Request permission via system dialog.
     * UI should trigger Accompanist launchMultiplePermissionRequest.
     */
    data object RequestPermission : AnimalListEffect

    /**
     * Show custom rationale dialog.
     */
    data class ShowRationaleDialog(val type: RationaleDialogType) : AnimalListEffect

    /**
     * Navigate to app settings for manual permission grant.
     */
    data object OpenSettings : AnimalListEffect
}
