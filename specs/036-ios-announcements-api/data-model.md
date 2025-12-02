# Data Models: iOS Announcements API Integration

**Feature**: 036-ios-announcements-api  
**Platform**: iOS  
**Date**: 2025-12-01

## Overview

This document defines iOS domain models and their mapping to backend API responses. iOS uses Swift structs for immutable models with Codable for JSON parsing.

## Domain Models (iOS)

### Animal

**Purpose**: Represents a pet announcement for display in Animal List screen.

**Location**: `/iosApp/iosApp/Domain/Models/Animal.swift` (EXISTING - no changes)

**Swift Definition**:
```swift
struct Animal: Identifiable, Codable {
    let id: String
    let name: String
    let species: Species
    let status: AnimalStatus
    let photoUrl: String
    let lastSeenDate: Date
    let coordinate: Coordinate
    let breed: String?
    let description: String
    let contactPhone: String
}

enum Species: String, Codable {
    case dog
    case cat
    case bird
    case other
}

enum AnimalStatus: String, Codable {
    case missing
    case found
}

struct Coordinate: Codable {
    let latitude: Double
    let longitude: Double
}
```

**Validation Rules**:
- `id`: Required, non-empty string
- `name`: Required, non-empty string (petName from backend)
- `species`: Required, must map to valid Species enum case
- `status`: Required, must map to valid AnimalStatus enum case
- `photoUrl`: Required, non-empty string (URL format)
- `lastSeenDate`: Required, valid ISO 8601 date
- `coordinate`: Required, valid latitude (-90 to 90) and longitude (-180 to 180)
- `breed`: Optional, can be nil
- `description`: Required, non-empty string
- `contactPhone`: Required, non-empty string

**Notes**:
- Existing model already defined in previous features
- No changes needed to model structure
- Backend field `petName` maps to Swift property `name` (custom CodingKeys)

---

### PetDetails

**Purpose**: Represents complete pet information for Pet Details screen.

**Location**: `/iosApp/iosApp/Domain/Models/PetDetails.swift` (EXISTING - no changes)

**Swift Definition**:
```swift
struct PetDetails: Identifiable, Codable {
    let id: String
    let name: String
    let species: Species
    let status: AnimalStatus
    let photoUrl: String
    let lastSeenDate: Date
    let coordinate: Coordinate
    let breed: String?
    let microchipNumber: String?
    let contactEmail: String?
    let contactPhone: String
    let reward: Double?
    let description: String
    let createdAt: Date
    let updatedAt: Date
}
```

**Validation Rules**:
- All required fields from `Animal` model
- `microchipNumber`: Optional, can be nil
- `contactEmail`: Optional, can be nil (must be valid email format if present)
- `reward`: Optional, can be nil (positive number if present)
- `createdAt`: Required, valid ISO 8601 datetime
- `updatedAt`: Required, valid ISO 8601 datetime

**Notes**:
- Extends `Animal` model with additional fields for detail view
- Backend field `petName` maps to Swift property `name` (custom CodingKeys)
- Existing model already defined in previous features

---

## API Response Models (iOS)

### AnnouncementsListResponse

**Purpose**: Wrapper for list endpoint response containing array of announcements.

**Location**: `/iosApp/iosApp/Data/Repositories/AnimalRepository.swift` (internal DTO)

**Swift Definition**:
```swift
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
    
    enum CodingKeys: String, CodingKey {
        case id, petName, species, status, photoUrl, lastSeenDate
        case locationLatitude, locationLongitude, breed, sex, age
        case description, phone, email
    }
}
```

**Notes**:
- Private DTO (Data Transfer Object) for JSON parsing only
- Maps backend response fields exactly as received
- Converted to domain `Animal` model after parsing
- List endpoint wraps array in `{ data: [...] }` structure

---

### PetDetailsDTO

**Purpose**: Backend response for single pet details (no wrapper).

**Location**: `/iosApp/iosApp/Data/Repositories/AnimalRepository.swift` (internal DTO)

**Swift Definition**:
```swift
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
    
    enum CodingKeys: String, CodingKey {
        case id, petName, species, status, photoUrl, lastSeenDate
        case locationLatitude, locationLongitude, breed, sex, age
        case microchipNumber, email, phone, reward, description
        case createdAt, updatedAt
    }
}
```

**Notes**:
- Private DTO for JSON parsing only
- Details endpoint returns single object (no wrapper)
- Converted to domain `PetDetails` model after parsing
- All date strings (ISO 8601) parsed to Swift `Date`

---

## Field Mapping (Backend → iOS)

### Type Conversions

