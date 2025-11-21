# Research: Static Analysis Git Hooks

**Date**: 2025-11-19  
**Feature**: Static Analysis Git Hooks  
**Plan**: [plan.md](./plan.md)

## Overview

This document captures research findings for implementing git pre-commit hooks with static analysis for Kotlin code in the shared module and Android platform. All "NEEDS CLARIFICATION" items from Technical Context have been resolved.

## 1. Static Analysis Tool Selection for Kotlin

### Decision

**Use Detekt, ktlint, and Android Lint in combination**

### Rationale

- **Detekt**: Provides comprehensive static analysis with 300+ built-in rules covering code smells, complexity, potential bugs, naming conventions, and performance issues
- **ktlint**: Focuses specifically on code formatting and style consistency based on official Kotlin coding conventions
- **Android Lint**: Provides Android-specific checks that Detekt/ktlint cannot detect (API usage, resource validation, security issues, Jetpack Compose issues)
- **Complementary Coverage**:
  - Detekt → Kotlin code quality (shared + composeApp modules)
  - ktlint → Kotlin formatting (shared + composeApp modules)
  - Android Lint → Android-specific issues (composeApp only)
- **Kotlin Multiplatform Support**: Detekt and ktlint fully support KMP; Android Lint targets Android module specifically
- **Gradle Integration**: All three have mature Gradle integration (Android Lint built into Android Gradle Plugin)
- **Community Adoption**: Industry standard in Kotlin/Android ecosystem

### Alternatives Considered

- **Detekt + ktlint Only (without Android Lint)**: Rejected - Would miss Android-specific issues like API compatibility, resource problems, security vulnerabilities, and Compose-specific bugs
- **IntelliJ IDEA Inspections**: Rejected - Not automatable in git hooks, requires IDE
- **Custom Script Analysis**: Rejected - Would require significant maintenance, reinventing existing tools

### Configuration Approach

- **Detekt**: Use `detekt.yml` at repository root with custom rule severity tuning
- **ktlint**: Use `.editorconfig` at repository root for formatting rules
- **Android Lint**: Use `composeApp/lint.xml` for Android-specific rule configuration
- **Gradle Tasks**: `./gradlew detekt`, `./gradlew ktlintCheck`, and `./gradlew :composeApp:lintDebug` for manual runs
- **Hook Integration**: Call all three tools in sequence in pre-commit hook (Android Lint only when composeApp files change)

## 2. Gradle Plugin Integration

### Decision

**Use official Gradle plugins with custom configuration**

- Detekt: `io.gitlab.arturbosch.detekt` version 1.23.3+
- ktlint: `org.jlleitschuh.gradle.ktlint` version 11.6.1+

### Rationale

- **Official Plugins**: Both are official, well-maintained Gradle plugins
- **Incremental Build Support**: Both support Gradle's incremental build system for performance
- **Configuration DSL**: Both provide Kotlin DSL for type-safe Gradle configuration
- **Multi-Module Support**: Both work correctly with Gradle multi-module projects (shared + composeApp)
- **Reporting**: Both generate HTML and XML reports for violation tracking
- **CI/CD Ready**: While CI/CD is out of scope now, plugins are already configured for future use

### Integration Pattern

```kotlin
// Root build.gradle.kts
plugins {
    id("io.gitlab.arturbosch.detekt") version "1.23.3" apply false
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1" apply false
}

// shared/build.gradle.kts and composeApp/build.gradle.kts
plugins {
    id("io.gitlab.arturbosch.detekt")
    id("org.jlleitschuh.gradle.ktlint")
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom(files("$rootDir/detekt.yml"))
    baseline = file("detekt-baseline.xml") // For migration
}

ktlint {
    android.set(true) // For composeApp module
    version.set("1.0.1") // ktlint engine version
}
```

### Alternatives Considered

- **Direct Tool CLI Calls**: Rejected - Would bypass Gradle's caching and incremental build optimizations
- **Custom Gradle Tasks**: Rejected - Reinventing functionality already in official plugins
- **Pre-built JAR Execution**: Rejected - More complex, loses Gradle integration benefits

## 3. Cross-Platform Git Hook Compatibility

### Decision

**Use POSIX-compliant shell scripts with explicit cross-platform testing**

### Rationale

