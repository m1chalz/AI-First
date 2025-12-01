import XCTest
@testable import iosApp

/// Unit tests for HTTP-based AnimalRepository implementation
/// Tests network operations, JSON decoding, error handling, and data transformation
final class AnimalRepositoryTests: XCTestCase {
    var sut: AnimalRepository!
    var mockURLSession: URLSession!
    
    override func setUp() {
        super.setUp()
        // Use URLProtocol mocking for URLSession
        let configuration = URLSessionConfiguration.ephemeral
        configuration.protocolClasses = [MockURLProtocol.self]
        mockURLSession = URLSession(configuration: configuration)
        sut = AnimalRepository(urlSession: mockURLSession)
    }
    
    override func tearDown() {
        sut = nil
        mockURLSession = nil
        MockURLProtocol.requestHandler = nil
        super.tearDown()
    }
    
    // MARK: - getAnimals Tests
    
    /// T009: Test getAnimals with valid JSON response should return parsed Animal array
    func testGetAnimals_whenServerReturnsValidData_shouldReturnAnimals() async throws {
        // Given - valid JSON response
        let jsonData = """
        {
            "data": [
                {
                    "id": "550e8400-e29b-41d4-a716-446655440000",
                    "petName": "Max",
                    "species": "DOG",
                    "status": "MISSING",
                    "photoUrl": "http://localhost:3000/images/max.jpg",
                    "lastSeenDate": "2024-11-15",
                    "locationLatitude": 52.2297,
                    "locationLongitude": 21.0122,
                    "breed": "Golden Retriever",
                    "sex": "MALE",
                    "age": 5,
                    "description": "Friendly dog",
                    "phone": "+48123456789",
                    "email": "owner@example.com"
                }
            ]
        }
        """.data(using: .utf8)!
        
        MockURLProtocol.requestHandler = { request in
            let response = HTTPURLResponse(
                url: request.url!,
                statusCode: 200,
                httpVersion: nil,
                headerFields: nil
            )!
            return (response, jsonData)
        }
        
        // When - fetch animals
        let animals = try await sut.getAnimals(near: nil)
        
        // Then - verify parsed correctly
        XCTAssertEqual(animals.count, 1)
        XCTAssertEqual(animals[0].id, "550e8400-e29b-41d4-a716-446655440000")
        XCTAssertEqual(animals[0].name, "Max")
        XCTAssertEqual(animals[0].species, .dog)
        XCTAssertEqual(animals[0].status, .active) // MISSING mapped to ACTIVE
        XCTAssertEqual(animals[0].coordinate.latitude, 52.2297, accuracy: 0.0001)
        XCTAssertEqual(animals[0].coordinate.longitude, 21.0122, accuracy: 0.0001)
    }
    
    /// T010: Test getAnimals with location parameters should include lat/lng query params in URL
    func testGetAnimals_withLocationParameters_shouldIncludeQueryParams() async throws {
        // Given - location parameters
        let userLocation = UserLocation(latitude: 52.2297, longitude: 21.0122)
        var capturedRequest: URLRequest?
        
        MockURLProtocol.requestHandler = { request in
            capturedRequest = request
            let jsonData = """
            {"data": []}
            """.data(using: .utf8)!
            let response = HTTPURLResponse(
                url: request.url!,
                statusCode: 200,
                httpVersion: nil,
                headerFields: nil
            )!
            return (response, jsonData)
        }
        
        // When - fetch with location
        _ = try await sut.getAnimals(near: userLocation)
        
        // Then - verify query parameters in URL
        let url = capturedRequest?.url
        XCTAssertNotNil(url)
        let components = URLComponents(url: url!, resolvingAgainstBaseURL: false)
        XCTAssertEqual(components?.queryItems?.count, 2)
        XCTAssertTrue(components?.queryItems?.contains(where: {
            $0.name == "lat" && $0.value == "52.2297"
        }) ?? false)
        XCTAssertTrue(components?.queryItems?.contains(where: {
            $0.name == "lng" && $0.value == "21.0122"
        }) ?? false)
    }
    
