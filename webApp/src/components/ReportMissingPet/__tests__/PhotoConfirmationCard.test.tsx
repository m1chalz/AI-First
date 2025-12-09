import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { PhotoConfirmationCard } from '../PhotoConfirmationCard';
import { PhotoAttachment } from '../../../models/ReportMissingPetFlow';

describe('PhotoConfirmationCard', () => {
  const mockPhoto: PhotoAttachment = {
    file: new File(['content'], 'test.jpg', { type: 'image/jpeg' }),
    filename: 'test.jpg',
    size: 2048,
    mimeType: 'image/jpeg',
    previewUrl: 'blob:mock-url'
  };

  describe('render', () => {
    it('renders confirmation card with filename and file size', () => {
      // given / when
      render(<PhotoConfirmationCard photo={mockPhoto} onRemove={vi.fn()} />);

      // then
      expect(screen.getByTestId('animalPhoto.confirmationCard')).toBeTruthy();
      expect(screen.getByTestId('animalPhoto.filename.text').textContent).toBe('test.jpg');
      expect(screen.getByTestId('animalPhoto.filesize.text').textContent).toBe('2.0 KB');
    });
  });

  describe('file size formatting', () => {
    it.each([
      [1024, '1.0 KB'],
      [2048, '2.0 KB'],
      [1024 * 1024, '1.0 MB'],
      [1024 * 1024 * 5, '5.0 MB']
    ])('formats %d bytes as %s', (_bytes, expected) => {
      // given / when
      const photo: PhotoAttachment = {
        ...mockPhoto,
        size: _bytes
      };
      render(<PhotoConfirmationCard photo={photo} onRemove={vi.fn()} />);

      // then
      expect(screen.getByTestId('animalPhoto.filesize.text').textContent).toBe(expected);
    });
  });

  describe('remove button', () => {
    it('renders remove button with aria-label and calls onRemove when clicked', () => {
      // given
      const onRemove = vi.fn();
      render(<PhotoConfirmationCard photo={mockPhoto} onRemove={onRemove} />);

      // then
      const removeButton = screen.getByTestId('animalPhoto.remove.click');
      expect(removeButton).toBeTruthy();
      expect(removeButton.getAttribute('aria-label')).toBe('Remove photo');

      // when
      fireEvent.click(removeButton);

      // then
      expect(onRemove).toHaveBeenCalled();
    });
  });

  describe('different file types', () => {
    it.each([
      { filename: 'photo.jpg', mimeType: 'image/jpeg' },
      { filename: 'photo.png', mimeType: 'image/png' },
      { filename: 'photo.gif', mimeType: 'image/gif' },
      { filename: 'my-vacation-photo.jpg', mimeType: 'image/jpeg' }
    ])('renders confirmation for file $filename', ({ filename, mimeType }) => {
      // given
      const photo: PhotoAttachment = {
        ...mockPhoto,
        filename,
        mimeType
      };

      // when
      render(<PhotoConfirmationCard photo={photo} onRemove={vi.fn()} />);

      // then
      expect(screen.getByTestId('animalPhoto.filename.text').textContent).toBe(filename);
    });
  });
});
