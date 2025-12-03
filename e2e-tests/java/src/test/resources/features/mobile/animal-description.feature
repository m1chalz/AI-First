@android @ios @mobile
Feature: Animal Description Screen
  As a pet owner reporting a missing pet
  I want to enter descriptive details about my pet
  So that responders can identify my pet before contacting me

  Background:
    Given the user is on the Animal Description screen

  # User Story 1: Provide animal context before contact (P1 - MVP)

  @us1 @smoke
  Scenario: User can fill required fields and continue
    When the user selects "Dog" as the species
    And the user enters "Labrador" as the race
    And the user selects "Male" as the gender
    And the user taps the Continue button
    Then the user should be on the Contact Details screen

  @us1
  Scenario: Species dropdown shows available options
    When the user taps the species dropdown
    Then the dropdown should display "Dog", "Cat", "Bird", "Rabbit", "Other"

  @us1
  Scenario: Race field is disabled until species is selected
    Then the race field should be disabled
    When the user selects "Cat" as the species
    Then the race field should be enabled

  @us1
  Scenario: Changing species clears the race field
    Given the user has selected "Dog" as the species
    And the user has entered "Golden Retriever" as the race
    When the user selects "Cat" as the species
    Then the race field should be empty

  @us1
  Scenario: User can select gender using card selector
    When the user taps the Female gender card
    Then the Female card should be selected
    And the Male card should not be selected
    When the user taps the Male gender card
    Then the Male card should be selected
    And the Female card should not be selected

  @us1
  Scenario: User can enter optional pet name
    When the user enters "Buddy" as the pet name
    Then the pet name field should display "Buddy"

  @us1
  Scenario: User can enter optional age
    When the user enters "5" as the age
    Then the age field should display "5"

  @us1
  Scenario: Date picker shows today's date by default
    Then the date field should display today's date

  @us1 @validation
  Scenario: Continue button shows validation error when species is empty
    Given the user has NOT selected a species
    And the user has entered "Labrador" as the race
    And the user has selected "Male" as the gender
    When the user taps the Continue button
    Then a validation error should be displayed
    And the user should remain on the Animal Description screen

  @us1 @validation
  Scenario: Continue button shows validation error when race is empty
    Given the user has selected "Dog" as the species
    And the user has NOT entered a race
    And the user has selected "Male" as the gender
    When the user taps the Continue button
    Then a validation error should be displayed
    And the user should remain on the Animal Description screen

  @us1 @validation
  Scenario: Continue button shows validation error when gender is not selected
    Given the user has selected "Dog" as the species
    And the user has entered "Labrador" as the race
    And the user has NOT selected a gender
    When the user taps the Continue button
    Then a validation error should be displayed
    And the user should remain on the Animal Description screen

  @us1 @navigation
  Scenario: User can navigate back to Photo screen
    When the user taps the back button
    Then the user should be on the Photo screen

  @us1 @persistence
  Scenario: Data persists when navigating back and returning
    Given the user has selected "Dog" as the species
    And the user has entered "Husky" as the race
    And the user has selected "Female" as the gender
    When the user taps the back button
    And the user navigates forward to the Animal Description screen
    Then the species field should display "Dog"
    And the race field should display "Husky"
    And the Female card should be selected

  # User Story 2: Capture last known location details (P2)

  @us2 @gps
  Scenario: User can request GPS position
    Given location permissions are granted
    When the user taps the "Request GPS position" button
    Then the GPS button should show loading state
    And the latitude field should be populated with a valid value
    And the longitude field should be populated with a valid value

  @us2 @gps @permissions
  Scenario: GPS request shows permission denied message
    Given location permissions are NOT granted
    When the user taps the "Request GPS position" button
    Then a toast message mentioning "permission" should be displayed
    And the app settings should be opened

  @us2 @gps @manual
  Scenario: User can manually enter coordinates
    When the user enters "52.2297" in the latitude field
    And the user enters "21.0122" in the longitude field
    Then the latitude field should display "52.2297"
    And the longitude field should display "21.0122"

  @us2 @gps @validation
  Scenario: Invalid latitude shows validation error
    Given the user has selected "Dog" as the species
    And the user has entered "Labrador" as the race
    And the user has selected "Male" as the gender
    When the user enters "100" in the latitude field
    And the user taps the Continue button
    Then a validation error mentioning "latitude" should be displayed

  @us2 @gps @validation
  Scenario: Invalid longitude shows validation error
    Given the user has selected "Dog" as the species
    And the user has entered "Labrador" as the race
    And the user has selected "Male" as the gender
    When the user enters "200" in the longitude field
    And the user taps the Continue button
    Then a validation error mentioning "longitude" should be displayed

  @us2 @gps @helper
  Scenario: GPS section shows helper text for manual entry
    Then the screen should display text "coordinates are the only location fallback"

  @us2 @gps @success
  Scenario: GPS success shows confirmation message
    Given location permissions are granted
    When the user taps the "Request GPS position" button
    And the GPS request completes successfully
    Then a success message should be displayed

