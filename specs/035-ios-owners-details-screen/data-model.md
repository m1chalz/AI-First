# Data Model: iOS Owner's Details Screen

**Feature**: iOS Owner's Details Screen (Step 4/4)  
**Date**: 2025-12-02  
**Platform**: iOS (Swift)

## Overview

This document defines the data models used in the iOS Owner's Details screen implementation. Models follow Swift conventions (structs for value types, classes for reference types) and align with iOS MVVM-C architecture from the constitution.

## Domain Models

### OwnerContactDetails

**Purpose**: Session-bound structure containing contact information for Step 4

**Type**: Struct (value type, immutable after creation)

**Location**: Part of `ReportMissingPetFlowState` (defined in spec 017)

```swift
struct OwnerContactDetails {
    var phone: String
    var email: String
    var rewardDescription: String?
}
```

**Relationships**:
- Stored in `ReportMissingPetFlowState.contactDetails`
- Populated by `ContactDetailsViewModel` after successful validation

**Notes**:
- This is a simple data structure with no validation logic
- Validation is performed by `ContactDetailsViewModel` before saving to FlowState
- Values stored here are assumed to be already validated

---

### CreateAnnouncementRequest

**Purpose**: DTO for step 1 submission (POST /api/v1/announcements)

**Type**: Struct (value type, Codable for JSON serialization)

**Location**: `/iosApp/iosApp/Domain/Models/` or `/iosApp/iosApp/Data/DTOs/`

```swift
struct CreateAnnouncementRequest: Codable {
    let species: String                 // "DOG" or "CAT"
    let sex: String                     // "MALE" or "FEMALE"
    let lastSeenDate: String            // ISO 8601 date string (YYYY-MM-DD)
    let locationLatitude: Double
    let locationLongitude: Double
    let email: String
    let phone: String
    let status: String                  // "MISSING" (constant)
    let microchipNumber: String?        // Optional (Step 1)
    let description: String?            // Optional (Step 3)
    let reward: String?                 // Optional (Step 4)
    
    // CodingKeys for snake_case JSON mapping
    enum CodingKeys: String, CodingKey {
        case species
        case sex
        case lastSeenDate = "last_seen_date"
        case locationLatitude = "location_latitude"
        case locationLongitude = "location_longitude"
        case email
        case phone
        case status
        case microchipNumber = "microchip_number"
        case description
        case reward
    }
}
```

**Source Data**:
- `species`: From Step 3 (`flowState.animalSpecies`)
- `sex`: From Step 3 (`flowState.animalGender`)
- `lastSeenDate`: From Step 3 (`flowState.disappearanceDate`)
- `locationLatitude`, `locationLongitude`: From Step 3 (`flowState.animalLatitude`, `flowState.animalLongitude`)
- `microchipNumber`: From Step 1 (`flowState.chipNumber` - optional)
- `description`: From Step 3 (`flowState.animalAdditionalDescription` - optional)
- `email`, `phone`, `reward`: From Step 4 (`flowState.contactDetails`)

**Mapping**:
```swift
// Construct from FlowState
let request = CreateAnnouncementRequest(
    species: flowState.animalSpecies!.rawValue.uppercased(), // e.g., "DOG"
    sex: flowState.animalGender!.rawValue.uppercased(),      // e.g., "MALE"
    lastSeenDate: ISO8601DateFormatter().string(from: flowState.disappearanceDate!),
    locationLatitude: flowState.animalLatitude ?? 0.0,
    locationLongitude: flowState.animalLongitude ?? 0.0,
    email: flowState.contactDetails!.email,
    phone: flowState.contactDetails!.phone,
    status: "MISSING",
    microchipNumber: flowState.chipNumber,                    // Optional
    description: flowState.animalAdditionalDescription,       // Optional
    reward: flowState.contactDetails!.rewardDescription       // Optional
)
```

---

### AnnouncementResponse

**Purpose**: Response DTO for step 1 (POST /api/v1/announcements returns HTTP 201)

**Type**: Struct (value type, Codable for JSON deserialization)

**Location**: `/iosApp/iosApp/Domain/Models/` or `/iosApp/iosApp/Data/DTOs/`

