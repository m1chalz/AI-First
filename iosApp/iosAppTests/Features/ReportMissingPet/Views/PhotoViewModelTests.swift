import XCTest
import UniformTypeIdentifiers
@testable import PetSpot

@MainActor
final class PhotoViewModelTests: XCTestCase {
    
    var flowState: ReportMissingPetFlowState!
    var cache: PhotoAttachmentCacheFake!
    var toastScheduler: ToastSchedulerFake!
    var photoSelectionProcessor: PhotoSelectionProcessorStub!
    var sut: PhotoViewModel!
    
    override func setUp() {
        super.setUp()
        cache = PhotoAttachmentCacheFake()
        toastScheduler = ToastSchedulerFake()
        flowState = ReportMissingPetFlowState(photoAttachmentCache: cache)
        photoSelectionProcessor = PhotoSelectionProcessorStub()
        sut = PhotoViewModel(
            flowState: flowState,
            photoAttachmentCache: cache,
            toastScheduler: toastScheduler,
            photoSelectionProcessor: photoSelectionProcessor
        )
    }
    
    override func tearDown() {
        sut = nil
        cache = nil
        toastScheduler = nil
        flowState = nil
        photoSelectionProcessor = nil
        super.tearDown()
    }
    
    func testHandlePhotoSelection_whenSaveSucceeds_shouldConfirmAttachment() async {
        // Given
        let selection = makeSelection()
        
        // When
        await sut.handlePhotoSelection(selection)
        
        // Then
        guard case .confirmed(let metadata) = sut.attachmentStatus else {
            return XCTFail("Expected confirmed status after successful save")
        }
        XCTAssertEqual(metadata.fileName, selection.fileName)
        XCTAssertEqual(flowState.photoAttachment?.fileName, selection.fileName)
        XCTAssertTrue(cache.lastSavedData?.isEmpty == false)
    }
    
    func testHandlePhotoSelection_shouldPersistFlowStateForNavigation() async {
        // Given
        let selection = makeSelection(fileName: "cat.png")
        
        // When
        await sut.handlePhotoSelection(selection)
        
        // Then
        XCTAssertEqual(flowState.photoStatus, sut.attachmentStatus)
        XCTAssertEqual(flowState.photoAttachment?.fileName, "cat.png")
    }
    
    func testHandlePhotoSelection_whenSaveFails_shouldResetState() async {
        // Given
        cache.saveError = PhotoAttachmentCacheError.writeFailed
        let selection = makeSelection()
        
        // When
        await sut.handlePhotoSelection(selection)
        
        // Then
        guard case .empty = sut.attachmentStatus else {
            return XCTFail("Attachment status should remain empty when save fails")
        }
        XCTAssertEqual(sut.helperMessage, L10n.AnimalPhoto.Helper.required)
    }
    
    func testHandleNext_whenAttachmentConfirmed_shouldTriggerOnNext() async {
        // Given
        await sut.handlePhotoSelection(makeSelection())
        var didNavigate = false
        sut.onNext = { didNavigate = true }
        
        // When
        sut.handleNext()
        
        // Then
        XCTAssertTrue(didNavigate)
    }
    
    func testHandleBack_shouldTriggerOnBackCallback() {
        // Given
        var didGoBack = false
        sut.onBack = { didGoBack = true }
        
        // When
        sut.handleBack()
        
        // Then
        XCTAssertTrue(didGoBack)
    }
    
    func testHandleNext_withoutAttachment_shouldShowMandatoryToast() {
        // When
        sut.handleNext()
        
        // Then
        XCTAssertTrue(sut.showsMandatoryToast)
        XCTAssertEqual(toastScheduler.scheduledDurations.last, 3.0)
    }
    
    func testRemoveAttachment_shouldResetStateAndClearCache() async {
        // Given
        await sut.handlePhotoSelection(makeSelection())
        
        // When
        sut.removeAttachment()
        await waitForAsyncOperations()
        
        // Then
        guard case .empty = sut.attachmentStatus else {
            return XCTFail("Attachment status should return to empty after removal")
        }
        XCTAssertNil(flowState.photoAttachment)
        XCTAssertEqual(cache.clearCallCount, 1)
    }
    
    func testHandleNext_afterRemoval_shouldReplayToast() async {
        // Given
        await sut.handlePhotoSelection(makeSelection())
        sut.removeAttachment()
        await waitForAsyncOperations()
        
        // When
        sut.handleNext()
        
        // Then
        XCTAssertTrue(sut.showsMandatoryToast)
    }
    
    func testHandlePickerCancellation_shouldUpdateHelperCopy() {
        // When
        sut.handlePickerCancellation()
        
        // Then
        XCTAssertEqual(sut.helperMessage, L10n.AnimalPhoto.Helper.pickerCancelled)
        guard case .empty = sut.attachmentStatus else {
            return XCTFail("Status should remain empty after cancellation")
        }
    }
    
