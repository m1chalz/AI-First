package com.intive.aifirst.petspot.features.reportmissing.domain.usecases

import com.intive.aifirst.petspot.features.reportmissing.domain.repositories.PhotoMetadataRepository

/**
 * Use case for extracting metadata from a selected photo URI.
 * Provides filename and file size from content URI for display in UI.
 *
 * Future business rules could include:
 * - File size validation (max 10MB)
 * - File format validation (JPEG, PNG only)
 * - Image compression before upload
 */
class ExtractPhotoMetadataUseCase(
    private val repository: PhotoMetadataRepository,
) {
    /**
     * Extracts metadata from a content URI.
     *
     * @param uri Content URI of the selected photo
     * @return Pair of (filename, sizeBytes)
     * @throws IllegalStateException if metadata extraction fails
     */
    suspend operator fun invoke(uri: String): Pair<String, Long> = repository.extractMetadata(uri)
}
