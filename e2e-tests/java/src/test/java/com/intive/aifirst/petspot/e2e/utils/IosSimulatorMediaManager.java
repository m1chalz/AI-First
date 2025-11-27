package com.intive.aifirst.petspot.e2e.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Base64;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Utility responsible for preparing deterministic media assets inside the iOS simulator.
 *
 * <p>The animal photo flow relies on SwiftUI PhotosPicker which can only select images that
 * already exist in the simulator library. To keep E2E tests deterministic we inject a tiny PNG
 * fixture into the currently booted simulator before scenarios tagged with {@code @missingPetPhoto}.
 *
 * <p>Implementation details:
 * <ul>
 *   <li>Only runs on macOS (required for {@code xcrun simctl})</li>
 *   <li>Writes an embedded Base64 PNG to {@code target/simulator-media/missing-pet-photo.png}</li>
 *   <li>Executes {@code xcrun simctl addmedia booted <fixture>} once per test session</li>
 * </ul>
 */
public final class IosSimulatorMediaManager {

    private static final String FIXTURE_FILE_NAME = "missing-pet-photo.png";
    private static final String OUTPUT_DIRECTORY = "target/simulator-media";
    private static final AtomicBoolean MEDIA_IMPORTED = new AtomicBoolean(false);
    private static final Set<String> REQUIRED_TAGS = Set.of("@ios", "@missingPetPhoto");

    /**
     * Simple 1x1 PNG (peach color) encoded as Base64 to avoid committing binary artifacts.
     */
    private static final String SAMPLE_PHOTO_BASE64 =
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAucB9W9hUZ0AAAAASUVORK5CYII=";

    private IosSimulatorMediaManager() {
        // Utility class
    }

    /**
     * Ensures the sample photo is present in the simulator library for the provided scenario tags.
     *
     * @param scenarioTags Cucumber scenario tags (e.g., @ios, @missingPetPhoto)
     */
    public static void ensurePhotoLibrarySeeded(Set<String> scenarioTags) {
        if (!shouldSeedForScenario(scenarioTags)) {
            return;
        }

        if (MEDIA_IMPORTED.get()) {
            return;
        }

        synchronized (IosSimulatorMediaManager.class) {
            if (MEDIA_IMPORTED.get()) {
                return;
            }

            try {
                Path fixturePath = writeFixtureToDisk();
                addMediaToSimulator(fixturePath);
                MEDIA_IMPORTED.set(true);
            } catch (IOException | InterruptedException ex) {
                throw new IllegalStateException("Failed to prepare simulator photo fixture", ex);
            }
        }
    }

    private static boolean shouldSeedForScenario(Set<String> scenarioTags) {
        if (!isMacOs()) {
            System.out.println("[IosSimulatorMediaManager] Skipping photo injection (non-macOS host).");
            return false;
        }
        return scenarioTags.containsAll(REQUIRED_TAGS);
    }

    private static boolean isMacOs() {
        String osName = System.getProperty("os.name", "").toLowerCase();
        return osName.contains("mac");
    }

    private static Path writeFixtureToDisk() throws IOException {
        Path outputDir = Paths.get(System.getProperty("user.dir"))
                .resolve(OUTPUT_DIRECTORY);
        Files.createDirectories(outputDir);

        Path fixturePath = outputDir.resolve(FIXTURE_FILE_NAME);
        if (!Files.exists(fixturePath)) {
            byte[] bytes = Base64.getDecoder().decode(SAMPLE_PHOTO_BASE64.getBytes(StandardCharsets.UTF_8));
            Files.write(fixturePath, bytes);
            System.out.println("[IosSimulatorMediaManager] Created simulator media fixture at " + fixturePath);
        }
        return fixturePath;
    }

    private static void addMediaToSimulator(Path fixturePath) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(
                "xcrun",
                "simctl",
                "addmedia",
                "booted",
                fixturePath.toAbsolutePath().toString()
        );
        builder.redirectErrorStream(true);

        Process process = builder.start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            String message = "[IosSimulatorMediaManager] simctl addmedia failed (exit=" + exitCode + "): " + output;
            throw new IllegalStateException(message);
        }

        System.out.println("[IosSimulatorMediaManager] Seeded iOS simulator photo library at "
                + Instant.now() + ". Output: " + output.trim());
    }
}

