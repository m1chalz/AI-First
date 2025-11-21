# Quickstart: Static Analysis Git Hooks

**Date**: 2025-11-19  
**Feature**: Static Analysis Git Hooks  
**Plan**: [plan.md](./plan.md)

## Overview

This guide provides step-by-step instructions for installing and using the static analysis git hooks in the PetSpot project. The hooks automatically check Kotlin code quality using three complementary tools (Detekt, ktlint, and Android Lint) when you commit changes to the shared module or Android platform.

## Prerequisites

- Git installed and configured
- Java Development Kit (JDK) 17 or later
- Gradle 8.0 or later (using project's Gradle Wrapper is recommended)
- Repository cloned locally

## Installation (One-Time Setup)

### Step 1: Configure Gradle Plugins

The Detekt and ktlint Gradle plugins should already be configured in the project's `build.gradle.kts` files. Verify by checking:

```bash
# Check if plugins are applied
./gradlew tasks --group verification

# You should see:
# - detekt - Runs detekt analysis
# - ktlintCheck - Runs ktlint checks
# - ktlintFormat - Formats code with ktlint
# - lintDebug - Runs Android Lint (in composeApp module)
```

If plugins are not configured, they will be added during implementation.

### Step 2: Run Installation Script

From the repository root, run the installation script:

```bash
cd /path/to/AI-First

# Make installation script executable
chmod +x scripts/install-hooks.sh

# Run installation
./scripts/install-hooks.sh
```

The script will:
1. Check for existing pre-commit hooks (and offer to back them up)
2. Install the new pre-commit hook in `.git/hooks/pre-commit`
3. Make the hook executable
4. Verify installation

**Expected Output:**
```text
Installing git pre-commit hook for static analysis...
✓ Git hook installed successfully
  To bypass hook: git commit --no-verify
```

### Step 3: Verify Installation

Make a test commit to verify the hook is working:

```bash
# Create a test file with intentional violation
echo "fun test(){}" > test.kt
git add test.kt
git commit -m "Test commit"

# You should see static analysis running
# Expected: Commit blocked due to formatting violation (missing space after fun name)

# Clean up
git reset HEAD~1
rm test.kt
```

## Usage

### Normal Commits

When you make a commit, the hook runs automatically:

```bash
# Stage your changes
git add shared/src/commonMain/kotlin/Service.kt

# Commit (hook runs automatically)
git commit -m "[GG-123] Add new service method"

# If no violations:
#   ✓ Static analysis passed!
#   [main abc123] [GG-123] Add new service method

# If violations found:
#   ✗ Static analysis failed
#   See errors above
#   Commit blocked
```

### Understanding Analysis Output

#### Clean Commit (No Issues)

```text
Running static analysis on staged Kotlin files...
Analyzing 2 files:
  - shared/src/commonMain/kotlin/Service.kt
  - composeApp/src/androidMain/kotlin/ui/Screen.kt

✓ detekt analysis passed
✓ ktlint check passed

Static analysis passed!
[main abc123] [GG-123] Add new service method
```

#### Commit with Errors (Blocked)

```text
Running static analysis on staged Kotlin files...
Analyzing 1 file:
  - shared/src/commonMain/kotlin/Service.kt

✗ detekt analysis failed

ERRORS (2):
  Service.kt:45:5 - ComplexMethod
    The function processData is too complex (20 complexity)
    Threshold: 15
    
  Service.kt:78:12 - LongMethod
    The function loadData is too long (68 lines)
    Threshold: 60

Fix the errors above and try committing again.
To bypass this check: git commit --no-verify
```

#### Commit with Warnings (Allowed)

```text
Running static analysis on staged Kotlin files...
Analyzing 1 file:
  - shared/src/commonMain/kotlin/Model.kt

⚠ detekt analysis completed with warnings

WARNINGS (1):
  Model.kt:23:25 - MagicNumber
    Magic number detected: 42
    Consider extracting to a named constant

✓ ktlint check passed

Commit allowed with warnings.
[main abc123] [GG-123] Update model
```

### Bypassing the Hook (Emergency Only)

In emergency situations (hotfixes, time-sensitive deployments), you can bypass the hook:

```bash
# Bypass the hook with --no-verify flag
git commit --no-verify -m "[HOTFIX] Emergency fix"

# Warning: Only use this for genuine emergencies
# Create a follow-up ticket to fix any bypassed violations
```

**When to bypass:**
- Production hotfixes that can't wait
- Fixes to generated code that will be regenerated
- Temporary changes that will be reverted immediately

**When NOT to bypass:**
- "I'll fix it later" - Fix it now
- "The rule is wrong" - Discuss with team, add suppression with justification
- "I'm in a hurry" - Plan better next time

### Running Analysis Manually

You can run analysis without committing:

```bash
# Run detekt on entire codebase
./gradlew detekt

# Run ktlint check on entire codebase
./gradlew ktlintCheck

# Run Android Lint on composeApp
./gradlew :composeApp:lintDebug

# Run all three tools
./gradlew detekt ktlintCheck :composeApp:lintDebug

# View HTML reports
open build/reports/detekt/detekt.html
open shared/build/reports/ktlint/ktlint.html
open composeApp/build/reports/lint/lint.html
```

### Auto-Fixing Formatting Issues

ktlint can automatically fix many formatting violations:

```bash
# Auto-fix formatting
./gradlew ktlintFormat

# Then stage and commit
git add -u
git commit -m "[GG-123] Fix formatting"
```

## Baseline Analysis (For Project Setup)

If you're setting up the hooks for the first time on a project with existing code, you'll need to handle baseline violations:

### Step 1: Generate Baseline Report

Run full analysis to identify all existing violations:

```bash
# Run baseline analysis script
./scripts/analyze-baseline.sh

# This generates:
# - build/reports/baseline-violations.txt
# - Console output with summary statistics
```

**Expected Output:**
```text
Running baseline analysis on entire codebase...

Analyzing modules:
  ✓ shared (45 files)
  ✓ composeApp (38 files)

Results:
  Total Violations: 127
  - Errors: 45
  - Warnings: 82
  
  Files Affected: 23

Top Violations:
  1. MagicNumber: 25 occurrences
  2. ComplexMethod: 15 occurrences
  3. LongMethod: 12 occurrences

Report saved to: build/reports/baseline-violations.txt
```

### Step 2: Generate Detekt Baseline File

Create a baseline file to allow immediate hook enforcement:

```bash
# Generate baseline file (ignores existing violations)
./gradlew detektBaseline

# This creates: detekt-baseline.xml
```

### Step 3: Fix Violations Incrementally

```bash
# Track progress
./scripts/track-violations.sh

# Fix violations or add suppressions
# Then update baseline
./gradlew detektBaseline

# When all violations are fixed, delete baseline file
rm detekt-baseline.xml
```

## Suppressing Unavoidable Violations

For violations that can't be fixed (e.g., platform API constraints), use inline suppressions:

```kotlin
// Platform API requires this complex signature
// See: https://example.com/platform-docs
@Suppress("ComplexMethod", "LongParameterList")
fun platformSpecificMethod(
    param1: String,
    param2: Int,
    param3: Boolean,
    // ... more parameters
) {
    // Implementation
}
```

**Suppression Rules:**
1. Always include a comment explaining WHY suppression is needed
2. Be specific - only suppress the exact rules needed
3. Keep suppression scope minimal (function-level, not file-level)
4. Get code review approval for all suppressions
5. Document suppressions in commit messages

## Troubleshooting

### Hook Not Running

**Problem:** Commits succeed without running analysis

**Solution:**
```bash
# Check if hook exists
ls -la .git/hooks/pre-commit

# If missing, reinstall
./scripts/install-hooks.sh

# Verify hook is executable
chmod +x .git/hooks/pre-commit
```

### Hook Fails with "Command Not Found"

**Problem:** `./gradlew: command not found`

**Solution:**
```bash
# Verify Gradle Wrapper exists
ls -la gradlew

# Make Gradle Wrapper executable
chmod +x gradlew

# Verify Java is installed
java -version
```

### Analysis Takes Too Long

**Problem:** Commit hangs for >30 seconds

**Solution:**
```bash
# Check how many files are staged
git diff --cached --name-only | wc -l

# If many files (>30), consider:
# 1. Commit in smaller batches
# 2. Use bypass flag for large refactoring
git commit --no-verify -m "[GG-123] Large refactoring"

# 3. Run analysis in parallel (if configured)
./gradlew detekt ktlintCheck --parallel
```

### False Positives

**Problem:** Hook blocks commit for valid code

**Solution:**
1. **Verify it's truly a false positive**
   - Review the rule documentation
   - Discuss with team
   
2. **Add inline suppression with justification**
   ```kotlin
   @Suppress("RuleName") // Justification here
   fun validFunction() { }
   ```

3. **Or update configuration to disable rule**
   - Edit `detekt.yml`
   - Set `active: false` for the rule
   - Commit configuration change

### Hook Conflicts with Existing Hooks

**Problem:** Already have a pre-commit hook

**Solution:**
```bash
# Backup existing hook
cp .git/hooks/pre-commit .git/hooks/pre-commit.backup

# Merge hooks manually
# Create a combined hook that runs both:
cat .git/hooks/pre-commit.backup > .git/hooks/pre-commit
cat scripts/pre-commit-hook.sh >> .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit
```

## Best Practices

### Development Workflow

1. **Write code with quality in mind**
   - Run manual analysis frequently: `./gradlew detekt ktlintCheck`
   - Fix violations as you go, not at commit time
   - Use IDE inspections (IntelliJ IDEA detects same issues)

2. **Before committing large changes**
   - Run manual analysis first
   - Fix violations before staging
   - Commit in logical, small batches

3. **When violations are found**
   - Don't bypass without good reason
   - Fix immediately or add justified suppression
   - Ask team for help if rule seems wrong

### Team Communication

1. **Onboarding new developers**
   - Include hook installation in onboarding checklist
   - Explain bypass policy clearly
   - Share this quickstart guide

2. **Rule changes**
   - Discuss configuration changes with team
   - Document rationale in commit message
   - Update this guide if workflow changes

3. **Suppression reviews**
   - Review suppressions in code review
   - Question suppressions without clear justification
   - Periodically audit all suppressions

## Commands Reference

```bash
# Installation
./scripts/install-hooks.sh

# Manual Analysis
./gradlew detekt                            # Run detekt only
./gradlew ktlintCheck                       # Run ktlint check only
./gradlew :composeApp:lintDebug            # Run Android Lint only
./gradlew detekt ktlintCheck :composeApp:lintDebug  # Run all three

# Auto-Fix
./gradlew ktlintFormat                      # Fix formatting violations

# Baseline Analysis
./scripts/analyze-baseline.sh               # Full codebase analysis (all tools)
./scripts/track-violations.sh               # Track progress on fixes
./gradlew detektBaseline                    # Generate Detekt baseline file
./gradlew :composeApp:lintBaseline          # Generate Android Lint baseline file

# Reports
open build/reports/detekt/detekt.html
open shared/build/reports/ktlint/ktlint.html
open composeApp/build/reports/lint/lint.html

# Bypass (Emergency Only)
git commit --no-verify -m "Message"
```

## Configuration Files

- `detekt.yml` - Detekt rule configuration (repository root)
- `.editorconfig` - ktlint formatting rules (repository root)
- `composeApp/lint.xml` - Android Lint rule configuration (Android module)
- `detekt-baseline.xml` - Existing Detekt violations to ignore (temporary)
- `composeApp/lint-baseline.xml` - Existing Android Lint violations to ignore (temporary)
- `.git/hooks/pre-commit` - Git hook script (installed by setup)

## Support

If you encounter issues not covered in this guide:

1. Check project README for updates
2. Ask team members who have successfully set up hooks
3. Review git hook logs: `cat .git/hooks/pre-commit`
4. File an issue or discuss in team chat

## Next Steps

After installation:

1. ✅ Verify hook is working with test commit
2. ✅ Run baseline analysis if setting up for first time
3. ✅ Configure IDE to use same rules (optional but recommended)
4. ✅ Read team's coding standards and rule justifications
5. ✅ Share this guide with new team members

## Related Documentation

- [Implementation Plan](./plan.md) - Technical details and architecture
- [Data Model](./data-model.md) - Analysis output structures
- [Research](./research.md) - Tool selection rationale
- [Detekt Documentation](https://detekt.dev/) - Official detekt docs
- [ktlint Documentation](https://pinterest.github.io/ktlint/) - Official ktlint docs
- [Android Lint Documentation](https://developer.android.com/studio/write/lint) - Official Android Lint docs

