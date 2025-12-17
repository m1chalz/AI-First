@animalList @web
Feature: Animal List - Web Application
  As a web user
  I want to view a list of missing and found animals
  So that I can find my lost pet or help others find theirs

  Background:
    Given the application is running

  # ========================================
  # Test 1: Smoke test - Animal list + Report button + location filtering
  # WHAT IT TESTS:
  #   - Page loads successfully
  #   - Announcements are displayed
  #   - "Report a Missing Animal" button is visible
  #   - Button remains visible while scrolling (FR-003)
  #   - Location filtering - far away announcements not visible (soft assert)
  # ========================================

  @smoke @location
  Scenario: User views animal list with all UI elements
    # Setup - Create test data at Wroclaw + far away location
    Given I create a test announcement at coordinates "51.1" "17.0" with name "E2E-TestDog"
    Given I create a test announcement at coordinates "40.7" "-74.0" with name "E2E-FarAwayPet"
    
    # Set location to Wroclaw for location filtering
    When I set device location to "51.1" "17.0"
    
    # Action - Navigate to animal list
    When I navigate to the pet list page
    Then the page should load successfully
    
    # Verification - Button is visible before scrolling
    And I should see the "Report a Missing Animal" button
    
    # Verification - Scroll to find nearby announcement
    When I scroll until I see the announcement for "E2E-TestDog"
    Then I should see the announcement for "E2E-TestDog"
    
    # Verification - Button remains visible after scrolling (FR-003 from spec 005)
    And I should see the "Report a Missing Animal" button
    
    # Soft assertion - location filtering (logs warning if fails, doesn't stop test)
    And I should NOT see the announcement for "E2E-FarAwayPet" (soft assert)
    
    # Cleanup
    And I delete all test announcements via API

  # ========================================
  # Test 2: Full list without location vs filtered list with location
  # WHAT IT TESTS:
  #   - WITHOUT location: ALL animals are visible (no filtering)
  #   - WITH location: Only nearby animals visible (location filtering)
  #   - Verifies location filtering toggle behavior
  # ========================================

  Scenario: Web user sees full list without location and filtered list with location
    # Setup - Create nearby (Wroclaw) and far away (New York) announcements
    Given I create a test announcement at coordinates "51.1" "17.0" with name "E2E-NearbyPet"
    Given I create a test announcement at coordinates "40.7" "-74.0" with name "E2E-DistantPet"
    
    # PART 1: Without location - should see ALL announcements (no filtering)
    # Note: No "set device location" step = no URL params = all announcements visible
    When I navigate to the pet list page
    Then the page should load successfully
    
    # Verification - Both announcements should be visible (full list, no filtering)
    When I scroll until I see the announcement for "E2E-NearbyPet"
    Then I should see the announcement for "E2E-NearbyPet"
    When I scroll until I see the announcement for "E2E-DistantPet"
    Then I should see the announcement for "E2E-DistantPet"
    
    # PART 2: With location (Wroclaw) - should see only nearby announcements
    When I set device location to "51.1" "17.0"
    And I navigate to the pet list page
    Then the page should load successfully
    
    # Verification - Only nearby announcement visible, distant filtered out
    When I scroll until I see the announcement for "E2E-NearbyPet"
    Then I should see the announcement for "E2E-NearbyPet"
    And I should NOT see the announcement for "E2E-DistantPet"
    
    # Cleanup
    And I delete all test announcements via API

