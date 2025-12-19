# Quickstart: Web Map Pins Development

**Feature**: 064-web-map-pins  
**Date**: 2025-12-19  
**Platform**: Web (`/webApp` module)

## Overview

This guide helps developers set up their environment and start working on the web map pins feature. Follow these steps to build, test, and verify the implementation.

---

## Prerequisites

### Required Software

- **Node.js**: v24 (LTS) or compatible
- **npm**: v10+ (comes with Node.js)
- **Git**: For version control
- **Modern browser**: Chrome, Firefox, Safari, or Edge (latest 2 versions)

### Verify Installation

```bash
node --version  # Should be v24.x or compatible
npm --version   # Should be 10.x or higher
```

---

## Initial Setup

### 1. Clone Repository (if not already done)

```bash
git clone <repository-url>
cd AI-First
```

### 2. Checkout Feature Branch

```bash
git checkout 064-web-map-pins
```

### 3. Install Dependencies

```bash
# Install web app dependencies
cd webApp
npm install

# Verify Leaflet dependencies are installed
npm list leaflet react-leaflet @types/leaflet
```

**Expected Output**:
```
webApp@1.0.0 /path/to/AI-First/webApp
├── leaflet@1.9.4
├── react-leaflet@4.2.1
└── @types/leaflet@1.9.8
```

---

## Development Workflow

### 1. Start Backend Server (Required)

The web app needs the backend API for fetching announcements.

```bash
# From project root
cd server
npm install
npm run dev
```

**Backend will run on**: http://localhost:3000

**Verify backend is running**:
```bash
curl http://localhost:3000/api/v1/announcements
```

### 2. Start Web Dev Server

```bash
# From project root
cd webApp
npm run start
```

**Web app will run on**: http://localhost:5173

**Dev server features**:
- Hot module replacement (HMR)
- Automatic recompilation on file changes
- Source maps for debugging

### 3. Open in Browser

Navigate to: http://localhost:5173

You should see the PetSpot landing page with the map.

---

## Development Commands

### Run Unit Tests

```bash
cd webApp

# Run all tests (watch mode)
npm test

# Run tests once (CI mode)
npm test -- --run

# Run tests with coverage
npm test -- --coverage

# Run specific test file
npm test -- use-map-pins.test.ts
```

**Coverage reports**: `/webApp/coverage/index.html`

### Run Linter

```bash
cd webApp

# Check for lint errors
npm run lint

# Fix auto-fixable errors
npm run lint -- --fix
```

### Run Type Checking

```bash
cd webApp

# Check TypeScript types
npx tsc --noEmit
```

### Run E2E Tests

```bash
# From project root
cd e2e-tests/web

# Install Playwright browsers (first time only)
npx playwright install

# Run E2E tests for map pins
npx playwright test specs/064-web-map-pins.spec.ts

# Run E2E tests in headed mode (see browser)
npx playwright test specs/064-web-map-pins.spec.ts --headed

# Run E2E tests with debug
npx playwright test specs/064-web-map-pins.spec.ts --debug
```

---

## Project Structure

```
webApp/src/
├── components/
│   └── map/
│       ├── MapPinLayer.tsx           # NEW: Renders pin markers with pop-ups
│       ├── __tests__/
│       │   └── MapPinLayer.test.tsx  # NEW: Component tests
│       └── (existing map components)
├── hooks/
│   ├── use-map-pins.ts               # NEW: Pin fetching hook
│   └── __test__/
│       └── use-map-pins.test.ts      # NEW: Hook unit tests
├── lib/
│   ├── map-pin-helpers.ts            # NEW: Pin icon helpers (Leaflet divIcon)
│   └── __test__/
│       └── map-pin-helpers.test.ts   # NEW: Utility tests
└── services/
    └── AnnouncementService.ts        # EXISTING: Reused

e2e-tests/web/specs/
└── 064-web-map-pins.spec.ts          # NEW: E2E tests
```

---

## Testing Strategy

### Unit Tests (80% Coverage Required)

**What to test**:
- ✅ Custom hooks (`use-map-pins`)
- ✅ Utility functions (`map-pin-helpers` - icon creation)
- ✅ Component rendering and interactions

**Example test structure**:
```typescript
describe('use-map-pins', () => {
  it('should fetch pins when user location is provided', async () => {
    // Given: User location
    const { result } = renderHook(() => useMapPins({ lat: 52.5, lng: 13.4 }));
    
    // When: Hook mounts
    await waitFor(() => expect(result.current.loading).toBe(false));
    
    // Then: Pins are loaded
    expect(result.current.pins).toHaveLength(2);
    expect(result.current.error).toBeNull();
  });
});
```

### Component Tests

