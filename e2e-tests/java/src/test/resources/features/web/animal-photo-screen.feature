@web @animalPhoto
Feature: Report Missing Pet - Animal Photo Screen

  Background:
    Given I navigate to "/report-missing/photo"

  @US1
  Scenario: User uploads valid photo successfully
    When I select a valid JPEG photo file
    Then the confirmation card should be displayed
    And the filename should be displayed
    And the file size should be displayed
    When I click the continue button on photo screen
    Then the current URL should contain "/report-missing/details"

  @US1
  Scenario: User uploads photo via file picker
    When I click the browse button
    Then the file picker dialog should be triggered
    When I select a valid PNG photo file
    Then the confirmation card should be displayed

  @US1
  Scenario: User uploads photo via drag and drop
    Given I have a valid photo file ready
    When I drag and drop the photo file onto the drop zone
    Then the confirmation card should be displayed
    And the filename should be displayed

  @US2
  Scenario: User sees validation error for invalid file type
    When I select an invalid PDF file
    Then the toast notification should be displayed
    And the toast should contain "Please upload JPG, PNG, GIF, WEBP, BMP, TIFF, HEIC, or HEIF format"
    And the confirmation card should not be displayed

  @US2
  Scenario: User sees validation error for oversized file
    When I select a file larger than 20MB
    Then the toast notification should be displayed
    And the toast should contain "File size exceeds 20MB limit"

  @US2
  Scenario: User cannot continue without uploading photo
    When I click the continue button on photo screen without photo
    Then the toast notification should be displayed
    And the toast should contain "Photo is mandatory"
    And the current URL should contain "/report-missing/photo"

  @US3
  Scenario: User removes uploaded photo
    When I select a valid JPEG photo file
    Then the confirmation card should be displayed
    When I click the remove button
    Then the confirmation card should not be displayed
    And the upload card should be displayed

  @US3
  Scenario: User can upload photo after removing previous one
    When I select a valid JPEG photo file named "photo1.jpg"
    Then the confirmation card should be displayed
    When I click the remove button
    And I select a valid JPEG photo file named "photo2.jpg"
    Then the confirmation card should be displayed
    And the filename should display "photo2.jpg"

  @US4
  Scenario: User cancels flow via back button
    When I select a valid JPEG photo file
    And I click the back button on photo screen
    Then the current URL should contain "/report-missing/microchip"

  @US4
  Scenario: Page refresh clears photo state
    When I select a valid JPEG photo file
    Then the confirmation card should be displayed
    When I refresh the page
    Then the photo upload form should be empty

