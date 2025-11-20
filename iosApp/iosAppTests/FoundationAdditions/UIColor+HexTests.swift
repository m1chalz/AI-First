import XCTest
import UIKit
@testable import PetSpot

/**
 * Unit tests for UIColor+Hex extension.
 * Tests hex string parsing, color conversion, and edge cases.
 * Follows Given-When-Then structure per project constitution.
 */
final class UIColorHexTests: XCTestCase {
    
    // MARK: - Helper Methods
    
    /**
     * Extracts RGBA components from UIColor.
     * Returns tuple of (red, green, blue, alpha) in 0-255 range.
     */
    private func getRGBA(from color: UIColor) -> (r: Int, g: Int, b: Int, a: Int) {
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0
        
        color.getRed(&red, green: &green, blue: &blue, alpha: &alpha)
        
        return (
            r: Int(round(red * 255)),
            g: Int(round(green * 255)),
            b: Int(round(blue * 255)),
            a: Int(round(alpha * 255))
        )
    }
    
    // MARK: - Test 6-Character RGB Format
    
    /**
     * Tests that 6-char hex with hash creates correct color.
     */
    func test_sixCharHexWithHash_shouldParseCorrectly() {
        // Given - hex string "#2D2D2D" (dark gray)
        let hexString = "#2D2D2D"
        
        // When - creating UIColor from hex
        let color = UIColor(hex: hexString)
        let rgba = getRGBA(from: color)
        
        // Then - should have RGB values 45, 45, 45 and full opacity
        XCTAssertEqual(rgba.r, 45, "Red component should be 45")
        XCTAssertEqual(rgba.g, 45, "Green component should be 45")
        XCTAssertEqual(rgba.b, 45, "Blue component should be 45")
        XCTAssertEqual(rgba.a, 255, "Alpha should be 255 (opaque)")
    }
    
    /**
     * Tests that 6-char hex without hash creates correct color.
     */
    func test_sixCharHexWithoutHash_shouldParseCorrectly() {
        // Given - hex string "FAFAFA" (background color)
        let hexString = "FAFAFA"
        
        // When - creating UIColor from hex
        let color = UIColor(hex: hexString)
        let rgba = getRGBA(from: color)
        
        // Then - should have RGB values 250, 250, 250 and full opacity
        XCTAssertEqual(rgba.r, 250, "Red component should be 250")
        XCTAssertEqual(rgba.g, 250, "Green component should be 250")
        XCTAssertEqual(rgba.b, 250, "Blue component should be 250")
        XCTAssertEqual(rgba.a, 255, "Alpha should be 255 (opaque)")
    }
    
    // MARK: - Test 8-Character ARGB Format
    
    /**
     * Tests that 8-char ARGB hex parses with correct alpha.
     */
    func test_eightCharARGBHex_shouldParseWithAlpha() {
        // Given - hex string "#80FF0000" (50% transparent red)
        let hexString = "#80FF0000"
        
        // When - creating UIColor from hex
        let color = UIColor(hex: hexString)
        let rgba = getRGBA(from: color)
        
        // Then - should have RGB 255, 0, 0 and alpha 128 (50%)
        XCTAssertEqual(rgba.r, 255, "Red component should be 255")
        XCTAssertEqual(rgba.g, 0, "Green component should be 0")
        XCTAssertEqual(rgba.b, 0, "Blue component should be 0")
        XCTAssertEqual(rgba.a, 128, "Alpha should be 128 (50% opacity)")
    }
    
    /**
     * Tests that 8-char ARGB hex with full opacity parses correctly.
     */
    func test_eightCharARGBHexFullOpacity_shouldParseCorrectly() {
        // Given - hex string "#FF2D2D2D" (fully opaque dark gray)
        let hexString = "#FF2D2D2D"
        
        // When - creating UIColor from hex
        let color = UIColor(hex: hexString)
        let rgba = getRGBA(from: color)
        
        // Then - should have RGB 45, 45, 45 and alpha 255
        XCTAssertEqual(rgba.r, 45, "Red component should be 45")
        XCTAssertEqual(rgba.g, 45, "Green component should be 45")
        XCTAssertEqual(rgba.b, 45, "Blue component should be 45")
        XCTAssertEqual(rgba.a, 255, "Alpha should be 255 (fully opaque)")
    }
    
