# No API Contracts

This feature does not require backend API changes. All data is already available from the existing announcement fetch endpoint.

**Existing Endpoint Used**: `GET /api/announcements`

The announcement response already includes all fields needed for the annotation callout:
- name, photoUrl, species, breed
- coordinate (latitude, longitude)
- lastSeenDate, status
- email, phone, description
