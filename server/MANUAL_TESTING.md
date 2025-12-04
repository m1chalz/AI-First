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

---

## Photo Upload Tests (POST /api/v1/announcements/:id/photos)

### Setup: Create an announcement to test photo upload

First, create an announcement that we'll use for photo upload testing:

```bash
# Create a test announcement (no photoUrl in body!)
curl -X POST http://localhost:3000/api/v1/announcements \
  -H "Content-Type: application/json" \
  -d '{
    "species": "Golden Retriever",
    "sex": "MALE",
    "lastSeenDate": "2025-11-20",
    "status": "MISSING",
    "locationLatitude": 40.785091,
    "locationLongitude": -73.968285,
    "email": "john@example.com"
  }' | jq '.id, .managementPassword'
```

Save the returned `id` and `managementPassword` for the tests below.

### T080: Test successful photo upload with valid credentials

Upload a valid image (JPEG) to an announcement:

```bash
# Set variables (replace with actual values from creation response)
ANNOUNCEMENT_ID="abc123-def456-ghi789"
MANAGEMENT_PASSWORD="secret123"

# Create Basic auth credentials: base64(announcementId:managementPassword)
CREDENTIALS=$(echo -n "${ANNOUNCEMENT_ID}:${MANAGEMENT_PASSWORD}" | base64)

# Upload a photo
curl -X POST "http://localhost:3000/api/v1/announcements/${ANNOUNCEMENT_ID}/photos" \
  -H "Authorization: Basic ${CREDENTIALS}" \
  -F "photo=@/path/to/image.jpg"
```

**Expected**: HTTP 201 (empty response body)

**Verify**: Photo file created in `public/images/${ANNOUNCEMENT_ID}.jpeg`

### T081: Test upload with invalid credentials (403)

```bash
ANNOUNCEMENT_ID="abc123-def456-ghi789"

# Create wrong credentials
WRONG_CREDENTIALS=$(echo -n "${ANNOUNCEMENT_ID}:wrongpassword" | base64)

curl -X POST "http://localhost:3000/api/v1/announcements/${ANNOUNCEMENT_ID}/photos" \
  -H "Authorization: Basic ${WRONG_CREDENTIALS}" \
  -F "photo=@/path/to/image.jpg"
```

**Expected**: HTTP 403 with error:
```json
{
  "error": {
    "code": "UNAUTHORIZED",
    "message": "Invalid credentials for this announcement"
  }
}
```

### T082: Test upload without Authorization header (401)

```bash
ANNOUNCEMENT_ID="abc123-def456-ghi789"

# Attempt upload without Authorization header
curl -X POST "http://localhost:3000/api/v1/announcements/${ANNOUNCEMENT_ID}/photos" \
  -F "photo=@/path/to/image.jpg"
```

**Expected**: HTTP 401 with error:
```json
{
  "error": {
    "code": "UNAUTHENTICATED",
    "message": "Authorization header is required"
  }
}
```

### T083: Test upload to non-existent announcement (404)

```bash
# Use a non-existent announcement ID
FAKE_ID="ffffffff-ffff-ffff-ffff-ffffffffffff"
CREDENTIALS=$(echo -n "${FAKE_ID}:anypassword" | base64)

curl -X POST "http://localhost:3000/api/v1/announcements/${FAKE_ID}/photos" \
  -H "Authorization: Basic ${CREDENTIALS}" \
  -F "photo=@/path/to/image.jpg"
```

**Expected**: HTTP 404 with error:
```json
{
  "error": {
    "code": "NOT_FOUND",
    "message": "Announcement with ID ... not found"
  }
}
```

### T084: Test upload with invalid image format (400)

```bash
ANNOUNCEMENT_ID="abc123-def456-ghi789"
MANAGEMENT_PASSWORD="secret123"
CREDENTIALS=$(echo -n "${ANNOUNCEMENT_ID}:${MANAGEMENT_PASSWORD}" | base64)

# Try uploading a text file disguised as image
echo "not an image" > /tmp/fake.jpg

curl -X POST "http://localhost:3000/api/v1/announcements/${ANNOUNCEMENT_ID}/photos" \
  -H "Authorization: Basic ${CREDENTIALS}" \
  -F "photo=@/tmp/fake.jpg"
```

**Expected**: HTTP 400 with error:
```json
{
  "error": {
    "code": "INVALID_FILE_FORMAT",
    "message": "Invalid image format. Supported formats: JPEG, PNG, GIF, WebP, BMP, TIFF, HEIC, HEIF"
  }
}
```

### T085: Test upload with file > 20 MB (413)

```bash
ANNOUNCEMENT_ID="abc123-def456-ghi789"
MANAGEMENT_PASSWORD="secret123"
CREDENTIALS=$(echo -n "${ANNOUNCEMENT_ID}:${MANAGEMENT_PASSWORD}" | base64)

# Create a 21 MB file
dd if=/dev/zero bs=1M count=21 of=/tmp/large.jpg

curl -X POST "http://localhost:3000/api/v1/announcements/${ANNOUNCEMENT_ID}/photos" \
  -H "Authorization: Basic ${CREDENTIALS}" \
  -F "photo=@/tmp/large.jpg"
```

