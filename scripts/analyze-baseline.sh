#!/bin/sh
# Baseline Analysis Script for Static Analysis Git Hooks
# Purpose: Run full codebase analysis to generate baseline violation report
# Usage: ./scripts/analyze-baseline.sh
#
# This script analyzes the entire codebase with Detekt, ktlint, and Android Lint
# to identify all existing code quality issues before enabling commit hooks.

set -e

# Get repository root
REPO_ROOT=$(git rev-parse --show-toplevel)
cd "$REPO_ROOT" || exit 1

# Output configuration
REPORT_DIR="$REPO_ROOT/build/reports"
REPORT_FILE="$REPORT_DIR/baseline-violations.txt"
TIMESTAMP=$(date '+%Y-%m-%d %H:%M:%S')

echo "========================================"
echo "Baseline Analysis for Static Analysis"
echo "========================================"
echo "Timestamp: $TIMESTAMP"
echo ""

# Create report directory
mkdir -p "$REPORT_DIR"

# Initialize report file
echo "Static Analysis Baseline Report" > "$REPORT_FILE"
echo "Generated: $TIMESTAMP" >> "$REPORT_FILE"
echo "========================================" >> "$REPORT_FILE"
echo "" >> "$REPORT_FILE"

# Initialize counters
TOTAL_ERRORS=0
TOTAL_WARNINGS=0
TOTAL_INFO=0
DETEKT_EXIT=0
KTLINT_EXIT=0
LINT_EXIT=0

echo "Analyzing modules:"
echo "  - shared (Kotlin Multiplatform)"
echo "  - composeApp (Android)"
echo ""

# Run Detekt analysis
echo "[1/3] Running Detekt analysis..."
if ./gradlew detekt --continue 2>&1; then
    echo "  ✓ Detekt analysis completed"
else
    DETEKT_EXIT=$?
    echo "  ⚠ Detekt found violations"
fi

# Run ktlint analysis
echo "[2/3] Running ktlint analysis..."
if ./gradlew ktlintCheck --continue 2>&1; then
    echo "  ✓ ktlint check completed"
else
    KTLINT_EXIT=$?
    echo "  ⚠ ktlint found violations"
fi

# Run Android Lint analysis
echo "[3/3] Running Android Lint analysis..."
if ./gradlew :composeApp:lintDebug --continue 2>&1; then
    echo "  ✓ Android Lint completed"
else
    LINT_EXIT=$?
    echo "  ⚠ Android Lint found violations"
fi

echo ""
echo "Analysis complete. Processing results..."
echo ""

# Parse Detekt XML output
DETEKT_XML="$REPO_ROOT/build/reports/detekt/detekt.xml"
if [ -f "$DETEKT_XML" ]; then
    echo "=== DETEKT VIOLATIONS ===" >> "$REPORT_FILE"
    # Extract violations from XML (simplified - counts error elements)
    DETEKT_COUNT=$(grep -c '<error' "$DETEKT_XML" 2>/dev/null || echo "0")
    TOTAL_ERRORS=$((TOTAL_ERRORS + DETEKT_COUNT))
    echo "Found $DETEKT_COUNT Detekt violations" >> "$REPORT_FILE"
    echo "" >> "$REPORT_FILE"
fi

# Parse ktlint output (plain text)
# ktlint outputs to stderr, captured in build logs
echo "=== KTLINT VIOLATIONS ===" >> "$REPORT_FILE"
echo "Check build/reports for ktlint violations" >> "$REPORT_FILE"
echo "" >> "$REPORT_FILE"

# Parse Android Lint XML output
LINT_XML="$REPO_ROOT/composeApp/build/reports/lint-results-debug.xml"
if [ -f "$LINT_XML" ]; then
    echo "=== ANDROID LINT VIOLATIONS ===" >> "$REPORT_FILE"
    # Extract violations from XML
    LINT_COUNT=$(grep -c '<issue' "$LINT_XML" 2>/dev/null || echo "0")
    TOTAL_WARNINGS=$((TOTAL_WARNINGS + LINT_COUNT))
    echo "Found $LINT_COUNT Android Lint violations" >> "$REPORT_FILE"
    echo "" >> "$REPORT_FILE"
fi

# Summary statistics
echo "=== SUMMARY ===" >> "$REPORT_FILE"
echo "Total Errors: $TOTAL_ERRORS" >> "$REPORT_FILE"
echo "Total Warnings: $TOTAL_WARNINGS" >> "$REPORT_FILE"
echo "Total Info: $TOTAL_INFO" >> "$REPORT_FILE"
echo "" >> "$REPORT_FILE"

# Display summary to console
echo "========================================"
echo "Results Summary"
echo "========================================"
echo "Total Violations: $((TOTAL_ERRORS + TOTAL_WARNINGS + TOTAL_INFO))"
echo "  - Errors: $TOTAL_ERRORS"
echo "  - Warnings: $TOTAL_WARNINGS"
echo "  - Info: $TOTAL_INFO"
echo ""
echo "Detailed reports available at:"
echo "  - Detekt: build/reports/detekt/detekt.html"
echo "  - ktlint: Check console output above"
echo "  - Android Lint: composeApp/build/reports/lint/lint-results-debug.html"
echo ""
echo "Report saved to: $REPORT_FILE"
echo "========================================"

exit 0

