# API Contracts Reference: Android Owner's Details Screen

**Feature**: 045-android-owners-details-screen  
**Date**: 2025-12-04

## Overview

This screen consumes two backend APIs defined in specs 009 and 021:
1. **POST /api/v1/announcements** - Create announcement (Step 1)
2. **POST /api/v1/announcements/:id/photos** - Upload photo (Step 2)

Both APIs must succeed for navigation to Summary screen.

---

## Step 1: Create Announcement

**Endpoint**: `POST /api/v1/announcements`  
**Spec Reference**: [009-create-announcement](../../009-create-announcement/)

### Request

```http
POST /api/v1/announcements HTTP/1.1
Content-Type: application/json

{
  "species": "DOG",
  "sex": "MALE",
  "lastSeenDate": "2025-12-04",
  "locationLatitude": 52.2297,
  "locationLongitude": 21.0122,
  "email": "owner@example.com",
  "phone": "+48123456789",
  "status": "MISSING",
  "microchipNumber": "12345-67890-12345",
  "description": "Brown fur, white paws",
  "reward": "$250 gift card"
}
```

### Request Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| species | string | Yes | Animal species (e.g., "DOG", "CAT") |
| sex | string | Yes | Animal gender ("MALE" or "FEMALE") |
| lastSeenDate | string | Yes | ISO 8601 date (YYYY-MM-DD) |
| locationLatitude | number | Yes | GPS latitude (-90 to 90) |
| locationLongitude | number | Yes | GPS longitude (-180 to 180) |
| email | string | Yes | Owner's email address |
| phone | string | Yes | Owner's phone number |
| status | string | Yes | Always "MISSING" for this flow |
| microchipNumber | string | No | 15-digit chip number (formatted or raw) |
| description | string | No | Additional description (max 500 chars) |
| reward | string | No | Reward description (max 120 chars) |

### Response (201 Created)

```json
{
  "id": "bb3fc451-1f51-407d-bb85-2569dc9baed3",
  "managementPassword": "467432",
  "species": "DOG",
  "sex": "MALE",
  "lastSeenDate": "2025-12-04",
  "locationLatitude": 52.2297,
  "locationLongitude": 21.0122,
  "email": "owner@example.com",
  "phone": "+48123456789",
  "status": "MISSING",
  "microchipNumber": "12345-67890-12345",
  "description": "Brown fur, white paws",
  "reward": "$250 gift card",
  "photoUrl": null,
  "createdAt": "2025-12-04T10:30:00Z"
}
```

### Response Fields (Relevant)

| Field | Type | Description |
|-------|------|-------------|
| id | string | UUID for photo upload endpoint |
| managementPassword | string | 6-digit code for user reference |
| photoUrl | string? | Always null (photo uploaded separately) |

### Error Responses

| Status | Description |
|--------|-------------|
| 400 | Invalid request body (validation failed) |
| 500 | Server error |

---

## Step 2: Upload Photo

**Endpoint**: `POST /api/v1/announcements/:id/photos`  
**Spec Reference**: [021-announcement-photo-upload](../../021-announcement-photo-upload/)

### Request

```http
POST /api/v1/announcements/bb3fc451-1f51-407d-bb85-2569dc9baed3/photos HTTP/1.1
Content-Type: multipart/form-data
Authorization: Basic YmIzZmM0NTEtMWY1MS00MDdkLWJiODUtMjU2OWRjOWJhZWQzOjQ2NzQzMg==

--boundary
Content-Disposition: form-data; name="photo"; filename="pet.jpg"
Content-Type: image/jpeg

<binary image data>
--boundary--
```

### Authentication

**Type**: HTTP Basic Authentication  
**Credentials**: `id:managementPassword` from Step 1 response

**Header Construction**:
```kotlin
val credentials = "$announcementId:$managementPassword"
// e.g., "bb3fc451-1f51-407d-bb85-2569dc9baed3:467432"

val basicAuth = "Basic " + Base64.encodeToString(
    credentials.toByteArray(),
    Base64.NO_WRAP
)
// Result: "Basic YmIzZmM0NTEtMWY1MS00MDdkLWJiODUtMjU2OWRjOWJhZWQzOjQ2NzQzMg=="
```

