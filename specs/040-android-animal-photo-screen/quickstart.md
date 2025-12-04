# Quickstart: Android Animal Photo Screen

**Feature**: 040-android-animal-photo-screen  
**Date**: 2025-12-02

## Prerequisites

1. Android Studio Hedgehog (2023.1.1) or later
2. JDK 17+
3. Android SDK 34 (target) with API 24+ (min)
4. Device or emulator running Android 7.0+

## Build & Run

```bash
# From repository root
./gradlew :composeApp:assembleDebug

# Install on connected device
./gradlew :composeApp:installDebug
```

## Run Tests

```bash
# Unit tests
./gradlew :composeApp:testDebugUnitTest

# Unit tests with coverage report
./gradlew :composeApp:testDebugUnitTest koverHtmlReport
# View report: composeApp/build/reports/kover/html/index.html
```

## Key Files to Modify

### MVI State & Intents

```
composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/mvi/
├── ReportMissingUiState.kt      # Add PhotoAttachmentState
├── ReportMissingIntent.kt       # Add photo intents
├── ReportMissingEffect.kt       # Add ShowToast, LaunchPhotoPicker
└── ReportMissingReducer.kt      # Add photo state reducers
```

### ViewModel

```
composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/viewmodels/
└── PhotoViewModel.kt            # NEW: Photo step ViewModel (follows ChipNumberViewModel pattern)
```

### UI Components

```
composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/photo/
├── PhotoScreen.kt               # Add photo picker launcher
├── PhotoContent.kt              # Replace placeholder with real UI
└── components/
    ├── PhotoConfirmationCard.kt # NEW: Thumbnail + metadata + remove
    └── PhotoEmptyState.kt       # NEW: Browse button + helper text
```

### Utilities

```
composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/core/util/
└── FileSizeFormatter.kt         # NEW: Format bytes to KB/MB
```

## Implementation Order

### Phase 1: Data Layer (No UI changes yet)

1. Create `PhotoAttachmentState` data class
2. Create `PhotoStatus` enum
3. Extend `ReportMissingUiState` with `photoAttachment` field
4. Add photo intents to `ReportMissingIntent`
5. Add effects to `ReportMissingEffect`
6. Implement reducers in `ReportMissingReducer`
7. Write unit tests for reducers

### Phase 2: ViewModel Layer

1. Create `PhotoViewModel` following `ChipNumberViewModel` pattern
2. Integrate with `ReportMissingFlowState` for photo data persistence
3. Add photo metadata extraction logic
4. Implement Continue validation for photo step (show toast if no photo)
5. Write unit tests for ViewModel

### Phase 3: UI Layer

1. Create `FileSizeFormatter` utility
2. Create `PhotoEmptyState` composable (Browse button + helper)
3. Create `PhotoConfirmationCard` composable (thumbnail + info + remove)
4. Update `PhotoContent` to use new components
5. Update `PhotoScreen` to launch photo picker
6. Add test tags to all interactive elements
7. Create preview functions with `PreviewParameterProvider`

### Phase 4: Integration & E2E

1. Wire up photo picker with fallback logic
2. Implement URI permission handling
3. Test on physical device
4. Create E2E test scenarios

## Test Tags Reference

| Element | Test Tag |
|---------|----------|
| Browse button | `animalPhoto.browseButton` |
| Remove button | `animalPhoto.removeButton` |
| Continue button | `animalPhoto.continueButton` |
| Filename text | `animalPhoto.filename` |
| File size text | `animalPhoto.fileSize` |
| Thumbnail image | `animalPhoto.thumbnail` |
| Loading indicator | `animalPhoto.loadingIndicator` |
| Content container | `reportMissing.photo.content` |

## Manual Testing Checklist

### Happy Path

- [ ] Tap "Browse" → Photo Picker opens
- [ ] Select JPG/PNG image → Confirmation card appears
- [ ] Thumbnail shows selected photo
- [ ] Filename displayed (truncated if >20 chars)
- [ ] File size shows in KB/MB format
- [ ] Tap "Continue" → Navigates to Description step
- [ ] Navigate back → Photo still selected
- [ ] Rotate device → Photo persists

### Validation

- [ ] Tap "Continue" without photo → Toast "Photo is mandatory" shows with Android standard long duration (`Toast.LENGTH_LONG`)
- [ ] Toast disappears after the standard long duration (~3.5s)
- [ ] Select photo → Remove (X) → Confirmation card disappears
- [ ] After remove, "Continue" shows toast again

### Edge Cases

- [ ] Cancel Photo Picker → Returns to empty state (photo not selected)
- [ ] Select very large file → Handles gracefully
- [ ] Process death (use "Don't keep activities") → Photo NOT restored (expected, consistent with chip number)
- [ ] Navigate back from any step → Photo preserved (state NOT cleared on back)
- [ ] Exit flow entirely (leave nav graph) → Draft cleared

> Accessibility audits (TalkBack announcements) are deferred and not part of this milestone.

## Dependencies

No new dependencies required. Uses existing:

- `io.coil-kt:coil-compose` - Image loading (already in project)
- `androidx.activity:activity-compose` - Activity Result APIs (already in project)

## Troubleshooting

### Photo Picker Not Available

On older devices without Photo Picker support, the code automatically falls back to `ACTION_GET_CONTENT`. To test fallback:

```kotlin
// Force fallback in debug builds
if (BuildConfig.DEBUG && forcePickerFallback) {
    // Use GetContent instead of PickVisualMedia
}
```

### URI Permission Denied

If photo becomes inaccessible after selection:

1. Ensure `takePersistableUriPermission()` is called immediately after selection
2. Check that URI is from a content provider (starts with `content://`)
3. Verify permission flags include `FLAG_GRANT_READ_URI_PERMISSION`

### State Not Persisting

If state lost on rotation or navigation:

1. Verify `PhotoViewModel` is initialized with `ReportMissingFlowState`
2. Check that `flowState.updatePhoto()` is called before navigation
3. Verify ViewModel initializes from `flowState.data.value` on creation
4. Note: State does NOT survive process death (consistent with chip number step)

