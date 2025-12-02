package com.intive.aifirst.petspot.features.reportmissing.util

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests for MicrochipNumberFormatter.
 * Tests format() and extractDigits() methods following Given-When-Then structure.
 */
class MicrochipNumberFormatterTest {

    // ===== format() tests =====

    @Test
    fun `format should return unchanged when input has fewer than 5 digits`() {
        // Given
        val input = "1234"

        // When
        val result = MicrochipNumberFormatter.format(input)

        // Then
        assertEquals("1234", result)
    }

    @Test
    fun `format should add first hyphen after 5 digits`() {
        // Given
        val input = "123456"

        // When
        val result = MicrochipNumberFormatter.format(input)

        // Then
        assertEquals("12345-6", result)
    }

    @Test
    fun `format should add second hyphen after 10 digits`() {
        // Given
        val input = "12345678901"

        // When
        val result = MicrochipNumberFormatter.format(input)

        // Then
        assertEquals("12345-67890-1", result)
    }

    @Test
    fun `format should format complete 15 digit number correctly`() {
        // Given
        val input = "123456789012345"

        // When
        val result = MicrochipNumberFormatter.format(input)

        // Then
        assertEquals("12345-67890-12345", result)
    }

    @Test
    fun `format should truncate input exceeding 15 digits`() {
        // Given
        val input = "123456789012345678"

        // When
        val result = MicrochipNumberFormatter.format(input)

        // Then
        assertEquals("12345-67890-12345", result)
    }

    @Test
    fun `format should handle already formatted input`() {
        // Given
        val input = "12345-67890-12345"

        // When
        val result = MicrochipNumberFormatter.format(input)

        // Then
        assertEquals("12345-67890-12345", result)
    }

    @Test
    fun `format should extract digits from mixed input`() {
        // Given
        val input = "abc123def456"

        // When
        val result = MicrochipNumberFormatter.format(input)

        // Then
        assertEquals("12345-6", result)
    }

    @Test
    fun `format should return empty for empty input`() {
        // Given
        val input = ""

        // When
        val result = MicrochipNumberFormatter.format(input)

        // Then
        assertEquals("", result)
    }

    @Test
    fun `format should handle exactly 5 digits without hyphen`() {
        // Given
        val input = "12345"

        // When
        val result = MicrochipNumberFormatter.format(input)

        // Then
        assertEquals("12345", result)
    }

    @Test
    fun `format should handle exactly 10 digits with one hyphen`() {
        // Given
        val input = "1234567890"

        // When
        val result = MicrochipNumberFormatter.format(input)

        // Then
        assertEquals("12345-67890", result)
    }

    // ===== extractDigits() tests =====

    @Test
    fun `extractDigits should return unchanged for digits only`() {
        // Given
        val input = "123456789012345"

        // When
        val result = MicrochipNumberFormatter.extractDigits(input)

        // Then
        assertEquals("123456789012345", result)
    }

    @Test
    fun `extractDigits should remove hyphens`() {
        // Given
        val input = "12345-67890-12345"

        // When
        val result = MicrochipNumberFormatter.extractDigits(input)

        // Then
        assertEquals("123456789012345", result)
    }

    @Test
    fun `extractDigits should remove letters`() {
        // Given
        val input = "abc123def456"

        // When
        val result = MicrochipNumberFormatter.extractDigits(input)

        // Then
        assertEquals("123456", result)
    }

    @Test
    fun `extractDigits should return empty for non-digit input`() {
        // Given
        val input = "abcdef"

        // When
        val result = MicrochipNumberFormatter.extractDigits(input)

        // Then
        assertEquals("", result)
    }

    @Test
    fun `extractDigits should return empty for empty input`() {
        // Given
        val input = ""

        // When
        val result = MicrochipNumberFormatter.extractDigits(input)

        // Then
        assertEquals("", result)
    }

    @Test
    fun `extractDigits should handle special characters`() {
        // Given
        val input = "123!@#456$%^789"

        // When
        val result = MicrochipNumberFormatter.extractDigits(input)

        // Then
        assertEquals("123456789", result)
    }
}

