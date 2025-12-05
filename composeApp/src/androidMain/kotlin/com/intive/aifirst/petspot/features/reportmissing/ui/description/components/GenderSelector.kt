package com.intive.aifirst.petspot.features.reportmissing.ui.description.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.intive.aifirst.petspot.features.reportmissing.domain.models.AnimalGender

// Design colors
private val PrimaryBlue = Color(0xFF155DFC)
private val BorderColor = Color(0xFFE5E9EC)
private val TextColor = Color(0xFF545F71)
private val ErrorTextColor = Color(0xFFE7000B)

/**
 * Gender selector composable with two horizontal radio button options (Female/Male).
 * Matches the Figma design with bordered containers and radio buttons.
 *
 * @param selectedGender Currently selected gender (null for none)
 * @param onGenderSelected Callback when a gender is selected
 * @param errorMessage Error message to display (null if no error)
 * @param modifier Modifier for the selector
 */
@Composable
fun GenderSelector(
    selectedGender: AnimalGender?,
    onGenderSelected: (AnimalGender) -> Unit,
    errorMessage: String? = null,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            GenderOption(
                label = "Female",
                isSelected = selectedGender == AnimalGender.FEMALE,
                onClick = { onGenderSelected(AnimalGender.FEMALE) },
                modifier =
                    Modifier
                        .weight(1f)
                        .testTag("animalDescription.genderFemale"),
            )

            GenderOption(
                label = "Male",
                isSelected = selectedGender == AnimalGender.MALE,
                onClick = { onGenderSelected(AnimalGender.MALE) },
                modifier =
                    Modifier
                        .weight(1f)
                        .testTag("animalDescription.genderMale"),
            )
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = ErrorTextColor,
            )
        }
    }
}

@Composable
private fun GenderOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .height(57.dp)
                .selectable(
                    selected = isSelected,
                    onClick = onClick,
                    role = Role.RadioButton,
                ),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(width = 1.dp, color = BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        // Box to center content vertically
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = isSelected,
                    // Handled by Card's selectable
                    onClick = null,
                    colors =
                        RadioButtonDefaults.colors(
                            selectedColor = PrimaryBlue,
                            unselectedColor = TextColor,
                        ),
                )

                // More spacing between radio button and text
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextColor,
                )
            }
        }
    }
}

// ========================================
// Preview
// ========================================

private data class GenderSelectorPreviewState(
    val selectedGender: AnimalGender?,
    val errorMessage: String?,
)

private class GenderSelectorPreviewProvider : PreviewParameterProvider<GenderSelectorPreviewState> {
    override val values =
        sequenceOf(
            GenderSelectorPreviewState(selectedGender = null, errorMessage = null),
            GenderSelectorPreviewState(selectedGender = AnimalGender.FEMALE, errorMessage = null),
            GenderSelectorPreviewState(selectedGender = AnimalGender.MALE, errorMessage = null),
            GenderSelectorPreviewState(selectedGender = null, errorMessage = "This field cannot be empty"),
        )
}

@Preview(name = "Gender Selector", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun GenderSelectorPreview(
    @PreviewParameter(GenderSelectorPreviewProvider::class) state: GenderSelectorPreviewState,
) {
    MaterialTheme {
        GenderSelector(
            selectedGender = state.selectedGender,
            onGenderSelected = {},
            errorMessage = state.errorMessage,
            modifier = Modifier.padding(16.dp),
        )
    }
}
