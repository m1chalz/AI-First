# Feature Specification: Announcements Location Query

**Feature Branch**: `033-announcements-location-query`  
**Created**: 2025-11-29  
**Status**: Draft  
**Input**: User description: "zmiana tylko w /server. dodajemy opcjonalne parametry lat, lng i range w endpoincie /api/v1/announcements (query params). jeśli parametry są podane, endpoint ma zwracać tylko ogłoszenia które znajdują się w podanym promieniu [km] od podanych koordynatów (lat, lng). jeśli podany jest parametr lat, lng również musi (i odwrotnie) - inaczej zwracamy HTTP 400. parametr range jest opcjonalny - jeśli lat i lng są podane a range nie, to dla range przyjmujemy 5"

## Clarifications

### Session 2025-11-29

- Q: What happens when range is set to 0? → A: Treat as invalid parameter and return HTTP 400 error
- Q: What happens when the range parameter is provided but lat/lng are not? → A: Ignore the range parameter and return all announcements (existing behavior)
- Q: How does the system handle announcements that don't have location data? → A: Location data is mandatory for all announcements (this edge case doesn't apply)
- Q: Should there be a maximum limit for the range parameter? → A: No maximum limit - accept any positive number
- Q: Should the API response include calculated distance from search coordinates for each announcement? → A: No - return only announcement data without distance information
- Q: When location filtering is applied, how should the endpoint order announcements? → A: Keep the existing default ordering (e.g., newest first)
- Q: Should the `range` parameter accept decimal values? → A: No - reject non-integer values with HTTP 400

## User Scenarios & Testing

### User Story 1 - Filter Announcements by Location with Custom Radius (Priority: P1)

Users can search for pet announcements near their current location or any specified location, with the ability to control the search radius. This allows users to find relevant announcements within a specific distance from their coordinates.

**Why this priority**: This is the core functionality - enabling location-based filtering is the primary value of this feature. Users need to be able to specify coordinates and a custom radius to find announcements that are geographically relevant to them.

**Independent Test**: Can be fully tested by making a GET request to `/api/v1/announcements?lat=50.0&lng=20.0&range=10` and verifying that only announcements within 10km of the specified coordinates are returned. Delivers immediate value by showing location-filtered results.

**Acceptance Scenarios**:

1. **Given** there are announcements at various locations in the database, **When** a user requests announcements with lat=50.0, lng=20.0, and range=10, **Then** the system returns only announcements within 10 kilometers of those coordinates
2. **Given** a user is at a specific location, **When** they search with a large radius (e.g., 50km), **Then** all announcements within that radius are returned
3. **Given** a user searches with a small radius (e.g., 1km), **When** no announcements exist within that radius, **Then** an empty list is returned with a 200 OK status

---

### User Story 2 - Filter Announcements by Location with Default Radius (Priority: P2)

Users can search for announcements near a location without specifying a radius, and the system will automatically use a sensible default of 5 kilometers. This simplifies the user experience for common use cases.

**Why this priority**: This provides convenience for users who don't need to customize the search radius. A default value reduces friction and makes the API easier to use for typical scenarios.

**Independent Test**: Can be fully tested by making a GET request to `/api/v1/announcements?lat=50.0&lng=20.0` (without range parameter) and verifying that announcements within 5km are returned. Delivers value by providing a reasonable default search area.

**Acceptance Scenarios**:

1. **Given** a user wants to search near a location, **When** they provide only lat and lng parameters without range, **Then** the system automatically applies a 5km radius and returns matching announcements
2. **Given** there are announcements at 3km and 7km from the specified coordinates, **When** a user searches without specifying range, **Then** only the announcement at 3km is returned (within the 5km default)

---

### User Story 3 - Validation of Coordinate Parameters (Priority: P3)

The system validates that latitude and longitude are provided together as a pair. Users cannot provide only one coordinate without the other, ensuring data integrity and preventing meaningless queries.

