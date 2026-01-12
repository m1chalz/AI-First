import SwiftUI
import PhotosUI
import Foundation

/// Full PhotosPicker-driven UI for the Animal Photo step (1/3 of Found Pet flow).
struct FoundPetPhotoView: View {
    @ObservedObject var viewModel: FoundPetPhotoViewModel
    
    @State private var pickerSelection: PhotosPickerItem?
    
    var body: some View {
        ZStack(alignment: .bottom) {
            ScrollView {
                VStack(alignment: .leading, spacing: 24) {
                    titleSection
                    AnimalPhotoBrowseView(
                        pickerSelection: $pickerSelection,
                        isLoading: viewModel.isProcessingSelection || viewModel.isAttachmentLoading
                    )
                    if let metadata = viewModel.cardMetadata {
                        AnimalPhotoItemView(
                            model: .init(metadata: metadata, showsLoadingIcon: viewModel.isAttachmentLoading)
                        ) {
                            viewModel.removeAttachment()
                        }
                        .transition(.opacity.combined(with: .move(edge: .top)))
                    }
                }
                .padding(.horizontal, 22)
                .padding(.top, 32)
            }
            .background(Color.white)
            .safeAreaInset(edge: .bottom) {
                Color.clear.frame(height: 120)
            }
            
            VStack(spacing: 12) {
                if viewModel.showsMandatoryToast {
                    ToastView(model: .init(text: L10n.AnimalPhoto.Toast.mandatory))
                        .transition(.move(edge: .bottom).combined(with: .opacity))
                        .accessibilityIdentifier("animalPhoto.toast")
                }
                
                Button(action: viewModel.handleNext) {
                    Text(L10n.AnimalPhoto.Button.continue)
                        .font(.system(size: 18, weight: .semibold))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 16)
                        .background(Color(hex: "#155DFC"))
                        .cornerRadius(10)
                }
                .accessibilityIdentifier("animalPhoto.continue")
            }
            .padding(.horizontal, 22)
            .padding(.vertical, 24)
            .frame(maxWidth: .infinity)
            .background(Color.white.ignoresSafeArea(edges: .bottom))
        }
        .overlay(alignment: .topTrailing) {
#if DEBUG
            if ProcessInfo.processInfo.environment["UITEST_SHOW_PHOTO_DEBUG"] == "1" {
                AnimalPhotoDebugControls(viewModel: viewModel)
                    .padding(.top, 8)
                    .padding(.trailing, 12)
            }
#endif
        }
        .background(Color.white.ignoresSafeArea())
        .animation(.easeInOut(duration: 0.2), value: viewModel.showsMandatoryToast)
        .animation(.easeInOut(duration: 0.2), value: viewModel.cardMetadata?.id)
        .onChange(of: pickerSelection) { _, newSelection in
            guard let item = newSelection else { return }
            Task {
                await viewModel.processPickerItem(item)
            }
            pickerSelection = nil
        }
    }
    
    // MARK: - Subviews
    
    private var titleSection: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(L10n.ReportFoundPet.Photo.heading)
                .font(.system(size: 32, weight: .regular))
                .foregroundColor(Color(hex: "#2D2D2D"))
                .accessibilityIdentifier("reportFoundPet.photo.heading")
            Text(L10n.ReportFoundPet.Photo.body)
                .font(.system(size: 16))
                .foregroundColor(Color(hex: "#545F71"))
                .accessibilityIdentifier("reportFoundPet.photo.body")
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
    
}

#if DEBUG
private struct AnimalPhotoDebugControls: View {
    @ObservedObject var viewModel: FoundPetPhotoViewModel
    
    var body: some View {
        VStack(alignment: .trailing, spacing: 4) {
            Button("CancelSim") {
                viewModel.handlePickerCancellation()
            }
            .font(.caption2)
            .padding(6)
            .background(Color.yellow.opacity(0.4))
            .cornerRadius(6)
            .accessibilityIdentifier("animalPhoto.debug.cancel")
            
            Button("FailSim") {
                viewModel.handleSelectionFailure()
            }
            .font(.caption2)
            .padding(6)
            .background(Color.red.opacity(0.4))
            .cornerRadius(6)
            .accessibilityIdentifier("animalPhoto.debug.fail")
        }
    }
}
#endif

// MARK: - Previews

#if DEBUG
struct FoundPetPhotoView_Previews: PreviewProvider {
    private final class PreviewCache: PhotoAttachmentCacheProtocol {
        func save(data: Data, metadata: PhotoAttachmentMetadata) async throws -> PhotoAttachmentMetadata { metadata }
        func loadCurrent() async throws -> PhotoAttachmentMetadata? { nil }
        func fileExists(at url: URL) async -> Bool { false }
        func clearCurrent() async throws {}
    }
    
    private final class PreviewToastScheduler: ToastSchedulerProtocol {
        func schedule(duration: TimeInterval, handler: @escaping () -> Void) {}
        func cancel() {}
    }
    
    static var previews: some View {
        let cache = PreviewCache()
        let flowState = FoundPetReportFlowState(photoAttachmentCache: cache)
        let viewModel = FoundPetPhotoViewModel(
            flowState: flowState,
            photoAttachmentCache: cache,
            toastScheduler: PreviewToastScheduler()
        )
        
        return NavigationView {
            FoundPetPhotoView(viewModel: viewModel)
        }
    }
}
#endif

