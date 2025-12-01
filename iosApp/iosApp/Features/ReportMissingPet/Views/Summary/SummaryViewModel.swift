import Foundation

/// ViewModel for Summary screen (Step 5 - no progress indicator).
/// Minimal implementation - only navigation callbacks (no data display logic yet).
@MainActor
class SummaryViewModel: ObservableObject {
    // MARK: - Dependencies
    
    private let flowState: ReportMissingPetFlowState
    
    // MARK: - Coordinator Communication
    
    var onSubmit: (() -> Void)?
    var onBack: (() -> Void)?
    
    // MARK: - Initialization
    
    init(flowState: ReportMissingPetFlowState) {
        self.flowState = flowState
    }
    
    // MARK: - Actions
    
    /// Submit report (placeholder - no backend yet).
    /// Exits flow after submission.
    func handleSubmit() {
        onSubmit?()
    }
    
    /// Navigate back to previous screen (Contact Details).
    func handleBack() {
        onBack?()
    }
    
    // MARK: - Deinitialization
    
    deinit {
        print("deinit SummaryViewModel")
    }
}

