package com.intive.aifirst.petspot.data

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.intive.aifirst.petspot.domain.usecases.PermissionChecker

/**
 * Android implementation of PermissionChecker.
 * Uses ContextCompat and ActivityCompat for permission checks.
 *
 * @param context Application context for permission checks
 * @param activityProvider Lambda to provide current Activity for rationale check
 */
class AndroidPermissionChecker(
    private val context: Context,
    private val activityProvider: (() -> Activity?)? = null,
) : PermissionChecker {
    // Track permissions that have been requested this session
    private val requestedPermissions = mutableSetOf<String>()

    override fun isPermissionGranted(permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    override fun shouldShowRationale(permission: String): Boolean {
        val activity = activityProvider?.invoke() ?: return false
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    override fun hasNeverBeenRequested(permission: String): Boolean {
        // If permission is granted, it has been requested
        if (isPermissionGranted(permission)) {
            return false
        }

        // If shouldShowRationale is true, user has denied before
        val activity = activityProvider?.invoke()
        if (activity != null && ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            return false
        }

        // Check SharedPreferences to see if we've ever requested this permission
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return !prefs.getBoolean(getPermissionKey(permission), false)
    }

    /**
     * Mark a permission as having been requested.
     * Call this after launching the permission request dialog.
     */
    fun markPermissionRequested(permission: String) {
        requestedPermissions.add(permission)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(getPermissionKey(permission), true).apply()
    }

    private fun getPermissionKey(permission: String): String = "requested_$permission"

    companion object {
        private const val PREFS_NAME = "permission_prefs"
    }
}
