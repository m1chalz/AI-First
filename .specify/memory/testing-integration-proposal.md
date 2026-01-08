# Testing Strategy Integration into Spec-Kit Framework

**Version**: 1.0  
**Created**: 2026-01-08  
**Purpose**: Define how the testing workflow from `docs/testing-workflow.md` integrates with the spec-kit framework (SPEC → PLAN → TASKS)

---

## Overview

This document bridges the testing strategy (`docs/testing-workflow.md`) with the spec-kit framework templates, ensuring testing requirements are captured at every phase of feature development.

### Integration Points

| Spec-Kit Phase | Testing Integration | Templates Affected |
|----------------|---------------------|-------------------|
| **SPEC** | Test identifiers, acceptance scenarios | `spec-template.md` |
| **PLAN** | Test strategy decisions, coverage targets | `plan-template.md` |
| **TASKS** | Test implementation tasks (unit, integration, E2E) | `tasks-template.md` ✅ Already complete |
| **ESTIMATION** | Testing effort estimation | `estimation-methodology.md` |

---

## Phase 1: SPEC Template Updates

### Current State

The `spec-template.md` already includes:
- ✅ Given-When-Then acceptance scenarios per user story
- ✅ Independent test descriptions per user story

### Proposed Additions

#### 1. Add Test Identifiers Section (Mandatory for UI Features)

Insert after "Success Criteria" section:

```markdown
## Test Identifiers *(mandatory for UI features)*

<!--
  TEST IDENTIFIERS: All interactive UI elements MUST have stable test identifiers for E2E testing.
  See docs/testing-workflow.md for complete guide.
-->

### Naming Convention

Format: `{screen}.{element}.{action-or-id}`

### Test Identifiers for This Feature

| Screen | Element | Test Identifier | Platform Notes |
|--------|---------|----------------|----------------|
| [Screen Name] | [Button/Input/Link] | `[screen].[element].[action]` | Android: testTag, iOS: accessibilityIdentifier, Web: data-testid |

**Example**:
| Pet Details | Share Button | `petDetails.shareButton.click` | All platforms |
| Pet List | List Item | `petList.item.${petId}` | Dynamic ID based on pet |

**Checklist**:
- [ ] All buttons have test identifiers
- [ ] All input fields have test identifiers
- [ ] All navigation elements have test identifiers
- [ ] List items use stable IDs (not indices)
- [ ] Naming follows `{screen}.{element}.{action}` convention
```

#### 2. Add Testing Strategy Section (Optional, for Complex Features)

Insert after "Test Identifiers":

```markdown
## Testing Strategy *(optional - use for complex features)*

<!--
  TESTING STRATEGY: For complex features, document which test types apply.
  See docs/testing-workflow.md for test decision tree.
-->

### Test Types Required

| Test Type | Required? | Rationale |
|-----------|-----------|-----------|
| **Unit Tests** | ✅ Yes | Business logic in ViewModels, services, use cases |
| **Integration Tests** | ✅ Yes | Backend API endpoints (all platforms consume REST API) |
| **E2E Tests** | ✅ Yes | Critical user flow for pet sharing across platforms |
| **Manual Tests** | ⚠️ Optional | Social platform preview validation (Facebook, Twitter) |

### Coverage Targets

- **Backend**: 80% (services, lib, API endpoints) - TDD mandatory
- **Web**: 80% (hooks, lib functions) - TDD mandatory
- **Android**: 80% (ViewModels, use cases, domain models)
- **iOS**: 80% (ViewModels, domain models)

### Test Complexity Indicators

- **Simple** (1 SP or less): Standard unit + integration + E2E tests
- **Medium** (2-3 SP): Add edge case tests, error scenario tests
- **Complex** (5+ SP): Add integration with external systems, performance tests (if needed), security tests (if auth-related)
```

#### 3. Update User Story Template

Enhance the "Independent Test" section:

