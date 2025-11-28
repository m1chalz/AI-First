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

show_usage() {
  echo "Usage: $0 [OPTIONS]"
  echo ""
  echo "Options:"
  echo "  --backend     Update backend service only"
  echo "  --frontend    Update frontend service only"
  echo "  --all         Update both services"
  echo "  --help        Show this help message"
  echo ""
  echo "Examples:"
  echo "  $0 --backend"
  echo "  $0 --frontend"
  echo "  $0 --all"
}

pull_updates() {
  log_info "Pulling latest code from Git..."
  cd "$REPO_ROOT"
  git pull origin main
  log_success "Code updated"
}

build_service() {
  local service=$1
  log_info "Building $service image..."
  cd "$DEPLOYMENT_DIR"
  docker compose build "$service"
  log_success "$service image built"
}

recreate_service() {
  local service=$1
  log_info "Recreating $service container..."
  cd "$DEPLOYMENT_DIR"
  docker compose up -d --force-recreate "$service"
  log_success "$service container recreated"
}

verify_service() {
  local service=$1
  log_info "Verifying $service health..."
  
  local max_attempts=30
  local attempt=0
  
  while [ $attempt -lt $max_attempts ]; do
    if docker compose ps | grep -q "${service}.*Up"; then
      log_success "$service is running"
      break
    fi
    attempt=$((attempt + 1))
    sleep 1
  done
  
  if [ $attempt -eq $max_attempts ]; then
    log_error "$service failed to start"
    return 1
  fi
}

update_backend() {
  echo ""
  echo -e "${GREEN}========================================${NC}"
  echo -e "${GREEN}Updating Backend${NC}"
  echo -e "${GREEN}========================================${NC}"
  echo ""
  
  pull_updates
  build_service backend
  recreate_service backend
  verify_service backend
  
  echo ""
  log_success "Backend updated successfully"
  log_info "Tail logs: docker compose logs -f backend"
}

update_frontend() {
  echo ""
  echo -e "${GREEN}========================================${NC}"
  echo -e "${GREEN}Updating Frontend${NC}"
  echo -e "${GREEN}========================================${NC}"
  echo ""
  
  pull_updates
  build_service frontend
  recreate_service frontend
  verify_service frontend
  
  echo ""
  log_success "Frontend updated successfully"
  log_info "Tail logs: docker compose logs -f frontend"
}

update_all() {
  echo ""
  echo -e "${GREEN}========================================${NC}"
  echo -e "${GREEN}Updating All Services${NC}"
  echo -e "${GREEN}========================================${NC}"
  echo ""
  
  pull_updates
  
  log_info "Building all images..."
  cd "$DEPLOYMENT_DIR"
  docker compose build
  log_success "All images built"
  
  log_info "Recreating all containers..."
  docker compose up -d --force-recreate
  log_success "All containers recreated"
  
  verify_service backend
  verify_service frontend
  
  echo ""
  log_success "All services updated successfully"
  log_info "Tail logs: docker compose logs -f"
}

main() {
  if [ $# -eq 0 ]; then
    log_error "No option specified"
    echo ""
    show_usage
    exit 1
  fi
  
  case "$1" in
    --backend)
      update_backend
      ;;
    --frontend)
      update_frontend
      ;;
    --all)
      update_all
      ;;
    --help)
      show_usage
      ;;
    *)
      log_error "Unknown option: $1"
      echo ""
      show_usage
      exit 1
      ;;
  esac
}

main "$@"

