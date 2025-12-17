# Data Model: Web Application Landing Page

**Feature**: 061-web-landing-page  
**Date**: 2025-12-17

## Overview

This document defines the data entities and their relationships for the web application landing page feature. The landing page displays static content (hero section, feature cards, footer) and dynamic content (recently lost pet announcements fetched from backend API).

## Entities

### 1. Announcement (Backend Entity - Reused)

Represents a pet announcement (lost or found). This entity already exists in the backend (`server/src/types/announcement.d.ts`) and is consumed by the frontend.

**TypeScript Definition** (Frontend):

```typescript
interface Announcement {
  id: string;
  petName: string | null;
  species: string;
  breed: string | null;
  sex: string;
  age: number | null;
  description: string | null;
  microchipNumber: string | null;
  locationLatitude: number;
  locationLongitude: number;
  email: string | null;
  phone: string | null;
  photoUrl: string | null;
  lastSeenDate: string; // ISO 8601 date string
  status: 'MISSING' | 'FOUND';
  reward: string | null;
  createdAt: string; // ISO 8601 date string
  updatedAt: string; // ISO 8601 date string
}
```

**Usage on Landing Page**:
- Display up to 5 most recent MISSING announcements
- Show: photo, status badge, location, species/breed, report date
- Filter: `status === 'MISSING'`
- Sort: by `createdAt` descending (newest first)

**Validation Rules** (Applied by backend):
- `id`: UUID v4
- `status`: Must be 'MISSING' or 'FOUND'
- `lastSeenDate`, `createdAt`, `updatedAt`: ISO 8601 date strings
- `locationLatitude`: -90 to 90
- `locationLongitude`: -180 to 180

**State Transitions**: N/A (read-only for landing page)

---

### 2. FeatureCard (Frontend Entity - New)

Represents a feature card in the hero section. These are static, display-only elements.

**TypeScript Definition**:

```typescript
interface FeatureCard {
  id: string; // Unique identifier for React key
  icon: React.ComponentType<{ className?: string; size?: number }>; // react-icons component
  iconColor: string; // Hex color for icon background
  title: string;
  description: string;
}
```

**Data**:

```typescript
const FEATURE_CARDS: FeatureCard[] = [
  {
    id: 'search-database',
    icon: HiOutlineSearch,
    iconColor: '#155DFC', // Blue
    title: 'Search Database',
    description: 'Browse through our comprehensive database of lost and found pets in your area'
  },
  {
    id: 'report-missing',
    icon: HiOutlineExclamation,
    iconColor: '#FB2C36', // Red
    title: 'Report Missing',
    description: 'Report your missing pet and reach thousands of helpful community members'
  },
  {
    id: 'found-pet',
    icon: HiOutlineCheckCircle,
    iconColor: '#10B981', // Green (Emerald)
    title: 'Found a Pet',
    description: 'Found a lost pet? Help reunite them with their worried owner'
  },
  {
    id: 'location-based',
    icon: HiOutlineLocationMarker,
    iconColor: '#7C3AED', // Purple (Violet)
    title: 'Location Based',
    description: 'Get notifications about lost pets in your immediate neighborhood'
  }
];
```

**Usage on Landing Page**:
- Display all 4 cards in hero section
- Cards are purely informational (not interactive)
- Desktop: 4-column horizontal grid
- Tablet: 2x2 grid

**Validation Rules**: N/A (static data)

**State Transitions**: N/A (static, no state changes)

---

### 3. FooterLink (Frontend Entity - New)

Represents a navigation or legal link in the footer.

**TypeScript Definition**:

```typescript
interface FooterLink {
  id: string; // Unique identifier for React key
  label: string;
  url: string; // Internal route or external URL
  icon?: React.ComponentType<{ className?: string; size?: number }>; // Optional icon
  isExternal?: boolean; // True for external links, false for internal routes
  isPlaceholder?: boolean; // True for non-functional placeholder links
}
```

**Data**:

```typescript
// Quick Links
const FOOTER_QUICK_LINKS: FooterLink[] = [
  {
    id: 'report-lost',
    label: 'Report Lost Pet',
    url: '/report-missing/microchip',
    isExternal: false,
    isPlaceholder: false
  },
  {
    id: 'report-found',
    label: 'Report Found Pet',
    url: '#', // Placeholder - no route yet
    isExternal: false,
    isPlaceholder: true
  },
  {
    id: 'search-database',
    label: 'Search Database',
    url: '#', // Placeholder - non-functional per spec
    isExternal: false,
    isPlaceholder: true
  }
];

// Legal Links
const FOOTER_LEGAL_LINKS: FooterLink[] = [
  {
    id: 'privacy',
    label: 'Privacy Policy',
    url: '#', // Placeholder
    isExternal: false,
    isPlaceholder: true
  },
  {
    id: 'terms',
    label: 'Terms of Service',
    url: '#', // Placeholder
    isExternal: false,
    isPlaceholder: true
  },
  {
    id: 'cookies',
    label: 'Cookie Policy',
    url: '#', // Placeholder
    isExternal: false,
    isPlaceholder: true
  }
];

// Contact Information
interface ContactInfo {
  email: string;
  phone: string;
  address: string;
}

const FOOTER_CONTACT: ContactInfo = {
  email: 'contact@petspot.com',
  phone: '1-800-PET-SPOT',
  address: '123 Pet Street, Animal City, AC 12345'
};
```

