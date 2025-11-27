import Foundation
import UniformTypeIdentifiers

/// ViewModel responsible for orchestrating the Animal Photo step (browse → confirm → continue).
@MainActor
final class PhotoViewModel: ObservableObject {
    // MARK: - Published State
    
    @Published private(set) var attachmentStatus: PhotoAttachmentStatus
    @Published private(set) var showsMandatoryToast: Bool = false
    @Published private(set) var helperMessage: String = L10n.AnimalPhoto.Helper.required
    
    // MARK: - Dependencies
    
    private let flowState: ReportMissingPetFlowState
    private let photoAttachmentCache: PhotoAttachmentCacheProtocol
    private let toastScheduler: ToastSchedulerProtocol
    
    // MARK: - Coordinator Communication
    
    var onNext: (() -> Void)?
    var onBack: (() -> Void)?
    
    // MARK: - Initialization
    
    init(
        flowState: ReportMissingPetFlowState,
        photoAttachmentCache: PhotoAttachmentCacheProtocol,
        toastScheduler: ToastSchedulerProtocol
    ) {
        self.flowState = flowState
        self.photoAttachmentCache = photoAttachmentCache
        self.toastScheduler = toastScheduler
        self.attachmentStatus = flowState.photoStatus
        
        Task {
            await restorePersistedAttachment()
        }
    }
    
    // MARK: - Intent Handling
    
    /**
     Handles the result returned by `PhotosPicker`.
     
     - Parameter selection: Normalized selection payload containing binary data and metadata.
     */
    func handlePhotoSelection(_ selection: PhotoSelection) async {
        guard PhotoAttachmentMetadata.isSupported(utiIdentifier: selection.contentType.identifier),
              !selection.data.isEmpty else {
            resetAttachmentState()
            return
        }
        
        updateStatus(.loading(progress: nil))
        helperMessage = L10n.AnimalPhoto.Helper.loading
        
        do {
            let metadata = PhotoAttachmentMetadata(
                id: UUID(),
                fileName: selection.fileName,
                fileSizeBytes: selection.data.count,
                utiIdentifier: selection.contentType.identifier,
                pixelWidth: selection.pixelWidth,
                pixelHeight: selection.pixelHeight,
                assetIdentifier: selection.assetIdentifier,
                cachedURL: URL(fileURLWithPath: "/dev/null"),
                savedAt: Date()
            )
            
            let savedMetadata = try await photoAttachmentCache.save(
                data: selection.data,
                metadata: metadata
            )
            
            applyConfirmedAttachment(savedMetadata)
        } catch {
            handleSelectionFailure()
        }
    }
    
    /// Removes any cached attachment and reverts to the empty state.
    func removeAttachment() {
        Task {
            try? await photoAttachmentCache.clearCurrent()
        }
        resetAttachmentState()
    }
    
    /// Handles the Continue CTA. Advances only when a photo is confirmed.
    func handleNext() {
        guard case .confirmed = attachmentStatus else {
            showMandatoryToast()
            return
        }
        
        onNext?()
    }
    
    /// Navigate back to the previous screen (Chip Number).
    func handleBack() {
        onBack?()
    }
    
    // MARK: - Private Helpers
    
    private func updateStatus(_ status: PhotoAttachmentStatus) {
        attachmentStatus = status
        flowState.photoStatus = status
    }
    
    private func applyConfirmedAttachment(_ metadata: PhotoAttachmentMetadata) {
        toastScheduler.cancel()
        showsMandatoryToast = false
        helperMessage = L10n.AnimalPhoto.Helper.required
        flowState.photoAttachment = metadata
        updateStatus(.confirmed(metadata: metadata))
    }
    
    private func resetAttachmentState(helperMessage message: String? = nil) {
        flowState.photoAttachment = nil
        updateStatus(.empty)
        helperMessage = message ?? L10n.AnimalPhoto.Helper.required
    }
    
    private func showMandatoryToast() {
        toastScheduler.cancel()
        showsMandatoryToast = true
        toastScheduler.schedule(duration: 3.0) { [weak self] in
            Task { @MainActor in
                self?.showsMandatoryToast = false
            }
        }
    }
    
    /// Rehydrates the confirmed card from FlowState or disk if available.
    private func restorePersistedAttachment() async {
        if let metadata = flowState.photoAttachment,
           await photoAttachmentCache.fileExists(at: metadata.cachedURL) {
            applyConfirmedAttachment(metadata)
            return
        }
        
        guard let cached = try? await photoAttachmentCache.loadCurrent() else {
            resetAttachmentState()
            return
        }
        
        applyConfirmedAttachment(cached)
    }
    
    /// Called when the Photos picker is dismissed without selecting an asset.
    func handlePickerCancellation() {
        resetAttachmentState(helperMessage: L10n.AnimalPhoto.Helper.pickerCancelled)
    }
    
    /// Called when loading/saving the attachment fails.
    func handleSelectionFailure() {
        resetAttachmentState()
    }
    
    deinit {
        toastScheduler.cancel()
    }
}

