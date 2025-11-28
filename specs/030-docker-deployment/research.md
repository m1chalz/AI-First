# Research: Docker-Based Deployment with Nginx Reverse Proxy

**Feature**: 030-docker-deployment  
**Date**: 2025-11-28  
**Purpose**: Document technical decisions and best practices for Docker deployment

## Research Questions

### 1. Docker Multi-Stage Builds for Node.js Applications

**Decision**: Use multi-stage builds for both backend and frontend

**Rationale**:
- **Backend**: Single-stage build sufficient (production dependencies only)
  - Base image: `node:24-alpine` (smallest official Node.js image)
  - Install production dependencies only (`npm ci --omit=dev`)
  - Copy source code and run with node directly
- **Frontend**: Multi-stage build essential for production optimization
  - Stage 1 (build): `node:24-alpine` to build React app with Vite
  - Stage 2 (serve): `nginx:alpine` to serve static files efficiently
  - Minimizes final image size (only static assets, no Node.js in production image)

**Alternatives Considered**:
-

 Fat images with dev dependencies: Rejected due to larger image size, slower deployments, security risks
- Docker Hub pre-built images: Rejected because requirement specifies building locally on VM

**Best Practices Applied**:
- Use Alpine-based images for minimal size
- `.dockerignore` files to exclude `node_modules/`, `.git/`, coverage reports
- Layer caching optimization (COPY package*.json before npm install)
- Non-root user for security in production containers

---

### 2. Nginx Reverse Proxy Configuration with Path-Based Routing

**Decision**: Separate nginx container for reverse proxy with path-based routing to backend/frontend

**Rationale**:
- **Single entry point**: nginx container binds to port 80, routes traffic internally
- **Path-based routing**:
  - `location /api` → `proxy_pass http://backend:3000`
  - `location /images` → `proxy_pass http://backend:3000`
  - `location /` → `proxy_pass http://frontend:8080`
- **Docker networking**: Containers communicate via service names in docker-compose network
- **Clean separation**: Main nginx handles routing, frontend nginx serves static files

**Alternatives Considered**:
- nginx installed on host (not in container): Rejected for consistency with containerized approach
- Backend handling all requests: Rejected because frontend needs independent updates
- Traefik reverse proxy: Rejected for simplicity (nginx more familiar, less configuration)

**Best Practices Applied**:
- `proxy_set_header Host $host` to preserve original host header
- `proxy_set_header X-Real-IP $remote_addr` for client IP logging
- `proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for` for proxy chain
- `proxy_set_header X-Forwarded-Proto $scheme` for protocol awareness
- Client max body size configured for file uploads
- Timeouts configured appropriately for API requests

---

### 3. Docker Volume Strategies for Data Persistence

**Decision**: Use bind mounts to explicit host paths for SQLite database and uploaded images

**Rationale**:
- **Bind mounts** (`/var/lib/petspot/db:/app/server/pets.db`):
  - Explicit host path: `/var/lib/petspot/db/pets.db`
  - Explicit container path: `/app/server/pets.db`
  - Transparent: Easy to see where data lives on host filesystem
  - Backup-friendly: Standard directory structure for backup scripts
  - Debugging: Direct access to database file for troubleshooting
- **Image uploads** (`/var/lib/petspot/images:/app/server/public/images`):
  - Explicit host path: `/var/lib/petspot/images`
  - Explicit container path: `/app/server/public/images`
  - Same benefits as database bind mount

**Alternatives Considered**:
- Named volumes (e.g., `petspot-db:/app/pets.db`): Rejected because less transparent, harder to backup
- No persistence (data in container): Rejected due to data loss on container recreation
- NFS/cloud storage: Rejected for complexity (future enhancement if needed)

**Best Practices Applied**:
- Create host directories before first run (documented in deploy.sh)
- Appropriate permissions on host directories (chmod, chown)
- Volume paths clearly documented in docker-compose.yml comments
- Backup strategy documented for `/var/lib/petspot/` directory

---

### 4. Shell Script Patterns for Deployment Automation

**Decision**: Create four shell scripts (deploy.sh, update.sh, build.sh, logs.sh) with error handling and logging

**Rationale**:
- **deploy.sh**: Complete initial deployment workflow
  - Check prerequisites (Docker, docker-compose, Git, ports 80/443 free)
  - Clone repository
  - Create persistent directories
  - Build Docker images
  - Start containers with docker-compose up
  - Verify deployment (health checks)
- **update.sh**: Update applications workflow
  - Git pull latest changes
  - Rebuild specified images (backend, frontend, or both)
  - Restart containers with docker-compose up --force-recreate
  - Verify updated deployment
- **build.sh**: Build Docker images only (no deployment)
  - Build backend image
  - Build frontend image
  - Tag images appropriately
- **logs.sh**: View container logs
  - Wrapper around `docker compose logs` with examples

**Alternatives Considered**:
- Makefile: Rejected because shell scripts more familiar to DevOps teams
- Single monolithic script: Rejected for modularity and clarity
- Manual commands only: Rejected due to error-prone copy-paste

**Best Practices Applied**:
- `set -euo pipefail` for strict error handling
- Color-coded output (green for success, red for errors, yellow for warnings)
- Verbose logging with timestamps
- Pre-flight checks (verify Docker, docker-compose, Git installed)
- Idempotent operations where possible
- Clear usage instructions (`--help` flag)

