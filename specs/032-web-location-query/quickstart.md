# Quickstart: Web Browser Location for Pet Listings

**Date**: 2025-11-29  
**Feature**: 032-web-location-query  
**Branch**: `032-web-location-query`

## Overview

This guide helps developers set up and test the web location feature locally. The feature adds browser-based geolocation to the pet listings page, allowing users to see pets filtered by proximity when location permission is granted.

---

## Prerequisites

- Node.js v24 (LTS) installed
- npm installed
- Modern browser with Geolocation API support (Chrome 5+, Firefox 3.5+, Safari 5+, Edge 12+)
- Backend server running on `http://localhost:3000` (see backend setup below)

---

## Quick Setup

### 1. Clone and Checkout Branch

```bash
git checkout 032-web-location-query
```

### 2. Install Dependencies

```bash
cd webApp
npm install
```

### 3. Start Backend Server (Required)

The web app needs the backend API to fetch pet listings.

```bash
cd server
npm install
npm run dev
```

Backend should be running on `http://localhost:3000`.

**Verify Backend**:
```bash
curl http://localhost:3000/api/v1/announcements
# Should return JSON with pet listings
```

### 4. Start Web Dev Server

```bash
cd webApp
npm run start
```

Web app should be running on `http://localhost:5173` (or similar Vite dev server port).

### 5. Open in Browser

Navigate to `http://localhost:5173` (or the port shown in terminal).

---

## Testing the Feature

### Scenario 1: First-Time User (Permission Prompt)

**Objective**: Test browser permission prompt and successful location fetch.