    /// T011: Test getAnimals with HTTP 500 error should throw RepositoryError.httpError
    func testGetAnimals_whenServerReturns500_shouldThrowHttpError() async {
        // Given - server error response
        MockURLProtocol.requestHandler = { request in
            let response = HTTPURLResponse(
                url: request.url!,
                statusCode: 500,
                httpVersion: nil,
                headerFields: nil
            )!
            return (response, Data())
        }
        
        // When/Then - should throw error
        do {
            _ = try await sut.getAnimals(near: nil)
            XCTFail("Expected error to be thrown")
        } catch let error as RepositoryError {
            if case .httpError(let statusCode) = error {
                XCTAssertEqual(statusCode, 500)
            } else {
                XCTFail("Expected httpError, got \(error)")
            }
        } catch {
            XCTFail("Expected RepositoryError, got \(error)")
        }
    }
    
    /// T012: Test getAnimals with invalid JSON should throw RepositoryError.decodingFailed
    func testGetAnimals_whenResponseHasInvalidJSON_shouldThrowDecodingError() async {
        // Given - invalid JSON
        let invalidJSON = "{ invalid json }".data(using: .utf8)!
        
        MockURLProtocol.requestHandler = { request in
            let response = HTTPURLResponse(
                url: request.url!,
                statusCode: 200,
                httpVersion: nil,
                headerFields: nil
            )!
            return (response, invalidJSON)
        }
        
        // When/Then - should throw decoding error
        do {
            _ = try await sut.getAnimals(near: nil)
            XCTFail("Expected error to be thrown")
        } catch let error as RepositoryError {
            if case .decodingFailed = error {
                // Expected error
            } else {
                XCTFail("Expected decodingFailed, got \(error)")
            }
        } catch {
            XCTFail("Expected RepositoryError, got \(error)")
        }
    }
    
    /// T013: Test getAnimals with invalid species enum should skip invalid items (compactMap behavior)
    func testGetAnimals_whenItemHasInvalidSpecies_shouldSkipItem() async throws {
        // Given - JSON with one valid and one invalid species
        let jsonData = """
        {
            "data": [
                {
                    "id": "1",
                    "petName": "Valid Dog",
                    "species": "DOG",
                    "status": "MISSING",
                    "photoUrl": "http://localhost:3000/images/1.jpg",
                    "lastSeenDate": "2024-11-15",
                    "locationLatitude": 52.2297,
                    "locationLongitude": 21.0122,
                    "breed": "Golden Retriever",
                    "sex": "MALE",
                    "age": 5,
                    "description": "Valid",
                    "phone": "+48123456789",
                    "email": null
                },
                {
                    "id": "2",
                    "petName": "Invalid Species",
                    "species": "DINOSAUR",
                    "status": "MISSING",
                    "photoUrl": "http://localhost:3000/images/2.jpg",
                    "lastSeenDate": "2024-11-15",
                    "locationLatitude": 52.2297,
                    "locationLongitude": 21.0122,
                    "breed": null,
                    "sex": "MALE",
                    "age": 5,
                    "description": "Invalid",
                    "phone": "+48123456789",
                    "email": null
                }
            ]
        }
        """.data(using: .utf8)!
        
        MockURLProtocol.requestHandler = { request in
            let response = HTTPURLResponse(
                url: request.url!,
                statusCode: 200,
                httpVersion: nil,
                headerFields: nil
            )!
            return (response, jsonData)
        }
        
        // When - fetch animals
        let animals = try await sut.getAnimals(near: nil)
        
        // Then - only valid item returned, invalid skipped
        XCTAssertEqual(animals.count, 1)
        XCTAssertEqual(animals[0].name, "Valid Dog")
    }
    
