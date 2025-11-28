#!/bin/bash
set -euo pipefail

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

log_error() {
  echo -e "${RED}✗ $1${NC}"
}

log_success() {
  echo -e "${GREEN}✓ $1${NC}"
}

log_info() {
  echo -e "${YELLOW}→ $1${NC}"
}

COMMIT_HASH=$(git rev-parse --short HEAD)
DATE=$(date +%Y%m%d)
TIME=$(date +%H%M%S)
IMAGE_TAG="${COMMIT_HASH}-${DATE}T${TIME}"

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Building Docker Images${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

log_info "Git commit: $COMMIT_HASH"
log_info "Date and time: $DATE at $TIME"
log_info "Image tag: $IMAGE_TAG"
echo ""

if [ ! -f "../server/Dockerfile" ]; then
  log_error "Backend Dockerfile not found at ../server/Dockerfile"
  exit 1
fi

if [ ! -f "../webApp/Dockerfile" ]; then
  log_error "Frontend Dockerfile not found at ../webApp/Dockerfile"
  exit 1
fi

log_info "Building backend image..."
docker build -t petspot-backend:${IMAGE_TAG} -f ../server/Dockerfile ../server/
docker tag petspot-backend:${IMAGE_TAG} petspot-backend:latest
log_success "Backend image built: petspot-backend:${IMAGE_TAG}"
log_success "Also tagged as: petspot-backend:latest"

log_info "Building frontend image..."
docker build -t petspot-frontend:${IMAGE_TAG} -f ../webApp/Dockerfile ../webApp/
docker tag petspot-frontend:${IMAGE_TAG} petspot-frontend:latest
log_success "Frontend image built: petspot-frontend:${IMAGE_TAG}"
log_success "Also tagged as: petspot-frontend:latest"

echo ""
log_success "Build complete!"
echo ""

echo "Built images:"
docker images | grep petspot | grep -E "(${IMAGE_TAG}|latest)"

echo ""
echo "To start containers:"
echo "  IMAGE_TAG=${IMAGE_TAG} docker compose up -d"
echo ""
echo "Or use latest tag:"
echo "  docker compose up -d"
echo ""

export IMAGE_TAG
