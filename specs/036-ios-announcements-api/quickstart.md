# Quickstart: iOS Announcements API Integration

**Feature**: 036-ios-announcements-api  
**Platform**: iOS  
**Estimated Time**: 4-6 hours  
**Date**: 2025-12-01

## Prerequisites

- Xcode 15+ with Swift 5.9+
- Backend server running on `http://localhost:3000`
- Existing iOS app structure with MVVM-C architecture
- Existing `AnimalRepositoryProtocol` defined
- Existing `AnimalListViewModel` and `PetDetailsViewModel`

## Implementation Overview

This feature replaces mock data with real backend API calls for iOS Animal List and Pet Details screens. The implementation follows these steps:

1. Create HTTP-based `AnimalRepository` implementation
2. Update `ServiceContainer` to provide HTTP repository
3. Add unit tests for repository and ViewModels
4. Add E2E tests for user scenarios
5. Verify integration with manual testing

**No UI changes required** - existing views already display data from ViewModels.

---

## Step 1: Create APIConfig (5 minutes)

Create configuration file for backend base URL.

**File**: `/iosApp/iosApp/Configuration/APIConfig.swift` (NEW)

```swift
import Foundation

enum APIConfig {
    /// Base URL for PetSpot backend API
    /// Development: Local server on localhost:3000
    /// Production: Update this constant for production environment
    static let baseURL = "http://localhost:3000"
    
    /// API version prefix
    static let apiVersion = "/api/v1"
    
    /// Full base URL with API version
    static var fullBaseURL: String {
        return baseURL + apiVersion
    }
}
```

**iOS ATS Exception**: Add to `Info.plist` to allow HTTP localhost connections:

```xml
<key>NSAppTransportSecurity</key>
<dict>
    <key>NSAllowsLocalNetworking</key>
    <true/>
</dict>
```

**Test**: Build project to verify no compilation errors.

---

## Step 2: Create HTTP AnimalRepository Implementation (60 minutes)

Implement `AnimalRepositoryProtocol` using URLSession for HTTP requests.

**File**: `/iosApp/iosApp/Data/Repositories/AnimalRepository.swift` (NEW)

