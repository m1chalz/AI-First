# Data Model: Web Animal Photo Screen

**Feature**: `037-web-animal-photo-screen`  
**Date**: 2025-12-01  
**Status**: Complete

## Overview

This document defines the data entities and state management for the Animal Photo Screen (step 2/4 of the "report missing pet" flow). The data model extends the existing `ReportMissingPetFlowState` to include photo attachment data.

## Entities

### 1. PhotoAttachment

Represents a photo file selected by the user for the missing pet report.

**TypeScript Interface**:
```typescript
export interface PhotoAttachment {
  file: File;                      // Native File object for upload
  filename: string;                // Display name (extracted from file.name)
  size: number;                    // File size in bytes
  mimeType: string;                // MIME type (e.g., "image/jpeg")
  previewUrl: string | null;       // Blob URL for preview (optional, cleanup required)
}
```

**Attributes**:

| Field | Type | Required | Description | Validation Rules |
|-------|------|----------|-------------|------------------|
| `file` | `File` | Yes | Native browser File object containing image data | Must be valid File instance |
| `filename` | `string` | Yes | Original filename for display purposes | Non-empty string, max 255 chars |
| `size` | `number` | Yes | File size in bytes | Must be > 0 and ≤ 20,971,520 (20MB) |
| `mimeType` | `string` | Yes | MIME type of the image file | Must be one of: `image/jpeg`, `image/png`, `image/gif`, `image/webp` |
| `previewUrl` | `string \| null` | No | Blob URL created via `URL.createObjectURL()` for preview | Valid blob: URL or null |

**Lifecycle**:
1. **Creation**: Created when user selects/drops a valid image file
2. **Validation**: MIME type and size validated before creation
3. **Storage**: Stored in React Context (`ReportMissingPetFlowState.photo`)
4. **Persistence**: Survives forward/backward navigation within active flow session
5. **Cleanup**: Blob URL revoked when photo removed, component unmounts, or flow cancelled
6. **Destruction**: Cleared when user cancels flow or completes submission

**Example**:
```typescript
const photoAttachment: PhotoAttachment = {
  file: selectedFile,  // File object from input or drop event
  filename: "my-dog-photo.jpg",
  size: 2048576,  // 2MB
  mimeType: "image/jpeg",
  previewUrl: "blob:http://localhost:3000/abc123def456"  // Generated blob URL
};
```

### 2. ReportMissingPetFlowState (Extended)

Represents the complete state of the 4-step "report missing pet" flow.

**TypeScript Interface** (updated):
```typescript
export enum FlowStep {
  Microchip = 'microchip',  // Step 1/4
  Photo = 'photo',          // Step 2/4 - NEW
  Details = 'details',      // Step 3/4 - future
  Contact = 'contact',      // Step 4/4 - future
  Completed = 'completed',
}

export interface ReportMissingPetFlowState {
  currentStep: FlowStep;           // Current step in flow
  microchipNumber: string;         // From step 1/4 (optional, empty string if not provided)
  photo: PhotoAttachment | null;   // From step 2/4 (NEW - mandatory for completion)
  // Future steps will add more fields (description, location, contact info)
}

export const initialFlowState: ReportMissingPetFlowState = {
  currentStep: FlowStep.Microchip,
  microchipNumber: '',
  photo: null,  // NEW
};
```

**Attributes**:

| Field | Type | Required | Description | Default Value |
|-------|------|----------|-------------|---------------|
| `currentStep` | `FlowStep` | Yes | Current active step in the flow | `FlowStep.Microchip` |
| `microchipNumber` | `string` | No | 15-digit microchip number (digits only, no hyphens) | `''` (empty string) |
| `photo` | `PhotoAttachment \| null` | No* | Uploaded photo attachment | `null` |

*Note: Photo is optional in flow state but mandatory for flow completion (enforced via UI validation)

**State Transitions**:
```
Initial State → Microchip (1/4) → Photo (2/4) → Details (3/4) → Contact (4/4) → Completed

State at each step:
- Step 1/4: { currentStep: 'microchip', microchipNumber: '', photo: null }
- Step 2/4: { currentStep: 'photo', microchipNumber: '123...', photo: null }
- After photo upload: { currentStep: 'photo', microchipNumber: '123...', photo: PhotoAttachment }
- Click Continue (valid): Navigate to step 3/4
- Click Back Arrow (any step): Clear all state, return to pet list
```

### 3. UploadUIState (Component-Local State)

