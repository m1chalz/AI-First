#!/bin/sh
# Pre-commit Git Hook for Static Analysis
# This hook is installed by running: ./scripts/install-hooks.sh
#
# It automatically runs static analysis (Detekt, ktlint, Android Lint) on staged
# Kotlin files before allowing a commit.
#
# To bypass this hook in emergencies: git commit --no-verify

# Get repository root
REPO_ROOT=$(git rev-parse --show-toplevel)

# Run the staged file analysis script
"$REPO_ROOT/scripts/analyze-staged.sh"

# Pass through the exit code
exit $?

