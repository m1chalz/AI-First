# Quickstart Guide: Web Map Component on Landing Page

**Feature**: 063-web-map-view  
**Date**: 2025-12-18  
**Audience**: Developers implementing the map component

## Overview

This quickstart guide provides step-by-step instructions for implementing the interactive map component on the web landing page. Follow the phases in order for a smooth TDD workflow.

---

## Prerequisites

Before starting, ensure you have:

- [ ] Node.js v24 (LTS) installed
- [ ] `/webApp` dependencies installed (`npm install`)
- [ ] `/webApp` dev server running (`npm run start`)
- [ ] GeolocationContext already exists in `/webApp/src/contexts/GeolocationContext.tsx`
- [ ] Landing page already exists in `/webApp/src/pages/Home.tsx`

---

## Phase 1: Install Dependencies

### 1.1 Install Map Libraries

```bash
cd webApp
npm install leaflet react-leaflet
npm install -D @types/leaflet
```

**Rationale**:
- `leaflet`: Core map rendering library (MIT license, ~40 KB gzipped)
- `react-leaflet`: React bindings for Leaflet (hooks-based API)
- `@types/leaflet`: TypeScript definitions (dev dependency)

### 1.2 Verify Installation

```bash
npm list leaflet react-leaflet
```

**Expected Output**:
```
webApp@1.0.0 /Users/you/code/AI-First/webApp
├── leaflet@1.9.4
└── react-leaflet@4.2.1
```

---

## Phase 2: Create TypeScript Interfaces (TDD Red)

### 2.1 Create Contracts Directory

```bash
mkdir -p src/types
```

### 2.2 Copy Interface Definitions

Copy the interface definitions from `/specs/063-web-map-view/contracts/map-component.interface.ts` to `/webApp/src/types/map.ts`.

**Key Interfaces**:
- `Coordinates` - Geographic location (lat, lng)
- `MapState` - Current map state
- `MapError` - Error states
- `UseMapStateReturn` - Hook return type
- `MAP_CONFIG` - Constants

---

## Phase 3: Implement useMapState Hook (TDD)

### 3.1 Write Failing Tests for Hook

**File**: `/webApp/src/hooks/__tests__/use-map-state.test.ts`

```typescript
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { useMapState } from '../use-map-state';
import * as GeolocationContext from '../../contexts/GeolocationContext';

vi.mock('../../contexts/GeolocationContext');

describe('useMapState', () => {
  it('should return current location as center when permission granted', async () => {
    // Given
    vi.spyOn(GeolocationContext, 'useGeolocation').mockReturnValue({
      coordinates: { latitude: 51.1079, longitude: 17.0385 },
      error: null,
      isLoading: false,
      permissionCheckCompleted: true,
      requestPermission: vi.fn(),
    });
    
    // When
    const { result } = renderHook(() => useMapState());
    
    // Then
    await waitFor(() => {
      expect(result.current.center).toEqual({ latitude: 51.1079, longitude: 17.0385 });
      expect(result.current.zoom).toBe(13);
      expect(result.current.showPermissionPrompt).toBe(false);
    });
  });
  
  it('should show permission prompt when permission denied', () => {
    // Given
    vi.spyOn(GeolocationContext, 'useGeolocation').mockReturnValue({
      coordinates: null,
      error: { code: 1, message: 'Permission denied' },
      isLoading: false,
      permissionCheckCompleted: true,
      requestPermission: vi.fn(),
    });
    
    // When
    const { result } = renderHook(() => useMapState());
    
    // Then
    expect(result.current.showPermissionPrompt).toBe(true);
    expect(result.current.error?.type).toBe('PERMISSION_DENIED');
  });
  
  // ... more tests for fallback logic, error handling, etc.
});
```

**Run Tests** (should fail):
```bash
npm test src/hooks/__tests__/use-map-state.test.ts
```

### 3.2 Implement Hook (Green Phase)

**File**: `/webApp/src/hooks/use-map-state.ts`

```typescript
import { useState } from 'react';
import { useGeolocation } from '../contexts/GeolocationContext';
import { UseMapStateReturn, MAP_CONFIG, createMapError } from '../types/map';

/**
 * Custom hook for managing map state (location, zoom, error handling).
 */
export function useMapState(): UseMapStateReturn {
  const geolocation = useGeolocation();
  
  // Determine map center based on priority
  const center = geolocation.coordinates || MAP_CONFIG.FALLBACK_LOCATION;
  
  // Determine error state
  const error = determineError(geolocation);
  
  // Show permission prompt if permission not granted
  const showPermissionPrompt = !geolocation.coordinates && !geolocation.isLoading;
  
  // Handler
  const handleRequestPermission = () => {
    geolocation.requestPermission();
  };
  
  return {
    center,
    zoom: MAP_CONFIG.DEFAULT_ZOOM,
    isLoading: geolocation.isLoading,
    error,
    showPermissionPrompt,
    handleRequestPermission,
  };
}

// Helper function

function determineError(geolocation) {
  if (!geolocation.error) return null;
  
  if (geolocation.error.code === 1) {
    return createMapError('PERMISSION_DENIED');
  }
  
  return createMapError('LOCATION_UNAVAILABLE');
}
```

