# Quickstart: iOS Project Structure Refactoring

**Feature**: KAN-34-ios-prepare-project-structure
**Estimated Effort**: 2 SP (~10 days budget)

## Prerequisites

- Xcode installed and iosApp project opens without errors
- All existing tests pass before starting
- Git working tree is clean

## Step-by-Step Guide

### Before Starting

1. **Verify baseline**:
   ```bash
   cd /Users/msz/dev/ai-first/AI-First/iosApp
   xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16'
   ```
   All tests must pass.

2. **Open Xcode**:
   - Open `iosApp/iosApp.xcodeproj`
   - Ensure no build errors

### Commit 1: Directory Structure (Move Files)

**Goal**: Create new directory structure and move files.

1. **In Xcode Navigator**:
   - Right-click on `Features` folder
   - Select "New Group" → name it `ReportMissingAndFoundPet`

2. **Create Common/ structure**:
   - Inside `ReportMissingAndFoundPet`, create groups:
     - `Common/`
       - `Components/Form/`
       - `Components/Photo/`
       - `Components/Toast/`
       - `Helpers/`
       - `Models/`
       - `Services/`

3. **Move ReportMissingPet folder**:
   - Drag existing `ReportMissingPet` folder INTO `ReportMissingAndFoundPet`

4. **Move shared files to Common/**:
   - See `research.md` for complete file inventory (26 files to Common/)
   - Move `Helpers/`, `Services/` folders to `Common/`
   - Move `Models/` to `Common/` EXCEPT `ReportMissingPetFlowState.swift` (stays in ReportMissingPet/)
   - Move component files to appropriate `Common/Components/` subdirectories

5. **Mirror in Tests**:
   - Create same structure in `iosAppTests/Features/`
   - Move test files to match production structure (16 files)

6. **Verify & Commit**:
   ```bash
   xcodebuild build -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16'
   xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16'
   # Manual: Run app, test Report Missing Pet flow
   git add .
   git commit -m "[KAN-34] Move files to ReportMissingAndFoundPet structure"
   ```

### Commit 2: Class Renames + Reference Updates

**Goal**: Add MissingPet prefix to full-screen views, ViewModels, and coordinator. Xcode "Refactor → Rename" updates all references automatically.

1. **Rename Coordinator** (Xcode → Refactor → Rename):
   - `ReportMissingPetCoordinator` → `MissingPetReportCoordinator`

2. **Rename Views** (each via Xcode Refactor → Rename):
   - `AnimalDescriptionView` → `MissingPetAnimalDescriptionView`
   - `ChipNumberView` → `MissingPetChipNumberView`
   - `ContactDetailsView` → `MissingPetContactDetailsView`
   - `PhotoView` → `MissingPetPhotoView`
   - `SummaryView` → `MissingPetSummaryView`
   - Manually rename: `SummaryView+Constants.swift` → `MissingPetSummaryView+Constants.swift`

3. **Rename ViewModels** (each via Xcode Refactor → Rename):
   - Add `MissingPet` prefix to all 5 ViewModel classes

4. **Rename Test Files** (manual file rename in Xcode):
   - Match test class names to production class names

5. **Verify & Commit**:
   ```bash
   xcodebuild build -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16'
   xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16'
   # Manual: Complete full Report Missing Pet flow
   git add .
   git commit -m "[KAN-34] Rename classes with MissingPet prefix"
   ```

## Verification Checklist

After all commits:

- [ ] `xcodebuild build` succeeds with no errors
- [ ] `xcodebuild test` passes all tests (same count as before)
- [ ] Manual test: Report Missing Pet flow works end-to-end
- [ ] Directory structure matches plan.md AFTER section
- [ ] All full-screen views have `MissingPet` prefix
- [ ] Coordinator renamed to `MissingPetReportCoordinator`
- [ ] No stale file references in Xcode (no "?" icons)

## Rollback

If any step fails:
```bash
git reset --hard HEAD~1  # Undo last commit
# Or for multiple commits:
git reset --hard origin/KAN-34-ios-prepare-project-structure
```

## Files Summary

| Category | Count | Destination |
|----------|-------|-------------|
| Production - Common | 26 | `ReportMissingAndFoundPet/Common/` |
| Production - Missing Pet | 15 | `ReportMissingAndFoundPet/ReportMissingPet/` (incl. FlowState) |
| Tests - Common | 7 | `iosAppTests/.../Common/` |
| Tests - Missing Pet | 9 | `iosAppTests/.../ReportMissingPet/` (incl. FlowState test) |
| **Total** | **57** | |

See `research.md` for detailed file-by-file inventory.

