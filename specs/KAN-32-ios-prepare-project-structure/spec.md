# Feature Specification: iOS Project Structure Refactoring for Report Missing & Found Pet

**Feature Branch**: `KAN-32-ios-prepare-project-structure`  
**Created**: 2026-01-08  
**Status**: Draft  
**Jira Ticket**: KAN-32  
**Design**: N/A (refactoring task - no UI changes)  
**Input**: User description: "Prepare iOS project structure for Report Found Pet feature (069-report-found-pet). Create shared structure for both Report Missing and Report Found Pet features with common components."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Code Organization for Feature Expansion (Priority: P1)

As a developer preparing for the Report Found Pet feature (069), I need the iOS project structure to reflect the logical relationship between "Report Missing Pet" and "Report Found Pet" features by organizing shared components in a common directory, so that future implementation of Report Found Pet can reuse components without duplication.

**Why this priority**: This is the foundation for implementing the Report Found Pet feature. Without this restructuring, code duplication is inevitable and maintenance becomes problematic.

**Independent Test**: Can be tested by building the app after restructuring and verifying all tests pass. No functional changes should be visible to end users.

**Acceptance Scenarios**:

1. **Given** the iOS project has ReportMissingPet feature, **When** developer navigates to Features directory, **Then** they see ReportMissingAndFoundPet parent directory containing ReportMissingPet subdirectory and Common subdirectory
2. **Given** the restructuring is complete, **When** developer builds the iOS app, **Then** compilation succeeds without errors
3. **Given** the restructuring is complete, **When** developer runs all iOS tests, **Then** all tests pass with same results as before refactoring
4. **Given** the restructuring is complete, **When** developer manually tests Report Missing Pet flow, **Then** all screens and functionality work identically to before refactoring

### User Story 2 - Clear Component Ownership and Naming (Priority: P2)

As a developer working on pet reporting features, I need all full-screen views and coordinators to have clear naming that indicates they belong to "Missing Pet" flow, so that when "Found Pet" views are added later, there is no naming confusion.

**Why this priority**: Clear naming prevents bugs and confusion when both Missing and Found flows are implemented. This is secondary to the structural changes but important for maintainability.

**Independent Test**: Can be tested by code review - all full-screen views, view models, and coordinators should have MissingPet prefix in their class names.

**Acceptance Scenarios**:

1. **Given** the refactoring is complete, **When** developer searches for "ReportMissingPetCoordinator", **Then** they find it renamed to "MissingPetReportCoordinator"
2. **Given** the refactoring is complete, **When** developer opens any full-screen view in ReportMissingPet directory, **Then** the class name starts with "MissingPet" prefix (e.g., MissingPetAnimalDescriptionView)
3. **Given** the refactoring is complete, **When** developer opens any view model for full-screen views, **Then** the class name starts with "MissingPet" prefix (e.g., MissingPetAnimalDescriptionViewModel)

---

### Edge Cases

- What happens when Xcode file references become stale after moving files? → Ensure all files are properly moved in Xcode project (not just filesystem) and references are updated
- How does system handle import statements after moving files? → All import statements must be updated to reflect new file locations
- What if tests reference old class names? → All test files must be updated to reference new prefixed class names
- What if coordinator navigation references use old names? → All navigation code must be updated to use new coordinator and view names

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST create new directory structure `iosApp/Features/ReportMissingAndFoundPet/` containing subdirectories `ReportMissingPet/` and `Common/`
- **FR-002**: System MUST move existing `iosApp/Features/ReportMissingPet/` directory to become `iosApp/Features/ReportMissingAndFoundPet/ReportMissingPet/`
- **FR-003**: System MUST create `iosApp/Features/ReportMissingAndFoundPet/Common/` directory for shared components
- **FR-004**: System MUST keep in `ReportMissingPet/` subdirectory only: coordinator, full-screen views and their view models (preserving subdirectory structure)
- **FR-005**: System MUST move to `Common/` subdirectory: all helpers, services, models, reusable components, and form components
- **FR-006**: ReportMissingPetCoordinator MUST be renamed to MissingPetReportCoordinator
- **FR-007**: All full-screen views MUST have "MissingPet" prefix added to class names:
  - AnimalDescriptionView → MissingPetAnimalDescriptionView
  - ChipNumberView → MissingPetChipNumberView
  - ContactDetailsView → MissingPetContactDetailsView
  - PhotoView → MissingPetPhotoView
  - SummaryView → MissingPetSummaryView
