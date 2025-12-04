# Data Model: Android Missing Pet Report Flow

**Feature Branch**: `018-android-missing-pet-flow`  
**Date**: 2025-12-01  
**Status**: Complete

## Figma Designs Reference

| Step | Screen | Figma Link |
|------|--------|------------|
| 1/4 | Microchip Number | [297:7954](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-7954&m=dev) |
| 2/4 | Animal Photo | [297:7991](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-7991&m=dev) |
| 3/4 | Animal Description | [297:8209](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-8209&m=dev) |
| 4/4 | Contact Details | [297:8113](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-8113&m=dev) |
| Summary | Report Created | [297:8193](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-8193&m=dev) |

## Overview

This document defines the data models for the Missing Pet Report Flow feature. Since this is a UI-only feature with no backend integration, models focus on presentation state management following MVI architecture.

> **Note**: The Figma designs show complete form fields for future implementation. Per spec scope, this phase implements **navigation scaffolding with placeholder UI only** - actual form fields will be added in subsequent features.

## Entities

### 1. ReportMissingUiState

**Purpose**: Immutable UI state representing the entire flow state across all 5 screens.

**Location**: `/composeApp/src/androidMain/.../features/reportmissing/presentation/mvi/ReportMissingUiState.kt`

```kotlin
/**
 * Immutable UI state for Missing Pet Report flow.
 * Single source of truth for all 5 screens in the wizard.
 * Managed by ReportMissingViewModel, shared across nav graph.
 */
data class ReportMissingUiState(
    // Current step tracking
    val currentStep: FlowStep = FlowStep.CHIP_NUMBER,
    
    // Step 1: Chip Number
    val chipNumber: String = "",
    
    // Step 2: Photo
    val photoUri: String? = null,
    
    // Step 3: Description
    val description: String = "",
    
    // Step 4: Contact Details
    val contactEmail: String = "",
    val contactPhone: String = "",
    
    // UI state flags
    val isLoading: Boolean = false,
) {
    companion object {
        val Initial = ReportMissingUiState()
    }
    
    /**
     * Progress indicator visibility logic.
     * Shows progress on data collection screens (1-4), hidden on summary.
     */
    val showProgressIndicator: Boolean
        get() = currentStep != FlowStep.SUMMARY
    
    /**
     * Current step number for progress indicator (1-4).
     * Returns 0 for summary screen (not displayed).
     */
    val progressStepNumber: Int
        get() = when (currentStep) {
            FlowStep.CHIP_NUMBER -> 1
            FlowStep.PHOTO -> 2
            FlowStep.DESCRIPTION -> 3
            FlowStep.CONTACT_DETAILS -> 4
            FlowStep.SUMMARY -> 0
        }
    
    /**
     * Total steps for progress indicator (always 4 data collection steps).
     */
    val progressTotalSteps: Int = 4
}

/**
 * Enum representing the 5 screens in the flow.
 * Determines which screen content to display and progress indicator state.
 */
enum class FlowStep {
    CHIP_NUMBER,    // Step 1/4
    PHOTO,          // Step 2/4
    DESCRIPTION,    // Step 3/4
    CONTACT_DETAILS, // Step 4/4
    SUMMARY         // No progress indicator
}
```

### 2. ReportMissingIntent

**Purpose**: Sealed interface capturing all user interactions in the flow.

**Location**: `/composeApp/src/androidMain/.../features/reportmissing/presentation/mvi/ReportMissingIntent.kt`

```kotlin
/**
 * Sealed interface for user intents in Missing Pet Report flow.
 * Represents all possible user actions across all 5 screens.
 */
sealed interface ReportMissingIntent {
    // Navigation intents
    data object NavigateNext : ReportMissingIntent
    data object NavigateBack : ReportMissingIntent
    
    // Step 1: Chip Number
    data class UpdateChipNumber(val value: String) : ReportMissingIntent
    
    // Step 2: Photo
    data class UpdatePhotoUri(val uri: String?) : ReportMissingIntent
    
    // Step 3: Description
    data class UpdateDescription(val value: String) : ReportMissingIntent
    
    // Step 4: Contact Details
    data class UpdateContactEmail(val value: String) : ReportMissingIntent
    data class UpdateContactPhone(val value: String) : ReportMissingIntent
    
    // Summary actions (placeholder for future backend integration)
    data object Submit : ReportMissingIntent
}
```