```markdown
### User Story 1 - [Brief Title] (Priority: P1)

[Describe this user journey in plain language]

**Why this priority**: [Explain the value and why it has this priority level]

**Independent Test**: [How to verify this story works on its own]

**Test Coverage**:
- Unit Tests: [Which components need unit tests - ViewModels, services, etc.]
- Integration Tests: [Which API endpoints need integration tests]
- E2E Tests: [Which user flow needs E2E coverage]
- Test Identifiers: [Key UI elements needing test IDs for this story]

**Acceptance Scenarios**:

1. **Given** [initial state], **When** [action], **Then** [expected outcome]
```

---

## Phase 2: PLAN Template Updates

### Current State

The `plan-template.md` already includes:
- ✅ Constitution Check with 80% test coverage requirement
- ✅ Platform-specific test locations and commands
- ✅ Given-When-Then test structure requirement

### Proposed Additions

#### 1. Add Test Strategy Decision Section

Insert after "Constitution Check":

```markdown
## Test Strategy Decisions *(gate: must complete before Phase 0)*

<!--
  TEST STRATEGY: Document testing approach based on feature complexity.
  Reference: docs/testing-workflow.md
-->

### Test Type Selection

| Test Type | Include? | Rationale | Estimated Tasks |
|-----------|----------|-----------|-----------------|
| **Unit Tests** | ✅ Yes | [e.g., ViewModels, services, use cases] | [e.g., ~15 tasks] |
| **Integration Tests** | ✅ Yes | [e.g., 3 API endpoints] | [e.g., ~3 tasks] |
| **E2E Tests** | ✅ Yes | [e.g., 2 user flows] | [e.g., ~4 tasks] |
| **Manual Tests** | ⚠️ If needed | [e.g., Social platform preview validation] | [N/A - documented in spec] |

### Test Workflow Selection

| Platform | Test Approach | Rationale |
|----------|---------------|-----------|
| **Backend** | TDD (Red-Green-Refactor) | Mandatory per constitution |
| **Web** | TDD (Red-Green-Refactor) | Mandatory per constitution |
| **Android** | Test-After (Implement → Test) | Permitted per constitution |
| **iOS** | Test-After (Implement → Test) | Permitted per constitution |
| **E2E** | Test-After (All platforms complete → E2E) | After feature completion |

### Coverage Targets Per Platform

| Platform | Target | Test Locations | Notes |
|----------|--------|----------------|-------|
| Backend | 80% | `/server/src/services/__test__/`, `/server/src/lib/__test__/`, `/server/src/__test__/` | Unit + Integration |
| Web | 80% | `/webApp/src/hooks/__test__/`, `/webApp/src/lib/__test__/` | Unit tests |
| Android | 80% | `/composeApp/src/androidUnitTest/` | ViewModels, use cases, domain |
| iOS | 80% | `/iosApp/iosAppTests/` | ViewModels, domain |
| E2E | 100% user stories | `/e2e-tests/java/src/test/resources/features/[feature].feature` | Gherkin scenarios |

### Test Environment Requirements

- [ ] Backend API endpoints available (for integration tests)
- [ ] Test data fixtures created (for E2E tests)
- [ ] Selenium Grid / Appium setup verified (for E2E mobile tests)
- [ ] Test identifiers implemented on all UI elements
- [ ] Fake/mock repositories created (for unit tests)
```

#### 2. Add Test Effort Estimation Section

Insert after "Test Strategy Decisions":

```markdown
## Test Effort Estimation *(gate: update after tasks.md)*

<!--
  TEST EFFORT: Estimate testing tasks separately to ensure adequate coverage.
  Testing typically adds 30-40% to implementation effort.
-->

### Test Task Breakdown

| Platform | Unit Tests | Integration Tests | E2E Tests | Total Test Tasks |
|----------|------------|-------------------|-----------|------------------|
| Backend | [X tasks] | [Y tasks] | N/A | [X+Y tasks] |
| Web | [X tasks] | N/A | [Y tasks] | [X+Y tasks] |
| Android | [X tasks] | N/A | [Y tasks] | [X+Y tasks] |
| iOS | [X tasks] | N/A | [Y tasks] | [X+Y tasks] |
| **Total** | **[sum]** | **[sum]** | **[sum]** | **[total]** |

### Test Task Ratio

- **Implementation Tasks**: [X tasks] (~70%)
- **Test Tasks**: [Y tasks] (~30-40%)
- **Total Tasks**: [X+Y tasks]

**Test Task Percentage**: [Y / (X+Y) × 100]%

**Sanity Check**: Test tasks should be 30-40% of total. If < 20%, tests may be underestimated. If > 50%, implementation may be underestimated.
```

