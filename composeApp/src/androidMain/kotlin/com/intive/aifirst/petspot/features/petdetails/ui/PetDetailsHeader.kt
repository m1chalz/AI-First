package com.intive.aifirst.petspot.features.petdetails.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Close button overlay for Pet Details photo section.
 * Per Figma design: white circular button with X icon.
 */
@Composable
fun PetDetailsCloseButton(
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.White, CircleShape)
                .border(0.667.dp, Color(0xFFE5E9EC), CircleShape)
                .clickable(role = Role.Button, onClick = onCloseClick)
                .testTag("petDetails.backButton"),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            modifier = Modifier.size(24.dp),
            tint = Color(0xFF101828),
        )
    }
}

@Preview(name = "Close Button", showBackground = true)
@Composable
private fun PetDetailsCloseButtonPreview() {
    PetDetailsCloseButton(onCloseClick = {})
}