| Backend Type | iOS Type | Conversion Logic |
|-------------|----------|------------------|
| `string` (id, UUID) | `String` | Direct mapping (no conversion needed) |
| `string` (petName) | `String` (name) | Direct mapping with custom CodingKeys |
| `string` (species) | `Species` enum | Map string to enum case (lowercase) - backend returns UPPERCASE |
| `string` (status) | `AnimalStatus` enum | Map string to enum case (lowercase) - backend returns UPPERCASE |
| `string` (photoUrl) | `String` | Direct mapping |
| `string` (ISO date) | `Date` | Parse with ISO8601DateFormatter |
| `number` (locationLatitude, locationLongitude) | `Coordinate` struct | Combine into single struct |
| `string?` (breed) | `String?` | Optional - direct mapping |
| `string?` (sex) | N/A | Backend field not used in iOS domain model |
| `number?` (age) | N/A | Backend field not used in iOS domain model |
| `string?` (microchipNumber) | `String?` | Optional - direct mapping |
| `string?` (email) | `String?` | Optional - direct mapping |
| `string` (phone) | `String` | Direct mapping |
| `string?` (reward) | `Double?` | Parse numeric value from string (e.g., "500 PLN" → 500.0) |
| `string` (description) | `String` | Direct mapping |
| `string` (ISO datetime) | `Date` | Parse with ISO8601DateFormatter - **NOTE: updatedAt uses different format!** |

### Custom CodingKeys

**Animal and PetDetails models**:
```swift
enum CodingKeys: String, CodingKey {
    case id, species, status, photoUrl, lastSeenDate
    case coordinate, breed, description, contactPhone
    case microchipNumber, contactEmail, reward
    case createdAt, updatedAt
    case name = "petName"  // Backend uses "petName", iOS uses "name"
}
```

**Coordinate struct**:
```swift
enum CodingKeys: String, CodingKey {
    case latitude, longitude
}
```

---

## Data Transformation Logic

### DTO → Domain Model Conversion

**AnnouncementDTO → Animal**:
```swift
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
```

**Usage in Repository**:
```swift
// Convert DTOs to domain models, skipping invalid items
let animals = listResponse.data.compactMap { dto in
    Animal(from: dto)
}
```

