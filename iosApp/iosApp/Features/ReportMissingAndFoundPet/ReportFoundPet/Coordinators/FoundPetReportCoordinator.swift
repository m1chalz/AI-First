import UIKit
import SwiftUI

/// Coordinator for Found Pet Report modal flow (3-step).
/// Creates and manages own UINavigationController for modal presentation.
/// Owns FoundPetReportFlowState and injects it into all ViewModels.
/// Flow: Photo (1/3) → Pet Details (2/3) → Contact Information (3/3) → Exit
class FoundPetReportCoordinator: CoordinatorInterface {
    // MARK: - Properties
    
    weak var parentCoordinator: CoordinatorInterface?
    var childCoordinators: [CoordinatorInterface] = []
    var navigationController: UINavigationController? // Modal nav controller
    
    /// Callback triggered when user successfully sends report (User Story 3: T066)
    /// Set by parent coordinator (AnimalListCoordinator) to trigger list refresh
    var onReportSent: (() -> Void)?
    
    private let parentNavigationController: UINavigationController
    private var flowState: FoundPetReportFlowState?
    
    // MARK: - Dependencies
    
    private let locationService: LocationServiceProtocol
    private let photoAttachmentCache: PhotoAttachmentCacheProtocol
    private let announcementSubmissionService: AnnouncementSubmissionServiceProtocol
    
    // MARK: - Initialization
    
    /// Creates coordinator for found pet report flow.
    /// - Parameters:
    ///   - parentNavigationController: Parent navigation controller to present modal from
    ///   - locationService: Service for location operations
    ///   - photoAttachmentCache: Cache for photo attachments
    ///   - announcementSubmissionService: Service for submitting announcements
    init(
        parentNavigationController: UINavigationController,
        locationService: LocationServiceProtocol,
        photoAttachmentCache: PhotoAttachmentCacheProtocol,
        announcementSubmissionService: AnnouncementSubmissionServiceProtocol
    ) {
        self.parentNavigationController = parentNavigationController
        self.locationService = locationService
        self.photoAttachmentCache = photoAttachmentCache
        self.announcementSubmissionService = announcementSubmissionService
    }
    
    // MARK: - CoordinatorInterface
    
    /// Starts the found pet report flow.
    /// Creates modal UINavigationController and presents photo screen (Step 1/3).
    func start(animated: Bool) async {
        // Create dedicated UINavigationController for modal flow
        // This provides independent navigation stack separate from parent
        let modalNavController = UINavigationController()
        self.navigationController = modalNavController
        
        // Configure modal presentation with .fullScreen to prevent swipe-to-dismiss
        modalNavController.modalPresentationStyle = .fullScreen
        
        // Create FlowState as coordinator property (owned by coordinator lifecycle)
        // This state object will be injected into all ViewModels
        let flowState = FoundPetReportFlowState(
            photoAttachmentCache: photoAttachmentCache
        )
        self.flowState = flowState
        
        // Navigate to photo screen (step 1/3) - new entry point
        navigateToPhoto()
        
        // Present modally from parent navigation controller
        parentNavigationController.present(modalNavController, animated: animated)
    }
    
    // MARK: - Navigation Methods
    
    /// Navigate to photo screen (Step 1/3).
    /// Entry point for the 3-step Found Pet flow.
    private func navigateToPhoto() {
        guard let flowState = flowState,
              let modalNavController = navigationController else { return }
        
        let toastScheduler = ToastScheduler()
        let viewModel = FoundPetPhotoViewModel(
            flowState: flowState,
            photoAttachmentCache: photoAttachmentCache,
            toastScheduler: toastScheduler
        )
        
        viewModel.onNext = { [weak self] in
            self?.navigateToPetDetails()
        }
        
        viewModel.onBack = { [weak self] in
            self?.exitFlow()
        }
        
        let view = FoundPetPhotoView(viewModel: viewModel)
        let hostingController = UIHostingController(
            rootView: NavigationBackHiding { view }
        )
        
        // Configure navigation bar - Step 1/3 with dismiss button (entry point)
        hostingController.title = L10n.ReportFoundPet.Photo.screenTitle
        configureProgressIndicator(hostingController: hostingController, step: 1, total: 3)
        configureCustomDismissButton(hostingController: hostingController, action: { [weak viewModel] in
            viewModel?.handleBack()
        })
        
        // Set as root view controller (entry point)
        modalNavController.setViewControllers([hostingController], animated: false)
    }
    
    /// Navigate to pet details screen (Step 2/3).
    /// Uses existing AnimalDescription with added collar data (microchip) field.
    private func navigateToPetDetails() {
        guard let flowState = flowState,
              let modalNavController = navigationController else { return }
        
        let toastScheduler = ToastScheduler()
        let locationHandler = LocationPermissionHandler(locationService: locationService)
        let viewModel = FoundPetAnimalDescriptionViewModel(
            flowState: flowState,
            locationHandler: locationHandler,
            toastScheduler: toastScheduler
        )
        
        viewModel.onContinue = { [weak self] in
            self?.navigateToContactInformation()
        }
        
        viewModel.onBack = { [weak self] in
            self?.navigationController?.popViewController(animated: true)
        }
        
        let view = FoundPetAnimalDescriptionView(viewModel: viewModel)
        let hostingController = UIHostingController(
            rootView: NavigationBackHiding { view }
        )
        
        // Configure navigation bar - Step 2/3
        hostingController.title = L10n.ReportFoundPet.PetDetails.screenTitle
        configureProgressIndicator(hostingController: hostingController, step: 2, total: 3)
        configureCustomBackButton(hostingController: hostingController, action: { [weak viewModel] in
            viewModel?.onBackTapped()
        })
        
        modalNavController.pushViewController(hostingController, animated: true)
    }
    
