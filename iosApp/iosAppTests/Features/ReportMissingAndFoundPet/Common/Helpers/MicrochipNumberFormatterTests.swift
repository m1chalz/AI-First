import XCTest
@testable import PetSpot

final class MicrochipNumberFormatterTests: XCTestCase {
    // MARK: - format(_:)

    func testFormat_whenInputHasFewerThanFiveDigits_shouldReturnUnformatted() {
        // Given
        let input = "1234"

        // When
        let result = MicrochipNumberFormatter.format(input)

        // Then
        XCTAssertEqual(result, "1234")
    }

    func testFormat_whenInputHasSixDigits_shouldInsertFirstHyphen() {
        // Given
        let input = "123456"

        // When
        let result = MicrochipNumberFormatter.format(input)

        // Then
        XCTAssertEqual(result, "12345-6")
    }

    func testFormat_whenInputHasElevenDigits_shouldInsertSecondHyphen() {
        // Given
        let input = "12345678901"

        // When
        let result = MicrochipNumberFormatter.format(input)

        // Then
        XCTAssertEqual(result, "12345-67890-1")
    }

    func testFormat_whenInputHasFifteenDigits_shouldFormatFully() {
        // Given
        let input = "123456789012345"

        // When
        let result = MicrochipNumberFormatter.format(input)

        // Then
        XCTAssertEqual(result, "12345-67890-12345")
    }

    func testFormat_whenInputHasMoreThanFifteenDigits_shouldTruncate() {
        // Given
        let input = "1234567890123456789"

        // When
        let result = MicrochipNumberFormatter.format(input)

        // Then
        XCTAssertEqual(result, "12345-67890-12345")
    }

    func testFormat_whenInputContainsHyphens_shouldReformat() {
        // Given
        let input = "12345-67890-12345"

        // When
        let result = MicrochipNumberFormatter.format(input)

        // Then
        XCTAssertEqual(result, "12345-67890-12345")
    }

    func testFormat_whenInputContainsLetters_shouldFilterToDigits() {
        // Given
        let input = "abc123def456"

        // When
        let result = MicrochipNumberFormatter.format(input)

        // Then
        XCTAssertEqual(result, "12345-6")
    }

    func testFormat_whenInputIsEmpty_shouldReturnEmpty() {
        // Given
        let input = ""

        // When
        let result = MicrochipNumberFormatter.format(input)

        // Then
        XCTAssertEqual(result, "")
    }

    // MARK: - extractDigits(_:)

    func testExtractDigits_whenInputHasOnlyDigits_shouldReturnSameString() {
        // Given
        let input = "123456789012345"

        // When
        let result = MicrochipNumberFormatter.extractDigits(input)

        // Then
        XCTAssertEqual(result, "123456789012345")
    }

    func testExtractDigits_whenInputHasHyphens_shouldRemoveThem() {
        // Given
        let input = "12345-67890-12345"

        // When
        let result = MicrochipNumberFormatter.extractDigits(input)

        // Then
        XCTAssertEqual(result, "123456789012345")
    }

    func testExtractDigits_whenInputHasLetters_shouldRemoveThem() {
        // Given
        let input = "abc123def456"

        // When
        let result = MicrochipNumberFormatter.extractDigits(input)

        // Then
        XCTAssertEqual(result, "123456")
    }

    func testExtractDigits_whenInputHasSpecialCharacters_shouldRemoveThem() {
        // Given
        let input = "123!@#456$%^789"

        // When
        let result = MicrochipNumberFormatter.extractDigits(input)

        // Then
        XCTAssertEqual(result, "123456789")
    }

    func testExtractDigits_whenInputIsEmpty_shouldReturnEmpty() {
        // Given
        let input = ""

        // When
        let result = MicrochipNumberFormatter.extractDigits(input)

        // Then
        XCTAssertEqual(result, "")
    }
}
