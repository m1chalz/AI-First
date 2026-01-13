# Testing Workflow & Strategy

**Purpose**: This document explains when and what type of tests to run during development, code review, and deployment.

---

## Table of Contents

1. [Testing Pyramid](#testing-pyramid)
2. [When to Run Tests](#when-to-run-tests)
3. [Development Workflows](#development-workflows)
4. [Quality Gates](#quality-gates)
5. [Test Decision Tree](#test-decision-tree)
6. [Coverage Requirements](#coverage-requirements)
7. [Troubleshooting](#troubleshooting)

---

## Testing Pyramid

PetSpot follows the standard testing pyramid with these ratios:

```
         /\
        /E2E\        ~5% of tests (slowest, most expensive)
       /----\        - User flows across platforms
      / Intg \       ~15% of tests (medium speed)
     /--------\      - API endpoints, service integration
    /   Unit   \     ~80% of tests (fastest, most coverage)
   /------------\    - Business logic, ViewModels, services
```

### Test Types by Platform

| Platform | Unit Tests | Integration Tests | E2E Tests |
|----------|-----------|-------------------|-----------|
| **Android** | Domain, ViewModels, repositories | N/A | Java/Appium |
| **iOS** | Domain, ViewModels, repositories | N/A | Java/Appium |
| **Web** | Hooks, services, components | N/A | Java/Selenium |
| **Backend** | Services, lib utilities | REST API endpoints | N/A |

---

## When to Run Tests

### 🟢 Always (Every Code Change)

**Unit Tests** - Fast feedback loop (< 10 seconds)

```bash
# Android
./gradlew :composeApp:testDebugUnitTest

# iOS (in Xcode or CLI)
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15'

# Web
cd webApp && npm test

# Backend
cd server && npm test
```

**When**: After every code change, before committing

**Why**: Catch bugs immediately, validate logic, maintain code quality

---

### 🟡 Frequently (Before Committing)

**Unit Tests + Coverage Report** - Verify coverage targets (< 30 seconds)

```bash
# Android
./gradlew :composeApp:testDebugUnitTest koverHtmlReport
# View: composeApp/build/reports/kover/html/index.html

# iOS
# In Xcode: Product → Test (with coverage enabled)
# View: Coverage tab in Report Navigator

# Web
cd webApp && npm test --coverage
# View: webApp/coverage/index.html

# Backend
cd server && npm test --coverage
# View: server/coverage/index.html
```

**When**: Before committing code

**Why**: Ensure 80% coverage requirement is met

---

### 🟠 Regularly (Before Push/PR)

**Linting + Static Analysis** - Code quality checks (< 1 minute)

```bash
# Android (Detekt + ktlint + Android Lint)
./gradlew detekt ktlintCheck :composeApp:lintDebug

# Backend + Web (ESLint)
cd server && npm run lint
cd webApp && npm run lint
```

**When**: Before pushing to remote, before opening PR

**Why**: Maintain code style consistency, catch potential bugs

---

**Integration Tests** - API endpoint validation (1-3 minutes)

```bash
# Backend only
cd server && npm test --coverage
```

**When**: After implementing/changing API endpoints

**Why**: Verify REST API contracts, HTTP status codes, error handling

---

### 🔴 Occasionally (Before Merge/Deploy)

**E2E Smoke Tests** - Critical user flows (5-15 minutes)

```bash
# Web smoke tests (fastest)
cd e2e-tests/java
mvn test -Dtest=WebTestRunner -Dcucumber.filter.tags="@web and @smoke"

# Mobile smoke tests (slower)
mvn test -Dtest=AndroidTestRunner -Dcucumber.filter.tags="@android and @smoke"
mvn test -Dtest=IosTestRunner -Dcucumber.filter.tags="@ios and @smoke"
```

**When**: Before merging major features, before deployment

**Why**: Validate critical user journeys across real UI

---

**Full E2E Test Suite** - Complete regression (30-60 minutes)

```bash
cd e2e-tests/java

# All platforms
mvn test -Dtest=WebTestRunner
mvn test -Dtest=AndroidTestRunner
mvn test -Dtest=IosTestRunner
```

**When**: Before production deployment, weekly regression runs

**Why**: Comprehensive validation of all features across platforms

---

## Development Workflows

### Backend Development (TDD - Mandatory)

Backend follows strict **Test-Driven Development**:

```
1. Write Failing Test (RED)
   └─ npm test (should fail)

2. Implement Minimal Code (GREEN)
   └─ npm test (should pass)

3. Refactor Code (REFACTOR)
   └─ npm test (should still pass)
   └─ npm test --coverage (80%+ required)

4. Commit
   └─ npm run lint (must pass)
```

**Example**:

```typescript
// Step 1: Write failing test (RED)
it('should return pet by id', async () => {
    const pet = await petService.getPetById('123')
    expect(pet).toBeDefined()
    expect(pet.id).toBe('123')
})

// Step 2: Implement minimal solution (GREEN)
async getPetById(id: string) {
    return await db('pets').where({ id }).first()
}

// Step 3: Refactor (if needed)
async getPetById(id: string) {
    const pet = await db('pets').where({ id }).first()
    if (!pet) throw new NotFoundError('Pet not found')
    return pet
}
```

---

### Frontend Development (Test-After)

Frontend platforms (Android/iOS/Web) follow **implementation-first, then test**:

```
1. Implement Feature
   └─ ViewModels, repositories, UI components

2. Write Unit Tests
   └─ Test domain logic, ViewModels, services
   └─ Run: npm test (or ./gradlew test)

3. Verify Coverage
   └─ Run: npm test --coverage
   └─ Ensure: 80%+ coverage for business logic

4. Commit
   └─ Run: npm run lint (or ./gradlew ktlintCheck)
   └─ Git hooks run automatically
```

**Coverage Targets**:
- ✅ **Must test**: ViewModels, domain logic, repositories, services, hooks
- ⚠️ **Optional**: UI components (Compose/SwiftUI/React) - focus on logic, not rendering
- ❌ **Don't test**: Simple getters/setters, trivial mappers, generated code

---

### E2E Test Development

E2E tests are written **after feature completion** across all platforms:

```
1. Feature Complete on All Platforms
   └─ Android, iOS, Web, Backend

2. Write Gherkin Scenario
   └─ e2e-tests/java/src/test/resources/features/

3. Implement Step Definitions
   └─ Java Page Objects + Step Definitions

4. Run E2E Test
   └─ mvn test -Dtest=WebTestRunner (or Android/iOS)

5. Verify Cross-Platform
   └─ Same scenario should pass on all platforms
```

**When to write E2E tests**:
- ✅ New user-facing feature (create announcement, view details, search)
- ✅ Critical user flow (authentication, payment, data submission)
- ✅ Cross-platform behavior (same feature works on Android/iOS/Web)
- ❌ Internal refactoring (no user-visible changes)
- ❌ Unit-testable logic (use unit tests instead)

---

## Quality Gates

### Pre-Commit Checklist

Before running `git commit`, ensure:

- [x] Unit tests pass locally
- [x] Coverage ≥ 80% for changed files
- [x] Linter passes (no errors)
- [x] Code follows platform conventions

**Automated**: Git hooks (installed via `./scripts/install-hooks.sh`) will run:
- Static analysis (Detekt for Android)
- Linting
- Tests for staged files

**To bypass** (emergency only):
```bash
git commit --no-verify -m "Emergency fix"
```

---

### Pre-Push Checklist

Before running `git push`, ensure:

- [x] All unit tests pass
- [x] Integration tests pass (backend changes)
- [x] Linting passes on all platforms
- [x] No TODO/FIXME comments for critical issues
- [x] Coverage reports reviewed

**Commands**:
```bash
# Run all checks
./gradlew test detekt ktlintCheck :composeApp:lintDebug
cd server && npm test --coverage && npm run lint
cd webApp && npm test --coverage && npm run lint
```

---

### Pre-Merge (Pull Request) Checklist

Before merging PR to `main`, ensure:

- [x] All unit tests pass
- [x] All integration tests pass
- [x] Coverage ≥ 80% (enforced per platform)
- [x] Code review approved
- [x] Smoke E2E tests pass (critical flows)
- [x] No linter warnings
- [x] Documentation updated (if needed)

**Smoke tests** (run by reviewer or CI):
```bash
cd e2e-tests/java
mvn test -Dcucumber.filter.tags="@smoke"
```

---

### Pre-Deployment Checklist

Before deploying to production:

- [x] All tests pass (unit + integration + E2E)
- [x] Full E2E regression suite passes
- [x] Manual smoke test on staging environment
- [x] Database migrations tested (if any)
- [x] Rollback plan documented

**Full E2E suite**:
```bash
cd e2e-tests/java
mvn clean test
```

---

## Test Decision Tree

### When implementing a new feature, what should I test?

```
┌─────────────────────────────────┐
│ Is this backend API logic?      │
├─────────────────────────────────┤
│ YES → TDD Workflow               │
│   1. Write failing test          │
│   2. Implement minimal code      │
│   3. Refactor                    │
│   4. Integration test (endpoint) │
│   5. E2E test (user flow)        │
└─────────────────────────────────┘
│ NO ↓
┌─────────────────────────────────┐
│ Is this business logic?          │
│ (ViewModel, service, use case)   │
├─────────────────────────────────┤
│ YES → Unit Test (REQUIRED)       │
│   - Test all branches            │
│   - Test error cases             │
│   - Mock dependencies            │
│   - 80%+ coverage                │
└─────────────────────────────────┘
│ NO ↓
┌─────────────────────────────────┐
│ Is this UI component?            │
│ (Composable, SwiftUI View, React)│
├─────────────────────────────────┤
│ YES → E2E Test (OPTIONAL)        │
│   - Only if critical user flow   │
│   - Use test identifiers         │
│   - Unit test logic, not render  │
└─────────────────────────────────┘
│ NO ↓
┌─────────────────────────────────┐
│ Is this a mapper/utility?        │
├─────────────────────────────────┤
│ YES → Unit Test (if complex)     │
│   - Test edge cases              │
│   - Test null handling           │
│   - Skip trivial mappers         │
└─────────────────────────────────┘
```

---

## Coverage Requirements

### 80% Minimum (Enforced)

Each platform MUST maintain **80% line + branch coverage**:

| Platform | What to Cover | What to Skip |
|----------|---------------|--------------|
| **Android** | ViewModels, use cases, domain models, repositories | UI Composables, data classes with no logic |
| **iOS** | ViewModels, domain models, repositories | SwiftUI Views, simple structs |
| **Web** | Hooks, services, models | React components (render), CSS |
| **Backend** | Services, lib utilities, API endpoints | Express middleware (simple), generated code |

### Coverage Reports

**View coverage after running tests**:

```bash
# Android
./gradlew :composeApp:testDebugUnitTest koverHtmlReport
open composeApp/build/reports/kover/html/index.html

# iOS (in Xcode)
# Product → Test → Show Report Navigator → Coverage tab

# Web
cd webApp && npm test --coverage
open coverage/index.html

# Backend
cd server && npm test --coverage
open coverage/index.html
```

### What if coverage is below 80%?

1. **Identify untested code**: Review coverage report
2. **Write missing tests**: Focus on business logic, not UI rendering
3. **Refactor if needed**: Extract testable logic from UI components
4. **Re-run coverage**: Verify 80%+ achieved
5. **Commit**: Only commit when coverage requirement met

---

## Test Naming Conventions

### Given-When-Then Structure (Mandatory)

All tests MUST follow Given-When-Then (Arrange-Act-Assert):

**Android (Kotlin)**:
```kotlin
@Test
fun `should load pets successfully when repository returns data`() = runTest {
    /* Given */
    val mockPets = listOf(Pet(id = "1", name = "Max"))
    coEvery { repository.getPets() } returns Result.success(mockPets)
    
    /* When */
    viewModel.handleIntent(UserIntent.LoadPets)
    
    /* Then */
    val state = viewModel.uiState.value
    assertEquals(mockPets, state.pets)
    assertFalse(state.isLoading)
}
```

**iOS (Swift)**:
```swift
func testLoadPets_whenRepositorySucceeds_shouldUpdatePetsState() async {
    /* Given */
    let mockPets = [Pet(id: "1", name: "Max")]
    fakeRepository.getPetsResult = .success(mockPets)
    
    /* When */
    await viewModel.loadPets()
    
    /* Then */
    XCTAssertEqual(viewModel.pets, mockPets)
    XCTAssertFalse(viewModel.isLoading)
}
```

**Web/Backend (TypeScript)**:
```typescript
it('should return 200 when pet exists', async () => {
    /* Given */
    const petId = '123'
    await db('pets').insert({ id: petId, name: 'Max' })
    
    /* When */
    const response = await request(app).get(`/api/v1/announcements/${petId}`)
    
    /* Then */
    expect(response.status).toBe(200)
    expect(response.body.id).toBe(petId)
})
```

---

## Test Identifiers (E2E)

All interactive UI elements MUST have test identifiers:

| Platform | Identifier | Example |
|----------|-----------|---------|
| **Android** | `Modifier.testTag()` | `.testTag("petList.item.123")` |
| **iOS** | `.accessibilityIdentifier()` | `.accessibilityIdentifier("petList.item.123")` |
| **Web** | `data-testid` | `data-testid="petList.item.123"` |

**Naming convention**: `{screen}.{element}.{action-or-id}`

Examples:
- `petList.addButton.click`
- `petDetails.shareButton.click`
- `reportMissing.photoUpload.input`
- `petList.item.${pet.id}`

---

## Troubleshooting

### "Tests are too slow"

**Symptoms**: Unit tests take > 10 seconds

**Solutions**:
- ✅ Mock external dependencies (API, database)
- ✅ Use in-memory implementations for repositories
- ✅ Avoid real network calls
- ✅ Run tests in parallel (if framework supports)

**Example** (before/after):
```kotlin
// ❌ Slow - real API call
val pets = api.getPets()

// ✅ Fast - mocked
coEvery { api.getPets() } returns mockPets
```

---

### "Coverage is stuck below 80%"

**Symptoms**: Coverage report shows < 80%

**Solutions**:

1. **Identify untested files**: Check coverage report
   ```bash
   # Look for files with < 80% in report
   open coverage/index.html
   ```

2. **Test business logic first**: Focus on ViewModels, services, use cases
   - ✅ Test: State changes, error handling, data transformations
   - ❌ Skip: UI rendering, simple getters, generated code

3. **Extract testable logic**: Move logic from UI to ViewModels
   ```kotlin
   // ❌ Hard to test - logic in UI
   @Composable
   fun PetList() {
       val pets = remember { repository.getPets() } // Hard to mock
   }
   
   // ✅ Easy to test - logic in ViewModel
   class PetListViewModel(private val repository: PetRepository) {
       fun loadPets() { /* testable */ }
   }
   ```

---

### "E2E tests are flaky"

**Symptoms**: Tests pass/fail randomly

**Solutions**:

1. **Add explicit waits**: Don't rely on implicit timing
   ```java
   // ❌ Flaky - might not be ready
   driver.findElement(By.id("button")).click();
   
   // ✅ Stable - wait for element
   WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
   wait.until(ExpectedConditions.elementToBeClickable(By.id("button"))).click();
   ```

2. **Use stable test identifiers**: Avoid XPath, CSS selectors
   ```java
   // ❌ Flaky - brittle selector
   driver.findElement(By.xpath("//div[3]/button"))
   
   // ✅ Stable - test identifier
   driver.findElement(By.cssSelector("[data-testid='petList.addButton.click']"))
   ```

3. **Create test data via API**: Don't rely on seed data
   ```java
   // ❌ Flaky - assumes data exists
   driver.get("/pets/123")
   
   // ✅ Stable - create data first
   String petId = createTestPet()
   driver.get("/pets/" + petId)
   deleteTestPet(petId) // cleanup
   ```

---

### "Git hooks are failing"

**Symptoms**: `git commit` rejected by pre-commit hook

**Solutions**:

1. **Run checks manually**:
   ```bash
   ./gradlew detekt ktlintCheck :composeApp:lintDebug
   npm run lint
   npm test
   ```

2. **Fix issues** shown in output

3. **Commit again**

**Emergency bypass** (use sparingly):
```bash
git commit --no-verify -m "Emergency fix"
```

---

## Quick Reference Card

Print this and keep it visible!

```
┌─────────────────────────────────────────────────────────┐
│                 PETSPOT TESTING CHEAT SHEET              │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  🟢 ALWAYS (every change)                                │
│     npm test (or ./gradlew test)                         │
│                                                          │
│  🟡 BEFORE COMMIT                                        │
│     npm test --coverage (80%+ required)                  │
│     npm run lint                                         │
│                                                          │
│  🟠 BEFORE PUSH                                          │
│     All unit tests + integration tests                   │
│     Static analysis (Detekt/ESLint)                      │
│                                                          │
│  🔴 BEFORE MERGE                                         │
│     Smoke E2E: mvn test -Dcucumber.filter.tags="@smoke"  │
│     Code review approved                                 │
│                                                          │
│  📊 COVERAGE REQUIREMENT                                 │
│     80% minimum (line + branch)                          │
│                                                          │
│  🧪 TEST TYPES                                           │
│     Unit: Fast, business logic (80% of tests)            │
│     Integration: API endpoints (15% of tests)            │
│     E2E: User flows (5% of tests)                        │
│                                                          │
│  📝 TEST STRUCTURE                                       │
│     /* Given */ - Setup                                  │
│     /* When */  - Action                                 │
│     /* Then */  - Assert                                 │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

---

## Additional Resources

- [Testing Commands](../README.md#testing) - Quick reference for all test commands
- [E2E Tests Setup](../e2e-tests/README.md) - Complete E2E infrastructure guide
- [Backend Manual Testing](../server/MANUAL_TESTING.md) - API testing with curl
- [Static Analysis Setup](./static-analysis-setup.md) - Linting and code quality tools
- [Constitution](../.specify/memory/constitution.md) - Architectural principles

---

## Questions?

If you have questions about testing strategy:

1. **Check this document first**
2. **Review platform-specific README** (`server/README.md`, `e2e-tests/README.md`)
3. **Ask in team chat** or code review
4. **Update this document** if you find gaps or ambiguities

**Last Updated**: January 2026
