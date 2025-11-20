import UIKit

/**
 * Extension for creating UIColor from hex string.
 * Supports only 6-char RGB and 8-char ARGB formats.
 *
 * Supported formats:
 * - `UIColor(hex: "#2D2D2D")` - 6-char RGB with hash
 * - `UIColor(hex: "2D2D2D")` - 6-char RGB without hash
 * - `UIColor(hex: "#FF2D2D2D")` - 8-char ARGB with hash
 *
 * Any other format returns black.
 */
extension UIColor {
    convenience init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 6: // RGB (24-bit)
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8: // ARGB (32-bit)
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            // Unsupported format - return black
            (a, r, g, b) = (255, 0, 0, 0)
        }
        
        self.init(
            red: CGFloat(r) / 255,
            green: CGFloat(g) / 255,
            blue: CGFloat(b) / 255,
            alpha: CGFloat(a) / 255
        )
    }
}

