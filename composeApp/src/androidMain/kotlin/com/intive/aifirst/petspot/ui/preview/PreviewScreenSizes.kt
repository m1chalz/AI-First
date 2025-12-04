package com.intive.aifirst.petspot.ui.preview

import androidx.compose.ui.tooling.preview.Preview

/**
 * Multi-preview annotation for responsive UI validation across device width classes.
 * Validates SC-005 responsiveness requirement by generating previews for:
 * - Compact: 360×640dp (standard phone)
 * - Medium: 600×480dp (foldable inner display, small tablet)
 * - Expanded: 840×600dp (large tablet)
 *
 * Usage:
 * ```
 * @PreviewScreenSizes
 * @Composable
 * private fun MyContentPreview() {
 *     MaterialTheme {
 *         MyContent(state = MyUiState.Initial)
 *     }
 * }
 * ```
 */
@Preview(name = "Compact Phone", showBackground = true, widthDp = 360, heightDp = 640)
@Preview(name = "Medium Foldable", showBackground = true, widthDp = 600, heightDp = 480)
@Preview(name = "Expanded Tablet", showBackground = true, widthDp = 840, heightDp = 600)
annotation class PreviewScreenSizes
