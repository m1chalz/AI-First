@file:Suppress("ktlint:standard:function-naming") // Composable functions use PascalCase

package com.intive.aifirst.petspot.features.animallist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Composable for displaying empty state when no animals are available.
 * Shows user-friendly message encouraging action.
 *
 * Message per FR-009: "No animals reported yet. Tap 'Report a Missing Animal' to add the first one."
 */
@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "No animals reported yet. Tap 'Report a Missing Animal' to add the first one.",
            fontSize = 16.sp,
            // Secondary text color
            color = Color(0xFF545F71),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
        )
    }
}