### 3. ReportMissingEffect

**Purpose**: Sealed interface for one-off events (navigation, exit flow).

**Location**: `/composeApp/src/androidMain/.../features/reportmissing/presentation/mvi/ReportMissingEffect.kt`

```kotlin
/**
 * Sealed interface for one-off effects in Missing Pet Report flow.
 * Navigation events handled via SharedFlow, not state.
 */
sealed interface ReportMissingEffect {
    // Navigate to next screen in flow
    data class NavigateToStep(val step: FlowStep) : ReportMissingEffect
    
    // Navigate back to previous screen
    // Note: From step 1, popBackStack() automatically exits nested graph to AnimalList
    data object NavigateBack : ReportMissingEffect
}
```

### 4. Navigation Routes

**Purpose**: Type-safe navigation routes for the nested flow.

**Location**: `/composeApp/src/androidMain/.../navigation/NavRoute.kt` (additions)

```kotlin
// Add to existing NavRoute sealed interface

/**
 * Nested routes for Missing Pet Report flow.
 * Used within NavRoute.ReportMissing navigation graph.
 */
sealed interface ReportMissingRoute {
    /**
     * Chip number input screen (Step 1/4).
     */
    @Serializable
    data object ChipNumber : ReportMissingRoute
    
    /**
     * Photo selection screen (Step 2/4).
     */
    @Serializable
    data object Photo : ReportMissingRoute
    
    /**
     * Description input screen (Step 3/4).
     */
    @Serializable
    data object Description : ReportMissingRoute
    
    /**
     * Contact details input screen (Step 4/4).
     */
    @Serializable
    data object ContactDetails : ReportMissingRoute
    
    /**
     * Summary screen (no progress indicator).
     */
    @Serializable
    data object Summary : ReportMissingRoute
}
```

## Field Validation Rules

> **Note**: This is a UI-only feature. Validation is out of scope per spec. Fields accept any input.

| Field | Type | Validation | Notes |
|-------|------|------------|-------|
| chipNumber | String | None | Optional, free-form text |
| photoUri | String? | None | Optional, URI from photo picker |
| description | String | None | Optional, multi-line text |
| contactEmail | String | None | Optional, free-form text |
| contactPhone | String | None | Optional, free-form text |

## State Transitions

### Navigation Flow

```
AnimalList ──[ReportMissing button]──► ChipNumber (1/4)
                                           │
                                           ▼
ChipNumber (1/4) ◄──[Back]────────── Photo (2/4)
     │                                     │
     │ [Next]                              ▼
     │                              Description (3/4)
     │                                     │
     │                                     ▼
     │                             ContactDetails (4/4)
     │                                     │
     │                                     ▼
     │                                Summary (no indicator)
     │                                     │
     ▼                                     │
AnimalList ◄───────[Back from 1]───────────┘
                   [Submit from Summary]
```

### Reducer State Transitions

```kotlin
// Simplified reducer logic
fun reduce(state: ReportMissingUiState, intent: ReportMissingIntent): ReportMissingUiState {
    return when (intent) {
        is ReportMissingIntent.UpdateChipNumber -> 
            state.copy(chipNumber = intent.value)
        is ReportMissingIntent.UpdatePhotoUri -> 
            state.copy(photoUri = intent.uri)
        is ReportMissingIntent.UpdateDescription -> 
            state.copy(description = intent.value)
        is ReportMissingIntent.UpdateContactEmail -> 
            state.copy(contactEmail = intent.value)
        is ReportMissingIntent.UpdateContactPhone -> 
            state.copy(contactPhone = intent.value)
        // Navigation intents handled via effects, not state
        else -> state
    }
}
```

## Preview Data Provider

**Purpose**: Provide sample states for Compose previews.

