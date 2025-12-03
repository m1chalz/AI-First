import SwiftUI

/// View for Summary screen (Step 5 - Report Created Confirmation).
/// Displays confirmation messaging, management password, and Close button.
struct SummaryView: View {
    // MARK: - Properties
    
    private let constants = Constants()
    @ObservedObject var viewModel: SummaryViewModel

    // MARK: - Body
    
    var body: some View {
        ZStack {
            Color.white
                .edgesIgnoringSafeArea(.all)
            
            VStack(spacing: 0) {
                // Scrollable content
                ScrollView {
                    VStack(spacing: constants.verticalSpacing) {
                        // Title
                        Text(L10n.ReportCreated.title)
                            .font(constants.titleFont)
                            .foregroundColor(constants.titleColor)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .accessibilityIdentifier("summary.title")
                        
                        // Body Paragraph 1
                        Text(L10n.ReportCreated.bodyParagraph1)
                            .font(constants.bodyFont)
                            .foregroundColor(constants.bodyColor)
                            .lineSpacing(constants.bodyLineSpacing)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .fixedSize(horizontal: false, vertical: true)
                            .accessibilityIdentifier("summary.bodyParagraph1")
                        
                        // Body Paragraph 2
                        Text(L10n.ReportCreated.bodyParagraph2)
                            .font(constants.bodyFont)
                            .foregroundColor(constants.bodyColor)
                            .lineSpacing(constants.bodyLineSpacing)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .fixedSize(horizontal: false, vertical: true)
                            .accessibilityIdentifier("summary.bodyParagraph2")
                        
                        // Management Password Module
                        Button(action: viewModel.copyPasswordToClipboard) {
                            passwordContainer
                        }
                        .buttonStyle(PlainButtonStyle())
                        
                        Spacer(minLength: 40)
                    }
                    .padding(.horizontal, constants.horizontalPadding)
                    .padding(.top, constants.topSafeAreaInset)
                }
                .safeAreaInset(edge: .bottom) {
                    Color.clear.frame(height: 120)
                }
                
                // Toast + Close Button (fixed at bottom)
                VStack(spacing: 12) {
                    if viewModel.showsCodeCopiedToast {
                        ToastView(model: .init(text: L10n.ReportCreated.codeCopied))
                            .transition(.move(edge: .bottom).combined(with: .opacity))
                            .accessibilityIdentifier("summary.toast")
                    }
                    
                    Button(action: viewModel.handleSubmit) {
                        Text(L10n.ReportMissingPet.Button.close)
                            .font(constants.buttonFont)
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 16)
                            .background(constants.buttonBackgroundColor)
                            .cornerRadius(constants.buttonCornerRadius)
                    }
                    .accessibilityIdentifier("summary.closeButton")
                }
                .padding(.horizontal, 22)
                .padding(.vertical, 24)
                .frame(maxWidth: .infinity)
                .background(Color.white.ignoresSafeArea(edges: .bottom))
            }
        }
        .background(Color.white.ignoresSafeArea())
    }
    
    // MARK: - Subviews
    
    private var passwordContainer: some View {
        ZStack {
            // Gradient background
            RoundedRectangle(cornerRadius: constants.passwordBackgroundCornerRadius)
                .fill(
                    LinearGradient(
                        colors: [
                            constants.gradientStartColor,
                            constants.gradientEndColor
                        ],
                        startPoint: .leading,
                        endPoint: .trailing
                    )
                )
            
            // Glow overlay
            RoundedRectangle(cornerRadius: constants.passwordBackgroundCornerRadius)
                .fill(constants.glowColor.opacity(constants.glowOpacity))
                .blur(radius: constants.glowBlurRadius)
            
            // Password text
            Text(viewModel.displayPassword)
                .font(constants.passwordFont)
                .kerning(constants.passwordKerning)
                .foregroundColor(.white)
                .accessibilityIdentifier("summary.password")
        }
        .frame(width: constants.passwordContainerWidth, height: constants.passwordContainerHeight)
    }
}

// MARK: - Preview

#if DEBUG
struct SummaryView_Previews: PreviewProvider {
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
        Group {
            // With password
            SummaryView(
                viewModel: {
                    let cache = PreviewCache()
                    let flowState = ReportMissingPetFlowState(photoAttachmentCache: cache)
                    flowState.managementPassword = "5216577"
                    let vm = SummaryViewModel(
                        flowState: flowState,
                        toastScheduler: PreviewToastScheduler()
                    )
                    return vm
                }()
            )
            .previewDisplayName("With Password")
            
            // Without password (nil)
            SummaryView(
                viewModel: {
                    let cache = PreviewCache()
                    let flowState = ReportMissingPetFlowState(photoAttachmentCache: cache)
                    flowState.managementPassword = nil
                    let vm = SummaryViewModel(
                        flowState: flowState,
                        toastScheduler: PreviewToastScheduler()
                    )
                    return vm
                }()
            )
            .previewDisplayName("Password Nil")
        }
    }
}
#endif