```swift
struct AnnouncementResponse: Codable {
    let id: String                      // UUID (e.g., "bb3fc451-1f51-407d-bb85-2569dc9baed3")
    let managementPassword: String      // 6-digit string (e.g., "467432")
    let species: String
    let sex: String
    let lastSeenDate: String
    let locationLatitude: Double
    let locationLongitude: Double
    let email: String
    let phone: String
    let status: String
    let microchipNumber: String?
    let description: String?
    let reward: String?
    let photoUrl: String?               // Always null (photo uploaded separately)
    
    // CodingKeys for snake_case JSON mapping
    enum CodingKeys: String, CodingKey {
        case id
        case managementPassword = "management_password"
        case species
        case sex
        case lastSeenDate = "last_seen_date"
        case locationLatitude = "location_latitude"
        case locationLongitude = "location_longitude"
        case email
        case phone
        case status
        case microchipNumber = "microchip_number"
        case description
        case reward
        case photoUrl = "photo_url"
    }
}
```

**Usage**:
- Extract `id` and `managementPassword` for step 2 (photo upload with Basic auth)
- Pass `managementPassword` to summary screen via coordinator closure `onReportSent(managementPassword: String)`

---

### PhotoUploadRequest

**Purpose**: Metadata for step 2 submission (POST /api/v1/announcements/:id/photos)

**Type**: Struct (value type)

**Location**: `/iosApp/iosApp/Domain/Models/` or `/iosApp/iosApp/Data/DTOs/`

```swift
struct PhotoUploadRequest {
    let announcementId: String          // From step 1 response (AnnouncementResponse.id)
    let photo: PhotoAttachmentMetadata  // Contains cachedURL (file path to disk cache)
    let managementPassword: String      // From step 1 response (for Basic auth)
}
```

**Notes**:
- Repository loads photo `Data` from `photo.cachedURL` during upload
- Basic auth header: `Authorization: Basic <base64(announcementId:managementPassword)>`
- Multipart form-data with `photo` file field

---

### PhotoAttachmentMetadata

**Purpose**: Metadata for photo stored in disk cache (from spec 028)

**Type**: Struct (value type) - already defined in spec 028

**Location**: `/iosApp/iosApp/Domain/Models/` (reused from spec 028)

```swift
struct PhotoAttachmentMetadata {
    let cachedURL: URL              // File path to Library/Caches/PetSpot/ReportMissingPet
    let mimeType: String            // e.g., "image/jpeg"
    let fileSize: Int64             // Bytes
    let capturedAt: Date            // Timestamp when photo was taken/selected
}
```

**Usage**:
- Stored in `ReportMissingPetFlowState.photoAttachment` (PhotoAttachmentMetadata)
- Passed to repository for photo upload (step 2)
- Repository reads `Data(contentsOf: photo.cachedURL)` to get file bytes

---

## Presentation Models

### ContactDetailsViewModel (ObservableObject)

**Purpose**: ViewModel for ContactDetailsView, manages input state, validation, and 2-step submission

**Type**: Class (reference type, ObservableObject)

**Location**: `/iosApp/iosApp/Features/ReportMissingPet/ContactDetails/Views/ContactDetailsViewModel.swift` (modify existing placeholder)

