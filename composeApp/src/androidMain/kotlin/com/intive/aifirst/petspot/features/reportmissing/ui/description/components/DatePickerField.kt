package com.intive.aifirst.petspot.features.reportmissing.ui.description.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// Design colors (matching ChipNumberContent)
private val PrimaryBlue = Color(0xFF155DFC)
private val BorderColor = Color(0xFFD1D5DC)
private val TextColor = Color(0xFF364153)

/**
 * Date picker field composable with Material 3 DatePickerDialog.
 * Styled to match Figma design with rounded corners and calendar icon.
 * Uses app theme colors (white background, primary blue accent).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    isDialogVisible: Boolean,
    onOpenDialog: () -> Unit,
    onDismissDialog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val displayText = selectedDate?.format(dateFormatter) ?: ""

    OutlinedTextField(
        value = displayText,
        onValueChange = { },
        readOnly = true,
        enabled = false,
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Select date",
                tint = TextColor,
            )
        },
        colors =
            OutlinedTextFieldDefaults.colors(
                // Focused state - primary blue (matching ChipNumberContent)
                focusedBorderColor = PrimaryBlue,
                cursorColor = PrimaryBlue,
                // Unfocused/disabled state
                disabledTextColor = TextColor,
                disabledBorderColor = BorderColor,
                disabledContainerColor = Color.White,
                disabledTrailingIconColor = TextColor,
            ),
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenDialog)
                .testTag("animalDescription.datePickerField"),
    )

    if (isDialogVisible) {
        val datePickerState =
            rememberDatePickerState(
                initialSelectedDateMillis = selectedDate?.toEpochMillis(),
                selectableDates = PastAndTodaySelectableDates,
            )

        DatePickerDialog(
            onDismissRequest = onDismissDialog,
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date =
                                Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            onDateSelected(date)
                        }
                    },
                ) {
                    Text("OK", color = PrimaryBlue)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDialog) {
                    Text("Cancel", color = PrimaryBlue)
                }
            },
            colors =
                DatePickerDefaults.colors(
                    containerColor = Color.White,
                ),
        ) {
            DatePicker(
                state = datePickerState,
                colors =
                    DatePickerDefaults.colors(
                        containerColor = Color.White,
                        titleContentColor = TextColor,
                        headlineContentColor = TextColor,
                        weekdayContentColor = TextColor,
                        subheadContentColor = TextColor,
                        navigationContentColor = TextColor,
                        yearContentColor = TextColor,
                        currentYearContentColor = PrimaryBlue,
                        selectedYearContentColor = Color.White,
                        selectedYearContainerColor = PrimaryBlue,
                        dayContentColor = TextColor,
                        selectedDayContentColor = Color.White,
                        selectedDayContainerColor = PrimaryBlue,
                        todayContentColor = PrimaryBlue,
                        todayDateBorderColor = PrimaryBlue,
                    ),
            )
        }
    }
}

/** Restricts date selection to today and past dates only. */
@OptIn(ExperimentalMaterial3Api::class)
private object PastAndTodaySelectableDates : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val today = LocalDate.now().toEpochMillis()
        return utcTimeMillis <= today + ONE_DAY_MILLIS // Allow today (end of day)
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year <= LocalDate.now().year
    }
}

private const val ONE_DAY_MILLIS = 24 * 60 * 60 * 1000L

private fun LocalDate.toEpochMillis(): Long = this.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

// ========================================
// Preview
// ========================================

private class DatePickerFieldPreviewProvider : PreviewParameterProvider<LocalDate?> {
    override val values =
        sequenceOf(
            null,
            LocalDate.of(2024, 11, 18),
            LocalDate.now(),
        )
}

@Preview(name = "Date Picker Field", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun DatePickerFieldPreview(
    @PreviewParameter(DatePickerFieldPreviewProvider::class) selectedDate: LocalDate?,
) {
    MaterialTheme {
        DatePickerField(
            selectedDate = selectedDate,
            onDateSelected = {},
            isDialogVisible = false,
            onOpenDialog = {},
            onDismissDialog = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
