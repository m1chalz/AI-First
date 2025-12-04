import Foundation

/// Internal DTO for photo upload request (POST /api/v1/announcements/:id/photos)
/// Contains metadata needed for multipart form-data submission
struct PhotoUploadRequestDTO {
    let announcementId: String
    let photo: PhotoAttachmentMetadata
    let managementPassword: String
}

