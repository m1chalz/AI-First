package com.intive.aifirst.petspot.features.reportmissing.ui.description.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.intive.aifirst.petspot.features.reportmissing.ui.components.ReportMissingColors
import com.intive.aifirst.petspot.features.reportmissing.ui.components.StyledOutlinedTextField

/**
 * GPS Location section for the Animal Description screen.
 * Provides a Request GPS button and latitude/longitude input fields.
 * Styled according to Figma design with outlined button and "00000" placeholders.
 *
 * @param latitude Current latitude value (as String for text field)
 * @param longitude Current longitude value (as String for text field)
 * @param onRequestGps Callback when Request GPS button is clicked
 * @param onLatitudeChanged Callback when latitude field changes
 * @param onLongitudeChanged Callback when longitude field changes
 * @param isLoading True if GPS request is in progress
 * @param latitudeError Error message for latitude field (null if valid)
 * @param longitudeError Error message for longitude field (null if valid)
 * @param modifier Modifier for the section
 */
@Composable
fun GpsLocationSection(
    latitude: String,
    longitude: String,
    onRequestGps: () -> Unit,
    onLatitudeChanged: (String) -> Unit,
    onLongitudeChanged: (String) -> Unit,
    isLoading: Boolean = false,
    latitudeError: String? = null,
    longitudeError: String? = null,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Request GPS Position button - outlined style per Figma
        OutlinedButton(
            onClick = onRequestGps,
            enabled = !isLoading,
            shape = RoundedCornerShape(10.dp),
            border =
                BorderStroke(
                    width = 2.dp,
                    color = if (isLoading) ReportMissingColors.BorderColor else ReportMissingColors.PrimaryBlue,
                ),
            colors =
                ButtonDefaults.outlinedButtonColors(
                    contentColor = ReportMissingColors.PrimaryBlue,
                    disabledContentColor = ReportMissingColors.BorderColor,
                ),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .testTag("animalDescription.requestGpsButton"),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = ReportMissingColors.PrimaryBlue,
                    strokeWidth = 2.dp,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Getting location...")
            } else {
                Text("Request GPS position")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lat / Long label (single label above both fields per Figma)
        Text(
            text = "Lat / Long",
            style = MaterialTheme.typography.bodyMedium,
            color = ReportMissingColors.LabelColor,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Latitude and Longitude fields in a row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Latitude field
            StyledOutlinedTextField(
                value = latitude,
                onValueChange = onLatitudeChanged,
                placeholder = "00000",
                keyboardType = KeyboardType.Decimal,
                isError = latitudeError != null,
                modifier =
                    Modifier
                        .weight(1f)
                        .testTag("animalDescription.latitudeField"),
            )

            // Longitude field
            StyledOutlinedTextField(
                value = longitude,
                onValueChange = onLongitudeChanged,
                placeholder = "00000",
                keyboardType = KeyboardType.Decimal,
                isError = longitudeError != null,
                modifier =
                    Modifier
                        .weight(1f)
                        .testTag("animalDescription.longitudeField"),
            )
        }

        // Error message (single message below both fields per Figma)
        if (latitudeError != null || longitudeError != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = latitudeError ?: longitudeError ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = ReportMissingColors.ErrorTextColor,
            )
        }
    }
}

// ========================================
// Preview
// ========================================

private data class GpsLocationSectionState(
    val latitude: String,
    val longitude: String,
    val isLoading: Boolean,
    val latitudeError: String?,
    val longitudeError: String?,
)

private class GpsLocationSectionProvider : PreviewParameterProvider<GpsLocationSectionState> {
    override val values =
        sequenceOf(
            // Empty state
            GpsLocationSectionState(
                latitude = "",
                longitude = "",
                isLoading = false,
                latitudeError = null,
                longitudeError = null,
            ),
            // Loading state
            GpsLocationSectionState(
                latitude = "",
                longitude = "",
                isLoading = true,
                latitudeError = null,
                longitudeError = null,
            ),
            // Filled state (GPS success fills fields)
            GpsLocationSectionState(
                latitude = "52.2297",
                longitude = "21.0122",
                isLoading = false,
                latitudeError = null,
                longitudeError = null,
            ),
            // Error latitude state
            GpsLocationSectionState(
                latitude = "invalid",
                longitude = "20",
                isLoading = false,
                latitudeError = "Invalid latitude format",
                longitudeError = null,
            ),
            // Error longitude state
            GpsLocationSectionState(
                latitude = "80",
                longitude = "invalid",
                isLoading = false,
                latitudeError = null,
                longitudeError = "Invalid longitude format",
            ),
        )
}

@Preview(name = "GPS Location Section", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun GpsLocationSectionPreview(
    @PreviewParameter(GpsLocationSectionProvider::class) state: GpsLocationSectionState,
) {
    MaterialTheme {
        GpsLocationSection(
            latitude = state.latitude,
            longitude = state.longitude,
            onRequestGps = {},
            onLatitudeChanged = {},
            onLongitudeChanged = {},
            isLoading = state.isLoading,
            latitudeError = state.latitudeError,
            longitudeError = state.longitudeError,
            modifier = Modifier.padding(16.dp),
        )
    }
}