    /// T014: Test getAnimals with duplicate IDs should deduplicate and log warning
    func testGetAnimals_whenListHasDuplicateIds_shouldDeduplicateAndKeepFirst() async throws {
        // Given - JSON with duplicate IDs
        let jsonData = """
        {
            "data": [
                {
                    "id": "duplicate-id",
                    "petName": "First Max",
                    "species": "DOG",
                    "status": "MISSING",
                    "photoUrl": "http://localhost:3000/images/1.jpg",
                    "lastSeenDate": "2024-11-15",
                    "locationLatitude": 52.2297,
                    "locationLongitude": 21.0122,
                    "breed": "Golden Retriever",
                    "sex": "MALE",
                    "age": 5,
                    "description": "First",
                    "phone": "+48123456789",
                    "email": null
                },
                {
                    "id": "duplicate-id",
                    "petName": "Second Max",
                    "species": "DOG",
                    "status": "MISSING",
                    "photoUrl": "http://localhost:3000/images/2.jpg",
                    "lastSeenDate": "2024-11-15",
                    "locationLatitude": 52.2297,
                    "locationLongitude": 21.0122,
                    "breed": "Labrador",
                    "sex": "MALE",
                    "age": 3,
                    "description": "Second",
                    "phone": "+48987654321",
                    "email": null
                }
            ]
        }
        """.data(using: .utf8)!
        
        MockURLProtocol.requestHandler = { request in
            let response = HTTPURLResponse(
                url: request.url!,
                statusCode: 200,
                httpVersion: nil,
                headerFields: nil
            )!
            return (response, jsonData)
        }
        
        // When - fetch animals
        let animals = try await sut.getAnimals(near: nil)
        
        // Then - only one item returned (first occurrence kept)
        XCTAssertEqual(animals.count, 1)
        XCTAssertEqual(animals[0].name, "First Max")
    }
    
    /// T015: Test getAnimals with empty list should return empty array
    func testGetAnimals_whenServerReturnsEmptyList_shouldReturnEmptyArray() async throws {
        // Given - empty data array
        let jsonData = """
        {
            "data": []
        }
        """.data(using: .utf8)!
        
        MockURLProtocol.requestHandler = { request in
            let response = HTTPURLResponse(
                url: request.url!,
                statusCode: 200,
                httpVersion: nil,
                headerFields: nil
            )!
            return (response, jsonData)
        }
        
        // When - fetch animals
        let animals = try await sut.getAnimals(near: nil)
        
        // Then - empty array returned
        XCTAssertEqual(animals.count, 0)
    }
    
    // MARK: - getPetDetails Tests (User Story 2)
    
    /// T037: Test getPetDetails with valid JSON response should return PetDetails with all fields
    func testGetPetDetails_whenServerReturnsValidData_shouldReturnPetDetails() async throws {
        // Given - valid pet details JSON
        let jsonData = """
        {
            "id": "550e8400-e29b-41d4-a716-446655440000",
            "petName": "Max",
            "species": "DOG",
            "status": "MISSING",
            "photoUrl": "http://localhost:3000/images/max.jpg",
            "lastSeenDate": "2024-11-15",
            "locationLatitude": 52.2297,
            "locationLongitude": 21.0122,
            "breed": "Golden Retriever",
            "sex": "MALE",
            "age": 5,
            "microchipNumber": "123456789012345",
            "email": "owner@example.com",
            "phone": "+48123456789",
            "reward": "500 PLN",
            "description": "Friendly dog",
            "createdAt": "2024-11-20T10:30:00.000Z",
            "updatedAt": "2024-11-20T10:30:00.000Z"
        }
        """.data(using: .utf8)!
        
        MockURLProtocol.requestHandler = { request in
            let response = HTTPURLResponse(
                url: request.url!,
                statusCode: 200,
                httpVersion: nil,
                headerFields: nil
            )!
            return (response, jsonData)
        }
        
        // When - fetch pet details
        let details = try await sut.getPetDetails(id: "550e8400-e29b-41d4-a716-446655440000")
        
        // Then - verify all fields
        XCTAssertEqual(details.id, "550e8400-e29b-41d4-a716-446655440000")
        XCTAssertEqual(details.petName, "Max")
        XCTAssertEqual(details.species, .dog)
        XCTAssertEqual(details.status, .active) // MISSING mapped to ACTIVE
        XCTAssertEqual(details.breed, "Golden Retriever")
        XCTAssertEqual(details.microchipNumber, "123456789012345")
        XCTAssertEqual(details.email, "owner@example.com")
        XCTAssertEqual(details.reward, "500 PLN")
    }
    