- **Git Bash on Windows**: Windows developers use Git Bash, which provides POSIX shell environment
- **Portable Shell Constructs**: Stick to POSIX sh syntax (avoid bash-specific features like `[[`, `**`)
- **Path Normalization**: Use Git's built-in path handling (`git diff --name-only`) instead of filesystem paths
- **Line Endings**: Configure hooks with LF line endings, use `.gitattributes` to enforce
- **Gradle Wrapper**: Use `./gradlew` which has both Unix and Windows variants

### Best Practices Applied

1. **Shebang**: Use `#!/bin/sh` (not `#!/bin/bash`) for maximum compatibility
2. **Path Handling**: Use relative paths from repository root, avoid absolute paths
3. **Line Endings**: Add `.gitattributes` entry: `*.sh text eol=lf`
4. **Error Handling**: Explicit exit codes, clear error messages
5. **Gradle Invocation**: Detect OS and use appropriate Gradle wrapper (./gradlew vs gradlew.bat)

### Hook Script Pattern

```bash
#!/bin/sh
# pre-commit hook for static analysis

# Get repository root
REPO_ROOT=$(git rev-parse --show-toplevel)
cd "$REPO_ROOT" || exit 1

# Get staged Kotlin files
STAGED_KOTLIN_FILES=$(git diff --cached --name-only --diff-filter=ACM | grep '\.kt$' || true)

if [ -z "$STAGED_KOTLIN_FILES" ]; then
    echo "No Kotlin files staged, skipping analysis"
    exit 0
fi

echo "Running static analysis on staged Kotlin files..."

# Run detekt on staged files
./gradlew detekt --quiet || exit 1

# Run ktlint on staged files
./gradlew ktlintCheck --quiet || exit 1

echo "Static analysis passed!"
exit 0
```

### Alternatives Considered

- **Node.js-based Hooks (Husky)**: Rejected - Adds Node.js dependency to Kotlin/Android project unnecessarily
- **Python-based Hooks**: Rejected - Adds Python dependency, most developers already have Git/Bash
- **Platform-Specific Scripts**: Rejected - Would require maintaining separate scripts for each OS

## 4. Baseline Violation Handling Strategy

### Decision

**Use Detekt's built-in baseline feature combined with inline suppressions**

### Rationale

- **Detekt Baseline File**: Detekt supports `baseline.xml` to ignore existing violations during migration
- **Inline Suppressions**: For violations that can't be fixed, use `@Suppress("RuleName")` with comments
- **Two-Phase Approach**:
  1. Phase 1: Generate baseline to allow immediate hook enforcement
  2. Phase 2: Incrementally fix violations, update baseline, eventually remove baseline file
- **Suppression Documentation**: Require comment above `@Suppress` explaining why it's needed
- **Baseline Tracking**: Use `scripts/track-violations.sh` to monitor baseline size over time

### Baseline Generation

```bash
# Generate initial baseline
./gradlew detektBaseline

# This creates detekt-baseline.xml with all current violations
# Hook will not block commits for violations in baseline
```

### Suppression Format

```kotlin
// Necessary due to platform API constraint requiring this signature
@Suppress("LongParameterList", "ComplexMethod")
fun platformSpecificMethod(...) { }
```

### Migration Strategy

1. **Week 1**: Generate baseline, enable hooks with baseline
2. **Week 2-4**: Fix easy violations (formatting, unused imports)
3. **Week 5-8**: Fix medium violations (complexity, naming)
4. **Week 9+**: Suppress remaining violations with justification, remove baseline file

### Alternatives Considered

- **No Baseline (Fix Everything First)**: Rejected - Would delay rollout by weeks/months
- **Permanent Baseline**: Rejected - Would allow technical debt to accumulate
- **Configuration-Only Exclusions**: Rejected - Less visible than inline suppressions, harder to audit

## 5. Performance Optimization for Large Changesets

### Decision

**Rely on Gradle's incremental build and file filtering in git hook**

### Rationale

- **Git Diff Filtering**: Hook only analyzes files in staging area (`git diff --cached --name-only`)
- **Gradle Incremental Build**: Detekt and ktlint plugins support incremental analysis (only changed files)
- **Gradle Build Cache**: Enable Gradle build cache for cross-session speedup
- **No Parallel Execution Needed**: Sequential detekt → ktlint is fast enough with incremental builds
- **Typical Commit Size**: Most commits change 2-10 files, incremental analysis is <5 seconds

### Performance Targets

- **Small commits (1-5 files)**: < 3 seconds
- **Medium commits (6-15 files)**: < 10 seconds
- **Large commits (16-30 files)**: < 20 seconds
- **Fallback**: Developers can bypass with `--no-verify` if analysis is taking too long

