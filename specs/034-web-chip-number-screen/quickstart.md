# Quickstart Guide: Web Microchip Number Screen

**Feature**: 034-web-chip-number-screen  
**Date**: 2025-12-01  
**Phase**: 1 (Design & Contracts)

## Overview

This guide helps developers set up, run, and test the web microchip number screen (step 1 of the "report missing pet" flow) locally.

---

## Prerequisites

- Node.js v24 (LTS) installed
- npm installed
- Repository cloned locally
- Branch `034-web-chip-number-screen` checked out

---

## Setup

### 1. Install Dependencies

```bash
cd webApp
npm install
```

**Expected output**: Dependencies installed successfully (React, React Router, Vitest, etc.)

### 2. Verify Existing Setup

Ensure React Router is already configured in the project:

```bash
# Check if react-router-dom is installed
npm list react-router-dom
```

**Expected**: `react-router-dom@6.x.x` (or similar)

If not installed (unlikely), add it:

```bash
npm install react-router-dom@^6.20.0
```

---

## Running Locally

### 1. Start Development Server

```bash
cd webApp
npm run start
```

**Expected output**:
```
  VITE v5.x.x  ready in XXX ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: use --host to expose
```

### 2. Navigate to Feature

Open browser and go to:
- Pet list: `http://localhost:5173/pets`
- Click "Report Missing Pet" button (or directly access): `http://localhost:5173/report-missing/microchip`

### 3. Test User Flows

**Flow 1: Enter microchip number and continue**
1. Navigate to `http://localhost:5173/report-missing/microchip`
2. Enter digits: `123456789012345`
3. Verify formatting: Should display as `12345-67890-12345`
4. Click "Continue"
5. Verify navigation to photo step (or shows "Photo step coming soon" placeholder)

**Flow 2: Skip microchip number (optional field)**
1. Navigate to `http://localhost:5173/report-missing/microchip`
2. Leave input field empty
3. Click "Continue" (button should be enabled)
4. Verify navigation to photo step

**Flow 3: Cancel flow**
1. Navigate to `http://localhost:5173/report-missing/microchip`
2. Enter some data (optional)
3. Click back arrow button in header
4. Verify navigation to `/pets` (pet list)
5. Verify flow state is cleared

**Flow 4: Browser back button**
1. Navigate to `http://localhost:5173/report-missing/microchip`
2. Enter some data (optional)
3. Click browser back button
4. Verify navigation to `/pets` (pet list)
5. Verify flow state is cleared

**Flow 5: Page refresh**
1. Navigate to `http://localhost:5173/report-missing/microchip`
2. Enter some data
3. Refresh page (Cmd+R / Ctrl+R)
4. Verify input field is empty (flow state cleared)

**Flow 6: Direct URL access to photo step**
1. Directly access: `http://localhost:5173/report-missing/photo`
2. Verify redirect to `/report-missing/microchip` (no flow state exists)

**Flow 7: Paste with non-numeric characters**
1. Navigate to `http://localhost:5173/report-missing/microchip`
2. Copy text: `ABC123XYZ456DEF789`
3. Paste into input field (Cmd+V / Ctrl+V)
4. Verify only digits remain: `123456789` (formatted as `12345-6789`)

---

## Testing

### Unit Tests

Run all unit tests with coverage:

```bash
cd webApp
npm test -- --coverage
```

**Expected output**:
```
✓ src/utils/__tests__/microchip-formatter.test.ts (X tests)
✓ src/hooks/__tests__/use-microchip-formatter.test.ts (X tests)
✓ src/components/ReportMissingPet/__tests__/MicrochipNumberContent.test.tsx (X tests)

Test Files  X passed (X)
     Tests  X passed (X)
  Coverage  XX.XX% (target: 80%+)
```

**Coverage target**: 80%+ line and branch coverage

**Check coverage report**:
```bash
open webApp/coverage/index.html
```

### Run Specific Test File

```bash
cd webApp
npm test -- src/utils/__tests__/microchip-formatter.test.ts
```

### Run Tests in Watch Mode

```bash
cd webApp
npm test -- --watch
```

---

### E2E Tests

Run web E2E tests with Selenium + Cucumber:

```bash
# From repository root
mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web and @reportMissingPet"
```

