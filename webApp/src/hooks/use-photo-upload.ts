import { useState, useEffect } from 'react';
import { PhotoAttachment } from '../models/NewAnnouncementFlow';
import { getFileValidationError } from '../utils/file-validation';

interface UsePhotoUploadReturn {
  photo: PhotoAttachment | null;
  error: string | null;
  isDragOver: boolean;
  handleFileSelect: (file: File) => void;
  handleDrop: (e: React.DragEvent<HTMLDivElement>) => void;
  handleDragOver: (e: React.DragEvent<HTMLDivElement>) => void;
  handleDragLeave: () => void;
  removePhoto: () => void;
}

export function usePhotoUpload(
  initialPhoto: PhotoAttachment | null,
  showToast: (message: string, duration: number) => void
): UsePhotoUploadReturn {
  const [photo, setPhoto] = useState<PhotoAttachment | null>(initialPhoto);
  const [error, setError] = useState<string | null>(null);
  const [isDragOver, setIsDragOver] = useState(false);

  const handleFileSelect = (file: File) => {
    const validationError = getFileValidationError(file);

    if (validationError) {
      setError(validationError);
      showToast(validationError, 5000);
      return;
    }

    if (photo?.previewUrl) {
      URL.revokeObjectURL(photo.previewUrl);
    }

    const previewUrl = URL.createObjectURL(file);
    const newPhoto: PhotoAttachment = {
      file,
      filename: file.name,
      size: file.size,
      mimeType: file.type,
      previewUrl
    };

    setPhoto(newPhoto);
    setError(null);
  };

  const handleDrop = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDragOver(false);

    const files = e.dataTransfer.files;
    if (files && files.length > 0) {
      handleFileSelect(files[0]);
    }
  };

  const handleDragOver = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDragOver(true);
  };

  const handleDragLeave = () => {
    setIsDragOver(false);
  };

  const removePhoto = () => {
    if (photo?.previewUrl) {
      URL.revokeObjectURL(photo.previewUrl);
    }
    setPhoto(null);
  };

  useEffect(
    () => () => {
      if (photo?.previewUrl) {
        URL.revokeObjectURL(photo.previewUrl);
      }
    },
    [photo]
  );

  return {
    photo,
    error,
    isDragOver,
    handleFileSelect,
    handleDrop,
    handleDragOver,
    handleDragLeave,
    removePhoto
  };
}
