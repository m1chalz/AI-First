# Research: Web Animal Photo Screen

**Feature**: `037-web-animal-photo-screen`  
**Date**: 2025-12-01  
**Status**: Complete

## Research Summary

This feature implements step 2/4 of the "report missing pet" flow for the web platform. Technical research focused on identifying reusable patterns and components from the existing step 1/4 implementation (Microchip Number Screen) to maximize code reuse and maintain architectural consistency.

## Key Findings

### 1. Existing Architecture Patterns (REUSE)

**Decision**: Follow the exact same architectural pattern as MicrochipNumberScreen  
**Rationale**: Consistency across flow steps, proven patterns, minimal learning curve  
**Alternatives considered**:
- Creating a new pattern → Rejected: Would introduce inconsistency and increase maintenance burden
- Using third-party upload library → Rejected: Adds unnecessary dependency, native HTML5 APIs sufficient

**Pattern to follow**:
```typescript
// MicrochipNumberScreen pattern (PROVEN):
export function PhotoScreen() {
  const navigate = useNavigate();
  const { flowState, updateFlowState, clearFlowState } = useReportMissingPetFlow();
  const { /* photo-specific state */ } = usePhotoUpload(flowState.photo);

  const handleContinue = () => {
    updateFlowState({ photo: photoData, currentStep: FlowStep.Details });
    navigate('/report-missing/details');
  };

  const handleBack = () => {
    clearFlowState();
    navigate('/');
  };

  useBrowserBackHandler(handleBack);

  return (
    <ReportMissingPetLayout
      title="Animal photo"
      progress="2/4"
      onBack={handleBack}
    >
      {/* Photo-specific content */}
    </ReportMissingPetLayout>
  );
}
```

### 2. Flow State Management (REUSE + MINOR UPDATE)

**Decision**: Extend existing `ReportMissingPetFlowContext` to include photo data  
**Rationale**: Maintains consistency with microchip storage pattern, single source of truth  
**Alternatives considered**:
- Separate photo context → Rejected: Adds complexity, violates single flow state principle
- Component-local state only → Rejected: Photo needs to persist across step navigation

**State structure update**:
```typescript
// BEFORE (existing):
export interface ReportMissingPetFlowState {
  currentStep: FlowStep;
  microchipNumber: string;
}

// AFTER (this feature):
export interface ReportMissingPetFlowState {
  currentStep: FlowStep;
  microchipNumber: string;
  photo: PhotoAttachment | null;  // NEW
}

export interface PhotoAttachment {
  file: File;                      // File object for upload
  filename: string;                // Display name
  size: number;                    // Bytes
  mimeType: string;                // e.g., "image/jpeg"
  previewUrl: string | null;       // blob: URL for preview (optional)
}
```

### 3. File Upload Implementation (NEW CUSTOM HOOK)

**Decision**: Create `use-photo-upload` custom hook following `use-microchip-formatter` pattern  
**Rationale**: Encapsulates file handling logic, testable in isolation, reusable pattern  
**Alternatives considered**:
- Inline file handling in component → Rejected: Harder to test, violates separation of concerns
- Third-party upload library (e.g., react-dropzone) → Rejected: Adds 50KB dependency for features we can implement in <100 lines

**Hook API design** (inspired by use-microchip-formatter):
```typescript
export function usePhotoUpload(initialPhoto: PhotoAttachment | null) {
  const [photo, setPhoto] = useState<PhotoAttachment | null>(initialPhoto);
  const [error, setError] = useState<string | null>(null);
  const [isDragOver, setIsDragOver] = useState(false);

  const validateFile = (file: File): string | null => {
    // Returns error message or null if valid
  };

  const handleFileSelect = (file: File) => {
    const validationError = validateFile(file);
    if (validationError) {
      setError(validationError);
      return;
    }
    
    const previewUrl = URL.createObjectURL(file);
    setPhoto({
      file,
      filename: file.name,
      size: file.size,
      mimeType: file.type,
      previewUrl,
    });
    setError(null);
  };

  const handleDrop = (e: React.DragEvent) => { /* ... */ };
  const handleDragOver = (e: React.DragEvent) => { /* ... */ };
  const handleDragLeave = () => { /* ... */ };
  const removePhoto = () => { /* cleanup + reset */ };

  return {
    photo,
    error,
    isDragOver,
    handleFileSelect,
    handleDrop,
    handleDragOver,
    handleDragLeave,
    removePhoto,
  };
}
```

