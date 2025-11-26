package com.intive.aifirst.petspot.lib

/**
 * Utility object for formatting microchip numbers in the Pet Details screen.
 */
object MicrochipFormatter {
    
    /**
     * Formats microchip number to "000-000-000-000" pattern.
     * Strips non-digit characters and groups by 3 digits.
     *
     * @param microchip Raw microchip number (may contain dashes or spaces)
     * @return Formatted microchip or "—" if invalid/empty
     */
    fun formatMicrochip(microchip: String?): String {
        if (microchip.isNullOrBlank()) return "—"
        
        // Strip non-digit characters
        val digits = microchip.filter { it.isDigit() }
        
        // Must have exactly 12 digits for valid format
        if (digits.length != 12) {
            return if (microchip.isNotBlank()) microchip else "—"
        }
        
        // Format as 000-000-000-000
        return digits.chunked(3).joinToString("-")
    }
}

