This is a Kotlin Multiplatform project targeting Android, iOS, Web, with a standalone Node.js backend.

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./composeApp/src/commonMain/kotlin) is for code that's common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple's CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
    folder is the appropriate location.

* [/iosApp](./iosApp/iosApp) contains iOS applications using **MVVM-C architecture** (UIKit coordinators + SwiftUI views).
  The app uses coordinator pattern for navigation management with UIHostingController wrapping SwiftUI views.
  See [iosApp/README.md](./iosApp/README.md) for architecture details.

* [/shared](./shared/src) is for the code that will be shared between all targets in the project.
  The most important subfolder is [commonMain](./shared/src/commonMain/kotlin). If preferred, you
  can add code to the platform-specific folders here too.

* [/webApp](./webApp) contains web React application. It uses the Kotlin/JS library produced
  by the [shared](./shared) module.

* [/server](./server) contains the Node.js/Express backend API. This is a standalone TypeScript
  service (NOT part of KMP) that provides REST API endpoints consumed by all platform clients
  (Android, iOS, Web). See [server/README.md](./server/README.md) for backend-specific documentation.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### Build and Run Web Application

To build and run the development version of the web app, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:
1. Install [Node.js](https://nodejs.org/en/download) (which includes `npm`)
2. Build Kotlin/JS shared code:
   - on macOS/Linux
     ```shell
     ./gradlew :shared:jsBrowserDevelopmentLibraryDistribution
     ```
   - on Windows
     ```shell
     .\gradlew.bat :shared:jsBrowserDevelopmentLibraryDistribution
     ```
3. Build and run the web application
   ```shell
   npm install
   npm run start
   ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE's toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

### Build and Run Backend Server

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

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
