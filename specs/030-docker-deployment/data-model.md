# Data Model: Deployment Configuration Structure

**Feature**: 030-docker-deployment  
**Date**: 2025-11-28  
**Purpose**: Define the structure and relationships of Docker deployment configuration

## Overview

This feature introduces deployment infrastructure, not traditional application data models. The "data model" here describes the configuration files, their structure, and interdependencies.

## Configuration File Hierarchy

```
deployment/
├── docker-compose.yml          [Orchestration Root]
│   ├── services:
│   │   ├── nginx               → nginx/nginx.conf
│   │   ├── backend             → /server/Dockerfile
│   │   └── frontend            → /webApp/Dockerfile
│   ├── networks:
│   │   └── petspot-network     [Internal bridge network]
│   └── volumes:
│       ├── database            → /var/lib/petspot/db
│       └── images              → /var/lib/petspot/images
│
├── nginx/
│   └── nginx.conf              [Reverse Proxy Routes]
│       ├── location /api       → http://backend:3000
│       ├── location /images    → http://backend:3000
│       └── location /          → http://frontend:8080
│
├── scripts/
│   ├── deploy.sh               [Calls: build.sh → docker compose up]
│   ├── update.sh               [Calls: git pull → build.sh → docker compose up --force-recreate]
│   ├── build.sh                [Builds: backend + frontend images]
│   └── logs.sh                 [Wraps: docker compose logs]
│
├── .env                        [Environment Variables - git-ignored]
└── README.md                   [Documentation]
```

## Configuration Entities

### 1. Docker Compose Configuration (`docker-compose.yml`)

**Purpose**: Orchestrates all containers and defines their relationships

**Structure**:
```yaml
services:
  nginx:          # Service 1: Reverse Proxy
  backend:        # Service 2: Node.js API
  frontend:       # Service 3: React App

networks:
  petspot-network:  # Internal bridge network

volumes:
  # Bind mounts (explicit host paths)
```

**Key Relationships**:
- nginx → backend: Proxies `/api`, `/images` requests
- nginx → frontend: Proxies all other requests
- backend → volumes: Persists database and images
- All services → network: Communicate via service names

**Validation Rules**:
- Service names MUST match DNS names used in nginx.conf
- Port mappings MUST NOT conflict (only nginx exposes 80)
- Volume paths MUST be absolute on host
- Restart policy MUST be `unless-stopped` for all services

---

### 2. Nginx Reverse Proxy Configuration (`nginx/nginx.conf`)

**Purpose**: Routes HTTP requests to backend or frontend based on URL path

**Structure**:
```nginx
upstream backend {
    server backend:3000;
}

upstream frontend {
    server frontend:8080;
}

server {
    listen 80;
    
    location /api { ... }        # → backend
    location /images { ... }     # → backend
    location / { ... }           # → frontend
}
```

**Key Relationships**:
- Upstream `backend` → docker-compose service `backend` (port 3000)
- Upstream `frontend` → docker-compose service `frontend` (port 8080)
- Location blocks define routing rules

**Validation Rules**:
- Upstream server names MUST match docker-compose service names
- Port numbers MUST match container internal ports
- Location order matters (most specific first, `/` last)
- Proxy headers MUST preserve original request information

---

### 3. Backend Dockerfile (`/server/Dockerfile`)

**Purpose**: Builds backend Node.js application container

**Structure**:
```dockerfile
FROM node:24-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci --omit=dev
COPY . .
EXPOSE 3000
CMD ["node", "src/index.js"]
```

**Key Relationships**:
- Base image: `node:24-alpine`
- Build context: `/server` directory
- Exposes port 3000 (internal, not mapped to host)
- Mounts volumes for `pets.db` and `public/images/`

**Validation Rules**:
- WORKDIR MUST be `/app` (matches volume mount paths)
- EXPOSE MUST match port in docker-compose (3000)
- CMD MUST start the Express server
- USER SHOULD be non-root for security

---

### 4. Frontend Dockerfile (`/webApp/Dockerfile`)

**Purpose**: Builds and serves frontend React application (multi-stage)

**Structure**:
```dockerfile
# Stage 1: Build
FROM node:24-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# Stage 2: Serve
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 8080
```

**Key Relationships**:
- Build stage: Uses `node:24-alpine` to compile React app
- Serve stage: Uses `nginx:alpine` to serve static files
- Exposes port 8080 (internal, not mapped to host)
- nginx.conf configures static file server

**Validation Rules**:
- Build output directory MUST match Vite default (`dist/`)
- nginx.conf MUST listen on port 8080
- EXPOSE MUST match port in docker-compose (8080)
- Static files MUST be copied to nginx html directory

