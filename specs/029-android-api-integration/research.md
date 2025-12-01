# Research: Android Backend API Integration

**Feature**: 029-android-api-integration  
**Date**: 2025-11-28

## HTTP Client Selection

### Decision: Ktor Client + OkHttp Engine + Kotlinx Serialization

### Rationale
- **Ktor Client** is Kotlin-native, idiomatic, and has native coroutine support
- **OkHttp Engine** provides robust HTTP handling, interceptors for logging/auth, and connection pooling
- **Kotlinx Serialization** is Kotlin-native, works well with data classes, and avoids reflection overhead
- Team is already familiar with Ktor - no learning curve
- Future-proof: ready for KMP if project ever goes multiplatform

### Alternatives Considered

| Option | Pros | Cons | Decision |
|--------|------|------|----------|
| **Ktor Client + OkHttp** | Kotlin-native, native coroutines, multiplatform ready, team familiar | Slightly less common on Android | ✅ Selected |
| **Retrofit + OkHttp** | Industry standard, annotation-based API | Java legacy, requires coroutine adapter | ❌ Rejected (no advantage given team knows Ktor) |
| **Volley** | Google-maintained | Dated API, callback-based, no coroutine support | ❌ Rejected |

### JSON Serialization

| Option | Pros | Cons | Decision |
|--------|------|------|----------|
| **Kotlinx Serialization** | Kotlin-native, compile-time, no reflection | Requires compiler plugin | ✅ Selected |
| **Moshi** | Fast, Kotlin support | Additional dependency, reflection mode | ❌ Rejected |
| **Gson** | Popular, simple | Reflection-based, slower, legacy | ❌ Rejected |

## API Base URL Configuration

### Decision: BuildConfig field with environment-specific values

### Rationale
- Base URL should be configurable per build type (debug vs release)
- Avoid hardcoding URLs in source code
- Enable testing with MockWebServer without code changes

### Implementation
```kotlin
// build.gradle.kts
android {
    buildTypes {
        debug {
            buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:3000\"") // Android emulator localhost
        }
        release {
            buildConfigField("String", "API_BASE_URL", "\"https://api.petspot.com\"") // Production
        }
    }
}
```

**Note**: `10.0.2.2` is the Android emulator's alias for the host machine's `localhost`.

## Error Handling Strategy

### Decision: Uniform error handling with Result wrapper

### Rationale
- Per spec clarification: All HTTP errors (4xx, 5xx) treated uniformly
- Use Kotlin `Result<T>` or custom sealed class for success/failure
- Map network exceptions to user-friendly error messages

### Error Categories

| Error Type | HTTP Status | Handling |
|------------|-------------|----------|
| Network unreachable | N/A (IOException) | Generic error message + retry |
| Server error | 5xx | Generic error message + retry |
| Client error | 4xx | Generic error message + retry |
| Timeout | N/A (SocketTimeoutException) | Generic error message + retry |

### Implementation Pattern
```kotlin
suspend fun getAnimals(): List<Animal> {
    return try {
        val response: AnnouncementsResponseDto = httpClient.get("$baseUrl/api/v1/announcements").body()
        response.data.map { it.toDomain() }
    } catch (e: IOException) {
        throw AnimalRepositoryException("Network error", e)
    } catch (e: ClientRequestException) {
        throw AnimalRepositoryException("Client error", e)
    } catch (e: ServerResponseException) {
        throw AnimalRepositoryException("Server error", e)
    }
}
```

## DTO to Domain Model Mapping

### Decision: Extension functions in dedicated mapper file

### Rationale
- Keep DTOs decoupled from domain models
- Single responsibility: DTOs handle serialization, mappers handle conversion
- Easy to test mapping logic in isolation

### Mapping Rules (from spec clarifications)

| API Field | Domain Field | Handling |
|-----------|--------------|----------|
| `status` | `AnimalStatus` | Unknown values → `MISSING` (FR-010) |
| `petName` | `name` | Null → empty string or "Unknown" |
| `breed` | `breed` | Null → empty string |
| `age` | `approximateAge` | Null → null (optional) |
| `microchipNumber` | `microchipNumber` | Null → null (optional) |
| `reward` | `rewardAmount` | Null → null (optional) |
| `sex` | `gender` | Map to `AnimalGender` enum |
| `locationLatitude/Longitude` | `location` | Create `Location` object |
| `managementPassword` | N/A | NEVER mapped (FR-009) |

## Testing Strategy

### Decision: Ktor MockEngine for integration tests, unit tests for mappers

### Unit Tests (no network)
- `AnnouncementMapperTest`: Test all DTO → Domain mappings
- Test edge cases: null fields, unknown status values, invalid data

### Integration Tests (Ktor MockEngine)
- `AnimalRepositoryImplTest`: Test full HTTP flow with mocked responses
- Test success, error (4xx, 5xx), network failure scenarios
- Ktor's MockEngine is simpler than MockWebServer - no server setup needed

### Dependencies
```kotlin
// build.gradle.kts
testImplementation("io.ktor:ktor-client-mock:2.3.7")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
```

## Dependency Versions

| Dependency | Version | Purpose |
|------------|---------|---------|
| `io.ktor:ktor-client-core` | 2.3.7 | Ktor HTTP client core |
| `io.ktor:ktor-client-okhttp` | 2.3.7 | OkHttp engine for Ktor |
| `io.ktor:ktor-client-content-negotiation` | 2.3.7 | Content type handling |
| `io.ktor:ktor-serialization-kotlinx-json` | 2.3.7 | JSON serialization plugin |
| `io.ktor:ktor-client-logging` | 2.3.7 | Request/response logging |
| `org.jetbrains.kotlinx:kotlinx-serialization-json` | 1.6.3 | JSON serialization |
| `io.ktor:ktor-client-mock` | 2.3.7 | Testing with mock engine |