    /// Navigate to contact information screen (Step 3/3).
    private func navigateToContactInformation() {
        guard let flowState = flowState,
              let modalNavController = navigationController else { return }
        
        let viewModel = FoundPetContactDetailsViewModel(
            submissionService: announcementSubmissionService,
            flowState: flowState
        )
        
        // On successful submission: notify parent and exit flow immediately (no Summary screen)
        viewModel.onReportSent = { [weak self] _ in
            self?.onReportSent?() // Notify parent coordinator for list refresh
            self?.exitFlow() // Exit immediately - Summary screen removed
        }
        
        viewModel.onBack = { [weak self] in
            self?.navigationController?.popViewController(animated: true)
        }
        
        let view = FoundPetContactDetailsView(viewModel: viewModel)
        let hostingController = UIHostingController(
            rootView: NavigationBackHiding { view }
        )
        
        // Configure navigation bar - Step 3/3
        hostingController.title = L10n.ReportFoundPet.ContactInfo.screenTitle
        configureProgressIndicator(hostingController: hostingController, step: 3, total: 3)
        configureCustomBackButton(hostingController: hostingController, action: { [weak viewModel] in
            viewModel?.handleBack()
        })
        
        modalNavController.pushViewController(hostingController, animated: true)
    }
    
    /// Exits the flow: dismisses modal, clears state, notifies parent.
    /// Called when user taps back on step 1 or completes submission.
    func exitFlow() {
        // Clear all form data from flow state before dismissing
        let currentFlowState = flowState
        flowState = nil
        
        Task {
            await currentFlowState?.clear()
        }
        
        // Dismiss modal UINavigationController
        navigationController?.dismiss(animated: true) { [weak self] in
            guard let self = self else { return }
            // Notify parent coordinator to remove child from childCoordinators array
            self.parentCoordinator?.childDidFinish(self)
        }
    }
    
    // MARK: - Deinitialization
    
    deinit {
        print("deinit FoundPetReportCoordinator")
    }
}

// MARK: - Private Helpers

private extension FoundPetReportCoordinator {
    /// Configures progress indicator as plain text in navigation bar.
    /// Displays current step fraction (e.g., "1/4", "2/4") as text on right side.
    /// - Parameters:
    ///   - hostingController: Hosting controller to configure
    ///   - step: Current step number (1-4)
    ///   - total: Total steps (always 4)
    func configureProgressIndicator(
        hostingController: UIHostingController<some View>,
        step: Int,
        total: Int
    ) {
        // Create label with localized progress text
        let label = UILabel()
        label.text = L10n.ReportMissingPet.Progress.format(step, total)
        label.font = UIFont.systemFont(ofSize: 17, weight: .regular)
        label.textColor = UIColor(hex: "#2D2D2D") // Dark gray
        label.sizeToFit()
        
        // Wrap in bar button item
        let barButtonItem = UIBarButtonItem(customView: label)
        hostingController.navigationItem.rightBarButtonItem = barButtonItem
        
        // Test identifier for E2E tests
        label.accessibilityIdentifier = "reportFoundPet.progressIndicator"
    }
    
    /// Configures custom back button (chevron-left) in navigation bar.
    /// Replaces default system back button with custom chevron-left icon for consistent styling.
    /// - Parameters:
    ///   - hostingController: Hosting controller to configure
    ///   - action: Action to execute when back button tapped
    func configureCustomBackButton(
        hostingController: UIHostingController<some View>,
        action: @escaping () -> Void
    ) {
        // Create chevron-left button
        let backButton = UIButton(type: .system)
        backButton.setImage(UIImage(systemName: "chevron.left"), for: .normal)
        backButton.tintColor = UIColor(hex: "#2D2D2D") // Dark gray
        backButton.addAction(UIAction { _ in
            action()
        }, for: .touchUpInside)
        
        // Wrap in bar button item
        let backBarButtonItem = UIBarButtonItem(customView: backButton)
        hostingController.navigationItem.leftBarButtonItem = backBarButtonItem
        
        // Test identifier for E2E tests
        backButton.accessibilityIdentifier = "reportFoundPet.backButton"
    }
    
    /// Configures custom dismiss button (X icon) in navigation bar.
    /// Used on first screen to indicate modal dismissal instead of backward navigation.
    /// - Parameters:
    ///   - hostingController: Hosting controller to configure
    ///   - action: Action to execute when dismiss button tapped
    func configureCustomDismissButton(
        hostingController: UIHostingController<some View>,
        action: @escaping () -> Void
    ) {
        // Create X (xmark) button
        let dismissButton = UIButton(type: .system)
        dismissButton.setImage(UIImage(systemName: "xmark"), for: .normal)
        dismissButton.tintColor = UIColor(hex: "#2D2D2D") // Dark gray
        dismissButton.addAction(UIAction { _ in
            action()
        }, for: .touchUpInside)
        
        // Wrap in bar button item
        let dismissBarButtonItem = UIBarButtonItem(customView: dismissButton)
        hostingController.navigationItem.leftBarButtonItem = dismissBarButtonItem
        
        // Test identifier for E2E tests
        dismissButton.accessibilityIdentifier = "foundPet.microchip.backButton"
    }
}