---

### 5. Frontend Nginx Configuration (`/webApp/nginx.conf`)

**Purpose**: Serves React static files and handles client-side routing

**Structure**:
```nginx
server {
    listen 8080;
    root /usr/share/nginx/html;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

**Key Relationships**:
- Copied into frontend container at build time
- Serves files from `/usr/share/nginx/html` (Dockerfile COPY destination)
- Handles React Router by falling back to `index.html`

**Validation Rules**:
- Listen port MUST be 8080 (matches EXPOSE in Dockerfile)
- Root directory MUST match Dockerfile COPY destination
- try_files MUST fallback to index.html for SPA routing

---

### 6. Environment Variables (`.env`)

**Purpose**: Store environment-specific configuration (git-ignored)

**Structure**:
```env
# Backend
NODE_ENV=production
DATABASE_PATH=/app/server/pets.db
IMAGES_PATH=/app/server/public/images

# Frontend
VITE_API_URL=http://localhost:80

# Host paths
HOST_DB_PATH=/var/lib/petspot/db
HOST_IMAGES_PATH=/var/lib/petspot/images
```

**Key Relationships**:
- Loaded by docker-compose via `env_file` directive
- Referenced in containers as environment variables
- `.env.example` checked into Git, `.env` git-ignored

**Validation Rules**:
- Paths MUST be absolute
- No secrets in `.env.example` (template only)
- `.env` MUST be created from `.env.example` during deployment

---

## Deployment Shell Scripts

### 1. deploy.sh

**Purpose**: Perform initial deployment on fresh VM

**Logic Flow**:
```bash
1. Check prerequisites
   ├── Docker installed?
   ├── docker-compose installed?
   ├── Git installed?
   ├── Ports 80/443 free?
   └── SSH keys configured?

2. Clone repository
   └── git clone <repo-url>

3. Create persistent directories
   ├── mkdir -p /var/lib/petspot/db
   ├── mkdir -p /var/lib/petspot/images
   └── chown/chmod as needed

4. Create .env from .env.example
   └── cp .env.example .env (user must edit)

5. Build Docker images
   └── ./deployment/scripts/build.sh

6. Start containers
   └── docker compose -f deployment/docker-compose.yml up -d

7. Verify deployment
   ├── curl http://localhost:80
   ├── curl http://localhost:80/api/pets
   └── docker compose ps
```

**Exit Codes**:
- 0: Success
- 1: Prerequisites check failed
- 2: Git clone failed
- 3: Directory creation failed
- 4: Docker build failed
- 5: Container start failed

---

### 2. update.sh

**Purpose**: Update applications (backend, frontend, or both)

**Logic Flow**:
```bash
1. Parse arguments
   └── --backend | --frontend | --all

2. Git pull latest changes
   └── git pull origin main

3. Build specified images
   ├── If --backend: docker build -t backend:latest ./server
   ├── If --frontend: docker build -t frontend:latest ./webApp
   └── If --all: build both

4. Recreate containers
   └── docker compose up -d --force-recreate <service>

5. Verify update
   └── curl health checks
```

**Exit Codes**:
- 0: Success
- 1: Invalid arguments
- 2: Git pull failed
- 3: Build failed
- 4: Container recreation failed

---

### 3. build.sh

**Purpose**: Build Docker images with commit hash + timestamp tags

**Logic Flow**:
```bash
1. Generate image tag
   ├── Get commit hash: git rev-parse --short HEAD
   ├── Get timestamp: date +%Y%m%d-%H%M%S
   └── Combine: IMAGE_TAG="${COMMIT_HASH}-${TIMESTAMP}"

2. Build backend image
   ├── docker build -t petspot-backend:${IMAGE_TAG} -f server/Dockerfile server/
   └── docker tag petspot-backend:${IMAGE_TAG} petspot-backend:latest

3. Build frontend image
   ├── docker build -t petspot-frontend:${IMAGE_TAG} -f webApp/Dockerfile webApp/
   └── docker tag petspot-frontend:${IMAGE_TAG} petspot-frontend:latest

4. Export IMAGE_TAG for docker-compose
   └── export IMAGE_TAG
```

**Tagging Format**: `(commit-hash)-(timestamp)`
- Example: `abc1234-20251128-143022`
- Provides traceability and rollback capability

**Exit Codes**:
- 0: Success
- 1: Backend build failed
- 2: Frontend build failed

---

### 4. logs.sh

**Purpose**: View container logs with examples

**Logic Flow**:
```bash
1. Parse arguments
   ├── --service <name>: Show logs for specific service
   ├── --follow: Follow log output
   └── --tail <n>: Show last n lines

