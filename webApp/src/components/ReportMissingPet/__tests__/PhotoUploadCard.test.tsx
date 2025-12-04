import { describe, it, expect, vi } from 'vitest';
import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { PhotoUploadCard } from '../PhotoUploadCard';

describe('PhotoUploadCard', () => {
  describe('render', () => {
    it('renders upload card with text, browse button, and file input', () => {
      // given / when
      const fileInputRef: React.RefObject<HTMLInputElement> = { current: null };
      render(
        <PhotoUploadCard
          isDragOver={false}
          onDrop={vi.fn()}
          onDragOver={vi.fn()}
          onDragLeave={vi.fn()}
          onBrowseClick={vi.fn()}
          fileInputRef={fileInputRef}
          onFileInputChange={vi.fn()}
        />
      );

      // then
      expect(screen.getByText('Upload animal photo')).toBeTruthy();
      expect(screen.getByText('JPEG, PNG, GIF • Max 20MB')).toBeTruthy();
      const button = screen.getByTestId('animalPhoto.browse.click');
      expect(button).toBeTruthy();
      expect(button.textContent).toBe('Browse');
      const fileInput = screen.getByTestId('animalPhoto.fileInput.field') as HTMLInputElement;
      expect(fileInput.getAttribute('accept')).toBe('image/jpeg,image/png,image/gif,image/webp,image/bmp,image/tiff,image/heic,image/heif');
      expect(fileInput.type).toBe('file');
    });
  });

  describe('drag-over state', () => {
    it('applies dragOverHighlight class when isDragOver is true', () => {
      // given / when
      const fileInputRef: React.RefObject<HTMLInputElement> = { current: null };
      render(
        <PhotoUploadCard
          isDragOver={true}
          onDrop={vi.fn()}
          onDragOver={vi.fn()}
          onDragLeave={vi.fn()}
          onBrowseClick={vi.fn()}
          fileInputRef={fileInputRef}
          onFileInputChange={vi.fn()}
        />
      );

      // then
      const uploadCard = screen.getByTestId('animalPhoto.dropZone.area');
      expect(uploadCard.className).toContain('dragOverHighlight');
    });

    it('does not apply dragOverHighlight class when isDragOver is false', () => {
      // given / when
      const fileInputRef: React.RefObject<HTMLInputElement> = { current: null };
      render(
        <PhotoUploadCard
          isDragOver={false}
          onDrop={vi.fn()}
          onDragOver={vi.fn()}
          onDragLeave={vi.fn()}
          onBrowseClick={vi.fn()}
          fileInputRef={fileInputRef}
          onFileInputChange={vi.fn()}
        />
      );

      // then
      const uploadCard = screen.getByTestId('animalPhoto.dropZone.area');
      expect(uploadCard.className).not.toContain('dragOverHighlight');
    });
  });

  describe('event handlers', () => {
    it('calls onBrowseClick when browse button is clicked', () => {
      // given
      const onBrowseClick = vi.fn();
      const fileInputRef: React.RefObject<HTMLInputElement> = { current: null };
      render(
        <PhotoUploadCard
          isDragOver={false}
          onDrop={vi.fn()}
          onDragOver={vi.fn()}
          onDragLeave={vi.fn()}
          onBrowseClick={onBrowseClick}
          fileInputRef={fileInputRef}
          onFileInputChange={vi.fn()}
        />
      );

      // when
      fireEvent.click(screen.getByTestId('animalPhoto.browse.click'));

      // then
      expect(onBrowseClick).toHaveBeenCalled();
    });

    it('calls onDrop when file is dropped', () => {
      // given
      const onDrop = vi.fn((e) => {
        e.preventDefault();
        e.stopPropagation();
      });
      const fileInputRef: React.RefObject<HTMLInputElement> = { current: null };
      render(
        <PhotoUploadCard
          isDragOver={false}
          onDrop={onDrop}
          onDragOver={vi.fn()}
          onDragLeave={vi.fn()}
          onBrowseClick={vi.fn()}
          fileInputRef={fileInputRef}
          onFileInputChange={vi.fn()}
        />
      );

      // when
      fireEvent.drop(screen.getByTestId('animalPhoto.dropZone.area'));

      // then
      expect(onDrop).toHaveBeenCalled();
    });

    it('calls onDragOver when file is dragged over', () => {
      // given
      const onDragOver = vi.fn((e) => {
        e.preventDefault();
        e.stopPropagation();
      });
      const fileInputRef: React.RefObject<HTMLInputElement> = { current: null };
      render(
        <PhotoUploadCard
          isDragOver={false}
          onDrop={vi.fn()}
          onDragOver={onDragOver}
          onDragLeave={vi.fn()}
          onBrowseClick={vi.fn()}
          fileInputRef={fileInputRef}
          onFileInputChange={vi.fn()}
        />
      );

      // when
      fireEvent.dragOver(screen.getByTestId('animalPhoto.dropZone.area'));

      // then
      expect(onDragOver).toHaveBeenCalled();
    });

    it('calls onDragLeave when file is dragged away', () => {
      // given
      const onDragLeave = vi.fn();
      const fileInputRef: React.RefObject<HTMLInputElement> = { current: null };
      render(
        <PhotoUploadCard
          isDragOver={false}
          onDrop={vi.fn()}
          onDragOver={vi.fn()}
          onDragLeave={onDragLeave}
          onBrowseClick={vi.fn()}
          fileInputRef={fileInputRef}
          onFileInputChange={vi.fn()}
        />
      );

      // when
      fireEvent.dragLeave(screen.getByTestId('animalPhoto.dropZone.area'));

      // then
      expect(onDragLeave).toHaveBeenCalled();
    });

    it('calls onFileInputChange when file is selected', () => {
      // given
      const onFileInputChange = vi.fn();
      const fileInputRef: React.RefObject<HTMLInputElement> = { current: null };
      render(
        <PhotoUploadCard
          isDragOver={false}
          onDrop={vi.fn()}
          onDragOver={vi.fn()}
          onDragLeave={vi.fn()}
          onBrowseClick={vi.fn()}
          fileInputRef={fileInputRef}
          onFileInputChange={onFileInputChange}
        />
      );

      // when
      const fileInput = screen.getByTestId('animalPhoto.fileInput.field');
      fireEvent.change(fileInput);

      // then
      expect(onFileInputChange).toHaveBeenCalled();
    });
  });

});
