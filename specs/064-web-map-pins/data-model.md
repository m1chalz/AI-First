# Data Model: Web Map Pins

**Feature**: 064-web-map-pins  
**Date**: 2025-12-19  
**Phase**: Phase 1 - Data Model Design

## Overview

This document defines the data structures, types, and state models for the web map pins feature. All types are TypeScript interfaces/types used in the `/webApp` module.

---

## Domain Models

### PetPin

Represents a single pet announcement as a map pin marker.

```typescript
/**
 * Pet announcement data for displaying as a map pin.
 * Derived from Announcement entity via transformation.
 */
interface PetPin {
  /** Unique identifier (matches Announcement.id) */
  id: string;
  
  /** Pet name for display in pop-up */
  name: string;
  
  /** Pet type (e.g., "dog", "cat", "bird") */
  species: string;
  
  /** Announcement status determines pin color */
  status: 'missing' | 'found';
  
  /** Last seen/found location - latitude */
  latitude: number;
  
  /** Last seen/found location - longitude */
  longitude: number;
  
  /** Pet photo URL */
  photoUrl: string;
  
  /** Contact phone number for pop-up */
  phoneNumber: string;
  
  /** Contact email for pop-up */
  email: string;
  
  /** Timestamp of announcement creation (ISO 8601) */
  createdAt: string;
}
```

**Transformation**:
- Converts `status` from uppercase (MISSING/FOUND) to lowercase (missing/found)

---

## State Models

### MapPinsState

State managed by `useMapPins` hook.

```typescript
/**
 * State for managing map pins data, loading, and errors.
 */
interface MapPinsState {
  /** Array of pins to display on map */
  pins: PetPin[];
  
  /** Loading state (true during fetch) */
  loading: boolean;
  
  /** Error state (null if no error) */
  error: Error | null;
  
  /** Retry function to re-fetch pins */
  retry: () => void;
}
```

---

## API Response Models

**Usage**:
```typescript
// Transform announcements to pins inline
const pins = announcements.data.map(a => ({
  id: a.id,
  name: a.petName,
  species: a.species,
  status: a.status.toLowerCase() as 'missing' | 'found',
  latitude: a.locationLatitude,
  longitude: a.locationLongitude,
  photoUrl: a.photoUrl,
  phoneNumber: a.phone,
  email: a.email,
  createdAt: a.createdAt
}));
```
