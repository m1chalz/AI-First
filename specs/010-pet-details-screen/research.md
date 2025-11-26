# Research: Pet Details Screen (Android UI)

**Feature**: Pet Details Screen  
**Date**: 2025-11-25  
**Phase**: 0 - Outline & Research

## Research Questions & Findings

### 1. Image Loading Library

**Question**: Should we use Coil for image loading, or is there an existing solution?

**Decision**: Add Coil library for image loading.

**Rationale**:
- Coil is the recommended image loading library for Jetpack Compose
- Provides automatic caching, memory management, and placeholder support
- Well-maintained, actively developed, and widely used in Android Compose projects
- Supports async image loading with proper lifecycle handling
- No existing image loading library found in the project

**Alternatives Considered**:
- Glide: More complex API, requires more setup for Compose
- Picasso: Older library, less optimized for Compose
- Manual implementation: Too much boilerplate and error-prone

**Implementation**:
- Add `io.coil-kt:coil-compose` dependency to `composeApp/build.gradle.kts`
- Use `AsyncImage` composable from Coil for pet photo display
- Configure placeholder and error handling for failed image loads

---

### 2. Minimum SDK Version

**Question**: What is the minimum Android SDK version for this project?

**Decision**: Android API 24 (Android 7.0 Nougat).

**Rationale**:
- Confirmed from `gradle/libs.versions.toml`: `android-minSdk = "24"`
- API 24 provides sufficient features for this UI screen
- Supports modern Compose features and Material 3 components
- Good device coverage (covers ~95% of active Android devices)

**Alternatives Considered**:
- API 21 (Android 5.0): Would increase device coverage but lacks some modern features
- API 26+ (Android 8.0+): Would reduce device coverage unnecessarily

---

### 3. Status Value Mapping

**Question**: Spec mentions MISSING/FOUND/CLOSED, but codebase has ACTIVE/FOUND/CLOSED. How should we handle this?

**Decision**: Map `AnimalStatus.ACTIVE` to "MISSING" display text for the details screen.

**Rationale**:
- The spec clearly states status values: MISSING, FOUND, CLOSED
- The existing `AnimalStatus` enum uses ACTIVE, FOUND, CLOSED
- ACTIVE semantically means "actively missing/searching" which maps to MISSING
- Badge colors already match spec requirements (Red for ACTIVE/MISSING, Blue for FOUND, Gray for CLOSED)
- Avoid changing shared domain model to prevent breaking changes across platforms

**Alternatives Considered**:
- Change `AnimalStatus` enum: Would break iOS and Web implementations
- Create separate PetDetailsStatus enum: Adds unnecessary complexity
- Use display name mapping: Clean solution that preserves existing model

**Implementation**:
- Use `AnimalStatus.ACTIVE` internally
- Display "MISSING" text when status is `ACTIVE` in the UI
- Badge colors already correct (Red for ACTIVE, Blue for FOUND, Gray for CLOSED)

---

### 4. Missing Fields in Animal Model

**Question**: Spec requires microchip number, reward amount, and approximate age, but `Animal` model doesn't have these fields. How should we handle this?

**Decision**: Add optional fields to Animal model in shared module OR create Android-specific PetDetails model that extends Animal.

**Rationale**:
- Spec requires: microchip number (optional), reward amount (optional), approximate age (optional)
- Current `Animal` model lacks these fields
- Two approaches:
  1. **Option A (Recommended)**: Add fields to shared `Animal` model (affects all platforms)
  2. **Option B**: Create Android-specific `PetDetails` data class that wraps `Animal` with additional fields

**Alternatives Considered**:
- Option A: Add to shared model
  - Pros: Consistent across platforms, single source of truth
  - Cons: Requires updating iOS and Web implementations
- Option B: Android-specific model
  - Pros: No impact on other platforms, faster implementation
  - Cons: Duplication, potential sync issues

**Decision**: **Option A** - Add fields to shared `Animal` model.

**Rationale for Option A**:
- These fields are part of the domain model and should be consistent across platforms
- Future iOS and Web implementations will need these fields anyway
- Maintains single source of truth
- Aligns with platform independence principle (shared domain models are acceptable)

**Implementation**:
- Add to `shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/models/Animal.kt`:
  - `val microchipNumber: String?` (optional)
  - `val rewardAmount: String?` (optional, string field as per spec)
  - `val approximateAge: String?` (optional)
