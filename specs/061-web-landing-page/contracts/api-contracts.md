# API Contracts: Web Application Landing Page

**Feature**: 061-web-landing-page  
**Date**: 2025-12-17

## Overview

This document defines the API contracts between the web frontend and the backend server for the landing page feature. The landing page consumes an existing backend API endpoint to fetch recently lost pet announcements.

## Base URL

- **Development**: `http://localhost:3000`
- **Production**: TBD (configured via `config.apiBaseUrl`)

---

## Endpoints

### GET /api/v1/announcements

Fetch all pet announcements with optional location-based filtering.

**Used by**: Landing Page (Recently Lost Pets section)

**Request**:

```http
GET /api/v1/announcements?lat={latitude}&lng={longitude}&range={rangeKm} HTTP/1.1
Host: localhost:3000
Accept: application/json
```

**Query Parameters** (all optional):

| Parameter | Type   | Required | Description                                    | Example     |
|-----------|--------|----------|------------------------------------------------|-------------|
| `lat`     | number | No       | User's latitude for distance calculation       | `40.7128`   |
| `lng`     | number | No       | User's longitude for distance calculation      | `-74.0060`  |
| `range`   | number | No       | Maximum distance in km (filtering radius)      | `50`        |

**Response**:

```json
{
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "petName": "Max",
      "species": "dog",
      "breed": "Golden Retriever",
      "sex": "MALE",
      "age": 3,
      "description": "Friendly golden retriever, answers to Max",
      "microchipNumber": "123456789012345",
      "locationLatitude": 40.7580,
      "locationLongitude": -73.9855,
      "email": "owner@example.com",
      "phone": "+1234567890",
      "photoUrl": "/public/images/max.jpg",
      "lastSeenDate": "2025-12-15",
      "status": "MISSING",
      "reward": "$500",
      "createdAt": "2025-12-15T10:30:00Z",
      "updatedAt": "2025-12-15T10:30:00Z"
    },
    {
      "id": "650e8400-e29b-41d4-a716-446655440001",
      "petName": "Luna",
      "species": "cat",
      "breed": "Siamese",
      "sex": "FEMALE",
      "age": 2,
      "description": "Blue-eyed Siamese cat, very shy",
      "microchipNumber": null,
      "locationLatitude": 40.7489,
      "locationLongitude": -73.9680,
      "email": "owner2@example.com",
      "phone": null,
      "photoUrl": "/public/images/luna.jpg",
      "lastSeenDate": "2025-12-14",
      "status": "MISSING",
      "reward": null,
      "createdAt": "2025-12-14T14:20:00Z",
      "updatedAt": "2025-12-14T14:20:00Z"
    }
  ]
}
```

**Response Fields**:

| Field                | Type     | Nullable | Description                                      |
|----------------------|----------|----------|--------------------------------------------------|
| `data`               | array    | No       | Array of announcement objects                    |
| `data[].id`          | string   | No       | UUID v4 identifier                               |
| `data[].petName`     | string   | Yes      | Name of the pet                                  |
| `data[].species`     | string   | No       | Species (e.g., "dog", "cat")                     |
| `data[].breed`       | string   | Yes      | Breed of the pet                                 |
| `data[].sex`         | string   | No       | Sex (MALE, FEMALE, UNKNOWN)                      |
| `data[].age`         | number   | Yes      | Age in years                                     |
| `data[].description` | string   | Yes      | Description of the pet                           |
| `data[].microchipNumber` | string | Yes    | Microchip number (15 digits)                     |
| `data[].locationLatitude` | number | No    | Latitude (-90 to 90)                             |
| `data[].locationLongitude` | number | No   | Longitude (-180 to 180)                          |
| `data[].email`       | string   | Yes      | Owner's email                                    |
| `data[].phone`       | string   | Yes      | Owner's phone number                             |
| `data[].photoUrl`    | string   | Yes      | Relative path to pet photo                       |
| `data[].lastSeenDate`| string   | No       | Date last seen (YYYY-MM-DD)                      |
| `data[].status`      | string   | No       | Status: "MISSING" or "FOUND"                     |
| `data[].reward`      | string   | Yes      | Reward information                               |
| `data[].createdAt`   | string   | No       | ISO 8601 timestamp (creation)                    |
| `data[].updatedAt`   | string   | No       | ISO 8601 timestamp (last update)                 |

**Status Codes**:

| Code | Description                                              |
|------|----------------------------------------------------------|
| 200  | Success - returns array of announcements                 |
| 500  | Internal server error - backend unavailable or error     |

**Error Response** (500):

```json
{
  "error": "Internal server error",
  "message": "Failed to fetch announcements"
}
```

**Frontend Processing** (Landing Page):

```typescript
// 1. Fetch announcements with user location (if available)
const response = await fetch(`/api/v1/announcements?lat=${lat}&lng=${lng}`);
const json = await response.json();

// 2. Filter for MISSING status only
const missingPets = json.data.filter(announcement => announcement.status === 'MISSING');

// 3. Sort by createdAt (newest first)
const sorted = missingPets.sort((a, b) => 
  new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
);

// 4. Limit to 5 items
const recentPets = sorted.slice(0, 5);

// 5. Update state
setState({ announcements: recentPets, isLoading: false, error: null });
```

**Notes**:
- Backend returns **both** MISSING and FOUND announcements
- Frontend **must filter** for `status === 'MISSING'` before display
- Backend **may** include distance calculation if lat/lng provided (format TBD)
- Photo URLs are **relative paths** - prepend `config.apiBaseUrl` when displaying
- Empty array response is valid (no announcements exist)

---

