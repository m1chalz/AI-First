import Foundation
import UIKit
@testable import PetSpot

/// Fake flow state for testing Animal Description screen.
/// Mimics ReportMissingPetFlowState without actual ObservableObject overhead in tests.
class FakeReportMissingPetFlowState: ReportMissingPetFlowState {
    
    // Override to track state changes in tests
    override init() {
        super.init()
    }
    
    /// Convenience initializer with pre-populated animal description data
    convenience init(
        disappearanceDate: Date? = nil,
        animalSpecies: AnimalSpecies? = nil,
        animalRace: String? = nil,
        animalGender: AnimalGender? = nil,
        animalAge: Int? = nil,
        animalLatitude: Double? = nil,
        animalLongitude: Double? = nil,
        animalAdditionalDescription: String? = nil
    ) {
        self.init()
        self.disappearanceDate = disappearanceDate
        self.animalSpecies = animalSpecies
        self.animalRace = animalRace
        self.animalGender = animalGender
        self.animalAge = animalAge
        self.animalLatitude = animalLatitude
        self.animalLongitude = animalLongitude
        self.animalAdditionalDescription = animalAdditionalDescription
    }
}