---

## Phase 3: TASKS Template Updates

### Current State ✅

The `tasks-template.md` is **already excellent** and fully compliant with testing strategy:

- ✅ Mandatory unit tests per platform (80% coverage)
- ✅ Mandatory backend unit + integration tests (TDD workflow)
- ✅ Mandatory E2E tests (all user stories)
- ✅ Given-When-Then structure enforced
- ✅ Tests organized by user story (independent testing)
- ✅ Test tasks BEFORE implementation tasks (TDD for backend/web)
- ✅ Platform-specific test locations and commands documented

### Minor Enhancement (Optional)

Add a "Test Execution Checklist" at the end:

```markdown
---

## Test Execution Checklist *(run before marking feature complete)*

<!--
  TEST EXECUTION: Verify all test types pass before feature sign-off.
  Reference: docs/testing-workflow.md for full testing workflow.
-->

### Unit Tests

- [ ] **Android**: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport` → 80%+ coverage
- [ ] **iOS**: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES` → 80%+ coverage
- [ ] **Web**: `cd webApp && npm test --coverage` → 80%+ coverage
- [ ] **Backend**: `cd server && npm test --coverage` → 80%+ coverage

### Integration Tests

- [ ] **Backend API**: `cd server && npm test --coverage` → All endpoints tested

### E2E Tests

- [ ] **Web**: `cd e2e-tests/java && mvn test -Dtest=WebTestRunner` → All scenarios pass
- [ ] **Android**: `cd e2e-tests/java && mvn test -Dtest=AndroidTestRunner` → All scenarios pass
- [ ] **iOS**: `cd e2e-tests/java && mvn test -Dtest=IosTestRunner` → All scenarios pass

### Code Quality

- [ ] **Android**: `./gradlew detekt ktlintCheck :composeApp:lintDebug` → No violations
- [ ] **Backend**: `cd server && npm run lint` → No violations
- [ ] **Web**: `cd webApp && npm run lint` → No violations

### Test Reports

- [ ] Android coverage: `composeApp/build/reports/kover/html/index.html` reviewed
- [ ] iOS coverage: Xcode Coverage Report reviewed
- [ ] Web coverage: `webApp/coverage/index.html` reviewed
- [ ] Backend coverage: `server/coverage/index.html` reviewed
- [ ] E2E Web report: `e2e-tests/java/target/cucumber-reports/web/index.html` reviewed
- [ ] E2E Android report: `e2e-tests/java/target/cucumber-reports/android/index.html` reviewed
- [ ] E2E iOS report: `e2e-tests/java/target/cucumber-reports/ios/index.html` reviewed

### Manual Verification (if applicable)

- [ ] Social platform previews validated (Facebook, Twitter, LinkedIn)
- [ ] Native share sheet tested on physical devices
- [ ] Clipboard fallback tested on browsers without Web Share API
```

---

## Phase 4: ESTIMATION METHODOLOGY Updates

### Current State

The `estimation-methodology.md` includes:
- ✅ Story Point definition (includes 80% test coverage)
- ✅ Budget formula: `SP × 4 days × 1.3`
- ✅ Per-platform breakdown after TASKS

### Proposed Addition

Add a "Testing Effort Estimation" section after "Task-to-Days Conversion":

