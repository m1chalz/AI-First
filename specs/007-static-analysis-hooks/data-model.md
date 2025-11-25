# Data Model: Static Analysis Git Hooks

**Date**: 2025-11-19  
**Feature**: Static Analysis Git Hooks  
**Plan**: [plan.md](./plan.md)

## Overview

This document defines the data structures and formats used by the static analysis git hooks feature. Since this is tooling infrastructure, the data model focuses on analysis output formats, configuration structures, and report formats rather than domain entities.

## 1. Analysis Result

Represents a single violation detected by static analysis tools (Detekt or ktlint).

### Structure

```kotlin
data class AnalysisViolation(
    val tool: Tool,              // Which tool detected this violation
    val file: String,            // Relative path from repository root
    val line: Int,               // Line number where violation occurs
    val column: Int?,            // Column number (optional, not all tools provide)
    val rule: String,            // Rule ID (e.g., "ComplexMethod", "MaxLineLength")
    val severity: Severity,      // Error, Warning, or Info
    val message: String,         // Human-readable description
    val canBeAutoCorrected: Boolean = false  // Whether tool can auto-fix
)

enum class Tool {
    DETEKT,
    KTLINT,
    ANDROID_LINT
}

enum class Severity {
    ERROR,    // Blocks commit
    WARNING,  // Allows commit, displays in output
    INFO      // Informational only
}
```

### Example (Detekt Output)

```text
ComplexMethod - /shared/src/commonMain/kotlin/Service.kt:45:5
  The function processData is too complex (20 complexity). Consider refactoring.
  Severity: ERROR
```

### Example (ktlint Output)

```text
MaxLineLength - /composeApp/src/androidMain/kotlin/ui/Screen.kt:78:1
  Exceeded max line length (120)
  Severity: ERROR
  Can be auto-corrected: No
```

### Example (Android Lint Output)

```text
NewApi - /composeApp/src/androidMain/kotlin/MainActivity.kt:42:5
  Call requires API level 33 (current min is 24): android.app.Notification.Builder#setForegroundServiceBehavior
  Severity: ERROR
  Can be auto-corrected: No
```

## 2. Violation Report

Aggregated analysis results for a set of files (either staged files or entire codebase).

### Structure

```kotlin
data class ViolationReport(
    val timestamp: Instant,
    val scope: AnalysisScope,
    val filesAnalyzed: Int,
    val violations: List<AnalysisViolation>,
    val summary: ViolationSummary
)

enum class AnalysisScope {
    STAGED_FILES,      // Git hook analyzing only staged files
    FULL_CODEBASE,     // Baseline analysis of all Kotlin files
    SHARED_MODULE,     // Only /shared module
    ANDROID_PLATFORM   // Only /composeApp module
}

data class ViolationSummary(
    val errorCount: Int,
    val warningCount: Int,
    val infoCount: Int,
    val filesByViolationCount: Map<String, Int>,  // File -> violation count
    val rulesByFrequency: Map<String, Int>         // Rule -> occurrence count
)
```

### Example Report (Terminal Output)

```text
Static Analysis Report
Scope: Staged Files
Files Analyzed: 5
Timestamp: 2025-11-19 14:30:45

ERRORS (3):
  /shared/src/commonMain/kotlin/Service.kt:
    - Line 45: ComplexMethod - Function too complex (20 complexity)
    - Line 78: LongMethod - Function too long (65 lines)
  
  /composeApp/src/androidMain/kotlin/ui/Screen.kt:
    - Line 120: MaxLineLength - Exceeded max line length (120)

WARNINGS (2):
  /shared/src/commonMain/kotlin/Model.kt:
    - Line 23: MagicNumber - Magic number used (42)
  
  /composeApp/src/androidMain/kotlin/ViewModel.kt:
    - Line 55: UnusedPrivateMember - Private member never used

Summary:
  ✗ 3 errors (commit blocked)
  ⚠ 2 warnings (commit allowed with warnings)
  
Most violated files:
  1. Service.kt (2 violations)
  2. Screen.kt (1 violation)

Most common rules:
  1. ComplexMethod (1 occurrence)
  2. LongMethod (1 occurrence)
```

