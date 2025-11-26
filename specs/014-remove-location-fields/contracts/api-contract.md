# API Contract: Remove Location Fields

**Feature**: Remove Location Fields  
**Date**: 2025-01-27  
**Status**: Updated

## Endpoint: POST /announcements

### Request Body

**Removed Fields**:
- `locationCity` (string, optional) - ❌ Removed
- `locationRadius` (number, optional) - ❌ Removed

**Retained Fields**:
- `locationLatitude` (number, required) - ✅ Retained
- `locationLongitude` (number, required) - ✅ Retained

### Request Example (Valid)

```json
{
  "species": "Dog",
  "sex": "MALE",
  "locationLatitude": 40.785091,
  "locationLongitude": -73.968285,
  "photoUrl": "https://example.com/photo.jpg",
  "lastSeenDate": "2025-01-20",
  "status": "MISSING",
  "email": "test@example.com"
}
```

### Request Example (Invalid - Contains Removed Fields)

```json
{
  "species": "Dog",
  "sex": "MALE",
  "locationLatitude": 40.785091,
  "locationLongitude": -73.968285,
  "locationCity": "New York",
  "locationRadius": 5,
  "photoUrl": "https://example.com/photo.jpg",
  "lastSeenDate": "2025-01-20",
  "status": "MISSING",
  "email": "test@example.com"
}
```

**Response**: 400 Bad Request
```json
{
  "error": {
    "code": "INVALID_FIELD",
    "message": "locationCity is not a valid field",
    "field": "locationCity"
  }
}
```

### Response Body (201 Created)

**Removed Fields**:
- `locationCity` (string | null) - ❌ Removed
- `locationRadius` (number | null) - ❌ Removed

**Retained Fields**:
- `locationLatitude` (number) - ✅ Retained
- `locationLongitude` (number) - ✅ Retained

### Response Example

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "species": "Dog",
  "sex": "MALE",
  "locationLatitude": 40.785091,
  "locationLongitude": -73.968285,
  "photoUrl": "https://example.com/photo.jpg",
  "lastSeenDate": "2025-01-20",
  "status": "MISSING",
  "email": "test@example.com",
  "managementPassword": "847362",
  "createdAt": "2025-01-27T12:34:56.789Z"
}
```

## Endpoint: GET /announcements

### Response Body

**Removed Fields**:
- `locationCity` (string | null) - ❌ Removed
- `locationRadius` (number | null) - ❌ Removed

**Retained Fields**:
- `locationLatitude` (number) - ✅ Retained
- `locationLongitude` (number) - ✅ Retained

### Response Example

```json
{
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "species": "Dog",
      "sex": "MALE",
      "locationLatitude": 40.785091,
      "locationLongitude": -73.968285,
      "photoUrl": "https://example.com/photo.jpg",
      "lastSeenDate": "2025-01-20",
      "status": "MISSING",
      "email": "test@example.com",
      "createdAt": "2025-01-27T12:34:56.789Z",
      "updatedAt": "2025-01-27T12:34:56.789Z"
    }
  ]
}
```

## Endpoint: GET /announcements/:id

### Response Body

**Removed Fields**:
- `locationCity` (string | null) - ❌ Removed
- `locationRadius` (number | null) - ❌ Removed

**Retained Fields**:
- `locationLatitude` (number) - ✅ Retained
- `locationLongitude` (number) - ✅ Retained

### Response Example

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "species": "Dog",
  "sex": "MALE",
  "locationLatitude": 40.785091,
  "locationLongitude": -73.968285,
  "photoUrl": "https://example.com/photo.jpg",
  "lastSeenDate": "2025-01-20",
  "status": "MISSING",
  "email": "test@example.com",
  "createdAt": "2025-01-27T12:34:56.789Z",
  "updatedAt": "2025-01-27T12:34:56.789Z"
}
```

## Error Responses

### 400 Bad Request - Invalid Field

**Trigger**: Client sends `location`, `locationCity`, or `locationRadius` fields

**Response**:
```json
{
  "error": {
    "code": "INVALID_FIELD",
    "message": "{field} is not a valid field",
    "field": "locationCity"
  }
}
```

**Error Codes**:
- `INVALID_FIELD` - Field is not recognized (unknown/deprecated field)

## Breaking Changes

### Removed Request Fields
- `locationCity` (string, optional)
- `locationRadius` (number, optional)

### Removed Response Fields
- `locationCity` (string | null)
- `locationRadius` (number | null)

### Migration Notes
- Clients sending `locationCity` or `locationRadius` will receive `INVALID_FIELD` error
- Clients expecting `locationCity` or `locationRadius` in responses will not receive these fields
- Location information is now provided solely via `locationLatitude` and `locationLongitude`

## Backward Compatibility

**Status**: ❌ Breaking Change

This is a breaking change. Clients must:
1. Stop sending `locationCity` and `locationRadius` in requests
2. Stop expecting `locationCity` and `locationRadius` in responses
3. Use `locationLatitude` and `locationLongitude` for all location data

