# Quickstart: Android Owner's Details Screen

**Feature**: 045-android-owners-details-screen  
**Date**: 2025-12-04

## Prerequisites

- Android Studio (latest stable)
- JDK 17+
- Running backend server (see `/server/README.md`)

## Setup Steps

### 1. Checkout Branch

```bash
git checkout 045-android-owners-details-screen
```

### 2. Sync Gradle

Open project in Android Studio and let it sync dependencies.

### 3. Run Backend (Required for Submission)

```bash
cd server
npm install
npm run dev
```

Backend runs at `http://localhost:3000`.

### 4. Run Android App

- Select `composeApp` configuration
- Target: Android emulator (API 26+) or physical device
- Click Run ▶️

### 5. Navigate to Owner's Details

1. Launch app
2. Tap "Report Missing Animal" on pet list
3. Complete Steps 1-3 (chip, photo, description)
4. Arrive at Step 4 (Owner's Details)

## Key Files to Modify

### MVI Components (Create)

```
composeApp/src/androidMain/.../features/reportmissing/presentation/mvi/
├── OwnerDetailsUiState.kt      # UI state data class
├── OwnerDetailsUserIntent.kt   # User action sealed class
└── OwnerDetailsUiEffect.kt     # Navigation/feedback effects
```

### ViewModel (Create)

```
composeApp/src/androidMain/.../features/reportmissing/presentation/viewmodels/
└── OwnerDetailsViewModel.kt    # MVI ViewModel
```

### Domain (Create)

```
composeApp/src/androidMain/.../features/reportmissing/domain/
├── models/
│   └── AnnouncementModels.kt   # DTOs for API
├── repositories/
│   └── AnnouncementRepository.kt  # Repository interface
└── usecases/
    └── SubmitAnnouncementUseCase.kt  # 2-step submission
```

### Data (Create)

```
composeApp/src/androidMain/.../features/reportmissing/data/repositories/
└── AnnouncementRepositoryImpl.kt  # API implementation
```

### UI (Modify)

```
composeApp/src/androidMain/.../features/reportmissing/ui/contactdetails/
├── ContactDetailsScreen.kt     # State host (wire to new ViewModel)
└── ContactDetailsContent.kt    # Stateless UI (implement form)
```

### Utility (Create)

```
composeApp/src/androidMain/.../features/reportmissing/util/
└── OwnerDetailsValidator.kt    # Phone/email validation
```

### Flow State (Modify)

```
composeApp/src/androidMain/.../features/reportmissing/presentation/state/
└── ReportMissingFlowState.kt   # Add rewardDescription field
```

### Navigation (Modify)

```
composeApp/src/androidMain/.../features/reportmissing/ui/
└── ReportMissingNavGraph.kt    # Update ContactDetails route
```

### DI (Modify)

```
composeApp/src/androidMain/.../di/
└── ReportMissingModule.kt      # Register new dependencies
```

## Testing

### Run Unit Tests

```bash
./gradlew :composeApp:testDebugUnitTest
```

### Run Tests with Coverage

```bash
./gradlew :composeApp:testDebugUnitTest koverHtmlReport
```

View report at: `composeApp/build/reports/kover/html/index.html`

### Test Files to Create

```
composeApp/src/androidUnitTest/.../features/reportmissing/
├── presentation/viewmodels/
│   └── OwnerDetailsViewModelTest.kt
├── domain/usecases/
│   └── SubmitAnnouncementUseCaseTest.kt
└── util/
    └── OwnerDetailsValidatorTest.kt
```

## Verification Checklist

- [ ] Phone input accepts digits and leading +
- [ ] Email input validates on Continue tap
- [ ] Reward field has character counter (120 max)
- [ ] Continue shows spinner during submission
- [ ] Back button disabled during submission
- [ ] Snackbar appears on submission error with Retry
- [ ] Success navigates to Summary with managementPassword
- [ ] All testTags present for automation
- [ ] Unit tests achieve 80% coverage

## Common Issues

### Backend Connection Failed

Ensure backend is running and `API_BASE_URL` in `BuildConfig` points to correct host:
- Emulator: `http://10.0.2.2:3000` (configured in `build.gradle.kts` debug buildType)
- Physical device: Update `API_BASE_URL` to machine's IP address

### Photo Upload Fails

- Check photo URI is valid content:// URI
- Ensure persistable URI permission was taken in Photo screen
- Verify Basic auth header is correctly encoded

### Validation Not Triggering

Validation runs only on `ContinueClicked` intent. Real-time validation is not in scope.

