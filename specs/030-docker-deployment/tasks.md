# Tasks: Docker-Based Deployment with Nginx Reverse Proxy

**Input**: Design documents from `/specs/030-docker-deployment/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/

**Tests**: This is an infrastructure/deployment feature - testing is performed manually via deployment verification procedures documented in quickstart.md. No automated unit tests required for Docker configuration files and shell scripts.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each deployment capability.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

This feature creates deployment infrastructure in `/deployment` directory and adds Dockerfiles to `/server` and `/webApp` directories.

---

## Phase 1: Setup (Project Structure)

**Purpose**: Create directory structure and copy configuration templates

- [x] T001 Create `/deployment` directory in repository root
- [x] T002 [P] Create `/deployment/nginx` subdirectory for nginx reverse proxy config
- [x] T003 [P] Create `/deployment/scripts` subdirectory for shell scripts
- [x] T004 [P] Create `.dockerignore` file in `/server` directory
- [x] T005 [P] Create `.dockerignore` file in `/webApp` directory
- [x] T006 [P] Copy `/specs/030-docker-deployment/contracts/docker-compose.yml` to `/deployment/docker-compose.yml`
- [x] T007 [P] Copy `/specs/030-docker-deployment/contracts/nginx.conf` to `/deployment/nginx/nginx.conf`
- [x] T008 [P] Create `.env.example` file in `/deployment` directory with environment variable template

---

## Phase 2: Foundational (Core Configuration Files)

**Purpose**: Docker images and core configuration that ALL user stories depend on

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T009 [P] Copy `/specs/030-docker-deployment/contracts/Dockerfile.backend` to `/server/Dockerfile`
- [x] T010 [P] Copy `/specs/030-docker-deployment/contracts/Dockerfile.frontend` to `/webApp/Dockerfile`
- [x] T011 [P] Create `/webApp/nginx.conf` for frontend static file serving (listens on port 8080)
- [x] T012 Verify backend Dockerfile CMD uses `npm start` (includes --experimental-transform-types flag)
- [x] T013 Verify docker-compose.yml uses `${IMAGE_TAG:-latest}` for backend and frontend images
- [x] T014 Verify nginx.conf has simplified proxy_pass directives (no header preservation)
- [x] T015 Update `/server/.dockerignore` to exclude `node_modules/`, `.git/`, `coverage/`, `*.test.ts`, `__test__/`
- [x] T016 Update `/webApp/.dockerignore` to exclude `node_modules/`, `.git/`, `coverage/`, `dist/`, `*.test.ts`, `__test__/`
- [x] T017 Update `/.gitignore` to exclude `/deployment/.env` (secrets file)

**Checkpoint**: Core configuration files ready - shell script implementation can now begin

---

## Phase 3: User Story 1 - Initial Deployment Setup (Priority: P1) 🎯 MVP

**Goal**: DevOps engineer can perform initial deployment of both backend and frontend applications on a VM with nginx reverse proxy routing requests by URL path

**Independent Test**: 
1. Start with fresh VM (Docker installed, ports 80/443 free)
2. Run deployment script
3. Verify: `curl http://localhost` returns frontend
4. Verify: `curl http://localhost/api/pets` returns backend JSON
5. Verify: `curl http://localhost/images/test.jpg` serves image from backend
6. Success = all 3 containers running, routing works correctly

### Implementation for User Story 1