Represents the UI state of the photo upload component (not stored in global flow state).

**TypeScript Interface**:
```typescript
interface UploadUIState {
  isDragOver: boolean;       // Whether user is dragging file over drop zone
  error: string | null;      // Current validation error message (or null if no error)
  toastMessage: string | null;  // Current toast notification message (or null if hidden)
}
```

**Attributes**:

| Field | Type | Required | Description | Default Value |
|-------|------|----------|-------------|---------------|
| `isDragOver` | `boolean` | Yes | Indicates if file is being dragged over drop zone (for visual feedback) | `false` |
| `error` | `string \| null` | Yes | Current validation error message to display | `null` |
| `toastMessage` | `string \| null` | Yes | Current toast notification message | `null` |

**State Transitions**:
```
Empty State:
  { isDragOver: false, error: null, toastMessage: null }

Drag Over:
  { isDragOver: true, error: null, toastMessage: null }

Validation Error (oversized file):
  { isDragOver: false, error: "File size exceeds 20MB limit", toastMessage: "File size exceeds 20MB limit" }

Valid File Selected:
  { isDragOver: false, error: null, toastMessage: null }

Continue Without Photo:
  { isDragOver: false, error: null, toastMessage: "Photo is mandatory" }
```

## Validation Rules

### PhotoAttachment Validation

**MIME Type Validation**:
- **Rule**: File MUST have MIME type matching one of: `image/jpeg`, `image/png`, `image/gif`, `image/webp`, `image/bmp`, `image/tiff`, `image/heic`, `image/heif`
- **Enforcement**: Client-side validation in `use-photo-upload` hook
- **Error Message**: "Please upload JPG, PNG, GIF, WEBP, BMP, TIFF, HEIC, or HEIF format"
- **Duration**: Toast displayed for 5 seconds

**File Size Validation**:
- **Rule**: File size MUST NOT exceed 20,971,520 bytes (20MB)
- **Enforcement**: Client-side validation in `use-photo-upload` hook
- **Error Message**: "File size exceeds 20MB limit"
- **Duration**: Toast displayed for 5 seconds

**Mandatory Photo Validation**:
- **Rule**: User MUST attach photo before proceeding to step 3/4
- **Enforcement**: UI validation on Continue button click (button remains enabled, toast shown if no photo)
- **Error Message**: "Photo is mandatory"
- **Duration**: Toast displayed for 3 seconds

**File Loading Validation**:
- **Rule**: File MUST load successfully via FileReader or file input
- **Enforcement**: Error handling in `use-photo-upload` hook
- **Error Message**: "Failed to load file"
- **Duration**: Toast displayed for 5 seconds

## State Management Architecture

### Context API (React Context)

**Provider**: `ReportMissingPetFlowProvider`  
**Location**: `/webApp/src/contexts/ReportMissingPetFlowContext.tsx`  
**Scope**: Entire "report missing pet" flow (wraps all 4 steps)

**API**:
```typescript
export interface ReportMissingPetFlowContextValue {
  flowState: ReportMissingPetFlowState;
  updateFlowState: (updates: Partial<ReportMissingPetFlowState>) => void;
  clearFlowState: () => void;
}

// Usage in PhotoScreen:
const { flowState, updateFlowState, clearFlowState } = useReportMissingPetFlow();

// Save photo to flow state:
updateFlowState({
  photo: photoAttachment,
  currentStep: FlowStep.Details
});

// Cancel flow (clears all state):
clearFlowState();
```

### Custom Hooks

**`use-photo-upload` Hook**:
- **Purpose**: Encapsulates photo file handling, validation, and preview URL management
- **Location**: `/webApp/src/hooks/use-photo-upload.ts`
- **State**: Component-local state (not in global flow state)
- **Lifecycle**: Manages blob URL creation and cleanup via `useEffect`

