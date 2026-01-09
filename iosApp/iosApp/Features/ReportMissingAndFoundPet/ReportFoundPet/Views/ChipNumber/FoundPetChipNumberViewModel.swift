import Foundation

/// ViewModel for Chip Number screen (Step 1/4) handling formatting and persistence.
@MainActor
class FoundPetChipNumberViewModel: ObservableObject {
    // MARK: - Published State
    
    @Published var chipNumber: String = ""
    
    // MARK: - Dependencies
    
    private let flowState: FoundPetReportFlowState
    
    // MARK: - Coordinator Communication
    
    var onNext: (() -> Void)?
    var onBack: (() -> Void)?
    
    // MARK: - Initialization
    
    init(flowState: FoundPetReportFlowState) {
        self.flowState = flowState
        
        if let savedDigits = flowState.chipNumber {
            chipNumber = MicrochipNumberFormatter.format(savedDigits)
        }
    }
    
    // MARK: - Formatting
    
    /// Formats provided input and updates the published chip number.
    func formatChipNumber(_ input: String) {
        let formatted = MicrochipNumberFormatter.format(input)
        guard formatted != chipNumber else { return }
        chipNumber = formatted
    }
    
    // MARK: - Actions
    
    /// Navigate to next screen (Photo) and persist digits to flow state.
    func handleNext() {
        let digits = MicrochipNumberFormatter.extractDigits(chipNumber)
        flowState.chipNumber = digits.isEmpty ? nil : digits
        onNext?()
    }
    
    /// Navigate back (exit flow from step 1).
    func handleBack() {
        onBack?()
    }
    
    // MARK: - Deinitialization
    
    deinit {
        print("deinit FoundPetChipNumberViewModel")
    }
}