```markdown
## Testing Effort Estimation

Testing effort is included in Story Points but should be explicitly tracked for learning.

### Testing Effort Guidelines

| Feature Complexity | Test Tasks % | Test Days Ratio | Example |
|--------------------|--------------|-----------------|---------|
| **Simple** (1-2 SP) | 20-30% | 0.2-0.3× implementation | 1 SP feature: 3 days impl + 1 day tests = 4 days |
| **Medium** (3-5 SP) | 30-40% | 0.3-0.4× implementation | 3 SP feature: 9 days impl + 3.5 days tests = 12.5 days |
| **Complex** (8-13 SP) | 40-50% | 0.4-0.5× implementation | 8 SP feature: 20 days impl + 10 days tests = 30 days |

### Test Type Breakdown

Typical test effort distribution:

- **Unit Tests**: 50-60% of test time (most coverage, fast feedback)
- **Integration Tests**: 15-20% of test time (backend API endpoints)
- **E2E Tests**: 25-30% of test time (user flows, Page Objects, stability fixes)

### Testing Effort Formula

```
Test Days = Implementation Days × Test Ratio
Total Days = Implementation Days + Test Days
```

Example (3 SP feature):
```
Implementation: 9 days (70%)
Testing: 3.5 days (30%) = 9 × 0.35
Total: 12.5 days ≈ 13 days
```

### Variance Tracking for Testing

When re-estimating after PLAN/TASKS, track testing variance separately:

| Metric | Initial | Final | Variance | Reason |
|--------|---------|-------|----------|--------|
| Implementation Days | [X] | [Y] | [(Y-X)/X × 100%] | [Reuse, native APIs, etc.] |
| Testing Days | [X] | [Y] | [(Y-X)/X × 100%] | [Test complexity, E2E flakiness, etc.] |
| **Total Days** | **[X]** | **[Y]** | **[(Y-X)/X × 100%]** | |

**Testing Variance Patterns**:
- **-30% to -50%**: Backend API already works, minimal testing needed
- **+30% to +50%**: E2E tests flaky, required significant stabilization effort
- **+50% to +100%**: Complex test scenarios, edge cases not anticipated
```

---

## Phase 5: Create Testing Checklist Template

Create a new template file: `.specify/templates/testing-checklist-template.md`

