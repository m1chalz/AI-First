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
