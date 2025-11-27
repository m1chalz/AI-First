# Quick Start: Pet Details Screen (Web UI)

**Feature**: 027-web-pet-details-screen  
**Date**: 2025-11-27

## Overview

This guide provides a quick start for implementing the pet details modal feature. The implementation starts with updating the animal list page to match the Figma design, then implements the pet details modal.

## Prerequisites

- Node.js v18+ installed
- npm or yarn package manager
- Access to backend API (`GET /api/v1/announcements/:id`)
- Figma design access (node-id=168-4656 for list, node-id=168-4985 for modal)

## Implementation Order

### Phase 1: Update Animal List Page (START HERE)

**Goal**: Update existing animal list page to match Figma design (node-id=168-4656)

1. **Update Layout Structure**:
   - Add left sidebar component (219px width, #4F3C4C background)
   - Update main content area (1181px width)
   - Ensure total width is 1440px

2. **Update Header**:
   - Left: "PetSpot" title (Hind Regular, 24px, #2D2D2D)
   - Right: "Report a Missing Animal" button (Primary blue #155DFC, 44px height)

3. **Update Animal Cards**:
   - Update card structure: Left (192px photo), Middle (~792px info), Right (144px status/date/button)
   - Add location coordinates display (format: "52.2297° N, 21.0122° E")
   - Add "Details" button (outlined blue, 127px × 44px, border-radius 10px)
   - Update card styles to match design (1180px width, 136px height, 14px border-radius)

4. **Update Data Model**:
   - Add `location.latitude` and `location.longitude` to `Animal` interface
   - Update `lastSeenDate` to ISO 8601 format

**Note**: Filters and search functionality will be implemented in a separate specification.

### Phase 2: Implement Pet Details Modal

**Goal**: Implement pet details modal that opens from "Details" button

1. **Create Modal Component Structure**:
   ```
   webApp/src/components/PetDetailsModal/
   ├── PetDetailsModal.tsx          # Main modal (state host)
   ├── PetDetailsContent.tsx        # Stateless content component
   ├── PetDetailsHeader.tsx         # Header with close, date, contacts
   ├── PetHeroImage.tsx             # Hero image with badges
   └── PetDetailsFields.tsx         # Form fields section
   ```

2. **Create Modal Hook**:
   - `useModal.ts`: Manages modal open/close state and selected pet ID
   - `usePetDetails.ts`: Fetches pet details by ID

3. **Create Formatting Utilities**:
   - `date-formatter.ts`: Format ISO date to "MMM DD, YYYY"
   - `coordinate-formatter.ts`: Format lat/lng to "XX.XXXX° N/S, XX.XXXX° E/W"
   - `microchip-formatter.ts`: Format microchip to "000-000-000-000"
   - `map-url-builder.ts`: Build Google Maps/OpenStreetMap URLs

4. **Implement Modal Features**:
   - React Portal for rendering outside DOM hierarchy
   - Focus trap (keyboard navigation cycles within modal)
   - Body scroll lock (prevent background scrolling)
   - ESC key close handler
   - Backdrop click close handler
   - Error state with retry button
   - Loading state with spinner
   - 10-second timeout handling

5. **Create PetDetails Type**:
   - Create `webApp/src/types/pet-details.ts` with `PetDetails` interface
   - Define `PetStatus` and `PetSex` enums matching backend values (MISSING/FOUND/CLOSED, MALE/FEMALE/UNKNOWN)
   - Include all fields from backend API response

6. **Update AnimalRepository**:
   - Replace `getMockAnimals()` with real API call to `GET /api/v1/announcements`
   - Map backend response format to `Animal` type:
     - Map `status`: MISSING→ACTIVE, FOUND→FOUND, CLOSED→CLOSED
     - Map `sex`: MALE→MALE, FEMALE→FEMALE, UNKNOWN→UNKNOWN
     - Map `species`: DOG→DOG, CAT→CAT, BIRD→BIRD, RABBIT→RABBIT, other→OTHER
     - Map `petName` to `name` (use "Unknown" if null)
     - Map `locationLatitude`/`locationLongitude` to `location` object
   - Add `getPetById(id: string): Promise<PetDetails>` method calling `GET /api/v1/announcements/:id`
   - Handle API errors (404, 500, network errors)
   - Use environment variable `VITE_API_BASE_URL` (default: `http://localhost:3000/api/v1`)

7. **Integrate Modal with List**:
   - Update `AnimalList` to manage modal state
   - Wire "Details" button to open modal
   - Pass pet ID to modal component

### Phase 3: Testing

1. **Unit Tests**:
   - Formatting utilities (date, coordinates, microchip)
   - Modal hooks (useModal, usePetDetails)
   - Modal components (PetDetailsModal, PetDetailsContent)

2. **Integration Tests**:
   - Modal opens from list page
   - Pet details fetch and display
   - Error handling and retry
   - Modal close (ESC, backdrop, close button)

3. **E2E Tests**:
   - User clicks "Details" button → modal opens
   - User views pet details → all fields display correctly
   - User closes modal → returns to list

## Key Files to Create/Update

### New Files

```
webApp/src/
├── components/
│   ├── AnimalList/
│   │   ├── Sidebar.tsx                 # NEW
│   │   └── Sidebar.module.css          # NEW
│   │
│   └── PetDetailsModal/                # NEW directory
│       ├── PetDetailsModal.tsx
│       ├── PetDetailsModal.module.css
│       ├── PetDetailsContent.tsx
│       ├── PetDetailsContent.module.css
│       ├── PetDetailsHeader.tsx
│       ├── PetDetailsHeader.module.css
│       ├── PetHeroImage.tsx
│       ├── PetHeroImage.module.css
│       ├── PetDetailsFields.tsx
│       └── PetDetailsFields.module.css
│
├── hooks/
│   ├── use-pet-details.ts              # NEW
│   └── use-modal.ts                    # NEW
│
├── utils/
│   ├── date-formatter.ts               # NEW
│   ├── coordinate-formatter.ts         # NEW
│   ├── microchip-formatter.ts          # NEW
│   └── map-url-builder.ts              # NEW
│
└── types/
    └── pet-details.ts                  # NEW
```

### Updated Files

```
webApp/src/
├── components/
│   └── AnimalList/
│       ├── AnimalList.tsx              # UPDATE: Add sidebar, filters, modal integration
│       ├── AnimalList.module.css       # UPDATE: New layout styles
│       ├── AnimalCard.tsx              # UPDATE: New card structure, Details button
│       └── AnimalCard.module.css       # UPDATE: New card styles
│
├── hooks/
│   └── use-animal-list.ts              # UPDATE: May need updates for new list structure
│
├── services/
│   └── animal-repository.ts            # UPDATE: Replace mock data with backend API calls, add getPetById method, map backend response to Animal/PetDetails types
│
└── types/
    ├── animal.ts                       # UPDATE: Change Location interface from {city, radiusKm} to {latitude?, longitude?}
    └── pet-details.ts                  # NEW: PetDetails type definition
```

## Development Commands

```bash
# Install dependencies (if needed)
cd webApp
npm install

# Run development server
npm run start

# Run tests
npm test

# Run tests with coverage
npm test -- --coverage

# Lint code
npm run lint

# Build for production
npm run build
```

## Testing Checklist

- [ ] Animal list page matches Figma design (node-id=168-4656)
- [ ] Sidebar displays correctly (219px width, #4F3C4C background)
- [ ] Animal cards display correctly (new structure with coordinates)
- [ ] Animal list loads data from backend API (GET /api/v1/announcements)
- [ ] Location coordinates display correctly on cards (latitude/longitude format)
- [ ] "Details" button opens modal
- [ ] Modal displays pet details correctly
- [ ] Modal closes via ESC key, backdrop click, or close button
- [ ] Focus traps within modal when open
- [ ] Body scroll locks when modal is open
- [ ] Error state displays with retry button
- [ ] Loading state displays spinner
- [ ] Timeout after 10 seconds shows error
- [ ] Date formatting works correctly ("Nov 18, 2025")
- [ ] Coordinate formatting works correctly ("52.2297° N, 21.0122° E")
- [ ] Microchip formatting works correctly ("000-000-000-000")
- [ ] "Show on the map" button opens external map
- [ ] All test identifiers present (`data-testid` attributes)
- [ ] Unit tests achieve 80% coverage
- [ ] E2E tests pass

## Design References

- **Animal List Page**: https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=168-4656&m=dev
- **Pet Details Modal**: https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=168-4985&m=dev

## Next Steps

1. Start with Phase 1: Update animal list page to match design
2. Implement Phase 2: Create pet details modal
3. Write tests (Phase 3)
4. Run E2E tests to verify end-to-end flow
5. Review and refine based on feedback

## Notes

- Backend API endpoints are already implemented:
  - `GET /api/v1/announcements` - Returns list of all announcements
  - `GET /api/v1/announcements/:id` - Returns single announcement details
- AnimalRepository must be updated to call backend API instead of using mock data
- Status mapping: Backend uses MISSING/FOUND/CLOSED, frontend uses ACTIVE/FOUND/CLOSED (MISSING→ACTIVE)
- Location structure changed from `{city, radiusKm}` to `{latitude?, longitude?}` to match backend
- Modal state management uses React state (useState), not URL query parameters
- All formatting happens in components (date, coordinates, microchip)
- Error handling uses generic message with retry button
- External map integration opens Google Maps/OpenStreetMap in new tab
- API base URL configured via `VITE_API_BASE_URL` environment variable (default: `http://localhost:3000/api/v1`)