```kotlin
class ReportMissingUiStatePreviewProvider : PreviewParameterProvider<ReportMissingUiState> {
    override val values = sequenceOf(
        // Step 1: Empty chip number
        ReportMissingUiState.Initial,
        
        // Step 1: With chip number entered
        ReportMissingUiState(
            currentStep = FlowStep.CHIP_NUMBER,
            chipNumber = "123456789012345"
        ),
        
        // Step 2: Photo screen
        ReportMissingUiState(
            currentStep = FlowStep.PHOTO,
            chipNumber = "123456789012345"
        ),
        
        // Step 3: Description with photo
        ReportMissingUiState(
            currentStep = FlowStep.DESCRIPTION,
            chipNumber = "123456789012345",
            photoUri = "content://photo/1"
        ),
        
        // Step 4: Contact details
        ReportMissingUiState(
            currentStep = FlowStep.CONTACT_DETAILS,
            chipNumber = "123456789012345",
            photoUri = "content://photo/1",
            description = "Small brown dog, friendly"
        ),
        
        // Step 5: Summary with all data
        ReportMissingUiState(
            currentStep = FlowStep.SUMMARY,
            chipNumber = "123456789012345",
            photoUri = "content://photo/1",
            description = "Small brown dog, friendly",
            contactEmail = "owner@example.com",
            contactPhone = "+1234567890"
        )
    )
}
```

## Relationships

```
ReportMissingViewModel (1) ──owns──► ReportMissingUiState (1)
         │
         ├──processes──► ReportMissingIntent (many)
         │
         └──emits──► ReportMissingEffect (many)

ReportMissingUiState ──contains──► FlowStep (1)
```

## Reusable Components

### StepHeader

**Purpose**: Common header bar for data collection screens (Steps 1-4) with back navigation, title, and progress indicator.

**Location**: `/composeApp/src/androidMain/.../features/reportmissing/ui/components/StepHeader.kt`

**Visual Layout**:
```
┌─────────────────────────────────────────────────┐
│  [←]       Microchip number           ○ 1/4    │
│ (back)         (title)            (progress)   │
└─────────────────────────────────────────────────┘
```

**Implementation**:
```kotlin
/**
 * Header bar for data collection screens in Missing Pet Report flow.
 * Displays back button, centered title, and progress indicator.
 * Used on Steps 1-4 (NOT on Summary screen).
 */
@Composable
fun StepHeader(
    title: String,
    currentStep: Int,
    totalSteps: Int = 4,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(40.dp)
                .border(0.667.dp, Color(0xFFE5E9EC), CircleShape)
                .testTag("reportMissing.header.backButton.click")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(20.dp)
            )
        }
        
        // Title (centered)
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF2D2D2D),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .testTag("reportMissing.header.title")
        )
        
        // Progress Indicator
        StepProgressIndicator(
            currentStep = currentStep,
            totalSteps = totalSteps,
            modifier = Modifier.testTag("reportMissing.header.progress")
        )
    }
}
```

**Design Specs (from Figma)**:
- Back button: 40x40px, circular, border `#E5E9EC` (0.667px)
- Arrow icon: 20x20px
- Title: Inter 14px Regular, `#2D2D2D`, tracking -0.28px, centered
- Progress: 40x40px circular (see StepProgressIndicator)
- Padding: 16px vertical

### StepProgressIndicator

**Purpose**: Circular progress indicator showing current step (e.g., "1/4", "2/4").

**Location**: `/composeApp/src/androidMain/.../features/reportmissing/ui/components/StepProgressIndicator.kt`

**Visual Layout**:
```
    ╭───────╮
   ╱  arc    ╲
  │           │
  │    1/4    │
  │           │
   ╲         ╱
    ╰───────╯
```

**Implementation**:
```kotlin
/**
 * Circular progress indicator for Missing Pet Report flow.
 * Shows partial arc based on progress + step text (e.g., "1/4").
 */
@Composable
fun StepProgressIndicator(
    currentStep: Int,
    totalSteps: Int = 4,
    modifier: Modifier = Modifier
) {
    val progress = currentStep.toFloat() / totalSteps.toFloat()
    
    Box(
        modifier = modifier.size(40.dp),
        contentAlignment = Alignment.Center
    ) {
        // Background circle (gray track)
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = Color(0xFFE5E9EC),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        
        // Progress arc (blue)
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = Color(0xFF155DFC),
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        
        // Step text
        Text(
            text = "$currentStep/$totalSteps",
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.plus_jakarta_sans_bold)),
                fontSize = 11.667.sp,
                fontWeight = FontWeight.Bold
            ),
            color = Color(0xFF2D2D2D),
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("reportMissing.progress.text")
        )
    }
}
```

