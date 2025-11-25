#!/bin/sh
# Git Hook Installation Script
# Purpose: Install pre-commit hook for static analysis
# Usage: ./scripts/install-hooks.sh
#
# This script installs the git pre-commit hook that runs static analysis
# on Kotlin files before allowing commits.

set -e

# Get repository root
REPO_ROOT=$(git rev-parse --show-toplevel)
cd "$REPO_ROOT" || exit 1

HOOK_SOURCE="$REPO_ROOT/scripts/pre-commit-hook.sh"
HOOK_DEST="$REPO_ROOT/.git/hooks/pre-commit"

echo "========================================"
echo "Installing Git Pre-Commit Hook"
echo "========================================"
echo ""

# Check if hook source exists
if [ ! -f "$HOOK_SOURCE" ]; then
    echo "✗ Error: Hook source not found at $HOOK_SOURCE"
    exit 1
fi

# Check if hook already exists
if [ -f "$HOOK_DEST" ]; then
    echo "⚠ Warning: Pre-commit hook already exists"
    echo ""
    echo "Existing hook: $HOOK_DEST"
    echo ""
    printf "Do you want to backup the existing hook? (y/n): "
    read -r response
    
    if [ "$response" = "y" ] || [ "$response" = "Y" ]; then
        BACKUP_FILE="$HOOK_DEST.backup.$(date +%Y%m%d_%H%M%S)"
        cp "$HOOK_DEST" "$BACKUP_FILE"
        echo "  ✓ Backed up to: $BACKUP_FILE"
    else
        echo "  Skipping backup"
    fi
    echo ""
fi

# Install the hook
echo "Installing hook..."
cp "$HOOK_SOURCE" "$HOOK_DEST"
chmod +x "$HOOK_DEST"

echo "  ✓ Hook installed at: $HOOK_DEST"
echo ""

# Verify installation
if [ -x "$HOOK_DEST" ]; then
    echo "========================================"
    echo "✓ Installation Successful"
    echo "========================================"
    echo ""
    echo "The git pre-commit hook is now active."
    echo ""
    echo "Usage:"
    echo "  - Normal commits: git commit -m \"Message\""
    echo "  - Hook runs automatically before commit"
    echo "  - Bypass hook (emergency only): git commit --no-verify"
    echo ""
    echo "Tools that will run:"
    echo "  - Detekt (Kotlin code quality)"
    echo "  - ktlint (Kotlin formatting)"
    echo "  - Android Lint (Android-specific checks, when composeApp files change)"
    echo ""
    echo "For more information, see: docs/static-analysis-setup.md"
    echo "========================================"
else
    echo "✗ Error: Hook installation failed (not executable)"
    exit 1
fi

exit 0

