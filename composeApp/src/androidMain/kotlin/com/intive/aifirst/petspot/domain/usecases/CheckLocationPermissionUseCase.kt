package com.intive.aifirst.petspot.domain.usecases

import android.Manifest
import com.intive.aifirst.petspot.domain.models.PermissionStatus

/**
 * Use case for checking current location permission status.
 *
 * Checks both ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION permissions
 * and determines the appropriate PermissionStatus:
 * - NotRequested: Permission has never been requested
 * - Granted: At least one location permission is granted
 * - Denied: Permission denied, with shouldShowRationale flag
 */
class CheckLocationPermissionUseCase(
    private val permissionChecker: PermissionChecker,
) {
    /**
     * Invokes the use case to check current permission status.
     *
     * @return Current permission status
     */
    operator fun invoke(): PermissionStatus {
        val fineGranted =
            permissionChecker.isPermissionGranted(
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        val coarseGranted =
            permissionChecker.isPermissionGranted(
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )

        // If any permission is granted, return Granted
        if (fineGranted || coarseGranted) {
            return PermissionStatus.Granted(
                fineLocation = fineGranted,
                coarseLocation = coarseGranted,
            )
        }

        // Check if permission has never been requested
        val neverRequested =
            permissionChecker.hasNeverBeenRequested(
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        if (neverRequested) {
            return PermissionStatus.NotRequested
        }

        // Permission denied - check if rationale should be shown
        val shouldShowRationale =
            permissionChecker.shouldShowRationale(
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) ||
                permissionChecker.shouldShowRationale(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )

        return PermissionStatus.Denied(shouldShowRationale = shouldShowRationale)
    }
}
