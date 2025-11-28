# Feature Specification: Docker-Based Deployment with Nginx Reverse Proxy

**Feature Branch**: `030-docker-deployment`  
**Created**: 2025-11-28  
**Status**: Draft  
**Input**: User description: "Deploy backend and frontend applications using Docker, docker-compose, and nginx on a VM with path-based routing"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Initial Deployment Setup (Priority: P1)

As a DevOps engineer, I need to perform the initial deployment of both backend and frontend applications on the VM so that the applications are accessible to end users through a single hostname with path-based routing.

**Why this priority**: This is the foundational capability - without it, no applications can be deployed. It establishes the entire infrastructure and must work before any updates or maintenance can occur.

**Independent Test**: Can be fully tested by deploying both applications from scratch on a fresh VM, then verifying that HTTP requests to `/api/*` and `/images/*` reach the backend and all other requests reach the frontend. Success means users can access both applications through a single domain.

**Acceptance Scenarios**:

1. **Given** a VM with SSH access and Docker installed, **When** deployment configuration is applied, **Then** both backend and frontend containers start successfully and are accessible
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

**Independent Test**: Can be tested by making a code change, building images on the VM using provided scripts or commands, and verifying that containers run the updated code. Success means complete build-to-deploy cycle works on the VM.

**Acceptance Scenarios**:

1. **Given** source code on the VM, **When** Docker build command is executed, **Then** backend and frontend images are built successfully
2. **Given** newly built images, **When** containers are recreated with new images, **Then** updated application code is running
3. **Given** a failed image build, **When** reviewing build logs, **Then** error messages clearly indicate what went wrong

---

### Edge Cases

- What happens when the backend container fails or crashes while the frontend is running?
- How does the system handle port conflicts if standard ports (80, 443) are already in use?
- What happens when Docker daemon is not running or becomes unavailable?
- What happens if nginx configuration is invalid and nginx fails to start?
- How are container logs accessed when troubleshooting issues?
- What happens if SQLite database file becomes corrupted?
- How does the system handle insufficient disk space during image build?
- What happens if Docker build fails partway through (e.g., npm install error)?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST provide Docker configuration (Dockerfile) for backend application that includes all necessary dependencies and runs the Node.js server
- **FR-002**: System MUST provide Docker configuration (Dockerfile) for frontend application that serves static files or runs the development server
- **FR-003**: System MUST provide docker-compose configuration that orchestrates both backend and frontend containers with proper networking
- **FR-004**: System MUST configure nginx as a reverse proxy that routes requests with `/api` and `/images` prefixes to backend container and all other requests to frontend container
- **FR-005**: System MUST expose applications through standard HTTP port (80) or HTTPS port (443) on the VM
- **FR-006**: System MUST persist SQLite database file (pets.db) across container restarts and updates using Docker volumes
- **FR-007**: System MUST persist uploaded image files across container restarts and updates using Docker volumes
- **FR-008**: Docker images MUST be buildable on the VM from source code without requiring external image registries
- **FR-009**: Deployment process MUST include clear step-by-step instructions for initial setup on a fresh VM
- **FR-010**: Deployment process MUST include clear step-by-step instructions for building images and updating applications
- **FR-011**: System MUST provide mechanism to view application logs from running containers
- **FR-012**: System MUST handle backend API responses without CORS issues when frontend makes cross-origin requests

### Key Entities

- **Backend Container**: Runs Node.js Express server on internal port, exposes REST API with `/api` prefix and serves uploaded images with `/images` prefix, uses SQLite database (pets.db)
- **Frontend Container**: Serves React application static files or runs development server, makes HTTP requests to backend through nginx proxy
- **Nginx Container**: Acts as reverse proxy and single entry point, routes traffic based on URL path, exposes port 80/443 to external network
- **Docker Network**: Internal network connecting all containers, allows containers to communicate using service names
- **Docker Volumes**: Persistent storage for SQLite database file (pets.db) and uploaded image files that must survive container recreation and updates

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: DevOps engineer can perform initial deployment on a fresh VM in under 30 minutes by following provided documentation
- **SC-002**: Applications remain accessible through a single hostname with correct routing for 99.9% of requests (backend for `/api/*` and `/images/*`, frontend for others)
- **SC-003**: DevOps engineer can build and update either application independently in under 15 minutes (including image build time)
- **SC-004**: SQLite database and uploaded images persist across 100% of container updates and restarts with zero data loss
- **SC-005**: System successfully recovers from individual container failures within 2 minutes using Docker restart policies
- **SC-006**: Application logs are accessible within 5 seconds using standard Docker commands
- **SC-007**: Frontend successfully communicates with backend API without CORS errors in 100% of legitimate requests

## Assumptions

- Docker and docker-compose are already installed on the VM (or installation instructions are readily available)
- VM has sufficient resources (CPU, RAM, disk space) to run both applications simultaneously and build Docker images
- SSH access to VM is already configured and secured
- Domain name or IP address for accessing applications is already available and DNS is configured
- HTTPS/SSL certificates will be addressed in a future enhancement (starting with HTTP is acceptable)
- Manual deployment is acceptable for initial version (CI/CD pipeline is future work)
- Standard Docker networking and volume configuration is sufficient for application needs
- Backend API is designed to work behind a reverse proxy with path prefix
- Backend uses SQLite database (pets.db) which is suitable for containerization
- Docker images will be built locally on the VM from source code (no external image registry required)
- Application downtime during updates is acceptable (no need for zero-downtime deployments)
- No sensitive secrets need to be managed (environment variables use default/non-sensitive values)
