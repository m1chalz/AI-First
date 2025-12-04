# API Contracts: iOS Owner's Details Screen

**Feature**: iOS Owner's Details Screen (Step 4/4)  
**Date**: 2025-12-02  
**Platform**: iOS (Swift)

## Overview

This feature uses existing backend API contracts defined in spec 009 and spec 021. No new API endpoints are created for this iOS implementation - it consumes the existing announcements and photo upload APIs.

## Backend API Contracts (Existing)

### Step 1: Create Missing Pet Announcement

**API Contract**: Defined in [Spec 009: Create Announcement](../009-create-announcement/contracts/openapi.yaml)

**Endpoint**: `POST /api/v1/announcements`

**Request** (JSON):
```json
{
  "species": "DOG",
  "sex": "MALE",
  "last_seen_date": "2024-12-01",
  "location_latitude": 52.2297,
  "location_longitude": 21.0122,
  "email": "owner@example.com",
  "phone": "+48123456789",
  "status": "MISSING",
  "microchip_number": "123456789012345",
  "description": "Golden retriever, very friendly, responds to 'Max'",
  "reward": "$250 gift card + hugs"
}
```

**Response** (HTTP 201 Created):
```json
{
  "id": "bb3fc451-1f51-407d-bb85-2569dc9baed3",
  "management_password": "467432",
  "species": "DOG",
  "sex": "MALE",
  "last_seen_date": "2024-12-01",
  "location_latitude": 52.2297,
  "location_longitude": 21.0122,
  "email": "owner@example.com",
  "phone": "+48123456789",
  "status": "MISSING",
  "microchip_number": "123456789012345",
  "description": "Golden retriever, very friendly, responds to 'Max'",
  "reward": "$250 gift card + hugs",
  "photo_url": null
}
```

**Key Response Fields**:
- `id` (UUID): Announcement identifier for step 2 (photo upload)
- `management_password` (6-digit string): Password for photo upload authentication and future announcement management
- `photo_url` (null): Photo is uploaded separately in step 2

**Error Responses**:
- HTTP 400 Bad Request: Invalid input data (e.g., missing required fields, invalid email format)
- HTTP 500 Internal Server Error: Backend failure (database, email service)

**Side Effects**:
- Announcement saved to database
- Confirmation email sent asynchronously to owner (contains managementPassword)

---

### Step 2: Upload Photo for Announcement

**API Contract**: Defined in [Spec 021: Announcement Photo Upload](../021-announcement-photo-upload/contracts/openapi.yaml)

**Endpoint**: `POST /api/v1/announcements/:id/photos`

**Authentication**: Basic Auth (header: `Authorization: Basic <base64(id:managementPassword)>`)

**Request** (multipart/form-data):
```
POST /api/v1/announcements/bb3fc451-1f51-407d-bb85-2569dc9baed3/photos
Authorization: Basic YmIzZmM0NTEtMWY1MS00MDdkLWJiODUtMjU2OWRjOWJhZWQzOjQ2NzQzMg==
Content-Type: multipart/form-data; boundary=----WebKitFormBoundary

------WebKitFormBoundary
Content-Disposition: form-data; name="photo"; filename="pet.jpg"
Content-Type: image/jpeg

<binary photo data>
------WebKitFormBoundary--
```

**Response** (HTTP 201 Created):
```json
{
  "photo_url": "https://api.petspot.com/uploads/announcements/bb3fc451-1f51-407d-bb85-2569dc9baed3/pet.jpg"
}
```

**Error Responses**:
- HTTP 401 Unauthorized: Invalid or missing Basic Auth credentials
- HTTP 404 Not Found: Announcement ID does not exist
- HTTP 413 Payload Too Large: Photo file exceeds size limit
- HTTP 415 Unsupported Media Type: File is not a valid image (JPEG, PNG, WebP)
- HTTP 500 Internal Server Error: Backend failure (file storage, database)

**Side Effects**:
- Photo file saved to storage (e.g., S3, local disk)
- Announcement record updated with `photo_url`

---

## iOS Client Implementation

### AnimalRepositoryProtocol (Extended)

Extending existing Swift protocol with announcement operations:

```swift
protocol AnimalRepositoryProtocol {
    // ... existing methods (getAnimals, getPetDetails) ...
    
    /// Step 1: Create missing pet announcement
    /// - Parameter request: CreateAnnouncementRequest with announcement data
    /// - Returns: AnnouncementResponse with id and managementPassword
    /// - Throws: NetworkError (offline, timeout, backend 4xx/5xx)
    func createAnnouncement(request: CreateAnnouncementRequest) async throws -> AnnouncementResponse
    
    /// Step 2: Upload photo for existing announcement
    /// - Parameter request: PhotoUploadRequest with announcementId, photo metadata, and managementPassword
    /// - Throws: NetworkError (auth failure, backend 4xx/5xx)
    func uploadPhoto(request: PhotoUploadRequest) async throws
}
```

### AnimalRepository Implementation (Extended)

Adding new methods to existing `AnimalRepository` class:

