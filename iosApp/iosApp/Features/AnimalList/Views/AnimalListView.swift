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
            
            VStack(spacing: 0) {
                // Content area
                ZStack {
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
                            }
                            .padding(.horizontal, 16)
                            .padding(.bottom, 80) // Space for fixed button
                        }
                        .accessibilityIdentifier("animalList.list")
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                
                // Fixed button at bottom (per mobile design - single button)
                Button(action: {
                    viewModel.reportMissing()
                }) {
                    Text("Report a Missing Animal")
                        .font(.system(size: 16))
                        .foregroundColor(.white)
                        .padding(.vertical, 16)
                        .frame(maxWidth: .infinity)
                        .background(Color(hex: "#2D2D2D")) // Primary button color
                        .cornerRadius(2)
                }
                .padding(16)
                .background(
                    Color(hex: "#FAFAFA")
                        .shadow(color: Color.black.opacity(0.1), radius: 8, x: 0, y: -2)
                )
                .accessibilityIdentifier("animalList.reportMissingButton")
            }
        }
    }
}

