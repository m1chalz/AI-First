# Quickstart: Web Animal Photo Screen

**Feature**: `037-web-animal-photo-screen`  
**Date**: 2025-12-01  
**Status**: Complete

## Overview

This quickstart guide provides setup instructions for developing and testing the Animal Photo Screen (step 2/4 of the "report missing pet" flow). This feature heavily reuses existing infrastructure from step 1/4 (Microchip Number Screen), so setup is minimal.

## Prerequisites

- **Node.js**: v24 (LTS) - same as backend requirement
- **npm**: v10+ (comes with Node.js)
- **Git**: Repository already cloned at `/Users/pawelkedra/code/AI-First`
- **Browser**: Chrome, Firefox, Safari, or Edge (modern versions with ES2015+ support)

## Repository Structure

```
/Users/pawelkedra/code/AI-First/
├── webApp/                          # Web application (React + TypeScript)
│   ├── src/
│   │   ├── components/
│   │   │   └── ReportMissingPet/    # Flow components (step 1/4 already exists)
│   │   ├── contexts/                # React Context for flow state
│   │   ├── hooks/                   # Custom hooks
│   │   ├── models/                  # TypeScript types/interfaces
│   │   ├── routes/                  # React Router routes
│   │   ├── utils/                   # Utility functions
│   │   └── __tests__/               # Unit tests (Vitest + React Testing Library)
│   ├── package.json
│   └── vite.config.ts
├── e2e-tests/                       # E2E tests (Selenium + Cucumber)
│   ├── src/test/resources/features/web/  # Gherkin scenarios
│   ├── src/test/java/.../pages/          # Page Object Model
│   ├── src/test/java/.../steps-web/      # Step definitions
│   └── pom.xml                      # Maven configuration
└── specs/037-web-animal-photo-screen/  # This feature spec
```

## Development Setup

### 1. Install Web App Dependencies