```swift
class AnimalRepository: AnimalRepositoryProtocol {
    private let httpClient: HTTPClient
    private let baseURL = "https://api.petspot.com/api/v1"
    
    init(httpClient: HTTPClient) {
        self.httpClient = httpClient
    }
    
    // ... existing methods (getAnimals, getPetDetails) ...
    
    func createAnnouncement(request: CreateAnnouncementRequest) async throws -> AnnouncementResponse {
        let url = URL(string: "\(baseURL)/announcements")!
        let encoder = JSONEncoder()
        encoder.keyEncodingStrategy = .convertToSnakeCase
        let body = try encoder.encode(request)
        
        var urlRequest = URLRequest(url: url)
        urlRequest.httpMethod = "POST"
        urlRequest.setValue("application/json", forHTTPHeaderField: "Content-Type")
        urlRequest.httpBody = body
        
        let (data, response) = try await httpClient.data(for: urlRequest)
        
        guard let httpResponse = response as? HTTPURLResponse else {
            throw NetworkError.invalidResponse
        }
        
        guard httpResponse.statusCode == 201 else {
            throw NetworkError.httpError(statusCode: httpResponse.statusCode)
        }
        
        let decoder = JSONDecoder()
        decoder.keyDecodingStrategy = .convertFromSnakeCase
        return try decoder.decode(AnnouncementResponse.self, from: data)
    }
    
    func uploadPhoto(request: PhotoUploadRequest) async throws {
        let url = URL(string: "\(baseURL)/announcements/\(request.announcementId)/photos")!
        
        // Load photo data from disk cache
        let photoData = try Data(contentsOf: request.photo.cachedURL)
        
        // Build multipart form-data
        let boundary = "Boundary-\(UUID().uuidString)"
        var body = Data()
        
        body.append("--\(boundary)\r\n".data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"photo\"; filename=\"pet.jpg\"\r\n".data(using: .utf8)!)
        body.append("Content-Type: \(request.photo.mimeType)\r\n\r\n".data(using: .utf8)!)
        body.append(photoData)
        body.append("\r\n--\(boundary)--\r\n".data(using: .utf8)!)
        
        // Build request with Basic Auth
        var urlRequest = URLRequest(url: url)
        urlRequest.httpMethod = "POST"
        urlRequest.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
        
        let credentials = "\(request.announcementId):\(request.managementPassword)"
        let base64Credentials = credentials.data(using: .utf8)!.base64EncodedString()
        urlRequest.setValue("Basic \(base64Credentials)", forHTTPHeaderField: "Authorization")
        
        urlRequest.httpBody = body
        
        let (_, response) = try await httpClient.data(for: urlRequest)
        
        guard let httpResponse = response as? HTTPURLResponse else {
            throw NetworkError.invalidResponse
        }
        
        guard httpResponse.statusCode == 201 else {
            throw NetworkError.httpError(statusCode: httpResponse.statusCode)
        }
    }
}
```

---

## Error Handling

### Network Errors

iOS client detects and handles the following error scenarios:

| Error Type | Detection | User Message | Retry Behavior |
|------------|-----------|--------------|----------------|
| Offline (no network) | `URLError.notConnectedToInternet` | "No connection. Please check your network and try again." | User taps "Try Again" → full 2-step retry |
| Timeout | `URLError.timedOut` | "Something went wrong. Please try again later." | User taps "Try Again" → full 2-step retry |
| Backend 4xx | HTTP 400-499 | "Something went wrong. Please try again later." | User taps "Try Again" → full 2-step retry |
| Backend 5xx | HTTP 500-599 | "Something went wrong. Please try again later." | User taps "Try Again" → full 2-step retry |
| Invalid response | Non-HTTP response | "Something went wrong. Please try again later." | User taps "Try Again" → full 2-step retry |

### Partial Failure Handling

**Out of Scope for Initial Implementation**:
- Step 1 succeeds, Step 2 fails → Currently retries full 2-step flow (creates duplicate announcement)
- Future iteration: Store `announcementId` locally, retry only Step 2 on failure

---

## Contract Validation

### Step 1 Request Validation (iOS)

Before sending request, iOS client validates:
- `phone`: 7-11 digits (after sanitization: remove spaces/dashes)
- `email`: RFC 5322 basic format (local@domain.tld)
- `reward`: Max 120 characters (enforced by ValidatedTextField maxLength)

Backend performs additional validation (e.g., species enum, location bounds).

### Step 2 Request Validation (iOS)

Before sending request, iOS client validates:
- Photo file exists at `PhotoAttachmentMetadata.cachedURL`
- Photo file is readable (can load Data)
- `managementPassword` is non-empty (from step 1 response)

Backend validates:
- Basic Auth credentials match announcement ID
- Photo file is valid image (JPEG, PNG, WebP)
- Photo file size is within limits

---

## References

- [Spec 009: Create Announcement API](../009-create-announcement/spec.md) - Step 1 contract
- [Spec 009: OpenAPI Contract](../009-create-announcement/contracts/openapi.yaml) - Step 1 OpenAPI schema
- [Spec 021: Announcement Photo Upload API](../021-announcement-photo-upload/spec.md) - Step 2 contract
- [Spec 021: OpenAPI Contract](../021-announcement-photo-upload/contracts/openapi.yaml) - Step 2 OpenAPI schema
- [Data Model](./data-model.md) - iOS request/response models