**PetDetailsDTO → PetDetails**:
```swift
extension PetDetails {
    /// Failable initializer - returns nil if DTO contains invalid data
    init?(from dto: PetDetailsDTO) {
        // Same enum validation as Animal
        guard let species = Species(rawValue: dto.species.lowercased()) else {
            print("Warning: Invalid species '\(dto.species)' for announcement \(dto.id)")
            return nil
        }
        guard let status = AnimalStatus(rawValue: dto.status.lowercased()) else {
            print("Warning: Invalid status '\(dto.status)' for announcement \(dto.id)")
            return nil
        }
        
        // Date parsing with ISO8601DateFormatter
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
        
        // Parse reward string (e.g., "500 PLN") to Double
        let rewardValue: Double? = {
            guard let rewardStr = dto.reward else { return nil }
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

**Usage in Repository**:
```swift
// For single item, throw error if invalid
guard let details = PetDetails(from: dto) else {
    throw RepositoryError.invalidData
}
return details
```

---

## Date Format Inconsistency (Backend Issue)

**Critical**: Backend returns two different date formats in the same response:

- **`createdAt`**: ISO 8601 format with timezone
  - Example: `"2025-11-18T10:00:00.000Z"`
  - Format: `YYYY-MM-DDTHH:MM:SS.sssZ`
  - Parseable by `ISO8601DateFormatter`

- **`updatedAt`**: Custom format without timezone
  - Example: `"2025-12-01 14:24:13"`
  - Format: `YYYY-MM-DD HH:MM:SS` (space instead of 'T', no timezone)
  - NOT parseable by `ISO8601DateFormatter`

**Solution**: iOS client must handle both formats with fallback:
1. Try parsing with `ISO8601DateFormatter` first
2. If fails, fallback to `DateFormatter` with pattern `"yyyy-MM-dd HH:mm:ss"`
3. Assume UTC timezone for custom format

This ensures compatibility with current backend implementation while remaining forward-compatible if backend standardizes to ISO 8601.

---

## Error Handling

### Parsing Errors

**List Endpoint - Graceful Degradation**:

When converting list of DTOs to domain models, invalid items are **skipped** instead of failing entire list:

**Invalid Species/Status Enum**:
- Backend returns unrecognized value (e.g., `"rabbit"` when iOS only has `dog`, `cat`, `bird`, `other`)
- Behavior: Failable init returns `nil`, item is logged and skipped
- Log: `"Warning: Invalid species 'rabbit' for announcement {id}, skipping item"`
- Repository uses `compactMap` to filter out `nil` results
- Result: Valid items displayed, invalid items skipped

**Invalid Date Format**:
- Backend returns date in wrong format (e.g., `"2024-13-01"` - invalid month)
- Behavior: Failable init returns `nil`, item is logged and skipped
- Log: `"Warning: Invalid date '2024-13-01' for announcement {id}, skipping item"`
- Result: Valid items displayed, invalid items skipped

**Malformed JSON (entire response)**:
- Backend returns completely invalid JSON structure
- Behavior: JSONDecoder throws decoding error before reaching domain conversion
- Repository catches error, logs, throws to ViewModel
- ViewModel displays generic error message: "Unable to load data. Please try again later."

**Details Endpoint - Fail Fast**:

When fetching single pet details, any invalid data results in error (no graceful degradation):

**Invalid Data in Details**:
- Backend returns invalid species/status/date for single pet
- Behavior: Failable init returns `nil`, repository throws `RepositoryError.invalidData`
- ViewModel displays error message: "Unable to load pet details"
- User can go back to list and try another pet

**Missing Required Field**:
- Backend omits required field (e.g., missing `petName`)
- Behavior: Swift Codable throws decoding error automatically
- Repository catches error, logs, throws to ViewModel
- ViewModel displays generic error message

---

## State Transitions

No complex state transitions in this feature. Models are immutable value types (structs) representing snapshots of data at a point in time.

**Lifecycle**:
1. Backend returns JSON response
2. Repository decodes JSON to DTO
3. Repository converts DTO to domain model
4. Domain model passed to ViewModel
5. ViewModel publishes model via `@Published` property
6. SwiftUI view observes and renders

---

## Validation Summary

| Field | Required | Validation | Error Handling |
|-------|----------|-----------|----------------|
| id | ✅ | Non-empty string | Codable throws if missing |
| name (petName) | ✅ | Non-empty string | Codable throws if missing |
| species | ✅ | Valid enum case | Custom init throws if invalid |
| status | ✅ | Valid enum case | Custom init throws if invalid |
| photoUrl | ✅ | Non-empty string | Codable throws if missing |
| lastSeenDate | ✅ | Valid ISO 8601 date | Custom init throws if invalid |
| coordinate | ✅ | Valid lat/lng | Codable throws if missing |
| breed | ❌ | Optional | No validation |
| microchipNumber | ❌ | Optional | No validation |
| contactEmail | ❌ | Optional | No validation (UI may validate) |
| contactPhone | ✅ | Non-empty string | Codable throws if missing |
| reward | ❌ | Optional positive number | No validation |
| description | ✅ | Non-empty string | Codable throws if missing |
| createdAt | ✅ | Valid ISO 8601 datetime | Custom init throws if invalid |
| updatedAt | ✅ | Valid ISO 8601 datetime | Custom init throws if invalid |

---

## Testing Data

### Sample Valid Response (List)

```json
{
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "petName": "Max",
      "species": "DOG",
      "status": "MISSING",
      "photoUrl": "/images/max.jpg",
      "lastSeenDate": "2024-11-15",
      "locationLatitude": 52.2297,
      "locationLongitude": 21.0122,
      "breed": "Golden Retriever",
      "sex": "MALE",
      "age": 5,
      "description": "Friendly golden retriever, responds to Max",
      "phone": "+48123456789",
      "email": "owner@example.com"
    }
  ]
}
```

### Sample Valid Response (Details)

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "petName": "Max",
  "species": "DOG",
  "status": "MISSING",
  "photoUrl": "/images/max.jpg",
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
  "description": "Friendly golden retriever, responds to Max",
  "createdAt": "2024-11-20T10:30:00.000Z",
  "updatedAt": "2024-12-01 14:24:13"
}
```

### Sample Error Cases

**Invalid Species**:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "petName": "Max",
  "species": "DINOSAUR",  // Invalid - not in iOS enum (even after lowercasing)
  "status": "MISSING",
  "locationLatitude": 52.2297,
  "locationLongitude": 21.0122,
  // ... rest of fields
}
```

**Missing Required Field**:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  // Missing "petName"
  "species": "DOG",
  "status": "MISSING",
  "locationLatitude": 52.2297,
  "locationLongitude": 21.0122,
  // ... rest of fields
}
```

**Invalid Date Format**:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "petName": "Max",
  "species": "DOG",
  "status": "MISSING",
  "photoUrl": "/images/max.jpg",
  "lastSeenDate": "15-11-2024",  // Invalid format (should be YYYY-MM-DD)
  "locationLatitude": 52.2297,
  "locationLongitude": 21.0122,
  // ... rest of fields
}
```

---

## Summary

- **Domain Models**: Existing `Animal` and `PetDetails` structs (no changes)
- **DTOs**: Private structs in repository for JSON parsing
- **Validation**: Enum mapping, date parsing, required field checks
- **Error Handling**: Throw on invalid data, log errors, show generic message to user
- **Testing**: Unit tests for DTO→Domain conversion with valid and error cases

**Next Step**: Generate API contracts in `/contracts` directory.

