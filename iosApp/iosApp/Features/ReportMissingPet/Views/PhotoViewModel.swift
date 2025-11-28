import Foundation
import UniformTypeIdentifiers

/// ViewModel responsible for orchestrating the Animal Photo step (browse → confirm → continue).
@MainActor
final class PhotoViewModel: ObservableObject {
    // MARK: - Published State
    
    @Published private(set) var attachmentStatus: PhotoAttachmentStatus
    @Published private(set) var showsMandatoryToast: Bool = false
    @Published private(set) var helperMessage: String = L10n.AnimalPhoto.Helper.required
    @Published private(set) var isProcessingSelection: Bool = false
    
    // MARK: - Computed State
    
    /// Returns confirmed attachment metadata if available.
    var confirmedMetadata: PhotoAttachmentMetadata? {
        if case .confirmed(let metadata) = attachmentStatus {
            return metadata
        }
        return nil
    }
    
    /// Returns metadata for the card that should be shown on screen.
    var cardMetadata: PhotoAttachmentMetadata? {
        pendingMetadata ?? confirmedMetadata
    }

    /// Returns true when attachment is being loaded/processed.
    var isAttachmentLoading: Bool {
        if case .loading = attachmentStatus {
            return true
        }
        return false
    }
    
    // MARK: - Dependencies
    
    private let flowState: ReportMissingPetFlowState
    private let photoAttachmentCache: PhotoAttachmentCacheProtocol
    private let toastScheduler: ToastSchedulerProtocol
    private let photoSelectionProcessor: PhotoSelectionProcessing
    private let simulatedLoadingDelay: TimeInterval
    
    // MARK: - Coordinator Communication
    
    var onNext: (() -> Void)?
    var onBack: (() -> Void)?
    
    // MARK: - Private State
    
    private var restorationTask: Task<Void, Never>?
    private var pendingMetadata: PhotoAttachmentMetadata?
    
    // MARK: - Initialization
    
    init(
        flowState: ReportMissingPetFlowState,
        photoAttachmentCache: PhotoAttachmentCacheProtocol,
        toastScheduler: ToastSchedulerProtocol,
        photoSelectionProcessor: PhotoSelectionProcessing = PhotoSelectionProcessor(),
        simulatedLoadingDelay: TimeInterval = .zero
    ) {
        self.flowState = flowState
        self.photoAttachmentCache = photoAttachmentCache
        self.toastScheduler = toastScheduler
        self.photoSelectionProcessor = photoSelectionProcessor
        self.simulatedLoadingDelay = simulatedLoadingDelay
        self.attachmentStatus = flowState.photoStatus
        
        restorationTask = Task {
            await restorePersistedAttachment()
        }
    }
    
    /// Waits for restoration to complete. For testing only.
    func waitForRestoration() async {
        await restorationTask?.value
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

        pendingMetadata = metadata
        updateStatus(.loading(progress: nil))
        
        do {
            let savedMetadata = try await photoAttachmentCache.save(
                data: selection.data,
                metadata: metadata
            )
            await simulateICloudDelayIfRequested()
            
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
    
    /// Kicks off processing of a selected Photos picker item using the helper processor.
    func processPickerItem(_ item: PhotoPickerItemProviding) async {
        isProcessingSelection = true
        defer { isProcessingSelection = false }
        
        do {
            let selection = try await photoSelectionProcessor.process(item)
            await handlePhotoSelection(selection)
        } catch {
            if error is CancellationError {
                handlePickerCancellation()
            } else {
                handleSelectionFailure()
            }
        }
    }
    
    // MARK: - Private Helpers
    
    private func updateStatus(_ status: PhotoAttachmentStatus) {
        attachmentStatus = status
        flowState.photoStatus = status
    }
    
    private func applyConfirmedAttachment(_ metadata: PhotoAttachmentMetadata) {
        pendingMetadata = nil
        toastScheduler.cancel()
        showsMandatoryToast = false
        helperMessage = L10n.AnimalPhoto.Helper.required
        flowState.photoAttachment = metadata
        updateStatus(.confirmed(metadata: metadata))
    }
    
    private func resetAttachmentState(helperMessage message: String? = nil) {
        pendingMetadata = nil
        flowState.photoAttachment = nil
        updateStatus(.empty)
        helperMessage = message ?? L10n.AnimalPhoto.Helper.required
    }
    
    /// Adds an optional delay to mimic iCloud download timing for testing/debugging.
    private func simulateICloudDelayIfRequested() async {
        guard simulatedLoadingDelay > .zero else { return }
        let nanos = UInt64(simulatedLoadingDelay * 1_000_000_000)
        try? await Task.sleep(nanoseconds: nanos)
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

