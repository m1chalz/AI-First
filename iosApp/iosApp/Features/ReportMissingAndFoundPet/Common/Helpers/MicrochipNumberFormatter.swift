import Foundation

/// Formats microchip numbers for display and extracts raw digits for storage.
struct MicrochipNumberFormatter {
    /// Maximum number of digits allowed in a microchip number.
    static let maxDigits = 15

    /// Formats raw digits with hyphens inserted after the 5th and 10th digits.
    /// - Parameter input: String that may contain digits, hyphens, or other characters.
    /// - Returns: Formatted string limited to 15 digits in `00000-00000-00000` shape.
    static func format(_ input: String) -> String {
        let digits = extractDigits(input)
        let limited = String(digits.prefix(maxDigits))

        var formatted = ""
        for (index, digit) in limited.enumerated() {
            if index == 5 || index == 10 {
                formatted.append("-")
            }
            formatted.append(digit)
        }

        return formatted
    }

    /// Extracts only numeric digits from a given string.
    /// - Parameter input: Any string that may contain digits and non-digit characters.
    /// - Returns: String containing only characters `0-9`.
    static func extractDigits(_ input: String) -> String {
        input.filter(\.isNumber)
    }
}
