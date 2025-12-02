# Research: Android Animal Photo Screen

**Feature**: 040-android-animal-photo-screen  
**Date**: 2025-12-02

## Research Tasks

### 1. Android Photo Picker API

**Question**: Best approach for photo selection on Android 7.0+ with Photo Picker availability on Android 13+?

**Decision**: Use `ActivityResultContracts.PickVisualMedia` as primary, with `ActivityResultContracts.GetContent` fallback.

**Rationale**:
- Photo Picker (`PickVisualMedia`) is the modern, privacy-preserving approach (no permission required on Android 13+)
- Available on Android 13+ natively, backported to Android 11+ via Google Play Services module updates
- For devices without Photo Picker support, `GetContent` with `image/*` MIME type provides equivalent functionality
- Both approaches use Activity Result API, enabling consistent code structure

**Alternatives Considered**:
1. **Direct MediaStore query**: Rejected - requires `READ_EXTERNAL_STORAGE` permission, more complex, worse UX
2. **Third-party picker libraries**: Rejected - unnecessary dependency, platform API is sufficient
3. **Camera capture only**: Rejected - spec explicitly defers camera to future milestone

**Implementation Pattern**:
```kotlin
// Primary: Photo Picker (Android 13+ or with backport)
val pickMedia = rememberLauncherForActivityResult(
    ActivityResultContracts.PickVisualMedia()
) { uri -> /* handle result */ }

// Fallback check
if (ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(context)) {
    pickMedia.launch(PickVisualMediaRequest(ImageOnly))
} else {
    // Fallback to GetContent
    getContent.launch("image/*")
}
```

---

### 2. URI Permission Persistence

**Question**: How to ensure selected photo URI remains accessible throughout the flow?

**Decision**: Take persistable URI permissions using `ContentResolver.takePersistableUriPermission()`.

**Rationale**:
- Content URIs from picker may become inaccessible after activity recreation
- `FLAG_GRANT_READ_URI_PERMISSION` is temporary; persistable permissions survive process death
- Required for FR-012: "app MUST take persistable URI permissions"

**Alternatives Considered**:
1. **Copy file to app storage**: Rejected - unnecessary storage usage, slower, complex cleanup
2. **Re-request on every access**: Rejected - poor UX, may fail if original source changed
3. **Store as Base64**: Rejected - memory intensive, defeats purpose of URI-based access

**Implementation Pattern**:
```kotlin
uri?.let {
    context.contentResolver.takePersistableUriPermission(
        it,
        Intent.FLAG_GRANT_READ_URI_PERMISSION
    )
}
```

---

### 3. Photo Metadata Extraction

**Question**: How to extract filename and file size from content URI?

**Decision**: Query `ContentResolver` with `OpenableColumns` projection.

**Rationale**:
- Standard Android approach for content URIs
- Works with any content provider (Photos, Files, Downloads)
- Provides `DISPLAY_NAME` and `SIZE` columns

**Alternatives Considered**:
1. **Parse URI path**: Rejected - content URIs don't expose filesystem paths
2. **Open InputStream and read**: Rejected - inefficient for just metadata
3. **Use DocumentFile**: Rejected - overkill for simple metadata query

**Implementation Pattern**:
```kotlin
fun getFileMetadata(context: Context, uri: Uri): Pair<String, Long>? {
    context.contentResolver.query(
        uri,
        arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
        null, null, null
    )?.use { cursor ->
        if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            val size = cursor.getLong(cursor.getColumnIndexOrThrow(OpenableColumns.SIZE))
            return Pair(name, size)
        }
    }
    return null
}
```

---

### 4. Thumbnail Loading in Compose

**Question**: Best approach for loading photo thumbnail in Jetpack Compose?

**Decision**: Use Coil (`AsyncImage` composable) - already a project dependency.

**Rationale**:
- Coil is Compose-first, lightweight, and already used in the project
- Handles caching, memory management, and placeholder/error states
- Supports content URIs natively

**Alternatives Considered**:
1. **Glide**: Rejected - heavier, less Compose-native integration
2. **Manual BitmapFactory**: Rejected - no caching, complex lifecycle handling
3. **Picasso**: Rejected - no official Compose support

