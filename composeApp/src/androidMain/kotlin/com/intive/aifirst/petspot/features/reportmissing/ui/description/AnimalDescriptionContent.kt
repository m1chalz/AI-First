package com.intive.aifirst.petspot.features.reportmissing.ui.description

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.intive.aifirst.petspot.features.reportmissing.domain.models.AnimalGender
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.AnimalDescriptionUiState
import com.intive.aifirst.petspot.features.reportmissing.ui.components.ContinueButton
import com.intive.aifirst.petspot.features.reportmissing.ui.components.ReportMissingColors
import com.intive.aifirst.petspot.features.reportmissing.ui.components.ScreenTitleSection
import com.intive.aifirst.petspot.features.reportmissing.ui.components.StepHeader
import com.intive.aifirst.petspot.features.reportmissing.ui.components.StyledOutlinedTextField
import com.intive.aifirst.petspot.features.reportmissing.ui.description.components.DatePickerField
import com.intive.aifirst.petspot.features.reportmissing.ui.description.components.GenderSelector
import com.intive.aifirst.petspot.features.reportmissing.ui.description.components.SpeciesDropdown
import com.intive.aifirst.petspot.ui.preview.PreviewScreenSizes
import java.time.LocalDate

/**
 * Stateless content composable for Animal Description screen (Step 3/4).
 * Displays form fields for pet details matching the Figma design.
 *
 * Follows the same edge-to-edge pattern as ChipNumberContent and PhotoContent:
 * - statusBarsPadding() on main Column
 * - navigationBarsPadding() on Continue button (via ContinueButton component)
 */
@Composable
fun AnimalDescriptionContent(
    state: AnimalDescriptionUiState,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onDateClick: () -> Unit = {},
    onDateSelected: (LocalDate) -> Unit = {},
    onDatePickerDismiss: () -> Unit = {},
    onPetNameChanged: (String) -> Unit = {},
    onSpeciesSelected: (String) -> Unit = {},
    onRaceChanged: (String) -> Unit = {},
    onGenderSelected: (AnimalGender) -> Unit = {},
    onAgeChanged: (String) -> Unit = {},
    onContinueClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .testTag("animalDescription.content"),
    ) {
        // Header with back button, title, and progress indicator
        StepHeader(
            title = "Animal description",
            currentStep = 3,
            onBackClick = onBackClick,
            modifier = Modifier.testTag("animalDescription.backButton"),
        )

        // Main content - scrollable, aligned to top
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            ScreenTitleSection(
                title = "Your pet's details",
                subtitle = "Fill out the details about the missing animal.",
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Date of disappearance
            FormField(label = "Date of disappearance") {
                DatePickerField(
                    selectedDate = state.disappearanceDate,
                    onDateSelected = onDateSelected,
                    isDialogVisible = state.isDatePickerVisible,
                    onOpenDialog = onDateClick,
                    onDismissDialog = onDatePickerDismiss,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animal name (optional)
            FormField(label = "Animal name (optional)") {
                StyledOutlinedTextField(
                    value = state.petName,
                    onValueChange = onPetNameChanged,
                    placeholder = "-",
                    modifier = Modifier.testTag("animalDescription.petNameField"),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animal species (required)
            FormField(
                label = "Animal species",
                errorMessage = state.speciesError,
            ) {
                SpeciesDropdown(
                    selectedSpecies = state.animalSpecies,
                    onSpeciesSelected = onSpeciesSelected,
                    isError = state.speciesError != null,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animal race (required, enabled after species selection)
            FormField(
                label = "Animal race",
                labelColor = if (state.isRaceFieldEnabled) {
                    ReportMissingColors.LabelColor
                } else {
                    ReportMissingColors.DisabledLabelColor
                },
                errorMessage = state.raceError,
            ) {
                StyledOutlinedTextField(
                    value = state.animalRace,
                    onValueChange = onRaceChanged,
                    placeholder = "-",
                    enabled = state.isRaceFieldEnabled,
                    isError = state.raceError != null,
                    modifier = Modifier.testTag("animalDescription.raceField"),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Gender selector (required)
            GenderSelector(
                selectedGender = state.animalGender,
                onGenderSelected = onGenderSelected,
                errorMessage = state.genderError,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Animal age (optional)
            FormField(
                label = "Animal age (optional)",
                errorMessage = state.ageError,
            ) {
                StyledOutlinedTextField(
                    value = state.animalAge,
                    onValueChange = onAgeChanged,
                    placeholder = "-",
                    keyboardType = KeyboardType.Number,
                    isError = state.ageError != null,
                    modifier = Modifier.testTag("animalDescription.ageField"),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Continue button - stays at bottom with navigation bar padding
        ContinueButton(
            onClick = onContinueClick,
            modifier = Modifier.testTag("animalDescription.continueButton"),
        )
    }
}

/**
 * Form field wrapper with label above the content.
 */
@Composable
private fun FormField(
    label: String,
    labelColor: Color = ReportMissingColors.LabelColor,
    errorMessage: String? = null,
    content: @Composable () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = labelColor,
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = ReportMissingColors.ErrorTextColor,
            )
        }
    }
}

// ========================================
// Preview Parameter Provider
// ========================================

class AnimalDescriptionUiStateProvider : PreviewParameterProvider<AnimalDescriptionUiState> {
    override val values: Sequence<AnimalDescriptionUiState> = sequenceOf(
        // Empty state
        AnimalDescriptionUiState(disappearanceDate = LocalDate.now()),
        // Validation error state
        AnimalDescriptionUiState(
            disappearanceDate = LocalDate.now(),
            speciesError = "This field cannot be empty",
            raceError = "This field cannot be empty",
            genderError = "This field cannot be empty",
        ),
        // Filled state
        AnimalDescriptionUiState(
            disappearanceDate = LocalDate.of(2024, 11, 18),
            petName = "Buddy",
            animalSpecies = "Dog",
            animalRace = "Labrador",
            animalGender = AnimalGender.FEMALE,
            animalAge = "5",
        ),
    )
}

// ========================================
// Previews
// ========================================

@Preview(name = "Animal Description Content", showBackground = true)
@PreviewScreenSizes
@Composable
private fun AnimalDescriptionContentPreview(
    @PreviewParameter(AnimalDescriptionUiStateProvider::class) state: AnimalDescriptionUiState,
) {
    MaterialTheme {
        AnimalDescriptionContent(state = state)
    }
}
