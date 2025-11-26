package com.intive.aifirst.petspot.lib

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Unit tests for DateFormatter.
 * Follows Given-When-Then pattern.
 */
class DateFormatterTest {
    
    @Test
    fun `should format valid date DD-MM-YYYY to MMM DD, YYYY`() {
        // Given
        val input = "18/11/2025"
        
        // When
        val result = DateFormatter.formatPetDate(input)
        
        // Then
        assertEquals("Nov 18, 2025", result)
    }
    
    @Test
    fun `should format January date correctly`() {
        // Given
        val input = "01/01/2025"
        
        // When
        val result = DateFormatter.formatPetDate(input)
        
        // Then
        assertEquals("Jan 01, 2025", result)
    }
    
    @Test
    fun `should format December date correctly`() {
        // Given
        val input = "25/12/2024"
        
        // When
        val result = DateFormatter.formatPetDate(input)
        
        // Then
        assertEquals("Dec 25, 2024", result)
    }
    
    @Test
    fun `should return dash for null input`() {
        // Given
        val input: String? = null
        
        // When
        val result = DateFormatter.formatPetDate(input)
        
        // Then
        assertEquals("—", result)
    }
    
    @Test
    fun `should return dash for blank input`() {
        // Given
        val input = "   "
        
        // When
        val result = DateFormatter.formatPetDate(input)
        
        // Then
        assertEquals("—", result)
    }
    
    @Test
    fun `should return original string for invalid date format`() {
        // Given
        val input = "invalid-date"
        
        // When
        val result = DateFormatter.formatPetDate(input)
        
        // Then
        assertEquals("invalid-date", result)
    }
    
    @Test
    fun `should return original string for wrong date format`() {
        // Given
        val input = "2025-11-18" // Wrong format (ISO instead of DD/MM/YYYY)
        
        // When
        val result = DateFormatter.formatPetDate(input)
        
        // Then
        assertEquals("2025-11-18", result)
    }
}