```swift
@MainActor
class ContactDetailsViewModel: ObservableObject {
    // MARK: - Input State (bound to ValidatedTextField)
    @Published var phone: String = ""
    @Published var email: String = ""
    @Published var rewardDescription: String = ""
    
    // MARK: - Validation Error State (displayed in ValidatedTextField)
    @Published var phoneError: String? = nil
    @Published var emailError: String? = nil
    
    // MARK: - Loading State (controls Continue button spinner and back button)
    @Published var isSubmitting: Bool = false
    
    // MARK: - Alert State (popup for submission errors)
    @Published var alertMessage: String? = nil
    @Published var showAlert: Bool = false
    
    // MARK: - Dependencies (injected via initializer)
    private let submissionService: AnnouncementSubmissionService
    private let flowState: ReportMissingPetFlowState
    
    // MARK: - Coordinator Callback (navigation to summary on success)
    var onReportSent: ((String) -> Void)?  // Passes managementPassword
    
    // MARK: - Initializer (Manual DI)
    init(submissionService: AnnouncementSubmissionService, flowState: ReportMissingPetFlowState) {
        self.submissionService = submissionService
        self.flowState = flowState
        
        // Prepopulate from FlowState if returning from summary
        self.phone = flowState.contactDetails?.phone ?? ""
        self.email = flowState.contactDetails?.email ?? ""
        self.rewardDescription = flowState.contactDetails?.rewardDescription ?? ""
    }
    
    // MARK: - Computed Properties (validation)
    var isFormValid: Bool {
        return isPhoneValid && isEmailValid
    }
    
    private var isPhoneValid: Bool {
        let sanitized = phone.filter { $0.isNumber || $0 == "+" }
        let digitCount = sanitized.filter { $0.isNumber }.count
        return digitCount >= 7 && digitCount <= 11
    }
    
    private var isEmailValid: Bool {
        let emailRegex = #"^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$"#
        let predicate = NSPredicate(format: "SELF MATCHES[c] %@", emailRegex)
        return predicate.evaluate(with: email.trimmingCharacters(in: .whitespaces))
    }
    
    // MARK: - Actions
    
    /// Validates inputs and submits announcement via service
    func submitForm() async {
        // Clear previous errors
        phoneError = nil
        emailError = nil
        
        // Validate phone
        if !isPhoneValid {
            phoneError = L10n.tr("owners_details.phone.error")  // "Enter at least 7 digits"
            return
        }
        
        // Validate email
        if !isEmailValid {
            emailError = L10n.tr("owners_details.email.error")  // "Enter a valid email address"
            return
        }
        
        // Save validated inputs to FlowState
        flowState.contactDetails = OwnerContactDetails(
            phone: phone,
            email: email,
            rewardDescription: rewardDescription.isEmpty ? nil : rewardDescription
        )
        
        // Start submission
        isSubmitting = true
        defer { isSubmitting = false }
        
        do {
            // Delegate to service for 2-step submission
            let managementPassword = try await submissionService.submitAnnouncement(flowState: flowState)
            
            // Success: navigate to summary with managementPassword
            onReportSent?(managementPassword)
            
            // Emit analytics event
            // AnalyticsService.track("missing_pet.step4_completed")
            
        } catch {
            // Failure: show error popup
            handleSubmissionError(error)
        }
    }
    
    private func handleSubmissionError(_ error: Error) {
        // Determine error type (network vs backend)
        if (error as NSError).domain == NSURLErrorDomain {
            // Network error (offline, timeout)
            alertMessage = L10n.tr("owners_details.error.no_connection.message")
        } else {
            // Backend error (4xx/5xx)
            alertMessage = L10n.tr("owners_details.error.generic.message")
        }
        showAlert = true
    }
}
```

**Responsibilities** (UI concerns only):
- Manage input state (`phone`, `email`, `rewardDescription`)
- Validate inputs on Continue tap (`isPhoneValid`, `isEmailValid`)
- Display validation errors inline (`phoneError`, `emailError`)
- Manage loading state (`isSubmitting`)
- Display submission errors via popup alerts (`alertMessage`, `showAlert`)
- Navigate to summary on success via `onReportSent(managementPassword)` closure

**State Properties**:
- Input state: `phone`, `email`, `rewardDescription` (bound to ValidatedTextField via `$` binding)
- Validation errors: `phoneError`, `emailError` (displayed inline by ValidatedTextField)
- Loading state: `isSubmitting` (controls Continue button spinner and back button disabled state)
- Alert state: `alertMessage`, `showAlert` (popup for submission errors)

**Methods**:
- `submitForm() async`: Main action for Continue button tap
  1. Validate inputs (phone, email)
  2. Save to FlowState.contactDetails
  3. Delegate to service: `submissionService.submitAnnouncement(flowState:)`
  4. On success: invoke `onReportSent(managementPassword)` closure
  5. On failure: show popup alert with retry

**Dependencies** (Manual DI):
- `AnnouncementSubmissionService`: Business logic for 2-step submission
- `ReportMissingPetFlowState`: Session state container

---

## Service Layer Models