**Steps**:
1. Open browser in **Incognito/Private mode** (ensures permission hasn't been requested before)
2. Navigate to `http://localhost:5173`
3. Browser should immediately show native permission prompt: "Allow [site] to access your location?"
4. Click **"Allow"**
5. Observe:
   - Full-page loading spinner appears
   - After ~1-2 seconds, pet listings load
   - Browser console shows location coordinates (check DevTools)
   - Network tab shows API call to `/api/v1/announcements?lat=XX.XXXX&lng=XX.XXXX`

**Expected Result**: Pets load with location-based filtering, no error messages.

---

### Scenario 2: Location Permission Denied

**Objective**: Test graceful fallback when user blocks location access.

**Steps**:
1. Open browser in **Incognito/Private mode**
2. Navigate to `http://localhost:5173`
3. Browser shows permission prompt
4. Click **"Block"** or **"Don't Allow"** (or dismiss the prompt)
5. Observe:
   - Full-page loading spinner appears briefly
   - Pet listings load (all pets, no location filtering)
   - **Informational banner** appears above pet listings:
     - Message: "See pets available near you for easier adoption"
     - Generic instructions for enabling location in browser settings
     - **X button** to close the banner
   - Network tab shows API call to `/api/v1/announcements` (no `lat`/`lng` params)

**Expected Result**: All pets displayed, informational banner shown, no blocking errors.

**Test Banner Dismissal**:
1. Click the **X button** on the banner
2. Banner should disappear
3. Pet listings remain visible
4. **Reload page** (F5)
5. Banner should **reappear** (dismissal not persisted across page loads)

---

### Scenario 3: Location Permission Already Granted

**Objective**: Test automatic location fetch for returning users.

**Steps**:
1. In a **non-incognito** browser window, visit `http://localhost:5173`
2. Grant location permission if prompted (see Scenario 1)
3. Close the tab
4. Reopen `http://localhost:5173`
5. Observe:
   - **No permission prompt** (browser remembers previous grant)
   - Loading spinner appears immediately
   - Location fetched automatically
   - Pet listings load with location params

**Expected Result**: Seamless experience, no user interaction required, pets filtered by location.

---

### Scenario 4: Location Fetch Timeout

**Objective**: Test graceful fallback when location fetch exceeds 3-second timeout.

**Steps**:
1. Open Chrome DevTools (F12)
2. Go to **Sensors** tab (Ctrl+Shift+P → type "sensors")
3. Set location to **"Custom location"**
4. Enable **"Emulate geolocation timeout"** (or use a mock that delays response)
5. Reload page
6. Observe:
   - Loading spinner appears
   - After 3 seconds, location fetch times out
   - Pet listings load **without** location params (fallback mode)
   - **No error message** displayed (silent fallback per spec)

**Expected Result**: Pets load after timeout, no error shown to user.

---

### Scenario 5: Pet Listings API Failure

**Objective**: Test error handling when backend API fails.

**Steps**:
1. **Stop the backend server** (`Ctrl+C` in server terminal)
2. Reload page `http://localhost:5173`
3. Observe:
   - Loading spinner appears
   - After fetch fails, **error message** displays:
     - Message: "Unable to load pets. Try again" (or similar)
     - **"Retry" button** visible
4. **Restart backend server** (`npm run dev`)
5. Click **"Retry"** button
6. Observe:
   - Loading spinner reappears
   - Pets load successfully

**Expected Result**: Clear error message with retry functionality, successful recovery after retry.

---

### Scenario 6: No Pets Returned (Empty State)

**Objective**: Test empty state display when API returns zero pets.

**Setup**:
1. Modify backend API to return empty array:
   ```typescript
   // In server/src/services/announcementService.ts (temporary change for testing)
   return []; // Force empty response
   ```
2. Restart backend server

**Steps**:
1. Reload page `http://localhost:5173`
2. Observe:
   - Loading spinner appears
   - After fetch completes, **empty state** message displays:
     - Message: "No pets nearby"

**Expected Result**: Clear empty state message, no errors.

**Cleanup**: Revert backend changes after testing.

---

## Browser DevTools Testing

### Simulating Different Locations

**Chrome DevTools**:
1. Open DevTools (F12)
2. Press `Ctrl+Shift+P` (Command palette)
3. Type "sensors" and select "Show Sensors"
4. Under "Location", select a preset (e.g., "Berlin", "San Francisco") or enter custom coordinates
5. Reload page to apply location

**Firefox DevTools**:
1. Open DevTools (F12)
2. Go to **Settings** (gear icon)
3. Enable **"Custom Geolocation"** under Advanced Settings
4. Enter custom coordinates
5. Reload page to apply location

**Verify API Call**:
- Check **Network tab** in DevTools
- Find request to `/api/v1/announcements`
- Verify query params: `?lat=XX.XXXX&lng=XX.XXXX`
- Coordinates should be rounded to **4 decimal places**

---

### Simulating Permission States

**Reset Permission State**:

**Chrome**:
1. Click the **lock icon** or **site settings icon** in address bar
2. Find "Location" permission
3. Select "Reset permission" or "Clear"
4. Reload page to trigger fresh permission prompt

**Firefox**:
1. Click the **lock icon** in address bar
2. Click "More Information" → "Permissions"
3. Find "Access Your Location" → "Clear"
4. Reload page

**Safari**:
1. Safari → Settings → Websites → Location
2. Find `localhost` → "Deny" or "Remove"
3. Reload page

---

## Testing Without Geolocation API

**Objective**: Test graceful fallback when Geolocation API is unavailable (e.g., HTTP deployment, unsupported browser).

**Simulate Unsupported Browser**:
1. Open browser DevTools Console
2. Run:
   ```javascript
   delete navigator.geolocation;
   ```
3. Reload page
4. Observe:
   - **No error message** displayed
   - Pet listings load **without** location params
   - App functions normally (fallback mode)

**Expected Result**: Silent fallback, no breaking errors, pets load successfully.

---

## Running Tests

### Unit Tests

Run all unit tests for the location feature:

```bash
cd webApp
npm test -- --coverage
```

**Key Test Files**:
- `src/__tests__/hooks/useGeolocation.test.ts` - Location hook tests
- `src/__tests__/hooks/usePetListings.test.ts` - Pet listings hook tests (with location)
- `src/__tests__/components/LocationBanner.test.tsx` - Banner component tests
- `src/__tests__/utils/location.test.ts` - Coordinate formatting tests

**Coverage Target**: 80% line + branch coverage (verify in `webApp/coverage/index.html`).

---

### E2E Tests

Run Playwright tests for the location feature:

```bash
cd e2e-tests/web
npx playwright test specs/location-pet-listings.spec.ts
```

**Test Scenarios Covered**:
- User Story 1: Location-authorized users (pre-granted permission)
- User Story 2: First-time permission request
- User Story 3: Blocked permission recovery path

**View Test Report**:
```bash
npx playwright show-report
```

---

## Troubleshooting

### Issue: Permission Prompt Doesn't Appear

**Cause**: Permission already granted or denied in previous session.

**Solution**: Reset permission (see "Simulating Permission States" above).

---

### Issue: Location Always Fails

**Possible Causes**:
1. **No HTTPS**: Geolocation API requires HTTPS in production (localhost is exempt)
   - Solution: Use `localhost` for local dev (not `127.0.0.1` or IP address)
2. **System Location Services Disabled**: OS-level location is off
   - Solution: Enable location services in system settings
3. **Browser Doesn't Support Geolocation API**: Old browser version
   - Solution: Update browser or test in modern browser

---

### Issue: Backend API Returns 404

**Cause**: Backend server not running or incorrect API URL.

**Solution**:
1. Verify backend is running: `curl http://localhost:3000/api/v1/announcements`
2. Check `VITE_API_BASE_URL` environment variable in `.env` file (should be `http://localhost:3000`)
3. Restart backend server if needed

---

### Issue: Loading Spinner Never Disappears

**Possible Causes**:
1. **Location Fetch Hangs**: Increase timeout or check browser DevTools console for errors
2. **Backend API Hangs**: Check backend server logs for errors
3. **JavaScript Error**: Check browser DevTools console for uncaught exceptions

**Debug Steps**:
1. Open browser DevTools Console
2. Check for error messages
3. Verify Network tab shows completed requests (not pending)
4. Check backend server terminal for errors

---

## Key Implementation Files

### Hooks
- `/webApp/src/hooks/use-geolocation.ts` - Custom hook for browser location
- `/webApp/src/hooks/use-animal-list.ts` - Animal list hook (extended with location)

### Components
- `/webApp/src/components/LocationBanner/LocationBanner.tsx` - Blocked permission banner
- `/webApp/src/components/LoadingOverlay/LoadingOverlay.tsx` - Full-page loading spinner
- `/webApp/src/components/AnimalList/AnimalList.tsx` - Main list component (extended with location)
- `/webApp/src/components/AnimalList/EmptyState.tsx` - Empty state message (may need update for location context)

### Services
- `/webApp/src/services/animal-repository.ts` - Extended `getAnimals()` method

### Utilities
- `/webApp/src/utils/location.ts` - Coordinate formatting utility

### Main Entry
- `/webApp/src/index.tsx` - Application entry point
- `/webApp/src/components/AnimalList/AnimalList.tsx` - Main component integrating location logic

---

## Quick Reference

### Feature Flags / Environment Variables

**None required** - feature is enabled by default.

Optional environment variable:
```bash
# .env file in /webApp
VITE_API_BASE_URL=http://localhost:3000
```

---

### Browser Compatibility

| Browser | Minimum Version | Geolocation API | Permissions API |
|---------|----------------|-----------------|-----------------|
| Chrome  | 5+             | ✅ Yes          | ✅ Yes (43+)    |
| Firefox | 3.5+           | ✅ Yes          | ✅ Yes (46+)    |
| Safari  | 5+             | ✅ Yes          | ✅ Yes (16+)    |
| Edge    | 12+            | ✅ Yes          | ✅ Yes (79+)    |

**Graceful Degradation**: If Geolocation API is unavailable, app falls back to showing all pets without location filtering (no errors).

---

## Next Steps

After verifying local functionality:
1. Review implementation in `/webApp/src/`
2. Run full test suite: `npm test -- --coverage`
3. Run E2E tests: `npx playwright test`
4. Verify 80%+ test coverage in `webApp/coverage/index.html`
5. Ready for code review and merge

---

## Support

For questions or issues:
- Review feature spec: `/specs/032-web-location-query/spec.md`
- Review data model: `/specs/032-web-location-query/data-model.md`
- Review API contract: `/specs/032-web-location-query/contracts/animal-repository-extension.md`
- Check research findings: `/specs/032-web-location-query/research.md`
