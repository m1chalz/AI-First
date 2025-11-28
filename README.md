# PetSpot - Multi-Platform Application

This is a multi-platform project with independent implementations for Android, iOS, and Web, backed by a Node.js REST API.

## Architecture Overview

Each platform implements its own full stack independently:
- **Android**: Kotlin with Jetpack Compose (MVI architecture)
- **iOS**: Swift with SwiftUI (MVVM-C architecture)
- **Web**: TypeScript with React
- **Backend**: Node.js with Express (REST API for all platforms)

**No shared compiled code** between platforms - each platform maintains its own domain models, business logic, and presentation layer.

## Project Structure

### `/composeApp` - Android Application (Full Stack)

Android application implementing the complete technology stack in Kotlin:
- **Domain layer**: Models, use cases, repository interfaces
- **Data layer**: Repository implementations, API clients, database
- **Presentation layer**: ViewModels with MVI architecture
- **UI layer**: Jetpack Compose screens
- **Dependency Injection**: Koin (mandatory)

**MVI Architecture** (Model-View-Intent):
- Single `StateFlow<UiState>` source of truth with immutable data classes
- Sealed `UserIntent` for all user interactions
- Pure reducer functions for state transitions
- `SharedFlow<UiEffect>` for one-off events (navigation, snackbars)
- Koin for dependency injection (ViewModels, repositories, use cases)

### `/iosApp` - iOS Application (Full Stack)

iOS application implementing the complete technology stack in Swift:
- **Domain layer**: Models, repository protocols
- **Data layer**: Repository implementations, HTTP clients
- **Presentation layer**: ViewModels (ObservableObject with @Published properties) - call repositories directly
- **Coordinators**: UIKit-based navigation management
- **UI layer**: SwiftUI views wrapped in UIHostingController
- **Dependency Injection**: Manual DI with constructor injection (NO use cases layer)

**MVVM-C Architecture** (Model-View-ViewModel-Coordinator):
- UIKit coordinators manage navigation flow
- ViewModels call repositories directly (NO use cases)
- ViewModels contain presentation logic with @Published state
- SwiftUI views observe ViewModels (no business/navigation logic in views)
- Coordinators create views and inject repositories via manual DI

See [iosApp/README.md](./iosApp/README.md) for iOS architecture details.

### `/webApp` - Web Application (Full Stack)

React application implementing the complete technology stack in TypeScript:
- **Domain layer**: TypeScript models and interfaces
- **Service layer**: HTTP services consuming backend API
- **State management**: React hooks and Context
- **UI layer**: React components

The web app operates independently with native TypeScript patterns - no Kotlin/JS dependencies.

### `/server` - Backend API (Node.js/Express)

Standalone Node.js backend providing REST API endpoints for all platform clients:
- **REST API**: Express routers with endpoint definitions
- **Business logic**: Service layer (testable, pure functions)
- **Database**: Knex query builder + SQLite (designed for PostgreSQL migration)
- **Testing**: Vitest (unit) + SuperTest (integration)
- **Code quality**: ESLint with TypeScript, 80% test coverage requirement

**NOT part of any platform** - backend is consumed via HTTP by Android, iOS, and Web.

See [server/README.md](./server/README.md) for backend-specific documentation.

## Building and Running

### Android Application

Build and run the Android app from the terminal:

**macOS/Linux:**
```shell
./gradlew :composeApp:assembleDebug
```

**Windows:**
```shell
.\gradlew.bat :composeApp:assembleDebug
```

Or use the run configuration from your IDE's toolbar.

### iOS Application

Open the `/iosApp` directory in Xcode and run it from there, or use the run configuration from your IDE's toolbar.

### Web Application