```swift
import Foundation

/// HTTP-based implementation of AnimalRepositoryProtocol
/// Consumes backend REST API endpoints:
/// - GET /api/v1/announcements (with optional lat/lng query params)
/// - GET /api/v1/announcements/:id
class AnimalRepository: AnimalRepositoryProtocol {
    private let urlSession: URLSession
    
    init(urlSession: URLSession = .shared) {
        self.urlSession = urlSession
    }
    
    // MARK: - AnimalRepositoryProtocol
    
    func getAnimals(latitude: Double?, longitude: Double?) async throws -> [Animal] {
        var urlComponents = URLComponents(string: "\(APIConfig.fullBaseURL)/announcements")!
        
        // Add optional location query parameters
        if let lat = latitude, let lng = longitude {
            urlComponents.queryItems = [
                URLQueryItem(name: "lat", value: String(lat)),
                URLQueryItem(name: "lng", value: String(lng))
            ]
        }
        
        guard let url = urlComponents.url else {
            throw RepositoryError.invalidURL
        }
        
        do {
            let (data, response) = try await urlSession.data(from: url)
            
            guard let httpResponse = response as? HTTPURLResponse else {
                throw RepositoryError.invalidResponse
            }
            
            guard httpResponse.statusCode == 200 else {
                throw RepositoryError.httpError(statusCode: httpResponse.statusCode)
            }
            
            let listResponse = try JSONDecoder.apiDecoder.decode(
                AnnouncementsListResponse.self,
                from: data
            )
            
            // Convert DTOs to domain models, skipping invalid items
            // compactMap filters out nil results from failable init
            let animals = listResponse.data.compactMap { dto in
                Animal(from: dto)
            }
            
            // Deduplicate by ID (keep first occurrence)
            let uniqueAnimals = Dictionary(
                animals.map { ($0.id, $0) },
                uniquingKeysWith: { first, _ in
                    print("Warning: Duplicate announcement ID: \(first.id)")
                    return first
                }
            ).values
            
            return Array(uniqueAnimals)
            
        } catch let error as DecodingError {
            print("JSON decoding error: \(error)")
            throw RepositoryError.decodingFailed(error)
        } catch {
            print("Network error: \(error)")
            throw RepositoryError.networkError(error)
        }
    }
    
    func getAnimalDetails(id: String) async throws -> PetDetails {
        guard let url = URL(string: "\(APIConfig.fullBaseURL)/announcements/\(id)") else {
            throw RepositoryError.invalidURL
        }
        
        do {
            let (data, response) = try await urlSession.data(from: url)
            
            guard let httpResponse = response as? HTTPURLResponse else {
                throw RepositoryError.invalidResponse
            }
            
            guard httpResponse.statusCode == 200 else {
                if httpResponse.statusCode == 404 {
                    throw RepositoryError.notFound
                }
                throw RepositoryError.httpError(statusCode: httpResponse.statusCode)
            }
            
            let dto = try JSONDecoder.apiDecoder.decode(PetDetailsDTO.self, from: data)
            
            // Convert DTO to domain model, throw error if invalid
            guard let details = PetDetails(from: dto) else {
                print("Error: Failed to convert DTO to PetDetails for id: \(id)")
                throw RepositoryError.invalidData
            }
            return details
            
        } catch let error as DecodingError {
            print("JSON decoding error: \(error)")
            throw RepositoryError.decodingFailed(error)
        } catch {
            print("Network error: \(error)")
            throw RepositoryError.networkError(error)
        }
    }
}

// MARK: - DTOs (Private)

private struct AnnouncementsListResponse: Codable {
    let data: [AnnouncementDTO]
}

private struct AnnouncementDTO: Codable {
    let id: String
    let petName: String
    let species: String
    let status: String
    let photoUrl: String
    let lastSeenDate: String
    let locationLatitude: Double
    let locationLongitude: Double
    let breed: String?
    let sex: String?
    let age: Int?
    let description: String
    let phone: String
    let email: String?
}

private struct PetDetailsDTO: Codable {
    let id: String
    let petName: String
    let species: String
    let status: String
    let photoUrl: String
    let lastSeenDate: String
    let locationLatitude: Double
    let locationLongitude: Double
    let breed: String?
    let sex: String?
    let age: Int?
    let microchipNumber: String?
    let email: String?
    let phone: String
    let reward: String?
    let description: String
    let createdAt: String
    let updatedAt: String
}

// MARK: - Repository Error

enum RepositoryError: Error, LocalizedError {
    case invalidURL
    case invalidResponse
    case httpError(statusCode: Int)
    case networkError(Error)
    case decodingFailed(Error)
    case notFound
    case invalidData
    
    var errorDescription: String? {
        switch self {
        case .invalidURL:
            return "Invalid URL configuration"
        case .invalidResponse:
            return "Invalid server response"
        case .httpError(let statusCode):
            return "Server error: \(statusCode)"
        case .networkError:
            return "Network connection failed"
        case .decodingFailed:
            return "Failed to parse server response"
        case .notFound:
            return "Announcement not found"
        case .invalidData:
            return "Invalid data received from server"
        }
    }
}

// MARK: - JSONDecoder Extension

extension JSONDecoder {
    static var apiDecoder: JSONDecoder {
        let decoder = JSONDecoder()
        decoder.dateDecodingStrategy = .custom { decoder in
            let container = try decoder.singleValueContainer()
            let dateString = try container.decode(String.self)
            
            // Try ISO 8601 date format (YYYY-MM-DD)
            let dateFormatter = ISO8601DateFormatter()
            dateFormatter.formatOptions = [.withFullDate]
            if let date = dateFormatter.date(from: dateString) {
                return date
            }
            
            // Try ISO 8601 datetime format (with time and timezone)
            dateFormatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
            if let date = dateFormatter.date(from: dateString) {
                return date
            }
            
            throw DecodingError.dataCorruptedError(
                in: container,
                debugDescription: "Invalid date format: \(dateString)"
            )
        }
        return decoder
    }
}

// MARK: - Domain Model Extensions

extension Animal {
    /// Failable initializer - returns nil if DTO contains invalid data
    /// Allows graceful handling of invalid items in list (skip instead of crash)
    init?(from dto: AnnouncementDTO) {
        guard let species = Species(rawValue: dto.species.lowercased()) else {
            print("Warning: Invalid species '\(dto.species)' for announcement \(dto.id), skipping item")
            return nil
        }
        guard let status = AnimalStatus(rawValue: dto.status.lowercased()) else {
            print("Warning: Invalid status '\(dto.status)' for announcement \(dto.id), skipping item")
            return nil
        }
        
        let dateFormatter = ISO8601DateFormatter()
        dateFormatter.formatOptions = [.withFullDate]
        guard let lastSeen = dateFormatter.date(from: dto.lastSeenDate) else {
            print("Warning: Invalid date '\(dto.lastSeenDate)' for announcement \(dto.id), skipping item")
            return nil
        }
        
        self.id = dto.id
        self.name = dto.petName
        self.species = species
        self.status = status
        self.photoUrl = dto.photoUrl
        self.lastSeenDate = lastSeen
        self.coordinate = Coordinate(latitude: dto.locationLatitude, longitude: dto.locationLongitude)
        self.breed = dto.breed
        self.description = dto.description
        self.contactPhone = dto.phone
    }
}

extension PetDetails {
    /// Failable initializer - returns nil if DTO contains invalid data
    init?(from dto: PetDetailsDTO) {
        guard let species = Species(rawValue: dto.species.lowercased()) else {
            print("Warning: Invalid species '\(dto.species)' for announcement \(dto.id)")
            return nil
        }
        guard let status = AnimalStatus(rawValue: dto.status.lowercased()) else {
            print("Warning: Invalid status '\(dto.status)' for announcement \(dto.id)")
            return nil
        }
        
        let dateFormatter = ISO8601DateFormatter()
        dateFormatter.formatOptions = [.withFullDate]
        guard let lastSeen = dateFormatter.date(from: dto.lastSeenDate) else {
            print("Warning: Invalid date '\(dto.lastSeenDate)' for announcement \(dto.id)")
            return nil
        }
        
        dateFormatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
        guard let created = dateFormatter.date(from: dto.createdAt) else {
            print("Warning: Invalid createdAt '\(dto.createdAt)' for announcement \(dto.id)")
            return nil
        }
        
        // updatedAt can be in two formats:
        // 1. ISO 8601: "2025-11-18T10:00:00.000Z"
        // 2. Custom format: "2025-12-01 14:24:13"
        let updated: Date
        if let isoDate = dateFormatter.date(from: dto.updatedAt) {
            updated = isoDate
        } else {
            // Fallback to custom format "YYYY-MM-DD HH:MM:SS"
            let customFormatter = DateFormatter()
            customFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
            customFormatter.timeZone = TimeZone(identifier: "UTC")
            guard let customDate = customFormatter.date(from: dto.updatedAt) else {
                print("Warning: Invalid updatedAt '\(dto.updatedAt)' for announcement \(dto.id)")
                return nil
            }
            updated = customDate
        }
        
        // Parse reward string (e.g., "500 PLN") to Double if needed
        // For now, iOS will store reward as String since backend returns it as "500 PLN"
        // If PetDetails model expects Double, we'd need to parse, but based on the current
        // domain model definition, we'll keep it as optional Double for compatibility
        let rewardValue: Double? = {
            guard let rewardStr = dto.reward else { return nil }
            // Try to extract numeric value from string like "500 PLN"
            let components = rewardStr.components(separatedBy: CharacterSet.decimalDigits.inverted)
            let numericString = components.joined()
            return Double(numericString)
        }()
        
        self.id = dto.id
        self.name = dto.petName
        self.species = species
        self.status = status
        self.photoUrl = dto.photoUrl
        self.lastSeenDate = lastSeen
        self.coordinate = Coordinate(latitude: dto.locationLatitude, longitude: dto.locationLongitude)
        self.breed = dto.breed
        self.microchipNumber = dto.microchipNumber
        self.contactEmail = dto.email
        self.contactPhone = dto.phone
        self.reward = rewardValue
        self.description = dto.description
        self.createdAt = created
        self.updatedAt = updated
    }
}
```