**Run Tests** (should pass):
```bash
npm test src/hooks/__tests__/use-map-state.test.ts
```

---

## Phase 4: Create Map Components (TDD)

### 4.1 MapPermissionPrompt Component

**File**: `/webApp/src/components/map/MapPermissionPrompt.tsx`

```typescript
import React from 'react';
import { MapPermissionPromptProps } from '../../types/map';
import styles from './MapPermissionPrompt.module.css';

/**
 * Displays informational message when location permission not granted.
 */
export function MapPermissionPrompt({ onRequestPermission, className, 'data-testid': testId }: MapPermissionPromptProps) {
  return (
    <div className={`${styles.container} ${className || ''}`} data-testid={testId || 'landingPage.map.permissionPrompt'}>
      <div className={styles.content}>
        <h3 className={styles.title}>Location Access Required</h3>
        <p className={styles.message}>
          To show you pets near your location, we need access to your device's location.
        </p>
        <button
          className={styles.button}
          onClick={onRequestPermission}
          data-testid="landingPage.map.consentButton"
        >
          Allow Location Access
        </button>
      </div>
    </div>
  );
}
```

**Test File**: `/webApp/src/components/map/__tests__/MapPermissionPrompt.test.tsx`

```typescript
import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { MapPermissionPrompt } from '../MapPermissionPrompt';

describe('MapPermissionPrompt', () => {
  it('should render permission message', () => {
    // Given
    const mockOnRequestPermission = vi.fn();
    
    // When
    render(<MapPermissionPrompt onRequestPermission={mockOnRequestPermission} />);
    
    // Then
    expect(screen.getByText(/Location Access Required/i)).toBeInTheDocument();
  });
  
  it('should call onRequestPermission when button clicked', () => {
    // Given
    const mockOnRequestPermission = vi.fn();
    render(<MapPermissionPrompt onRequestPermission={mockOnRequestPermission} />);
    
    // When
    fireEvent.click(screen.getByTestId('landingPage.map.consentButton'));
    
    // Then
    expect(mockOnRequestPermission).toHaveBeenCalledTimes(1);
  });
});
```

### 4.2 MapErrorState Component

**File**: `/webApp/src/components/map/MapErrorState.tsx`

```typescript
import React from 'react';
import { MapErrorStateProps } from '../../types/map';
import styles from './MapErrorState.module.css';

/**
 * Displays error message when map fails to load.
 */
export function MapErrorState({ error, className, 'data-testid': testId }: MapErrorStateProps) {
  return (
    <div className={`${styles.container} ${className || ''}`} data-testid={testId || 'landingPage.map.errorState'}>
      <div className={styles.content}>
        <h3 className={styles.title}>Map Error</h3>
        <p className={styles.message}>{error.message}</p>
      </div>
    </div>
  );
}
```

### 4.3 MapView Component (Main Component)

**File**: `/webApp/src/components/map/MapView.tsx`

```typescript
import React from 'react';
import { MapContainer, TileLayer } from 'react-leaflet';
import { MapViewProps } from '../../types/map';
import { useMapState } from '../../hooks/use-map-state';
import { MapPermissionPrompt } from './MapPermissionPrompt';
import { MapErrorState } from './MapErrorState';
import styles from './MapView.module.css';
import 'leaflet/dist/leaflet.css';

/**
 * Interactive map component for landing page.
 */
export function MapView({ className, 'data-testid': testId }: MapViewProps) {
  const {
    center,
    zoom,
    isLoading,
    error,
    showPermissionPrompt,
    handleRequestPermission,
  } = useMapState();
  
  // Show permission prompt
  if (showPermissionPrompt) {
    return <MapPermissionPrompt onRequestPermission={handleRequestPermission} />;
  }
  
  // Show loading state
  if (isLoading) {
    return <div className={styles.loading} data-testid="landingPage.map.loading">Loading map...</div>;
  }
  
  // Show error state (no fallback map)
  if (error && !error.showFallbackMap) {
    return <MapErrorState error={error} />;
  }
  
  // Show map (with optional error message for location unavailable)
  return (
    <div className={`${styles.container} ${className || ''}`} data-testid={testId || 'landingPage.map'}>
      {error && error.showFallbackMap && (
        <div className={styles.errorBanner} data-testid="landingPage.map.errorBanner">
          {error.message}
        </div>
      )}
      <MapContainer
        center={[center.latitude, center.longitude]}
        zoom={zoom}
        style={{ height: '400px', width: '100%' }}
        minZoom={10}
        maxZoom={18}
        data-testid="landingPage.map.container"
      >
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
      </MapContainer>
    </div>
  );
}
```

---

## Phase 5: Integration with Landing Page

### 5.1 Update Home.tsx

**File**: `/webApp/src/pages/Home.tsx`

