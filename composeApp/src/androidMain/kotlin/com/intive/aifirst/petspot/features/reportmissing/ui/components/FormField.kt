package com.intive.aifirst.petspot.features.reportmissing.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Form field wrapper with label above the content.
 * Used throughout the Report Missing Pet flow for consistent field styling.
 *
 * @param label Label text displayed above the field
 * @param modifier Modifier for the component
 * @param labelColor Color for the label text (defaults to LabelColor, use ErrorTextColor when invalid)
 * @param errorMessage Optional error message to display below the field
 * @param content The field content (e.g., StyledOutlinedTextField, Dropdown, etc.)
 */
@Composable
fun FormField(
    label: String,
    modifier: Modifier = Modifier,
    labelColor: Color = ReportMissingColors.LabelColor,
    errorMessage: String? = null,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (errorMessage != null) ReportMissingColors.ErrorTextColor else labelColor,
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
