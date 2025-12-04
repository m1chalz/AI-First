# Quickstart Guide: Web Announcement Submission

**Feature**: Web announcement submission integration  
**Date**: 2025-12-03  
**Audience**: Developers implementing this feature

## Prerequisites

Before starting development, ensure you have:

- **Node.js v24 (LTS)** installed
- **Backend server** (`/server`) set up and running
- **Web application** (`/webApp`) set up and running
- **Database** initialized with migrations (backend requirement)
- **Git** on the feature branch: `043-web-announcement-submission`

---

## Environment Setup

### 1. Backend Server Setup

Start the backend server in development mode:

```bash
# From repository root
cd server

# Install dependencies (if not already done)
npm install

# Run database migrations
npm run knex:migrate

# Start development server
npm run dev
```

**Expected Output**:
```
Server running on http://localhost:3000
Database connected
```

**Verify Backend**:
```bash
# Test GET /api/v1/announcements endpoint
curl http://localhost:3000/api/v1/announcements

# Expected: {"data": [...]}
```

---

### 2. Web Application Setup

Start the web application in development mode:

```bash
# From repository root
cd webApp

# Install dependencies (if not already done)
npm install

# Start development server
npm run start
```

**Expected Output**:
```
VITE v5.x.x ready in XXX ms

➜  Local:   http://localhost:5173/
➜  Network: use --host to expose
```

**Verify Web App**:
- Open http://localhost:5173/ in your browser
- You should see the Pet List screen
- Click "Report Missing Pet" button to enter the flow

---

## Development Workflow

### Step 1: Rename Existing Service & Create New Files

**Rename existing service** (consolidate announcement operations):

```bash
# Rename service file
mv src/services/animal-repository.ts src/services/announcement-service.ts

# Rename test file
mv src/__tests__/services/animal-repository.test.ts src/__tests__/services/announcement-service.test.ts
```

**Create new files** in `/webApp/src/`:

**Models** (kebab-case filenames):
```bash
touch src/models/announcement-submission.ts
touch src/models/api-error.ts
```

**Hook** (kebab-case filename):
```bash
touch src/hooks/use-announcement-submission.ts
mkdir -p src/hooks/__tests__
touch src/hooks/__tests__/use-announcement-submission.test.ts
```

---

### Step 2: Implement Data Models

**File**: `src/models/announcement-submission.ts`

Implement the interfaces defined in [data-model.md](./data-model.md):
- `AnnouncementSubmissionDto`
- `AnnouncementResponse`
- Helper function: `mapFlowStateToDto()`

**File**: `src/models/api-error.ts`

Implement the error types defined in [data-model.md](./data-model.md):
- `ApiError` (discriminated union)
- `NetworkError`, `ValidationError`, `DuplicateMicrochipError`, `ServerError`
- Type guards: `isApiError()`, `isNetworkError()`, etc.

**Verification**:
```bash
# TypeScript should compile without errors
npm run type-check
```

---

### Step 3: Update Renamed Announcement Service

**File**: `src/services/announcement-service.ts` (renamed from `animal-repository.ts`)

1. **Rename class and export**:
```typescript
// BEFORE:
export class AnimalRepository { ... }
export const animalRepository = new AnimalRepository();

// AFTER:
export class AnnouncementService { ... }
export const announcementService = new AnnouncementService();
```

2. **Keep existing methods**: `getAnimals()`, `getPetById()`

3. **Add new POST methods**:
```typescript
import config from '../config/config';
import type { Animal } from '../types/animal';
import type { AnnouncementSubmissionDto, AnnouncementResponse } from '../models/announcement-submission';
import type { ApiError } from '../models/api-error';

export class AnnouncementService {
  // EXISTING: Keep these methods unchanged
  async getAnimals(): Promise<Animal[]> { ... }
  async getPetById(id: string): Promise<Animal> { ... }
  
  // NEW: Add POST methods
  async createAnnouncement(dto: AnnouncementSubmissionDto): Promise<AnnouncementResponse> {
    // TODO: Implement POST /api/v1/announcements
  }

  async uploadPhoto(announcementId: string, photo: File, managementPassword: string): Promise<void> {
    // TODO: Implement POST /api/v1/announcements/:id/photos with Basic Auth
  }
}

export const announcementService = new AnnouncementService();
```

4. **Update imports in other files**:
```bash
# Find all files importing animal-repository
grep -r "from.*animal-repository" src/

# Update each file:
# BEFORE: import { animalRepository } from '../services/animal-repository';
# AFTER: import { announcementService } from '../services/announcement-service';
```

**Files to update**:
- `src/hooks/use-animal-list.ts`
- `src/hooks/use-pet-details.ts`
- `src/hooks/__tests__/hooks/use-animal-list.test.ts`
- `src/hooks/__tests__/use-pet-details.test.ts`

