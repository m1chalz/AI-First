# Research: Android Owner's Details Screen

**Feature**: 045-android-owners-details-screen  
**Date**: 2025-12-04

## Research Tasks

### 1. Backend API Integration Pattern

**Context**: Screen requires 2-step submission - announcement creation then photo upload.

**Decision**: Use existing Ktor HTTP client with OkHttp engine and suspend functions.

**Rationale**:
- Project already uses Ktor client (visible in `build.gradle.kts` dependencies)
- Ktor provides native coroutine support with suspend functions
- Content negotiation plugin handles JSON serialization via kotlinx.serialization
- Basic auth for photo upload handled via request header

**Alternatives considered**:
- Retrofit: Not used in current codebase, would add inconsistency
- Raw OkHttp: Lower-level than needed, Ktor provides cleaner Kotlin-first API

### 2. Flow State Extension Pattern

**Context**: Need to add `rewardDescription` field to shared flow state.

**Decision**: Extend existing `FlowData` data class in `ReportMissingFlowState.kt`.

**Rationale**:
- `FlowData` already has `contactPhone` and `contactEmail` fields
- Adding `rewardDescription: String = ""` follows existing pattern
- Add `updateRewardDescription()` method matching other update methods

**Alternatives considered**:
- Separate state class for Step 4: Unnecessary complexity, flow state handles all steps
- Store in ViewModel only: Would lose data on back/forward navigation

### 3. Validation Pattern

**Context**: Phone (7-11 digits) and email (RFC 5322) validation on submit.

**Decision**: Create `OwnerDetailsValidator` utility class with pure functions.

**Rationale**:
- Matches `AnimalDescriptionValidator` pattern from spec 042
- Pure functions are easily unit-tested
- Validation triggered only on ContinueClicked intent

**Validation Rules**:
```kotlin
// Phone: Extract digits, allow leading +, require 7-11 digits
fun validatePhone(phone: String): ValidationResult {
    val digits = phone.filter { it.isDigit() || it == '+' }
                      .let { if (it.startsWith("+")) it.drop(1) else it }
                      .filter { it.isDigit() }
    return if (digits.length in 7..11) ValidationResult.Valid
           else ValidationResult.Invalid("Enter at least 7 digits")
}

// Email: Basic RFC 5322 pattern
fun validateEmail(email: String): ValidationResult {
    val trimmed = email.trim()
    val pattern = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
    return if (pattern.matches(trimmed)) ValidationResult.Valid
           else ValidationResult.Invalid("Enter a valid email address")
}
```

**Alternatives considered**:
- Android Patterns.EMAIL_ADDRESS: Too strict for some valid emails
- Real-time validation: Spec requires validation on submit only

### 4. 2-Step Submission Orchestration

**Context**: Must complete announcement creation AND photo upload before navigation.

**Decision**: Create `SubmitAnnouncementUseCase` that orchestrates both steps.

**Rationale**:
- Single responsibility: ViewModel doesn't know about 2-step process
- Testable: Mock repository for unit tests
- Handles both success and failure atomically

**Flow**:
```
SubmitAnnouncementUseCase.invoke(flowData)
  1. POST /api/v1/announcements → AnnouncementResponse(id, managementPassword)
  2. POST /api/v1/announcements/{id}/photos with Basic auth (id:managementPassword)
  3. Return Result.success(managementPassword) or Result.failure(exception)
```

**Alternatives considered**:
- ViewModel orchestrates: Violates separation of concerns
- Two separate use cases: Complicates error handling and retry logic

### 5. Basic Auth for Photo Upload

**Context**: Photo upload requires Basic auth with `id:managementPassword` credentials.

**Decision**: Build Authorization header manually in repository.

**Rationale**:
- Single endpoint needs this auth pattern
- No need for OkHttp interceptor (would affect all requests)
- Standard Base64 encoding: `Basic ${Base64.encode("$id:$managementPassword")}`

**Implementation**:
```kotlin
val credentials = "$announcementId:$managementPassword"
val basicAuth = "Basic " + Base64.encodeToString(
    credentials.toByteArray(), 
    Base64.NO_WRAP
)
// Add header: Authorization: $basicAuth
```

