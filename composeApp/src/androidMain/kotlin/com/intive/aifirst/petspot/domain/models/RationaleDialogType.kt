package com.intive.aifirst.petspot.domain.models

/**
 * Type of custom permission rationale dialog to display.
 * Determines dialog content, buttons, and user actions.
 */
sealed class RationaleDialogType {
    /**
     * Educational rationale shown before system permission dialog.
     * Displayed when `shouldShowRequestPermissionRationale` returns true.
     *
     * Actions: "Continue" (triggers system dialog), "Not Now" (dismisses)
     */
    data object Educational : RationaleDialogType()

    /**
     * Informational rationale shown when permission is denied.
     * Displayed when user selected "Don't Allow" or "Don't ask again".
     *
     * Actions: "Go to Settings" (opens app settings), "Cancel" (dismisses)
     */
    data object Informational : RationaleDialogType()
}
