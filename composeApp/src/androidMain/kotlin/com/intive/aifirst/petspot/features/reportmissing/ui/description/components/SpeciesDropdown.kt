package com.intive.aifirst.petspot.features.reportmissing.ui.description.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.intive.aifirst.petspot.features.reportmissing.data.SpeciesTaxonomy

// Design colors (matching ChipNumberContent)
private val PrimaryBlue = Color(0xFF155DFC)
private val BorderColor = Color(0xFFD1D5DC)
private val ErrorBorderColor = Color(0xFFFB2C36)
private val TextColor = Color(0xFF364153)

/**
 * Species dropdown composable using Material 3 ExposedDropdownMenuBox.
 * Styled to match Figma design with rounded corners, proper borders,
 * and app theme colors (white background, primary blue accent).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeciesDropdown(
    selectedSpecies: String,
    onSpeciesSelected: (String) -> Unit,
    isError: Boolean = false,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val speciesOptions = SpeciesTaxonomy.species

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.testTag("animalDescription.speciesDropdown"),
    ) {
        OutlinedTextField(
            value = selectedSpecies,
            onValueChange = { },
            readOnly = true,
            isError = isError,
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors =
                OutlinedTextFieldDefaults.colors(
                    // Focused state - primary blue (matching ChipNumberContent)
                    focusedBorderColor = if (isError) ErrorBorderColor else PrimaryBlue,
                    cursorColor = PrimaryBlue,
                    // Unfocused state
                    unfocusedBorderColor = if (isError) ErrorBorderColor else BorderColor,
                    errorBorderColor = ErrorBorderColor,
                    // Container colors
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                ),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = Color.White,
        ) {
            speciesOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.displayName, color = TextColor) },
                    onClick = {
                        onSpeciesSelected(option.displayName)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    colors =
                        MenuDefaults.itemColors(
                            textColor = TextColor,
                        ),
                )
            }
        }
    }
}

// ========================================
// Preview
// ========================================

private data class SpeciesDropdownPreviewState(
    val selectedSpecies: String,
    val isError: Boolean,
)

private class SpeciesDropdownPreviewProvider : PreviewParameterProvider<SpeciesDropdownPreviewState> {
    override val values =
        sequenceOf(
            SpeciesDropdownPreviewState(selectedSpecies = "", isError = false),
            SpeciesDropdownPreviewState(selectedSpecies = "Dog", isError = false),
            SpeciesDropdownPreviewState(selectedSpecies = "", isError = true),
        )
}

@Preview(name = "Species Dropdown", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun SpeciesDropdownPreview(
    @PreviewParameter(SpeciesDropdownPreviewProvider::class) state: SpeciesDropdownPreviewState,
) {
    MaterialTheme {
        SpeciesDropdown(
            selectedSpecies = state.selectedSpecies,
            onSpeciesSelected = {},
            isError = state.isError,
            modifier = Modifier.padding(16.dp),
        )
    }
}
