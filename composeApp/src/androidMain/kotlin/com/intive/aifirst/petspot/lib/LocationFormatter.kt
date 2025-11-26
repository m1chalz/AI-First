package com.intive.aifirst.petspot.lib

import com.intive.aifirst.petspot.domain.models.Location
import kotlin.math.abs

/**
 * Utility object for formatting location coordinates in the Pet Details screen.
 */
object LocationFormatter {
    
    /**
     * Formats location coordinates to display format.
     * Example: (52.2297, 21.0122) → "52.2297° N, 21.0122° E"
     *
     * @param location Location with latitude and longitude
     * @return Formatted coordinates or "—" if coordinates unavailable
     */
    fun formatCoordinates(location: Location?): String {
        if (location == null) return "—"
        
        val latitude = location.latitude
        val longitude = location.longitude
        
        if (latitude == null || longitude == null) return "—"
        
        val latDirection = if (latitude >= 0) "N" else "S"
        val lonDirection = if (longitude >= 0) "E" else "W"
        
        val formattedLat = String.format("%.4f", abs(latitude))
        val formattedLon = String.format("%.4f", abs(longitude))
        
        return "$formattedLat° $latDirection, $formattedLon° $lonDirection"
    }
}

