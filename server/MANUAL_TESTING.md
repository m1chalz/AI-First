# Manual Testing Guide for POST /api/v1/announcements

## Prerequisites

1. Start the development server:
   ```bash
   npm run dev
   ```

2. Server should be running on `http://localhost:3000`

## Test Cases

### T075: Test successful announcement creation with email contact

```bash
curl -X POST http://localhost:3000/api/v1/announcements \
  -H "Content-Type: application/json" \
  -d '{
    "species": "Golden Retriever",
    "sex": "MALE",
    "lastSeenDate": "2025-11-20",
    "photoUrl": "https://example.com/photo.jpg",
    "status": "MISSING",
    "locationLatitude": 40.785091,
    "locationLongitude": -73.968285,
    "email": "john@example.com"
  }'
```

**Expected**: HTTP 201 with announcement data including `managementPassword` (6-digit number)

### T076: Test successful announcement creation with phone contact

```bash
curl -X POST http://localhost:3000/api/v1/announcements \
  -H "Content-Type: application/json" \
  -d '{
    "species": "Siamese Cat",
    "sex": "FEMALE",
    "lastSeenDate": "2025-11-19",
    "photoUrl": "https://example.com/cat.jpg",
    "status": "FOUND",
    "locationLatitude": 51.5074,
    "locationLongitude": -0.1278,
    "phone": "+44 20 7946 0958"
  }'
```

**Expected**: HTTP 201 with announcement data including `managementPassword`

### T077: Test validation error with missing required field

```bash
curl -X POST http://localhost:3000/api/v1/announcements \
  -H "Content-Type: application/json" \
  -d '{
    "sex": "MALE",
    "lastSeenDate": "2025-11-20",
    "photoUrl": "https://example.com/photo.jpg",
    "status": "MISSING",
    "locationLatitude": 40.785091,
    "locationLongitude": -73.968285,
    "email": "john@example.com"
  }'
```

**Expected**: HTTP 400 with error:
```json
{
  "error": {
    "code": "MISSING_VALUE",
    "message": "cannot be empty",
    "field": "species"
  }
}
```

### T078: Test duplicate microchip with HTTP 409

```bash
# First, create an announcement with microchip
curl -X POST http://localhost:3000/api/v1/announcements \
  -H "Content-Type: application/json" \
  -d '{
    "species": "Golden Retriever",
    "sex": "MALE",
    "lastSeenDate": "2025-11-20",
    "photoUrl": "https://example.com/photo1.jpg",
    "status": "MISSING",
    "locationLatitude": 40.785091,
    "locationLongitude": -73.968285,
    "email": "john@example.com",
    "microchipNumber": "123456789012345"
  }'

# Then, attempt to create duplicate with same microchip
curl -X POST http://localhost:3000/api/v1/announcements \
  -H "Content-Type: application/json" \
  -d '{
    "species": "Labrador",
    "sex": "FEMALE",
    "lastSeenDate": "2025-11-21",
    "photoUrl": "https://example.com/photo2.jpg",
    "status": "FOUND",
    "locationLatitude": 40.785091,
    "locationLongitude": -73.968285,
    "email": "jane@example.com",
    "microchipNumber": "123456789012345"
  }'
```

**Expected**: HTTP 409 with error:
```json
{
  "error": {
    "code": "CONFLICT",
    "message": "An entity with this value already exists",
    "field": "microchipNumber"
  }
}
```

### T079: Test XSS prevention with HTML tags in description

```bash
curl -X POST http://localhost:3000/api/v1/announcements \
  -H "Content-Type: application/json" \
  -d '{
    "species": "Golden Retriever",
    "sex": "MALE",
    "description": "<script>alert(\"XSS\")</script>Friendly dog",
    "petName": "<div>Buddy</div>",
    "lastSeenDate": "2025-11-20",
    "photoUrl": "https://example.com/photo.jpg",
    "status": "MISSING",
    "locationLatitude": 40.785091,
    "locationLongitude": -73.968285,
    "email": "john@example.com"
  }'
```

**Expected**: HTTP 201 with sanitized text fields:
- `description`: "Friendly dog" (HTML tags removed)
- `petName`: "Buddy" (HTML tags removed)
- `species`: "Golden Retriever" (no HTML tags)

## Verification Checklist

- [ ] T075: Email contact announcement created successfully
- [ ] T076: Phone contact announcement created successfully
- [ ] T077: Validation error returned for missing field
- [ ] T078: Duplicate microchip returns HTTP 409
- [ ] T079: HTML tags stripped from text fields

