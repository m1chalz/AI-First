package com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi

/**
 * Sealed interface representing error types for the fullscreen map.
 * UI layer is responsible for mapping these to user-facing strings.
 */
sealed interface FullscreenMapError {
    /** Location permission was denied or not granted */
    data object PermissionDenied : FullscreenMapError

    /** Network connectivity issue */
    data object NetworkError : FullscreenMapError

    /** Generic/unknown error */
    data object Unknown : FullscreenMapError
}
