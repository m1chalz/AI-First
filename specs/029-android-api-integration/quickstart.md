# Quickstart: Android Backend API Integration

**Feature**: 029-android-api-integration  
**Date**: 2025-11-28

## Prerequisites

1. **Android Studio** (Hedgehog or newer)
2. **JDK 17** (required by project)
3. **Backend server running** on `localhost:3000`

## Setup Steps

### 1. Start the Backend Server

```bash
# From repository root
cd server
npm install
npm run dev
```

Verify server is running: `curl http://localhost:3000/api/v1/announcements`

### 2. Build Android App

```bash
# From repository root
./gradlew :composeApp:assembleDebug
```

### 3. Run on Emulator

The app is configured to use `10.0.2.2:3000` for debug builds (Android emulator's localhost alias).

```bash
./gradlew :composeApp:installDebug
```

Or run from Android Studio: Select `composeApp` configuration and click Run.

## Key Files

| File | Purpose |
|------|---------|
| `data/api/AnnouncementApiClient.kt` | Ktor HTTP client wrapper |
| `data/api/dto/AnnouncementDto.kt` | API response data classes |
| `data/mappers/AnnouncementMapper.kt` | DTO → Domain model conversion |
| `data/AnimalRepositoryImpl.kt` | Repository implementation (HTTP calls) |
| `di/DataModule.kt` | Koin DI configuration (HttpClient setup) |

## Testing

### Run Unit Tests

```bash
./gradlew :composeApp:testDebugUnitTest
```

### Run Tests with Coverage

```bash
./gradlew :composeApp:testDebugUnitTest koverHtmlReport
```

Open coverage report: `composeApp/build/reports/kover/html/index.html`

### Test Against Mock Engine

Unit tests use Ktor MockEngine - no real backend required:

```kotlin
@Test
fun `should fetch animals from API`() = runTest {
    // Given
    val mockEngine = MockEngine { request ->
        respond(
            content = """{"data": [...]}""",
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, "application/json")
        )
    }
    val client = HttpClient(mockEngine) { /* config */ }
    val repository = AnimalRepositoryImpl(client)
    
    // When
    val result = repository.getAnimals()
    
    // Then
    assertEquals(2, result.size)
}
```

## Debugging Tips

### Network Inspection

1. Enable Ktor Logging plugin (already configured for debug builds)
2. Check Logcat with tag `Ktor` for request/response details

### Common Issues

| Issue | Solution |
|-------|----------|
| `Connection refused` | Ensure backend server is running |
| `cleartext HTTP not permitted` | Use emulator (10.0.2.2) or add network security config |
| `UnknownHostException` | Check emulator network settings |

### API Response Verification

Test API directly from emulator shell:

```bash
adb shell
curl http://10.0.2.2:3000/api/v1/announcements
```

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                      UI Layer (Compose)                      │
│  AnimalListScreen ←──────────────────→ PetDetailsScreen     │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                        │
│  AnimalListViewModel ←────────────→ PetDetailsViewModel     │
│  (StateFlow<UiState>, dispatchIntent)                       │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      Domain Layer                            │
│  GetAnimalsUseCase ←───────────────→ GetAnimalByIdUseCase   │
│          │                                    │              │
│          └─────────────┬──────────────────────┘              │
│                        ▼                                     │
│              AnimalRepository (interface)                    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       Data Layer                             │
│  AnimalRepositoryImpl ──────────────→ AnnouncementApiClient │
│          │                                    │              │
│          ▼                                    ▼              │
│  AnnouncementMapper                    Ktor Client + OkHttp  │
│  (DTO → Domain)                        (HTTP client)         │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Backend Server                            │
│  GET /api/v1/announcements                                  │
│  GET /api/v1/announcements/:id                              │
└─────────────────────────────────────────────────────────────┘
```

## Related Documentation

- [Feature Specification](./spec.md)
- [Implementation Plan](./plan.md)
- [Data Model Mapping](./data-model.md)
- [Research & Decisions](./research.md)
- [Backend API Documentation](../../server/README.md)

