import Foundation

/// ViewModel for Chip Number screen (Step 1/4).
/// Minimal implementation - only navigation callbacks (no form logic yet).
@MainActor
class ChipNumberViewModel: ObservableObject {
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
    
    /// Navigate to next screen (Photo).
    /// TODO: Save chip number to flowState in future implementation.
    func handleNext() {
        onNext?()
    }
    
    /// Navigate back (exit flow from step 1).
    func handleBack() {
        onBack?()
    }
    
    // MARK: - Deinitialization
    
    deinit {
        print("deinit ChipNumberViewModel")
    }
}

