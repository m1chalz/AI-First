@animalList
Feature: Animal List
  As a user
  I want to view a list of missing and found animals
  So that I can find my lost pet or help others find theirs

  Background:
    Given the application is running

  # ========================================
  # Test 1: Display animal list with UI elements
  # ========================================

  @web @ios @android @smoke
  Scenario: User views animal list with all UI elements
    # Setup - Create test data via API
    Given I create a test announcement via API with name "E2E-TestDog" and species "DOG"
    
    # Action - Navigate to animal list (without geolocation = shows all announcements)
    When I navigate to the pet list page
    Then the page should load successfully
    
    # Verification - List elements and announcement visible
    And I should see the announcement for "E2E-TestDog"
    And I should see the "Report a Missing Animal" button
    
    # Cleanup
    And I delete the test announcement via API

  # ========================================
  # Test 2: Location-based filtering
  # NOTE: Requires Selenium CDP support for Chrome 142+
  # Currently Chrome 142 is too new - no selenium-devtools-v142 available
  # ========================================

  @web @ios @android @pending
  Scenario: User sees only nearby animals when location is set
    # Setup - Create announcement near (Wroclaw) and far (New York)
    Given I create a test announcement at coordinates "51.1" "17.0" with name "E2E-NearbyPet"
    And I create a test announcement at coordinates "40.7" "-74.0" with name "E2E-FarAwayPet"
    
    # Action - Navigate with location (Wroclaw)
    When I navigate to the pet list page with location "51.1" "17.0"
    Then the page should load successfully
    
    # Verification - Only nearby pet should be visible
    And I should see the announcement for "E2E-NearbyPet"
    And I should NOT see the announcement for "E2E-FarAwayPet"
    
    # Cleanup
    And I delete all test announcements via API