**Expected**: HTTP 413 with error:
```json
{
  "error": {
    "code": "PAYLOAD_TOO_LARGE",
    "message": "Payload too large"
  }
}
```

### T086: Test upload without photo field (400)

```bash
ANNOUNCEMENT_ID="abc123-def456-ghi789"
MANAGEMENT_PASSWORD="secret123"
CREDENTIALS=$(echo -n "${ANNOUNCEMENT_ID}:${MANAGEMENT_PASSWORD}" | base64)

# Send form data without photo field
curl -X POST "http://localhost:3000/api/v1/announcements/${ANNOUNCEMENT_ID}/photos" \
  -H "Authorization: Basic ${CREDENTIALS}" \
  -F "name=test"
```

**Expected**: HTTP 400 with error:
```json
{
  "error": {
    "code": "MISSING_FILE",
    "message": "Photo field is required"
  }
}
```

### T087: Test photo replacement workflow

```bash
ANNOUNCEMENT_ID="abc123-def456-ghi789"
MANAGEMENT_PASSWORD="secret123"
CREDENTIALS=$(echo -n "${ANNOUNCEMENT_ID}:${MANAGEMENT_PASSWORD}" | base64)

# Upload first photo
echo "First photo upload..."
curl -X POST "http://localhost:3000/api/v1/announcements/${ANNOUNCEMENT_ID}/photos" \
  -H "Authorization: Basic ${CREDENTIALS}" \
  -F "photo=@/path/to/image1.jpg"

# Verify first photo exists
ls -lh public/images/${ANNOUNCEMENT_ID}.*

# Upload second photo (different format)
echo "Second photo upload..."
curl -X POST "http://localhost:3000/api/v1/announcements/${ANNOUNCEMENT_ID}/photos" \
  -H "Authorization: Basic ${CREDENTIALS}" \
  -F "photo=@/path/to/image2.png"

# Verify: should have replaced previous photo
ls -lh public/images/${ANNOUNCEMENT_ID}.*
```

**Expected**: 
- First request returns 201
- Second request returns 201
- Only ONE file exists in `public/images/` with the announcement ID (new format)
- Old photo file is deleted

### T088: Test photoUrl rejection in announcement creation

Attempt to include `photoUrl` field in announcement creation:

```bash
curl -X POST http://localhost:3000/api/v1/announcements \
  -H "Content-Type: application/json" \
  -d '{
    "species": "Golden Retriever",
    "sex": "MALE",
    "lastSeenDate": "2025-11-20",
    "status": "MISSING",
    "locationLatitude": 40.785091,
    "locationLongitude": -73.968285,
    "email": "john@example.com",
    "photoUrl": "https://example.com/photo.jpg"
  }'
```

**Expected**: HTTP 400 with error:
```json
{
  "error": {
    "code": "INVALID_FIELD",
    "message": "photoUrl is not a valid field",
    "field": "photoUrl"
  }
}
```

### Helper Script: Generate Base64 Credentials

Create a helper script to generate credentials easily:

```bash
#!/bin/bash
# save as: scripts/get-auth-header.sh

ANNOUNCEMENT_ID=$1
MANAGEMENT_PASSWORD=$2

if [ -z "$ANNOUNCEMENT_ID" ] || [ -z "$MANAGEMENT_PASSWORD" ]; then
  echo "Usage: ./get-auth-header.sh <announcement-id> <password>"
  exit 1
fi

CREDENTIALS=$(echo -n "${ANNOUNCEMENT_ID}:${MANAGEMENT_PASSWORD}" | base64)
echo "Authorization: Basic ${CREDENTIALS}"
```

Usage:
```bash
chmod +x scripts/get-auth-header.sh
./scripts/get-auth-header.sh abc123-def456 secret123
```

---

## Admin Delete Announcement Tests (DELETE /api/admin/v1/announcements/:id)

**⚠️ ADMIN ONLY**: This endpoint requires admin token for internal use only.

### Setup: Create an announcement to delete

First, create an announcement that we'll use for deletion testing:

```bash
curl -X POST http://localhost:3000/api/v1/announcements \
  -H "Content-Type: application/json" \
  -d '{
    "species": "Golden Retriever",
    "sex": "MALE",
    "lastSeenDate": "2025-11-20",
    "status": "MISSING",
    "locationLatitude": 40.785091,
    "locationLongitude": -73.968285,
    "email": "john@example.com"
  }' | jq '.id'
```

Save the returned `id` for the tests below.

### T089: Test successful announcement deletion with admin token (and photos)

Delete an announcement using the admin token. This will also delete any associated photo files:

