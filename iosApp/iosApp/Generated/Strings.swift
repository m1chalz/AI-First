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
  public enum AnimalDescription {
    /// Age (optional)
    public static let ageLabel = L10n.tr("Localizable", "animalDescription.ageLabel", fallback: "Age (optional)")
    /// e.g., 5
    public static let agePlaceholder = L10n.tr("Localizable", "animalDescription.agePlaceholder", fallback: "e.g., 5")
    /// %d/%d
    public static func characterCount(_ p1: Int, _ p2: Int) -> String {
      return L10n.tr("Localizable", "animalDescription.characterCount", p1, p2, fallback: "%d/%d")
    }
    /// Continue
    public static let continueButton = L10n.tr("Localizable", "animalDescription.continueButton", fallback: "Continue")
    /// 00000
    public static let coordinatePlaceholder = L10n.tr("Localizable", "animalDescription.coordinatePlaceholder", fallback: "00000")
    /// Lat / Long
    public static let coordinatesLabel = L10n.tr("Localizable", "animalDescription.coordinatesLabel", fallback: "Lat / Long")
    /// MARK: - Animal Description Screen
    public static let dateLabel = L10n.tr("Localizable", "animalDescription.dateLabel", fallback: "Date of disappearance")
    /// Additional description (optional)
    public static let descriptionLabel = L10n.tr("Localizable", "animalDescription.descriptionLabel", fallback: "Additional description (optional)")
    /// Describe distinguishing features...
    public static let descriptionPlaceholder = L10n.tr("Localizable", "animalDescription.descriptionPlaceholder", fallback: "Describe distinguishing features...")
    /// Gender
    public static let genderLabel = L10n.tr("Localizable", "animalDescription.genderLabel", fallback: "Gender")
    /// Location captured successfully
    public static let gpsHelperText = L10n.tr("Localizable", "animalDescription.gpsHelperText", fallback: "Location captured successfully")
    /// e.g., 52.2297
    public static let latitudePlaceholder = L10n.tr("Localizable", "animalDescription.latitudePlaceholder", fallback: "e.g., 52.2297")
    /// e.g., 21.0122
    public static let longitudePlaceholder = L10n.tr("Localizable", "animalDescription.longitudePlaceholder", fallback: "e.g., 21.0122")
    /// Animal race (optional)
    public static let raceLabel = L10n.tr("Localizable", "animalDescription.raceLabel", fallback: "Animal race (optional)")
    /// e.g., Golden Retriever
    public static let racePlaceholder = L10n.tr("Localizable", "animalDescription.racePlaceholder", fallback: "e.g., Golden Retriever")
    /// Request GPS position
    public static let requestGPSButton = L10n.tr("Localizable", "animalDescription.requestGPSButton", fallback: "Request GPS position")
    /// Animal species
    public static let speciesLabel = L10n.tr("Localizable", "animalDescription.speciesLabel", fallback: "Animal species")
    /// Select an option
    public static let speciesPlaceholder = L10n.tr("Localizable", "animalDescription.speciesPlaceholder", fallback: "Select an option")
    public enum DatePicker {
      /// Done
      public static let done = L10n.tr("Localizable", "animalDescription.datePicker.done", fallback: "Done")
      /// Select Date
      public static let title = L10n.tr("Localizable", "animalDescription.datePicker.title", fallback: "Select Date")
    }
    public enum Error {
      /// Age must be between 0 and 40
      public static let invalidAge = L10n.tr("Localizable", "animalDescription.error.invalidAge", fallback: "Age must be between 0 and 40")
      /// Invalid coordinate format
      public static let invalidCoordinateFormat = L10n.tr("Localizable", "animalDescription.error.invalidCoordinateFormat", fallback: "Invalid coordinate format")
      /// Latitude must be between -90 and 90
      public static let invalidLatitude = L10n.tr("Localizable", "animalDescription.error.invalidLatitude", fallback: "Latitude must be between -90 and 90")
      /// Longitude must be between -180 and 180
      public static let invalidLongitude = L10n.tr("Localizable", "animalDescription.error.invalidLongitude", fallback: "Longitude must be between -180 and 180")
      /// Please select a date
      public static let missingDate = L10n.tr("Localizable", "animalDescription.error.missingDate", fallback: "Please select a date")
      /// Please select a gender
      public static let missingGender = L10n.tr("Localizable", "animalDescription.error.missingGender", fallback: "Please select a gender")
      /// Please enter the breed or race
      public static let missingRace = L10n.tr("Localizable", "animalDescription.error.missingRace", fallback: "Please enter the breed or race")
      /// Please select a species
      public static let missingSpecies = L10n.tr("Localizable", "animalDescription.error.missingSpecies", fallback: "Please select a species")
    }
    public enum Toast {
      /// Please correct the highlighted fields
      public static let validationErrors = L10n.tr("Localizable", "animalDescription.toast.validationErrors", fallback: "Please correct the highlighted fields")
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
  public enum AnimalPhoto {
    /// MARK: - Animal Photo Screen
    public static let title = L10n.tr("Localizable", "animalPhoto.title", fallback: "Your pet's photo")
    public enum Alert {
      public enum Missing {
        /// Attach a JPG, PNG, HEIC, GIF or WEBP file before continuing.
        public static let message = L10n.tr("Localizable", "animalPhoto.alert.missing.message", fallback: "Attach a JPG, PNG, HEIC, GIF or WEBP file before continuing.")
        /// Photo required
        public static let title = L10n.tr("Localizable", "animalPhoto.alert.missing.title", fallback: "Photo required")
      }
    }
    public enum Banner {
      /// Try again
      public static let retry = L10n.tr("Localizable", "animalPhoto.banner.retry", fallback: "Try again")
      /// Open Settings
      public static let settings = L10n.tr("Localizable", "animalPhoto.banner.settings", fallback: "Open Settings")
    }
    public enum Button {
      /// Browse
      public static let browse = L10n.tr("Localizable", "animalPhoto.button.browse", fallback: "Browse")
      /// Continue
      public static let `continue` = L10n.tr("Localizable", "animalPhoto.button.continue", fallback: "Continue")
      /// Remove
      public static let remove = L10n.tr("Localizable", "animalPhoto.button.remove", fallback: "Remove")
    }
    public enum Helper {
      /// Downloading from iCloud…
      public static let loading = L10n.tr("Localizable", "animalPhoto.helper.loading", fallback: "Downloading from iCloud…")
      /// Select a photo to continue – you can browse again anytime.
      public static let pickerCancelled = L10n.tr("Localizable", "animalPhoto.helper.pickerCancelled", fallback: "Select a photo to continue – you can browse again anytime.")
      /// Please upload a photo of the missing animal.
      public static let `required` = L10n.tr("Localizable", "animalPhoto.helper.required", fallback: "Please upload a photo of the missing animal.")
    }
    public enum Tile {
      /// JPEG, PNG, HEIC, GIF, WEBP • Max 10MB
      public static let subtitle = L10n.tr("Localizable", "animalPhoto.tile.subtitle", fallback: "JPEG, PNG, HEIC, GIF, WEBP • Max 10MB")
      /// Upload animal photo
      public static let title = L10n.tr("Localizable", "animalPhoto.tile.title", fallback: "Upload animal photo")
    }
    public enum Toast {
      /// Photo is mandatory
      public static let mandatory = L10n.tr("Localizable", "animalPhoto.toast.mandatory", fallback: "Photo is mandatory")
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
    /// Reptile
    public static let reptile = L10n.tr("Localizable", "animalSpecies.reptile", fallback: "Reptile")
    /// Rodent
    public static let rodent = L10n.tr("Localizable", "animalSpecies.rodent", fallback: "Rodent")
  }
  public enum AnimalStatus {
    /// MARK: - Animal Status
    public static let active = L10n.tr("Localizable", "animalStatus.active", fallback: "MISSING")
    /// CLOSED
    public static let closed = L10n.tr("Localizable", "animalStatus.closed", fallback: "CLOSED")
    /// FOUND
    public static let found = L10n.tr("Localizable", "animalStatus.found", fallback: "FOUND")
  }
  public enum Common {
    /// Back
    public static let back = L10n.tr("Localizable", "common.back", fallback: "Back")
    /// Cancel
    public static let cancel = L10n.tr("Localizable", "common.cancel", fallback: "Cancel")
    /// Continue
    public static let `continue` = L10n.tr("Localizable", "common.continue", fallback: "Continue")
    /// MARK: - Common
    public static let loading = L10n.tr("Localizable", "common.loading", fallback: "Loading...")
    /// OK
    public static let ok = L10n.tr("Localizable", "common.ok", fallback: "OK")
    /// Retry
    public static let retry = L10n.tr("Localizable", "common.retry", fallback: "Retry")
  }
  public enum Location {
    public enum Permission {
      public enum Popup {
        /// Enable location access in Settings to see nearby pets.
        public static let message = L10n.tr("Localizable", "location.permission.popup.message", fallback: "Enable location access in Settings to see nearby pets.")
        /// MARK: - Location Permission Popup
        public static let title = L10n.tr("Localizable", "location.permission.popup.title", fallback: "Location Access Needed")
        public enum Cancel {
          /// Cancel
          public static let button = L10n.tr("Localizable", "location.permission.popup.cancel.button", fallback: "Cancel")
        }
        public enum Settings {
          /// Go to Settings
          public static let button = L10n.tr("Localizable", "location.permission.popup.settings.button", fallback: "Go to Settings")
        }
      }
    }
  }
  public enum MicrochipNumber {
    /// Continue
    public static let continueButton = L10n.tr("Localizable", "microchipNumber.continueButton", fallback: "Continue")
    /// Microchip identification is the most efficient way to reunite with your pet. If your pet has been microchipped and you know the microchip number, please enter it here.
    public static let description = L10n.tr("Localizable", "microchipNumber.description", fallback: "Microchip identification is the most efficient way to reunite with your pet. If your pet has been microchipped and you know the microchip number, please enter it here.")
    /// Microchip number (optional)
    public static let fieldLabel = L10n.tr("Localizable", "microchipNumber.fieldLabel", fallback: "Microchip number (optional)")
    /// 00000-00000-00000
    public static let fieldPlaceholder = L10n.tr("Localizable", "microchipNumber.fieldPlaceholder", fallback: "00000-00000-00000")
    /// Microchip number
    public static let heading = L10n.tr("Localizable", "microchipNumber.heading", fallback: "Microchip number")
    /// 1/4
    public static let progress = L10n.tr("Localizable", "microchipNumber.progress", fallback: "1/4")
    /// MARK: - Microchip Number Screen
    public static let title = L10n.tr("Localizable", "microchipNumber.title", fallback: "Microchip number")
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
      /// Lat / Long
      public static let placeOfDisappearance = L10n.tr("Localizable", "petDetails.label.placeOfDisappearance", fallback: "Lat / Long")
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
  public enum ReportMissingPet {
    public enum Button {
      /// Continue
      public static let `continue` = L10n.tr("Localizable", "reportMissingPet.button.continue", fallback: "Continue")
      /// Submit
      public static let submit = L10n.tr("Localizable", "reportMissingPet.button.submit", fallback: "Submit")
    }
    public enum ChipNumber {
      /// 00000-00000-00000
      public static let placeholder = L10n.tr("Localizable", "reportMissingPet.chipNumber.placeholder", fallback: "00000-00000-00000")
      /// MARK: - Report Missing Pet Flow
      public static let title = L10n.tr("Localizable", "reportMissingPet.chipNumber.title", fallback: "Microchip number")
    }
    public enum ContactDetails {
      /// Contact Details
      public static let title = L10n.tr("Localizable", "reportMissingPet.contactDetails.title", fallback: "Contact Details")
    }
    public enum Description {
      /// Description
      public static let title = L10n.tr("Localizable", "reportMissingPet.description.title", fallback: "Description")
    }
    public enum Photo {
      /// Pet Photo
      public static let title = L10n.tr("Localizable", "reportMissingPet.photo.title", fallback: "Pet Photo")
    }
    public enum Progress {
      /// %d/%d
      public static func format(_ p1: Int, _ p2: Int) -> String {
        return L10n.tr("Localizable", "reportMissingPet.progress.format", p1, p2, fallback: "%d/%d")
      }
    }
    public enum Summary {
      /// Summary
      public static let title = L10n.tr("Localizable", "reportMissingPet.summary.title", fallback: "Summary")
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
