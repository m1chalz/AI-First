# PetSpot Deployment Guide

Complete guide for deploying PetSpot backend and frontend on a VM using Docker.

## Quick Links

- **Full Documentation**: See [`deployment/README.md`](deployment/README.md)
- **Quick Start**: 5-step initial deployment in under 30 minutes
- **Update Guide**: Update backend or frontend independently
- **Image Tagging**: Commit-based tags for traceability and rollback

## Prerequisites

Before starting, ensure your VM has:

- Docker v20.10+
- docker-compose v2.0+
- Git
- Ports 80/443 available
- 2GB RAM, 20GB disk space

## Initial Deployment (5 Steps)

### 1. Clone Repository

```bash
git clone <repository-url> petspot
cd petspot
```

### 2. Navigate to Deployment

```bash
cd deployment
```

### 3. Run Deploy Script

```bash
./scripts/deploy.sh
```

This script automatically:
- Checks prerequisites
- Creates persistent directories
- Builds Docker images
- Starts containers
- Verifies health

### 4. Access Application

- **Frontend**: http://localhost
- **API**: http://localhost/api/v1/announcements
- **Health**: http://localhost/health

### 5. View Logs

```bash
docker compose logs -f
```

## Key Scripts

| Script | Purpose | Usage |
|--------|---------|-------|
| `deploy.sh` | Initial deployment | `./scripts/deploy.sh` |
| `update.sh` | Update apps | `./scripts/update.sh --backend/--frontend/--all` |
| `build.sh` | Build images | `./scripts/build.sh` |
| `logs.sh` | View logs | `./scripts/logs.sh --service backend --follow` |

## Image Tagging

Images are tagged with commit hash + date + time for full traceability:

```
Format: [commit-hash]-[date]T[time]
Example: a1b2c3d-20251128T143022
```

Benefits:
- ✅ Trace every image to specific Git commit
- ✅ Rollback to any previous version
- ✅ Audit trail for compliance

## Data Persistence

All data persists across container recreation:

- **Database**: `/var/lib/petspot/db/pets.db` (SQLite)
- **Images**: `/var/lib/petspot/images/` (uploaded files)

## Updating Applications

### Update Backend Only

```bash
cd deployment
./scripts/update.sh --backend
```

### Update Frontend Only

```bash
cd deployment
./scripts/update.sh --frontend
```

### Update Both

```bash
cd deployment
./scripts/update.sh --all
```

## Rollback Procedure

If deployment has issues:

```bash
# List available images
docker images | grep petspot

# Restart with previous tag
IMAGE_TAG=a1b2c3d-20251127T102030 docker compose up -d --force-recreate
```

## Git Workflow

Complete workflow from code change to production:

```bash
# Make code change
nano ../server/src/routes/announcements.ts

# Commit
git add -A
git commit -m "Update API"
git push origin main

# On VM
cd deployment
git pull origin main
./scripts/build.sh
./scripts/update.sh --all
curl http://localhost/api/v1/announcements
```

## Troubleshooting

### Port 80 Already in Use

```bash
sudo netstat -tuln | grep ':80'
sudo systemctl stop apache2
```

### Container Won't Start

```bash
docker compose logs backend
docker compose build --no-cache
```

### Database Issues

```bash
sudo chmod -R 777 /var/lib/petspot/db
```

### Disk Space

```bash
docker system df
docker image prune -a
```

## Common Operations

### View Logs

```bash
# Real-time all services
./scripts/logs.sh --follow

# Specific service
./scripts/logs.sh --service backend --follow

# Last 100 lines
./scripts/logs.sh --tail 100
```

### Stop All Containers

```bash
docker compose down
```

### Start All Containers

```bash
docker compose up -d
```

### Clean Up

```bash
docker compose down -v
sudo rm -rf /var/lib/petspot/*
```

## Performance Metrics

- **Initial Deployment**: 15-30 minutes (includes image build)
- **Update**: 5-15 minutes (backend/frontend only)
- **Build Time**: 5-10 minutes (depends on VM resources)

## Future Enhancements

- [ ] HTTPS/SSL with Let's Encrypt
- [ ] Automated backups for `/var/lib/petspot/`
- [ ] Monitoring and alerting
- [ ] CI/CD pipeline for automated deployments
- [ ] Multi-instance deployment (Docker Swarm/Kubernetes)

## Support

For detailed information, see:
- [`deployment/README.md`](deployment/README.md) - Complete deployment documentation
- [`specs/030-docker-deployment/`](specs/030-docker-deployment/) - Specification and design documents

## Architecture

```
┌─────────────────────────────────────────┐
│         Nginx Reverse Proxy             │
│  Listens on 80, routes traffic          │
└────────────┬────────────────────────────┘
             │
      ┌──────┴──────┐
      │             │
      ▼             ▼
┌─────────────┐  ┌──────────────┐
│  Backend    │  │   Frontend   │
│ Port 3000   │  │   Port 8080  │
│ Express API │  │  React SPA   │
└─────────────┘  └──────────────┘
      │
      ▼
┌──────────────────────────────┐
│    SQLite Database           │
│ /var/lib/petspot/db/pets.db  │
└──────────────────────────────┘
```

---

**Last Updated**: 2025-11-28  
**Version**: 1.0

