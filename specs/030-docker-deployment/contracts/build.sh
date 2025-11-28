#!/bin/bash
# Build Script Template
# Location: /deployment/scripts/build.sh
# Purpose: Build Docker images with commit hash + timestamp tags

set -euo pipefail

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Get Git commit hash (short)
COMMIT_HASH=$(git rev-parse --short HEAD)

# Get timestamp
TIMESTAMP=$(date +%Y%m%d-%H%M%S)

# Generate image tag: (commit-hash)-(timestamp)
IMAGE_TAG="${COMMIT_HASH}-${TIMESTAMP}"

echo -e "${GREEN}Building Docker images with tag: ${IMAGE_TAG}${NC}"

# Build backend image
echo -e "${YELLOW}Building backend image...${NC}"
docker build -t petspot-backend:${IMAGE_TAG} -f ../server/Dockerfile ../server/
docker tag petspot-backend:${IMAGE_TAG} petspot-backend:latest

echo -e "${GREEN}✓ Backend image built: petspot-backend:${IMAGE_TAG}${NC}"

# Build frontend image
echo -e "${YELLOW}Building frontend image...${NC}"
docker build -t petspot-frontend:${IMAGE_TAG} -f ../webApp/Dockerfile ../webApp/
docker tag petspot-frontend:${IMAGE_TAG} petspot-frontend:latest

echo -e "${GREEN}✓ Frontend image built: petspot-frontend:${IMAGE_TAG}${NC}"

# Export IMAGE_TAG for docker-compose
export IMAGE_TAG

echo ""
echo -e "${GREEN}✓ Build complete!${NC}"
echo -e "Images tagged with: ${IMAGE_TAG}"
echo -e "Also tagged as: latest"
echo ""
echo "To start containers with these images:"
echo "  IMAGE_TAG=${IMAGE_TAG} docker compose -f docker-compose.yml up -d"
echo ""
echo "Or use the latest tag:"
echo "  docker compose -f docker-compose.yml up -d"

