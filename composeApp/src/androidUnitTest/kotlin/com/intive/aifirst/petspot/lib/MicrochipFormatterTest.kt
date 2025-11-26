package com.intive.aifirst.petspot.lib

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Unit tests for MicrochipFormatter.
 * Follows Given-When-Then pattern.
 */
class MicrochipFormatterTest {
    
    @Test
    fun `should format 12-digit microchip to dashed pattern`() {
        // Given
        val input = "123456789012"
        
        // When
        val result = MicrochipFormatter.formatMicrochip(input)
        
        // Then
        assertEquals("123-456-789-012", result)
    }
    
    @Test
    fun `should strip existing dashes and reformat`() {
        // Given
        val input = "123-456-789-012"
        
        // When
        val result = MicrochipFormatter.formatMicrochip(input)
        
        // Then
        assertEquals("123-456-789-012", result)
    }
    
    @Test
    fun `should strip spaces and format`() {
        // Given
        val input = "123 456 789 012"
        
        // When
        val result = MicrochipFormatter.formatMicrochip(input)
        
        // Then
        assertEquals("123-456-789-012", result)
    }
    
    @Test
    fun `should return dash for null input`() {
        // Given
        val input: String? = null
        
        // When
        val result = MicrochipFormatter.formatMicrochip(input)
        
        // Then
        assertEquals("—", result)
    }
    
    @Test
    fun `should return dash for blank input`() {
        // Given
        val input = "   "
        
        // When
        val result = MicrochipFormatter.formatMicrochip(input)
        
        // Then
        assertEquals("—", result)
    }
    
    @Test
    fun `should return original for less than 12 digits`() {
        // Given
        val input = "12345678"
        
        // When
        val result = MicrochipFormatter.formatMicrochip(input)
        
        // Then
        assertEquals("12345678", result) // Returns as-is since not 12 digits
    }
    
    @Test
    fun `should return original for more than 12 digits`() {
        // Given
        val input = "1234567890123456"
        
        // When
        val result = MicrochipFormatter.formatMicrochip(input)
        
        // Then
        assertEquals("1234567890123456", result) // Returns as-is since not 12 digits
    }
}

