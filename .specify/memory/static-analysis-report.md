# Android Static Analysis Report

**Date:** 2025-11-26  
**Project:** PetSpot - Android (composeApp)  
**Branch:** 010-pet-details-screen

---

## Executive Summary

The Android project has **2 failing** static analysis tools and **1 passing**:

| Tool | Status | Issues |
|------|--------|--------|
| **Detekt** | ✅ PASS | 0 violations |
| **ktlint** | ❌ FAIL | 211+ violations |
| **Android Lint** | ❌ FAIL | 5 errors, 5 warnings |

**Action Required:** Fix ktlint and Lint violations before merge

---

## 1. Detekt Analysis ✅ PASS

### Status
- **Result:** SUCCESS (no violations)
- **Command:** `./gradlew :composeApp:detekt`
- **Configuration:** `detekt.yml` (root)

### Details
Detekt performed a comprehensive static analysis against 71 rules covering:
- Code complexity (cyclomatic complexity, nesting depth, method length)
- Exception handling best practices
- Naming conventions
- Performance optimizations
- Potential bugs
- Style guidelines

**All modules passed:**
- ✅ `composeApp` (Android main source)
- ✅ `shared` (Kotlin Multiplatform code)

---

## 2. ktlint Formatting Analysis ❌ FAIL

### Status
- **Result:** FAILED
- **Command:** `./gradlew :composeApp:ktlintCheck`
- **Violations:** 211 total across 4 test files

### Critical Error
```
Rule 'standard:argument-list-wrapping' throws exception in file 'PetDetailsReducer.kt' at position (25:27)
KtLint failed to parse file: PetDetailsReducer.kt
```

The `PetDetailsReducer.kt` file (lines 22-26) has a syntax issue that crashes ktlint:

```kotlin
fun reduce(state: PetDetailsUiState, result: Result<Animal>): PetDetailsUiState =
    result.fold(
        onSuccess = { pet -> 
            state.copy(pet = pet, isLoading = false, error = null) 
        },
```

**Issue:** Multiline lambda with trailing spaces and inconsistent formatting

### Violations Summary

| Rule | Count | Files Affected |
|------|-------|-----------------|
| `no-trailing-spaces` | 122 | All 4 test files |
| `blank-line-before-declaration` | 41 | All 4 test files |
| `multiline-expression-wrapping` | 13 | 3 test files |
| `function-signature` | 8 | 3 test files |
| `no-consecutive-blank-lines` | 6 | 4 test files |
| `no-empty-first-line-in-class-body` | 6 | 4 test files |
| `trailing-comma-on-call-site` | 5 | 2 test files |

### Affected Files
1. `composeApp/src/androidUnitTest/kotlin/.../GetAnimalByIdUseCaseTest.kt` - ~29 violations
2. `composeApp/src/androidUnitTest/kotlin/.../PetDetailsReducerTest.kt` - ~61 violations
3. `composeApp/src/androidUnitTest/kotlin/.../PetDetailsViewModelTest.kt` - ~92 violations
4. `composeApp/src/androidUnitTest/kotlin/.../DateFormatterTest.kt` - ~14 violations
5. `composeApp/src/androidUnitTest/kotlin/.../LocationFormatterTest.kt` - ~12 violations
6. `composeApp/src/androidUnitTest/kotlin/.../MicrochipFormatterTest.kt` - ~12 violations

**Note:** All violations are in test files (`androidUnitTest/`), not production code.

---

## 3. Android Lint Analysis ❌ FAIL

### Status
- **Result:** FAILED (errors abort build)
- **Command:** `./gradlew :composeApp:lint`
- **Total Issues:** 5 errors, 5 warnings (4 errors, 5 warnings filtered by baseline)

### Errors (BUILD BLOCKING)

#### 1. NewApi: java.time.DateTimeFormatter (5 occurrences)

**File:** `DateFormatter.kt` lines 13, 14, 27-29

**Problem:** `java.time` API requires Android API 26, but minSdk is 24

```kotlin
// Line 13-14: DateTimeFormatter creation
private val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.US)
private val outputFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.US)

// Line 27-29: Date parsing and parsing exception
val date = LocalDate.parse(dateString, inputFormatter)
date.format(outputFormatter)
} catch (e: DateTimeParseException) {
```

