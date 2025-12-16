@web @057-navigation
Feature: Web Navigation Bar
  As a PetSpot user
  I want to use the navigation bar
  So that I can quickly access different sections of the application

  Background:
    Given user is on the Home page

  @P1 @US1
  Scenario Outline: User navigates to different sections
    When user clicks "<section>" in the navigation bar
    Then user should be on the "<section>" page
    And "<section>" navigation item should be highlighted

    Examples:
      | section     |
      | Home        |
      | Lost Pet    |
      | Found Pet   |
      | Contact Us  |
      | Account     |

  @P1 @US1
  Scenario: Navigation bar displays all items
    Then navigation bar should display all navigation items
    And navigation bar should display the PetSpot logo

  @P1 @US1
  Scenario: Active navigation state on Home page
    Then "Home" navigation item should be highlighted
    And other navigation items should not be highlighted

  @P1 @US1
  Scenario: Active state updates on direct URL access
    Given user directly accesses "/lost-pets" URL
    Then "Lost Pet" navigation item should be highlighted
    And other navigation items should not be highlighted