**First time only** (skip if you've already set up the web app for step 1/4):

```bash
cd /Users/pawelkedra/code/AI-First/webApp
npm install
```

**Expected output**:
```
added X packages in Ys
```

### 2. Start Development Server

```bash
cd /Users/pawelkedra/code/AI-First/webApp
npm run start
```

**Expected output**:
```
VITE vX.X.X  ready in XXX ms

➜  Local:   http://localhost:5173/
➜  Network: use --host to expose
```

**Browser**: Open `http://localhost:5173/` - you should see the PetSpot web app.

### 3. Verify Existing Flow (Step 1/4)

Before implementing step 2/4, verify that step 1/4 (Microchip Number Screen) is working:

1. Navigate to `http://localhost:5173/`
2. Click "Report Missing Pet" (or equivalent action to start flow)
3. You should see the Microchip Number Screen:
   - Header with back arrow, "Microchip number" title, and "1/4" progress
   - Heading: "Identification by Microchip"
   - Input field with formatting (00000-00000-00000)
   - Continue button

If step 1/4 works correctly, the infrastructure is ready for step 2/4 implementation.

## Key Files to Understand (Before Implementation)

Review these existing files to understand the patterns you'll follow:

### 1. Flow State Management (REUSE)

**File**: `/webApp/src/contexts/ReportMissingPetFlowContext.tsx`

- Manages flow state via React Context
- Provides `updateFlowState()` and `clearFlowState()` methods
- You'll extend `ReportMissingPetFlowState` to include photo data

### 2. Layout Components (REUSE)

**Files**:
- `/webApp/src/components/ReportMissingPet/ReportMissingPetLayout.tsx` - Layout wrapper
- `/webApp/src/components/ReportMissingPet/Header.tsx` - Header with back arrow
- `/webApp/src/components/ReportMissingPet/ReportMissingPetLayout.module.css` - Shared styles

All styles you need already exist in `ReportMissingPetLayout.module.css` (heading, description, buttons, inputs).

### 3. Custom Hooks (PATTERN REFERENCE)

**File**: `/webApp/src/hooks/use-microchip-formatter.ts`

- Shows the pattern for creating form input hooks
- You'll create `use-photo-upload.ts` following this pattern

**File**: `/webApp/src/hooks/use-browser-back-handler.ts`

- Handles browser back button (REUSE unchanged)

### 4. Screen Component (PATTERN REFERENCE)

**File**: `/webApp/src/components/ReportMissingPet/MicrochipNumberScreen.tsx`

- Shows the exact pattern for implementing flow screens
- Your `PhotoScreen.tsx` will follow this structure:
  1. Import layout, hooks, and context
  2. Define `handleContinue()` - save to flow state, navigate to next step
  3. Define `handleBack()` - clear flow state, navigate to pet list
  4. Use `useBrowserBackHandler(handleBack)`
  5. Return `<ReportMissingPetLayout>` with screen content

## Running Tests

### Unit Tests (Vitest + React Testing Library)

**Run all web app tests**:
```bash
cd /Users/pawelkedra/code/AI-First/webApp
npm test
```

**Run tests in watch mode** (during development):
```bash
npm test -- --watch
```

**Run tests with coverage**:
```bash
npm test -- --coverage
```

**Coverage report location**: `/webApp/coverage/index.html`

**Expected coverage**: 80%+ line and branch coverage for new code

### E2E Tests (Selenium + Cucumber)

**Prerequisites**:
- Java 11+ installed
- Maven installed
- WebDriver binaries (ChromeDriver, GeckoDriver) in PATH or managed by WebDriverManager

**Run web E2E tests only**:
```bash
cd /Users/pawelkedra/code/AI-First/e2e-tests
mvn test -Dcucumber.filter.tags="@web"
```

**Expected output**:
```
[INFO] --- maven-surefire-plugin:X.X.X:test (default-test) @ petspot-e2e-tests ---
[INFO] Running com.intive.aifirst.petspot.RunCucumberTest
...
[INFO] Tests run: X, Failures: 0, Errors: 0, Skipped: 0
```

**E2E report location**: `/e2e-tests/target/cucumber-reports/web/index.html`

## Implementation Checklist

Use this checklist to track your implementation progress:

### Phase 1: Data Model & State Management

- [ ] Update `/webApp/src/models/ReportMissingPetFlow.ts`:
  - [ ] Add `PhotoAttachment` interface
  - [ ] Add `photo: PhotoAttachment | null` to `ReportMissingPetFlowState`
  - [ ] Update `initialFlowState` to include `photo: null`
  - [ ] Add `FlowStep.Photo` enum value

- [ ] Update `/webApp/src/contexts/ReportMissingPetFlowContext.tsx`:
  - [ ] No code changes needed (already supports any `Partial<ReportMissingPetFlowState>`)
  - [ ] Update tests to include photo state

### Phase 2: Utilities

- [ ] Create `/webApp/src/utils/file-validation.ts`:
  - [ ] `ALLOWED_MIME_TYPES` constant
  - [ ] `MAX_FILE_SIZE_BYTES` constant
  - [ ] `validateFileMimeType(file: File): boolean`
  - [ ] `validateFileSize(file: File): boolean`
  - [ ] `getFileValidationError(file: File): string | null`

- [ ] Create `/webApp/src/utils/format-file-size.ts`:
  - [ ] `formatFileSize(bytes: number): string` (e.g., "2.5 MB")

- [ ] Create tests:
  - [ ] `/webApp/src/utils/__tests__/file-validation.test.ts`
  - [ ] `/webApp/src/utils/__tests__/format-file-size.test.ts`

### Phase 3: Custom Hook

- [ ] Create `/webApp/src/hooks/use-photo-upload.ts`:
  - [ ] State: `photo`, `error`, `isDragOver`
  - [ ] `handleFileSelect(file: File)` - validate and set photo
  - [ ] `handleDrop(e: React.DragEvent)` - handle drop event
  - [ ] `handleDragOver(e: React.DragEvent)` - handle drag over
  - [ ] `handleDragLeave()` - handle drag leave
  - [ ] `removePhoto()` - clear photo and revoke blob URL
  - [ ] `useEffect` for blob URL cleanup

- [ ] Create tests:
  - [ ] `/webApp/src/hooks/__tests__/use-photo-upload.test.ts`

### Phase 4: Toast Notification

- [ ] Create `/webApp/src/hooks/use-toast.ts`:
  - [ ] State: `message`, `duration`
  - [ ] `showToast(message: string, duration?: number)` - display toast

- [ ] Create `/webApp/src/components/Toast/Toast.tsx`:
  - [ ] Simple component rendering toast notification
  - [ ] Auto-hide after duration

- [ ] Create `/webApp/src/components/Toast/Toast.module.css`:
  - [ ] Toast styling (bottom center, fade in/out)

- [ ] Create tests:
  - [ ] `/webApp/src/hooks/__tests__/use-toast.test.ts`
  - [ ] `/webApp/src/components/Toast/__tests__/Toast.test.tsx`

### Phase 5: PhotoScreen Component

- [ ] Create `/webApp/src/components/ReportMissingPet/PhotoScreen.tsx`:
  - [ ] Import layout, hooks, context
  - [ ] Use `useReportMissingPetFlow()`
  - [ ] Use `usePhotoUpload(flowState.photo)`
  - [ ] Use `useBrowserBackHandler(handleBack)`
  - [ ] Use `useToast()`
  - [ ] Define `handleContinue()` - validate photo, update flow state, navigate
  - [ ] Define `handleBack()` - clear flow state, navigate to pet list
  - [ ] Render `<ReportMissingPetLayout>` with:
    - [ ] Upload area (empty state / confirmation card)
    - [ ] Browse button + hidden file input
    - [ ] Drag-and-drop handlers
    - [ ] Continue button
    - [ ] Toast notification

- [ ] Create tests:
  - [ ] `/webApp/src/components/ReportMissingPet/__tests__/PhotoScreen.test.tsx`

### Phase 6: Routing

- [ ] Update `/webApp/src/routes/report-missing-pet-routes.tsx`:
  - [ ] Add route for `/report-missing/photo` → `<PhotoScreen />`

### Phase 7: E2E Tests

- [ ] Create `/e2e-tests/src/test/resources/features/web/animal-photo-screen.feature`:
  - [ ] Gherkin scenarios for all user stories

- [ ] Create `/e2e-tests/src/test/java/.../pages/AnimalPhotoPage.java`:
  - [ ] Page Object Model with XPath locators for all elements

- [ ] Create `/e2e-tests/src/test/java/.../steps-web/AnimalPhotoSteps.java`:
  - [ ] Step definitions implementing Gherkin scenarios

### Phase 8: Verification

- [ ] Run unit tests: `npm test -- --coverage` (80%+ coverage)
- [ ] Run E2E tests: `mvn test -Dcucumber.filter.tags="@web"`
- [ ] Manual testing in all target browsers (Chrome, Firefox, Safari, Edge)
- [ ] Verify responsive layout (mobile 320px, tablet 768px, desktop 1024px+)
- [ ] Verify browser back button handling
- [ ] Verify browser refresh handling (state cleared)
- [ ] Verify direct URL access handling (redirect to step 1/4)

## Common Development Commands

### Web App

```bash
# Start dev server
npm run start

# Run tests (watch mode)
npm test -- --watch

# Run tests with coverage
npm test -- --coverage

# Build for production
npm run build

# Run linter
npm run lint

# Type checking
npm run type-check  # (if configured)
```

### E2E Tests

```bash
# Run all E2E tests
mvn test

# Run web tests only
mvn test -Dcucumber.filter.tags="@web"

# Run specific scenario
mvn test -Dcucumber.filter.tags="@web and @photo-upload"

# Clean and run tests
mvn clean test
```

## Troubleshooting

### Issue: Dev server not starting

**Symptoms**: `npm run start` fails or hangs

**Solutions**:
1. Delete `node_modules` and reinstall: `rm -rf node_modules package-lock.json && npm install`
2. Check port 5173 is not in use: `lsof -i :5173`
3. Clear Vite cache: `rm -rf webApp/.vite`

### Issue: Tests failing with "Cannot find module"

**Symptoms**: Import errors in tests

**Solutions**:
1. Ensure all dependencies installed: `npm install`
2. Check `tsconfig.json` path mappings are correct
3. Restart Vitest watch mode

### Issue: File input not working in tests

**Symptoms**: File upload tests fail

**Solutions**:
1. Use `new File()` constructor with proper MIME type in tests
2. Mock `URL.createObjectURL` in tests: `global.URL.createObjectURL = vi.fn(() => 'mock-blob-url')`
3. Mock FileReader if needed

### Issue: E2E tests cannot find elements

**Symptoms**: Selenium cannot locate elements by XPath

**Solutions**:
1. Verify `data-testid` attributes are present in components
2. Verify XPath in Page Object Model matches actual HTML
3. Add explicit waits in E2E tests: `WebDriverWait`
4. Check if element is in shadow DOM (unlikely for React, but possible)

## Next Steps

1. Review existing implementation (step 1/4) to understand patterns
2. Follow implementation checklist from Phase 1 → Phase 8
3. Run tests frequently during development
4. Commit changes following project commit conventions
5. Create PR for review

## Additional Resources

- **React Testing Library docs**: https://testing-library.com/docs/react-testing-library/intro/
- **Vitest docs**: https://vitest.dev/
- **Selenium WebDriver docs**: https://www.selenium.dev/documentation/
- **Cucumber docs**: https://cucumber.io/docs/cucumber/
- **MDN File API**: https://developer.mozilla.org/en-US/docs/Web/API/File
- **MDN Drag and Drop**: https://developer.mozilla.org/en-US/docs/Web/API/HTML_Drag_and_Drop_API

---

**Quickstart Complete**: Developer setup instructions ready. Follow the implementation checklist to build the feature.

