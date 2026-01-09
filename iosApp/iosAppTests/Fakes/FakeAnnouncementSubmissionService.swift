import Foundation
@testable import PetSpot

/// Fake implementation of AnnouncementSubmissionServiceProtocol for unit testing
class FakeAnnouncementSubmissionService: AnnouncementSubmissionServiceProtocol {
    // MARK: - Test Configuration
    
    var shouldThrow = false
    var throwsError: Error?
    var mockManagementPassword = "123456"
    
    // MARK: - Call Tracking
    
    var submitAnnouncementCalled = false
    var lastFlowState: (any PetReportFlowStateProtocol)?
    
    // MARK: - Service Methods
    
    @MainActor
    func submitAnnouncement(flowState: any PetReportFlowStateProtocol) async throws -> String {
        submitAnnouncementCalled = true
        lastFlowState = flowState
        
        if shouldThrow {
            throw throwsError ?? RepositoryError.networkError(NSError(domain: "Test", code: -1))
        }
        
        return mockManagementPassword
    }
}

