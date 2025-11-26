import Foundation
import UIKit

/// Shared state for Missing Pet Report flow.
/// Owned by ReportMissingPetCoordinator and injected into all ViewModels.
/// Persists data during forward/backward navigation within active session.
class ReportMissingPetFlowState: ObservableObject {
    // MARK: - Step 1: Chip Number
    
    /// Microchip number (optional, formatted as 00000-00000-00000 for display)
    /// Stored as digits-only string (no dashes)
    @Published var chipNumber: String?
    
    // MARK: - Step 2: Photo
    
    /// Selected photo of the pet (optional)
    @Published var photo: UIImage?
    
    // MARK: - Step 3: Description
    
    /// Additional description about the pet (optional, multi-line)
    @Published var description: String?
    
    // MARK: - Step 4: Contact Details
    
    /// Owner's email address (optional)
    @Published var contactEmail: String?
    
    /// Owner's phone number (optional)
    @Published var contactPhone: String?
    
    // MARK: - Initialization
    
    init() {
        // All properties start as nil
    }
    
    // MARK: - Methods
    
    /// Clears all flow state (called when exiting flow)
    func clear() {
        chipNumber = nil
        photo = nil
        description = nil
        contactEmail = nil
        contactPhone = nil
    }
    
    // MARK: - Computed Properties (Validation)
    
    /// Returns true if chip number has been entered
    var hasChipNumber: Bool {
        guard let chip = chipNumber, !chip.isEmpty else { return false }
        return true
    }
    
    /// Returns true if photo has been selected
    var hasPhoto: Bool {
        photo != nil
    }
    
    /// Returns true if description has been entered
    var hasDescription: Bool {
        guard let desc = description, !desc.isEmpty else { return false }
        return true
    }
    
    /// Returns true if at least one contact method provided
    var hasContactInfo: Bool {
        let hasEmail = contactEmail != nil && !(contactEmail?.isEmpty ?? true)
        let hasPhone = contactPhone != nil && !(contactPhone?.isEmpty ?? true)
        return hasEmail || hasPhone
    }
    
    /// Returns formatted chip number with dashes (00000-00000-00000)
    /// Returns nil if chipNumber is nil or empty
    var formattedChipNumber: String? {
        guard let chip = chipNumber, !chip.isEmpty else { return nil }
        
        // Insert dashes at positions 5 and 10
        let digits = chip.filter { $0.isNumber }
        guard digits.count >= 5 else { return digits }
        
        var formatted = ""
        for (index, char) in digits.enumerated() {
            if index == 5 || index == 10 {
                formatted.append("-")
            }
            formatted.append(char)
        }
        
        return formatted
    }
}

