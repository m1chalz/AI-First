package com.intive.aifirst.petspot.lib

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

/**
 * Utility object for formatting dates in the Pet Details screen.
 * Uses java.time API with core library desugaring for API 24+ support.
 */
object DateFormatter {
    private val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.US)
    private val outputFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.US)

    /**
     * Converts date from "DD/MM/YYYY" format to "MMM DD, YYYY" format.
     * Example: "18/11/2025" → "Nov 18, 2025"
     *
     * @param dateString Date in DD/MM/YYYY format
     * @return Formatted date or "—" if invalid/empty
     */
    fun formatPetDate(dateString: String?): String {
        if (dateString.isNullOrBlank()) return "—"

        return try {
            val date = LocalDate.parse(dateString, inputFormatter)
            date.format(outputFormatter)
        } catch (e: DateTimeParseException) {
            dateString // Return original string if parsing fails
        }
    }
}