### AnnouncementSubmissionService

**Purpose**: Business logic service for 2-step announcement submission (orchestrates repository calls)

**Type**: Class (reference type)

**Location**: `/iosApp/iosApp/Domain/Services/AnnouncementSubmissionService.swift`

```swift
/// Orchestrates 2-step announcement submission (create + photo upload)
class AnnouncementSubmissionService {
    private let repository: AnimalRepositoryProtocol
    
    init(repository: AnimalRepositoryProtocol) {
        self.repository = repository
    }
    
    /// Submits complete announcement with photo
    /// - Parameter flowState: ReportMissingPetFlowState with all data from Steps 1-4
    /// - Returns: managementPassword for summary screen
    /// - Throws: NetworkError on submission failure
    func submitAnnouncement(flowState: ReportMissingPetFlowState) async throws -> String {
        // Build request from FlowState
        let request = buildAnnouncementRequest(from: flowState)
        
        // Step 1: Create announcement
        let response = try await repository.createAnnouncement(request: request)
        
        // Step 2: Upload photo (if exists)
        if let photoAttachment = flowState.photoAttachment {
            let uploadRequest = PhotoUploadRequest(
                announcementId: response.id,
                photo: photoAttachment,
                managementPassword: response.managementPassword
            )
            try await repository.uploadPhoto(request: uploadRequest)
        }
        
        // Return managementPassword for summary
        return response.managementPassword
    }
    
    private func buildAnnouncementRequest(from flowState: ReportMissingPetFlowState) -> CreateAnnouncementRequest {
        guard let contactDetails = flowState.contactDetails else {
            fatalError("contactDetails must be set before submission")
        }
        
        guard let species = flowState.animalSpecies else {
            fatalError("animalSpecies must be set before submission")
        }
        
        guard let gender = flowState.animalGender else {
            fatalError("animalGender must be set before submission")
        }
        
        guard let disappearanceDate = flowState.disappearanceDate else {
            fatalError("disappearanceDate must be set before submission")
        }
        
        return CreateAnnouncementRequest(
            species: species.rawValue.uppercased(),  // "DOG" or "CAT"
            sex: gender.rawValue.uppercased(),       // "MALE" or "FEMALE"
            lastSeenDate: ISO8601DateFormatter().string(from: disappearanceDate),
            locationLatitude: flowState.animalLatitude ?? 0.0,  // Default to 0.0 if not set
            locationLongitude: flowState.animalLongitude ?? 0.0,
            email: contactDetails.email.trimmingCharacters(in: .whitespaces),
            phone: contactDetails.phone.filter { $0.isNumber || $0 == "+" },  // Sanitize
            status: "MISSING",
            microchipNumber: flowState.chipNumber,
            description: flowState.animalAdditionalDescription,
            reward: contactDetails.rewardDescription
        )
    }
}
```

**Responsibilities**:
- Build CreateAnnouncementRequest from ReportMissingPetFlowState
- Orchestrate 2-step submission (announcement creation → photo upload)
- Extract managementPassword from response
- Throw errors for ViewModel to handle (network, backend errors)

**Dependency Injection**:
- Receives `AnimalRepositoryProtocol` in constructor (existing repository extended with announcement methods)
- Injected into `ContactDetailsViewModel` by coordinator

---

## Repository Layer Models

### AnimalRepositoryProtocol (Extend Existing)

**Purpose**: Protocol for animal/announcement operations - data layer only

**Type**: Protocol (interface) - **ALREADY EXISTS**

**Location**: `/iosApp/iosApp/Domain/Repositories/AnimalRepositoryProtocol.swift`

**Existing Methods**:
- `getAnimals(near: UserLocation?) async throws -> [Animal]`
- `getPetDetails(id: String) async throws -> PetDetails`

**ADD New Methods for Announcements**:
```swift
protocol AnimalRepositoryProtocol {
    // ... existing methods (getAnimals, getPetDetails) ...
    
    /// Creates a new missing pet announcement
    /// - Parameter request: Announcement data (species, contact, location, etc.)
    /// - Returns: AnnouncementResponse with id and managementPassword
    /// - Throws: NetworkError on failure
    func createAnnouncement(request: CreateAnnouncementRequest) async throws -> AnnouncementResponse
    
    /// Uploads photo for an existing announcement
    /// - Parameter request: Photo metadata with announcementId and managementPassword for auth
    /// - Throws: NetworkError on failure
    func uploadPhoto(request: PhotoUploadRequest) async throws
}
```

