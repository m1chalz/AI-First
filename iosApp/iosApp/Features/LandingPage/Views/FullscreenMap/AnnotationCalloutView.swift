import SwiftUI

/// Custom annotation callout card displayed above selected map pins.
/// Shows pet details including photo, name, species/breed, location, date,
/// contact info, description, and status badge.
///
/// **Design** (Figma node 1192:5893):
/// - White card with 12px corner radius
/// - Drop shadow: 0px 3px 14px rgba(0,0,0,0.4)
/// - Pointer arrow (20pt wide, 10pt tall) pointing down to pin
/// - Photo: 216×120px with 8px corner radius
///
/// **Usage**:
/// Embedded inside `Annotation` content in `FullscreenMapView`,
/// conditionally shown when `selectedPinId == pin.id`.
struct AnnotationCalloutView: View {
    let model: Model
    
    var body: some View {
        VStack(spacing: 0) {
            // Main card content with white background and shadow
            cardContent
                .background(Color.white)
                .cornerRadius(12)
                .shadow(color: .black.opacity(0.4), radius: 7, x: 0, y: 3)
            
            // Pointer arrow at bottom (FR-003, FR-013)
            CalloutPointer()
                .fill(Color.white)
                .frame(width: 20, height: 10)
                .shadow(color: .black.opacity(0.2), radius: 2, x: 0, y: 2)
        }
        .accessibilityIdentifier(model.accessibilityId)
    }
    
    // MARK: - Card Content (T015)
    
    /// Main card layout with all pet information fields.
    private var cardContent: some View {
        VStack(alignment: .leading, spacing: 0) {
            // Pet photo (216×120px, 8px radius) - FR-004
            photoView
            
            // Content section with padding
            VStack(alignment: .leading, spacing: 4) {
                // Pet name (16px bold, #333)
                Text(model.petName)
                    .font(.system(size: 16, weight: .bold))
                    .foregroundColor(Color(hex: "#333333"))
                
                // Species • Breed (13px, #666)
                Text(model.speciesAndBreed)
                    .font(.system(size: 13))
                    .foregroundColor(Color(hex: "#666666"))
                
                // Location (13px, #666)
                Text(model.locationText)
                    .font(.system(size: 13))
                    .foregroundColor(Color(hex: "#666666"))
                
                // Date (13px, #666)
                Text(model.dateText)
                    .font(.system(size: 13))
                    .foregroundColor(Color(hex: "#666666"))
                
                // Email (optional - FR-008)
                if let email = model.emailText {
                    Text(email)
                        .font(.system(size: 13))
                        .foregroundColor(Color(hex: "#666666"))
                }
                
                // Phone (optional - FR-007)
                if let phone = model.phoneText {
                    Text(phone)
                        .font(.system(size: 13))
                        .foregroundColor(Color(hex: "#666666"))
                }
                
                // Description (optional - FR-006)
                if let description = model.descriptionText {
                    Text(description)
                        .font(.system(size: 14))
                        .foregroundColor(Color(hex: "#444444"))
                        .padding(.top, 4)
                }
                
                // Status badge (FR-009)
                statusBadge
                    .padding(.top, 8)
            }
            .padding(.horizontal, 21)
            .padding(.top, 8)
            .padding(.bottom, 14)
        }
    }
    
    // MARK: - Photo View (T016, T017)
    
    /// Pet photo with placeholder for missing/invalid URLs (FR-004, FR-005).
    @ViewBuilder
    private var photoView: some View {
        if let urlString = model.photoUrl, let url = URL(string: urlString) {
            // Valid URL - attempt to load with AsyncImage
            AsyncImage(url: url) { phase in
                switch phase {
                case .empty:
                    // Loading state - show placeholder
                    placeholderImage
                case .success(let image):
                    // Successfully loaded image
                    image
                        .resizable()
                        .scaledToFill()
                        .frame(width: 216, height: 120)
                        .clipped()
                        .cornerRadius(8)
                case .failure:
                    // Error - show placeholder
                    placeholderImage
                @unknown default:
                    placeholderImage
                }
            }
            .frame(width: 216, height: 120)
        } else {
            // Invalid/empty URL - show placeholder immediately (FR-005)
            placeholderImage
        }
    }
    
    /// Placeholder image for missing photo (FR-005).
    /// Matches Announcement List style: rounded rectangle with pawprint icon.
    private var placeholderImage: some View {
        ZStack {
            RoundedRectangle(cornerRadius: 8)
                .fill(Color(hex: "#EEEEEE"))
                .frame(width: 216, height: 120)
            Image(systemName: "pawprint.fill")
                .font(.system(size: 32))
                .foregroundColor(Color(hex: "#93A2B4"))
        }
    }
    
    // MARK: - Status Badge (T018)
    
    /// Status badge showing "MISSING"/"FOUND" with colored background (FR-009).
    private var statusBadge: some View {
        Text(model.statusText)
            .font(.system(size: 12, weight: .bold))
            .foregroundColor(.white)
            .padding(.horizontal, 12)
            .padding(.vertical, 4)
            .background(Color(hex: model.statusColorHex))
            .cornerRadius(12)
    }
}

// MARK: - Callout Pointer (T013)

/// Pointer arrow shape for callout bubble.
/// Triangle pointing down: 20pt wide, 10pt tall.
struct CalloutPointer: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        path.move(to: CGPoint(x: rect.midX - 10, y: 0))
        path.addLine(to: CGPoint(x: rect.midX, y: rect.height))
        path.addLine(to: CGPoint(x: rect.midX + 10, y: 0))
        path.closeSubpath()
        return path
    }
}

// MARK: - Preview

#if DEBUG
struct AnnotationCalloutView_Previews: PreviewProvider {
    static var previews: some View {
        // Sample model for preview
        let model = AnnotationCalloutView.Model(
            photoUrl: "https://placekitten.com/216/120",
            petName: "Simba",
            speciesAndBreed: "Cat • Tabby",
            locationText: "📍 52.2297° N, 21.0122° E",
            dateText: "📅 Jan 08, 2025",
            emailText: "📧 contact@example.com",
            phoneText: "📞 (555) 123-4567",
            descriptionText: "Orange tabby with white paws. Very friendly.",
            statusText: "MISSING",
            statusColorHex: "#FF9500",
            accessibilityId: "fullscreenMap.annotation.preview"
        )
        
        AnnotationCalloutView(model: model)
            .padding()
            .background(Color.gray.opacity(0.3))
            .previewLayout(.sizeThatFits)
    }
}
#endif
