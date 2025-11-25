// swiftlint:disable all
// Generated using SwiftGen — https://github.com/SwiftGen/SwiftGen

import Foundation

// swiftlint:disable superfluous_disable_command file_length implicit_return prefer_self_in_static_references

// MARK: - Strings

// swiftlint:disable explicit_type_interface function_parameter_count identifier_name line_length
// swiftlint:disable nesting type_body_length type_name vertical_whitespace_opening_braces
public enum L10n {
  public enum AnimalCard {
    public enum Location {
      /// MARK: - Animal Card
      public static func format(_ p1: Any, _ p2: Int) -> String {
        return L10n.tr("Localizable", "animalCard.location.format", String(describing: p1), p2, fallback: "%@, +%dkm")
      }
    }
  }
  public enum AnimalGender {
    /// Female
    public static let female = L10n.tr("Localizable", "animalGender.female", fallback: "Female")
    /// MARK: - Animal Gender
    public static let male = L10n.tr("Localizable", "animalGender.male", fallback: "Male")
    /// Unknown
    public static let unknown = L10n.tr("Localizable", "animalGender.unknown", fallback: "Unknown")
  }
  public enum AnimalList {
    /// MARK: - Animal List Screen
    public static let navigationTitle = L10n.tr("Localizable", "animalList.navigationTitle", fallback: "Missing Animals")
    public enum Button {
      /// Report Found Animal
      public static let reportFound = L10n.tr("Localizable", "animalList.button.reportFound", fallback: "Report Found Animal")
      /// Report a Missing Animal
      public static let reportMissing = L10n.tr("Localizable", "animalList.button.reportMissing", fallback: "Report a Missing Animal")
    }
    public enum EmptyState {
      /// No animals reported yet. Tap 'Report a Missing Animal' to add the first one.
      public static let message = L10n.tr("Localizable", "animalList.emptyState.message", fallback: "No animals reported yet. Tap 'Report a Missing Animal' to add the first one.")
    }
    public enum Error {
      /// Something went wrong. Please try again.
      public static let generic = L10n.tr("Localizable", "animalList.error.generic", fallback: "Something went wrong. Please try again.")
      /// No internet connection. Please check your network settings.
      public static let networkUnavailable = L10n.tr("Localizable", "animalList.error.networkUnavailable", fallback: "No internet connection. Please check your network settings.")
      /// Error: %@
      public static func `prefix`(_ p1: Any) -> String {
        return L10n.tr("Localizable", "animalList.error.prefix", String(describing: p1), fallback: "Error: %@")
      }
      /// Error
      public static let title = L10n.tr("Localizable", "animalList.error.title", fallback: "Error")
    }
    public enum Loading {
      /// Loading animals...
      public static let message = L10n.tr("Localizable", "animalList.loading.message", fallback: "Loading animals...")
    }
  }
  public enum AnimalSpecies {
    /// Bird
    public static let bird = L10n.tr("Localizable", "animalSpecies.bird", fallback: "Bird")
    /// Cat
    public static let cat = L10n.tr("Localizable", "animalSpecies.cat", fallback: "Cat")
    /// MARK: - Animal Species
    public static let dog = L10n.tr("Localizable", "animalSpecies.dog", fallback: "Dog")
    /// Other
    public static let other = L10n.tr("Localizable", "animalSpecies.other", fallback: "Other")
    /// Rabbit
    public static let rabbit = L10n.tr("Localizable", "animalSpecies.rabbit", fallback: "Rabbit")
  }
  public enum AnimalStatus {
    /// MARK: - Animal Status
    public static let active = L10n.tr("Localizable", "animalStatus.active", fallback: "Active")
    /// Closed
    public static let closed = L10n.tr("Localizable", "animalStatus.closed", fallback: "Closed")
    /// Found
    public static let found = L10n.tr("Localizable", "animalStatus.found", fallback: "Found")
    public enum Badge {
      /// CLOSED
      public static let closed = L10n.tr("Localizable", "animalStatus.badge.closed", fallback: "CLOSED")
      /// FOUND
      public static let found = L10n.tr("Localizable", "animalStatus.badge.found", fallback: "FOUND")
      /// MISSING
      public static let missing = L10n.tr("Localizable", "animalStatus.badge.missing", fallback: "MISSING")
    }
  }
  public enum Common {
    /// Cancel
    public static let cancel = L10n.tr("Localizable", "common.cancel", fallback: "Cancel")
    /// MARK: - Common
    public static let loading = L10n.tr("Localizable", "common.loading", fallback: "Loading...")
    /// OK
    public static let ok = L10n.tr("Localizable", "common.ok", fallback: "OK")
    /// Retry
    public static let retry = L10n.tr("Localizable", "common.retry", fallback: "Retry")
  }
  public enum PetDetails {
    public enum Button {
      /// Remove Report
      public static let removeReport = L10n.tr("Localizable", "petDetails.button.removeReport", fallback: "Remove Report")
      /// Show on the map
      public static let showOnMap = L10n.tr("Localizable", "petDetails.button.showOnMap", fallback: "Show on the map")
    }
    public enum Error {
      /// Failed to load pet details
      public static let title = L10n.tr("Localizable", "petDetails.error.title", fallback: "Failed to load pet details")
    }
    public enum Label {
      /// Animal Additional Description
      public static let additionalDescription = L10n.tr("Localizable", "petDetails.label.additionalDescription", fallback: "Animal Additional Description")
      /// Animal Approx. Age
      public static let animalAge = L10n.tr("Localizable", "petDetails.label.animalAge", fallback: "Animal Approx. Age")
      /// Animal Name
      public static let animalName = L10n.tr("Localizable", "petDetails.label.animalName", fallback: "Animal Name")
      /// Animal Race
      public static let animalRace = L10n.tr("Localizable", "petDetails.label.animalRace", fallback: "Animal Race")
      /// Animal Sex
      public static let animalSex = L10n.tr("Localizable", "petDetails.label.animalSex", fallback: "Animal Sex")
      /// Animal Species
      public static let animalSpecies = L10n.tr("Localizable", "petDetails.label.animalSpecies", fallback: "Animal Species")
      /// Contact owner
      public static let contactOwner = L10n.tr("Localizable", "petDetails.label.contactOwner", fallback: "Contact owner")
      /// Date of Disappearance
      public static let dateOfDisappearance = L10n.tr("Localizable", "petDetails.label.dateOfDisappearance", fallback: "Date of Disappearance")
      /// Microchip number
      public static let microchipNumber = L10n.tr("Localizable", "petDetails.label.microchipNumber", fallback: "Microchip number")
      /// Place of Disappearance / City
      public static let placeOfDisappearance = L10n.tr("Localizable", "petDetails.label.placeOfDisappearance", fallback: "Place of Disappearance / City")
      /// Vaccination ID
      public static let vaccinationId = L10n.tr("Localizable", "petDetails.label.vaccinationId", fallback: "Vaccination ID")
    }
    public enum Loading {
      /// MARK: - Pet Details Screen
      public static let message = L10n.tr("Localizable", "petDetails.loading.message", fallback: "Loading pet details...")
    }
    public enum Location {
      /// ±%d km
      public static func radiusFormat(_ p1: Int) -> String {
        return L10n.tr("Localizable", "petDetails.location.radiusFormat", p1, fallback: "±%d km")
      }
    }
    public enum Photo {
      /// Image not available
      public static let notAvailable = L10n.tr("Localizable", "petDetails.photo.notAvailable", fallback: "Image not available")
    }
    public enum Reward {
      /// Reward
      public static let label = L10n.tr("Localizable", "petDetails.reward.label", fallback: "Reward")
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
