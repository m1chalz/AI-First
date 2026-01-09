# Feature Specification: iOS Project Structure Refactoring for Report Missing & Found Pet

**Feature Branch**: `KAN-34-ios-prepare-project-structure`  
**Created**: 2026-01-08  
**Status**: Draft  
**Jira Ticket**: KAN-34  
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
- What if test file organization diverges from production structure? → Test directory structure must mirror production (iosAppTests/Features/ReportMissingAndFoundPet/{ReportMissingPet,Common}/) to maintain consistency

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST create explicit file inventory mapping all files to their destination folders (ReportMissingPet/ or Common/) before starting refactoring
- **FR-002**: System MUST create new directory structure `iosApp/Features/ReportMissingAndFoundPet/` containing subdirectories `ReportMissingPet/` and `Common/`
- **FR-003**: System MUST move existing `iosApp/Features/ReportMissingPet/` directory to become `iosApp/Features/ReportMissingAndFoundPet/ReportMissingPet/`
- **FR-004**: System MUST create `iosApp/Features/ReportMissingAndFoundPet/Common/` directory for shared components
- **FR-005**: System MUST keep in `ReportMissingPet/` subdirectory only: coordinator, full-screen views and their view models (preserving subdirectory structure)
- **FR-006**: System MUST move to `Common/` subdirectory: all helpers, services, models, reusable components, and form components (per file inventory from FR-001)
- **FR-007**: ReportMissingPetCoordinator MUST be renamed to MissingPetReportCoordinator
- **FR-008**: All full-screen views MUST have "MissingPet" prefix added to class names:
  - AnimalDescriptionView → MissingPetAnimalDescriptionView
  - ChipNumberView → MissingPetChipNumberView
  - ContactDetailsView → MissingPetContactDetailsView
  - PhotoView → MissingPetPhotoView
  - SummaryView → MissingPetSummaryView
- **FR-009**: All full-screen view models MUST have "MissingPet" prefix added to class names:
  - AnimalDescriptionViewModel → MissingPetAnimalDescriptionViewModel
  - ChipNumberViewModel → MissingPetChipNumberViewModel
  - ContactDetailsViewModel → MissingPetContactDetailsViewModel
  - PhotoViewModel → MissingPetPhotoViewModel
  - SummaryViewModel → MissingPetSummaryViewModel
- **FR-010**: All import statements in affected files MUST be updated to reflect new file locations
- **FR-011**: All class references in coordinator, tests, and other files MUST be updated to use new prefixed names
- **FR-012**: Test directory structure MUST mirror production structure: `iosAppTests/Features/ReportMissingAndFoundPet/ReportMissingPet/` for feature-specific tests, `iosAppTests/Features/ReportMissingAndFoundPet/Common/` for shared component tests
- **FR-013**: All test files MUST be moved to match their corresponding production file locations in the new structure
- **FR-014**: Xcode project file MUST be updated to reflect new directory structure and file locations for both production and test files
- **FR-015**: Application MUST compile successfully after all changes
- **FR-016**: All existing unit tests MUST pass after all changes
- **FR-017**: Manual testing MUST confirm Report Missing Pet flow works identically to before refactoring

### Key Entities

- **ReportMissingPet Feature Directory**: Contains coordinator and full-screen views specific to missing pet reporting flow
- **Common Directory**: Contains helpers, services, models, and reusable components that will be shared between Missing and Found pet features
- **Full-Screen Views**: Complete screen views with their view models (AnimalDescription, ChipNumber, ContactDetails, Photo, Summary)
- **Reusable Components**: Smaller UI components used within screens (form inputs, photo browsers, toast notifications)
- **Helpers**: Utility classes for photo handling, formatting, and processing
- **Services**: Business logic services for photo caching and toast scheduling
- **Models**: Data structures for flow state, photo attachments, and selections

### Implementation Constraints

- **IC-001**: Refactoring MUST be executed in multiple atomic commits for safety and reviewability:
  - Commit 1: Move files to new directory structure
  - Commit 2: Rename classes with MissingPet prefix
  - Commit 3: Update all import statements and references
- **IC-002**: Each atomic commit MUST be followed by full verification testing (build + unit tests + manual smoke test) before proceeding to next commit
- **IC-003**: Each commit MUST compile successfully and pass all tests before being committed to git
- **IC-004**: All file operations MUST be performed within Xcode to maintain project file consistency
- **IC-005**: If a commit introduces errors preventing continuation, use `git reset --hard HEAD~1` to rollback (feature branch - history rewrite acceptable)

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: File inventory document exists listing all files with their source and destination paths before refactoring starts
- **SC-002**: File inventory covers 100% of files currently in iosApp/Features/ReportMissingPet/ directory
- **SC-003**: iOS application compiles successfully with zero build errors after restructuring
- **SC-004**: All existing iOS unit tests pass with same results as before refactoring (100% pass rate maintained)
- **SC-005**: Manual testing confirms Report Missing Pet flow completes successfully with identical behavior to before refactoring
- **SC-006**: Code review confirms all full-screen views and coordinators have MissingPet prefix in class names
- **SC-007**: Code review confirms production directory structure matches specification (ReportMissingAndFoundPet/ReportMissingPet/ and ReportMissingAndFoundPet/Common/)
- **SC-008**: Code review confirms test directory structure mirrors production structure (iosAppTests/Features/ReportMissingAndFoundPet/ReportMissingPet/ and Common/)
- **SC-009**: All file moves (production and test files) are reflected in Xcode project file (not just filesystem)
- **SC-010**: No broken import statements remain after refactoring

## Design Deliverables

N/A - This is a code refactoring task with no UI changes or design requirements.

---

## Clarifications

### Session 2026-01-08

- Q: What should be the git commit strategy for this refactoring? → A: Multiple atomic commits: 1) move files, 2) rename classes, 3) update imports
- Q: When should verification testing (build + unit tests + manual smoke test) be executed during refactoring? → A: After each atomic commit (move → test, rename → test, imports → test)
- Q: What should be the rollback strategy if a commit introduces errors preventing continuation? → A: Hard reset to commit before problematic change (rewrite history)
- Q: Should explicit file inventory with destination folders be created before implementation? → A: Create explicit file inventory mapping before execution starts
- Q: Should test directory structure mirror the new production structure or keep current layout? → A: Mirror production structure: iosAppTests/Features/ReportMissingAndFoundPet/{ReportMissingPet,Common}/

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

