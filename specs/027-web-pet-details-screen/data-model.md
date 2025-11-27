# Data Model: Pet Details Screen (Web UI)

**Feature**: 027-web-pet-details-screen  
**Date**: 2025-11-27  
**Status**: ✅ Complete

## Overview

This document defines the data models used in the pet details modal feature. Models are TypeScript interfaces/types used in the web application (`/webApp`).

## Entities

### PetDetails

Represents comprehensive information about a pet, fetched from the backend API endpoint `GET /api/v1/announcements/:id`.

**Type Definition**:
```typescript
// webApp/src/types/pet-details.ts

export enum PetStatus {
  MISSING = 'MISSING',
  FOUND = 'FOUND',
  CLOSED = 'CLOSED'
}

export enum PetSex {
  MALE = 'MALE',
  FEMALE = 'FEMALE',
  UNKNOWN = 'UNKNOWN'
}

export interface PetDetails {
  // Required fields
  id: string;                    // UUID string
  photoUrl: string;              // URL to pet photo
  status: PetStatus;             // MISSING, FOUND, or CLOSED
  lastSeenDate: string;          // ISO 8601 date (YYYY-MM-DD)
  species: string;               // Pet species (e.g., "DOG", "CAT")
  sex: PetSex;                   // MALE, FEMALE, or UNKNOWN
  
  // Contact information (at least one required)
  phone?: string | null;          // Phone number (may be masked by backend)
  email?: string | null;         // Email address
  
  // Optional fields
  petName?: string | null;        // Pet's name
  microchipNumber?: string | null; // Raw microchip number (string, formatted in component)
  breed?: string | null;          // Pet breed
  age?: number | null;            // Approximate age (number)
  locationLatitude?: number | null; // Latitude coordinate (number, formatted in component)
  locationLongitude?: number | null; // Longitude coordinate (number, formatted in component)
  description?: string | null;    // Additional description text
  reward?: string | null;        // Reward amount (string, displayed as-is)
  createdAt?: string | null;     // ISO 8601 timestamp
  updatedAt?: string | null;     // ISO 8601 timestamp
}
```

**API Response Format** (from backend):
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "petName": "Max",
  "photoUrl": "https://example.com/photo.jpg",
  "status": "MISSING",
  "lastSeenDate": "2025-11-18",
  "species": "DOG",
  "breed": "Golden Retriever",
  "sex": "MALE",
  "age": 5,
  "description": "Friendly dog with brown fur",
  "microchipNumber": "123456789012345",
  "locationLatitude": 52.2297,
  "locationLongitude": 21.0122,
  "email": "john@example.com",
  "phone": "+48 123 456 789",
  "reward": "500 PLN",
  "createdAt": "2025-11-24T12:34:56.789Z",
  "updatedAt": "2025-11-24T12:34:56.789Z"
}
```

**Validation Rules**:
- `id`: Required, must be valid UUID string
- `photoUrl`: Required, must be valid URL
- `status`: Required, must be one of: MISSING, FOUND, CLOSED
- `lastSeenDate`: Required, ISO 8601 date format (YYYY-MM-DD)
- `species`: Required, string
- `sex`: Required, must be one of: MALE, FEMALE, UNKNOWN
- `phone` or `email`: At least one must be present (backend validation)
- `locationLatitude`: Optional, number between -90 and 90
- `locationLongitude`: Optional, number between -180 and 180
- `microchipNumber`: Optional, string (raw format, formatted in component)

**Formatting Rules** (applied in components):
- `lastSeenDate`: Format from ISO 8601 to "MMM DD, YYYY" (e.g., "Nov 18, 2025")
- `locationLatitude`/`locationLongitude`: Format to "XX.XXXX° N/S, XX.XXXX° E/W" (e.g., "52.2297° N, 21.0122° E")
- `microchipNumber`: Format to "000-000-000-000" by adding dashes
- `phone`: Display exactly as received from API (no formatting)
- `reward`: Display as-is (string field, no formatting)

---

### Animal (List Item)

Represents a simplified pet entry displayed in the animal list. Used for list cards and filtering.

**Type Definition** (existing, may need updates):
```typescript
// webApp/src/types/animal.ts (existing)

export interface Animal {
  id: string;
  name: string;
  photoUrl: string;
  location: {
    latitude?: number;      // NEW: Add latitude/longitude for coordinates display
    longitude?: number;
  };
  species: AnimalSpecies;
  breed: string;
  gender: AnimalGender;
  status: AnimalStatus;
  lastSeenDate: string;     // ISO 8601 date (YYYY-MM-DD)
  description: string;
  email?: string;
  phone?: string;
}
```

**Updates Required**:
- Add `location.latitude` and `location.longitude` fields to support coordinate display
- Update `lastSeenDate` to ISO 8601 format (currently may be in different format)
- Ensure `status` values match PetStatus enum (MISSING, FOUND, CLOSED)

---

### ModalState

Represents the state of the pet details modal.

**Type Definition**:
```typescript
// webApp/src/hooks/use-modal.ts

