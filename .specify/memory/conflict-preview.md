# Conflict Preview - Detailed Comparison

**Purpose**: Review each conflict and decide how to resolve it.

---

## Conflict 1: `.cursor/rules/specify-rules.mdc`

### Summary
Rules/configuration file that documents active technologies for each spec.

### YOUR VERSION (010-pet-details-screen)
```
- Kotlin (JVM 17 target) (010-pet-details-screen)
- N/A (UI-only feature, data fetched from repository) (010-pet-details-screen)
```

### MAIN VERSION (iOS branch)
Includes YOUR content PLUS:
```
- TypeScript with Node.js v24 (LTS) + Express.js (REST framework), Knex (query builder), SQLite3 (database driver), Vitest (testing), SuperTest (API integration tests) (008-pet-details-api)
- SQLite database (designed for PostgreSQL migration) using existing `announcement` table (008-pet-details-api)
- N/A (infrastructure migration, no data storage changes) (011-migrate-from-kmp)
- Swift 5.9+ + SwiftUI (UI layer), UIKit (coordinator navigation), URLSession or Alamofire (HTTP client) (012-ios-pet-details-screen)
- N/A (data fetched from repository, no local persistence for this feature) (012-ios-pet-details-screen)
- ... and other entries
```

### Analysis
- **Main** has newer specs (008, 011, 012) that aren't in your version
- Your version is slightly outdated
- These don't conflict - main just has more complete documentation

### 🎯 Recommendation
**MERGE BOTH** - Take main's version (it has everything including your Android entry)

---

## Conflict 2: `e2e-tests/mobile/screens/PetDetailsScreen.ts`

### Summary
Screen Object Model for E2E tests - defines test IDs and element locators.

### YOUR VERSION (Android)
Test IDs follow Android/Compose convention:
```typescript
readonly testIds = {
    content: 'petDetails.content',
    loading: 'petDetails.loading',
    statusBadge: 'petDetails.statusBadge',
    species: 'petDetails.species',
    disappearanceDate: 'petDetails.disappearanceDate',
    phone: 'petDetails.phone',
    email: 'petDetails.email',
    // ... 175 lines total
};
```

### MAIN VERSION (iOS)
Test IDs follow iOS/SwiftUI convention:
```typescript
readonly testIds = {
    detailsView: 'petDetails.view',
    loadingSpinner: 'petDetails.loading',
    errorMessage: 'petDetails.error.message',
    photoImage: 'petDetails.photo.image',
    microchipField: 'petDetails.microchip.field',
    speciesField: 'petDetails.species.field',
    radiusField: 'petDetails.radius.field',  // iOS has radius field!
    removeReportButton: 'petDetails.removeReport.button',  // iOS has this
    // ... 185 lines total
};
```

### Key Differences
| Feature | Android (You) | iOS (Main) |
|---------|--------------|-----------|
| **Structure** | Simple naming | Detailed naming (e.g., `.field`, `.button`, `.image` suffixes) |
| **Radius field** | ❌ Not included | ✅ Included |
| **Remove Report** | ❌ Not included | ✅ Included |
| **Error handling** | Single `error` | Separate `errorMessage` |
| **Location** | Simple `location` | Separate `locationField` + `radiusField` |

### ⚠️ Issue
Your Android version doesn't include Radius or Remove Report features. Main's iOS version has them. This suggests **Android spec is incomplete** compared to iOS.