    /// T038: Test getPetDetails with HTTP 404 error should throw RepositoryError.notFound
    func testGetPetDetails_whenServerReturns404_shouldThrowNotFoundError() async {
        // Given - 404 response
        MockURLProtocol.requestHandler = { request in
            let response = HTTPURLResponse(
                url: request.url!,
                statusCode: 404,
                httpVersion: nil,
                headerFields: nil
            )!
            return (response, Data())
        }
        
        // When/Then - should throw not found error
        do {
            _ = try await sut.getPetDetails(id: "non-existent-id")
            XCTFail("Expected error to be thrown")
        } catch let error as RepositoryError {
            if case .notFound = error {
                // Expected error
            } else {
                XCTFail("Expected notFound, got \(error)")
            }
        } catch {
            XCTFail("Expected RepositoryError, got \(error)")
        }
    }
    
    /// T039: Test getPetDetails with HTTP 500 error should throw RepositoryError.httpError
    func testGetPetDetails_whenServerReturns500_shouldThrowHttpError() async {
        // Given - server error response
        MockURLProtocol.requestHandler = { request in
            let response = HTTPURLResponse(
                url: request.url!,
                statusCode: 500,
                httpVersion: nil,
                headerFields: nil
            )!
            return (response, Data())
        }
        
        // When/Then - should throw http error
        do {
            _ = try await sut.getPetDetails(id: "some-id")
            XCTFail("Expected error to be thrown")
        } catch let error as RepositoryError {
            if case .httpError(let statusCode) = error {
                XCTAssertEqual(statusCode, 500)
            } else {
                XCTFail("Expected httpError, got \(error)")
            }
        } catch {
            XCTFail("Expected RepositoryError, got \(error)")
        }
    }
    
    /// T040: Test getPetDetails with invalid JSON should throw RepositoryError.decodingFailed
    func testGetPetDetails_whenResponseHasInvalidJSON_shouldThrowDecodingError() async {
        // Given - invalid JSON
        let invalidJSON = "{ invalid json }".data(using: .utf8)!
        
        MockURLProtocol.requestHandler = { request in
            let response = HTTPURLResponse(
                url: request.url!,
                statusCode: 200,
                httpVersion: nil,
                headerFields: nil
            )!
            return (response, invalidJSON)
        }
        
        // When/Then - should throw decoding error
        do {
            _ = try await sut.getPetDetails(id: "some-id")
            XCTFail("Expected error to be thrown")
        } catch let error as RepositoryError {
            if case .decodingFailed = error {
                // Expected error
            } else {
                XCTFail("Expected decodingFailed, got \(error)")
            }
        } catch {
            XCTFail("Expected RepositoryError, got \(error)")
        }
    }
    
    /// T041: Test getPetDetails with invalid species should throw RepositoryError.invalidData
    func testGetPetDetails_whenResponseHasInvalidSpecies_shouldThrowInvalidDataError() async {
        // Given - JSON with invalid species
        let jsonData = """
        {
            "id": "550e8400-e29b-41d4-a716-446655440000",
            "petName": "Max",
            "species": "DINOSAUR",
            "status": "MISSING",
            "photoUrl": "http://localhost:3000/images/max.jpg",
            "lastSeenDate": "2024-11-15",
            "locationLatitude": 52.2297,
            "locationLongitude": 21.0122,
            "breed": "Golden Retriever",
            "sex": "MALE",
            "age": 5,
            "microchipNumber": null,
            "email": null,
            "phone": "+48123456789",
            "reward": null,
            "description": "Friendly dog",
            "createdAt": "2024-11-20T10:30:00.000Z",
            "updatedAt": "2024-11-20T10:30:00.000Z"
        }
        """.data(using: .utf8)!
        
        MockURLProtocol.requestHandler = { request in
            let response = HTTPURLResponse(
                url: request.url!,
                statusCode: 200,
                httpVersion: nil,
                headerFields: nil
            )!
            return (response, jsonData)
        }
        
        // When/Then - should throw invalid data error
        do {
            _ = try await sut.getPetDetails(id: "some-id")
            XCTFail("Expected error to be thrown")
        } catch let error as RepositoryError {
            if case .invalidData = error {
                // Expected error
            } else {
                XCTFail("Expected invalidData, got \(error)")
            }
        } catch {
            XCTFail("Expected RepositoryError, got \(error)")
        }
    }
    
