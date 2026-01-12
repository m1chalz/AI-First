package com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi

/**
 * One-off effects for Fullscreen Map screen.
 * Used for navigation and other side effects that shouldn't persist in state.
 */
sealed interface FullscreenMapEffect {
    /** Navigate back to previous screen. */
    data object NavigateBack : FullscreenMapEffect
}