**Implementation Pattern**:
```kotlin
AsyncImage(
    model = photoUri,
    contentDescription = "Selected photo",
    modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)),
    contentScale = ContentScale.Crop,
    placeholder = painterResource(R.drawable.ic_photo_placeholder),
    error = painterResource(R.drawable.ic_photo_error)
)
```

---

### 5. State Persistence Across Navigation

**Question**: How to persist photo selection across navigation while maintaining consistency with existing flow?

**Decision**: Use existing `ReportMissingFlowState` (nav graph scoped) - same pattern as chip number step.

**Rationale**:
- Consistent with chip number implementation (no SavedStateHandle there either)
- `ReportMissingFlowState` already handles `photoUri` field
- State survives navigation between steps and configuration changes
- Process death is acceptable loss for draft data (user can re-select photo)
- Simpler implementation, no Parcelable complexity

**Alternatives Considered**:
1. **SavedStateHandle**: Rejected - not used by chip number step, would break consistency
2. **Room database**: Rejected - overkill for temporary draft state
3. **SharedPreferences**: Rejected - not lifecycle-aware, requires manual cleanup

**Implementation Pattern**:
```kotlin
// Existing pattern from ChipNumberViewModel
class PhotoViewModel(
    private val flowState: ReportMissingFlowState,
    private val onNavigateToDescription: () -> Unit,
    private val onNavigateBack: () -> Unit,
) : ViewModel() {
    
    init {
        // Initialize from shared flow state
        viewModelScope.launch {
            val photoData = flowState.data.value.photoUri
            // ... restore state
        }
    }
    
    private fun handleContinueClicked() {
        // Save to shared state before navigation
        flowState.updatePhotoUri(/* uri */)
        onNavigateToDescription()
    }
}
```

---

### 6. Toast Display in MVI Architecture

**Question**: How to show toast message while maintaining MVI unidirectional flow?

**Decision**: Emit toast as `UiEffect` via `SharedFlow`, handle in Compose `LaunchedEffect`.

**Rationale**:
- Toasts are one-off events, not persistent state - fits `UiEffect` pattern
- Maintains separation: ViewModel decides WHEN to show, UI decides HOW
- Existing pattern in codebase (navigation effects)

**Alternatives Considered**:
1. **State-based with auto-clear**: Rejected - timing complexity, recomposition issues
2. **Direct Toast.show() in ViewModel**: Rejected - violates separation of concerns
3. **Snackbar in state**: Rejected - toast specifically requested, not snackbar

**Implementation Pattern**:
```kotlin
// In ViewModel
sealed interface ReportMissingEffect {
    // ... existing
    data class ShowToast(val message: String, val duration: Int = Toast.LENGTH_SHORT) : ReportMissingEffect
}

// In PhotoScreen
LaunchedEffect(viewModel) {
    viewModel.effects.collect { effect ->
        when (effect) {
            is ReportMissingEffect.ShowToast -> {
                Toast.makeText(context, effect.message, effect.duration).show()
            }
            // ... other effects
        }
    }
}
```

---

### 7. File Size Formatting

**Question**: How to format bytes to human-readable KB/MB?

**Decision**: Create simple utility function in `core/util/FileSizeFormatter.kt`.

**Rationale**:
- Simple, testable utility
- No external dependency needed for basic formatting
- Reusable across features

**Alternatives Considered**:
1. **Android Formatter.formatFileSize()**: Considered but may include unwanted locale formatting
2. **Third-party library**: Rejected - overkill for simple formatting

**Implementation Pattern**:
```kotlin
object FileSizeFormatter {
    fun format(bytes: Long): String = when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "%.1f KB".format(bytes / 1024.0)
        else -> "%.1f MB".format(bytes / (1024.0 * 1024.0))
    }
}
```

---

## Summary

All technical decisions align with existing project patterns:
- MVI architecture preserved
- Koin DI unchanged (ViewModel already scoped)
- Jetpack Compose patterns followed
- State persistence via ReportMissingFlowState (nav graph scoped, not SavedStateHandle)
- Photo Picker with GetContent fallback for broad device support