**Test**: Build project to verify no compilation errors.

---

## Step 3: Update ServiceContainer (10 minutes)

Wire HTTP repository into dependency injection container.

**File**: `/iosApp/iosApp/DI/ServiceContainer.swift` (UPDATE)

Find the existing `animalRepository` property and update its implementation:

```swift
// BEFORE:
lazy var animalRepository: AnimalRepositoryProtocol = FakeAnimalRepository()

// AFTER:
lazy var animalRepository: AnimalRepositoryProtocol = AnimalRepository(
    urlSession: URLSession.shared
)
```

**Note**: ViewModels and coordinators already use `AnimalRepositoryProtocol` - no changes needed.

**Test**: Build and run app - should now fetch real data from backend (if server is running).

---

## Step 4: Add Repository Unit Tests (45 minutes)

Test HTTP repository with URLSession mocking.

**File**: `/iosApp/iosAppTests/Data/Repositories/AnimalRepositoryTests.swift` (NEW)

```swift
import XCTest
@testable import iosApp

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
    
    func testGetAnimals_whenServerReturnsValidData_shouldReturnAnimals() async throws {
        // Given - valid JSON response
        let jsonData = """
        {
            "data": [
                {
                    "id": "550e8400-e29b-41d4-a716-446655440000",
                    "petName": "Max",
                    "species": "dog",
                    "status": "missing",
                    "photoUrl": "http://localhost:3000/images/max.jpg",
                    "lastSeenDate": "2024-11-15",
                    "latitude": 52.2297,
                    "longitude": 21.0122,
                    "breed": "Golden Retriever",
                    "description": "Friendly dog",
                    "contactPhone": "+48123456789"
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
        let animals = try await sut.getAnimals(latitude: nil, longitude: nil)
        
        // Then - verify parsed correctly
        XCTAssertEqual(animals.count, 1)
        XCTAssertEqual(animals[0].id, "550e8400-e29b-41d4-a716-446655440000")
        XCTAssertEqual(animals[0].name, "Max")
        XCTAssertEqual(animals[0].species, .dog)
        XCTAssertEqual(animals[0].status, .missing)
    }
    
    func testGetAnimals_withLocationParameters_shouldIncludeQueryParams() async throws {
        // Given - location parameters
        let expectedLat = 52.2297
        let expectedLng = 21.0122
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
        _ = try await sut.getAnimals(latitude: expectedLat, longitude: expectedLng)
        
        // Then - verify query parameters in URL
        let url = capturedRequest?.url
        XCTAssertNotNil(url)
        let components = URLComponents(url: url!, resolvingAgainstBaseURL: false)
        XCTAssertEqual(components?.queryItems?.count, 2)
        XCTAssertTrue(components?.queryItems?.contains(where: {
            $0.name == "lat" && $0.value == String(expectedLat)
        }) ?? false)
        XCTAssertTrue(components?.queryItems?.contains(where: {
            $0.name == "lng" && $0.value == String(expectedLng)
        }) ?? false)
    }
    
    func testGetAnimals_whenServerReturns500_shouldThrowError() async {
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
            _ = try await sut.getAnimals(latitude: nil, longitude: nil)
            XCTFail("Expected error to be thrown")
        } catch let error as RepositoryError {
            if case .httpError(let statusCode) = error {
                XCTAssertEqual(statusCode, 500)
            } else {
                XCTFail("Expected httpError, got \(error)")
            }
        }
    }
    
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
            _ = try await sut.getAnimals(latitude: nil, longitude: nil)
            XCTFail("Expected error to be thrown")
        } catch let error as RepositoryError {
            if case .decodingFailed = error {
                // Expected error
            } else {
                XCTFail("Expected decodingFailed, got \(error)")
            }
        }
    }
    
    // MARK: - getAnimalDetails Tests
    
    func testGetAnimalDetails_whenServerReturnsValidData_shouldReturnDetails() async throws {
        // Given - valid details JSON
        let jsonData = """
        {
            "id": "550e8400-e29b-41d4-a716-446655440000",
            "petName": "Max",
            "species": "dog",
            "status": "missing",
            "photoUrl": "http://localhost:3000/images/max.jpg",
            "lastSeenDate": "2024-11-15",
            "latitude": 52.2297,
            "longitude": 21.0122,
            "breed": "Golden Retriever",
            "microchipNumber": "123456789012345",
            "contactEmail": "owner@example.com",
            "contactPhone": "+48123456789",
            "reward": 500.0,
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
        
        // When - fetch details
        let details = try await sut.getAnimalDetails(id: "1")
        
        // Then - verify all fields
        XCTAssertEqual(details.id, "550e8400-e29b-41d4-a716-446655440000")
        XCTAssertEqual(details.name, "Max")
        XCTAssertEqual(details.microchipNumber, "123456789012345")
        XCTAssertEqual(details.contactEmail, "owner@example.com")
        XCTAssertEqual(details.reward, 500.0)
    }
    
    func testGetAnimalDetails_whenServerReturns404_shouldThrowNotFoundError() async {
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
            _ = try await sut.getAnimalDetails(id: "999")
            XCTFail("Expected error to be thrown")
        } catch let error as RepositoryError {
            if case .notFound = error {
                // Expected error
            } else {
                XCTFail("Expected notFound, got \(error)")
            }
        }
    }
}

// MARK: - Mock URLProtocol

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
```

