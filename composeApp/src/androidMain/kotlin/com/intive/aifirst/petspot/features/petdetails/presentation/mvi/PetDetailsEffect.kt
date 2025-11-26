package com.intive.aifirst.petspot.features.petdetails.presentation.mvi

import com.intive.aifirst.petspot.composeapp.domain.models.Location

/**
 * Sealed interface for one-off side effects on Pet Details screen.
 * Effects are emitted once and consumed by the UI layer.
 */
sealed interface PetDetailsEffect {
    /** Navigate back to previous screen */
    data object NavigateBack : PetDetailsEffect
    
    /** Show location on external map app */
    data class ShowMap(val location: Location) : PetDetailsEffect
    
    /** Show error message when map app is not available */
    data object MapNotAvailable : PetDetailsEffect
}

