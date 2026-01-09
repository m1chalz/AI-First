import Foundation

/// Shared date formatting utilities for consistent date display across the app.
enum DateFormatting {
    
    /// Formats date string from API format to display format.
    /// - Parameter dateString: Date in "yyyy-MM-dd" format (API format)
    /// - Returns: Formatted date as "MMM dd, yyyy" (e.g., "Jan 08, 2025")
    ///
    /// **Usage**:
    /// ```swift
    /// DateFormatting.formatDate("2025-01-08") // "Jan 08, 2025"
    /// ```
    static func formatDate(_ dateString: String) -> String {
        let inputFormatter = DateFormatter()
        inputFormatter.dateFormat = "yyyy-MM-dd"
        
        guard let date = inputFormatter.date(from: dateString) else {
            return dateString // Return original if parsing fails
        }
        
        let outputFormatter = DateFormatter()
        outputFormatter.dateFormat = "MMM dd, yyyy"
        return outputFormatter.string(from: date)
    }
}

