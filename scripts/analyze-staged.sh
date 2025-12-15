#!/bin/sh
# Analyze Staged Files Script for Static Analysis Git Hooks
# Purpose: Run static analysis only on files staged for commit
# Usage: Called by pre-commit hook, or run manually: ./scripts/analyze-staged.sh
#
# This script checks staged files:
# - Kotlin: Detekt, ktlint, Android Lint (for composeApp files)
# - Java (e2e-tests/java/): Checkstyle, SpotBugs
# - Gherkin (*.feature): gherkin-lint

# Exit on error, but handle errors explicitly where needed
set -e

# Get repository root
REPO_ROOT=$(git rev-parse --show-toplevel)
cd "$REPO_ROOT" || exit 1

# Initialize global error tracking
HAS_ERRORS=false

# ============================================
# KOTLIN FILES ANALYSIS
# ============================================

echo "========================================"
echo "Checking staged Kotlin files..."
echo "========================================"
echo ""

# Get staged Kotlin files
STAGED_KOTLIN_FILES=$(git diff --cached --name-only --diff-filter=ACM | grep '\.kt$' || true)

if [ -z "$STAGED_KOTLIN_FILES" ]; then
    echo "No Kotlin files staged, skipping Kotlin analysis"
    echo ""
else

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

    # Run Detekt analysis
    echo "[1/3] Running Detekt analysis..."
    set +e  # Temporarily allow errors
    ./gradlew detekt --quiet
    DETEKT_EXIT=$?
    set -e
    
    if [ $DETEKT_EXIT -eq 0 ]; then
        echo "  ✓ Detekt passed"
    else
        HAS_ERRORS=true
        echo "  ✗ Detekt found violations"
        echo ""
        echo "  View detailed report: build/reports/detekt/detekt.html"
    fi

    # Run ktlint analysis
    echo "[2/3] Running ktlint check..."
    set +e
    ./gradlew ktlintCheck --quiet
    KTLINT_EXIT=$?
    set -e
    
    if [ $KTLINT_EXIT -eq 0 ]; then
        echo "  ✓ ktlint passed"
    else
        HAS_ERRORS=true
        echo "  ✗ ktlint found violations"
        echo ""
        echo "  Run './gradlew ktlintFormat' to auto-fix formatting issues"
    fi

    # Run Android Lint (only if composeApp files changed)
    if [ "$HAS_ANDROID_FILES" = true ]; then
        echo "[3/3] Running Android Lint..."
        set +e
        ./gradlew :composeApp:lintDebug --quiet
        LINT_EXIT=$?
        set -e
        
        if [ $LINT_EXIT -eq 0 ]; then
            echo "  ✓ Android Lint passed"
        else
            HAS_ERRORS=true
            echo "  ✗ Android Lint found violations"
            echo ""
            echo "  View detailed report: composeApp/build/reports/lint/lint-results-debug.html"
        fi
    else
        echo "[3/3] Skipping Android Lint (no composeApp files changed)"
    fi

    echo ""
fi

# ============================================
# JAVA E2E FILES ANALYSIS
# ============================================

echo "========================================"
echo "Checking staged Java files (e2e-tests)..."
echo "========================================"
echo ""

# Get staged Java files ONLY from e2e-tests/java/
STAGED_E2E_JAVA_FILES=$(git diff --cached --name-only --diff-filter=ACM | grep '\.java$' | grep '^e2e-tests/java/' || true)

if [ -z "$STAGED_E2E_JAVA_FILES" ]; then
    echo "No Java files staged from e2e-tests/java/, skipping Java analysis"
    echo ""
else
    # Count staged files
    JAVA_FILE_COUNT=$(echo "$STAGED_E2E_JAVA_FILES" | wc -l | tr -d ' ')
    echo "Analyzing $JAVA_FILE_COUNT staged Java file(s) from e2e-tests/java/:"
    echo "$STAGED_E2E_JAVA_FILES" | sed 's/^/  - /'
    echo ""

    # Performance warning for large changesets
    if [ "$JAVA_FILE_COUNT" -gt 30 ]; then
        echo "⚠ Warning: Large changeset ($JAVA_FILE_COUNT files)"
        echo "  Analysis may take longer. You can bypass with: git commit --no-verify"
        echo ""
    fi

    # Change to e2e-tests/java directory for Maven commands
    cd "$REPO_ROOT/e2e-tests/java" || exit 1

    # Compile test sources first (required for SpotBugs)
    echo "[1/3] Compiling test sources..."
    set +e
    mvn test-compile -q -DskipTests 2>&1 | grep -v "^\[INFO\]" || true
    COMPILE_EXIT=$?
    set -e
    
    if [ $COMPILE_EXIT -ne 0 ]; then
        HAS_ERRORS=true
        echo "  ✗ Compilation failed"
        echo ""
        echo "  Run 'cd e2e-tests/java && mvn test-compile' for details"
        echo ""
        # Return to repo root
        cd "$REPO_ROOT" || exit 1
    else
        echo "  ✓ Compilation successful"
        
        # Run Checkstyle and SpotBugs in single Maven call (optimization)
        echo "[2/3] Running Checkstyle and SpotBugs..."
        set +e
        mvn checkstyle:check spotbugs:check -q 2>&1 | grep -E "^\[ERROR\]|^\[WARNING\]" || true
        CHECKS_EXIT=$?
        set -e
        
        if [ $CHECKS_EXIT -eq 0 ]; then
            echo "  ✓ Checkstyle passed"
            echo "  ✓ SpotBugs passed"
        else
            HAS_ERRORS=true
            echo "  ✗ Code quality checks found violations"
            echo ""
            echo "  Checkstyle report: e2e-tests/java/target/checkstyle-result.xml"
            echo "  SpotBugs report: e2e-tests/java/target/spotbugsXml.xml"
            echo "  Run 'cd e2e-tests/java && mvn checkstyle:check spotbugs:check' for details"
        fi
        
        echo ""
        # Return to repo root
        cd "$REPO_ROOT" || exit 1
    fi