**Solutions:**
1. Increase minSdk to 26 (requires verification)
2. Add core library desugaring support (recommended for Java 8+ APIs on older devices)
3. Use `@TargetApi(26)` annotation to suppress on specific methods
4. Use a backward-compatible date library (e.g., ThreeTenABP)

---

### Warnings (Non-blocking)

#### 1. DefaultLocale: String.format (2 occurrences)

**File:** `LocationFormatter.kt` lines 29-30

```kotlin
val formattedLat = String.format("%.4f", abs(latitude))
val formattedLon = String.format("%.4f", abs(longitude))
```

**Issue:** Implicitly uses device locale, causing bugs in non-English locales (e.g., Turkish)

**Fix:** Use explicit `Locale.US` or `Locale.ROOT`:
```kotlin
val formattedLat = String.format(Locale.US, "%.4f", abs(latitude))
```

---

#### 2. UseKtx Suggestions (2 occurrences)

**Files:**
- `PetDetailsScreen.kt:51` - Use `String.toUri()` instead of `Uri.parse()`
- `StatusBadge.kt:23` - Use `String.toColorInt()` instead of `Color.parseColor()`

**These are suggestions, not errors** - can be addressed during refactoring.

---

#### 3. IconLocation: Bitmap in densityless folder

**File:** `res/drawable/ic_list_image_default.png`

**Issue:** PNG bitmap placed in generic `drawable/` folder instead of density-specific folder

**Fix:** Move to `drawable-mdpi/` (or appropriate density folder)

---

## Recommendations

### Priority 1: Critical (Blocks Build)

1. **Fix PetDetailsReducer.kt ktlint crash**
   - Remove trailing spaces in lambda bodies
   - Fix multiline expression formatting to start on new line
   - Add proper blank line spacing

2. **Resolve Android Lint NewApi errors for DateTimeFormatter**
   - Decision: Choose between:
     - Option A: Add core library desugaring (recommended for future-proofing)
     - Option B: Increase minSdk to 26 (simple but limits device support)
   - Implement chosen solution across all 5 occurrences

### Priority 2: High (Test Code Quality)

3. **Fix ktlint violations in test files**
   - Remove 122 trailing spaces
   - Add blank lines before declarations (41 cases)
   - Fix multiline expression wrapping (13 cases)
   - Add trailing commas (5 cases)

   **Recommendation:** Run `./gradlew :composeApp:ktlintFormat` to auto-fix style violations

### Priority 3: Medium (Lint Warnings)

4. **Fix DefaultLocale in LocationFormatter**
   - Add `Locale.US` parameter to `String.format()` calls

5. **Address UseKtx suggestions** (optional - no functional impact)
   - Modernize API calls to use KTX extensions

6. **Move bitmap to density-specific folder**
   - Create `drawable-mdpi/` folder
   - Move `ic_list_image_default.png`

---

## Next Steps

1. **Run ktlint auto-format:**
   ```bash
   ./gradlew :composeApp:ktlintFormat
   ```

2. **Fix PetDetailsReducer.kt manually** (if ktlint format doesn't resolve the parse error)

3. **Address DateTimeFormatter API level issue** (choose desugaring vs. minSdk bump)

4. **Fix DefaultLocale warnings** in LocationFormatter

5. **Re-run all tools:**
   ```bash
   ./gradlew :composeApp:detekt :composeApp:ktlintCheck :composeApp:lint
   ```

6. **Verify all three tools pass before merge**

---

## Tool Configurations

### Detekt
- **Config:** `/detekt.yml` (root)
- **Rules:** 71 active rules across 10 categories
- **Max Issues:** 0 (fail on any error-level issues)

### ktlint
- **Config:** `composeApp/build.gradle.kts` (android.set(true))
- **Version:** Latest from `libs.versions.ktlint.engine`
- **Auto-fix:** Supported via `ktlintFormat` task

### Android Lint
- **Config:** `composeApp/build.gradle.kts` + `lint.xml`
- **Baseline:** `lint-baseline.xml` (1 fixed UseKtx issue)
- **Abort on Error:** Enabled (will fail build)