**Run Tests**: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15'`

**Expected**: All repository tests pass.

---

## Step 5: Update ViewModel Tests (30 minutes)

Verify ViewModels work correctly with API integration.

**File**: `/iosApp/iosAppTests/Features/AnimalList/ViewModels/AnimalListViewModelTests.swift` (UPDATE)

Add test for API integration:

```swift
func testLoadAnimals_whenRepositorySucceeds_shouldUpdateAnimalsPublisher() async {
    // Given - fake repository with API-like data
    let expectedAnimals = [
        Animal(
            id: "550e8400-e29b-41d4-a716-446655440000",
            name: "Max",
            species: .dog,
            status: .missing,
            photoUrl: "http://localhost:3000/images/max.jpg",
            lastSeenDate: Date(),
            coordinate: Coordinate(latitude: 52.2297, longitude: 21.0122),
            breed: "Golden Retriever",
            description: "Friendly dog",
            contactPhone: "+48123456789"
        )
    ]
    fakeRepository.animalsToReturn = expectedAnimals
    
    // When - load animals
    await viewModel.loadAnimals()
    
    // Then - animals published
    XCTAssertEqual(viewModel.animals.count, 1)
    XCTAssertEqual(viewModel.animals[0].id, "550e8400-e29b-41d4-a716-446655440000")
    XCTAssertEqual(viewModel.animals[0].name, "Max")
    XCTAssertFalse(viewModel.isLoading)
}
```

