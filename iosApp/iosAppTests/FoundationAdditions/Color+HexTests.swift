import XCTest
import SwiftUI
@testable import PetSpot

/**
 * Unit tests for Color+Hex extension.
 * Smoke tests to verify Color -> UIColor conversion works correctly.
 * Full hex parsing logic is tested in UIColor+HexTests.
 * Follows Given-When-Then structure per project constitution.
 */
final class ColorHexTests: XCTestCase {
    
    // MARK: - Helper Methods
    
    /**
     * Extracts RGBA components from SwiftUI Color.
     * Returns tuple of (red, green, blue, alpha) in 0-255 range.
     */
    private func getRGBA(from color: Color) -> (r: Int, g: Int, b: Int, a: Int) {
        let uiColor = UIColor(color)
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0
        
        uiColor.getRed(&red, green: &green, blue: &blue, alpha: &alpha)
        
        return (
            r: Int(round(red * 255)),
            g: Int(round(green * 255)),
            b: Int(round(blue * 255)),
            a: Int(round(alpha * 255))
        )
    }
    
    // MARK: - Smoke Tests
    
    /**
     * Tests that basic 6-char RGB hex works through UIColor conversion.
     */
    func test_sixCharRGB_shouldDelegateToUIColor() {
        // Given - hex string "#FAFAFA" (background color)
        let hexString = "#FAFAFA"
        
        // When - creating Color from hex (delegates to UIColor)
        let color = Color(hex: hexString)
        let rgba = getRGBA(from: color)
        
        // Then - should have correct RGB values
        XCTAssertEqual(rgba.r, 250, "Red component should be 250")
        XCTAssertEqual(rgba.g, 250, "Green component should be 250")
        XCTAssertEqual(rgba.b, 250, "Blue component should be 250")
        XCTAssertEqual(rgba.a, 255, "Alpha should be 255 (opaque)")
    }
    
    /**
     * Tests that 8-char ARGB with alpha works through UIColor conversion.
     */
    func test_eightCharARGB_shouldDelegateToUIColorWithAlpha() {
        // Given - hex string "#80FF0000" (50% transparent red)
        let hexString = "#80FF0000"
        
        // When - creating Color from hex (delegates to UIColor)
        let color = Color(hex: hexString)
        let rgba = getRGBA(from: color)
        
        // Then - should have correct RGBA values including alpha
        XCTAssertEqual(rgba.r, 255, "Red component should be 255")
        XCTAssertEqual(rgba.g, 0, "Green component should be 0")
        XCTAssertEqual(rgba.b, 0, "Blue component should be 0")
        XCTAssertEqual(rgba.a, 128, "Alpha should be 128 (50% opacity)")
    }
    
    /**
     * Tests that invalid format falls back to black through UIColor conversion.
     */
    func test_invalidFormat_shouldDelegateToUIColorAndReturnBlack() {
        // Given - invalid hex string (3-char, unsupported)
        let hexString = "#F00"
        
        // When - creating Color from hex (delegates to UIColor)
        let color = Color(hex: hexString)
        let rgba = getRGBA(from: color)
        
        // Then - should return black as per UIColor+Hex behavior
        XCTAssertEqual(rgba.r, 0, "Red component should be 0 (unsupported format)")
        XCTAssertEqual(rgba.g, 0, "Green component should be 0 (unsupported format)")
        XCTAssertEqual(rgba.b, 0, "Blue component should be 0 (unsupported format)")
        XCTAssertEqual(rgba.a, 255, "Alpha should be 255 (opaque)")
    }
}
