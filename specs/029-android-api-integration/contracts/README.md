# API Contracts: Android Backend API Integration

**Feature**: 029-android-api-integration  
**Date**: 2025-11-28

## API Documentation Reference

The backend API is fully documented in:

📄 **[/server/README.md](../../../server/README.md)**

## Endpoints Used by This Feature

### GET /api/v1/announcements

Retrieves all pet announcements for the AnimalListScreen.

- **Response**: `{ "data": [...] }` with array of announcement objects
- **Used by**: `GetAnimalsUseCase` → `AnimalListViewModel`

### GET /api/v1/announcements/:id

Retrieves a single pet announcement for the PetDetailsScreen.

- **Path Parameter**: `id` (UUID)
- **Response**: Single announcement object (no `data` wrapper)
- **Used by**: `GetAnimalByIdUseCase` → `PetDetailsViewModel`

## Contract Considerations

### Fields Consumed (Android Client)

| Field | Required | Notes |
|-------|----------|-------|
| `id` | ✅ | Unique identifier |
| `petName` | ❌ | Fallback to "Unknown" if null |
| `species` | ✅ | Parsed to `AnimalSpecies` enum |
| `breed` | ❌ | Fallback to empty string |
| `sex` | ✅ | Parsed to `AnimalGender` enum |
| `age` | ❌ | Formatted as "X years" |
| `description` | ❌ | Fallback to empty string |
| `microchipNumber` | ❌ | Optional display |
| `locationLatitude` | ❌ | For map display |
| `locationLongitude` | ❌ | For map display |
| `lastSeenDate` | ✅ | ISO date format |
| `email` | ❌ | Contact info |
| `phone` | ❌ | Contact info |
| `photoUrl` | ✅ | Image display |
| `status` | ✅ | Badge display |
| `reward` | ❌ | Reward badge |

### Fields NOT Consumed

| Field | Reason |
|-------|--------|
| `managementPassword` | Security (FR-009) - excluded from DTO |
| `createdAt` | Not needed for display |
| `updatedAt` | Not needed for display |

## Error Responses

All error responses follow this format:

```json
{
  "error": {
    "code": "ERROR_CODE",
    "message": "Human readable message"
  }
}
```

### Handled Error Codes

| HTTP Status | Error Code | Android Handling |
|-------------|------------|------------------|
| 404 | `NOT_FOUND` | Generic error message + retry |
| 500 | `INTERNAL_SERVER_ERROR` | Generic error message + retry |
| Any 4xx | Various | Generic error message + retry |
| Any 5xx | Various | Generic error message + retry |

**Note**: Per spec clarification, all HTTP errors are treated uniformly with a generic error message.

