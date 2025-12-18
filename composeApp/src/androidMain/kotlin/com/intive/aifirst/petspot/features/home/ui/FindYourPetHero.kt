package com.intive.aifirst.petspot.features.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.intive.aifirst.petspot.R

/**
 * Hero section for the Home screen with quick navigation buttons.
 * Displays "Find Your Pet" title with two action buttons:
 * - "Lost Pet" (red) - navigates to Lost Pets tab
 * - "Found Pet" (blue) - navigates to Found Pets tab
 */
@Composable
fun FindYourPetHero(
    onLostPetClick: () -> Unit,
    onFoundPetClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(Color(0xFFE0E7FF))
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .testTag("home.findYourPetHero.container"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Title
        Text(
            text = "Find Your Pet",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF101828),
            modifier = Modifier.testTag("home.findYourPetHero.title"),
        )

        // Buttons row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Lost Pet Button (Red)
            Button(
                onClick = onLostPetClick,
                modifier =
                    Modifier
                        .weight(1f)
                        .height(56.dp)
                        .testTag("home.findYourPetHero.lostPetButton"),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFB2C36),
                    ),
                shape = RoundedCornerShape(16.dp),
                elevation =
                    ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                    ),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_warning),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.White,
                    )
                    Text(
                        text = "Lost Pet",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                    )
                }
            }

            // Found Pet Button (Blue)
            Button(
                onClick = onFoundPetClick,
                modifier =
                    Modifier
                        .weight(1f)
                        .height(56.dp)
                        .testTag("home.findYourPetHero.foundPetButton"),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF155DFC),
                    ),
                shape = RoundedCornerShape(16.dp),
                elevation =
                    ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                    ),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.White,
                    )
                    Text(
                        text = "Found Pet",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FindYourPetHeroPreview() {
    MaterialTheme {
        Surface {
            FindYourPetHero(
                onLostPetClick = {},
                onFoundPetClick = {},
            )
        }
    }
}
