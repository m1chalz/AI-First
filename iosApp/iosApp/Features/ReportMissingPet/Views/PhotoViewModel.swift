import Foundation

/// ViewModel for Photo screen (Step 2/4).
/// Minimal implementation - only navigation callbacks (no photo picker logic yet).
@MainActor
class PhotoViewModel: ObservableObject {
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
    
    /// Navigate to next screen (Description).
    /// TODO: Save photo to flowState in future implementation.
    func handleNext() {
        onNext?()
    }
    
    /// Navigate back to previous screen (Chip Number).
    func handleBack() {
        onBack?()
    }
    
    // MARK: - Deinitialization
    
    deinit {
        print("deinit PhotoViewModel")
    }
}

