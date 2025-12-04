# Implementation Plan: Docker-Based Deployment with Nginx Reverse Proxy

**Branch**: `030-docker-deployment` | **Date**: 2025-11-28 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/030-docker-deployment/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Deploy backend (`/server`) and frontend (`/webApp`) applications on a VM using Docker, docker-compose, and nginx reverse proxy. The system will:
- Build Docker images locally on the VM from Git-deployed source code
- Use nginx container as reverse proxy routing `/api/*` and `/images/*` to backend (port 3000), all other requests to frontend (port 8080)
- Persist SQLite database (`/var/lib/petspot/db`) and uploaded images (`/var/lib/petspot/images`) using bind mounts
- Provide shell scripts (deploy.sh, update.sh, build.sh) for deployment automation
- Support independent updates of backend or frontend with acceptable downtime

## Technical Context

**Language/Version**: 
- Backend: Node.js v24 (LTS) + TypeScript
- Frontend: React + TypeScript + Vite
- Shell Scripts: Bash (for deployment automation)

**Primary Dependencies**: 
- Docker (containerization platform)
- docker-compose (container orchestration)
- nginx (official Docker image for reverse proxy)
- Node.js base images (for backend and frontend builds)

**Storage**: 
- SQLite database file (`server/pets.db`) persisted to `/var/lib/petspot/db` via bind mount
- Uploaded images (`server/public/images/`) persisted to `/var/lib/petspot/images` via bind mount

**Testing**: 
- Manual testing of deployment scripts on fresh VM
- Verification of container startup and routing behavior
- Data persistence testing across container recreation

**Target Platform**: 
- Linux VM (Ubuntu/Debian recommended) with Docker and docker-compose installed
- HTTP port 80 exposed to external network
- SSH access for deployment operations

**Project Type**: Infrastructure/DevOps (deployment configuration for existing web + backend applications)

**Performance Goals**: 
- Initial deployment completes in under 30 minutes
- Application updates complete in under 15 minutes (including image build)
- Container recovery from failures within 2 minutes

**Constraints**: 
- No external Docker registry (all images built locally on VM)
- Acceptable downtime during updates (no zero-downtime requirement)
- HTTP only initially (HTTPS/SSL deferred to future work)
- Manual deployment (no CI/CD pipeline)

**Scale/Scope**: 
- 3 Docker containers (nginx, backend, frontend)
- Small-scale deployment (single VM, non-clustered)
- SQLite database suitable for initial phase

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is a backend-only/infrastructure feature affecting deployment configuration, NOT application code. Most platform-specific checks (Android MVI, iOS MVVM-C, E2E tests for mobile/web) are marked N/A. Focus is on Backend Architecture & Quality Standards for deployment scripts and configuration files.

### Platform Architecture Compliance

- [x] **Platform Independence**: N/A - No application code changes, only deployment infrastructure
  - This feature adds deployment configuration (Dockerfiles, docker-compose.yml, nginx.conf)
  - Does not modify Android, iOS, or Web application code
  - Does not add new backend business logic or API endpoints
  - Violation justification: N/A (infrastructure-only feature)

- [x] **Android MVI Architecture**: N/A - No Android code changes in this feature
  - Violation justification: N/A (no Android application changes)

- [x] **iOS MVVM-C Architecture**: N/A - No iOS code changes in this feature
  - Violation justification: N/A (no iOS application changes)

- [x] **Interface-Based Design**: N/A - No domain logic or repositories added
  - Violation justification: N/A (deployment configuration only)

