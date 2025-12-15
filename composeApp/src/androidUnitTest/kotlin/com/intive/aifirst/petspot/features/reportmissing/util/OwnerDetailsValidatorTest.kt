package com.intive.aifirst.petspot.features.reportmissing.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * Unit tests for [OwnerDetailsValidator].
 * Tests phone validation (7-11 digits, leading +, sanitization) and email validation (RFC 5322 basic).
 */
class OwnerDetailsValidatorTest {
    // ═══════════════════════════════════════════════════════════════════════════
    // Phone Validation Tests
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    fun `validatePhone should return Valid for 7 digits`() {
        // Given
        val phone = "1234567"

        // When
        val result = OwnerDetailsValidator.validatePhone(phone)

        // Then
        assertIs<OwnerDetailsValidationResult.Valid>(result)
    }

    @Test
    fun `validatePhone should return Valid for 11 digits`() {
        // Given
        val phone = "12345678901"

        // When
        val result = OwnerDetailsValidator.validatePhone(phone)

        // Then
        assertIs<OwnerDetailsValidationResult.Valid>(result)
    }

    @Test
    fun `validatePhone should return Valid for phone with leading plus`() {
        // Given
        val phone = "+48123456789"

        // When
        val result = OwnerDetailsValidator.validatePhone(phone)

        // Then
        assertIs<OwnerDetailsValidationResult.Valid>(result)
    }

    @Test
    fun `validatePhone should return Valid when sanitizing whitespace`() {
        // Given
        val phone = "123 456 789"

        // When
        val result = OwnerDetailsValidator.validatePhone(phone)

        // Then
        assertIs<OwnerDetailsValidationResult.Valid>(result)
    }

    @Test
    fun `validatePhone should return Valid when sanitizing dashes`() {
        // Given
        val phone = "123-456-789"

        // When
        val result = OwnerDetailsValidator.validatePhone(phone)

        // Then
        assertIs<OwnerDetailsValidationResult.Valid>(result)
    }

    @Test
    fun `validatePhone should return Valid for mixed format with plus spaces and dashes`() {
        // Given
        val phone = "+48 123-456-789"

        // When
        val result = OwnerDetailsValidator.validatePhone(phone)

        // Then
        assertIs<OwnerDetailsValidationResult.Valid>(result)
    }

    @Test
    fun `validatePhone should return Invalid for less than 7 digits`() {
        // Given
        val phone = "123456"

        // When
        val result = OwnerDetailsValidator.validatePhone(phone)

        // Then
        assertIs<OwnerDetailsValidationResult.Invalid>(result)
        assertEquals("Enter at least 7 digits", result.message)
    }

    @Test
    fun `validatePhone should return Invalid for more than 11 digits with correct message`() {
        // Given
        val phone = "123456789012"

        // When
        val result = OwnerDetailsValidator.validatePhone(phone)

        // Then
        assertIs<OwnerDetailsValidationResult.Invalid>(result)
        assertEquals("Enter no more than 11 digits", result.message)
    }

    @Test
    fun `validatePhone should return Invalid for more than 11 digits`() {
        // Given
        val phone = "123456789012"

        // When
        val result = OwnerDetailsValidator.validatePhone(phone)

        // Then
        assertIs<OwnerDetailsValidationResult.Invalid>(result)
    }

    @Test
    fun `validatePhone should return Invalid for empty string`() {
        // Given
        val phone = ""

        // When
        val result = OwnerDetailsValidator.validatePhone(phone)

        // Then
        assertIs<OwnerDetailsValidationResult.Invalid>(result)
    }

    @Test
    fun `validatePhone should return Invalid for phone containing letters`() {
        // Given
        val phone = "123456abc"

        // When
        val result = OwnerDetailsValidator.validatePhone(phone)

        // Then
        assertIs<OwnerDetailsValidationResult.Invalid>(result)
        assertEquals("Phone number cannot contain letters", result.message)
    }

    @Test
    fun `validatePhone should return Invalid for only whitespace`() {
        // Given
        val phone = "   "

        // When
        val result = OwnerDetailsValidator.validatePhone(phone)

        // Then
        assertIs<OwnerDetailsValidationResult.Invalid>(result)
    }

    @Test
    fun `validatePhone should not count leading plus as digit`() {
        // Given - 6 digits + leading plus should be invalid (need 7 digits)
        val phone = "+123456"

        // When
        val result = OwnerDetailsValidator.validatePhone(phone)

        // Then
        assertIs<OwnerDetailsValidationResult.Invalid>(result)
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Email Validation Tests
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    fun `validateEmail should return Valid for standard email`() {
        // Given
        val email = "owner@example.com"

        // When
        val result = OwnerDetailsValidator.validateEmail(email)

        // Then
        assertIs<OwnerDetailsValidationResult.Valid>(result)
    }

    @Test
    fun `validateEmail should return Valid for email with subdomain`() {
        // Given
        val email = "user@mail.example.com"

        // When
        val result = OwnerDetailsValidator.validateEmail(email)

        // Then
        assertIs<OwnerDetailsValidationResult.Valid>(result)
    }

    @Test
    fun `validateEmail should return Valid for email with plus sign`() {
        // Given
        val email = "user+tag@example.com"

        // When
        val result = OwnerDetailsValidator.validateEmail(email)

        // Then
        assertIs<OwnerDetailsValidationResult.Valid>(result)
    }

    @Test
    fun `validateEmail should return Valid for email with dots in local part`() {
        // Given
        val email = "first.last@example.com"

        // When
        val result = OwnerDetailsValidator.validateEmail(email)

        // Then
        assertIs<OwnerDetailsValidationResult.Valid>(result)
    }

    @Test
    fun `validateEmail should return Valid after trimming whitespace`() {
        // Given
        val email = "  owner@example.com  "

        // When
        val result = OwnerDetailsValidator.validateEmail(email)

        // Then
        assertIs<OwnerDetailsValidationResult.Valid>(result)
    }

    @Test
    fun `validateEmail should return Invalid for missing at sign`() {
        // Given
        val email = "ownerexample.com"

        // When
        val result = OwnerDetailsValidator.validateEmail(email)

        // Then
        assertIs<OwnerDetailsValidationResult.Invalid>(result)
        assertEquals("Enter a valid email address", result.message)
    }

    @Test
    fun `validateEmail should return Invalid for missing domain`() {
        // Given
        val email = "owner@"

        // When
        val result = OwnerDetailsValidator.validateEmail(email)

        // Then
        assertIs<OwnerDetailsValidationResult.Invalid>(result)
    }

    @Test
    fun `validateEmail should return Invalid for missing TLD`() {
        // Given
        val email = "owner@example"

        // When
        val result = OwnerDetailsValidator.validateEmail(email)

        // Then
        assertIs<OwnerDetailsValidationResult.Invalid>(result)
    }

    @Test
    fun `validateEmail should return Invalid for missing local part`() {
        // Given
        val email = "@example.com"

        // When
        val result = OwnerDetailsValidator.validateEmail(email)

        // Then
        assertIs<OwnerDetailsValidationResult.Invalid>(result)
    }

    @Test
    fun `validateEmail should return Invalid for empty string`() {
        // Given
        val email = ""

        // When
        val result = OwnerDetailsValidator.validateEmail(email)

        // Then
        assertIs<OwnerDetailsValidationResult.Invalid>(result)
    }

    @Test
    fun `validateEmail should return Invalid for email with space in middle`() {
        // Given
        val email = "owner @example.com"

        // When
        val result = OwnerDetailsValidator.validateEmail(email)

        // Then
        assertIs<OwnerDetailsValidationResult.Invalid>(result)
    }
}
