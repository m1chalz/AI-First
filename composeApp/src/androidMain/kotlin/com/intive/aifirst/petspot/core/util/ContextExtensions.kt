package com.intive.aifirst.petspot.core.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 * Opens the app's system settings page where users can manage permissions,
 * storage, notifications, and other app-specific settings.
 *
 * Useful for directing users to enable permissions that were permanently denied.
 */
fun Context.openAppSettings() {
    val intent =
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null),
        )
    startActivity(intent)
}
