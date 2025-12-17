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
            
            // Valid 1x1 pixel JPEG (tested and working)
            String base64Jpeg = "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAABAAEDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAn/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAAAAX/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwCwAB//2Q==";
            byte[] minimalJpeg = Base64.getDecoder().decode(base64Jpeg);
            
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