### 4. File Validation Rules (NEW UTILITIES)

**Decision**: Pure utility functions for validation (separate from hook)  
**Rationale**: Easy to test, reusable, follows existing utility pattern (microchip-formatter.ts)  
**Alternatives considered**:
- Validation inside hook only → Rejected: Harder to test in isolation
- Server-side validation only → Rejected: Spec requires client-side validation for immediate feedback

**Validation utilities**:
```typescript
// /webApp/src/utils/file-validation.ts

export const ALLOWED_MIME_TYPES = [
  'image/jpeg',
  'image/png',
  'image/gif',
  'image/webp',
  'image/bmp',
  'image/tiff',
  'image/heic',
  'image/heif',
];

export const MAX_FILE_SIZE_BYTES = 20 * 1024 * 1024; // 20MB

export function validateFileMimeType(file: File): boolean {
  return ALLOWED_MIME_TYPES.includes(file.type);
}

export function validateFileSize(file: File): boolean {
  return file.size <= MAX_FILE_SIZE_BYTES;
}

export function getFileValidationError(file: File): string | null {
  if (!validateFileMimeType(file)) {
    return 'Please upload JPG, PNG, GIF, WEBP, BMP, TIFF, HEIC, or HEIF format';
  }
  if (!validateFileSize(file)) {
    return 'File size exceeds 20MB limit';
  }
  return null;
}
```

### 5. Toast Notifications (NEW COMPONENT/HOOK)

**Decision**: Create simple toast notification component/hook for validation errors and mandatory photo message  
**Rationale**: Spec requires toast notifications for validation feedback (3-5 seconds), minimal UI pattern  
**Alternatives considered**:
- Third-party toast library (react-hot-toast, react-toastify) → Rejected: Adds dependency for simple requirement
- Alert dialogs → Rejected: Too intrusive, spec specifically calls for toast notifications
- Inline error messages only → Rejected: Spec requires toast for mandatory photo enforcement

**Toast implementation approach**:
```typescript
// Simple hook for toast state management
export function useToast() {
  const [message, setMessage] = useState<string | null>(null);
  
  const showToast = (msg: string, duration = 3000) => {
    setMessage(msg);
    setTimeout(() => setMessage(null), duration);
  };
  
  return { message, showToast };
}

// Toast component (simple CSS-based)
export function Toast({ message }: { message: string | null }) {
  if (!message) return null;
  
  return (
    <div className={styles.toast}>
      {message}
    </div>
  );
}
```

### 6. Drag-and-Drop Implementation (NATIVE HTML5)

**Decision**: Use native HTML5 drag-and-drop events (no library)  
**Rationale**: Native API sufficient for requirements, no external dependencies  
**Alternatives considered**:
- react-dropzone library → Rejected: Adds 50KB for features we need only ~30 lines to implement
- File API only (no drag-and-drop) → Rejected: Spec explicitly requires drag-and-drop support

**Implementation pattern**:
```typescript
const handleDrop = (e: React.DragEvent<HTMLDivElement>) => {
  e.preventDefault();
  e.stopPropagation();
  setIsDragOver(false);
  
  const files = e.dataTransfer.files;
  if (files && files.length > 0) {
    handleFileSelect(files[0]); // Accept first file only
  }
};

const handleDragOver = (e: React.DragEvent<HTMLDivElement>) => {
  e.preventDefault();
  e.stopPropagation();
  setIsDragOver(true);
};

const handleDragLeave = () => {
  setIsDragOver(false);
};
```

### 7. Responsive Layout and Styling (REUSE)

**Decision**: Reuse 100% of existing `ReportMissingPetLayout.module.css` styles  
**Rationale**: All required styles already exist (heading, description, buttons, responsive breakpoints)  
**Alternatives considered**: None - existing styles fully support requirements

**Reusable styles from existing CSS**:
- `.heading` - main heading style
- `.description` - explanatory text style
- `.primaryButton` - Continue button (with hover/active states)
- `.secondaryButton` - optional secondary actions
- `.pageContainer` - page layout
- `.contentCard` - white card with border
- `.contentInner` - inner content wrapper
- Responsive breakpoints (@media queries) - already defined

**New styles needed** (PhotoScreen-specific):
- `.dropZone` - drag-and-drop area with border and hover state
- `.confirmationCard` - green card showing uploaded file info
- `.toast` - toast notification styling

