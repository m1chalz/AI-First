package com.intive.aifirst.petspot.features.mapPreview.presentation.mvi

/**
 * User intents for Map Preview component.
 * Sealed interface captures all possible user actions.
 */
sealed interface MapPreviewIntent {
    /** Load map data (location + nearby animals) */
    data object LoadMap : MapPreviewIntent

    /** Request location permission from user */
    data object RequestPermission : MapPreviewIntent

    /** System callback: permission was granted */
    data object PermissionGranted : MapPreviewIntent

    /** System callback: permission was denied */
    data object PermissionDenied : MapPreviewIntent

    /** Retry loading after error */
    data object Retry : MapPreviewIntent
}