export interface ModalState {
  isOpen: boolean;
  selectedPetId: string | null;
}
```

**State Transitions**:
- `isOpen: false, selectedPetId: null` → Initial state (modal closed)
- `isOpen: true, selectedPetId: "uuid"` → Modal open with pet ID selected
- `isOpen: false, selectedPetId: null` → Modal closed (reset to initial state)

---

## Relationships

- **PetDetails** ← fetched by → `GET /api/v1/announcements/:id` (backend API)
- **Animal** → used in → **AnimalList** component (list cards)
- **PetDetails** → displayed in → **PetDetailsModal** component
- **ModalState** → managed by → **useModal** hook

---

## Data Flow

1. **List Page Load**:
   - `AnimalList` component calls `useAnimalList()` hook
   - Hook fetches animals via `AnimalRepository.getAnimals()`
   - Animals displayed in list cards

2. **Modal Open**:
   - User clicks "Details" button on animal card
   - `AnimalCard` calls `onClick` callback with `animal.id`
   - `AnimalList` updates modal state: `setSelectedPetId(id)`, `setIsModalOpen(true)`
   - `PetDetailsModal` receives `selectedPetId` prop

3. **Pet Details Fetch**:
   - `PetDetailsModal` calls `usePetDetails(selectedPetId)` hook
   - Hook fetches pet details via `AnimalRepository.getPetById(id)`
   - Pet details displayed in modal content

4. **Modal Close**:
   - User clicks close button, backdrop, or presses ESC
   - `PetDetailsModal` calls `onClose` callback
   - `AnimalList` resets modal state: `setIsModalOpen(false)`, `setSelectedPetId(null)`
   - Focus returns to "Details" button that triggered modal

---

## Error Handling

**API Error Response Format**:
```json
{
  "error": {
    "code": "NOT_FOUND" | "INTERNAL_SERVER_ERROR",
    "message": "Resource not found" | "Internal server error"
  }
}
```

**Error States**:
- **404 NOT_FOUND**: Pet with specified ID does not exist
- **500 INTERNAL_SERVER_ERROR**: Server error
- **Network Error**: Request failed (timeout, connection error)
- **Timeout**: Request exceeds 10 seconds

**Error Handling Strategy**:
- All errors display generic message: "Failed to load pet details"
- Error state replaces modal content (hides spinner and content)
- "Retry" button allows unlimited retry attempts
- User can always close modal via close button or ESC key

---

## Formatting Utilities

### Date Formatter

```typescript
// webApp/src/utils/date-formatter.ts

export function formatDate(isoDate: string): string {
  const date = new Date(isoDate);
  return date.toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric'
  });
}

// Example: "2025-11-18" → "Nov 18, 2025"
```

### Coordinate Formatter

```typescript
// webApp/src/utils/coordinate-formatter.ts

export function formatCoordinates(lat: number, lng: number): string {
  const latDir = lat >= 0 ? 'N' : 'S';
  const lngDir = lng >= 0 ? 'E' : 'W';
  return `${Math.abs(lat).toFixed(4)}° ${latDir}, ${Math.abs(lng).toFixed(4)}° ${lngDir}`;
}

// Example: 52.2297, 21.0122 → "52.2297° N, 21.0122° E"
```

### Microchip Formatter

```typescript
// webApp/src/utils/microchip-formatter.ts

export function formatMicrochip(raw: string): string {
  const cleaned = raw.replace(/\D/g, '');
  if (cleaned.length !== 15) {
    return raw; // Return original if invalid format
  }
  return cleaned.replace(/(\d{3})(\d{3})(\d{3})(\d{3})(\d{3})/, '$1-$2-$3-$4-$5');
}

// Example: "123456789012345" → "123-456-789-012-345"
// Note: Adjust regex pattern based on actual microchip format requirements
```

---

## Summary

- **PetDetails**: Main entity for pet details modal, fetched from backend API
- **Animal**: Simplified entity for list display (may need updates for coordinates)
- **ModalState**: Simple state management for modal open/close
- **Formatting**: Utilities for date, coordinates, and microchip formatting
- **Error Handling**: Generic error message with retry mechanism

All data models follow TypeScript best practices with proper typing and nullability handling.