```markdown
# Testing Checklist: [FEATURE NAME]

**Feature Branch**: `[###-feature-name]`  
**Created**: [DATE]  
**Reference**: `docs/testing-workflow.md`

---

## Pre-Implementation Checklist

Before writing any implementation code:

- [ ] **Test identifiers planned**: All UI elements have planned test IDs in spec.md
- [ ] **Acceptance scenarios defined**: Given-When-Then scenarios in spec.md
- [ ] **Test strategy documented**: Test types and coverage targets in plan.md
- [ ] **Fake repositories planned**: Test doubles for unit tests (per platform)
- [ ] **Test data fixtures planned**: Seed data for E2E tests

---

## Test Implementation Checklist

### Backend (TDD - Red-Green-Refactor)

#### RED Phase
- [ ] Failing unit test written for service in `/server/src/services/__test__/`
- [ ] Failing unit test written for utility in `/server/src/lib/__test__/`
- [ ] Failing integration test written for endpoint in `/server/src/__test__/`
- [ ] Tests run: `npm test` → All new tests FAIL ✅

#### GREEN Phase
- [ ] Minimal service implementation passes unit test
- [ ] Minimal utility implementation passes unit test
- [ ] Minimal endpoint implementation passes integration test
- [ ] Tests run: `npm test` → All tests PASS ✅

#### REFACTOR Phase
- [ ] Code quality improved (Clean Code principles applied)
- [ ] ESLint violations fixed: `npm run lint` ✅
- [ ] Tests still pass: `npm test` ✅
- [ ] Coverage verified: `npm test --coverage` → 80%+ ✅

### Web (TDD - Red-Green-Refactor)

#### RED Phase
- [ ] Failing unit test written for hook in `/webApp/src/hooks/__test__/`
- [ ] Failing unit test written for lib function in `/webApp/src/lib/__test__/`
- [ ] Tests run: `npm test` → All new tests FAIL ✅

#### GREEN Phase
- [ ] Minimal hook implementation passes unit test
- [ ] Minimal lib function implementation passes unit test
- [ ] Tests run: `npm test` → All tests PASS ✅

#### REFACTOR Phase
- [ ] Code quality improved (Clean Code principles applied)
- [ ] ESLint violations fixed: `npm run lint` ✅
- [ ] Tests still pass: `npm test` ✅
- [ ] Coverage verified: `npm test --coverage` → 80%+ ✅

### Android (Test-After)

- [ ] ViewModels unit tested in `/composeApp/src/androidUnitTest/.../presentation/`
- [ ] Use cases unit tested in `/composeApp/src/androidUnitTest/.../domain/usecases/`
- [ ] Domain models unit tested (if complex logic exists)
- [ ] Fake repositories created in `/composeApp/src/androidUnitTest/.../fakes/`
- [ ] Tests follow Given-When-Then structure ✅
- [ ] Tests run: `./gradlew :composeApp:testDebugUnitTest` ✅
- [ ] Coverage verified: `./gradlew koverHtmlReport` → 80%+ ✅
- [ ] Static analysis: `./gradlew detekt ktlintCheck` ✅

### iOS (Test-After)

- [ ] ViewModels unit tested in `/iosApp/iosAppTests/Features/[Feature]/[ViewModel]Tests.swift`
- [ ] Domain models unit tested in `/iosApp/iosAppTests/Domain/Models/`
- [ ] Fake repositories created in `/iosApp/iosAppTests/Fakes/`
- [ ] Tests follow Given-When-Then structure ✅
- [ ] Tests run: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15'` ✅
- [ ] Coverage verified: Xcode Coverage Report → 80%+ ✅

### E2E Tests (After All Platforms Complete)

- [ ] Gherkin feature file created in `/e2e-tests/java/src/test/resources/features/`
- [ ] Page Objects created in `/e2e-tests/java/src/test/java/.../pages/` (Web)
- [ ] Screen Objects created in `/e2e-tests/java/src/test/java/.../screens/` (Mobile)
- [ ] Step definitions implemented in `/e2e-tests/java/src/test/java/.../steps/`
- [ ] Test identifiers verified on all platforms (testTag, accessibilityIdentifier, data-testid)
- [ ] Web E2E passes: `mvn test -Dtest=WebTestRunner` ✅
- [ ] Android E2E passes: `mvn test -Dtest=AndroidTestRunner` ✅
- [ ] iOS E2E passes: `mvn test -Dtest=IosTestRunner` ✅

---

## Coverage Verification Checklist

Run before marking feature complete:

### Unit Test Coverage

- [ ] **Backend**: `cd server && npm test --coverage` → 80%+ ✅
  - [ ] Services: 80%+ coverage
  - [ ] Lib utilities: 80%+ coverage
- [ ] **Web**: `cd webApp && npm test --coverage` → 80%+ ✅
  - [ ] Hooks: 80%+ coverage
  - [ ] Lib functions: 80%+ coverage
- [ ] **Android**: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport` → 80%+ ✅
  - [ ] ViewModels: 80%+ coverage
  - [ ] Use cases: 80%+ coverage
- [ ] **iOS**: Xcode Coverage Report → 80%+ ✅
  - [ ] ViewModels: 80%+ coverage
  - [ ] Domain models: 80%+ coverage

### Integration Test Coverage

- [ ] **Backend API**: All endpoints tested in `/server/src/__test__/` → 80%+ ✅

### E2E Test Coverage

- [ ] **Web**: All user stories have E2E scenarios → 100% ✅
- [ ] **Android**: All user stories have E2E scenarios → 100% ✅
- [ ] **iOS**: All user stories have E2E scenarios → 100% ✅

---

## Quality Gates Checklist

### Pre-Commit

- [ ] Unit tests pass locally (all platforms)
- [ ] Coverage ≥ 80% for changed files (all platforms)
- [ ] Linter passes (no errors)
- [ ] Code follows platform conventions

### Pre-Push

- [ ] All unit tests pass (all platforms)
- [ ] Integration tests pass (backend changes)
- [ ] Linting passes on all platforms
- [ ] Coverage reports reviewed (80%+)

### Pre-Merge (Pull Request)

- [ ] All unit tests pass
- [ ] All integration tests pass
- [ ] Coverage ≥ 80% (enforced per platform)
- [ ] Code review approved
- [ ] Smoke E2E tests pass: `mvn test -Dcucumber.filter.tags="@smoke"`
- [ ] No linter warnings
- [ ] Documentation updated (if needed)

### Pre-Deployment

- [ ] All tests pass (unit + integration + E2E)
- [ ] Full E2E regression suite passes: `cd e2e-tests/java && mvn clean test`
- [ ] Manual smoke test on staging environment
- [ ] Database migrations tested (if any)
- [ ] Rollback plan documented

---

## Notes

- Reference `docs/testing-workflow.md` for detailed testing workflow
- TDD is **mandatory** for backend and web
- Test-after is **permitted** for Android and iOS
- 80% coverage is **non-negotiable** per constitution
- Given-When-Then structure is **mandatory** for all tests
```

---

## Implementation Roadmap

### Phase 1: Template Updates (Immediate)

1. ✅ Update `spec-template.md` with Test Identifiers section
2. ✅ Update `spec-template.md` with Testing Strategy section
3. ✅ Update `plan-template.md` with Test Strategy Decisions section
4. ✅ Update `plan-template.md` with Test Effort Estimation section
5. ✅ Update `tasks-template.md` with Test Execution Checklist (minor)

### Phase 2: Documentation Updates (Immediate)

1. ✅ Update `estimation-methodology.md` with Testing Effort Estimation section
2. ✅ Create `testing-checklist-template.md`
3. ✅ Cross-reference `docs/testing-workflow.md` in all templates

### Phase 3: Validation (Next Feature)

1. Apply updated templates to next new feature
2. Validate testing sections are completed correctly
3. Gather feedback from developers
4. Refine templates based on feedback

### Phase 4: Backfill Existing Features (Optional)

1. Identify existing features with incomplete test documentation
2. Apply Test Identifiers section retroactively
3. Apply Testing Strategy section retroactively
4. Update estimation variance tracking to include testing effort

---

## Summary of Changes

| Template | Changes | Status |
|----------|---------|--------|
| `spec-template.md` | Add Test Identifiers + Testing Strategy sections | ✅ Ready |
| `plan-template.md` | Add Test Strategy Decisions + Test Effort Estimation | ✅ Ready |
| `tasks-template.md` | Add Test Execution Checklist (minor) | ✅ Already excellent |
| `estimation-methodology.md` | Add Testing Effort Estimation section | ✅ Ready |
| `testing-checklist-template.md` | Create new template | ✅ Ready |

---

## Benefits

1. **Consistency**: Testing requirements documented at every spec-kit phase
2. **Traceability**: Test identifiers → Acceptance scenarios → Test tasks → Test execution
3. **Estimation Accuracy**: Testing effort explicitly tracked and estimated
4. **Quality Gates**: Clear checkpoints for test coverage and quality
5. **Developer Guidance**: Templates guide developers through TDD and test-after workflows
6. **Compliance**: Ensures 80% coverage requirement is met per constitution

---

## Questions & Decisions

### Decision 1: Mandatory vs Optional Sections

**Proposal**: 
- Test Identifiers: **Mandatory** for UI features, N/A for backend-only
- Testing Strategy: **Optional** (use for complex features 5+ SP)
- Test Strategy Decisions: **Mandatory** in plan.md
- Test Effort Estimation: **Mandatory** in plan.md (updated after tasks.md)

**Rationale**: Balance between completeness and overhead for simple features.

### Decision 2: Template Update Timing

**Proposal**: Update templates immediately, apply to next new feature

**Rationale**: Low risk, high benefit. Existing features don't need backfill unless updating specs.

### Decision 3: Testing Checklist Usage

**Proposal**: Testing checklist is **optional** - use for complex features or when team needs guidance

**Rationale**: tasks.md already captures test tasks. Checklist is redundant but useful for verification.

---

**Last Updated**: 2026-01-08  
**Author**: AI Assistant  
**Review Status**: Pending team review