- Update `MockAnimalData` to include sample values for these fields
- Update Android, iOS, and Web type definitions to include these fields

---

### 5. Date Format Conversion

**Question**: Spec requires date format "MMM DD, YYYY" (e.g., "Nov 18, 2025"), but model stores "DD/MM/YYYY". How should we format dates?

**Decision**: Create a date formatting utility function to convert from "DD/MM/YYYY" to "MMM DD, YYYY" format.

**Rationale**:
- Model stores dates as "DD/MM/YYYY" (e.g., "18/11/2025")
- Spec requires display format "MMM DD, YYYY" (e.g., "Nov 18, 2025")
- Need to parse and reformat dates in the UI layer
- Keep date parsing logic in a utility function for reusability

**Alternatives Considered**:
- Change model format: Would break existing code and other platforms
- Use Android Date/Time API: More robust but requires parsing string first
- Simple string manipulation: Fragile, error-prone

**Implementation**:
- Create `DateFormatter.kt` utility in `composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/lib/`
- Function: `fun formatPetDate(dateString: String): String`
- Parse "DD/MM/YYYY" and format to "MMM DD, YYYY"
- Handle invalid date strings gracefully (return original string or "—")

---

### 6. Microchip Number Formatting

**Question**: Spec requires microchip format "000-000-000-000". How should we format microchip numbers?

**Decision**: Create a formatting utility function to format microchip numbers with dashes.

**Rationale**:
- Spec requires display format: "000-000-000-000"
- Model may store without dashes or with different formatting
- Need consistent formatting in UI layer

**Implementation**:
- Create `MicrochipFormatter.kt` utility in `composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/lib/`
- Function: `fun formatMicrochip(microchip: String?): String`
- Format to "000-000-000-000" pattern (12 digits with dashes)
- Handle null/empty values (return "—")
- Handle invalid formats gracefully (return as-is or "—")

---

### 7. Get Animal By ID Use Case

**Question**: Do we need a new use case to fetch a single animal by ID, or can we filter from the list?

**Decision**: Create `GetAnimalByIdUseCase` that calls repository method `getAnimalById(id: String)`.

**Rationale**:
- More efficient than fetching all animals and filtering
- Follows existing use case pattern (`GetAnimalsUseCase`)
- Repository should support fetching single entity by ID
- Enables future caching and optimization

**Alternatives Considered**:
- Filter from list: Inefficient, requires loading all animals
- Direct repository call from ViewModel: Bypasses use case layer, violates architecture

**Implementation**:
- Add `getAnimalById(id: String): Animal` method to `AnimalRepository` interface
- Implement in `AnimalRepositoryImpl` (filter from mock data for now)
- Create `GetAnimalByIdUseCase` class following `GetAnimalsUseCase` pattern
- Add to Koin DI module

---

### 8. Navigation Parameter Handling

**Question**: How should we extract the animal ID from navigation route?

**Decision**: Use type-safe navigation with `NavRoute.AnimalDetail(animalId: String)`.

**Rationale**:
- Navigation already uses type-safe routes with kotlinx-serialization
- `NavRoute.AnimalDetail` already defined with `animalId` parameter
- Extract from `backStackEntry.toRoute<NavRoute.AnimalDetail>()` in NavGraph
- Type-safe, compile-time checked, no string parsing needed

**Implementation**:
- Uncomment and implement `NavRoute.AnimalDetail` route in `NavGraph.kt`
- Extract `animalId` from route: `val route = backStackEntry.toRoute<NavRoute.AnimalDetail>()`
- Pass `route.animalId` to `PetDetailsScreen`

---

## Summary

All research questions resolved. Key decisions:
1. **Coil**: Add for image loading
2. **minSdk**: API 24 (confirmed from project config)
3. **Status mapping**: Map ACTIVE → "MISSING" in UI
4. **Missing fields**: Add microchipNumber, rewardAmount, approximateAge to shared Animal model
5. **Date formatting**: Create utility function for "DD/MM/YYYY" → "MMM DD, YYYY"
6. **Microchip formatting**: Create utility function for "000-000-000-000" format
7. **Use case**: Create GetAnimalByIdUseCase following existing pattern
8. **Navigation**: Use existing type-safe navigation with NavRoute.AnimalDetail

All clarifications resolved. Ready for Phase 1 (Design & Contracts).