**Shell Scripts**:
- [x] T018 [P] [US1] Copy `/specs/030-docker-deployment/contracts/build.sh` to `/deployment/scripts/build.sh`
- [x] T019 [US1] Update build.sh to generate IMAGE_TAG as `(commit-hash)-(timestamp)` format
- [x] T020 [US1] Update build.sh to build backend image: `docker build -t petspot-backend:${IMAGE_TAG} -f ../server/Dockerfile ../server/`
- [x] T021 [US1] Update build.sh to build frontend image: `docker build -t petspot-frontend:${IMAGE_TAG} -f ../webApp/Dockerfile ../webApp/`
- [x] T022 [US1] Update build.sh to tag images as `latest` for convenience
- [x] T023 [US1] Update build.sh to export IMAGE_TAG environment variable for docker-compose
- [x] T024 [US1] Create `/deployment/scripts/deploy.sh` for initial deployment workflow
- [x] T025 [US1] Add prerequisite checks to deploy.sh (Docker, docker-compose, Git, ports 80/443 free)
- [x] T026 [US1] Add Git repository cloning step to deploy.sh (if running on fresh VM)
- [x] T027 [US1] Add persistent directory creation to deploy.sh (`mkdir -p /var/lib/petspot/db /var/lib/petspot/images`)
- [x] T028 [US1] Add permissions setup to deploy.sh (`chmod 755 /var/lib/petspot/*`, set ownership)
- [x] T029 [US1] Add .env file creation to deploy.sh (copy from .env.example, prompt user to edit)
- [x] T030 [US1] Add call to build.sh in deploy.sh
- [x] T031 [US1] Add container startup to deploy.sh (`docker compose -f deployment/docker-compose.yml up -d`)
- [x] T032 [US1] Add deployment verification to deploy.sh (health checks with curl, container status)
- [x] T033 [US1] Add color-coded output to deploy.sh (green=success, red=error, yellow=warning)
- [x] T034 [US1] Add error handling to deploy.sh (`set -euo pipefail`, exit codes for different failure types)
- [x] T035 [P] [US1] Make build.sh executable (`chmod +x /deployment/scripts/build.sh`)
- [x] T036 [P] [US1] Make deploy.sh executable (`chmod +x /deployment/scripts/deploy.sh`)

**Documentation**:
- [x] T037 [P] [US1] Create `/deployment/README.md` with complete deployment documentation
- [x] T038 [US1] Document prerequisites in README (Docker, docker-compose, Git, SSH, ports)
- [x] T039 [US1] Document initial deployment steps in README (5-step quick start)
- [x] T040 [US1] Document verification procedures in README (curl commands, health checks)
- [x] T041 [US1] Document troubleshooting common issues in README (port conflicts, permissions, container failures)
- [x] T042 [US1] Document data persistence paths in README (`/var/lib/petspot/db`, `/var/lib/petspot/images`)
- [x] T043 [US1] Document environment variables in .env.example with comments
- [x] T044 [US1] Add inline comments to docker-compose.yml explaining service configuration
- [x] T045 [US1] Add inline comments to nginx.conf explaining routing rules
- [x] T046 [US1] Add inline comments to build.sh explaining tagging strategy

**Manual Testing (User Story 1)**:
- [ ] T047 [US1] Test initial deployment on fresh VM following deploy.sh
- [ ] T048 [US1] Verify frontend accessible at `http://localhost`
- [ ] T049 [US1] Verify backend API accessible at `http://localhost/api/v1/announcements`
- [ ] T050 [US1] Verify images served at `http://localhost/images/*`
- [ ] T051 [US1] Verify all 3 containers running (`docker compose ps`)
- [ ] T052 [US1] Verify container logs accessible (`docker compose logs`)
- [ ] T053 [US1] Verify deployment completes in under 30 minutes

**Checkpoint**: At this point, User Story 1 should be fully functional - initial deployment works end-to-end

---

## Phase 4: User Story 2 - Application Updates (Priority: P2)

**Goal**: DevOps engineer can update either backend or frontend application independently with data persistence across container recreation

**Independent Test**:
1. Make code change to backend (e.g., modify API response)
2. Run update script for backend only
3. Verify: New backend version serves updated response
4. Verify: SQLite database data still intact
5. Verify: Uploaded images still accessible
6. Success = update completes in <15 min, data persists, frontend unaffected

### Implementation for User Story 2

**Shell Scripts**:
- [x] T054 [P] [US2] Create `/deployment/scripts/update.sh` for application update workflow
- [x] T055 [US2] Add argument parsing to update.sh (`--backend`, `--frontend`, `--all`)
- [x] T056 [US2] Add usage/help flag to update.sh (`--help`)
- [x] T057 [US2] Add Git pull step to update.sh (`git pull origin main`)
- [x] T058 [US2] Add selective image building to update.sh (build only specified services)
- [x] T059 [US2] Add container recreation to update.sh (`docker compose up -d --force-recreate <service>`)
- [x] T060 [US2] Add update verification to update.sh (health checks, logs tail)
- [x] T061 [US2] Add color-coded output to update.sh
- [x] T062 [US2] Add error handling to update.sh (`set -euo pipefail`, exit codes)
- [x] T063 [P] [US2] Make update.sh executable (`chmod +x /deployment/scripts/update.sh`)

