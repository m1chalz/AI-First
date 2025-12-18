# API Contracts: iOS Landing Page (Home Tab)

**Branch**: `058-ios-landing-page-list` | **Date**: 2025-12-16

## Summary

This feature **does not introduce new API endpoints or modify existing contracts**. The iOS landing page reuses the existing GET /api/v1/announcements endpoint without any changes.

---

## Reused API Endpoints

### 1. GET /api/v1/announcements

**Purpose**: Fetch list of all pet announcements (landing page uses client-side filtering to display first 5 most recent)

**Contract Location**: `/specs/036-ios-announcements-api/contracts/announcements-list.yaml`

**Usage in Landing Page**:
- iOS client calls `GET /api/v1/announcements` (no query parameters required for basic list)
- Optional: Include `lat` and `lng` query parameters if user has granted location permissions (for distance calculation)
- iOS LandingPageViewModel performs client-side sorting (by `createdAt` descending) and filtering (limit 5 items)

**Request Example** (without location):
```http
GET /api/v1/announcements HTTP/1.1
Host: localhost:3000
Accept: application/json
```

**Request Example** (with location):
```http
GET /api/v1/announcements?lat=52.2297&lng=21.0122 HTTP/1.1
Host: localhost:3000
Accept: application/json
```

**Response Example**:
```json
{
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "petName": "Max",
      "species": "dog",
      "status": "missing",
      "photoUrl": "http://localhost:3000/images/max.jpg",
      "lastSeenDate": "2024-11-15",
      "latitude": 52.2297,
      "longitude": 21.0122,
      "breed": "Golden Retriever",
      "description": "Friendly golden retriever, responds to Max",
      "contactPhone": "+48123456789"
    }
  ]
}
```

**HTTP Status Codes**:
- `200 OK`: Successfully retrieved announcements list (may be empty array)
- `500 Internal Server Error`: Backend error (database failure, etc.)

**Client-Side Processing**:
1. Parse response JSON into `[Announcement]` array
2. Sort by `createdAt` field descending (newest first)
3. Take first 5 items: `.prefix(5)`
4. Display on landing page

**Error Handling**:
- Network errors: Display error message with retry button
- Empty response (`data: []`): Display empty state view
- Invalid items: Skip during parsing (handled by existing AnnouncementRepository)

---

## Backend Compatibility

**Existing Backend Version**: v1.0.0 (no changes required)

**Future Optimization Opportunity** (not in this iteration):
- Backend could support query parameters `?sort=createdAt:desc&limit=5` to reduce payload size
- This would be an **additive change** (backward compatible) and can be implemented later without breaking iOS client
- iOS client would benefit from smaller network payload and faster parsing

---

## Contract Validation

No contract changes → No validation needed. Existing endpoint already tested and deployed in previous features (036-ios-announcements-api).

---

## Summary

The landing page feature is a pure iOS client-side implementation that leverages existing backend infrastructure. No API changes, no contract updates, no backend development required. Client-side filtering and sorting provide the required "5 most recent announcements" functionality without backend modifications.

