package com.intive.aifirst.petspot.features.petdetails.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Reward badge per Figma design: money bag icon with "Reward" label and amount.
 * Displayed on photo with white text.
 */
@Composable
fun RewardBadge(
    reward: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.testTag("petDetails.rewardBadge"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Paid,
            contentDescription = "Reward",
            modifier = Modifier.size(24.dp),
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "Reward",
                color = Color.White,
                fontSize = 16.sp,
                lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.height(0.dp))
            Text(
                text = reward,
                color = Color.White,
                fontSize = 16.sp,
                lineHeight = 24.sp
            )
        }
    }
}