fi

# ============================================
# GHERKIN FEATURE FILES ANALYSIS
# ============================================

echo "========================================"
echo "Checking staged Gherkin files (.feature)..."
echo "========================================"
echo ""

# Get staged feature files
STAGED_FEATURE_FILES=$(git diff --cached --name-only --diff-filter=ACM | grep '\.feature$' || true)

if [ -z "$STAGED_FEATURE_FILES" ]; then
    echo "No Gherkin feature files staged, skipping Gherkin analysis"
    echo ""
else
    # Count staged files
    FEATURE_FILE_COUNT=$(echo "$STAGED_FEATURE_FILES" | wc -l | tr -d ' ')
    echo "Analyzing $FEATURE_FILE_COUNT staged feature file(s):"
    echo "$STAGED_FEATURE_FILES" | sed 's/^/  - /'
    echo ""

    # Check if gherkin-lint is available
    if ! command -v npx >/dev/null 2>&1; then
        echo "  ⚠ Warning: npx not found, skipping gherkin-lint"
        echo "  Install Node.js to enable Gherkin linting"
        echo ""
    elif [ ! -f "$REPO_ROOT/e2e-tests/package.json" ]; then
        echo "  ⚠ Warning: e2e-tests/package.json not found, skipping gherkin-lint"
        echo "  Run 'cd e2e-tests && npm install' to enable Gherkin linting"
        echo ""
    else
        # Change to e2e-tests directory
        cd "$REPO_ROOT/e2e-tests" || exit 1
        
        # Ensure gherkin-lint is installed
        if [ ! -d "node_modules/gherkin-lint" ]; then
            echo "[1/2] Installing gherkin-lint..."
            set +e
            npm install --silent 2>&1 | grep -v "^npm" || true
            INSTALL_EXIT=$?
            set -e
            
            if [ $INSTALL_EXIT -ne 0 ]; then
                echo "  ⚠ Warning: Failed to install gherkin-lint"
                echo ""
                cd "$REPO_ROOT" || exit 1
            else
                echo "  ✓ Installation successful"
            fi
        fi
        
        # Run gherkin-lint on staged files
        if [ -d "node_modules/gherkin-lint" ]; then
            echo "[2/2] Running gherkin-lint..."
            set +e
            
            # Create temporary file list for gherkin-lint
            TEMP_FILE_LIST=$(mktemp)
            echo "$STAGED_FEATURE_FILES" | while read -r file; do
                # Convert repo-relative path to e2e-tests-relative path
                if echo "$file" | grep -q "^e2e-tests/"; then
                    echo "$file" | sed 's|^e2e-tests/||'
                fi
            done > "$TEMP_FILE_LIST"
            
            # Run gherkin-lint on files
            if [ -s "$TEMP_FILE_LIST" ]; then
                cat "$TEMP_FILE_LIST" | xargs -I {} npx gherkin-lint {} 2>&1
                GHERKIN_EXIT=$?
            else
                GHERKIN_EXIT=0
            fi
            
            rm -f "$TEMP_FILE_LIST"
            set -e
            
            if [ $GHERKIN_EXIT -eq 0 ]; then
                echo "  ✓ Gherkin-lint passed"
            else
                HAS_ERRORS=true
                echo "  ✗ Gherkin-lint found violations"
                echo ""
                echo "  Run 'cd e2e-tests && npm run gherkin-lint' to see all violations"
                echo "  Some issues may be auto-fixable with: cd e2e-tests && npm run gherkin-lint:fix"
            fi
        fi
        
        echo ""
        # Return to repo root
        cd "$REPO_ROOT" || exit 1
    fi
fi

# ============================================
# FINAL DECISION
# ============================================

echo "========================================"
if [ "$HAS_ERRORS" = true ]; then
    echo "✗ Static analysis FAILED"
    echo "========================================"
    echo "Fix the errors above and try committing again."
    echo ""
    echo "To bypass this check (emergency only):"
    echo "  git commit --no-verify -m \"Your message\""
    echo "========================================"
    exit 1
else
    echo "✓ Static analysis PASSED"
    echo "========================================"
    exit 0
fi

