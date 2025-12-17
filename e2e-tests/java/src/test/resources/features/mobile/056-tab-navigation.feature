@android @mobile @tab-navigation
Feature: Tab Navigation
  As a PetSpot user
  I want to navigate between different sections using tabs
  So that I can access different features of the application

  Background:
    Given the app is launched on the home screen

  @smoke
  Scenario: User taps Home tab and navigates to landing page
    Given I am on any screen in the app
    When I tap the "Home" tab
    Then I should see the home landing page

  @smoke
  Scenario: User taps Lost Pet tab and navigates to lost pet announcements
    Given I am on the home screen
    When I tap the "Lost Pet" tab
    Then I should see the lost pet announcements list

  @smoke
  Scenario: User taps Found Pet tab and navigates to found pet announcements
    Given I am on the home screen
    When I tap the "Found Pet" tab
    Then I should see the found pet announcements list

  Scenario: User taps Contact Us tab and navigates to placeholder screen
    Given I am on the home screen
    When I tap the "Contact Us" tab
    Then I should see the "Coming soon" placeholder screen

  Scenario: User taps Account tab and navigates to placeholder screen
    Given I am on the home screen
    When I tap the "Account" tab
    Then I should see the "Coming soon" placeholder screen

  @smoke
  Scenario: Current tab is visually indicated
    Given I am on the home screen
    When I tap the "Lost Pet" tab
    Then the "Lost Pet" tab should be visually selected
    And the "Home" tab should not be visually selected