    func testRestorePersistedAttachment_shouldUseFlowStateMetadata() async {
        // Given
        let metadata = makeMetadata(fileName: "persisted.jpg")
        let freshFlowState = ReportMissingPetFlowState(photoAttachmentCache: cache)
        freshFlowState.photoAttachment = metadata
        
        let freshCache = PhotoAttachmentCacheFake()
        freshCache.fileExistsResult = true
        
        // When
        let viewModel = PhotoViewModel(
            flowState: freshFlowState,
            photoAttachmentCache: freshCache,
            toastScheduler: toastScheduler
        )
        await viewModel.waitForRestoration()
        
        // Then
        XCTAssertEqual(freshCache.fileExistsCallCount, 1, "fileExists should be called once")
        XCTAssertEqual(freshCache.lastCheckedURL, metadata.cachedURL, "fileExists should check the metadata URL")
        
        guard case .confirmed(let confirmed) = viewModel.attachmentStatus else {
            return XCTFail("Expected confirmed attachment to be restored. Status: \(viewModel.attachmentStatus)")
        }
        XCTAssertEqual(confirmed.fileName, "persisted.jpg")
    }
    
    func testProcessPickerItem_shouldUseProcessorAndConfirmSelection() async {
        // Given
        let selection = makeSelection(fileName: "processor.jpg")
        photoSelectionProcessor.processResult = .success(selection)
        let pickerItem = PhotoPickerItemStub()
        
        // When
        await sut.processPickerItem(pickerItem)
        
        // Then
        XCTAssertEqual(photoSelectionProcessor.processCallCount, 1)
        guard case .confirmed(let metadata) = sut.attachmentStatus else {
            return XCTFail("Expected confirmed metadata after successful processing")
        }
        XCTAssertEqual(metadata.fileName, "processor.jpg")
        XCTAssertFalse(sut.isProcessingSelection)
    }
    
    func testProcessPickerItem_whenProcessorThrowsCancellation_shouldTriggerCancellationFlow() async {
        // Given
        photoSelectionProcessor.processResult = .failure(CancellationError())
        let pickerItem = PhotoPickerItemStub()
        
        // When
        await sut.processPickerItem(pickerItem)
        
        // Then
        XCTAssertEqual(sut.helperMessage, L10n.AnimalPhoto.Helper.pickerCancelled)
    }
    
    func testProcessPickerItem_whenProcessorThrowsError_shouldResetState() async {
        // Given
        photoSelectionProcessor.processResult = .failure(PhotoSelectionProcessorError.emptyTransferable)
        let pickerItem = PhotoPickerItemStub()
        
        // When
        await sut.processPickerItem(pickerItem)
        
        // Then
        guard case .empty = sut.attachmentStatus else {
            return XCTFail("Attachment status should remain empty on processor failure")
        }
    }
    
    // MARK: - Helpers
    
    private func makeSelection(fileName: String = "pet.jpg") -> PhotoSelection {
        PhotoSelection(
            data: Data(repeating: 0xFF, count: 1_024),
            fileName: fileName,
            contentType: .jpeg,
            pixelWidth: 400,
            pixelHeight: 300,
            assetIdentifier: "asset-id"
        )
    }
    
    private func waitForAsyncOperations() async {
        await Task.yield()
        try? await Task.sleep(nanoseconds: 5_000_000)
    }
    
    private func makeMetadata(fileName: String) -> PhotoAttachmentMetadata {
        PhotoAttachmentMetadata(
            id: UUID(),
            fileName: fileName,
            fileSizeBytes: 512_000,
            utiIdentifier: UTType.jpeg.identifier,
            pixelWidth: 400,
            pixelHeight: 300,
            assetIdentifier: "asset",
            cachedURL: URL(fileURLWithPath: "/tmp/\(fileName)"),
            savedAt: Date()
        )
    }
}

// MARK: - Test Doubles

private final class PhotoSelectionProcessorStub: PhotoSelectionProcessing {
    var processResult: Result<PhotoSelection, Error> = .success(
        PhotoSelection(
            data: Data(),
            fileName: "default.jpg",
            contentType: .jpeg,
            pixelWidth: 1,
            pixelHeight: 1,
            assetIdentifier: nil
        )
    )
    
    private(set) var processCallCount = 0
    
    func process(_ item: PhotoPickerItemProviding) async throws -> PhotoSelection {
        processCallCount += 1
        switch processResult {
        case .success(let selection):
            return selection
        case .failure(let error):
            throw error
        }
    }
}

private struct PhotoPickerItemStub: PhotoPickerItemProviding {
    var itemIdentifier: String? = UUID().uuidString
    var loadTransferableHandler: (() async throws -> Any?)?
    
    func loadTransferable<T>(type: T.Type) async throws -> T? {
        guard let handler = loadTransferableHandler else { return nil }
        let value = try await handler()
        return value as? T
    }
}
