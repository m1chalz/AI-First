import Foundation

/// ViewModel for Contact Details screen (Step 4/4).
/// Manages input state, validation, and 2-step submission.
@MainActor
class ContactDetailsViewModel: ObservableObject {
    // MARK: - Input State (bound to ValidatedTextField)
    
    @Published var phone: String = ""
    @Published var email: String = ""
    @Published var rewardDescription: String = ""
    
    // MARK: - Validation Error State (displayed in ValidatedTextField)
    
    @Published var phoneError: String? = nil
    @Published var emailError: String? = nil
    
    // MARK: - Loading State (controls Continue button spinner and back button)
    
    @Published var isSubmitting: Bool = false
    
    // MARK: - Alert State (popup for submission errors)
    
    @Published var alertMessage: String? = nil
    @Published var showAlert: Bool = false
    
    // MARK: - Dependencies (injected via initializer)
    
    private let submissionService: AnnouncementSubmissionService
    private let flowState: ReportMissingPetFlowState
    
    // MARK: - Coordinator Callbacks
    
    var onNext: (() -> Void)?
    var onBack: (() -> Void)?
    
    /// Callback triggered when report successfully sent with managementPassword
    var onReportSent: ((String) -> Void)?
    
    // MARK: - Initialization (Manual DI)
    
    init(submissionService: AnnouncementSubmissionService, flowState: ReportMissingPetFlowState) {
        self.submissionService = submissionService
        self.flowState = flowState
        
        // Prepopulate from FlowState if returning from summary
        if let contactDetails = flowState.contactDetails {
            self.phone = contactDetails.phone
            self.email = contactDetails.email
            self.rewardDescription = contactDetails.rewardDescription ?? ""
        }
    }
    
    // MARK: - Computed Properties (validation)
    
    var isFormValid: Bool {
        return isPhoneValid && isEmailValid
    }
    
    private var isPhoneValid: Bool {
        let sanitized = phone.filter { $0.isNumber || $0 == "+" }
        let digitCount = sanitized.filter { $0.isNumber }.count
        return digitCount >= 7 && digitCount <= 11
    }
    
    private var isEmailValid: Bool {
        let emailRegex = #"^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$"#
        let predicate = NSPredicate(format: "SELF MATCHES[c] %@", emailRegex)
        return predicate.evaluate(with: email.trimmingCharacters(in: .whitespaces))
    }
    
    // MARK: - ValidatedTextField Models
    
    var phoneTextFieldModel: ValidatedTextField.Model {
        ValidatedTextField.Model(
            label: L10n.OwnersDetails.Phone.label,
            placeholder: L10n.OwnersDetails.Phone.placeholder,
            errorMessage: phoneError,
            isDisabled: isSubmitting,
            keyboardType: .phonePad,
            accessibilityID: "ownersDetails.phoneInput"
        )
    }
    
    var emailTextFieldModel: ValidatedTextField.Model {
        ValidatedTextField.Model(
            label: L10n.OwnersDetails.Email.label,
            placeholder: L10n.OwnersDetails.Email.placeholder,
            errorMessage: emailError,
            isDisabled: isSubmitting,
            keyboardType: .emailAddress,
            accessibilityID: "ownersDetails.emailInput"
        )
    }
    
    var rewardTextFieldModel: ValidatedTextField.Model {
        ValidatedTextField.Model(
            label: L10n.OwnersDetails.Reward.label,
            placeholder: L10n.OwnersDetails.Reward.placeholder,
            errorMessage: nil,
            isDisabled: isSubmitting,
            keyboardType: .default,
            maxLength: 120,
            accessibilityID: "ownersDetails.rewardInput"
        )
    }
    
    // MARK: - Actions
    
    /// Validates inputs and submits announcement via service.
    /// Orchestrates 2-step submission: (1) create announcement → (2) upload photo.
    func submitForm() async {
        // Clear previous errors
        phoneError = nil
        emailError = nil
        
        // Validate all fields and collect errors
        var hasErrors = false
        
        if !isPhoneValid {
            phoneError = L10n.OwnersDetails.Phone.error // "Enter at least 7 digits"
            hasErrors = true
        }
        
        if !isEmailValid {
            emailError = L10n.OwnersDetails.Email.error // "Enter a valid email address"
            hasErrors = true
        }
        
        // Stop if any validation errors
        if hasErrors {
            return
        }
        
        // Save validated inputs to FlowState
        flowState.contactDetails = OwnerContactDetails(
            phone: phone,
            email: email,
            rewardDescription: rewardDescription.isEmpty ? nil : rewardDescription
        )
        
        // Start submission
        isSubmitting = true
        defer { isSubmitting = false }
        
        do {
            // Delegate to service for 2-step submission
            let managementPassword = try await submissionService.submitAnnouncement(flowState: flowState)
            
            // Success: navigate to summary with managementPassword
            onReportSent?(managementPassword)
            
        } catch {
            // Failure: show error popup
            handleSubmissionError(error)
        }
    }
    
    private func handleSubmissionError(_ error: Error) {
        // Determine error type (network vs backend)
        if (error as NSError).domain == NSURLErrorDomain {
            // Network error (offline, timeout)
            alertMessage = L10n.OwnersDetails.Error.NoConnection.message
        } else {
            // Backend error (4xx/5xx)
            alertMessage = L10n.OwnersDetails.Error.Generic.message
        }
        showAlert = true
    }
    
    /// Navigate back to previous screen (Description).
    func handleBack() {
        onBack?()
    }
    
    // MARK: - Deinitialization
    
    deinit {
        print("deinit ContactDetailsViewModel")
    }
}

