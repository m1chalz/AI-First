# Data Model: Animal List Screen

**Feature**: Animal List Screen  
**Date**: 2025-11-19  
**Status**: Phase 1 - Data Model Design

## Overview

This document defines the data model for the Animal List Screen feature. The model is designed for the shared Kotlin Multiplatform module and will be consumed by all three platforms (Android, iOS, Web).

## Domain Models

### Animal

Primary entity representing an animal in the system.

**Location**: `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/models/Animal.kt`

```kotlin
package com.intive.aifirst.petspot.domain.models

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Represents an animal in the PetSpot system.
 * Contains all information needed for list display and detail views.
 * Shared across all platforms via Kotlin Multiplatform.
 *
 * @property id Unique identifier (UUID or database ID)
 * @property name Name of the animal (e.g., "Buddy", "Mittens")
 * @property photoUrl URL or placeholder identifier for animal photo
 * @property location Geographic location with radius for search area
 * @property species Animal species (Dog, Cat, Bird, etc.)
 * @property breed Specific breed name (e.g., "Maine Coon", "German Shepherd")
 * @property gender Biological sex (Male, Female, Unknown)
 * @property status Current status (Active, Found, Closed)
 * @property lastSeenDate Date when animal was last seen (for Active status) or found (for Found status)
 * @property description Detailed text description (visible on web, truncated on mobile)
 * @property email Contact email of the person who reported/owns the animal (optional)
 * @property phone Contact phone number of the person who reported/owns the animal (optional)
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
data class Animal(
    val id: String,
    val name: String,
    val photoUrl: String,  // URL or placeholder identifier
    val location: Location,
    val species: AnimalSpecies,
    val breed: String,
    val gender: AnimalGender,
    val status: AnimalStatus,
    val lastSeenDate: String,  // Format: DD/MM/YYYY (as specified in spec)
    val description: String,
    val email: String?,  // Optional contact email
    val phone: String?   // Optional contact phone
)
```

**Validation Rules**:
- `id`: Non-empty string (UUID format recommended for real API)
- `name`: Non-empty string (min 1 char)
- `photoUrl`: Non-empty string (URL or asset path)
- `breed`: Non-empty string (min 1 char)
- `lastSeenDate`: String in DD/MM/YYYY format (e.g., "18/11/2025")
- `description`: Can be empty (optional field)
- `email`: Optional (nullable), must be valid email format if provided (e.g., "owner@example.com")
- `phone`: Optional (nullable), must be valid phone format if provided (e.g., "+48 123 456 789")

**Mock Data Example**:
```kotlin
Animal(
    id = "1",
    name = "Fluffy",
    photoUrl = "placeholder_cat",
    location = Location(city = "Pruszkow", radiusKm = 5),
    species = AnimalSpecies.CAT,
    breed = "Maine Coon",
    gender = AnimalGender.MALE,
    status = AnimalStatus.ACTIVE,
    lastSeenDate = "18/11/2025",
    description = "Friendly orange tabby cat, last seen near the park.",
    email = "owner@example.com",
    phone = "+48 123 456 789"
)
```

---

### Location

Geographic location with search radius.

**Location**: `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/models/Location.kt`

```kotlin
package com.intive.aifirst.petspot.domain.models

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Represents a geographic location with search radius.
 * Domain model for animal location data.
 *
 * @property city City or area name
 * @property radiusKm Search radius in kilometers
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
data class Location(
    val city: String,
    val radiusKm: Int
)
```

**Example**:
```kotlin
Location(city = "Pruszkow", radiusKm = 5)
```

**Note**: Display formatting (e.g., "Pruszkow, +5km") should be handled in the UI/presentation layer, not in the domain model.

---

### AnimalSpecies

Enum representing animal species.

**Location**: `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/models/AnimalSpecies.kt`

```kotlin
package com.intive.aifirst.petspot.domain.models

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Animal species types supported by the system.
 * Determines icon/image display and filtering options.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
enum class AnimalSpecies(val displayName: String) {
    DOG("Dog"),
    CAT("Cat"),
    BIRD("Bird"),
    RABBIT("Rabbit"),
    OTHER("Other")
}
```

**Usage**:
- Displayed in animal card (e.g., "Cat | Maine Coon")
- Used for filtering (future feature)
- Determines icon/placeholder image selection

---

### AnimalGender

Enum representing animal gender.

**Location**: `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/models/AnimalGender.kt`

