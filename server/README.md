## Installing dependencies

`npm install`

## Running development server

`npm start`

The server is active on [localhost:3000](localhost:3000)

## Environment variables

Name | Description                         | Default value
-----|-------------------------------------|---------------
PORT | Port on which the server is exposed | 3000

## API Endpoints

### GET `/api/v1/announcements`

Retrieves all pet announcements. Optionally filter by location using geographic coordinates and radius.

**Query Parameters (all optional):**
- `lat` (number): Latitude coordinate (-90 to 90). Must be provided with `lng`.
- `lng` (number): Longitude coordinate (-180 to 180). Must be provided with `lat`.
- `range` (integer): Search radius in kilometers (positive integer). Defaults to 5km if `lat`/`lng` provided without `range`. Ignored if `lat`/`lng` not provided.

**Examples:**
- `GET /api/v1/announcements` - Returns all announcements
- `GET /api/v1/announcements?lat=50.0614&lng=19.9383` - Returns announcements within 5km (default) of coordinates
- `GET /api/v1/announcements?lat=50.0614&lng=19.9383&range=10` - Returns announcements within 10km of coordinates

**Response (200 OK):**
```json
{
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "petName": "Max",
      "species": "Golden Retriever",
      "breed": "Purebred",
      "sex": "MALE",
      "age": 5,
      "description": "Friendly dog with brown fur",
      "microchipNumber": "123456789012345",
      "locationLatitude": 40.785091,
      "locationLongitude": -73.968285,
      "lastSeenDate": "2025-11-20",
      "email": "john@example.com",
      "phone": "+1 555 123 4567",
      "photoUrl": "https://example.com/photo.jpg",
      "status": "MISSING",
      "reward": "500 USD",
      "createdAt": "2025-11-24T12:34:56.789Z",
      "updatedAt": "2025-11-24T12:34:56.789Z"
    }
  ]
}
```

**Note:** Returns empty array `{"data": []}` if no announcements exist or no announcements match the location filter.

**Error Responses:**

- **400 Bad Request**: Invalid location parameters
  ```json
  {
    "error": {
      "code": "INVALID_PARAMETER",
      "message": "Parameter 'lng' is required when 'lat' is provided",
      "field": "lng"
    }
  }
  ```
  
  **Common validation errors:**
  - `lat` or `lng` must be valid numbers
  - `lat` must be between -90 and 90
  - `lng` must be between -180 and 180
  - Both `lat` and `lng` must be provided together (coordinate pair)
  - `range` must be a positive integer
  - `range` must be greater than zero

- **500 Internal Server Error**: Server error
  ```json
  {
    "error": {
      "code": "INTERNAL_SERVER_ERROR",
      "message": "Internal server error"
    }
  }
  ```

---

### GET `/api/v1/announcements/:id`

Retrieves a single pet announcement by its ID.

**Path Parameters:**
- `id` (string): UUID of the announcement

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "petName": "Max",
  "species": "Golden Retriever",
  "breed": "Purebred",
  "sex": "MALE",
  "age": 5,
  "description": "Friendly dog with brown fur",
  "microchipNumber": "123456789012345",
  "locationLatitude": 40.785091,
  "locationLongitude": -73.968285,
  "lastSeenDate": "2025-11-20",
  "email": "john@example.com",
  "phone": "+1 555 123 4567",
  "photoUrl": "https://example.com/photo.jpg",
  "status": "MISSING",
  "reward": "500 USD",
  "createdAt": "2025-11-24T12:34:56.789Z",
  "updatedAt": "2025-11-24T12:34:56.789Z"
}
```

**Note:** 
- Optional fields may be `null` if not provided
- `managementPassword` is **never** included in GET responses (security)

**Error Responses:**

- **404 Not Found**: Announcement with given ID does not exist
  ```json
  {
    "error": {
      "code": "NOT_FOUND",
      "message": "Resource not found"
    }
  }
  ```

- **500 Internal Server Error**: Server error
  ```json
  {
    "error": {
      "code": "INTERNAL_SERVER_ERROR",
      "message": "Internal server error"
    }
  }
  ```

---

### POST `/api/v1/announcements`

Creates a new pet announcement (lost or found pet listing).

**Request Body:**
```json
{
  "species": "Golden Retriever",
  "sex": "MALE",
  "lastSeenDate": "2025-11-20",
  "photoUrl": "https://example.com/photo.jpg",
  "status": "MISSING",
  "locationLatitude": 40.785091,
  "locationLongitude": -73.968285,
  "email": "john@example.com"
}
```

**Required Fields:**
- `species` (string): Pet species
- `sex` (string): Pet sex
- `lastSeenDate` (string): ISO 8601 date (YYYY-MM-DD), must be today or past
- `photoUrl` (string): Valid URL with http or https protocol
- `status` (string): Either "MISSING" or "FOUND"
- `locationLatitude` (number): Decimal between -90 and 90
- `locationLongitude` (number): Decimal between -180 and 180
- At least one of: `email` (string) OR `phone` (string)

**Optional Fields:**
- `petName` (string)
- `breed` (string)
- `age` (number): Positive integer
- `description` (string)
- `microchipNumber` (string): Numeric only, must be unique
- `reward` (string)

**Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "species": "Golden Retriever",
  "sex": "MALE",
  "lastSeenDate": "2025-11-20",
  "photoUrl": "https://example.com/photo.jpg",
  "status": "MISSING",
  "locationLatitude": 40.785091,
  "locationLongitude": -73.968285,
  "email": "john@example.com",
  "managementPassword": "847362",
  "createdAt": "2025-11-24T12:34:56.789Z"
}
```

**Error Responses:**

- **400 Bad Request**: Validation error
  ```json
  {
    "error": {
      "code": "MISSING_VALUE",
      "message": "cannot be empty",
      "field": "species"
    }
  }
  ```

- **409 Conflict**: Duplicate microchip number
  ```json
  {
    "error": {
      "code": "CONFLICT",
      "message": "An entity with this value already exists",
      "field": "microchipNumber"
    }
  }
  ```

- **413 Payload Too Large**: Request body exceeds 10MB limit
  ```json
  {
    "error": {
      "code": "PAYLOAD_TOO_LARGE",
      "message": "Request payload exceeds maximum size limit"
    }
  }
  ```

**Validation:**
- Fail-fast validation (returns first error only)
- All text fields are sanitized to prevent XSS attacks
- Unknown fields are rejected
- Management password is generated automatically (6-digit numeric)
