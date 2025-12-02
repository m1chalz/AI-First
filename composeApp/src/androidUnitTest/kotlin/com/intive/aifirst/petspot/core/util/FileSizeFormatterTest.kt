package com.intive.aifirst.petspot.core.util

import kotlin.test.Test
import kotlin.test.assertEquals

class FileSizeFormatterTest {
    @Test
    fun `format bytes when less than 1024 should return B suffix`() {
        // Given
        val bytes = 512L

        // When
        val result = FileSizeFormatter.format(bytes)

        // Then
        assertEquals("512 B", result)
    }

    @Test
    fun `format bytes when zero should return 0 B`() {
        // Given
        val bytes = 0L

        // When
        val result = FileSizeFormatter.format(bytes)

        // Then
        assertEquals("0 B", result)
    }

    @Test
    fun `format bytes when exactly 1024 should return KB suffix`() {
        // Given
        val bytes = 1024L

        // When
        val result = FileSizeFormatter.format(bytes)

        // Then
        assertEquals("1.0 KB", result)
    }

    @Test
    fun `format bytes when in KB range should return KB with one decimal`() {
        // Given
        val bytes = 1536L // 1.5 KB

        // When
        val result = FileSizeFormatter.format(bytes)

        // Then
        assertEquals("1.5 KB", result)
    }

    @Test
    fun `format bytes when less than MB should return KB`() {
        // Given
        val bytes = 512 * 1024L // 512 KB

        // When
        val result = FileSizeFormatter.format(bytes)

        // Then
        assertEquals("512.0 KB", result)
    }

    @Test
    fun `format bytes when exactly 1 MB should return MB suffix`() {
        // Given
        val bytes = 1024 * 1024L

        // When
        val result = FileSizeFormatter.format(bytes)

        // Then
        assertEquals("1.0 MB", result)
    }

    @Test
    fun `format bytes when in MB range should return MB with one decimal`() {
        // Given
        val bytes = (1.5 * 1024 * 1024).toLong()

        // When
        val result = FileSizeFormatter.format(bytes)

        // Then
        assertEquals("1.5 MB", result)
    }

    @Test
    fun `format bytes when large MB value should return MB`() {
        // Given
        val bytes = (5.7 * 1024 * 1024).toLong()

        // When
        val result = FileSizeFormatter.format(bytes)

        // Then
        assertEquals("5.7 MB", result)
    }
}
