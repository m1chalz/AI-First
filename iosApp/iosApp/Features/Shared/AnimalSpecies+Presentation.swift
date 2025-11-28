import Foundation

extension AnimalSpecies {
    /// Human-readable display name for the species
    var displayName: String {
        switch self {
        case .dog: return L10n.AnimalSpecies.dog
        case .cat: return L10n.AnimalSpecies.cat
        case .bird: return L10n.AnimalSpecies.bird
        case .rabbit: return L10n.AnimalSpecies.rabbit
        case .rodent: return L10n.AnimalSpecies.rodent
        case .reptile: return L10n.AnimalSpecies.reptile
        case .other: return L10n.AnimalSpecies.other
        }
    }
}

