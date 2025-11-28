package com.intive.aifirst.petspot.domain.models

/**
 * Location permission authorization state on Android.
 * Represents all possible states of location permission handling.
 */
sealed class PermissionStatus {
    /**
     * Permission has not been requested yet (first app launch).
     */
    data object NotRequested : PermissionStatus()

    /**
     * Permission request is in progress (system dialog displayed).
     */
    data object Requesting : PermissionStatus()

    /**
     * Permission granted by user.
     *
     * @property fineLocation True when ACCESS_FINE_LOCATION granted
     * @property coarseLocation True when ACCESS_COARSE_LOCATION granted (may be true without fineLocation on Android 12+)
     */
    data class Granted(
        val fineLocation: Boolean,
        val coarseLocation: Boolean,
    ) : PermissionStatus() {
        init {
            require(fineLocation || coarseLocation) {
                "At least one location permission must be granted"
            }
        }
    }

    /**
     * Permission denied by user.
     *
     * @property shouldShowRationale True when educational rationale should be shown before next request,
     *                               false when user selected "Don't ask again"
     */
    data class Denied(
        val shouldShowRationale: Boolean,
    ) : PermissionStatus()
}
