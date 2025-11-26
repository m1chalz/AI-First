# Contracts: Complete Java E2E Test Coverage

**Feature**: 025-java-e2e-coverage  
**Date**: 2025-11-26

## Overview

This directory contains contract definitions for the E2E test implementation. These contracts serve as implementation blueprints for Java/Cucumber test scenarios.

## Contract Files

### 1. web-pet-list.feature
**Purpose**: Complete Gherkin feature file for web Animal List testing

**Content**: 
- 2 existing scenarios (from Spec 016)
- 8 new scenarios (Feature 025)
- Total: 10 scenarios providing 100% coverage of Spec 005 web requirements

**Scenarios Added**:
1. List scrolls smoothly
2. Scrolling stops at last item
3. Animal card tap triggers navigation
4. Button remains visible during scroll
5. Report button tap action
6. Animal card details display correctly
7. Loading state displayed during initial load
8. Report Found Animal button visible (web only)
9. Reserved search space present

**Implementation**: Copy to `/e2e-tests/java/src/test/resources/features/web/pet-list.feature`

---

### 2. mobile-pet-details.feature
**Purpose**: Complete Gherkin feature file for iOS Pet Details testing (new feature file)

**Content**:
- 12 MVP scenarios covering core flows
- Provides 35-40% coverage of Spec 012 requirements
- Focus: Navigation, loading/error states, contact interactions, status badges, remove button

**Scenarios**:
1. Navigate to pet details from list
2. Display loading state
3. Display pet details after loading completes
4. Display error state with retry button
5. Retry button reloads data
6. Contact owner via phone tap
7. Contact owner via email tap
8. Display MISSING status badge with red color
9. Display FOUND status badge with blue color
10. Display Remove Report button at bottom
11. Handle Remove Report button tap
12. Display fallback when photo not available

**Implementation**: Create at `/e2e-tests/java/src/test/resources/features/mobile/pet-details.feature`

---

### 3. mobile-pet-list-updates.md
**Purpose**: Instructions for modifying existing mobile Pet List feature file

**Content**:
- Add 3 button interaction scenarios
- Comment out 4 invalid search scenarios with TODO markers
- Step definition requirements for new scenarios
- Screen Object method requirements

**Changes**:
1. **Add**: Button visible at bottom
2. **Add**: Button remains visible during scroll
3. **Add**: Button tap triggers action
4. **Comment Out**: Search for specific species (Android)
5. **Comment Out**: Search for specific species (iOS)
6. **Comment Out**: Clear search results
7. **Comment Out**: Search with no results

**Implementation**: Modify `/e2e-tests/java/src/test/resources/features/mobile/pet-list.feature`

---

## Contract Summary

| Contract | Type | Target File | Action | Scenarios |
|----------|------|-------------|--------|-----------|
| web-pet-list.feature | Feature File | `/features/web/pet-list.feature` | Extend | Add 8 |
| mobile-pet-details.feature | Feature File | `/features/mobile/pet-details.feature` | Create | Create 12 |
| mobile-pet-list-updates.md | Instructions | `/features/mobile/pet-list.feature` | Modify | Add 3, Comment 4 |

**Total New Scenarios**: 23 (8 web + 12 mobile Pet Details + 3 mobile button)  
**Total Modified Scenarios**: 4 (commented out search scenarios)

---

## Implementation Order

**Phase 1**: Web Animal List (8 scenarios)
1. Extend `PetListPage.java` with 8 new methods
2. Copy `web-pet-list.feature` to target location
3. Implement step definitions in `PetListWebSteps.java`
4. Test: `mvn test -Dcucumber.filter.tags="@web"`

**Phase 2**: Mobile Pet Details (12 scenarios)
1. Create `PetDetailsScreen.java` with dual annotations
2. Copy `mobile-pet-details.feature` to target location
3. Create `PetDetailsMobileSteps.java` with step definitions
4. Test: `mvn test -Dcucumber.filter.tags="@ios" -Dcucumber.features="**/pet-details.feature"`

**Phase 3**: Mobile Animal List Updates (3 new, 4 commented)
1. Add 3 button methods to `PetListScreen.java`
2. Modify existing `/features/mobile/pet-list.feature` per instructions
3. Add button step definitions to `PetListMobileSteps.java`
4. Comment out search step definitions with TODO markers
5. Test: `mvn test -Dcucumber.filter.tags="@ios" -Dcucumber.features="**/pet-list.feature"`

---

## Validation

After implementation, verify:

- [ ] Web coverage: 10/10 scenarios passing (100%)
- [ ] Mobile Pet Details coverage: 12/12 scenarios passing (35-40%)
- [ ] Mobile Animal List coverage: 9/10 scenarios passing (90%)
- [ ] No search-related test failures
- [ ] HTML reports generated for each platform
- [ ] Test execution time under 5 minutes per platform

---

## References

- [Spec 005: Animal List Screen](/specs/005-animal-list/spec.md)
- [Spec 012: iOS Pet Details Screen](/specs/012-ios-pet-details-screen/spec.md)
- [Spec 016: E2E Java Migration](/specs/016-e2e-java-migration/spec.md)
- [Plan](../plan.md)
- [Research](../research.md)
- [Data Model](../data-model.md)
- [Quickstart](../quickstart.md)

