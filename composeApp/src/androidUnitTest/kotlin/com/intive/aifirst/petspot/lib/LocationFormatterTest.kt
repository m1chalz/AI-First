package com.intive.aifirst.petspot.lib

import com.intive.aifirst.petspot.domain.models.Location
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Unit tests for LocationFormatter.
 * Follows Given-When-Then pattern.
 */
class LocationFormatterTest {
    
    @Test
    fun `should format positive coordinates to N and E`() {
        // Given
        val location = Location("Warsaw", 5, 52.2297, 21.0122)
        
        // When
        val result = LocationFormatter.formatCoordinates(location)
        
        // Then
        assertEquals("52.2297° N, 21.0122° E", result)
    }
    
    @Test
    fun `should format negative latitude to S`() {
        // Given
        val location = Location("Sydney", 5, -33.8688, 151.2093)
        
        // When
        val result = LocationFormatter.formatCoordinates(location)
        
        // Then
        assertEquals("33.8688° S, 151.2093° E", result)
    }
    
    @Test
    fun `should format negative longitude to W`() {
        // Given
        val location = Location("New York", 5, 40.7128, -74.0060)
        
        // When
        val result = LocationFormatter.formatCoordinates(location)
        
        // Then
        assertEquals("40.7128° N, 74.0060° W", result)
    }
    
    @Test
    fun `should format both negative coordinates to S and W`() {
        // Given
        val location = Location("Buenos Aires", 5, -34.6037, -58.3816)
        
        // When
        val result = LocationFormatter.formatCoordinates(location)
        
        // Then
        assertEquals("34.6037° S, 58.3816° W", result)
    }
    
    @Test
    fun `should return dash for null location`() {
        // Given
        val location: Location? = null
        
        // When
        val result = LocationFormatter.formatCoordinates(location)
        
        // Then
        assertEquals("—", result)
    }
    
    @Test
    fun `should return dash when latitude is null`() {
        // Given
        val location = Location("Unknown", 5, null, 21.0122)
        
        // When
        val result = LocationFormatter.formatCoordinates(location)
        
        // Then
        assertEquals("—", result)
    }
    
    @Test
    fun `should return dash when longitude is null`() {
        // Given
        val location = Location("Unknown", 5, 52.2297, null)
        
        // When
        val result = LocationFormatter.formatCoordinates(location)
        
        // Then
        assertEquals("—", result)
    }
    
    @Test
    fun `should return dash when both coordinates are null`() {
        // Given
        val location = Location("Unknown", 5, null, null)
        
        // When
        val result = LocationFormatter.formatCoordinates(location)
        
        // Then
        assertEquals("—", result)
    }
}