### 8. Browser Navigation Handling (REUSE)

**Decision**: Reuse existing `useBrowserBackHandler` hook unchanged  
**Rationale**: Proven pattern from step 1/4, handles browser back button correctly  
**Alternatives considered**: None - existing solution fully meets requirements

### 9. Routing and Navigation (REUSE + MINOR UPDATE)

**Decision**: Add new route to existing `report-missing-pet-routes.tsx` file  
**Rationale**: Maintains route cohesion, easy to add step 2/4 alongside step 1/4  
**Alternatives considered**: None - existing routing pattern is ideal

**Route update**:
```typescript
// BEFORE (existing):
<Route path="/report-missing" element={<ReportMissingPetFlowProvider />}>
  <Route path="microchip" element={<MicrochipNumberScreen />} />
</Route>

// AFTER (this feature):
<Route path="/report-missing" element={<ReportMissingPetFlowProvider />}>
  <Route path="microchip" element={<MicrochipNumberScreen />} />
  <Route path="photo" element={<PhotoScreen />} />  {/* NEW */}
</Route>
```

### 10. Memory Management (NEW PATTERN)

**Decision**: Use `URL.createObjectURL()` for preview and clean up with `URL.revokeObjectURL()`  
**Rationale**: Prevents memory leaks from blob URLs, follows web best practices  
**Alternatives considered**:
- Base64 encoding → Rejected: Increases memory usage 33%, slower for large images
- No preview → Rejected: Spec allows preview, improves UX

**Cleanup pattern**:
```typescript
useEffect(() => {
  // Cleanup blob URL when component unmounts or photo changes
  return () => {
    if (photo?.previewUrl) {
      URL.revokeObjectURL(photo.previewUrl);
    }
  };
}, [photo]);
```

## Testing Strategy

### Unit Tests (Vitest + React Testing Library)

**Components to test**:
1. `PhotoScreen.test.tsx` - component integration tests
   - File picker selection
   - Drag-and-drop events
   - Validation error display
   - Continue button behavior (with/without photo)
   - Back button navigation
   - Toast notification display

2. `use-photo-upload.test.ts` - hook tests
   - File selection and state updates
   - Drag-and-drop event handling
   - File validation (MIME type, size)
   - Preview URL creation and cleanup
   - Remove photo functionality

3. `file-validation.test.ts` - utility tests
   - MIME type validation (valid/invalid formats)
   - File size validation (under/over 20MB)
   - Error message generation

4. `format-file-size.test.ts` - utility tests
   - Byte formatting (KB, MB display)

### E2E Tests (Selenium + Cucumber)

**Scenarios** (Gherkin in `animal-photo-screen.feature`):
1. User uploads photo via file picker → confirmation card displayed → Continue navigates to step 3/4
2. User uploads photo via drag-and-drop → confirmation card displayed
3. User attempts to upload invalid format → validation error toast displayed (5 seconds)
4. User attempts to upload oversized file → validation error toast displayed (5 seconds)
5. User clicks Continue without photo → mandatory photo toast displayed (3 seconds)
6. User uploads photo, removes it, clicks Continue → mandatory photo toast displayed
7. User clicks back arrow → returns to pet list, flow state cleared

## Dependencies

**No new dependencies required** - all features implemented with existing dependencies:
- React (existing)
- React Router (existing)
- Vitest + React Testing Library (existing)
- Native browser APIs (File API, FileReader, URL.createObjectURL, drag-and-drop events)

## Risk Assessment

**Low Risk**:
- Reusing proven patterns from step 1/4
- Native browser APIs (well-supported in target browsers)
- Minimal new code (mostly hook + utilities)
- No external dependencies added

**Medium Risk**:
- Browser inconsistencies in drag-and-drop behavior (mitigated: test in all target browsers)
- Memory leaks from blob URLs (mitigated: proper cleanup in useEffect)

**High Risk**: None identified

## Next Steps (Phase 1: Design & Contracts)

1. Create `data-model.md` - document PhotoAttachment entity and flow state extension
2. Create `quickstart.md` - developer setup instructions (reusing existing setup from step 1/4)
3. No API contracts needed (client-side only feature)
4. Update agent context with new technology decisions (toast notifications, drag-and-drop patterns)

---

**Research Complete**: All technical unknowns resolved. Ready to proceed to Phase 1: Design & Contracts.

