import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import { usePhotoUpload } from '../use-photo-upload';

describe('use-photo-upload', () => {
  let mockBlobUrl: string;
  let createObjectURLSpy: ReturnType<typeof vi.fn>;
  let revokeObjectURLSpy: ReturnType<typeof vi.fn>;

  beforeEach(() => {
    mockBlobUrl = 'blob:http://localhost/mock-blob-url';
    createObjectURLSpy = vi.fn(() => mockBlobUrl);
    revokeObjectURLSpy = vi.fn();

    globalThis.URL.createObjectURL = createObjectURLSpy as unknown as (obj: Blob | MediaSource) => string;
    globalThis.URL.revokeObjectURL = revokeObjectURLSpy as unknown as (url: string) => void;
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  describe('initial state', () => {
    it('initializes with null photo', () => {
      // given
      const showToast = vi.fn();

      // when
      const { result } = renderHook(() => usePhotoUpload(null, showToast));

      // then
      expect(result.current.photo).toBe(null);
      expect(result.current.error).toBe(null);
      expect(result.current.isDragOver).toBe(false);
    });

    it('initializes with provided photo', () => {
      // given
      const showToast = vi.fn();
      const initialPhoto = {
        file: new File(['content'], 'test.jpg', { type: 'image/jpeg' }),
        filename: 'test.jpg',
        size: 1024,
        mimeType: 'image/jpeg',
        previewUrl: 'blob:existing-url'
      };

      // when
      const { result } = renderHook(() => usePhotoUpload(initialPhoto, showToast));

      // then
      expect(result.current.photo).toEqual(initialPhoto);
    });
  });

  describe('handleFileSelect', () => {
    it('stores valid file as PhotoAttachment', () => {
      // given
      const showToast = vi.fn();
      const { result } = renderHook(() => usePhotoUpload(null, showToast));
      const validFile = new File([new ArrayBuffer(1024)], 'photo.jpg', { type: 'image/jpeg' });

      // when
      act(() => {
        result.current.handleFileSelect(validFile);
      });

      // then
      expect(result.current.photo).toEqual({
        file: validFile,
        filename: 'photo.jpg',
        size: 1024,
        mimeType: 'image/jpeg',
        previewUrl: mockBlobUrl
      });
      expect(result.current.error).toBe(null);
      expect(createObjectURLSpy).toHaveBeenCalledWith(validFile);
      expect(showToast).not.toHaveBeenCalled();
    });

    it('sets error for oversized file', () => {
      // given
      const showToast = vi.fn();
      const { result } = renderHook(() => usePhotoUpload(null, showToast));
      const oversizedFile = new File([new ArrayBuffer(21 * 1024 * 1024)], 'large.jpg', { type: 'image/jpeg' });

      // when
      act(() => {
        result.current.handleFileSelect(oversizedFile);
      });

      // then
      expect(result.current.photo).toBe(null);
      expect(result.current.error).toBe('File size exceeds 20MB limit');
      expect(showToast).toHaveBeenCalledWith('File size exceeds 20MB limit', 5000);
      expect(createObjectURLSpy).not.toHaveBeenCalled();
    });

    it('sets error for invalid format', () => {
      // given
      const showToast = vi.fn();
      const { result } = renderHook(() => usePhotoUpload(null, showToast));
      const invalidFile = new File(['content'], 'document.pdf', { type: 'application/pdf' });

      // when
      act(() => {
        result.current.handleFileSelect(invalidFile);
      });

      // then
      expect(result.current.photo).toBe(null);
      expect(result.current.error).toBe('Please upload JPG, PNG, GIF, WEBP, BMP, TIFF, HEIC, or HEIF format');
      expect(showToast).toHaveBeenCalledWith('Please upload JPG, PNG, GIF, WEBP, BMP, TIFF, HEIC, or HEIF format', 5000);
      expect(createObjectURLSpy).not.toHaveBeenCalled();
    });

    it('clears error when valid file selected after error', () => {
      // given
      const showToast = vi.fn();
      const { result } = renderHook(() => usePhotoUpload(null, showToast));
      const invalidFile = new File(['content'], 'document.pdf', { type: 'application/pdf' });
      const validFile = new File([new ArrayBuffer(1024)], 'photo.jpg', { type: 'image/jpeg' });

      act(() => {
        result.current.handleFileSelect(invalidFile);
      });

      // when
      act(() => {
        result.current.handleFileSelect(validFile);
      });

      // then
      expect(result.current.error).toBe(null);
      expect(result.current.photo).not.toBe(null);
    });

    it('revokes old blob URL when replacing photo', () => {
      // given
      const showToast = vi.fn();
      const { result } = renderHook(() => usePhotoUpload(null, showToast));
      const firstFile = new File([new ArrayBuffer(1024)], 'photo1.jpg', { type: 'image/jpeg' });
      const secondFile = new File([new ArrayBuffer(2048)], 'photo2.jpg', { type: 'image/jpeg' });

      act(() => {
        result.current.handleFileSelect(firstFile);
      });
      const firstBlobUrl = result.current.photo?.previewUrl;

      // when
      act(() => {
        result.current.handleFileSelect(secondFile);
      });

      // then
      expect(revokeObjectURLSpy).toHaveBeenCalledWith(firstBlobUrl);
      expect(result.current.photo?.filename).toBe('photo2.jpg');
    });
  });

  describe('handleDrop', () => {
    it('processes dropped file', () => {
      // given
      const showToast = vi.fn();
      const { result } = renderHook(() => usePhotoUpload(null, showToast));
      const file = new File([new ArrayBuffer(1024)], 'photo.jpg', { type: 'image/jpeg' });
      const dropEvent = {
        preventDefault: vi.fn(),
        stopPropagation: vi.fn(),
        dataTransfer: {
          files: [file]
        }
      } as unknown as React.DragEvent<HTMLDivElement>;

      // when
      act(() => {
        result.current.handleDrop(dropEvent);
      });

      // then
      expect(dropEvent.preventDefault).toHaveBeenCalled();
      expect(dropEvent.stopPropagation).toHaveBeenCalled();
      expect(result.current.photo?.filename).toBe('photo.jpg');
      expect(result.current.isDragOver).toBe(false);
    });

    it('handles empty file list', () => {
      // given
      const showToast = vi.fn();
      const { result } = renderHook(() => usePhotoUpload(null, showToast));
      const dropEvent = {
        preventDefault: vi.fn(),
        stopPropagation: vi.fn(),
        dataTransfer: {
          files: []
        }
      } as unknown as React.DragEvent<HTMLDivElement>;

      // when
      act(() => {
        result.current.handleDrop(dropEvent);
      });

      // then
      expect(result.current.photo).toBe(null);
    });

    it('processes only first file when multiple dropped', () => {
      // given
      const showToast = vi.fn();
      const { result } = renderHook(() => usePhotoUpload(null, showToast));
      const file1 = new File([new ArrayBuffer(1024)], 'photo1.jpg', { type: 'image/jpeg' });
      const file2 = new File([new ArrayBuffer(2048)], 'photo2.jpg', { type: 'image/jpeg' });
      const dropEvent = {
        preventDefault: vi.fn(),
        stopPropagation: vi.fn(),
        dataTransfer: {
          files: [file1, file2]
        }
      } as unknown as React.DragEvent<HTMLDivElement>;

      // when
      act(() => {
        result.current.handleDrop(dropEvent);
      });

      // then
      expect(result.current.photo?.filename).toBe('photo1.jpg');
    });
  });

  describe('drag events', () => {
    it('sets isDragOver to true on handleDragOver', () => {
      // given
      const showToast = vi.fn();
      const { result } = renderHook(() => usePhotoUpload(null, showToast));
      const dragEvent = {
        preventDefault: vi.fn(),
        stopPropagation: vi.fn()
      } as unknown as React.DragEvent<HTMLDivElement>;

      // when
      act(() => {
        result.current.handleDragOver(dragEvent);
      });

      // then
      expect(result.current.isDragOver).toBe(true);
      expect(dragEvent.preventDefault).toHaveBeenCalled();
      expect(dragEvent.stopPropagation).toHaveBeenCalled();
    });

    it('sets isDragOver to false on handleDragLeave', () => {
      // given
      const showToast = vi.fn();
      const { result } = renderHook(() => usePhotoUpload(null, showToast));
      const dragEvent = {
        preventDefault: vi.fn(),
        stopPropagation: vi.fn()
      } as unknown as React.DragEvent<HTMLDivElement>;

      act(() => {
        result.current.handleDragOver(dragEvent);
      });

      // when
      act(() => {
        result.current.handleDragLeave();
      });

      // then
      expect(result.current.isDragOver).toBe(false);
    });
  });

  describe('removePhoto', () => {
    it('clears photo and revokes blob URL', () => {
      // given
      const showToast = vi.fn();
      const { result } = renderHook(() => usePhotoUpload(null, showToast));
      const file = new File([new ArrayBuffer(1024)], 'photo.jpg', { type: 'image/jpeg' });

      act(() => {
        result.current.handleFileSelect(file);
      });
      const blobUrl = result.current.photo?.previewUrl;

      // when
      act(() => {
        result.current.removePhoto();
      });

      // then
      expect(result.current.photo).toBe(null);
      expect(revokeObjectURLSpy).toHaveBeenCalledWith(blobUrl);
    });

    it('does nothing when photo is already null', () => {
      // given
      const showToast = vi.fn();
      const { result } = renderHook(() => usePhotoUpload(null, showToast));

      // when
      act(() => {
        result.current.removePhoto();
      });

      // then
      expect(result.current.photo).toBe(null);
      expect(revokeObjectURLSpy).not.toHaveBeenCalled();
    });
  });

  describe('cleanup on unmount', () => {
    it('revokes blob URL when component unmounts with photo', () => {
      // given
      const showToast = vi.fn();
      const { result, unmount } = renderHook(() => usePhotoUpload(null, showToast));
      const file = new File([new ArrayBuffer(1024)], 'photo.jpg', { type: 'image/jpeg' });

      act(() => {
        result.current.handleFileSelect(file);
      });
      const blobUrl = result.current.photo?.previewUrl;

      // when
      unmount();

      // then
      expect(revokeObjectURLSpy).toHaveBeenCalledWith(blobUrl);
    });

    it('does not call revokeObjectURL when unmounting without photo', () => {
      // given
      const showToast = vi.fn();
      const { unmount } = renderHook(() => usePhotoUpload(null, showToast));

      // when
      unmount();

      // then
      expect(revokeObjectURLSpy).not.toHaveBeenCalled();
    });
  });
});
