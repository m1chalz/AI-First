# Research: iOS Owner's Details Screen

**Feature**: iOS Owner's Details Screen (Step 4/4)  
**Date**: 2025-12-02  
**Status**: Complete

## Overview

This document consolidates technical decisions and research findings for implementing the iOS Owner's Details screen. All key decisions were pre-determined through spec clarifications and constitution requirements.

## Technical Decisions

### 1. Component Reuse Strategy

**Decision**: Reuse ValidatedTextField component from spec 031 (Animal Description screen)

**Rationale**:
- Spec 031 already implemented ValidatedTextField component in iOS for text input with validation
- Same validation UX pattern (inline errors on Continue tap, red borders)
- Supports maxLength parameter for reward field (120 chars)
- Consistent validation behavior across Missing Pet flow
- Reduces code duplication and testing effort

**Implementation**:
- Phone field: `ValidatedTextField(text: $phone, error: phoneError, maxLength: nil)`
- Email field: `ValidatedTextField(text: $email, error: emailError, maxLength: nil)`
- Reward field: `ValidatedTextField(text: $rewardDescription, error: nil, maxLength: 120)` (optional, no validation errors)

### 2. Validation Timing

**Decision**: Validate only on Continue tap (not on blur, not on keystroke)

**Rationale**:
- Matches pattern from spec 031 (Animal Description screen)
- Reduces user friction - errors appear only when user attempts to proceed
- Aligns with iOS best practices for form validation
- Simpler state management - no need for blur tracking

**Implementation**:
- ViewModel exposes `isPhoneValid` and `isEmailValid` computed properties
- `submitForm()` method triggers validation before submission
- If validation fails, set `@Published var phoneError: String?` and `@Published var emailError: String?`
- ValidatedTextField observes error properties and displays inline errors

### 3. 2-Step Submission Architecture

**Decision**: Use intermediate service object (AnnouncementSubmissionService) to encapsulate 2-step flow

**Rationale**:
- Step 1: POST /api/v1/announcements (create announcement) → extract id + managementPassword
- Step 2: POST /api/v1/announcements/:id/photos (upload photo with Basic auth)
- Encapsulating both steps in service keeps repository methods simple
- ViewModel focuses on UI concerns (validation, loading states, error handling, navigation)
- Service handles business logic (request building, 2-step orchestration, photo loading)
- Testable in isolation (can mock service in ViewModel tests)

**Alternative Considered**: ViewModel calls repository directly for both steps
- Rejected because: ViewModel would contain too much orchestration logic (photo loading, request building, 2-step coordination)

**Dependency Injection Chain**:
```
Repository (data layer)
    ↓ injected into
Service (business logic)
    ↓ injected into
ViewModel (presentation)
```

**Service Responsibilities**:
- Build CreateAnnouncementRequest from FlowState
- Execute step 1: create announcement via repository
- Load photo Data from PhotoAttachmentMetadata.cachedURL
- Execute step 2: upload photo via repository
- Return managementPassword on success
- Throw errors for ViewModel to handle (convert to alerts)

### 4. Repository Protocol Design

**Decision**: AnnouncementRepositoryProtocol with methods for announcement creation and photo upload

**Rationale**:
- Constitution requires protocol suffix for iOS repository protocols
- Manual DI with constructor injection (ServiceContainer provides repository to ViewModel)
- Repository protocol enables test doubles (FakeAnnouncementRepository for tests)

**Protocol Interface** (preliminary):
```swift
protocol AnnouncementRepositoryProtocol {
    func createAnnouncement(request: CreateAnnouncementRequest) async throws -> AnnouncementResponse
    func uploadPhoto(announcementId: String, photo: PhotoAttachmentMetadata, managementPassword: String) async throws
}
```

**Alternative Considered**: Single `submitAnnouncement(request:)` method combining both steps
- Rejected because: Less flexible for future features (e.g., editing announcements, retrying photo upload separately)

### 5. Localization Strategy

**Decision**: SwiftGen L10n with Polish (PL) and English (EN) locales

**Rationale**:
- Constitution mandates SwiftGen for all displayed text
- String keys follow pattern `owners_details.{element}.{property}` (e.g., `L10n.tr("owners_details.phone.placeholder")`)
- Device locale determines display language (Polish for pl-PL, English for all others)
- Type-safe localization - compile-time errors for missing keys

**String Keys** (defined in `Localizable.strings`):
- Screen title, subtitle
- Field labels (Phone number, Email, Reward (optional))
- Placeholders (Enter phone number, Enter email address, Describe reward)
- Validation errors (Enter at least 7 digits, Enter a valid email address)
- Button labels (Continue, Back)
- Alert messages (No connection, Something went wrong, Try Again, Cancel)

### 6. Error Handling Pattern

**Decision**: Generic popup alert for all submission failures (network, backend, timeout)

**Rationale**:
- Spec clarifies: display UIAlertController-style alert with "Try Again" / "Cancel" buttons
- No auto-dismiss timers - user controls dismissal
- Network errors: "No connection. Please check your network and try again."
- Backend/timeout errors: "Something went wrong. Please try again later."
- Retry action retries full 2-step submission from step 1
- Simpler UX than inline error handling (validation errors are inline, submission errors are popup)

**Implementation**:
- ViewModel: `@Published var alertMessage: String?`
- ViewModel: `@Published var showAlert: Bool = false`
- View: `.alert(isPresented: $viewModel.showAlert) { Alert(title: Text(viewModel.alertMessage ?? ""), primaryButton: .default(Text("Try Again")) { viewModel.submitForm() }, secondaryButton: .cancel()) }`

