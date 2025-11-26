import UIKit
import SwiftUI

/// Coordinator for Missing Pet Report modal flow.
/// Creates and manages own UINavigationController for modal presentation.
/// Owns ReportMissingPetFlowState and injects it into all ViewModels.
class ReportMissingPetCoordinator: CoordinatorInterface {
    // MARK: - Properties
    
    weak var parentCoordinator: CoordinatorInterface?
    var childCoordinators: [CoordinatorInterface] = []
    var navigationController: UINavigationController? // Modal nav controller
    
    private let parentNavigationController: UINavigationController
    private var flowState: ReportMissingPetFlowState?
    
    // MARK: - Initialization
    
    /// Creates coordinator for missing pet report flow.
    /// - Parameter parentNavigationController: Parent navigation controller to present modal from
    init(parentNavigationController: UINavigationController) {
        self.parentNavigationController = parentNavigationController
    }
    
    // MARK: - CoordinatorInterface
    
    /// Starts the missing pet report flow.
    /// Creates modal UINavigationController and presents chip number screen.
    func start(animated: Bool) async {
        // Create dedicated UINavigationController for modal flow
        // This provides independent navigation stack separate from parent
        let modalNavController = UINavigationController()
        self.navigationController = modalNavController
        
        // Configure modal presentation with .fullScreen to prevent swipe-to-dismiss
        modalNavController.modalPresentationStyle = .fullScreen
        
        // Create FlowState as coordinator property (owned by coordinator lifecycle)
        // This state object will be injected into all ViewModels
        let flowState = ReportMissingPetFlowState()
        self.flowState = flowState
        
        // Navigate to chip number screen (step 1/4) - entry point
        navigateToChipNumber()
        
        // Present modally from parent navigation controller
        parentNavigationController.present(modalNavController, animated: animated)
    }
    
    // MARK: - Navigation Methods
    
    /// Navigate to chip number screen (Step 1/4).
    /// Entry point for the flow.
    private func navigateToChipNumber() {
        guard let flowState = flowState,
              let modalNavController = navigationController else { return }
        
        let viewModel = ChipNumberViewModel(flowState: flowState)
        
        viewModel.onNext = { [weak self] in
            self?.navigateToPhoto()
        }
        
        viewModel.onBack = { [weak self] in
            self?.exitFlow()
        }
        
        let view = ChipNumberView(viewModel: viewModel)
        let hostingController = UIHostingController(
            rootView: NavigationBackHiding { view }
        )
        
        // Configure navigation bar
        hostingController.title = L10n.ReportMissingPet.ChipNumber.title
        configureProgressIndicator(hostingController: hostingController, step: 1, total: 4)
        configureCustomDismissButton(hostingController: hostingController, action: { [weak viewModel] in
            viewModel?.handleBack()
        })
        
        // Push to modal nav controller
        modalNavController.setViewControllers([hostingController], animated: false)
    }
    
    /// Navigate to photo screen (Step 2/4).
    private func navigateToPhoto() {
        guard let flowState = flowState,
              let modalNavController = navigationController else { return }
        
        let viewModel = PhotoViewModel(flowState: flowState)
        
        viewModel.onNext = { [weak self] in
            self?.navigateToDescription()
        }
        
        viewModel.onBack = { [weak self] in
            self?.navigationController?.popViewController(animated: true)
        }
        
        let view = PhotoView(viewModel: viewModel)
        let hostingController = UIHostingController(
            rootView: NavigationBackHiding { view }
        )
        
        // Configure navigation bar
        hostingController.title = L10n.ReportMissingPet.Photo.title
        configureProgressIndicator(hostingController: hostingController, step: 2, total: 4)
        configureCustomBackButton(hostingController: hostingController, action: { [weak viewModel] in
            viewModel?.handleBack()
        })
        
        modalNavController.pushViewController(hostingController, animated: true)
    }
    
    /// Navigate to description screen (Step 3/4).
    private func navigateToDescription() {
        guard let flowState = flowState,
              let modalNavController = navigationController else { return }
        
        let viewModel = DescriptionViewModel(flowState: flowState)
        
        viewModel.onNext = { [weak self] in
            self?.navigateToContactDetails()
        }
        
        viewModel.onBack = { [weak self] in
            self?.navigationController?.popViewController(animated: true)
        }
        
        let view = DescriptionView(viewModel: viewModel)
        let hostingController = UIHostingController(
            rootView: NavigationBackHiding { view }
        )
        
        // Configure navigation bar
        hostingController.title = L10n.ReportMissingPet.Description.title
        configureProgressIndicator(hostingController: hostingController, step: 3, total: 4)
        configureCustomBackButton(hostingController: hostingController, action: { [weak viewModel] in
            viewModel?.handleBack()
        })
        
        modalNavController.pushViewController(hostingController, animated: true)
    }
    
