import Foundation

/// ViewModel for Description screen (Step 3/4).
/// Minimal implementation - only navigation callbacks (no form logic yet).
@MainActor
class DescriptionViewModel: ObservableObject {
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
    
    /// Navigate to next screen (Contact Details).
    /// TODO: Save description to flowState in future implementation.
    func handleNext() {
        onNext?()
    }
    
    /// Navigate back to previous screen (Photo).
    func handleBack() {
        onBack?()
    }
}