**Run Tests**: Verify existing ViewModel tests still pass with no changes to ViewModel code.

---

## Step 6: Add E2E Tests (45 minutes)

Create end-to-end tests for iOS app with backend.

**File**: `/e2e-tests/mobile/specs/ios-announcements-api.spec.ts` (NEW)

```typescript
import { expect } from 'chai';
import { Given, When, Then } from '@cucumber/cucumber';
import { driver } from '../config/appium-config';
import { AnimalListScreen } from '../screens/AnimalListScreen';
import { PetDetailsScreen } from '../screens/PetDetailsScreen';

describe('iOS Announcements API Integration', function() {
    this.timeout(120000);
    
    let animalListScreen: AnimalListScreen;
    let petDetailsScreen: PetDetailsScreen;
    
    before(async function() {
        animalListScreen = new AnimalListScreen(driver);
        petDetailsScreen = new PetDetailsScreen(driver);
    });
    
    it('should display real announcements from backend on Animal List screen', async function() {
        // Given - backend has announcements in database
        // (Prerequisite: backend server running with test data)
        
        // When - user opens Animal List screen
        await driver.pause(2000); // Wait for API call
        
        // Then - announcements are displayed
        const isListDisplayed = await animalListScreen.isListDisplayed();
        expect(isListDisplayed).to.be.true;
        
        const itemCount = await animalListScreen.getAnimalCount();
        expect(itemCount).to.be.greaterThan(0);
    });
    
    it('should display pet details when tapping on animal card', async function() {
        // Given - Animal List is displayed with items
        const isListDisplayed = await animalListScreen.isListDisplayed();
        expect(isListDisplayed).to.be.true;
        
        // When - user taps first animal card
        await animalListScreen.tapFirstAnimal();
        await driver.pause(1500); // Wait for API call
        
        // Then - Pet Details screen shows complete information
        const isPetNameDisplayed = await petDetailsScreen.isPetNameDisplayed();
        expect(isPetNameDisplayed).to.be.true;
        
        const isContactPhoneDisplayed = await petDetailsScreen.isContactPhoneDisplayed();
        expect(isContactPhoneDisplayed).to.be.true;
    });
    
    it('should display error message when backend is unavailable', async function() {
        // Given - backend server is stopped (manual prerequisite)
        // This test requires manual backend shutdown for verification
        
        // When - user opens Animal List screen
        // (Restart app with backend stopped)
        
        // Then - error message is displayed
        // Note: Implement error state detection in screen object
    });
});
```

**Run E2E Tests**: 
1. Start backend: `cd server && npm run dev`
2. Add test data to backend database
3. Run tests: `npm run test:mobile:ios` (from repo root)

---

## Step 7: Manual Testing Checklist (15 minutes)

Verify integration manually before marking feature complete.

