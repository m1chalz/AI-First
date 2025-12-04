package com.intive.aifirst.petspot.features.reportmissing.presentation.mvi

/**
 * Photo selection status for UI rendering.
 */
enum class PhotoStatus {
    /** No photo selected - show empty state with Browse button */
    EMPTY,

    /** Photo selected, processing metadata - show loading indicator */
    LOADING,

    /** Photo ready - show confirmation card with thumbnail */
    CONFIRMED,

    /** Photo load failed - revert to empty state */
    ERROR,
}
