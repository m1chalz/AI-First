# Data Model: Web Missing Pet Announcement Submission

**Feature**: Web announcement submission integration  
**Date**: 2025-12-03  
**Platform**: Web (TypeScript/React)

## Overview

This document defines the TypeScript interfaces and types used for announcement submission. All types are defined in `/webApp/src/models/` and follow existing naming conventions (kebab-case filenames).

**Service Consolidation**: The existing `animal-repository.ts` will be renamed to `announcement-service.ts` to consolidate all announcement-related API operations (both GET and POST) in a single service class.

---

## Core Entities

### AnnouncementSubmissionDto

**Purpose**: Request payload for creating an announcement via POST /api/v1/announcements

**File**: `/webApp/src/models/announcement-submission.ts`

```typescript
/**
 * Data transfer object for creating a missing pet announcement.
 * Maps client-side flow state to backend API format.
 */
export interface AnnouncementSubmissionDto {
  /** Pet's name (optional) */
  petName?: string;
  
  /** Species: 'dog' | 'cat' | 'other' */
  species: string;
  
  /** Breed (optional, free text) */
  breed?: string;
  
  /** Sex: 'male' | 'female' */
  sex: string;
  
  /** Age in years (optional) */
  age?: number;
  
  /** Free-text description (optional) */
  description?: string;
  
  /** Microchip number (optional, 15 digits) */
  microchipNumber?: string;
  
  /** Latitude coordinate (required) */
  locationLatitude: number;
  
  /** Longitude coordinate (required) */
  locationLongitude: number;
  
  /** Owner's email (optional, but at least one contact method required) */
  email?: string;
  
  /** Owner's phone (optional, but at least one contact method required) */
  phone?: string;
  
  /** Date last seen (ISO 8601 date string: YYYY-MM-DD) */
  lastSeenDate: string;
  
  /** Announcement status (always 'MISSING' for new announcements) */
  status: 'MISSING';
  
  /** Reward amount (optional, free text) */
  reward?: string;
}
```

**Validation Rules**:
- At least one of `email` or `phone` must be provided (FR-012)
- `locationLatitude` and `locationLongitude` must be provided (FR-013)
- `species` must be one of: 'dog', 'cat', 'other'
- `sex` must be one of: 'male', 'female'
- `status` is always 'MISSING' for new announcements (FR-003)

**Mapping from ReportMissingPetFlowState**:
```typescript
function mapFlowStateToDto(flowState: ReportMissingPetFlowState): AnnouncementSubmissionDto {
  return {
    petName: undefined, // Not collected in current flow
    species: flowState.species!,
    breed: flowState.breed || undefined,
    sex: flowState.sex!,
    age: flowState.age ?? undefined,
    description: flowState.description || undefined,
    microchipNumber: flowState.microchipNumber || undefined,
    locationLatitude: flowState.latitude!,
    locationLongitude: flowState.longitude!,
    email: flowState.email || undefined,
    phone: flowState.phone || undefined,
    lastSeenDate: flowState.lastSeenDate,
    status: 'MISSING',
    reward: flowState.reward || undefined
  };
}
```

---

### AnnouncementResponse

**Purpose**: Response payload from POST /api/v1/announcements

**File**: `/webApp/src/models/announcement-submission.ts`

```typescript
/**
 * Response from backend after creating an announcement.
 * Includes the management password needed for photo upload.
 */
export interface AnnouncementResponse {
  /** Unique announcement ID (UUID) */
  id: string;
  
  /** Management password for updating/deleting announcement */
  managementPassword: string;
  
  /** All fields from AnnouncementSubmissionDto */
  petName?: string | null;
  species: string;
  breed?: string | null;
  sex: string;
  age?: number | null;
  description?: string | null;
  microchipNumber?: string | null;
  locationLatitude: number;
  locationLongitude: number;
  email?: string | null;
  phone?: string | null;
  photoUrl: string | null;
  lastSeenDate: string;
  status: 'MISSING' | 'FOUND';
  reward?: string | null;
  
  /** Timestamp of creation (ISO 8601) */
  createdAt: string;
  
  /** Timestamp of last update (ISO 8601) */
  updatedAt: string;
}
```

**Usage**:
- Extract `id` and `managementPassword` for photo upload
- Store `managementPassword` for display on summary screen
- Response includes all announcement data for verification

---

## Error Types

### ApiError (Discriminated Union)

**Purpose**: Type-safe error handling for API responses

**File**: `/webApp/src/models/api-error.ts`

```typescript
/**
 * Base error type for API failures.
 * Use discriminated union for type-safe error handling.
 */
export type ApiError = 
  | NetworkError 
  | ValidationError 
  | DuplicateMicrochipError 
  | ServerError;

/**
 * Network connectivity error (fetch failed, timeout, etc.)
 */
export interface NetworkError {
  type: 'network';
  message: string;
}

/**
 * Validation error from backend (400 Bad Request)
 */
export interface ValidationError {
  type: 'validation';
  message: string;
  field?: string; // Optional field name for targeted error display
}

/**
 * Duplicate microchip number error (409 Conflict)
 */
export interface DuplicateMicrochipError {
  type: 'duplicate_microchip';
  message: string;
}

/**
 * Server error (500 Internal Server Error)
 */
export interface ServerError {
  type: 'server';
  message: string;
  statusCode: number;
}
```

**Error Handling Pattern**:
```typescript
try {
  await submitAnnouncement();
} catch (error) {
  if (error instanceof Error && 'type' in error) {
    const apiError = error as ApiError;
    
    switch (apiError.type) {
      case 'network':
        showToast('Network error. Please check your connection.');
        break;
      case 'duplicate_microchip':
        showToast('This microchip already exists. If this is your announcement, use your management password to update it.');
        break;
      case 'validation':
        showToast(`Validation error: ${apiError.message}`);
        break;
      case 'server':
        showToast(`Server error (${apiError.statusCode}). Please try again later.`);
        break;
    }
  }
}
```

