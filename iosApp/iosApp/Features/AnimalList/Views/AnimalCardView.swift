import SwiftUI
import Shared

/**
 * SwiftUI view for displaying a single animal card in the list.
 * Layout: [Image] [Location + Species|Breed] [Status Badge + Date]
 *
 * Design matches Figma specifications (node 52:6541):
 * - Card background: #FAFAFA
 * - Card border radius: 4pt
 * - Card shadow: 1pt offset, 4pt blur
 * - Card padding: 8pt
 * - Image placeholder: 63pt circular (#EEEEEE background)
 * - Location icon before text (13pt)
 * - Species|Breed with separator (16pt | 14pt) - separate Text views for proper wrapping
 * - Status badge: 12pt text, 10pt radius
 * - Date: 13pt text (#545F71)
 *
 * Note: Uses @ObservedObject (not @StateObject) because ViewModel lifecycle
 * is managed by parent AnimalListViewModel for performance and data consistency.
 * View consumes presentation-ready properties from ViewModel (locationText, statusColor, etc.)
 * and does not access raw Animal model directly.
 *
 * - Parameter viewModel: Card ViewModel injected by parent
 */
struct AnimalCardView: View {
    @ObservedObject var viewModel: AnimalCardViewModel
    
    var body: some View {
        HStack(alignment: .center, spacing: 10) {
            // Photo placeholder (63pt circular)
            ZStack {
                Circle()
                    .fill(Color(hex: "#EEEEEE")) // Light gray placeholder
                    .frame(width: 63, height: 63)
                
                // Animal icon placeholder
                Image(systemName: "pawprint.fill")
                    .font(.system(size: 24))
                    .foregroundColor(Color(hex: "#93A2B4")) // Tertiary text color
            }
            
            // Animal info column (location + species/breed)
            VStack(alignment: .leading, spacing: 8) {
                // Location with icon
                HStack(spacing: 4) {
                    Image(systemName: "mappin")
                        .font(.system(size: 13))
                        .foregroundColor(Color(hex: "#545F71")) // Secondary text color
                    
                    Text(viewModel.locationText)
                        .font(.system(size: 13))
                        .foregroundColor(Color(hex: "#545F71")) // Secondary text color
                }
                
                // Species | Breed
                HStack(spacing: 4) {
                    Text(viewModel.speciesName)
                        .font(.system(size: 16))
                        .foregroundColor(Color(hex: "#2D2D2D")) // Primary text color
                    
                    Text("|")
                        .font(.system(size: 16))
                        .foregroundColor(Color(hex: "#93A2B4")) // Tertiary text color
                    
                    Text(viewModel.breedName)
                        .font(.system(size: 14))
                        .foregroundColor(Color(hex: "#2D2D2D")) // Primary text color
                }
            }
            
            Spacer()
            
            // Status column (badge + date) - right aligned
            VStack(alignment: .trailing, spacing: 4) {
                // Status badge
                Text(viewModel.statusText)
                    .font(.system(size: 12))
                    .foregroundColor(.white)
                    .padding(.horizontal, 10)
                    .padding(.vertical, 2)
                    .background(Color(hex: viewModel.statusColorHex))
                    .cornerRadius(10)
                
                // Date
                Text(viewModel.dateText)
                    .font(.system(size: 13))
                    .foregroundColor(Color(hex: "#545F71")) // Secondary text color
            }
            .padding(.vertical, 8)
        }
        .padding(8)
        .background(Color(hex: "#FAFAFA"))
        .cornerRadius(4)
        .shadow(color: Color.black.opacity(0.05), radius: 4, x: 0, y: 1)
        .accessibilityIdentifier("animalList.item.\(viewModel.id)")
        .onTapGesture { viewModel.handleTap() }
    }
}

