package com.intive.aifirst.petspot.domain.usecases

/**
 * Interface for checking Android permissions.
 * Abstracts Android permission checking for testability.
 */
interface PermissionChecker {
    /**
     * Checks if a specific permission is granted.
     *
     * @param permission The permission to check (e.g., Manifest.permission.ACCESS_FINE_LOCATION)
     * @return true if permission is granted
     */
    fun isPermissionGranted(permission: String): Boolean

    /**
     * Checks if rationale should be shown for a permission.
     * Returns true when the user has previously denied the permission
     * but hasn't selected "Don't ask again".
     *
     * @param permission The permission to check
     * @return true if rationale should be shown before requesting again
     */
    fun shouldShowRationale(permission: String): Boolean

    /**
     * Checks if permission has never been requested.
     * Used to distinguish between "never requested" and "denied" states.
     *
     * @param permission The permission to check
     * @return true if permission has never been requested
     */
    fun hasNeverBeenRequested(permission: String): Boolean
}