1. Install [Node.js](https://nodejs.org/en/download) (includes `npm`)
2. Navigate to webApp directory and install dependencies:
   ```shell
   cd webApp
   npm install
   ```
3. Run the development server:
   ```shell
   npm run start
   ```

### Backend Server

The backend server provides REST API endpoints for all platform clients.

1. Navigate to the server directory:
   ```shell
   cd server
   ```

2. Install dependencies (first time only):
   ```shell
   npm install
   ```

3. Run the development server with hot reload:
   ```shell
   npm run dev
   ```
   
   The server will be active on [http://localhost:3000](http://localhost:3000)

4. Run tests:
   ```shell
   npm test
   ```

5. Run tests with coverage:
   ```shell
   npm test -- --coverage
   ```

See [server/README.md](./server/README.md) for more backend commands and documentation.

### Docker Deployment

Deploy PetSpot on a VM using Docker and docker-compose:

**Quick Start** (5 steps, ~30 minutes):
```bash
cd deployment
./scripts/deploy.sh
```

**Key Features:**
- ✅ Automated deployment script
- ✅ Nginx reverse proxy routing
- ✅ SQLite database persistence
- ✅ Independent backend/frontend updates
- ✅ Image tagging for traceability and rollback

**Common Operations:**
```bash
# Update backend only
./scripts/update.sh --backend

# Update frontend only
./scripts/update.sh --frontend

# View logs
./scripts/logs.sh --service backend --follow

# Build Docker images
./scripts/build.sh
```

For complete deployment guide, see:
- [DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md) - Quick reference
- [deployment/README.md](./deployment/README.md) - Full documentation
- [specs/030-docker-deployment/](./specs/030-docker-deployment/) - Technical specifications

### Static Analysis

This project uses automated static analysis tools (Detekt, ktlint, and Android Lint) to ensure consistent code quality across the Kotlin codebase (shared module and Android platform).

#### Quick Setup

1. Install the git pre-commit hook (one-time setup):
   ```shell
   ./scripts/install-hooks.sh
   ```

2. The hook will automatically run on every commit, checking:
   - **Detekt**: Kotlin code quality (complexity, code smells, potential bugs)
   - **ktlint**: Kotlin code formatting and style
   - **Android Lint**: Android-specific issues (API usage, resources, Compose)

#### Tools & Commands

- **Run all checks manually**:
  ```shell
  ./gradlew detekt ktlintCheck :composeApp:lintDebug
  ```

- **Auto-fix formatting issues**:
  ```shell
  ./gradlew ktlintFormat
  ```

- **Full codebase analysis** (for baseline assessment):
  ```shell
  ./scripts/analyze-baseline.sh
  ```

- **Track violation progress**:
  ```shell
  ./scripts/track-violations.sh
  ```

- **Emergency bypass** (use sparingly):
  ```shell
  git commit --no-verify -m "Your message"
  ```

For detailed setup instructions, troubleshooting, and best practices, see [docs/static-analysis-setup.md](./docs/static-analysis-setup.md).

---

## Testing

Each platform maintains its own test suites with 80% coverage requirement:

**Android** (JUnit + Turbine):
```shell
./gradlew :composeApp:testDebugUnitTest koverHtmlReport
```

**iOS** (XCTest):
```shell
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES
```

**Web** (Vitest + React Testing Library):
```shell
cd webApp
npm test -- --coverage
```

**Backend** (Vitest + SuperTest):
```shell
cd server
npm test -- --coverage
```

**End-to-End Tests** (Playwright + Appium):
```shell
# Web E2E tests
npx playwright test

# Mobile E2E tests (Android)
npm run test:mobile:android

# Mobile E2E tests (iOS)
npm run test:mobile:ios
```

## Architecture Principles

This project follows strict architectural principles defined in `.specify/memory/constitution.md`:

1. **Platform Independence**: Each platform implements full stack independently (no shared compiled code)
2. **80% Test Coverage**: Unit tests for all domain logic and ViewModels per platform
3. **Interface-Based Design**: Repository interfaces per platform for testability
4. **Dependency Injection**: 
   - Android: Koin (mandatory)
   - iOS: Manual DI with constructor injection (mandatory)
   - Web: React Context (recommended)
5. **Asynchronous Programming**: Platform-native async patterns (Kotlin Coroutines, Swift Concurrency, JS async/await)
6. **Test Identifiers**: All interactive UI elements have stable test IDs for E2E testing
7. **Given-When-Then Tests**: All tests follow Given-When-Then structure
8. **Android MVI Architecture**: Mandatory for Android Compose screens (with use cases)
9. **iOS MVVM-C Architecture**: Mandatory for iOS SwiftUI screens (ViewModels call repositories directly - NO use cases)
10. **Backend Quality Standards**: TDD workflow, Clean Code principles, ESLint enforcement

## Learn More

- [Kotlin for Android](https://kotlinlang.org/docs/android-overview.html)
- [Swift and SwiftUI](https://developer.apple.com/swift/)
- [React with TypeScript](https://react.dev/learn/typescript)
- [Node.js and Express](https://expressjs.com/)