**Documentation**:
- [x] T064 [US2] Document update procedures in README.md (update backend, update frontend, update both)
- [x] T065 [US2] Document Git workflow in README.md (git pull, build, recreate)
- [x] T066 [US2] Document data persistence verification in README.md (check database, check images)
- [x] T067 [US2] Document acceptable downtime during updates in README.md
- [x] T068 [US2] Add examples to README.md showing update.sh usage with different flags

**Manual Testing (User Story 2)**:
- [ ] T069 [US2] Test backend-only update (modify backend code, run update.sh --backend)
- [ ] T070 [US2] Verify updated backend code running
- [ ] T071 [US2] Verify SQLite database data persists after backend update
- [ ] T072 [US2] Verify uploaded images persist after backend update
- [ ] T073 [US2] Verify frontend continues running during backend update
- [ ] T074 [US2] Test frontend-only update (modify frontend code, run update.sh --frontend)
- [ ] T075 [US2] Verify updated frontend code running
- [ ] T076 [US2] Verify backend continues running during frontend update
- [ ] T077 [US2] Test full update (update.sh --all)
- [ ] T078 [US2] Verify update completes in under 15 minutes

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - Build and Deploy from Source (Priority: P3)

**Goal**: DevOps engineer can build Docker images directly on VM from source code with commit hash + timestamp tagging for traceability

**Independent Test**:
1. Make code change and commit to Git
2. Pull changes on VM
3. Run build.sh script
4. Verify: Images tagged with `(commit-hash)-(timestamp)` format
5. Verify: Images also tagged as `latest`
6. Verify: Can trace image back to specific commit
7. Success = build completes successfully, tags enable rollback

### Implementation for User Story 3

**Shell Scripts**:
- [x] T079 [US3] Verify build.sh generates commit hash with `git rev-parse --short HEAD`
- [x] T080 [US3] Verify build.sh generates timestamp with `date +%Y%m%d-%H%M%S`
- [x] T081 [US3] Verify build.sh combines into IMAGE_TAG format: `${COMMIT_HASH}-${TIMESTAMP}`
- [x] T082 [US3] Add output showing generated IMAGE_TAG to build.sh
- [x] T083 [US3] Add Docker build context validation to build.sh (check directories exist)
- [x] T084 [US3] Add build failure detection to build.sh (exit on first failure)
- [x] T085 [US3] Add image listing to build.sh output (show built images with tags)
- [x] T086 [US3] Add usage instructions to build.sh output (how to use tagged images)
- [x] T087 [P] [US3] Create `/deployment/scripts/logs.sh` for viewing container logs
- [x] T088 [US3] Add service selection to logs.sh (`--service <name>`, `--all`)
- [x] T089 [US3] Add follow flag to logs.sh (`--follow` for real-time logs)
- [x] T090 [US3] Add tail option to logs.sh (`--tail <n>` for last n lines)
- [x] T091 [US3] Add usage examples to logs.sh
- [x] T092 [P] [US3] Make logs.sh executable (`chmod +x /deployment/scripts/logs.sh`)

**Documentation**:
- [x] T093 [US3] Document build process in README.md (build.sh usage, image tagging)
- [x] T094 [US3] Document image tagging strategy in README.md (commit-hash)-(timestamp) format, benefits)
- [x] T095 [US3] Document log viewing in README.md (logs.sh usage with examples)
- [x] T096 [US3] Document rollback procedure in README.md (using previous image tags)
- [x] T097 [US3] Document build troubleshooting in README.md (disk space, build errors, dependencies)
- [x] T098 [US3] Add image management section to README.md (listing images, cleaning old images)
- [x] T099 [US3] Add examples showing full Git-to-deploy workflow in README.md

**Manual Testing (User Story 3)**:
- [ ] T100 [US3] Test building images from source (run build.sh)
- [ ] T101 [US3] Verify images tagged with correct format `(commit-hash)-(timestamp)`
- [ ] T102 [US3] Verify images also tagged as `latest`
- [ ] T103 [US3] Verify commit hash matches current Git HEAD
- [ ] T104 [US3] Verify timestamp is current
- [ ] T105 [US3] Test building with code changes (modify code, commit, pull, build)
- [ ] T106 [US3] Verify new IMAGE_TAG generated for new commit
- [ ] T107 [US3] Test logs.sh with different flags (--service backend, --follow, --tail 100)
- [ ] T108 [US3] Verify build completes without errors
- [ ] T109 [US3] Verify can trace image to source code via commit hash