## 3. Rule Configuration

Configuration for static analysis rules (Detekt and ktlint).

### Detekt Configuration Structure

```yaml
# detekt.yml

build:
  maxIssues: 0              # Fail build if any issues found
  excludeCorrectable: false  # Include auto-correctable issues
  weights:
    complexity: 2           # Weight for complexity issues
    LongParameterList: 1
    style: 1
    comments: 1

config:
  validation: true          # Validate config file on load
  warningsAsErrors: false   # Treat warnings as errors

complexity:
  active: true
  ComplexMethod:
    active: true
    threshold: 15           # Max cyclomatic complexity
    includeStaticDeclarations: false
    includePrivateDeclarations: false
  LongMethod:
    active: true
    threshold: 60           # Max lines in method
  LongParameterList:
    active: true
    functionThreshold: 6    # Max parameters
    constructorThreshold: 7
  TooManyFunctions:
    active: true
    thresholdInFiles: 11
    thresholdInClasses: 11

style:
  active: true
  MaxLineLength:
    active: true
    maxLineLength: 120
    excludePackageStatements: true
    excludeImportStatements: true
  MagicNumber:
    active: true
    ignoreNumbers: ['-1', '0', '1', '2']
    ignoreHashCodeFunction: true
    ignorePropertyDeclaration: true
    ignoreAnnotation: true
    ignoreEnums: true

formatting:
  active: true
  android: false            # Don't use Android-specific formatting
  autoCorrect: true         # Allow auto-correction
  Indentation:
    active: true
    indentSize: 4
  NoUnusedImports:
    active: true
```

### ktlint Configuration Structure

```ini
# .editorconfig

root = true

[*]
charset = utf-8
end_of_line = lf
insert_final_newline = true
trim_trailing_whitespace = true

[*.{kt,kts}]
indent_size = 4
indent_style = space
max_line_length = 120
ij_kotlin_allow_trailing_comma = true
ij_kotlin_allow_trailing_comma_on_call_site = true

# Disable specific ktlint rules
ktlint_standard_no-wildcard-imports = disabled  # Allow wildcard imports
ktlint_standard_filename = disabled              # Allow flexible file naming
```

## 4. Baseline File

Detekt baseline file format (XML) for managing existing violations during migration.

### Structure

```xml
<!-- detekt-baseline.xml -->
<SmellBaseline>
  <ManuallySuppressedIssues></ManuallySuppressedIssues>
  <CurrentIssues>
    <ID>ComplexMethod:Service.kt$Service$fun processData():45:5</ID>
    <ID>LongMethod:Repository.kt$RepositoryImpl$fun fetchData():120:5</ID>
    <ID>TooManyFunctions:Utils.kt$Utils:15:1</ID>
  </CurrentIssues>
</SmellBaseline>
```

### Properties

- **ManuallySuppressedIssues**: Violations explicitly marked to ignore (rare)
- **CurrentIssues**: List of violation IDs present when baseline was generated
- **ID Format**: `RuleName:FilePath$ClassName$FunctionSignature:Line:Column`

### Usage

```bash
# Generate baseline
./gradlew detektBaseline

# Analysis ignores violations in baseline
./gradlew detekt

# Update baseline (after fixing some violations)
./gradlew detektBaseline
```

## 5. Progress Tracking Data

Data structure for tracking baseline violation remediation progress.

### Structure

```kotlin
data class ProgressSnapshot(
    val timestamp: Instant,
    val totalViolations: Int,
    val violationsByModule: Map<String, Int>,  // "shared" -> count, "composeApp" -> count
    val violationsBySeverity: Map<Severity, Int>,
    val violationsByRule: Map<String, Int>,
    val filesWithViolations: Int,
    val changedSinceLastSnapshot: Int  // Violations fixed or added
)

data class ProgressHistory(
    val snapshots: List<ProgressSnapshot>,
    val firstSnapshot: ProgressSnapshot,  // Baseline
    val latestSnapshot: ProgressSnapshot,
    val totalFixed: Int,                  // Violations fixed since baseline
    val percentComplete: Double           // (totalFixed / baseline) * 100
)
```

### Example Progress Report

