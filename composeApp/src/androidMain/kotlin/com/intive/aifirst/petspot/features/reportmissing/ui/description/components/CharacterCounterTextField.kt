package com.intive.aifirst.petspot.features.reportmissing.ui.description.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.intive.aifirst.petspot.features.reportmissing.ui.components.ReportMissingColors

/**
 * Multi-line text field with character counter for description input.
 * Enforces a maximum character limit with visual feedback.
 *
 * @param value Current text value
 * @param onValueChange Callback when text changes (already truncated to maxChars)
 * @param maxChars Maximum character limit (default 500 per FR-011)
 * @param placeholder Placeholder text when empty
 * @param modifier Modifier for the component
 */
@Composable
fun CharacterCounterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    maxChars: Int = 500,
    placeholder: String = "Enter description...",
    modifier: Modifier = Modifier,
) {
    val charCount = value.length
    val isAtLimit = charCount >= maxChars

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                // Truncate to max chars (handles paste)
                onValueChange(newValue.take(maxChars))
            },
            placeholder = {
                Text(
                    text = placeholder,
                    color = ReportMissingColors.PlaceholderColor,
                )
            },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ReportMissingColors.PrimaryBlue,
                unfocusedBorderColor = ReportMissingColors.BorderColor,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
            ),
            minLines = 4,
            maxLines = 6,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Character counter
        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$charCount/$maxChars",
                style = MaterialTheme.typography.bodySmall,
                color = if (isAtLimit) {
                    ReportMissingColors.ErrorTextColor
                } else {
                    ReportMissingColors.LabelColor
                },
                modifier = Modifier.padding(end = 4.dp),
            )
        }
    }
}

// ========================================
// Preview
// ========================================

private data class CharacterCounterTextFieldState(
    val value: String,
    val maxChars: Int = 500,
)

private class CharacterCounterTextFieldProvider :
    PreviewParameterProvider<CharacterCounterTextFieldState> {
    override val values = sequenceOf(
        // Empty state
        CharacterCounterTextFieldState(value = ""),
        // Partial fill
        CharacterCounterTextFieldState(
            value = "My dog Buddy went missing near the park. He's friendly and responds to his name.",
        ),
        // Near limit
        CharacterCounterTextFieldState(
            value = "A".repeat(480),
        ),
        // At limit
        CharacterCounterTextFieldState(
            value = "B".repeat(500),
        ),
    )
}

@Preview(name = "Character Counter TextField", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun CharacterCounterTextFieldPreview(
    @PreviewParameter(CharacterCounterTextFieldProvider::class) state: CharacterCounterTextFieldState,
) {
    MaterialTheme {
        CharacterCounterTextField(
            value = state.value,
            onValueChange = {},
            maxChars = state.maxChars,
            modifier = Modifier.padding(16.dp),
        )
    }
}