### Gradle Configuration for Performance

```kotlin
// Enable build cache
org.gradle.caching=true

// Enable configuration cache
org.gradle.configuration-cache=true

// Parallel execution (for full builds)
org.gradle.parallel=true
```

### Hook Optimization

```bash
# Count staged Kotlin files
FILE_COUNT=$(echo "$STAGED_KOTLIN_FILES" | wc -l | tr -d ' ')

if [ "$FILE_COUNT" -gt 30 ]; then
    echo "Warning: Large changeset ($FILE_COUNT files), analysis may take longer"
    echo "You can bypass with: git commit --no-verify"
fi
```

### Alternatives Considered

- **Parallel Tool Execution**: Rejected - Sequential is simpler, both tools are already fast
- **Sampling (Only Check Some Files)**: Rejected - Would miss violations in unchecked files
- **Time-Based Timeout**: Rejected - Better to let analysis complete, provide bypass option

## 6. Additional Research Findings

### Rule Configuration Philosophy

**Decision**: Start with strict default rules, loosen based on team feedback

- **Initial Configuration**: Enable all Detekt rules at default severity
- **Team Iteration**: Collect feedback for first 2 weeks, adjust rules causing friction
- **False Positive Policy**: If rule triggers false positives >10%, disable or suppress
- **Formatting Rules**: Follow official Kotlin coding conventions (ktlint default)

### Suppression Review Process

**Decision**: Suppressions require code review justification

- **Suppression Comment**: Required above every `@Suppress` explaining why needed
- **Code Review**: Reviewers must validate suppression justification
- **Periodic Audit**: Monthly review of all suppressions to identify unnecessary ones
- **Metrics**: Track suppression count, aim to reduce over time

### Hook Installation Approach

**Decision**: Manual installation script with documentation

From clarification session: Manual installation over auto-install on build

Benefits:
- Developers explicitly opt-in, understand what they're installing
- No build performance impact for developers not ready to enable
- Clear documentation makes troubleshooting easier
- Team lead can coordinate rollout timing

Installation Script Pattern:
```bash
#!/bin/bash
# scripts/install-hooks.sh

echo "Installing git pre-commit hook for static analysis..."

# Check if hook already exists
if [ -f .git/hooks/pre-commit ]; then
    echo "Warning: pre-commit hook already exists"
    echo "Backup existing hook? (y/n)"
    read -r response
    if [ "$response" = "y" ]; then
        cp .git/hooks/pre-commit .git/hooks/pre-commit.backup
    fi
fi

# Copy hook script
cp scripts/pre-commit-hook.sh .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit

echo "✓ Git hook installed successfully"
echo "  To bypass hook: git commit --no-verify"
```

## Summary of Decisions

| Research Area | Decision | Key Benefit |
|--------------|----------|-------------|
| Static Analysis Tools | Detekt + ktlint + Android Lint | Comprehensive coverage (Kotlin quality + formatting + Android-specific) |
| Gradle Integration | Official Gradle plugins | Incremental builds, proper caching |
| Cross-Platform Compatibility | POSIX sh scripts + Git Bash | Works on macOS, Linux, Windows |
| Baseline Handling | Detekt baseline + inline suppressions (Android Lint baseline too) | Enable immediate rollout, track progress |
| Performance | Gradle incremental + git diff filtering | Fast analysis (~6-15s typical commits with Android Lint) |
| Installation | Manual script with documentation | Explicit opt-in, clear process |

## References

- [Detekt Documentation](https://detekt.dev/)
- [ktlint Documentation](https://pinterest.github.io/ktlint/)
- [Android Lint Documentation](https://developer.android.com/studio/write/lint)
- [Detekt Gradle Plugin](https://github.com/detekt/detekt)
- [ktlint Gradle Plugin](https://github.com/JLLeitschuh/ktlint-gradle)
- [Git Hooks Documentation](https://git-scm.com/book/en/v2/Customizing-Git-Git-Hooks)
- [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- [Android Lint Checks Reference](https://googlesamples.github.io/android-custom-lint-rules/checks/index.md.html)

## Next Steps

Proceed to Phase 1 (Design & Contracts):
1. Create data-model.md defining analysis result structures
2. Create contracts/ directory with detekt.yml and .editorconfig
3. Create quickstart.md with installation and usage instructions