### 🎯 Recommendation
**KEEP BOTH FILES** (these are platform-specific)
- Android: `e2e-tests/mobile/screens/PetDetailsScreen.ts` (your version)
- iOS: `e2e-tests/mobile/screens/PetDetailsScreen.ts` (main's version - COMPLETELY DIFFERENT!)

Wait... Actually they're the SAME FILENAME. This is a real conflict!

**SOLUTION**: Rename or reorganize
- Option A: Rename iOS version to something like `PetDetailsScreenIOS.ts`
- Option B: Keep only the Android version for now (iOS doesn't exist on your branch yet)

---

## Conflict 3: `e2e-tests/mobile/steps/petDetailsSteps.ts`

### Summary
E2E test step definitions using Cucumber Given/When/Then syntax.

### YOUR VERSION (Android)
```typescript
// Android-specific steps
// 122 lines total
```

### MAIN VERSION (iOS)
```typescript
// iOS-specific steps
// 162 lines total
```

### Analysis
- Same situation as the screens file
- Different implementations for different platforms
- Same filename = conflict

### 🎯 Recommendation
**KEEP BOTH** but rename to be platform-specific:
- `e2e-tests/mobile/steps/petDetailsSteps.ts` → Keep Android
- `e2e-tests/mobile/steps/petDetailsStepsIOS.ts` → Rename iOS from main

---

## Conflict 4: `specs/010-pet-details-screen/checklists/requirements.md`

### Summary
Checklist of feature requirements.

### YOUR VERSION (Android)
- 84 lines
- Android-specific requirements

### MAIN VERSION (iOS)
- 85 lines
- iOS-specific requirements

### 🎯 Recommendation
**COMBINE BOTH** into a single requirements file with sections:
```markdown
# Requirements Checklist

## Shared Requirements
- User can tap pet from list to view details
- Loading state shown during data fetch
- Error handling with retry button
- [...]

## Android Implementation
- [Android-specific requirements]

## iOS Implementation  
- [iOS-specific requirements]
```

---

## Conflict 5: `specs/010-pet-details-screen/design/README.md`

### Summary
Design documentation linking to Figma, design notes, UI component specs.

### YOUR VERSION (Android)
- 82 lines
- Compose UI component references
- Android-specific design notes

### MAIN VERSION (iOS)
- 85 lines  
- SwiftUI component references
- iOS-specific design notes

### 🎯 Recommendation
**COMBINE BOTH** into platform-aware documentation:
```markdown
# Pet Details Screen - Design Documentation

## Shared Design Principles
- [...]

## Android Design Implementation
[Your 82 lines of Android-specific content]

## iOS Design Implementation
[Main's 85 lines of iOS-specific content]
```

---

## Conflict 6: `specs/010-pet-details-screen/spec.md`

### Summary
Main feature specification document.

### YOUR VERSION (Android)
- 183 lines
- Android-focused
- Includes Nov 26 clarifications about Remove Report button being OUT OF SCOPE
- Aligned with current Android implementation

### MAIN VERSION (iOS)
- 190 lines
- iOS-focused
- Nov 21 clarifications that have REMOVE REPORT BUTTON IN SCOPE for iOS
- Different scope than Android

### ⚠️ Critical Difference
```
YOUR VERSION:
"Is the 'Remove Report' button included in scope? → A: No, the Remove 
Report button has been removed from the design and is not in scope"

MAIN VERSION:
"Should the 'Remove Report' button be visible for all users or only 
for owners? → A: Show button always (permission handling will be done 
by backend)"
```

**This is a SCOPE DIFFERENCE between platforms!**
- Android: Remove Report button is OUT of scope
- iOS: Remove Report button is IN scope

### 🎯 Recommendation
**COMBINE BOTH** but document platform differences:
```markdown
# Pet Details Screen - Feature Specification

## Specification (Shared)
[Common requirements...]

## Clarifications - Android
[Your Nov 26 clarifications - Remove Report OUT OF SCOPE]

## Clarifications - iOS
[Main's clarifications - Remove Report IN SCOPE]

## Implementation

### Android Implementation
[Your 183 lines, no Remove Report button]

### iOS Implementation  
[Main's 190 lines, includes Remove Report button]
```

---

## Summary Table

| File | Conflict Type | Recommendation | Action |
|------|---------------|-----------------|--------|
| `.cursor/rules/specify-rules.mdc` | Outdated version | **MERGE BOTH** | Use main's version (has everything) |
| `e2e-tests/mobile/screens/PetDetailsScreen.ts` | Platform-specific | **RENAME** | Rename iOS to `PetDetailsScreenIOS.ts` |
| `e2e-tests/mobile/steps/petDetailsSteps.ts` | Platform-specific | **RENAME** | Rename iOS to `petDetailsStepsIOS.ts` |
| `specs/010-pet-details-screen/checklists/requirements.md` | Platform-specific | **COMBINE** | Create Android + iOS sections |
| `specs/010-pet-details-screen/design/README.md` | Platform-specific | **COMBINE** | Create Android + iOS sections |
| `specs/010-pet-details-screen/spec.md` | Different scope! | **COMBINE** | Document Android vs iOS differences |

---

## What Do You Think?

For each conflict, please let me know:

1. **`.cursor/rules/specify-rules.mdc`** 
   - [ ] Use main's version (recommended)
   - [ ] Use your version
   - [ ] Manually combine

2. **`e2e-tests/mobile/screens/PetDetailsScreen.ts`**
   - [ ] Rename iOS version to `PetDetailsScreenIOS.ts` (recommended)
   - [ ] Keep only Android
   - [ ] Keep only iOS

3. **`e2e-tests/mobile/steps/petDetailsSteps.ts`**
   - [ ] Rename iOS version to `petDetailsStepsIOS.ts` (recommended)
   - [ ] Keep only Android
   - [ ] Keep only iOS

4. **`specs/010-pet-details-screen/checklists/requirements.md`**
   - [ ] Combine (create Android + iOS sections) (recommended)
   - [ ] Keep only Android
   - [ ] Keep only iOS

5. **`specs/010-pet-details-screen/design/README.md`**
   - [ ] Combine (create Android + iOS sections) (recommended)
   - [ ] Keep only Android
   - [ ] Keep only iOS

6. **`specs/010-pet-details-screen/spec.md`**
   - [ ] Combine (document platform differences) (recommended)
   - [ ] Keep only Android
   - [ ] Keep only iOS




