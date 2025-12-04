# Tasks: Complete Java E2E Test Coverage

**Branch**: `025-java-e2e-coverage`  
**Spec**: [spec.md](./spec.md) | **Plan**: [plan.md](./plan.md)

## Task Breakdown

### Phase 1: Mobile Pet Details E2E Coverage (Priority: P1)

**Goal**: Implement 10-12 essential Pet Details scenarios to achieve 35-40% coverage of Spec 012

#### Task 1.1: Create Pet Details Feature File
- **File**: `/e2e-tests/java/src/test/resources/features/mobile/pet-details.feature`
- **Status**: ✅ COMPLETED
- **Scenarios**: 12 total (navigation, loading, error, contact, badges, remove button, photo placeholder)
- **Tags**: @mobile, @ios, @smoke
- **Acceptance**: Feature file created with all MVP scenarios following Given-When-Then structure

#### Task 1.2: Implement Pet Details Screen Object Model
- **File**: `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/screens/PetDetailsScreen.java`
- **Status**: ✅ COMPLETED
- **Elements**: 13 locators with dual annotations (@AndroidFindBy, @iOSXCUITFindBy)
  - detailsView, loadingSpinner, errorView, retryButton
  - petPhoto, phoneNumber, emailAddress, statusBadge
  - removeReportButton, petName, speciesText, breedText, photoPlaceholder
- **Methods**: 25+ interaction and verification methods
- **Acceptance**: Screen Object follows dual annotation pattern per Spec 016

#### Task 1.3: Implement Pet Details Step Definitions
- **File**: `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/steps/mobile/PetDetailsMobileSteps.java`
- **Status**: ✅ COMPLETED
- **Steps**: 40+ Cucumber steps covering all 12 scenarios
- **Categories**: Navigation (5), Loading (7), Loaded State (5), Error State (8), Contact (8), Status Badges (6), Remove Button (7), Photo Placeholder (4)
- **Acceptance**: All Gherkin steps have corresponding Java implementations with clear assertions

#### Task 1.4: Execute and Verify Pet Details Tests
- **Command**: `mvn test -Dcucumber.filter.tags="@ios"`
- **Status**: ⏳ PENDING
- **Expected**: 12/12 scenarios passing
- **Coverage**: 35-40% of Spec 012 requirements
- **Acceptance**: All scenarios pass, HTML report shows green status

---

### Phase 2: Web Animal List E2E Coverage (Priority: P1)

**Goal**: Implement 8 missing scenarios to achieve 90-100% coverage of Spec 005

#### Task 2.1: Extend Web Pet List Feature File
- **File**: `/e2e-tests/java/src/test/resources/features/web/pet-list.feature`
- **Status**: ⏳ PENDING
- **New Scenarios**: 8 total
  - List scrolls smoothly
  - Scrolling stops at last item
  - Animal card tap triggers navigation
  - Button remains visible during scroll
  - Report button tap action
  - Animal card details display
  - Loading state displayed
  - Report Found Animal button (web only)
  - Reserved search space
- **Acceptance**: 10 total scenarios (2 existing + 8 new)

#### Task 2.2: Extend Web Page Object Model
- **File**: `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/pages/PetListPage.java`
- **Status**: ⏳ PENDING
- **New Methods**: 8 total
  - scrollToBottom(), isScrollable(), canScrollFurther()
  - clickAnimalCard(String id), isButtonVisibleAfterScroll()
  - clickReportMissingButton(), getSearchPlaceholderHeight()
  - getReportFoundButton()
- **Acceptance**: Methods use @FindBy XPath locators per Spec 016

#### Task 2.3: Implement Web Step Definitions
- **File**: `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/steps/web/PetListWebSteps.java`
- **Status**: ⏳ PENDING
- **Steps**: 20+ new Cucumber steps for 8 scenarios
- **Acceptance**: All steps use Page Object methods with clear assertion messages

#### Task 2.4: Execute and Verify Web Tests
- **Command**: `mvn test -Dcucumber.filter.tags="@web"`
- **Status**: ⏳ PENDING
- **Expected**: 10/10 scenarios passing (100% coverage)
- **Coverage**: 90-100% of Spec 005 requirements
- **Acceptance**: HTML report shows all scenarios green

---

### Phase 3: Mobile Animal List Updates (Priority: P2)

**Goal**: Comment out 4 invalid search scenarios from mobile pet-list.feature