```kotlin
package com.intive.aifirst.petspot.domain.models

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Animal gender/sex.
 * Displayed as icon on web version (per Figma spec).
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
enum class AnimalGender(val displayName: String) {
    MALE("Male"),
    FEMALE("Female"),
    UNKNOWN("Unknown")
}
```

**Usage**:
- Web: Displayed as gender icon (male/female symbol)
- Mobile: May be shown in detail view (not in list card per Figma)

---

### AnimalStatus

Enum representing animal status (Active, Found, or Closed).

**Location**: `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/models/AnimalStatus.kt`

```kotlin
package com.intive.aifirst.petspot.domain/models

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Status of an animal in the system.
 * Determines badge color and text displayed in list.
 *
 * @property displayName Human-readable status label
 * @property badgeColor Hex color for status badge
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
enum class AnimalStatus(
    val displayName: String,
    val badgeColor: String  // Hex color
) {
    ACTIVE("Active", "#FF0000"),    // Red badge - actively missing/searching
    FOUND("Found", "#0074FF"),      // Blue badge - animal has been found
    CLOSED("Closed", "#93A2B4")     // Gray badge - case closed/resolved
}
```

**Usage**:
- Status badge displayed on each card
- Red background for Active, Blue for Found, Gray for Closed
- Badge text: "Active", "Found", or "Closed"

---

## Entity Relationships

```
Animal
├── Location (composition)
├── AnimalSpecies (enum)
├── AnimalGender (enum)
└── AnimalStatus (enum)
```

**Notes**:
- No foreign key relationships in this UI-only phase
- Animal is self-contained entity (no joins required)
- Future: May add `ownerId`, `reporterId` for user associations

---

## State Management Models

### Android MVI State

**Location**: `/composeApp/src/androidMain/.../features/animallist/presentation/mvi/AnimalListUiState.kt`

```kotlin
/**
 * Immutable UI state for Animal List screen.
 * Single source of truth for Compose UI rendering.
 */
data class AnimalListUiState(
    val animals: List<Animal> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    /** Computed property: true when data loaded but list is empty */
    val isEmpty: Boolean
        get() = animals.isEmpty() && !isLoading && error == null

    companion object {
        val Initial = AnimalListUiState()
    }
}
```

---

### iOS ViewModel State

**Location**: `/iosApp/iosApp/ViewModels/AnimalListViewModel.swift`

```swift
/// ViewModel state for Animal List screen.
/// Observable properties drive SwiftUI view updates.
@MainActor
class AnimalListViewModel: ObservableObject {
    /// List of animals displayed to user
    @Published var animals: [Animal] = []
    
    /// Loading state indicator
    @Published var isLoading: Bool = false
    
    /// Error message (nil if no error)
    @Published var errorMessage: String?
    
    /// Computed: true when data loaded but list is empty
    var isEmpty: Bool {
        animals.isEmpty && !isLoading && errorMessage == nil
    }
}
```

---

### Web Hook State

**Location**: `/webApp/src/hooks/useAnimalList.ts`

```typescript
/**
 * Hook state for Animal List component.
 * Encapsulates loading, data, and error states.
 */
interface UseAnimalListResult {
    animals: Animal[];
    isLoading: boolean;
    error: string | null;
    isEmpty: boolean;
    loadAnimals: () => Promise<void>;
    selectAnimal: (id: string) => void;
    reportMissing: () => void;
    reportFound: () => void;
}
```

---

## Mock Data Generation

### Mock Repository Data

8-12 animals with varied attributes for testing scrolling and states.

**Location**: Platform-specific mock repositories

**Data Set** (example):
```kotlin
listOf(
    Animal(
        id = "1",
        name = "Fluffy",
        photoUrl = "placeholder_cat",
        location = Location("Pruszkow", 5),
        species = AnimalSpecies.CAT,
        breed = "Maine Coon",
        gender = AnimalGender.MALE,
        status = AnimalStatus.ACTIVE,
        lastSeenDate = "18/11/2025",
        description = "Friendly orange tabby, last seen near the park.",
        email = "john@example.com",
        phone = "+48 123 456 789"
    ),
    Animal(
        id = "2",
        name = "Rex",
        photoUrl = "placeholder_dog",
        location = Location("Warsaw", 10),
        species = AnimalSpecies.DOG,
        breed = "German Shepherd",
        gender = AnimalGender.FEMALE,
        status = AnimalStatus.ACTIVE,
        lastSeenDate = "17/11/2025",
        description = "Large black and tan dog, wearing red collar.",
        email = "anna@example.com",
        phone = null  // Only email provided
    ),
    Animal(
        id = "3",
        name = "Bella",
        photoUrl = "placeholder_cat",
        location = Location("Krakow", 3),
        species = AnimalSpecies.CAT,
        breed = "Siamese",
        gender = AnimalGender.FEMALE,
        status = AnimalStatus.FOUND,
        lastSeenDate = "19/11/2025",
        description = "Blue-eyed white cat found near train station.",
        email = null,  // Only phone provided
        phone = "+48 987 654 321"
    ),
    // ... 5-9 more animals with varied attributes
)
```

