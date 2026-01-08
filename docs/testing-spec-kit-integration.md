# Testing Strategy Integration with Spec-Kit Framework

**Quick Reference** | **Full Proposal**: [`.specify/memory/testing-integration-proposal.md`](../.specify/memory/testing-integration-proposal.md)

---

## Overview

This document summarizes how the testing workflow (`docs/testing-workflow.md`) integrates with the spec-kit framework (SPEC → PLAN → TASKS → ESTIMATION).

---

## Quick Integration Map

| Phase | Testing Integration | What Gets Added |
|-------|---------------------|-----------------|
| **SPEC** | Test identifiers, acceptance scenarios | Test ID table, testing strategy section |
| **PLAN** | Test strategy decisions, coverage targets | Test type selection, effort estimation |
| **TASKS** | Test implementation tasks | ✅ Already complete! |
| **ESTIMATION** | Testing effort tracking | Test task ratios, variance tracking |

---

## Key Additions to Templates

### 1. spec.md Gets:

**Test Identifiers Section** (mandatory for UI features):
```markdown
## Test Identifiers

| Screen | Element | Test Identifier | Platform Notes |
|--------|---------|----------------|----------------|
| Pet Details | Share Button | `petDetails.shareButton.click` | All platforms |
```

**Testing Strategy Section** (optional for complex features):
```markdown
## Testing Strategy

| Test Type | Required? | Rationale |
|-----------|-----------|-----------|
| Unit Tests | ✅ Yes | ViewModels, services, use cases |
| Integration Tests | ✅ Yes | Backend API endpoints |
| E2E Tests | ✅ Yes | Critical user flows |
```

### 2. plan.md Gets:

**Test Strategy Decisions** (mandatory):
```markdown
## Test Strategy Decisions

| Test Type | Include? | Rationale | Estimated Tasks |
|-----------|----------|-----------|-----------------|
| Unit Tests | ✅ Yes | ViewModels, services | ~15 tasks |
| Integration Tests | ✅ Yes | 3 API endpoints | ~3 tasks |
| E2E Tests | ✅ Yes | 2 user flows | ~4 tasks |
```

**Test Effort Estimation** (mandatory):
```markdown
## Test Effort Estimation

| Platform | Unit Tests | Integration Tests | E2E Tests | Total |
|----------|------------|-------------------|-----------|-------|
| Backend | 5 tasks | 3 tasks | N/A | 8 tasks |
| Web | 5 tasks | N/A | 2 tasks | 7 tasks |
| Android | 6 tasks | N/A | 2 tasks | 8 tasks |
| iOS | 6 tasks | N/A | 2 tasks | 8 tasks |

**Test Task Percentage**: 31% (within 30-40% target ✅)
```

### 3. tasks.md Gets:

**Test Execution Checklist** (optional, for verification):
```markdown
## Test Execution Checklist

### Unit Tests
- [ ] Android: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport` → 80%+ ✅
- [ ] iOS: `xcodebuild test ...` → 80%+ ✅
- [ ] Web: `npm test --coverage` → 80%+ ✅
- [ ] Backend: `npm test --coverage` → 80%+ ✅

### E2E Tests
- [ ] Web: `mvn test -Dtest=WebTestRunner` ✅
- [ ] Android: `mvn test -Dtest=AndroidTestRunner` ✅
- [ ] iOS: `mvn test -Dtest=IosTestRunner` ✅
```

### 4. estimation-methodology.md Gets:

**Testing Effort Guidelines**:
```markdown
## Testing Effort Estimation

| Feature Complexity | Test Tasks % | Example |
|--------------------|--------------|---------|
| Simple (1-2 SP) | 20-30% | 3 days impl + 1 day tests = 4 days |
| Medium (3-5 SP) | 30-40% | 9 days impl + 3.5 days tests = 12.5 days |
| Complex (8-13 SP) | 40-50% | 20 days impl + 10 days tests = 30 days |
```

---

## Testing Workflow by Spec-Kit Phase

### Phase 1: SPEC (spec.md)

**Focus**: What needs to be tested?

- ✅ Define Given-When-Then acceptance scenarios per user story
- ✅ Document test identifiers for all UI elements
- ✅ Identify test types needed (unit, integration, E2E)
- ✅ Estimate test complexity (simple, medium, complex)

**Output**: Clear testing requirements for implementation team

---

### Phase 2: PLAN (plan.md)

**Focus**: How will we test it?

- ✅ Select test types (unit, integration, E2E)
- ✅ Choose test workflow (TDD mandatory for backend, TDD recommended for web, test-after for mobile)
- ✅ Set coverage targets (80% per platform)
- ✅ Estimate test effort (30-40% of total effort)
- ✅ Verify constitution compliance (80% coverage requirement)

**Output**: Test strategy and effort estimation

---

### Phase 3: TASKS (tasks.md)

**Focus**: What test tasks need to be done?

✅ **Already complete!** The tasks template is excellent:
- Test tasks organized by user story
- TDD workflow enforced (Red-Green-Refactor mandatory for backend, recommended for web)
- Platform-specific test locations documented
- 80% coverage targets specified
- Given-When-Then structure enforced

**Output**: Concrete test tasks per platform

---

### Phase 4: ESTIMATION (estimation-methodology.md)

**Focus**: How much testing effort vs implementation?

- ✅ Track testing days separately from implementation days
- ✅ Verify test task ratio (30-40% of total)
- ✅ Monitor testing variance (underestimated/overestimated)
- ✅ Learn patterns (e.g., "E2E tests always take 2× estimate")

**Output**: Testing effort variance and learning

---

## Testing Effort Formula

```
Test Days = Implementation Days × Test Ratio