```bash
ANNOUNCEMENT_ID="<id-from-above>"

curl -X DELETE "http://localhost:3000/api/admin/v1/announcements/${ANNOUNCEMENT_ID}" \
  -H "Authorization: tajnehasloadmina"
```

**Expected**: HTTP 204 (No Content - empty response body)

**Verify**: Announcement and photos are deleted:
```bash
# Announcement should return 404
curl -X GET "http://localhost:3000/api/v1/announcements/${ANNOUNCEMENT_ID}"

# Photo files should be deleted from public/images/
ls -la public/images/${ANNOUNCEMENT_ID}.*
# Should return: "No such file or directory"
```

### T090: Test deletion without Authorization header (401)

```bash
ANNOUNCEMENT_ID="<id-to-delete>"

curl -X DELETE "http://localhost:3000/api/admin/v1/announcements/${ANNOUNCEMENT_ID}"
```

**Expected**: HTTP 401 with error:
```json
{
  "error": {
    "requestId": "...",
    "code": "UNAUTHENTICATED",
    "message": "Missing Authorization header"
  }
}
```

### T091: Test deletion with invalid admin token (401)

```bash
ANNOUNCEMENT_ID="<id-to-delete>"

curl -X DELETE "http://localhost:3000/api/admin/v1/announcements/${ANNOUNCEMENT_ID}" \
  -H "Authorization: wrongtoken"
```

**Expected**: HTTP 401 with error:
```json
{
  "error": {
    "requestId": "...",
    "code": "UNAUTHENTICATED",
    "message": "Invalid admin token"
  }
}
```

### T092: Test deletion of non-existent announcement (404)

```bash
FAKE_ID="ffffffff-ffff-ffff-ffff-ffffffffffff"
ADMIN_TOKEN="tajnehasloadmina"

curl -X DELETE "http://localhost:3000/api/admin/v1/announcements/${FAKE_ID}" \
  -H "Authorization: ${ADMIN_TOKEN}"
```

**Expected**: HTTP 404 with error:
```json
{
  "error": {
    "requestId": "...",
    "code": "NOT_FOUND",
    "message": "Resource not found"
  }
}
```

### T093: Test that deletion only removes the specified announcement

Create two announcements and delete only one:

```bash
# Create first announcement
ID1=$(curl -s -X POST http://localhost:3000/api/v1/announcements \
  -H "Content-Type: application/json" \
  -d '{
    "species": "Golden Retriever",
    "sex": "MALE",
    "lastSeenDate": "2025-11-20",
    "status": "MISSING",
    "locationLatitude": 40.785091,
    "locationLongitude": -73.968285,
    "email": "john@example.com"
  }' | jq -r '.id')

# Create second announcement
ID2=$(curl -s -X POST http://localhost:3000/api/v1/announcements \
  -H "Content-Type: application/json" \
  -d '{
    "species": "Siamese Cat",
    "sex": "FEMALE",
    "lastSeenDate": "2025-11-19",
    "status": "FOUND",
    "locationLatitude": 51.5074,
    "locationLongitude": -0.1278,
    "phone": "+44 20 7946 0958"
  }' | jq -r '.id')

# Delete first announcement
ADMIN_TOKEN="tajnehasloadmina"
curl -X DELETE "http://localhost:3000/api/admin/v1/announcements/${ID1}" \
  -H "Authorization: ${ADMIN_TOKEN}"

# Verify first is deleted (404)
echo "First announcement (should be 404):"
curl -X GET "http://localhost:3000/api/v1/announcements/${ID1}"

# Verify second still exists (200)
echo "Second announcement (should be 200):"
curl -X GET "http://localhost:3000/api/v1/announcements/${ID2}"
```

**Expected**:
- Delete request returns HTTP 204
- First announcement returns HTTP 404
- Second announcement returns HTTP 200 with data

## Verification Checklist

### Announcement Creation Tests
- [ ] T075: Email contact announcement created successfully
- [ ] T076: Phone contact announcement created successfully
- [ ] T077: Validation error returned for missing field
- [ ] T078: Duplicate microchip returns HTTP 409
- [ ] T079: HTML tags stripped from text fields

### Photo Upload Tests
- [ ] T080: Photo uploaded successfully with valid credentials (201)
- [ ] T081: Invalid credentials rejected (403)
- [ ] T082: Missing Authorization header rejected (401)
- [ ] T083: Non-existent announcement returns 404
- [ ] T084: Invalid image format rejected (400)
- [ ] T085: Files > 20 MB rejected (413)
- [ ] T086: Missing photo field rejected (400)
- [ ] T087: Photo replacement works correctly
- [ ] T088: photoUrl field rejected in creation (400)

### Admin Delete Announcement Tests
- [ ] T089: Announcement deleted successfully with admin token (204)
- [ ] T090: Missing Authorization header rejected (401)
- [ ] T091: Invalid admin token rejected (401)
- [ ] T092: Non-existent announcement returns 404
- [ ] T093: Only specified announcement is deleted (others remain)

