package com.intive.aifirst.petspot.features.animallist.presentation.mvi

/**
 * Sealed interface for one-off effects in Animal List screen.
 * Represents side effects like navigation that should happen only once.
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
}
