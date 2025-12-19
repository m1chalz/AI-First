#!/usr/bin/env bash
#
# sync-jira-feature.sh
# 
# Creates or updates a Jira Feature ticket based on spec.md content.
# Extracts Story Points from the estimation section and syncs to Jira.
#
# Usage: ./sync-jira-feature.sh [--json] [--dry-run]
#
# Requirements:
# - Atlassian MCP server configured and authenticated
# - spec.md file with Estimation section containing Story Points
#
# Jira Configuration (AI First project):
# - Cloud ID: 2b980644-05e9-43e0-aab6-1bcf1e6cb9de
# - Project Key: KAN
# - Issue Type: Feature (ID: 10003)
# - Story Points Field: customfield_10016

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/common.sh"

# Parse arguments
JSON_MODE=false
DRY_RUN=false
while [[ $# -gt 0 ]]; do
    case "$1" in
        --json) JSON_MODE=true; shift ;;
        --dry-run) DRY_RUN=true; shift ;;
        --help|-h)
            echo "Usage: $0 [--json] [--dry-run]"
            echo ""
            echo "Options:"
            echo "  --json     Output in JSON format"
            echo "  --dry-run  Parse spec.md but don't create/update Jira ticket"
            echo "  --help     Show this help message"
            echo ""
            echo "This script reads the spec.md file from the current feature branch"
            echo "and creates or updates a corresponding Jira Feature ticket."
            exit 0
            ;;
        *) echo "Unknown option: $1" >&2; exit 1 ;;
    esac
done

# Get feature paths
eval "$(get_feature_paths)"

# Validate we're on a feature branch
if ! check_feature_branch "$CURRENT_BRANCH" "$HAS_GIT"; then
    exit 1
fi

# Check spec.md exists
if [[ ! -f "$FEATURE_SPEC" ]]; then
    echo "Error: spec.md not found at $FEATURE_SPEC" >&2
    echo "Create a spec.md first using: specify new '<feature description>'" >&2
    exit 1
fi

# Extract feature title from first heading
extract_title() {
    local spec_file="$1"
    # Get the first H1 heading and extract the title after the colon
    local title=$(grep -m1 "^# " "$spec_file" | sed 's/^# Feature Specification: //' | sed 's/^# //')
    echo "$title"
}

# Extract Story Points from Estimation section
extract_story_points() {
    local spec_file="$1"
    # Look for "**Story Points**: X" in the Initial Estimate section
    local sp=$(grep -E "^\- \*\*Story Points\*\*:" "$spec_file" | head -1 | sed 's/.*: //' | tr -d '[:space:]')
    # Validate it's a Fibonacci number
    if [[ "$sp" =~ ^(1|2|3|5|8|13)$ ]]; then
        echo "$sp"
    else
        echo ""
    fi
}

# Extract description (user stories + requirements)
extract_description() {
    local spec_file="$1"
    # Get content from User Scenarios to Estimation section (or end if no Estimation)
    local content=$(awk '/^## User Scenarios/,/^## Estimation/' "$spec_file" | head -n -1)
    if [[ -z "$content" ]]; then
        # Fallback: get everything between first heading and Estimation
        content=$(awk '/^## /,/^## Estimation/' "$spec_file" | head -n -1)
    fi
    echo "$content"
}

# Extract existing Jira ticket from spec.md
extract_jira_ticket() {
    local spec_file="$1"
    # Look for "**Jira Ticket**: [KAN-XXX](url)" or "**Jira Ticket**: KAN-XXX"
    local ticket=$(grep -E "^\*\*Jira Ticket\*\*:" "$spec_file" | sed 's/.*\[//' | sed 's/\].*//' | grep -oE "KAN-[0-9]+" || echo "")
    echo "$ticket"
}

# Main logic
FEATURE_TITLE=$(extract_title "$FEATURE_SPEC")
STORY_POINTS=$(extract_story_points "$FEATURE_SPEC")
EXISTING_TICKET=$(extract_jira_ticket "$FEATURE_SPEC")

if [[ -z "$FEATURE_TITLE" ]]; then
    echo "Error: Could not extract feature title from spec.md" >&2
    echo "Ensure spec.md has a heading like: # Feature Specification: Your Feature Name" >&2
    exit 1
fi

if [[ -z "$STORY_POINTS" ]]; then
    echo "Warning: Could not extract Story Points from spec.md" >&2
    echo "Ensure spec.md has: - **Story Points**: X (where X is 1, 2, 3, 5, 8, or 13)" >&2
    STORY_POINTS="0"  # Will not set story points if 0
fi

# Prepare output
if $JSON_MODE; then
    cat <<EOF
{
  "feature_branch": "$CURRENT_BRANCH",
  "feature_title": "$FEATURE_TITLE",
  "story_points": $STORY_POINTS,
  "existing_ticket": "$EXISTING_TICKET",
  "dry_run": $DRY_RUN
}
EOF
else
    echo "Feature Branch: $CURRENT_BRANCH"
    echo "Feature Title: $FEATURE_TITLE"
    echo "Story Points: $STORY_POINTS"
    echo "Existing Ticket: ${EXISTING_TICKET:-none}"
fi

if $DRY_RUN; then
    echo ""
    echo "[DRY RUN] Would create/update Jira ticket with above data"
    echo "[DRY RUN] No changes made to Jira or spec.md"
    exit 0
fi

# Output JSON for MCP/agent consumption
# The actual Jira API call should be made by the agent using MCP tools
# This script prepares the data
echo ""
echo "=== JIRA_SYNC_DATA ==="
cat <<JIRA_DATA
{
  "action": "${EXISTING_TICKET:+update}${EXISTING_TICKET:-create}",
  "cloud_id": "2b980644-05e9-43e0-aab6-1bcf1e6cb9de",
  "project_key": "KAN",
  "issue_type": "Feature",
  "issue_type_id": "10003",
  "existing_ticket": "$EXISTING_TICKET",
  "fields": {
    "summary": "$FEATURE_TITLE",
    "story_points": $STORY_POINTS,
    "story_points_field": "customfield_10016",
    "labels": ["$CURRENT_BRANCH"]
  },
  "spec_file": "$FEATURE_SPEC"
}
JIRA_DATA