Where Test Ratio:
- Simple features (1-2 SP): 0.2-0.3 (20-30%)
- Medium features (3-5 SP): 0.3-0.4 (30-40%)
- Complex features (8-13 SP): 0.4-0.5 (40-50%)
```

**Example** (3 SP feature):
```
Implementation: 9 days (70%)
Testing: 3.5 days (30%) = 9 × 0.35
Total: 12.5 days
```

---

## Test Type Distribution

Typical test effort breakdown:

- **Unit Tests**: 50-60% (fastest, most coverage)
- **Integration Tests**: 15-20% (backend API endpoints)
- **E2E Tests**: 25-30% (user flows, Page Objects, stability)

---

## Quality Gates by Phase

### SPEC Phase
- [ ] All UI elements have planned test identifiers
- [ ] All user stories have Given-When-Then acceptance scenarios
- [ ] Test strategy documented (for complex features)

### PLAN Phase
- [ ] Test types selected (unit, integration, E2E)
- [ ] Test workflow chosen (TDD vs test-after)
- [ ] Coverage targets set (80% per platform)
- [ ] Test effort estimated (30-40% of total)
- [ ] Constitution compliance verified

### TASKS Phase
- [ ] Test tasks created before implementation tasks (TDD)
- [ ] Test locations specified per platform
- [ ] Fake repositories planned
- [ ] Test data fixtures planned

### IMPLEMENTATION Phase
- [ ] Unit tests: 80%+ coverage ✅
- [ ] Integration tests: All API endpoints ✅
- [ ] E2E tests: All user stories ✅
- [ ] Linting: No violations ✅
- [ ] Code review: Approved ✅

---

## When to Use What

### Use Test Identifiers Section (spec.md)
- ✅ **Always** for UI features (Android, iOS, Web)
- ❌ **Skip** for backend-only features

### Use Testing Strategy Section (spec.md)
- ✅ **Use** for complex features (5+ SP)
- ✅ **Use** when testing approach is non-standard
- ⚠️ **Optional** for simple features (1-2 SP)

### Use Test Strategy Decisions (plan.md)
- ✅ **Always** - mandatory for all features

### Use Test Effort Estimation (plan.md)
- ✅ **Always** - mandatory after tasks.md completion

### Use Test Execution Checklist (tasks.md)
- ✅ **Use** for complex features or team onboarding
- ⚠️ **Optional** for experienced teams

---

## Benefits

1. **Traceability**: Test IDs → Scenarios → Tasks → Execution
2. **Estimation Accuracy**: Testing effort explicitly tracked
3. **Quality Assurance**: 80% coverage enforced at every phase
4. **Developer Guidance**: TDD workflow documented and enforced
5. **Learning**: Testing variance tracked for future estimates

---

## Next Steps

### Immediate (Update Templates)

1. Update `spec-template.md` with Test Identifiers + Testing Strategy sections
2. Update `plan-template.md` with Test Strategy Decisions + Test Effort Estimation
3. Update `tasks-template.md` with Test Execution Checklist (minor)
4. Update `estimation-methodology.md` with Testing Effort Estimation section
5. Create `testing-checklist-template.md` (optional helper)

### Next Feature (Validation)

1. Apply updated templates to next new feature
2. Gather developer feedback
3. Refine templates based on real usage

### Optional (Backfill)

1. Identify existing features missing test documentation
2. Retroactively add Test Identifiers section
3. Retroactively add Testing Strategy section

---

## Quick Reference Commands

### Run Unit Tests
```bash
# Android
./gradlew :composeApp:testDebugUnitTest koverHtmlReport

# iOS
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES

# Web
cd webApp && npm test --coverage

# Backend
cd server && npm test --coverage
```

### Run E2E Tests
```bash
cd e2e-tests/java

# Web
mvn test -Dtest=WebTestRunner

# Android
mvn test -Dtest=AndroidTestRunner

# iOS
mvn test -Dtest=IosTestRunner
```

### Run Linters
```bash
# Android
./gradlew detekt ktlintCheck :composeApp:lintDebug

# Backend + Web
cd server && npm run lint
cd webApp && npm run lint
```

---

## Resources

- **Full Proposal**: [`.specify/memory/testing-integration-proposal.md`](../.specify/memory/testing-integration-proposal.md)
- **Testing Workflow**: [`docs/testing-workflow.md`](testing-workflow.md)
- **Estimation Methodology**: [`.specify/memory/estimation-methodology.md`](../.specify/memory/estimation-methodology.md)
- **Constitution**: [`.specify/memory/constitution.md`](../.specify/memory/constitution.md)

---

**Last Updated**: 2026-01-08
