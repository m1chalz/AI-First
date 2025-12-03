# Contracts: Android Animal Description Screen

**Feature**: 042-android-animal-description-screen  
**Date**: 2025-12-03

## Overview

This feature is **UI-only** with no backend integration. Data is stored in in-memory flow state until final submission (handled by a future spec).

## No API Contracts Needed

### Rationale

1. **No Backend Calls**: This screen does not make any HTTP requests
2. **In-Memory State**: All data persists in `ReportMissingFlowState` until flow completion
3. **Local GPS Only**: Location is fetched from device, not a backend service
4. **Static Taxonomy**: Species list is bundled with the app (no API fetch)

### Future Integration

When backend integration is implemented (future spec), the following endpoints will likely be needed:

```yaml
# Future: POST /api/v1/announcements (from spec 009)
# Will include animal description fields in request body
```

## Internal Contracts

### Flow State Contract

The `ReportMissingFlowState.FlowData` structure serves as the internal contract between steps:

```kotlin
// Step 3 (Animal Description) writes:
- disappearanceDate: LocalDate
- animalSpecies: String
- animalRace: String
- animalGender: AnimalGender
- animalAge: Int?
- latitude: Double?
- longitude: Double?
- additionalDescription: String

// Step 4 (Contact Details) reads these and adds:
- contactEmail: String
- contactPhone: String

// Summary screen reads all fields for display
// Final submission sends all data to backend
```

### Location Repository Contract

Reused from spec 026:

```kotlin
interface LocationRepository {
    suspend fun getCurrentLocation(): Result<LocationCoordinates>
    suspend fun checkPermissionStatus(): PermissionStatus
}
```

