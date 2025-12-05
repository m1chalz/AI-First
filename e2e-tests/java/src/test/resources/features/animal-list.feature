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
    
    # Verification - Button remains visible while scrolling (FR-003 from spec 005)
    When I scroll down the page
    Then I should see the "Report a Missing Animal" button
    
    # Cleanup
    And I delete the test announcement via API

  # ========================================
  # Test 2: Location-based filtering + Empty state
  # NOTE: Requires geolocation mocking:
  # - Web: Selenium CDP (blocked by Chrome 142+ compatibility)
  # - iOS: Appium GPS simulation or Simulator location settings
  # - Android: Appium GPS mock
  # Will be enabled with Docker Selenium Grid (spec 053)
  # ========================================

  @web @ios @android @pending
  Scenario: User sees only nearby animals and empty state when no animals in area
    # Setup - Create announcement ONLY in Wroclaw (51.1, 17.0)
    # No announcement in New York area
    Given I create a test announcement at coordinates "51.1" "17.0" with name "E2E-NearbyPet"
    
    # Action 1 - Navigate with location FAR from Wroclaw (New York: 40.7, -74.0)
    When I navigate to the pet list page with location "40.7" "-74.0"
    Then the page should load successfully
    
    # Verification 1 - No nearby animals = empty state (FR-019 from spec 032)
    And I should NOT see the announcement for "E2E-NearbyPet"
    And I should see empty state message
    
    # Action 2 - Navigate with location in Wroclaw (same as announcement)
    When I navigate to the pet list page with location "51.1" "17.0"
    Then the page should load successfully
    
    # Verification 2 - Nearby animal is visible
    And I should see the announcement for "E2E-NearbyPet"
    And I should see the "Report a Missing Animal" button
    
    # Cleanup
    And I delete all test announcements via API

