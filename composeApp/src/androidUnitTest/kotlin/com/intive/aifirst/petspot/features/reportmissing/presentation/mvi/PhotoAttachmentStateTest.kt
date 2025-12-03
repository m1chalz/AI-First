package com.intive.aifirst.petspot.features.reportmissing.presentation.mvi

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PhotoAttachmentStateTest {
    @Test
    fun `hasPhoto should return false when uri is null`() {
        // Given
        val state =
            PhotoAttachmentState(
                uri = null,
                status = PhotoStatus.CONFIRMED,
            )

        // When
        val result = state.hasPhoto

        // Then
        assertFalse(result)
    }

    @Test
    fun `hasPhoto should return false when status is not CONFIRMED`() {
        // Given
        val state =
            PhotoAttachmentState(
                uri = "content://photo/1",
                status = PhotoStatus.LOADING,
            )

        // When
        val result = state.hasPhoto

        // Then
        assertFalse(result)
    }

    @Test
    fun `hasPhoto should return false when status is EMPTY`() {
        // Given
        val state =
            PhotoAttachmentState(
                uri = "content://photo/1",
                status = PhotoStatus.EMPTY,
            )

        // When
        val result = state.hasPhoto

        // Then
        assertFalse(result)
    }

    @Test
    fun `hasPhoto should return false when status is ERROR`() {
        // Given
        val state =
            PhotoAttachmentState(
                uri = "content://photo/1",
                status = PhotoStatus.ERROR,
            )

        // When
        val result = state.hasPhoto

        // Then
        assertFalse(result)
    }

    @Test
    fun `hasPhoto should return true when uri is present and status is CONFIRMED`() {
        // Given
        val state =
            PhotoAttachmentState(
                uri = "content://photo/1",
                filename = "dog.jpg",
                sizeBytes = 1024,
                status = PhotoStatus.CONFIRMED,
            )

        // When
        val result = state.hasPhoto

        // Then
        assertTrue(result)
    }

    @Test
    fun `displayFilename should return empty string when filename is null`() {
        // Given
        val state =
            PhotoAttachmentState(
                filename = null,
            )

        // When
        val result = state.displayFilename

        // Then
        assertEquals("", result)
    }

    @Test
    fun `displayFilename should return filename as-is when length is 20 or less`() {
        // Given
        val state =
            PhotoAttachmentState(
                filename = "dog_photo.jpg",
            )

        // When
        val result = state.displayFilename

        // Then
        assertEquals("dog_photo.jpg", result)
    }

    @Test
    fun `displayFilename should return filename as-is when exactly 20 characters`() {
        // Given
        val state =
            PhotoAttachmentState(
                filename = "12345678901234567890",
            )

        // When
        val result = state.displayFilename

        // Then
        assertEquals("12345678901234567890", result)
    }

    @Test
    fun `displayFilename should truncate filename when longer than 20 characters`() {
        // Given
        val state =
            PhotoAttachmentState(
                filename = "my_missing_pet_photo_2024.jpg",
            )

        // When
        val result = state.displayFilename

        // Then
        assertEquals("my_missing_pet_ph...", result)
    }

    @Test
    fun `displayFilename should take first 17 characters and add ellipsis when truncating`() {
        // Given
        val state =
            PhotoAttachmentState(
                filename = "very_long_filename_that_exceeds_twenty_chars.jpg",
            )

        // When
        val result = state.displayFilename

        // Then
        assertEquals("very_long_filenam...", result)
        assertEquals(20, result.length)
    }

    @Test
    fun `formattedSize should delegate to FileSizeFormatter`() {
        // Given
        val state =
            PhotoAttachmentState(
                sizeBytes = 1_234_567,
            )

        // When
        val result = state.formattedSize

        // Then
        assertEquals("1.2 MB", result)
    }

    @Test
    fun `Empty companion should create state with EMPTY status`() {
        // Given & When
        val state = PhotoAttachmentState.Empty

        // Then
        assertEquals(null, state.uri)
        assertEquals(null, state.filename)
        assertEquals(0, state.sizeBytes)
        assertEquals(PhotoStatus.EMPTY, state.status)
        assertFalse(state.hasPhoto)
    }
}