```text
Baseline Violation Progress Report
Generated: 2025-11-19 16:00:00

Baseline (2025-11-05):
  Total Violations: 127
  Errors: 45
  Warnings: 82
  Files Affected: 23

Current (2025-11-19):
  Total Violations: 68 (-59, -46.5%)
  Errors: 12 (-33, -73.3%)
  Warnings: 56 (-26, -31.7%)
  Files Affected: 15 (-8)

Top Remaining Violations:
  1. MagicNumber: 15 occurrences
  2. ComplexMethod: 12 occurrences
  3. LongMethod: 8 occurrences
  4. UnusedPrivateMember: 7 occurrences

Modules:
  shared: 28 violations (-25, -47.2%)
  composeApp: 40 violations (-34, -45.9%)

Estimated Completion: 2 weeks at current pace
```

## 6. Hook Execution Context

Data passed between git hook stages.

### Structure

```kotlin
data class HookContext(
    val repoRoot: String,              // Absolute path to repository root
    val stagedFiles: List<String>,     // Relative paths of staged Kotlin files
    val hookType: HookType,            // PRE_COMMIT, PRE_PUSH, etc.
    val bypassRequested: Boolean,      // True if --no-verify flag used
    val gradleCommand: String          // "./gradlew" or "gradlew.bat"
)

enum class HookType {
    PRE_COMMIT,   // Run before commit
    PRE_PUSH,     // Run before push (future)
    COMMIT_MSG    // Run to validate commit message (future)
}
```

### Example (Shell Script Variables)

```bash
# pre-commit hook context
REPO_ROOT="/Users/developer/projects/AI-First"
STAGED_KOTLIN_FILES="shared/src/commonMain/kotlin/Service.kt
composeApp/src/androidMain/kotlin/ui/Screen.kt"
FILE_COUNT=2
GRADLE_CMD="./gradlew"
```

## Data Flow

```
1. Developer stages files:
   git add shared/src/commonMain/kotlin/Service.kt

2. Developer commits:
   git commit -m "Add service"

3. Git invokes pre-commit hook:
   .git/hooks/pre-commit

4. Hook gathers context:
   HookContext(
     repoRoot = "/Users/.../AI-First",
     stagedFiles = ["shared/src/.../Service.kt"],
     hookType = PRE_COMMIT,
     bypassRequested = false
   )

5. Hook runs analysis:
   ./gradlew detekt
   ./gradlew ktlintCheck

6. Tools generate violations:
   List<AnalysisViolation>

7. Hook generates report:
   ViolationReport(
     scope = STAGED_FILES,
     filesAnalyzed = 1,
     violations = [...]
   )

8. Hook decides outcome:
   - If errorCount > 0: Block commit (exit 1)
   - If warningCount > 0: Allow commit, display warnings (exit 0)
   - If no violations: Allow commit (exit 0)
```

## File Formats

### Analysis Tool Output (Parsed by Hook)

**Detekt XML Output:**
```xml
<checkstyle>
  <file name="/path/to/Service.kt">
    <error line="45" column="5" 
           severity="error" 
           message="The function is too complex" 
           source="detekt.complexity.ComplexMethod"/>
  </file>
</checkstyle>
```

**ktlint Output (Plain Text):**
```text
/path/to/Screen.kt:78:1: Exceeded max line length (120)
```

### Suppression Format (In Kotlin Source)

```kotlin
// Suppression for unavoidable complexity in platform API wrapper
@Suppress("ComplexMethod", "LongMethod")
fun complexPlatformMethod() {
    // Implementation
}
```

## Summary

This data model defines:
- **AnalysisViolation**: Individual code quality issues detected by Detekt, ktlint, or Android Lint
- **ViolationReport**: Aggregated results with summary statistics across all three tools
- **Rule Configuration**: YAML/INI/XML structures for tool configuration (Detekt, ktlint, Android Lint)
- **Baseline File**: XML format for managing existing violations (Detekt and Android Lint baselines)
- **Progress Tracking**: Remediation progress over time across all tools
- **Hook Context**: Execution context for git hooks

All data structures are designed for clarity in terminal output and easy parsing by scripts. No persistent storage is required - all data is generated on-demand from source code analysis.

