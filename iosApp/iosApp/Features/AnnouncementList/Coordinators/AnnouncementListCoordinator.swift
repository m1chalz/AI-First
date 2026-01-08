import UIKit
import SwiftUI

/**
 * Coordinator for Announcement List flow following MVVM-C architecture.
 * Manages navigation and creates UIHostingController for SwiftUI views.
 *
 * **Root coordinator pattern**: Creates its own UINavigationController in init(),
 * suitable for use as a tab's root coordinator where navigation controller
 * is not provided externally. Sub-coordinators (PetDetailsCoordinator,
 * ReportMissingPetCoordinator) receive this navigation controller as a dependency.
 *
 * Responsibilities:
 * - Creates and configures AnnouncementListViewModel
 * - Sets up coordinator closures for navigation
 * - Creates UIHostingController with SwiftUI view
 * - Handles navigation to child screens (details, report forms)
 */
class AnnouncementListCoordinator: CoordinatorInterface {
    var navigationController: UINavigationController?
    var childCoordinators: [CoordinatorInterface] = []
    
    /// Announcement List ViewModel reference for triggering refresh after report sent (User Story 3: T066)
    /// Weak to avoid keeping ViewModel in memory when screen is not visible
    /// UIHostingController/SwiftUI holds the strong reference
    private weak var announcementListViewModel: AnnouncementListViewModel?
    
    // MARK: - Dependencies
    
    private let repository: AnnouncementRepositoryProtocol
    private let locationService: LocationServiceProtocol
    private let photoAttachmentCache: PhotoAttachmentCacheProtocol
    private let announcementSubmissionService: AnnouncementSubmissionServiceProtocol
    
    /**
     * Creates AnnouncementListCoordinator with its own navigation controller.
     *
     * **Root coordinator pattern**: This coordinator creates and owns its
     * UINavigationController, making it suitable for use as a tab's root.
     * The navigation controller is then shared with sub-coordinators.
     *
     * - Parameters:
     *   - repository: Repository for fetching announcements
     *   - locationService: Service for location operations
     *   - photoAttachmentCache: Cache for photo attachments in report flow
     *   - announcementSubmissionService: Service for submitting announcements
     */
    init(
        repository: AnnouncementRepositoryProtocol,
        locationService: LocationServiceProtocol,
        photoAttachmentCache: PhotoAttachmentCacheProtocol,
        announcementSubmissionService: AnnouncementSubmissionServiceProtocol
    ) {
        self.repository = repository
        self.locationService = locationService
        self.photoAttachmentCache = photoAttachmentCache
        self.announcementSubmissionService = announcementSubmissionService
        self.navigationController = UINavigationController()
    }
    
    /**
     * Starts the Animal List flow.
     * Creates ViewModel, sets up navigation closures, and presents view.
     * 
     * - Parameter animated: Whether to animate the transition
     */
    func start(animated: Bool) async {
        guard let navigationController = navigationController else { return }
        
        // Create LocationPermissionHandler inline (each ViewModel needs its own instance)
        let locationHandler = LocationPermissionHandler(locationService: locationService)
        
        // Create ViewModel with dependencies (iOS MVVM-C: ViewModels call repositories directly)
        // onAnimalSelected closure is passed to child listViewModel via constructor
        let announcementListViewModel = AnnouncementListViewModel(
            repository: repository,
            locationHandler: locationHandler,
            onAnimalSelected: { [weak self] animalId in
                self?.showAnimalDetails(animalId: animalId)
            }
        )
        
        // Store weak reference for refresh triggering after report sent (User Story 3: T066)
        self.announcementListViewModel = announcementListViewModel
        
        // Set up coordinator closures for navigation (feature-specific buttons)
        announcementListViewModel.onReportMissing = { [weak self] in
            self?.showReportMissing()
        }
        
        announcementListViewModel.onReportFound = { [weak self] in
            self?.showReportFound()
        }
        
        // Create SwiftUI view with ViewModel
        let view = AnnouncementListView(viewModel: announcementListViewModel)
        
        // Wrap in UIHostingController for UIKit navigation
        let hostingController = UIHostingController(rootView: view)
        
        // Configure navigation bar
        hostingController.title = L10n.AnnouncementList.navigationTitle
        hostingController.navigationItem.largeTitleDisplayMode = .never
        
        // Show navigation bar and replace splash screen with animal list
        navigationController.isNavigationBarHidden = false
        navigationController.setViewControllers([hostingController], animated: animated)
    }
    
    // MARK: - Public Navigation Methods
    
    /**
     * Shows pet details screen for given announcement ID.
     * Called from TabCoordinator for cross-tab navigation (Home → Lost Pets).
     *
     * **Cross-Tab Navigation Pattern**:
     * 1. User taps announcement on Home tab
     * 2. HomeCoordinator invokes `onShowPetDetails` closure
     * 3. TabCoordinator switches tab and calls this method
     * 4. This coordinator pushes pet detail screen onto its navigation stack
     *
     * - Parameter announcementId: ID of announcement to show details for
     */
    func showPetDetails(for announcementId: String) {
        showAnimalDetails(animalId: announcementId)
    }
    
    // MARK: - Private Navigation Methods
    
    /**
     * Shows animal details screen.
     * Internal implementation - use `showPetDetails(for:)` for external calls.
     *
     * - Parameter animalId: ID of selected animal
     */
    private func showAnimalDetails(animalId: String) {
        // Placeholder: print log (real implementation will create child coordinator)
        print("Navigate to animal details: \(animalId)")
        // Future: let detailCoordinator = AnimalDetailCoordinator(...)
        // Future: detailCoordinator.start()

        let coordinator = PetDetailsCoordinator(
            navigationController: navigationController,
            petId: animalId,
            repository: repository
        )

        childCoordinators.append(coordinator)

        Task { @MainActor in
            await coordinator.start(animated: true)
        }

    }
    
    /**
     * Shows Report Missing Animal form.
     * Creates and starts MissingPetReportCoordinator as child coordinator.
     * User Story 3 (T066): Sets up callback chain to refresh list when report is sent.
     */
    private func showReportMissing() {
        guard let navigationController = navigationController else { return }
        
        // Create child coordinator with injected dependencies
        let reportCoordinator = MissingPetReportCoordinator(
            parentNavigationController: navigationController,
            locationService: locationService,
            photoAttachmentCache: photoAttachmentCache,
            announcementSubmissionService: announcementSubmissionService
        )
        reportCoordinator.parentCoordinator = self
        
        // User Story 3 (T066): Set callback to refresh list when user sends report
        // Coordinator -> ViewModel communication via public method
        reportCoordinator.onReportSent = { [weak self] in
            self?.announcementListViewModel?.requestToRefreshData()
        }
        
        childCoordinators.append(reportCoordinator)
        
        // Start modal flow
        Task { @MainActor in
            await reportCoordinator.start(animated: true)
        }
    }
    
    /**
     * Shows Report Found Animal form.
     * Mocked for now - will create ReportFoundCoordinator in future.
     * Note: Not exposed in iOS mobile UI per design, but included for completeness.
     */
    private func showReportFound() {
        // Placeholder: print log (real implementation will create child coordinator)
        print("Navigate to report found form")
        // Future: let reportCoordinator = ReportFoundCoordinator(...)
        // Future: reportCoordinator.start()
    }
    
    // MARK: - CoordinatorInterface Protocol
    
    // MARK: - Deinitialization
    
    deinit {
        print("deinit AnimalListCoordinator")
    }
}
