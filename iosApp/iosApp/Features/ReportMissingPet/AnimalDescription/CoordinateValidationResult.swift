import Foundation

/// Result of coordinate validation for latitude and longitude fields.
enum CoordinateValidationResult: Equatable {
    case valid
    case invalid(latError: String?, longError: String?)
    
    var isValid: Bool {
        if case .valid = self {
            return true
        }
        return false
    }
}

