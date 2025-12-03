package com.intive.aifirst.petspot.core.util

/**
 * Formats file size in bytes to human-readable format (B, KB, MB).
 */
object FileSizeFormatter {
    /**
     * Format bytes to human-readable string.
     *
     * @param bytes File size in bytes
     * @return Formatted string (e.g., "1.2 KB", "3.5 MB")
     */
    fun format(bytes: Long): String =
        when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "%.1f KB".format(bytes / 1024.0)
            else -> "%.1f MB".format(bytes / (1024.0 * 1024.0))
        }
}
