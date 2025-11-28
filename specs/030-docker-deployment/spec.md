# Feature Specification: Docker-Based Deployment with Nginx Reverse Proxy

**Feature Branch**: `030-docker-deployment`  
**Created**: 2025-11-28  
**Status**: Draft  
**Input**: User description: "Deploy backend and frontend applications using Docker, docker-compose, and nginx on a VM with path-based routing"

## Clarifications

### Session 2025-11-28

- Q: Uploaded images storage path in backend? → A: Store in `server/public/images/` directory
- Q: Frontend deployment mode (static build vs dev server)? → A: Production build (static files served by nginx or simple HTTP server)
- Q: SQLite database file location in backend? → A: Store in `server/pets.db` (root of server directory)
- Q: Docker restart policy for container recovery? → A: `unless-stopped` - restart unless manually stopped
- Q: Source code deployment method to VM? → A: Git clone/pull from repository
- Q: Frontend static file server type? → A: nginx inside frontend container
- Q: Deployment automation level? → A: Shell scripts for common operations (deploy.sh, update.sh, build.sh)
- Q: Internal container ports? → A: Backend: 3000, Frontend: 8080
- Q: Nginx deployment strategy? → A: Nginx as separate Docker container in docker-compose
- Q: Docker volume type for persistence? → A: Bind mounts with explicit host paths
- Q: Host paths for backend persistence bind mounts? → A: Use `/var/lib/petspot/db` for SQLite and `/var/lib/petspot/images` for uploads
- Q: Preferred method for viewing container logs? → A: Use `docker compose logs <service>` documented with examples
- Q: Strategy when host ports 80/443 are occupied? → A: Operators must free the ports before deployment proceeds
- Q: Docker image tagging format? → A: Use `(commit-hash)-(timestamp)` format, e.g., `abc1234-20251128-143022`, also tag as `latest`
- Q: Nginx proxy header configuration? → A: Keep simple with basic `proxy_pass` directives only, no header preservation needed
- Q: Backend startup command? → A: Use `npm start` which includes `--experimental-transform-types` flag for TypeScript

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Initial Deployment Setup (Priority: P1)

As a DevOps engineer, I need to perform the initial deployment of both backend and frontend applications on the VM so that the applications are accessible to end users through a single hostname with path-based routing.

**Why this priority**: This is the foundational capability - without it, no applications can be deployed. It establishes the entire infrastructure and must work before any updates or maintenance can occur.

**Independent Test**: Can be fully tested by deploying both applications from scratch on a fresh VM, then verifying that HTTP requests to `/api/*` and `/images/*` reach the backend and all other requests reach the frontend. Success means users can access both applications through a single domain.

**Acceptance Scenarios**:

1. **Given** a VM with SSH access and Docker installed, **When** deployment configuration is applied, **Then** nginx, backend, and frontend containers start successfully and are accessible
2. **Given** deployed applications, **When** a request is made to `/api/pets`, **Then** the backend API responds with pet data
3. **Given** deployed applications, **When** a request is made to `/images/some-photo.jpg`, **Then** the backend serves the image file
4. **Given** deployed applications, **When** a request is made to `/` or any non-api/non-images path, **Then** the frontend application loads and displays correctly
5. **Given** deployed applications, **When** the frontend makes API calls to `/api/*` or requests images from `/images/*`, **Then** requests are properly routed to the backend without CORS issues

---

### User Story 2 - Application Updates (Priority: P2)

As a DevOps engineer, I need to update either the backend or frontend application independently so that new features and fixes can be deployed without affecting the other application or requiring full system redeployment.

**Why this priority**: Once initial deployment works, the ability to update applications is the next most critical operation. This will be performed frequently as development continues, so it must be reliable and straightforward.

**Independent Test**: Can be tested by making a visible change to either application (e.g., change API response or UI text), rebuilding that application's Docker image, and deploying the update. Success means the updated application reflects changes while the other application continues running unaffected.

**Acceptance Scenarios**:

1. **Given** deployed applications, **When** backend code is updated and redeployed, **Then** new backend version serves updated API responses and all data persists (SQLite database, uploaded images)
2. **Given** deployed applications, **When** frontend code is updated and redeployed, **Then** new frontend version displays updated UI
3. **Given** backend with SQLite database containing pet records, **When** backend container is recreated, **Then** all database records remain intact

---

### User Story 3 - Build and Deploy from Source (Priority: P3)

As a DevOps engineer, I need to build Docker images directly on the VM from source code so that applications can be deployed and updated without relying on external image registries.

**Why this priority**: While essential for the deployment workflow, this is a supporting capability. The deployment can be manually tested once the container orchestration (P1) and data persistence (P2) are working.

**Independent Test**: Can be tested by making a code change, pushing to Git repository, pulling updates on VM, building images using provided scripts or commands, and verifying that containers run the updated code. Success means complete Git-pull-to-deploy cycle works on the VM.

**Acceptance Scenarios**:

