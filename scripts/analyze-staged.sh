#!/bin/sh
# Analyze Staged Files Script for Static Analysis Git Hooks
# Purpose: Run static analysis only on files staged for commit
# Usage: Called by pre-commit hook, or run manually: ./scripts/analyze-staged.sh
#
# This script checks only the Kotlin files that are staged for commit,
# running Detekt, ktlint, and Android Lint as needed.

set -e

# Get repository root
REPO_ROOT=$(git rev-parse --show-toplevel)
cd "$REPO_ROOT" || exit 1

echo "Running static analysis on staged Kotlin files..."
echo ""

# Get staged Kotlin files
STAGED_KOTLIN_FILES=$(git diff --cached --name-only --diff-filter=ACM | grep '\.kt$' || true)

if [ -z "$STAGED_KOTLIN_FILES" ]; then
    echo "No Kotlin files staged, skipping analysis"
    exit 0
fi

# Count staged files
FILE_COUNT=$(echo "$STAGED_KOTLIN_FILES" | wc -l | tr -d ' ')
echo "Analyzing $FILE_COUNT staged Kotlin file(s):"
echo "$STAGED_KOTLIN_FILES" | sed 's/^/  - /'
echo ""

# Check if any composeApp files are staged
ANDROID_FILES=$(echo "$STAGED_KOTLIN_FILES" | grep '^composeApp/' || true)
HAS_ANDROID_FILES=false
if [ -n "$ANDROID_FILES" ]; then
    HAS_ANDROID_FILES=true
fi

# Performance warning for large changesets
if [ "$FILE_COUNT" -gt 30 ]; then
    echo "⚠ Warning: Large changeset ($FILE_COUNT files)"
    echo "  Analysis may take longer. You can bypass with: git commit --no-verify"
    echo ""
fi

# Initialize exit codes
DETEKT_EXIT=0
KTLINT_EXIT=0
LINT_EXIT=0
HAS_ERRORS=false
HAS_WARNINGS=false

# Run Detekt analysis
echo "[1/3] Running Detekt analysis..."
if ./gradlew detekt --quiet; then
    echo "  ✓ Detekt passed"
else
    DETEKT_EXIT=$?
    HAS_ERRORS=true
    echo "  ✗ Detekt found violations"
    echo ""
    echo "  View detailed report: build/reports/detekt/detekt.html"
fi

# Run ktlint analysis
echo "[2/3] Running ktlint check..."
if ./gradlew ktlintCheck --quiet; then
    echo "  ✓ ktlint passed"
else
    KTLINT_EXIT=$?
    HAS_ERRORS=true
    echo "  ✗ ktlint found violations"
    echo ""
    echo "  Run './gradlew ktlintFormat' to auto-fix formatting issues"
fi

# Run Android Lint (only if composeApp files changed)
if [ "$HAS_ANDROID_FILES" = true ]; then
    echo "[3/3] Running Android Lint..."
    if ./gradlew :composeApp:lintDebug --quiet; then
        echo "  ✓ Android Lint passed"
    else
        LINT_EXIT=$?
        HAS_ERRORS=true
        echo "  ✗ Android Lint found violations"
        echo ""
        echo "  View detailed report: composeApp/build/reports/lint/lint-results-debug.html"
    fi
else
    echo "[3/3] Skipping Android Lint (no composeApp files changed)"
fi

echo ""

# Decide outcome
if [ "$HAS_ERRORS" = true ]; then
    echo "========================================"
    echo "✗ Static analysis FAILED"
    echo "========================================"
    echo "Fix the errors above and try committing again."
    echo ""
    echo "To bypass this check (emergency only):"
    echo "  git commit --no-verify -m \"Your message\""
    echo "========================================"
    exit 1
elif [ "$HAS_WARNINGS" = true ]; then
    echo "========================================"
    echo "⚠ Static analysis completed with warnings"
    echo "========================================"
    echo "Commit allowed, but please review the warnings."
    echo "========================================"
    exit 0
else
    echo "========================================"
    echo "✓ Static analysis PASSED"
    echo "========================================"
    exit 0
fi

