import Foundation

/// Validation errors for Animal Description form fields.
/// Used by ViewModel to track and display field-specific error messages.
enum ValidationError: Equatable {
    case missingDate
    case missingSpecies
    case missingRace
    case missingGender
    case invalidAge(String)         // Error message parameter
    case invalidLatitude(String)    // Error message parameter
    case invalidLongitude(String)   // Error message parameter
    
    /// User-facing error message for display in UI.
    var message: String {
        switch self {
        case .missingDate:
            return L10n.AnimalDescription.Error.missingDate
        case .missingSpecies:
            return L10n.AnimalDescription.Error.missingSpecies
        case .missingRace:
            return L10n.AnimalDescription.Error.missingRace
        case .missingGender:
            return L10n.AnimalDescription.Error.missingGender
        case .invalidAge(let msg):
            return msg
        case .invalidLatitude(let msg):
            return msg
        case .invalidLongitude(let msg):
            return msg
        }
    }
    
    /// Field identifier for mapping errors to UI components.
    var field: FormField {
        switch self {
        case .missingDate: return .date
        case .missingSpecies: return .species
        case .missingRace: return .race
        case .missingGender: return .gender
        case .invalidAge: return .age
        case .invalidLatitude: return .latitude
        case .invalidLongitude: return .longitude
        }
    }
}

