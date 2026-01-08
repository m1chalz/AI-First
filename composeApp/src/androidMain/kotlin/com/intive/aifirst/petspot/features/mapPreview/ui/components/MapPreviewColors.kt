package com.intive.aifirst.petspot.features.mapPreview.ui.components

import androidx.compose.ui.graphics.Color

/**
 * Shared design color constants for the Map Preview feature.
 * Aligned with Figma design specifications.
 */
object MapPreviewColors {
    // Primary
    /** Primary blue - buttons, loader, interactive elements - #155DFC */
    val Primary = Color(0xFF155DFC)

    // Header & Text
    /** Header text color - #101828 */
    val HeaderText = Color(0xFF101828)

    /** Legend label text color - #4A5565 */
    val LegendText = Color(0xFF4A5565)

    /** Overlay text color (same as header) - #101828 */
    val OverlayText = Color(0xFF101828)

    /** Subtitle/body text color - #545F71 */
    val SubtitleText = Color(0xFF545F71)

    // Map
    /** Map container background color - #E5E7EB */
    val MapBackground = Color(0xFFE5E7EB)

    // Pin Colors
    /** Missing pet pin - Red #FB2C36 */
    val MissingPin = Color(0xFFFB2C36)

    /** Found pet pin - Blue #2B7FFF */
    val FoundPin = Color(0xFF2B7FFF)

    // Error State
    /** Error icon color - Red #FB2C36 */
    val ErrorIcon = Color(0xFFFB2C36)
}
