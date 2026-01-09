# Quickstart: iOS Prepare Found Pet Flow as Missing Pet Copy

**Feature**: KAN-34-ios-prepare-found-flow-as-missing-copy  
**Platform**: iOS only

## Overview

This is a **scaffolding feature** - no new functionality, just preparing code structure for future development.

## Implementation Steps

1. **Copy source files** from `ReportMissingPet/` to `ReportFoundPet/` (13 files)
2. **Rename types** in copied files: `MissingPet*` → `FoundPet*`
3. **Rename identifiers**: `missingPet.*` → `foundPet.*`
4. **Copy test files** with same renames (9 files)
5. **Uncomment button** in `AnnouncementListView.swift`
6. **Implement coordinator** method `showReportFound()`
7. **Add files to Xcode** project
8. **Verify** compilation and tests pass

## Key Files

| Action | File |
|--------|------|
| Uncomment | `iosApp/iosApp/Features/AnnouncementList/Views/AnnouncementListView.swift` |
| Modify | `iosApp/iosApp/Features/AnnouncementList/Coordinators/AnnouncementListCoordinator.swift` |
| Create | `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/**` |
| Create | `iosApp/iosAppTests/Features/ReportMissingAndFoundPet/ReportFoundPet/**` |

## Verification

```bash
# Build
xcodebuild build -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16'

# Test
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES
```

## Notes

- No backend changes required
- No new localization keys (reuses existing Missing keys)
- Common/ components shared between flows (no duplication)
- Flow content unchanged - this is preparation for future divergence