**What to test**:
- ✅ Pin markers render correctly
- ✅ Leaflet pop-up integration (Leaflet handles pop-up state)
- ✅ Loading/error states display correctly

### E2E Tests

**What to test**:
- ✅ Pins appear on landing page map
- ✅ Clicking pin opens pop-up with pet details
- ✅ Pop-up closes via close button
- ✅ Pop-up closes via click-outside
- ✅ Error state shows retry button

---

## Debugging Tips

### View Component in React DevTools

1. Install [React DevTools](https://react.dev/learn/react-developer-tools) browser extension
2. Open DevTools → Components tab
3. Find `MapPinLayer` component
4. Inspect props and state

### Mock API Responses

```typescript
// In test files
import { vi } from 'vitest';

vi.mock('@/services/AnnouncementService', () => ({
  announcementService: {
    getAnnouncements: vi.fn().mockResolvedValue({
      data: [mockAnnouncement]
    })
  }
}));
```

### View Network Requests

1. Open browser DevTools → Network tab
2. Filter by "XHR" or "Fetch"
3. Look for requests to `/api/v1/announcements`
4. Inspect request parameters and response data

---

## Common Issues

### Issue: "Cannot find module 'leaflet'"

**Solution**: Reinstall dependencies
```bash
cd webApp
rm -rf node_modules package-lock.json
npm install
```

### Issue: Backend API returns 404

**Solution**: Verify backend server is running
```bash
cd server
npm run dev
```

Check backend logs for errors.

### Issue: Pins not appearing on map

**Debug steps**:
1. Open browser console, check for errors
2. Verify API returns data: `curl http://localhost:3000/api/v1/announcements?lat=52.5&lng=13.4&range=10`
3. Check network tab for failed requests
4. Verify user location is available

### Issue: Tests failing with "ReferenceError: L is not defined"

**Solution**: Mock Leaflet in test setup
```typescript
// vite.config.ts
export default defineConfig({
  test: {
    setupFiles: ['./src/test/setup.ts'],
    environment: 'jsdom'
  }
});

// src/test/setup.ts
global.L = {
  divIcon: vi.fn(),
  // ... other Leaflet mocks
};
```

### Issue: E2E tests fail with "Cannot find map element"

**Solution**: Ensure map loads before interacting
```typescript
await page.waitForSelector('[data-testid="landingPage.map"]', { timeout: 5000 });
```

---

## Code Style Guidelines

### TypeScript

- ✅ Use strict mode (enabled in `tsconfig.json`)
- ✅ Prefer `interface` over `type` for object shapes
- ✅ Use descriptive names: `useMapPins`, not `usePins`
- ✅ Max 3 nesting levels (enforced by ESLint)

### React

- ✅ Functional components only (no class components)
- ✅ Custom hooks for business logic
- ✅ Keep components thin (presentation only)
- ✅ Use `data-testid` for all interactive elements

### Documentation

- ✅ JSDoc for complex functions only
- ✅ Skip self-explanatory code
- ✅ Keep comments concise (1-3 sentences)

---

## Next Steps

### Implementation Checklist

1. [ ] Create `use-map-pins` hook with tests (TDD)
2. [ ] Create `map-pin-helpers` utilities with tests (TDD)
3. [ ] Build `MapPinLayer` component with tests (uses Leaflet's built-in pop-up)
4. [ ] Integrate into landing page
5. [ ] Write E2E tests
6. [ ] Verify 80% coverage (`npm test -- --coverage`)
7. [ ] Run linter (`npm run lint`)
8. [ ] Manual testing in browser

### Definition of Done

- ✅ All unit tests passing
- ✅ 80% test coverage achieved
- ✅ E2E tests passing
- ✅ No linter errors
- ✅ Manual testing complete (all user stories verified)
- ✅ Code reviewed and approved
- ✅ Documentation updated

---

## Resources

### Documentation

- [Leaflet.js Docs](https://leafletjs.com/reference.html)
- [React-Leaflet Docs](https://react-leaflet.js.org/)
- [Vitest Docs](https://vitest.dev/)
- [React Testing Library](https://testing-library.com/react)
- [Playwright Docs](https://playwright.dev/)

### Internal Docs

- Feature Spec: [`spec.md`](./spec.md)
- Data Model: [`data-model.md`](./data-model.md)
- API Contract: [`contracts/api-announcements.md`](./contracts/api-announcements.md)
- Research: [`research.md`](./research.md)

### Design Reference

- [Figma Wireframes](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=1071-3871&m=dev)

---

## Support

If you encounter issues not covered in this guide:

1. Check existing documentation in `/specs/064-web-map-pins/`
2. Review related feature: `/specs/063-web-map-view/`
3. Check project constitution: `/.specify/memory/constitution-web.md`
4. Ask team for help in Slack/Discord/etc.

---

**Happy coding!** 🚀