### Test Case 1: Display Announcements List
- [ ] Start backend server: `cd server && npm run dev`
- [ ] Verify backend has announcements: `curl http://localhost:3000/api/v1/announcements`
- [ ] Run iOS app in simulator
- [ ] Navigate to Animal List screen
- [ ] Verify announcements displayed (not empty state)
- [ ] Verify pet names, photos, and status badges match backend data

### Test Case 2: Location Filtering
- [ ] Grant location permissions to app
- [ ] Verify Animal List sends location query params (check backend logs)
- [ ] Deny location permissions
- [ ] Verify Animal List fetches all announcements (no query params)

### Test Case 3: Pet Details
- [ ] Tap on any animal card from list
- [ ] Verify Pet Details screen loads
- [ ] Verify all fields displayed (name, breed, description, contact, etc.)
- [ ] Verify optional fields show "—" when missing (e.g., no email)

### Test Case 4: Error Handling
- [ ] Stop backend server
- [ ] Open Animal List screen
- [ ] Verify error message displayed: "Unable to load data. Please try again later."
- [ ] Check Xcode console for error logs

### Test Case 5: Network Timeout
- [ ] Add network delay (iOS simulator: Network Link Conditioner → Very Bad Network)
- [ ] Open Animal List screen
- [ ] Verify loading indicator shown
- [ ] Wait for timeout (~60 seconds)
- [ ] Verify error message eventually displayed

---

## Verification Steps

### Build & Test Commands

```bash
# Build iOS project
xcodebuild -scheme iosApp -sdk iphonesimulator build

# Run unit tests with coverage
xcodebuild test -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 15' \
  -enableCodeCoverage YES

# View coverage report
# Xcode → Product → Show Build Folder in Finder → Logs → Test → Coverage

# Run E2E tests
cd e2e-tests
npm run test:mobile:ios
```

### Expected Results

- **Unit Tests**: All pass, coverage ≥ 80%
- **E2E Tests**: All user scenarios pass
- **Manual Tests**: All checklist items verified
- **No UI Changes**: Existing views render correctly
- **No ViewModel Changes**: Existing ViewModels work without modification

---

## Troubleshooting

### Issue: "Connection Failed" Error in App

**Solution**: Verify backend server is running on `http://localhost:3000`
```bash
cd server
npm run dev
# Server should log: "Server listening on port 3000"
```

### Issue: "Invalid Response" Error

**Solution**: Verify backend returns correct JSON structure
```bash
curl http://localhost:3000/api/v1/announcements
# Should return: {"data": [...]}
```

### Issue: URLSession Error "NSURLErrorDomain code -1022"

**Solution**: Add ATS exception to `Info.plist` (see Step 1)

### Issue: App Shows Empty List

**Solution**: Verify backend has test data
```bash
curl http://localhost:3000/api/v1/announcements
# Should return announcements, not empty array
```

---

## Rollback Plan

If integration fails, revert to mock data:

1. Open `/iosApp/iosApp/DI/ServiceContainer.swift`
2. Change repository back to fake:
   ```swift
   lazy var animalRepository: AnimalRepositoryProtocol = FakeAnimalRepository()
   ```
3. Rebuild and run app
4. App returns to mock data behavior

---

## Next Steps

After completing this quickstart:

1. Mark feature as complete in project board
2. Create pull request with changes
3. Request code review
4. Merge to main after approval
5. Verify on physical device (if available)
6. Update documentation if needed
7. Plan next feature (e.g., Android API integration)

---

## Summary

This quickstart guide provides step-by-step implementation instructions for iOS Announcements API integration. Total estimated time: **4-6 hours** including tests and verification.

**Key Files Modified/Created**:
- NEW: `/iosApp/iosApp/Configuration/APIConfig.swift`
- NEW: `/iosApp/iosApp/Data/Repositories/AnimalRepository.swift`
- UPDATE: `/iosApp/iosApp/DI/ServiceContainer.swift`
- NEW: `/iosApp/iosAppTests/Data/Repositories/AnimalRepositoryTests.swift`
- UPDATE: `/iosApp/iosAppTests/Features/AnimalList/ViewModels/AnimalListViewModelTests.swift`
- NEW: `/e2e-tests/mobile/specs/ios-announcements-api.spec.ts`

**No Changes Required**:
- ViewModels (already use protocol)
- Views (already display data from ViewModels)
- Coordinators (no navigation changes)
- Domain models (already defined)