---

### 5. Container Build Optimization

**Decision**: Optimize Docker builds for speed and caching

**Rationale**:
- **Layer caching**:
  - Copy `package.json` and `package-lock.json` before source code
  - Run `npm ci` as separate layer (cached if dependencies unchanged)
  - Copy source code in later layer (changes frequently)
- **Build context**:
  - Use `.dockerignore` to exclude unnecessary files
  - Reduce build context size for faster uploads to Docker daemon
- **Image tags**:
  - Tag images with timestamps or commit SHAs for versioning
  - Use `latest` tag for convenience
- **Parallel builds**:
  - Build backend and frontend images in parallel (future optimization)

**Alternatives Considered**:
- BuildKit advanced caching: Deferred to future optimization
- Docker layer caching with CI/CD: Not applicable for manual deployment
- Pre-built base images: Not necessary for small deployments

**Best Practices Applied**:
- Minimize layers by combining RUN commands where logical
- Use `COPY --chown` to set ownership in single layer
- Clean up package manager caches in same RUN command (`npm cache clean --force`)
- Leverage Node.js official images (security-maintained)
- Use exact Node.js version (node:24-alpine) for reproducibility

---

## Technology Stack Decisions

| Component | Technology | Version | Justification |
|-----------|-----------|---------|---------------|
| Container Platform | Docker | Latest stable | Industry standard, already required by project |
| Orchestration | docker-compose | v2.x | Simple, suitable for single-VM deployment |
| Reverse Proxy | nginx | 1.25-alpine (official image) | High performance, mature, well-documented |
| Base Image (Backend) | node:24-alpine | 24.x-alpine | Official image, Alpine for minimal size, Node LTS |
| Base Image (Frontend Build) | node:24-alpine | 24.x-alpine | Consistency with backend, build-time only |
| Base Image (Frontend Serve) | nginx:alpine | 1.25-alpine | Minimal size, production-ready static file server |
| Shell Scripting | Bash | 4.x+ | Ubiquitous on Linux systems, familiar to DevOps |

---

## Security Considerations

1. **Non-root users in containers**:
   - Backend and frontend containers run as non-root user (node, nginx users)
   - Reduces attack surface if container compromised

2. **Network isolation**:
   - Containers communicate via Docker internal network (not exposed to host)
   - Only nginx container exposes port 80 externally

3. **Secrets management**:
   - No secrets in Dockerfiles or docker-compose.yml
   - Environment variables loaded from `.env` file (git-ignored)
   - Documentation includes `.env.example` template

4. **Image scanning** (future):
   - Document `docker scan` usage for vulnerability detection
   - Recommend regular base image updates

5. **Minimal images**:
   - Alpine-based images reduce attack surface
   - Production images contain only runtime dependencies

---

## Performance Optimizations

1. **nginx caching** (future enhancement):
   - Static assets cached with appropriate headers
   - Gzip compression enabled for text-based assets

2. **Image layer optimization**:
   - Dependencies installed before source code (better caching)
   - Multi-stage builds eliminate build tools from production images

3. **Container restart policies**:
   - `unless-stopped` ensures containers restart after failures
   - Health checks documented for future implementation

4. **Resource limits** (future):
   - Memory limits for containers to prevent OOM issues
   - CPU limits to ensure fair resource allocation

---

## Deployment Workflow Summary

```
┌─────────────────────────────────────────────────────────┐
│  Initial Deployment (deploy.sh)                         │
│  1. Check prerequisites (Docker, ports, Git)            │
│  2. Clone repository via Git                            │
│  3. Create persistent directories (/var/lib/petspot/)   │
│  4. Build Docker images (backend, frontend)             │
│  5. Start containers with docker-compose up -d          │
│  6. Verify deployment (curl health checks)              │
└─────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────┐
│  Application Updates (update.sh)                        │
│  1. Git pull latest changes                             │
│  2. Build specified images (backend/frontend/both)      │
│  3. Recreate containers with new images                 │
│  4. Verify updated deployment                           │
└─────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────┐
│  Monitoring & Troubleshooting (logs.sh)                 │
│  - View real-time logs: docker compose logs -f          │
│  - View specific service: docker compose logs backend   │
│  - Check container status: docker compose ps            │
└─────────────────────────────────────────────────────────┘
```

---

## References

- Docker documentation: https://docs.docker.com/
- docker-compose reference: https://docs.docker.com/compose/compose-file/
- nginx reverse proxy guide: https://docs.nginx.com/nginx/admin-guide/web-server/reverse-proxy/
- Node.js Docker best practices: https://github.com/nodejs/docker-node/blob/main/docs/BestPractices.md
- Alpine Linux Docker images: https://hub.docker.com/_/alpine

---

## Decision Log

| Date | Decision | Rationale |
|------|----------|-----------|
| 2025-11-28 | Use bind mounts over named volumes | Transparency, backup simplicity, troubleshooting ease |
| 2025-11-28 | Separate nginx container for reverse proxy | Consistent containerized approach, easier orchestration |
| 2025-11-28 | Multi-stage build for frontend only | Backend simple enough for single-stage, frontend benefits from optimization |
| 2025-11-28 | Shell scripts over Makefile | Familiarity, portability, better error handling |
| 2025-11-28 | unless-stopped restart policy | Auto-recovery from failures, manual control during maintenance |