**HTTP Status Code Mapping**:
- 400 → `ValidationError`
- 409 → `DuplicateMicrochipError`
- 500, 502, 503 → `ServerError`
- Network failure (no response) → `NetworkError`

---

## Utility Types

### PhotoUploadPayload

**Purpose**: Internal type for photo upload request construction

**File**: `/webApp/src/services/announcement-service.ts` (renamed from `animal-repository.ts`, not exported)

```typescript
interface PhotoUploadPayload {
  announcementId: string;
  photo: File;
  managementPassword: string;
}
```

**Usage**: Used internally by `AnnouncementService.uploadPhoto()` method

---

## Type Guards

**File**: `/webApp/src/models/api-error.ts`

```typescript
/**
 * Type guard to check if error is an ApiError
 */
export function isApiError(error: unknown): error is ApiError {
  return (
    typeof error === 'object' &&
    error !== null &&
    'type' in error &&
    typeof (error as ApiError).type === 'string'
  );
}

/**
 * Type guard for NetworkError
 */
export function isNetworkError(error: unknown): error is NetworkError {
  return isApiError(error) && error.type === 'network';
}

/**
 * Type guard for ValidationError
 */
export function isValidationError(error: unknown): error is ValidationError {
  return isApiError(error) && error.type === 'validation';
}

/**
 * Type guard for DuplicateMicrochipError
 */
export function isDuplicateMicrochipError(error: unknown): error is DuplicateMicrochipError {
  return isApiError(error) && error.type === 'duplicate_microchip';
}

/**
 * Type guard for ServerError
 */
export function isServerError(error: unknown): error is ServerError {
  return isApiError(error) && error.type === 'server';
}
```

---

## Existing Types (Reused)

### PhotoAttachment

**Source**: `/webApp/src/models/ReportMissingPetFlow.ts` (existing)

```typescript
export interface PhotoAttachment {
  file: File;
  filename: string;
  size: number;
  mimeType: string;
  previewUrl: string | null;
}
```

**Usage**: Extract `file` property for photo upload

---

### ReportMissingPetFlowState

**Source**: `/webApp/src/models/ReportMissingPetFlow.ts` (existing)

```typescript
export interface ReportMissingPetFlowState {
  currentStep: FlowStep;
  microchipNumber: string;
  photo: PhotoAttachment | null;
  lastSeenDate: string;
  species: AnimalSpecies | null;
  breed: string;
  sex: AnimalSex | null;
  age: number | null;
  description: string;
  latitude: number | null;
  longitude: number | null;
  phone: string;
  email: string;
  reward: string;
}
```

**Usage**: Source data for `AnnouncementSubmissionDto` mapping

---

## Type Relationships

```
ReportMissingPetFlowState
  └─> mapFlowStateToDto() ─> AnnouncementSubmissionDto
                               └─> POST /api/v1/announcements
                                     └─> AnnouncementResponse
                                           ├─> id, managementPassword
                                           └─> POST /api/v1/announcements/:id/photos
                                                 └─> Success (201) or ApiError
```

---

## Validation Helpers

**File**: `/webApp/src/services/announcement-service.ts` (renamed from `animal-repository.ts`)

```typescript
/**
 * Validates announcement DTO before submission
 */
function validateAnnouncementDto(dto: AnnouncementSubmissionDto): void {
  // FR-012: At least one contact method required
  if (!dto.email && !dto.phone) {
    throw {
      type: 'validation',
      message: 'At least one contact method (email or phone) is required',
      field: 'contact'
    } as ValidationError;
  }
  
  // FR-013: Location coordinates required
  if (dto.locationLatitude === undefined || dto.locationLongitude === undefined) {
    throw {
      type: 'validation',
      message: 'Location coordinates are required',
      field: 'location'
    } as ValidationError;
  }
}
```

---

## File Structure Summary

```
/webApp/src/
├── models/
│   ├── announcement-submission.ts    # NEW - AnnouncementSubmissionDto, AnnouncementResponse
│   ├── api-error.ts                  # NEW - ApiError types and type guards
│   └── ReportMissingPetFlow.ts       # EXISTING - PhotoAttachment, ReportMissingPetFlowState
└── services/
    └── announcement-service.ts       # RENAMED from animal-repository.ts - Extended with POST methods
```

---

## Testing Considerations

**Type Safety Tests**:
- Compile-time checking via TypeScript strict mode
- Test discriminated union handling in error scenarios
- Verify type guards correctly identify error types

**Mapping Tests**:
- Test `mapFlowStateToDto()` with various flow states
- Verify optional field handling (undefined vs empty string)
- Test validation logic (at least one contact method)

**Sample Test Data**:
```typescript
export const MOCK_FLOW_STATE: ReportMissingPetFlowState = {
  currentStep: FlowStep.Contact,
  microchipNumber: '123456789012345',
  photo: {
    file: new File(['test'], 'test.jpg', { type: 'image/jpeg' }),
    filename: 'test.jpg',
    size: 1024,
    mimeType: 'image/jpeg',
    previewUrl: 'blob:...'
  },
  lastSeenDate: '2025-12-03',
  species: 'dog',
  breed: 'Labrador',
  sex: 'male',
  age: 5,
  description: 'Friendly golden lab',
  latitude: 52.2297,
  longitude: 21.0122,
  phone: '+48123456789',
  email: 'owner@example.com',
  reward: '500 PLN'
};
```

---

**Data Model Complete**: All TypeScript interfaces defined, ready for implementation.