    /// Navigate to contact details screen (Step 4/4).
    private func navigateToContactDetails() {
        guard let flowState = flowState,
              let modalNavController = navigationController else { return }
        
        let viewModel = ContactDetailsViewModel(flowState: flowState)
        
        viewModel.onNext = { [weak self] in
            self?.navigateToSummary()
        }
        
        viewModel.onBack = { [weak self] in
            self?.navigationController?.popViewController(animated: true)
        }
        
        let view = ContactDetailsView(viewModel: viewModel)
        let hostingController = UIHostingController(
            rootView: NavigationBackHiding { view }
        )
        
        // Configure navigation bar
        hostingController.title = L10n.ReportMissingPet.ContactDetails.title
        configureProgressIndicator(hostingController: hostingController, step: 4, total: 4)
        configureCustomBackButton(hostingController: hostingController, action: { [weak viewModel] in
            viewModel?.handleBack()
        })
        
        modalNavController.pushViewController(hostingController, animated: true)
    }
    
    /// Navigate to summary screen (Step 5 - No Progress Indicator).
    private func navigateToSummary() {
        guard let flowState = flowState,
              let modalNavController = navigationController else { return }
        
        let viewModel = SummaryViewModel(flowState: flowState)
        
        viewModel.onSubmit = { [weak self] in
            self?.exitFlow() // Placeholder - no backend submission yet
        }
        
        viewModel.onBack = { [weak self] in
            self?.navigationController?.popViewController(animated: true)
        }
        
        let view = SummaryView(viewModel: viewModel)
        let hostingController = UIHostingController(
            rootView: NavigationBackHiding { view }
        )
        
        // Configure navigation bar
        hostingController.title = L10n.ReportMissingPet.Summary.title
        // NO progress indicator on summary screen
        configureCustomBackButton(hostingController: hostingController, action: { [weak viewModel] in
            viewModel?.handleBack()
        })
        
        modalNavController.pushViewController(hostingController, animated: true)
    }
    
    /// Exits the flow: dismisses modal, clears state, notifies parent.
    /// Called when user taps back on step 1 or completes submission.
    func exitFlow() {
        // Clear all form data from flow state before dismissing
        flowState?.clear()
        flowState = nil
        
        // Dismiss modal UINavigationController
        navigationController?.dismiss(animated: true) { [weak self] in
            guard let self = self else { return }
            // Notify parent coordinator to remove child from childCoordinators array
            self.parentCoordinator?.childDidFinish(self)
        }
    }
    
    // MARK: - Helper Methods
    
    /// Configures progress indicator as plain text in navigation bar.
    /// Displays current step fraction (e.g., "1/4", "2/4") as text on right side.
    /// - Parameters:
    ///   - hostingController: Hosting controller to configure
    ///   - step: Current step number (1-4)
    ///   - total: Total steps (always 4)
    private func configureProgressIndicator(
        hostingController: UIHostingController<some View>,
        step: Int,
        total: Int
    ) {
        // Create label with progress text
        let label = UILabel()
        label.text = "\(step)/\(total)"
        label.font = UIFont.systemFont(ofSize: 17, weight: .regular)
        label.textColor = UIColor(hex: "#2D2D2D") // Dark gray
        label.sizeToFit()
        
        // Wrap in bar button item
        let barButtonItem = UIBarButtonItem(customView: label)
        hostingController.navigationItem.rightBarButtonItem = barButtonItem
        
        // Accessibility
        label.accessibilityIdentifier = "reportMissingPet.progressIndicator"
        label.accessibilityLabel = L10n.ReportMissingPet.Progress.accessibilityLabel(step, total)
    }
    
    /// Configures custom back button (chevron-left) in navigation bar.
    /// Replaces default system back button with custom chevron-left icon for consistent styling.
    /// - Parameters:
    ///   - hostingController: Hosting controller to configure
    ///   - action: Action to execute when back button tapped
    private func configureCustomBackButton(
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
        
        // Accessibility
        backButton.accessibilityIdentifier = "reportMissingPet.backButton"
        backButton.accessibilityLabel = L10n.Common.back
    }
    
    /// Configures custom dismiss button (X icon) in navigation bar.
    /// Used on first screen to indicate modal dismissal instead of backward navigation.
    /// - Parameters:
    ///   - hostingController: Hosting controller to configure
    ///   - action: Action to execute when dismiss button tapped
    private func configureCustomDismissButton(
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
        
        // Accessibility
        dismissButton.accessibilityIdentifier = "reportMissingPet.dismissButton"
        dismissButton.accessibilityLabel = L10n.Common.cancel
    }
    
    // MARK: - Deinitialization
    
    deinit {
        print("deinit ReportMissingPetCoordinator")
    }
}

