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
    
    // MARK: - Initialization
    
    init(flowState: ReportMissingPetFlowState) {
        self.flowState = flowState
    }
    
    // MARK: - Actions
    
    /// Navigate to next screen (Summary).
    /// TODO: Save contact details to flowState in future implementation.
    func handleNext() {
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