```typescript
import { HeroSection } from '../components/home/HeroSection';
import { MapView } from '../components/map/MapView'; // NEW
import { RecentPetsSection } from '../components/home/RecentPetsSection';

export function Home() {
  return (
    <main>
      <HeroSection />
      <MapView /> {/* NEW: Insert between Description and Recently Lost Pets */}
      <RecentPetsSection />
    </main>
  );
}
```

---

## Phase 6: E2E Tests (Playwright)

### 6.1 Create E2E Test File

**File**: `/e2e-tests/web/specs/map-view.spec.ts`

```typescript
import { test, expect } from '@playwright/test';

test.describe('Landing Page Map View', () => {
  test('should display map between Description and Recently Lost Pets', async ({ page, context }) => {
    // Grant location permission
    await context.grantPermissions(['geolocation'], { origin: 'http://localhost:3000' });
    await context.setGeolocation({ latitude: 51.1079, longitude: 17.0385 });
    
    // Given: User navigates to landing page
    await page.goto('http://localhost:3000');
    
    // When: Page loads
    await page.waitForSelector('[data-testid="landingPage.map"]');
    
    // Then: Map is displayed between sections
    const map = page.locator('[data-testid="landingPage.map"]');
    await expect(map).toBeVisible();
  });
  
  test('should show permission prompt when permission denied', async ({ page }) => {
    // Given: User has denied location permission
    await page.goto('http://localhost:3000');
    
    // When: Page loads
    await page.waitForSelector('[data-testid="landingPage.map.permissionPrompt"]');
    
    // Then: Permission prompt is displayed
    const prompt = page.locator('[data-testid="landingPage.map.permissionPrompt"]');
    await expect(prompt).toBeVisible();
    await expect(prompt).toContainText('Location Access Required');
  });
});
```

### 6.2 Run E2E Tests

```bash
cd e2e-tests/web
npx playwright test specs/map-view.spec.ts
```

---

## Phase 7: Test Coverage

### 7.1 Run Unit Tests with Coverage

```bash
cd webApp
npm test --coverage
```

### 7.2 Verify 80% Coverage Target

Check coverage report for:
- `/src/hooks/use-map-state.ts` - 80%+
- `/src/components/map/*` - Recommended

### 7.3 View Coverage Report

```bash
open coverage/index.html
```

---

## Checklist

**Phase 1: Dependencies**
- [ ] Installed leaflet, react-leaflet, @types/leaflet
- [ ] Verified installation

**Phase 2: TypeScript Interfaces**
- [ ] Copied interface definitions to `/webApp/src/types/map.ts`

**Phase 3: useMapState Hook**
- [ ] Wrote failing tests for hook
- [ ] Implemented hook with location priority logic
- [ ] Unit tests pass (80% coverage)

**Phase 4: Map Components**
- [ ] Implemented `MapPermissionPrompt` component
- [ ] Implemented `MapErrorState` component
- [ ] Implemented `MapView` component (main)
- [ ] Component tests pass

**Phase 5: Integration**
- [ ] Integrated `MapView` into `Home.tsx` landing page
- [ ] Verified positioning between Description and Recently Lost Pets

**Phase 6: E2E Tests**
- [ ] Wrote Playwright E2E tests for User Story 1 & 2
- [ ] E2E tests pass

**Phase 7: Coverage**
- [ ] Unit test coverage ≥ 80%
- [ ] Component test coverage recommended
- [ ] E2E tests cover critical flows

---

## Troubleshooting

### Map Tiles Not Loading

**Problem**: Map displays gray tiles or fails to load.

**Solution**:
1. Check network tab for CORS errors
2. Verify OpenStreetMap tile URL is correct
3. Check browser console for Leaflet errors
4. Error message instructs user to refresh page

### Permission Prompt Not Appearing

**Problem**: Browser doesn't show permission dialog.

**Solution**:
1. Check if permission already granted/denied (browser settings)
2. Verify HTTPS or localhost (geolocation requires secure context)
3. Clear browser cache and cookies
4. Test in incognito mode

### Tests Failing in CI

**Problem**: E2E tests fail in CI/CD pipeline.

**Solution**:
1. Mock geolocation in test setup
2. Use Playwright's `context.grantPermissions()`
3. Set static geolocation with `context.setGeolocation()`
4. Increase timeout for map tile loading

---

## Next Steps

After completing this quickstart:

1. Run full test suite: `npm test` (webApp) + E2E tests
2. Verify test coverage ≥ 80%
3. Manual QA testing in browsers (Chrome, Firefox, Safari, Edge)
4. Code review and PR submission
5. Deploy to staging for user acceptance testing

---

## Resources

- **Leaflet Docs**: https://leafletjs.com/reference.html
- **react-leaflet Docs**: https://react-leaflet.js.org/docs/start-introduction
- **OpenStreetMap Tiles**: https://wiki.openstreetmap.org/wiki/Tiles
- **Geolocation API**: https://developer.mozilla.org/en-US/docs/Web/API/Geolocation_API
- **Playwright Docs**: https://playwright.dev/docs/intro

