package com.intive.aifirst.petspot.features.reportmissing.ui.components

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Styled OutlinedTextField for the Report Missing Pet flow.
 * Matches Figma design with consistent colors, borders, and rounded corners.
 *
 * @param value Current text value
 * @param onValueChange Callback when text changes
 * @param modifier Modifier for the text field
 * @param placeholder Placeholder text (displayed when empty)
 * @param enabled Whether the field is enabled
 * @param isError Whether to display error state
 * @param keyboardType Keyboard type for input
 * @param visualTransformation Visual transformation for the input (e.g., masking)
 * @param singleLine Whether the field is single line
 * @param interactionSource Optional interaction source for focus/press states
 */
@Composable
fun StyledOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            if (placeholder.isNotEmpty()) {
                Text(
                    text = placeholder,
                    color = ReportMissingColors.PlaceholderColor,
                )
            }
        },
        enabled = enabled,
        isError = isError,
        singleLine = singleLine,
        shape = RoundedCornerShape(10.dp),
        visualTransformation = visualTransformation,
        interactionSource = interactionSource,
        colors = OutlinedTextFieldDefaults.colors(
            // Focused state - primary blue
            focusedBorderColor = ReportMissingColors.PrimaryBlue,
            cursorColor = ReportMissingColors.PrimaryBlue,
            // Unfocused state
            unfocusedBorderColor = ReportMissingColors.BorderColor,
            disabledBorderColor = ReportMissingColors.BorderColor,
            // Error state
            errorBorderColor = ReportMissingColors.ErrorBorderColor,
            // Container colors
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier.fillMaxWidth(),
    )
}

// ========================================
// Preview
// ========================================

/**
 * Custom MutableInteractionSource that starts with a focused state.
 * Used for previews to show the focused appearance.
 */
private class PreviewFocusedInteractionSource : MutableInteractionSource {
    private val _interactions = MutableStateFlow<Interaction>(FocusInteraction.Focus())
    override val interactions: Flow<Interaction> = _interactions

    override suspend fun emit(interaction: Interaction) {
        _interactions.value = interaction
    }

    override fun tryEmit(interaction: Interaction): Boolean {
        _interactions.value = interaction
        return true
    }
}

private data class StyledOutlinedTextFieldState(
    val value: String,
    val placeholder: String,
    val enabled: Boolean,
    val isError: Boolean,
    val isFocused: Boolean,
)

private class StyledOutlinedTextFieldProvider : PreviewParameterProvider<StyledOutlinedTextFieldState> {
    override val values = sequenceOf(
        // Empty with placeholder
        StyledOutlinedTextFieldState(
            value = "",
            placeholder = "00000-00000-00000",
            enabled = true,
            isError = false,
            isFocused = false,
        ),
        // Focused (shows primary blue border)
        StyledOutlinedTextFieldState(
            value = "Typing...",
            placeholder = "-",
            enabled = true,
            isError = false,
            isFocused = true,
        ),
        // Filled
        StyledOutlinedTextFieldState(
            value = "Buddy",
            placeholder = "-",
            enabled = true,
            isError = false,
            isFocused = false,
        ),
        // Disabled
        StyledOutlinedTextFieldState(
            value = "",
            placeholder = "-",
            enabled = false,
            isError = false,
            isFocused = false,
        ),
        // Error state
        StyledOutlinedTextFieldState(
            value = "",
            placeholder = "-",
            enabled = true,
            isError = true,
            isFocused = false,
        ),
    )
}

@Preview(name = "Styled Outlined TextField", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun StyledOutlinedTextFieldPreview(
    @PreviewParameter(StyledOutlinedTextFieldProvider::class) state: StyledOutlinedTextFieldState,
) {
    MaterialTheme {
        val interactionSource = remember(state.isFocused) {
            if (state.isFocused) PreviewFocusedInteractionSource() else MutableInteractionSource()
        }

        StyledOutlinedTextField(
            value = state.value,
            onValueChange = {},
            placeholder = state.placeholder,
            enabled = state.enabled,
            isError = state.isError,
            interactionSource = interactionSource,
            modifier = Modifier.padding(16.dp),
        )
    }
}