- [x] **Dependency Injection**: N/A - No dependency injection setup required for deployment scripts
  - Violation justification: N/A (shell scripts and Docker configuration don't use DI)

- [x] **80% Test Coverage - Platform-Specific**: Partially applicable
  - No unit tests required for Docker configuration files (declarative YAML/conf)
  - Shell scripts are tested manually through deployment verification
  - Documentation will include manual testing procedures
  - Violation justification: Infrastructure scripts tested via manual deployment verification, not unit tests

- [x] **End-to-End Tests**: N/A - No new user-facing features requiring E2E tests
  - Deployment process verified manually according to documentation
  - Existing E2E tests continue to run against deployed applications
  - Violation justification: N/A (infrastructure deployment, not user-facing features)

- [x] **Asynchronous Programming Standards**: N/A - No async application code in this feature
  - Violation justification: N/A (deployment configuration only)

- [x] **Test Identifiers for UI Controls**: N/A - No UI elements added
  - Violation justification: N/A (no user interface changes)

- [x] **Public API Documentation**: Applicable for shell scripts
  - Shell scripts MUST include inline comments explaining each step
  - README.md MUST document deployment procedures with examples
  - nginx.conf MUST include comments explaining routing logic
  - docker-compose.yml MUST include comments for service configuration
  - Violation justification: N/A (will provide comprehensive documentation)

- [x] **Given-When-Then Test Structure**: N/A - No automated tests for deployment scripts
  - Manual testing procedures will follow Given-When-Then format in documentation
  - Violation justification: N/A (manual testing procedures documented)

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: Compliant - Using existing Node.js v24 + Express + TypeScript backend
  - Runtime: Node.js v24 (LTS) - already in use
  - Framework: Express.js - already in use
  - Language: TypeScript with strict mode - already in use
  - Database: SQLite with Knex - already in use
  - Violation justification: N/A (compliant with existing stack)

- [x] **Backend Code Quality**: N/A - No backend application code changes
  - Only adding Dockerfile for backend (declarative configuration)
  - Violation justification: N/A (no new backend logic, only Docker configuration)

- [x] **Backend Dependency Management**: N/A - No new npm dependencies added to backend
  - Dockerfile will reference existing package.json
  - Violation justification: N/A (no dependency changes)

- [x] **Backend Directory Structure**: Compliant - No changes to `/server/src/` structure
  - Adding Dockerfile at `/server/Dockerfile` (standard Docker practice)
  - Violation justification: N/A (compliant)

- [x] **Backend TDD Workflow**: N/A - No backend business logic added
  - Violation justification: N/A (deployment configuration only)

- [x] **Backend Testing Strategy**: N/A - No backend logic requiring tests
  - Existing backend tests continue to run unchanged
  - Violation justification: N/A (no new backend logic)

## Project Structure

### Documentation (this feature)

```text
specs/030-docker-deployment/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output - Docker/nginx best practices
├── data-model.md        # Phase 1 output - Deployment configuration structure
├── quickstart.md        # Phase 1 output - Quick deployment guide
├── contracts/           # Phase 1 output - Configuration file templates
│   ├── Dockerfile.backend
│   ├── Dockerfile.frontend
│   ├── docker-compose.yml
│   └── nginx.conf
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
AI-First/
├── server/
│   ├── Dockerfile                    # NEW - Backend Docker image build
│   ├── .dockerignore                 # NEW - Exclude node_modules, coverage from image
│   ├── src/                          # EXISTING - Backend source code
│   ├── pets.db                       # EXISTING - SQLite database (to be mounted)
│   └── public/images/                # EXISTING - Uploaded images directory (to be mounted)
│
├── webApp/
│   ├── Dockerfile                    # NEW - Frontend Docker image build (multi-stage)
│   ├── .dockerignore                 # NEW - Exclude node_modules from image
│   ├── nginx.conf                    # NEW - Frontend nginx configuration (serves static files)
│   └── src/                          # EXISTING - Frontend source code
│
├── deployment/                        # NEW - Deployment configuration directory
│   ├── docker-compose.yml            # NEW - Orchestrates nginx, backend, frontend containers
│   ├── nginx/
│   │   └── nginx.conf                # NEW - Main nginx reverse proxy configuration
│   ├── scripts/
│   │   ├── deploy.sh                 # NEW - Initial deployment script
│   │   ├── update.sh                 # NEW - Update applications script
│   │   ├── build.sh                  # NEW - Build Docker images script
│   │   └── logs.sh                   # NEW - View container logs script
│   ├── .env.example                  # NEW - Environment variables template
│   └── README.md                     # NEW - Deployment documentation
│
└── .gitignore                        # UPDATED - Add deployment/.env (secrets)
```

**Structure Decision**: 
- Created new `/deployment` directory to contain all Docker and deployment-related files
- Dockerfiles placed in respective application directories (`/server`, `/webApp`)
- docker-compose.yml and nginx configuration centralized in `/deployment`
- Shell scripts organized in `/deployment/scripts` for easy discovery
- This structure separates deployment concerns from application code while keeping related files together

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

No violations requiring justification. This feature is infrastructure-only and does not introduce architectural complexity to the application codebase.
