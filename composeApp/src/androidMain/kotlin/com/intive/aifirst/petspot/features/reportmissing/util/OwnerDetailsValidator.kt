package com.intive.aifirst.petspot.features.reportmissing.util

/**
 * Result type for Owner's Details field validation.
 * Valid/Invalid for single-field validation with error message.
 */
sealed interface OwnerDetailsValidationResult {
    data object Valid : OwnerDetailsValidationResult
    data class Invalid(val message: String) : OwnerDetailsValidationResult
}

/**
 * Pure validation functions for Owner's Details screen fields.
 */
object OwnerDetailsValidator {
    private const val MIN_PHONE_DIGITS = 7
    private const val MAX_PHONE_DIGITS = 11

    /**
     * Validates phone number.
     * - Sanitizes whitespace and dashes before counting
     * - Allows leading + but doesn't count it
     * - Requires 7-11 digits
     * - Rejects letters
     */
    fun validatePhone(phone: String): OwnerDetailsValidationResult {
        val trimmed = phone.trim()

        // Check for letters - reject if any found
        if (trimmed.any { it.isLetter() }) {
            return OwnerDetailsValidationResult.Invalid("Enter at least 7 digits")
        }

        // Sanitize: remove whitespace, dashes, parentheses, dots
        val sanitized = trimmed.filter { it.isDigit() || it == '+' }

        // Extract digits only (remove leading + for counting)
        val digitsOnly = sanitized.removePrefix("+").filter { it.isDigit() }

        return if (digitsOnly.length in MIN_PHONE_DIGITS..MAX_PHONE_DIGITS) {
            OwnerDetailsValidationResult.Valid
        } else {
            OwnerDetailsValidationResult.Invalid("Enter at least 7 digits")
        }
    }

    /**
     * Validates email address.
     * - RFC 5322 basic pattern (local@domain.tld)
     * - Case-insensitive
     * - Trims whitespace
     */
    fun validateEmail(email: String): OwnerDetailsValidationResult {
        val trimmed = email.trim()

        // Basic RFC 5322 pattern: something@something.something
        val emailPattern = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")

        return if (emailPattern.matches(trimmed)) {
            OwnerDetailsValidationResult.Valid
        } else {
            OwnerDetailsValidationResult.Invalid("Enter a valid email address")
        }
    }
}