**Design Specs (from Figma)**:
- Container: 40x40px
- Arc stroke: ~3px, rounded caps
- Track color: `#E5E9EC` (gray)
- Progress color: `#155DFC` (blue)
- Text: Plus Jakarta Sans Bold, 11.667px, `#2D2D2D`
- Arc starts at top (-90°), fills clockwise based on progress

**Test Identifiers**:
- `reportMissing.header.backButton.click` - Back button tap
- `reportMissing.header.title` - Header title text
- `reportMissing.header.progress` - Progress indicator container
- `reportMissing.progress.text` - Progress text (e.g., "1/4")

## Figma UI Specifications

### Step 1: Microchip Number (1/4)
- **Header**: "Microchip number" (centered)
- **Title**: "Identification by Microchip"
- **Subtitle**: "Microchip identification is the most efficient way to reunite with your pet..."
- **Input**: "Microchip number (optional)" with placeholder `00000-00000-00000`
- **Button**: "Continue" (primary blue #155dfc)
- **Back**: Arrow-left icon button (circular, border #e5e9ec)
- **Progress**: Circular indicator showing "1/4"

### Step 2: Animal Photo (2/4)
- **Header**: "Animal photo" (centered)
- **Title**: "Your pet's photo"
- **Subtitle**: "Please upload a photo of the missing animal."
- **Upload Card**:
  - Icon with indigo background (#4f39f6)
  - "Upload animal photo"
  - "JPEG, PNG, GIF • Max 10MB"
  - "Browse" button (purple #4f39f6)
- **Button**: "Continue"
- **Progress**: "2/4"

### Step 3: Animal Description (3/4)
- **Header**: "Animal description" (centered)
- **Title**: "Your pet's details"
- **Subtitle**: "Fill out the details about the missing animal."
- **Fields** (future implementation):
  - Date of disappearance (date picker)
  - Animal species (dropdown)
  - Animal race (dropdown, disabled until species selected)
  - Gender (radio: Female/Male)
  - Animal age (optional)
  - "Request GPS position" button (outlined blue)
  - Lat/Long (two text inputs)
  - Animal additional description (optional, multiline textarea)
- **Button**: "Continue"
- **Progress**: "3/4"

### Step 4: Contact Details (4/4)
- **Header**: "Owner's details" (centered)
- **Title**: "Your contact info"
- **Subtitle**: "Add your contact information's and potential reward."
- **Fields**:
  - Phone number (text input)
  - Email (text input)
  - Reward for the finder (optional, amount input)
- **Button**: "Continue"
- **Progress**: "4/4"

### Step 5: Summary (No Progress)
- **Header**: None (no back button or progress indicator)
- **Title**: "Report created"
- **Description**: Confirmation text explaining report was created, notification will be sent if found, and removal code info
- **Code Display**: Large 7-digit code (e.g., "5216577") with pink gradient blur effect
- **Button**: "Close" (not "Continue")

### Design Tokens
- **Primary Blue**: #155dfc
- **Purple**: #4f39f6
- **Text Dark**: #2d2d2d
- **Text Secondary**: #545f71
- **Border**: #d1d5dc, #e5e9ec
- **Background Disabled**: #f3f3f5
- **Font Heading**: Inter Regular, 32px
- **Font Body**: Hind Regular, 16px
- **Font Label**: Hind Regular, 16px
- **Border Radius**: 10px (inputs, buttons)

## Test Considerations

- **UiState tests**: Verify computed properties (`showProgressIndicator`, `progressStepNumber`)
- **Reducer tests**: Verify pure state transitions for each intent type
- **ViewModel tests**: Verify intent dispatch → state update + effect emission
- **Navigation tests**: Verify correct route transitions via E2E tests

