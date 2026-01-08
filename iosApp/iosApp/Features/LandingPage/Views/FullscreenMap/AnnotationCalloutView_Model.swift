import Foundation
import CoreLocation

/// Extension providing the presentation Model for AnnotationCalloutView.
/// Contains formatted strings ready for UI display.
extension AnnotationCalloutView {
    
    /// Presentation model for annotation callout card.
    /// Contains pre-formatted strings for direct UI binding.
    struct Model {
        /// Valid photo URL or nil for placeholder display
        let photoUrl: String?
        
        /// Pet name (never nil in display - uses fallback)
        let petName: String
        
        /// Formatted as "Species • Breed" or just "Species" if breed is nil
        let speciesAndBreed: String
        
        /// Formatted as "📍 52.2297° N, 21.0122° E"
        let locationText: String
        
        /// Formatted as "📅 Jan 08, 2025"
        let dateText: String
        
        /// Formatted as "📧 email@example.com" or nil if not available
        let emailText: String?
        
        /// Formatted as "📞 (555) 123-4567" or nil if not available
        let phoneText: String?
        
        /// Description text or nil if not available
        let descriptionText: String?
        
        /// Status text ("MISSING", "FOUND", or "CLOSED")
        let statusText: String
        
        /// Status badge color hex (e.g., "#FF9500")
        let statusColorHex: String
        
        /// Accessibility identifier for the callout root view
        let accessibilityId: String
        
        /// Memberwise initializer for testing and previews.
        init(
            photoUrl: String?,
            petName: String,
            speciesAndBreed: String,
            locationText: String,
            dateText: String,
            emailText: String?,
            phoneText: String?,
            descriptionText: String?,
            statusText: String,
            statusColorHex: String,
            accessibilityId: String
        ) {
            self.photoUrl = photoUrl
            self.petName = petName
            self.speciesAndBreed = speciesAndBreed
            self.locationText = locationText
            self.dateText = dateText
            self.emailText = emailText
            self.phoneText = phoneText
            self.descriptionText = descriptionText
            self.statusText = statusText
            self.statusColorHex = statusColorHex
            self.accessibilityId = accessibilityId
        }
        
        /// Creates a presentation Model from a MapPin.
        /// - Parameter pin: Source pin with all announcement data
        init(from pin: MapPin) {
            // Photo URL validation - empty string becomes nil for placeholder
            self.photoUrl = pin.photoUrl.isEmpty ? nil : pin.photoUrl
            
            // Pet name with localized fallback
            self.petName = pin.petName ?? L10n.AnnotationCallout.unknownPet
            
            // Species • Breed formatting (omit breed if nil)
            let speciesName = pin.species.displayName
            if let breed = pin.breed {
                self.speciesAndBreed = "\(speciesName) • \(breed)"
            } else {
                self.speciesAndBreed = speciesName
            }
            
            // Location with emoji prefix (uses shared CoordinateFormatting utility)
            self.locationText = "📍 \(CoordinateFormatting.formatCoordinates(pin.coordinate))"
            
            // Date with emoji prefix (uses shared DateFormatting utility)
            self.dateText = "📅 \(DateFormatting.formatDate(pin.lastSeenDate))"
            
            // Optional contact fields with emoji prefixes (FR-007, FR-008)
            self.emailText = pin.ownerEmail.map { "📧 \($0)" }
            self.phoneText = pin.ownerPhone.map { "📞 \($0)" }
            
            // Optional description (FR-006 - omit if nil)
            self.descriptionText = pin.petDescription
            
            // Status badge - reuse existing L10n via AnnouncementStatus+Presentation.displayName
            self.statusText = pin.status.displayName
            self.statusColorHex = pin.status.annotationBadgeColorHex
            
            self.accessibilityId = "fullscreenMap.annotation.\(pin.id)"
        }
    }
}

// Note: Date and coordinate formatting now uses shared utilities:
// - DateFormatting.formatDate(_:) in FoundationAdditions/DateFormatting.swift
// - CoordinateFormatting.formatCoordinates(_:) in FoundationAdditions/CoordinateFormatting.swift

