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
    
    # Mobile: Restart app to load fresh data
    When I restart the app
    
    # Action - Navigate to animal list
    When I navigate to the pet list page
    Then the page should load successfully
    
    # Verification - Button is visible before scrolling
    And I should see the "Report a Missing Animal" button
    
    # Verification - Scroll to find announcement (tests scrolling + finds test data)
    When I scroll until I see the announcement for "E2E-TestDog"
    Then I should see the announcement for "E2E-TestDog"
    
    # Verification - Button remains visible after scrolling (FR-003 from spec 005)
    And I should see the "Report a Missing Animal" button
    
    # Cleanup
    And I delete the test announcement via API

  # ========================================
  # Test 2: Location-based filtering + Empty state
  # NOTE: @location tag enables location permission + GPS mocking
  # - Mobile: Appium GPS simulation
  # - Web: Selenium CDP (requires spec 053 for Chrome compatibility)
  # ========================================

  @ios @android @location @pending
  Scenario: User sees only nearby animals and empty state when no animals in area
    # Setup - Create announcements at different locations
    Given I create a test announcement at coordinates "51.1" "17.0" with name "E2E-NearbyPet"
    Given I create a test announcement at coordinates "40.7" "-74.0" with name "E2E-FarPet"
    
    # Restart app to load fresh data
    When I restart the app
    
    # Action 1 - Set location FAR from announcement (New York area)
    And I set device location to "40.7" "-74.0"
    When I navigate to the pet list page
    Then the page should load successfully
    
    # Verification 1 - Only nearby animal visible (location filtering)
    And I should see the announcement for "E2E-FarPet"
    And I should NOT see the announcement for "E2E-NearbyPet"
    
    # Action 2 - Set location in Wroclaw (where E2E-NearbyPet is)
    And I set device location to "51.1" "17.0"
    When I restart the app
    When I navigate to the pet list page
    Then the page should load successfully
    
    # Verification 2 - Now Wroclaw animal visible, NY not visible
    And I should see the announcement for "E2E-NearbyPet"
    And I should NOT see the announcement for "E2E-FarPet"
    
    # Cleanup
    And I delete all test announcements via API
    
  # ========================================
  # Test 3: Empty state when no animals in area
  # ========================================
  
  @ios @android @location @pending
  Scenario: User sees empty state when no animals in current location
    # Setup - Create announcement only in Wroclaw
    Given I create a test announcement at coordinates "51.1" "17.0" with name "E2E-OnlyInWroclaw"
    
    # Restart app and set location far away (middle of ocean)
    When I restart the app
    And I set device location to "0.0" "0.0"
    When I navigate to the pet list page
    Then the page should load successfully
    
    # Verification - No animals = empty state
    And I should see empty state message
    
    # Cleanup
    And I delete the test announcement via API