2. Execute docker compose logs
   └── docker compose -f deployment/docker-compose.yml logs <args>
```

**Usage Examples**:
```bash
./logs.sh --service backend --follow
./logs.sh --service frontend --tail 100
./logs.sh --all --follow
```

---

## Data Persistence Model

### Bind Mount Strategy

```
Host Filesystem                 Container Filesystem
┌──────────────────────┐        ┌──────────────────────┐
│ /var/lib/petspot/    │        │ backend container    │
│ ├── db/              │ ────► │ /app/server/         │
│ │   └── pets.db      │ <────┤ │   └── pets.db        │
│ │                    │        │ │                    │
│ └── images/          │ ────► │ /app/server/public/  │
│     ├── photo1.jpg   │ <────┤ │   └── images/        │
│     └── photo2.jpg   │        │       ├── photo1.jpg │
└──────────────────────┘        │       └── photo2.jpg │
                                └──────────────────────┘
```

**Persistence Rules**:
1. SQLite database file persists to `/var/lib/petspot/db/pets.db`
2. Uploaded images persist to `/var/lib/petspot/images/`
3. Bind mounts are two-way (host ↔ container)
4. Data survives container recreation, updates, and VM reboots
5. Host directories MUST exist before first container start

---

## Configuration Validation Checklist

- [ ] docker-compose.yml service names match nginx upstream servers
- [ ] Internal container ports match nginx proxy_pass ports
- [ ] Volume bind mount paths are absolute on host
- [ ] Restart policies set to `unless-stopped` for all services
- [ ] Only nginx container exposes ports to host (port 80)
- [ ] Backend Dockerfile EXPOSE matches docker-compose port (3000)
- [ ] Frontend Dockerfile EXPOSE matches docker-compose port (8080)
- [ ] Frontend nginx.conf listen port matches EXPOSE (8080)
- [ ] Main nginx.conf upstream ports match container internal ports
- [ ] .env paths match volume mount destinations
- [ ] .dockerignore files exclude node_modules, .git, coverage

---

## Deployment State Transitions

```
┌──────────────┐  deploy.sh   ┌──────────────┐
│ Fresh VM     │ ──────────► │ Deployed     │
│ (No Docker)  │              │ (Running)    │
└──────────────┘              └──────┬───────┘
                                     │
                    update.sh        │
                  ┌──────────────────┴──────────────────┐
                  │                                     │
                  ▼                                     │
         ┌──────────────┐                              │
         │ Updating     │                              │
         │ (Rebuilding) │                              │
         └──────┬───────┘                              │
                │ docker compose up --force-recreate   │
                ▼                                     │
         ┌──────────────┐                              │
         │ Deployed     │ ◄────────────────────────────┘
         │ (Updated)    │
         └──────────────┘
```

**State Definitions**:
- **Fresh VM**: Docker installed, ports free, no containers
- **Deployed**: All 3 containers running, data persisted
- **Updating**: Containers recreated with new images
- **Deployed (Updated)**: New code running, data preserved

---

## Dependencies Between Configuration Files

```
docker-compose.yml (root)
├── requires: nginx/nginx.conf
├── requires: server/Dockerfile
├── requires: webApp/Dockerfile
├── requires: .env (runtime)
└── creates: petspot-network

nginx/nginx.conf
├── requires: backend service (from docker-compose.yml)
├── requires: frontend service (from docker-compose.yml)
└── listens on: port 80 (exposed to host)

server/Dockerfile
├── requires: server/package.json
├── requires: server/src/**/*
├── exposes: port 3000 (internal)
└── volumes: /app/server/pets.db, /app/server/public/images

webApp/Dockerfile
├── requires: webApp/package.json
├── requires: webApp/src/**/*
├── requires: webApp/nginx.conf
├── exposes: port 8080 (internal)
└── builds: static files → /usr/share/nginx/html

Scripts (deploy.sh, update.sh, build.sh, logs.sh)
├── require: Docker, docker-compose, Git
├── operate on: docker-compose.yml
└── create/modify: .env, /var/lib/petspot/*
```

---

## Summary

This deployment configuration model defines:
1. **3 Docker containers** (nginx, backend, frontend) orchestrated by docker-compose
2. **Path-based routing** via nginx reverse proxy (/ api, /images → backend; rest → frontend)
3. **Data persistence** via bind mounts (/var/lib/petspot/db, /var/lib/petspot/images)
4. **Deployment automation** via 4 shell scripts (deploy, update, build, logs)
5. **Configuration hierarchy** with clear dependencies and validation rules

All components work together to provide a reproducible, maintainable deployment infrastructure.

