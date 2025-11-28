#!/bin/bash
set -euo pipefail

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEPLOYMENT_DIR="$(dirname "$SCRIPT_DIR")"
REPO_ROOT="$(dirname "$DEPLOYMENT_DIR")"

log_success() {
  echo -e "${GREEN}✓ $1${NC}"
}

log_error() {
  echo -e "${RED}✗ $1${NC}"
}

log_warning() {
  echo -e "${YELLOW}⚠ $1${NC}"
}

log_info() {
  echo -e "${YELLOW}→ $1${NC}"
}

check_prerequisites() {
  log_info "Checking prerequisites..."
  
  if ! command -v docker &> /dev/null; then
    log_error "Docker not found. Please install Docker."
    exit 1
  fi
  log_success "Docker found"
  
  if ! command -v docker &> /dev/null || ! docker compose version &> /dev/null; then
    log_error "docker-compose not found. Please install docker-compose."
    exit 1
  fi
  log_success "docker-compose found"
  
  if ! command -v git &> /dev/null; then
    log_error "Git not found. Please install Git."
    exit 1
  fi
  log_success "Git found"
  
  if netstat -tuln 2>/dev/null | grep -q ":80 " || netstat -tuln 2>/dev/null | grep -q ":443 "; then
    log_error "Port 80 or 443 already in use"
    exit 1
  fi
  log_success "Ports 80 and 443 are free"
}

setup_directories() {
  log_info "Setting up persistent directories..."
  
  sudo mkdir -p /var/lib/petspot/db /var/lib/petspot/images
  sudo chown -R "$USER:$USER" /var/lib/petspot
  chmod -R 755 /var/lib/petspot
  
  log_success "Persistent directories created"
}

setup_env() {
  log_info "Setting up environment..."
  
  if [ ! -f "$DEPLOYMENT_DIR/.env" ]; then
    cp "$DEPLOYMENT_DIR/envExample" "$DEPLOYMENT_DIR/.env"
    log_success ".env file created from template"
    log_warning "Please review and edit .env if needed: nano $DEPLOYMENT_DIR/.env"
  else
    log_success ".env file already exists"
  fi
}

build_images() {
  log_info "Building Docker images..."
  
  cd "$DEPLOYMENT_DIR"
  bash ./scripts/build.sh
  
  log_success "Docker images built"
}

start_containers() {
  log_info "Starting containers..."
  
  cd "$DEPLOYMENT_DIR"
  docker compose up -d
  
  sleep 5
  log_success "Containers started"
}

verify_deployment() {
  log_info "Verifying deployment..."
  
  local max_attempts=30
  local attempt=0
  
  while [ $attempt -lt $max_attempts ]; do
    if docker compose ps | grep -q "petspot-nginx.*Up"; then
      log_success "Nginx is running"
      break
    fi
    attempt=$((attempt + 1))
    sleep 1
  done
  
  if [ $attempt -eq $max_attempts ]; then
    log_error "Nginx failed to start"
    exit 1
  fi
  
  docker compose ps
  
  log_info "Running health checks..."
  
  if curl -sf http://localhost &> /dev/null; then
    log_success "Frontend is accessible at http://localhost"
  else
    log_warning "Frontend health check failed (may need more time to start)"
  fi
  
  if curl -sf http://localhost/api/v1/announcements &> /dev/null; then
    log_success "Backend is accessible at http://localhost/api/v1/announcements"
  else
    log_warning "Backend health check failed (may need more time to start)"
  fi
  
  log_success "Deployment verification complete"
}

main() {
  echo ""
  echo -e "${GREEN}========================================${NC}"
  echo -e "${GREEN}PetSpot Initial Deployment${NC}"
  echo -e "${GREEN}========================================${NC}"
  echo ""
  
  check_prerequisites
  echo ""
  
  setup_directories
  echo ""
  
  setup_env
  echo ""
  
  build_images
  echo ""
  
  start_containers
  echo ""
  
  verify_deployment
  echo ""
  
  echo -e "${GREEN}========================================${NC}"
  echo -e "${GREEN}✓ Deployment Complete!${NC}"
  echo -e "${GREEN}========================================${NC}"
  echo ""
  echo "Access your application:"
  echo "  Frontend: http://localhost"
  echo "  Backend API: http://localhost/api"
  echo ""
  echo "View logs:"
  echo "  docker compose logs -f"
  echo ""
}

main