```typescript
export function usePhotoUpload(initialPhoto: PhotoAttachment | null) {
  const [photo, setPhoto] = useState<PhotoAttachment | null>(initialPhoto);
  const [error, setError] = useState<string | null>(null);
  const [isDragOver, setIsDragOver] = useState(false);

  // ... validation and event handlers ...

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

## State Persistence Rules

### What Persists

✅ **Persists across forward/backward navigation within active flow session**:
- Microchip number (from step 1/4)
- Photo attachment (from step 2/4) - including File object, filename, size, MIME type
- Blob preview URL (regenerated if needed)
- Current step indicator

### What Does NOT Persist

❌ **Does NOT persist across browser refresh**:
- All flow state cleared
- User returned to pet list
- Must restart flow from step 1/4

❌ **Does NOT persist after flow cancellation** (back arrow click):
- All flow state cleared immediately
- User returned to pet list
- Browser back button treated same as in-app back arrow

❌ **Does NOT persist after flow completion**:
- Flow state cleared after successful submission
- User redirected to confirmation or pet list

## Memory Management

### Blob URL Lifecycle

**Creation**:
```typescript
const previewUrl = URL.createObjectURL(file);
setPhoto({ ...photoData, previewUrl });
```

**Cleanup** (mandatory to prevent memory leaks):
```typescript
useEffect(() => {
  // Cleanup on unmount or when photo changes
  return () => {
    if (photo?.previewUrl) {
      URL.revokeObjectURL(photo.previewUrl);
    }
  };
}, [photo]);
```

**Cleanup Triggers**:
1. Photo removed by user (X button clicked)
2. New photo selected (replaces existing photo)
3. Component unmounts (navigation away from screen)
4. Flow cancelled (back arrow clicked)

## Data Flow Diagram

```
┌─────────────────────────────────────────────────────────────┐
│  PhotoScreen Component                                       │
│                                                              │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  usePhotoUpload Hook (Component-Local State)           │ │
│  │  - photo: PhotoAttachment | null                       │ │
│  │  - error: string | null                                │ │
│  │  - isDragOver: boolean                                 │ │
│  └───────────────┬────────────────────────────────────────┘ │
│                  │                                           │
│                  │ File selected/dropped                     │
│                  ▼                                           │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  File Validation                                       │ │
│  │  - validateFileMimeType(file)                          │ │
│  │  - validateFileSize(file)                              │ │
│  │  - getFileValidationError(file)                        │ │
│  └───────────────┬────────────────────────────────────────┘ │
│                  │                                           │
│                  │ Valid file                                │
│                  ▼                                           │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  Create PhotoAttachment                                │ │
│  │  - file: File object                                   │ │
│  │  - previewUrl: URL.createObjectURL(file)               │ │
│  └───────────────┬────────────────────────────────────────┘ │
│                  │                                           │
│                  │ Click Continue                            │
│                  ▼                                           │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  updateFlowState({ photo, currentStep: 'details' })   │ │
│  └───────────────┬────────────────────────────────────────┘ │
└──────────────────┼───────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────┐
│  ReportMissingPetFlowContext (Global Flow State)            │
│                                                              │
│  flowState: {                                                │
│    currentStep: 'details',                                   │
│    microchipNumber: '123456789012345',                       │
│    photo: PhotoAttachment { file, filename, size, ... }      │
│  }                                                           │
└──────────────────┬───────────────────────────────────────────┘
                   │
                   ▼ Navigate to /report-missing/details
┌─────────────────────────────────────────────────────────────┐
│  DetailsScreen (Step 3/4)                                    │
│  - Access flowState.photo via useReportMissingPetFlow()      │
└─────────────────────────────────────────────────────────────┘
```

## Testing Considerations

### Unit Test Data Fixtures

**Valid PhotoAttachment**:
```typescript
const validPhoto: PhotoAttachment = {
  file: new File(['fake image data'], 'test-photo.jpg', { type: 'image/jpeg' }),
  filename: 'test-photo.jpg',
  size: 2048576,  // 2MB
  mimeType: 'image/jpeg',
  previewUrl: 'blob:http://localhost/mock-preview-url',
};
```

**Invalid Files for Testing**:
```typescript
// Oversized file (11MB)
const oversizedFile = new File(
  [new ArrayBuffer(11 * 1024 * 1024)],
  'large-photo.jpg',
  { type: 'image/jpeg' }
);

// Invalid MIME type
const invalidTypeFile = new File(
  ['fake pdf data'],
  'document.pdf',
  { type: 'application/pdf' }
);
```

### E2E Test Data

**Test Files** (to be placed in `/e2e-tests/fixtures/`):
- `valid-photo-2mb.jpg` - Valid 2MB JPEG for happy path tests
- `valid-photo-png.png` - Valid PNG for format variation testing
- `oversized-photo-11mb.jpg` - 11MB file for size validation testing
- `invalid-document.pdf` - PDF file for MIME type validation testing

---

**Data Model Complete**: All entities, validation rules, and state management patterns documented. Ready for implementation.
