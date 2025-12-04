package com.intive.aifirst.petspot.features.reportmissing.domain.repositories

/**
 * Repository for extracting photo metadata from content URIs.
 */
interface PhotoMetadataRepository {
    /**
     * Extracts filename and file size from a content URI.
     *
     * @param uri Content URI of the selected photo
     * @return Pair of filename and size in bytes
     * @throws IllegalStateException if metadata extraction fails
     */
    suspend fun extractMetadata(uri: String): Pair<String, Long>
}
