import SwiftUI
import Shared

/**
 * Main SwiftUI view for displaying list of animals.
 * Follows MVVM-C architecture with ViewModel managing state.
 *
 * Features:
 * - Scrollable list of animal cards (LazyVStack for performance)
 * - Loading indicator
 * - Error message display
 * - Empty state message
 * - "Report a Missing Animal" button (fixed at bottom)
 * - Reserved space for future search component
 *
 * Layout per FR-010: This is the primary entry point screen.
 *
 * - Parameter viewModel: ViewModel injected by coordinator
 */
struct AnimalListView: View {
    @ObservedObject var viewModel: AnimalListViewModel
    
    var body: some View {
        ZStack {
            Color(hex: "#FAFAFA") // Background color
                .ignoresSafeArea()
            
            // Content area with scrollable list
            if viewModel.isLoading {
                // Loading indicator
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: Color(hex: "#2D2D2D")))
                    .scaleEffect(1.5)
            } else if let errorMessage = viewModel.errorMessage {
                // Error message
                Text("Error: \(errorMessage)")
                    .font(.system(size: 16))
                    .foregroundColor(.red)
                    .multilineTextAlignment(.center)
                    .padding(32)
            } else if viewModel.isEmpty {
                // Empty state
                EmptyStateView()
            } else {
                // Animal list
                ScrollView {
                    LazyVStack(spacing: 8) { // 8pt gap per Figma
                        // Reserved space for search component (FR-004)
                        Color.clear
                            .frame(height: 56) // 48-56pt height per spec
                        
                        // Animal cards
                        ForEach(viewModel.animals, id: \.id) { animal in
                            AnimalCardView(
                                animal: animal,
                                onTap: {
                                    viewModel.selectAnimal(id: animal.id)
                                }
                            )
                        }
                        
                        // Bottom spacing for floating buttons
                        Color.clear
                            .frame(height: 200)
                    }
                    .padding(.horizontal, 16)
                }
                .accessibilityIdentifier("animalList.list")
            }
            
            // Floating buttons at bottom right (node 71:7472)
            VStack(alignment: .trailing, spacing: 30) {
                Spacer()
                
                // Report Found Animal button (optional - not in MVP mobile)
                // Commented out per design decision for mobile
                /*
                Button(action: {
                    viewModel.reportFound()
                }) {
                    Text("Report Found Animal")
                        .font(.system(size: 14))
                        .foregroundColor(Color(hex: "#2D2D2D"))
                        .padding(.horizontal, 10)
                        .padding(.vertical, 10)
                        .background(Color(hex: "#EFF4F8"))
                        .cornerRadius(16)
                        .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 4)
                }
                */
                
                // Report a Missing Animal button (primary action)
                Button(action: {
                    viewModel.reportMissing()
                }) {
                    Text("Report a Missing Animal")
                        .font(.system(size: 14))
                        .foregroundColor(.white)
                        .padding(.horizontal, 21)
                        .padding(.vertical, 21)
                        .background(Color(hex: "#2D2D2D"))
                        .cornerRadius(16)
                        .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 4)
                }
                .accessibilityIdentifier("animalList.reportMissingButton")
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .bottomTrailing)
            .padding(.trailing, 19)
            .padding(.bottom, 32)
        }
    }
}

