# Implementation Plan: Android Backend API Integration

**Branch**: `029-android-api-integration` | **Date**: 2025-11-28 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/029-android-api-integration/spec.md`

## Summary

Replace mock `AnimalRepositoryImpl` with a real HTTP client that fetches pet announcements from the backend REST API. The existing MVI architecture, domain models, use cases, and ViewModels remain unchanged - only the repository implementation is swapped. This enables `AnimalListScreen` to display live data from `GET /api/v1/announcements` and `PetDetailsScreen` to display details from `GET /api/v1/announcements/:id`.

## Technical Context

**Language/Version**: Kotlin 2.0+, JVM 17  
**Primary Dependencies**: Ktor Client + OkHttp Engine (HTTP client), Kotlinx Serialization (JSON), Koin (DI - already configured)  
**Storage**: N/A (read-only API consumption)  
**Testing**: JUnit 6 + Kotlin Test + Turbine (Flow testing), Ktor MockEngine (API mocking)  
**Target Platform**: Android (minSdk 24, targetSdk 35)  
**Project Type**: Mobile (Android)  
**Performance Goals**: N/A (not in scope per clarification)  
**Constraints**: N/A  
**Scale/Scope**: Single feature affecting 2 screens, ~5 new/modified files

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: Domain models, use cases, repositories, ViewModels in `/composeApp` ✓
  - iOS: N/A (Android-only feature)
  - Web: N/A (Android-only feature)
  - Backend: Not modified - only consuming existing API ✓
  - NO shared compiled code between platforms ✓

- [x] **Android MVI Architecture**: Android features follow the mandated Compose MVI loop
  - Single `StateFlow<UiState>` source of truth with immutable data classes ✓ (existing)
  - Sealed `UserIntent` and `UiEffect` types co-located with feature packages ✓ (existing)
  - Reducers implemented as pure functions and unit-tested ✓ (existing)
  - Navigation uses Jetpack Navigation Component ✓ (existing)
  - Composable screens follow two-layer pattern ✓ (existing AnimalListContent, PetDetailsContent)
  - No changes to MVI layer - only repository implementation

- [x] **iOS MVVM-C Architecture**: N/A (Android-only feature)

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories
  - Android: `AnimalRepository` interface exists in `/composeApp/src/androidMain/.../domain/repositories/` ✓
  - Implementation will update existing `AnimalRepositoryImpl` in `/composeApp/src/androidMain/.../data/` ✓
  - Use cases reference interface, not concrete implementation ✓

- [x] **Dependency Injection**: Plan includes DI setup
  - Android: Koin already configured in `/composeApp/src/androidMain/.../di/` ✓
  - Will update `dataModule` to provide Ktor HttpClient and API client ✓

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests
  - Android: Tests will be added to `/composeApp/src/androidUnitTest/` ✓
  - New tests for: DTO mapping, repository implementation, error handling
  - Coverage target: 80% line + branch coverage ✓

- [x] **End-to-End Tests**: N/A
  - E2E tests already exist for AnimalListScreen and PetDetailsScreen
  - No new user flows added - only data source changed
  - Existing E2E tests will validate integration

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns
  - Android: Kotlin Coroutines (`suspend` functions) + Flow for state ✓
  - Retrofit with coroutine adapters ✓
  - No prohibited patterns ✓

- [x] **Test Identifiers for UI Controls**: N/A
  - No new UI elements added
  - Existing testTags remain unchanged

- [x] **Public API Documentation**: Plan ensures public APIs have documentation
  - New repository implementation will have KDoc ✓
  - DTO classes will have field documentation ✓

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow convention
  - Unit tests will use Given-When-Then with backtick test names ✓
  - MockWebServer tests will verify request/response mapping ✓

### Backend Architecture & Quality Standards

- [x] **Backend checks**: N/A - `/server` not modified
  - Feature only consumes existing API endpoints
  - No backend code changes required

## Project Structure

### Documentation (this feature)

```text
specs/029-android-api-integration/
├── plan.md              # This file
├── research.md          # HTTP client selection, error handling patterns
├── data-model.md        # DTO ↔ Domain model mapping
├── quickstart.md        # Setup instructions for new developers
├── contracts/           # API contract reference
│   └── README.md        # Link to server README.md API docs
└── tasks.md             # Implementation tasks (created by /speckit.tasks)
```

### Source Code (repository root)

```text
composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/
├── data/
│   ├── AnimalRepositoryImpl.kt          # MODIFY: Replace mock with Ktor HTTP client call
│   ├── api/
│   │   ├── AnnouncementApiClient.kt     # NEW: Ktor client wrapper
│   │   └── dto/
│   │       └── AnnouncementDto.kt       # NEW: API response DTOs
│   └── mappers/
│       └── AnnouncementMapper.kt        # NEW: DTO → Domain model mapper
├── di/
│   └── DataModule.kt                    # MODIFY: Add Ktor HttpClient, API client
└── domain/
├── models/
    │   └── Animal.kt                    # UNCHANGED: Existing domain model
    └── repositories/
        └── AnimalRepository.kt          # UNCHANGED: Existing interface

composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/
└── data/
    ├── AnimalRepositoryImplTest.kt      # NEW: Repository unit tests (Ktor MockEngine)
    └── mappers/
        └── AnnouncementMapperTest.kt    # NEW: Mapper unit tests
```

**Structure Decision**: Minimal changes to existing architecture. New files added under `/data/api/` for Ktor HTTP layer, `/data/mappers/` for DTO conversion. Existing MVI layer untouched.

## Complexity Tracking

No constitution violations. All checks pass or are N/A for this Android-only, API-consuming feature.
