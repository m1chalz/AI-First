
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
 * Informational rationale dialog shown when location permission is denied.
 * Displayed when user selected "Don't Allow" or "Don't ask again".
 *
 * Uses benefit-focused messaging per FR-004:
 * - Explains value of location permission
 * - Offers path to Settings for permission grant
 * - Provides Cancel option for users who don't want location features
 *
 * @param onGoToSettings Callback when user taps "Go to Settings"
 * @param onCancel Callback when user taps "Cancel"
 */
@Composable
fun InformationalRationaleDialog(
    onGoToSettings: () -> Unit = {},
    onCancel: () -> Unit = {},
) {
    Dialog(onDismissRequest = onCancel) {
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
                        .testTag("animalList.rationaleDialog"),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Icon/Emoji
                Text(
                    text = "📍",
                    fontSize = 48.sp,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = "Location Access",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Benefit-focused message (per FR-004)
                Text(
                    text =
                        "Enable location to see missing pets near you " +
                            "and help reunite them with their families faster.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "You can enable location in Settings.",
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
                    // Cancel button
                    OutlinedButton(
                        onClick = onCancel,
                        modifier =
                            Modifier
                                .weight(1f)
                                .testTag("animalList.rationaleDialog.cancelButton"),
                    ) {
                        Text(
                            text = "Cancel",
                            textAlign = TextAlign.Center,
                        )
                    }

                    // Go to Settings button (primary action)
                    Button(
                        onClick = onGoToSettings,
                        modifier =
                            Modifier
                                .weight(1f)
                                .testTag("animalList.rationaleDialog.goToSettingsButton"),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF155DFC),
                            ),
                    ) {
                        Text(
                            text = "Go to Settings",
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InformationalRationaleDialogPreview() {
    MaterialTheme {
        InformationalRationaleDialog()
    }
}
