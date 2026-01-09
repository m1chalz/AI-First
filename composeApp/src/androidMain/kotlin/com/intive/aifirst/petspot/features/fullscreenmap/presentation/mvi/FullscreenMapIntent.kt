package com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi

import com.google.android.gms.maps.model.LatLngBounds

/**
 * User intents for Fullscreen Map screen.
 * Sealed interface captures all possible user actions.
 */
sealed interface FullscreenMapIntent {
    /** Initialize the map (load initial data). */
    data object Initialize : FullscreenMapIntent

    /** Map viewport changed (pan/zoom complete). */
    data class OnViewportChanged(val bounds: LatLngBounds) : FullscreenMapIntent

    /** User tapped a pin on the map. */
    data class OnAnimalTapped(val animalId: String) : FullscreenMapIntent

    /** User dismissed the pet details popup. */
    data object OnPopupDismissed : FullscreenMapIntent

    /** User tapped retry button after error. */
    data object OnRetryTapped : FullscreenMapIntent

    /** User pressed back button. */
    data object OnBackPressed : FullscreenMapIntent
}