**Checkpoint**: All user stories should now be independently functional - complete deployment pipeline works

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final improvements and comprehensive documentation

- [ ] T110 [P] Add project root README.md section documenting deployment directory
- [ ] T111 [P] Create DEPLOYMENT_GUIDE.md in project root linking to /deployment/README.md
- [ ] T112 [P] Add deployment quickstart to main project README.md
- [ ] T113 Validate all shell scripts have proper error handling and exit codes
- [ ] T114 Validate all shell scripts have usage/help output
- [ ] T115 Validate all configuration files have inline comments
- [ ] T116 Review and improve error messages in shell scripts
- [ ] T117 Add shell script header comments documenting purpose and usage
- [ ] T118 Verify .gitignore excludes /deployment/.env
- [ ] T119 Verify .env.example checked into Git (template only, no secrets)
- [ ] T120 Test complete deployment workflow following quickstart.md on clean VM
- [ ] T121 Document backup strategy for `/var/lib/petspot/` in README.md
- [ ] T122 Document HTTPS/SSL future enhancement in README.md
- [ ] T123 Document CI/CD future enhancement in README.md
- [ ] T124 Create deployment checklist in README.md (pre-flight checks)
- [ ] T125 Add common operations quick reference table to README.md

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-5)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3)
- **Polish (Phase 6)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Extends US1 but should work independently
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - Used by US1/US2 but should work independently

### Within Each User Story

- Shell scripts before documentation
- Core scripts before helper scripts
- build.sh before deploy.sh before update.sh
- Manual testing after implementation complete
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks (T001-T008) can run in parallel
- All Foundational tasks (T009-T017) can run in parallel
- Once Foundational phase completes, US2 and US3 script development can proceed in parallel while US1 is being documented/tested
- Documentation tasks within a story can run in parallel with script development (different files)

---

## Parallel Example: User Story 1

```bash
# These tasks can run in parallel (different files):
T018: Copy build.sh template
T024: Create deploy.sh file
T037: Create README.md file

# These must run sequentially (same file):
T024: Create deploy.sh
T025: Add prerequisite checks to deploy.sh
T026: Add Git cloning to deploy.sh
(continue building deploy.sh sequentially)
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (directory structure)
2. Complete Phase 2: Foundational (Docker/nginx config files)
3. Complete Phase 3: User Story 1 (deploy.sh + build.sh + documentation)
4. **STOP and VALIDATE**: Test initial deployment on fresh VM
5. Deploy to production if ready

### Incremental Delivery

1. Complete Setup + Foundational → Infrastructure ready
2. Add User Story 1 → Test independently → Deploy! (MVP - can deploy once)
3. Add User Story 2 → Test independently → Deploy! (can now update apps)
4. Add User Story 3 → Test independently → Deploy! (full pipeline with tagging)
5. Each story adds capability without breaking previous stories

### Single Developer Strategy

1. Complete Phases 1-2 (Setup + Foundational): ~2 hours
2. Complete Phase 3 (US1 - deploy.sh + build.sh + docs): ~4-5 hours
3. **Test US1 independently** → MVP working!
4. Complete Phase 4 (US2 - update.sh + docs): ~2-3 hours
5. **Test US2 independently** → Updates working!
6. Complete Phase 5 (US3 - logs.sh + tagging + docs): ~2-3 hours
7. **Test US3 independently** → Full pipeline working!
8. Complete Phase 6 (Polish): ~1-2 hours

**Total Estimated Time**: 12-16 hours for complete implementation

---

## Notes

- This is an infrastructure feature - no application code changes
- No unit tests required (configuration files tested via manual deployment)
- Testing is performed manually following procedures in quickstart.md
- Focus on clear documentation and robust error handling in shell scripts
- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Commit after each logical group of tasks
- Stop at any checkpoint to validate story independently
- Deploy scripts must be idempotent where possible (safe to run multiple times)

---

## Success Criteria Validation

After completing all tasks, verify:

- [ ] **US1 Success**: Initial deployment completes in <30 minutes, all containers running, routing works
- [ ] **US2 Success**: Updates complete in <15 minutes, data persists (database + images), independent updates work
- [ ] **US3 Success**: Images tagged with `(commit-hash)-(timestamp)`, builds succeed from source, traceability to Git commits
- [ ] **Overall Success**: All 3 user stories work independently, deployment is reproducible, documentation is complete

