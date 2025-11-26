package com.intive.aifirst.petspot.features.petdetails.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.intive.aifirst.petspot.domain.models.AnimalStatus

/**
 * Status badge per Figma design: pill-shaped badge with status text.
 */
@Composable
fun StatusBadge(
    status: AnimalStatus,
    modifier: Modifier = Modifier
) {
    val (text, backgroundColor) = when (status) {
        AnimalStatus.ACTIVE -> "MISSING" to Color(0xFFFF0000) // Red
        AnimalStatus.FOUND -> "FOUND" to Color(0xFF0074FF)    // Blue
        AnimalStatus.CLOSED -> "CLOSED" to Color(0xFF93A2B4)  // Gray
    }
    
    Text(
        text = text,
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(50) // Pill shape per Figma
            )
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag("petDetails.statusBadge"),
        color = Color.White,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )
}

