package com.intive.aifirst.petspot.e2e.utils;

import com.intive.aifirst.petspot.e2e.utils.TestDataApiHelper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Common Cucumber step definitions shared across all platforms.
 * 
 * <p>This class contains API-driven steps for test data setup and cleanup.
 * These steps are platform-agnostic and can be used in web, iOS, and Android tests.
 * 
 * <h2>Step Categories:</h2>
 * <ul>
 *   <li>API Setup: Creating test announcements via backend API</li>
 *   <li>API Cleanup: Deleting test announcements after tests</li>
 *   <li>API Verification: Verifying announcement data via API</li>
 * </ul>
 * 
 * @see com.intive.aifirst.petspot.e2e.utils.TestDataApiHelper
 */
public class CommonSteps {
    
    // Store last created announcement ID for use in subsequent steps
    private static String lastCreatedAnnouncementId;
    private static String lastCreatedPetName;
    
    // ========================================
    // API Setup Steps
    // ========================================
    
    /**
     * Creates a test announcement with name and species via API.
     * 
     * Example: Given I create a test announcement via API with name "TestDog" and species "DOG"
     */
    @Given("I create a test announcement via API with name {string} and species {string}")
    public void createAnnouncementWithNameAndSpecies(String petName, String species) {
        Map<String, String> data = new HashMap<>();
        data.put("petName", petName);
        data.put("species", species);
        // Include petName in description so it's visible in the card
        data.put("description", petName + " - E2E test announcement for " + petName);
        data.put("phone", "+48123456789");
        
        lastCreatedAnnouncementId = TestDataApiHelper.createAnnouncement(data);
        lastCreatedPetName = petName;
        
        assertNotNull(lastCreatedAnnouncementId, "Announcement should be created");
    }
    
    /**
     * Creates a test announcement with location coordinates via API.
     * 
     * Example: Given I create a test announcement at coordinates "51.1" "17.0" with name "NearbyPet"
     */
    @Given("I create a test announcement at coordinates {string} {string} with name {string}")
    public void createAnnouncementAtCoordinates(String lat, String lng, String petName) {
        Map<String, String> data = new HashMap<>();
        data.put("petName", petName);
        data.put("species", "DOG");
        data.put("locationLatitude", lat);
        data.put("locationLongitude", lng);
        // Include petName in description so it's visible in the card
        data.put("description", petName + " - E2E test announcement at " + lat + ", " + lng);
        data.put("phone", "+48123456789");
        
        lastCreatedAnnouncementId = TestDataApiHelper.createAnnouncement(data);
        lastCreatedPetName = petName;
        
        assertNotNull(lastCreatedAnnouncementId, "Announcement should be created");
    }
    
    /**
     * Creates a test announcement with specific date via API.
     * 
     * Example: Given I create a test announcement with date "2025-01-01" and name "OlderPet"
     */
    @Given("I create a test announcement with date {string} and name {string}")
    public void createAnnouncementWithDate(String date, String petName) {
        Map<String, String> data = new HashMap<>();
        data.put("petName", petName);
        data.put("species", "DOG");
        data.put("lastSeenDate", date);
        // Include petName in description so it's visible in the card
        data.put("description", petName + " - E2E test announcement dated " + date);
        data.put("phone", "+48123456789");
        
        lastCreatedAnnouncementId = TestDataApiHelper.createAnnouncement(data);
        lastCreatedPetName = petName;
        
        assertNotNull(lastCreatedAnnouncementId, "Announcement should be created");
    }
    
    /**
     * Creates a test announcement with full details via API.
     * 
     * Example: Given I create a test announcement via API with the following details:
     *          | petName | TestCat |
     *          | species | CAT |
     *          | breed | Persian |
     *          | sex | FEMALE |
     */
    @Given("I create a test announcement via API with the following details:")
    public void createAnnouncementWithDetails(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        
        lastCreatedAnnouncementId = TestDataApiHelper.createAnnouncement(data);
        lastCreatedPetName = data.getOrDefault("petName", "unnamed");
        
        assertNotNull(lastCreatedAnnouncementId, "Announcement should be created");
    }
    
    // ========================================
    // API Cleanup Steps
    // ========================================
    
    /**
     * Deletes the last created test announcement via API.
     * 
     * Example: And I delete the test announcement via API
     */
    @And("I delete the test announcement via API")
    public void deleteLastCreatedAnnouncement() {
        if (lastCreatedAnnouncementId != null) {
            TestDataApiHelper.deleteAnnouncement(lastCreatedAnnouncementId);
            lastCreatedAnnouncementId = null;
            lastCreatedPetName = null;
        }
    }
    
    /**
     * Deletes a specific test announcement by name via API.
     * 
     * Example: And I delete the test announcement "TestDog" via API
     */
    @And("I delete the test announcement {string} via API")
    public void deleteAnnouncementByName(String petName) {
        String id = TestDataApiHelper.getCreatedAnnouncementId(petName);
        if (id != null) {
            TestDataApiHelper.deleteAnnouncement(id);
        }
    }
    
    /**
     * Cleans up all test announcements created during the scenario.
     * 
     * Example: And I cleanup all test announcements via API
     */
    @And("I cleanup all test announcements via API")
    public void cleanupAllAnnouncements() {
        TestDataApiHelper.cleanupAllCreatedAnnouncements();
        lastCreatedAnnouncementId = null;
        lastCreatedPetName = null;
    }
    
    /**
     * Deletes all test announcements created during the scenario.
     * Alias for cleanup method with different wording.
     * 
     * Example: And I delete all test announcements via API
     */
    @And("I delete all test announcements via API")
    public void deleteAllTestAnnouncements() {
        TestDataApiHelper.cleanupAllCreatedAnnouncements();
        lastCreatedAnnouncementId = null;
        lastCreatedPetName = null;
    }
    
    // ========================================
    // API Verification Steps
    // ========================================
    
    /**
     * Verifies that the announcement exists in the backend.
     * 
     * Example: Then the announcement should exist in the backend API
     */
    @Then("the announcement should exist in the backend API")
    public void verifyAnnouncementExists() {
        assertNotNull(lastCreatedAnnouncementId, "No announcement was created");
        
        Map<String, Object> announcement = TestDataApiHelper.getAnnouncement(lastCreatedAnnouncementId);
        assertNotNull(announcement, "Announcement should exist in backend");
        assertEquals(lastCreatedAnnouncementId, announcement.get("id"), "ID should match");
    }
    
    /**
     * Verifies that the announcement has specific field value.
     * 
     * Example: And the announcement should have species "DOG" in the backend API
     */
    @And("the announcement should have {string} {string} in the backend API")
    public void verifyAnnouncementField(String field, String expectedValue) {
        assertNotNull(lastCreatedAnnouncementId, "No announcement was created");
        
        Map<String, Object> announcement = TestDataApiHelper.getAnnouncement(lastCreatedAnnouncementId);
        Object actualValue = announcement.get(field);
        
        assertNotNull(actualValue, "Field '" + field + "' should exist");
        assertEquals(expectedValue, actualValue.toString(), "Field '" + field + "' should match");
    }
    
    // ========================================
    // Getters for use in other step classes
    // ========================================
    
    /**
     * Gets the ID of the last created announcement.
     * 
     * @return Announcement ID or null
     */
    public static String getLastCreatedAnnouncementId() {
        return lastCreatedAnnouncementId;
    }
    
    /**
     * Gets the pet name of the last created announcement.
     * 
     * @return Pet name or null
     */
    public static String getLastCreatedPetName() {
        return lastCreatedPetName;
    }
}

