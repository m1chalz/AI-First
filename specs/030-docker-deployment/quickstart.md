# Quickstart: Docker Deployment

**Feature**: 030-docker-deployment  
**Purpose**: Get the PetSpot application deployed on a VM in under 30 minutes

## Prerequisites

Before starting, ensure your VM has:

- [ ] **Docker** installed (v20.10+)
  ```bash
  docker --version
  ```
- [ ] **docker-compose** installed (v2.0+)
  ```bash
  docker compose version
  ```
- [ ] **Git** installed
  ```bash
  git --version
  ```
- [ ] **Ports 80/443 available** (not in use by other services)
  ```bash
  sudo netstat -tuln | grep ':80\|:443'
  # Should return empty if ports are free
  ```
- [ ] **SSH access** configured
- [ ] **Sufficient resources**: 2GB RAM, 20GB disk space

## Quick Deployment (5 Steps)

### Step 1: Clone Repository

```bash
# Clone the repository
git clone <repository-url> petspot
cd petspot
```

### Step 2: Create Persistent Directories

```bash
# Create directories for data persistence
sudo mkdir -p /var/lib/petspot/db
sudo mkdir -p /var/lib/petspot/images

# Set appropriate permissions
sudo chown -R $USER:$USER /var/lib/petspot
chmod -R 755 /var/lib/petspot
```

### Step 3: Configure Environment

```bash
# Navigate to deployment directory
cd deployment

# Create .env from template
cp .env.example .env

# Edit .env if needed (optional - defaults work for most cases)
nano .env
```

### Step 4: Build Docker Images

```bash
# Build backend and frontend images
./scripts/build.sh

# This takes 5-10 minutes depending on VM resources
```

### Step 5: Start Containers

```bash
# Start all containers in detached mode
docker compose up -d

# Wait for containers to be healthy (30-60 seconds)
docker compose ps

# Verify deployment
curl http://localhost
curl http://localhost/api/health
```

## Verification

After deployment, verify the system:

```bash
# Check container status
docker compose ps

# Expected output:
# NAME                 STATUS        PORTS
# petspot-nginx        Up (healthy)  0.0.0.0:80->80/tcp
# petspot-backend      Up (healthy)
# petspot-frontend     Up (healthy)

# Test frontend
curl -I http://localhost
# Expected: HTTP/1.1 200 OK

# Test backend API
curl http://localhost/api/pets
# Expected: JSON array of pets

# Test image serving
curl -I http://localhost/images/test.jpg
# Expected: HTTP/1.1 200 OK (if test.jpg exists)
```

## View Logs

```bash
# View logs for all services
docker compose logs -f

# View logs for specific service
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f nginx

# View last 100 lines
docker compose logs --tail=100
```

## Updating Applications

### Update Backend Only

```bash
# Pull latest code
git pull origin main

# Rebuild backend image
docker compose build backend

# Recreate backend container
docker compose up -d --force-recreate backend

# Verify update
docker compose logs -f backend
```

### Update Frontend Only

```bash
# Pull latest code
git pull origin main

# Rebuild frontend image
docker compose build frontend

# Recreate frontend container
docker compose up -d --force-recreate frontend

# Verify update
docker compose logs -f frontend
```

### Update Both Applications

```bash
# Use the update script
./scripts/update.sh --all

# Or manually:
git pull origin main
docker compose build
docker compose up -d --force-recreate
```

## Common Operations

### Stop All Containers

```bash
docker compose down
```

### Start All Containers

```bash
docker compose up -d
```

### Restart Specific Service

```bash
docker compose restart backend
docker compose restart frontend
docker compose restart nginx
```

### Remove Everything (including volumes)

```bash
# WARNING: This removes ALL data including database
docker compose down -v

# To keep data, use:
docker compose down
```

## Troubleshooting

### Port 80 Already in Use

```bash
# Find what's using port 80
sudo netstat -tuln | grep ':80'

# Stop conflicting service (example: Apache)
sudo systemctl stop apache2

# Or use a different port in docker-compose.yml:
# ports:
#   - "8080:80"
```

### Container Won't Start

```bash
# Check container logs
docker compose logs <service-name>

# Check Docker daemon
sudo systemctl status docker

# Restart Docker if needed
sudo systemctl restart docker
```

### Database File Permissions

```bash
# If backend can't access pets.db
sudo chown -R $USER:$USER /var/lib/petspot/db
chmod 644 /var/lib/petspot/db/pets.db
```

### Images Not Serving

```bash
# Check images directory permissions
sudo chown -R $USER:$USER /var/lib/petspot/images
chmod -R 755 /var/lib/petspot/images

# Verify backend can access images
docker exec petspot-backend ls -la /app/server/public/images
```

### Containers Keep Restarting

```bash
# Check container status
docker compose ps

# View recent logs
docker compose logs --tail=50 <service-name>

# Common causes:
# 1. Port conflict (check with: sudo netstat -tuln)
# 2. Missing environment variables (check .env file)
# 3. Build errors (rebuild: docker compose build --no-cache)
```

## Health Checks

### Manual Health Check

```bash
# Frontend health
curl http://localhost

# Backend health
curl http://localhost/api/health

# Nginx health
curl http://localhost/health
```

### Container Health Status

```bash
# View health status
docker compose ps

# Inspect specific container health
docker inspect petspot-backend --format='{{.State.Health.Status}}'
```

## Directory Structure After Deployment

```
petspot/
├── server/
│   ├── Dockerfile             # Backend Docker image definition
│   ├── pets.db               # Mounted from /var/lib/petspot/db/pets.db
│   └── public/images/        # Mounted from /var/lib/petspot/images/
├── webApp/
│   ├── Dockerfile             # Frontend Docker image definition (multi-stage)
│   └── nginx.conf             # Frontend nginx config (serves static files)
├── deployment/
│   ├── docker-compose.yml     # Container orchestration
│   ├── nginx/
│   │   └── nginx.conf        # Main reverse proxy config
│   ├── scripts/
│   │   ├── deploy.sh         # Initial deployment script
│   │   ├── update.sh         # Update applications script
│   │   ├── build.sh          # Build images script
│   │   └── logs.sh           # View logs script
│   ├── .env                  # Environment variables (created from .env.example)
│   └── README.md             # Full deployment documentation
└── /var/lib/petspot/         # Host persistence directories
    ├── db/
    │   └── pets.db           # SQLite database (persisted)
    └── images/               # Uploaded images (persisted)
```

## Next Steps

- Set up HTTPS with Let's Encrypt (future enhancement)
- Configure automated backups for `/var/lib/petspot/`
- Set up monitoring and alerting
- Implement CI/CD pipeline for automated deployments

## Quick Reference

| Command | Purpose |
|---------|---------|
| `docker compose up -d` | Start all containers |
| `docker compose down` | Stop all containers |
| `docker compose ps` | View container status |
| `docker compose logs -f` | View live logs |
| `docker compose build` | Rebuild all images |
| `docker compose restart <service>` | Restart specific service |
| `./scripts/update.sh --all` | Update both applications |
| `./scripts/logs.sh --service backend` | View backend logs |

## Support

For issues or questions:
1. Check logs: `docker compose logs -f`
2. Review documentation: `deployment/README.md`
3. Verify prerequisites are met
4. Check firewall rules (port 80 must be open)

---

**Deployment Time**: Typically 15-30 minutes for first-time deployment  
**Update Time**: Typically 5-15 minutes depending on code changes