## Frontend Service Interface (Existing)

### AnnouncementService

**File**: `webApp/src/services/announcement-service.ts` (already exists)

**Interface**:

```typescript
export interface AnnouncementService {
  /**
   * Fetches all pet announcements from the backend.
   * Optionally includes user location for distance calculation.
   */
  getAnnouncements(location?: { lat: number; lng: number } | null): Promise<Announcement[]>;
}
```

**Notes**:
- Service already exists in the codebase
- Used by existing `useAnnouncementList()` hook
- Returns all announcements (both MISSING and FOUND)
- Landing page will filter for MISSING status in component

**Usage in Landing Page** (via `useAnnouncementList` hook):

```typescript
import { useAnnouncementList } from '../hooks/use-announcement-list';

// In Landing Page component
function RecentPetsSection() {
  // Hook automatically uses announcementService and GeolocationContext
  const { announcements, isLoading, error } = useAnnouncementList();

  // Filter, sort, limit to 5 most recent MISSING pets
  const recentMissingPets = announcements
    .filter(a => a.status === 'MISSING')
    .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
    .slice(0, 5);

  if (isLoading) return <LoadingSpinner />;
  if (error) return <ErrorMessage error={error} />;
  if (recentMissingPets.length === 0) return <EmptyState />;

  return (
    <div>
      {recentMissingPets.map(announcement => (
        <LandingPageCard key={announcement.id} announcement={announcement} />
      ))}
    </div>
  );
}
```

---

## Custom Hook: useAnnouncementList (Existing)

**File**: `webApp/src/hooks/use-announcement-list.ts` (already exists)

**Interface**:

```typescript
export interface UseAnnouncementListResult {
  announcements: Announcement[];
  isLoading: boolean;
  error: string | null;
  isEmpty: boolean;
  loadAnnouncements: () => Promise<void>;
  geolocationError: GeolocationPositionError | null;
}

/**
 * Custom React hook for fetching and managing announcements.
 * Integrates with GeolocationContext for location-based filtering.
 * Returns ALL announcements (both MISSING and FOUND).
 */
export function useAnnouncementList(): UseAnnouncementListResult;
```

**Usage in Landing Page** (with filtering):

```typescript
import { useAnnouncementList } from '../hooks/use-announcement-list';

function LandingPage() {
  const { announcements, isLoading, error } = useAnnouncementList();

  // Filter, sort, and limit to 5 most recent MISSING pets
  const recentMissingPets = announcements
    .filter(a => a.status === 'MISSING')
    .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
    .slice(0, 5);

  if (isLoading) return <LoadingSpinner />;
  if (error) return <ErrorMessage error={error} />;
  if (recentMissingPets.length === 0) return <EmptyState />;

  return (
    <div>
      {recentMissingPets.map(announcement => (
        <LandingPageCard key={announcement.id} announcement={announcement} />
      ))}
    </div>
  );
}
```

**Notes**:
- Hook already exists - no new implementation needed
- Already integrates with `GeolocationContext` for user location
- Returns all announcements - filtering/sorting done in component
- Reuses existing `announcementService.getAnnouncements()`

---

## Error Handling

### Client-Side Errors

| Scenario                  | Error Message                                                      | UI Display                        |
|---------------------------|--------------------------------------------------------------------|-----------------------------------|
| Network failure           | "Unable to load recent pets. Please refresh the page to try again." | Error message in Recent Pets section |
| API returns 500           | "Unable to load recent pets. Please refresh the page to try again." | Error message in Recent Pets section |
| No MISSING pets           | N/A                                                                | Empty state: "No recent lost pet reports. Check back soon!" |
| Photo load failure        | N/A                                                                | Placeholder image (generic pet silhouette) |
| Geolocation unavailable   | N/A (graceful fallback)                                            | Fetch announcements without location |

**Error Recovery**:
- No automatic retry mechanism (per spec)
- No manual retry button (per spec)
- User must manually refresh page to retry

---

## Data Validation

### Frontend Validation

**Before Display**:
- Filter: `status === 'MISSING'` (exclude FOUND announcements)
- Validate: `announcement.id` is non-empty string
- Validate: `announcement.status` is 'MISSING' or 'FOUND'
- Validate: `announcement.createdAt` is valid ISO 8601 string
- Handle null/undefined fields gracefully (display fallbacks)

**Photo URL Processing**:
- Prepend `config.apiBaseUrl` to relative photoUrl
- Example: `/public/images/pet.jpg` → `http://localhost:3000/public/images/pet.jpg`
- Handle null photoUrl with placeholder image

### Backend Validation

Handled by existing backend endpoint (no changes required).

---

## Security Considerations

- **CORS**: Backend must allow requests from frontend origin
- **Rate Limiting**: Backend may implement rate limiting (not required for landing page)
- **Authentication**: Not required for landing page (public endpoint)
- **Data Sanitization**: Backend sanitizes data before storage (not frontend responsibility)

---

## Performance Considerations

- **Caching**: No caching strategy required (data freshness prioritized)
- **Lazy Loading**: Pet card photos loaded lazily (`loading="lazy"` attribute)
- **Pagination**: Not applicable (max 5 items displayed)
- **Bundle Size**: Use tree-shakeable imports from `react-icons`

---

## Summary

The landing page consumes a single existing backend endpoint (`GET /api/v1/announcements`) with no modifications required to the backend. Frontend is responsible for:
1. Fetching announcements with optional user location
2. Filtering for `status === 'MISSING'`
3. Sorting by `createdAt` (newest first)
4. Limiting to 5 items
5. Error handling and empty state display

No new backend endpoints, database changes, or API modifications are needed.