### 7. Loading State Management

**Decision**: Continue button shows ActivityIndicator spinner and disables; back button disables during submission

**Rationale**:
- Prevents double submission (user tapping Continue multiple times)
- Back button disabled to prevent navigation away mid-submission (prevents orphaned announcements)
- Input fields remain editable but not submittable (Continue disabled)
- Spec clarifies: isSubmitting = true during submission, false on completion or error

**Implementation**:
- ViewModel: `@Published var isSubmitting: Bool = false`
- View: Continue button `.disabled(viewModel.isSubmitting)` with conditional `.progressViewStyle()` overlay
- View: Back button `.disabled(viewModel.isSubmitting)` (dimmed or disabled state)

### 8. Validation Logic

**Decision**: Phone validation (7-11 digits), Email validation (RFC 5322 basic)

**Rationale**:
- Spec clarifies: phone accepts digits and leading "+", trim spaces/dashes for validation but preserve formatting in UI
- Email: case-insensitive, trim whitespace, RFC 5322-compatible pattern (basic local@domain.tld)
- Matches validation from spec 006 (Pets API) for consistency

**Validation Rules**:
- Phone: `^\\+?\\d{7,11}$` (sanitized from user input by removing spaces/dashes)
- Email: Standard email regex or SwiftUI built-in validation (if available)

### 9. Photo Data Loading

**Decision**: Repository loads Data from PhotoAttachmentMetadata.cachedURL during step 2

**Rationale**:
- Spec clarifies: FlowState stores PhotoAttachmentMetadata containing cachedURL (file path to disk cache)
- Separates UI logic from data access (ViewModel passes metadata, repository reads file)
- PhotoAttachmentCache handles disk I/O operations (already implemented in spec 028)

**Alternative Considered**: ViewModel loads Data before calling repository
- Rejected because: ViewModel should not perform file I/O (violates separation of concerns)

### 10. Accessibility Identifiers

**Decision**: Use convention `ownersDetails.{element}` (camelCase, no action suffix) for all interactive elements

**Rationale**:
- Constitution requires accessibilityIdentifier on all interactive SwiftUI views
- iOS convention uses `{screen}.{element}` format (without action suffix, unlike Android)
- Enables E2E testing with Appium (Screen Object Model uses these IDs)
- Consistent naming across iOS app

**Identifiers**:
- `ownersDetails.backButton`
- `ownersDetails.phoneInput`
- `ownersDetails.emailInput`
- `ownersDetails.rewardInput`
- `ownersDetails.continueButton`
- `ownersDetails.progressBadge`
- `ownersDetails.title`
- `ownersDetails.subtitle`

## Dependencies

### Existing Components (Reuse)
- **ValidatedTextField** (from spec 031): Text input with validation error display, maxLength support
- **TextAreaView** (from spec 031): NOT USED (reward field uses single-line ValidatedTextField per spec clarification)
- **ReportMissingPetFlowState** (from spec 017): Session container for flow data persistence
- **ReportMissingPetCoordinator** (from spec 017): Navigation and ViewModel creation (already integrated)
- **PhotoAttachmentMetadata** (from spec 028): Photo metadata with cachedURL for disk cache
- **PhotoAttachmentCache** (from spec 028): Disk I/O for photo files

### Modified Components (Extend Existing)
- **AnimalRepositoryProtocol**: ADD new methods for announcement operations
  - `createAnnouncement(request:) async throws -> AnnouncementResponse`
  - `uploadPhoto(request:) async throws`
- **AnimalRepository**: IMPLEMENT new methods (HTTP client calling backend API)
  - Step 1: POST /api/v1/announcements
  - Step 2: POST /api/v1/announcements/:id/photos

### New Components (Create)
- **AnnouncementSubmissionService**: Business logic service for 2-step submission orchestration (REQUIRED)
  - Receives AnimalRepositoryProtocol in constructor
  - Builds requests from FlowState
  - Orchestrates announcement creation + photo upload
  - Returns managementPassword or throws errors
- **FakeAnnouncementSubmissionService**: Test double for ViewModel tests
- **ContactDetailsViewModel**: ViewModel implementation (modify existing placeholder)
  - Receives Service in constructor
  - Focuses on UI state, validation, and navigation
- **ContactDetailsView**: SwiftUI view implementation (modify existing placeholder)

### External Dependencies
- **Backend API**: POST /api/v1/announcements (spec 009), POST /api/v1/announcements/:id/photos (spec 021)
- **SwiftGen**: Localization system for L10n.tr() (already configured in project)
- **URLSession**: HTTP client for API calls (standard iOS, no third-party dependency)

## Open Questions

None - all clarifications resolved in spec.md clarifications section (Session 2025-12-02).

## References

- [Spec 035: iOS Owner's Details Screen](./spec.md)
- [Spec 017: iOS Missing Pet Flow](../017-ios-missing-pet-flow/spec.md) - Coordinator, FlowState, navigation integration
- [Spec 031: iOS Animal Description Screen](../031-ios-animal-description-screen/spec.md) - ValidatedTextField component, validation pattern
- [Spec 028: iOS Animal Photo Screen](../028-ios-animal-photo-screen/spec.md) - PhotoAttachmentMetadata, PhotoAttachmentCache
- [Spec 009: Create Announcement API](../009-create-announcement/spec.md) - POST /api/v1/announcements contract
- [Spec 021: Announcement Photo Upload API](../021-announcement-photo-upload/spec.md) - POST /api/v1/announcements/:id/photos contract
- [Constitution](../../.specify/memory/constitution.md) - iOS MVVM-C architecture, manual DI, SwiftGen requirement

