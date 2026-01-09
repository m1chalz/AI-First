import Foundation
import UIKit

/// ViewModel for Summary screen (Step 5 - Report Created Confirmation).
/// Displays confirmation messaging, management password, and handles clipboard copy.
@MainActor
class FoundPetSummaryViewModel: ObservableObject {
    // MARK: - Dependencies
    
    private let flowState: FoundPetReportFlowState
    private let toastScheduler: ToastSchedulerProtocol
    
    // MARK: - Coordinator Communication
    
    var onClose: (() -> Void)?
    
    // MARK: - Published Properties
    
    /// Controls toast visibility for clipboard copy confirmation
    @Published var showsCodeCopiedToast = false
    
    // MARK: - Computed Properties
    
    /// Management password for display (empty string if nil)
    var displayPassword: String {
        flowState.managementPassword ?? ""
    }
    
    // MARK: - Initialization
    
    init(flowState: FoundPetReportFlowState, toastScheduler: ToastSchedulerProtocol) {
        self.flowState = flowState
        self.toastScheduler = toastScheduler
    }
    
    // MARK: - Actions
    
    /// Closes the summary screen and exits the entire report flow.
    func handleClose() {
        onClose?()
    }
    
    /// Copies management password to clipboard and shows toast confirmation
    func copyPasswordToClipboard() {
        let password = displayPassword
        guard !password.isEmpty else { return }
        
        UIPasteboard.general.string = password
        toastScheduler.cancel()
        showsCodeCopiedToast = true
        toastScheduler.schedule(duration: 2.0) { [weak self] in
            Task { @MainActor in
                self?.showsCodeCopiedToast = false
            }
        }
    }
    
    // MARK: - Deinitialization
    
    deinit {
        toastScheduler.cancel()
        print("deinit FoundPetSummaryViewModel")
    }
}