**Usage on Landing Page**:
- Display in footer columns (Quick Links, Legal Links, Contact)
- Functional links navigate to internal routes or external URLs
- Placeholder links are non-interactive or use `href="#"`

**Validation Rules**: N/A (static data)

**State Transitions**: N/A (static, no state changes)

---

### 4. LandingPageState (Frontend State - New)

Represents the application state for the landing page.

**TypeScript Definition**:

```typescript
interface LandingPageState {
  announcements: Announcement[]; // Recently lost pets (max 5, MISSING only)
  isLoading: boolean;
  error: string | null;
  userLocation: {
    lat: number | null;
    lng: number | null;
  } | null;
}
```

**Initial State**:

```typescript
const initialState: LandingPageState = {
  announcements: [],
  isLoading: true,
  error: null,
  userLocation: null
};
```

**State Transitions**:

1. **Loading**: `isLoading = true` when fetching announcements
2. **Success**: `isLoading = false`, `announcements = data`, `error = null`
3. **Error**: `isLoading = false`, `error = errorMessage`, `announcements = []`
4. **Empty**: `isLoading = false`, `announcements = []`, `error = null` (no MISSING pets)

**Validation Rules**:
- `announcements`: Must be array of valid `Announcement` objects
- `announcements.length`: Maximum 5 items
- All announcements: `status === 'MISSING'`
- `error`: Non-empty string if error occurred, null otherwise

---

## Entity Relationships

```
┌─────────────────────────┐
│   LandingPage           │
│   (React Component)     │
└───────────┬─────────────┘
            │
            │ contains (static)
            ├──────────────────┐
            │                  │
            ▼                  ▼
┌─────────────────┐  ┌──────────────────┐
│  FeatureCard    │  │   FooterLink     │
│  (4 cards)      │  │   (multiple)     │
└─────────────────┘  └──────────────────┘
            │
            │ fetches (dynamic)
            ▼
┌─────────────────────────┐
│  LandingPageState       │
│  - announcements[]      │
│  - isLoading            │
│  - error                │
│  - userLocation         │
└───────────┬─────────────┘
            │
            │ contains
            ▼
┌─────────────────────────┐
│  Announcement           │
│  (max 5, MISSING only)  │
│  - Fetched from backend │
└─────────────────────────┘
```

**Relationships**:
- **LandingPage → FeatureCard**: One-to-many (static, 4 cards)
- **LandingPage → FooterLink**: One-to-many (static, multiple links)
- **LandingPage → LandingPageState**: One-to-one (state management)
- **LandingPageState → Announcement**: One-to-many (max 5, dynamic)

---

## Data Flow

### Fetching Recent Pets

```
User navigates to Landing Page
          ↓
Component mounts
          ↓
Check user geolocation (optional)
          ↓
Call GET /api/v1/announcements?lat={lat}&lng={lng}
          ↓
Backend returns { data: Announcement[] }
          ↓
Frontend filters: status === 'MISSING'
          ↓
Frontend sorts: by createdAt desc
          ↓
Frontend limits: take first 5
          ↓
Update state: announcements = filtered
          ↓
Render pet cards
```

### Error Handling

```
API call fails
          ↓
Update state: error = "Unable to load recent pets. Please refresh the page to try again."
          ↓
Render error message
          ↓
User manually refreshes page (no retry button per spec)
```

### Empty State

```
API returns empty array OR no MISSING pets after filtering
          ↓
Update state: announcements = [], error = null
          ↓
Render empty state: "No recent lost pet reports. Check back soon!"
```

---

## Notes

- **Announcement entity**: Reused from existing backend, no changes required
- **FeatureCard, FooterLink**: Static data, no backend persistence needed
- **LandingPageState**: Client-side only, no synchronization with backend
- **Distance calculation**: If backend provides distance in response (when lat/lng sent), display it. Otherwise show "Location unknown"
- **Photo URLs**: Relative paths from backend, prepended with `config.apiBaseUrl` (e.g., `/public/images/pet.jpg` → `http://localhost:3000/public/images/pet.jpg`)
- **Component Organization**: All landing page components stored in `/webApp/src/components/home/` directory for better organization

---

## Database Schema

N/A - No new database tables required. Landing page consumes existing `announcement` table via backend API.

---

## API Data Transfer Objects (DTOs)

### GET /api/v1/announcements Response

```typescript
interface AnnouncementsResponse {
  data: Announcement[];
}
```

**Frontend Transformation**:

```typescript
// Fetch announcements
const response = await fetch('/api/v1/announcements?lat=40.7128&lng=-74.0060');
const json: AnnouncementsResponse = await response.json();

// Filter for MISSING status
const missingPets = json.data.filter(a => a.status === 'MISSING');

// Sort by createdAt (newest first)
const sorted = missingPets.sort((a, b) => 
  new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
);

// Limit to 5
const recentPets = sorted.slice(0, 5);

// Update state
setState({ announcements: recentPets, isLoading: false, error: null });
```

---

## Summary

The landing page primarily consumes existing backend entities (`Announcement`) with minimal frontend-specific entities (`FeatureCard`, `FooterLink`, `LandingPageState`). No new backend models or database changes are required. All business logic for filtering, sorting, and limiting announcements is implemented in the frontend.