**Reference**: See [contracts/announcements-api.md](./contracts/announcements-api.md) for API details.

**Update Test File**:
1. Rename describe block: `'AnimalRepository'` → `'AnnouncementService'`
2. Rename test instances: `AnimalRepository` → `AnnouncementService`
3. Keep existing GET tests unchanged
4. Add new POST tests for `createAnnouncement()` and `uploadPhoto()`

**Test-Driven Development**:
1. Write NEW tests for POST methods in `src/__tests__/services/announcement-service.test.ts` FIRST
2. Run tests: `npm test -- announcement-service.test.ts`
3. Implement POST methods to pass tests
4. Verify coverage: `npm test -- --coverage announcement-service.test.ts`

---

### Step 4: Implement Submission Hook

**File**: `src/hooks/use-announcement-submission.ts`

Follow the hook pattern from `use-animal-list.ts`:

```typescript
import { useState } from 'react';
import { announcementService } from '../services/announcement-service';
import type { ReportMissingPetFlowState } from '../models/ReportMissingPetFlow';
import type { ApiError } from '../models/api-error';

export function useAnnouncementSubmission() {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<ApiError | null>(null);
  const [managementPassword, setManagementPassword] = useState<string | null>(null);

  const submitAnnouncement = async (flowState: ReportMissingPetFlowState): Promise<boolean> => {
    // TODO: Implement submission logic
  };

  return { isSubmitting, error, managementPassword, submitAnnouncement };
}
```

**Test-Driven Development**:
1. Write tests in `src/hooks/__tests__/use-announcement-submission.test.ts` FIRST
2. Mock `announcementService` in tests
3. Run tests: `npm test -- use-announcement-submission.test.ts`
4. Implement hook logic to pass tests

---

### Step 5: Update ContactScreen Component

**File**: `src/components/ReportMissingPet/ContactScreen.tsx`

Integrate the submission hook:

```typescript
import { useAnnouncementSubmission } from '../../hooks/use-announcement-submission';

export function ContactScreen() {
  const { flowState } = useReportMissingPetFlow();
  const { isSubmitting, error, managementPassword, submitAnnouncement } = useAnnouncementSubmission();
  const { showToast } = useToast();

  const handleContinue = async () => {
    // Validate contact form first (existing logic)
    const isValid = validateContactForm();
    if (!isValid) return;

    // Submit announcement
    const success = await submitAnnouncement(flowState);
    
    if (success) {
      // Navigate to summary with management password
      navigate(ReportMissingPetRoutes.summary, { state: { managementPassword } });
    } else if (error) {
      // Display error via toast
      showToast(getErrorMessage(error));
    }
  };

  return (
    <ReportMissingPetLayout>
      {/* Existing form fields */}
      
      <button
        onClick={handleContinue}
        disabled={isSubmitting}
        className={styles.primaryButton}
        data-testid="contact.continue.button"
      >
        {isSubmitting ? 'Submitting...' : 'Continue'}
      </button>
    </ReportMissingPetLayout>
  );
}
```

**Update Tests**:
- Modify `src/components/ReportMissingPet/__tests__/ContactScreen.test.ts`
- Mock `useAnnouncementSubmission` hook
- Test successful submission flow
- Test error handling scenarios

---

### Step 6: Update SummaryScreen Component

**File**: `src/components/ReportMissingPet/SummaryScreen.tsx`

Display management password and add exit confirmation:

```typescript
import { useLocation } from 'react-router-dom';
import { useEffect } from 'react';

export function SummaryScreen() {
  const location = useLocation();
  const managementPassword = location.state?.managementPassword;

  // Exit confirmation warning
  useEffect(() => {
    const handleBeforeUnload = (e: BeforeUnloadEvent) => {
      e.preventDefault();
      e.returnValue = ''; // Chrome requires returnValue to be set
    };
    
    window.addEventListener('beforeunload', handleBeforeUnload);
    
    return () => {
      window.removeEventListener('beforeunload', handleBeforeUnload);
    };
  }, []);

  return (
    <ReportMissingPetLayout title="Summary" progress="4/4" onBack={handleBack}>
      {/* Management Password Display */}
      <div className={styles.passwordCard} data-testid="summary.password.card">
        <h3>Your Management Password</h3>
        <div className={styles.password} data-testid="summary.password.text">
          {managementPassword}
        </div>
        <p>Save this password! You'll need it to edit or delete your announcement.</p>
      </div>

      {/* Existing flow state summary */}
    </ReportMissingPetLayout>
  );
}
```

**Update Tests**:
- Modify `src/components/ReportMissingPet/__tests__/SummaryScreen.test.ts`
- Test management password display
- Test exit confirmation (mock `beforeunload` event)

