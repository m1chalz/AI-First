package com.intive.aifirst.petspot.features.mapPreview.presentation.mvi

/**
 * Error types for map preview feature.
 * Used for exhaustive error handling in UI.
 */
sealed interface MapPreviewError {
    /** Location could not be determined (GPS unavailable, timeout) */
    data object LocationNotAvailable : MapPreviewError

    /** Network request failed (no connectivity, server error) */
    data object NetworkError : MapPreviewError

    /** Map SDK failed to load or render */
    data object MapLoadFailed : MapPreviewError
}
