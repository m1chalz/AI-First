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
}