**Implementation**: `AnimalRepository` (in `/iosApp/iosApp/Data/Repositories/AnimalRepository.swift`) - **ALREADY EXISTS**
- ADD implementation for new methods:
  - `createAnnouncement`: POST /api/v1/announcements (JSON body)
  - `uploadPhoto`: POST /api/v1/announcements/:id/photos (multipart form-data with Basic auth)
- Uses URLSession to call backend API
- Simple data access layer - NO business logic

**Note**: We're extending existing AnimalRepository instead of creating separate AnnouncementRepository. Naming will be refactored later.

---

## State Flow Diagram

```
User Input (ContactDetailsView)
    ↓
ContactDetailsViewModel (@Published properties)
    ↓
submitForm() triggered on Continue tap
    ↓
Validate inputs (phone, email) in ViewModel
    ├── Invalid → Set error properties → ValidatedTextField shows errors
    └── Valid → Continue
        ↓
    Save to ReportMissingPetFlowState.contactDetails
        ↓
    Call submissionService.submitAnnouncement(flowState:)
        ↓
    AnnouncementSubmissionService (Business Logic Layer)
        ↓
    Build CreateAnnouncementRequest from FlowState
        ↓
    Step 1: repository.createAnnouncement(request) → AnnouncementResponse
        ├── Success → Extract id + managementPassword
        │       ↓
        │   Step 2: repository.uploadPhoto(announcementId, photo, managementPassword)
        │       ├── Success → Return managementPassword to ViewModel
        │       │       ↓
        │       │   ViewModel: onReportSent(managementPassword) → Navigate to Summary
        │       └── Failure → Throw error to ViewModel
        │               ↓
        │           ViewModel: handleSubmissionError() → Show alert popup
        └── Failure → Throw error to ViewModel
                ↓
            ViewModel: handleSubmissionError() → Show alert popup
```

**Layer Responsibilities**:
- **View**: User interaction, display state from ViewModel
- **ViewModel**: Validation, UI state management, error alerts, navigation
- **Service**: Business logic (request building, 2-step orchestration)
- **Repository**: Data access (HTTP calls to backend API)

---

## Validation Summary

All validation logic resides in `ContactDetailsViewModel` (presentation layer) per constitution requirement. `OwnerContactDetails` struct is a simple data container with no validation logic.

| Field | Validation Rule | Error Message | Enforced By |
|-------|----------------|---------------|-------------|
| Phone | 7-11 digits (after sanitization: remove spaces/dashes) | "Enter at least 7 digits" | `ContactDetailsViewModel.isPhoneValid` (computed property) |
| Email | RFC 5322 basic (local@domain.tld) | "Enter a valid email address" | `ContactDetailsViewModel.isEmailValid` (computed property) |
| Reward | Max 120 characters | (None - enforced by maxLength parameter) | ValidatedTextField component |

**Validation Flow**:
1. User taps Continue → ViewModel calls `submitForm()`
2. ViewModel checks `isPhoneValid` and `isEmailValid` computed properties
3. If invalid → Set `phoneError` or `emailError` @Published properties → View displays inline errors
4. If valid → Save to `FlowState.contactDetails` (already validated data) → Proceed with submission

---

## References

- [Spec 035: iOS Owner's Details Screen](./spec.md)
- [Spec 017: iOS Missing Pet Flow](../017-ios-missing-pet-flow/spec.md) - ReportMissingPetFlowState
- [Spec 028: iOS Animal Photo Screen](../028-ios-animal-photo-screen/spec.md) - PhotoAttachmentMetadata
- [Spec 009: Create Announcement API](../009-create-announcement/spec.md) - POST /api/v1/announcements contract
- [Spec 021: Announcement Photo Upload API](../021-announcement-photo-upload/spec.md) - POST /api/v1/announcements/:id/photos contract

