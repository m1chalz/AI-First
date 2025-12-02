import Foundation

/// ViewModel for Contact Details screen (Step 4/4).
/// Minimal implementation - only navigation callbacks (no form logic yet).
@MainActor
class ContactDetailsViewModel: ObservableObject {
    // MARK: - Dependencies
    
    private let flowState: ReportMissingPetFlowState
    
    // MARK: - Coordinator Communication
    
    var onNext: (() -> Void)?
    var onBack: (() -> Void)?
    
    /// Callback triggered when user successfully sends report (User Story 3: T066)
    /// Called when user taps Next/Submit button to send the report
    var onReportSent: (() -> Void)?
    
    // MARK: - Initialization
    
    init(flowState: ReportMissingPetFlowState) {
        self.flowState = flowState
    }
    
    // MARK: - Actions
    
    /// Submit report and navigate to summary screen.
    /// User Story 3 (T066): Notifies parent about report submission before showing summary.
    /// TODO: Save contact details to flowState and send to backend in future implementation.
    func handleNext() {
        // Notify parent that report was sent (for list refresh)
        onReportSent?()
        
        // Navigate to summary/confirmation screen
        onNext?()
    }
    
    /// Navigate back to previous screen (Description).
    func handleBack() {
        onBack?()
    }
    
    // MARK: - Deinitialization
    
    deinit {
        print("deinit ContactDetailsViewModel")
    }
}

