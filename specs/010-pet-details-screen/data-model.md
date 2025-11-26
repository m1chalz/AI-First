# Data Model: Pet Details Screen

**Feature**: Pet Details Screen (Android UI)  
**Date**: 2025-11-25  
**Phase**: 1 - Design & Contracts

## Entities

### PetDetails (extends Animal)

The Pet Details screen displays comprehensive information about a pet. The data model extends the existing `Animal` domain model with additional optional fields required by the spec.

**Source**: Based on `Animal` model from `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/models/Animal.kt` with additions.

#### Core Fields (from Animal model)

- **id** (String, required): Unique identifier for the pet
- **name** (String, required): Pet name
- **photoUrl** (String, required): URL or placeholder identifier for pet photo
- **location** (Location, optional): Geographic location with latitude and longitude coordinates
- **species** (AnimalSpecies, required): Pet species (DOG, CAT, BIRD, RABBIT, OTHER)
- **breed** (String, optional): Specific breed name
- **gender** (AnimalGender, required): Biological sex (MALE, FEMALE, UNKNOWN)
- **status** (AnimalStatus, required): Current status (ACTIVE → displayed as "MISSING", FOUND, CLOSED)
- **lastSeenDate** (String, required): Date in "DD/MM/YYYY" format (displayed as "MMM DD, YYYY")
- **description** (String, optional): Additional descriptive text
- **email** (String?, optional): Contact email (at least one of email or phone required)
- **phone** (String?, optional): Contact phone (at least one of email or phone required)

#### Additional Fields (to be added to Animal model)

- **microchipNumber** (String?, optional): Microchip number (displayed as "000-000-000-000")
- **rewardAmount** (String?, optional): Reward amount as string (displayed as-is, no formatting)
- **approximateAge** (String?, optional): Approximate age description

#### Location Entity

- **latitude** (Double, optional): Latitude coordinate (displayed as "52.2297° N")
- **longitude** (Double, optional): Longitude coordinate (displayed as "21.0122° E")

#### Status Mapping

- **ACTIVE** → Displayed as "MISSING" with red badge (#FF0000)
- **FOUND** → Displayed as "FOUND" with blue badge (#0074FF)
- **CLOSED** → Displayed as "CLOSED" with gray badge (#93A2B4)

## Validation Rules

### Required Fields

- Photo URL (required) - fallback to gray box with "Image not available" if fails to load
- Status (required) - must be one of ACTIVE, FOUND, CLOSED
- Date of disappearance (required) - must be valid date string
- Species (required) - must be valid AnimalSpecies enum value
- Sex (required) - must be valid AnimalGender enum value
- At least one contact method (required) - either phone or email must be present

### Optional Fields

- Breed - display "—" if missing
- Approximate age - display "—" if missing
- Microchip number - display "—" if missing
- Location - disable "Show on the map" button if missing
- Additional description - display "—" if missing
- Reward amount - hide reward badge if missing

### Formatting Rules

1. **Date Format**: Convert from "DD/MM/YYYY" to "MMM DD, YYYY" (e.g., "18/11/2025" → "Nov 18, 2025")
2. **Microchip Format**: Format to "000-000-000-000" pattern (12 digits with dashes)
3. **Phone/Email**: Display in full without masking
4. **Location**: Display as "{latitude}° N/S, {longitude}° E/W" (e.g., "52.2297° N, 21.0122° E")
5. **Reward**: Display as-is (no formatting, e.g., "500 PLN")

## State Transitions

### PetDetailsUiState

```kotlin
data class PetDetailsUiState(
    val pet: Animal? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    companion object {
        val Initial = PetDetailsUiState()
    }
}
```

**State Flow**:
1. **Initial** → `isLoading = false`, `pet = null`, `error = null`
2. **Loading** → `isLoading = true`, `pet = null`, `error = null` (triggered by LoadPet intent)
3. **Success** → `isLoading = false`, `pet = Animal(...)`, `error = null`
4. **Error** → `isLoading = false`, `pet = null`, `error = "Error message"`

## Edge Cases

### Missing Data Handling

- **Photo fails to load**: Display gray box with "Image not available" text
- **Optional fields missing**: Display "—" for breed, age, microchip, location, description
- **No reward**: Hide reward badge completely
- **Missing contact info**: Display only available method (phone or email)
- **Invalid date format**: Display original string or "—"
- **Invalid microchip format**: Display as-is or "—"
- **Location unavailable**: Disable "Show on the map" button

### Text Truncation

- **Short fields** (species, breed, sex, age, microchip, location coordinates): Truncate with ellipsis after 1-2 lines
- **Long fields** (Additional Description): Allow full multi-line display with screen scrolling

### Loading States

- **Initial load**: Show full-screen spinner/progress indicator
- **Image loading**: Show placeholder until image loads or fails

## Data Flow

1. **Navigation**: User taps pet list item → `AnimalListEffect.NavigateToDetails(animalId)` → NavRoute.AnimalDetail(animalId)
2. **Screen Load**: PetDetailsScreen receives `animalId` from route
3. **ViewModel Init**: PetDetailsViewModel dispatches `PetDetailsIntent.LoadPet(animalId)`
4. **Use Case**: GetAnimalByIdUseCase calls `repository.getAnimalById(animalId)`
5. **Repository**: AnimalRepositoryImpl fetches animal data (mock or API)
6. **State Update**: Reducer updates UiState with pet data or error
7. **UI Render**: Compose UI renders based on UiState

## Related Models

- **Animal** (shared domain model): Base entity with core pet information
- **Location**: Geographic location with latitude and longitude coordinates
- **AnimalSpecies**: Enum (DOG, CAT, BIRD, RABBIT, OTHER)
- **AnimalGender**: Enum (MALE, FEMALE, UNKNOWN)
- **AnimalStatus**: Enum (ACTIVE, FOUND, CLOSED)



