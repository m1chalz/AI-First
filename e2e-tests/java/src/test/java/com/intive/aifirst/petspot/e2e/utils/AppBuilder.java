package com.intive.aifirst.petspot.e2e.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Automatically builds and prepares mobile apps for E2E testing.
 * 
 * <p>This class handles:
 * <ul>
 *   <li>Detecting which platform needs to be built (iOS, Android, or both)</li>
 *   <li>Uninstalling old app from device/simulator</li>
 *   <li>Building fresh app (iOS and Android can build in parallel)</li>
 *   <li>Copying built app to e2e-tests/java/apps/ directory</li>
 * </ul>
 * 
 * <p><strong>Auto-Build Feature:</strong> Apps are built automatically before first test.
 * Set {@code -Dskip.app.build=true} to skip building (use existing apps).
 * 
 * <h2>Usage:</h2>
 * <pre>{@code
 * // In Hooks @Before
 * AppBuilder.ensureAppsBuilt("iOS");  // Builds only iOS app
 * AppBuilder.ensureAppsBuilt("Android");  // Builds only Android app
 * }</pre>
 * 
 * @see AppiumDriverManager
 */
public class AppBuilder {
    
    /** Project root directory */
    private static final String PROJECT_ROOT = System.getProperty("user.dir").replace("/e2e-tests/java", "");
    
    /** iOS app output path */
    private static final String IOS_BUILD_PATH = PROJECT_ROOT + "/iosApp/build/Build/Products/Debug-iphonesimulator/PetSpot.app";
    
    /** Android app output path */
    private static final String ANDROID_BUILD_PATH = PROJECT_ROOT + "/composeApp/build/outputs/apk/debug/composeApp-debug.apk";
    
    /** E2E apps directory */
    private static final String APPS_DIR = PROJECT_ROOT + "/e2e-tests/java/apps";
    
    /** iOS app destination */
    private static final String IOS_APP_DEST = APPS_DIR + "/petspot-ios.app";
    
    /** Android app destination */
    private static final String ANDROID_APP_DEST = APPS_DIR + "/petspot-android.apk";
    
    /** iOS bundle ID */
    private static final String IOS_BUNDLE_ID = "com.intive.aifirst.petspot.PetSpot";
    
    /** Android package name */
    private static final String ANDROID_PACKAGE = "com.intive.aifirst.petspot";
    
    /** Singleton flag to ensure apps are built only once per test run */
    private static volatile boolean initialized = false;
    
    /** Lock object for thread-safe initialization */
    private static final Object lock = new Object();
    
    /**
     * Ensures apps are built and ready for testing.
     * Thread-safe singleton - builds only once per test run.
     * 
     * @param platform Platform to build: "iOS", "Android", or null for both
     */
    public static void ensureAppsBuilt(String platform) {
        // Quick check without synchronization
        if (initialized) {
            return;
        }
        
        // Check if build should be skipped
        if (Boolean.getBoolean("skip.app.build")) {
            System.out.println("⚠️  App build skipped (skip.app.build=true)");
            initialized = true;
            return;
        }
        
        // Thread-safe initialization
        synchronized (lock) {
            if (initialized) {
                return;
            }
            
            System.out.println("\n========================================");
            System.out.println("📱 PREPARING APPS FOR E2E TESTS");
            System.out.println("========================================");
            
            try {
                boolean buildIos = platform == null || "iOS".equalsIgnoreCase(platform);
                boolean buildAndroid = platform == null || "Android".equalsIgnoreCase(platform);
                
                // Build apps in parallel
                List<CompletableFuture<Void>> buildTasks = new ArrayList<>();
                ExecutorService executor = Executors.newFixedThreadPool(2);
                
                if (buildIos) {
                    buildTasks.add(CompletableFuture.runAsync(() -> {
                        uninstallIosApp();
                        buildIosApp();
                        copyIosApp();
                    }, executor));
                }
                
                if (buildAndroid) {
                    buildTasks.add(CompletableFuture.runAsync(() -> {
                        uninstallAndroidApp();
                        buildAndroidApp();
                        copyAndroidApp();
                    }, executor));
                }
                
                // Wait for all builds to complete
                CompletableFuture.allOf(buildTasks.toArray(new CompletableFuture[0])).get();
                executor.shutdown();
                executor.awaitTermination(1, TimeUnit.MINUTES);
                
                System.out.println("========================================");
                System.out.println("✅ APPS READY FOR TESTING");
                System.out.println("========================================\n");
                
                initialized = true;
                
            } catch (Exception e) {
                throw new RuntimeException("Failed to prepare apps: " + e.getMessage(), e);
            }
        }
    }
    