**Expected output**:
```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running cucumber.RunCucumberTest
Feature: Report Missing Pet - Step 1 Microchip Number

  Scenario: User enters microchip number with automatic formatting
    ✓ Given user is on the pet list screen
    ✓ When they initiate the "report missing pet" flow
    ✓ Then they see the microchip number screen
    ...

X scenarios (X passed)
X steps (X passed)

[INFO] Tests run: X, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**Check E2E test report**:
```bash
open e2e-tests/target/cucumber-reports/web/index.html
```

### Run E2E Tests for Specific Scenario

```bash
mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web and @reportMissingPet and @scenario1"
```

---

## Debugging

### Debug Component in Browser DevTools

1. Open browser DevTools (F12)
2. Go to "Sources" tab
3. Find component file: `src/components/ReportMissingPet/Step1MicrochipNumber.tsx`
4. Set breakpoints
5. Interact with the page to trigger breakpoints

### Debug React Context State

Add React DevTools extension to browser:
- Chrome: [React Developer Tools](https://chrome.google.com/webstore/detail/react-developer-tools/)
- Firefox: [React Developer Tools](https://addons.mozilla.org/en-US/firefox/addon/react-devtools/)

**Inspect flow state**:
1. Open React DevTools
2. Find `ReportMissingPetFlowProvider` component
3. View Context value (flowState with currentStep enum, updateFlowState, clearFlowState)
4. Watch state updates as you interact with the form (currentStep should progress: Microchip → Photo → Details → Contact)

### Debug Tests

Run tests with debugging:

```bash
cd webApp
npm test -- --inspect-brk
```

Then open `chrome://inspect` in Chrome and attach to the Node process.

---

## Common Issues & Solutions

### Issue 1: "React Router not found"

**Symptom**: Import errors for `react-router-dom`

**Solution**:
```bash
cd webApp
npm install react-router-dom@^6.20.0
```

### Issue 2: Tests failing with "Cannot find module"

**Symptom**: Import errors in tests

**Solution**: Verify `vite.config.ts` has correct test configuration:
```typescript
import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: './src/test/setup.ts',
  },
});
```

### Issue 3: E2E tests cannot find element

**Symptom**: `NoSuchElementException` in E2E tests

**Solution**:
1. Verify `data-testid` attributes exist on elements
2. Check Page Object Model locators match `data-testid` values
3. Add explicit waits in step definitions:
   ```java
   WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
   wait.until(ExpectedConditions.visibilityOf(page.continueButton));
   ```

### Issue 4: Flow state not persisting between steps

**Symptom**: Flow state is lost when navigating between steps

**Solution**:
1. Verify `ReportMissingPetFlowProvider` wraps the entire flow route
2. Check React Router configuration has correct nesting
3. Ensure `<Outlet />` is used in parent route component
4. Verify `currentStep` enum is being updated correctly when user progresses

### Issue 5: Browser back button not working as expected

**Symptom**: Browser back doesn't cancel flow or clear state

**Solution**:
1. Verify `useBrowserBackHandler` hook is called in MicrochipNumberScreen component
2. Check `popstate` event listener is correctly attached
3. Ensure `clearFlowState` is called before navigation

---

## Verification Checklist

Before committing, verify:

- [ ] `npm run start` launches app successfully
- [ ] All 7 manual test flows pass
- [ ] `npm test -- --coverage` shows 80%+ coverage
- [ ] Unit tests pass (no failures)
- [ ] E2E tests pass (web scenarios)
- [ ] All interactive elements have `data-testid` attributes
- [ ] No ESLint errors: `npm run lint`
- [ ] No TypeScript errors: `npm run type-check` (if command exists)
- [ ] Code formatted: `npm run format` (if command exists)

---

## Next Steps

After local testing passes:

1. **Push branch**: `git push origin 034-web-chip-number-screen`
2. **Open PR**: Create pull request with description referencing spec
3. **CI/CD**: Wait for CI pipeline to run all tests
4. **Code review**: Address feedback from reviewers
5. **Merge**: Once approved and CI passes

---

## Useful Commands Reference

| Command | Description |
|---------|-------------|
| `npm run start` | Start dev server (webApp/) |
| `npm test` | Run all unit tests (webApp/) |
| `npm test -- --coverage` | Run tests with coverage report (webApp/) |
| `npm test -- --watch` | Run tests in watch mode (webApp/) |
| `npm run lint` | Run ESLint (webApp/) |
| `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web"` | Run web E2E tests |
| `open webApp/coverage/index.html` | View unit test coverage report |
| `open e2e-tests/target/cucumber-reports/web/index.html` | View E2E test report |

---

**Quickstart guide completed**: 2025-12-01  
**Ready for**: Implementation (Phase 2 - Tasks)

