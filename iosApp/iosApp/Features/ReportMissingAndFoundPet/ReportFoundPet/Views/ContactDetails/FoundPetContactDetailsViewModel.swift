import Foundation

/// ViewModel for Contact Information screen (Step 3/3 of Found Pet flow).
/// Manages input state, validation, and 2-step submission.
/// Includes optional caregiver phone and current address (iOS-only, not sent to backend).
@MainActor
class FoundPetContactDetailsViewModel: ObservableObject {
    // MARK: - Input State (bound to ValidatedTextField)
    
    @Published var phone: String = ""
    @Published var email: String = ""
    
    /// Caregiver phone number (optional, iOS-only - not sent to backend per FR-016)
    @Published var caregiverPhone: String = ""
    
    /// Current physical address (optional, iOS-only - not sent to backend per FR-016)
    @Published var currentAddress: String = ""
    
    // MARK: - Validation Error State (displayed in ValidatedTextField)
    
    @Published var phoneError: String? = nil
    @Published var emailError: String? = nil
    @Published var caregiverPhoneError: String? = nil
    
    // MARK: - Loading State (controls Continue button spinner and back button)
    
    @Published var isSubmitting: Bool = false
    
    // MARK: - Alert State (popup for submission errors)
    
    @Published var alertMessage: String? = nil
    @Published var showAlert: Bool = false
    
    // MARK: - Dependencies (injected via initializer)
    
    private let submissionService: AnnouncementSubmissionServiceProtocol
    private let flowState: FoundPetReportFlowState
    
    // MARK: - Coordinator Callbacks
    
    var onNext: (() -> Void)?
    var onBack: (() -> Void)?
    
    /// Callback triggered when report successfully sent with managementPassword
    var onReportSent: ((String) -> Void)?
    
    // MARK: - Initialization (Manual DI)
    
    init(submissionService: AnnouncementSubmissionServiceProtocol, flowState: FoundPetReportFlowState) {
        self.submissionService = submissionService
        self.flowState = flowState
        
        // Prepopulate from FlowState if returning
        if let contactDetails = flowState.contactDetails {
            self.phone = contactDetails.phone
            self.email = contactDetails.email
        }
        
        // Load iOS-only fields (not sent to backend)
        if let caregiverPhone = flowState.caregiverPhoneNumber {
            self.caregiverPhone = caregiverPhone
        }
        if let currentAddress = flowState.currentPhysicalAddress {
            self.currentAddress = currentAddress
        }
    }
    
    // MARK: - Computed Properties (validation)
    
    var isFormValid: Bool {
        return isPhoneValid && isEmailValid && isCaregiverPhoneValid
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
    
    /// Caregiver phone is optional - empty is valid, non-empty requires 7-11 digits
    var isCaregiverPhoneValid: Bool {
        if caregiverPhone.isEmpty { return true }
        let sanitized = caregiverPhone.filter { $0.isNumber || $0 == "+" }
        let digitCount = sanitized.filter { $0.isNumber }.count
        return digitCount >= 7 && digitCount <= 11
    }
    
    /// Character count for current address (max 500)
    var addressCharacterCount: String {
        "\(currentAddress.count)/500"
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
    
    /// Model for caregiver phone field (optional, iOS-only)
    var caregiverPhoneTextFieldModel: ValidatedTextField.Model {
        ValidatedTextField.Model(
            label: L10n.ReportFoundPet.ContactInfo.caregiverPhoneLabel,
            placeholder: L10n.ReportFoundPet.ContactInfo.caregiverPhonePlaceholder,
            errorMessage: caregiverPhoneError,
            isDisabled: isSubmitting,
            keyboardType: .phonePad,
            accessibilityID: "reportFoundPet.contactInfo.caregiverPhone.input"
        )
    }
    
    /// Model for current address text area (optional, iOS-only, max 500 chars)
    var currentAddressTextAreaModel: TextAreaView.Model {
        TextAreaView.Model(
            label: L10n.ReportFoundPet.ContactInfo.currentAddressLabel,
            placeholder: L10n.ReportFoundPet.ContactInfo.currentAddressPlaceholder,
            maxLength: 500,
            characterCountText: addressCharacterCount,
            accessibilityID: "reportFoundPet.contactInfo.currentAddress.input"
        )
    }
    
    // MARK: - Actions
    
    /// Validates inputs and submits announcement via service.
    /// Orchestrates 2-step submission: (1) create announcement → (2) upload photo.
    func submitForm() async {
        // Clear previous errors
        phoneError = nil
        emailError = nil
        caregiverPhoneError = nil
        
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
        
        // Validate caregiver phone if not empty
        if !isCaregiverPhoneValid {
            caregiverPhoneError = L10n.OwnersDetails.Phone.error // "Enter at least 7 digits"
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
            rewardDescription: nil
        )
        
        // Save iOS-only fields to FlowState (NOT sent to backend per FR-016)
        flowState.caregiverPhoneNumber = caregiverPhone.isEmpty ? nil : caregiverPhone
        flowState.currentPhysicalAddress = currentAddress.isEmpty ? nil : currentAddress
        
        // Start submission
        isSubmitting = true
        defer { isSubmitting = false }
        
        do {
            // Delegate to service for 2-step submission
            let managementPassword = try await submissionService.submitAnnouncement(flowState: flowState)
            
            // Success: clear any previous error state and navigate to summary
            showAlert = false
            alertMessage = nil
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
        print("deinit FoundPetContactDetailsViewModel")
    }
}

