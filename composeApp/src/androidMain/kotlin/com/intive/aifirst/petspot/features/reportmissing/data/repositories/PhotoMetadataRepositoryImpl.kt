package com.intive.aifirst.petspot.features.reportmissing.data.repositories

import android.app.Application
import android.provider.OpenableColumns
import androidx.core.net.toUri
import com.intive.aifirst.petspot.features.reportmissing.domain.repositories.PhotoMetadataRepository

/**
 * Implementation of PhotoMetadataRepository using Android ContentResolver.
 *
 * @param application Application context for ContentResolver access (safe to hold)
 */
class PhotoMetadataRepositoryImpl(
    private val application: Application,
) : PhotoMetadataRepository {
    override suspend fun extractMetadata(uri: String): Pair<String, Long> {
        val contentUri = uri.toUri()
        application.contentResolver.query(
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
