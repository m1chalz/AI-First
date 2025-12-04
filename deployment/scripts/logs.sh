#!/bin/bash
set -euo pipefail

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

show_usage() {
  echo "Usage: $0 [OPTIONS]"
  echo ""
  echo "Options:"
  echo "  --service <name>    Show logs for specific service (backend, frontend, nginx)"
  echo "  --all               Show logs for all services (default)"
  echo "  --follow            Follow logs in real-time (like tail -f)"
  echo "  --tail <n>          Show last n lines (default: all)"
  echo "  --help              Show this help message"
  echo ""
  echo "Examples:"
  echo "  $0 --service backend"
  echo "  $0 --service backend --follow"
  echo "  $0 --all --tail 100"
  echo "  $0 --service frontend --follow --tail 50"
}

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEPLOYMENT_DIR="$(dirname "$SCRIPT_DIR")"

SERVICE=""
FOLLOW_FLAG=""
TAIL_FLAG=""

while [[ $# -gt 0 ]]; do
  case $1 in
    --service)
      SERVICE="$2"
      shift 2
      ;;
    --all)
      SERVICE=""
      shift
      ;;
    --follow)
      FOLLOW_FLAG="-f"
      shift
      ;;
    --tail)
      TAIL_FLAG="--tail $2"
      shift 2
      ;;
    --help)
      show_usage
      exit 0
      ;;
    *)
      echo -e "${RED}Unknown option: $1${NC}"
      echo ""
      show_usage
      exit 1
      ;;
  esac
done

cd "$DEPLOYMENT_DIR"

if [ -z "$SERVICE" ]; then
  echo -e "${YELLOW}Showing logs for all services...${NC}"
  docker compose logs $FOLLOW_FLAG $TAIL_FLAG
else
  echo -e "${YELLOW}Showing logs for $SERVICE...${NC}"
  docker compose logs $FOLLOW_FLAG $TAIL_FLAG "$SERVICE"
fi