1. **Given** source code deployed via Git clone on the VM, **When** Docker build command is executed, **Then** backend and frontend images are built successfully
2. **Given** newly built images, **When** containers are recreated with new images, **Then** updated application code is running
3. **Given** a failed image build, **When** reviewing build logs, **Then** error messages clearly indicate what went wrong

---

### Edge Cases

- What happens when the backend container fails or crashes while the frontend is running?
- How does the system handle port conflicts if standard ports (80, 443) are already in use? → Deployment checklist requires verifying ports 80/443 are free and freeing them before running docker-compose
- What happens when Docker daemon is not running or becomes unavailable?
- What happens if nginx configuration is invalid and nginx fails to start?
- How are container logs accessed when troubleshooting issues?
- What happens if SQLite database file becomes corrupted?
- How does the system handle insufficient disk space during image build?
- What happens if Docker build fails partway through (e.g., npm install error)?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST provide Docker configuration (Dockerfile) for backend application that includes all necessary dependencies and runs the Node.js server
- **FR-002**: System MUST provide Docker configuration (Dockerfile) for frontend application that builds production static files and serves them using nginx
- **FR-003**: System MUST provide docker-compose configuration that orchestrates nginx, backend, and frontend containers with proper networking and `unless-stopped` restart policy
- **FR-004**: System MUST configure nginx as a reverse proxy that routes requests with `/api` and `/images` prefixes to backend container (port 3000) and all other requests to frontend container (port 8080)
- **FR-005**: System MUST expose applications through standard HTTP port (80) or HTTPS port (443) on the VM
- **FR-006**: System MUST persist SQLite database file (server/pets.db) across container restarts and updates using bind mounts to explicit host path `/var/lib/petspot/db`
- **FR-007**: System MUST persist uploaded image files stored in `server/public/images/` directory across container restarts and updates using bind mounts to explicit host path `/var/lib/petspot/images`
- **FR-008**: Docker images MUST be buildable on the VM from source code (deployed via Git clone/pull) without requiring external image registries
- **FR-009**: Deployment process MUST provide shell scripts and documentation for initial setup on a fresh VM (e.g., deploy.sh)
- **FR-010**: Deployment process MUST provide shell scripts and documentation for building images and updating applications (e.g., build.sh, update.sh)
- **FR-011**: System MUST provide documented instructions for viewing application logs using `docker compose logs <service>` (including examples for backend, frontend, and nginx)
- **FR-013**: Deployment documentation MUST include a pre-flight check to ensure host ports 80/443 are free and steps to stop conflicting services before starting nginx
- **FR-012**: System MUST handle backend API responses without CORS issues when frontend makes cross-origin requests

### Key Entities

- **Backend Container**: Runs Node.js Express server on internal port 3000, exposes REST API with `/api` prefix and serves uploaded images with `/images` prefix, uses SQLite database (server/pets.db)
- **Frontend Container**: Serves React application production build (static files) via nginx on internal port 8080, makes HTTP requests to backend through main nginx proxy
- **Nginx Container**: Separate container managed by docker-compose, acts as reverse proxy and single entry point, routes traffic based on URL path, exposes port 80/443 to external network
- **Docker Network**: Internal network connecting all containers, allows containers to communicate using service names
- **Docker Volumes**: Bind mounts to explicit host paths providing persistent storage for SQLite database file (server/pets.db) and uploaded image files (server/public/images/) that must survive container recreation and updates

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: DevOps engineer can perform initial deployment on a fresh VM in under 30 minutes by following provided documentation
- **SC-002**: Applications remain accessible through a single hostname with correct routing for 99.9% of requests (backend for `/api/*` and `/images/*`, frontend for others)
- **SC-003**: DevOps engineer can build and update either application independently in under 15 minutes (including image build time)
- **SC-004**: SQLite database and uploaded images persist across 100% of container updates and restarts with zero data loss
- **SC-005**: System successfully recovers from individual container failures within 2 minutes using Docker restart policy (`unless-stopped`)
- **SC-006**: Application logs are accessible within 5 seconds using standard Docker commands
- **SC-007**: Frontend successfully communicates with backend API without CORS errors in 100% of legitimate requests

## Assumptions

- Docker and docker-compose are already installed on the VM (or installation instructions are readily available)
- VM has sufficient resources (CPU, RAM, disk space) to run both applications simultaneously and build Docker images
- SSH access to VM is already configured and secured
- Domain name or IP address for accessing applications is already available and DNS is configured
- HTTPS/SSL certificates will be addressed in a future enhancement (starting with HTTP is acceptable)
- Manual deployment is acceptable for initial version (CI/CD pipeline is future work)
- Standard Docker networking configuration is sufficient for application needs
- Bind mounts will use explicit host paths relative to the deployment directory for data persistence
- Backend API is designed to work behind a reverse proxy with path prefix
- Backend uses SQLite database (server/pets.db) which is suitable for containerization
- Docker images will be built locally on the VM from source code deployed via Git clone/pull (no external image registry required)
- Git repository access is available from the VM (for cloning and pulling updates)
- Application downtime during updates is acceptable (no need for zero-downtime deployments)
- No sensitive secrets need to be managed (environment variables use default/non-sensitive values)
