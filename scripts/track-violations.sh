#!/bin/sh
# Progress Tracking Script for Baseline Violations
# Purpose: Track progress on fixing baseline violations over time
# Usage: ./scripts/track-violations.sh

set -e

# Get repository root
REPO_ROOT=$(git rev-parse --show-toplevel)
cd "$REPO_ROOT" || exit 1

TIMESTAMP=$(date '+%Y-%m-%d %H:%M:%S')

echo "========================================"
echo "Baseline Violation Progress Tracking"
echo "========================================"
echo "Generated: $TIMESTAMP"
echo ""

# Count violations from detekt baseline
DETEKT_BASELINE="$REPO_ROOT/detekt-baseline.xml"
DETEKT_COUNT=0
if [ -f "$DETEKT_BASELINE" ]; then
    DETEKT_COUNT=$(grep -c '<ID>' "$DETEKT_BASELINE" 2>/dev/null || echo "0")
fi

# Count violations from Android Lint baseline
LINT_BASELINE="$REPO_ROOT/composeApp/lint-baseline.xml"
LINT_COUNT=0
if [ -f "$LINT_BASELINE" ]; then
    LINT_COUNT=$(grep -c '<issue' "$LINT_BASELINE" 2>/dev/null || echo "0")
fi

# Display progress
echo "Current Baseline Violations:"
echo "  - Detekt: $DETEKT_COUNT violations"
echo "  - Android Lint: $LINT_COUNT violations"
echo ""

TOTAL=$((DETEKT_COUNT + LINT_COUNT))
echo "Total: $TOTAL violations remaining"
echo ""

if [ "$TOTAL" -eq 0 ]; then
    echo "✓ Excellent! No baseline violations remain."
    echo "  You can now delete the baseline files:"
    echo "  - rm detekt-baseline.xml"
    echo "  - rm composeApp/lint-baseline.xml"
else
    echo "Continue fixing violations and run this script to track progress."
    echo "Run './gradlew detektBaseline' and './gradlew :composeApp:lintBaseline' to update baselines."
fi

echo "========================================"

exit 0

