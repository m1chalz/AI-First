package com.intive.aifirst.petspot.e2e.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * API Helper for creating and managing test data via backend API.
 * 
 * <p>This utility class provides methods to create, retrieve, and delete
 * announcements directly via the backend REST API. Tests should use this
 * to set up their own test data instead of relying on seed data.
 * 
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Create announcement
 * String id = TestDataApiHelper.createAnnouncement(Map.of(
 *     "petName", "TestDog",
 *     "species", "DOG",
 *     "locationLatitude", "51.1",
 *     "locationLongitude", "17.0"
 * ));
 * 
 * // ... run test ...
 * 
 * // Cleanup
 * TestDataApiHelper.deleteAnnouncement(id);
 * }</pre>
 * 
 * @see com.intive.aifirst.petspot.e2e.steps.CommonSteps
 */
public class TestDataApiHelper {
    
    private static final String BASE_URL = System.getProperty("api.base.url", "http://localhost:3000");
    private static final String API_PATH = "/api/v1/announcements";
    private static final String ADMIN_API_PATH = "/api/admin/v1/announcements";
    
    // Admin token for delete operations
    private static final String ADMIN_TOKEN = System.getProperty("admin.token", "tajnehasloadmina");
    
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();
    
    // Store created announcement IDs for cleanup
    private static final Map<String, String> createdAnnouncements = new HashMap<>();
    
    // Store management passwords for photo upload
    private static final Map<String, String> managementPasswords = new HashMap<>();
    
