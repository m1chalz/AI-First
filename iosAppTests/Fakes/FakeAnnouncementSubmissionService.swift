import Foundation
@testable import iosApp

/// Fake implementation of AnnouncementSubmissionService for unit testing
class FakeAnnouncementSubmissionService {
    // MARK: - Test Configuration
    
    var shouldThrow = false
    var throwsError: Error?
    var mockManagementPassword = "123456"
    
    // MARK: - Call Tracking
    
    var submitAnnouncementCalled = false
    var lastFlowState: ReportMissingPetFlowState?
    
    // MARK: - Service Methods
    
    @MainActor
    func submitAnnouncement(flowState: ReportMissingPetFlowState) async throws -> String {
        submitAnnouncementCalled = true
        lastFlowState = flowState
        
        if shouldThrow {
            throw throwsError ?? RepositoryError.networkError(NSError(domain: "Test", code: -1))
        }
        
        return mockManagementPassword
    }
}