**Requirements**:
- Mix of species (Dog, Cat, Bird)
- Mix of statuses (majority Active, some Found, few Closed)
- Varied locations (different cities)
- Varied names, breeds, and genders
- Realistic dates (recent dates in DD/MM/YYYY format)
- Mix of contact info: some with email only, some with phone only, some with both, some with neither

---

## Data Flow

```
AnimalRepositoryImpl (mocked data)
    ↓ (returns Result<List<Animal>>)
GetAnimalsUseCase
    ↓ (invoked by ViewModel)
ViewModel State Update
    ↓ (StateFlow/Published/useState)
UI Rendering
    ↓ (LazyColumn/LazyVStack/map)
Animal Cards Displayed
```

**Error Flow**:
```
Repository throws exception
    ↓
Use case catches and returns Result.failure(exception)
    ↓
ViewModel updates error state
    ↓
UI displays error message
```

**Empty Flow**:
```
Repository returns empty list
    ↓
Use case returns Result.success(emptyList())
    ↓
ViewModel sets isEmpty = true
    ↓
UI displays empty state message
```

---

## Platform-Specific Considerations

### Android

- Use `@Parcelize` if Animal needs to be passed via Compose Navigation (future)
- Photo placeholder: Use drawable resource ID or asset path
- Date formatting: Consider using `java.time.LocalDate` for parsing (API 26+)

### iOS

- Animal model generated from shared KMP module (via `shared.framework`)
- Photo placeholder: Use `UIImage(named:)` with asset catalog
- Date formatting: Use `DateFormatter` with DD/MM/YYYY pattern

### Web

- Animal model imported from shared Kotlin/JS module
- Photo placeholder: Use asset URL or data URI
- Date formatting: Use `Intl.DateTimeFormat` or date-fns library

---

## Validation & Business Rules

1. **ID Uniqueness**: Each animal must have unique ID (enforced by mock repository)
2. **Date Format**: All dates in DD/MM/YYYY format (future: validate format)
3. **Required Fields**: id, name, photoUrl, location, species, breed, gender, status, lastSeenDate are mandatory
4. **Optional Fields**: description can be empty string, email and phone are nullable
5. **Enum Validation**: species, gender, status must be valid enum values
6. **Location Radius**: radiusKm must be positive integer (> 0)
7. **Name Constraints**: name should be non-empty and reasonable length (1-50 chars recommended)
8. **Breed Constraints**: breed should be non-empty and reasonable length (1-50 chars recommended)
9. **Email Validation**: If provided, must match valid email format (regex validation in future)
10. **Phone Validation**: If provided, should be valid phone number (international format recommended, e.g., +XX XXX XXX XXX)
11. **Privacy**: Contact information (email, phone) should be displayed only on detail screens, not in list view

---

## Migration to Real API

When backend is ready:

1. **Add API Response Model**: Create DTO matching backend JSON schema
2. **Add Mapping Layer**: Map API DTO to domain Animal model
3. **Update Date Handling**: Parse ISO-8601 dates from API, format to DD/MM/YYYY for display
4. **Add Photo URLs**: Replace placeholders with real image URLs from CDN
5. **Add Pagination**: Support infinite scrolling with page/limit parameters
6. **Add Filtering**: Support species, status, location filters
7. **Add Status Transitions**: Implement business rules for status changes (Active → Found/Closed)

**No changes to domain model required** - API responses will be mapped to existing Animal structure.

---

## Summary

- ✅ Animal domain model defined with all required fields from spec
- ✅ Supporting models: Location, AnimalSpecies, AnimalGender, AnimalStatus
- ✅ Models exported for JavaScript consumption (@JsExport)
- ✅ State management models defined for each platform (MVI, MVVM, hooks)
- ✅ Mock data strategy documented (8-12 varied animals)
- ✅ Data flow and error/empty handling described
- ✅ Platform-specific considerations documented
- ✅ Validation rules and business logic defined
- ✅ Migration path to real API outlined

**Status**: Data model complete. Ready for contracts definition.