**Why this priority**: While important for data validation and user guidance, this is defensive functionality that prevents incorrect usage rather than enabling core features. It ensures the API is used correctly.

**Independent Test**: Can be fully tested by making requests with incomplete coordinate pairs (e.g., `/api/v1/announcements?lat=50.0` or `/api/v1/announcements?lng=20.0`) and verifying that HTTP 400 errors are returned with clear error messages.

**Acceptance Scenarios**:

1. **Given** a user attempts to filter by location, **When** they provide only the lat parameter without lng, **Then** the system returns HTTP 400 with an error message indicating that lng is required when lat is provided
2. **Given** a user attempts to filter by location, **When** they provide only the lng parameter without lat, **Then** the system returns HTTP 400 with an error message indicating that lat is required when lng is provided
3. **Given** a user makes a request without any location parameters, **When** the request is processed, **Then** all announcements are returned (no filtering applied)

---

### Edge Cases

- What happens when the range parameter is provided but lat/lng are not? (System ignores the range parameter and returns all announcements as if no filtering was requested)
- What happens when range is set to 0? (System validates and returns HTTP 400 as zero is not a valid search radius)
- What happens when lat/lng values are out of valid ranges (lat: -90 to 90, lng: -180 to 180)? (System should validate and return HTTP 400)
- What happens when range is negative? (System should validate and return HTTP 400)
- What happens when all announcements are very far from the search coordinates? (Return empty array with 200 OK)
- What happens when range contains decimals? (System should reject with HTTP 400 because only integer kilometers are allowed)

## Requirements

### Functional Requirements

- **FR-001**: The `/api/v1/announcements` endpoint MUST accept optional query parameters: `lat` (latitude), `lng` (longitude), and `range` (radius in kilometers)
- **FR-002**: When `lat` and `lng` parameters are provided, the system MUST return only announcements that are within the specified or default radius from the given coordinates
- **FR-003**: The system MUST validate that if `lat` is provided, `lng` must also be provided, and vice versa
- **FR-004**: When `lat`/`lng` coordinate pair validation fails, the system MUST return HTTP 400 status with a descriptive error message
- **FR-005**: When `lat` and `lng` are provided but `range` is not, the system MUST default to a 5 kilometer radius
- **FR-006**: The system MUST calculate distances between coordinates and announcement locations using the Haversine formula (or equivalent geodetic distance calculation)
- **FR-007**: The system MUST validate that `lat` is between -90 and 90, and `lng` is between -180 and 180
- **FR-008**: The system MUST validate that `range` (when provided) is a positive integer greater than zero; non-integer inputs MUST return HTTP 400
- **FR-009**: When no location parameters are provided, the system MUST return all announcements (existing behavior preserved)
- **FR-010**: When `range` is provided without `lat`/`lng` coordinates, the system MUST ignore the range parameter and return all announcements
- **FR-011**: The response format MUST remain unchanged - return only announcement data without adding distance information to the response payload
- **FR-012**: Applying location filtering MUST preserve the existing default announcement ordering (e.g., newest-first), without re-sorting by distance

### Key Entities

- **Announcement**: Represents a pet announcement with mandatory location data (latitude, longitude coordinates) and other announcement details. All announcements in the system have location coordinates, which are used for distance-based filtering.
- **Geographic Coordinates**: Latitude and longitude pair representing a point on Earth's surface, used for both the search origin and announcement locations.

## Success Criteria

### Measurable Outcomes

- **SC-001**: Users can successfully retrieve announcements within a specified radius (e.g., 10km) of given coordinates
- **SC-002**: When users provide coordinates without a radius, the system automatically applies a 5km default and returns relevant results
- **SC-003**: Invalid requests (missing lat or lng in a pair) return HTTP 400 error within 100ms
- **SC-004**: Distance calculations are accurate within 1% error margin for typical distances (1-100km range)
- **SC-005**: The endpoint maintains backward compatibility - requests without location parameters continue to return all announcements as before
- **SC-006**: All validation errors include clear, actionable error messages describing what parameter is missing or invalid