    /**
     * Creates a new announcement via API.
     * 
     * @param data Map containing announcement fields:
     *             - petName (optional): Pet name
     *             - species (required): DOG, CAT, BIRD, OTHER
     *             - breed (optional): Breed name
     *             - sex (required): MALE, FEMALE, UNKNOWN
     *             - age (optional): Age in years
     *             - description (optional): Description text
     *             - microchipNumber (optional): 15-digit chip number
     *             - locationLatitude (required): Latitude coordinate
     *             - locationLongitude (required): Longitude coordinate
     *             - email (optional): Contact email
     *             - phone (optional): Contact phone
     *             - lastSeenDate (optional): Date in YYYY-MM-DD format (defaults to today)
     *             - status (optional): MISSING or FOUND (defaults to MISSING)
     *             - reward (optional): Reward amount
     * @return Announcement ID (UUID)
     * @throws RuntimeException if API call fails
     */
    public static String createAnnouncement(Map<String, String> data) {
        try {
            // Build request body with defaults
            Map<String, Object> body = new HashMap<>();
            
            // Required fields
            body.put("species", data.getOrDefault("species", "DOG"));
            body.put("sex", data.getOrDefault("sex", "UNKNOWN"));
            body.put("locationLatitude", Double.parseDouble(data.getOrDefault("locationLatitude", "51.1")));
            body.put("locationLongitude", Double.parseDouble(data.getOrDefault("locationLongitude", "17.0")));
            // Use yesterday to avoid timezone issues (server may be in UTC)
            body.put("lastSeenDate", data.getOrDefault("lastSeenDate", LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_DATE)));
            body.put("status", data.getOrDefault("status", "MISSING"));
            
            // Optional fields
            if (data.containsKey("petName")) body.put("petName", data.get("petName"));
            if (data.containsKey("breed")) body.put("breed", data.get("breed"));
            if (data.containsKey("age")) body.put("age", Integer.parseInt(data.get("age")));
            if (data.containsKey("description")) body.put("description", data.get("description"));
            if (data.containsKey("microchipNumber")) body.put("microchipNumber", data.get("microchipNumber"));
            if (data.containsKey("email")) body.put("email", data.get("email"));
            if (data.containsKey("phone")) body.put("phone", data.get("phone"));
            if (data.containsKey("reward")) body.put("reward", data.get("reward"));
            
            String jsonBody = gson.toJson(body);
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + API_PATH))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 201) {
                throw new RuntimeException("Failed to create announcement. Status: " + response.statusCode() + 
                    ", Body: " + response.body());
            }
            
            // Parse response to get ID and managementPassword
            JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
            String id = jsonResponse.get("id").getAsString();
            String managementPassword = jsonResponse.get("managementPassword").getAsString();
            
            // Store for cleanup and photo upload
            String petName = data.getOrDefault("petName", "unnamed");
            createdAnnouncements.put(petName, id);
            managementPasswords.put(id, managementPassword);
            
            System.out.println("Created announcement: " + petName + " (ID: " + id + ")");
            
            // Upload placeholder photo so announcement appears in list
            uploadPlaceholderPhoto(id, managementPassword);
            
            return id;
            
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to create announcement: " + e.getMessage(), e);
        }
    }
    
    /**
     * Gets announcement from API as raw JSON string (for debugging).
     * 
     * @param id Announcement ID
     * @return Raw JSON response with status code
     * @throws RuntimeException if API call fails
     */
    public static String getAnnouncementFromApi(String id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + API_PATH + "/" + id))
                .header("Content-Type", "application/json")
                .GET()
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return "Status: " + response.statusCode() + ", Body: " + response.body();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get announcement from API", e);
        }
    }
    
    /**
     * Gets an announcement by ID via API.
     * 
     * @param id Announcement UUID
     * @return Map containing announcement fields
     * @throws RuntimeException if API call fails or announcement not found
     */
    public static Map<String, Object> getAnnouncement(String id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + API_PATH + "/" + id))
                .header("Content-Type", "application/json")
                .GET()
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 404) {
                throw new RuntimeException("Announcement not found: " + id);
            }
            
            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to get announcement. Status: " + response.statusCode());
            }
            
            return gson.fromJson(response.body(), Map.class);
            
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to get announcement: " + e.getMessage(), e);
        }
    }
    
    /**
     * Deletes an announcement by ID via admin API.
     * 
     * @param id Announcement UUID
     * @throws RuntimeException if API call fails
     */
    public static void deleteAnnouncement(String id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + ADMIN_API_PATH + "/" + id))
                .header("Content-Type", "application/json")
                .header("Authorization", ADMIN_TOKEN)
                .DELETE()
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 204 && response.statusCode() != 404) {
                throw new RuntimeException("Failed to delete announcement. Status: " + response.statusCode());
            }
            
            System.out.println("Deleted announcement: " + id);
            
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to delete announcement: " + e.getMessage(), e);
        }
    }
    
    /**
     * Uploads a placeholder photo for the announcement.
     * This is required because the backend only shows announcements with photos.
     * 
     * @param announcementId Announcement UUID
     * @param managementPassword Password returned from create
     */
    private static void uploadPlaceholderPhoto(String announcementId, String managementPassword) {
        try {
            String boundary = "----WebKitFormBoundary" + UUID.randomUUID().toString().replace("-", "");
            
            // Create a minimal valid JPEG (1x1 red pixel)
            byte[] minimalJpeg = new byte[] {
                (byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0, 0x00, 0x10, 0x4A, 0x46,
                0x49, 0x46, 0x00, 0x01, 0x01, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00,
                (byte)0xFF, (byte)0xDB, 0x00, 0x43, 0x00, 0x08, 0x06, 0x06, 0x07, 0x06,
                0x05, 0x08, 0x07, 0x07, 0x07, 0x09, 0x09, 0x08, 0x0A, 0x0C, 0x14, 0x0D,
                0x0C, 0x0B, 0x0B, 0x0C, 0x19, 0x12, 0x13, 0x0F, 0x14, 0x1D, 0x1A, 0x1F,
                0x1E, 0x1D, 0x1A, 0x1C, 0x1C, 0x20, 0x24, 0x2E, 0x27, 0x20, 0x22, 0x2C,
                0x23, 0x1C, 0x1C, 0x28, 0x37, 0x29, 0x2C, 0x30, 0x31, 0x34, 0x34, 0x34,
                0x1F, 0x27, 0x39, 0x3D, 0x38, 0x32, 0x3C, 0x2E, 0x33, 0x34, 0x32,
                (byte)0xFF, (byte)0xC0, 0x00, 0x0B, 0x08, 0x00, 0x01, 0x00, 0x01, 0x01, 0x01, 0x11, 0x00,
                (byte)0xFF, (byte)0xC4, 0x00, 0x1F, 0x00, 0x00, 0x01, 0x05, 0x01, 0x01, 0x01,
                0x01, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01,
                0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B,
                (byte)0xFF, (byte)0xC4, 0x00, (byte)0xB5, 0x10, 0x00, 0x02, 0x01, 0x03, 0x03,
                0x02, 0x04, 0x03, 0x05, 0x05, 0x04, 0x04, 0x00, 0x00, 0x01, 0x7D, 0x01,
                0x02, 0x03, 0x00, 0x04, 0x11, 0x05, 0x12, 0x21, 0x31, 0x41, 0x06, 0x13,
                0x51, 0x61, 0x07, 0x22, 0x71, 0x14, 0x32, (byte)0x81, (byte)0x91, (byte)0xA1, 0x08,
                0x23, 0x42, (byte)0xB1, (byte)0xC1, 0x15, 0x52, (byte)0xD1, (byte)0xF0, 0x24, 0x33,
                0x62, 0x72, (byte)0x82, 0x09, 0x0A, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x25,
                0x26, 0x27, 0x28, 0x29, 0x2A, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A,
                0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4A, 0x53, 0x54, 0x55, 0x56,
                0x57, 0x58, 0x59, 0x5A, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6A,
                0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7A, (byte)0x83, (byte)0x84, (byte)0x85,
                (byte)0x86, (byte)0x87, (byte)0x88, (byte)0x89, (byte)0x8A, (byte)0x92, (byte)0x93, (byte)0x94,
                (byte)0x95, (byte)0x96, (byte)0x97, (byte)0x98, (byte)0x99, (byte)0x9A, (byte)0xA2, (byte)0xA3,
                (byte)0xA4, (byte)0xA5, (byte)0xA6, (byte)0xA7, (byte)0xA8, (byte)0xA9, (byte)0xAA, (byte)0xB2,
                (byte)0xB3, (byte)0xB4, (byte)0xB5, (byte)0xB6, (byte)0xB7, (byte)0xB8, (byte)0xB9, (byte)0xBA,
                (byte)0xC2, (byte)0xC3, (byte)0xC4, (byte)0xC5, (byte)0xC6, (byte)0xC7, (byte)0xC8, (byte)0xC9,
                (byte)0xCA, (byte)0xD2, (byte)0xD3, (byte)0xD4, (byte)0xD5, (byte)0xD6, (byte)0xD7, (byte)0xD8,
                (byte)0xD9, (byte)0xDA, (byte)0xE1, (byte)0xE2, (byte)0xE3, (byte)0xE4, (byte)0xE5, (byte)0xE6,
                (byte)0xE7, (byte)0xE8, (byte)0xE9, (byte)0xEA, (byte)0xF1, (byte)0xF2, (byte)0xF3, (byte)0xF4,
                (byte)0xF5, (byte)0xF6, (byte)0xF7, (byte)0xF8, (byte)0xF9, (byte)0xFA,
                (byte)0xFF, (byte)0xDA, 0x00, 0x08, 0x01, 0x01, 0x00, 0x00, 0x3F, 0x00,
                (byte)0xFB, (byte)0xD5, 0x00, 0x00, 0x00, 0x00,
                (byte)0xFF, (byte)0xD9
            };
            
            // Build multipart body
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String lineEnd = "\r\n";
            
            // File part
            baos.write(("--" + boundary + lineEnd).getBytes(StandardCharsets.UTF_8));
            baos.write(("Content-Disposition: form-data; name=\"photo\"; filename=\"test.jpg\"" + lineEnd).getBytes(StandardCharsets.UTF_8));
            baos.write(("Content-Type: image/jpeg" + lineEnd + lineEnd).getBytes(StandardCharsets.UTF_8));
            baos.write(minimalJpeg);
            baos.write((lineEnd + "--" + boundary + "--" + lineEnd).getBytes(StandardCharsets.UTF_8));
            
            byte[] body = baos.toByteArray();
            
            // Basic auth header
            String auth = Base64.getEncoder().encodeToString(
                ("user:" + managementPassword).getBytes(StandardCharsets.UTF_8)
            );
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + API_PATH + "/" + announcementId + "/photos"))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .header("Authorization", "Basic " + auth)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 201) {
                System.err.println("Warning: Failed to upload photo. Status: " + response.statusCode() + 
                    ", Body: " + response.body());
            } else {
                System.out.println("Uploaded placeholder photo for announcement: " + announcementId);
            }
            
        } catch (IOException | InterruptedException e) {
            System.err.println("Warning: Failed to upload photo: " + e.getMessage());
        }
    }
    
    /**
     * Gets the ID of a previously created announcement by pet name.
     * 
     * @param petName Pet name used when creating the announcement
     * @return Announcement ID or null if not found
     */
    public static String getCreatedAnnouncementId(String petName) {
        return createdAnnouncements.get(petName);
    }
    
    /**
     * Deletes all announcements created during the test session.
     * Should be called in @After hook.
     */
    public static void cleanupAllCreatedAnnouncements() {
        for (Map.Entry<String, String> entry : createdAnnouncements.entrySet()) {
            try {
                deleteAnnouncement(entry.getValue());
            } catch (Exception e) {
                System.err.println("Failed to cleanup announcement " + entry.getKey() + ": " + e.getMessage());
            }
        }
        createdAnnouncements.clear();
    }
    
    /**
     * Clears the tracking map without deleting announcements.
     * Useful when announcements are expected to be deleted by the test itself.
     */
    public static void clearTracking() {
        createdAnnouncements.clear();
    }
}

