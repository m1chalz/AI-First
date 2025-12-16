#!/bin/bash

# Selenium Grid Shutdown Script
# Stops both ARM and x86 Selenium Grid containers

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "🛑 Stopping Selenium Grid..."
echo ""

# Try to stop both configurations (in case user switched architectures)
for COMPOSE_FILE in docker-compose.selenium-arm.yml docker-compose.selenium-x86.yml; do
    if [ -f "$COMPOSE_FILE" ]; then
        echo "   Checking $COMPOSE_FILE..."
        
        # Check if any containers from this compose file are running
        RUNNING=$(docker-compose -f "$COMPOSE_FILE" ps -q 2>/dev/null | wc -l | tr -d ' ')
        
        if [ "$RUNNING" -gt 0 ]; then
            echo "   Found running containers, stopping..."
            docker-compose -f "$COMPOSE_FILE" down
            echo "   ✅ Stopped containers from $COMPOSE_FILE"
        else
            echo "   No running containers from $COMPOSE_FILE"
        fi
    fi
done

echo ""
echo "✅ Selenium Grid stopped successfully"
echo ""

# Show remaining Selenium/Seleniarm containers (if any)
REMAINING=$(docker ps --filter "name=selenium" --filter "name=seleniarm" --format "{{.Names}}" 2>/dev/null || true)
if [ -n "$REMAINING" ]; then
    echo "⚠️  Warning: Some Selenium containers are still running:"
    echo "$REMAINING"
    echo ""
    echo "To force remove them:"
    echo "   docker ps --filter 'name=selenium' --filter 'name=seleniarm' -q | xargs docker rm -f"
fi

