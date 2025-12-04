package com.intive.aifirst.petspot.features.reportmissing.data.repositories

import android.content.ContentResolver
import android.provider.OpenableColumns
import androidx.core.net.toUri
import com.intive.aifirst.petspot.features.reportmissing.domain.repositories.PhotoMetadataRepository

/**
 * Implementation of PhotoMetadataRepository using Android ContentResolver.
 *
 * @param contentResolver ContentResolver for querying photo metadata (inject directly, NOT Context)
 */
class PhotoMetadataRepositoryImpl(
    private val contentResolver: ContentResolver,
) : PhotoMetadataRepository {
    override suspend fun extractMetadata(uri: String): Pair<String, Long> {
        val contentUri = uri.toUri()
        contentResolver.query(
            contentUri,
            arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
            null,
            null,
            null,
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndexOrThrow(OpenableColumns.SIZE)
                val filename = cursor.getString(nameIndex) ?: "photo.jpg"
                val sizeBytes = cursor.getLong(sizeIndex)
                return Pair(filename, sizeBytes)
            }
        }
        throw IllegalStateException("Failed to extract metadata from URI: $uri")
    }
}