    /**
     * Uninstalls iOS app from all booted simulators.
     */
    private static void uninstallIosApp() {
        System.out.println("🗑️  Uninstalling old iOS app from simulators...");
        try {
            runCommand(new String[]{
                "xcrun", "simctl", "uninstall", "booted", IOS_BUNDLE_ID
            }, false);
            System.out.println("✅ iOS app uninstalled");
        } catch (Exception e) {
            System.out.println("⚠️  iOS app not installed or already removed: " + e.getMessage());
        }
    }
    
    /**
     * Uninstalls Android app from all connected emulators/devices.
     */
    private static void uninstallAndroidApp() {
        System.out.println("🗑️  Uninstalling old Android app from emulators...");
        try {
            runCommand(new String[]{
                "adb", "uninstall", ANDROID_PACKAGE
            }, false);
            System.out.println("✅ Android app uninstalled");
        } catch (Exception e) {
            System.out.println("⚠️  Android app not installed or already removed: " + e.getMessage());
        }
    }
    
    /**
     * Builds iOS app for simulator using xcodebuild.
     */
    private static void buildIosApp() {
        System.out.println("🔨 Building iOS app...");
        try {
            runCommand(new String[]{
                "xcodebuild",
                "-project", PROJECT_ROOT + "/iosApp/iosApp.xcodeproj",
                "-scheme", "iosApp",
                "-sdk", "iphonesimulator",
                "-configuration", "Debug",
                "-derivedDataPath", PROJECT_ROOT + "/iosApp/build",
                "clean", "build"
            }, true);
            System.out.println("✅ iOS app built successfully");
        } catch (Exception e) {
            throw new RuntimeException("iOS build failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Builds Android app using Gradle.
     */
    private static void buildAndroidApp() {
        System.out.println("🔨 Building Android app...");
        try {
            runCommand(new String[]{
                PROJECT_ROOT + "/gradlew",
                ":composeApp:assembleDebug",
                "--quiet"
            }, true);
            System.out.println("✅ Android app built successfully");
        } catch (Exception e) {
            throw new RuntimeException("Android build failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Copies built iOS app to e2e-tests apps directory.
     */
    private static void copyIosApp() {
        System.out.println("📦 Copying iOS app to apps directory...");
        try {
            Path source = Paths.get(IOS_BUILD_PATH);
            Path dest = Paths.get(IOS_APP_DEST);
            
            // Delete old app if exists
            if (Files.exists(dest)) {
                deleteDirectory(dest.toFile());
            }
            
            // Copy new app
            copyDirectory(source.toFile(), dest.toFile());
            System.out.println("✅ iOS app copied to: " + IOS_APP_DEST);
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy iOS app: " + e.getMessage(), e);
        }
    }
    
    /**
     * Copies built Android APK to e2e-tests apps directory.
     */
    private static void copyAndroidApp() {
        System.out.println("📦 Copying Android APK to apps directory...");
        try {
            Path source = Paths.get(ANDROID_BUILD_PATH);
            Path dest = Paths.get(ANDROID_APP_DEST);
            
            Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("✅ Android APK copied to: " + ANDROID_APP_DEST);
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy Android APK: " + e.getMessage(), e);
        }
    }
    
    /**
     * Runs a shell command and waits for completion.
     * 
     * @param command Command array (e.g., ["xcodebuild", "-scheme", "iosApp"])
     * @param printOutput Whether to print command output to console
     * @throws Exception if command fails
     */
    private static void runCommand(String[] command, boolean printOutput) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(new File(PROJECT_ROOT));
        pb.redirectErrorStream(true);
        
        Process process = pb.start();
        
        if (printOutput) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
        }
        
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Command failed with exit code " + exitCode);
        }
    }
    
    /**
     * Recursively copies a directory.
     */
    private static void copyDirectory(File source, File dest) throws IOException {
        if (source.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdirs();
            }
            
            String[] files = source.list();
            if (files != null) {
                for (String file : files) {
                    copyDirectory(new File(source, file), new File(dest, file));
                }
            }
        } else {
            Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
    
    /**
     * Recursively deletes a directory.
     */
    private static void deleteDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        dir.delete();
    }
}



