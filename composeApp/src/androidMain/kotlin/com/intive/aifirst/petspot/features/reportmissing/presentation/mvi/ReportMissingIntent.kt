package com.intive.aifirst.petspot.features.reportmissing.presentation.mvi

/**
 * Sealed interface for user intents in Missing Pet Report flow.
 * Represents all possible user actions across all 5 screens.
 */
sealed interface ReportMissingIntent {
    // Navigation intents
    data object NavigateNext : ReportMissingIntent

    data object NavigateBack : ReportMissingIntent

    // Step 1: Chip Number
    data class UpdateChipNumber(val value: String) : ReportMissingIntent

    // Step 2: Photo
    data class UpdatePhotoUri(val uri: String?) : ReportMissingIntent

    // User tapped Browse button to select a photo
    data object OpenPhotoPicker : ReportMissingIntent

    // Photo picker returned with a selected URI
    data class PhotoSelected(val uri: String) : ReportMissingIntent

    // Photo metadata loaded successfully
    data class PhotoMetadataLoaded(
        val uri: String,
        val filename: String,
        val sizeBytes: Long,
    ) : ReportMissingIntent

    // Photo loading failed (corrupted, unavailable)
    data object PhotoLoadFailed : ReportMissingIntent

    // User tapped Remove (X) button on confirmation card
    data object RemovePhoto : ReportMissingIntent

    // User dismissed photo picker without selection
    data object PhotoPickerCancelled : ReportMissingIntent

    // Step 3: Description
    data class UpdateDescription(val value: String) : ReportMissingIntent

    // Step 4: Contact Details
    data class UpdateContactEmail(val value: String) : ReportMissingIntent

    data class UpdateContactPhone(val value: String) : ReportMissingIntent

    // Summary actions (placeholder for future backend integration)
    data object Submit : ReportMissingIntent
}
