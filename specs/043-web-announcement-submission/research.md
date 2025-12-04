# Research: Web Missing Pet Announcement Submission

**Feature**: Web announcement submission integration  
**Date**: 2025-12-03  
**Status**: Complete

## Research Questions & Decisions

### 1. Backend API Integration Patterns

**Question**: What HTTP client pattern should be used for API calls?

**Investigation**:
- Reviewed existing codebase:
  - `animal-repository.ts` uses native `fetch` API
  - `config.ts` provides `apiBaseUrl` for environment-specific URLs
  - No external HTTP libraries (Axios, etc.) in use
  - Existing service already handles GET /api/v1/announcements
- Patterns found:
  ```typescript
  const response = await fetch(`${config.apiBaseUrl}/api/v1/endpoint`);
  if (!response.ok) {
    throw new Error(`Failed: ${response.status} ${response.statusText}`);
  }
  return await response.json();
  ```

**Decision**: Rename `animal-repository.ts` to `announcement-service.ts` and extend with POST methods

**Implementation Strategy**:
1. Rename file: `animal-repository.ts` → `announcement-service.ts`
2. Rename class: `AnimalRepository` → `AnnouncementService`
3. Update export: `animalRepository` → `announcementService`
4. Keep existing GET methods: `getAnimals()`, `getPetById()`
5. Add new POST methods: `createAnnouncement()`, `uploadPhoto()`
6. Update all imports across codebase

**Rationale**:
- Consistent with existing codebase patterns
- No additional dependencies needed
- Native browser API with broad support
- Adequate for project needs (simple REST calls)

**Alternatives Considered**:
- Axios: Rejected - adds dependency, not needed for simple requests
- Custom HTTP wrapper: Rejected - over-engineering for current needs

---

### 2. Photo Upload with Multipart/Form-Data

**Question**: How should photo files be uploaded to the backend?

**Investigation**:
- Backend uses multer middleware (`upload-middleware.ts`) expecting multipart/form-data
- Backend expects field name: `photo`
- Backend route: `POST /api/v1/announcements/:id/photos`
- Browser `FormData` API:
  ```typescript
  const formData = new FormData();
  formData.append('photo', file);
  ```

**Decision**: Use `FormData` API with `fetch` for photo upload

**Rationale**:
- Native browser API, no dependencies
- Works seamlessly with backend multer middleware
- Proper MIME type handling
- Progress events available if needed later

**Implementation Pattern**:
```typescript
const formData = new FormData();
formData.append('photo', photoFile);

const response = await fetch(`${config.apiBaseUrl}/api/v1/announcements/${id}/photos`, {
  method: 'POST',
  headers: {
    'Authorization': `Basic ${btoa(`${id}:${managementPassword}`)}`
  },
  body: formData
});
```

**Alternatives Considered**:
- Base64 encoding: Rejected - larger payload (33% overhead), requires different backend handling
- Blob URLs: Rejected - requires different backend handling, no benefit over FormData

---

### 3. Management Password Authentication

**Question**: How should the management password be sent for photo upload authentication?

**Investigation**:
- Reviewed backend middleware:
  - `basic-auth-middleware.ts`: Parses HTTP Basic Auth from `Authorization` header
  - `announcement-auth-middleware.ts`: Validates announcement access using Basic Auth
- Expected format: `Authorization: Basic ${base64(username:password)}`
- For announcements: username = announcement ID, password = management password

**Decision**: Use HTTP Basic Auth with announcement ID as username and management password as password

**Rationale**:
- Backend already expects Basic Auth format
- Standard HTTP authentication mechanism
- Browser handles encoding via `btoa()`
- Consistent with backend middleware design

**Implementation Pattern**:
```typescript
const credentials = `${announcementId}:${managementPassword}`;
const encodedCredentials = btoa(credentials);

headers: {
  'Authorization': `Basic ${encodedCredentials}`
}
```

**Alternatives Considered**:
- Bearer token: Rejected - backend uses Basic Auth, would require backend changes
- Custom header (X-Management-Password): Rejected - non-standard, backend expects Basic Auth
- Query parameter: Rejected - insecure, passwords should not be in URLs

---

### 4. Error Handling Strategy

**Question**: How should API errors be handled and displayed to users?

**Investigation**:
- Existing patterns:
  - `use-toast.ts`: Toast notifications for transient messages
  - `use-animal-list.ts`: Error state in hooks, error prop in components
  - Error types needed: Network, validation (400), duplicate microchip (409), server (500)
- Requirements:
  - FR-011: Preserve form data on failure
  - FR-009, FR-010: User-friendly error messages
  - Clarification: Display errors via toast

**Decision**: Use toast notifications for errors, preserve form data via ReportMissingPetFlowContext

**Rationale**:
- Consistent with existing error handling
- Non-blocking UX (user can see error and retry)
- Form data already persisted in context (no additional work)
- Toast auto-dismisses, keeping UI clean

