// swiftlint:disable all
// Generated using SwiftGen — https://github.com/SwiftGen/SwiftGen

import Foundation

// swiftlint:disable superfluous_disable_command file_length implicit_return prefer_self_in_static_references

// MARK: - Strings

// swiftlint:disable explicit_type_interface function_parameter_count identifier_name line_length
// swiftlint:disable nesting type_body_length type_name vertical_whitespace_opening_braces
internal enum L10n {
  internal enum AnimalCard {
    internal enum Location {
      /// MARK: - Animal Card
      internal static func format(_ p1: Any, _ p2: Int) -> String {
        return L10n.tr("Localizable", "animalCard.location.format", String(describing: p1), p2, fallback: "%@, +%dkm")
      }
    }
  }
  internal enum AnimalGender {
    /// Female
    internal static let female = L10n.tr("Localizable", "animalGender.female", fallback: "Female")
    /// MARK: - Animal Gender
    internal static let male = L10n.tr("Localizable", "animalGender.male", fallback: "Male")
    /// Unknown
    internal static let unknown = L10n.tr("Localizable", "animalGender.unknown", fallback: "Unknown")
  }
  internal enum AnimalList {
    /// MARK: - Animal List Screen
    internal static let navigationTitle = L10n.tr("Localizable", "animalList.navigationTitle", fallback: "Missing Animals")
    internal enum Button {
      /// Report Found Animal
      internal static let reportFound = L10n.tr("Localizable", "animalList.button.reportFound", fallback: "Report Found Animal")
      /// Report a Missing Animal
      internal static let reportMissing = L10n.tr("Localizable", "animalList.button.reportMissing", fallback: "Report a Missing Animal")
    }
    internal enum EmptyState {
      /// No animals reported yet. Tap 'Report a Missing Animal' to add the first one.
      internal static let message = L10n.tr("Localizable", "animalList.emptyState.message", fallback: "No animals reported yet. Tap 'Report a Missing Animal' to add the first one.")
    }
    internal enum Error {
      /// Something went wrong. Please try again.
      internal static let generic = L10n.tr("Localizable", "animalList.error.generic", fallback: "Something went wrong. Please try again.")
      /// No internet connection. Please check your network settings.
      internal static let networkUnavailable = L10n.tr("Localizable", "animalList.error.networkUnavailable", fallback: "No internet connection. Please check your network settings.")
      /// Error: %@
      internal static func `prefix`(_ p1: Any) -> String {
        return L10n.tr("Localizable", "animalList.error.prefix", String(describing: p1), fallback: "Error: %@")
      }
      /// Error
      internal static let title = L10n.tr("Localizable", "animalList.error.title", fallback: "Error")
    }
    internal enum Loading {
      /// Loading animals...
      internal static let message = L10n.tr("Localizable", "animalList.loading.message", fallback: "Loading animals...")
    }
  }
  internal enum AnimalSpecies {
    /// Bird
    internal static let bird = L10n.tr("Localizable", "animalSpecies.bird", fallback: "Bird")
    /// Cat
    internal static let cat = L10n.tr("Localizable", "animalSpecies.cat", fallback: "Cat")
    /// MARK: - Animal Species
    internal static let dog = L10n.tr("Localizable", "animalSpecies.dog", fallback: "Dog")
    /// Other
    internal static let other = L10n.tr("Localizable", "animalSpecies.other", fallback: "Other")
    /// Rabbit
    internal static let rabbit = L10n.tr("Localizable", "animalSpecies.rabbit", fallback: "Rabbit")
  }
  internal enum AnimalStatus {
    /// MARK: - Animal Status
    internal static let active = L10n.tr("Localizable", "animalStatus.active", fallback: "Active")
    /// Closed
    internal static let closed = L10n.tr("Localizable", "animalStatus.closed", fallback: "Closed")
    /// Found
    internal static let found = L10n.tr("Localizable", "animalStatus.found", fallback: "Found")
  }
  internal enum Common {
    /// Cancel
    internal static let cancel = L10n.tr("Localizable", "common.cancel", fallback: "Cancel")
    /// MARK: - Common
    internal static let loading = L10n.tr("Localizable", "common.loading", fallback: "Loading...")
    /// OK
    internal static let ok = L10n.tr("Localizable", "common.ok", fallback: "OK")
    /// Retry
    internal static let retry = L10n.tr("Localizable", "common.retry", fallback: "Retry")
  }
  internal enum PetDetails {
    internal enum Button {
      /// Remove Report
      internal static let removeReport = L10n.tr("Localizable", "petDetails.button.removeReport", fallback: "Remove Report")
      /// Show on the map
      internal static let showOnMap = L10n.tr("Localizable", "petDetails.button.showOnMap", fallback: "Show on the map")
    }
    internal enum Error {
      /// Failed to load pet details
      internal static let title = L10n.tr("Localizable", "petDetails.error.title", fallback: "Failed to load pet details")
    }
    internal enum Label {
      /// Animal Additional Description
      internal static let additionalDescription = L10n.tr("Localizable", "petDetails.label.additionalDescription", fallback: "Animal Additional Description")
      /// Animal Approx. Age
      internal static let animalAge = L10n.tr("Localizable", "petDetails.label.animalAge", fallback: "Animal Approx. Age")
      /// Animal Name
      internal static let animalName = L10n.tr("Localizable", "petDetails.label.animalName", fallback: "Animal Name")
      /// Animal Race
      internal static let animalRace = L10n.tr("Localizable", "petDetails.label.animalRace", fallback: "Animal Race")
      /// Animal Sex
      internal static let animalSex = L10n.tr("Localizable", "petDetails.label.animalSex", fallback: "Animal Sex")
      /// Animal Species
      internal static let animalSpecies = L10n.tr("Localizable", "petDetails.label.animalSpecies", fallback: "Animal Species")
      /// Contact owner
      internal static let contactOwner = L10n.tr("Localizable", "petDetails.label.contactOwner", fallback: "Contact owner")
      /// Date of Disappearance
      internal static let dateOfDisappearance = L10n.tr("Localizable", "petDetails.label.dateOfDisappearance", fallback: "Date of Disappearance")
      /// Microchip number
      internal static let microchipNumber = L10n.tr("Localizable", "petDetails.label.microchipNumber", fallback: "Microchip number")
      /// Place of Disappearance / City
      internal static let placeOfDisappearance = L10n.tr("Localizable", "petDetails.label.placeOfDisappearance", fallback: "Place of Disappearance / City")
      /// Vaccination ID
      internal static let vaccinationId = L10n.tr("Localizable", "petDetails.label.vaccinationId", fallback: "Vaccination ID")
    }
    internal enum Loading {
      /// MARK: - Pet Details Screen
      internal static let message = L10n.tr("Localizable", "petDetails.loading.message", fallback: "Loading pet details...")
    }
    internal enum Location {
      /// ±%d km
      internal static func radiusFormat(_ p1: Int) -> String {
        return L10n.tr("Localizable", "petDetails.location.radiusFormat", p1, fallback: "±%d km")
      }
    }
    internal enum Photo {
      /// Image not available
      internal static let notAvailable = L10n.tr("Localizable", "petDetails.photo.notAvailable", fallback: "Image not available")
    }
    internal enum Reward {
      /// Reward
      internal static let label = L10n.tr("Localizable", "petDetails.reward.label", fallback: "Reward")
    }
  }
}
// swiftlint:enable explicit_type_interface function_parameter_count identifier_name line_length
// swiftlint:enable nesting type_body_length type_name vertical_whitespace_opening_braces

// MARK: - Implementation Details

extension L10n {
  private static func tr(_ table: String, _ key: String, _ args: CVarArg..., fallback value: String) -> String {
    let format = BundleToken.bundle.localizedString(forKey: key, value: value, table: table)
    return String(format: format, locale: Locale.current, arguments: args)
  }
}

// swiftlint:disable convenience_type
private final class BundleToken {
  static let bundle: Bundle = {
    #if SWIFT_PACKAGE
    return Bundle.module
    #else
    return Bundle(for: BundleToken.self)
    #endif
  }()
}
// swiftlint:enable convenience_type
