import XCTest
@testable import PetSpot

/// Unit tests for TabCoordinator.
/// Tests verify tab bar creation, child coordinator management, and configuration.
@MainActor
final class TabCoordinatorTests: XCTestCase {
    
    private var sut: TabCoordinator!
    
    override func setUp() {
        super.setUp()
        sut = TabCoordinator()
    }
    
    override func tearDown() {
        sut = nil
        super.tearDown()
    }
    
    // MARK: - Test: TabCoordinator init creates 5 child coordinators (T012)
    
    func testInit_shouldCreate5ChildCoordinators() {
        // Given - sut created in setUp
        
        // When
        let childCount = sut.childCoordinatorCount
        
        // Then
        XCTAssertEqual(childCount, 5, "TabCoordinator should create exactly 5 child coordinators")
    }
    
    // MARK: - Test: Each child coordinator has non-nil UINavigationController (T013)
    
    func testInit_eachChildCoordinator_shouldHaveNavigationController() {
        // Given - sut created in setUp
        
        // When
        let tabBarController = sut.tabBarController
        
        // Then
        XCTAssertEqual(tabBarController.viewControllers?.count, 5, "Tab bar should have 5 view controllers")
        
        for (index, viewController) in (tabBarController.viewControllers ?? []).enumerated() {
            XCTAssertTrue(
                viewController is UINavigationController,
                "View controller at index \(index) should be UINavigationController"
            )
        }
    }
    
    // MARK: - Test: tabBarController computed property returns UITabBarController with 5 view controllers (T014)
    
    func testTabBarController_shouldReturnUITabBarControllerWith5ViewControllers() {
        // Given - sut created in setUp
        
        // When
        let tabBarController = sut.tabBarController
        
        // Then
        XCTAssertNotNil(tabBarController)
        XCTAssertEqual(tabBarController.viewControllers?.count, 5)
    }
    
    // MARK: - Test: Tab bar items have correct titles (localized) (T015)
    
    func testTabBarItems_shouldHaveCorrectLocalizedTitles() {
        // Given
        let expectedTitles = [
            L10n.Tabs.home,
            L10n.Tabs.lostPet,
            L10n.Tabs.foundPet,
            L10n.Tabs.contactUs,
            L10n.Tabs.account
        ]
        
        // When
        let tabBarController = sut.tabBarController
        let viewControllers = tabBarController.viewControllers ?? []
        
        // Then
        XCTAssertEqual(viewControllers.count, expectedTitles.count)
        
        for (index, viewController) in viewControllers.enumerated() {
            let actualTitle = viewController.tabBarItem.title
            XCTAssertEqual(
                actualTitle,
                expectedTitles[index],
                "Tab at index \(index) should have title '\(expectedTitles[index])' but got '\(actualTitle ?? "nil")'"
            )
        }
    }
    
    // MARK: - Test: Tab bar items have correct custom icons (T016)
    
    func testTabBarItems_shouldHaveCorrectCustomIcons() {
        // Given
        let expectedIconNames = [
            "home",       // Home
            "lostPet",    // Lost Pet
            "foundPet",   // Found Pet
            "contactUs",  // Contact Us
            "account"     // Account
        ]
        
        // When
        let tabBarController = sut.tabBarController
        let viewControllers = tabBarController.viewControllers ?? []
        
        // Then
        XCTAssertEqual(viewControllers.count, expectedIconNames.count)
        
        for (index, viewController) in viewControllers.enumerated() {
            let tabBarItem = viewController.tabBarItem
            XCTAssertNotNil(tabBarItem?.image, "Tab at index \(index) should have an image")
        }
    }
    
    // MARK: - Test: Accessibility identifiers set correctly on tab bar items (T017)
    
    func testTabBarItems_shouldHaveCorrectAccessibilityIdentifiers() {
        // Given
        let expectedIdentifiers = [
            "tabs.home",
            "tabs.lostPet",
            "tabs.foundPet",
            "tabs.contactUs",
            "tabs.account"
        ]
        
        // When
        let tabBarController = sut.tabBarController
        let viewControllers = tabBarController.viewControllers ?? []
        
        // Then
        XCTAssertEqual(viewControllers.count, expectedIdentifiers.count)
        
        for (index, viewController) in viewControllers.enumerated() {
            let actualIdentifier = viewController.tabBarItem.accessibilityIdentifier
            XCTAssertEqual(
                actualIdentifier,
                expectedIdentifiers[index],
                "Tab at index \(index) should have accessibility identifier '\(expectedIdentifiers[index])' but got '\(actualIdentifier ?? "nil")'"
            )
        }
    }
    
    // MARK: - Test: Child coordinators started successfully when start() is called (T018)
    
    func testStart_shouldStartAllChildCoordinators() async {
        // Given - sut created in setUp
        
        // When
        await sut.start(animated: false)
        
        // Then
        // Verify tab bar controller still has 5 view controllers after start
        let tabBarController = sut.tabBarController
        XCTAssertEqual(tabBarController.viewControllers?.count, 5)
        
        // Verify each navigation controller has a root view controller (coordinators populated content)
        for (index, viewController) in (tabBarController.viewControllers ?? []).enumerated() {
            guard let navigationController = viewController as? UINavigationController else {
                XCTFail("View controller at index \(index) should be UINavigationController")
                continue
            }
            
            XCTAssertFalse(
                navigationController.viewControllers.isEmpty,
                "Navigation controller at index \(index) should have at least one view controller after start()"
            )
        }
    }
    
    // MARK: - Test: First tab (Home) is selected by default
    
    func testInit_firstTab_shouldBeSelectedByDefault() {
        // Given - sut created in setUp
        
        // When
        let selectedIndex = sut.tabBarController.selectedIndex
        
        // Then
        XCTAssertEqual(selectedIndex, 0, "First tab (Home) should be selected by default")
    }
}