**Alternatives considered**:
- OkHttp Authenticator: Designed for 401 challenge-response, not preemptive auth
- Custom interceptor: Overkill for single endpoint

### 6. MVI Effect Pattern for Navigation

**Context**: Navigate to summary with managementPassword after successful submission.

**Decision**: Use `SharedFlow<OwnerDetailsUiEffect>` for one-off navigation events.

**Rationale**:
- Matches existing pattern in ChipNumber, Photo, Description screens
- Effects consumed once, not replayed on recomposition
- `NavigateToSummary(managementPassword: String)` carries required data

**Effect Types**:
```kotlin
sealed interface OwnerDetailsUiEffect {
    data class NavigateToSummary(val managementPassword: String) : OwnerDetailsUiEffect
    data object NavigateBack : OwnerDetailsUiEffect
    data class ShowSnackbar(val message: String, val action: SnackbarAction?) : OwnerDetailsUiEffect
}

sealed interface SnackbarAction {
    data object Retry : SnackbarAction
    data object Dismiss : SnackbarAction
}
```

**Alternatives considered**:
- Navigation via UiState: Would cause navigation on recomposition
- Direct NavController access: Violates MVI pattern

### 7. Composable Screen Pattern

**Context**: Constitution requires two-layer pattern for testability and previews.

**Decision**: `ContactDetailsScreen` (state host) + `ContactDetailsContent` (stateless).

**Rationale**:
- Matches existing pattern in ChipNumberScreen, PhotoScreen, DescriptionScreen
- Stateless composable enables preview with PreviewParameterProvider
- Clear separation of ViewModel collection from UI rendering

**Structure**:
```kotlin
// State host - collects state, dispatches intents
@Composable
fun ContactDetailsScreen(viewModel: OwnerDetailsViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.effects.collect { /* handle effects */ } }
    ContactDetailsContent(
        state = state,
        onPhoneChanged = { viewModel.dispatchIntent(UpdatePhone(it)) },
        onEmailChanged = { viewModel.dispatchIntent(UpdateEmail(it)) },
        // ... other callbacks
    )
}

// Stateless - pure UI rendering
@Composable
fun ContactDetailsContent(
    state: OwnerDetailsUiState,
    onPhoneChanged: (String) -> Unit = {},
    onEmailChanged: (String) -> Unit = {},
    // ...
) { /* UI implementation */ }
```

**Alternatives considered**:
- Single composable: No preview support, harder to test
- State hoisting via parameters: More boilerplate, less readable

### 8. Snackbar with Retry Action

**Context**: Submission errors show Snackbar with "Retry" action.

**Decision**: Use `SnackbarHostState` in Scaffold with effect-driven messages.

**Rationale**:
- Material 3 Snackbar supports action buttons
- Effect-driven: ShowSnackbar effect triggers snackbar display
- Retry action dispatches `RetryClicked` intent

**Implementation**:
```kotlin
val snackbarHostState = remember { SnackbarHostState() }

LaunchedEffect(Unit) {
    viewModel.effects.collect { effect ->
        when (effect) {
            is ShowSnackbar -> {
                val result = snackbarHostState.showSnackbar(
                    message = effect.message,
                    actionLabel = if (effect.action == SnackbarAction.Retry) "Retry" else null
                )
                if (result == SnackbarResult.ActionPerformed && effect.action == SnackbarAction.Retry) {
                    viewModel.dispatchIntent(RetryClicked)
                }
            }
            // ... other effects
        }
    }
}
```

**Alternatives considered**:
- Toast: No action button support
- AlertDialog: Too intrusive for transient errors

## Summary

All unknowns resolved. Ready for Phase 1 design artifacts.

| Area | Decision |
|------|----------|
| HTTP Client | Ktor with OkHttp engine + suspend functions |
| Flow State | Extend FlowData with rewardDescription |
| Validation | Pure functions in OwnerDetailsValidator |
| Submission | SubmitAnnouncementUseCase orchestrates 2-step |
| Photo Auth | Manual Basic auth header in repository |
| Navigation | SharedFlow<UiEffect> with NavigateToSummary |
| Composables | Two-layer pattern (Screen + Content) |
| Error Display | Snackbar with Retry action via effect |