    /**
     * Tests that 8-char ARGB hex with zero alpha parses correctly.
     */
    func test_eightCharARGBHexZeroAlpha_shouldParseCorrectly() {
        // Given - hex string "#00FFFFFF" (fully transparent white)
        let hexString = "#00FFFFFF"
        
        // When - creating UIColor from hex
        let color = UIColor(hex: hexString)
        let rgba = getRGBA(from: color)
        
        // Then - should have RGB 255, 255, 255 and alpha 0
        XCTAssertEqual(rgba.r, 255, "Red component should be 255")
        XCTAssertEqual(rgba.g, 255, "Green component should be 255")
        XCTAssertEqual(rgba.b, 255, "Blue component should be 255")
        XCTAssertEqual(rgba.a, 0, "Alpha should be 0 (fully transparent)")
    }
    
    // MARK: - Test Unsupported Formats
    
    /**
     * Tests that 3-char hex (unsupported) returns black.
     */
    func test_threeCharHex_shouldReturnBlack() {
        // Given - 3-char hex string (unsupported format)
        let hexString = "#F00"
        
        // When - creating UIColor from hex
        let color = UIColor(hex: hexString)
        let rgba = getRGBA(from: color)
        
        // Then - should return black (unsupported format)
        XCTAssertEqual(rgba.r, 0, "Red component should be 0 (unsupported format)")
        XCTAssertEqual(rgba.g, 0, "Green component should be 0 (unsupported format)")
        XCTAssertEqual(rgba.b, 0, "Blue component should be 0 (unsupported format)")
        XCTAssertEqual(rgba.a, 255, "Alpha should be 255 (opaque)")
    }
    
    /**
     * Tests that invalid hex length returns black.
     */
    func test_invalidHexLength_shouldReturnBlack() {
        // Given - hex string with invalid length (5 chars)
        let hexString = "#12345"
        
        // When - creating UIColor from hex
        let color = UIColor(hex: hexString)
        let rgba = getRGBA(from: color)
        
        // Then - should return black (unsupported format)
        XCTAssertEqual(rgba.r, 0, "Red component should be 0 (unsupported format)")
        XCTAssertEqual(rgba.g, 0, "Green component should be 0 (unsupported format)")
        XCTAssertEqual(rgba.b, 0, "Blue component should be 0 (unsupported format)")
        XCTAssertEqual(rgba.a, 255, "Alpha should be 255 (opaque)")
    }
    
    /**
     * Tests that empty string returns black.
     */
    func test_emptyString_shouldReturnBlack() {
        // Given - empty hex string
        let hexString = ""
        
        // When - creating UIColor from hex
        let color = UIColor(hex: hexString)
        let rgba = getRGBA(from: color)
        
        // Then - should return black
        XCTAssertEqual(rgba.r, 0, "Red component should be 0 (empty string)")
        XCTAssertEqual(rgba.g, 0, "Green component should be 0 (empty string)")
        XCTAssertEqual(rgba.b, 0, "Blue component should be 0 (empty string)")
        XCTAssertEqual(rgba.a, 255, "Alpha should be 255 (opaque)")
    }
    
    /**
     * Tests that hex with spaces gets trimmed correctly.
     */
    func test_hexWithSpaces_shouldTrimAndParse() {
        // Given - hex string with spaces
        let hexString = " #FAFAFA "
        
        // When - creating UIColor from hex
        let color = UIColor(hex: hexString)
        let rgba = getRGBA(from: color)
        
        // Then - should trim non-alphanumeric (space, hash) and parse correctly
        XCTAssertEqual(rgba.r, 250, "Red component should be 250")
        XCTAssertEqual(rgba.g, 250, "Green component should be 250")
        XCTAssertEqual(rgba.b, 250, "Blue component should be 250")
    }
    
    /**
     * Tests lowercase hex characters are parsed correctly.
     */
    func test_lowercaseHex_shouldParseCorrectly() {
        // Given - lowercase hex string
        let hexString = "#fafafa"
        
        // When - creating UIColor from hex
        let color = UIColor(hex: hexString)
        let rgba = getRGBA(from: color)
        
        // Then - should parse same as uppercase
        XCTAssertEqual(rgba.r, 250, "Red component should be 250")
        XCTAssertEqual(rgba.g, 250, "Green component should be 250")
        XCTAssertEqual(rgba.b, 250, "Blue component should be 250")
    }
}

