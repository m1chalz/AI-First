# Contracts: Android Animal List Screen Layout Update

This feature does **not** introduce new or changed backend/API contracts.

## Scope

- The Android Animal List screen continues to rely on existing data sources and ViewModel state.
- All REST API endpoints and backend contracts defined in other specs (e.g., `006-pets-api`) remain the single source of truth.
- No new request/response shapes, routes, or query parameters are added as part of this UI-only change.

## Data Requirements

The existing Animal entity already provides all fields needed for the new card layout:

| UI Element | Source Field | Notes |
|------------|--------------|-------|
| Pet photo | `photoUrl` | Falls back to initial placeholder |
| Location | `location.city` | Displayed with location icon |
| Distance | `location.radiusKm` | Displayed as "{radiusKm} km" |
| Species | `species.displayName` | e.g., "Dog", "Cat" |
| Breed | `breed` | e.g., "Golden Retriever" |
| Status | `status` | ACTIVE → "MISSING", FOUND → "FOUND" |
| Date | `lastSeenDate` | Format: DD/MM/YYYY |

## API Contract Status

No changes required to:
- `GET /api/pets` - Returns list of pets (unchanged)
- `GET /api/pets/:id` - Returns pet details (unchanged)
- Pet entity schema - All required fields already present