---

## Testing Strategy

### Unit Tests

Run unit tests for specific files:

```bash
# Test service
npm test -- announcement-service.test.ts

# Test hook
npm test -- use-announcement-submission.test.ts

# Test components
npm test -- ContactScreen.test.ts
npm test -- SummaryScreen.test.ts
```

Run all tests with coverage:

```bash
npm test -- --coverage
```

**Coverage Requirements**:
- Target: 80% line + branch coverage
- Check report: `webApp/coverage/index.html`

---

### Manual Testing

#### Test Case 1: Successful Submission

1. Navigate to http://localhost:5173/report-missing
2. Enter microchip number (15 digits)
3. Upload a photo (< 10MB)
4. Fill in animal details (species, sex, age, breed, description)
5. Fill in contact info (at least email or phone)
6. Click "Continue"
7. **Expected**: Loading indicator appears
8. **Expected**: Navigate to Summary screen
9. **Expected**: Management password displayed
10. **Expected**: Warning when trying to leave page

#### Test Case 2: Duplicate Microchip Error

1. Create an announcement with microchip number `123456789012345`
2. Try to create another announcement with the same microchip
3. Click "Continue" on contact screen
4. **Expected**: Error toast: "This microchip already exists. If this is your announcement, use your management password to update it."
5. **Expected**: Form data preserved (can retry with different microchip)

#### Test Case 3: Network Error

1. Stop the backend server: `Ctrl+C` in backend terminal
2. Complete the flow and click "Continue"
3. **Expected**: Error toast: "Network error. Please check your connection."
4. **Expected**: Form data preserved
5. Restart backend server: `npm run dev`
6. Click "Continue" again
7. **Expected**: Successful submission

#### Test Case 4: Validation Error (Missing Contact)

1. Complete flow without entering email or phone
2. Click "Continue"
3. **Expected**: Error toast: "Please provide at least one contact method"
4. **Expected**: Stay on contact screen

---

### End-to-End Tests

Create E2E test file:

```bash
# Create feature file
mkdir -p e2e-tests/src/test/resources/features/web
touch e2e-tests/src/test/resources/features/web/043-announcement-submission.feature
```

Run E2E tests:

```bash
# From repository root
mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web and @043"
```

**Expected Output**:
- All scenarios pass
- Report generated at `e2e-tests/target/cucumber-reports/web/index.html`

---

## Debugging Tips

### Backend API Issues

**Problem**: 500 Internal Server Error

**Solution**:
1. Check backend logs in terminal
2. Verify database connection: `npm run knex:migrate:status`
3. Test endpoint manually: `curl -X POST http://localhost:3000/api/v1/announcements -H "Content-Type: application/json" -d '{"species":"dog","sex":"male","locationLatitude":52.2,"locationLongitude":21.0,"email":"test@example.com","lastSeenDate":"2025-12-03","status":"MISSING"}'`

---

### Photo Upload Issues

**Problem**: 401 Unauthorized on photo upload

**Solution**:
1. Verify management password is correct
2. Check Authorization header format:
   ```typescript
   console.log('Authorization header:', `Basic ${btoa(`${id}:${password}`)}`);
   ```
3. Verify announcement ID exists: `curl http://localhost:3000/api/v1/announcements/{id}`

---

### Frontend Error Handling Issues

**Problem**: Errors not displayed

**Solution**:
1. Check browser console for error logs
2. Verify toast hook is called: Add `console.log` in `handleContinue`
3. Verify error mapping in service: Check `ApiError` type construction

---

## Performance Verification

**Success Criteria Validation**:

1. **SC-001**: Submission time < 10 seconds
   - Use browser DevTools Network tab
   - Measure time from "Continue" click to Summary screen load
   - Expected: < 10s for announcement + photo upload

2. **SC-005**: Error display < 2 seconds
   - Simulate error (stop backend)
   - Measure time from "Continue" click to toast appearance
   - Expected: < 2s

3. **SC-004**: Photos up to 10MB supported
   - Upload a 10MB photo
   - Expected: Successful upload (may take longer, but should succeed)

---

## Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| TypeScript compilation errors | Run `npm run type-check` and fix type issues |
| Tests failing | Check mock setup in test files |
| Backend not responding | Verify backend is running on port 3000 |
| Photo upload timeout | Increase timeout in service implementation |
| Management password not displayed | Verify navigation state passing |

---

## Next Steps

After completing implementation:

1. Run full test suite: `npm test -- --coverage`
2. Verify 80% coverage achieved
3. Run E2E tests: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web"`
4. Manual testing of all user stories
5. Create pull request with implementation

---

**Quickstart Guide Complete**: Ready for implementation!

