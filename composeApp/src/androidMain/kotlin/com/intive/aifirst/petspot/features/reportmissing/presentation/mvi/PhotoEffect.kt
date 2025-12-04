package com.intive.aifirst.petspot.features.reportmissing.presentation.mvi

/**
 * One-off UI effects for Photo screen (MVI pattern).
 * These are events that should be consumed once (photo picker launch, toasts).
 */
sealed class PhotoEffect {
    /** Launch the system photo picker */
    data object LaunchPhotoPicker : PhotoEffect()

    /** Show toast indicating photo is mandatory */
    data object ShowPhotoMandatoryToast : PhotoEffect()

    /** Show toast indicating photo metadata extraction failed */
    data object ShowMetadataFailedToast : PhotoEffect()
}
