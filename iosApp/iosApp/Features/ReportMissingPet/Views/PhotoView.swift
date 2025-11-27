import SwiftUI
import PhotosUI
import UniformTypeIdentifiers
import ImageIO
import Foundation

/// Full PhotosPicker-driven UI for the Animal Photo step (2/4).
struct PhotoView: View {
    @ObservedObject var viewModel: PhotoViewModel
    
    @State private var pickerSelection: PhotosPickerItem?
    @State private var isProcessingSelection = false
    
    var body: some View {
        ZStack(alignment: .bottom) {
            ScrollView {
                VStack(alignment: .leading, spacing: 24) {
                    titleSection
                    AnimalPhotoEmptyStateView(
                        pickerSelection: $pickerSelection,
                        isLoading: isProcessingSelection || isAttachmentLoading
                    )
                    if isAttachmentLoading {
                        loadingIndicator
                    }
                    if let metadata = confirmedMetadata {
                    AnimalPhotoItemView(model: .init(metadata: metadata)) {
                            viewModel.removeAttachment()
                        }
                        .transition(.opacity.combined(with: .move(edge: .top)))
                    }
                }
                .padding(.horizontal, 22)
                .padding(.top, 32)
                .padding(.bottom, 180)
            }
            .background(Color.white)
            
            VStack(spacing: 12) {
                if viewModel.showsMandatoryToast {
                    ToastView(text: L10n.AnimalPhoto.Toast.mandatory)
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
        .animation(.easeInOut(duration: 0.2), value: confirmedMetadata?.id)
        .onChange(of: pickerSelection) { _, newSelection in
            Task {
                guard let item = newSelection else { return }
                await processPickerItem(item)
            }
        }
    }
    
    // MARK: - Subviews
    
    private var titleSection: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(L10n.AnimalPhoto.title)
                .font(.system(size: 32, weight: .regular))
                .foregroundColor(Color(hex: "#2D2D2D"))
            Text(viewModel.helperMessage)
                .font(.system(size: 16))
                .foregroundColor(Color(hex: "#545F71"))
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
    
    private var loadingIndicator: some View {
        HStack(spacing: 8) {
            ProgressView()
                .progressViewStyle(CircularProgressViewStyle(tint: Color(hex: "#155DFC")))
            Text(L10n.AnimalPhoto.Helper.loading)
                .font(.system(size: 14))
                .foregroundColor(Color(hex: "#545F71"))
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.horizontal, 4)
    }
    
    private var confirmedMetadata: PhotoAttachmentMetadata? {
        if case .confirmed(let metadata) = viewModel.attachmentStatus {
            return metadata
        }
        return nil
    }
    
    private var isAttachmentLoading: Bool {
        if case .loading = viewModel.attachmentStatus {
            return true
        }
        return false
    }
    
    // MARK: - Picker Handling
    
    private func processPickerItem(_ item: PhotosPickerItem) async {
        isProcessingSelection = true
        defer {
            isProcessingSelection = false
            pickerSelection = nil
        }
        
        do {
            guard let transferable = try await item.loadTransferable(type: AnimalPhotoTransferable.self) else {
                return
            }
            
            let selection = PhotoSelection(
                data: transferable.data,
                fileName: transferable.fileName ?? fallbackFilename(for: transferable.contentType),
                contentType: transferable.contentType,
                pixelWidth: transferable.pixelWidth,
                pixelHeight: transferable.pixelHeight,
                assetIdentifier: item.itemIdentifier
            )
            
            await viewModel.handlePhotoSelection(selection)
        } catch {
            if error is CancellationError {
                viewModel.handlePickerCancellation()
            } else {
                viewModel.handleSelectionFailure()
            }
        }
    }
    
    private func fallbackFilename(for type: UTType) -> String {
        let `extension` = type.preferredFilenameExtension ?? "img"
        return "animal-photo.\(`extension`)"
    }
}

#if DEBUG
private struct AnimalPhotoDebugControls: View {
    @ObservedObject var viewModel: PhotoViewModel
    
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
struct PhotoView_Previews: PreviewProvider {
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
        let flowState = ReportMissingPetFlowState(photoAttachmentCache: cache)
        let viewModel = PhotoViewModel(
            flowState: flowState,
            photoAttachmentCache: cache,
            toastScheduler: PreviewToastScheduler()
        )
        
        return NavigationView {
            PhotoView(viewModel: viewModel)
        }
    }
}
#endif