- **FR-008**: All full-screen view models MUST have "MissingPet" prefix added to class names:
  - AnimalDescriptionViewModel → MissingPetAnimalDescriptionViewModel
  - ChipNumberViewModel → MissingPetChipNumberViewModel
  - ContactDetailsViewModel → MissingPetContactDetailsViewModel
  - PhotoViewModel → MissingPetPhotoViewModel
  - SummaryViewModel → MissingPetSummaryViewModel
- **FR-009**: All import statements in affected files MUST be updated to reflect new file locations
- **FR-010**: All class references in coordinator, tests, and other files MUST be updated to use new prefixed names
- **FR-011**: Xcode project file MUST be updated to reflect new directory structure and file locations
- **FR-012**: Application MUST compile successfully after all changes
- **FR-013**: All existing unit tests MUST pass after all changes
- **FR-014**: Manual testing MUST confirm Report Missing Pet flow works identically to before refactoring

### Key Entities

- **ReportMissingPet Feature Directory**: Contains coordinator and full-screen views specific to missing pet reporting flow
- **Common Directory**: Contains helpers, services, models, and reusable components that will be shared between Missing and Found pet features
- **Full-Screen Views**: Complete screen views with their view models (AnimalDescription, ChipNumber, ContactDetails, Photo, Summary)
- **Reusable Components**: Smaller UI components used within screens (form inputs, photo browsers, toast notifications)
- **Helpers**: Utility classes for photo handling, formatting, and processing
- **Services**: Business logic services for photo caching and toast scheduling
- **Models**: Data structures for flow state, photo attachments, and selections

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: iOS application compiles successfully with zero build errors after restructuring
- **SC-002**: All existing iOS unit tests pass with same results as before refactoring (100% pass rate maintained)
- **SC-003**: Manual testing confirms Report Missing Pet flow completes successfully with identical behavior to before refactoring
- **SC-004**: Code review confirms all full-screen views and coordinators have MissingPet prefix in class names
- **SC-005**: Code review confirms directory structure matches specification (ReportMissingAndFoundPet/ReportMissingPet/ and ReportMissingAndFoundPet/Common/)
- **SC-006**: All file moves are reflected in Xcode project file (not just filesystem)
- **SC-007**: No broken import statements remain after refactoring

## Design Deliverables

N/A - This is a code refactoring task with no UI changes or design requirements.

---

## Estimation *(mandatory)*

### Initial Estimate

- **Story Points**: 2 SP
- **Initial Budget**: 2 × 4 × 1.3 = 10.4 days
- **Confidence**: ±50%
- **Anchor Comparison**: Simpler than Pet Details (3 SP) because this is iOS-only refactoring with no new functionality, no backend work, no cross-platform implementation. However, it requires careful attention to detail with file moves, renaming, and import updates across many files. Complexity is primarily in the number of files to move and update rather than technical difficulty.

### Re-Estimation (Updated After Each Phase)

| Phase | SP | Days | Confidence | Key Discovery |
|-------|-----|------|------------|---------------|
| Initial | 2 | 10.4 | ±50% | Gut feel from feature title - iOS-only refactoring task |
| After SPEC | — | — | ±30% | [Update when spec.md complete] |
| After PLAN | — | — | ±20% | [Update when plan.md complete] |
| After TASKS | — | — | ±15% | [Update when tasks.md complete] |

### Per-Platform Breakdown (After TASKS)

| Platform | Tasks | Days | Notes |
|----------|-------|------|-------|
| Backend | 0 | 0 | No backend changes |
| iOS | — | — | [Fill after tasks.md] |
| Android | 0 | 0 | No Android changes |
| Web | 0 | 0 | No Web changes |
| **Total** | | **—** | |

### Variance Tracking

| Metric | Initial | Final | Variance |
|--------|---------|-------|----------|
| **Story Points** | 2 SP | [Y SP] | [Calculate: (Y - 2) / 2 × 100%] |
| **Budget (days)** | 10.4 days | [Y days] | [Calculate: (Y - 10.4) / 10.4 × 100%] |

**Variance Reasons**: [Why was estimate different? Was file moving more/less complex than expected? Were there unexpected issues with Xcode project file or test updates?]

**Learning for Future Estimates**: [What pattern should the team apply to similar refactoring tasks?]

