#!/bin/bash

# Selenium Grid Startup Script
# Automatically detects system architecture and starts appropriate Selenium Grid

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "🔍 Detecting system architecture..."

ARCH=$(uname -m)
echo "   Architecture: $ARCH"

if [[ "$ARCH" == "arm64" || "$ARCH" == "aarch64" ]]; then
    echo "🍎 ARM architecture detected (Apple Silicon or ARM server)"
    COMPOSE_FILE="docker-compose.selenium-arm.yml"
    GRID_NAME="Seleniarm (ARM)"
elif [[ "$ARCH" == "x86_64" ]]; then
    echo "💻 x86/amd64 architecture detected (Intel/AMD)"
    COMPOSE_FILE="docker-compose.selenium-x86.yml"
    GRID_NAME="Selenium (x86)"
else
    echo "❌ Unknown architecture: $ARCH"
    echo "   Supported: arm64 (Apple Silicon), x86_64 (Intel/AMD)"
    exit 1
fi

echo ""
echo "🚀 Starting $GRID_NAME Grid..."
echo "   Using: $COMPOSE_FILE"
echo ""

docker-compose -f "$COMPOSE_FILE" up -d

echo ""
echo "⏳ Waiting for Grid to be ready..."
sleep 5

# Check if Grid is ready
MAX_RETRIES=30
RETRY_COUNT=0
GRID_READY=false

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if curl -s http://localhost:4444/wd/hub/status > /dev/null 2>&1; then
        GRID_READY=true
        break
    fi
    echo "   Attempt $((RETRY_COUNT + 1))/$MAX_RETRIES..."
    sleep 2
    RETRY_COUNT=$((RETRY_COUNT + 1))
done

echo ""
if [ "$GRID_READY" = true ]; then
    echo "✅ Selenium Grid is ready!"
    echo ""
    echo "📊 Grid Console: http://localhost:4444/ui"
    echo "🔗 Hub URL: http://localhost:4444"
    echo ""
    echo "🧪 Available browsers:"
    if [[ "$ARCH" == "arm64" || "$ARCH" == "aarch64" ]]; then
        echo "   - Chrome (Chromium)"
        echo "   - Firefox"
    else
        echo "   - Chrome"
        echo "   - Firefox"
        echo "   - Edge"
    fi
    echo ""
    echo "🎥 VNC Access (for debugging):"
    echo "   - Chrome:   vnc://localhost:5900"
    echo "   - Firefox:  vnc://localhost:5901"
    if [[ "$ARCH" == "x86_64" ]]; then
        echo "   - Edge:     vnc://localhost:5902"
    fi
    echo ""
    echo "🛑 To stop Grid:"
    echo "   docker-compose -f $COMPOSE_FILE down"
    echo ""
    
    # Show Grid status
    echo "📈 Grid Status:"
    curl -s http://localhost:4444/wd/hub/status | jq '.' || echo "   (jq not installed - raw JSON)"
else
    echo "❌ Selenium Grid failed to start within $((MAX_RETRIES * 2)) seconds"
    echo "   Check logs: docker-compose -f $COMPOSE_FILE logs"
    exit 1
fi

