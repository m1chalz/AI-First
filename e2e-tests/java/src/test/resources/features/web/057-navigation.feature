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

  # User Story 2 - Visual Design Consistency
  @P2 @US2
  Scenario: Navigation bar displays horizontal layout with logo on left
    Then navigation bar should display with horizontal layout
    And navigation bar logo should be positioned on the left side
    And navigation items should be positioned on the right side

  @P2 @US2
  Scenario: Navigation items display icon and label
    Then all navigation items should display an icon
    And all navigation items should display a text label
    And icons should appear before labels

  @P2 @US2
  Scenario: Active navigation item has visual styling
    Given user is on the Home page
    Then "Home" navigation item should have active styling
    And "Home" navigation item should have blue background color
    And "Home" navigation item should have blue text color

  @P2 @US2
  Scenario: Inactive navigation items have neutral styling
    Given user is on the Home page
    Then "Lost Pet" navigation item should have inactive styling
    And "Lost Pet" navigation item should have transparent background
    And "Lost Pet" navigation item should have gray text color

  @P2 @US2
  Scenario: Navigation item hover state provides feedback
    Given user is on the Home page
    When user hovers over "Lost Pet" navigation item
    Then "Lost Pet" navigation item should show hover feedback

  # User Story 3 - Navigation State Persistence
  @P3 @US3
  Scenario: Active state updates when navigating between sections
    Given user is on the Home page
    And "Home" navigation item should be highlighted
    When user clicks "Lost Pet" in the navigation bar
    Then "Lost Pet" navigation item should be highlighted
    And "Home" navigation item should not be highlighted

  @P3 @US3
  Scenario: Active state persists on direct URL access to Lost Pet
    Given user directly accesses "/lost-pets" URL
    Then "Lost Pet" navigation item should be highlighted
    And navigation bar should be visible

  @P3 @US3
  Scenario: Active state persists on direct URL access to Found Pet
    Given user directly accesses "/found-pets" URL
    Then "Found Pet" navigation item should be highlighted
    And navigation bar should be visible

  @P3 @US3
  Scenario: Active state persists on direct URL access to Contact
    Given user directly accesses "/contact" URL
    Then "Contact Us" navigation item should be highlighted
    And navigation bar should be visible

  @P3 @US3
  Scenario: Active state persists on direct URL access to Account
    Given user directly accesses "/account" URL
    Then "Account" navigation item should be highlighted
    And navigation bar should be visible

  @P3 @US3
  Scenario: Navigation bar remains visible across all routes
    When user clicks "Lost Pet" in the navigation bar
    Then navigation bar should be visible
    When user clicks "Found Pet" in the navigation bar
    Then navigation bar should be visible
    When user clicks "Contact Us" in the navigation bar
    Then navigation bar should be visible
    When user clicks "Account" in the navigation bar
    Then navigation bar should be visible

  @P3 @US3
  Scenario: Active state updates correctly with browser back button
    Given user is on the Home page
    When user clicks "Lost Pet" in the navigation bar
    And user clicks "Found Pet" in the navigation bar
    And user navigates back in browser
    Then "Lost Pet" navigation item should be highlighted

  @P3 @US3
  Scenario: All navigation items remain accessible after navigation
    Given user is on the Home page
    When user clicks "Lost Pet" in the navigation bar
    Then all navigation items should be clickable
    And navigation bar should display all navigation items