#### Task 3.1: Update Mobile Pet List Feature
- **File**: `/e2e-tests/java/src/test/resources/features/mobile/pet-list.feature`
- **Status**: ✅ COMPLETED (based on current file state)
- **Changes**: Removed 4 search scenarios (lines 32-44, 72-84 from old version)
- **TODO Markers**: No search scenarios present (clean state)
- **Acceptance**: No failing tests due to non-existent search functionality

#### Task 3.2: Update Mobile Step Definitions
- **File**: `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/steps/mobile/PetListMobileSteps.java`
- **Status**: ✅ COMPLETED (based on current file state)
- **Changes**: No search-related methods present
- **Acceptance**: All remaining mobile scenarios pass

---

### Phase 4: Test Reports and Documentation (Priority: P2)

**Goal**: Generate HTML reports and update documentation

#### Task 4.1: Generate Maven Cucumber Reports
- **Command**: `mvn clean test`
- **Status**: ⏳ PENDING
- **Reports**:
  - `/e2e-tests/target/cucumber-reports/web/index.html`
  - `/e2e-tests/target/cucumber-reports/ios/index.html`
  - `/e2e-tests/target/cucumber-reports/android/index.html` (if applicable)
- **Acceptance**: Reports show platform-specific test results with pass/fail status

#### Task 4.2: Update Quickstart Documentation
- **File**: `/specs/025-java-e2e-coverage/quickstart.md`
- **Status**: ⏳ PENDING
- **Changes**: Verify commands, coverage metrics, troubleshooting tips
- **Acceptance**: Documentation reflects actual test execution results

#### Task 4.3: Create Implementation Summary
- **File**: `/specs/025-java-e2e-coverage/IMPLEMENTATION_SUMMARY.md`
- **Status**: ⏳ PENDING
- **Content**: Coverage achieved, scenarios implemented, lessons learned
- **Acceptance**: Summary documents final coverage percentages and next steps

---

## Coverage Tracking

### Current Status

| Platform | Feature | Spec | Required | Implemented | Coverage | Status |
|----------|---------|------|----------|-------------|----------|--------|
| Mobile   | Pet Details | 012 | 30 | 12 | 40% | ✅ MVP COMPLETE |
| Mobile   | Pet List | 005 | 10 | 5 | 50% | 🟡 IN PROGRESS |
| Web      | Pet List | 005 | 10 | 2 | 20% | ⏳ PENDING |

### Success Criteria

- [x] **SC-002**: Mobile Pet Details coverage: 0% → 40% (12/30 scenarios)
- [ ] **SC-001**: Web Pet List coverage: 20% → 90%+ (10/10 scenarios)
- [ ] **SC-005**: Invalid search scenarios removed from mobile suite (4 scenarios)
- [ ] **SC-003**: All implemented scenarios pass via Maven
- [ ] **SC-004**: HTML reports generated for all platforms
- [ ] **SC-006**: QA can verify completeness via reports

---

## Next Steps

1. **Execute Pet Details Tests**: Run `mvn test -Dcucumber.filter.tags="@ios"` and verify all 12 scenarios pass
2. **Implement Web Scenarios**: Complete Phase 2 tasks (feature file, Page Object, step definitions)
3. **Execute Full Test Suite**: Run `mvn clean test` and verify all platforms pass
4. **Generate Reports**: Review HTML reports for coverage metrics
5. **Update Documentation**: Create implementation summary and update quickstart

---

## Notes

- **Test Identifiers**: All accessibility IDs follow Spec 012 conventions (`petDetails.element.action`)
- **Dual Annotations**: Screen Objects support both Android and iOS (iOS-first implementation)
- **Error Handling**: All step definitions include clear assertion messages per FR-015
- **Given-When-Then**: All scenarios follow AAA structure per FR-013

---

## Blockers & Risks

### Current Blockers
- None

### Risks
- **Risk 1**: iOS simulator availability
  - **Mitigation**: Document simulator setup in quickstart.md
- **Risk 2**: Appium server connectivity
  - **Mitigation**: Verify Appium server running before test execution
- **Risk 3**: Test execution time
  - **Mitigation**: Use @smoke tags for fast feedback loops

---

## References

- [Spec 012: iOS Pet Details Screen](../012-ios-pet-details-screen/spec.md)
- [Spec 005: Animal List](../005-animal-list/spec.md)
- [Spec 016: Java E2E Migration](../016-e2e-java-migration/spec.md)



