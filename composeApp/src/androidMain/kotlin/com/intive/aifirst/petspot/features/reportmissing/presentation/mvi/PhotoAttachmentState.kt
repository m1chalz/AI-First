package com.intive.aifirst.petspot.features.reportmissing.presentation.mvi

import com.intive.aifirst.petspot.core.util.FileSizeFormatter

/**
 * State representing photo attachment in the missing pet flow.
 * Immutable - create new instances for state changes.
 * Note: No Parcelize needed - state persists via ReportMissingFlowState, not SavedStateHandle.
 */
data class PhotoAttachmentState(
    /** Content URI of the selected photo, null if no selection */
    val uri: String? = null,
    /** Display filename (truncated to 20 chars with ellipsis in UI) */
    val filename: String? = null,
    /** File size in bytes */
    val sizeBytes: Long = 0,
    /** Current UI state of photo selection */
    val status: PhotoStatus = PhotoStatus.EMPTY,
) {
    companion object {
        val Empty = PhotoAttachmentState()
    }

    /** Whether a valid photo is currently attached */
    val hasPhoto: Boolean
        get() = uri != null && status == PhotoStatus.CONFIRMED

    /** Human-readable file size (e.g., "1.2 MB") */
    val formattedSize: String
        get() = FileSizeFormatter.format(sizeBytes)

    /** Filename truncated to max 20 characters with ellipsis */
    val displayFilename: String
        get() =
            filename?.let {
                if (it.length > 20) it.take(17) + "..." else it
            } ?: ""
}
