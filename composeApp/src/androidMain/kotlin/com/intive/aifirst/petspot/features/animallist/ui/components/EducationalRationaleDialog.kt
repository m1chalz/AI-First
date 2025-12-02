
package com.intive.aifirst.petspot.features.animallist.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

/**
 * Educational rationale dialog shown before system permission dialog.
 * Displayed when `shouldShowRequestPermissionRationale` returns true
 * (user denied permission once without "Don't ask again").
 *
 * Uses benefit-focused messaging per FR-005:
 * - Explains why location is valuable before showing system dialog
 * - Improves permission grant rates by providing context
 * - Offers "Not Now" for users who want to skip
 *
 * @param onContinue Callback when user taps "Continue" - triggers system dialog
 * @param onNotNow Callback when user taps "Not Now" - dismisses dialog
 */
@Composable
fun EducationalRationaleDialog(
    onContinue: () -> Unit = {},
    onNotNow: () -> Unit = {},
) {
    Dialog(onDismissRequest = onNotNow) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier =
                    Modifier
                        .padding(24.dp)
                        .testTag("animalList.educationalDialog"),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Icon/Emoji
                Text(
                    text = "🐾",
                    fontSize = 48.sp,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = "Help Find Nearby Pets",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Benefit-focused message (per FR-005)
                Text(
                    text =
                        "PetSpot uses your location to show missing pets in your area. " +
                            "You might spot a lost pet on your daily walk!",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Your location data is only used to find nearby pets and is never shared.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // Not Now button
                    OutlinedButton(
                        onClick = onNotNow,
                        modifier =
                            Modifier
                                .weight(1f)
                                .testTag("animalList.educationalDialog.notNowButton"),
                    ) {
                        Text(text = "Not Now")
                    }

                    // Continue button (primary action - triggers system dialog)
                    Button(
                        onClick = onContinue,
                        modifier =
                            Modifier
                                .weight(1f)
                                .testTag("animalList.educationalDialog.continueButton"),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF155DFC),
                            ),
                    ) {
                        Text(text = "Continue")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EducationalRationaleDialogPreview() {
    MaterialTheme {
        EducationalRationaleDialog()
    }
}
