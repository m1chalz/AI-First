import SwiftUI

/**
 * Extension for creating SwiftUI Color from hex string.
 * Supports only 6-char RGB and 8-char ARGB formats.
 *
 * Supported formats:
 * - `Color(hex: "#2D2D2D")` - 6-char RGB with hash
 * - `Color(hex: "2D2D2D")` - 6-char RGB without hash
 * - `Color(hex: "#FF2D2D2D")` - 8-char ARGB with hash
 *
 * Any other format returns black.
 *
 * Implementation delegates to UIColor+Hex for consistency.
 */
extension Color {
    init(hex: String) {
        self.init(uiColor: UIColor(hex: hex))
    }
}

