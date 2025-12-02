package com.intive.aifirst.petspot.features.reportmissing.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Formats microchip numbers for display and provides utility methods.
 * Format: 00000-00000-00000 (hyphens at positions 6 and 12)
 */
object MicrochipNumberFormatter {

    const val MAX_DIGITS = 15

    /**
     * Formats raw digits with hyphens: 00000-00000-00000
     *
     * Examples:
     * - "12345" → "12345"
     * - "123456" → "12345-6"
     * - "123456789012345" → "12345-67890-12345"
     */
    fun format(input: String): String {
        val digits = extractDigits(input).take(MAX_DIGITS)
        return buildString {
            digits.forEachIndexed { index, char ->
                if (index == 5 || index == 10) append('-')
                append(char)
            }
        }
    }

    /**
     * Extracts only numeric digits from input string.
     *
     * Examples:
     * - "12345-67890-12345" → "123456789012345"
     * - "abc123def456" → "123456"
     */
    fun extractDigits(input: String): String = input.filter { it.isDigit() }
}

/**
 * VisualTransformation that displays microchip numbers with hyphens.
 * Raw digits stored in state; formatted display shown to user.
 */
class MicrochipVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val formatted = MicrochipNumberFormatter.format(text.text)
        return TransformedText(
            AnnotatedString(formatted),
            MicrochipOffsetMapping(text.text.length)
        )
    }
}

/**
 * Maps cursor positions between raw digits and formatted display.
 * Handles hyphen insertion at positions 5 and 10.
 */
private class MicrochipOffsetMapping(private val originalLength: Int) : OffsetMapping {

    override fun originalToTransformed(offset: Int): Int {
        // Add 1 for each hyphen before this position
        return when {
            offset <= 5 -> offset
            offset <= 10 -> offset + 1  // After first hyphen
            else -> offset + 2           // After second hyphen
        }
    }

    override fun transformedToOriginal(offset: Int): Int {
        // Subtract hyphens from position
        return when {
            offset <= 5 -> offset
            offset <= 11 -> offset - 1   // Account for first hyphen
            else -> offset - 2            // Account for both hyphens
        }.coerceIn(0, originalLength)
    }
}