### Request Body

| Part | Type | Required | Description |
|------|------|----------|-------------|
| photo | file | Yes | Image file (JPG, PNG, GIF, WEBP) |

### Response (201 Created)

```json
{
  "photoUrl": "https://api.petspot.com/photos/abc123.jpg"
}
```

### Error Responses

| Status | Description |
|--------|-------------|
| 400 | Invalid file format or missing photo |
| 401 | Invalid or missing Basic auth |
| 404 | Announcement not found |
| 500 | Server error |

---

## Android Implementation

### Ktor Client Usage

The project uses Ktor HTTP client with OkHttp engine. Repository implementations call Ktor directly.

```kotlin
// Existing HttpClient configured in DI module
val httpClient = HttpClient(OkHttp) {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
    install(Logging) { level = LogLevel.BODY }
    defaultRequest {
        url(BuildConfig.API_BASE_URL)
        contentType(ContentType.Application.Json)
    }
}
```

### Repository Implementation

```kotlin
class AnnouncementRepositoryImpl(
    private val httpClient: HttpClient,
    private val contentResolver: ContentResolver  // Inject ContentResolver, NOT Context
) : AnnouncementRepository {
    
    override suspend fun createAnnouncement(
        request: AnnouncementCreateRequest
    ): Result<AnnouncementResponse> {
        return try {
            val response: AnnouncementResponse = httpClient.post("/api/v1/announcements") {
                setBody(request)
            }.body()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun uploadPhoto(
        announcementId: String,
        managementPassword: String,
        photoUri: String
    ): Result<Unit> {
        return try {
            // Build Basic auth header
            val credentials = "$announcementId:$managementPassword"
            val basicAuth = "Basic " + Base64.encodeToString(
                credentials.toByteArray(),
                Base64.NO_WRAP
            )
            
            // Read photo from URI using injected ContentResolver
            val uri = Uri.parse(photoUri)
            val inputStream = contentResolver.openInputStream(uri)
                ?: return Result.failure(IOException("Cannot open photo URI"))
            val bytes = inputStream.readBytes()
            inputStream.close()
            
            // Upload as multipart form
            httpClient.post("/api/v1/announcements/$announcementId/photos") {
                header(HttpHeaders.Authorization, basicAuth)
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("photo", bytes, Headers.build {
                                append(HttpHeaders.ContentType, "image/*")
                                append(HttpHeaders.ContentDisposition, "filename=\"pet.jpg\"")
                            })
                        }
                    )
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// DI registration (in Koin module):
// single { get<Context>().contentResolver }
// single<AnnouncementRepository> { AnnouncementRepositoryImpl(get(), get()) }
```

---

## Error Handling

### Network Errors

Handled reactively - no active online/offline check:

```kotlin
when (val result = repository.createAnnouncement(request)) {
    is Result.Success -> { /* proceed to step 2 */ }
    is Result.Failure -> {
        when (result.exception) {
            is SocketTimeoutException,
            is UnknownHostException,
            is ConnectException -> {
                // Network error - show "Something went wrong"
            }
            is ApiException -> {
                // Backend error (4xx/5xx) - show "Something went wrong"
            }
        }
    }
}
```

### Retry Logic

Full 2-step retry from Step 1:

```kotlin
fun retry() {
    viewModelScope.launch {
        _state.update { it.copy(isSubmitting = true) }
        
        // Always retry from Step 1 (announcement creation)
        val result = submitAnnouncementUseCase(flowState.data.value)
        
        result.fold(
            onSuccess = { password ->
                _state.update { it.copy(isSubmitting = false) }
                _effects.emit(NavigateToSummary(password))
            },
            onFailure = {
                _state.update { it.copy(isSubmitting = false) }
                _effects.emit(ShowSnackbar("Something went wrong. Please try again.", Retry))
            }
        )
    }
}
```