**Error Handling Pattern**:
```typescript
try {
  await submitAnnouncement();
} catch (error) {
  if (error instanceof ApiError) {
    switch (error.type) {
      case 'duplicate_microchip':
        showToast('This microchip already exists. If this is your announcement, use your management password to update it.');
        break;
      case 'validation':
        showToast(`Validation error: ${error.message}`);
        break;
      case 'network':
        showToast('Network error. Please check your connection and try again.');
        break;
      default:
        showToast('An error occurred. Please try again.');
    }
  }
}
```

**Alternatives Considered**:
- Modal dialogs: Rejected - blocking, worse UX
- Inline error messages: Rejected - less visible, requires additional UI changes
- Silent failure: Rejected - terrible UX

---

### 5. Loading State Management

**Question**: How should loading states be managed during async submission?

**Investigation**:
- Existing patterns:
  - `use-animal-list.ts`: `isLoading` state in hook
  - `use-pet-details.ts`: `isLoading` state with loading indicator
  - Components: Disable buttons during loading
- Submission involves two sequential API calls:
  1. POST /announcements (creates announcement)
  2. POST /announcements/:id/photos (uploads photo)

**Decision**: Use React state in custom hook (`isSubmitting`, `submissionError`)

**Rationale**:
- Follows existing patterns in codebase
- Simple, predictable state management
- Easy to test
- Covers both API calls with single loading state

**Implementation Pattern**:
```typescript
export function useAnnouncementSubmission() {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<ApiError | null>(null);
  
  const submitAnnouncement = async (flowState) => {
    setIsSubmitting(true);
    setError(null);
    
    try {
      // Create announcement
      const response = await announcementService.createAnnouncement(dto);
      
      // Upload photo
      await announcementService.uploadPhoto(response.id, photo, response.managementPassword);
      
      return true;
    } catch (err) {
      setError(err);
      return false;
    } finally {
      setIsSubmitting(false);
    }
  };
  
  return { isSubmitting, error, submitAnnouncement };
}
```

**Alternatives Considered**:
- Global loading context: Rejected - overkill for single feature
- No loading state: Rejected - poor UX, users need feedback
- Separate loading states for each API call: Rejected - unnecessary complexity

---

### 6. Exit Confirmation on Summary Screen

**Question**: How should exit confirmation be implemented when user navigates away from summary screen?

**Investigation**:
- Requirement: User Story 3, Acceptance Scenario 3 - warn user to save password before leaving
- Navigation types to handle:
  - Browser back button
  - Browser close/tab close
  - URL navigation
- Browser APIs:
  - `beforeunload` event: Fires before page unload, allows confirmation dialog
  - React Router: No built-in prompt in v6 (removed from v5)

**Decision**: Use browser `beforeunload` event for navigation away warning

**Rationale**:
- Standard browser API
- Covers all navigation types (back, close, URL change)
- Browser displays native confirmation dialog
- Simple implementation with `useEffect`

**Implementation Pattern**:
```typescript
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
```

**Alternatives Considered**:
- React Router prompt: Rejected - removed in v6, doesn't cover browser close
- Custom modal: Rejected - can't intercept browser close/back button
- No confirmation: Rejected - violates requirement (User Story 3, Scenario 3)

---

## Technology Stack Summary

| Component | Technology | Rationale |
|-----------|-----------|-----------|
| HTTP Client | Native `fetch` API | Consistent with existing code, no dependencies |
| Photo Upload | `FormData` API | Native support, works with backend multer |
| Authentication | HTTP Basic Auth | Backend expects Basic Auth format |
| Error Handling | Toast notifications | Consistent UX, non-blocking |
| Loading State | React `useState` | Simple, follows existing patterns |
| Exit Confirmation | `beforeunload` event | Covers all navigation types |
| Type Safety | TypeScript interfaces | Compile-time safety, IDE support |
| Testing | Vitest + React Testing Library | Existing test infrastructure |

---

## Integration Points

**Existing Code to Reuse**:
1. **HTTP Pattern**: `animal-repository.ts` - native fetch with config.apiBaseUrl
2. **Error Display**: `use-toast.ts` - toast notifications
3. **Flow State**: `ReportMissingPetFlowContext` - form data persistence
4. **Styling**: `ReportMissingPetLayout.module.css` - consistent UI styling
5. **Test Patterns**: Existing test files for hooks and components

**New Code to Create**:
1. **Service**: `announcement-service.ts` - API calls for submission
2. **Hook**: `use-announcement-submission.ts` - submission logic and state
3. **Models**: `announcement-submission.ts`, `api-error.ts` - TypeScript types
4. **Tests**: Unit tests for service, hook, and modified components

---

## Risk Analysis

| Risk | Mitigation |
|------|-----------|
| Network failure during submission | Preserve form data, display error, allow retry |
| Photo upload timeout | Use reasonable timeout (30s), display error, allow retry |
| Duplicate microchip number | Display helpful error message with guidance |
| Browser compatibility | Use native APIs with broad support (fetch, FormData) |
| Missing form data | Validate before submission (FR-012, FR-013) |

---

## Next Steps (Phase 1)

1. Create `data-model.md` with TypeScript interfaces
2. Create API contracts in `contracts/` directory
3. Create `quickstart.md` with developer setup instructions
4. Update agent context with new technologies (if any)

---

**Research Complete**: All unknowns resolved, ready for Phase 1 design.

