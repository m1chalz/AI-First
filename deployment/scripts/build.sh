#!/bin/bash
set -euo pipefail

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

COMMIT_HASH=$(git rev-parse --short HEAD)
TIMESTAMP=$(date +%Y%m%d-%H%M%S)
IMAGE_TAG="${COMMIT_HASH}-${TIMESTAMP}"

echo -e "${GREEN}Building Docker images with tag: ${IMAGE_TAG}${NC}"

echo -e "${YELLOW}Building backend image...${NC}"
docker build -t petspot-backend:${IMAGE_TAG} -f ../server/Dockerfile ../server/
docker tag petspot-backend:${IMAGE_TAG} petspot-backend:latest
echo -e "${GREEN}✓ Backend image built: petspot-backend:${IMAGE_TAG}${NC}"

echo -e "${YELLOW}Building frontend image...${NC}"
docker build -t petspot-frontend:${IMAGE_TAG} -f ../webApp/Dockerfile ../webApp/
docker tag petspot-frontend:${IMAGE_TAG} petspot-frontend:latest
echo -e "${GREEN}✓ Frontend image built: petspot-frontend:${IMAGE_TAG}${NC}"

export IMAGE_TAG

echo ""
echo -e "${GREEN}✓ Build complete!${NC}"
echo -e "Images tagged with: ${IMAGE_TAG}"
echo -e "Also tagged as: latest"