    /// T042: Test getPetDetails with missing optional fields should return PetDetails with nil values
    func testGetPetDetails_whenOptionalFieldsMissing_shouldReturnDetailsWithNilValues() async throws {
        // Given - JSON with nil optional fields
        let jsonData = """
        {
            "id": "550e8400-e29b-41d4-a716-446655440000",
            "petName": "Max",
            "species": "DOG",
            "status": "MISSING",
            "photoUrl": "http://localhost:3000/images/max.jpg",
            "lastSeenDate": "2024-11-15",
            "locationLatitude": 52.2297,
            "locationLongitude": 21.0122,
            "breed": null,
            "sex": "MALE",
            "age": null,
            "microchipNumber": null,
            "email": null,
            "phone": "+48123456789",
            "reward": null,
            "description": "Friendly dog",
            "createdAt": "2024-11-20T10:30:00.000Z",
            "updatedAt": "2024-11-20T10:30:00.000Z"
        }
        """.data(using: .utf8)!
        
        MockURLProtocol.requestHandler = { request in
            let response = HTTPURLResponse(
                url: request.url!,
                statusCode: 200,
                httpVersion: nil,
                headerFields: nil
            )!
            return (response, jsonData)
        }
        
        // When - fetch pet details
        let details = try await sut.getPetDetails(id: "some-id")
        
        // Then - optional fields should be nil
        XCTAssertNil(details.breed)
        XCTAssertNil(details.microchipNumber)
        XCTAssertNil(details.email)
        XCTAssertNil(details.reward)
        XCTAssertNil(details.approximateAge)
        XCTAssertEqual(details.petName, "Max")
    }
    
    /// T043a: Test getPetDetails with updatedAt in custom format should parse correctly
    func testGetPetDetails_whenUpdatedAtInCustomFormat_shouldParseCorrectly() async throws {
        // Given - JSON with updatedAt in "YYYY-MM-DD HH:MM:SS" format (backend inconsistency)
        let jsonData = """
        {
            "id": "550e8400-e29b-41d4-a716-446655440000",
            "petName": "Max",
            "species": "DOG",
            "status": "MISSING",
            "photoUrl": "http://localhost:3000/images/max.jpg",
            "lastSeenDate": "2024-11-15",
            "locationLatitude": 52.2297,
            "locationLongitude": 21.0122,
            "breed": "Golden Retriever",
            "sex": "MALE",
            "age": 5,
            "microchipNumber": null,
            "email": null,
            "phone": "+48123456789",
            "reward": null,
            "description": "Friendly dog",
            "createdAt": "2024-11-20T10:30:00.000Z",
            "updatedAt": "2025-12-01 14:24:13"
        }
        """.data(using: .utf8)!
        
        MockURLProtocol.requestHandler = { request in
            let response = HTTPURLResponse(
                url: request.url!,
                statusCode: 200,
                httpVersion: nil,
                headerFields: nil
            )!
            return (response, jsonData)
        }
        
        // When - fetch pet details
        let details = try await sut.getPetDetails(id: "some-id")
        
        // Then - updatedAt should be parsed successfully (no crash/error)
        XCTAssertEqual(details.petName, "Max")
        XCTAssertEqual(details.updatedAt, "2025-12-01 14:24:13")
    }
}

// MARK: - Mock URLProtocol

/// Mock URLProtocol for testing URLSession without real network calls
class MockURLProtocol: URLProtocol {
    static var requestHandler: ((URLRequest) throws -> (HTTPURLResponse, Data))?
    
    override class func canInit(with request: URLRequest) -> Bool {
        return true
    }
    
    override class func canonicalRequest(for request: URLRequest) -> URLRequest {
        return request
    }
    
    override func startLoading() {
        guard let handler = MockURLProtocol.requestHandler else {
            fatalError("Request handler not set")
        }
        
        do {
            let (response, data) = try handler(request)
            client?.urlProtocol(self, didReceive: response, cacheStoragePolicy: .notAllowed)
            client?.urlProtocol(self, didLoad: data)
            client?.urlProtocolDidFinishLoading(self)
        } catch {
            client?.urlProtocol(self, didFailWithError: error)
        }
    }
    
    override func stopLoading() {
        // No-op
    }
}

